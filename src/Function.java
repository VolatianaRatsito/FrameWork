
package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import session.MySession;

import java.io.File;
import java.lang.ModuleLayer.Controller;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import annotation.*;
import util.Mapping;

import com.google.gson.Gson;

public class Function {

    public VerbMethod getVerbMethod(Method m){
        VerbMethod vb = new VerbMethod();
        vb.setMethodName(m.getName());
        if (m.isAnnotationPresent(Post.class)) {
            vb.setVerb("post");
        }else{
            vb.setVerb("get");
        }
        return vb;
    }

    public Object invokeMethod(Method method, Class<?> clazz, HttpServletRequest request) throws Exception{
        Object res = null;
        Object instance = clazz.newInstance();
        Parameter[] params = method.getParameters();
        Object[] paramValues = new Object[params.length];
        int count = 0;
        for (int i = 0; i < params.length; i++) {
            Class<?> paramType = params[i].getType();
            if(paramType.getSimpleName().equals("String") || paramType.getSimpleName().equals("int") || paramType.getSimpleName().equals("double")){
                String inputValue = getInputValue(params[i],request);
                try{
                    Object paramInstance = caster(inputValue,paramType);
                    paramValues[count] = paramInstance;
                    count++;
                }catch(IllegalArgumentException e){throw e;}
            }else{
                Object paramInstance = new Object();
                paramInstance = createParamInstance(params[i],request);
                if(paramInstance!=null){
                    paramValues[count] = paramInstance;
                    count++;
                }else{ throw new Exception ("erreur here");}
            }
        }
        try {
            if (params.length>0 ) {
                res = method.invoke(instance,paramValues);
            }else{
                res = method.invoke(instance);
            }

            // verifier si la méthode a une anotation restAPI
            if (method.isAnnotationPresent(RestAPI.class)) {
                String json = getResponseWithRestAPI(res);
                JsonObject jsonObj = new JsonObject(json);
                return jsonObj;
            }

            return res;
        } catch (IllegalAccessException e) {
            throw e;
        }
    }

    public String getResponseWithRestAPI(Object initialResponse){
        String json = null;
        Gson gson = new Gson();
        if (initialResponse.getClass().getSimpleName().equalsIgnoreCase("ModelView")) {
            ModelView mv = (ModelView)initialResponse;
            HashMap<String,Object> data = mv.getData();
            json = gson.toJson(data);
        }else{
            json =  gson.toJson(initialResponse);
        }
        return json;
    }
    

    // GetInputValue si primitif
    public String getInputValue(Parameter param,HttpServletRequest request) throws Exception {
        String value = null;
        if(param.isAnnotationPresent(AnnotationParameter.class)){
            AnnotationParameter annotation = param.getAnnotation(AnnotationParameter.class);
            String annotationValue = annotation.valeur();
            String res = request.getParameter(annotationValue);
            value = res;
            return value;
        }else{
            throw new Exception("ETU2625 annotation required");
        }
    }

    // Recupérer tous les inputs cooorespondant au paramètre: et setAttribute
    public Object createParamInstance(Parameter param,HttpServletRequest request)throws Exception{
        Object paramInstance = null;
        Class<?> paramType = param.getType();
        if(param.isAnnotationPresent(AnnotationParameter.class)){
            if (paramType.getSimpleName().equals("MySession")) {
                HttpSession session = request.getSession();
                MySession ms = new MySession(session);
                paramInstance = ms;
            }else{
                AnnotationParameter annotation = param.getAnnotation(AnnotationParameter.class);
                String valeur = annotation.valeur();
                paramInstance = paramType.getDeclaredConstructor().newInstance();
                Enumeration<String> inputNames = request.getParameterNames();
                while(inputNames.hasMoreElements()){
                    String inputName = inputNames.nextElement();
                    String[] decomposition = inputName.split("\\.");
                    if(isCorrespondant(decomposition,valeur)){
                        String inputValue = request.getParameter(inputName);
                        Method setMethod = getSetMethod(paramType,decomposition[1]);
                        invokeMethodSet(paramInstance,setMethod,inputValue);
                    }  
    
                }
            }
            return paramInstance;
        }else{
            // String paramName = param.getName();
            // paramInstance = paramType.getDeclaredConstructor().newInstance();
            // Enumeration<String> inputNames = request.getParameterNames();
            // while(inputNames.hasMoreElements()){
            //     String inputName = inputNames.nextElement();
            //     String[] decomposition = inputName.split("\\.");
            //     if(isCorrespondant(decomposition,paramName)){
            //         String inputValue = request.getParameter(inputName);
            //         Method setMethod = getSetMethod(paramType,decomposition[1]);
            //         invokeMethodSet(paramInstance,setMethod,inputValue);
            //     }

            // }
            throw new Exception("ETU2625 annotation required");
        }
    }
    // Invoker methode set 
    public void invokeMethodSet(Object object,Method methSet,String valeur) throws Exception{
        Class<?>[] setParams = methSet.getParameterTypes();
        try{
            Object param = caster(valeur,setParams[0]);
            methSet.invoke(object,param);
        }catch(IllegalArgumentException e){
            throw e;
        }
    }
    // Recuperer methode set
    public Method getSetMethod(Class<?> parameterTypes, String paramAnnotationValue){
        Method[] methods = parameterTypes.getDeclaredMethods();
        Method res = null;
        for (Method method : methods) {
            if(method.getName().equalsIgnoreCase("set"+paramAnnotationValue)){
                res = method;
                break;
            }
        }
        return res;
    }

