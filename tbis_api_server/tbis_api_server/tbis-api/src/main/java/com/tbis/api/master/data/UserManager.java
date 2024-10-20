package com.tbis.api.master.data;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cissol.core.model.ApiResult;
import com.cissol.core.model.User;
import com.cissol.core.util.DatabaseUtil;
import com.itextpdf.xmp.impl.Base64;
import com.tbis.api.common.util.MailSendUtil;
import com.tbis.api.master.model.EmailInput;
import com.tbis.api.master.model.Employee;
import com.tbis.api.master.model.Role;
import com.tbis.api.master.model.RoleMenu;
import com.tbis.api.master.model.RoleOperation;
import com.tbis.api.master.model.UnloadDock;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class UserManager {
	private static final String insertQuery = "INSERT INTO employee_master(em_code,em_name,em_address1,em_address2,em_city,em_email,em_mobile,em_phone,em_user_name,em_type,em_status,userid,tsid)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,getcurrenttsid(now()))";
	private static final String modifyQuery = "UPDATE employee_master set em_name=?,em_address1=?,em_address2=?,em_city=?,em_email=?,em_mobile=?,em_phone=?,em_type=?,em_status=?,tsid=getcurrenttsid(now()) where em_code=?";
	private static final String insertUsers = "insert into user_master(user_name,user_mail,password,mobile_no,role_id,user_type,tsid,last_password_update_date,created_by,profile_image) values(?,?,SHA2(?,256),?,?,?,getcurrenttsid(now()),now(),?,?)";
	private static final String updateUsers = "update user_master set role_id=?,user_mail=?,mobile_no=?,tsid=getcurrenttsid(now()),update_by=?,update_time=now(),profile_image=? where user_id=? ";
	private static final String select = "SELECT a.em_code,a.em_name,a.em_address1,a.em_address2,a.em_city,a.em_email,a.em_mobile,a.em_phone,a.em_user_name,a.em_type,a.em_status,u.user_name,u.password,u.role_id,r.role_name,a.userid,u.profile_image FROM employee_master a inner join user_master u on a.userid=u.user_id inner join roles_master r on u.role_id=r.role_id where a.em_code=?";
	private static final String updateEmployeeKey = "update tran_seq_no set seq_no=seq_no+1 where tran_id=100";
	private static final String selectEmployeeKey = "select seq_no from tran_seq_no where tran_id=100";
	private static final String insertDefaultBranch = "insert into user_default_branch(udb_userid,udb_branchcode) values(?,?)";
	private static final String updateDefaultBranch = "update user_default_branch set udb_branchcode=? where udb_userid=? ";
	private static final String insertUserBranches = "insert into user_branch_map(ubm_userid,ubm_branchcode,ubm_location) values(?,?,?)";
	private static final String deleteUserBranches = "delete from user_branch_map where ubm_userid=? ";
	private static final String updateProfileImage = "update user_master set tsid=getcurrenttsid(now()),profile_image=? where user_id=? ";
	private static final String updateUserPassword = "update user_master set password=SHA2(?,256),tsid=getcurrenttsid(now()) where user_id=? ";
	private static final String selectUserBranch = "select b.branchcode,b.branchname,ifnull(a.ubm_branchcode,0) selected,ifnull(lm.lm_code,dl.lm_code) locationcode,ifnull(lm.lm_name,dl.lm_name) locationname from branch_master b inner join location_master dl on dl.branchcode=b.branchcode and dl.lm_default=1 left join user_branch_map a on b.branchcode=a.ubm_branchcode and a.ubm_userid=? left join location_master lm on lm.branchcode=a.ubm_branchcode and lm.lm_code=a.ubm_location ";
	private static final String insertRoleMaster = "insert into roles_master(role_name,role_description,parentid,created_by,created_time,tsid) values(?,?,?,?,now(),getcurrenttsid(now())) ";
	private static final String updateRoleMaster = "update roles_master set role_name=?,role_description=?,parentid=?,update_by=?,update_time=now() where role_id=? ";
	private static final String selectRoleMaster = "select a.role_name,a.role_description,a.role_id,b.role_name parentname,b.role_id parentid from roles_master a inner join roles_master b on a.parentid=b.role_id where a.role_id=? ";
	private static final String selectRoleMenus = "select a.menu_id,a.menu_name,menu_name menu_caption,a.menu_parent_id parent_id,ifnull(c.menu_id,0) selected from role_menu_map b  inner join roles_master cm on b.role_id=cm.parentid and cm.role_id=? inner join wms_menus a on a.menu_id=b.menu_id left join role_menu_map c on c.role_id=cm.role_id and a.menu_id=c.menu_id where a.menu_parent_id!=-1 order by a.menu_parent_id asc,a.menu_id asc";
	private static final String selectParentRoleMenus = "select a.menu_id,a.menu_name,menu_name menu_caption,a.menu_parent_id parent_id,0 selected from role_menu_map b  inner join wms_menus a on a.menu_id=b.menu_id where b.role_id=? and a.menu_parent_id!=-1 order by a.menu_parent_id asc,a.menu_id asc";
	private static final String selectParentRoleOperations = "select a.menu_id so_menu_id,a.menu_action_id so_id,a.action_name so_name,a.action_caption so_caption,a.report_column so_report_column, 0 selected,m.menu_report_id from role_operation_map b  inner join wms_menu_actions a on a.menu_id=b.rom_menu_id and a.menu_action_id=b.rom_so_id inner join wms_menus m on a.menu_id=m.menu_id where b.rom_role_id=? order by a.menu_id asc";
	private static final String selectRoleOperations = "select a.menu_id so_menu_id,a.menu_action_id so_id,a.action_name so_name,a.action_caption so_caption,a.report_column so_report_column, ifnull(c.rom_menu_id,0) selected,m.menu_report_id from role_operation_map b  inner join roles_master cm on b.rom_role_id=cm.parentid and cm.role_id=? inner join wms_menu_actions a on a.menu_id=b.rom_menu_id and a.menu_action_id=b.rom_so_id  inner join wms_menus m on a.menu_id=m.menu_id left join role_operation_map c on c.rom_menu_id=b.rom_menu_id and c.rom_so_id=b.rom_so_id and cm.role_id=c.rom_role_id order by a.menu_id asc";
	private static final String insertRoleMenus = "insert into role_menu_map(role_id,menu_id) values(?,?)";
	private static final String deleteRoleMenus = "delete from role_menu_map where role_id=?";
	private static final String deleteEmployeeProcess = "delete from employee_process_map where epm_emp_code=?";
	private static final String insertEmployeeProcess = "insert into employee_process_map(epm_process_id,epm_emp_code,epm_status) values(?,?,?)";
	private static final String selectEmployeeProcessMap = "select a.srp_code,a.srp_status_caption,b.pg_name,ifnull(e.epm_process_id,0) selected  from service_routing_process a inner join product_group b on a.srp_pg_code=b.pg_code left join employee_process_map e on a.srp_code=e.epm_process_id and e.epm_emp_code=? ";
	private static final String insertRoleOperations = "insert into role_operation_map(rom_role_id,rom_menu_id,rom_so_id) values(?,?,?)";
	private static final String deleteRoleOperations = "delete from role_operation_map where rom_role_id=?";
	private static final String insertRoleColumns = "insert into role_column_map(rcm_role_id,rcm_menu_id,rcm_report_id,rcm_column_name) values(?,?,?,?)";
	private static final String deleteRoleColumns = "delete from role_column_map where rcm_role_id=?";
	private static final String selectParentRoleColumns = "select b.rcm_menu_id,b.rcm_report_id,b.rcm_column_name,r.column_title,0 selected from role_column_map b inner join report_dtl r on b.rcm_report_id=r.report_id and b.rcm_column_name=r.column_name where b.rcm_role_id=? and r.is_operation=0 and b.rcm_menu_id!=0 order by r.column_order asc";
	private static final String selectRoleColumns = "select b.rcm_menu_id,b.rcm_report_id,b.rcm_column_name,r.column_title,ifnull(c.rcm_report_id,0) selected from role_column_map b inner join wms_menus menu on menu.menu_id=b.rcm_menu_id inner join report_dtl r on b.rcm_report_id=r.report_id and b.rcm_column_name=r.column_name inner join roles_master cm on b.rcm_role_id=cm.parentid and cm.role_id=? left join role_column_map c on c.rcm_menu_id=b.rcm_menu_id and c.rcm_report_id=b.rcm_report_id and cm.role_id=c.rcm_role_id and b.rcm_column_name=c.rcm_column_name where r.is_operation=0 and b.rcm_menu_id!=0 order by r.column_order asc ";
	private static final String insertRoleReportColumns = "insert into role_column_map(rcm_role_id,rcm_menu_id,rcm_report_id,rcm_column_name) select m.role_id ,0, d.report_id,column_name from report_dtl d inner join multi_menu_report_map b on d.report_id=b.report_id inner join role_menu_map m on b.menu_id=m.menu_id where m.role_id=?";
	private static final String checkUserCode = "select * from employee_master where ifnull(em_code,0)=?";
	private static final String selectuser = "SELECT u.user_id,u.user_name,u.user_mail,u.mobile_no,u.role_id,u.access_mobile_site,u.profile_image,ifnull(c.customerid,0) customerid,ifnull(c.customername,'') customername from user_master u left join customer_master c on u.user_id=c.userid where u.user_name=? and u.password=SHA2(?,256) ";
	private static final String selectuserdetail = "SELECT u.user_id,u.user_name,u.user_mail,u.mobile_no,u.role_id,u.access_mobile_site from user_master u where u.user_id=? ";
	private static final String checkUserName = "select 'x' from user_master where (user_mail=? or user_name=? or mobile_no=?) and user_id!=?";
	private static final String checkOldPassword = "select 'x' from user_master u where u.user_id=? and u.password=SHA2(?,256)";
	private static final String insertUserLocation = "insert into user_location_map(user_id,locationid,warehouseid,sublocationid,dockid) values(?,?,?,?,?)";
	private static final String removeUserLocation = "delete from user_location_map where user_id=? and dockid=? ";

	public ApiResult<Employee> manageEmployee(Employee emp) {
		if (emp.getEmployeeId() == 0) {
			return addEmployee(emp);
		} else {
			return modifyEmployee(emp);
		}
	}

	public ApiResult<Role> manageRole(Role role) {
		if (role.getRoleId() == 0) {
			return addRole(role);
		} else {
			return modifyRole(role);
		}
	}

	public ApiResult<Employee> addEmployee(Employee emp) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<Employee> result = new ApiResult<>();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateEmployeeKey);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = conn.prepareStatement(selectEmployeeKey);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				code = rs.getString("seq_no");
			}
			rs.close();
			pstmt = conn.prepareStatement(checkUserName);
			pstmt.setString(1, emp.getEmail());
			pstmt.setString(2, emp.getUserName());
			pstmt.setString(3, emp.getMobileNo());
			pstmt.setString(4, "0");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result.isSuccess = false;
				result.message = "E-mail or Mobile number already exists.";
				return result;
			}
			rs.close();
			pstmt.close();
			int userId = 0;
			pstmt = conn.prepareStatement(insertUsers, PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setString(1, emp.getUserName());
			pstmt.setString(2, emp.getEmail());
			pstmt.setString(3, emp.getPassword());
			pstmt.setString(4, emp.getMobileNo());
			pstmt.setInt(5, emp.getRoleId());
			pstmt.setString(6, emp.getEmployeeType());
			pstmt.setInt(7, emp.getCreatedBy());
			pstmt.setString(8, emp.getProfileImage());
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				userId = rs.getInt(1);
			}
			rs.close();
			pstmt.close();

			pstmt = conn.prepareStatement(insertQuery);
			pstmt.clearParameters();
			// em_code,em_name,em_address1,em_address2,em_city,em_email,em_mobile,em_phone,em_user_name,em_type,em_status
			pstmt.setString(1, code);
			pstmt.setString(2, emp.getEmployeeName());
			pstmt.setString(3, emp.getAddress1());
			pstmt.setString(4, emp.getAddress2());
			pstmt.setString(5, emp.getCity());
			pstmt.setString(6, emp.getEmail());
			pstmt.setString(7, emp.getMobileNo());
			pstmt.setString(8, emp.getPhone());
			pstmt.setString(9, emp.getUserName());
			pstmt.setString(10, emp.getEmployeeType());
			pstmt.setString(11, emp.getEmployeeStatus());
			pstmt.setInt(12, userId);
			pstmt.executeUpdate();
			pstmt.close();

