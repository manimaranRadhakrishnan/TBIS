package com.cissol.core.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileUploadUtil {

	private static String FS = System.getProperty("file.separator");

	public static HashMap<String, File> saveUploadFiles(HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> parameters, String path, String importFileName)
			throws ServletException, IOException {
		HashMap<String, File> h = new HashMap<String, File>();
		boolean isMultipart = false;//// ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {

			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
			servletFileUpload.setSizeMax(-1);
			try {
				List items = null; //// servletFileUpload.parseRequest(request);
				Iterator iter = items.iterator();
				FileItem file = null;
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					String fileName = item.getFieldName();
					if (!item.isFormField()) {
						file = item;
					} else if (parameters != null) {
						parameters.put(item.getFieldName(), item.getString());
					}
				}
				if (file != null) {
					if ("".equals(importFileName)) {
						importFileName = file.getName();
					}
					File tempFile = null;
					System.out.println(" Uploaded file name " + importFileName);
					if (!"".equals(path)) {
						tempFile = new File(path + File.separator + importFileName);
					} else {
						tempFile = new File(importFileName);
					}
					file.write(tempFile);
					h.put(file.getFieldName(), tempFile);
				}
			} catch (Exception e) {
				throw new ServletException(e.getMessage());
			}
		}
		return h;
	}
}
