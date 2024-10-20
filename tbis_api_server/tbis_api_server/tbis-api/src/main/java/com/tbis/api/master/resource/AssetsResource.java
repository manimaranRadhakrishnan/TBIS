package com.tbis.api.master.resource;

import com.cissol.core.model.ApiResult;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.AssetsService;
import com.tbis.api.master.model.AssetsModel;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

@Path("asset")
@Secured
public class AssetsResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo uriInfo;
	
    @GET
    @Path("/media")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AssetsModel> getAsset(@QueryParam("type") int type) {
    	ApiResult<AssetsModel> result=null;
    	try{
    		AssetsService b=AssetsService.getInstance();
    		result=b.getImage(type);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	
}
