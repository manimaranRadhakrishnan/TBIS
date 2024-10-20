package com.cissol.core.report;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.cissol.core.util.DatabaseUtil;

public class WritePDFImpl extends WritePDF {
	public void renderTitleForPage() throws Exception {
		ArrayList<HashMap<String, String>> titles = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> title = new HashMap<String, String>();
		title.put("size", "14");
		title.put("title", "Medical Researh Foundation");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		title.put("font", "b");
		titles.add(title);
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "41/18,College Road, Nungambakkam");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		title.put("font", "b");
		titles.add(title);
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "Chennai-600 006,Tamil nadu,India");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		title.put("font", "b");
		titles.add(title);
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Phone:    +91-44-28271616");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		titles.add(title);
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Fax:         +91-44-28271616");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		titles.add(title);
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Email Id:  commercial@snmail.org");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		titles.add(title);
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Web site:  http://sankaranethralaya.org");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		titles.add(title);
		createTitle(getPageContentMinWidth() + 50, getCurrentYPos(), titles);
		drawImage(0, getCurrentXPos(), getCurrentYPos(), 48, 80);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "12");
		title.put("title", "Order Number:  064359");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		titles.add(title);
		title = new HashMap<String, String>();
		title.put("size", "12");
		title.put("title", "Order Date:    25/02/2014");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "l");
		titles.add(title);
		createTitle(getPageContentMaxWidth() - 150, getPageContentMaxHeight() - 60, titles);
		drawLine(getPageMarginLeft(), getCurrentYPos() - 5, getPageWidth() - getPageMarginRight(),
				getCurrentYPos() - 5);
		changeYPosBy(5);
	}

	public void writeSimpleDocument(OutputStream stream) throws Exception {
		ArrayList<String> imgs = new ArrayList<String>();
		imgs.add("/web/images/comp_logo.png");
		initializeDocument(36, 36, 36, 36, true, 0, imgs, PDRectangle.A4);
		renderTitleForPage();
		ArrayList<HashMap<String, String>> titles = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> title = new HashMap<String, String>();

		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "14");
		title.put("title", "Purchase Order");
		title.put("width", String.valueOf(getContentWidth() - 50));
		title.put("align", "c");
		title.put("font", "b");
		titles.add(title);
		createTitle(getCurrentXPos(), getCurrentYPos(), titles);
		drawLine(getPageMarginLeft(), getCurrentYPos() - 5, getPageWidth() - getPageMarginRight(),
				getCurrentYPos() - 5);
		changeYPosBy(5);

		// vendor name starts
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Vendor Name:");
		title.put("width", String.valueOf((getContentWidth() * 20 / 100)));
		title.put("align", "l");
		titles.add(title);
		createTitle(getCurrentXPos(), getCurrentYPos(), titles);
		float cy = createParagraph("", "HUBRA PHARMACEUTICALS", getContentWidth() * 80 / 100,
				getCurrentXPos() + (getContentWidth() * 20 / 100), getCurrentYPos(), 0, 10);
		setCurrentYPos(cy);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Vendor Code:");
		title.put("width", String.valueOf((getContentWidth() * 20 / 100)));
		title.put("align", "l");
		titles.add(title);
		createTitle(getCurrentXPos(), getCurrentYPos(), titles);
		cy = createParagraph("", "7008011", getContentWidth() * 80 / 100,
				getCurrentXPos() + (getContentWidth() * 20 / 100), getCurrentYPos(), 0, 10);
		setCurrentYPos(cy);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Vendor Address:");
		title.put("width", String.valueOf((getContentWidth() / 20 * 100)));
		title.put("align", "l");
		titles.add(title);
		createTitle(getCurrentXPos(), getCurrentYPos(), titles);
		cy = createParagraph("", "24/1,VENKATESA GRAMMI ST,CHINDTADEIPET,", getContentWidth() * 80 / 100,
				getCurrentXPos() + (getContentWidth() * 20 / 100), getCurrentYPos(), 0, 10);
		setCurrentYPos(cy);
		cy = createParagraph("", "24/1,VENKATESA GRAMMI ST,CHINDTADEIPET,", getContentWidth() * 80 / 100,
				getCurrentXPos() + (getContentWidth() * 20 / 100), getCurrentYPos(), 0, 10);
		setCurrentYPos(cy);
		cy = createParagraph("", "CHENNAI,TN,INDIA 600 002", getContentWidth() * 80 / 100,
				getCurrentXPos() + (getContentWidth() * 20 / 100), getCurrentYPos(), 0, 10);
		setCurrentYPos(cy);

		drawLine(getPageMarginLeft(), getCurrentYPos() - 5, getPageWidth() - getPageMarginRight(),
				getCurrentYPos() - 5);
		changeYPosBy(5);

		// vendor name ends

		/*
		 * ArrayList<String> pTitles=new ArrayList<String>(); pTitles.add("Title1");
		 * /*pTitles.add("2.Title2"); pTitles.add("3.Title3"); pTitles.add("4.Title4");
		 * pTitles.add("5.Title5");
		 */
		/*
		 * ArrayList<String> pTexts=new ArrayList<String>(); pTexts.
		 * add("1.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also1"
		 * ); pTexts.
		 * add("2.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also2"
		 * ); pTexts.
		 * add("3.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also3"
		 * ); pTexts.
		 * add("4.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also4"
		 * ); pTexts.
		 * add("5.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also5"
		 * ); HashMap<String,List<String>> m=new HashMap<String,List<String>>();
		 * m.put("Title1", pTexts); currentYPos=createBulletedList(pTitles, m,
		 * getContentWidth(), currentXPos, currentYPos-600);
		 * currentYPos=createParagraph("**I'm the Title**"
		 * ,"im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also"
		 * , 200f, currentXPos, currentYPos,0);
		 */
		System.out.println(getPageWidth() - getPageMarginLeft() - getPageMarginRight());
		float pageWidth = getPageWidth() - getPageMarginLeft() - getPageMarginRight();
		float snoWidth = pageWidth * 5 / 100;
		float codeWidth = pageWidth * 10 / 100;
		float descWidth = pageWidth * 20 / 100;
		float qtyWidth = pageWidth * 10 / 100;
		float unitWidth = pageWidth * 10 / 100;
		float rateWidth = pageWidth * 10 / 100;
		float discWidth = pageWidth * 10 / 100;
		float taxWidth = pageWidth * 5 / 100;
		float ratWidth = pageWidth * 10 / 100;
		float amtWidth = pageWidth * 10 / 100;
		ArrayList<HashMap<String, String>> c = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(snoWidth));
		c1.put("caption", "SNo");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(codeWidth));
		c1.put("caption", "Item Code");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(descWidth));
		c1.put("caption", "Item Description");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(qtyWidth));
		c1.put("caption", "Quantity");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(unitWidth));
		c1.put("caption", "Unit");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(rateWidth));
		c1.put("caption", "Rate");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(discWidth));
		c1.put("caption", "Discount");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(taxWidth));
		c1.put("caption", "Tax");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(ratWidth));
		c1.put("caption", "Rate After");
		c1.put("caption1", "Tax");
		c1.put("name", "so_name");
		c1.put("align", "l");
		c.add(c1);
		c1 = new HashMap<String, String>();
		c1.put("margin", "2");
		c1.put("width", String.valueOf(amtWidth));
		c1.put("caption", "Amount");
		c1.put("caption1", "(in Rs)");
		c1.put("name", "so_action");
		c1.put("align", "l");
		c.add(c1);
		HashMap<String, String> v = new HashMap<String, String>();
		v.put("so_action", "600");
		cy = createTableHeader(c, 25, getPageWidth() - getPageMarginLeft() - getPageMarginRight(), getPageMarginLeft(),
				getCurrentYPos(), 2);
		setCurrentYPos(cy);
		Connection conn = null;
		conn = DatabaseUtil.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select so_name,so_caption,so_action from screen_operations ");
		cy = createTableContent(c, rs, 25, 2, getPageWidth() - getPageMarginLeft() - getPageMarginRight(),
				getPageMarginLeft(), getCurrentYPos(), 2);
		setCurrentYPos(cy);
		rs.close();
		stmt.close();
		conn.close();
		// cy=createTableFooter(c, v,20,
		// getPageWidth()-getPageMarginLeft()-getPageMarginRight(), getPageMarginLeft(),
		// getCurrentYPos());
		createPageAndChangePos();
		renderTitleForPage();
		cy = getCurrentYPos();
		// left side
		drawLine(pageWidth * 50 / 100, getCurrentYPos(), pageWidth * 50 / 100, getCurrentYPos() - 300);
		changeYPosBy(100);
		drawLine(getPageMarginLeft(), getCurrentYPos(), pageWidth * 50 / 100, getCurrentYPos());
		changeYPosBy(60);
		drawLine(getPageMarginLeft(), getCurrentYPos(), getPageWidth() - getPageMarginRight(), getCurrentYPos());
		float iy = getCurrentYPos();
		changeYPosBy(50);
		drawLine(getPageMarginLeft(), getCurrentYPos(), pageWidth * 50 / 100, getCurrentYPos());
		changeYPosBy(50);
		// drawLine(getPageMarginLeft(), getCurrentYPos(), pageWidth*50/100,
		// getCurrentYPos());
		changeYPosBy(40);
		drawLine(getPageMarginLeft(), getCurrentYPos(), getPageWidth() - getPageMarginRight(), getCurrentYPos());
		// end left side
		setCurrentYPos(cy);
		// right side
		drawLine(pageWidth * 80 / 100, getCurrentYPos(), pageWidth * 80 / 100, iy);
		changeYPosBy(20);
		drawLine(pageWidth * 50 / 100, getCurrentYPos(), getPageWidth() - getPageMarginRight(), getCurrentYPos());
		changeYPosBy(20);
		drawLine(pageWidth * 50 / 100, getCurrentYPos(), getPageWidth() - getPageMarginRight(), getCurrentYPos());
		changeYPosBy(20);
		drawLine(pageWidth * 50 / 100, getCurrentYPos(), getPageWidth() - getPageMarginRight(), getCurrentYPos());
		// end right side
		setCurrentYPos(cy - 15);
		iy = createParagraph("DELIVERY AT",
				"SANKARA NETHRALAYA, 21,PYCROFTS ROAD, NUNGAMBAKKAM, CHENNAI ,TAMILNADU 600 034", pageWidth * 45 / 100,
				getCurrentXPos(), getCurrentYPos(), 0, 8);
		setCurrentYPos(iy - 5);
		iy = createParagraph("DELIVERY INSTRUCTION", "AS AND WHEN REQUIRED", pageWidth * 45 / 100, getCurrentXPos(),
				getCurrentYPos(), 0, 8);
		setCurrentYPos(cy - 115);
		iy = createParagraph("NOTE:", "FOR STORES 3 MONTHS ORDER", pageWidth * 45 / 100, getCurrentXPos(),
				getCurrentYPos(), 0, 8);
		setCurrentYPos(iy - 5);
		iy = createParagraph("ORDER VALIDITY :", " 03/03/2014", pageWidth * 45 / 100, getCurrentXPos(),
				getCurrentYPos(), 0, 8);
		setCurrentYPos(cy - 175);
		// iy=createParagraph("PAYMENT TERMS", "90 DAYS FROM THE DATE OF RECEIPT OF YOUR
		// INVOICE ", pageWidth*45/100, getCurrentXPos(), getCurrentYPos(), 0, 8);
		// setCurrentYPos(cy-225);
		iy = createParagraph("AMOUNT IN WORDS", "Rupees Fifty thousand one hundred and fifty three paise only ",
				pageWidth * 45 / 100, getCurrentXPos(), getCurrentYPos(), 0, 8);
		setCurrentYPos(cy - 225);
		iy = createParagraph("NOTE:", "PURCHASE ORDER NUMBER MUST APPEAR IN ALL YOUR CORRESPONDENCE ",
				pageWidth * 45 / 100, getCurrentXPos(), getCurrentYPos(), 0, 8);
		setCurrentYPos(cy - 5);
		float tmpTitleWidth = ((pageWidth * 80 / 100) - (pageWidth * 50 / 100)) * 50 / 100;
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "DISCOUNT@");
		title.put("width", String.valueOf(tmpTitleWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + 5, getCurrentYPos(), titles);
		setCurrentYPos(cy - 25);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Sub Total");
		title.put("width", String.valueOf(tmpTitleWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + 5, getCurrentYPos(), titles);
		setCurrentYPos(cy - 45);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "TAX@");
		title.put("width", String.valueOf(tmpTitleWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + 5, getCurrentYPos(), titles);
		setCurrentYPos(cy - 65);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "Total In (Rs)");
		title.put("width", String.valueOf(tmpTitleWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + 5, getCurrentYPos(), titles);

		setCurrentYPos(cy - 5);
		float tmpValueWidth = ((pageWidth * 80 / 100) - (pageWidth * 50 / 100)) * 50 / 100;
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "0.00% Rs");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + tmpTitleWidth, getCurrentYPos(), titles);
		setCurrentYPos(cy - 25);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + tmpTitleWidth, getCurrentYPos(), titles);
		setCurrentYPos(cy - 45);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "0.00% Rs");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + tmpTitleWidth, getCurrentYPos(), titles);
		setCurrentYPos(cy - 65);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 50 / 100) + tmpTitleWidth, getCurrentYPos(), titles);

		setCurrentYPos(cy - 5);
		tmpValueWidth = (pageWidth - (pageWidth * 80 / 100));
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "0.00");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 80 / 100) + 5, getCurrentYPos(), titles);
		setCurrentYPos(cy - 25);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "58100.53");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 80 / 100) + 5, getCurrentYPos(), titles);
		setCurrentYPos(cy - 45);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "0.00");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 80 / 100) + 5, getCurrentYPos(), titles);
		setCurrentYPos(cy - 65);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "58100.53");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 80 / 100) + 5, getCurrentYPos(), titles);

		tmpValueWidth = (pageWidth - (pageWidth * 60 / 100));
		setCurrentYPos(cy - 260);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "10");
		title.put("title", "AUTHORISED SIGNATORY");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "r");
		titles.add(title);
		createTitle((pageWidth * 60 / 100) + 5, getCurrentYPos(), titles);

		setCurrentYPos(cy - 300);

		// Terms & Conditions
		ArrayList<String> pTitles = new ArrayList<String>();
		pTitles.add("Terms & Conditions:");
		ArrayList<String> pTexts = new ArrayList<String>();
		pTexts.add(
				"1.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also1");
		pTexts.add(
				"2.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also2");
		pTexts.add(
				"3.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also3");
		pTexts.add(
				"4.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also4");
		pTexts.add(
				"5.im going to print this in the pdf as a multiline text with multiline support. i can do this for the table also5");
		HashMap<String, List<String>> m = new HashMap<String, List<String>>();
		m.put("Terms & Conditions:", pTexts);
		float y = createBulletedList(pTitles, m, getContentWidth(), getCurrentXPos(), getCurrentYPos() - 15, 8);

		// Terms ends
		setCurrentYPos(y);
		drawLine(getPageMarginLeft(), getCurrentYPos() - 5, getPageWidth() - getPageMarginRight(),
				getCurrentYPos() - 5);
		changeYPosBy(5);

		tmpValueWidth = pageWidth * 20 / 100;
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "Prepared User:");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle(getPageMarginLeft() + 5, getCurrentYPos(), titles);

		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "Prepared Date:");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle(getPageMarginLeft() + 5, getCurrentYPos(), titles);

		setCurrentYPos(y - 5);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "dinesh");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 20 / 100) + 5, getCurrentYPos(), titles);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "25/02/2014");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 20 / 100) + 5, getCurrentYPos(), titles);

		setCurrentYPos(y - 5);
		tmpValueWidth = pageWidth * 20 / 100;
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "E & OE");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 40 / 100) + 5, getCurrentYPos(), titles);

		setCurrentYPos(y - 5);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "Printed User:");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 60 / 100) + 5, getCurrentYPos(), titles);

		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "Printed Date:");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 60 / 100) + 5, getCurrentYPos(), titles);

		setCurrentYPos(y - 5);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "dinesh");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 80 / 100) + 5, getCurrentYPos(), titles);
		titles.clear();
		title = new HashMap<String, String>();
		title.put("size", "8");
		title.put("title", "25/02/2014");
		title.put("width", String.valueOf(tmpValueWidth));
		title.put("align", "l");
		titles.add(title);
		createTitle((pageWidth * 80 / 100) + 5, getCurrentYPos(), titles);

		// drawImage(currentXPos+100,currentYPos-100);
		// page number
		closeContent();
		printPageNumbers("ORD12345A");
		saveDocument(stream);
		closeDocument();
	}
}