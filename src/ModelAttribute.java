package mg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.swing.text.html.parser.Element;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.METHOD})
public @interface ModelAttribute {
    String value() default "";
}
