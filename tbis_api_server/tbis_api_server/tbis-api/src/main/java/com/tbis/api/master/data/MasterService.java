package com.tbis.api.master.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.ApplicationType;
import com.tbis.api.master.model.AssetCategory;
import com.tbis.api.master.model.AssetCategoryBudget;
import com.tbis.api.master.model.AssetSubCategory;
import com.tbis.api.master.model.AssetSubCategoryBudget;
import com.tbis.api.master.model.Department;
import com.tbis.api.master.model.Designation;
import com.tbis.api.master.model.MailConfigs;
import com.tbis.api.master.model.WmsColorCode;
import com.tbis.api.master.model.WmsConfiguration;

public class MasterService {
	
	//	Designation
	private static final String insertDesignationQuery="insert into cq_wms_designation (cwd_desig_name,cwd_desig_short,cwd_issystem,cwd_isactive,cwd_created_by,cwd_createdt,cwd_tsid) values (?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyDesignationQuery="update cq_wms_designation set cwd_desig_name=?,cwd_desig_short=?,cwd_issystem=?,cwd_isactive=?,cwd_update_by=?,cwd_updatedt=now(),cwd_tsid=getcurrenttsid(now()) where cwd_desig_id=?;";
	private static final String selectDesignationQuery="select cwd_desig_id,cwd_desig_name,cwd_desig_short,cwd_issystem,cwd_isactive isactive from cq_wms_designation where cwd_desig_id=?;";
	
	//	Department
	private static final String insertDepartmentQuery="insert into cq_wms_department (cwd_dept_name,cwd_dept_short,cwd_issystem,cwd_isactive,cwd_created_by,cwd_createdt,cwd_tsid) values (?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyDepartmentQuery="update cq_wms_department set cwd_dept_name=?,cwd_dept_short=?,cwd_issystem=?,cwd_isactive=?,cwd_update_by=?,cwd_updatedt=now(),cwd_tsid=getcurrenttsid(now()) where cwd_dept_id=?;";
	private static final String selectDepartmentQuery="select cwd_dept_id,cwd_dept_name,cwd_dept_short,cwd_issystem,cwd_isactive isactive from cq_wms_department where cwd_dept_id=?;";
	
	//	ApplicationType
	private static final String insertApplicationTypeQuery="insert into cq_wms_application_type (cwa_desc,cwa_issystem,cwa_forma,cwa_formc,cwa_formi,cwa_formg,cwa_isactive,cwa_created_by,cwa_createdt,cwa_tsid) values (?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyApplicationTypeQuery="update cq_wms_application_type set cwa_desc=?,cwa_issystem=?,cwa_forma=?,cwa_formc=?,cwa_formi=?,cwa_formg=?,cwa_isactive=?,cwa_update_by=?,cwa_updatedt=now(),cwa_tsid=getcurrenttsid(now()) where cwa_id=?;";
	private static final String selectApplicationTypeQuery="select cwa_id,cwa_desc,cwa_issystem,cwa_forma,cwa_formc,cwa_formi,cwa_formg,cwa_isactive isactive from cq_wms_application_type where cwa_id=?;";
	
	//	AssetCategory
	private static final String insertAssetCategoryQuery="insert into cq_wms_asset_category (cwac_name,cwac_short,cwac_issystem,cwac_isactive,cwac_created_by,cwac_createdt,cwac_tsid) values (?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyAssetCategoryQuery="update cq_wms_asset_category set cwac_name=?,cwac_short=?,cwac_issystem=?,cwac_isactive=?,cwac_update_by=?,cwac_updatedt=now(),cwac_tsid=getcurrenttsid(now()) where cwac_id=?;";
	private static final String selectAssetCategoryQuery="select cwac_id,cwac_name,cwac_short,cwac_issystem,cwac_isactive isactive from cq_wms_asset_category where cwac_id=?;";
	
