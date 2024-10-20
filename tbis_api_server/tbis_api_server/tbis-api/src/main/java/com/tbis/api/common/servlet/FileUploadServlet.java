package com.tbis.api.common.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileUploadServlet extends HttpServlet {

	HashMap dataMap = new HashMap();
	private static String FS=System.getProperty("file.separator");
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
                HashMap<String, File> hmresultmap = saveUploadFiles(request, response, parameters);
                String fileName=(String)parameters.get("fileName");
                response.setContentType("text/plain");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
                response.setHeader("Cache-Control", "no-store");
                PrintWriter out = response.getWriter();
                String result=(String)parameters.get("result");
                out.print(result);
                out.close();
            } catch (Exception e) {
                    e.printStackTrace();
		    throw new ServletException(e);
            }
        }
    }
    public static HashMap <String,File> saveUploadFiles(HttpServletRequest request,HttpServletResponse response,HashMap<String,String> parameters) throws ServletException, IOException {
	      HashMap<String,File> h=new HashMap<String,File>();
	      boolean isMultipart = false; //ServletFileUpload.isMultipartContent(request);
			  if (isMultipart){
	       
	            DiskFileItemFactory factory = new DiskFileItemFactory();
				  ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
				  servletFileUpload.setSizeMax(-1);
				  try 
				  {
			         List items =null;// servletFileUpload.parseRequest(request);
			         Iterator iter = items.iterator();
			         FileItem file=null;
			         while (iter.hasNext()) 
					 {
			        	 FileItem item = (FileItem) iter.next();
	                     String fileName = item.getFieldName();
	                     if(!item.isFormField()){
	                       file=item;
	                     }else if(parameters!=null){
	                        parameters.put(item.getFieldName(),item.getString());
	                     }
					 }
			         	InputStream inputStream = null; 
	                    if(file!=null){
	                    	inputStream=file.getInputStream();
	                    	System.out.println("dsfsfdfdgdg"+file.getName());
	  			          	String originalFileName=file.getName();
	  			          	String loginId=(String)request.getSession().getAttribute("LOGIN_ID");
	  			            String groupCode=(String)request.getSession().getAttribute("GroupCode");
	  			          	parameters.put("userId", loginId);
	  			            
	  			          	File f=null;
	  			          	f=new File(System.getProperty("catalina.home") + FS + "../images"+FS+request.getServerName()+FS+"/image_file"+FS+groupCode+FS);
	  			          	System.out.println("====filepath==="+f.getAbsolutePath());
	  			          	f.mkdirs();
		                    File tempFile=null;
		                   	tempFile=new File(f.getAbsolutePath()+FS+originalFileName);
		                   	file.write(tempFile);
		                    h.put(file.getFieldName(), tempFile);
		                     
		                      parameters.put("filePath", f.getAbsolutePath()+FS+ file.getName());
		                      parameters.put("fileName",  file.getName());
		                      
//		                      if (fileType!=null && (fileType.equals("supplierDoc") ||fileType.equals("logo") ||fileType.equals("COMP") ||fileType.equals("Digitalcert"))){
//		                    	  boolean res=updateRecords(parameters,inputStream,tempFile.length());
//		                      }else{
		                    	  parameters.put("result", "File uploaded");
//		                      }
	                    }
					
	            }catch(Exception e){
	            	e.printStackTrace();
	                throw new ServletException(e.getMessage());
	            }
			  } 
	      return h;
	  }
//    private static boolean updateRecords(HashMap<String,String> parameters,InputStream file,long fileSize){
//    	boolean result=false;
//    	Connection conn = null; 
//        String message = null;
//        String fileType="";
//        PreparedStatement statement=null;
//        try {
//        	fileType=parameters.get("fileType");
//            conn=DatabaseUtil.getConnection();
//            conn.setAutoCommit(false);
//            if (fileType!=null && fileType.equals("supplierDoc")){
//	            String sql = "INSERT INTO supplierdocuments (suppliercode,documenttype,documentfile,documentstatus,createdby,createdtime,filename) values (?,?,?,?,?,now(),?)";
//	            statement = conn.prepareStatement(sql);
//	            statement.setString(1, parameters.get("supplierCode"));
//	            statement.setString(2, parameters.get("documentName"));
//	            if (file != null) {
//	                // fetches input stream of the upload file for the blob column
////	                statement.setBinaryStream(3, file,16777216);
//	            	statement.setString(3,parameters.get("filePath"));
//	            }
//	            statement.setString(4, parameters.get("documentStatus"));
//	            statement.setString(5, parameters.get("userId"));
//	            statement.setString(6, parameters.get("fileName"));
//            }else if (fileType!=null && fileType.equals("logo")){
//            	String sql = "UPDATE companymast set logofile=?,logopath=? where code=?";
//	            statement = conn.prepareStatement(sql);
//	            if (file != null) {
//	            	statement.setString(1,parameters.get("fileName"));
//	            }
//	            statement.setString(2,parameters.get("filePath"));
//	            statement.setString(3, parameters.get("companyCode"));
//            }else if (fileType!=null && fileType.equals("Digitalcert")){
//            	ReadCertificates cert=new ReadCertificates();
//            	PublicKey key= cert.readPublicKeyFromCertificate(file);
//            	byte[] keys=key.getEncoded();
//            	//String pk=new String(keys);
//            	String sql = "UPDATE suppliermast set certificate=?,keyalg=? where suppliercode=?";
//	            statement = conn.prepareStatement(sql);
//	            if (file != null) {
//	            	statement.setBytes(1,keys);
//	            	statement.setString(2, key.getAlgorithm());
//	            }
//	            statement.setString(3, parameters.get("supplierCode"));
//            }else if (fileType!=null && fileType.equals("COMP")){
//	            String sql = "INSERT INTO companydocuments (companycode,documenttype,documentfile,documentstatus,createdby,createdtime,filename,allowaccess) values (?,?,?,?,?,now(),?,?)";
//	            statement = conn.prepareStatement(sql);
//	            statement.setString(1, cmpCode);
//	            statement.setString(2, parameters.get("documentName"));
//	            if (file != null) {
//	                // fetches input stream of the upload file for the blob column
////	                statement.setBinaryStream(3, file,16777216);
//	            	statement.setString(3,parameters.get("filePath"));
//	            }
//	            statement.setString(4, parameters.get("documentStatus"));
//	            statement.setString(5, parameters.get("userId"));
//	            statement.setString(6, parameters.get("fileName"));
//	            statement.setString(7, parameters.get("allowAccess"));
//            }
//            // sends the statement to the database server
//            int row = statement.executeUpdate();
//            if (row > 0) {
//            	result=true;
//                message = "File uploaded";
//            }
//            conn.commit();
//        } catch (Exception ex) {
//        	try{
//				if(conn!=null){
//					conn.rollback();
//				}
//			}catch(SQLException esql){
//				esql.printStackTrace();
//			}
//            message = "ERROR: " + ex.getMessage();
//            ex.printStackTrace();
//        } finally {
//        	try {
//	        	if (statement!=null){
//	        		statement.close();
//	        	}
//	            if (conn != null) {
//	            	conn.close();
//	            }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//        parameters.put("result", message);
//    	return result;
//    }
    
}
