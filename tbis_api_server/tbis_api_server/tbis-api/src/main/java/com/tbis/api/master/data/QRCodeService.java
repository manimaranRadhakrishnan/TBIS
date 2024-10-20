package com.tbis.api.master.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.cissol.core.report.WritePDF;
import com.cissol.core.util.DatabaseUtil;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.master.model.PDFPosition;

import jakarta.ws.rs.core.MultivaluedMap;

public class QRCodeService extends WritePDF {
	private static final SimpleDateFormat formatddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat formatyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<HashMap<String, String>> titles = new ArrayList<HashMap<String, String>>();
	private static final String asnPartDetail="SELECT a.asnno,a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,d.spq qty,d.binqty requestedqty ,"
			+ "c.customer_erp_code,sc.tbisscancode,sc.palletno,DATE_FORMAT(a.createddate,'%d-%m-%Y %h:%i %p') asndate "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join asnscancodes sc on sc.asndetailid=d.asndetailid  "
			+ " where a.asnid=? order by sc.palletno,case when p.exclusiveclubno =1 then substring(sc.tbisscancode,41) end";

	private static final String asnPalletDetail="SELECT a.asnno,sum(pal.binqty*p.binqty) qty,sum(pal.binqty) requestedqty ,"
			+ "c.customer_erp_code,pcode.palletscancode,pcode.palletno,DATE_FORMAT(a.createddate,'%d-%m-%Y %h:%i %p') asndate  FROM asnmaster a "
			+ "inner join asndetail d on a.asnid=d.asnid "
			+ "inner join  `part_master` p on p.partid=d.partid "
			+ "inner join customer_master c on c.customerId=a.customerid "
			+ "inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ "inner join asnpalletscancodes pcode on pcode.asnid=a.asnid "
			+ "inner join asnpallets pal on pal.asnid=a.asnid and pal.asndetailid=d.asndetailid "
			+ "and pal.partid=d.partid and pal.palletno=pcode.palletno "
			+ "where a.asnid=? group by a.asnno,c.customer_erp_code,pcode.palletscancode,pcode.palletno,DATE_FORMAT(a.createddate,'%d-%m-%Y %h:%i %p') order by pcode.palletno";

	public void renderTitleForPage() throws Exception {
		createTitle(getPageMarginLeft(), getCurrentYPos(), titles);
		changeYPosBy(5);
	}

