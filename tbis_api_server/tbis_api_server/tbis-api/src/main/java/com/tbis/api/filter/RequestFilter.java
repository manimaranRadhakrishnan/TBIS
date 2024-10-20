package com.tbis.api.filter;

import java.io.IOException;
import java.security.Principal;

import com.cissol.core.model.User;
import com.tbis.api.master.data.UserManager;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RequestFilter  implements ContainerRequestFilter {

	@Context
	UriInfo uriInfo;
	
    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
    	System.out.println("Coming inside the request filter");
    	if(!requestContext.getUriInfo().getPath().contains("/login") && !requestContext.getUriInfo().getPath().contains("esop/")) {
	    	String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
	        
	        // Check if the HTTP Authorization header is present and formatted correctly
	        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	            
	            throw new NotAuthorizedException("Authorization header must be provided");
	        }
	
	        // Extract the token from the HTTP Authorization header
	        String token = authorizationHeader.substring("Bearer".length()).trim();
	
	        try {
	
	            // Validate the token
	        	UserManager u=UserManager.getInstance();
	        	Claims claims=u.getAllClaimsFromToken(token);
	        	if(claims.containsKey("userid")) {
	        		User user=u.getUser((int)claims.get("userid"));
	        		requestContext.setSecurityContext(new SecurityContext() {
						
						@Override
						public boolean isUserInRole(String role) {
							// TODO Auto-generated method stub
							return true;
						}
						
						@Override
						public boolean isSecure() {
							// TODO Auto-generated method stub
	                        return uriInfo.getAbsolutePath().toString().startsWith("https");
						}
						
						@Override
						public Principal getUserPrincipal() {
							// TODO Auto-generated method stub
							return user;
						}
						
						@Override
						public String getAuthenticationScheme() {
							// TODO Auto-generated method stub
							return "Token";
						}
					});
	        	}else {
	                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
	        	}
	        } catch (Exception e) {
	            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
	        }
    	}
    }
}

