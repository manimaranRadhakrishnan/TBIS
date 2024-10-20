package com.tbis.api.master.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.common.util.MailSendUtil;
import com.tbis.api.master.model.ASNBinTag;
import com.tbis.api.master.model.ASNPartMovement;
import com.tbis.api.master.model.AsnIncidentLog;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.CustomerSoftwares;
import com.tbis.api.master.model.EmailInput;
import com.tbis.api.master.model.GateEntryInput;
import com.tbis.api.master.model.LineSpacePartConfig;
import com.tbis.api.master.model.PackingType;
import com.tbis.api.master.model.SOPContent;
import com.tbis.api.master.model.SOPMaster;
import com.tbis.api.master.model.SOPMenu;
import com.tbis.api.master.model.SOPTOCMaster;
import com.tbis.api.master.model.ScanDetails;
import com.tbis.api.master.model.ScanHeader;
import com.tbis.api.master.model.Sops;

public class SopService {
	
	private static final String insertEsop="INSERT INTO esopmaster (sopname, sopname_local, sopdesc, sopdesc_local, soptypeid, sopsourcetypeid, warehouseid, isactive, created_by, created_date,tsid, sopicon) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), getcurrenttsid(now()), ?);";
	private static final String updateEsop="UPDATE esopmaster SET sopname=?, sopname_local=?, sopdesc=?, sopdesc_local=?, soptypeid=?, sopsourcetypeid=?, warehouseid=?, isactive=?, updated_by=?, updated_date=now(), tsid=getcurrenttsid(now()), sopicon=? WHERE sopid=?;";
	private static final String selectEsop="SELECT sopid, sopname, sopname_local, sopdesc, sopdesc_local, soptypeid, sopsourcetypeid, warehouseid, isactive, sopicon FROM esopmaster where sopid=?;";
	private static final String insertEsopToc="INSERT INTO esoptoc (sopid, soptopicname, soptopicname_local, soptopicparentid, soptopicorder, soptypeid, sopsourcetypeid, sopsourceurl, isactive, created_by, created_date,tsid) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(),getcurrenttsid(now()));";
	private static final String updateEsopToc="UPDATE esoptoc SET sopid=?, soptopicname=?, soptopicname_local=?, soptopicparentid=?, soptopicorder=?, soptypeid=?, sopsourcetypeid=?, sopsourceurl=?, isactive=?, updated_by=?, updated_date= now(), tsid=getcurrenttsid(now()) WHERE soptocid=?;";
	private static final String selectEsopToc="SELECT soptocid, sopid, soptopicname, soptopicname_local, soptopicparentid, soptopicorder, soptypeid, sopsourcetypeid, sopsourceurl, isactive, created_by, created_date, updated_by, updated_date, tsid FROM esoptoc where soptocid=?;";
	
	private static final String ldMenus = "select soptypeid,soptypename,soptypename_local,menuicon from esoptypes where isactive =1 order by soptypeid";
	private static final String ldSops = "select sopid,sopname,sopname_local,sopdesc,sopdesc_local,ifnull(sopicon,'blank.png') sopicon,et.soptypeid,et.soptypename  from esopmaster e inner join esoptypes et on (et.soptypeid=e.soptypeid) where e.soptypeid =?";
	private static final String ldSopContent = "select e.soptocid,e.sopid,e.soptopicname,e.soptopicname_local,e.soptopicparentid,e.soptopicorder,e.soptypeid,e.sopsourcetypeid,e.sopsourceurl,ifnull(m.sopicon,'blank.png') sopicon,sopname  from esoptoc e "
			+ "inner join esopmaster m on (e.sopid=m.sopid) inner join esoptypes et on (et.soptypeid=e.soptypeid) "
			+ "inner join esopsourcetypes st on (e.sopsourcetypeid=st.sopsourcetypeid) where e.soptocid =?";
	
	
	
	public ApiResult<SOPMaster> manageSopMaster(SOPMaster l) {
		if (l.getSopId() == 0) {
			return addSopMaster(l);
		} else {
			return modifySopMaster(l);
		}
	}
	
