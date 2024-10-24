package mg.tool;

import jakarta.servlet.http.*;
import mg.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import mg.exception.*;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.PrintWriter;

import jakarta.servlet.http.*;

public class ReflectionUtil {

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
            return invokeMethod(method, object, request);
    }
    
    private static boolean isRestApiAnnotated(Method method, HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        if (method.isAnnotationPresent(RestApi.class)) {            
            Object[] params = getParameters(request, method);
            method.setAccessible(true);
            Object result = (params.length == 0) ? method.invoke(object) : method.invoke(object, params);
    
            response.setContentType("application/json");
    
            // Vérifier le type de retour et renvoyer en JSON
            if (result instanceof String) {
                String jsonResult = convertToJson(result);
                response.getWriter().print(jsonResult);
                response.getWriter().flush();
                return true; // Réponse déjà envoyée
            } else if (result instanceof ModelAndView) {
                ModelAndView modelAndView = (ModelAndView) result;
                String jsonResult = convertToJson(modelAndView.getData());
                response.getWriter().print(jsonResult);
                return true; // Réponse déjà envoyée
            }
        }
        return false; // Pas d'annotation @RestApi
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
    
            // Mise à jour de la session si nécessaire
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
    
    private static Object invokeMethod(Method method, Object object, HttpServletRequest request) throws Exception {
        Object[] params = getParameters(request, method);
        method.setAccessible(true);
        return (params.length == 0) ? method.invoke(object) : method.invoke(object, params);
    }
    

    public static Method getMethodWithRequestParams(Class<?> clazz, String methodName, HttpServletRequest request) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) ) {
                return method;
            }
        }
        return null;
    }

    private static String convertToJson(Object object) {
    Gson gson = new Gson();
    return gson.toJson(object);
}

    public static Object[] getParameters(HttpServletRequest request, Method method) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParameter.class)) {
                RequestParameter requestParam = parameters[i].getAnnotation(RequestParameter.class);
                String paramName = requestParam.value();
                if (parameters[i].getType().equals(MultiPart.class)) {
                    Part part = request.getPart(paramName);
                      if (part != null) {
                        String fileName = part.getSubmittedFileName();
                        long fileSize = part.getSize();         
                        InputStream fileContent = part.getInputStream();
                        MultiPart multiPart = new MultiPart(fileName, fileSize, fileContent);
                        params[i] = multiPart;
                    } else {
                        params[i] = null;
                    }
                } else {
                    String paramValue = request.getParameter(paramName);  // Récupère les autres paramètres
                    params[i] = convertType(paramValue, parameters[i].getType());
                }
            } 
            else if (parameters[i].isAnnotationPresent(ModelAttribute.class)) {
                String className = parameters[i].getType().getName();
                if(className.equals("mg.tool.MySession")){
                    params[i] = new MySession(request.getSession());
                }
                else{
                   params[i] = RetourneObjetFromFormulaire(className, request);
                   System.out.println(params[i]);
                }

            }
            else {
                throw new AnnotationException("ETU002566 : Toutes les parametres doivent etre annotees");
           }
        }

        return params;
    }

    public static Object RetourneObjetFromFormulaire(String className, HttpServletRequest request) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object obj = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
        }
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
             System.out.println("Liste des cles dans remove: "+ attributeName);
            session.removeAttribute(attributeName);
        }
    }
}
