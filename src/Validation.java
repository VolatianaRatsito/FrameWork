package mg.tool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import mg.exception.*;
import mg.annotation.*;

public class Validation {

    public static List<ValidationException> validate(Object obj) {
        List<ValidationException> exceptions = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); 
            try {
                Object fieldValue = field.get(obj);
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation instanceof NotNull) {
                        if (fieldValue == null) {
                            exceptions.add(new ValidationException(
                                field.getName(),
                                ((NotNull) annotation).message(),
                                fieldValue
                            ));
                        }
                    }

                    if (annotation instanceof Mail) {
                        if (fieldValue != null && !isValidEmail(fieldValue.toString())) {
                            exceptions.add(new ValidationException(
                                field.getName(),
                                ((Mail) annotation).message(),
                                fieldValue
                            ));
                        }
                    }
                    if (annotation instanceof Max) {
                        int maxValue = ((Max) annotation).value();
                        if (fieldValue != null && fieldValue instanceof Integer) {
                            if ((Integer) fieldValue > maxValue) {
                                exceptions.add(new ValidationException(
                                    field.getName(),
                                    ((Max) annotation).message().replace("{value}", String.valueOf(maxValue)),
                                    fieldValue
                                ));
                            }
                        }
                    }
                    if (annotation instanceof Min) {
                        int minValue = ((Min) annotation).value();
                        if (fieldValue != null && fieldValue instanceof Integer) {
                            if ((Integer) fieldValue < minValue) {
                                exceptions.add(new ValidationException(
                                    field.getName(),
                                    ((Min) annotation).message().replace("{value}", String.valueOf(minValue)),
                                    fieldValue
                                ));
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                exceptions.add(new ValidationException(
                    field.getName(),
                    "Erreur lors de la validation du champ",
                    null
                ));
            }
        }

        return exceptions;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }
}
