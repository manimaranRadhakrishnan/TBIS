package com.tbis.api.master.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.master.model.CustomerAddress;
import com.tbis.api.master.model.CustomerContacts;
import com.tbis.api.master.model.CustomerContracts;
import com.tbis.api.master.model.CustomerDocuments;
import com.tbis.api.master.model.CustomerLineMap;
import com.tbis.api.master.model.CustomerLineRackMap;
import com.tbis.api.master.model.CustomerManpowers;
import com.tbis.api.master.model.CustomerMaster;
import com.tbis.api.master.model.CustomerPartsLineMap;
import com.tbis.api.master.model.PackingType;
import com.tbis.api.master.model.PartMaster;
import com.tbis.api.master.model.SpaceMaster;
import com.tbis.api.master.model.CustomerSoftwares;

public class CustomerService {
		private static final String insertQuery="INSERT INTO customer_master (customer_erp_code, customername, address, address2, city, district, state, phone, mobile, email, gstin, lat, lang, barcodeconfigid, bankaccountno, ifsccode, bankname, contractstartdate, contractenddate, primarysublocationid, kyc_verified, approveby, approveddate, isactive,	tatinmin,supplymaxrotation, locationid,userid,created_by,primarydockid, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
		private static final String modifyQuery="UPDATE customer_master SET customer_erp_code=?, customername=?, address=?, address2=?, city=?, district=?, state=?, phone=?, mobile=?, email=?, gstin=?, lat=?, lang=?, barcodeconfigid=?, bankaccountno=?, ifsccode=?, bankname=?, contractstartdate=?, contractenddate=?, primarysublocationid=?, kyc_verified=?, approveby=?, approveddate=?, isactive=?,tatinmin=?,supplymaxrotation=?,locationid=? ,updated_by=?,primarydockid=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE customerid=?;";
		private static final String select="select c.customerid,c.customer_erp_code, c.customername, c.address, c.address2, c.city, c.district, c.state, c.phone, c.mobile, c.email, c.gstin, c.lat, c.lang, c.barcodeconfigid, c.bankaccountno, c.ifsccode, c.bankname, c.contractstartdate, c.contractenddate, c.primarysublocationid, c.kyc_verified, c.approveby, c.approveddate, c.isactive,c.tatinmin,c.supplymaxrotation,c.locationid,c.primarydockid,u.user_id,u.profile_image from customer_master c inner join user_master u on u.user_id=c.userid WHERE customerid=?;";
		private static final String insertUsers="insert into user_master(user_name,user_mail,password,mobile_no,role_id,user_type,tsid,last_password_update_date,created_by,profile_image) values(?,?,SHA2(?,256),?,?,?,getcurrenttsid(now()),now(),?,?)";
		private static final String updateUsers="update user_master set profile_image=? where user_id=?";
		private static final String duplicateCheck="SELECT customer_erp_code, customername FROM customer_master WHERE customerid != ? and customer_erp_code= ?";
		private static final String duplicateEmail="SELECT email, customername FROM customer_master WHERE customerid != ? and email= ?";
		private static final String duplicateMobile="SELECT mobile, customername FROM customer_master WHERE customerid != ? and mobile= ?";
		
		private static final String insertContactQuery="INSERT INTO customer_contacts(customerid, contactpersonname, contactpersondesignation, contactemail, contactphone, contactmobile, isactive, created_by,ismail,issms,created_date,tsid)VALUES(?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
		private static final String modifyContactQuery="UPDATE customer_contacts SET customerid=?, contactpersonname=?, contactpersondesignation=?, contactemail=?, contactphone=?, contactmobile=?, isactive?, updated_by=?,ismail=?,issms=? updated_date=now(), tsid=getcurrenttsid(now()) WHERE contactid=?;";
		private static final String selectContact="SELECT customerid, contactpersonname, contactpersondesignation, contactemail, contactphone, contactmobile, isactive,ismail,issms FROM customer_contacts  where contactid=?";
		private static final String deleteContactQuery="delete from customer_contacts  where contactid=? ";
		
		private static final String selectSubLocationSpace="select s.spaceid,s.sublocationid,s.spacename,s.colorcode,s.linespaceid,s.lineno,s.columnno,s.lineusageid,s.customerid,s.partid ,cm.customername ,l.usagename ,pm.partdescription from spacemaster s left join customer_master cm on cm.customerId = s.customerid left join lineusagemast l on l.usageid = s.lineusageid left join part_master pm on pm.partid = s.partid WHERE s.sublocationid=? and s.customerid=? ";
		private static final String updateSpaceMaster="update spacemaster set  lineusageid=?,colorcode=?,partid=? where sublocationid=? and lineno=? and columnno=? ";
		private static final String insertPartSpaceConfig="insert into linespacepartconfig(partspacename, partid, sublocationid, fromlinespaceid, tolinespaceid, fromlineno, tolineno, fromcol, tocol, allocatedbins,  fifoorder, currentfifoorder,lineusageid,spaceoccupation,customerid)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		private static final String insertSpaceParts="insert into linespaceparts(linespacepartconfigid,partid)values(?,?)";
		private static final String insertFreeSapceParts="insert into linespaceparts(linespacepartconfigid,partid) select ?,partid from part_master where customerid=? ";
//		/* Customer Documents */
//		private static final String insertDocQuery="INSERT INTO customer_documents(customerid, documenttype, documentno, documentpath, validfrom, validto, isactive, created_by, created_date,tsid)VALUES(?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
//		private static final String modifyDocQuery="UPDATE customer_documents SET  customerid=?, documenttype=?, documentno=?,documentpath=?, validfrom=?, validto=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE documentid=?;";
//		private static final String selectDoc="select documentid,customerid, documenttype, documentno, documentpath, validfrom, validto, isactive from customer_documents where documentid=?;";
//		private static final String deleteDocQuery="delete from customer_documents where documentid=?;";
//		
//		private static final String insertEntityDocQuery="INSERT INTO wmsdocuments(entitytypeid,entityid, documenttype, documentno, documentpath, validfrom, validto, isactive, created_by, created_date,tsid)VALUES(1,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
//		private static final String modifyEntityDocQuery="UPDATE wmsdocuments SET  documenttype=?, documentno=?,documentpath=?, validfrom=?, validto=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE documentid=? and entitytypeid=1;";
//		private static final String selectEntityDoc="select documentid,entityid as customerid, documenttype, documentno, documentpath, validfrom, validto, isactive from wmsdocuments where documentid=? and entitytypeid=1;";
//		private static final String deleteEntityDocQuery="update wmsdocuments set isactive=0 where documentid=? and entitytypeid=1;";
//		
		