	public ApiResult<SOPMaster> addSopMaster(SOPMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertEsop,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setString(1, l.getSopName());
			pstmt.setString(2, l.getSopNameLocal());
			pstmt.setString(3, l.getSopDesc());
			pstmt.setString(4, l.getSopDescLocal());
			pstmt.setInt(5, l.getSopTypeid());
			pstmt.setInt(6, l.getSopSourceTypeid());
			pstmt.setInt(7, l.getWarehouseId());
			pstmt.setBoolean(8, l.isActive());
			pstmt.setInt(9, l.getUserId());
			pstmt.setString(10, l.getSopIcon());
			pstmt.executeUpdate();
			rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				code=rs.getString(1);
				l.setSopId(Long.parseLong(code));
			}
			rs.close();
			pstmt.close();
			conn.commit();
			result.isSuccess = true;
			result.message = "SOP Master Added Successfully";
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
	public ApiResult<SOPMaster> modifySopMaster(SOPMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = l.getSopId()+"";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateEsop);
			pstmt.clearParameters();
			pstmt.setString(1, l.getSopName());
			pstmt.setString(2, l.getSopNameLocal());
			pstmt.setString(3, l.getSopDesc());
			pstmt.setString(4, l.getSopDescLocal());
			pstmt.setInt(5, l.getSopTypeid());
			pstmt.setInt(6, l.getSopSourceTypeid());
			pstmt.setInt(7, l.getWarehouseId());
			pstmt.setBoolean(8, l.isActive());
			pstmt.setInt(9, l.getUserId());
			pstmt.setString(10, l.getSopIcon());
			pstmt.setLong(11, l.getSopId());

			pstmt.executeUpdate();
			pstmt.close();
			conn.commit();
			result.isSuccess = true;
			result.message = "SOP Master Modify Successfully";
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

	
	public ApiResult<SOPMaster> getSopMaster(long SopId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<SOPMaster> result = new ApiResult<SOPMaster>();
		SOPMaster l = new SOPMaster();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectEsop);
			pstmt.setLong(1, SopId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setSopId(rs.getLong("sopid"));
				l.setSopTypeid(rs.getInt("soptypeid"));
				l.setSopSourceTypeid(rs.getInt("sopsourcetypeid"));
				l.setWarehouseId(rs.getInt("warehouseid"));
				l.setSopName(rs.getString("sopname"));
				l.setSopNameLocal(rs.getString("sopname_local"));
				l.setSopDesc(rs.getString("sopdesc"));
				l.setSopDescLocal(rs.getString("sopdesc_local"));
				l.setSopIcon(rs.getString("sopicon"));
				l.setActive(rs.getBoolean("isactive"));
			}
			result.isSuccess=true;
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
	
	public ApiResult<SOPTOCMaster> manageTocMaster(SOPTOCMaster l) {
		if (l.getSopTocid() == 0) {
			return addTocMaster(l);
		} else {
			return modifyTocMaster(l);
		}
	}
	
