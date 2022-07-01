package com.gemosity.identity.dto;

public class OAuthAuthorizeRequest {
    private String response_type;
    private String client_id;
    private String redirect_uri;
    private String scope;

    /**
     * @return String       if response_type = 'token', caller has requested an Access Token
     *                      if response_type = 'id_token', caller has requested an ID Token
     *                      if response_type = 'id_token_token', caller has requested an Access and ID Token
     */
    public String getResponse_type() {
        return response_type;
    }

    /**
     * @param response_type Return an Access Token for response_type = 'token',
     *                      return an ID Token for response_type = 'id_token', return Access and ID tokens
     *                      for response_type = 'id_token_token'
     */
    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String getScope() {
        return scope;
    }

    /**
     * @param scope profile (returns an ID token containing name, family_name, given_name, middle_name, nickname,
     *              picture, updated_at), email (email, email_verified).
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

}
