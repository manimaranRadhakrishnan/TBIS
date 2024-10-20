package com.tbis.api.master.data;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.DeliveryLocation;
import com.tbis.api.master.model.LineRack;
import com.tbis.api.master.model.LineSpace;
import com.tbis.api.master.model.PackingType;
import com.tbis.api.master.model.PlantDocks;
import com.tbis.api.master.model.Plants;
import com.tbis.api.master.model.ScanDetails;
import com.tbis.api.master.model.SpaceMaster;
import com.tbis.api.master.model.SubLocation;
import com.tbis.api.master.model.UnloadDock;
import com.tbis.api.master.model.Warehouse;

public class WarehouseService {
	private static final String insertQuery="INSERT INTO warehouse(locationid, warehousename, warehouseshortcode, address, address2, city, district, state, phone, mobile, email, gstin, latitute, longitude, isactive, created_by, created_date, tsid)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()))";			 
	private static final String modifyQuery="UPDATE warehouse set locationid=?, warehousename=?, warehouseshortcode=?, address=?, address2=?, city=?, district=?, state=?, phone=?, mobile=?, email=?, gstin=?, latitute=?, longitude=?,isactive=?,updated_by=?,updated_date=now(),tsid=getcurrenttsid(now()) where warehousid=?";
	private static final String select="SELECT warehousid, locationid, warehousename, warehouseshortcode, address, address2, city, district, state, phone, mobile, email, gstin, latitute, longitude, isactive FROM warehouse where warehousid=?";
	private static final String duplicateCheck="SELECT customer_erp_code, customername FROM customer_master WHERE customerid != ? and customer_erp_code= ?";
	/* Sub Location */
	private static final String insertSubLocationQuery="INSERT INTO sublocation (warehouseid, sublocationname, sublocationshortcode, slwidth,sllength,slheight,area_in_m2,area_in_m3,slrows,slcolumns,uwidth,ulength,isactive,created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
	private static final String modifySubLocationQuery="UPDATE sublocation SET warehouseid=?, sublocationname=?, sublocationshortcode=?,slheight=? ,area_in_m2=?,area_in_m3=? ,slrows=?,slcolumns=?,uwidth=?,ulength=?,isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE sublocationid=?;";
	private static final String selectSubLocation="SELECT sublocationid, warehouseid, sublocationname, sublocationshortcode, slwidth,sllength,slheight,area_in_sqfeet, area_in_m2,area_in_m3,customer_storage_area, isactive,slrows,slcolumns,uwidth,ulength FROM sublocation WHERE sublocationid=?;";
	private static final String duplicateSubLocationCheck="SELECT sublocationshortcode, sublocationname FROM sublocation WHERE sublocationid != ? and sublocationshortcode= ?";
	private static final String deleteLineSpace="delete from linespace where sublocationid =?";
	private static final String deleteSpaceMaster="delete from spacemaster where sublocationid =?";		
	private static final String selectSubLocationSpace="select s.spaceid,s.sublocationid,s.spacename,s.colorcode,s.linespaceid,s.lineno,s.columnno,s.lineusageid,s.customerid,s.partid ,cm.customername ,l.usagename ,pm.partdescription from spacemaster s left join customer_master cm on cm.customerId = s.customerid left join lineusagemast l on l.usageid = s.lineusageid left join part_master pm on pm.partid = s.partid WHERE s.sublocationid=?;";
	private static final String checkSpaceCreatedQry="select spaceid from spacemaster  WHERE sublocationid=?;";
	
	/* Line Space */
	private static final String insertSpaceQuery="INSERT INTO linespace (sublocationid, linespacecode, customer_storage_area, good_stock_area_length, good_stock_area_width, before_inspection_area_width, before_inspection_area_length, after_inspection_area_width, after_inspection_area_length, allowed_stack_height, noof_part_storage_bays,linelength,linewidth,lineheight,aream2,aream3, lineusageid,isactive, created_by, created_date, tsid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()))"; 
	private static final String modifySpaceQuery="UPDATE linespace SET sublocationid=?, linespacecode=?, customer_storage_area=?, good_stock_area_length=?, good_stock_area_width=?, before_inspection_area_width=?, before_inspection_area_length=?, after_inspection_area_width=?, after_inspection_area_length=?,linelength=?,linewidth=?,lineheight=?,aream2=?,aream3=?, lineusageid=? ,isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE linespaceid = ?";
	private static final String selectSpace="SELECT linespaceid, sublocationid, linespacecode, customer_storage_area, good_stock_area_length, good_stock_area_width, before_inspection_area_width, before_inspection_area_length, after_inspection_area_width, after_inspection_area_length, allowed_stack_height, noof_part_storage_bays,linelength,linewidth,lineheight,aream2,aream3, isactive,lineusageid FROM linespace WHERE linespaceid = ?";
	private static final String duplicateLineSpaceCheck="SELECT sublocationshortcode, sublocationname FROM sublocation WHERE sublocationid != ? and sublocationshortcode= ?";
	
	/* Line Space Auto Generation */
	private static final String createSpaceQuery="INSERT INTO linespace (sublocationid, linespacecode, lineno,linelength,linewidth,lineheight,areaM2,areaM3,isactive, created_by, created_date, tsid) VALUES(?,?,?,?,?,?,?,?,1,?,now(),getcurrenttsid(now()))"; 
	private static final String changeSpaceQuery="UPDATE linespace SET sublocationid=?, linespacecode=?, lineno=?,isactive=1, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE linespaceid = ?";

