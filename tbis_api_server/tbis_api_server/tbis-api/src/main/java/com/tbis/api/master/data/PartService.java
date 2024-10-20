package com.tbis.api.master.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.PackingType;
import com.tbis.api.master.model.PartAssembly;
import com.tbis.api.master.model.PartDeliveryLocation;
import com.tbis.api.master.model.PartKitMaster;
import com.tbis.api.master.model.PartMaster;

public class PartService {
		private static final String insertQuery="INSERT INTO part_master (partno, partdescription, packingtypeid, spq, noofstack, noofpackinginpallet, packingheight, packingwidth, packinglength, packingweight, palletheight, palletwidth, palletlength, palletweight, dispatchtype, loadingtype, repack, m2, m3, isactive, created_by, created_date, tsid,unloaddockid,binqty,locationid,requestinspection,deliverylocationid,plantname,unloadingdock,repackqty,packingweightwithpart,opackingweight,opackingweightwithpart,onoofstack,onoofpackinginpallet,opalletheight,opalletwidth,opalletlength, opalletweight, finalpartid,opackingtypeid,customerpartcode,customerid,pnoofstack,obinqty) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"; 
		private static final String modifyQuery="UPDATE part_master SET partno=?, partdescription=?, packingtypeid=?, spq=?, noofstack=?, noofpackinginpallet=?, packingheight=?, packingwidth=?, packinglength=?, packingweight=?, palletheight=?, palletwidth=?, palletlength=?, palletweight=?, dispatchtype=?, loadingtype=?, repack=?, m2=?, m3=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()),unloaddockid=?,binqty=?,locationid=?,requestinspection=?,deliverylocationid=?,plantname=?,unloadingdock=?,repackqty=?,packingweightwithpart=?,opackingweight=?,opackingweightwithpart=?,onoofstack=?,onoofpackinginpallet=?,opalletheight=?,opalletwidth=?,opalletlength=?, opalletweight=?,opackingtypeid=?,customerpartcode=?,customerid=?,pnoofstack=?,obinqty=? WHERE partid = ?;";
		private static final String select="SELECT partid, partno, partdescription, packingtypeid, spq, noofstack, noofpackinginpallet, packingheight, packingwidth, packinglength, packingweight, palletheight, palletwidth, palletlength, palletweight, dispatchtype, loadingtype, repack, m2, m3, isactive,unloaddockid,binqty,locationid,requestinspection,deliverylocationid,plantname,unloadingdock,repackqty,packingweightwithpart,opackingweight,opackingweightwithpart,onoofstack,onoofpackinginpallet,opalletheight,opalletwidth,opalletlength, opalletweight, finalpartid,opackingtypeid,customerpartcode,customerid,pnoofstack,obinqty FROM part_master WHERE partid = ?;"; 
		private static final String duplicateCheck="SELECT partno, partid FROM part_master WHERE partid != ? and partno=? ";
		
		private static final String insertPackingQuery="INSERT INTO packingtypes ( packingname, packingshortname, PackingHeight, PackingWidth, PackingLength, PackingWeight, M2, M3, isactive, created_by, created_date, tsid,packingcategory) VALUES(?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?)"; 
		private static final String modifyPackingQuery="UPDATE packingtypes SET packingname=?, packingshortname=?, PackingHeight=?, PackingWidth=?, PackingLength=?, PackingWeight=?, M2=?, M3=?, isactive=?, updated_by=?,updated_date=now(), tsid=getcurrenttsid(now()), packingcategory=? WHERE packingtypeid = ?";
		private static final String selectPacking="SELECT packingtypeid, packingname, packingshortname, PackingHeight, PackingWidth, PackingLength, PackingWeight, M2, M3, isactive,packingcategory FROM packingtypes WHERE packingtypeid = ?";
		private static final String duplicatePackingCheck="SELECT packingname, packingshortname FROM packingtypes WHERE packingtypeid != ? and packingshortname= ?";
		
		
		private static final String insertKitQuery="INSERT INTO part_kit_master( part_id_enduser, part_id_customer, isactive, created_by, created_date, tsid) VALUES(?,?,?,?,now(),getcurrenttsid(now()))"; 
		private static final String modifyKitQuery="UPDATE part_kit_master SET part_id_enduser=?, part_id_customer=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE partkitmasterid = ?";
		private static final String selectKit="SELECT partkitmasterid, part_id_enduser, part_id_customer, isactive FROM part_kit_master WHERE partkitmasterid = ?";
		