//			pstmt=conn.prepareStatement(insertDefaultBranch);
//			pstmt.clearParameters();
//			pstmt.setString(1, (String)l.get("username"));
//			pstmt.setString(2, (String)l.get("defbranch"));
//			pstmt.executeUpdate();
//			pstmt.close();

//			Map branchData=(Map)l.get("branch_data");
//			ArrayList detailData=(ArrayList)branchData.get("v");
//
//			int s=detailData.size();
//			pstmt=conn.prepareStatement(insertUserBranches);
//			for(int i=0;i<s;i++){
//				ArrayList detail=(ArrayList)detailData.get(i);
//				if("true".equals(detail.get(2))){
//					pstmt.clearParameters();
//					pstmt.setString(1, (String)l.get("username"));
//					pstmt.setString(2, (String)detail.get(1));					
//					pstmt.setString(3, (String)detail.get(3));
//					pstmt.addBatch();
//				}
//			}
//			pstmt.executeBatch();
//			pstmt.close();

//			Map processData=(Map)l.get("processes_data");
//			ArrayList processDetailData=(ArrayList)processData.get("v");
//
//			s=processDetailData.size();
//			pstmt=conn.prepareStatement(insertEmployeeProcess);
//			for(int i=0;i<s;i++){
//				ArrayList detail=(ArrayList)processDetailData.get(i);
//				if("true".equals(detail.get(3))){
//					pstmt.clearParameters();
//					pstmt.setString(1, (String)detail.get(2));
//					pstmt.setString(2, code);
//					pstmt.setString(3, "Active");
//					pstmt.addBatch();
//				}
//			}
//			pstmt.executeBatch();
//			pstmt.close();

			conn.commit();
			EmailInput em = new EmailInput();
			em.setEmail(emp.getEmail());
			em.setFilterCode(String.valueOf(userId));
			em.setPassword(emp.getPassword());
			em.setConfigId(1);
			MailSendUtil.sendMailNotification(conn, em);
			result.isSuccess = true;
			result.message = "Employee added successfully";
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

	public ApiResult<Employee> modifyEmployee(Employee emp) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<Employee> result = new ApiResult<>();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(checkUserName);
			pstmt.setString(1, emp.getEmail());
			pstmt.setString(2, emp.getUserName());
			pstmt.setString(3, emp.getMobileNo());
			pstmt.setInt(4, emp.getUserId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result.isSuccess = false;
				result.message = "E-mail or Mobile number already exists.";
				return result;
			}
			rs.close();
			pstmt.close();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyQuery);
			pstmt.clearParameters();
			pstmt.setString(1, emp.getEmployeeName());
			pstmt.setString(2, emp.getAddress1());
			pstmt.setString(3, emp.getAddress2());
			pstmt.setString(4, emp.getCity());
			pstmt.setString(5, emp.getEmail());
			pstmt.setString(6, emp.getMobileNo());
			pstmt.setString(7, emp.getPhone());
			pstmt.setString(8, emp.getEmployeeType());
			pstmt.setString(9, emp.getEmployeeStatus());
			pstmt.setInt(10, emp.getEmployeeId());
			pstmt.executeUpdate();

			pstmt = conn.prepareStatement(updateUsers);
			pstmt.clearParameters();
			pstmt.setInt(1, emp.getRoleId());
			pstmt.setString(2, emp.getEmail());
			pstmt.setString(3, emp.getMobileNo());
			pstmt.setInt(4, emp.getCreatedBy());
			pstmt.setString(5, emp.getProfileImage());
			pstmt.setInt(6, emp.getUserId());
			pstmt.executeUpdate();

