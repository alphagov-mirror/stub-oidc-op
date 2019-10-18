package uk.gov.ida.verifystubop.views;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import io.dropwizard.views.View;

import java.net.URI;

public class ResponseView extends View {

    private final State state;
    private final AuthorizationCode authCode;
    private final JWT idToken;
    private final URI redirectURI;
    private AccessToken accessToken;

    public ResponseView(State state, AuthorizationCode authCode, JWT idToken, URI redirectURI) {
        super("response.mustache");

        this.state = state;
        this.authCode = authCode;
        this.idToken = idToken;
        this.redirectURI = redirectURI;
    }

    public ResponseView(State state, AuthorizationCode authCode, JWT idToken, URI redirectURI, AccessToken accessToken) {

        this(state, authCode, idToken, redirectURI);

        this.accessToken = accessToken;
    }

    public String getState() {
        return state.getValue();
    }

    public String getAuthCode() {
        return authCode.getValue();
    }

    public String getIdToken() {
        return idToken.serialize();
    }

    public String getRedirectURI() {
        return redirectURI.toString();
    }

    public String getAccessToken() {
        if (accessToken == null) {
            return "";
        }
        return accessToken.toJSONString();
    }
}
