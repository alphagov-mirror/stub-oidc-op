package uk.gov.ida.stuboidcop.resources;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.stuboidcop.services.RequestValidationService;
import uk.gov.ida.stuboidcop.views.ErrorResponseView;
import uk.gov.ida.stuboidcop.views.ResponseView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/formPost")
public class OidcFormPostResource {

    private final RequestValidationService requestValidationService;
    private static final Logger LOG = LoggerFactory.getLogger(OidcFormPostResource.class);

    public OidcFormPostResource(RequestValidationService requestValidationService) {
        this.requestValidationService = requestValidationService;
    }

    //TODO: The spec states there should be a post method for this endpoint as well
    @GET
    @Path("/authorize")
    @Produces(MediaType.TEXT_HTML)
    public View authorize(@Context UriInfo uriInfo) {
        URI uri = uriInfo.getRequestUri();

        try {
            AuthenticationRequest authenticationRequest = AuthenticationRequest.parse(uri);

            AuthenticationResponse response = requestValidationService.handleAuthenticationRequest(authenticationRequest);

            if (!response.indicatesSuccess()) {
                AuthenticationErrorResponse errorResponse = response.toErrorResponse();
                return new ErrorResponseView(
                        errorResponse.getErrorObject().getCode(),
                        errorResponse.getErrorObject().getDescription(),
                        errorResponse.getErrorObject().getHTTPStatusCode(),
                        errorResponse.getState(),
                        errorResponse.getRedirectionURI());
            } else {
                AuthenticationSuccessResponse successResponse = response.toSuccessResponse();
                return new ResponseView(
                        authenticationRequest.getState(),
                        successResponse.getAuthorizationCode(),
                        successResponse.getIDToken(),
                        authenticationRequest.getRedirectionURI(),
                        successResponse.getAccessToken());
            }

        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse URI: " + uri.toString() + " to authentication request", e);
        }
    }
}
