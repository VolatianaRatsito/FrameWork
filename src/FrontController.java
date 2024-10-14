package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

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
    String path=null;
    String packageController =null;
    HashMap<String,Mapping> hmap = new HashMap<>();
    Function f = new Function();

    public void init() throws ServletException{
        try {
            ServletContext servletContext = getServletContext();
            path =servletContext.getRealPath("WEB-INF/classes");
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
                    }else{
                        Mapping mapping = hmap.get(url);
                        if (!mapping.getVerbMethods().equals(vb) && mapping.getClassName().equalsIgnoreCase(class1.getName())) {
                            mapping.addVerbMethod(vb);
                            vb.setParameterTypes(method.getParameterTypes());
                        }else{
                            throw new ServletException("L'URL "+url+" EXISTE DEJA!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try{
            PrintWriter out = response.getWriter();
            String urlCourant = request.getServletPath();
            if (hmap.containsKey(urlCourant)) {
                Mapping map = hmap.get(urlCourant);
                Class<?> clazz = Class.forName(map.getClassName());
                MySet<VerbMethod> verbMethods = map.getVerbMethods();
                // Check du type de veb de l'url appelant:
                VerbMethod vm = verbMethods.getVerbMethodCorresp(request.getMethod());
                if (vm != null) {
                    Method meth = clazz.getDeclaredMethod(vm.getMethodName(), vm.getParameterTypes());
                    Object retour = null;
                    try {
                        retour = f.invokeMethod(meth, clazz, request);
                    } catch (Exception e) {
                        // request.setAttribute("erreur",e.getMessage());
                        // RequestDispatcher dispacht = request.getRequestDispatcher("/erreur.jsp");
                        // dispacht.forward(request,response);
                        response.sendError(400,e.getMessage());
                    }
                    String valres = null;
                    if (retour.getClass().getSimpleName().equalsIgnoreCase("JsonObject")) {
                        JsonObject jsobj = (JsonObject)retour;
                        String jsonVal = jsobj.getJsonValue();
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        out.println(jsonVal);
                    }else if (retour.getClass().getSimpleName().equalsIgnoreCase("String")) {
                        valres = (String)retour;
                        out.println("<h2>Method value: " + valres + "</h2>");
                    }else if (retour.getClass().getSimpleName().equalsIgnoreCase("ModelView")) {
                        ModelView mv = (ModelView)retour;
                        String url = mv.getUrl();
                        HashMap<String,Object> data = mv.getData();
                        data.forEach((key,value)->{
                            request.setAttribute(key,value);
                        });
                        RequestDispatcher dispacht = request.getRequestDispatcher(url);
                        dispacht.forward(request,response);                                           
                    }else{
                        response.sendError(400,"TYPE DE RETOUR NON RECONNU");
                    }
                }else{
                    response.sendError(400,"VERB OU METHOD INCORRECT");
                }
            }else{
                response.sendError(404, "URL NOT FOUND");
            }
            }catch(Exception e){
                response.sendError(400,e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}