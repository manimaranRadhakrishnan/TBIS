package com.cissol.core.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cissol.core.util.FileUploadUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileUploadServlet extends HttpServlet {

	HashMap dataMap = new HashMap();
	private static String FS = System.getProperty("file.separator");

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	public static final Object syncLock = new Object();

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		synchronized (FileUploadServlet.syncLock) {
			HashMap<String, String> parameters = new HashMap<String, String>();
			try {
				processRequest(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				HashMap<String, File> hmresultmap = FileUploadUtil.saveUploadFiles(request, response, parameters, "",
						"");
				String fileName = (String) parameters.get("fileName");
				response.setContentType("text/plain");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "no-store");
				PrintWriter out = response.getWriter();
				out.print("File Uploaded");
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e);
			}
		}
	}
}
