package com.tbis.api.master.resource;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.ConfigurationService;
import com.tbis.api.master.model.CardMaster;
import com.tbis.api.master.model.MailConfigs;
import com.tbis.api.master.model.WmsColorCode;
import com.tbis.api.master.model.WmsConfiguration;

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


@Path("config")
@Secured
public class ConfigurationResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo uriInfo;
	
    @POST
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<WmsConfiguration> manageConfiguration(WmsConfiguration config) {
    	ApiResult<WmsConfiguration> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		ConfigurationService b=ConfigurationService.getInstance();
    		config.setUserId(user.getUserId());
    		result=b.manageConfiguration(config);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<WmsConfiguration> getConfiguration(@QueryParam("code") int code) {
    	ApiResult<WmsConfiguration> result=null;
    	try{
    		ConfigurationService b=ConfigurationService.getInstance();
    		result=b.getConfiguration(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/card")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CardMaster> manageCardMaster(CardMaster input) {
    	ApiResult<CardMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		ConfigurationService b=ConfigurationService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCardMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/card")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<CardMaster> getCardMaster(@QueryParam("code") int code) {
    	ApiResult<CardMaster> result=null;
    	try{
    		ConfigurationService b=ConfigurationService.getInstance();
    		result=b.getCardMaster(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/color")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<WmsColorCode> manageWmsColorCode(WmsColorCode input) {
    	ApiResult<WmsColorCode> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		ConfigurationService b=ConfigurationService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageColorCode(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/color")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<WmsColorCode> getColorCode(@QueryParam("code") int code) {
    	ApiResult<WmsColorCode> result=null;
    	try{
    		ConfigurationService b=ConfigurationService.getInstance();
    		result=b.getColorCode(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/mailtemplate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<MailConfigs> manageMailConfig(MailConfigs input) {
    	ApiResult<MailConfigs> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		ConfigurationService b=ConfigurationService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageMailConfig(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/mailtemplate")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<MailConfigs> getMailConfig(@QueryParam("code") int code) {
    	ApiResult<MailConfigs> result=null;
    	try{
    		ConfigurationService b=ConfigurationService.getInstance();
    		result=b.getMailConfig(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
}