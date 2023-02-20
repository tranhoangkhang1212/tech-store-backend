package TechStore.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ExceptionUnauthorizedMarket extends RuntimeException {
    public ExceptionUnauthorizedMarket(String msg){
        super(msg);
    }
    public ExceptionUnauthorizedMarket(String message, Throwable cause) {
        super(message, cause);
    }
}
