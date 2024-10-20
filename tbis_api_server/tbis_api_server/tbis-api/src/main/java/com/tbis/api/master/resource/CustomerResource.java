package com.tbis.api.master.resource;
import java.util.ArrayList;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.CustomerService;
import com.tbis.api.master.model.CustomerAddress;
import com.tbis.api.master.model.CustomerContacts;
import com.tbis.api.master.model.CustomerContracts;
import com.tbis.api.master.model.CustomerDocuments;
import com.tbis.api.master.model.CustomerMaster;
import com.tbis.api.master.model.CustomerPartsLineMap;
import com.tbis.api.master.model.CustomerSoftwares;
import com.tbis.api.master.model.SpaceMaster;
import com.tbis.api.master.model.CustomerLineMap;
import com.tbis.api.master.model.CustomerLineRackMap;
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

@Path("customer")
@Secured
public class CustomerResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo  uriInfo;
	
    @POST
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerMaster> manageCustomerMaster(CustomerMaster input) {
    	ApiResult<CustomerMaster> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CustomerService b=CustomerService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCustomerMaster(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/master")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerMaster> getLocation(@QueryParam("code") long code) {
    	ApiResult<CustomerMaster> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.getCustomerMaster(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @POST
    @Path("/partsmap")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerPartsLineMap> manageCustomerPartsLineMap(CustomerPartsLineMap input) {
    	ApiResult<CustomerPartsLineMap> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CustomerService b=CustomerService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCustomerPartsMap(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/partsmap")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerPartsLineMap> getCustomerPartsLineMap(@QueryParam("code") long code) {
    	ApiResult<CustomerPartsLineMap> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.getCustomerPartsMap(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/spacemap")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerLineMap> CustomerLineMap(CustomerLineMap input) {
    	ApiResult<CustomerLineMap> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CustomerService b=CustomerService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCustomerLine(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    } 
    
    @POST
    @Path("/rackmap")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerLineRackMap> CustomerLineRackMap(CustomerLineRackMap input) {
    	ApiResult<CustomerLineRackMap> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CustomerService b=CustomerService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCustomerLineRack(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    } 
    
    //software 
    
    @POST
    @Path("/software")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerSoftwares> manageCustomerSoftwares(CustomerSoftwares input) {
    	ApiResult<CustomerSoftwares> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CustomerService b=CustomerService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCustomerSoftwares(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/software")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerSoftwares> getCustomerSoftwares(@QueryParam("code") long code) {
    	ApiResult<CustomerSoftwares> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.getCustomerSoftwares(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
   
 
    //customer manpower 
    @POST
    @Path("/customermanpower")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerManpowers> manageCustomerManpowers(CustomerManpowers input) {
    	ApiResult<CustomerManpowers> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CustomerService b=CustomerService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCustomerManpowers(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/customermanpower")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerManpowers> getCustomerManpowers(@QueryParam("code") long code) {
    	ApiResult<CustomerManpowers> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.getCustomerManpowers(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/contact")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerContacts> CustomerContact(CustomerContacts input) {
    	ApiResult<CustomerContacts> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		CustomerService b=CustomerService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageCustomerContact(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    } 
    @GET
    @Path("/contact")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<CustomerContacts> getCustomerContacts(@QueryParam("code") long code) {
    	ApiResult<CustomerContacts> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.getCustomerContacts(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/customersublocationspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<SpaceMaster> getCustomerSpace(@QueryParam("customerid") long customerid,@QueryParam("sublocationid") long sublocationid) {
    	ArrayList<SpaceMaster> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.getCustomerSpace(customerid,sublocationid);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/updatecustomerspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SpaceMaster> updateCustomerSpace(SpaceMaster input) {
    	ApiResult<SpaceMaster> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.updateSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @POST
    @Path("/removecustomerspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SpaceMaster> removeCustomerSpace(SpaceMaster input) {
    	ApiResult<SpaceMaster> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.removeSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }

    @POST
    @Path("/checkallocationspace")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<SpaceMaster> checkCustomerSpaceAllocation(SpaceMaster input) {
    	ApiResult<SpaceMaster> result=null;
    	try{
    		CustomerService b=CustomerService.getInstance();
    		result=b.checkCustomerSpaceAllocation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
}