	public void writeSimpleDocument(MultivaluedMap<String, String> parameters, OutputStream out) throws Exception {
		Connection con = null;
		try {
			con = DatabaseUtil.getConnection();
			writeSimpleDocument(con, parameters, out);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
	}

	public void writeSimpleDocument(Connection con, MultivaluedMap<String, String> parameters, OutputStream out)
			throws Exception {
		PreparedStatement hdr = null;
		PreparedStatement dtl = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String id=(String)parameters.getFirst("id");
			initializeDocument(0, 0, 0, 0, false, 0, null, PDRectangle.A4);
			ArrayList<PDFPosition> labels=new ArrayList<PDFPosition>();
			int marginLeft=10;
			int marginBottom=5;
			int startX=20;
			int endX=200;			
			int startY=826;
			int endY=691;
			int height=131;
			int width=180;
			
			for(int y=1;y<=6;y++) {
				startX=20;
				endX=200;
				for(int x=1;x<=3;x++) {
					PDFPosition p=new PDFPosition();
					p.startX=startX;
					p.endX=endX;
					p.startY=startY;
					p.endY=endY;
					labels.add(p);
					startX=endX+marginLeft;
					endX=startX+width;
				}
				startY=endY-marginBottom;
				endY=startY-height;
			}
			String fPath=FileUtil.getLogoPath("logo.png");
			PDImageXObject  pdLogoXObject = LosslessFactory.createFromImage(getDocument(), ImageIO.read(new File(fPath)));
			hdr=con.prepareStatement(asnPalletDetail);
			hdr.setString(1,id);
			rs=hdr.executeQuery();
			int i=-1;
			while(rs.next()) {
				i++;
				StringBuffer sb = new StringBuffer ();
				sb.append ( rs.getString("palletscancode") );
				// other lines with the content of qrcode
	
				Map<EncodeHintType, Object> qrParam = new HashMap<EncodeHintType, Object> ();
				qrParam.put ( EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M );
				qrParam.put ( EncodeHintType.CHARACTER_SET, "UTF-8" );
	
				BarcodeQRCode qrcode = new BarcodeQRCode( sb.toString (),33 , 33, qrParam );
				
	            PDImageXObject  pdImageXObject = LosslessFactory.createFromImage(getDocument(), convertToBufferedImage(qrcode.createAwtImage ( Color.BLACK, Color.WHITE )));
				PDFPosition p=labels.get(i);
				drawLine(p.startX, p.startY, p.endX, p.startY);
				drawLine(p.startX, p.endY, p.endX, p.endY);
				drawLine(p.endX, p.startY, p.endX, p.endY);
				drawLine(p.startX, p.startY, p.startX, p.endY);
				getContent().drawImage(pdLogoXObject, p.startX+5, p.endY+80,55,55);
	            getContent().drawImage(pdImageXObject, p.startX+1, p.endY+5,75,75);
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-45);            
				getContent().showText("ASN#:"+rs.getString("asnno"));
				getContent().endText();
	            getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-60);            
				getContent().showText(rs.getString("customer_erp_code"));
				getContent().endText();
//				getContent().beginText();
//				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
//				getContent().newLineAtOffset(p.startX+75,p.startY-45);            
//				getContent().showText(rs.getString("partno"));
//				getContent().endText();
////				createParagraph("",rs.getString("partdescription"), 90,p.startX+75f , p.startY-60f,0, 10);
//				getContent().beginText();
//				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
//				getContent().newLineAtOffset(p.startX+75,p.startY-60);
//				if(rs.getString("partdescription").length()>20) {
//					getContent().showText(rs.getString("partdescription").substring(0,18));
//				}else {
//					getContent().showText(rs.getString("partdescription"));
//				}
//				getContent().endText();
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-75);            
				getContent().showText("Qty: "+rs.getString("requestedqty"));
				getContent().endText();
//				getContent().beginText();
//				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
//				getContent().newLineAtOffset(p.startX+75,p.startY-60);            
//				getContent().showText("SL.No: "+rs.getString("tbisscancode").substring(40));
//				getContent().endText();
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-90);            
				getContent().showText("PAL.No: "+rs.getString("palletno"));
				getContent().endText();
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-105);            
				getContent().showText(rs.getString("asndate"));
				getContent().endText();
				if(i>0 && i%17==0) {
					closeContent();
					createPageAndChangePos();
					i=-1;
				}
			}
			hdr.close();
			rs.close();
			closeContent();
			createPageAndChangePos();
			hdr=con.prepareStatement(asnPartDetail);
			hdr.setString(1,id);
			rs=hdr.executeQuery();
			i=-1;
			while(rs.next()) {
				i++;
				StringBuffer sb = new StringBuffer ();
				sb.append ( rs.getString("tbisscancode") );
				// other lines with the content of qrcode
	
				Map<EncodeHintType, Object> qrParam = new HashMap<EncodeHintType, Object> ();
				qrParam.put ( EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M );
				qrParam.put ( EncodeHintType.CHARACTER_SET, "UTF-8" );
	
				BarcodeQRCode qrcode = new BarcodeQRCode( sb.toString (),33 , 33, qrParam );
				
	            PDImageXObject  pdImageXObject = LosslessFactory.createFromImage(getDocument(), convertToBufferedImage(qrcode.createAwtImage ( Color.BLACK, Color.WHITE )));
				PDFPosition p=labels.get(i);
				drawLine(p.startX, p.startY, p.endX, p.startY);
				drawLine(p.startX, p.endY, p.endX, p.endY);
				drawLine(p.endX, p.startY, p.endX, p.endY);
				drawLine(p.startX, p.startY, p.startX, p.endY);
				getContent().drawImage(pdLogoXObject, p.startX+5, p.endY+80,55,55);
	            getContent().drawImage(pdImageXObject, p.startX+1, p.endY+5,75,75);
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-15);            
				getContent().showText("ASN#:"+rs.getString("asnno"));
				getContent().endText();
	            getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-30);            
				getContent().showText(rs.getString("customer_erp_code"));
				getContent().endText();
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 12);
				getContent().newLineAtOffset(p.startX+75,p.startY-45);            
				getContent().showText(rs.getString("partno"));
				getContent().endText();
//				createParagraph("",rs.getString("partdescription"), 90,p.startX+75f , p.startY-60f,0, 10);
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-60);
				if(rs.getString("partdescription").length()>18) {
					getContent().showText(rs.getString("partdescription").substring(0,18));
				}else {
					getContent().showText(rs.getString("partdescription"));
				}
				getContent().endText();
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-75);            
				getContent().showText("Qty: "+rs.getString("binqty"));
				getContent().endText();
				int slno=Integer.parseInt(rs.getString("tbisscancode").substring(40));
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 12);
				getContent().newLineAtOffset(p.startX+75,p.startY-90);            
				getContent().showText("BIN: "+slno+"/"+rs.getString("requestedqty"));
				getContent().endText();
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-105);            
				getContent().showText("PAL.No: "+rs.getString("palletno"));
				getContent().endText();
				getContent().beginText();
				getContent().setFont(PDType1Font.TIMES_ROMAN, 10);
				getContent().newLineAtOffset(p.startX+75,p.startY-120);            
				getContent().showText(rs.getString("asndate"));
				getContent().endText();
				if(i>0 && i%17==0) {
					closeContent();
					createPageAndChangePos();
					i=-1;
				}
			}
			closeContent();
			saveDocument(out);
			closeDocument();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (hdr != null) {
					hdr.close();
				}
				if (dtl != null) {
					dtl.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (getDocument() != null) {
					closeDocument();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
	}
	public static BufferedImage convertToBufferedImage(Image image)
	{
	    BufferedImage newImage = new BufferedImage(
	        image.getWidth(null), image.getHeight(null),
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
}