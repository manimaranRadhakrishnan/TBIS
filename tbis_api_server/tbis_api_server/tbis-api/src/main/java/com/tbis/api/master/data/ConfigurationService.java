package com.tbis.api.master.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.CardMaster;
import com.tbis.api.master.model.MailConfigs;
import com.tbis.api.master.model.WmsColorCode;
import com.tbis.api.master.model.WmsConfiguration;

public class ConfigurationService {
	private static final String insertQuery = "INSERT INTO wms_configuration(configuration_name,configuration_value,configuration_value_type, issystem,update_by, update_time, tsid)VALUES(?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyQuery = "UPDATE wms_configuration set configuration_value=?, update_by=?,update_time=now(),tsid=getcurrenttsid(now()) where config_id=?;";
	private static final String select = "SELECT config_id, configuration_name, configuration_value, configuration_value_type  FROM wms_configuration where config_id=?;";

	/* Color Code */
	private static final String insertColorQuery = "INSERT INTO wms_colorcode(colorname, issystem, isactive, created_by, created_time, tsid)VALUES(?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyColorQuery = "UPDATE wms_colorcode set colorname=?, issystem=?,isactive=?,update_by=?,update_time=now(),tsid=getcurrenttsid(now()) where colorcodeid=?;";
	private static final String selectColor = "SELECT colorcodeid, colorname, issystem, isactive FROM wms_colorcode where colorcodeid=?;";

	
	/* Card Configuration */
	private static final String insertCardQuery = "INSERT INTO cardmaster (cardname, carddatalength, partnostart, vendorstart, userlocationstart, qtystart, slnostart, created_by, created_date, tsid) VALUES (?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyCardQuery = "UPDATE cardmaster set cardname=?, carddatalength=?, partnostart=?, vendorstart=?, userlocationstart=?, qtystart=?, slnostart=?, updated_by=?, updated_date=now(),tsid=getcurrenttsid(now()) where cardid=?;";
	private static final String selectCard = "SELECT cardid, cardname, carddatalength, partnostart, vendorstart, userlocationstart, qtystart, slnostart FROM cardmaster where cardid=?;";

	/* Mail Template */
	private static final String insertMailTemplateQuery = "INSERT INTO maitemplate(mailtemplatename, mailsubject, mailcontent, adddata,isactive, created_by, created_date, tsid) VALUES (?,?,?,?,?,?,now(),getcurrenttsid(now()));";
	private static final String modifyMailTemplateQuery = "Update maitemplate set mailtemplatename=?, mailsubject=?, mailcontent=?, adddata=?,isactive=? ,updated_by=?, updated_date=now(),tsid=getcurrenttsid(now()) where mailtemplateId=?;";
	private static final String selectMailTemplate = "select mailtemplatename, mailsubject, mailcontent, adddata,isactive from maitemplate where mailtemplateId=?;";
	
