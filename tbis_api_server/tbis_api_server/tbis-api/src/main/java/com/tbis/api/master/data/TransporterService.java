package com.tbis.api.master.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.TransportVehicle;
import com.tbis.api.master.model.Transporter;
import com.tbis.api.master.model.VechicleIntrance;
import com.tbis.api.master.model.VehicleTypeMaster;

public class TransporterService {

	private static final String insertQuery="INSERT INTO transporter  (vechicleno, transportercode, transportername, vechiclesize, vechiclestatus, isactive, created_by, created_date,tsid,address, city, district, state,gstin, lat, lang,locationid) VALUES (?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?,?,?,?,?,?,?,?);"; 
	private static final String modifyQuery="UPDATE transporter SET vechicleno=?, transportercode=?, transportername=?, vechiclesize=?, vechiclestatus=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()), address=?, city=?, district=?, state=?,gstin=?, lat=?, lang=?,locationid=? WHERE vechicleid=?;";
	private static final String select="SELECT vechicleid, vechicleno, transportercode, transportername, vechiclesize, vechiclestatus, isactive,address,city,district,state,gstin,lat,lang,locationid FROM transporter WHERE vechicleid=?;";
	
	private static final String insertVehicleQuery = "INSERT INTO vechicleintrans (vechicleno, drivername, drivermobile, statusid, created_by, created_date, tsid,returnpack,returnpackqty) VALUES (?,?,?,?,?,now(),getcurrenttsid(now()),?,?);";
	private static final String modifyVehicleQuery = "UPDATE vechicleintrans set vechicleno=?, drivername=?, drivermobile=?, statusid=?,updated_by=?, update_date=now(),tsid=getcurrenttsid(now()) where transid=?;";
	private static final String selectVehicle = "SELECT transid, vechicleno, drivername, drivermobile, statusid FROM vechicleintrans where transid=?;";
	private static final String checkVehicleExist = "SELECT transid, vechicleno, drivername, drivermobile, statusid,returnpack,returnpackqty FROM vechicleintrans where vechicleno=? and statusid=1;";

	private static final String insertTransporterVehicleQuery="INSERT INTO transportervehicles(transporterid,vehicleno,vehiclesize,vehiclecapacity,vehicleaxix,vehiclelength,vehiclewidth,vehicleheight,vehiclem2,vehiclem3,isactive, created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
	private static final String modifyTransporterVehicleQuery="UPDATE transportervehicles SET transporterid=?, vehicleno=?, vehiclesize=?, vehiclecapacity=?, vehicleaxix=?, vehiclelength=?, vehiclewidth=?, vehicleheight=?, vehiclem2=?, vehiclem3=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE vehicleid=?;";
	private static final String selectTransporterVehicle="select vehicleid,transporterid,vehicleno,vehiclesize,vehiclecapacity,vehicleaxix,vehiclelength,vehiclewidth,vehicleheight,vehiclem2,vehiclem3,isactive from transportervehicles where vehicleid = ?";
	private static final String deleteTransporterVehicleQuery="update transportervehicles set isactive=0 where vehicleid=?;";
	
	private static final String insertVehicletypeQuery="insert into vehicle_type_master (vehicletypename,length,width,height,capacity,axel,isactive,created_by,created_date,tsid) VALUES (?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()))"; 
	private static final String modifyVehicletypeQuery="update vehicle_type_master set vehicletypename=?,length=?,width=?,height=?,capacity=?,axel=?,isactive =?,updated_by=?,updated_date=now(),tsid=getcurrenttsid(now()) where vehicletypeid=?";
	private static final String selectVehicletypeQuery="select vehicletypeid,vehicletypename,length,width,height,capacity,axel,isactive from vehicle_type_master where vehicletypeid=?";
	
