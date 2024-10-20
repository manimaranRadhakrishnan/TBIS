package com.tbis.api.master.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.ScanDetails;

public class ScanDetailsService {
		private static final String insertQuery="INSERT INTO scandetails (transid, scandata, partno,vendorcode,userlocation,qty,serialno,created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
		private static final String select="SELECT s.scanid, s.transid, s.scandata, s.partno,s.vendorcode,s.userlocation,s.qty,s.serialno,ifnull(p.partid,0) partid,ifnull(p.partdescription,'') partdescription,ifnull(c.customerId,0) customerId,ifnull(c.customername,'') customername,ifnull(m.customerpartid,0) customerpartid FROM scandetails s left join part_master p on s.partno=p.partno left join customer_master c on c.customer_erp_code=s.vendorcode left join customer_parts_line_map m on c.customerid=m.customerid and p.partid=m.partid where transid=?;";
		private static final String deleteScannedData="delete from scandetails where transid=? and scanid=? ";
		public ApiResult<ScanDetails> addScanDetails(ArrayList<ScanDetails> l,int userId){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);				
				pstmt=conn.prepareStatement(insertQuery);
				for (ScanDetails scanDetails : l) {
					pstmt.clearParameters();
					pstmt.setLong(1, scanDetails.getTransId());
					pstmt.setString(2, scanDetails.getScanData());
					pstmt.setString(3, scanDetails.getPartNo());
					pstmt.setString(4, scanDetails.getVendorCode());
					pstmt.setString(5, scanDetails.getUserLocation());
					pstmt.setString(6, scanDetails.getQty());
					pstmt.setString(7, scanDetails.getSerialNo());
					pstmt.setInt(8,userId);
					pstmt.addBatch();					
				}
				pstmt.executeBatch();
				conn.commit();
				result.isSuccess=true;
				result.message="Scan Details added successfully";
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
		public ApiResult<ArrayList<ScanDetails>> addCardScanDetails(ScanDetails scanDetails){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult<ArrayList<ScanDetails>> result=new ApiResult<ArrayList<ScanDetails>>();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				if(!"".equals(scanDetails.getScanData())) {
					conn.setAutoCommit(false);				
					pstmt=conn.prepareStatement(insertQuery);
					pstmt.clearParameters();
					pstmt.setLong(1, scanDetails.getTransId());
					pstmt.setString(2, scanDetails.getScanData());
					pstmt.setString(3, scanDetails.getPartNo());
					pstmt.setString(4, scanDetails.getVendorCode());
					pstmt.setString(5, scanDetails.getUserLocation());
					pstmt.setString(6, scanDetails.getQty());
					pstmt.setString(7, scanDetails.getSerialNo());
					pstmt.setInt(8,scanDetails.getUserId());
					pstmt.executeUpdate();
					conn.commit();
				}
				result.isSuccess=true;
				result.result=getScanDetails(scanDetails.getTransId()).result;
				result.message="Scan Details added successfully";
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
		public ApiResult<ArrayList<ScanDetails>> deleteScanDetails(ScanDetails scanDetails){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);				
				pstmt=conn.prepareStatement(deleteScannedData);
				pstmt.setLong(1, scanDetails.getTransId());
				pstmt.setLong(2, scanDetails.getScanId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.result=getScanDetails(scanDetails.getTransId()).result;
				result.message="Scan Details deleted successfully";
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
		public ApiResult<ArrayList<ScanDetails>> getScanDetails(long scanId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<ArrayList<ScanDetails>> result=new ApiResult<ArrayList<ScanDetails>>();
			ArrayList<ScanDetails> resultData=new ArrayList<ScanDetails>();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(select);
				pstmt.setLong(1, scanId);
				rs=pstmt.executeQuery();
				while(rs.next()){
					//,s.vendorcode,s.userlocation,s.qty,s.serialno,p.partid,p.partdescription,c.customerId,c.customername
					ScanDetails l=new ScanDetails();
					l.setScanId(rs.getLong("scanid"));
					l.setTransId(rs.getLong("transid"));
					l.setScanData(rs.getString("scandata"));
					l.setPartNo(rs.getString("partno"));
					l.setVendorCode(rs.getString("vendorcode"));
					l.setUserLocation(rs.getString("userlocation"));
					l.setQty(rs.getString("qty"));
					l.setSerialNo(rs.getString("serialno"));
					l.setPartId(rs.getInt("partid"));
					l.setPartName(rs.getString("partdescription"));
					l.setVendorId(rs.getInt("customerId"));
					l.setVendorName(rs.getString("customername"));
					l.setCustomerPartId(rs.getLong("customerpartid"));
					resultData.add(l);
				}
				result.result=resultData;
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
		public static ScanDetailsService getInstance(){		
			 return new ScanDetailsService();
		}
	}