    // verifier si un input correspond à un paramètre
    public boolean isCorrespondant(String[] decomposition , String paramAnnotationValue){
        if(decomposition.length>1 && decomposition[0].equalsIgnoreCase(paramAnnotationValue)){
            return true;
        }
        return false;
    }
    
    // fonction pour caster un string en objet
    public Object caster (String s, Class<?> type) throws IllegalArgumentException{
        if (type==int.class) {
            return Integer.parseInt(s);
        }else if (type==double.class) {
            return Double.parseDouble(s);
        }else if (type==String.class) {
            return s;
        }
        throw new IllegalArgumentException("Type d'argument non supporté");
    }

    // Verifier si une classe est un controller
    public static boolean isController(Class<?> c){
        if (c.isAnnotationPresent(AnnotationController.class)) {
            return true;
        }
        
        return false;
    }


    // recuperer tous les methodes annotes Url dans un controller
    public List<Method> getAnnotatedMethods(Class<?> c)throws Exception{
        Method[] methods = c.getDeclaredMethods();
        List<Method> res =new ArrayList<>();
        for (Method method : methods) {
            Annotation annotation = method.getAnnotation(Url.class);
            if (annotation!=null) {
                res.add(method);
            }
        }
        return res;
    }


    // Retourner la valeur de l'annotation
    public String getAnnotationValue(Method m)throws Exception{
        String value = null;
        Url annotation = m.getAnnotation(Url.class);
        if (annotation!=null) {
            value = annotation.value();
        }
        if (value!=null) {
            return value;
        }else{
            throw new Exception("missing value");
        }

    }

    // Retourner toutes les classes dans un package
    public List<Class<?>> findClasses(String directoryPath, String packageName) throws Exception {
        String packaString = packageName.replace(".", "/");
        directoryPath = directoryPath+"/"+packaString;
        File directory = new File(directoryPath);
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            throw new Exception("Le package spécifié n'existe pas!");
        }

        File[] files = directory.listFiles();
        if (files.length==0) {
            throw new Exception("Le package spécifié est vide!");
        }

        Class<?> temp = null;
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                temp = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                classes.add(temp);
            }
        }
        return classes;
    }

    // Retourner toutes les classes qui sont des Controllers
    public List<Class<?>> findController(String directory, String packageName)throws Exception{
        List<Class<?>> allClasses = this.findClasses(directory, packageName);
        List<Class<?>> controllers = new ArrayList<>();
        for (Class<?> class1 : allClasses) {
            if (Function.isController(class1)) {
                controllers.add(class1);
            }
        }
        return controllers;
    }

    // // Recuperer toutes les classes qui ont des methodes anotes Get
    public List<Class<?>> findControllerAndMethod(String directory, String packageName)throws Exception{
        List<Class<?>> allClasses = this.findController(directory, packageName);
        List<Class<?>> controllers = new ArrayList<>();
        for (Class<?> class1 : allClasses) {
            try {
                List<Method> m = this.getAnnotatedMethods(class1);
                if (m.size()>0) {
                    controllers.add(class1);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return controllers;
    }
}

