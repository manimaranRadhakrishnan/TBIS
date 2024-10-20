package com.cissol.core.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.cissol.core.util.FileDownloadUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String FS = System.getProperty("file.separator");

	public static final Object syncLock = new Object();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		synchronized (FileUploadServlet.syncLock) {
			try {
				String filePath = System.getProperty("catalina.home") + File.separator + "../"
						+ InitProjectServlet.context + File.separator + request.getParameter("filePath");
				String fileName = (String) request.getParameter("fileName");
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "no-store");
				OutputStream out = response.getOutputStream();
				FileDownloadUtil.downloadFile(out, filePath, fileName);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServletException(e);
			}
		}
	}
}
