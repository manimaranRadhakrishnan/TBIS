package com.cissol.core.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cissol.core.util.DatabaseUtil;

public class SendMailWrapper {
	private String mailConfig = "select mc_type,mc_port,mc_host,mc_isauth,mc_fromaddress,mc_password from mail_config where mc_type=?";

	public void sendMail(String type, String toAddresses, String ccAddresses, String bccAddresses, String subject,
			String content, String contentType, String attachmentFilePath, boolean runAsSeparateThread) {
		Connection con = null;
		try {
			con = DatabaseUtil.getConnection();
			sendMail(con, type, toAddresses, ccAddresses, bccAddresses, subject, content, contentType,
					attachmentFilePath, runAsSeparateThread);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
	}

	public void sendMail(Connection con, String type, String toAddresses, String ccAddresses, String bccAddresses,
			String subject, String content, String contentType, String attachmentFilePath,
			boolean runAsSeparateThread) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(mailConfig);
			pstmt.setString(1, type);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				PushMail pm = new PushMail();
				pm.setAttachmentFilePath(attachmentFilePath);
				pm.setContent(content);
				pm.setContentType(contentType);
				pm.setSubject(subject);
				pm.setToAddresses(toAddresses);
				pm.setCcAddresses(ccAddresses);
				pm.setBccAddresses(bccAddresses);
				pm.setHost(rs.getString("mc_host"));
				pm.setType(type);
				pm.setPort(rs.getString("mc_port"));
				pm.setFromAddress(rs.getString("mc_fromaddress"));
				pm.setPassword(rs.getString("mc_password"));
				pm.setIsAuth(rs.getInt("mc_isauth") == 1 ? "true" : "false");
				if (runAsSeparateThread) {
					Thread t = new Thread(pm);
					t.start();
				} else {
					pm.sendMail();
				}
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
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
	}
}