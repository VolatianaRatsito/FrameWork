package annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Get {
    String value(); // Déjà utilisé pour le chemin ou une autre valeur
    String url() default ""; // Nouveau champ pour l'URL avec une valeur par défaut
}
