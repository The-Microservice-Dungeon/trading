package dungeon.trading.core.exceptions;

public class RequestReturnedErrorException extends RuntimeException {
    public RequestReturnedErrorException(String reason) {
        super(reason);
    }
}
