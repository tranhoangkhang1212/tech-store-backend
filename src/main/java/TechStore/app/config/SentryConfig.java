package TechStore.app.config;

import TechStore.app.exception.*;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.util.List;

@Configuration
@Slf4j
public class SentryConfig {
    @Value("${sentry.environment}")
    private String environment;

    private static final List<String> ENVIRONMENTS = List.of("dev", "staging", "production");

    @Bean
    Sentry.OptionsConfiguration<SentryOptions> customOptionsConfiguration() {
        log.info("Sentry environment " + environment);
        return options -> {
            if (!ENVIRONMENTS.contains(environment)) {
                options.setDsn("");
            }
            options.setEnableShutdownHook(false);
        };
    }

    @Bean
    SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            if (isNotFoundExceptionEvent(event)) {
                return null;
            }
            if (isVerifyTokenExceptionEvent(event)) {
                return null;
            }
            if (event.getThrowable() instanceof RequestInvalidException){
                return null;
            }
            return event;
        };
    }

    private boolean isNotFoundExceptionEvent(SentryEvent event) {
        if (event.getThrowable() instanceof ResourceNotFoundException){
            return true;
        }
        if (event.getThrowable() instanceof ResourceExistException){
            return true;
        }
        return event.getThrowable() instanceof FileNotFoundException;
    }
    private boolean isVerifyTokenExceptionEvent(SentryEvent event) {
        if (event.getThrowable() instanceof ExceptionUnauthorizedMarket){
            return true;
        }
        if (event.getThrowable() instanceof JWTVerificationException){
            return true;
        }
        if (event.getThrowable() instanceof JWTVerifyException){
            return true;
        }
        return event.getThrowable() instanceof JWTDecodeException;
    }
}