		/* Customer Contacts */
//		private static final String insertContactQuery="INSERT INTO customer_contacts(customerid, contactpersonname, contactpersondesignation, contactemail, contactphone, contactmobile, isactive, created_by,ismail,issms,created_date,tsid,departmentname)VALUES(?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?);"; 
//		private static final String modifyContactQuery="UPDATE customer_contacts SET customerid=?, contactpersonname=?, contactpersondesignation=?, contactemail=?, contactphone=?, contactmobile=?, isactive?, updated_by=?,ismail=?,issms=? updated_date=now(), tsid=getcurrenttsid(now()),departmentname=? WHERE contactid=?;";
//		private static final String selectContact="SELECT customerid, contactpersonname, contactpersondesignation, contactemail, contactphone, contactmobile, isactive,ismail,issms,departmentname FROM customer_contacts  where contactid=?";		
//		private static final String deleteContactQuery="delete from customer_contacts where contactid=?;";

		
//		/* Customer Contracts */
//		private static final String insertContractQuery="INSERT INTO customer_contracts(customerid, contractno, pono, startdate, enddate, isactive, created_by, created_date, tsid,customersignoff,tbissignoff,warehousespace,customerbillingcycle,creditlimit,contracttype) VALUES (?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?,?,?,?,?,?);"; 
//		private static final String modifyContractQuery="UPDATE customer_contracts SET customerid=?, contractno=?, pono=?, startdate=?, enddate=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()), customersignoff=?, tbissignoff=?, warehousespace=?, customerbillingcycle=?, creditlimit=?, contracttype=? WHERE contractid=?;";
//		private static final String selectContract="select contractid ,customerid, contractno, pono, startdate, enddate, isactive,customersignoff,tbissignoff,warehousespace,customerbillingcycle,creditlimit,contracttype from customer_contracts where contractid = ?";
//		private static final String deleteContractQuery="delete from customer_contracts where contractid=?;";
//		
//		private static final String insertEntityContractQuery="INSERT INTO wmscontracts(entitytypeid,entityid, contractno, pono, startdate, enddate, isactive, created_by, created_date, tsid,customersignoff,tbissignoff,warehousespace,customerbillingcycle,creditlimit,contracttype) VALUES (1,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?,?,?,?,?,?);"; 
//		private static final String modifyEntityContractQuery="UPDATE wmscontracts SET  contractno=?, pono=?, startdate=?, enddate=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()), customersignoff=?, tbissignoff=?, warehousespace=?, customerbillingcycle=?, creditlimit=?, contracttype=? WHERE contractid=? and entitytypeid=1;";
//		private static final String selectEntityContract="select contractid ,entityid as customerid, contractno, pono, startdate, enddate, isactive,customersignoff,tbissignoff,warehousespace,customerbillingcycle,creditlimit,contracttype from wmscontracts where contractid = ? and entitytypeid=1";
//		private static final String deleteEntityContractQuery="update from wmscontracts set isactive=0 where contractid=? and entitytypeid=1;";
//		
		
		
		/* Customer Parts */

		private static final String insertPartsQuery="INSERT INTO customer_parts_line_map ( customerid, partid, customerpartcode, cardconfigid, linespaceid, linelotid, linerackid, maxqty,isactive, created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,?,?,now(), getcurrenttsid(now()));"; 
		private static final String modifyPartsQuery="UPDATE customer_parts_line_map SET customerid=?, partid=?, customerpartcode=?, cardconfigid=?, linespaceid=?, linelotid=?, linerackid=?,  isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE customerpartid=?;";
		private static final String selectParts="select customerid, partid, customerpartcode, barcoderequired, linespaceid, linelotid, linerackid, isactive from customer_parts_line_map where customerpartid=?;";
		private static final String duplicatePartsCheck="SELECT partid FROM customer_parts_line_map where customerid=? and partid=?";
		private static final String deltePartCheck="SELECT d.partid FROM asndetail d inner join asnmaster m on m.asnid=d.asnid where customerid=? and partid=?  limit 1;";
		private static final String deletePartMap="delete from customer_parts_line_map where customerpartid=?;";
		private static final String getPartCodeQry="select partno from part_master where partid=?;";
		/* Customer LineMap */
		
		private static final String insertLineMapQuery="INSERT INTO customer_line_space_map(customerid, linespaceid,isactive, created_by, created_date, tsid,startcol,endcol) VALUES (?,?,?,?,now(), getcurrenttsid(now()),?,?);";
		private static final String modifyLineMapQuery="update customer_line_space_map set isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()),startcol=?,endcol=? where customerlinespaceid=?;";
		private static final String deleteLineMapQuery="delete from  customer_line_space_map  where customerlinespaceid= ?;";
		private static final String checkProductMaped="select linespaceid from customer_parts_line_map where linespaceid=? and customerid=?;";
		private static final String updateSpaceMasterQuery="update spacemaster set customerid=? where linespaceid=? and columnno>=? and columnno<=?";
		
		/* Customer LineRackMap */
		private static final String insertLineRackMapQuery="INSERT INTO customer_line_rack_map(customerid, linerackid,isactive, created_by, created_date, tsid) VALUES (?,?,?,?,now(), getcurrenttsid(now()));";
		private static final String deleteLineRackMapQuery="update customer_line_rack_map set isactive=0 where customerlinerackid=?;";
		
		/* Customer Softwares */
		private static final String insertSoftwareQuery="INSERT INTO customer_software(customerid, softwarename, softwareurl, softwareusername, softwarepassword, isactive, created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
		private static final String modifySoftwareQuery="UPDATE customer_software SET customerid=?, softwarename=?, softwareurl=?, softwareusername=?, softwarepassword=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE softwareid=?;";
		private static final String selectSoftware="select softwareid ,customerid, softwarename, softwareurl, softwareusername, softwarepassword, isactive from customer_software where softwareid = ?";
		private static final String deleteSoftwareQuery="delete from customer_software where softwareid=?;";
		
//		/* Customer Address */
//		private static final String insertCustomerAddressQuery="INSERT INTO customer_address(customerid,address,city,district,state,gstin,lat,lang,pincode,phoneno,email,website,addresstype,isactive, created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
//		private static final String modifyCustomerAddressQuery="UPDATE customer_address SET customerid=?, address=?, city=?, district=?, state=?,gstin=?,lat=?,lang=?,pincode=?,phoneno=?,email=?,website=?,addresstype=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE addressid=?;";
//		private static final String selectCustomerAddress="select addressid,customerid,address,city,district,state,gstin,lat,lang,pincode,phoneno,email,website,addresstype,isactive from customer_address where addressid = ?";
//		private static final String deleteCustomerAddressQuery="delete from customer_address where addressid=?;";
//		
//		private static final String insertEntityAddressQuery="INSERT INTO wmsaddress(entitytypeid,entityid,address,city,district,state,gstin,lat,lang,pincode,phoneno,email,website,addresstype,isactive, created_by, created_date, tsid) VALUES (1,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
//		private static final String modifyEntityAddressQuery="UPDATE wmsaddress SET  address=?, city=?, district=?, state=?,gstin=?,lat=?,lang=?,pincode=?,phoneno=?,email=?,website=?,addresstype=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE addressid=? and entitytypeid=1;";
//		private static final String selectEntityAddress="select addressid,entityid as customerid,entityTypeId,address,city,district,state,gstin,lat,lang,pincode,phoneno,email,website,addresstype,isactive from wmsaddress where addressid = ? and entitytypeid=1";
//		private static final String deleteEntityAddressQuery="update wmsaddress set isActive=0 where addressid=? and entitytypeid=1;";
//		
		
