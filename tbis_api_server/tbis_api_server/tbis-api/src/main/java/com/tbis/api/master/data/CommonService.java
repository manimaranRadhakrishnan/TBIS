package com.tbis.api.master.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.master.model.CustomerAddress;
import com.tbis.api.master.model.CustomerContacts;
import com.tbis.api.master.model.CustomerContracts;
import com.tbis.api.master.model.CustomerDocuments;
import com.tbis.api.master.model.CustomerLineMap;
import com.tbis.api.master.model.CustomerManpowers;
import com.tbis.api.master.model.CustomerMaster;
import com.tbis.api.master.model.CustomerPartsLineMap;
import com.tbis.api.master.model.PackingType;
import com.tbis.api.master.model.CustomerSoftwares;
import com.tbis.api.master.model.EntityAddress;
import com.tbis.api.master.model.EntityContacts;
import com.tbis.api.master.model.EntityContracts;
import com.tbis.api.master.model.EntityDocuments;

public class CommonService {
		
		private static final String insertEntityDocQuery="INSERT INTO wmsdocuments(entitytypeid,entityid, documenttype, documentno, documentpath, validfrom, validto, isactive, created_by, created_date,tsid)VALUES(?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
		//private static final String modifyEntityDocQuery="UPDATE wmsdocuments SET  documenttype=?, documentno=?,documentpath=?, validfrom=?, validto=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE documentid=? and entitytypeid=1;";
		private static final String selectEntityDoc="select documentid,entityid, entitytypeid,documenttype, documentno, documentpath, validfrom, validto, isactive from wmsdocuments where documentid=?";
		private static final String deleteEntityDocQuery="update wmsdocuments set isactive=0 where documentid=?;";
		
		private static final String insertEntityContractQuery="INSERT INTO wmscontracts(entitytypeid,entityid, contractno, pono, startdate, enddate, isactive, created_by, created_date, tsid,customersignoff,tbissignoff,warehousespace,customerbillingcycle,creditlimit,contracttype) VALUES (?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?,?,?,?,?,?);"; 
		//private static final String modifyEntityContractQuery="UPDATE wmscontracts SET  contractno=?, pono=?, startdate=?, enddate=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()), customersignoff=?, tbissignoff=?, warehousespace=?, customerbillingcycle=?, creditlimit=?, contracttype=? WHERE contractid=?";
		private static final String selectEntityContract="select contractid ,entityid ,entitytypeid,contractno, pono, startdate, enddate, isactive,customersignoff,tbissignoff,warehousespace,customerbillingcycle,creditlimit,contracttype from wmscontracts where contractid = ? and entitytypeid=1";
		private static final String deleteEntityContractQuery="update wmscontracts set isactive=0 where contractid=?;";
		
		private static final String insertEntityAddressQuery="INSERT INTO wmsaddress(entitytypeid,entityid,address,city,district,state,gstin,lat,lang,pincode,phoneno,email,website,addresstype,isactive, created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
		//private static final String modifyEntityAddressQuery="UPDATE wmsaddress SET  address=?, city=?, district=?, state=?,gstin=?,lat=?,lang=?,pincode=?,phoneno=?,email=?,website=?,addresstype=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE addressid=? and entitytypeid=1;";
		private static final String selectEntityAddress="select addressid,entityid,entityTypeId,address,city,district,state,gstin,lat,lang,pincode,phoneno,email,website,addresstype,isactive from wmsaddress where addressid = ?";
		private static final String deleteEntityAddressQuery="update wmsaddress set isactive=0 where addressid=?;";

