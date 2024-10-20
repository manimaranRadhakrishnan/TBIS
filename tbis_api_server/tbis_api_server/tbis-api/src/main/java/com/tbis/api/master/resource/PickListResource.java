
package com.tbis.api.master.resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.PickListService;
import com.tbis.api.master.model.InvoiceDetails;
import com.tbis.api.master.model.LoadingDetails;
import com.tbis.api.master.model.PickListDetail;
import com.tbis.api.master.model.PickListMaster;
import com.tbis.api.master.model.PickWorkFlowInput;
import com.tbis.api.master.model.VehicleDetails;

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

@Path("picklist")
@Secured
public class PickListResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo  uriInfo;
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> managePickList(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.managePickList(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> getPickList(@QueryParam("code") long code) {
    	ApiResult<PickListMaster> result=null;
    	try{
    		PickListService b=PickListService.getInstance();
    		result=b.getPickList(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/uploadpicklist")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> uploadPickList(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetails) {
    	ApiResult<PickListMaster> result=new ApiResult<PickListMaster>();
    	User user=(User)context.getUserPrincipal();

       try {
           	System.out.println(fileDetails.getFileName());

           	String uploadedFileLocation = fileDetails.getFileName();
           	String path=FileUtil.getFilePath("picklists","import","");
        	uploadedFileLocation= path+uploadedFileLocation;
        	System.out.println(":::::::::::::::::::::::: "+uploadedFileLocation);

    	    FileUtil.writeToFile(uploadedInputStream, uploadedFileLocation);
	   		
    	    PickListService b=PickListService.getInstance();
	   		result=b.readPickListFile(user.getUserId(),uploadedFileLocation);	       
       } catch (Exception e) {
           result.isSuccess=false;
           result.message="File Upload Failed";
    	   e.printStackTrace();
        }
       return result;
    }

    // save uploaded file to new location
    @Path("/addpart")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> selectPickListPart(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.selectPickListPart(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    
    @Path("/addvehiclepart")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> selectVehiclePickListPart(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.selectVehiclePickListPart(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @Path("/addpicklists")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> addPickLists(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.addSelectedPickList(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @Path("/pickliststockdetail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<PickListDetail>> getPickListDetail(@QueryParam("picklistdetailid") long code) {
    	ApiResult<ArrayList<PickListDetail>> result=null;
    	try{
    		PickListService b=PickListService.getInstance();
    		result=b.getPickListPartDetail(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @Path("/pickliststocklinespacedetail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<PickListDetail>> getPickListLineSpaceDetail(@QueryParam("picklistdetailid") long code) {
    	ApiResult<ArrayList<PickListDetail>> result=null;
    	try{
    		PickListService b=PickListService.getInstance();
    		result=b.getPickListPartLineSpaceDetail(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @Path("/picklistscanneddetail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<PickListDetail>> getPickListScannedDetail(@QueryParam("picklistdetailid") long code) {
    	ApiResult<ArrayList<PickListDetail>> result=null;
    	try{
    		PickListService b=PickListService.getInstance();
    		result=b.getScannedPickListPartDetail(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @Path("/picklistpartscan")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListDetail> pickListPartScan(PickListDetail input) {
    	ApiResult<PickListDetail> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.getPickListPartScanDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @Path("/pickliststaging")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<PickListMaster>> getPickListStaging(@QueryParam("status") long status) {
    	ApiResult<ArrayList<PickListMaster>> result=null;
    	try{
    		PickListService b=PickListService.getInstance();
    		result=b.getPickListStaging(status);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @POST
    @Path("/updatepickliststockdetail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> updatePickListStockDetail(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updatePickListPartStockDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @Path("/shorthaulvehicle")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<VehicleDetails> updateShorthaulVehicle(VehicleDetails input) {
    	ApiResult<VehicleDetails> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateShorthaulVehicle(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
       
    @Path("/invoicedetail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<InvoiceDetails> updateInvoiceDetail(InvoiceDetails input) {
    	ApiResult<InvoiceDetails> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateInvoiceDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/uploadgateentry")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> uploadPickListGateEntry(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetails) {
    	ApiResult<PickListMaster> result=new ApiResult<PickListMaster>();
    	User user=(User)context.getUserPrincipal();

       try {
           	System.out.println(fileDetails.getFileName());

           	String uploadedFileLocation = fileDetails.getFileName();
           	String path=FileUtil.getFilePath("goodsoutward","importgateentry","");
        	uploadedFileLocation= path+uploadedFileLocation;
        	System.out.println(":::::::::::::::::::::::: "+uploadedFileLocation);

    	    FileUtil.writeToFile(uploadedInputStream, uploadedFileLocation);
	   		
    	    PickListService b=PickListService.getInstance();
	   		result=b.readPickListGateEntryFile(user.getUserId(),uploadedFileLocation);	       
       } catch (Exception e) {
           result.isSuccess=false;
           result.message="File Upload Failed";
    	   e.printStackTrace();
        }
       return result;
    }
    
    @POST
    @Path("/uploadsrvgenerated")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> uploadPickListSrvGenerated(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetails) {
    	ApiResult<PickListMaster> result=new ApiResult<PickListMaster>();
    	User user=(User)context.getUserPrincipal();

       try {
           	System.out.println(fileDetails.getFileName());

           	String uploadedFileLocation = fileDetails.getFileName();
           	String path=FileUtil.getFilePath("goodsoutward","importsrv","");
        	uploadedFileLocation= path+uploadedFileLocation;
        	System.out.println(":::::::::::::::::::::::: "+uploadedFileLocation);

    	    FileUtil.writeToFile(uploadedInputStream, uploadedFileLocation);
	   		
    	    PickListService b=PickListService.getInstance();
	   		result=b.readPickListSrvFile(user.getUserId(),uploadedFileLocation);	       
       } catch (Exception e) {
           result.isSuccess=false;
           result.message="File Upload Failed";
    	   e.printStackTrace();
        }
       return result;
    }
    
    @Path("/updateprocessstatus")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> updateProcessStatus(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateProcessStatus(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @Path("/kanbanscan")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListDetail> kanbanScan(PickListDetail input) {
    	ApiResult<PickListDetail> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.getPickListDetailFromKanbanQR(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @Path("/partscan")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListDetail> partScan(PickListDetail input) {
    	ApiResult<PickListDetail> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.getPickListDetailFromPartId(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @Path("/clearpartscan")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListDetail> clearPartScan(PickListDetail input) {
    	ApiResult<PickListDetail> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.clearPickListDetailFromPartId(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @Path("/updatekanbanstatus")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> updateKanbanStatus(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateKanbanProcessStatus(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @Path("/kanbanpicklist")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<PickListMaster>> getPickListMaster(@QueryParam("statusId") int statusid,@QueryParam("warehouseId") long warehouseid,@QueryParam("sublocationId") long sublocationid,@QueryParam("udcId") long udcid) {
    	ApiResult<ArrayList<PickListMaster>> result=null;
    	try{
    		PickListService b=PickListService.getInstance();
    		result=b.getPickKanbanList(statusid,warehouseid,sublocationid,udcid);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    
	@POST
	@Path("/uploadinvoice")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ApiResult<PickListMaster> uploadPickListInvUpload(
	@FormDataParam("file") InputStream uploadedInputStream,
	@FormDataParam("file") FormDataContentDisposition fileDetails) {
	ApiResult<PickListMaster> result=new ApiResult<PickListMaster>();
	User user=(User)context.getUserPrincipal();
	
	try {
	   	System.out.println(fileDetails.getFileName());
	
	   	String uploadedFileLocation = fileDetails.getFileName();
	   	String path=FileUtil.getFilePath("pickliststatus","invoice","");
		uploadedFileLocation= path+uploadedFileLocation;
		System.out.println(":::::::::::::::::::::::: "+uploadedFileLocation);
	
	    FileUtil.writeToFile(uploadedInputStream, uploadedFileLocation);
			
	    PickListService b=PickListService.getInstance();
			result=b.readPickListInvUploadFile(user.getUserId(),uploadedFileLocation);	       
	} catch (Exception e) {
	   result.isSuccess=false;
	   result.message="File Upload Failed";
	   e.printStackTrace();
	}
	return result;
	}
	
    
    @Path("/loadingupdate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<LoadingDetails> updateLoadingDetail(LoadingDetails input) {
    	ApiResult<LoadingDetails> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateLoadingDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    
    @POST
    @Path("/uploadspares")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> uploadPickListSpare(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetails) {
    	ApiResult<PickListMaster> result=new ApiResult<PickListMaster>();
    	User user=(User)context.getUserPrincipal();

       try {
           	System.out.println(fileDetails.getFileName());

           	String uploadedFileLocation = fileDetails.getFileName();
           	String path=FileUtil.getFilePath("picklists","importspares","");
        	uploadedFileLocation= path+uploadedFileLocation;
        	System.out.println(":::::::::::::::::::::::: "+uploadedFileLocation);

    	    FileUtil.writeToFile(uploadedInputStream, uploadedFileLocation);
	   		
    	    PickListService b=PickListService.getInstance();
	   		result=b.readPickListSpareFile(user.getUserId(),uploadedFileLocation);	       
       } catch (Exception e) {
           result.isSuccess=false;
           result.message="File Upload Failed";
    	   e.printStackTrace();
        }
       return result;
    }    
    
    @Path("/updateamend")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<PickListMaster> updateAmendPickList(PickListMaster input) {
    	ApiResult<PickListMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		PickListService b=PickListService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateAmendPickList(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    


}
