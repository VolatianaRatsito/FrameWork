package mg.tool;
import java.lang.annotation.Annotation;
import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.tool.Util;
import java.net.URL;
import java.net.URLDecoder;
import java.lang.reflect.*;

public class Util {

    // pour voir si une classe est annotée de Controller
    public static boolean isController(Class<?> c){
        Annotation[] existings = c.getAnnotations();
        for (Annotation annotation : existings) {
            if (annotation.annotationType().getName().equals("mg.annotation.Controller")) {
                return true;
            }        
        }
        return false;
    }

    // Trouver et retourner l'annotation Controller sur un objet donné (o).
    public static Annotation getAnnotationClass(Object o){
        Annotation[] temp = o.getClass().getAnnotations();
        for (Annotation annotation : temp) {
            if(annotation.annotationType().getSimpleName().equals("Controller"))
                return annotation;
        }
        return null;
    }

    // rechercher toutes les classes dans un répertoire donné et qui correspondent à un nom de package donné
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
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
            // si le fichier est un dossier
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                //appel récursif de la fonction
                classes.addAll(Util.findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                temp = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                if(Util.isController(temp))
                    classes.add(temp);
            }
        }
        return classes;
    }

    // chercher toutes les classes dans un package donné qui sont des controllers
      public static List<Class<?>> getClassesFromPackage(String packageName) throws IOException, ClassNotFoundException {
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

        public static Object executeMethod(String className, String methodName) {
        try {
            // Charger la classe à partir de son nom
            Class<?> clazz = Class.forName(className);

            // Trouver la méthode par son nom (en supposant qu'elle n'a pas de paramètres)
            Method method = clazz.getMethod(methodName);

            // Créer une instance de la classe
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // Exécuter la méthode sur l'instance créée
            return method.invoke(instance);

        } catch (ClassNotFoundException e) {
            System.out.println("Classe non trouvée : " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.out.println("Méthode non trouvée : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de l'exécution de la méthode : " + e.getMessage());
        }

        return null;
    }

    public static List<Field> getAllFields(Object object) throws IllegalArgumentException, IllegalAccessException {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = object.getClass();
        
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                fields.add(field);
            }
            // Remonter dans la hiérarchie des classes
            clazz = clazz.getSuperclass();
        }
        
        return fields;
    }
}
