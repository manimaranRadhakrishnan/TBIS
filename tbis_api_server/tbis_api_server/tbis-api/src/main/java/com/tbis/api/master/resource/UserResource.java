package com.tbis.api.master.resource;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.master.data.UserManager;
import com.tbis.api.master.model.Employee;
import com.tbis.api.master.model.Role;
import com.tbis.api.master.model.UnloadDock;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;


@Path("users")
public class UserResource {
		
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo  uriInfo;
	
	@Path("/employee")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Employee> addEmployee(Employee employee) {
    	ApiResult<Employee> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
//    		System.out.println(" "+formParams.getFirst("data"));
//    	ObjectMapper obj=new ObjectMapper();
//    	Map<String,Object> m=obj.readValue(formParams.getFirst("data"), new TypeReference<Map<String, Object>>() {});
//    	System.out.println(" "+m.get("savedata"));
    	UserManager l=UserManager.getInstance();
		employee.setCreatedBy(user.getUserId());
		result=l.manageEmployee(employee);
		}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	@Path("/employee")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Employee> getServiceProcess(@QueryParam("code") String code) {
    	ApiResult<Employee> result=new ApiResult<>();
    	try{
    		UserManager b=UserManager.getInstance();
    		result=b.getEmployee(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	
	@Path("/role")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Role> addServiceAgent(Role role) {
    	ApiResult<Role> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    	UserManager l=UserManager.getInstance();
		role.setUserId(user.getUserId());
		result=l.manageRole(role);

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	@Path("/role")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public  ApiResult<Role> getRole(@QueryParam("code") String code) {
		ApiResult<Role> result=null;
    	try{
    		UserManager b=UserManager.getInstance();
    		result=b.getRole(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	@Path("/role/menus")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<Role> getParentRoleMenus(@QueryParam("code") String code) {
		ApiResult<Role> result=null;
    	try{
    		UserManager b=UserManager.getInstance();
    		result=b.getParentRoleMenus(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	@Path("/usercheck")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@QueryParam("code") String code) {
    	String result="";
    	try{
    	UserManager l=UserManager.getInstance();
    	result=l.checkUser(code);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	
//	@Path("/login")
//	@POST
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.APPLICATION_JSON)
//	public ApiResult<User> authenticateUser(@FormParam("username") String username,@FormParam("password") String password) {
//		ApiResult<User> result=new ApiResult<>();
//    	try {
//    		UserManager manager=UserManager.getInstance();
//    		result.result=manager.getauthuser(username, password, "");
//    		if(result.result.getUserId()==0) {
//	    		result.isSuccess=false;
//	    		result.message="Failed";    			
//    		}else {
//	    		result.isSuccess=true;
//	    		result.message="Success";
//    		}
//        } catch (Exception e) {
//            result.isSuccess=false;
//            result.message=e.getMessage();
//        }
//    	return result;
//    }
	
	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public ApiResult<User> authenticateUser(@FormParam("username") String username,@FormParam("password") String password) {
		ApiResult<User> result=new ApiResult<>();
		System.out.println("::::::::::::::::::::::::::::::::::::    A ");
    	try {
    		UserManager manager=UserManager.getInstance();
    		System.out.println("::::::::::::::::::::::::::::::::::::    B ");
    		result.result=manager.getauthuser(username, password, "");
    		System.out.println("::::::::::::::::::::::::::::::::::::    C ");
    		if(result.result.getUserId()==0) {
    			System.out.println("::::::::::::::::::::::::::::::::::::    D ");
	    		result.isSuccess=false;
	    		result.message="Failed";    			
    		}else {
    			System.out.println("::::::::::::::::::::::::::::::::::::    E ");
	    		result.isSuccess=true;
	    		result.message="Success";
    		}
    		System.out.println("::::::::::::::::::::::::::::::::::::    F ");
        } catch (Exception e) {
    		System.out.println("::::::::::::::::::::::::::::::::::::    G ");
            result.isSuccess=false;
            result.message=e.getMessage();
        }
		System.out.println("::::::::::::::::::::::::::::::::::::    H ");
    	return result;
    }

	@Path("/updateprofile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<Employee> updateProfile(Employee employee) {
    	ApiResult<Employee> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    	UserManager l=UserManager.getInstance();
		employee.setCreatedBy(user.getUserId());
		result=l.updateProfile(employee);
		}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	
	@Path("/userlocation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<UnloadDock> addUserLocation(UnloadDock dock) {
    	ApiResult<UnloadDock> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    	UserManager l=UserManager.getInstance();
		result=l.addUserLocation(dock);
		}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
	
	@Path("/removeuserlocation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult<UnloadDock> removeUserLocation(UnloadDock dock) {
    	ApiResult<UnloadDock> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    	UserManager l=UserManager.getInstance();
		result=l.removeUserLocation(dock);
		}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
}