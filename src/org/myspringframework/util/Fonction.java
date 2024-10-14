package org.myspringframework.util;
import java.lang.annotation.Annotation;
import org.mysprint.annotation.*;
import java.io.*;
import java.lang.reflect.Modifier;
import java.util.stream.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.mysprint.annotation.*;
public class Fonction {
    public Fonction(){

    }
    public static boolean isController(Class<?> c){
        Annotation[] existings = c.getAnnotations();
        for (Annotation annotation : existings) {
            if (annotation.annotationType().getName().equals("org.mysprint.annotation.Controller") || 
            annotation.annotationType().getName().equals("org.mysprint.annotation.RestApi")) {
                return true;
            }        
        }
        return false;
    }

    public static Annotation getAnnotationClass(Object o){
        Annotation[] temp = o.getClass().getAnnotations();
        for (Annotation annotation : temp) {
            if(annotation.annotationType().getSimpleName().equals("Controller"))
                return annotation;
        }
        return null;
    }
    public List<Class<?>> getClassesFromPackage(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<Class<?>> classes = new ArrayList<>();
        if (!resources.hasMoreElements()) {
            Package pkg = Package.getPackage(packageName);
            if (pkg == null) {
                throw new ClassNotFoundException("Le package n'existe pas : " + packageName);
                }
        } else {
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(URLDecoder.decode(resource.getPath(), "UTF-8")));
            }
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
            if (classes.isEmpty()) {
                throw new ClassNotFoundException("Le package existe mais il est vide : " + packageName);
            }
        }
            return classes;    
    }
    public List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }
        Class<?> temp = null;
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                temp = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                if(Fonction.isController(temp))
                    classes.add(temp);
            }
        }
        return classes;
    }
    public Map<String, Mapping> mapControllersToRoutes(List<Class<?>> controllers) throws Exception {
        Map<String, Mapping> routeMap = new HashMap<>();
        String basePath="";
        for (Class<?> controller : controllers) {
            if (controller.isAnnotationPresent(RestApi.class)) {
            RestApi restApi = controller.getAnnotation(RestApi.class);
            basePath = restApi.value();  
            }
            
                Method[] methods = controller.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping mapping = method.getAnnotation(GetMapping.class);
                        String key = basePath+mapping.value(); // Supposons que @GetMapping a une valeur pour le chemin
                        Mapping mappingInfo = new Mapping(controller.getName(), method.getName());
                        if (routeMap.containsKey(key)) {
                            Mapping existingMapping = routeMap.get(key);
                            Class<?> controllerE=Class.forName(existingMapping.getNomClasse());
                            throw new Exception(" Redondance de l' url'" + key +  " du classe "+controller.getName()+"au niveau du methode :"+method.getName() +".\n" +"Cette url est deja declarer dans la classe : "+controllerE.getName()+" au niveau de methode :"+existingMapping.getNomMethode());
                        } else {
                            routeMap.put(key, mappingInfo);
                        }
                    }
                    else if(method.isAnnotationPresent(RestApi.class)){

                    }
                }
            basePath="";
            
        }
        return routeMap;
    }

    public Object executeMethod(String nomClasse, String nomMethode) {
    try {
        Class<?> clazz = Class.forName(nomClasse);
        Method method = clazz.getDeclaredMethod(nomMethode);
        method.setAccessible(true);
        return method.invoke(clazz.newInstance());
    } catch (Exception e) {
        e.printStackTrace();
        return "Erreur lors de l'exécution de la méthode";
    }
}

     public Mapping verifierSiDansHashMap(Map<String, Mapping> routeMap, String url) {
        Mapping mapping = routeMap.get(url);
            return mapping;
    }
}
