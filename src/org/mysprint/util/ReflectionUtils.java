package org.mysprint.util;

import org.mysprint.annotation.Column;
import org.mysprint.annotation.ModelAttribute;
import org.mysprint.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

public class ReflectionUtils {

    public static Object invokeMethodWithRequestParams(String className, String methodName, HttpServletRequest request) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object object = clazz.getDeclaredConstructor().newInstance();

        Method method = getMethodWithRequestParams(clazz, methodName);
        if (method == null) {
            throw new NoSuchMethodException("No suitable method found for " + methodName);
        }

        Object[] params = getParameters(request, method);
        method.setAccessible(true);
        return method.invoke(object, params);
    }

    public static Method getMethodWithRequestParams(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
    public static Object[] getParameters(HttpServletRequest request, Method method) throws Exception {
    Parameter[] parameters = method.getParameters();
    Object[] params = new Object[parameters.length];
    int i = 0;
    for (Parameter parameter : parameters) {
        String paramName = parameter.getName();
        Class<?> paramType = parameter.getType();
        String paramValue = request.getParameter(paramName);
        System.out.println(paramType);
        System.out.println(paramType.getSimpleName());
        if ((paramType.getSimpleName()).equals("MySession")) {
            MySession session = new MySession(request.getSession());
            params[i] = session;
        } else if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            String requestParamName = requestParam.value();
            params[i] = convertType(request.getParameter(requestParamName), paramType);
        } else if (parameter.isAnnotationPresent(ModelAttribute.class)) {
            params[i] = RetourneObjetFromFormulaire(paramType.getName(), request);
        } else {
            throw new Exception("Unsupported parameter type or annotation");
        }
        i++;
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
                  System.out.println( " Type: " + targetType.getSimpleName() +", value: " + value);
        if (value == null) {
            return null;
        }
        else if (targetType == String.class || (targetType.getSimpleName()).equals("String")) {
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
        // Ajoutez d'autres types si nécessaire
        return value;
    }
}
