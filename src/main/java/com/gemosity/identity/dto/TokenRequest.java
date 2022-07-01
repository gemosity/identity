package com.gemosity.identity.dto;

public class TokenRequest {
    private String grant_type;
    private String client_id;
    private String redirect_uri;
    private String scope;

    public String getGrant_type() {
        return grant_type;
    }

    /**
     * @param grant_type 'authorization_code' - Convert token returned from /authorize into an Access Token.
     *                   'refresh_token' - Passed in a refresh_token returns a new Access Token and Refresh Token.
     *                   'client_credentials' - Returns an Access Token. No refresh token is included.
     */
    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    /**
     * @param client_id Your application's Client ID
     */
    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    /**
     * @param redirect_uri Your application URI to redirect to after token is issued
     */
    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }


    public String getScope() {
        return scope;
    }

    /**
     * @param scope - Optional
     */
    public void setScope(String scope) {
        this.scope = scope;
    }
}
