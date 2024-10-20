package com.cissol.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDownloadUtil {
	private static String FS = System.getProperty("file.separator");

	public static void downloadFile(OutputStream out, String path, String downloadFileName) throws Exception {
		try {
			String filePath = "";
			if (!downloadFileName.equals("")) {
				if (path.endsWith(FS)) {
					filePath = path + downloadFileName;
				} else {
					filePath = path + FS + downloadFileName;
				}
			} else {
				filePath = path;
			}
			File f = new File(filePath);
			InputStream is = new FileInputStream(f);
			byte[] bytes = new byte[4096];
			while (is.read(bytes, 0, 4096) != -1) {
				out.write(bytes, 0, 4096);
			}
			is.close();
			out.flush();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static void deleteFile(String path, String downloadFileName) throws Exception {
		try {
			String filePath = "";
			if (!downloadFileName.equals("")) {
				if (path.endsWith(FS)) {
					filePath = path + downloadFileName;
				} else {
					filePath = path + FS + downloadFileName;
				}
			} else {
				filePath = path;
			}
			System.out.println(filePath);
			File f = new File(filePath);
			f.delete();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
