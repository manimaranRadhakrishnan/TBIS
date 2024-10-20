package com.tbis.api.master.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.glassfish.jersey.uri.UriComponent;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.AsnService;
import com.tbis.api.master.data.GoodStockQRCodeService;
import com.tbis.api.master.data.QRCodeService;
import com.tbis.api.master.data.ScanDetailsService;
import com.tbis.api.master.model.AsnIncidentLog;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.ScanDetails;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;


@Path("transaction")
@Secured
public class TransactionResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo uriInfo;

	@Context
	HttpServletResponse res;
	
    @POST
    @Path("/scan")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanDetails> addScannedData(ArrayList<ScanDetails> input) {
    	ApiResult<ScanDetails> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		ScanDetailsService b=ScanDetailsService.getInstance();
    		result=b.addScanDetails(input,user.getUserId());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/scancard")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<ScanDetails>> addCardScannedData(ScanDetails input) {
    	ApiResult<ArrayList<ScanDetails>> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		ScanDetailsService b=ScanDetailsService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.addCardScanDetails(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @POST
    @Path("/deletescancard")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<ScanDetails>> deleteCardScannedData(ScanDetails input) {
    	ApiResult<ArrayList<ScanDetails>> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		ScanDetailsService b=ScanDetailsService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.deleteScanDetails(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @POST
    @Path("/asn")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> addScannedDataAsn(ScanDetails input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.addScannedAsn(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/scan")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<ScanDetails>> getScanDetails(@QueryParam("code") int code) {
    	ApiResult<ArrayList<ScanDetails>> result=null;
    	try{
    		ScanDetailsService b=ScanDetailsService.getInstance();
    		result=b.getScanDetails(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/asndetail")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ScanDetails> getAsnScanDetails(AsnMaster m) {
    	ApiResult<ScanDetails> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnDetail(m.getAsnId(),m.getAsnNo());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/dispatchready")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnDispatchReady(ArrayList<Integer> asn) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.updateAsnDispatchReady(asn);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/cardconfirm")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnCardConfirmed(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.updateAsnCardConfirmed(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/dispatch")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnDispatch(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.updateAsnDispatch(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/dispatchconfirmation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnDispatchConfirmation(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.updateAsnDispatchConfirmation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/acknowledge")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnAcknowledge(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.updateAsnAcknowledge(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/asndispatchdetail")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ArrayList<ScanDetails>> getAsnDispatchDetails(@QueryParam("code") long code) {
    	ApiResult<ArrayList<ScanDetails>> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnDispatchDetail(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/scancarddispatch")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanDetails> getAsnDispatchCardDetail(ScanDetails input) {
    	ApiResult<ScanDetails> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnDispatchCardDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/scancarddispatchconfirm")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ScanDetails> getAsnDispatchConfirmedCardDetail(ScanDetails input) {
    	ApiResult<ScanDetails> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.getAsnDispatchConfirmedCardDetail(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/receipt")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateAsnReceipt(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		AsnService b=AsnService.getInstance();
    		result=b.updateAsnReceipt(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/partrequest")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> getPartRequestInfo(@QueryParam("code") int code) {
    	ApiResult<AsnMaster> result=null;
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.getPartRequestInfo(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/gateentrystatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnMaster> updateGateEntryStatus(AsnMaster input) {
    	ApiResult<AsnMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.updateGateEntry(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/asnincident")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AsnIncidentLog> updateAsnIncidentLog(AsnIncidentLog input) {
    	ApiResult<AsnIncidentLog> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		AsnService b=AsnService.getInstance();
    		result=b.updateAsnIncidentLog(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @Path("/generateqrcode")
	@GET
	@Produces("application/pdf")
	public StreamingOutput getPDF() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				try {
					MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(),
							true);
					User user = (User) context.getUserPrincipal();
					if (user != null) {
						queryParams.add("login_id", user.getUserName());
						queryParams.add("user_id", String.valueOf(user.getUserId()));
						queryParams.add("user_email", user.getEmailId());
						queryParams.add("role_id", String.valueOf(user.getRoleId()));
					}
					if (queryParams.getFirst("exportname") != null && !"".equals(queryParams.getFirst("exportname"))) {
						res.addHeader("Content-Disposition", "attachment;filename=" + queryParams.getFirst("exportname")
								+ ".pdf");
					} else {
						res.addHeader("Content-Disposition",
								"attachment;filename=QRCodes_" + queryParams.getFirst("id") + ".pdf");
					}
					QRCodeService pdf = new QRCodeService();
					pdf.writeSimpleDocument(queryParams, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

    @Path("/generatestockqrcode")
 	@GET
 	@Produces("application/pdf")
 	public StreamingOutput getStockPDF() {
 		return new StreamingOutput() {

 			@Override
 			public void write(OutputStream out) throws IOException, WebApplicationException {
 				try {
 					MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(),
 							true);
 					User user = (User) context.getUserPrincipal();
 					if (user != null) {
 						queryParams.add("login_id", user.getUserName());
 						queryParams.add("user_id", String.valueOf(user.getUserId()));
 						queryParams.add("user_email", user.getEmailId());
 						queryParams.add("role_id", String.valueOf(user.getRoleId()));
 					}
 					if (queryParams.getFirst("exportname") != null && !"".equals(queryParams.getFirst("exportname"))) {
 						res.addHeader("Content-Disposition", "attachment;filename=" + queryParams.getFirst("exportname")
 								+ ".pdf");
 					} else {
 						res.addHeader("Content-Disposition",
 								"attachment;filename=QRCodes_" + queryParams.getFirst("id") + ".pdf");
 					}
 					GoodStockQRCodeService pdf = new GoodStockQRCodeService();
 					pdf.writeSimpleDocument(queryParams, out);
 				} catch (Exception e) {
 					e.printStackTrace();
 				}
 			}
 		};
 	}

}