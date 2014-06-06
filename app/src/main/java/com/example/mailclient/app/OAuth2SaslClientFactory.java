package com.example.mailclient.app;

/**
 * Created by teo on 06/06/14.
 */

import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslClient;
import org.apache.harmony.javax.security.sasl.SaslClientFactory;

import java.util.Map;
import java.util.logging.Logger;


/**
 * A SaslClientFactory that returns instances of OAuth2SaslClient.
 *
 * <p>Only the "XOAUTH2" mechanism is supported. The {@code callbackHandler} is
 * passed to the OAuth2SaslClient. Other parameters are ignored.
 */
public class OAuth2SaslClientFactory implements SaslClientFactory {
    private static final Logger logger =
            Logger.getLogger(OAuth2SaslClientFactory.class.getName());

    public static final String OAUTH_TOKEN_PROP =
            "mail.imaps.sasl.mechanisms.oauth2.oauthToken";

    public SaslClient createSaslClient(String[] mechanisms,
                                       String authorizationId,
                                       String protocol,
                                       String serverName,
                                       Map<String, ?> props,
                                       CallbackHandler callbackHandler) {
        boolean matchedMechanism = false;
        for (int i = 0; i < mechanisms.length; ++i) {
            if ("XOAUTH2".equalsIgnoreCase(mechanisms[i])) {
                matchedMechanism = true;
                break;
            }
        }
        if (!matchedMechanism) {
            logger.info("Failed to match any mechanisms");
            return null;
        }
        return new OAuth2SaslClient((String) props.get(OAUTH_TOKEN_PROP),callbackHandler);
    }

    public String[] getMechanismNames(Map<String, ?> props) {
        return new String[] {"XOAUTH2"};
    }
}