	/* Space master */
	private static final String insertSpaceMasterQuery="INSERT INTO spacemaster(sublocationid, spacename,linespaceid, lineno,columnno, created_by, created_date, tsid) VALUES(?,?,?,?,?,?,now(),getcurrenttsid(now()))"; 
	private static final String modifySpaceMasterQuery="UPDATE spacemaster SET sublocationid=?, spacename=?, linespaceid=?,lineno=?,columnno=?,updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE spacemasterid = ?";

	/* Line Rack */
	private static final String insertRackQuery="INSERT INTO linerack (linerackcode, customer_storage_area, good_stock_area_length, good_stock_area_width, noof_part_storage_bays,good_stock_area_height,aream2,aream3,isactive, created_by, created_date, tsid,startline,endline,startcol,endcol) VALUES(?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?,?,?,?)"; 
	private static final String modifyRackQuery="UPDATE linerack SET linerackcode=?, customer_storage_area=?, good_stock_area_length=?, good_stock_area_width=?, noof_part_storage_bays=?,good_stock_area_height=?,aream2=?,aream3=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()), startline=?,endline=?,startcol=?,endcol=? WHERE linerackid = ?";
	private static final String selectRack="SELECT linerackid, linerackcode, customer_storage_area, good_stock_area_length, good_stock_area_width, noof_part_storage_bays, good_stock_area_height,aream2,aream3,isactive,startline,endline,startcol,endcol FROM linerack WHERE linerackid = ?";
	
	/* Unload Dock */
	private static final String insertUdockQuery="INSERT INTO unload_doc_master (udcname, sublocationid, isactive ,created_by, created_date, tsid) VALUES (?,?,?,?,now(),getcurrenttsid(now()));"; 
	private static final String modifyUdockQuery="UPDATE unload_doc_master SET udcname=?,sublocationid=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE udc_id=?;";
	private static final String selectUdock="SELECT udc_id, udcname, sublocationid, isactive FROM unload_doc_master WHERE udc_id=?;";
	
	/* Delivery Location */
	
	private static final String insertDeliveryLocationQuery="INSERT INTO deliverylocation(dlocationid,dlocationname,dlocationshortcode,locationid,isactive,created_by,created_date,tsid)VALUES(?,?,?,?,?,?,now(),getcurrenttsid(now()))"; 
	private static final String modifyDeliveryLocationQuery="UPDATE deliverylocation set  dlocationname=?,dlocationshortcode=?, locationid=?,isactive=?,updated_by=?,updated_date=now(),tsid=getcurrenttsid(now()) where dlocationid=?";
	private static final String selectDeliveryLocation="SELECT a.dlocationid,a.dlocationname,a.dlocationshortcode, a.locationid, a.isactive FROM deliverylocation a  where a.dlocationid=?";
	
	/* Plants */
	private static final String insertPlantsQuery="INSERT INTO plants(plantid,plantname,plantshorname,dlocationid,isactive,created_by,created_date,tsid)VALUES(?,?,?,?,?,?,now(),getcurrenttsid(now()))"; 
	private static final String modifyPlantsQuery="UPDATE plants set plantname=?,plantshorname=?, dlocationid=?, isactive=?,updated_by=?,updated_date=now(),tsid=getcurrenttsid(now()) where plantid=?";
	private static final String selectPlants="SELECT a.plantid,a.plantname,a.plantshorname,a.dlocationid,a.isactive FROM plants a where a.plantid=?";
	
	/* Plant Docks */
	private static final String insertPlantDocksQuery="INSERT INTO plantdocks(ddockid,dockname,dockshortname,dlocationid,isactive,created_by,created_date,tsid)VALUES(?,?,?,?,?,?,now(),getcurrenttsid(now()))"; 
	private static final String modifyPlantDocksQuery="UPDATE plantdocks set dockname=?,dockshortname=?, dlocationid=?, isactive=?,updated_by=?,updated_date=now(),tsid=getcurrenttsid(now()) where ddockid=?";
	private static final String selectPlantDocks="SELECT a.ddockid,a.dockname,a.dockshortname,a.dlocationid,a.isactive FROM plantdocks a where a.ddockid=?";
	
	/*Space master properties */
	private static final String updateSpaceMaster="update spacemaster set  lineusageid=?,customerid=?,colorcode=? where sublocationid=? and lineno=? and columnno=? ";
	private static final String checkSpaceAllocation="select spaceid,partid from spacemaster where sublocationid=? and (lineno>=? and lineno<=?) and (columnno>=? and columnno<=?) and ifnull(lineusageid,1)<>1 ";
	
	/*Remove space allocation */
	private static final String removeSpaceAllocation="update spacemaster set lineusageid=1,customerid=0,partid=0,colorcode='' where sublocationid=? and customerid=? ";
	private static final String checkPartMapping="select spaceid from spacemaster where sublocationid=? and customerid=? and (ifnull(lineusageid,1)<>13 or ifnull(partid,0)<>0)";
	