//			pstmt=conn.prepareStatement(updateDefaultBranch);
//			pstmt.clearParameters();
//			pstmt.setString(1, (String)l.get("defbranch"));
//			pstmt.setString(2, (String)l.get("username"));
//			pstmt.executeUpdate();
//			pstmt.close();
//
//			pstmt=conn.prepareStatement(deleteUserBranches);
//			pstmt.setString(1, (String)l.get("username"));
//			pstmt.executeUpdate();
//			pstmt.close();
//			
//			Map branchData=(Map)l.get("branch_data");
//			ArrayList detailData=(ArrayList)branchData.get("v");
//
//			int s=detailData.size();
//			pstmt=conn.prepareStatement(insertUserBranches);
//			for(int i=0;i<s;i++){
//				ArrayList detail=(ArrayList)detailData.get(i);
//				if("true".equals(detail.get(2))){
//					pstmt.clearParameters();
//					pstmt.setString(1, (String)l.get("username"));
//					pstmt.setString(2, (String)detail.get(1));
//					pstmt.setString(3, (String)detail.get(3));
//					pstmt.addBatch();
//				}
//			}
//			pstmt.executeBatch();
//			pstmt.close();
//
//			pstmt=conn.prepareStatement(deleteEmployeeProcess);
//			pstmt.setString(1, (String)l.get("code"));
//			pstmt.executeUpdate();
//			pstmt.close();

