package com.tbis.api.master.resource;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.AsnService;
import com.tbis.api.master.data.PickListService;
import com.tbis.api.master.model.ASNBinTag;
import com.tbis.api.master.model.ASNPartMovement;
import com.tbis.api.master.model.AsnIncidentLog;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.GateEntryInput;
import com.tbis.api.master.model.LineSpacePartConfig;
import com.tbis.api.master.model.PickListMaster;
import com.tbis.api.master.model.ScanDetails;
import com.tbis.api.master.model.ScanHeader;

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


@Path("asnmaster")
@Secured
public class AsnResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo uriInfo;
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> manageAsnMaster(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageAsnMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> getAsnMaster(@QueryParam("code") int code) {
    	ApiResult<AsnMaster> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnMaster(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/checkinwardpallet")
    @Produces(MediaType.APPLICATION_JSON)
	public ApiResult<AsnMaster> checkInwardPallet(@QueryParam("asnId") int asnId) {
    	ApiResult<AsnMaster> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getInwardPalletInfo(asnId);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/asnbins")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ASNBinTag> getAsnBins(@QueryParam("code") int code) {
    	ArrayList<ASNBinTag> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnBinTags(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
  
    @GET
    @Path("/asngindetail")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ScanHeader> getAsnGinDetail(@QueryParam("code") long code) {
    	ApiResult<ScanHeader> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnGinScanDetail(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
 
    @POST
    @Path("/gateentry")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnGateStatus(GateEntryInput input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateAsnGateStatus(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/asnscancard")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanHeader> getAsnCardDetail(ScanDetails input) {
    	ApiResult<ScanHeader> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnScanCardDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/updateginreceipt")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateginReceipt(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateGinReceipt(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/stockmovementscan")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanHeader> getAsnScanCardStockDetail(ScanDetails input) {
    	ApiResult<ScanHeader> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnScanCardStockDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @POST
    @Path("/mobilestockmovementscan")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanHeader> getMobileAsnScanCardStockDetail(ScanDetails input) {
    	ApiResult<ScanHeader> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getMobileAsnScanCardStockDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/updatestockmovement")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<String> manageStockMovemnetDetail(AsnMaster input) {
    	ApiResult<String> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		
    		System.out.println("::test ::::: "+ToStringBuilder.reflectionToString(input));
    		
    		result=b.manageStockMovemnetDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/addincident")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnIncidentLog> addAsnIncident(AsnIncidentLog input) {
    	ApiResult<AsnIncidentLog> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.addAsnIncidentLog(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/removeincident")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnIncidentLog> removeAsnIncident(AsnIncidentLog input) {
    	ApiResult<AsnIncidentLog> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.modifyAsnIncidentLog(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/incidents")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<AsnIncidentLog>> getAsnIncidents(@QueryParam("code") int code) {
    	ApiResult<ArrayList<AsnIncidentLog>> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnIncidentLog(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/spaceallocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanHeader> getPartStorageSpaceAllocation(ASNPartMovement input) {
    	ApiResult<ScanHeader> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getPartStorageSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/updateasnstockmovement")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<String> updateStockMovemnetDetail(AsnMaster input) {
    	ApiResult<String> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateStockMovemnetDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/palletspaceallocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanHeader> getPartPalletSpaceAllocation(ASNPartMovement input) {
    	ApiResult<ScanHeader> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getPalletStorageSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
 
    @POST
    @Path("/updatepalletstockmovemnetdetail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<String> updatePalletStockMovemnetDetail(AsnMaster input) {
    	ApiResult<String> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updatePalletStockMovemnetDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
 
    @POST
    @Path("/stockmovementspaceallocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanHeader> getPartStockMovementStorageSpaceAllocation(ASNPartMovement input) {
    	ApiResult<ScanHeader> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getPartStockMovementStorageSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/fifoboard")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<LineSpacePartConfig> getFifoBoard(@QueryParam("customerid") long customerId,@QueryParam("usageid") int usageId) {
    	ArrayList<LineSpacePartConfig> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getLineSpaceFifoDetail(customerId,usageId);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @POST
    @Path("/gateverify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateGateVerificationStatus(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateGateVerificationStatus(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/updateasnpartsadd")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnPartsAdd(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateAsnPartsAdd(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/uploadasnparts")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> uploadAsnParts(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetails) {
    	ApiResult<AsnMaster> result=new ApiResult<AsnMaster>();
    	User user=(User)context.getUserPrincipal();

       try {
           	System.out.println(fileDetails.getFileName());

           	String uploadedFileLocation = fileDetails.getFileName();
           	String path=FileUtil.getFilePath("asn","importasnparts","");
        	uploadedFileLocation= path+uploadedFileLocation;
        	System.out.println(":::::::::::::::::::::::: "+uploadedFileLocation);

    	    FileUtil.writeToFile(uploadedInputStream, uploadedFileLocation);
	   		
    	    AsnService b=AsnService.getInstance();
	   		result=b.readAsnPartsFile(user.getUserId(),uploadedFileLocation);	       
       } catch (Exception e) {
           result.isSuccess=false;
           result.message="File Upload Failed";
    	   e.printStackTrace();
        }
       return result;
    }
    
    @POST
    @Path("/inboundvehicleassign")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnInboundVehicleAssign(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.updateAsnInboundVehicleAssign(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

}