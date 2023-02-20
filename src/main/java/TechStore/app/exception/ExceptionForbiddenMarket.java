package TechStore.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ExceptionForbiddenMarket extends RuntimeException {
    public ExceptionForbiddenMarket(String msg){
        super(msg);
    }
    public ExceptionForbiddenMarket(String message, Throwable cause) {
        super(message, cause);
    }
}