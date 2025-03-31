package mg.exception;

public class ValidationException extends Exception {
    private final String fieldName;
    private final String message;
    private final String fieldValue;  // Valeur du champ pour aider dans le débogage

    public ValidationException(String fieldName, String message, String fieldValue) {
        super(message);  // Appelle le constructeur de la classe parente Exception
        this.fieldName = fieldName;
        this.message = message;
        this.fieldValue = fieldValue;  // Initialisation de la valeur du champ
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getFieldValue() {
        return fieldValue;  // Retourne la valeur du champ
    }

    @Override
    public String toString() {
        // Vous pouvez choisir d'ajouter la valeur du champ dans la méthode toString pour un meilleur affichage
        return "ValidationException: [Champ: " + fieldName + ", Message: " + message + ", Valeur: " + fieldValue + "]";
    }
}
