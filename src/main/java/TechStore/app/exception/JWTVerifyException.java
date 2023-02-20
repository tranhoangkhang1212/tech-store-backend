package TechStore.app.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JWTVerifyException extends JWTVerificationException {
    public JWTVerifyException(String message) {
        super(message);
    }

    public JWTVerifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
