package de.jodabyte.apisecurity.jwt;

import com.nimbusds.jose.JOSEException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


/**
 * A record that holds RSA private and public keys for JWT signing and verification.
 *
 * @param privateKey the RSA private key used for signing JWTs
 * @param publicKey  the RSA public key used for verifying JWTs
 */
public record SecretKeys(RSAPrivateKey privateKey, RSAPublicKey publicKey) {

    /**
     * Generates a new RSA key pair (public and private keys) for JWT signing and verification.
     */
    public static SecretKeys generateKeys() throws NoSuchAlgorithmException, JOSEException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new SecretKeys(privateKey, publicKey);
    }
}
