package TechStore.app.exception;

import TechStore.app.constant.EMessage;
import lombok.Getter;

@Getter
public class RequestInvalidException extends RuntimeException{
    private final EMessage eMessage;
    private final  Integer code;

    public RequestInvalidException(EMessage eMessage) {
        super(eMessage.getMessage());
        this.eMessage = eMessage;
        this.code = eMessage.getCode();
    }
    public RequestInvalidException(String msg, int code) {
        super(msg);
        this.eMessage = null;
        this.code = code;
    }
}