		private static final String insertEntityContactQuery="INSERT INTO wmscontacts(entitytypeid,entityid, contactpersonname, contactpersondesignation, contactemail, contactphone, contactmobile, isactive, created_by,ismail,issms,created_date,tsid,departmentname)VALUES(?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?);"; 
//		private static final String modifyContactQuery="UPDATE customer_contacts SET customerid=?, contactpersonname=?, contactpersondesignation=?, contactemail=?, contactphone=?, contactmobile=?, isactive?, updated_by=?,ismail=?,issms=? updated_date=now(), tsid=getcurrenttsid(now()),departmentname=? WHERE contactid=?;";
		private static final String selectEntityContact="SELECT entitytypeid,entityid, contactpersonname, contactpersondesignation, contactemail, contactphone, contactmobile, isactive,ismail,issms,departmentname FROM wmscontacts  where contactid=?";		
		private static final String deleteEntityContactQuery="update wmscontacts set isactive=0 where contactid=?;";

		
		/* Documents */
		public ApiResult<EntityDocuments> manageDocuments(EntityDocuments l){
			if(l.getDocumentId()==0) {
				return addDocuments(l);
			}else {
				return modifyDocuments(l);
			}
		}
		public ApiResult<EntityDocuments> addDocuments(EntityDocuments l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				String path= FileUtil.getFilePath("customer", "customerdocument",String.valueOf(l.getEntityId()));
				if(l.getEntityTypeId()== 2) {
					path= FileUtil.getFilePath("transporter", "transporterdocument",String.valueOf(l.getEntityId()));
				}else if (l.getEntityTypeId()== 3) {
					path= FileUtil.getFilePath("vehicle", "vehicledocument",String.valueOf(l.getEntityId()));
				}
				byte[] decodedBytes = Base64.decodeBase64(l.getDocument());			
				l.setDocumentPath(path+l.getDocumentName());
				FileUtil.writeToFile(decodedBytes,l.getDocumentPath());
				System.out.println(":::PATH::: " + path);
				pstmt=conn.prepareStatement(insertEntityDocQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getEntityTypeId());
				pstmt.setLong(2, l.getEntityId());
				pstmt.setString(3, l.getDocumentType());
				pstmt.setString(4, l.getDocumentNo());
				pstmt.setString(5, l.getDocumentPath());
				pstmt.setString(6, l.getValidFrom());
				pstmt.setString(7, l.getValidTo());
				pstmt.setBoolean(8, l.isActive());
				pstmt.setInt(9,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Documents added successfully";
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
		public ApiResult<EntityDocuments> modifyDocuments(EntityDocuments l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteEntityDocQuery);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getDocumentId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Document deleted successfully";
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
		public ApiResult<EntityDocuments> getDocuments(long DocumentsId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<EntityDocuments> result=new ApiResult<EntityDocuments>();
			EntityDocuments l=new EntityDocuments();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectEntityDoc);
				pstmt.setLong(1, DocumentsId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setDocumentId(rs.getLong("documentid"));
					l.setEntityId(rs.getInt("customerid"));
					l.setDocumentType(rs.getString("documenttype"));
					l.setDocumentNo(rs.getString("documentno"));
					l.setDocumentPath(FileUtil.readFileAsBase64(rs.getString("documentpath")));
					l.setValidFrom(rs.getString("validfrom"));
					l.setValidTo(rs.getString("validto"));
					l.setActive(rs.getBoolean("isactive"));
					l.setEntityTypeId(rs.getLong("entitytypeid"));
					
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
		
		/* Contracts */
		public ApiResult<EntityContracts> manageContracts(EntityContracts input){
			if(input.getContractId()==0) {
				return addContracts(input);
			}else {
				return modifyContracts(input);
			}
		}
		public ApiResult<EntityContracts> addContracts(EntityContracts l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertEntityContractQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getEntityTypeId());
				pstmt.setLong(2, l.getEntityId());
				pstmt.setString(3, l.getContractNo());
				pstmt.setString(4, l.getPoNo());
				pstmt.setString(5, l.getStartDate());
				pstmt.setString(6, l.getEndDate());
				pstmt.setBoolean(7,l.isActive());
				pstmt.setInt(8,l.getUserId());
				pstmt.setString(9,l.getCustomerSignOff());
				pstmt.setInt(10,l.getTbisSignOff());
				pstmt.setInt(11,l.getWarehouseSpace());
				pstmt.setInt(12,l.getBillingCycle());
				pstmt.setDouble(13,l.getCreditLimit());
				pstmt.setInt(14,l.getContractType());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Contract added successfully";
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
		public ApiResult<EntityContracts> modifyContracts(EntityContracts l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteEntityContractQuery);
				pstmt.clearParameters();
//				pstmt.setLong(1, l.getCustomerId());
//				pstmt.setString(2, l.getContractNo());
//				pstmt.setString(3, l.getPoNo());
//				pstmt.setString(4, l.getStartDate());
//				pstmt.setString(5, l.getEndDate());
//				pstmt.setBoolean(6,l.isActive());
//				pstmt.setInt(7,l.getUserId());
				pstmt.setLong(1, l.getContractId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Contract deleted";
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
		public ApiResult<EntityContracts> getContracts(long contractId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<EntityContracts> result=new ApiResult<EntityContracts>();
			EntityContracts l=new EntityContracts();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectEntityContract);
				pstmt.setLong(1, contractId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setContractId(rs.getInt("contractid"));
					l.setEntityId(rs.getInt("entityid"));
					l.setEntityTypeId(rs.getInt("entitytypeid"));
					l.setContractNo(rs.getString("contractno"));
					l.setPoNo(rs.getString("pono"));
					l.setStartDate(rs.getString("startdate"));
					l.setEndDate(rs.getString("enddate"));
					l.setActive(rs.getBoolean("isactive"));
					l.setCustomerSignOff(rs.getString("customersignoff"));
					l.setTbisSignOff(rs.getInt("tbissignoff"));
					l.setWarehouseSpace(rs.getInt("warehousespace"));
					l.setBillingCycle(rs.getInt("customerbillingcycle"));
					l.setCreditLimit(rs.getDouble("creditlimit"));
					l.setContractType(rs.getInt("contracttype"));
					
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
	
		/*Address */
		public ApiResult<EntityAddress> manageAddress(EntityAddress input){
			if(input.getAddressId()==0) {
				return addAddress(input);
			}else {
				return modifyAddress(input);
			}
		}
		public ApiResult<EntityAddress> addAddress(EntityAddress l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertEntityAddressQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getEntityTypeId());
				pstmt.setLong(2, l.getEntityId());
				pstmt.setString(3, l.getAddress());
				pstmt.setString(4, l.getCity());
				pstmt.setString(5, l.getDistrict());
				pstmt.setString(6, l.getState());
				pstmt.setString(7, l.getGstIn());
				pstmt.setDouble(8, l.getLat());
				pstmt.setDouble(9, l.getLang());
				pstmt.setString(10, l.getPincode());
				pstmt.setString(11, l.getPhoneNo());
				pstmt.setString(12, l.getEmail());
				pstmt.setString(13, l.getWebsite());
				pstmt.setString(14, l.getAddressType());
				pstmt.setBoolean(15,l.isActive());
				pstmt.setInt(16,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Customer Address added successfully";
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
		public ApiResult<EntityAddress> modifyAddress(EntityAddress l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteEntityAddressQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getAddressId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Address deleted";
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
		public ApiResult<EntityAddress> getAddress(long addressId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<EntityAddress> result=new ApiResult<EntityAddress>();
			EntityAddress l=new EntityAddress();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectEntityAddress);
				pstmt.setLong(1, addressId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					
					l.setAddressId(rs.getInt("addressid"));
					l.setEntityId(rs.getInt("entityid"));
					l.setEntityTypeId(rs.getInt("entitytypeid"));
					l.setAddress(rs.getString("address"));
					l.setCity(rs.getString("city"));
					l.setDistrict(rs.getString("district"));
					l.setState(rs.getString("state"));
					l.setGstIn(rs.getString("gstin"));
					l.setLat(rs.getDouble("lat"));
					l.setLang(rs.getDouble("lang"));
					l.setPincode(rs.getString("pincode"));
					l.setPhoneNo(rs.getString("phoneno"));
					l.setEmail(rs.getString("email"));
					l.setWebsite(rs.getString("website"));
					l.setAddressType(rs.getString("addresstype"));
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
		
		public ApiResult<EntityContacts> manageContact(EntityContacts input){
			if(input.getContactId()==0) {
				return addContact(input);
			}else {
				return modifyContact(input);
			}
		}
		public ApiResult<EntityContacts> addContact(EntityContacts l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertEntityContactQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getEntityTypeId());
				pstmt.setLong(2, l.getEntityId());
				pstmt.setString(3, l.getContactPersonName());
				pstmt.setString(4, l.getContactPersonDesignation());
				pstmt.setString(5, l.getContactEmail());
				pstmt.setString(6, l.getContactPhone());
				pstmt.setString(7,l.getContactMobile());
				pstmt.setBoolean(8,l.getIsActive());
				pstmt.setInt(9,l.getUserId());
				pstmt.setBoolean(10,l.getIsMail());
				pstmt.setBoolean(11,l.getIsSms());
				pstmt.setString(12, l.getDepartmentName());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Contact added successfully";
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
		public ApiResult<EntityContacts> modifyContact(EntityContacts l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteEntityContactQuery);
				pstmt.clearParameters();
//				pstmt.setLong(1, l.getCustomerId());
//				pstmt.setString(2, l.getContactPersonName());
//				pstmt.setString(3, l.getContactPersonDesignation());
//				pstmt.setString(4, l.getContactEmail());
//				pstmt.setString(5, l.getContactPhone());
//				pstmt.setString(6,l.getContactMobile());
//				pstmt.setBoolean(7,l.getIsActive());
//				pstmt.setInt(8,l.getUserId());
//				pstmt.setBoolean(9,l.getIsMail());
//				pstmt.setBoolean(10,l.getIsSms());
				pstmt.setLong(1, l.getContactId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Contact deleted successfully";
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
		public ApiResult<EntityContacts> getContacts(long contactId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<EntityContacts> result=new ApiResult<EntityContacts>();
			EntityContacts l=new EntityContacts();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectEntityContact);
				pstmt.setLong(1, contactId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setEntityTypeId(rs.getInt("entitytypeid"));
					l.setEntityId(rs.getInt("entityid"));
					l.setContactPersonName(rs.getString("contactpersonname"));
					l.setContactPersonDesignation(rs.getString("contactpersondesignation"));
					l.setContactEmail(rs.getString("contactemail"));
					l.setContactPhone(rs.getString("contactphone"));
					l.setContactMobile(rs.getString("contactmobile"));
					l.setIsActive(rs.getBoolean("isactive"));
					l.setIsMail(rs.getBoolean("ismail"));
					l.setIsSms(rs.getBoolean("issms"));
					l.setDepartmentName(rs.getString("departmentname"));
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

		
		public static CommonService getInstance(){		
			 return new CommonService();
		}
	}