	public ApiResult<Transporter> manageTransporter(Transporter input){
		if(input.getVechicleId()==0) {
			return addTransporter(input);
		}else {
			return modifyTransporter(input);
		}
	}
	public ApiResult<Transporter> addTransporter(Transporter l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getVechicleNo());
			pstmt.setString(2, l.getTransporterCode());
			pstmt.setString(3, l.getTransporterName());
			pstmt.setString(4, l.getVechicleSize());
			pstmt.setInt(5, l.getVechicleStatus());
			pstmt.setBoolean(6, l.getIsActive());
			pstmt.setInt(7,l.getUserId());
			pstmt.setString(8, l.getAddress());
			pstmt.setString(9, l.getCity());
			pstmt.setString(10, l.getDistrict());
			pstmt.setString(11, l.getState());
			pstmt.setString(12, l.getGstIn());
			pstmt.setDouble(13, l.getLat());
			pstmt.setDouble(14, l.getLang());
			pstmt.setInt(15,l.getLocationId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Transporter added successfully";
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	public ApiResult<Transporter> modifyTransporter(Transporter l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getVechicleNo());
			pstmt.setString(2, l.getTransporterCode());
			pstmt.setString(3, l.getTransporterName());
			pstmt.setString(4, l.getVechicleSize());
			pstmt.setInt(5, l.getVechicleStatus());
			pstmt.setBoolean(6, l.getIsActive());
			pstmt.setInt(7,l.getUserId());
			pstmt.setString(8, l.getAddress());
			pstmt.setString(9, l.getCity());
			pstmt.setString(10, l.getDistrict());
			pstmt.setString(11, l.getState());
			pstmt.setString(12, l.getGstIn());
			pstmt.setDouble(13, l.getLat());
			pstmt.setDouble(14, l.getLang());
			pstmt.setInt(15,l.getLocationId());
			pstmt.setInt(16,l.getVechicleId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Transporter modified successfully";
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<Transporter> getTransporter(long TransporterId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<Transporter> result=new ApiResult<Transporter>();
		Transporter l=new Transporter();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(select);
			pstmt.setLong(1, TransporterId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setVechicleId(rs.getInt("vechicleid"));
				l.setVechicleNo(rs.getString("vechicleno"));
				l.setTransporterCode(rs.getString("transportercode"));
				l.setTransporterName(rs.getString("transportername"));
				l.setVechicleSize(rs.getString("vechiclesize"));
				l.setVechicleStatus(rs.getInt("vechiclestatus"));
				l.setIsActive(rs.getBoolean("isactive"));
				l.setAddress(rs.getString("address"));
				l.setCity(rs.getString("city"));
				l.setDistrict(rs.getString("district"));
				l.setState(rs.getString("state"));
				l.setGstIn(rs.getString("gstin"));
				l.setLat(rs.getDouble("lat"));
				l.setLang(rs.getDouble("lang"));
				l.setLocationId(rs.getInt("locationid"));
			}
			result.result=l;
		}catch(Exception e){
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
			result.result=null;
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(rs!=null){
					rs.close();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	public ApiResult<VechicleIntrance> manageVehicleIntrance(VechicleIntrance l) {
		if (l.getTransId() == 0) {
			return addVehicleIntrance(l);
		} else {
			return modifyVehicleIntrance(l);
		}
	}

	public ApiResult<VechicleIntrance> addVehicleIntrance(VechicleIntrance l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<VechicleIntrance> result = new ApiResult<VechicleIntrance>();
		String code = "0";
		if(CheckVehicleStatus(l.getVechicleNo())) {
			result.isSuccess=false;
			result.message="Vechicle details already Exist";
			return result;
		}
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertVehicleQuery);
			pstmt.clearParameters();
			pstmt.setString(1,l.getVechicleNo());
			pstmt.setString(2,l.getDriverName());
			pstmt.setString(3,l.getDriverMobile());
			pstmt.setInt(4,l.getStatusId());
			pstmt.setInt(5,l.getUserId());
			pstmt.setString(6,l.getReturnPack());
			pstmt.setInt(7,l.getReturnPackQty());
			pstmt.executeUpdate();
			conn.commit();
			
			pstmt = conn.prepareStatement(checkVehicleExist);
			pstmt.setString(1, l.getVechicleNo());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setTransId(rs.getLong("transid"));
				l.setVechicleNo(rs.getString("vechicleno"));
				l.setDriverName(rs.getString("drivername"));
				l.setDriverMobile(rs.getString("drivermobile"));
				l.setStatusId(rs.getInt("statusid"));
				l.setReturnPack(rs.getString("returnpack"));
				l.setReturnPackQty(rs.getInt("returnpackqty"));
			}
			result.result = l;
			result.isSuccess = true;
			result.message = "Vechicle added successfully";
			
			
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess = false;
			result.message = e.getMessage();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	public ApiResult<VechicleIntrance> modifyVehicleIntrance(VechicleIntrance l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyVehicleQuery);
			pstmt.clearParameters();
			pstmt.setString(1,l.getVechicleNo());
			pstmt.setString(2,l.getDriverName());
			pstmt.setString(3,l.getDriverMobile());
			pstmt.setInt(4,l.getStatusId());
			pstmt.setInt(5,l.getUserId());
			pstmt.setLong(6, l.getTransId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Vechicle In Trance modified successfully";
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess = false;
			result.message = e.getMessage();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	public ApiResult<VechicleIntrance> getVehicleIntrance(int VechicleIntranceId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<VechicleIntrance> result = new ApiResult<VechicleIntrance>();
		VechicleIntrance l = new VechicleIntrance();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectVehicle);
			pstmt.setInt(1, VechicleIntranceId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setTransId(rs.getLong("transid"));
				l.setVechicleNo(rs.getString("vechicleno"));
				l.setDriverName(rs.getString("drivername"));
				l.setDriverMobile(rs.getString("drivermobile"));
				l.setStatusId(rs.getInt("statusid"));
			}
			if(l.getTransId()>0) {
	    		ScanDetailsService b=ScanDetailsService.getInstance();
	    		l.setScanDetails(b.getScanDetails(l.getTransId()).result);
			}
			result.result = l;
		} catch (Exception e) {
			e.printStackTrace();
			result.isSuccess = false;
			result.message = e.getMessage();
			result.result = null;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	private boolean CheckVehicleStatus(String vechicleNo){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		boolean result=false;
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(checkVehicleExist);
			pstmt.setString(1, vechicleNo);
			rs=pstmt.executeQuery();
			if(rs.next()){
				result=true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(rs!=null){
					rs.close();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
		
	}
	
	// Transport vehicle
	public ApiResult<TransportVehicle> manageTransportVehicle(TransportVehicle input){
		if(input.getVehicleId()==0) {
			return addTransportVehicle(input);
		}else {
			return modifyTransportVehicle(input);
		}
	}
	public ApiResult<TransportVehicle> addTransportVehicle(TransportVehicle l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertTransporterVehicleQuery);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getTransportId());
			pstmt.setString(2, l.getVehicleNo());
			pstmt.setString(3, l.getVechicleSize());
			pstmt.setDouble(4, l.getVehicleCapacity());
			pstmt.setInt(5, l.getVehicleAxix());
			pstmt.setDouble(6, l.getVehicleLength());
			pstmt.setDouble(7,l.getVehicleWidth());
			pstmt.setDouble(8, l.getVehicleHeight());
			pstmt.setDouble(9, l.getM2());
			pstmt.setDouble(10, l.getM3());
			pstmt.setBoolean(11, l.isActive());
			pstmt.setInt(12,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Transport Vehicle added successfully";
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	public ApiResult<TransportVehicle> modifyTransportVehicle(TransportVehicle l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(deleteTransporterVehicleQuery);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getVehicleId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Transport Vehicle deleted successfully";
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<TransportVehicle> getTransportVehicle(long TransporterId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<TransportVehicle> result=new ApiResult<TransportVehicle>();
		TransportVehicle l=new TransportVehicle();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectTransporterVehicle);
			pstmt.setLong(1, TransporterId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setVehicleId(rs.getInt("vehicleid"));
				l.setTransportId(rs.getInt("transporterid"));
				l.setVehicleNo(rs.getString("vehicleno"));
				l.setVechicleSize(rs.getString("vehiclesize"));
				l.setVehicleCapacity(rs.getDouble("vehiclecapacity"));
				l.setVehicleAxix(rs.getInt("vehicleaxix"));
				l.setVehicleLength(rs.getDouble("vehiclelength"));
				l.setVehicleWidth(rs.getDouble("vehiclewidth"));
				l.setVehicleHeight(rs.getDouble("vehicleheight"));
				l.setM2(rs.getDouble("vehiclem2"));
				l.setM3(rs.getDouble("vehiclem3"));
				l.setActive(rs.getBoolean("isactive"));
			}
			result.result=l;
		}catch(Exception e){
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
			result.result=null;
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(rs!=null){
					rs.close();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	// vehicle Type Master
	public ApiResult<VehicleTypeMaster> manageVehicleTypeMaster(VehicleTypeMaster input){
		if(input.getVehicleTypeId()==0) {
			return addVehicleTypeMaster(input);
		}else {
			return modifyVehicleTypeMaster(input);
		}
	}
	public ApiResult<VehicleTypeMaster> addVehicleTypeMaster(VehicleTypeMaster l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertVehicletypeQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getVehicleTypeName());
			pstmt.setDouble(2, l.getLength());
			pstmt.setDouble(3, l.getWidth());
			pstmt.setDouble(4, l.getHeight());
			pstmt.setDouble(5, l.getCapacity());
			pstmt.setInt(6, l.getAxel());
			pstmt.setBoolean(7, l.getIsActive());
			pstmt.setInt(8,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Vehicle Type Master added successfully";
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	public ApiResult<VehicleTypeMaster> modifyVehicleTypeMaster(VehicleTypeMaster l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyVehicletypeQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getVehicleTypeName());
			pstmt.setDouble(2, l.getLength());
			pstmt.setDouble(3, l.getWidth());
			pstmt.setDouble(4, l.getHeight());
			pstmt.setDouble(5, l.getCapacity());
			pstmt.setInt(6, l.getAxel());
			pstmt.setBoolean(7, l.getIsActive());
			pstmt.setInt(8,l.getUserId());
			pstmt.setInt(9, l.getVehicleTypeId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Vehicle Type Master Modified successfully";
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<VehicleTypeMaster> getVehicleTypeMaster(int vehicleTypeId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<VehicleTypeMaster> result=new ApiResult<VehicleTypeMaster>();
		VehicleTypeMaster l=new VehicleTypeMaster();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectVehicletypeQuery);
			pstmt.setInt(1, vehicleTypeId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setVehicleTypeId(rs.getInt("vehicletypeid"));
				l.setVehicleTypeName(rs.getString("vehicletypename"));
				l.setLength(rs.getDouble("length"));
				l.setWidth(rs.getDouble("width"));
				l.setHeight(rs.getDouble("height"));
				l.setCapacity(rs.getDouble("capacity"));
				l.setAxel(rs.getInt("axel"));
				l.setIsActive(rs.getBoolean("isactive"));
			}
			result.result=l;
		}catch(Exception e){
			e.printStackTrace();
			result.isSuccess=false;
			result.message=e.getMessage();
			result.result=null;
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(rs!=null){
					rs.close();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	public static TransporterService getInstance(){		
		 return new TransporterService();
	}
}
