package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;

import util.Function;
import util.JsonObject;
import util.Mapping;
import util.ModelView;
import util.MySet;
import util.VerbMethod;

public class FrontController extends HttpServlet {
    List<Class<?>> listeController = null;
    String path = null;
    String packageController = null;
    HashMap<String, Mapping> hmap = new HashMap<>();
    Function f = new Function();

    public void init() throws ServletException {
        try {
            ServletContext servletContext = getServletContext();
            path = servletContext.getRealPath("WEB-INF/classes");
            packageController = getServletContext().getInitParameter("packageController");
            listeController = f.findControllerAndMethod(path, packageController);

            for (Class<?> class1 : listeController) {
                List<Method> listMethods = f.getAnnotatedMethods(class1);

                for (Method method : listMethods) {
                    String url = f.getAnnotationValue(method);
                    VerbMethod vb = f.getVerbMethod(method);

                    if (!hmap.containsKey(url)) {
                        Mapping mapping = new Mapping();
                        mapping.setClassName(class1.getName());
                        vb.setParameterTypes(method.getParameterTypes());
                        mapping.addVerbMethod(vb);
                        hmap.put(url, mapping);
                    } else {
                        Mapping mapping = hmap.get(url);
                        if (containsVerb(mapping.getVerbMethods(), vb.getVerb())) {
                            log("Duplicate verb detected: " + vb.getVerb() + " for URL: " + url);
                            throw new ServletException("L'URL " + url + " EXISTE DÉJÀ AVEC LE MÊME VERBE!");
                        } else if (mapping.getVerbMethods().contains(vb)) {
                            log("Duplicate method name detected: " + vb.getMethodName() + " for URL: " + url);
                            throw new ServletException("L'URL " + url + " EXISTE DÉJÀ AVEC LA MÊME MÉTHODE!");
                        } else {
                            vb.setParameterTypes(method.getParameterTypes());
                            mapping.addVerbMethod(vb);
                        }
                    }
                }
            }
        } catch (ServletException e) {
            log("Initialization error: " + e.getMessage());
            throw new ServletException(e.getMessage());
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'initialisation du contrôleur: " + e.getMessage());
        }
    }

    private boolean containsVerb(MySet<VerbMethod> verbMethods, String verb) {
        for (VerbMethod vb : verbMethods) {
            if (vb.getVerb().equalsIgnoreCase(verb)) {
                return true;
            }
        }
        return false;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String urlCourant = request.getServletPath();
            if (hmap.containsKey(urlCourant)) {
                Mapping map = hmap.get(urlCourant);
                Class<?> clazz = Class.forName(map.getClassName());
                MySet<VerbMethod> verbMethods = map.getVerbMethods();

                VerbMethod vm = verbMethods.getVerbMethodCorresp(request.getMethod());

                if (vm != null) {
                    Method meth = clazz.getDeclaredMethod(vm.getMethodName(), vm.getParameterTypes());
                    Object retour = f.invokeMethod(meth, clazz, request);

                    if (retour instanceof JsonObject) {
                        JsonObject jsobj = (JsonObject) retour;
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        out.println(jsobj.getJsonValue());
                    } else if (retour instanceof String) {
                        out.println("<h2>Method value: " + retour + "</h2>");
                    } else if (retour instanceof ModelView) {
                        ModelView mv = (ModelView) retour;
                        String url = mv.getUrl();
                        HashMap<String,Object> data = mv.getData();
                        data.forEach((key,value)->{
                            request.setAttribute(key,value);
                        });
                        RequestDispatcher dispacht = request.getRequestDispatcher(url);
                        dispacht.forward(request, response);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "TYPE DE RETOUR NON RECONNU");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "VERB OU METHOD INCORRECT");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL NOT FOUND");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
