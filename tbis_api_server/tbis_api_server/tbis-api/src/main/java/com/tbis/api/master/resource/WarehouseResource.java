package com.tbis.api.master.resource;

import java.io.InputStream;
import java.util.ArrayList;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.AsnService;
import com.tbis.api.master.data.WarehouseService;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.DeliveryLocation;
import com.tbis.api.master.model.LineRack;
import com.tbis.api.master.model.LineSpace;
import com.tbis.api.master.model.PlantDocks;
import com.tbis.api.master.model.Plants;
import com.tbis.api.master.model.SpaceMaster;
import com.tbis.api.master.model.SubLocation;
import com.tbis.api.master.model.UnloadDock;
import com.tbis.api.master.model.Warehouse;

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


@Path("warehouse")
@Secured
public class WarehouseResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo uriInfo;
	
    @POST
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Warehouse> manageWarehouse(Warehouse warehouse) {
    	ApiResult<Warehouse> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		WarehouseService b=WarehouseService.getInstance();
    		warehouse.setUserId(user.getUserId());
    		result=b.manageWarehouse(warehouse);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Warehouse> getWarehouse(@QueryParam("code") int code) {
    	ApiResult<Warehouse> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getWarehouse(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/sublocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<SubLocation> manageSubLocation(SubLocation input) {
    	ApiResult<SubLocation> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageSubLocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/sublocation")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SubLocation> getSubLocation(@QueryParam("code") long code) {
    	ApiResult<SubLocation> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getSubLocation(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/sublocationspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<SpaceMaster> getSubLocationSpace(@QueryParam("code") long code) {
    	ArrayList<SpaceMaster> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getSubLocationSpace(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/sublocationspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SpaceMaster> updateSubLocationSpace(SpaceMaster input) {
    	ApiResult<SpaceMaster> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.updateSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/removesublocationspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SpaceMaster> removeSubLocationSpace(SpaceMaster input) {
    	ApiResult<SpaceMaster> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.removeSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/checksublocationspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SpaceMaster> checkSubLocationSpace(SpaceMaster input) {
    	ApiResult<SpaceMaster> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.checkSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/linespace")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<LineSpace> manageLineSpace(LineSpace input) {
    	ApiResult<LineSpace> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageLineSpace(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/linespace")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<LineSpace> getLineSpace(@QueryParam("code") long code) {
    	ApiResult<LineSpace> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getLineSpace(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/rack")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<LineRack> manageLineRack(LineRack input) {
    	ApiResult<LineRack> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		
    		WarehouseService b=WarehouseService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageLineRack(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/rack")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<LineRack> getLineRack(@QueryParam("code") long code) {
    	ApiResult<LineRack> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getLineRack(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/uldock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<UnloadDock> manageUnloadDock(UnloadDock input) {
    	ApiResult<UnloadDock> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageUnloadDock(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/uldock")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<UnloadDock> getLocation(@QueryParam("code") long code) {
    	ApiResult<UnloadDock> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getUnloadDock(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/deliverylocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<DeliveryLocation> manageDeliveryLocation(DeliveryLocation deliverylocation) {
    	ApiResult<DeliveryLocation> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		WarehouseService b=WarehouseService.getInstance();
    		deliverylocation.setUserId(user.getUserId());
    		result=b.manageDeliveryLocation(deliverylocation);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/deliverylocation")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<DeliveryLocation> getDeliveryLocation(@QueryParam("code") int code) {
    	ApiResult<DeliveryLocation> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getDeliveryLocation(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/plants")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Plants> managePlants(Plants input) {
    	ApiResult<Plants> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		
    		WarehouseService b=WarehouseService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePlants(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/plants")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Plants> getPlants(@QueryParam("code") long code) {
    	ApiResult<Plants> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getPlants(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/plantdocks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PlantDocks> managePlantDocks(PlantDocks input) {
    	ApiResult<PlantDocks> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		
    		WarehouseService b=WarehouseService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePlantDocks(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/plantdocks")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PlantDocks> getPlantDocks(@QueryParam("code") long code) {
    	ApiResult<PlantDocks> result=null;
    	try{
    		WarehouseService b=WarehouseService.getInstance();
    		result=b.getPlantDocks(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/uploadinbounddata")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Warehouse> uploadInboundData(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetails) {
    	ApiResult<Warehouse> result=new ApiResult<Warehouse>();
    	User user=(User)context.getUserPrincipal();

       try {
           	System.out.println(fileDetails.getFileName());

           	String uploadedFileLocation = fileDetails.getFileName();
           	String path=FileUtil.getFilePath("warehouse","importinbound","");
        	uploadedFileLocation= path+uploadedFileLocation;
        	System.out.println(":::::::::::::::::::::::: "+uploadedFileLocation);

    	    FileUtil.writeToFile(uploadedInputStream, uploadedFileLocation);
	   		
    	    WarehouseService b=WarehouseService.getInstance();
	   		result=b.readInbounddataFile(user.getUserId(),uploadedFileLocation,fileDetails.getFileName());	       
       } catch (Exception e) {
           result.isSuccess=false;
           result.message="File Upload Failed";
    	   e.printStackTrace();
        }
       return result;
    }
}