//			Map processData=(Map)l.get("processes_data");
//			ArrayList processDetailData=(ArrayList)processData.get("v");
//
//			s=processDetailData.size();
//			pstmt=conn.prepareStatement(insertEmployeeProcess);
//			for(int i=0;i<s;i++){
//				ArrayList detail=(ArrayList)processDetailData.get(i);
//				if("true".equals(detail.get(3))){
//					pstmt.clearParameters();
//					pstmt.setString(1, (String)detail.get(2));
//					pstmt.setString(2, (String)l.get("code"));
//					pstmt.setString(3, "Active");
//					pstmt.addBatch();
//				}
//			}
//			pstmt.executeBatch();
//			pstmt.close();

			conn.commit();
			result.isSuccess = true;
			result.message = "Employee modified successfully";
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

	public ApiResult<Employee> getEmployee(String code) {
		StringWriter l = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		String userName = "";
		ApiResult<Employee> apiResult = new ApiResult<>();
		Employee emp = new Employee();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(select);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				emp.setEmployeeId(rs.getInt("em_code"));
				emp.setEmployeeName(rs.getString("em_name"));
				emp.setAddress1(rs.getString("em_address1"));
				emp.setAddress2(rs.getString("em_address2"));
				emp.setCity(rs.getString("em_city"));
				emp.setEmail(rs.getString("em_email"));
				emp.setMobileNo(rs.getString("em_mobile"));
				emp.setPhone(rs.getString("em_phone"));
				emp.setUserName(rs.getString("em_user_name"));
				emp.setEmployeeStatus(rs.getString("em_status"));
				emp.setEmployeeType(rs.getString("em_type"));
				emp.setPassword(rs.getString("password"));
				emp.setRoleName(rs.getString("role_name"));
				emp.setRoleId(rs.getInt("role_id"));
				emp.setUserId(rs.getInt("userid"));
				emp.setProfileImage(rs.getString("profile_image"));
				userName = rs.getString("em_user_name");
			}
			rs.close();
			pstmt.close();
			apiResult.isSuccess = true;
			apiResult.result = emp;
//			pstmt=conn.prepareStatement(selectUserBranch);			
//			pstmt.setString(1, userName);
//			rs=pstmt.executeQuery();
//			generator.writeFieldName("branch_data");			
//			generator.writeStartArray();
//			int i=0;
//			while(rs.next()){
//				generator.writeStartArray();
//				AjaxResponseUtil.writeField(generator, String.valueOf(++i),null);
//				AjaxResponseUtil.writeField(generator, rs.getString("branchname"),rs.getString("branchcode"));
//				if(rs.getInt("selected")!=0){
//					AjaxResponseUtil.writeField(generator, String.valueOf(1),null);
//				}else{
//					AjaxResponseUtil.writeField(generator, String.valueOf(0),null);
//				}
//				AjaxResponseUtil.writeField(generator, rs.getString("locationname"),rs.getString("locationcode"));
//				generator.writeEndArray();
//			}
//			rs.close();
//			pstmt.close();			
//			generator.writeEndArray();

