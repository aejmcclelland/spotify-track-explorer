package com.amcclelland.ste_server.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeysConfig {

    @Bean
    RSAKey rsaKey() throws Exception {
        String privPem = System.getenv("JWT_PRIVATE_KEY_PEM");
        String pubPem = System.getenv("JWT_PUBLIC_KEY_PEM");

        if (privPem != null && pubPem != null) {
            RSAPrivateKey privateKey = PemUtils.readPrivateKey(privPem);
            RSAPublicKey publicKey = (RSAPublicKey) PemUtils.readPublicKey(pubPem);
            return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID("ste-env").build();
        }

        // Fallback for dev: generate ephemeral keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        return new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                .privateKey((RSAPrivateKey) kp.getPrivate())
                .keyID("ste-generated")
                .build();
    }

    @Bean
    JwtEncoder jwtEncoder(RSAKey rsaKey) {
        var jwkSource = new ImmutableJWKSet<SecurityContext>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    JwtDecoder jwtDecoder(RSAKey rsaKey) {
        try {
            return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
        } catch (com.nimbusds.jose.JOSEException e) {
            throw new RuntimeException("Failed to get RSA public key from JWK", e);
        }
    }

    // Minimal PEM reader helpers (PKCS#8 private, SPKI public)
    static class PemUtils {
        static RSAPrivateKey readPrivateKey(String pem) throws Exception {
            String content = strip(pem, "PRIVATE KEY");
            var keySpec = new java.security.spec.PKCS8EncodedKeySpec(Base64.getDecoder().decode(content));
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }

        static PublicKey readPublicKey(String pem) throws Exception {
            String content = strip(pem, "PUBLIC KEY");
            var keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(content));
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        }

        private static String strip(String pem, String type) {
            return pem.replace("-----BEGIN " + type + "-----", "")
                    .replace("-----END " + type + "-----", "")
                    .replaceAll("\\s", "");
        }
    }
}