		private static final String insertAssemQuery ="INSERT INTO partassembly(primarypartid, assemblypartname, assemblypartcode, isactive, created_by,qty, created_date, tsid) VALUES (?,?,?,?,?,?,now(),getcurrenttsid(now()));";
		private static final String modifyAssemQuery ="update partassembly set primarypartid=?, assemblypartname=?, assemblypartcode=?, isactive=?, updated_by=?,qty, updated_date=now(), tsid=getcurrenttsid(now()) WHERE partassemblyid=?";
		private static final String delAssemQuery ="delete from partassembly WHERE partassemblyid=?";
		private static final String selectAssemQuery ="select partassemblyid, primarypartid, assemblypartname, assemblypartcode, isactive from  partassembly WHERE partassemblyid=?";
		
		
		private static final String insertDeliveryLocation ="INSERT INTO partdeliverydock( partid, dlocationid, plantid, plantdockid, isactive, created_by, created_date,  tsid) VALUES (?,?,?,?,?,?,now(),getcurrenttsid(now()));";
		private static final String modifyDeliveryLocation ="update partdeliverydock set partid=?, dlocationid=?, plantid=?, plantdockid=?, isactive=?, created_by=?, created_date=?,  tsid=? where partdeliverydockid=?";
		private static final String delDeliveryLocation ="delete from partdeliverydock  where partdeliverydockid=?";
		private static final String selectDeliveryLocation ="SELECT partdeliverydockid, partid, dlocationid, plantid, plantdockid, isactive, created_by, created_date, updated_by, updated_date, tsid FROM partdeliverydock WHERE partdeliverydockid=?";
		
		
		public ApiResult<PartMaster> managePartMaster(PartMaster input){
			if(input.getPartId()==0) {
				return addPartMaster(input);
			}else {
				return modifyPartMaster(input);
			}
		}
		public ApiResult<PartMaster> addPartMaster(PartMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			if(CheckPartShortCode(l.getPartId(),l.getPartNo())) {
				result.isSuccess=false;
				result.message="Part Code Already Exists";
				return result;
			}
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertQuery);
				pstmt.clearParameters();
				pstmt.setString(1, l.getPartNo());
				pstmt.setString(2, l.getPartDescription());
				pstmt.setInt(3, l.getPackingTypeId());
				pstmt.setInt(4, l.getSpq());
				pstmt.setInt(5, l.getNoOfStack());
				pstmt.setInt(6, l.getNoOfPackingInPallet());
				pstmt.setDouble(7, l.getPackingHeight());
				pstmt.setDouble(8, l.getPackingWidth());
				pstmt.setDouble(9, l.getPackingLength());
				pstmt.setDouble(10, l.getPackingWeight());
				pstmt.setDouble(11, l.getPalletHeight());
				pstmt.setDouble(12, l.getPalletWidth());
				pstmt.setDouble(13, l.getPalletLength());
				pstmt.setDouble(14, l.getPalletWeight());
				pstmt.setInt(15, l.getDispatchType());
				pstmt.setInt(16, l.getLoadingType());
				pstmt.setInt(17, l.getRePack());
				pstmt.setDouble(18, l.getM2());
				pstmt.setDouble(19, l.getM3());
				pstmt.setBoolean(20, l.isActive());
				pstmt.setInt(21,l.getUserId());
				pstmt.setInt(22, l.getUnloadDockId());
				pstmt.setInt(23, l.getBinQty());
				pstmt.setInt(24, l.getLocationId());
				pstmt.setBoolean(25, l.getReqInspection());
				pstmt.setInt(26, l.getDeliveryLocationId());
				pstmt.setString(27, l.getPlantName());
				pstmt.setString(28, l.getUnloadingDock());
				pstmt.setInt(29, l.getRepackQty());
				
