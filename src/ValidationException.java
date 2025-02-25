package mg.exception;

public class ValidationException extends RuntimeException {
    private String fieldName;
    private String message;
    private Object fieldValue; 

    public ValidationException(String fieldName, String message, Object fieldValue) {
        super(message);
        this.fieldName = fieldName;
        this.message = message;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Erreur dans le champ '" + fieldName + "' (valeur : " + fieldValue + "): " + message;
    }
}
