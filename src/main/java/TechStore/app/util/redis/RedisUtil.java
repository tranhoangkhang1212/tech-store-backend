package TechStore.app.util.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {
    @Value("${environment.env}")
    private String env;

    @Value("${spring.application.name}")
    private String appName;

    private static final String USER_TOKEN = "UserToken";

    private static final String USER_LAST_ONLINE = "UserLastOnline";

    private static final String EMPLOYEE_TOKEN = "EmployeeToken";

    public String redisPrefix() {
        return env + ":" + appName + ":";
    }

    public String redisPrefixWithoutColon() {
        return env + ":" + appName;
    }

    public String buildRedisKey(String key) {
        return redisPrefix() + key;
    }

    public String buildUserTokenKey(Long userId) {
        return buildRedisKey(USER_TOKEN + ":" + userId);
    }

    public String buildEmployeeTokenKey(Long userId) {
        return buildRedisKey(EMPLOYEE_TOKEN + ":" + userId);
    }

    public String buildUserLastOnlineKey(Long userId) {
        return buildRedisKey(USER_LAST_ONLINE + ":" + userId);
    }
}

