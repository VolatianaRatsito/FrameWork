package mg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Authentified {
    // Vous pouvez ajouter des éléments à l'annotation si nécessaire
    // Par exemple, un élément pour spécifier le niveau d'authentification requis
    String level() default "user";
}