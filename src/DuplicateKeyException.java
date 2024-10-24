package mg.exception;

public class DuplicateKeyException extends Exception{
    public DuplicateKeyException(String message){
        super(message);
    }
}