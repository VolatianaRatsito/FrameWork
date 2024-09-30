# FrameWork
Framework:
-Classe GetMapping:Annotatuion au niveau methode
-Classe Mapping:nomClasse,nomMethode
-ListClassController:scannerPackage
ListHashMappMethode=boucle ListClassController pour obtenir listHashMap:cle:url par Annotation,valeur Mapping
On appel apppele ListHashMappMethode dans init
dans:ProcessRequest:On verifie si l'url de l'utilisateur est dans le ListHashMappMethode
    -si oui:return valeurHmap(Mapping) 
    -si non:return erreur
si j'ai les liste de classes avec ca:    this.controllers = f.getClassesFromPackage(packagecontroller);
         creer un fonction pour retourner un hashMap<String,objet> String sera la valeur de @GetMapping et objet sera cette classe:
         public class Mapping {
     String nomClasse;
     String nomMethode;
nomClasse le nom de classe traiter,nomMethode le nom de methode qui est annoter j'ai cette classe:
package org.myspringframework.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
}
je veux qu'il retourne hashMap<String,Mapping>
maintenant creer moi un fonction VerifierSiDansHashMap(String url) il returnera le hashMap genre le url et le Mapping s'il en en ,si non retourne url Not Found
Et creer un classe Exemple annotation@controllers et un methode annoter @GetMapping("/...")
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteMapper {

    public Map<String, Mapping> mapControllersToRoutes(List<Class<?>> controllers) throws ClassNotFoundException {
        Map<String, Mapping> routeMap = new HashMap<>();
        for (Class<?> controller : controllers) {
            try {
                Method[] methods = controller.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping mapping = method.getAnnotation(GetMapping.class);
                        String key = mapping.value(); // Supposons que @GetMapping a une valeur pour le chemin
                        Mapping mappingInfo = new Mapping(controller.getName(), method.getName());
                        if (routeMap.containsKey(key)) {
                            Mapping existingMapping = routeMap.get(key);
                            System.out.println("Conflit détecté pour l'URL '" + key + "':");
                            System.out.println("  Existant : " + existingMapping.getControllerName() + "#" + existingMapping.getMethodName());
                            System.out.println("  Nouveau  : " + mappingInfo.getControllerName() + "#" + mappingInfo.getMethodName());
                        } else {
                            routeMap.put(key, mappingInfo);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return routeMap;
    }

    // Classe interne pour stocker les informations de mapping
    public static class Mapping {
        private String controllerName;
        private String methodName;

        public Mapping(String controllerName, String methodName) {
            this.controllerName = controllerName;
            this.methodName = methodName;
        }

        public String getControllerName() {
            return controllerName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    // Annotation de simulation @GetMapping pour les besoins de cet exemple
    public @interface GetMapping {
        String value();
    }

    public static void main(String[] args) {
        // Simuler des contrôleurs pour tester la méthode
        class ControllerA {
            @GetMapping("/route1")
            public void methodA1() {}

            @GetMapping("/route2")
            public void methodA2() {}
        }

        class ControllerB {
            @GetMapping("/route1")
            public void methodB1() {}

            @GetMapping("/route3")
            public void methodB2() {}
        }

        RouteMapper routeMapper = new RouteMapper();
        try {
            List<Class<?>> controllers = List.of(ControllerA.class, ControllerB.class);
            Map<String, Mapping> routeMap = routeMapper.mapControllersToRoutes(controllers);
            // Afficher les résultats
            for (Map.Entry<String, Mapping> entry : routeMap.entrySet()) {
                System.out.println("URL: " + entry.getKey() + ", Controller: " + entry.getValue().getControllerName() + ", Method: " + entry.getValue().getMethodName());
            }
        } catch (ClassNotFoundException e) {
            e.print
