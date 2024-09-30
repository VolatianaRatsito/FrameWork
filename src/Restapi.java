package annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) // Permet à l'annotation d'être accessible à l'exécution
public @interface Restapi {
    String value() default ""; // Vous pouvez ajouter des paramètres si besoin
}
