package mg.controller;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import mg.annotation.*;

public class SecurityHandler implements InvocationHandler {

    private final Object target; // L'objet original à proxifier
    private final Map<String, String> session; // Simule une session utilisateur

    public SecurityHandler(Object target, Map<String, String> session) {
        this.target = target;
        this.session = session;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Vérifie si la méthode est annotée avec @Authentified
        if (method.isAnnotationPresent(Authentified.class)) {
            // Récupère la valeur de la session "auth"
            String authValue = session.get("auth");
            if (authValue == null) {
                throw new SecurityException("Accès refusé : utilisateur non authentifié.");
            }

            // Vérifie si la méthode est annotée avec @Role
            if (method.isAnnotationPresent(Role.class)) {
                Role roleAnnotation = method.getAnnotation(Role.class);
                String[] requiredRoles = roleAnnotation.value();
                String userRole = session.get("role");

                if (userRole == null || !containsRole(requiredRoles, userRole)) {
                    throw new SecurityException("Accès refusé : rôle insuffisant.");
                }
            }
        }

        // Si tout est OK, exécute la méthode originale
        return method.invoke(target, args);
    }

    // Vérifie si le rôle de l'utilisateur est dans la liste des rôles requis
    private boolean containsRole(String[] requiredRoles, String userRole) {
        for (String role : requiredRoles) {
            if (role.equals(userRole)) {
                return true;
            }
        }
        return false;
    }
}