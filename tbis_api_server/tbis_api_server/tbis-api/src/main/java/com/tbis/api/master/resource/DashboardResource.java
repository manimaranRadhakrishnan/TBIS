package com.tbis.api.master.resource;

import java.util.List;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.tbis.api.filter.Secured;
import com.tbis.api.master.data.AsnService;
import com.tbis.api.master.data.DashboardService;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.DashboardDetail;
import com.tbis.api.master.model.DashboardSummary;
import com.tbis.api.master.model.TBISDashboard;
import com.tbis.api.master.model.VendorFillRate;

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


@Path("dashboard")
@Secured
public class DashboardResource {
	@Context
	SecurityContext context;
	
	@Context 
	UriInfo uriInfo;
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<DashboardSummary> getAsnMaster(@QueryParam("fromdate") String fromDate,@QueryParam("todate") String toDate) {
    	ApiResult<DashboardSummary> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		DashboardService b=DashboardService.getInstance();    		
    		result=b.getDashboardSummary(fromDate,toDate,user.getRoleId(),user.getUserId());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<DashboardDetail> getVendorSupplyDetails(@QueryParam("fromdate") String fromDate,@QueryParam("todate") String toDate) {
    	ApiResult<DashboardDetail> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		DashboardService b=DashboardService.getInstance();    		
    		result=b.getVendorSupplyDetails(fromDate,toDate,user.getRoleId(),user.getUserId());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    @GET
    @Path("/tbisdash")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResult<TBISDashboard> getTbisDashboard(@QueryParam("fromdate") String fromDate,@QueryParam("todate") String toDate) {
    	ApiResult<TBISDashboard> result=null;
    	User user=(User)context.getUserPrincipal();
    	try{
    		DashboardService b=DashboardService.getInstance();    		
    		result=b.getLongShorHaulSummary(fromDate,toDate,user.getRoleId(),user.getUserId());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
}