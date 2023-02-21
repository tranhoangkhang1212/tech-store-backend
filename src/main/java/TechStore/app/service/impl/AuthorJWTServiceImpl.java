package TechStore.app.service.impl;

import TechStore.app.constant.ConstantApi;
import TechStore.app.constant.EMessage;
import TechStore.app.constant.InitialUserDataValue;
import TechStore.app.dto.TokenInfoDto;
import TechStore.app.dto.VerifyTokenDto;
import TechStore.app.dto.redis.UserToken;
import TechStore.app.entity.MarketPlaceKey;
import TechStore.app.exception.JWTVerifyException;
import TechStore.app.exception.RequestInvalidException;
import TechStore.app.repository.MarketPlaceKeyRepository;
import TechStore.app.service.AuthorJWTService;
import TechStore.app.util.EncryptUtil;
import TechStore.app.util.redis.RedisUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthorJWTServiceImpl implements AuthorJWTService {

    @Value("${jwt.expire_time}")
    private long timeExpire;
    @Value("${jwt.expire_time_admin}")
    private long timeExpireAdmin;
    @Autowired
    private MarketPlaceKeyRepository marketPlaceKeyRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    private Algorithm algorithmUser = null;
    private SecretKey userAesSecretKey = null;
    private Algorithm algorithmEmployee = null;
    private SecretKey employeeAesSecretKey = null;


    @SuppressWarnings("unchecked")
    @Override
    public TokenInfoDto generateToken(String email, Long id, String role, boolean isEmployee){
        Algorithm algorithm;
        SecretKey secretKey;
        String subject;
        String audience;
        String strPayload;
        String strHeader;
        long timeTokenExpire;
        String origin = httpServletRequest.getHeader("origin") != null ? httpServletRequest.getHeader("origin") : "";
        String userAgent = httpServletRequest.getHeader("user-agent") != null ? httpServletRequest.getHeader("user-agent") : "";
        if(isEmployee) {
            algorithm = getAlgorithmEmployee();
            secretKey = getEmployeeAesSecretKey();
            subject = ConstantApi.JWT_SUBJECT_EMPLOYEE;
            audience = ConstantApi.JWT_AUDIENCE_EMPLOYEE;
            strPayload = "Employee Payload";
            strHeader = "Employee Header";
            timeTokenExpire = timeExpireAdmin;
        } else {
            algorithm = getAlgorithmUser();
            secretKey = getUserAesSecretKey();
            subject = ConstantApi.JWT_SUBJECT;
            audience = ConstantApi.JWT_AUDIENCE;
            strPayload = "User Payload";
            strHeader = "User Header";
            timeTokenExpire = timeExpire;
        }
        String token = "";
        long currentTimestamp = System.currentTimeMillis();
        long expiresAt = currentTimestamp + timeTokenExpire;
        Map<String, String> payload = new HashMap<>();
        payload.put("payload", strPayload);
        Map<String, Object> header = new HashMap<>();
        header.put("header", strHeader);
        token = JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(expiresAt))
                .withIssuer(ConstantApi.SYS_MARKETPLACE)
                .withAudience(audience)
                .withExpiresAt(new Date(expiresAt))
                .withClaim(ConstantApi.JWT_EMAIL, email)
                .withClaim(ConstantApi.JWT_ROLE, role)
                .withClaim(ConstantApi.JWT_USER_ID, id)
                .withClaim(ConstantApi.JWT_ORIGIN, origin)
                .withClaim(ConstantApi.JWT_USER_AGENT, userAgent)
                .withPayload(payload)
                .withHeader(header)
                .sign(algorithm);

        String redisKey = isEmployee ? redisUtil.buildEmployeeTokenKey(id) : redisUtil.buildUserTokenKey(id);

        List<UserToken> userTokens = (List<UserToken>) redisTemplate.opsForValue().get(redisKey);
        if (userTokens == null) {
            userTokens = new ArrayList<>();
        }

        List<UserToken> savedUserTokens = userTokens.stream()
                .filter(e -> !Objects.equals(origin, e.getOrigin())).collect(Collectors.toList());
        try {
            String encryptToken = EncryptUtil.encrypt(secretKey, token);
            savedUserTokens.add(UserToken.builder()
                    .token(encryptToken).origin(origin).userAgent(userAgent)
                    .expiresAt(new Date(expiresAt)).userId(id).build());

            redisTemplate.opsForValue().set(redisKey, savedUserTokens, Duration.ofMillis(timeTokenExpire));
        } catch (Exception e) {
            log.error("Cannot encrypt message", e);
        }

        return new TokenInfoDto(token, expiresAt);
    }

    @Override
    public VerifyTokenDto verifyToken(String token, boolean isEmployee, boolean isInternal) throws JWTVerifyException {
        Algorithm algorithm;
        long timeTokenExpire;
        String subject;
        String audience;
        if(isEmployee) {
            algorithm = getAlgorithmEmployee();
            timeTokenExpire = timeExpireAdmin;
            subject = ConstantApi.JWT_SUBJECT_EMPLOYEE;
            audience = ConstantApi.JWT_AUDIENCE_EMPLOYEE;
        } else {
            algorithm = getAlgorithmUser();
            timeTokenExpire = timeExpire;
            subject = ConstantApi.JWT_SUBJECT;
            audience = ConstantApi.JWT_AUDIENCE;
        }
        DecodedJWT jwt;
        VerifyTokenDto verifyTokenDto = new VerifyTokenDto();
        try {
            jwt = JWT.decode(token);
        } catch (JWTDecodeException ex){
            throw new JWTVerifyException(ConstantApi.JWT_INVALID_TOKEN);
        }

        String userEmail = jwt.getClaim(ConstantApi.JWT_EMAIL).asString();
        String role = jwt.getClaim(ConstantApi.JWT_ROLE).asString();
        Long userId = jwt.getClaim(ConstantApi.JWT_USER_ID).asLong();
        String origin = jwt.getClaim(ConstantApi.JWT_ORIGIN).asString();
        String userAgent = jwt.getClaim(ConstantApi.JWT_USER_AGENT).asString();
        verifyTokenDto.setEmail(userEmail);
        verifyTokenDto.setRole(role);
        verifyTokenDto.setUserId(userId);

        try {
            long currentTimestamp = System.currentTimeMillis();
            JWTVerifier verifier = JWT.require(algorithm).withSubject(subject)
                    .withIssuer(ConstantApi.SYS_MARKETPLACE)
                    .withAudience(audience).acceptIssuedAt(currentTimestamp).build();
            verifier.verify(token);
            if (jwt.getExpiresAt().getTime() - currentTimestamp > timeTokenExpire) {
                return handleExpiredToken(isInternal, verifyTokenDto);
            }
        } catch (SignatureVerificationException e) {
            log.error(ConstantApi.JWT_INVALID_PRIVATE_KEY);
            throw new JWTVerifyException(ConstantApi.JWT_INVALID_PRIVATE_KEY);
        } catch (TokenExpiredException e) {
            return handleExpiredToken(isInternal, verifyTokenDto);
        } catch (InvalidClaimException e) {
            log.error(ConstantApi.JWT_INVALID_CLAIM);
            throw new JWTVerifyException(ConstantApi.JWT_INVALID_CLAIM);
        }

        if (isInternal || isEmployee) {
            return verifyTokenDto;
        }

        checkJwtTokenData(userId, token, origin, userAgent, isEmployee);

        redisTemplate.opsForValue().set(redisUtil.buildUserLastOnlineKey(userId), (new Date()), Duration.ofMillis(InitialUserDataValue.LAST_USER_ONLINE_DURATION));

        return verifyTokenDto;
    }

    @SuppressWarnings("unchecked")
    private void checkJwtTokenData(Long userId, String token, String origin, String userAgent, boolean isEmployee) {
        String redisKey = isEmployee ? redisUtil.buildEmployeeTokenKey(userId) : redisUtil.buildUserTokenKey(userId);
        SecretKey secretKey = isEmployee ? getEmployeeAesSecretKey() : getUserAesSecretKey();
        List<UserToken> userTokens = (List<UserToken>) redisTemplate.opsForValue().get(redisKey);
        if (userTokens == null) {
            userTokens = new ArrayList<>();
        }
        List<UserToken> sameOriginTokens = userTokens.stream()
                .filter(e -> Objects.equals(e.getUserId(), userId)
                        && Objects.equals(e.getOrigin(), origin)
                        && Objects.equals(e.getUserAgent(), userAgent)).toList();
        if (ObjectUtils.isEmpty(sameOriginTokens)) {
            throw new JWTVerifyException(ConstantApi.JWT_INVALID_TOKEN);
        }
        UserToken encryptUserToken = sameOriginTokens.get(0);
        String decryptToken = "";
        try {
            decryptToken = EncryptUtil.decrypt(secretKey, encryptUserToken.getToken());
        } catch (Exception e) {
            log.error("Cannot decrypt message", e);
        }
        if (!Objects.equals(decryptToken, token)) {
            throw new JWTVerifyException(ConstantApi.JWT_INVALID_TOKEN);
        }
    }

    private VerifyTokenDto handleExpiredToken(boolean isInternal, VerifyTokenDto verifyTokenDto) {
        if (isInternal) {
            return verifyTokenDto;
        } else {
            log.warn(ConstantApi.JWT_TOKEN_EXPIRED);
            throw new JWTVerifyException(ConstantApi.JWT_TOKEN_EXPIRED);
        }
    }

    private Algorithm getAlgorithmUser() {
        if(algorithmUser == null) {
//            MarketPlaceKey marketPlaceKeyUser = marketPlaceKeyRepository.findByCode(ConstantApi.MARKET_PLACE_SECRET_USER);
//            if(ObjectUtils.isEmpty(marketPlaceKeyUser)) {
//                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
//            }
            try {
//                algorithmUser = EncryptUtil.getAlgorithmCustom(marketPlaceKeyUser.getPrivateKey(), marketPlaceKeyUser.getPublicKey());
                algorithmUser = EncryptUtil.getAlgorithmCustom(
                        "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDjPoNMKId/e9bdvSQc8aFKJCKA3rs9Llp0PuzobYnBm6wNwTN0vPHOZcHg+WsOTRvCR/qq/in3kT6hsKWrc7E8M05O6kIrk9P5pAxanV2cNES8u2cmvLudlKlWqIAYmY0BQgA7N8REoFRWSxSweMkO8/+LnWdwdB6aHF+esOxwsoPO6Fw2s1BqQD06/V4uKO2M+qQVk9OjRuRRYnJacTV47ImNP08wtY0le5qSe4rkJQB292HOkwJqBzFp+tl6mZj7lnwqf+T+zvQeLPWL747xY/XZgHx4VoxjLXM/yhlrCrfwgfNYY1ZKW6rESxE6dFZvxijf1vQgJd+4aCB6tUpfAgMBAAECggEAS+tLpDr6Ic4217fkA8N6NjHiURmYhnUhBSssrO5Dkqo2jhXY4gxp4KHAZzAM4ydBueOgTFZDoREmUpCYmLI7KTie1eCPoM1viTyUYAWpC5Gu0Tru2FM/fwL2nBwJR66tHBl3yFD2QlMqOfY1gEuqKFO+MV+x+jDhNG18SKElLPTlvkX8UqHL7/uqgv56+J3r6afH5OoAQ8APEoRTNJWF8ogk3CtMn0+CR5AFmT0FpxJfWGIMK1b9PEu0SLwhwiSsvjr8kUh77MqzlLZVY8CVIWWXPglWJmkR+PiDSGTlqGKnsDJ18fjVs6iqhhaakaCvxE5kTzVWUwQWxIYD2N0BaQKBgQDrxE+o69glFbBx3AbztNenWfk+di6liqxmrBQvIwHXlLn8nixMPlWpzjlgn9T6lczDt0EJQ0fYUJw2z3FHlIIDu0u3xYncSC51ky4fZ8QbZKGiaOwsFeBZC8xKOTYSyWvqZwK2cY2sR4ySlhG0QITXUGzwTLWs5rR5hL7ThZ07ywKBgQD2vvaI467caSf/TMIJELh5i2PfqEhSWz67lYdA4RFy9cUMfeRoslLGNADXhShuDADGZ1OcWqn+LiAbcRtvhUaiNQkhNCJMU3JXSZonb5iiWpReRWS2h8TTbhR943bsagg39f+jva5G6gFtVmWZQieyOp/em7FaxQHtuvE2/T7BPQKBgQCGH8j9nSX6eutE6todnHyunNXZPdxtmoVXZ3+YDT9ICDkCSG2E9lU/Y61qlaLQ55V0pfTesyLVIY6s87hK82Y/fzalFkDGS136zz8G6L48ozP8s9nffpaBsd+HPiLP7zxwPifh6JmjL5T981ehBq9L2loPHmSgNyLmmqkAplt7nwKBgEFnxTnsAIH0beHCmvyELiIpzMOQe/s4rJxUupF8F3/9ncn95PnLqx1W05JNfbYOAHaXFRaxAbaay2/6v2cEUQDWRiFDDIRm6jwxCmfBsRf4IrQKePPZcVcKKuxjyzZHtP4Ae08QDI5HoTE2YaAKZTNdTttXMd/5sNUvZJlK78ORAoGBAOdRnsNbfZ9jbF0D97yoBH5KbqFIUQLakchc4Nt+7WnTy7lfhVsNjZTX2wsN/Jh+niW7pNg6s868bggxjO+u3P7TL7KRTod0RpJcUoLMTodADY3+7OdBfEI/C9haQ2yGhXeeLmuS3ovADF7qMPQrIeC6CLaZ3l0npTXMHzbrYOOO",
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4z6DTCiHf3vW3b0kHPGhSiQigN67PS5adD7s6G2JwZusDcEzdLzxzmXB4PlrDk0bwkf6qv4p95E+obClq3OxPDNOTupCK5PT+aQMWp1dnDREvLtnJry7nZSpVqiAGJmNAUIAOzfERKBUVksUsHjJDvP/i51ncHQemhxfnrDscLKDzuhcNrNQakA9Ov1eLijtjPqkFZPTo0bkUWJyWnE1eOyJjT9PMLWNJXuaknuK5CUAdvdhzpMCagcxafrZepmY+5Z8Kn/k/s70Hiz1i++O8WP12YB8eFaMYy1zP8oZawq38IHzWGNWSluqxEsROnRWb8Yo39b0ICXfuGggerVKXwIDAQAB");
            } catch (Exception e) {
                log.error("error to make algorithm user");
                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
            }
        }
        return algorithmUser;
    }

    private Algorithm getAlgorithmEmployee() {
        if(algorithmEmployee == null) {
            MarketPlaceKey marketPlaceKey = marketPlaceKeyRepository.findByCode(ConstantApi.MARKET_PLACE_SECRET_EMPLOYEE);
            if(ObjectUtils.isEmpty(marketPlaceKey)) {
                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
            }
            try {
                algorithmEmployee = EncryptUtil.getAlgorithmCustom(marketPlaceKey.getPrivateKey(), marketPlaceKey.getPublicKey());
            } catch (Exception e) {
                log.error("error to make algorithm Employee");
                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
            }
        }
        return algorithmEmployee;
    }

    private SecretKey getUserAesSecretKey() {
        if (userAesSecretKey == null) {
            MarketPlaceKey marketPlaceKeyUser = marketPlaceKeyRepository.findByCode(ConstantApi.MARKET_PLACE_SECRET_USER);
            if (ObjectUtils.isEmpty(marketPlaceKeyUser)) {
                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
            }
            try {
                userAesSecretKey = EncryptUtil.getSecretKey(marketPlaceKeyUser.getPrivateKey(), marketPlaceKeyUser.getPublicKey());
            } catch (Exception e) {
                log.error("error to make user aes secret key", e);
                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
            }
        }
        return userAesSecretKey;
    }

    private SecretKey getEmployeeAesSecretKey() {
        if (employeeAesSecretKey == null) {
            MarketPlaceKey marketPlaceKey = marketPlaceKeyRepository.findByCode(ConstantApi.MARKET_PLACE_SECRET_EMPLOYEE);
            if (ObjectUtils.isEmpty(marketPlaceKey)) {
                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
            }
            try {
                employeeAesSecretKey = EncryptUtil.getSecretKey(marketPlaceKey.getPrivateKey(), marketPlaceKey.getPublicKey());
            } catch (Exception e) {
                log.error("error to make employee aes secret key", e);
                throw new RequestInvalidException(EMessage.SYSTEM_ERROR);
            }
        }
        return employeeAesSecretKey;
    }

}
