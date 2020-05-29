package nl.hu.bep.security;

import nl.hu.bep.model.Player;
import nl.hu.bep.security.BoxSecurityContext;

import javax.ws.rs.container.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;

/// Niet door Sep Vrij geschreven !!
///


@Provider
@PreMatching
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Method for ContainerRequestFilter.
     */
    private static final Base64.Decoder base64decoder = Base64.getDecoder(); //threadsafe

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        // If it's a preflight request, we abort the request with
        // a 200 status, and the CORS headers are added in the
        // response filter method below.
        if (isPreflightRequest(request)) {
            request.abortWith(Response.ok().build());
            return;
        }

        //Authentication version 1
        boolean isSecure = request.getSecurityContext().isSecure();
        String scheme = request.getUriInfo().getRequestUri().getScheme();
        BoxSecurityContext msc = new BoxSecurityContext(null, scheme);
        String authKey = request.getHeaderString(HttpHeaders.AUTHORIZATION);

        try{
            String[] keys = authKey.split("\\.", -1);

            String auth_key = keys[0];

            String auth_username = new String(base64decoder.decode(keys[1]));
            String auth_regkey = new String(base64decoder.decode(keys[2]));
            String sessiontoken = new String(base64decoder.decode(keys[3]));

            Player player = Player.getPlayerByAuthName(auth_username);

            System.out.println("Getting user");
            System.out.println(auth_username);
            System.out.println(player);

            if (player != null) {
                if (player.doAuth(auth_key, auth_username)) {
                    msc = new BoxSecurityContext(player, scheme);
                }
            }
            if (!sessiontoken.equals("")){
                if (player.getAuth().getSession().checkSession(sessiontoken)){
                    msc = new BoxSecurityContext(player, scheme);
                }
            }
        }catch (Exception e){
            System.out.println("Authorization went wrong");
        }
        request.setSecurityContext(msc);
    }

    /**
     * A preflight request is an OPTIONS request
     * with an Origin header.
     */
    private static boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }

    /**
     * Method for ContainerResponseFilter.
     */
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {
        // if there is no Origin header, then it is not a
        // cross origin request. We don't do anything.
        if (request.getHeaderString("Origin") == null) {
            return;
        }

        // If it is a preflight request, then we add all
        // the CORS headers here.

        if (isPreflightRequest(request)) {
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
            response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            response.getHeaders().add("Access-Control-Allow-Headers",
                    "X-Requested-With, Authorization, authkey, regkey, name, command, Accept-Version, Content-MD5, CSRF-Token, Content-Type");
        }

        // Cross origin requests can be either simple requests
        // or preflight request. We need to add this header
        // to both type of requests. Only preflight requests
        // need the previously added headers.
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
    }
}