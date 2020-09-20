package svrij.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.HashMap;

@Path("/")
public class AuthenticationResource {
    final static public Key key = MacProvider.generateKey();

    private String createToken(String username, String role) throws JwtException {
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.MINUTE, 30);

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration.getTime())
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUserByPassword(@FormParam("username") String name,
                                               @FormParam("password") String pw){
        try {
            if (Account.getAccountByName(name) == null) throw new Exception("Gebruiker bestaat niet");
            String role = Account.validateLogin(name, pw);
            if (role == null) throw new Exception("Fout bij het valideren");

            String token = createToken(name, role);
            HashMap<String, String> map = new HashMap<>();

            map.put("JWT", token);
            map.put("name", name);
            map.put("role", role);

            return Response.ok(map).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerNewUser(@FormParam("username") String name,
                                    @FormParam("password") String pw){
        try {
            //User exists
            if (Account.getAccountByName(name) != null) return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User already exists").build();

            //Create new account
            new Account(name, pw, "User");
            String token = createToken(name, "User");
            SimpleEntry<String, String> JWT = new SimpleEntry<>("JWT", token);
            return Response.ok(JWT).build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
