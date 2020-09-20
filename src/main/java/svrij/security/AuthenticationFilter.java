package svrij.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        boolean isSecure = requestContext.getSecurityContext().isSecure();
        String scheme = requestContext.getUriInfo().getRequestUri().getScheme();
        //
        ZorgSecurityContext sc = new ZorgSecurityContext(null, scheme);
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer")){

            String token = authHeader.substring("Bearer".length()).trim();

            try{
                JwtParser parser = Jwts.parser().setSigningKey(AuthenticationResource.key);
                Claims claims = parser.parseClaimsJws(token).getBody();

                String user = claims.getSubject();
                sc = new ZorgSecurityContext(Account.getAccountByName(user), scheme);
                System.out.println(sc);

            }catch (Exception e){
                System.out.println("Invalid JWT");
            }
        }
        requestContext.setSecurityContext(sc);
        requestContext.setSecurityContext(new ZorgSecurityContext(Account.getAccountByName("svrij22"), ""));
    }
}
