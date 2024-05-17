package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.*;
import java.net.URL;
import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import util.Fonction;
import java.net.URL;
import java.net.URLDecoder;

public class FrontController extends HttpServlet {
     String packageName;
     List<Class<?>> controllerClasses = null;
     boolean isChecked = false;

   
    public void initVariable() throws ServletException {
        packageName = getServletContext().getInitParameter("packageName");
         try{
            Fonction fonction = new Fonction();
            this.controllerClasses = fonction.getControllerNames(packageName);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
     
    }

    protected void ProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getRequestURL().toString();
        response.setContentType("text/html");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("Bienvenue dans la page ");
             if(isChecked==false){
                this.initVariable();
                isChecked=true;
             }

            // Utiliser le nom du package comme vous le souhaitez
             out.println("Package name: " + packageName);

               // Afficher la liste des contrôleurs
                out.println("Contrôleurs trouvés :");
                for (Class<?> controllerClass : controllerClasses) {
                    out.println("- " + controllerClass.getName());
                }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProcessRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProcessRequest(request, response);
    }
}