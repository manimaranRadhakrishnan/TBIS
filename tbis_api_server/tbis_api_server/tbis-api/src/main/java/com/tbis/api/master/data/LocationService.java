package com.tbis.api.master.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.Location;
import com.tbis.api.master.model.PackingType;

public class LocationService{
	private static final String insertQuery="INSERT INTO location(locationid,locationname,locationshortcode,isactive,created_by,created_date,tsid)VALUES(?,?,?,?,?,now(),getcurrenttsid(now()))"; 
	private static final String modifyQuery="UPDATE location set locationname=?,locationshortcode=?,isactive=?,updated_by=?,updated_date=now(),tsid=getcurrenttsid(now()) where locationid=?";
	private static final String select="SELECT a.locationid,a.locationname,a.locationshortcode,a.isactive FROM location a  where a.locationId=?";
	private static final String updateLocationKey="update tran_seq_no set seq_no=seq_no+1 where tran_id=1003";
	private static final String selectLocationKey="select seq_no from tran_seq_no where tran_id=1003";
	private static final String duplicateCheck="SELECT locationid, locationshortcode FROM location WHERE locationId != ? and locationshortcode= ?";
	public ApiResult<Location> manageLocation(Location l){
		if(l.getLocationId()==0) {
			return addLocation(l);
		}else {
			return modifyLocation(l);
		}
	}
	public ApiResult<Location> addLocation(Location l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		if( CheckLocationShortCode(l.getLocationId(),l.getLocationShortCode())){
			result.isSuccess=false;
			result.message="Short Code Already Exists";
			return result;
		}
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(updateLocationKey);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt=conn.prepareStatement(selectLocationKey);
			rs=pstmt.executeQuery();
			if(rs.next()){
				code=rs.getString("seq_no");				
			}
			rs.close();
			pstmt=conn.prepareStatement(insertQuery);
			pstmt.clearParameters();
			pstmt.setString(1, code);
			pstmt.setString(2, l.getLocationName());
			pstmt.setString(3, l.getLocationShortCode());
			pstmt.setInt(4, l.getIsActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Location added successfully";
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
	
	public ApiResult<Location> modifyLocation(Location l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		if( CheckLocationShortCode(l.getLocationId(),l.getLocationShortCode())){
			result.isSuccess=false;
			result.message="Short Code Already Exists";
			return result;
		}
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getLocationName());
			pstmt.setString(2, l.getLocationShortCode());
			pstmt.setInt(3, l.getIsActive());
			pstmt.setInt(4, l.getUserId());
			pstmt.setInt(5, l.getLocationId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Location modified successfully";
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
	public ApiResult<Location> getLocation(int locationId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<Location> result=new ApiResult<Location>();
		Location l=new Location();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(select);
			pstmt.setInt(1, locationId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setLocationName(rs.getString("locationname"));
				l.setLocationShortCode(rs.getString("locationshortcode"));
				l.setIsActive(rs.getInt("isactive"));
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
	
	private boolean CheckLocationShortCode(long locationId,String locationShortcode){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		boolean result=false;
		PackingType l=new PackingType();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(duplicateCheck);
			pstmt.setLong(1, locationId);
			pstmt.setString(2, locationShortcode);
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
	public static LocationService getInstance(){		
		 return new LocationService();
	}
}