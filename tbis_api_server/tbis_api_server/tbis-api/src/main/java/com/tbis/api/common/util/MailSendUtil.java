package com.tbis.api.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import com.cissol.core.mail.SendMailWrapper;
import com.tbis.api.master.model.EmailInput;

public class MailSendUtil {
	
	public static void sendWebsiteEnquiry(Map<String,String> v){
		try{
			SendMailWrapper w=new SendMailWrapper();
			String msg="";
			msg="Dear Team,<br><br>Feedback Received from <br> Name   :"+v.get("name")+"<br> Email :"+v.get("email")+"<br> Phone :"+v.get("phone")+"<br> Message :"+v.get("message");
			w.sendMail("W","feedback@cissol.com",null,null,"Enquiry",msg,"text/html",null,true);
			msg="Hi "+v.get("name")+"<br><br>Thank you for your email.<br><br>We will endeavour to reply to you shortly.<br><br>Please DO NOT reply to this email.<br><br> Thank You<br>CISSOL Team.";

			w.sendMail("W",v.get("email"),null,null,"Thanks for your Enquiry",msg,"text/html",null,true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
//	public static void sendMailNotification(Map<String,String> v){
//		Connection con=null;
//		try{
//			con=DatabaseUtil.getConnection();
//			sendMailNotification(con,v);
//		}catch(Exception er){
//			er.printStackTrace();
//		}finally{
//			try{
//				if(con!=null){
//					con.close();
//				}
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//		}
//	}
	public static void sendMailNotification(Connection con,EmailInput a ){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String toAddress="";
		String compCode="";
		try{
			int configId=a.getConfigId();
			SendMailWrapper w=new SendMailWrapper();
			pstmt=con.prepareStatement("select mailsubject,mailcontent,ifnull(ccemails,'') ccemails from maitemplate where mailtemplateid=?");
			pstmt.clearParameters();
			pstmt.setInt(1,configId);
			rs=pstmt.executeQuery();
			if(rs.next()){
				a.setMailSubject(rs.getString("mailsubject"));
				if("".equals(a.getMailContent())) {
					a.setMailContent(rs.getString("mailcontent"));
				}
				a.setCcAddress(rs.getString("ccemails"));
			}
			System.out.println("==="+configId+"==="+toAddress);
			rs.close();
			pstmt.close();
			replaceValue(con,a);
			toAddress=a.getEmail();
			String ccAddress=a.getCcAddress();
			if(ccAddress!=null && !"".equals(ccAddress)) {
				toAddress=toAddress+","+ccAddress;
			}
			w.sendMail("A",toAddress,null,null,a.getMailSubject(),a.getMailContent(),"text/html",null,true);
			rs=null;
			pstmt=null;
		}catch(Exception er){
			er.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	public static void replaceValue(Connection con,EmailInput v){
		ResultSet rs=null;
		ResultSet getRs=null;
		PreparedStatement pstmt=null;
		try{
			String configField="";
			String fieldFormat="";
			String fieldQuery="";
			pstmt=con.prepareStatement("select config_field_format,config_field_name,config_field_query from mail_config_field where configid=?");
			pstmt.clearParameters();
			pstmt.setInt(1, v.getConfigId());
			getRs=pstmt.executeQuery();
			if(getRs.next()){
				fieldQuery=getRs.getString("config_field_query");
				configField=getRs.getString("config_field_name");
				fieldFormat=getRs.getString("config_field_format");
			}
			getRs.close();
			pstmt.close();
			String code=v.getFilterCode();
			if(fieldQuery!=null && !fieldQuery.equals("") && code!=null && !code.equals("")){
				pstmt=con.prepareStatement(fieldQuery);
				pstmt.clearParameters();
				pstmt.setString(1, code);
				getRs=pstmt.executeQuery();
				if(getRs.next()){
					String field[]=configField.split(",");
					String format[]=fieldFormat.split(",");
					for(int i=0;i<field.length;i++){
						v.setMailContent(v.getMailContent().replace("~"+format[i]+"~", getRs.getString(""+field[i]+"")));
					}
				}
			}
			if(v.getPassword()!=null && !v.getPassword().equals("")) {
				v.setMailContent(v.getMailContent().replace("~Password~", v.getPassword()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
				if(getRs!=null){
					getRs.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
//	public static void sendEnquiryNotification(Map<String,String> v){
//		SendMailWrapper w=new SendMailWrapper();
//		ResultSet rs=null;
//		PreparedStatement pstmt=null;
//		Connection con=null;
//		String attach="";
//		String FS=System.getProperty("file.separator");
//		String compCode="";
//		try{
//			compCode=(String)v.get("compCode");
//			
//			String domainName=CommonUtil.getDomainName("Portal");
//			Map<Object,Object> m=null;
//			if(domainName!=null && !domainName.equals("") && CommonUtil.portalEnabled("Portal")){
//				Client c = ClientBuilder.newClient();
//				WebTarget wt = c.target(domainName+"/eprocureportal/service/supplier/supplieremaillist?&code="+compCode);
//		        String res = wt.request().accept(MediaType.APPLICATION_JSON)
//				.get(String.class);
//		        System.out.println("===Fetch Email List Record Status==="+res);
//		        ObjectMapper obj=new ObjectMapper();
//	        	obj.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
//	        	 m=obj.readValue(res, new TypeReference<Map<Object,Object>>() {});
//			}
//			con=DatabaseUtil.getConnection();
//			String sendToAll=(String)v.get("sendToAll");
//			if(sendToAll!=null && sendToAll.equals("select")){
//				pstmt=con.prepareStatement("select distinct case when c.configvalue=1 then s.alternativeemail else s.email end email,s.gid,s.suppliercode from  suppliercategorymap m  inner join suppliermast s on s.suppliercode=m.suppliercode left join supplierconfig c on s.suppliercode=c.suppliercode and c.configname='useAlternativeMailId' where s.status='Active' and m.categorycode in(?)");
//				pstmt.clearParameters();
//				pstmt.setString(1, v.get("category"));
//			}else{
//				pstmt=con.prepareStatement("select distinct case when c.configvalue=1 then s.alternativeemail else s.email end email,s.gid,s.suppliercode from  suppliermast s  left join supplierconfig c on s.suppliercode=c.suppliercode and c.configname='useAlternativeMailId' where s.status='Active'");
//				pstmt.clearParameters();
//			}
//			rs=pstmt.executeQuery();
//			while (rs.next()){
//				attach=(String)v.get("attach");
//				if(attach!=null && !attach.equals("")){
//					String file=System.getProperty("catalina.home") + FS + ".."+FS+"eprocure"+FS+"web"+FS+"comp_file"+FS+"enquiry"+FS+compCode+FS+attach;
//					//file="E:\\Project\\eprocure\\eprocure\\web\\comp_file\\enquiry\\1\\dishes.txt";
//					System.out.println("===filepath==="+file);
//					w.sendMail("A",getEmail(m,rs.getString("email"),rs.getString("gid")),null,null,v.get("subject"),v.get("content"),"text/html",file,true);
//				}else{
//					w.sendMail("A",getEmail(m,rs.getString("email"),rs.getString("gid")),null,null,v.get("subject"),v.get("content"),"text/html",null,true);	
//				}
//				//w.sendMail(type,toAddresses,ccAddresses,bccAddresses,subject,content,contentType,attachmentFilePath,runAsSeparateThread)
//			}
//			rs.close();
//			pstmt.close();
//			con.close();
//			rs=null;
//			pstmt=null;
//			con=null;
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			try{
//				if(con!=null){
//					con.close();
//				}
//				if (pstmt!=null){
//					pstmt.close();
//				}
//				if(rs!=null){
//					rs.close();
//				}
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
//		}
//	}
//	
//	public static String getEmail(Map<Object,Object> v,String email,String gid){
//		String emailId="";
//		if(gid==null || gid.equals("0") || gid.equals("")){
//			emailId=email;
//		}else if (v!=null){
//			emailId=(String)v.get(gid);
//		}else{
//			emailId=email;
//		}
//		return emailId;
//	}
}