				pstmt.setDouble(30, l.getPackingWeightWithPart());
				pstmt.setDouble(31, l.getOpackingWeight());
				pstmt.setDouble(32, l.getOpackingWeightWithPart());
				pstmt.setInt(33, l.getOnoOfStack());
				pstmt.setInt(34, l.getOnoOfPackingInPallet());
				pstmt.setDouble(35, l.getOpalletHeight());
				pstmt.setDouble(36, l.getOpalletWidth());
				pstmt.setDouble(37, l.getOpalletLength());
				pstmt.setDouble(38, l.getOpalletWeight());
				pstmt.setLong(39, l.getFinalPartId());
				pstmt.setInt(40, l.getOpackingTypeId());
				pstmt.setString(41, l.getCustomerPartNo());
				pstmt.setLong(42, l.getCustomerId());
				pstmt.setInt(43, l.getpNoOfStack());
				pstmt.setInt(44, l.getoBinQty());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part Master added successfully";
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
		public ApiResult<PartMaster> modifyPartMaster(PartMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			if(CheckPartShortCode(l.getPartId(),l.getPartNo())) {
				result.isSuccess=false;
				result.message="Part Code Already Exists";
				return result;
			}
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(modifyQuery);
				pstmt.clearParameters();
				pstmt.setString(1, l.getPartNo());
				pstmt.setString(2, l.getPartDescription());
				pstmt.setInt(3, l.getPackingTypeId());
				pstmt.setInt(4, l.getSpq());
				pstmt.setInt(5, l.getNoOfStack());
				pstmt.setInt(6, l.getNoOfPackingInPallet());
				pstmt.setDouble(7, l.getPackingHeight());
				pstmt.setDouble(8, l.getPackingWidth());
				pstmt.setDouble(9, l.getPackingLength());
				pstmt.setDouble(10, l.getPackingWeight());
				pstmt.setDouble(11, l.getPalletHeight());
				pstmt.setDouble(12, l.getPalletWidth());
				pstmt.setDouble(13, l.getPalletLength());
				pstmt.setDouble(14, l.getPalletWeight());
				pstmt.setInt(15, l.getDispatchType());
				pstmt.setInt(16, l.getLoadingType());
				pstmt.setInt(17, l.getRePack());
				pstmt.setDouble(18, l.getM2());
				pstmt.setDouble(19, l.getM3());
				pstmt.setBoolean(20, l.isActive());
				pstmt.setInt(21,l.getUserId());
				pstmt.setInt(22,l.getUnloadDockId());
				pstmt.setInt(23, l.getBinQty());
				pstmt.setInt(24, l.getLocationId());
				pstmt.setBoolean(25, l.getReqInspection());
				pstmt.setInt(26, l.getDeliveryLocationId());
				pstmt.setString(27, l.getPlantName());
				pstmt.setString(28, l.getUnloadingDock());
				pstmt.setInt(29, l.getRepackQty());
				pstmt.setDouble(30, l.getPackingWeightWithPart());
				pstmt.setDouble(31, l.getOpackingWeight());
				pstmt.setDouble(32, l.getOpackingWeightWithPart());
				pstmt.setInt(33, l.getOnoOfStack());
				pstmt.setInt(34, l.getOnoOfPackingInPallet());
				pstmt.setDouble(35, l.getOpalletHeight());
				pstmt.setDouble(36, l.getOpalletWidth());
				pstmt.setDouble(37, l.getOpalletLength());
				pstmt.setDouble(38, l.getOpalletWeight());
//				pstmt.setLong(39, l.getFinalPartId());
				pstmt.setInt(39, l.getOpackingTypeId());
				pstmt.setString(40, l.getCustomerPartNo());
				pstmt.setLong(41, l.getCustomerId());
				pstmt.setInt(42, l.getpNoOfStack());
				pstmt.setInt(43, l.getoBinQty());
				pstmt.setLong(44,l.getPartId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part Master modified successfully";
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
		public ApiResult<PartMaster> getPartMaster(long partId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PartMaster> result=new ApiResult<PartMaster>();
			PartMaster l=new PartMaster();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(select);
				pstmt.setLong(1, partId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPartId(rs.getLong("partid"));
					l.setPartNo(rs.getString("partno"));
					l.setPartDescription(rs.getString("partdescription"));
					l.setPackingTypeId(rs.getInt("packingtypeid"));
					l.setSpq(rs.getInt("spq"));
					l.setNoOfStack(rs.getInt("noofstack"));
					l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
					l.setPackingHeight(rs.getDouble("packingheight"));
					l.setPackingWidth(rs.getDouble("packingwidth"));
					l.setPackingLength(rs.getDouble("packinglength"));
					l.setPackingWeight(rs.getDouble("packingweight"));
					l.setPalletHeight(rs.getDouble("palletheight"));
					l.setPalletWidth(rs.getDouble("palletwidth"));
					l.setPalletLength(rs.getDouble("palletlength"));
					l.setPalletWeight(rs.getDouble("palletweight"));
					l.setDispatchType(rs.getInt("dispatchtype"));
					l.setLoadingType(rs.getInt("loadingtype"));
					l.setRePack(rs.getInt("repack"));
					l.setM2(rs.getDouble("m2"));
					l.setM3(rs.getDouble("m3"));
					l.setActive(rs.getBoolean("isactive"));
					l.setUnloadDockId(rs.getInt("unloaddockid"));
					l.setBinQty(rs.getInt("binqty"));
					l.setLocationId(rs.getInt("locationid"));
					l.setReqInspection(rs.getBoolean("requestinspection"));
					l.setDeliveryLocationId(rs.getInt("deliverylocationid"));
					l.setPlantName(rs.getString("plantname"));
					l.setUnloadingDock(rs.getString("unloadingdock"));
					l.setRepackQty(rs.getInt("repackqty"));
					l.setPackingWeightWithPart(rs.getDouble("packingweightwithpart"));
					l.setOpackingWeight(rs.getDouble("opackingweight"));
					l.setOpackingWeightWithPart(rs.getDouble("opackingweightwithpart"));
					l.setOnoOfStack(rs.getInt("onoofstack"));
					l.setOnoOfPackingInPallet(rs.getInt("onoofpackinginpallet"));
					l.setOpalletHeight(rs.getDouble("opalletheight"));
					l.setOpalletWidth(rs.getDouble("opalletwidth"));
					l.setOpalletLength(rs.getDouble("opalletlength"));
					l.setOpalletWeight(rs.getDouble("opalletweight"));
					l.setFinalPartId(rs.getLong("finalpartid"));
					l.setOpackingTypeId(rs.getInt("opackingtypeid"));
					l.setCustomerPartNo(rs.getString("customerpartcode"));
					l.setCustomerId(rs.getLong("customerid"));
					l.setpNoOfStack(rs.getInt("pnoofstack"));
					l.setoBinQty(rs.getInt("obinqty"));
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
		

		private boolean CheckPartShortCode(long partId,String partShortcode){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(duplicateCheck);
				pstmt.setLong(1, partId);
				pstmt.setString(2, partShortcode);
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
		
		public ApiResult<PackingType> managePackingType(PackingType input){
			if(input.getPackingTypeId()==0) {
				return addPackingType(input);
			}else {
				return modifyPackingType(input);
			}
		}
		public ApiResult<PackingType> addPackingType(PackingType l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			if( CheckPackingShortCode(l.getPackingTypeId(),l.getPackingName())){
				result.isSuccess=false;
				result.message="Short Code Already Exists";
				return result;
			}
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertPackingQuery);
				pstmt.clearParameters();
				pstmt.setString(1,l.getPackingName());
				pstmt.setString(2,l.getPackingShortName());
				pstmt.setInt(3,l.getPackingHeight());
				pstmt.setInt(4,l.getPackingWidth());
				pstmt.setInt(5,l.getPackingLength());
				pstmt.setInt(6,l.getPackingWeight());
				pstmt.setInt(7,((l.getPackingWidth() * l.getPackingLength())/1000));
				pstmt.setInt(8,(l.getPackingWidth() * l.getPackingLength() *l.getPackingHeight())/1000);
				pstmt.setBoolean(9,l.isActive());
				pstmt.setInt(10,l.getUserId());
				pstmt.setString(11,l.getPackingCategory());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Packing Type added successfully";
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
		public ApiResult<PackingType> modifyPackingType(PackingType l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			if( CheckPackingShortCode(l.getPackingTypeId(),l.getPackingShortName())){
				result.isSuccess=false;
				result.message="Short Code Already Exists";
				return result;
			}
			try{
				
					conn=DatabaseUtil.getConnection();
					conn.setAutoCommit(false);
					pstmt=conn.prepareStatement(modifyPackingQuery);
					pstmt.setString(1,l.getPackingName());
					pstmt.setString(2,l.getPackingShortName());
					pstmt.setInt(3,l.getPackingHeight());
					pstmt.setInt(4,l.getPackingWidth());
					pstmt.setInt(5,l.getPackingLength());
					pstmt.setInt(6,l.getPackingWeight());
					pstmt.setInt(7,((l.getPackingWidth() * l.getPackingLength())/1000));
					pstmt.setInt(8,(l.getPackingWidth() * l.getPackingLength() *l.getPackingHeight())/1000);
					pstmt.setBoolean(9,l.isActive());
					pstmt.setInt(10,l.getUserId());
					pstmt.setString(11,l.getPackingCategory());
					pstmt.setInt(12,l.getPackingTypeId());
					pstmt.executeUpdate();
					conn.commit();
					result.isSuccess=true;
					result.message="Packing Type modified successfully";
				
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
		public ApiResult<PackingType> getPackingType(long packingTypeId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PackingType> result=new ApiResult<PackingType>();
			PackingType l=new PackingType();
			
			try{
				
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectPacking);
				pstmt.setLong(1, packingTypeId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPackingTypeId(rs.getInt("packingtypeid"));
					l.setPackingName(rs.getString("packingname"));
					l.setPackingShortName(rs.getString("packingshortname"));
					l.setPackingHeight(rs.getInt("packingheight"));
					l.setPackingWidth(rs.getInt("packingwidth"));
					l.setPackingLength(rs.getInt("packinglength"));
					l.setPackingWeight(rs.getInt("packingweight"));
					l.setM2(rs.getInt("m2"));
					l.setM3(rs.getInt("m3"));
					l.setActive(rs.getBoolean("isactive"));
					l.setPackingCategory(rs.getString("packingcategory"));
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
		private boolean CheckPackingShortCode(long packingTypeId,String packingTypeShortcode){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(duplicatePackingCheck);
				pstmt.setLong(1, packingTypeId);
				pstmt.setString(2, packingTypeShortcode);
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
		
		public ApiResult<PartKitMaster> managePartKitMaster(PartKitMaster input){
			if(input.getPartKitMasterId()==0) {
				return addPartKitMaster(input);
			}else {
				return modifyPartKitMaster(input);
			}
		}
		public ApiResult<PartKitMaster> addPartKitMaster(PartKitMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertKitQuery);
				pstmt.clearParameters();
				pstmt.setInt(1,l.getPartIdEndUser());
				pstmt.setInt(2,l.getPartIdCustomer());
				pstmt.setBoolean(3,l.isActive());
				pstmt.setInt(4,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part kit Master added successfully";
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
		
		public ApiResult<PartKitMaster> modifyPartKitMaster(PartKitMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(modifyKitQuery);
				pstmt.clearParameters();
				pstmt.setInt(1,l.getPartIdEndUser());
				pstmt.setInt(2,l.getPartIdCustomer());
				pstmt.setBoolean(3,l.isActive());
				pstmt.setInt(4,l.getUserId());
				pstmt.setLong(5,l.getPartKitMasterId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part kit Master modified successfully";
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
		public ApiResult<PartKitMaster> getPartKitMaster(long partKitMasterId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PartKitMaster> result=new ApiResult<PartKitMaster>();
			PartKitMaster l=new PartKitMaster();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectKit);
				pstmt.setLong(1, partKitMasterId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPartKitMasterId(rs.getInt("partkitmasterid"));
					l.setPartIdEndUser(rs.getInt("part_id_enduser"));
					l.setPartIdCustomer(rs.getInt("part_id_customer"));
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
		
		
		public ApiResult<PartAssembly> managePartAssemblyMaster(PartAssembly input){
			if(input.getPartassemblyId()==0) {
				return addPartAssembly(input);
			}else {
				return modifyPartAssembly(input);
			}
		}
		
		public ApiResult<PartAssembly> addPartAssembly(PartAssembly l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertAssemQuery);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getPrimaryPartid());
				pstmt.setString(2,l.getAssemblyPartname());
				pstmt.setString(3,l.getAssemblyPartcode());
				pstmt.setBoolean(4,l.getIsActive());
				pstmt.setInt(5,l.getUserId());
				pstmt.setInt(6,l.getQty());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part Assembly added successfully";
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
		
		public ApiResult<PartAssembly> modifyPartAssembly(PartAssembly l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(delAssemQuery);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getPartassemblyId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part Assembly deleted successfully";
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
		
		public ApiResult<PartAssembly> getPartAssembly(long partassemblyId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PartAssembly> result=new ApiResult<PartAssembly>();
			PartAssembly l=new PartAssembly();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectAssemQuery);
				pstmt.setLong(1, partassemblyId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPartassemblyId(partassemblyId);
					l.setPrimaryPartid(rs.getLong("primarypartid"));
					l.setAssemblyPartname(rs.getString("assemblypartname"));
					l.setAssemblyPartcode(rs.getString("assemblypartcode"));
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
		
		
		public ApiResult<PartDeliveryLocation> managePartDeliveryLocation(PartDeliveryLocation input){
			if(input.getPartDeliveryDockId()==0) {
				return addPartDeliveryLocation(input);
			}else {
				return modifyPartDeliveryLocation(input);
			}
		}
		public ApiResult<PartDeliveryLocation> addPartDeliveryLocation(PartDeliveryLocation l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertDeliveryLocation);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getPartId());
				pstmt.setInt(2,l.getdLocationId());
				pstmt.setInt(3,l.getPlantId());
				pstmt.setInt(4,l.getPlantDockId());
				pstmt.setBoolean(5,l.getIsActive());
				pstmt.setInt(6,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part delivery location added successfully";
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
		
		public ApiResult<PartDeliveryLocation> modifyPartDeliveryLocation(PartDeliveryLocation l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(delDeliveryLocation);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getPartDeliveryDockId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part delivery location deleted successfully";
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
		
		public static PartService getInstance(){		
			 return new PartService();
		}
	}