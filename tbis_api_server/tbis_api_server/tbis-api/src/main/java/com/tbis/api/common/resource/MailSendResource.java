package com.tbis.api.common.resource;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbis.api.common.util.MailSendUtil;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;


@Path("mailsend")
public class MailSendResource {
	
    @POST
    @Produces("text/plain")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String sendMail(MultivaluedMap<String, String> formParams,@Context final UriInfo  uriInfo) {
    	String result="Your message has been successfully sent to us";
    	try{
    	ObjectMapper obj=new ObjectMapper();
    	Map<String,Object> m=obj.readValue(formParams.getFirst("data"), new TypeReference<Map<String, Object>>() {});
    	System.out.println(" "+m.get("savedata"));
    	MailSendUtil a=new MailSendUtil();
    	a.sendWebsiteEnquiry((Map)m.get("savedata"));
    	}catch(Exception e){
    		result="STOP! Message not sent";
    		e.printStackTrace();
    	}
    	return result;
    }
    
}