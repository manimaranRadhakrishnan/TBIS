package com.tbis.api.master.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.uri.UriComponent;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.cissol.core.report.WritePDFReport;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.AsnService;
import com.tbis.api.master.data.CustomerService;
import com.tbis.api.master.data.SopService;
import com.tbis.api.master.model.ASNBinTag;
import com.tbis.api.master.model.ASNPartMovement;
import com.tbis.api.master.model.AsnIncidentLog;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.CustomerMaster;
import com.tbis.api.master.model.GateEntryInput;
import com.tbis.api.master.model.LineSpacePartConfig;
import com.tbis.api.master.model.SOPContent;
import com.tbis.api.master.model.SOPMaster;
import com.tbis.api.master.model.SOPMenu;
import com.tbis.api.master.model.SOPTOCMaster;
import com.tbis.api.master.model.ScanDetails;
import com.tbis.api.master.model.ScanHeader;
import com.tbis.api.master.model.Sops;

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


@Path("managelearn")
@Secured
public class SopManageResource {
	private static String FS=System.getProperty("file.separator");
	
	@Context
	SecurityContext context;
	
	@Context
	HttpServletResponse res;

	@Context 
	UriInfo uriInfo;
	
	@POST
	@Path("/sopmaster")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<SOPMaster> manageSopMaster(SOPMaster input) {
    	ApiResult<SOPMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		SopService b=SopService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageSopMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	

	@GET
	@Path("/sopdetail")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SOPMaster> getSopDetail(@QueryParam("code") long code) {
    	ApiResult<SOPMaster> result=null;
    	try{
    		SopService b=SopService.getInstance();
    		result=b.getSopMaster(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	
	@POST
	@Path("/tocmaster")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<SOPTOCMaster> manageTocMaster(SOPTOCMaster input) {
    	ApiResult<SOPTOCMaster> result=null;
    	User user=(User)context.getUserPrincipal();
		boolean isFile = true;
    	try{
    		SopService b=SopService.getInstance();
    		input.setUserId(user.getUserId());
			
			if(input.getSopSourceTypeid()==4 || input.getSopSourceTypeid()==0 || input.getSopSourceUrlData() == "" || input.getSopSourceUrlData() == null ){
				isFile=false;
			}
			if(isFile) {
				String path= FileUtil.getSopFilePath("esop",String.valueOf(input.getSopId()),input.getSopSourceTypeid());
				byte[] decodedBytes = Base64.decodeBase64(input.getSopSourceUrlData());
				input.setDocumentPath(path+input.getSopSourceUrl());
				FileUtil.writeToFile(decodedBytes,input.getDocumentPath());
				System.out.println(":::PATH6::: " + input.getDocumentPath());
			}
    		
    		result=b.manageTocMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
	@GET
	@Path("/tocdetail")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SOPTOCMaster> getTocDetail(@QueryParam("code") int code) {
    	ApiResult<SOPTOCMaster> result=null;
    	try{
    		SopService b=SopService.getInstance();
    		result=b.getTocDetail(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }


}