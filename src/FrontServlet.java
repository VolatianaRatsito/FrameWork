package controller;

import util.Mapping;
import util.ModelView;
import util.*;
import annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontServlet extends HttpServlet {

    // Simuler une base de données des mappings entre URL et classes/méthodes
    private Map<String, Mapping> urlMapping = new HashMap<>();

    // Initialiser les mappings dans le constructeur ou une méthode d'initialisation
    @Override
    public void init() throws ServletException {
        try {
            // Ajouter ici les mappings, par exemple pour une classe Controller avec une méthode annotée
            Class<?> controllerClass =Controller.class; // Remplacer par votre classe réelle
            for (Method method : controllerClass.getMethods()) {
                if (method.isAnnotationPresent(Get.class)) {
                    Get getAnnotation = method.getAnnotation(Get.class);
                    String url = getAnnotation.url();
                    urlMapping.put(url, new Mapping(controllerClass.getName(), method.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Récupérer l'URL demandée
            String requestUrl = request.getRequestURI();

            // Vérifier si l'URL correspond à un mapping
            if (urlMapping.containsKey(requestUrl)) {
                Mapping mapping = urlMapping.get(requestUrl);

                // Charger dynamiquement la classe et la méthode correspondante
                Class<?> controllerClass = Class.forName(mapping.getNomClasse());
                Object controller = controllerClass.getDeclaredConstructor().newInstance();
                Method method = controllerClass.getMethod(mapping.getNomMethode());

                // Vérifier si la méthode est annotée avec @Get
                if (method.isAnnotationPresent(Get.class)) {
                    Object result = method.invoke(controller);

                    // Si le résultat n'est pas une instance de ModelView
                    if (!(result instanceof ModelView)) {
                        // Conversion manuelle du résultat en JSON
                        StringBuilder jsonResponse = new StringBuilder();
                        jsonResponse.append("[");

                        // Supposons que le résultat soit une liste d'objets
                        if (result instanceof Iterable) {
                            for (Object item : (Iterable<?>) result) {
                                jsonResponse.append(objectToJson(item)).append(",");
                            }
                            // Suppression de la dernière virgule si nécessaire
                            if (jsonResponse.length() > 1) {
                                jsonResponse.setLength(jsonResponse.length() - 1);
                            }
                        }
                        jsonResponse.append("]");

                        // Envoi de la réponse en JSON
                        response.setContentType("application/json");
                        PrintWriter out = response.getWriter();
                        out.print(jsonResponse.toString());
                        out.flush();
                    } else {
                        // Si c'est une instance de ModelView, récupérer les données et les transformer en JSON
                        ModelView mv = (ModelView) result;
                        String jsonResponse = objectToJson(mv.getData());

                        response.setContentType("application/json");
                        PrintWriter out = response.getWriter();
                        out.print(jsonResponse);
                        out.flush();
                    }
                }
            } else {
                // Si l'URL n'est pas mappée, renvoyer une réponse JSON indiquant l'erreur
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"error\": \"404 - Not Found\"}");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour convertir un objet en chaîne JSON manuellement
    private String objectToJson(Object obj) {
        if (obj instanceof String) {
            return "\"" + obj.toString() + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Iterable) {
            StringBuilder jsonArray = new StringBuilder("[");
            for (Object item : (Iterable<?>) obj) {
                jsonArray.append(objectToJson(item)).append(",");
            }
            // Supprimer la dernière virgule
            if (jsonArray.length() > 1) {
                jsonArray.setLength(jsonArray.length() - 1);
            }
            jsonArray.append("]");
            return jsonArray.toString();
        }
        return "\"\"";
    }
}