		/* Customer Manpowers */
		private static final String insertManpowerQuery="INSERT INTO customer_manpower(customerid, manpowerdetail,shifta,shiftb,totalhead,isactive, created_by, created_date, tsid,categoryid) VALUES (?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?);"; 
		private static final String modifyManpowerQuery="UPDATE customer_manpower SET customerid=?, manpowerdetail=?, shifta=?, shiftb=?, totalhead=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()) WHERE manpowerid=?;";
		private static final String selectManpower="select manpowerid,customerid,manpowerdetail,shifta,shiftb,totalhead,isactive,categoryid from customer_manpower where manpowerid = ?";
		private static final String deleteManpowerQuery="delete from customer_manpower where manpowerid=?;";
		
		/*Remove space allocation */
		private static final String removeSpaceAllocation="update spacemaster set lineusageid=13,partid=0,colorcode='#F8F4E1' where sublocationid=? and customerid=? and lineusageid=? ";
		private static final String deleteLineSpacePartMapping="delete from linespaceparts where linespacepartconfigid in (select linespacepartconfigid from linespacepartconfig where sublocationid=? and customerid=? and lineusageid=?) ";
		private static final String deleteLineSpaceMapping="delete from linespacepartconfig where sublocationid=? and customerid=? and lineusageid=? ";
		
		/*check space allocation*/
		private static final String checkCustomerSpaceAllocation="select spaceid,partid from spacemaster where sublocationid=? and customerid=? and (lineno>=? and lineno<=?) and (columnno>=? and columnno<=?) and ifnull(lineusageid,1)<>13 ";
		
