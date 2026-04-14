package net.chess_platform.matchmaking_service.service;

import java.lang.reflect.InvocationTargetException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.annotation.PostConstruct;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.mmqueue.Player;

@Component
public class MMTokenParser {

    @Value("${matchmaking.token.issuer}")
    private String ISSUER;

    @Value("${matchmaking.token.expiration}")
    private Duration EXPIRATION;

    @Value("${matchmaking.token.algorithm}")
    private String ALGORITHM;

    @Value("${matchmaking.token.public-key}")
    private String PUBLIC_KEY;

    @Value("${matchmaking.token.private-key}")
    private String PRIVATE_KEY;

    private Algorithm algorithm;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        var publicKey = getRSAPublicKey();
        var privateKey = getRSAPrivateKey();

        algorithm = (Algorithm) Algorithm.class
                .getMethod(ALGORITHM, RSAPublicKey.class, RSAPrivateKey.class)
                .invoke(null, publicKey, privateKey);
    }

    public String createMatchmakingToken(Player player, Match.Type matchType, long matchId, String targetInstance) {
        var jwt = JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(Instant.now().plusMillis(EXPIRATION.toMillis()))
                .withClaim("matchId", matchId)
                .withClaim("playerId", player.getId().toString())
                .withClaim("matchType", matchType.name())
                .withClaim("target", targetInstance);

        if (matchType == Match.Type.RANKED) {
            jwt.withClaim("mmr", player.getRankedMmr());
        } else if (matchType == Match.Type.UNRANKED) {
            jwt.withClaim("mmr", player.getUnrankedMmr());
        }

        return jwt.sign(algorithm);
    }

    private RSAPublicKey getRSAPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PUBLIC_KEY = parsePEM(PUBLIC_KEY);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY)));
    }

    private RSAPrivateKey getRSAPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PRIVATE_KEY = parsePEM(PRIVATE_KEY);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(PRIVATE_KEY)));
    }

    private String parsePEM(String pem) {
        var key = new StringBuilder();
        var line = new StringBuilder();
        for (int i = 0; i < pem.length(); ++i) {
            var c = pem.charAt(i);
            if (c != '\n') {
                line.append(c);
            } else {
                if (!line.toString().startsWith("-----")) {
                    key.append(line);
                }
                line = new StringBuilder();
            }
        }

        return key.toString();
    }

}
