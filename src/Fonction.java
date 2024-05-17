package util;

import java.lang.annotation.Annotation;
import java.io.*;
import java.lang.reflect.Modifier;
import java.util.stream.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Fonction {
    public Fonction(){

    }
    public static boolean isController(Class<?> c){
        Annotation[] existings = c.getAnnotations();
        for (Annotation annotation : existings) {
            if (annotation.annotationType().getName().equals("org.myspringframework.annotation.Controller")) {
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
        public List<Class<?>> getControllerNames(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(URLDecoder.decode(resource.getPath(), "UTF-8")));
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
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


}