	public ApiResult<WmsConfiguration> manageConfiguration(WmsConfiguration l) {
		if (l.getConfigId() == 0) {
			return addConfiguration(l);
		} else {
			return modifyConfiguration(l);
		}
	}
	public ApiResult<WmsConfiguration> addConfiguration(WmsConfiguration l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getConfigurationName());
			pstmt.setString(2, l.getConfigurationValue());
			pstmt.setInt(3, 1);
			pstmt.setBoolean(4, true);
			pstmt.setInt(5, l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Configuration Added successfully";
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
	public ApiResult<WmsConfiguration> modifyConfiguration(WmsConfiguration l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getConfigurationValue());
			pstmt.setInt(2, l.getUserId());
			pstmt.setInt(3, l.getConfigId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Configuration Updated successfully";
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
	public ApiResult<WmsConfiguration> getConfiguration(int configId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<WmsConfiguration> result = new ApiResult<WmsConfiguration>();
		WmsConfiguration l = new WmsConfiguration();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(select);
			pstmt.setInt(1, configId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setConfigId(rs.getInt("config_id"));
				l.setConfigurationName(rs.getString("configuration_name"));
				l.setConfigurationValue(rs.getString("configuration_value"));
				l.setConfigurationValueType(1);
				l.setSystem(true);
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
	
	/* Card Configuration */
	public ApiResult<CardMaster> manageCardMaster(CardMaster l) {
		if (l.getCardId() == 0) {
			return addCardMaster(l);
		} else {
			return modifyCardMaster(l);
		}
	}
	public ApiResult<CardMaster> addCardMaster(CardMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertCardQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCardName());
			pstmt.setInt(2, l.getCardDataLength());
			pstmt.setString(3, l.getPartNoStart());
			pstmt.setString(4, l.getVendorStart());
			pstmt.setString(5, l.getQtyStart());
			pstmt.setString(6, l.getUserLocationStart());
			pstmt.setString(7, l.getSlNoStart());
			pstmt.setInt(8,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Card Master added successfully";
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
	public ApiResult<CardMaster> modifyCardMaster(CardMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyCardQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getCardName());
			pstmt.setInt(2, l.getCardDataLength());
			pstmt.setString(3, l.getPartNoStart());
			pstmt.setString(4, l.getVendorStart());
			pstmt.setString(5, l.getQtyStart());
			pstmt.setString(6, l.getUserLocationStart());
			pstmt.setString(7, l.getSlNoStart());
			pstmt.setInt(8,l.getUserId());
			pstmt.setInt(9, l.getCardId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Card Master modified successfully";
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
	public ApiResult<CardMaster> getCardMaster(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<CardMaster> result = new ApiResult<CardMaster>();
		CardMaster l = new CardMaster();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectCard);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setCardId(rs.getInt("cardid"));
				l.setCardName(rs.getString("cardname"));
				l.setCardDataLength(rs.getInt("carddatalength"));
				l.setPartNoStart(rs.getString("partnostart"));
				l.setVendorStart(rs.getString("vendorstart"));
				l.setQtyStart(rs.getString("userlocationstart"));
				l.setUserLocationStart(rs.getString("qtystart"));
				l.setSlNoStart(rs.getString("slnostart"));
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

	/* Color Code*/
	
	public ApiResult<WmsColorCode> manageColorCode(WmsColorCode l) {
		if (l.getColorCodeId() == 0) {
			return addColorCode(l);
		} else {
			return modifyColorCode(l);
		}
	}
	public ApiResult<WmsColorCode> addColorCode(WmsColorCode l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertColorQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getColorName());
			pstmt.setBoolean(2, l.isSystem());
			pstmt.setBoolean(3, l.isActive());
			pstmt.setInt(4, l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Color code added successfully";
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
	public ApiResult<WmsColorCode> modifyColorCode(WmsColorCode l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyColorQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getColorName());
			pstmt.setBoolean(2, l.isSystem());
			pstmt.setBoolean(3, l.isActive());
			pstmt.setInt(4, l.getUserId());
			pstmt.setInt(5, l.getColorCodeId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Color code modified successfully";
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
	public ApiResult<WmsColorCode> getColorCode(int WmsColorCodeId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<WmsColorCode> result = new ApiResult<WmsColorCode>();
		WmsColorCode l = new WmsColorCode();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectColor);
			pstmt.setInt(1, WmsColorCodeId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setColorCodeId(rs.getInt("colorCodeId"));
				l.setColorName(rs.getString("colorName"));
				l.setSystem(rs.getBoolean("isSystem"));
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

	/*Mail Template*/
	public ApiResult<MailConfigs> manageMailConfig(MailConfigs l) {
		if (l.getMailTemplateId() == 0) {
			return addMailConfig(l);
		} else {
			return modifyMailConfig(l);
		}
	}
	public ApiResult<MailConfigs> addMailConfig(MailConfigs l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertMailTemplateQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getMailTemplatename());
			pstmt.setString(2, l.getMailSubject());
			pstmt.setString(3, l.getMailContent());
			pstmt.setInt(4, l.getAttachData());
			pstmt.setBoolean(5, l.getIsActive());
			pstmt.setInt(6,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Mail Settings added successfully";
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
	public ApiResult<MailConfigs> modifyMailConfig(MailConfigs l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(modifyMailTemplateQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getMailTemplatename());
			pstmt.setString(2, l.getMailSubject());
			pstmt.setString(3, l.getMailContent());
			pstmt.setInt(4, l.getAttachData());
			pstmt.setBoolean(5, l.getIsActive());
			pstmt.setInt(6,l.getUserId());
			pstmt.setInt(7, l.getMailTemplateId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Mail Template modified successfully";
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
	public ApiResult<MailConfigs> getMailConfig(int code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<MailConfigs> result = new ApiResult<MailConfigs>();
		MailConfigs l = new MailConfigs();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(selectMailTemplate);
			pstmt.setInt(1, code);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setMailTemplateId(code);
				l.setMailTemplatename(rs.getString("mailtemplatename"));
				l.setMailSubject(rs.getString("mailsubject"));
				l.setMailContent(rs.getString("mailcontent"));
				l.setAttachData(rs.getInt("adddata"));
				l.setIsActive(rs.getBoolean("isactive"));
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

	public static ConfigurationService getInstance() {
		return new ConfigurationService();
	}
}
