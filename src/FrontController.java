package mg.controller.principal;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.tool.*;
import mg.annotation.*;
import mg.exception.*;
import java.net.URL;
import java.net.URLDecoder;
import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
public class FrontController extends HttpServlet {
    List<Class<?>> controllers = null;
    HashMap<String, Mapping> lists = new HashMap<>();
    String prefix;
    String suffix;
    String verbName;

 @Override
    public void init() throws ServletException {
        try {
            initVariables();
        } catch (DuplicateKeyException | EmptyPackageException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    private void initVariables() throws ServletException,DuplicateKeyException,EmptyPackageException {
        this.prefix = getServletContext().getInitParameter("prefix");
        this.suffix = getServletContext().getInitParameter("suffix");
        
        String controllerPackage = getInitParameter("controllers");
        if (controllerPackage != null && !controllerPackage.isEmpty()) {
            try {
                controllers = Util.getClassesFromPackage(controllerPackage);
                    
                    if(controllers.isEmpty()){
                        throw new EmptyPackageException("The package " + controllerPackage + " is empty.");
                    }

                for (Class<?> class1 : controllers) {
                    Method[] methods = class1.getDeclaredMethods();
                    for (Method method : methods) {              
                        
                        if (method.isAnnotationPresent(Url.class)) {
                            Url getAnnotation = method.getAnnotation(Url.class);
                            String annotationValue = getAnnotation.value();
                            
                            System.out.println("Valeur de l'annotation url: "+ annotationValue);
                            Annotation[] annotations = method.getAnnotations();
                          

                            // Parcourir les annotations et ignorer @Url
                            for (Annotation annotation : annotations) {
                                if (!annotation.annotationType().equals(Url.class)) {
                                    verbName = annotation.annotationType().getSimpleName();
                                }
                            }

                            String methodName = method.getName();
                            String className = class1.getName();
                            System.out.println("className trouve : " + className);
                            System.out.println("methodName trouve : " + methodName);
                            
                            VerbMethod verbMethod = new VerbMethod();
                            verbMethod.setMethodName(methodName);
                            verbMethod.setVerb(verbName);
                            
                            Set<VerbMethod> verbMethodSet = new HashSet<>();
                            verbMethodSet.add(verbMethod);

                            Mapping newMapping = new Mapping();
                            newMapping.setClassName(className);
                            newMapping.setVerbMethods(verbMethodSet);

                            if (lists.containsKey(annotationValue)) {
                                Mapping foundMapping = lists.get(annotationValue);
                                if (newMapping.containsVerbMethodFromObject(foundMapping)) {
                                    throw new Exception("L'url "+ annotationValue + "existe deja ");
                                }
                                foundMapping.addVerbMethod(verbMethod);
                            }

                            lists.put(annotationValue, newMapping);
                        }

                    }
                }
            } catch (Exception e) {
                throw new ServletException("Error initializing controllers from package: " + controllerPackage, e);
            }
        } else {
            throw new ServletException("No 'controllers' parameter provided.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.processRequest(request, response);
        } catch (Exception e) {
           e.printStackTrace();
        }
     
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException,Exception {
        String url = request.getRequestURI().substring(request.getContextPath().length());
        try {
            PrintWriter out = response.getWriter();
            out.println("You successfully arrived into FrontController, URL :" + url);

            if (this.controllers != null) {
                out.println("Voici la liste de vos controllers : ");
                for (Class<?> class1 : this.controllers) {
                    out.println(class1.getName());
                }
                out.println("url " + url);
                System.out.println("Notre url: "+ url);
                if (lists.containsKey(url)) {
                    Mapping mapping = lists.get(url);
                    String responseMethod = request.getMethod();
                     if (responseMethod == null) {
                        responseMethod = "GET"; 
                    }   
                    VerbMethod vb = mapping.getVerbMethodByUrl(responseMethod);                    
                    if (vb == null) {
                        ErrorHandler.sendError(request, response, "L'annotation de la méthode doit correspondre à l'annotation du formulaire.", HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    String verbName = vb.getVerb();

                    if (!verbName.equalsIgnoreCase(responseMethod) || verbName == null) {
                        ErrorHandler.sendError(request, response, "L'annotation de la méthode doit correspondre à l'annotation du formulaire.", HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    
                    String methodName = vb.getMethodName();
                    String className = mapping.getClassName();

                    out.println("Nom de la classe : " + className);
                    out.println("Nom de la methode : " + methodName);
                     
                    try {
                        // Object answer = Util.executeMethod(className, methodName);
                        Object answer = ReflectionUtil.invokeMethodWithRequestParams(className, methodName, request, response);
                        if (answer == null) {
                            return; // On sort de la méthode
                        }
                        
                        else if (answer instanceof String) {
                            String rep = (String) answer;
                        } 
                        else if (answer instanceof ModelAndView) {
                            ModelAndView rep = (ModelAndView) answer;
                            String urljsp = this.prefix + rep.getUrl() + this.suffix;
                            Map<String, Object> data = rep.getData();
                            if (data.isEmpty()) {
                                System.out.println("La map est vide.");
                            } else {
                                // Affichage des paires clé-valeur en utilisant entrySet()
                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                    System.out.println("Clé: " + entry.getKey() + ", Valeur: " + entry.getValue());
                                }
                            }
                            Set<String> keys = data.keySet();
                            for (String key : keys) {
                                Object value = data.get(key);
                                request.setAttribute(key, value);
                            }
                            request.getRequestDispatcher(urljsp).forward(request, response);
                        }
                        else {
                            throw new InvalidReturnTypeException("Invalid return type: " + answer.getClass().getName()+ ". Le type de retour doit etre un string ou un ModelAndView");
                        }

                    } catch (Exception e) {
                        out.println(e.getMessage());
                    }
                } else {
                    out.println("aucune méthode trouvée pour ce genre d'url");
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }
}