	//	AssetCategoryBudget
	private static final String insertAssetCategoryBudgetQuery="insert into cq_wms_asset_category_budget (cwacb_period,cwacb_budget,cwacb_budget_used,cwacb_warehouse_id,cwac_id,cwacb_isactive,cwacb_created_by,cwacb_createdt,cwacb_tsid) values (?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyAssetCategoryBudgetQuery="update cq_wms_asset_category_budget set cwacb_period=?,cwacb_budget=?,cwacb_budget_used=?,cwacb_warehouse_id=?,cwac_id=?,cwacb_isactive=?,cwacb_update_by=?,cwacb_updatedt=now(),cwacb_tsid=getcurrenttsid(now()) where cwacb_id=?;";
	private static final String selectAssetCategoryBudgetQuery="select cwacb_id,cwac_id,cwacb_period,cwacb_budget,cwacb_budget_used,cwacb_warehouse_id,cwacb_isactive isactive from cq_wms_asset_category_budget where cwacb_id=?;";
	
	//	AssetSubCategory
	private static final String insertAssetSubCategoryQuery="insert into cq_wms_asset_subcategory (cwac_id,cwasc_name,cwasc_short,cwasc_issystem,cwasc_isactive,cwasc_created_by,cwasc_createdt,cwasc_tsid) values (?,?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyAssetSubCategoryQuery="update cq_wms_asset_subcategory set cwac_id=?,cwasc_name=?,cwasc_short=?,cwasc_issystem=?,cwasc_isactive=?,cwasc_update_by=?,cwasc_updatedt=now(),cwasc_tsid=getcurrenttsid(now()) where cwasc_id=?;";
	private static final String selectAssetSubCategoryQuery="select cwasc_id,cwac_id,cwasc_name,cwasc_short,cwasc_issystem,cwasc_isactive isactive from cq_wms_asset_subcategory where cwasc_id=?;";
	
