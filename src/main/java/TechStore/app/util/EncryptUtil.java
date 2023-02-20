package TechStore.app.util;

import TechStore.app.constant.ConstantApi;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public final class EncryptUtil {
    private EncryptUtil() {
    }
    private static String privateKey;
    private static String publicKey;
    private static KeyFactory keyFactory;
    private static Algorithm algorithm;

    static {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(ConstantApi.SECURITY_RANDOM);
            keyFactory = KeyFactory.getInstance(ConstantApi.ALGORITHM);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ConstantApi.ALGORITHM);
            keyPairGenerator.initialize(2048, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            algorithm = Algorithm.RSA512(generateRSAPublicKey(), generateRSAPrivateKey());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Have error when init Algorithm RSA, error: {}", e.getMessage());
        }
    }

    private static RSAPublicKey generateRSAPublicKey() throws InvalidKeySpecException {
        X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        return (RSAPublicKey) keyFactory.generatePublic(keySpecPublic);
    }

    private static RSAPrivateKey generateRSAPrivateKey() throws InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpecPrivate);
    }

    public static Algorithm algorithm() {
        return algorithm;
    }

    public static Algorithm getAlgorithmCustom(String privateKey, String publicKey) throws InvalidKeySpecException {
        return Algorithm
                .RSA512((RSAPublicKey) getPublicKey(publicKey), (RSAPrivateKey) getPrivateKey(privateKey));
    }

    private static PrivateKey getPrivateKey(String key) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
        return keyFactory.generatePrivate(keySpecPrivate);
    }

    private static PublicKey getPublicKey(String key) throws InvalidKeySpecException {
        X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
        return keyFactory.generatePublic(keySpecPublic);
    }

    public static Pair<String, String> generateRSAKeys() {
        try {

            SecureRandom secRandom = SecureRandom.getInstance(ConstantApi.SECURITY_RANDOM);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ConstantApi.ALGORITHM);
            keyPairGenerator.initialize(2048, secRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            return Pair.of(privateKey, publicKey);
        } catch (Exception ex) {
            log.error("Error create rsa key for claim trace: ", ex);
            return Pair.of("", "");
        }
    }

    public static SecretKey getSecretKey(String privateKey, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(privateKey.toCharArray(), publicKey.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    public static String encrypt(SecretKey secretKey, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        byte[] combinedPayload = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combinedPayload, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combinedPayload, iv.length, encryptedBytes.length);
        return Base64.getEncoder().encodeToString(combinedPayload);
    }

    public static String decrypt(SecretKey secretKey, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] encryptedPayload = Base64.getDecoder().decode(message);
        byte[] iv = new byte[16];
        byte[] encryptedBytes = new byte[encryptedPayload.length - iv.length];
        System.arraycopy(encryptedPayload, 0, iv, 0, 16);
        System.arraycopy(encryptedPayload, iv.length, encryptedBytes, 0, encryptedBytes.length);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}

