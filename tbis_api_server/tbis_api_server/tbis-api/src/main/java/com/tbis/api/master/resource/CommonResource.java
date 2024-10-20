package com.tbis.api.master.resource;
import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.CommonService;
import com.tbis.api.master.data.CustomerService;
import com.tbis.api.master.model.CustomerAddress;
import com.tbis.api.master.model.CustomerContacts;
import com.tbis.api.master.model.CustomerContracts;
import com.tbis.api.master.model.CustomerDocuments;
import com.tbis.api.master.model.CustomerMaster;
import com.tbis.api.master.model.CustomerPartsLineMap;
import com.tbis.api.master.model.CustomerSoftwares;
import com.tbis.api.master.model.EntityAddress;
import com.tbis.api.master.model.EntityContacts;
import com.tbis.api.master.model.EntityContracts;
import com.tbis.api.master.model.EntityDocuments;
import com.tbis.api.master.model.CustomerLineMap;
import com.tbis.api.master.model.CustomerManpowers;

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

@Path("common")
@Secured
public class CommonResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo  uriInfo;
	
    @POST
    @Path("/address")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<EntityAddress> manageCustomerMaster(EntityAddress input) {
    	ApiResult<EntityAddress> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CommonService b=CommonService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageAddress(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/address")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<EntityAddress> getLocation(@QueryParam("code") long code) {
    	ApiResult<EntityAddress> result=null;
    	try{
    		CommonService b=CommonService.getInstance();
    		result=b.getAddress(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/documents")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<EntityDocuments> manageDocuments(EntityDocuments input) {
    	ApiResult<EntityDocuments> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		CommonService b=CommonService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageDocuments(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/documents")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<EntityDocuments> getCustomerDocuments(@QueryParam("code") long code) {
    	ApiResult<EntityDocuments> result=null;
    	try{
    		CommonService b=CommonService.getInstance();
    		result=b.getDocuments(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/contact")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<EntityContacts> manageCustomerContacts(EntityContacts input) {
    	ApiResult<EntityContacts> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		CommonService b=CommonService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageContact(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/contact")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<EntityContacts> getContacts(@QueryParam("code") long code) {
    	ApiResult<EntityContacts> result=null;
    	try{
    		CommonService b=CommonService.getInstance();
    		result=b.getContacts(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/contract")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<EntityContracts> manageContracts(EntityContracts input) {
    	ApiResult<EntityContracts> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CommonService b=CommonService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageContracts(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/contract")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<EntityContracts> getContracts(@QueryParam("code") long code) {
    	ApiResult<EntityContracts> result=null;
    	try{
    		CommonService b=CommonService.getInstance();
    		result=b.getContracts(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
}