	//	AssetSubCategoryBudget
	private static final String insertAssetSubCategoryBudgetQuery="insert into cq_wms_asset_subcategory_budget (cwasc_id,cwascb_warehouse_id,cwascb_budget,cwascb_budget_used,cwascb_issystem,cwascb_isactive,cwascb_created_by,cwascb_createdt,cwascb_tsid) values (?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyAssetSubCategoryBudgetQuery="update cq_wms_asset_subcategory_budget set cwasc_id=?,cwascb_warehouse_id=?,cwascb_budget=?,cwascb_budget_used=?,cwascb_issystem=?,cwascb_isactive=?,cwascb_update_by=?,cwascb_updatedt=now(),cwascb_tsid=getcurrenttsid(now()) where cwascb_id=?;";
	private static final String selectAssetSubCategoryBudgetQuery="select cwascb_id,cwasc_id,cwascb_warehouse_id,cwascb_budget,cwascb_budget_used,cwascb_issystem,cwascb_isactive isactive from cq_wms_asset_subcategory_budget where cwascb_id=?;";
	
	
	//	Designation
	public ApiResult<Designation> manageDesignation(Designation l) {
		if (l.getCwdDesigId() == 0) {
			return addDesignation(l);
		} else {
			return modifyDesignation(l);
		}
	}
	public ApiResult<Designation> addDesignation(Designation l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertDesignationQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwdDesigName());
			pstmt.setString(2, l.getCwdDesigShort());
			pstmt.setBoolean(3, l.getCwdIsSystem());
			pstmt.setBoolean(4, l.isActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Designation added successfully";
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
	public ApiResult<Designation> modifyDesignation(Designation l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyDesignationQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwdDesigName());
			pstmt.setString(2, l.getCwdDesigShort());
			pstmt.setBoolean(3, l.getCwdIsSystem());
			pstmt.setBoolean(4, l.isActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.setInt(6,l.getCwdDesigId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Designation modified successfully";
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
	public ApiResult<Designation> getDesignation(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<Designation> result = new ApiResult<Designation>();
		Designation l = new Designation();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectDesignationQuery);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCwdDesigId(rs.getInt("cwd_desig_id"));
				l.setCwdDesigName(rs.getString("cwd_desig_name"));
				l.setCwdDesigShort(rs.getString("cwd_desig_short"));
				l.setCwdIsSystem(rs.getBoolean("cwd_issystem"));
				l.setActive(rs.getBoolean("isactive"));
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

	//	Department
	public ApiResult<Department> manageDepartment(Department l) {
		if (l.getCwdDeptId() == 0) {
			return addDepartment(l);
		} else {
			return modifyDepartment(l);
		}
	}
	public ApiResult<Department> addDepartment(Department l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertDepartmentQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwdDeptName());
			pstmt.setString(2, l.getCwdDeptShort());
			pstmt.setBoolean(3, l.getCwdIsSystem());
			pstmt.setBoolean(4, l.isActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Department added successfully";
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
	public ApiResult<Department> modifyDepartment(Department l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyDepartmentQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwdDeptName());
			pstmt.setString(2, l.getCwdDeptShort());
			pstmt.setBoolean(3, l.getCwdIsSystem());
			pstmt.setBoolean(4, l.isActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.setInt(6, l.getCwdDeptId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Department modified successfully";
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
	public ApiResult<Department> getDepartment(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<Department> result = new ApiResult<Department>();
		Department l = new Department();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectDepartmentQuery);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCwdDeptId(rs.getInt("cwd_dept_id"));
				l.setCwdDeptName(rs.getString("cwd_dept_name"));
				l.setCwdDeptShort(rs.getString("cwd_dept_short"));
				l.setCwdIsSystem(rs.getBoolean("cwd_issystem"));
				l.setActive(rs.getBoolean("isactive"));
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

	//	ApplicationType
	public ApiResult<ApplicationType> manageApplicationType(ApplicationType l) {
		if (l.getCwaId() == 0) {
			return addApplicationType(l);
		} else {
			return modifyApplicationType(l);
		}
	}
	public ApiResult<ApplicationType> addApplicationType(ApplicationType l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertApplicationTypeQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwaDesc());
			pstmt.setBoolean(2, l.getCwaIsSystem());
			pstmt.setBoolean(3, l.getCwaFormA());
			pstmt.setBoolean(4, l.getCwaFormC());
			pstmt.setBoolean(5, l.getCwaFormI());
			pstmt.setBoolean(6, l.getCwaFormG());
			pstmt.setBoolean(7, l.isActive());
			pstmt.setInt(8,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Application Type added successfully";
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
	public ApiResult<ApplicationType> modifyApplicationType(ApplicationType l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyApplicationTypeQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwaDesc());
			pstmt.setBoolean(2, l.getCwaIsSystem());
			pstmt.setBoolean(3, l.getCwaFormA());
			pstmt.setBoolean(4, l.getCwaFormC());
			pstmt.setBoolean(5, l.getCwaFormI());
			pstmt.setBoolean(6, l.getCwaFormG());
			pstmt.setBoolean(7, l.isActive());
			pstmt.setInt(8,l.getUserId());
			pstmt.setInt(9, l.getCwaId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Application Type modified successfully";
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
	public ApiResult<ApplicationType> getApplicationType(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ApplicationType> result = new ApiResult<ApplicationType>();
		ApplicationType l = new ApplicationType();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectApplicationTypeQuery);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCwaId(rs.getInt("cwa_id"));
				l.setCwaDesc(rs.getString("cwa_desc"));
				l.setCwaIsSystem(rs.getBoolean("cwa_issystem"));
				l.setCwaFormA(rs.getBoolean("cwa_forma"));
				l.setCwaFormC(rs.getBoolean("cwa_formc"));
				l.setCwaFormI(rs.getBoolean("cwa_formi"));
				l.setCwaFormG(rs.getBoolean("cwa_formg"));
				l.setActive(rs.getBoolean("isactive"));
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

	//	AssetCategory
	public ApiResult<AssetCategory> manageAssetCategory(AssetCategory l) {
		if (l.getCwacId() == 0) {
			return addAssetCategory(l);
		} else {
			return modifyAssetCategory(l);
		}
	}
	public ApiResult<AssetCategory> addAssetCategory(AssetCategory l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertAssetCategoryQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwacName());
			pstmt.setString(2, l.getCwacShort());
			pstmt.setBoolean(3, l.getCwacIsSystem());
			pstmt.setBoolean(4, l.isActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Category added successfully";
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
	public ApiResult<AssetCategory> modifyAssetCategory(AssetCategory l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyAssetCategoryQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwacName());
			pstmt.setString(2, l.getCwacShort());
			pstmt.setBoolean(3, l.getCwacIsSystem());
			pstmt.setBoolean(4, l.isActive());
			pstmt.setInt(5,l.getUserId());
			pstmt.setInt(6, l.getCwacId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Category modified successfully";
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
	public ApiResult<AssetCategory> getAssetCategory(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AssetCategory> result = new ApiResult<AssetCategory>();
		AssetCategory l = new AssetCategory();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectAssetCategoryQuery);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCwacId(rs.getInt("cwac_id"));
				l.setCwacName(rs.getString("cwac_name"));
				l.setCwacShort(rs.getString("cwac_short"));
				l.setCwacIsSystem(rs.getBoolean("cwac_issystem"));
				l.setActive(rs.getBoolean("isactive"));
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


	//	AssetCategoryBudget
	public ApiResult<AssetCategoryBudget> manageAssetCategoryBudget(AssetCategoryBudget l) {
		if (l.getCwacbId() == 0) {
			return addAssetCategoryBudget(l);
		} else {
			return modifyAssetCategoryBudget(l);
		}
	}
	public ApiResult<AssetCategoryBudget> addAssetCategoryBudget(AssetCategoryBudget l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertAssetCategoryBudgetQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwacbPeriod());
			pstmt.setDouble(2, l.getCwacbBudget());
			pstmt.setDouble(3, l.getCwacbBudgetUsed());
			pstmt.setInt(4, l.getCwacbWarehouseId());
			pstmt.setInt(5, l.getCwacId());
			pstmt.setBoolean(6, l.isActive());
			pstmt.setInt(7,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Category budget added successfully";
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
	public ApiResult<AssetCategoryBudget> modifyAssetCategoryBudget(AssetCategoryBudget l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyAssetCategoryBudgetQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCwacbPeriod());
			pstmt.setDouble(2, l.getCwacbBudget());
			pstmt.setDouble(3, l.getCwacbBudgetUsed());
			pstmt.setInt(4, l.getCwacbWarehouseId());
			pstmt.setInt(5, l.getCwacId());
			pstmt.setBoolean(6, l.isActive());
			pstmt.setInt(7,l.getUserId());
			pstmt.setInt(8, l.getCwacbId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Category budget modified successfully";
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
	public ApiResult<AssetCategoryBudget> getAssetCategoryBudget(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AssetCategoryBudget> result = new ApiResult<AssetCategoryBudget>();
		AssetCategoryBudget l = new AssetCategoryBudget();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectAssetCategoryBudgetQuery);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCwacbId(rs.getInt("cwacb_id"));
				l.setCwacbPeriod(rs.getString("cwacb_period"));
				l.setCwacbBudget(rs.getDouble("cwacb_budget"));
				l.setCwacbBudgetUsed(rs.getDouble("cwacb_budget_used"));
				l.setCwacbWarehouseId(rs.getInt("cwacb_warehouse_id"));
				l.setCwacId(rs.getInt("cwac_id"));
				l.setActive(rs.getBoolean("isactive"));
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

	//	AssetSubCategory
	public ApiResult<AssetSubCategory> manageAssetSubCategory(AssetSubCategory l) {
		if (l.getCwascId() == 0) {
			return addAssetSubCategory(l);
		} else {
			return modifyAssetSubCategory(l);
		}
	}
	public ApiResult<AssetSubCategory> addAssetSubCategory(AssetSubCategory l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertAssetSubCategoryQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getCwacId());
			pstmt.setString(2, l.getCwascName());
			pstmt.setString(3, l.getCwascShort());
			pstmt.setBoolean(4, l.getCwascIsSystem());
			pstmt.setBoolean(5, l.isActive());
			pstmt.setInt(6,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Sub-Category added successfully";
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
	public ApiResult<AssetSubCategory> modifyAssetSubCategory(AssetSubCategory l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyAssetSubCategoryQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getCwacId());
			pstmt.setString(2, l.getCwascName());
			pstmt.setString(3, l.getCwascShort());
			pstmt.setBoolean(4, l.getCwascIsSystem());
			pstmt.setBoolean(5, l.isActive());
			pstmt.setInt(6,l.getUserId());
			pstmt.setInt(7, l.getCwascId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Sub-Category modified successfully";
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
	public ApiResult<AssetSubCategory> getAssetSubCategory(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AssetSubCategory> result = new ApiResult<AssetSubCategory>();
		AssetSubCategory l = new AssetSubCategory();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectAssetSubCategoryQuery);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCwascId(rs.getInt("cwasc_id"));
				l.setCwacId(rs.getInt("cwac_id"));
				l.setCwascName(rs.getString("cwasc_name"));
				l.setCwascShort(rs.getString("cwasc_short"));
				l.setCwascIsSystem(rs.getBoolean("cwasc_issystem"));
				l.setActive(rs.getBoolean("isactive"));
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

	//	AssetSubCategoryBudget
	public ApiResult<AssetSubCategoryBudget> manageAssetSubCategoryBudget(AssetSubCategoryBudget l) {
		if (l.getCwascbId() == 0) {
			return addAssetSubCategoryBudget(l);
		} else {
			return modifyAssetSubCategoryBudget(l);
		}
	}
	public ApiResult<AssetSubCategoryBudget> addAssetSubCategoryBudget(AssetSubCategoryBudget l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertAssetSubCategoryBudgetQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getCwascId());
			pstmt.setInt(2, l.getCwascbWarehouseId());
			pstmt.setDouble(3, l.getCwascbBudget());
			pstmt.setDouble(4, l.getCwascbBudgetUsed());
			pstmt.setBoolean(5, l.getCwascbIsSystem());
			pstmt.setBoolean(6, l.isActive());
			pstmt.setInt(7,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Sub-Category budget added successfully";
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
	public ApiResult<AssetSubCategoryBudget> modifyAssetSubCategoryBudget(AssetSubCategoryBudget l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyAssetSubCategoryBudgetQuery);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getCwascId());
			pstmt.setInt(2, l.getCwascbWarehouseId());
			pstmt.setDouble(3, l.getCwascbBudget());
			pstmt.setDouble(4, l.getCwascbBudgetUsed());
			pstmt.setBoolean(5, l.getCwascbIsSystem());
			pstmt.setBoolean(6, l.isActive());
			pstmt.setInt(7,l.getUserId());
			pstmt.setInt(8, l.getCwascbId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Asset Sub-Category budget modified successfully";
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
	public ApiResult<AssetSubCategoryBudget> getAssetSubCategoryBudget(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AssetSubCategoryBudget> result = new ApiResult<AssetSubCategoryBudget>();
		AssetSubCategoryBudget l = new AssetSubCategoryBudget();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectAssetSubCategoryBudgetQuery);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCwascbId(rs.getInt("cwascb_id"));
				l.setCwascId(rs.getInt("cwasc_id"));
				l.setCwascbWarehouseId(rs.getInt("cwascb_warehouse_id"));
				l.setCwascbBudget(rs.getDouble("cwascb_budget"));
				l.setCwascbBudgetUsed(rs.getDouble("cwascb_budget_used"));
				l.setCwascbIsSystem(rs.getBoolean("cwascb_issystem"));
				l.setActive(rs.getBoolean("isactive"));
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


	public static MasterService getInstance() {
		return new MasterService();
	}
}
