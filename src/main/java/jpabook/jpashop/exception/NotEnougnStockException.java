package jpabook.jpashop.exception;

public class NotEnougnStockException extends RuntimeException {
    public NotEnougnStockException() {
        super();
    }

    public NotEnougnStockException(String message) {
        super(message);
    }

    public NotEnougnStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnougnStockException(Throwable cause) {
        super(cause);
    }

}
