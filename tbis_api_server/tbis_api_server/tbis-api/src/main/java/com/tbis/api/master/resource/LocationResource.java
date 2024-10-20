package com.tbis.api.master.resource;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.LocationService;
import com.tbis.api.master.model.Location;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;


@Path("location")
@Secured
public class LocationResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo  uriInfo;
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Location> manageLocation(Location location) {
    	ApiResult<Location> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		LocationService b=LocationService.getInstance();
    		location.setUserId(user.getUserId());
    		result=b.manageLocation(location);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Location> getLocation(@QueryParam("code") int code) {
    	ApiResult<Location> result=null;
    	try{
    		LocationService b=LocationService.getInstance();
    		result=b.getLocation(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
}