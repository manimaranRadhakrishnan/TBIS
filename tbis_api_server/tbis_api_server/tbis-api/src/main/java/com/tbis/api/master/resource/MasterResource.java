package com.tbis.api.master.resource;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.MasterService;
import com.tbis.api.master.model.ApplicationType;
import com.tbis.api.master.model.AssetCategory;
import com.tbis.api.master.model.AssetCategoryBudget;
import com.tbis.api.master.model.AssetSubCategory;
import com.tbis.api.master.model.AssetSubCategoryBudget;
import com.tbis.api.master.model.CardMaster;
import com.tbis.api.master.model.Department;
import com.tbis.api.master.model.Designation;

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


@Path("masters")
@Secured
public class MasterResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo uriInfo;
	 
    @POST
    @Path("/designation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Designation> manageDesignation(Designation input) {
    	ApiResult<Designation> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		MasterService b=MasterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageDesignation(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/designation")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Designation> getDesignation(@QueryParam("code") int code) {
    	ApiResult<Designation> result=null;
    	try{
    		MasterService b=MasterService.getInstance();
    		result=b.getDesignation(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	 
    @POST
    @Path("/department")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Department> manageDepartment(Department input) {
    	ApiResult<Department> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		MasterService b=MasterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageDepartment(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/department")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Department> getDepartment(@QueryParam("code") int code) {
    	ApiResult<Department> result=null;
    	try{
    		MasterService b=MasterService.getInstance();
    		result=b.getDepartment(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	 
    @POST
    @Path("/applicationtype")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<ApplicationType> manageApplicationType(ApplicationType input) {
    	ApiResult<ApplicationType> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		MasterService b=MasterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageApplicationType(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/applicationtype")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<ApplicationType> getApplicationType(@QueryParam("code") int code) {
    	ApiResult<ApplicationType> result=null;
    	try{
    		MasterService b=MasterService.getInstance();
    		result=b.getApplicationType(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	 
    @POST
    @Path("/assetcategory")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AssetCategory> manageAssetCategory(AssetCategory input) {
    	ApiResult<AssetCategory> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		MasterService b=MasterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageAssetCategory(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/assetcategory")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AssetCategory> getAssetCategory(@QueryParam("code") int code) {
    	ApiResult<AssetCategory> result=null;
    	try{
    		MasterService b=MasterService.getInstance();
    		result=b.getAssetCategory(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	 
    @POST
    @Path("/assetcategorybudget")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AssetCategoryBudget> manageAssetCategoryBudget(AssetCategoryBudget input) {
    	ApiResult<AssetCategoryBudget> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		MasterService b=MasterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageAssetCategoryBudget(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/assetcategorybudget")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AssetCategoryBudget> getAssetCategoryBudget(@QueryParam("code") int code) {
    	ApiResult<AssetCategoryBudget> result=null;
    	try{
    		MasterService b=MasterService.getInstance();
    		result=b.getAssetCategoryBudget(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	 
    @POST
    @Path("/assetsubcategory")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AssetSubCategory> manageAssetSubCategory(AssetSubCategory input) {
    	ApiResult<AssetSubCategory> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		MasterService b=MasterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageAssetSubCategory(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/assetsubcategory")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AssetSubCategory> getAssetSubCategory(@QueryParam("code") int code) {
    	ApiResult<AssetSubCategory> result=null;
    	try{
    		MasterService b=MasterService.getInstance();
    		result=b.getAssetSubCategory(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	 
    @POST
    @Path("/assetsubcategorybudget")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<AssetSubCategoryBudget> manageAssetSubCategoryBudget(AssetSubCategoryBudget input) {
    	ApiResult<AssetSubCategoryBudget> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		System.out.println("user"+user.getUserName());
    		MasterService b=MasterService.getInstance();
    		input.setUserId(user.getUserId());
    		result=b.manageAssetSubCategoryBudget(input);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    @GET
    @Path("/assetsubcategorybudget")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<AssetSubCategoryBudget> getAssetSubCategoryBudget(@QueryParam("code") int code) {
    	ApiResult<AssetSubCategoryBudget> result=null;
    	try{
    		MasterService b=MasterService.getInstance();
    		result=b.getAssetSubCategoryBudget(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    

}