	public ApiResult<SOPTOCMaster> addTocMaster(SOPTOCMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			
			pstmt = conn.prepareStatement(insertEsopToc,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getSopId());
			pstmt.setString(2, l.getSopTopicName());
			pstmt.setString(3, l.getSopTopicNameLocal());
			pstmt.setInt(4, l.getSopTopicParentid());
			pstmt.setInt(5, l.getSopTopicOrder());
			pstmt.setInt(6, l.getSopTypeid());
			pstmt.setInt(7, l.getSopSourceTypeid());
			pstmt.setString(8, l.getSopSourceUrl());
			pstmt.setBoolean(9, l.isActive());
			pstmt.setInt(10, l.getUserId());
			pstmt.executeUpdate();
			rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				code=rs.getString(1);
				l.setSopTocid(Long.parseLong(code));
			}
			rs.close();
			pstmt.close();
			conn.commit();
			result.isSuccess = true;
			result.message = "SOP Topic Added Successfully";
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
	public ApiResult<SOPTOCMaster> modifyTocMaster(SOPTOCMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ApiResult result = new ApiResult();
		String code = l.getSopTocid()+"";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateEsopToc);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getSopId());
			pstmt.setString(2, l.getSopTopicName());
			pstmt.setString(3, l.getSopTopicNameLocal());
			pstmt.setInt(4, l.getSopTopicParentid());
			pstmt.setInt(5, l.getSopTopicOrder());
			pstmt.setInt(6, l.getSopTypeid());
			pstmt.setInt(7, l.getSopSourceTypeid());
			pstmt.setString(8, l.getSopSourceUrl());
			pstmt.setBoolean(9, l.isActive());
			pstmt.setInt(10, l.getUserId());
			pstmt.setLong(11, l.getSopTocid());
			pstmt.executeUpdate();
			pstmt.close();
			conn.commit();
			result.isSuccess = true;
			result.message = "Sop Topic Modify Successfully";
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

	
	
	public ApiResult<SOPTOCMaster> getTocDetail(long TocId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<SOPTOCMaster> result = new ApiResult<SOPTOCMaster>();
		SOPTOCMaster l = new SOPTOCMaster();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectEsopToc);
			pstmt.setLong(1, TocId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setSopTocid(rs.getLong("soptocid"));
				l.setSopId(rs.getLong("sopid"));
				l.setSopTopicParentid(rs.getInt("soptopicparentid"));
				l.setSopTopicOrder(rs.getInt("soptopicorder"));
				l.setSopTypeid(rs.getInt("soptypeid"));
				l.setSopSourceTypeid(rs.getInt("sopsourcetypeid"));
				l.setSopSourceUrl(rs.getString("sopsourceurl"));
				l.setSopTopicName(rs.getString("soptopicname"));
				l.setSopTopicNameLocal(rs.getString("soptopicname_local"));
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
	
	
	public ApiResult<ArrayList<SOPMenu>> getSOPMenus(String languageId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ArrayList<SOPMenu>> result = new ApiResult<ArrayList<SOPMenu>>();
		ArrayList<SOPMenu> sopmenus = new ArrayList<SOPMenu>();
		SOPMenu l = new SOPMenu();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(ldMenus);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				l = new SOPMenu();
				l.setMenuId(rs.getLong("soptypeid"));
				l.setMenuName(rs.getString("soptypename"));
				l.setMenuIcon(rs.getString("menuicon"));
				sopmenus.add(l);
			}
			result.result = sopmenus;
			result.isSuccess=true;
			result.message="Success";
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
	
	public ApiResult<ArrayList<Sops>> getSops(long MenuId,String languageId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ArrayList<Sops>> result = new ApiResult<ArrayList<Sops>>();
		ArrayList<Sops> sops = new ArrayList<Sops>();
		Sops l = new Sops();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(ldSops);
			pstmt.setLong(1, MenuId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				l = new Sops();
				l.setSopId(rs.getLong("sopid"));
				l.setSopTypeid(rs.getInt("soptypeid"));
				l.setSopDiplayype(rs.getString("soptypename"));
				l.setSopTitle(rs.getString("sopname"));
				l.setSopIcon(rs.getString("sopicon"));
				sops.add(l);
			}
			result.result = sops;
			result.isSuccess=true;
			result.message="Success";
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
	
	public ApiResult<SOPContent> getSopContent (long SopId,String languageId){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<SOPContent> result = new ApiResult<SOPContent>();
		SOPContent l = new SOPContent();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(ldSopContent);
			pstmt.setLong(1, SopId);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				l.setTocId(rs.getLong("soptocid"));
				l.setSopId(rs.getLong("sopid"));
				l.setSopTypeid(rs.getInt("customerid"));
				l.setSopIcon(rs.getString("sopicon"));
				l.setSopTitle(rs.getString("sopname"));
				l.setSopTopicTitle(rs.getString("soptopicname"));
				l.setSopTopicTypeid(rs.getInt("sopsourcetypeid"));
				l.setSopParentid(rs.getInt("soptopicparentid"));
				l.setSopTopicorder(rs.getInt("soptopicorder"));
				l.setSopSourceurl(rs.getString("sopsourceurl"));
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
	
	public static SopService getInstance() {
		return new SopService();
	}
}