		public ApiResult<CustomerMaster> manageCustomerMaster(CustomerMaster input){
			if(input.getCustomerId()==0) {
				return addCustomerMaster(input);
			}else {
				return modifyCustomerMaster(input);
			}
		}
		public ApiResult<CustomerMaster> addCustomerMaster(CustomerMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			if(CheckCustomerErpCode(l.getCustomerId(), l.getCustomerErpCode())) {
				result.isSuccess=false;
				result.message="Shorcode already Exist";
				return result;
			}
			if(CheckCustomerEmail(l.getCustomerId(), l.getEmail())) {
				result.isSuccess=false;
				result.message="Email  already Exist";
				return result;
			}
			if(CheckCustomerMobile(l.getCustomerId(), l.getMobile())) {
				result.isSuccess=false;
				result.message="Mobileno  already Exist";
				return result;
			}
			try{
				
			
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				
				int userId=0;
				pstmt=conn.prepareStatement(insertUsers,PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.clearParameters();
				pstmt.setString(1, l.getEmail());
				pstmt.setString(2,l.getEmail());
				pstmt.setString(3, "tbiscustomer");
				pstmt.setString(4, l.getMobile());
				pstmt.setInt(5, 3);
				pstmt.setString(6, "2");
				pstmt.setInt(7, l.getUserId());
				pstmt.setString(8, l.getProfileImage());
				pstmt.executeUpdate();
				rs=pstmt.getGeneratedKeys();
				if(rs.next()){
					userId=rs.getInt(1);
				}
				rs.close();
				pstmt.close();
				
				pstmt=conn.prepareStatement(insertQuery);
				pstmt.clearParameters();
				pstmt.setString(1, l.getCustomerErpCode());
				pstmt.setString(2, l.getCustomerName());
				pstmt.setString(3, l.getAddress());
				pstmt.setString(4, l.getAddress2());
				pstmt.setString(5, l.getCity());
				pstmt.setString(6, l.getDistrict());
				pstmt.setString(7, l.getState());
				pstmt.setString(8, l.getPhone());
				pstmt.setString(9, l.getMobile());
				pstmt.setString(10, l.getEmail());
				pstmt.setString(11, l.getGstIn());
				pstmt.setDouble(12, l.getLat());
				pstmt.setDouble(13, l.getLang());
				pstmt.setInt(14, l.getBarCodeConfigId());
				pstmt.setString(15, l.getBankAccountNo());
				pstmt.setString(16, l.getIfscCode());
				pstmt.setString(17, l.getBankName());
				if("".equals(l.getContractStartDate())) {
					pstmt.setString(18, null);					
				}else {
				pstmt.setString(18, l.getContractStartDate());
				}
				if("".equals( l.getContractEndDate())){
					pstmt.setString(19,null);
				}else {
				pstmt.setString(19, l.getContractEndDate());
				}
				pstmt.setInt(20, l.getPrimarySubLocationId());
				pstmt.setBoolean(21, l.isKycVerified());
				pstmt.setInt(22, l.getApproveBy());
				if("".equals(l.getApprovedDate())) {
					pstmt.setString(23, null);
				}else {
				pstmt.setString(23, l.getApprovedDate());
				}
				pstmt.setBoolean(24,l.isActive());
				pstmt.setInt(25,l.getTatinMin());
				pstmt.setInt(26,l.getSupplymaxRotation());
				pstmt.setInt(27,l.getLocationId());
				pstmt.setInt(28,userId);
				pstmt.setInt(29,l.getUserId());
				pstmt.setInt(30,l.getPrimaryDockId());
				
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Data added successfully";
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
		public ApiResult<CustomerMaster> modifyCustomerMaster(CustomerMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			
			if(CheckCustomerErpCode(l.getCustomerId(), l.getCustomerErpCode())) {
				result.isSuccess=false;
				result.message="Shorcode already Exist";
				return result;
			}
			if(CheckCustomerEmail(l.getCustomerId(), l.getEmail())) {
				result.isSuccess=false;
				result.message="Email  already Exist";
				return result;
			}
			if(CheckCustomerMobile(l.getCustomerId(), l.getMobile())) {
				result.isSuccess=false;
				result.message="Mobileno  already Exist";
				return result;
			}
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(modifyQuery);
				pstmt.clearParameters();
				pstmt.setString(1, l.getCustomerErpCode());
				pstmt.setString(2, l.getCustomerName());
				pstmt.setString(3, l.getAddress());
				pstmt.setString(4, l.getAddress2());
				pstmt.setString(5, l.getCity());
				pstmt.setString(6, l.getDistrict());
				pstmt.setString(7, l.getState());
				pstmt.setString(8, l.getPhone());
				pstmt.setString(9, l.getMobile());
				pstmt.setString(10, l.getEmail());
				pstmt.setString(11, l.getGstIn());
				pstmt.setDouble(12, l.getLat());
				pstmt.setDouble(13, l.getLang());
				pstmt.setInt(14, l.getBarCodeConfigId());
				pstmt.setString(15, l.getBankAccountNo());
				pstmt.setString(16, l.getIfscCode());
				pstmt.setString(17, l.getBankName());
				pstmt.setString(18, l.getContractStartDate());
				pstmt.setString(19, l.getContractEndDate());
				pstmt.setInt(20, l.getPrimarySubLocationId());
				pstmt.setBoolean(21, l.isKycVerified());
				pstmt.setInt(22, l.getApproveBy());
				pstmt.setString(23, l.getApprovedDate());
				pstmt.setBoolean(24,l.isActive());
				pstmt.setInt(25,l.getTatinMin());
				pstmt.setInt(26,l.getSupplymaxRotation());
				pstmt.setInt(27,l.getLocationId());
				pstmt.setInt(28,l.getUserId());
				pstmt.setInt(29,l.getPrimaryDockId());
				pstmt.setLong(30, l.getCustomerId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(updateUsers);
				pstmt.setString(1, l.getProfileImage());
				pstmt.setInt(2,l.getCustomerUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Data modified successfully";
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
		public ApiResult<CustomerMaster> getCustomerMaster(long customerId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<CustomerMaster> result=new ApiResult<CustomerMaster>();
			CustomerMaster l=new CustomerMaster();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(select);
				pstmt.setLong(1, customerId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setCustomerId(rs.getLong("customerid"));
					l.setCustomerErpCode(rs.getString("customer_erp_code"));
					l.setCustomerName(rs.getString("customername"));
					l.setAddress(rs.getString("address"));
					l.setAddress2(rs.getString("address2"));
					l.setCity(rs.getString("city"));
					l.setDistrict(rs.getString("district"));
					l.setState(rs.getString("state"));
					l.setPhone(rs.getString("phone"));
					l.setMobile(rs.getString("mobile"));
					l.setEmail(rs.getString("email"));
					l.setGstIn(rs.getString("gstin"));
					l.setLat(rs.getDouble("lat"));
					l.setLang(rs.getDouble("lang"));
					l.setBarCodeConfigId(rs.getInt("barcodeconfigid"));
					l.setBankAccountNo(rs.getString("bankaccountno"));
					l.setIfscCode(rs.getString("ifsccode"));
					l.setBankName(rs.getString("bankname"));
					l.setContractStartDate(rs.getString("contractstartdate"));
					l.setContractEndDate(rs.getString("contractenddate"));
					l.setPrimarySubLocationId(rs.getInt("primarysublocationid"));
					l.setKycVerified(rs.getBoolean("kyc_verified"));
					l.setApproveBy(rs.getInt("approveby"));
					l.setApprovedDate(rs.getString("approveddate"));
					l.setActive(rs.getBoolean("isactive"));
					l.setTatinMin(rs.getInt("tatinmin"));
					l.setSupplymaxRotation(rs.getInt("supplymaxrotation"));
					l.setCustomerUserId(rs.getInt("user_id"));
					l.setProfileImage(rs.getString("profile_image"));
					l.setLocationId(rs.getInt("locationid"));
					l.setPrimaryDockId(rs.getInt("primarydockid"));
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
		private boolean CheckCustomerErpCode(long customerId,String customerErpcode){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(duplicateCheck);
				pstmt.setLong(1, customerId);
				pstmt.setString(2, customerErpcode);
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
		private boolean CheckCustomerEmail(long customerId,String customerEmail){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(duplicateEmail);
				pstmt.setLong(1, customerId);
				pstmt.setString(2, customerEmail);
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
		private boolean CheckCustomerMobile(long customerId,String customerMobile){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(duplicateMobile);
				pstmt.setLong(1, customerId);
				pstmt.setString(2, customerMobile);
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
		
		
//		/* Customer Documents */
//		public ApiResult<CustomerDocuments> manageCustomerDocuments(CustomerDocuments l){
//			if(l.getDocumentId()==0) {
//				return addCustomerDocuments(l);
//			}else {
//				return modifyCustomerDocuments(l);
//			}
//		}
//		public ApiResult<CustomerDocuments> addCustomerDocuments(CustomerDocuments l){
//			Connection conn=null;
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			ApiResult result=new ApiResult();
//			String code="0";
//			try{
//				conn=DatabaseUtil.getConnection();
//				conn.setAutoCommit(false);
//
//				String path= FileUtil.getFilePath("customer", "customerdocument",String.valueOf(l.getCustomerId()));
//				byte[] decodedBytes = Base64.decodeBase64(l.getDocument());			
//				l.setDocumentPath(path+l.getDocumentName());
//				FileUtil.writeToFile(decodedBytes,l.getDocumentPath());
//				
//				pstmt=conn.prepareStatement(insertEntityDocQuery);
//				pstmt.clearParameters();
//				pstmt.setLong(1, l.getCustomerId());
//				pstmt.setString(2, l.getDocumentType());
//				pstmt.setString(3, l.getDocumentNo());
//				pstmt.setString(4, l.getDocumentPath());
//				pstmt.setString(5, l.getValidFrom());
//				pstmt.setString(6, l.getValidTo());
//				pstmt.setBoolean(7, l.isActive());
//				pstmt.setInt(8,l.getUserId());
//				pstmt.executeUpdate();
//				conn.commit();
//				result.isSuccess=true;
//				result.message="Documents added successfully";
//			}catch(Exception e){
//				try{
//					if(conn!=null){
//						conn.rollback();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}				
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
//		public ApiResult<CustomerDocuments> modifyCustomerDocuments(CustomerDocuments l){
//			Connection conn=null;
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			ApiResult result=new ApiResult();
//			try{
//				conn=DatabaseUtil.getConnection();
//				conn.setAutoCommit(false);
//				pstmt=conn.prepareStatement(deleteEntityDocQuery);
//				pstmt.clearParameters();
//				pstmt.setLong(1,l.getDocumentId());
//				pstmt.executeUpdate();
//				conn.commit();
//				result.isSuccess=true;
//				result.message="Document deleted successfully";
//			}catch(Exception e){
//				try{
//					if(conn!=null){
//						conn.rollback();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}				
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
//		public ApiResult<CustomerDocuments> getCustomerDocuments(long CustomerDocumentsId){
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			Connection conn=null;
//			ApiResult<CustomerDocuments> result=new ApiResult<CustomerDocuments>();
//			CustomerDocuments l=new CustomerDocuments();
//			try{
//				conn=DatabaseUtil.getConnection();
//				pstmt=conn.prepareStatement(selectEntityDoc);
//				pstmt.setLong(1, CustomerDocumentsId);
//				rs=pstmt.executeQuery();
//				if(rs.next()){
//					l.setDocumentId(rs.getLong("documentid"));
//					l.setCustomerId(rs.getInt("customerid"));
//					l.setDocumentType(rs.getString("documenttype"));
//					l.setDocumentNo(rs.getString("documentno"));
//					l.setDocumentPath(FileUtil.readFileAsBase64(rs.getString("documentpath")));
//					l.setValidFrom(rs.getString("validfrom"));
//					l.setValidTo(rs.getString("validto"));
//					l.setActive(rs.getBoolean("isactive"));
//					
//				}
//				result.result=l;
//			}catch(Exception e){
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//				result.result=null;
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}
//					if(rs!=null){
//						rs.close();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
		
		/* Customer Contacts */
		public ApiResult<CustomerContacts> manageCustomerContact(CustomerContacts input){
			if(input.getContactId()==0) {
				return addCustomerContact(input);
			}else {
				return modifyCustomerContact(input);
			}
		}
		public ApiResult<CustomerContacts> addCustomerContact(CustomerContacts l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertContactQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerId());
				pstmt.setString(2, l.getContactPersonName());
				pstmt.setString(3, l.getContactPersonDesignation());
				pstmt.setString(4, l.getContactEmail());
				pstmt.setString(5, l.getContactPhone());
				pstmt.setString(6,l.getContactMobile());
				pstmt.setBoolean(7,l.getIsActive());
				pstmt.setInt(8,l.getUserId());
				pstmt.setBoolean(9,l.getIsMail());
				pstmt.setBoolean(10,l.getIsSms());
//				pstmt.setString(11, "DEPT");
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
		public ApiResult<CustomerContacts> modifyCustomerContact(CustomerContacts l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteContactQuery);
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
		public ApiResult<CustomerContacts> getCustomerContacts(long contactId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<CustomerContacts> result=new ApiResult<CustomerContacts>();
			CustomerContacts l=new CustomerContacts();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectContact);
				pstmt.setLong(1, contactId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setCustomerId(rs.getInt("customerid"));
					l.setContactPersonName(rs.getString("contactpersonname"));
					l.setContactPersonDesignation(rs.getString("contactpersondesignation"));
					l.setContactEmail(rs.getString("contactemail"));
					l.setContactPhone(rs.getString("contactphone"));
					l.setContactMobile(rs.getString("contactmobile"));
					l.setIsActive(rs.getBoolean("isactive"));
					l.setIsMail(rs.getBoolean("ismail"));
					l.setIsSms(rs.getBoolean("issms"));
//					l.setDepartmentName("DEPT");
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

		/* Customer Contracts */
//		public ApiResult<CustomerContracts> manageCustomerContracts(CustomerContracts input){
//			if(input.getContractId()==0) {
//				return addCustomerContracts(input);
//			}else {
//				return modifyCustomerContracts(input);
//			}
//		}
//		public ApiResult<CustomerContracts> addCustomerContracts(CustomerContracts l){
//			Connection conn=null;
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			ApiResult result=new ApiResult();
//			String code="0";
//			try{
//				conn=DatabaseUtil.getConnection();
//				conn.setAutoCommit(false);
//				
//				pstmt=conn.prepareStatement(insertEntityContractQuery);
//				pstmt.clearParameters();
//				pstmt.setLong(1, l.getCustomerId());
//				pstmt.setString(2, l.getContractNo());
//				pstmt.setString(3, l.getPoNo());
//				pstmt.setString(4, l.getStartDate());
//				pstmt.setString(5, l.getEndDate());
//				pstmt.setBoolean(6,l.isActive());
//				pstmt.setInt(7,l.getUserId());
//				pstmt.setString(8,l.getCustomerSignOff());
//				pstmt.setInt(9,l.getTbisSignOff());
//				pstmt.setInt(10,l.getWarehouseSpace());
//				pstmt.setInt(11,l.getBillingCycle());
//				pstmt.setDouble(12,l.getCreditLimit());
//				pstmt.setInt(13,l.getContractType());
//				pstmt.executeUpdate();
//				conn.commit();
//				result.isSuccess=true;
//				result.message="Contract added successfully";
//			}catch(Exception e){
//				try{
//					if(conn!=null){
//						conn.rollback();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}				
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
//		public ApiResult<CustomerContracts> modifyCustomerContracts(CustomerContracts l){
//			Connection conn=null;
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			ApiResult result=new ApiResult();
//			try{
//				conn=DatabaseUtil.getConnection();
//				conn.setAutoCommit(false);
//				pstmt=conn.prepareStatement(deleteEntityContractQuery);
//				pstmt.clearParameters();
////				pstmt.setLong(1, l.getCustomerId());
////				pstmt.setString(2, l.getContractNo());
////				pstmt.setString(3, l.getPoNo());
////				pstmt.setString(4, l.getStartDate());
////				pstmt.setString(5, l.getEndDate());
////				pstmt.setBoolean(6,l.isActive());
////				pstmt.setInt(7,l.getUserId());
//				pstmt.setLong(1, l.getContractId());
//				pstmt.executeUpdate();
//				conn.commit();
//				result.isSuccess=true;
//				result.message="Contract deleted";
//			}catch(Exception e){
//				try{
//					if(conn!=null){
//						conn.rollback();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}				
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
//		public ApiResult<CustomerContracts> getCustomerContracts(long contractId){
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			Connection conn=null;
//			ApiResult<CustomerContracts> result=new ApiResult<CustomerContracts>();
//			CustomerContracts l=new CustomerContracts();
//			try{
//				conn=DatabaseUtil.getConnection();
//				pstmt=conn.prepareStatement(selectEntityContract);
//				pstmt.setLong(1, contractId);
//				rs=pstmt.executeQuery();
//				if(rs.next()){
//					l.setContractId(rs.getInt("contractid"));
//					l.setCustomerId(rs.getInt("customerid"));
//					l.setContractNo(rs.getString("contractno"));
//					l.setPoNo(rs.getString("pono"));
//					l.setStartDate(rs.getString("startdate"));
//					l.setEndDate(rs.getString("enddate"));
//					l.setActive(rs.getBoolean("isactive"));
//					l.setCustomerSignOff(rs.getString("customersignoff"));
//					l.setTbisSignOff(rs.getInt("tbissignoff"));
//					l.setWarehouseSpace(rs.getInt("warehousespace"));
//					l.setBillingCycle(rs.getInt("customerbillingcycle"));
//					l.setCreditLimit(rs.getDouble("creditlimit"));
//					l.setContractType(rs.getInt("contracttype"));
//					
//				}
//				result.result=l;
//			}catch(Exception e){
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//				result.result=null;
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}
//					if(rs!=null){
//						rs.close();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
			
		/* Customer LineMap */
		public ApiResult<CustomerLineMap> manageCustomerLine(CustomerLineMap input){
			if(input.getCustomerLineSpaceId()==0) {
				return addCustomerLine(input);
			}else {
				return modifyCustomerLine(input);
			}
		}
		public ApiResult<CustomerLineMap> addCustomerLine(CustomerLineMap l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertLineMapQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerId());
				pstmt.setLong(2, l.getLineSpaceId());
				pstmt.setBoolean(3,l.getIsActive());
				pstmt.setInt(4,l.getUserId());
				pstmt.setInt(5,l.getStartCol());
				pstmt.setInt(6,l.getEndCol());
				pstmt.executeUpdate();
				result.isSuccess=true;
				result.message="Space Mapped successfully";
				pstmt=conn.prepareStatement(updateSpaceMasterQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerId());
				pstmt.setLong(2, l.getLineSpaceId());
				pstmt.setInt(3,l.getStartCol());
				pstmt.setInt(4,l.getEndCol());
				pstmt.executeUpdate();
				conn.commit();
				
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
		public ApiResult<CustomerLineMap> modifyCustomerLine(CustomerLineMap l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			if(CheckLineMappedWithProduct(l.getLineSpaceId(),l.getCustomerId())) {
				result.isSuccess=false;
				result.message="Parts Mapped with this space";
				return result;
			}
			
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteLineMapQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerLineSpaceId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Space mapping deleted";
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
		
	/* Customer LineRackMap */
		public ApiResult<CustomerLineRackMap> manageCustomerLineRack(CustomerLineRackMap input){
			if(input.getCustomerLineRackId()==0) {
				return addCustomerLineRack(input);
			}else {
				return modifyCustomerLineRack(input);
			}
		}
		public ApiResult<CustomerLineRackMap> addCustomerLineRack(CustomerLineRackMap l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertLineRackMapQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerId());
				pstmt.setLong(2, l.getLineRackId());
				pstmt.setBoolean(3,l.getIsActive());
				pstmt.setInt(4,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Line Rack Mapped successfully";
				
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
		public ApiResult<CustomerLineRackMap> modifyCustomerLineRack(CustomerLineRackMap l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteLineRackMapQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerLineRackId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Line Rack mapping deleted";
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
		
		
		/* Customer Parts */
		public ApiResult<CustomerPartsLineMap> manageCustomerPartsMap(CustomerPartsLineMap l){
			if(l.getCustomerPartId()==0) {
				return addCustomerPartsMap(l);
			}else {
				return modifyCustomerPartsMap(l);
			}
		}
		public ApiResult<CustomerPartsLineMap> addCustomerPartsMap(CustomerPartsLineMap l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			if(l.getCardConfigId()==0) {
				l.setCardConfigId(1);
			}
			if("AUTO".equals(l.getCustomerPartCode())) {
				l.setCustomerPartCode(getPartCode(l.getPartId()));
			}
			if(CheckPartExists(l.getCustomerId(),l.getCustomerPartId())) {
				result.isSuccess=false;
				result.message="Part already mapped";
			}
			
			else {
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertPartsQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerId());
				pstmt.setLong(2, l.getPartId());
				pstmt.setString(3, l.getCustomerPartCode());
				pstmt.setInt(4, l.getCardConfigId());
				pstmt.setLong(5, l.getLineSpaceId());
				pstmt.setLong(6, l.getLineLotId());
				pstmt.setLong(7, l.getLineRackId());
				pstmt.setInt(8, l.getMaxQty());
				pstmt.setInt(9, l.getIsActive());
				pstmt.setInt(10,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part Map added successfully";
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
			}
			return result;
		}
		public ApiResult<CustomerPartsLineMap> modifyCustomerPartsMap(CustomerPartsLineMap l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			if(CheckPartTransact(l.getCustomerId(),l.getCustomerPartId())) {
				result.isSuccess=false;
				result.message="Can't delete already transacted Part ";
			} else {
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deletePartMap);
				pstmt.clearParameters();
//				pstmt.setLong(1, l.getCustomerId());
//				pstmt.setLong(2, l.getPartId());
//				pstmt.setString(3, l.getCustomerPartCode());
//				pstmt.setInt(4, l.getCardConfigId());
//				pstmt.setLong(5, l.getLineSpaceId());
//				pstmt.setLong(6, l.getLineLotId());
//				pstmt.setLong(7, l.getLineRackId());
//				pstmt.setInt(8, l.getIsActive());
//				pstmt.setInt(9,l.getUserId());
				pstmt.setLong(1,l.getCustomerPartId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Part removed ";
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
			}
			return result;
		}
		public ApiResult<CustomerPartsLineMap> getCustomerPartsMap(long customerPartId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<CustomerPartsLineMap> result=new ApiResult<CustomerPartsLineMap>();
			CustomerPartsLineMap l=new CustomerPartsLineMap();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectParts);
				pstmt.setLong(1, customerPartId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setCustomerId(rs.getLong("customerid"));
					l.setPartId(rs.getLong("partid"));
					l.setCustomerPartCode(rs.getString("customerpartcode"));
					l.setLineSpaceId(rs.getLong("linespaceid"));
					l.setLineLotId(rs.getLong("linelotid"));
					l.setLineRackId(rs.getLong("linerackid"));
					l.setIsActive(rs.getInt("isactive"));
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
		private boolean CheckPartExists(long customerId,long partId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(duplicatePartsCheck);
				pstmt.setLong(1, customerId);
				pstmt.setLong(2, partId);
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
		private boolean CheckPartTransact(long customerId,long partId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(deltePartCheck);
				pstmt.setLong(1, customerId);
				pstmt.setLong(2, partId);
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
		
		private boolean CheckLineMappedWithProduct(long lineMapId,long customerId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			boolean result=false;
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(checkProductMaped);
				pstmt.setLong(1, lineMapId);
				pstmt.setLong(1, customerId);
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
		
		private String getPartCode(long partId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			String result="NOTMAPPED";
			PackingType l=new PackingType();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(getPartCodeQry);
				pstmt.setLong(1, partId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					result= rs.getString("partno");
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
		
		/* Customer Software */
		public ApiResult<CustomerSoftwares> manageCustomerSoftwares(CustomerSoftwares input){
			if(input.getSoftwareId()==0) {
				return addCustomerSoftwares(input);
			}else {
				return modifyCustomerSoftwares(input);
			}
		}
		public ApiResult<CustomerSoftwares> addCustomerSoftwares(CustomerSoftwares l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				
				pstmt=conn.prepareStatement(insertSoftwareQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerId());
				pstmt.setString(2, l.getSoftwareName());
				pstmt.setString(3, l.getSoftwareUrl());
				pstmt.setString(4, l.getSoftwareUserName());
				pstmt.setString(5, l.getSoftwarePassword());
				pstmt.setBoolean(6,l.isActive());
				pstmt.setInt(7,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Customer Software added successfully";
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
		public ApiResult<CustomerSoftwares> modifyCustomerSoftwares(CustomerSoftwares l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteSoftwareQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getSoftwareId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Customer Software deleted";
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
		public ApiResult<CustomerSoftwares> getCustomerSoftwares(long softwareId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<CustomerSoftwares> result=new ApiResult<CustomerSoftwares>();
			CustomerSoftwares l=new CustomerSoftwares();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectSoftware);
				pstmt.setLong(1, softwareId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setSoftwareId(rs.getInt("softwareid"));
					l.setCustomerId(rs.getInt("customerid"));
					l.setSoftwareName(rs.getString("softwarename"));
					l.setSoftwareUrl(rs.getString("softwareurl"));
					l.setSoftwareUserName(rs.getString("softwareusername"));
					l.setSoftwarePassword(rs.getString("softwarepassword"));
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
		
		/* Customer Address */
//		public ApiResult<CustomerAddress> manageCustomerAddress(CustomerAddress input){
//			if(input.getAddressId()==0) {
//				return addCustomerAddress(input);
//			}else {
//				return modifyCustomerAddress(input);
//			}
//		}
//		public ApiResult<CustomerAddress> addCustomerAddress(CustomerAddress l){
//			Connection conn=null;
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			ApiResult result=new ApiResult();
//			String code="0";
//			try{
//				conn=DatabaseUtil.getConnection();
//				conn.setAutoCommit(false);
//				pstmt=conn.prepareStatement(insertEntityAddressQuery);
//				pstmt.clearParameters();
//				pstmt.setLong(1, l.getCustomerId());
//				pstmt.setString(2, l.getAddress());
//				pstmt.setString(3, l.getCity());
//				pstmt.setString(4, l.getDistrict());
//				pstmt.setString(5, l.getState());
//				pstmt.setString(6, l.getGstIn());
//				pstmt.setDouble(7, l.getLat());
//				pstmt.setDouble(8, l.getLang());
//				pstmt.setString(9, l.getPincode());
//				pstmt.setString(10, l.getPhoneNo());
//				pstmt.setString(11, l.getEmail());
//				pstmt.setString(12, l.getWebsite());
//				pstmt.setString(13, l.getAddressType());
//				pstmt.setBoolean(14,l.isActive());
//				pstmt.setInt(15,l.getUserId());
//				pstmt.executeUpdate();
//				conn.commit();
//				result.isSuccess=true;
//				result.message="Customer Address added successfully";
//			}catch(Exception e){
//				try{
//					if(conn!=null){
//						conn.rollback();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}				
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
//		public ApiResult<CustomerAddress> modifyCustomerAddress(CustomerAddress l){
//			Connection conn=null;
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			ApiResult result=new ApiResult();
//			try{
//				conn=DatabaseUtil.getConnection();
//				conn.setAutoCommit(false);
//				pstmt=conn.prepareStatement(deleteEntityAddressQuery);
//				pstmt.clearParameters();
//				pstmt.setLong(1, l.getAddressId());
//				pstmt.executeUpdate();
//				conn.commit();
//				result.isSuccess=true;
//				result.message="Customer Address deleted";
//			}catch(Exception e){
//				try{
//					if(conn!=null){
//						conn.rollback();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}				
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
//		public ApiResult<CustomerAddress> getCustomerAddress(long addressId){
//			PreparedStatement pstmt=null;
//			ResultSet rs=null;
//			Connection conn=null;
//			ApiResult<CustomerAddress> result=new ApiResult<CustomerAddress>();
//			CustomerAddress l=new CustomerAddress();
//			try{
//				conn=DatabaseUtil.getConnection();
//				pstmt=conn.prepareStatement(selectEntityAddress);
//				pstmt.setLong(1, addressId);
//				rs=pstmt.executeQuery();
//				if(rs.next()){
//					
//					l.setAddressId(rs.getInt("addressid"));
//					l.setCustomerId(rs.getInt("customerid"));
//					l.setAddress(rs.getString("address"));
//					l.setCity(rs.getString("city"));
//					l.setDistrict(rs.getString("district"));
//					l.setState(rs.getString("state"));
//					l.setGstIn(rs.getString("gstin"));
//					l.setLat(rs.getDouble("lat"));
//					l.setLang(rs.getDouble("lang"));
//					l.setPincode(rs.getString("pincode"));
//					l.setPhoneNo(rs.getString("phoneno"));
//					l.setEmail(rs.getString("email"));
//					l.setWebsite(rs.getString("website"));
//					l.setAddressType(rs.getString("addresstype"));
//					l.setActive(rs.getBoolean("isactive"));
//					
//				}
//				result.result=l;
//			}catch(Exception e){
//				e.printStackTrace();
//				result.isSuccess=false;
//				result.message=e.getMessage();
//				result.result=null;
//			}finally{
//				try{
//					if(conn!=null){
//						conn.close();
//					}
//					if(pstmt!=null){
//						pstmt.close();
//					}
//					if(rs!=null){
//						rs.close();
//					}
//				}catch(SQLException esql){
//					esql.printStackTrace();
//				}
//			}
//			return result;
//		}
		
		/* Customer Manpower */
		public ApiResult<CustomerManpowers> manageCustomerManpowers(CustomerManpowers input){
			if(input.getManpowerId()==0) {
				return addCustomerManpowers(input);
			}else {
				return modifyCustomerManpowers(input);
			}
		}
		public ApiResult<CustomerManpowers> addCustomerManpowers(CustomerManpowers l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				
				pstmt=conn.prepareStatement(insertManpowerQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getCustomerId());
				pstmt.setString(2, l.getManpowerDetail());
				pstmt.setInt(3, l.getShiftA());
				pstmt.setInt(4, l.getShiftB());
				pstmt.setInt(5, l.getShiftA() + l.getShiftB());
				pstmt.setBoolean(6,l.isActive());
				pstmt.setInt(7,l.getUserId());
				pstmt.setInt(8, l.getCategoryId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Customer Manpower added successfully";
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
		public ApiResult<CustomerManpowers> modifyCustomerManpowers(CustomerManpowers l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteManpowerQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getManpowerId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Customer Manpower deleted";
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
		public ApiResult<CustomerManpowers> getCustomerManpowers(long manpowerId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<CustomerManpowers> result=new ApiResult<CustomerManpowers>();
			CustomerManpowers l=new CustomerManpowers();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectManpower);
				pstmt.setLong(1, manpowerId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setManpowerId(rs.getInt("manpowerid"));
					l.setCustomerId(rs.getInt("customerid"));
					l.setManpowerDetail(rs.getString("manpowerdetail"));
					l.setShiftA(rs.getInt("shifta"));
					l.setShiftB(rs.getInt("shiftb"));
					l.setActive(rs.getBoolean("isactive"));
					l.setCategoryId(rs.getInt("categoryid"));
					
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
		public ArrayList<SpaceMaster> getCustomerSpace(long customerId,long primarySubLocationId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ArrayList<SpaceMaster> result = new ArrayList<SpaceMaster>();
			SpaceMaster l = new SpaceMaster();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectSubLocationSpace);
				pstmt.setLong(1, primarySubLocationId);
				pstmt.setLong(2, customerId);
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
		public ApiResult<SpaceMaster> updateSpaceAllocation(SpaceMaster input){
			Connection conn=null;
			PreparedStatement pstmt=null;
			PreparedStatement lstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(updateSpaceMaster);
				lstmt=conn.prepareStatement(insertPartSpaceConfig,PreparedStatement.RETURN_GENERATED_KEYS);
				for(int i=input.getStartRow();i<=input.getEndRow();i++){
			        for(int j=input.getStartCell();j<=input.getEndCell();j++){
						pstmt.clearParameters();
						pstmt.setInt(1, input.getLineUsageId());
						pstmt.setString(2, input.getColorCode());
						pstmt.setLong(3, input.getPartId());
						pstmt.setInt(4, input.getSubLocationId());
						pstmt.setInt(5, i);
						pstmt.setInt(6, j);
						pstmt.addBatch();		          
			        }
			     }
				pstmt.executeBatch();
				pstmt.close();
				if(input.getLineUsageId()==8 || input.getLineUsageId()==7) {
					//partspacename, partid, sublocationid, 
					//fromlinespaceid, tolinespaceid, fromlineno, 
					//tolineno, fromcol, tocol, allocatedbins,  
					//fifoorder, currentfifoorder
					long lineSpaceConfigId=0;
					lstmt.clearParameters();
					lstmt.setString(1, input.getPartSpaceName());
					lstmt.setLong(2, 0);
					lstmt.setInt(3, input.getSubLocationId());
					lstmt.setInt(4, input.getFromLineSpaceId());
					lstmt.setInt(5, input.getToLineSpaceId());
					lstmt.setInt(6, input.getStartRow());
					lstmt.setInt(7, input.getEndRow());
					lstmt.setInt(8, input.getStartCell());
					lstmt.setInt(9, input.getEndCell());
					lstmt.setInt(10, input.getMaxBins());
					lstmt.setInt(11, input.getFifoOrder());
					lstmt.setInt(12, input.getFifoOrder());
					lstmt.setInt(13, input.getLineUsageId());
					lstmt.setInt(14, input.getSpaceOccupation());
					lstmt.setLong(15, input.getCustomerid());
					lstmt.executeUpdate();
					rs=lstmt.getGeneratedKeys();
					if(rs.next()) {
						lineSpaceConfigId=rs.getLong(1);
					}
					rs.close();
					lstmt.close();
					rs=null;
					if(input.getSpaceOccupation()==3) {
						pstmt=conn.prepareStatement(insertFreeSapceParts);
						if(lineSpaceConfigId>0) {
							pstmt.clearParameters();
							pstmt.setLong(1, lineSpaceConfigId);
							pstmt.setLong(2,input.getCustomerid());
							pstmt.executeUpdate();													
						}
					}else {
						pstmt=conn.prepareStatement(insertSpaceParts);
						if(lineSpaceConfigId>0) {
							for(PartMaster p :input.getParts()) {
								pstmt.clearParameters();
								pstmt.setLong(1, lineSpaceConfigId);
								pstmt.setLong(2,p.getPartId());
								pstmt.executeUpdate();
							}						
						}
					}
				}
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
					if(lstmt!=null){
						lstmt.close();
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
			PreparedStatement lstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(deleteLineSpacePartMapping);
				pstmt.clearParameters();
				pstmt.setInt(1, input.getSubLocationId());
				pstmt.setLong(2, input.getCustomerid());
				pstmt.setLong(3, input.getLineUsageId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(deleteLineSpaceMapping);
				pstmt.clearParameters();
				pstmt.setInt(1, input.getSubLocationId());
				pstmt.setLong(2, input.getCustomerid());
				pstmt.setLong(3, input.getLineUsageId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(removeSpaceAllocation);
				pstmt.clearParameters();
				pstmt.setInt(1, input.getSubLocationId());
				pstmt.setLong(2, input.getCustomerid());
				pstmt.setLong(3, input.getLineUsageId());
				pstmt.executeUpdate();
				pstmt.close();
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
					if(lstmt!=null){
						lstmt.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<SpaceMaster> checkCustomerSpaceAllocation(SpaceMaster input){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(checkCustomerSpaceAllocation);
				pstmt.clearParameters();
				pstmt.setInt(1, input.getSubLocationId());
				pstmt.setLong(2, input.getCustomerid());
				pstmt.setInt(3, input.getStartRow());
				pstmt.setInt(4, input.getEndRow());
				pstmt.setInt(5, input.getStartCell());
				pstmt.setInt(6,input.getEndCell());
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

		public static CustomerService getInstance(){		
			 return new CustomerService();
		}
	}