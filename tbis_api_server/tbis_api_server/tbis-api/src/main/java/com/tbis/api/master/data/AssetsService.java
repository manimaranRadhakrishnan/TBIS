package com.tbis.api.master.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.AssetsModel;

public class AssetsService {
	
	private static final String getAsset="select * from assets a where a.type = ?";
	
	public static AssetsService getInstance() {
		return new AssetsService();
	}
	
	public  ApiResult<AssetsModel> getImage(int type) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AssetsModel> result = new ApiResult<AssetsModel>();
		AssetsModel asset = new AssetsModel();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(getAsset);
			pstmt.setInt(1, type);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				asset.setName(rs.getString("name"));
				result.isSuccess=true;
				result.result = asset;
			}else {
				result.isSuccess=false;
				result.result = null;
				result.message="Something went wrong";
			}
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

}
