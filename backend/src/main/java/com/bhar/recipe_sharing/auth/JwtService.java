package com.bhar.recipe_sharing.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.user.UserModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtService {

    private static final String HMAC_ALGO = "HmacSHA256";
    private final byte[] secretKeyBytes;
    private final long expirationSeconds;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtService(
        @Value("${jwt.secret:bhar-recipenet-secret-key-gastronomie-gold-signature-2026-very-secure}") String secret,
        @Value("${jwt.expiration-seconds:86400}") long expirationSeconds
    ) {
        this.secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UserModel user) {
        try {
            long now = Instant.now().getEpochSecond();
            long exp = now + expirationSeconds;

            Map<String, Object> header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
            );

            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", String.valueOf(user.getId()));
            payload.put("username", user.getUsername());
            payload.put("email", user.getEmail());
            payload.put("role", user.getRole() != null ? user.getRole() : "USER");
            payload.put("iat", now);
            payload.put("exp", exp);

            String encodedHeader = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String signatureInput = encodedHeader + "." + encodedPayload;

            String signature = hmacSha256(signatureInput);

            return signatureInput + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String signatureInput = parts[0] + "." + parts[1];
            String expectedSignature = hmacSha256(signatureInput);

            if (!MessageDigest.isEqual(parts[2].getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }

            Map<String, Object> claims = parsePayload(parts[1]);
            Number expNumber = (Number) claims.get("exp");
            if (expNumber == null) {
                return false;
            }

            long exp = expNumber.longValue();
            long now = Instant.now().getEpochSecond();
            return now <= exp;
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Map<String, Object> claims = getClaims(token);
        String sub = (String) claims.get("sub");
        return sub != null ? Long.valueOf(sub) : null;
    }

    public String extractUsername(String token) {
        Map<String, Object> claims = getClaims(token);
        return (String) claims.get("username");
    }

    public String extractRole(String token) {
        Map<String, Object> claims = getClaims(token);
        return (String) claims.get("role");
    }

    private Map<String, Object> getClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT structure");
            }
            return parsePayload(parts[1]);
        } catch (Exception e) {
            throw new RuntimeException("Could not extract claims from token", e);
        }
    }

    private Map<String, Object> parsePayload(String encodedPayload) throws Exception {
        byte[] bytes = Base64.getUrlDecoder().decode(encodedPayload);
        return objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
    }

    private String hmacSha256(String data) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, HMAC_ALGO);
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(rawHmac);
    }

    private String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