//			pstmt=conn.prepareStatement(selectEmployeeProcessMap);			
//			pstmt.setString(1, code);
//			rs=pstmt.executeQuery();
//			generator.writeFieldName("processes_data");			
//			generator.writeStartArray();
//			i=0;
//			while(rs.next()){
//				generator.writeStartArray();
//				AjaxResponseUtil.writeField(generator, String.valueOf(++i),null);
//				AjaxResponseUtil.writeField(generator, rs.getString("pg_name"),null);
//				AjaxResponseUtil.writeField(generator, rs.getString("srp_status_caption"),rs.getString("srp_code"));
//				if(rs.getInt("selected")!=0){
//					AjaxResponseUtil.writeField(generator, String.valueOf(1),null);
//				}else{
//					AjaxResponseUtil.writeField(generator, String.valueOf(0),null);
//				}
//				generator.writeEndArray();
//			}
//			generator.writeEndArray();

//			generator.writeEndObject();
//			generator.close();
		} catch (Exception e) {
			e.printStackTrace();
			apiResult.isSuccess = false;
			apiResult.message = e.getMessage();
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
		return apiResult;
	}

	public ApiResult<Role> addRole(Role role) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pColumns = null;
		ResultSet rs = null;
		ApiResult<Role> result = new ApiResult<Role>();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertRoleMaster, PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setString(1, role.getRoleName());
			pstmt.setString(2, role.getRoleDescription());
			pstmt.setInt(3, role.getParentRoleId());
			pstmt.setInt(4, role.getUserId());
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				code = rs.getString(1);
			}
			rs.close();

			pstmt = conn.prepareStatement(insertRoleMenus);
			ArrayList<RoleMenu> menus = (ArrayList<RoleMenu>) role.getRoleMenus();
			int s = menus.size();
			for (int i = 0; i < s; i++) {
				pstmt.clearParameters();
				pstmt.setString(1, code);
				pstmt.setInt(2, menus.get(i).getMenuId());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			pstmt.close();

			pstmt = conn.prepareStatement(insertRoleOperations);
			pColumns = conn.prepareStatement(insertRoleColumns);
			ArrayList<RoleOperation> operations = (ArrayList<RoleOperation>) role.getRoleOperations();
			if (operations != null) {
				s = operations.size();
				for (int i = 0; i < s; i++) {
					RoleOperation operation = operations.get(i);
					pstmt.clearParameters();
					pstmt.setString(1, code);
					pstmt.setInt(2, operation.getMenuId());
					pstmt.setInt(3, operation.getOperationId());
					pstmt.addBatch();
					if (operation.getReportId() != 0 && !"".equals((String) operation.getReportColumn())) {
						pColumns.clearParameters();
						pColumns.setString(1, code);
						pColumns.setInt(2, operation.getMenuId());
						pColumns.setInt(3, operation.getReportId());
						pColumns.setString(4, operation.getReportColumn());
						pColumns.addBatch();
					}
				}
				pstmt.executeBatch();
				pstmt.close();
			}
			ArrayList<RoleOperation> columns = (ArrayList<RoleOperation>) role.getRoleColumns();
			if (columns != null) {
				s = columns.size();
				for (int i = 0; i < s; i++) {
					RoleOperation column = (RoleOperation) columns.get(i);
					if (column.getReportId() != 0) {
						pColumns.clearParameters();
						pColumns.setString(1, code);
						pColumns.setInt(2, column.getMenuId());
						pColumns.setInt(3, column.getReportId());
						pColumns.setString(4, column.getReportColumn());
						pColumns.addBatch();
					}
				}
				pColumns.executeBatch();
				pColumns.close();
			}
//			pstmt=conn.prepareStatement(insertRoleReportColumns);
//			pstmt.clearParameters();
//			pstmt.setString(1, code);
//			pstmt.executeUpdate();
//			pstmt.close();

			conn.commit();
			result.isSuccess = true;
			result.message = "Role added successfully";
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
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (pColumns != null) {
					pColumns.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	public ApiResult<Role> modifyRole(Role role) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pColumns = null;
		ResultSet rs = null;
		ApiResult<Role> result = new ApiResult<Role>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateRoleMaster);
			pstmt.clearParameters();
			pstmt.setString(1, role.getRoleName());
			pstmt.setString(2, role.getRoleDescription());
			pstmt.setInt(3, role.getParentRoleId());
			pstmt.setInt(4, role.getUserId());
			pstmt.setInt(5, role.getRoleId());
			pstmt.executeUpdate();
			pstmt.close();

			pstmt = conn.prepareStatement(deleteRoleMenus);
			pstmt.setInt(1, role.getRoleId());
			pstmt.executeUpdate();
			pstmt.close();

			pstmt = conn.prepareStatement(deleteRoleOperations);
			pstmt.setInt(1, role.getRoleId());
			pstmt.executeUpdate();
			pstmt.close();

			pstmt = conn.prepareStatement(deleteRoleColumns);
			pstmt.setInt(1, role.getRoleId());
			pstmt.executeUpdate();
			pstmt.close();

			pstmt = conn.prepareStatement(insertRoleMenus);
			ArrayList<RoleMenu> menus = (ArrayList<RoleMenu>) role.getRoleMenus();
			int s = menus.size();
			for (int i = 0; i < s; i++) {
				pstmt.clearParameters();
				pstmt.setInt(1, role.getRoleId());
				pstmt.setInt(2, menus.get(i).getMenuId());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			pstmt.close();

			pstmt = conn.prepareStatement(insertRoleOperations);
			pColumns = conn.prepareStatement(insertRoleColumns);
			ArrayList<RoleOperation> operations = (ArrayList<RoleOperation>) role.getRoleOperations();
			if (operations != null) {
				s = operations.size();
				for (int i = 0; i < s; i++) {
					RoleOperation operation = operations.get(i);
					pstmt.clearParameters();
					pstmt.setInt(1, role.getRoleId());
					pstmt.setInt(2, operation.getMenuId());
					pstmt.setInt(3, operation.getOperationId());
					pstmt.addBatch();
					if (operation.getReportId() != 0 && !"".equals((String) operation.getReportColumn())) {
						pColumns.clearParameters();
						pColumns.setInt(1, role.getRoleId());
						pColumns.setInt(2, operation.getMenuId());
						pColumns.setInt(3, operation.getReportId());
						pColumns.setString(4, operation.getReportColumn());
						pColumns.addBatch();
					}
				}
				pstmt.executeBatch();
				pstmt.close();
			}
			ArrayList<RoleOperation> columns = (ArrayList<RoleOperation>) role.getRoleColumns();
			if (columns != null) {
				s = columns.size();
				for (int i = 0; i < s; i++) {
					RoleOperation column = (RoleOperation) columns.get(i);
					if (column.getReportId() != 0) {
						pColumns.clearParameters();
						pColumns.setInt(1, role.getRoleId());
						pColumns.setInt(2, column.getMenuId());
						pColumns.setInt(3, column.getReportId());
						pColumns.setString(4, column.getReportColumn());
						pColumns.addBatch();
					}
				}
				pColumns.executeBatch();
				pColumns.close();
			}
//			pstmt=conn.prepareStatement(insertRoleReportColumns);
//			pstmt.clearParameters();
//			pstmt.setInt(1, role.getRoleId());
//			pstmt.executeUpdate();
//			pstmt.close();

			conn.commit();
			result.isSuccess = true;
			result.message = "Role modified successfully";
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
				if (pColumns != null) {
					pColumns.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	public ApiResult<Role> getRole(String code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<Role> result = new ApiResult<Role>();
		Role role = new Role();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectRoleMaster);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				role.setRoleId(rs.getInt("role_id"));
				role.setRoleName(rs.getString("role_name"));
				role.setRoleDescription(rs.getString("role_description"));
				role.setParentRoleId(rs.getInt("parentid"));
				role.setParentRoleName(rs.getString("parentname"));
			}
			rs.close();
			pstmt.close();
			ArrayList<RoleMenu> roleMenus = new ArrayList<RoleMenu>();
			pstmt = conn.prepareStatement(selectRoleMenus);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RoleMenu menu = new RoleMenu();
				menu.setMenuId(rs.getInt("menu_id"));
				menu.setMenuName(rs.getString("menu_name"));
				menu.setMenuCaption(rs.getString("menu_caption"));
				menu.setParentId(rs.getInt("parent_id"));
				menu.setSelected(rs.getBoolean("selected"));
				roleMenus.add(menu);
			}
			rs.close();
			pstmt.close();
			ArrayList<RoleOperation> roleOperation = new ArrayList<RoleOperation>();
			pstmt = conn.prepareStatement(selectRoleOperations);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RoleOperation operation = new RoleOperation();
				operation.setMenuId(rs.getInt("so_menu_id"));
				operation.setOperationId(rs.getInt("so_id"));
				operation.setCaption(rs.getString("so_caption"));
				operation.setReportColumn(rs.getString("so_report_column"));
				operation.setSelected(rs.getBoolean("selected"));
				operation.setMenuReportId(rs.getInt("menu_report_id"));
				roleOperation.add(operation);
			}
			rs.close();
			pstmt.close();
			ArrayList<RoleOperation> roleColumns = new ArrayList<RoleOperation>();
			pstmt = conn.prepareStatement(selectRoleColumns);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RoleOperation operation = new RoleOperation();
				operation.setMenuId(rs.getInt("rcm_menu_id"));
				operation.setReportId(rs.getInt("rcm_report_id"));
				operation.setReportColumn(rs.getString("rcm_column_name"));
				operation.setCaption(rs.getString("column_title"));
				operation.setSelected(rs.getBoolean("selected"));
				roleColumns.add(operation);
			}
			rs.close();
			pstmt.close();
			role.setRoleMenus(roleMenus);
			role.setRoleColumns(roleColumns);
			role.setRoleOperations(roleOperation);
			result.isSuccess = true;
			result.result = role;
		} catch (Exception e) {
			e.printStackTrace();
			result.isSuccess = false;

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

	public ApiResult<Role> getParentRoleMenus(String code) {
		StringWriter l = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		Role role = new Role();
		ApiResult<Role> result = new ApiResult<Role>();
		try {
			conn = DatabaseUtil.getConnection();
			ArrayList<RoleMenu> roleMenus = new ArrayList<RoleMenu>();
			pstmt = conn.prepareStatement(selectParentRoleMenus);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RoleMenu menu = new RoleMenu();
				menu.setMenuId(rs.getInt("menu_id"));
				menu.setMenuName(rs.getString("menu_name"));
				menu.setMenuCaption(rs.getString("menu_caption"));
				menu.setParentId(rs.getInt("parent_id"));
				menu.setSelected(rs.getBoolean("selected"));
				roleMenus.add(menu);
			}
			rs.close();
			pstmt.close();
			ArrayList<RoleOperation> roleOperation = new ArrayList<RoleOperation>();
			pstmt = conn.prepareStatement(selectParentRoleOperations);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RoleOperation operation = new RoleOperation();
				operation.setMenuId(rs.getInt("so_menu_id"));
				operation.setOperationId(rs.getInt("so_id"));
				operation.setCaption(rs.getString("so_caption"));
				operation.setReportColumn(rs.getString("so_report_column"));
				operation.setSelected(rs.getBoolean("selected"));
				operation.setMenuReportId(rs.getInt("menu_report_id"));
				roleOperation.add(operation);
			}
			rs.close();
			pstmt.close();
			ArrayList<RoleOperation> roleColumns = new ArrayList<RoleOperation>();
			pstmt = conn.prepareStatement(selectParentRoleColumns);
			pstmt.setString(1, code);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RoleOperation operation = new RoleOperation();
				operation.setMenuId(rs.getInt("rcm_menu_id"));
				operation.setReportId(rs.getInt("rcm_report_id"));
				operation.setReportColumn(rs.getString("rcm_column_name"));
				operation.setCaption(rs.getString("column_title"));
				operation.setSelected(rs.getBoolean("selected"));
				roleColumns.add(operation);
			}
			rs.close();
			pstmt.close();
			role.setRoleMenus(roleMenus);
			role.setRoleColumns(roleColumns);
			role.setRoleOperations(roleOperation);
			result.isSuccess = true;
			result.result = role;
		} catch (Exception e) {
			e.printStackTrace();
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

	public String checkUser(String userCode) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String result = "";
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(checkUserCode);
			pstmt.clearParameters();
			pstmt.setString(1, userCode);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = "{\"result\":\"Success\",\"message\":\"Logged in successfully\",\"code\":\"" + userCode
						+ "\"}";
			} else {
				result = "{\"result\":\"Failed\",\"message\":\"UserCode is not correct/available\",\"code\":\"0\"}";
			}
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
			e.printStackTrace();
			result = "{\"result\":\"Error\",\"message\":\"Error while validating user code [" + e.getMessage() + "]\"}";
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

