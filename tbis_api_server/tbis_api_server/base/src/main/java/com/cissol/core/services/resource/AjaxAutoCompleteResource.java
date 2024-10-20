package com.cissol.core.services.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.glassfish.jersey.uri.UriComponent;

import com.cissol.core.model.User;
import com.cissol.core.util.AutoCompleteUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

@Path("autocomplete")
public class AjaxAutoCompleteResource {
	@Context
	HttpServletRequest req;
	@Context
	HttpServletResponse res;
	@Context 
	UriInfo uriInfo;
	@Context
	SecurityContext securityContext;
	
	@GET
	@Produces("application/json")
	public StreamingOutput getAutoCompleteResponse() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				PrintWriter p = new PrintWriter(out);
				MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(), true);
				User user = (User) securityContext.getUserPrincipal();
				if (user != null) {
					queryParams.add("login_id", user.getUserName());
					queryParams.add("user_id", String.valueOf(user.getUserId()));
					queryParams.add("user_email", user.getEmailId());
				}
				AutoCompleteUtil.getResultJson(queryParams, out);
			}
		};
	}
}