package net.chess_platform.chess_service.coordinator;

import java.lang.reflect.InvocationTargetException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;
import net.chess_platform.chess_service.coordinator.match.Match;

@Component
public class MMTokenParser {

    @Value("${matchmaking.token.algorithm}")
    private String ALGORITHM;

    @Value("${matchmaking.token.public-key}")
    private String PUBLIC_KEY;

    private Algorithm algorithm;

    private JWTVerifier verifier;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        var publicKey = getRSAPublicKey();

        algorithm = (Algorithm) Algorithm.class
                .getMethod(ALGORITHM, RSAKey.class)
                .invoke(null, publicKey);

        verifier = JWT.require(algorithm).build();
    }

    public MatchmakingToken verifyMatchmakingToken(String token) {
        var decodedToken = verifier.verify(token);
        return createMatchmakingToken(decodedToken);
    }

    public MatchmakingToken parseMatchmakingToken(String token) {
        var decodedToken = JWT.decode(token);
        return createMatchmakingToken(decodedToken);
    }

    private MatchmakingToken createMatchmakingToken(DecodedJWT decodedToken) {
        var matchId = decodedToken.getClaim("matchId").asLong();
        var target = decodedToken.getClaim("target").asString();
        var playerId = decodedToken.getClaim("playerId").as(UUID.class);
        var mmr = decodedToken.getClaim("mmr").asInt();
        var matchType = decodedToken.getClaim("matchType").as(Match.Type.class);

        return new MatchmakingToken(matchId,
                playerId,
                mmr,
                matchType,
                target);
    }

    private RSAPublicKey getRSAPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PUBLIC_KEY = parsePEM(PUBLIC_KEY);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY)));
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
