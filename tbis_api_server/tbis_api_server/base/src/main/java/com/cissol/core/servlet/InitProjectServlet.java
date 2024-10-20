package com.cissol.core.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.cissol.core.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InitProjectServlet extends HttpServlet {
	private static String FS = System.getProperty("file.separator");
	private static String appPath = System.getProperty("app.path");

	public static String context = "";
	public static boolean isMultiTenant = false;
	public static boolean checkExpiry = false;
	public static boolean isRegistration = false;
	public static String userIdField = "user_id";
	public static boolean smartBro = false;
	public static Properties prjConfig = new Properties();

	@Override
	public void init() throws ServletException {
		context = getInitParameter("context");
		try {
			loadProjectConfig();
			isMultiTenant = Boolean.parseBoolean(prjConfig.getProperty("multi_tenant_enabled"));
			checkExpiry = Boolean.parseBoolean(prjConfig.getProperty("check_expiry"));
			isRegistration = Boolean.parseBoolean(prjConfig.getProperty("is_registered_users"));
			userIdField = prjConfig.getProperty("user_id_field");
			smartBro = Boolean.parseBoolean(prjConfig.getProperty("smart_bro"));
			DatabaseUtil.databaseType = Integer.parseInt(prjConfig.getProperty("database_type"));
			DatabaseUtil.driverName = prjConfig.getProperty("driver_name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.init();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	private void loadProjectConfig() throws Exception {
		String realPath = this.getServletContext().getRealPath("");
		String prjConfigPath = realPath + FS + ".." + FS + "config" + FS + "project_config.ini";
		prjConfig.load(new FileInputStream(prjConfigPath));
	}
}