package mg.exception;

public class ValidationException extends Exception {
    private final String fieldName;
    private final String message;

    public ValidationException(String fieldName, String message) {
        super(message);  // Appelle le constructeur de la classe parente Exception
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return message;
    }
}