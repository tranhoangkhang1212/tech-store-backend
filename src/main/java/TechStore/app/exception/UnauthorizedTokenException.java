package TechStore.app.exception;

import TechStore.app.constant.EMessage;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class UnauthorizedTokenException extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write(new JSONObject()
                    .put("timestamp", LocalDateTime.now())
                    .put("message", "Access denied")
                    .put("status", HttpServletResponse.SC_UNAUTHORIZED)
                    .put("error", "Unauthorized")
                    .put("path", request.getRequestURI())
                    .toString());
        } catch (JSONException e) {
            throw new ExceptionUnauthorizedMarket(EMessage.SYSTEM_ERROR.toString());
        }
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("API_MARKETPLACE");
        super.afterPropertiesSet();
    }
}