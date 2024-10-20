package com.cissol.core.filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cissol.core.servlet.InitProjectServlet;
import com.cissol.core.util.DatabaseUtil;
import com.tomcat.auth.*;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		// Get the IP address of client machine.
		String ipAddress = request.getRemoteAddr();
		String host = request.getRemoteHost();
		String loginId = (String) request.getSession().getAttribute("LOGIN_ID");
		String userToken = "";
		boolean authToken = false;
		boolean isAuthenticated = true;
		if (loginId == null || "".equals(loginId)) {
			loginId = request.getRemoteUser();
			if (loginId == null) {
				loginId = "";
				userToken = request.getHeader("Authorization");
				if (userToken == null) {
					userToken = "";
				} else {
					userToken = userToken.replace("Bearer ", "");
				}
				System.out.println("Token: " + userToken);
			}
			request.getSession().setAttribute("User", loginId);
			request.getSession().setAttribute("PORTALCODE", "1");
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement ps = null;
			String loginType = "";
			String userCode = "";
			try {
				host = (String) request.getHeader("host");
				host = host.replaceAll(":8080", "");
				con = DatabaseUtil.getConnection();
				ps = con.prepareStatement("select user_name,user_id,user_displayname from users where "
						+ InitProjectServlet.userIdField + "=?");
				ps.clearParameters();
				ps.setString(1, userToken);
				rs = ps.executeQuery();
				if (rs.next()) {
					request.getSession().setAttribute("LOGIN_ID", rs.getString("user_name"));
					request.getSession().setAttribute("USER_ID", rs.getString("user_id"));
					request.getSession().setAttribute("USER_NAME", rs.getString("user_displayname"));
				}
				rs.close();
				ps.close();

				ps = con.prepareStatement("select em_name,em_code from employee_master where em_user_name=?");
				ps.clearParameters();
				ps.setString(1, loginId);
				rs = ps.executeQuery();
				if (rs.next()) {
					request.getSession().setAttribute("USER_ID", rs.getString("em_code"));
					request.getSession().setAttribute("USER_NAME", rs.getString("em_name"));
				}
				rs.close();
				ps.close();				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (con != null) {
						con.close();
					}
					if (rs != null) {
						rs.close();
					}
					if (ps != null) {
						ps.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} 
		try {
			if (isAuthenticated)
				chain.doFilter(req, res);
			else
				((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized");
			return;
		} finally {
		}
	}

	public void init(FilterConfig config) throws ServletException {

	}

	public void destroy() {
		// add code to release any resource
	}
}