	/*Inbound Import */
	private static final String insertImportInbound="insert into inboundfileimport (filepath, created_by, created_date, tsid) values (?,?,now(),getcurrenttsid(now()));";
	private static final String insertImportInboundData="INSERT INTO inbounddataimport (inboundid, actualinbounddate, planinbounddate, inboundno, inboundref, inboundmethod, warehouse, issuedby, issueddate, inboundstatus, shipper, invoiceno, eta, destinationport, shipmentmode, orderno, ordertype, orderdesc, contractno, wisepno, customerpno, backno, customerpartdesc, uom, qtybox, noofbox, totalqty, unitprice, currency, innerpackboxid, innerpacknetweight, caseno, caseuom, casegrossweight, casenetweight, casem3, containerno, containertype, impcountry, customer, deliverytocustomer, customercontractdesc, customercontractno, expcountry, supplier, ttcsectioncode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	
	public ApiResult<Warehouse> manageWarehouse(Warehouse l){
		if(l.getWarehousId()==0) {
			return addWarehouse(l);
		}else {
			return modifyWarehouse(l);
		}
	}
	public ApiResult<Warehouse> addWarehouse(Warehouse l){
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
			pstmt.setInt(1, l.getLocationId());
			pstmt.setString(2, l.getWarehouseName());
			pstmt.setString(3, l.getWarehouseShortCode());
			pstmt.setString(4, l.getAddress());
			pstmt.setString(5, l.getAddress2());
			pstmt.setString(6, l.getCity());
			pstmt.setString(7, l.getDistrict());
			pstmt.setString(8, l.getState());
			pstmt.setString(9, l.getPhone());
			pstmt.setString(10, l.getMobile());
			pstmt.setString(11, l.getEmail());
			pstmt.setString(12, l.getGstin());
			pstmt.setDouble(13, l.getLatitute());
			pstmt.setDouble(14, l.getLongitude());
			pstmt.setBoolean(15, l.isActive());
			pstmt.setInt(16,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Warehouse added successfully";
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
	public ApiResult<Warehouse> modifyWarehouse(Warehouse l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getLocationId());
			pstmt.setString(2, l.getWarehouseName());
			pstmt.setString(3, l.getWarehouseShortCode());
			pstmt.setString(4, l.getAddress());
			pstmt.setString(5, l.getAddress2());
			pstmt.setString(6, l.getCity());
			pstmt.setString(7, l.getDistrict());
			pstmt.setString(8, l.getState());
			pstmt.setString(9, l.getPhone());
			pstmt.setString(10, l.getMobile());
			pstmt.setString(11, l.getEmail());
			pstmt.setString(12, l.getGstin());
			pstmt.setDouble(13, l.getLatitute());
			pstmt.setDouble(14, l.getLongitude());
			pstmt.setBoolean(15, l.isActive());
			pstmt.setInt(16, l.getUserId());
			pstmt.setInt(17, l.getWarehousId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Warehouse modified successfully";
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
	public ApiResult<Warehouse> getWarehouse(int warehouseId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<Warehouse> result=new ApiResult<Warehouse>();
		Warehouse l=new Warehouse();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(select);
			pstmt.setInt(1, warehouseId);
			rs=pstmt.executeQuery();
			if(rs.next()){
			
				l.setWarehousId(rs.getInt("warehousid"));
				l.setLocationId(rs.getInt("locationid"));
				l.setWarehouseName(rs.getString("warehousename"));
				l.setWarehouseShortCode(rs.getString("warehouseshortcode"));
				l.setAddress(rs.getString("address"));
				l.setAddress2(rs.getString("address2"));
				l.setCity(rs.getString("city"));
				l.setDistrict(rs.getString("district"));
				l.setState(rs.getString("state"));
				l.setPhone(rs.getString("phone"));
				l.setMobile(rs.getString("mobile"));
				l.setEmail(rs.getString("email"));
				l.setGstin(rs.getString("gstin"));
				l.setLatitute(rs.getDouble("latitute"));
				l.setLongitude(rs.getDouble("longitude"));
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
	
	/* Sub Location */
	public ApiResult<SubLocation> manageSubLocation(SubLocation input){
		if(input.getSubLocationId()==0) {
			return addSubLocation(input);
		}else {
			return modifySubLocation(input);
		}
	}
	public ApiResult<SubLocation> addSubLocation(SubLocation l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		if(l.getSlHeight()==0) {
			l.setSlHeight(1.00);	
		}
		if(CheckSublocationShortCode(l.getSubLocationId(), l.getSubLocationShortCode())) {
			result.isSuccess=false;
			result.message="Shorcode already Exist";
			return result;
		}
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertSubLocationQuery,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getWarehouseId());
			pstmt.setString(2, l.getSubLocationName());
			pstmt.setString(3, l.getSubLocationShortCode());
			pstmt.setDouble(4, 0);
			pstmt.setDouble(5, 0);
			pstmt.setDouble(6, l.getSlHeight());
			pstmt.setDouble(7, l.getSlRows()*l.getSlColumns()* l.getuWidth()*l.getuLength());
			pstmt.setDouble(8, l.getSlRows()*l.getSlColumns()* l.getuWidth()*l.getuLength()*l.getSlHeight());
			pstmt.setInt(9, l.getSlRows());
			pstmt.setInt(10, l.getSlColumns());
			pstmt.setDouble(11, l.getuWidth());
			pstmt.setDouble(12, l.getuLength());
			pstmt.setBoolean(13, l.getIsActive());
			pstmt.setInt(14,l.getUserId());
			pstmt.executeUpdate();
			rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				code=rs.getString(1);
			}
			rs.close();
			pstmt.close();
			pstmt=null;
			
			if (CheckSpaceCreated(code)==false) {
				ArrayList<Integer> lineSpaceIds=new ArrayList<Integer>();
				pstmt=conn.prepareStatement(createSpaceQuery,PreparedStatement.RETURN_GENERATED_KEYS);
				for(int i=1;i<=l.getSlRows();i++) {
					pstmt.clearParameters();
					pstmt.setString(1, code);
					pstmt.setString(2, l.getSubLocationShortCode()+"-L"+i);
					pstmt.setInt(3, i);
					pstmt.setDouble(4, l.getuLength());
					pstmt.setDouble(5, l.getuWidth());
					pstmt.setDouble(6, l.getSlHeight());
					pstmt.setDouble(7, l.getuLength()*l.getuWidth()*l.getSlColumns());
					pstmt.setDouble(8, l.getuLength()*l.getuWidth()*l.getSlColumns()*l.getSlHeight());
					pstmt.setInt(9,l.getUserId());
					pstmt.executeUpdate();
					rs=pstmt.getGeneratedKeys();
					if(rs.next()){
						lineSpaceIds.add(rs.getInt(1));
					}
					rs.close();				
				}
				pstmt.close();
				pstmt=null;
				for(int j=0;j<lineSpaceIds.size();j++) {
					pstmt=conn.prepareStatement(insertSpaceMasterQuery);
					for(int i=1;i<=l.getSlColumns();i++) {
						pstmt.clearParameters();
						pstmt.setString(1, code);
						pstmt.setString(2, "L"+(j+1)+"-C"+i);
						pstmt.setInt(3, lineSpaceIds.get(j));
						pstmt.setInt(4, j+1);
						pstmt.setInt(5, i);
						pstmt.setInt(6,l.getUserId());
						pstmt.addBatch();
					}	
					pstmt.executeBatch();
				}
			}
			conn.commit();
			result.isSuccess=true;
			result.message="Sub Location added successfully";
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
	public ApiResult<SubLocation> modifySubLocation(SubLocation l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		if(CheckSublocationShortCode(l.getSubLocationId(), l.getSubLocationShortCode())) {
			result.isSuccess=false;
			result.message="Shorcode already Exist";
			return result;
		}
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifySubLocationQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getWarehouseId());
			pstmt.setString(2, l.getSubLocationName());
			pstmt.setString(3, l.getSubLocationShortCode());
			pstmt.setDouble(4, l.getSlHeight());
			pstmt.setDouble(5, l.getSlRows()*l.getSlColumns()* l.getuWidth()*l.getuLength());
			pstmt.setDouble(6, l.getSlRows()*l.getSlColumns()* l.getuWidth()*l.getuLength()*l.getSlHeight());
			pstmt.setDouble(7, l.getSlRows());
			pstmt.setDouble(8, l.getSlColumns());
			pstmt.setDouble(9,l.getuLength());
			pstmt.setDouble(10,l.getuWidth());
			pstmt.setBoolean(11, l.getIsActive());
			pstmt.setInt(12,l.getUserId());
			pstmt.setInt(13,l.getSubLocationId());
			pstmt.executeUpdate();
			
			/* If not Created */
			pstmt.close();
			pstmt=null;
			String code = l.getSubLocationId()+"";
			if (CheckSpaceCreated(code)==false) {
				ArrayList<Integer> lineSpaceIds=new ArrayList<Integer>();
				pstmt=conn.prepareStatement(createSpaceQuery,PreparedStatement.RETURN_GENERATED_KEYS);
				for(int i=1;i<=l.getSlRows();i++) {
					pstmt.clearParameters();
					pstmt.setLong(1, l.getSubLocationId());
					pstmt.setString(2, l.getSubLocationShortCode()+"-L"+i);
					pstmt.setInt(3, i);
					pstmt.setDouble(4, l.getuLength());
					pstmt.setDouble(5, l.getuWidth());
					pstmt.setDouble(6, l.getSlHeight());
					pstmt.setDouble(7, l.getuLength()*l.getuWidth()*l.getSlColumns());
					pstmt.setDouble(8, l.getuLength()*l.getuWidth()*l.getSlColumns()*l.getSlHeight());
					pstmt.setInt(9,l.getUserId());
					pstmt.executeUpdate();
					rs=pstmt.getGeneratedKeys();
					if(rs.next()){
						lineSpaceIds.add(rs.getInt(1));
					}
					rs.close();				
				}
				pstmt.close();
				pstmt=null;
				for(int j=0;j<lineSpaceIds.size();j++) {
					pstmt=conn.prepareStatement(insertSpaceMasterQuery);
					for(int i=1;i<=l.getSlColumns();i++) {
						pstmt.clearParameters();
						pstmt.setLong(1, l.getSubLocationId());
						pstmt.setString(2, "L"+(j+1)+"-C"+i);
						pstmt.setInt(3, lineSpaceIds.get(j));
						pstmt.setInt(4, j+1);
						pstmt.setInt(5, i);
						pstmt.setInt(6,l.getUserId());
						pstmt.addBatch();
					}	
					pstmt.executeBatch();
				}
			}
			conn.commit();
			result.isSuccess=true;
			result.message="Sub Location modified successfully";
		
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
	public ApiResult<SubLocation> getSubLocation(long subLocationId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<SubLocation> result=new ApiResult<SubLocation>();
		SubLocation l=new SubLocation();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectSubLocation);
			pstmt.setLong(1, subLocationId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setSubLocationId(rs.getInt("sublocationid"));
				l.setWarehouseId(rs.getInt("warehouseid"));
				l.setSubLocationName(rs.getString("sublocationname"));
				l.setSubLocationShortCode(rs.getString("sublocationshortcode"));
				l.setAreaInSqFeet(rs.getDouble("area_in_sqfeet"));
				l.setSlWidth(rs.getDouble("slwidth"));
				l.setSlLength(rs.getDouble("sllength"));
				l.setSlHeight(rs.getDouble("slheight"));
				l.setAreaInM2(rs.getDouble("area_in_m2"));
				l.setAreaInM3(rs.getDouble("area_in_m3"));
				l.setCustomerStorageArea(rs.getDouble("customer_storage_area"));
				l.setIsActive(rs.getBoolean("isactive"));
				l.setSlRows(rs.getInt("slrows"));
				l.setSlColumns(rs.getInt("slcolumns"));
				l.setuWidth(rs.getDouble("uwidth"));
				l.setuLength(rs.getDouble("ulength"));
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
	
	public ArrayList<SpaceMaster> getSubLocationSpace(long subLocationId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ArrayList<SpaceMaster> result = new ArrayList<SpaceMaster>();
		SpaceMaster l = new SpaceMaster();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectSubLocationSpace);
			pstmt.setLong(1, subLocationId);
			rs=pstmt.executeQuery();
			while (rs.next()) {
				l=new SpaceMaster();
				l.setSpaceId(rs.getInt("spaceid"));
				l.setSubLocationId(rs.getInt("sublocationid"));
				l.setSpaceName(rs.getString("spacename"));
				l.setColorCode(rs.getString("colorCode"));
				l.setLineNo(rs.getInt("lineno"));
				l.setColumnNo(rs.getInt("columnno"));
				l.setLineUsageId(rs.getInt("lineusageid"));
				l.setLineSpaceId(rs.getInt("linespaceid"));
				l.setCustomerid(rs.getLong("customerid"));
				l.setPartId(rs.getLong("partid"));
				l.setCustomerName(rs.getString("customername"));
				l.setLineUsageName(rs.getString("usagename"));
				l.setPartName(rs.getString("partdescription"));
				result.add(l);
			}
		}catch(Exception e){
			result=null;
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
	
	/* Line Space */
	public ApiResult<LineSpace> manageLineSpace(LineSpace input){
		if(input.getLineSpaceId()==0) {
			return addLineSpace(input);
		}else {
			return modifyLineSpace(input);
		}
	}
	public ApiResult<LineSpace> addLineSpace(LineSpace l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertSpaceQuery);
			pstmt.clearParameters();
			pstmt.setInt(1,l.getSubLocationId());
			pstmt.setString(2,l.getLineSpaceCode());
			pstmt.setDouble(3,l.getCustomerStorageArea());
			pstmt.setDouble(4,l.getGoodStockAreaLength());
			pstmt.setDouble(5,l.getGoodStockAreaWidth());
			pstmt.setDouble(6,l.getBeforeInspectionAreaWidth());
			pstmt.setDouble(7,l.getBeforeInspectionAreaLength());
			pstmt.setDouble(8,l.getAfterInspectionAreaWidth());
			pstmt.setDouble(9,l.getAfterInspectionAreaLength());
			pstmt.setDouble(10,l.getAllowedStackHeight());
			pstmt.setDouble(11,l.getNoofPartStorageBays());
			pstmt.setDouble(12,l.getLineLength());
			pstmt.setDouble(13,l.getLineWidth());
			pstmt.setDouble(14,l.getLineHeight());
			pstmt.setDouble(15, (l.getLineWidth()*l.getLineLength())/10000);
			pstmt.setDouble(16, (l.getLineWidth()*l.getLineLength()*l.getLineHeight())/10000);
			pstmt.setInt(17,l.getLineUsageId()) ;
			pstmt.setBoolean(18,l.getIsActive());
			pstmt.setInt(19,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Linespace added successfully";
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
	public ApiResult<LineSpace> modifyLineSpace(LineSpace l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifySpaceQuery);
			pstmt.clearParameters();
			pstmt.setInt(1,l.getSubLocationId());
			pstmt.setString(2,l.getLineSpaceCode());
			pstmt.setDouble(3,l.getCustomerStorageArea());
			pstmt.setDouble(4,l.getGoodStockAreaLength());
			pstmt.setDouble(5,l.getGoodStockAreaWidth());
			pstmt.setDouble(6,l.getBeforeInspectionAreaWidth());
			pstmt.setDouble(7,l.getBeforeInspectionAreaLength());
			pstmt.setDouble(8,l.getAfterInspectionAreaWidth());
			pstmt.setDouble(9,l.getAfterInspectionAreaLength());
			pstmt.setDouble(10,l.getLineLength());
			pstmt.setDouble(11,l.getLineWidth());
			pstmt.setDouble(12,l.getLineHeight());
			pstmt.setDouble(13, (l.getLineWidth()*l.getLineLength())/10000);
			pstmt.setDouble(14, (l.getLineWidth()*l.getLineLength()*l.getLineHeight())/10000);
			pstmt.setInt(15,l.getLineUsageId()) ;
			pstmt.setBoolean(16,l.getIsActive());
			pstmt.setInt(17,l.getUserId());
			pstmt.setInt(18,l.getLineSpaceId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Linespace modified successfully";
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
	public ApiResult<LineSpace> getLineSpace(long lineSpaceId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<LineSpace> result=new ApiResult<LineSpace>();
		LineSpace l=new LineSpace();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectSpace);
			pstmt.setLong(1, lineSpaceId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setLineSpaceId(rs.getInt("linespaceid"));
				l.setSubLocationId(rs.getInt("sublocationid"));
				l.setLineSpaceCode(rs.getString("linespacecode"));
				l.setCustomerStorageArea(rs.getDouble("customer_storage_area"));
				l.setGoodStockAreaLength(rs.getDouble("good_stock_area_length"));
				l.setGoodStockAreaWidth(rs.getDouble("good_stock_area_width"));
				l.setBeforeInspectionAreaLength(rs.getDouble("before_inspection_area_length"));
				l.setBeforeInspectionAreaWidth(rs.getDouble("before_inspection_area_width"));
				l.setAfterInspectionAreaLength(rs.getDouble("after_inspection_area_length"));
				l.setAfterInspectionAreaWidth(rs.getDouble("after_inspection_area_width"));
				l.setNoofPartStorageBays(rs.getInt("noof_part_storage_bays"));
				l.setAllowedStackHeight(rs.getInt("allowed_stack_height"));
				l.setLineHeight(rs.getDouble("lineheight"));
				l.setLineWidth(rs.getDouble("linewidth"));
				l.setLineLength(rs.getDouble("linelength"));
				l.setAreaM2(rs.getDouble("areaM2"));
				l.setAreaM3(rs.getDouble("areaM3"));
				l.setIsActive(rs.getBoolean("isactive"));
				l.setLineUsageId(rs.getInt("lineusageid"));
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
	
	
	/* Line Rack */
	public ApiResult<LineRack> manageLineRack(LineRack input){
		if(input.getLineRackId()==0) {
			return addLineRack(input);
		}else {
			return modifyLineRack(input);
		}
	}
	public ApiResult<LineRack> addLineRack(LineRack l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		if(l.getGoodStockAreaHeight()<=0) {
			l.setGoodStockAreaHeight(1.000);
		}
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertRackQuery);
			pstmt.clearParameters();
			pstmt.setString(1,l.getLineRackCode());
			pstmt.setDouble(2,l.getCustomerStorageArea());
			pstmt.setDouble(3,l.getGoodStockAreaLength());
			pstmt.setDouble(4,l.getGoodStockAreaWidth());
			pstmt.setInt(5,l.getNoofPartStorageBays());
			pstmt.setDouble(6,l.getGoodStockAreaHeight());
			pstmt.setDouble(7, (l.getGoodStockAreaWidth()*l.getGoodStockAreaLength())/10000);
			pstmt.setDouble(8, (l.getGoodStockAreaWidth()*l.getGoodStockAreaLength()*l.getGoodStockAreaHeight())/10000);
			pstmt.setBoolean(9,l.getIsActive());
			pstmt.setInt(10,l.getUserId());
			pstmt.setInt(11,l.getStartLine());
			pstmt.setInt(12,l.getEndLine());
			pstmt.setInt(13,l.getStartCol());
			pstmt.setInt(14,l.getEndCol());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Racks added successfully";
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
	public ApiResult<LineRack> modifyLineRack(LineRack l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyRackQuery);
			pstmt.clearParameters();
			pstmt.setString(1,l.getLineRackCode());
			pstmt.setDouble(2,l.getCustomerStorageArea());
			pstmt.setDouble(3,l.getGoodStockAreaLength());
			pstmt.setDouble(4,l.getGoodStockAreaWidth());
			pstmt.setInt(5,l.getNoofPartStorageBays());
			pstmt.setDouble(6,l.getGoodStockAreaHeight());
			pstmt.setDouble(7, (l.getGoodStockAreaWidth()*l.getGoodStockAreaLength())/10000);
			pstmt.setDouble(8, (l.getGoodStockAreaWidth()*l.getGoodStockAreaLength()*l.getGoodStockAreaHeight())/10000);
			pstmt.setBoolean(9,l.getIsActive());
			pstmt.setInt(10,l.getUserId());
			pstmt.setInt(11,l.getStartLine());
			pstmt.setInt(12,l.getEndLine());
			pstmt.setInt(13,l.getStartCol());
			pstmt.setInt(14,l.getEndCol());
			pstmt.setInt(15,l.getLineRackId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Lineracks modified successfully";
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
	public ApiResult<LineRack> getLineRack(long lineRackId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<LineRack> result=new ApiResult<LineRack>();
		LineRack l=new LineRack();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectRack);
			pstmt.setLong(1, lineRackId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setLineRackId(rs.getInt("linerackid"));
				l.setLineRackCode(rs.getString("linerackcode"));
				l.setCustomerStorageArea(rs.getDouble("customer_storage_area"));
				l.setGoodStockAreaLength(rs.getDouble("good_stock_area_length"));
				l.setGoodStockAreaWidth(rs.getDouble("good_stock_area_width"));
				l.setNoofPartStorageBays(rs.getInt("noof_part_storage_bays"));
				l.setGoodStockAreaHeight(rs.getDouble("good_stock_area_height"));
				l.setAreaM2(rs.getDouble("aream2"));
				l.setAreaM3(rs.getDouble("aream3"));
				l.setIsActive(rs.getBoolean("isactive"));
				l.setStartLine(rs.getInt("startline"));
				l.setEndLine(rs.getInt("endline"));
				l.setStartCol(rs.getInt("startcol"));
				l.setEndCol(rs.getInt("endcol"));
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
	
	public ApiResult<UnloadDock> manageUnloadDock(UnloadDock input){
		if(input.getUdcId()==0) {
			return addUnloadDock(input);
		}else {
			return modifyUnloadDock(input);
		}
	}
	public ApiResult<UnloadDock> addUnloadDock(UnloadDock l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertUdockQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getUdcName());
			pstmt.setInt(2, l.getSubLocationId());
			pstmt.setBoolean(3, l.isActive());
			pstmt.setInt(4,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Unload Dock added successfully";
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
	public ApiResult<UnloadDock> modifyUnloadDock(UnloadDock l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyUdockQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getUdcName());
			pstmt.setInt(2, l.getSubLocationId());
			pstmt.setBoolean(3, l.isActive());
			pstmt.setInt(4,l.getUserId());
			pstmt.setInt(5,l.getUdcId());
			
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Unload Dock modified successfully";
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
	public ApiResult<UnloadDock> getUnloadDock(long udcId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<UnloadDock> result=new ApiResult<UnloadDock>();
		UnloadDock l=new UnloadDock();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectUdock);
			pstmt.setLong(1, udcId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setUdcId(rs.getInt("udc_id"));
				l.setUdcName(rs.getString("udcname"));
				l.setSubLocationId(rs.getInt("sublocationid"));
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
	
	public ApiResult<DeliveryLocation> manageDeliveryLocation(DeliveryLocation l){
		if(l.getdLocationId()==0) {
			return addDeliveryLocation(l);
		}else {
			return modifyDeliveryLocation(l);
		}
	}
	public ApiResult<DeliveryLocation> addDeliveryLocation(DeliveryLocation l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertDeliveryLocationQuery);
			pstmt.clearParameters();
			pstmt.setString(1, code);
			pstmt.setString(2, l.getdLocationName());
			pstmt.setString(3, l.getdLocationShortCode());
			pstmt.setInt(4, l.getLocationId());
			pstmt.setBoolean(5, l.getIsActive());
			pstmt.setInt(6,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Delivery Location added successfully";
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
	
	public ApiResult<DeliveryLocation> modifyDeliveryLocation(DeliveryLocation l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyDeliveryLocationQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getdLocationName());
			pstmt.setString(2, l.getdLocationShortCode());
			pstmt.setInt(3, l.getLocationId());
			pstmt.setBoolean(4, l.getIsActive());
			pstmt.setInt(5, l.getUserId());
			pstmt.setInt(6, l.getdLocationId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Delivery Location modified successfully";
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
	public ApiResult<DeliveryLocation> getDeliveryLocation(int locationId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<DeliveryLocation> result=new ApiResult<DeliveryLocation>();
		DeliveryLocation l=new DeliveryLocation();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectDeliveryLocation);
			pstmt.setInt(1, locationId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				
				l.setdLocationName(rs.getString("dlocationname"));
				l.setdLocationShortCode(rs.getString("dlocationshortcode"));
				l.setLocationId(rs.getInt("locationid"));
				l.setdLocationId(rs.getInt("dlocationid"));
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
	
	private boolean CheckSpaceCreated(String sublocationId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		boolean result=false;
		PackingType l=new PackingType();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(checkSpaceCreatedQry);
			pstmt.setString(1, sublocationId);
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
	
	private boolean CheckSublocationShortCode(long sublocationId,String shortCode){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		boolean result=false;
		PackingType l=new PackingType();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(duplicateSubLocationCheck);
			pstmt.setLong(1, sublocationId);
			pstmt.setString(2, shortCode);
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
	
	private boolean CheckLineSpaceShortCode(long lineSpaceId,String shortCode){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		boolean result=false;
		PackingType l=new PackingType();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(duplicateLineSpaceCheck);
			pstmt.setLong(1, lineSpaceId);
			pstmt.setString(2, shortCode);
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
	
	/* Plants */
	public ApiResult<Plants> managePlants(Plants input){
		if(input.getPlantId()==0) {
			return addPlants(input);
		}else {
			return modifyPlants(input);
		}
	}
	public ApiResult<Plants> addPlants(Plants l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertPlantsQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getPlantId());
			pstmt.setString(2, l.getPlantName());
			pstmt.setString(3, l.getPlantShorName());
			pstmt.setInt(4, l.getdLocationId());
			pstmt.setBoolean(5, l.getIsActive());
			pstmt.setInt(6,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Plants added successfully";
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
	public ApiResult<Plants> modifyPlants(Plants l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyPlantsQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getPlantName());
			pstmt.setString(2, l.getPlantShorName());
			pstmt.setInt(3, l.getdLocationId());
			pstmt.setBoolean(4, l.getIsActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.setInt(6, l.getPlantId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Plants modified successfully";
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
	public ApiResult<Plants> getPlants(long PlantsId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<Plants> result=new ApiResult<Plants>();
		Plants l=new Plants();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectPlants);
			pstmt.setLong(1, PlantsId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setPlantId(rs.getInt("plantid"));
				l.setPlantName(rs.getString("plantname"));
				l.setPlantShorName(rs.getString("plantshorname"));
				l.setdLocationId(rs.getInt("dlocationid"));
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
	
	/* Plants Docks */
	
	public ApiResult<PlantDocks> managePlantDocks(PlantDocks input){
		if(input.getdDockId()==0) {
			return addPlantDocks(input);
		}else {
			return modifyPlantDocks(input);
		}
	}
	public ApiResult<PlantDocks> addPlantDocks(PlantDocks l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(insertPlantDocksQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getdDockId());
			pstmt.setString(2, l.getDockName());
			pstmt.setString(3, l.getDockShortName());
			pstmt.setInt(4, l.getdLocationId());
			pstmt.setBoolean(5, l.getIsActive());
			pstmt.setInt(6,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Plant Docks added successfully";
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
	public ApiResult<PlantDocks> modifyPlantDocks(PlantDocks l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(modifyPlantDocksQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getDockName());
			pstmt.setString(2, l.getDockShortName());
			pstmt.setInt(3, l.getdLocationId());
			pstmt.setBoolean(4, l.getIsActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.setInt(6, l.getdDockId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Plant Docks modified successfully";
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
	public ApiResult<PlantDocks> getPlantDocks(long PlantsId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<PlantDocks> result=new ApiResult<PlantDocks>();
		PlantDocks l=new PlantDocks();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectPlantDocks);
			pstmt.setLong(1, PlantsId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				l.setdDockId(rs.getInt("ddockid"));
				l.setDockName(rs.getString("dockname"));
				l.setDockShortName(rs.getString("dockshortname"));
				l.setdLocationId(rs.getInt("dlocationid"));
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
	public ApiResult<SpaceMaster> checkSpaceAllocation(SpaceMaster input){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(checkSpaceAllocation);
			pstmt.clearParameters();
			pstmt.setInt(1, input.getSubLocationId());
			pstmt.setInt(2, input.getStartRow());
			pstmt.setInt(3, input.getEndRow());
			pstmt.setInt(4, input.getStartCell());
			pstmt.setInt(5,input.getEndCell());
			rs=pstmt.executeQuery();
			if(rs.next()) {
				result.isSuccess=false;
				result.message="Space already mapped";
			}else {
				result.isSuccess=true;
				result.message="Space not mapped";
			}
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
	public ApiResult<SpaceMaster> updateSpaceAllocation(SpaceMaster input){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(updateSpaceMaster);
			for(int i=input.getStartRow();i<=input.getEndRow();i++){
		        for(int j=input.getStartCell();j<=input.getEndCell();j++){
					pstmt.clearParameters();
					pstmt.setInt(1, input.getLineUsageId());
					pstmt.setLong(2, input.getCustomerid());
					pstmt.setString(3, input.getColorCode());
					pstmt.setInt(4, input.getSubLocationId());
					pstmt.setInt(5, i);
					pstmt.setInt(6, j);
					pstmt.addBatch();		          
		        }
		     }
			pstmt.executeBatch();
			conn.commit();
			result.isSuccess=true;
			result.message="Space allocated successfully";
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
	public ApiResult<SpaceMaster> removeSpaceAllocation(SpaceMaster input){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(checkPartMapping);
			pstmt.clearParameters();
			pstmt.setInt(1, input.getSubLocationId());
			pstmt.setLong(2, input.getCustomerid());
			rs=pstmt.executeQuery();
			if(rs.next()) {
				result.isSuccess=false;
				result.message="Customer parts mapping exists. Cannot remove customer space mapping.";
				return result;
			}
			rs.close();
			pstmt.close();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(removeSpaceAllocation);
			pstmt.clearParameters();
			pstmt.setInt(1, input.getSubLocationId());
			pstmt.setLong(2, input.getCustomerid());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Space allocation removed successfully";
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
				if(rs!=null){
					rs.close();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	public ApiResult<Warehouse> readInbounddataFile(int userId,String fileLocation,String fileName){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ApiResult result=new ApiResult();
		ResultSet rs=null;
		String code="0";
		try{
			result.isSuccess=true;
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			
			pstmt=conn.prepareStatement(insertImportInbound,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setString(1, fileName);
			pstmt.setInt(2,userId);
			pstmt.executeUpdate();
			rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				code=rs.getString(1);
				System.out.println(":::::::insertImportInbound ID ::::::: "+Integer.parseInt(code));
			}
			rs.close();
			pstmt.close();
			pstmt=null;
			
			pstmt=conn.prepareStatement(insertImportInboundData);
						
			FileInputStream inputStream = new FileInputStream(fileLocation);
			XSSFWorkbook wb = new XSSFWorkbook(new File(fileLocation));
			XSSFSheet sheet = wb.getSheetAt(0);
			
			Iterator ite = sheet.rowIterator();
			int j=0;
			while(ite.hasNext()) {
				j++;
                Row row = (Row) ite.next(); 
                System.out.println("Row value fetched..! ");
                Iterator<Cell> cellIterator = row.cellIterator();
                System.out.println("Cell Iterator invoked..! ");
                int index=1;
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    System.out.println("getting cell value..! "+ cell +"---"+cell.getCellType()+" "+j+" "+index+" "+cell.getCellStyle().getDataFormat());
                    
                    if(index==1) {
                        pstmt.setInt(index, Integer.parseInt(code));
                        index++;
                        System.out.println("insed getting cell value..! "+ Integer.parseInt(code) +"---"+cell.getCellType()+" "+j+" "+index+" "+cell.getCellStyle().getDataFormat());
                    }
                    
                    switch(cell.getCellType()) { 
                    case STRING: //handle string columns
                    	pstmt.setString(index, cell.getStringCellValue());                                                                                     
                        break;
                    case NUMERIC: //handle double data
                    	if (DateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							String dateformatstring = "";
							System.out.println("sssssssssssssssssssssssssssss "+cell.getCellStyle().getDataFormat());
							if (cell.getCellStyle().getDataFormat() == 14) {
								dateformatstring = "yyyy-MM-dd"; 
							} else if (cell.getCellStyle().getDataFormat() == 18) {
								dateformatstring = "hh:mm"; 
							}
							String formattedvalue = new CellDateFormatter(dateformatstring).format(date);
							pstmt.setString(index, formattedvalue);
                        }else {
                        	pstmt.setInt(index, (int) cell.getNumericCellValue());
                        }
                        break;
                    case BLANK: //handle blank data
                        pstmt.setString(index, null);
                        break;
                    }
                    index++;
                    if(index>46) {
                    	break;
                    }
                }
                if(j>2) {
                	
                	pstmt.addBatch();
                }
            }
					
			if(!result.isSuccess) {
				conn.rollback();
				return result;
			}
			pstmt.executeBatch();
			pstmt.close();
			
			
			String sql =" update inbounddataimport i,(select @rownum := @rownum + 1 palletno, caseno,containerno,inboundid from (select caseno,containerno,inboundid from inbounddataimport r where asnstatusid=23 and inboundid =? group by caseno,containerno,inboundid ) b, (SELECT @rownum := 0) r ) s set i.palletno=s.palletno where i.inboundid=s.inboundid and i.containerno=s.containerno and i.caseno=s.caseno ";
			pstmt=conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, code);
			pstmt.executeUpdate();
			pstmt.close();
			
			sql =" update inbounddataimport i inner join part_master p on p.partno =i.wisepno set i.partid =p.partid where i.inboundid =? ";
			pstmt=conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, code);
			pstmt.executeUpdate();
			pstmt.close();
			
			sql =" update inbounddataimport i inner join customer_master p on p.customername =i.customer set i.customerid =p.customerId where i.inboundid =? ";
			pstmt=conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setString(1, code);
			pstmt.executeUpdate();
			pstmt.close();

			conn.commit();
			result.isSuccess=true;
			result.message="Inbound Data Uploaded Successfully";
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
	
	public static WarehouseService getInstance(){		
		 return new WarehouseService();
	}
}
