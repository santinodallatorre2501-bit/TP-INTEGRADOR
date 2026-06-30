package exception;

public class StockInvalidoException extends RuntimeException {
    public StockInvalidoException(String mensaje) {
        super(mensaje);
    }
}