package com.tbis.api.master.resource;
import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.TransporterService;
import com.tbis.api.master.model.TransportVehicle;
import com.tbis.api.master.model.Transporter;
import com.tbis.api.master.model.VechicleIntrance;
import com.tbis.api.master.model.VehicleTypeMaster;

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

@Path("transporter")
@Secured
public class TransporterResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo  uriInfo;
	
    @POST
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Transporter> manageTransporter(Transporter input) {
    	ApiResult<Transporter> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		TransporterService b=TransporterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageTransporter(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Transporter> getLocation(@QueryParam("code") long code) {
    	ApiResult<Transporter> result=null;
    	try{
    		TransporterService b=TransporterService.getInstance();
    		result=b.getTransporter(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/vehicle")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<VechicleIntrance> manageVechicleIntrance(VechicleIntrance input) {
    	ApiResult<VechicleIntrance> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		TransporterService b=TransporterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageVehicleIntrance(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/vehicle")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<VechicleIntrance> getVehicleIntrance(@QueryParam("code") int code) {
    	ApiResult<VechicleIntrance> result=null;
    	try{
    		TransporterService b=TransporterService.getInstance();
    		result=b.getVehicleIntrance(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/transportvehicle")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<TransportVehicle> manageTransportVehicle(TransportVehicle input) {
    	ApiResult<TransportVehicle> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		TransporterService b=TransporterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageTransportVehicle(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/transportvehicle")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<TransportVehicle> getTransportVehicle(@QueryParam("code") int code) {
    	ApiResult<TransportVehicle> result=null;
    	try{
    		TransporterService b=TransporterService.getInstance();
    		result=b.getTransportVehicle(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/vehicletypemaster")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<VehicleTypeMaster> manageVehicleTypeMaster(VehicleTypeMaster input) {
    	ApiResult<VehicleTypeMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		TransporterService b=TransporterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageVehicleTypeMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/vehicletypemaster")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<VehicleTypeMaster> getVehicleTypeMaster(@QueryParam("code") int code) {
    	ApiResult<VehicleTypeMaster> result=null;
    	try{
    		TransporterService b=TransporterService.getInstance();
    		result=b.getVehicleTypeMaster(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
}