//	public User getauthuser(String userid,String password,String deviceId){
//		Connection conn=null;
//		PreparedStatement pstmt=null;
//		PreparedStatement insStmt=null;
//		ResultSet rs=null;
//		User user=new User();
//
//		try{
//			
//			conn=DatabaseUtil.getConnection();
//			conn.setAutoCommit(false);
//			pstmt=conn.prepareStatement(selectuser);
//			pstmt.setString(1,userid);
//			pstmt.setString(2,password);
//			
//			rs=pstmt.executeQuery();
//			if(rs.next()){
//				user.setUserId(rs.getInt("user_id"));
//				user.setUserName(rs.getString("user_name"));
//				user.setEmailId(rs.getString("user_mail"));
//				user.setMobileNo(rs.getString("mobile_no"));
//				user.setAccessMobileSite(rs.getInt("access_mobile_site"));
//				user.setRoleId(rs.getInt("role_id"));
//				user.setToken(issueToken(user, "http://20.204.182.100"));
//				user.setProfileImage(rs.getString("profile_image"));
//			}rs.close();			
//			pstmt.close();
//		}catch (Exception e) {
//			e.printStackTrace();
//       }finally{
//       	try{
//       		if(conn!=null){
//       			conn.close();
//       		}
//       		if(pstmt!=null){
//       			pstmt.close();
//       		}
//       	}catch(SQLException esql){
//       		esql.printStackTrace();
//       	}
//       }
//		return user;
//	}

	public User getauthuser(String userid, String password, String deviceId) {
		System.out.println("::::::::::::::::::::::::::::::::::::    1 ");
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement insStmt = null;
		ResultSet rs = null;
		User user = new User();
		System.out.println("::::::::::::::::::::::::::::::::::::    2 ");
		try {
			System.out.println("::::::::::::::::::::::::::::::::::::    3 ");

			conn = DatabaseUtil.getConnection();
			System.out.println("::::::::::::::::::::::::::::::::::::    4 ");
			conn.setAutoCommit(false);
			System.out.println("::::::::::::::::::::::::::::::::::::    5 ");
			pstmt = conn.prepareStatement(selectuser);
			pstmt.setString(1, userid);
			pstmt.setString(2, password);
			System.out.println("::::::::::::::::::::::::::::::::::::    6 ");

			rs = pstmt.executeQuery();
			System.out.println("::::::::::::::::::::::::::::::::::::    7 ");
			if (rs.next()) {
				System.out.println("::::::::::::::::::::::::::::::::::::    8 ");
				user.setUserId(rs.getInt("user_id"));
				user.setUserName(rs.getString("user_name"));
				user.setEmailId(rs.getString("user_mail"));
				user.setMobileNo(rs.getString("mobile_no"));
				user.setAccessMobileSite(rs.getInt("access_mobile_site"));
				user.setRoleId(rs.getInt("role_id"));
				user.setToken(issueToken(user, "http://20.204.182.100"));
				user.setProfileImage(rs.getString("profile_image"));
				System.out.println("::::::::::::::::::::::::::::::::::::    9 ");
			}
			System.out.println("::::::::::::::::::::::::::::::::::::    10 ");
			rs.close();
			pstmt.close();
			System.out.println("::::::::::::::::::::::::::::::::::::    11 ");
		} catch (Exception e) {
			System.out.println("::::::::::::::::::::::::::::::::::::    12 ");
			e.printStackTrace();
		} finally {
			System.out.println("::::::::::::::::::::::::::::::::::::    13 ");
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
		System.out.println("::::::::::::::::::::::::::::::::::::    14 ");
		return user;
	}

	public User getUser(int userid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement insStmt = null;
		ResultSet rs = null;
		User user = new User();

		try {

			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(selectuserdetail);
			pstmt.setInt(1, userid);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				user.setUserId(rs.getInt("user_id"));
				user.setUserName(rs.getString("user_name"));
				user.setEmailId(rs.getString("user_mail"));
				user.setMobileNo(rs.getString("mobile_no"));
				user.setAccessMobileSite(rs.getInt("access_mobile_site"));
				user.setRoleId(rs.getInt("role_id"));
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
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
		return user;
	}

	private String issueToken(User user, String urlPath) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", user.getUserName());
		claims.put("userid", user.getUserId());
		claims.put("mobileno", user.getMobileNo());
		claims.put("emailid", user.getEmailId());
		claims.put("roleid", user.getRoleId());
		String jwtToken = Jwts.builder().setSubject(user.getUserName()).setIssuer(urlPath).setClaims(claims)
				.setIssuedAt(new Date()).setExpiration(toDate(LocalDateTime.now().plusMinutes(200L)))
				.signWith(SignatureAlgorithm.HS512,
						Base64.encode("tbis@1234abcdefghijklmnopqrst1234567890tbis@1234abcdefghijklmnopqrst1234567890"))
				.compact();
		return jwtToken;
	}

	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(
						Base64.encode("tbis@1234abcdefghijklmnopqrst1234567890tbis@1234abcdefghijklmnopqrst1234567890"))
				.build().parseSignedClaims(token).getPayload();
	}

	private Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static UserManager getInstance() {
		return new UserManager();
	}

	public ApiResult<Employee> updateProfile(Employee emp) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement insStmt = null;
		ResultSet rs = null;
		ApiResult<Employee> result = new ApiResult<Employee>();

		try {

			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			if (!"".equals(emp.getOldPassword())) {
				pstmt = conn.prepareStatement(checkOldPassword);
				pstmt.setInt(1, emp.getUserId());
				pstmt.setString(2, emp.getOldPassword());

				rs = pstmt.executeQuery();
				if (!rs.next()) {
					result.isSuccess = false;
					result.message = "Invalid old password.Try again.";
					return result;
				}
				rs.close();
				pstmt.close();
				pstmt = conn.prepareStatement(updateUserPassword);
				pstmt.setString(1, emp.getPassword());
				pstmt.setInt(2, emp.getUserId());
				pstmt.executeUpdate();
				result.isSuccess = true;
				result.message = "Password updated successfully. Login again with new password";
			} else {
				pstmt = conn.prepareStatement(updateProfileImage);
				pstmt.setString(1, emp.getProfileImage());
				pstmt.setInt(2, emp.getUserId());
				pstmt.executeUpdate();
				result.isSuccess = true;
				result.message = "Profile picture updated successfully";
			}
			conn.commit();
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

	public ApiResult<UnloadDock> addUserLocation(UnloadDock l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<UnloadDock> result = new ApiResult<>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertUserLocation);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getUserId());
			pstmt.setInt(2, l.getLocationId());
			pstmt.setInt(3, l.getWarehouseId());
			pstmt.setInt(4, l.getSubLocationId());
			pstmt.setInt(5, l.getUdcId());
			pstmt.executeUpdate();
			pstmt.close();
			conn.commit();
			result.isSuccess = true;
			result.message = "Location mapped successfully";
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

	public ApiResult<UnloadDock> removeUserLocation(UnloadDock l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<UnloadDock> result = new ApiResult<>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(removeUserLocation);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getUserId());
			pstmt.setInt(2, l.getUdcId());
			pstmt.executeUpdate();
			pstmt.close();
			conn.commit();
			result.isSuccess = true;
			result.message = "Location mapped removed successfully";
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
}