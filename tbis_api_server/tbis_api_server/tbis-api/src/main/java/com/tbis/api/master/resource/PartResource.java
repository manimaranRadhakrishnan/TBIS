package com.tbis.api.master.resource;
import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.PartService;
import com.tbis.api.master.model.PackingType;
import com.tbis.api.master.model.PartAssembly;
import com.tbis.api.master.model.PartDeliveryLocation;
import com.tbis.api.master.model.PartKitMaster;
import com.tbis.api.master.model.PartMaster;

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

@Path("parts")
@Secured
public class PartResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo  uriInfo;
	
    @POST
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PartMaster> managePartMaster(PartMaster input) {
    	ApiResult<PartMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PartService b=PartService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePartMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PartMaster> getPartMaster(@QueryParam("code") long code) {
    	ApiResult<PartMaster> result=null;
    	try{
    		PartService b=PartService.getInstance();
    		result=b.getPartMaster(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/packing")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PackingType> managePackingType(PackingType input) {
    	ApiResult<PackingType> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		PartService b=PartService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePackingType(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/packing")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PackingType> getPackingType(@QueryParam("code") long code) {
    	ApiResult<PackingType> result=null;
    	try{
    		PartService b=PartService.getInstance();
    		result=b.getPackingType(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    
    @POST
    @Path("/kit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PartKitMaster> managePartKitMaster(PartKitMaster input) {
    	ApiResult<PartKitMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PartService b=PartService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePartKitMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/kit")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PartKitMaster>getKit(@QueryParam("code") long code) {
    	ApiResult<PartKitMaster> result=null;
    	try{
    		PartService b=PartService.getInstance();
    		result=b.getPartKitMaster(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/assembly")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PartAssembly> managePartKitMaster(PartAssembly input) {
    	ApiResult<PartAssembly> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PartService b=PartService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePartAssemblyMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/assembly")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PartAssembly> getassembly(@QueryParam("code") long code) {
    	ApiResult<PartAssembly> result=null;
    	try{
    		PartService b=PartService.getInstance();
    		result=b.getPartAssembly(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/partdeliverylocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PartDeliveryLocation> managePartDeliveryLocation(PartDeliveryLocation input) {
    	ApiResult<PartDeliveryLocation> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PartService b=PartService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePartDeliveryLocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
}
