package TechStore.app.constant;

public class ConstantApi {
    public static final String MAINTENANCE_FLAG = "Maintenance flag";
    public static final Integer LEVEL_VALID_HERO_BREED = 30;
    public static final Integer MAX_TIMES_BREED = 5;

    private ConstantApi() {
    }

    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    public static final int LENGTH_CODE = 6;
    public static final String CODE_CLAIM_SECRET = "CLAIM-SECRET";
    public static final String MARKET_PLACE_SECRET_USER = "MARKETPLACE-SECRET-USER";
    public static final String MARKET_PLACE_SECRET_EMPLOYEE = "MARKETPLACE-SECRET-EMPLOYEE";
    public static final String ALGORITHM = "RSA";
    public static final String SECURITY_RANDOM = "SHA1PRNG";
    public static final String JWT_SUBJECT = "User";
    public static final String JWT_SUBJECT_EMPLOYEE = "Employee";
    public static final String JWT_SUBJECT_HASH_BLOCK = "This is hash block";
    public static final String JWT_AUDIENCE = "authentication to call api";
    public static final String JWT_AUDIENCE_EMPLOYEE = "authentication to call api Employee";
    public static final String HASH_BLOCK_AUDIENCE = "hash block";
    public static final String JWT_EMAIL = "email";
    public static final String JWT_ROLE = "role";
    public static final String JWT_USER_ID = "userId";
    public static final String JWT_ORIGIN = "origin";
    public static final String JWT_USER_AGENT = "userAgent";
    public static final String JWT_INVALID_PRIVATE_KEY = "Invalid private key";
    public static final String JWT_TOKEN_EXPIRED = "Token is expired";
    public static final String JWT_INVALID_CLAIM = "Invalid Claim";
    public static final String JWT_INVALID_TOKEN = "Invalid Token";

    public static final String SYS_MARKETPLACE = "Sys-marketplace";

}

