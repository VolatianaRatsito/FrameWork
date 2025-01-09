package mg.tool;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import mg.annotation.Column;
import mg.annotation.ModelAttribute;
import mg.annotation.RequestParameter;
import mg.exception.AnnotationException;
import mg.exception.ValidationException;
import mg.annotation.*;
import mg.tool.ModelAndView;
import mg.tool.MultiPart;
import mg.tool.MySession;
import mg.tool.Util;
import mg.tool.Validation;

import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.List;

public class Fonction {

    public static Object invokeMethodWithRequestParams(String className, String methodName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object object = clazz.getDeclaredConstructor().newInstance();
        Method method = getMethodWithRequestParams(clazz, methodName, request);
        if (method == null) {
            throw new NoSuchMethodException("No suitable method found for " + methodName);
        }
        if (isRestApiAnnotated(method, request, response, object)) {
            return null; 
        }  
        handleSession(object, request);
        return invokeMethod(method, object, request, response);
    }

    private static boolean isRestApiAnnotated(Method method, HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        if (method.isAnnotationPresent(RestApi.class)) {            
            Object[] params = getParameters(request, method, response);
            method.setAccessible(true);
            Object result = (params.length == 0) ? method.invoke(object) : method.invoke(object, params);

            response.setContentType("application/json");
  
            if (result instanceof String) {
                String jsonResult = convertToJson(result);
                response.getWriter().print(jsonResult);
                response.getWriter().flush();
                return true;
            } else if (result instanceof ModelAndView) {
                ModelAndView modelAndView = (ModelAndView) result;
                String jsonResult = convertToJson(modelAndView.getData());
                response.getWriter().print(jsonResult);
                return true;
            }
        }
        return false;
    }
    
    private static void handleSession(Object object, HttpServletRequest request) throws Exception {
        List<Field> allFields = Util.getAllFields(object);
        if (!allFields.isEmpty()) {
            HttpSession session = request.getSession();
            MySession mySession = new MySession(session);
    
            for (Field field : allFields) {
                if (field.getType().equals(MySession.class)) {
                    try {
                        Method setSessionMethod = object.getClass().getMethod("setSession", MySession.class);
                        setSessionMethod.invoke(object, mySession);
                    } catch (NoSuchMethodException e) {
                        System.out.println("No setSession method found in class " + object.getClass().getName());
                    }
                }
            }
            try {
                Method getSessionMethod = object.getClass().getMethod("getSession");
                MySession updatedSession = (MySession) getSessionMethod.invoke(object);
                if (updatedSession != null) {
                    session = updatedSession.getSession();
                } else {
                    System.out.println("Aucune modification de la session.");
                }
            } catch (NoSuchMethodException e) {
                System.out.println("No getSession method found in class " + object.getClass().getName());
            }
        }
    }

    private static Object invokeMethod(Method method, Object object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object[] params = getParameters(request, method, response);
        method.setAccessible(true);
        return (params.length == 0) ? method.invoke(object) : method.invoke(object, params);
    }

    public static Method getMethodWithRequestParams(Class<?> clazz, String methodName, HttpServletRequest request) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private static String convertToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static Object[] getParameters(HttpServletRequest request, Method method, HttpServletResponse response) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            // Gestion de @RequestParameter
            if (param.isAnnotationPresent(RequestParameter.class)) {
                RequestParameter requestParam = param.getAnnotation(RequestParameter.class);
                String paramName = requestParam.value();   
                if (param.getType().equals(MultiPart.class)) {
                    Part part = request.getPart(paramName);
                    if (part != null) {
                        String fileName = part.getSubmittedFileName();
                        long fileSize = part.getSize();
                        InputStream fileContent = part.getInputStream();
                        params[i] = new MultiPart(fileName, fileSize, fileContent);
                    } else {
                        params[i] = null;
                    }
                } else {
                    String paramValue = request.getParameter(paramName);
                    params[i] = convertType(paramValue, param.getType());
                }
            } 
            else if (param.isAnnotationPresent(ModelAttribute.class)) {
                String className = param.getType().getName();
                if ("mg.tool.MySession".equals(className)) {
                    params[i] = new MySession(request.getSession());
                } else {
                    Object objectFromForm = RetourneObjetFromFormulaire(className, request);
                    List<ValidationException> validationErrors = Validation.validate(objectFromForm);
                    if (!validationErrors.isEmpty()) {
                        request.setAttribute("errors", validationErrors);
                        request.setAttribute("object", objectFromForm);

                        if (method.isAnnotationPresent(IsError.class)) {
                            IsError isErrorAnnotation = method.getAnnotation(IsError.class);
                            String urlError = isErrorAnnotation.value();
                            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                                public String getMethod() {
                                    return "GET"; 
                                }
                            };

                            RequestDispatcher dispatcher = request.getRequestDispatcher(urlError);
                            dispatcher.forward(wrappedRequest, response);
                            return null; 
                        }
                    } else {
                        params[i] = objectFromForm;
                        System.out.println("Objet extrait et validé : " + params[i].getClass().getSimpleName());
                    }
                }
            } else {
                throw new AnnotationException("ETU002722 : Tous les paramètres doivent être annotés.");
            }
        }

        return params;
    }

    public static Object RetourneObjetFromFormulaire(String className, HttpServletRequest request) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object obj = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            String parameterName = column != null ? column.name() : field.getName();
            String parameterValue = request.getParameter(parameterName);
            Object value = convertType(parameterValue, field.getType());
            field.setAccessible(true);
            field.set(obj, value);
        }
        return obj;
    }

    public static Object convertType(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; 
    }

    public static void removeAllAttributes(HttpSession session) {
        if (session == null) {
            return;
        }

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            session.removeAttribute(attributeName);
        }
    }
}
