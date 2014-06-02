package com.example.mailclient.app;

import java.security.Provider;

/**
 * Created by teo on 02/06/14.
 */
public class OAuth2Provider extends Provider {
    private static final long serialVersionUID = 1L;

    public OAuth2Provider() {
        super("Google OAuth2 Provider", 1.0,
                "Provides the XOAUTH2 SASL Mechanism");
        put("SaslClientFactory.XOAUTH2",
                "com.google.code.samples.oauth2.OAuth2SaslClientFactory");
    }
}
