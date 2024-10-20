package com.tbis.api.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.URL;

import jakarta.xml.bind.DatatypeConverter;

public class FileUtil {
	private static String FS=System.getProperty("file.separator");
	public static void writeToFile(InputStream uploadedInputStream,String uploadedFileLocation) throws IOException{
	       try {
	          	File tempFile=null;
	          	tempFile=new File(uploadedFileLocation);

	          OutputStream out = new FileOutputStream(tempFile);
	          int read = 0;
	          byte[] bytes = new byte[1024];

	          //out = new FileOutputStream(new File(uploadedFileLocation));
	          while ((read = uploadedInputStream.read(bytes)) != -1) {
	             out.write(bytes, 0, read);
	          }
	          out.flush();
	          out.close();
	       } catch (IOException e) {
	          throw e;
	       }
	    }
		public static void writeToFile(byte[] fileData,String uploadedFileLocation) throws IOException{
	       try {
	          	File tempFile=null;
	          	tempFile=new File(uploadedFileLocation);

	          OutputStream out = new FileOutputStream(tempFile);
	          int read = 0;
	          out.write(fileData, 0, fileData.length);
	          out.flush();
	          out.close();
	       } catch (IOException e) {
	          throw e;
	       }
	    }
		
	public static String getFilePath(String fileType,String subType,String id) {
		String path="";
    	File f=null;
    	String fPath=System.getProperty("catalina.home") + FS + "../documents"+FS+fileType+FS+subType;
    	if(!"".equals(id)) {
    		fPath=fPath+FS+id;
    	}    	
    	f=new File(fPath);
    	System.out.println("====filepath==="+f.getAbsolutePath());
    	f.mkdirs();
    	path=f.getAbsolutePath()+FS;
		return path;
	}
	
	public static String getSopFilePath(String fileType,String subType,Integer id) {
		String path="";
    	File f=null;
    	String fPath=System.getProperty("catalina.home") + FS + "../documents"+FS+fileType+FS+subType;

		System.out.println(":::id::: " + id);
		
    	if(!"".equals(id)) {
    		System.out.println(":::id111111::: " + id);
    		if(id==1) {
				fPath=fPath+FS+"video";
				System.out.println(":::fPath1::: " + fPath);
			}else if(id==2) {
				fPath=fPath+FS+"pdf";
				System.out.println(":::fPath2::: " + fPath);
			}else if(id==3) {
				fPath=fPath+FS+"ppt";
				System.out.println(":::fPath3::: " + fPath);
			}
    	}    	
    	f=new File(fPath);
    	System.out.println("====filepath==="+f.getAbsolutePath());
    	f.mkdirs();
    	path=f.getAbsolutePath()+FS;
		return path;
	}
	
	public static String getLogoPath(String fileName) {
		String path="";
    	File f=null;
    	String fPath=System.getProperty("catalina.home") + FS + "../tbis"+FS+"web"+FS+"images";
    	path=fPath+FS+fileName;
		return path;
	}
	public static String readFileAsBase64(String path) {
		String base64 = "";
		try {
			base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(
				    Paths.get(path)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return base64;
	}
	
	public static void downloadUsingStream(String fileName,String fileType,String sopId,OutputStream out) throws IOException{
		String contentFolder="video";
		
		if(fileType.equals("1")) {
			contentFolder="video";
		}else if(fileType.equals("2")) {
			contentFolder="pdf";
		}else if(fileType.equals("3")) {
			contentFolder="ppt";
		}
		String path =getFilePath("esop",sopId,contentFolder)
				;

//		File modelFile = new File(path+FS+fileName);
//		if(!modelFile.exists()) {
//			download(path,modelFile);
//		}
        BufferedOutputStream bis = new BufferedOutputStream(out);
        FileInputStream fis = new FileInputStream(path+FS+fileName);
        byte[] buffer = new byte[4096];
        int count=0;
        while((count = fis.read(buffer,0,4096)) != -1)
        {
            bis.write(buffer, 0, count);
        }
        fis.close();
        bis.flush();
//        bis.close();
    }
	
	public static String getMediaPath(String fileName,String subType,String id) {
		String path="";
    	File f=null;
    	String fPath=System.getProperty("catalina.home") + FS + "../documents"+FS+"esop"+FS+id+FS+subType;
    	path=fPath+FS+fileName;
    	System.out.println("====filepath==="+path);
    	
		return path;
	}
	
	public static void download(String url, File destination) throws IOException {
	    URL website = new URL(url);
	    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
	    FileOutputStream fos = new FileOutputStream(destination);
	    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}
}
