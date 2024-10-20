package com.cissol.core.report;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
//import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
//import org.apache.pdfbox.pdmodel.graphics.PDGraphicsState;

import com.cissol.core.servlet.InitProjectServlet;
import com.cissol.core.util.DatabaseUtil;

abstract public class WritePDF {
	private static final SimpleDateFormat formatddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat formatddMMyyyyHH = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final DecimalFormat formatNumber = new DecimalFormat("####.00");
	private static final DecimalFormat formatInteger = new DecimalFormat("####");

	PDDocument document = null;
	float pageHeight = 0;
	float pageWidth = 0;
	float pageMarginLeft = 36;
	float pageMarginRight = 36;
	float pageMarginTop = 36;
	float pageMarginBottom = 36;
	float pageContentMaxHeight = 0;
	float pageContentMaxWidth = 0;
	float pageContentMinHeight = 0;
	float pageContentMinWidth = 0;
	PDPage currentPage = null;
	boolean border = false;
	float borderWidth = 15;
	PDPageContentStream content = null;
	float currentXPos = 0;
	float currentYPos = 0;
	boolean isPrintingTable = false;
	boolean continueParagraph = false;
	List<PDXObject> pdImages = new ArrayList<PDXObject>();
	PDRectangle pageSize = null;
	boolean useContext = true;

	public void setUseContext(boolean useContext) {
		this.useContext = useContext;
	}

	public PDDocument getDocument() {
		return document;
	}

	public void setDocument(PDDocument document) {
		this.document = document;
	}

	public float getPageHeight() {
		return pageHeight;
	}

	public void setPageHeight(float pageHeight) {
		this.pageHeight = pageHeight;
	}

	public float getPageWidth() {
		return pageWidth;
	}

	public void setPageWidth(float pageWidth) {
		this.pageWidth = pageWidth;
	}

	public float getPageMarginLeft() {
		return pageMarginLeft;
	}

	public void setPageMarginLeft(float pageMarginLeft) {
		this.pageMarginLeft = pageMarginLeft;
	}

	public float getPageMarginRight() {
		return pageMarginRight;
	}

	public void setPageMarginRight(float pageMarginRight) {
		this.pageMarginRight = pageMarginRight;
	}

	public float getPageMarginTop() {
		return pageMarginTop;
	}

	public void setPageMarginTop(float pageMarginTop) {
		this.pageMarginTop = pageMarginTop;
	}

	public float getPageMarginBottom() {
		return pageMarginBottom;
	}

	public void setPageMarginBottom(float pageMarginBottom) {
		this.pageMarginBottom = pageMarginBottom;
	}

	public float getPageContentMaxHeight() {
		return pageContentMaxHeight;
	}

	public void setPageContentMaxHeight(float pageContentMaxHeight) {
		this.pageContentMaxHeight = pageContentMaxHeight;
	}

	public float getPageContentMaxWidth() {
		return pageContentMaxWidth;
	}

	public void setPageContentMaxWidth(float pageContentMaxWidth) {
		this.pageContentMaxWidth = pageContentMaxWidth;
	}

	public float getPageContentMinHeight() {
		return pageContentMinHeight;
	}

	public void setPageContentMinHeight(float pageContentMinHeight) {
		this.pageContentMinHeight = pageContentMinHeight;
	}

	public float getPageContentMinWidth() {
		return pageContentMinWidth;
	}

	public void setPageContentMinWidth(float pageContentMinWidth) {
		this.pageContentMinWidth = pageContentMinWidth;
	}

	public float getCurrentXPos() {
		return currentXPos;
	}

	public void setCurrentXPos(float currentXPos) {
		this.currentXPos = currentXPos;
	}

	public float getCurrentYPos() {
		return currentYPos;
	}

	public void setCurrentYPos(float currentYPos) {
		this.currentYPos = currentYPos;
	}

	public boolean isPrintingTable() {
		return isPrintingTable;
	}

	public void setPrintingTable(boolean isPrintingTable) {
		this.isPrintingTable = isPrintingTable;
	}

	public List<PDXObject> getPdImages() {
		return pdImages;
	}

	public void setPdImages(List<PDXObject> pdImages) {
		this.pdImages = pdImages;
	}

	public PDPage getCurrentPage() {
		return currentPage;
	}

	public boolean isBorder() {
		return border;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public boolean isContinueParagraph() {
		return continueParagraph;
	}

	public void setContent(PDPageContentStream content) {
		this.content = content;
	}

	public void initializeDocument(float marginLeft, float marginRight, float marginTop, float marginBottom,
			boolean border, float bWidth, List<String> imageList, PDRectangle pageSize) throws Exception {
		document = new PDDocument();
		borderWidth = bWidth;
		this.border = border;
		pageMarginBottom = marginBottom;
		pageMarginTop = marginTop;
		pageMarginRight = marginRight;
		pageMarginLeft = marginLeft;
		this.pageSize = pageSize;
		getImages(imageList);
		PDPage p = createNewPage();
		pageHeight = p.getMediaBox().getHeight();
		pageWidth = p.getMediaBox().getWidth();
		pageContentMaxHeight = pageHeight - marginTop;
		pageContentMaxWidth = pageWidth - marginRight;
		pageContentMinWidth = marginLeft;
		pageContentMinHeight = marginBottom;
		if (border) {
			if (borderWidth >= 15) {
				pageContentMaxHeight -= bWidth;
				pageContentMinHeight += bWidth;
				pageContentMaxWidth -= bWidth;
				pageContentMinWidth += bWidth;
			} else {
				pageContentMaxHeight -= 5;
				pageContentMinHeight += 5;
				pageContentMaxWidth -= 5;
				pageContentMinWidth += 5;
			}
		}
		currentXPos = pageContentMinWidth;
		currentYPos = pageContentMaxHeight;
		currentPage = p;
		document.addPage(p);
	}

	public PDPage createNewPage() throws Exception {
		PDPage page = new PDPage(pageSize);
		PDFont f = PDType1Font.TIMES_BOLD;
		PDRectangle rect = page.getMediaBox();
		content = new PDPageContentStream(document, page);
		if (border) {
			if (borderWidth != 0) {
				content.setLineWidth(borderWidth);
			}
			content.drawLine(pageMarginLeft, rect.getHeight() - pageMarginTop, rect.getWidth() - pageMarginRight,
					rect.getHeight() - pageMarginTop);
			content.drawLine(pageMarginLeft, rect.getHeight() - pageMarginTop, pageMarginLeft, pageMarginBottom);
			content.drawLine(pageMarginLeft, pageMarginBottom, rect.getWidth() - pageMarginRight, pageMarginBottom);
			content.drawLine(rect.getWidth() - pageMarginRight, pageMarginBottom, rect.getWidth() - pageMarginRight,
					rect.getHeight() - pageMarginTop);
		}
		return page;
	}

	public abstract void renderTitleForPage() throws Exception;

	public void createPageAndChangePos() throws Exception {
		content.close();
		PDPage p = createNewPage();
		currentPage = p;
		document.addPage(p);
		currentXPos = pageContentMinWidth;
		currentYPos = pageContentMaxHeight;
	}

	public void createTitle(float startXPos, float startYPos, ArrayList<HashMap<String, String>> titles)
			throws Exception {
		int s = titles.size();
		for (int i = 0; i < s; i++) {
			HashMap<String, String> m = titles.get(i);
			int fontSize = Integer.parseInt(m.get("size"));
			String align = m.get("align");
			String title = m.get("title");
			String ft = m.get("font");
			PDFont font = null;
			if (ft != null && "b".equals(ft)) {
				font = PDType1Font.TIMES_BOLD;
			} else {
				font = PDType1Font.TIMES_ROMAN;
			}
			float width = Float.parseFloat(m.get("width"));
			float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
			float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
			float x = startXPos + ((width - titleWidth) / 2);
			if ("r".equals(align)) {
				x = startXPos + width - titleWidth;
			} else if ("l".equals(align)) {
				x = startXPos;
			}
			content.beginText();
			content.setFont(font, fontSize);
			content.moveTextPositionByAmount(x, startYPos - titleHeight);
			content.drawString(title);
			content.endText();
			startYPos -= titleHeight;
		}
		currentYPos = startYPos;
	}

	public void createFooter() {

	}

	public void createBody() {

	}

	public void createTable() {

	}

	public float createParagraph(String title, String inText, float width, float startXPos, float startYPos,
			float lineSpace, int fontSize) throws Exception {
		PDFont font = PDType1Font.TIMES_ROMAN;
		PDFont fontTitle = PDType1Font.TIMES_BOLD;
		int titleFontSize = 10;
		List<String> lines = new ArrayList<String>();
		int lastSpace = -1;
		String text = inText;

		while (text.length() > 0) {
			int spaceIndex = text.indexOf(' ', lastSpace + 1);
			if (spaceIndex < 0) {
				lines.add(text);
				text = "";
			} else {
				String subString = text.substring(0, spaceIndex);
				float size = fontSize * font.getStringWidth(subString) / 1000;
				if (size > width) {
					if (lastSpace < 0)
						lastSpace = spaceIndex;
					subString = text.substring(0, lastSpace);
					lines.add(subString);
					text = text.substring(lastSpace).trim();
					lastSpace = -1;
				} else {
					lastSpace = spaceIndex;
				}
			}
		}

		float current = startYPos;
		if (!"".equals(title)) {
			float titleHeight = fontTitle.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * titleFontSize;
			titleHeight += lineSpace;
			content.beginText();
			content.setFont(fontTitle, titleFontSize);
			content.moveTextPositionByAmount(startXPos, startYPos);
			current -= titleHeight;
			if (pageContentMinHeight > current) {
				content.endText();
				createPageAndChangePos();
				renderTitleForPage();
				content.beginText();
				content.setFont(fontTitle, titleFontSize);
				content.moveTextPositionByAmount(startXPos, getCurrentYPos());
				current = getCurrentYPos();
				current -= titleHeight;
			}
			content.drawString(title);
			content.moveTextPositionByAmount(startXPos, -titleHeight);
			content.endText();
		}
		if (!"".equals(inText)) {
			float leading = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
			leading += lineSpace;
			content.beginText();
			content.setFont(font, fontSize);
			content.moveTextPositionByAmount(startXPos, current);
			for (String line : lines) {
				current -= leading;
				if (pageContentMinHeight > current) {
					content.endText();
					createPageAndChangePos();
					renderTitleForPage();
					content.beginText();
					content.setFont(font, fontSize);
					content.moveTextPositionByAmount(startXPos, getCurrentYPos());
					current = getCurrentYPos();
					current -= leading;
				}
				content.drawString(line);
				content.moveTextPositionByAmount(0, -leading);
			}
			content.endText();
		}
		return current;
	}

	public float createBulletedList(List<String> titles, HashMap<String, List<String>> texts, float width,
			float startXPos, float startYPos, int fontSize) throws Exception {
		int s = titles.size();
		float nextY = startYPos;
		for (int i = 0; i < s; i++) {
			String title = titles.get(i);
			List<String> pTexts = texts.get(title);
			if (!"".equals(title)) {
				nextY = createParagraph(title, "", width, startXPos, nextY, 2, fontSize);
			}
			int ls = pTexts.size();
			for (int j = 0; j < ls; j++) {
				String text = pTexts.get(j);
				nextY = createParagraph("", text, width, startXPos + 20, nextY, 2, fontSize);
			}
		}
		return nextY;
	}

	public float createTableHeader(ArrayList<HashMap<String, String>> columns, float height, float width,
			float startXPos, float startYPos, int noOfLines) throws Exception {
		return createTableHeader(columns, height, width, startXPos, startYPos, noOfLines, true, 10);
	}

	public float createTableHeader(ArrayList<HashMap<String, String>> columns, float height, float width,
			float startXPos, float startYPos, int noOfLines, boolean drawColumnLines, float fontSize) throws Exception {
		float current = startYPos;
		int s = columns.size();
		float originalPosition = startYPos - height;
		PDFont font = PDType1Font.TIMES_BOLD;
		// float fontSize=10;
		if (pageContentMinHeight > originalPosition) {
			createPageAndChangePos();
		}
		content.drawLine(startXPos, startYPos, startXPos + width, startYPos);
		if (drawColumnLines) {
			content.drawLine(startXPos, startYPos, startXPos, startYPos - height);
			content.drawLine(startXPos + width, startYPos - height, startXPos + width, startYPos);
		}
		content.drawLine(startXPos, startYPos - height, startXPos + width, startYPos - height);
		float currentX = startXPos;
		float currentY = startYPos;
		float lineHeight = height / noOfLines;
		for (int i = 0; i < s; i++) {
			HashMap<String, String> column = columns.get(i);
			float margin = 0;
			float w = Float.parseFloat(column.get("width"));
			String title = column.get("caption");
			String title1 = column.get("caption1");
			float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
			float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
			currentY = startYPos;
			content.beginText();
			content.setFont(font, fontSize);
			content.moveTextPositionByAmount(currentX + (((w + margin - titleWidth) / 2)),
					currentY - ((lineHeight + 15 - titleHeight) / 2));
			content.drawString(title);
			content.endText();
			currentY = currentY - lineHeight;
			if (title1 != null) {
				titleWidth = font.getStringWidth(title1) / 1000 * fontSize;
				content.beginText();
				content.setFont(font, fontSize);
				content.moveTextPositionByAmount(currentX + (((w + margin - titleWidth) / 2)),
						currentY - ((lineHeight + 15 - titleHeight) / 2));
				content.drawString(title1);
				content.endText();
			}
			if (i != s - 1 && drawColumnLines) {
				content.drawLine(currentX + w + margin, startYPos, currentX + w + margin, startYPos - height);
			}
			currentX += (w + margin);
		}
		return current - height;
	}

	public float createTableFooter(ArrayList<HashMap<String, String>> columns, HashMap<String, String> values,
			float height, float width, float startXPos, float startYPos) throws Exception {
		return createTableFooter(columns, values, height, width, startXPos, startYPos, true, 10);
	}

	public float createTableFooter(ArrayList<HashMap<String, String>> columns, HashMap<String, String> values,
			float height, float width, float startXPos, float startYPos, boolean drawColumnLines, float fontSize)
			throws Exception {
		float current = startYPos;
		int s = columns.size();
		float originalPosition = startYPos - height;
		PDFont font = PDType1Font.TIMES_BOLD;
		if (pageContentMinHeight > originalPosition) {
			createPageAndChangePos();
		}
		content.drawLine(startXPos, startYPos, startXPos + width, startYPos);
		content.drawLine(startXPos, startYPos - height, startXPos + width, startYPos - height);
		if (drawColumnLines) {
			content.drawLine(startXPos, startYPos, startXPos, startYPos - height);
			content.drawLine(startXPos + width, startYPos - height, startXPos + width, startYPos);
		}
		float currentX = startXPos;
		float currentY = startYPos;
		float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
		for (int i = 0; i < s; i++) {
			HashMap<String, String> column = columns.get(i);
			float margin = Float.parseFloat(column.get("margin"));
			float w = Float.parseFloat(column.get("width"));
			String title = column.get("caption");
			String align = column.get("align");
			String value = values.get(column.get("name"));
			if (value != null) {
				if ("integer".equals(column.get("format"))) {
					value = formatInteger.format(Integer.parseInt(value));
				} else if ("number".equals(column.get("format"))) {
					value = formatNumber.format(Double.parseDouble(value));
				}
				float titleWidth = font.getStringWidth(value) / 1000 * fontSize;
				float x = currentX + (((w - margin - titleWidth) / 2));
				if ("r".equals(align)) {
					x = currentX + w - margin - titleWidth;
				} else if ("l".equals(align)) {
					x = currentX + margin;
				}
				content.beginText();
				content.setFont(font, fontSize);
				content.moveTextPositionByAmount(x, currentY - titleHeight);
				content.drawString(value);
				content.endText();
			}
			if (i != s - 1 && drawColumnLines) {
				content.drawLine(currentX + w, startYPos, currentX + w, startYPos - height);
			}
			currentX += w;
		}
		return current - height;
	}

	/*
	 * public float createTableContent(ArrayList<HashMap<String,String>>
	 * columns,List<HashMap<String,String>> rows,float headerHeight,float
	 * rowSpacing,float width,float startXPos,float startYPos)throws Exception{
	 * float current=startYPos; int s=columns.size(); int ls=rows.size(); PDFont
	 * font = PDType1Font.TIMES_ROMAN; float fontSize=10; float currentX=startXPos;
	 * float currentY=startYPos; float firstRow=15; float titleHeight =
	 * font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
	 * for(int j=0;j<ls;j++){ HashMap<String,String> row=rows.get(j); boolean
	 * textPresent=true; while(textPresent){ boolean tp=false; boolean drawn=false;
	 * for(int i=0;i<s;i++){ HashMap<String,String> column=columns.get(i); float
	 * margin=0; float w=Float.parseFloat(column.get("width")); String
	 * cValue=row.get(column.get("name")); String align=column.get("align"); float
	 * titleWidth = font.getStringWidth(cValue) / 1000 * fontSize;
	 * content.beginText(); content.setFont(font, fontSize); String pt=cValue;
	 * if(titleWidth>w){ float textWidth=0; int spaceIndex=-1; int
	 * lastSpaceIndex=-1; while(textWidth<=w){ lastSpaceIndex=spaceIndex;
	 * spaceIndex=cValue.indexOf(" ", spaceIndex+1); if(spaceIndex==-1){
	 * spaceIndex=cValue.length(); }
	 * textWidth=font.getStringWidth(cValue.substring(0,spaceIndex))/1000*fontSize;
	 * } if(lastSpaceIndex==-1){ lastSpaceIndex=cValue.length(); }
	 * pt=cValue.substring(0, lastSpaceIndex);
	 * cValue=cValue.substring(lastSpaceIndex).trim(); row.put(column.get("name"),
	 * cValue); }else{ cValue=""; row.put(column.get("name"), cValue); }
	 * if(!cValue.equals("")){ tp=true; } titleWidth=font.getStringWidth(pt) / 1000
	 * * fontSize; float x=currentX+(((w+margin)/2)-titleWidth);
	 * if("r".equals(align)){ x=currentX+w+margin-titleWidth; }else
	 * if("l".equals(align)){ x=currentX+margin; }
	 * if(pageContentMinHeight>currentY){ content.endText();
	 * drawColumnLines(columns,width,startXPos,startYPos,currentY);
	 * createPageAndChangePos();
	 * currentY=createTableHeader(columns,headerHeight,width,startXPos,
	 * pageContentMaxHeight); content.beginText(); content.setFont(font, fontSize);
	 * startYPos=currentY; } content.moveTextPositionByAmount(x,
	 * currentY-titleHeight); content.drawString(pt); content.endText();
	 * currentX+=(w+margin); } textPresent=tp; currentY-=titleHeight;
	 * currentX=startXPos; } currentY-=rowSpacing; } drawColumnLines(columns, width,
	 * startXPos,startYPos, currentY); return currentY; }
	 */
	public float createTableContent(ArrayList<HashMap<String, String>> columns, ResultSet rows, float headerHeight,
			float rowSpacing, float width, float startXPos, float startYPos, int headerLines) throws Exception {
		return createTableContent(columns, rows, headerHeight, rowSpacing, width, startXPos, startYPos, headerLines,
				true, 10);
	}

	public float createTableContent(ArrayList<HashMap<String, String>> columns, ResultSet rows, float headerHeight,
			float rowSpacing, float width, float startXPos, float startYPos, int headerLines, boolean drawLines,
			float fontSize) throws Exception {
		return createTableContent(columns, rows, headerHeight, rowSpacing, width, startXPos, startYPos, headerLines,
				drawLines, fontSize, 0);
	}

	public float createTableContent(ArrayList<HashMap<String, String>> columns, ResultSet rows, float headerHeight,
			float rowSpacing, float width, float startXPos, float startYPos, int headerLines, boolean drawLines,
			float fontSize, int borderBottom) throws Exception {
		return createTableContent(columns, rows, headerHeight, rowSpacing, width, startXPos, startYPos, headerLines,
				drawLines, fontSize, borderBottom, null, null);
	}

	public float createTableContent(ArrayList<HashMap<String, String>> columns, ResultSet rows, float headerHeight,
			float rowSpacing, float width, float startXPos, float startYPos, int headerLines, boolean drawLines,
			float fontSize, int borderBottom, HashMap<String, String> hideColumns, String[] keyColumns)
			throws Exception {
		float current = startYPos;
		int s = columns.size();
		PDFont font = PDType1Font.TIMES_ROMAN;
		// float fontSize=10;
		float currentX = startXPos;
		float currentY = startYPos;
		float firstRow = 15;
		float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
		int kc = 0;
		if (keyColumns != null) {
			kc = keyColumns.length;
		}
		while (rows.next()) {
			// HashMap<String,String> row=rows.get(j);
			boolean textPresent = true;
			boolean setValue = true;
			boolean hideColumn = true;
			if (kc > 0 && hideColumns != null) {
				for (int p = 0; p < kc; p++) {
					if (!hideColumns.get(keyColumns[p]).equals(rows.getString(keyColumns[p]))) {
						hideColumns.put(keyColumns[p], rows.getString(keyColumns[p]));
						hideColumn = false;
					}
				}
			}
			while (textPresent) {
				boolean tp = false;
				for (int i = 0; i < s; i++) {
					HashMap<String, String> column = columns.get(i);
					float margin = Float.parseFloat(column.get("margin"));
					float w = Float.parseFloat(column.get("width"));
					String cValue = column.get("value");
					if (setValue) {
						cValue = rows.getString(column.get("name"));
						if (hideColumn && "1".equals(column.get("hideonrepeat"))) {
							cValue = "";
						}
						if (cValue == null) {
							cValue = "";
						}

						if (!cValue.equals("") && column.get("format") != null) {
							if ("date".equals(column.get("format"))) {
								cValue = formatddMMyyyy.format(rows.getDate(column.get("name")));
							} else if ("integer".equals(column.get("format"))) {
								cValue = formatInteger.format(rows.getInt(column.get("name")));
							} else if ("number".equals(column.get("format"))) {
								cValue = formatNumber.format(rows.getInt(column.get("name")));
							} else if ("datetime".equals(column.get("format"))) {
								cValue = formatddMMyyyyHH.format(rows.getDate(column.get("name")));
							}
						}
					}
					String align = column.get("align");
					float titleWidth = font.getStringWidth(cValue) / 1000 * fontSize;
					content.beginText();
					content.setFont(font, fontSize);
					String pt = cValue;
					if (titleWidth > w - margin) {
						float textWidth = 0;
						int spaceIndex = -1;
						int lastSpaceIndex = -1;
						while (textWidth <= w - margin) {
							lastSpaceIndex = spaceIndex;
							spaceIndex = cValue.indexOf(" ", spaceIndex + 1);
							if (spaceIndex == -1) {
								spaceIndex = cValue.length();
							}
							textWidth = font.getStringWidth(cValue.substring(0, spaceIndex)) / 1000 * fontSize;
						}
						if (lastSpaceIndex == -1) {
							lastSpaceIndex = cValue.length();
						}
						pt = cValue.substring(0, lastSpaceIndex);
						cValue = cValue.substring(lastSpaceIndex).trim();
						column.put("value", cValue);
					} else {
						cValue = "";
						column.put("value", cValue);
					}
					if (!cValue.equals("")) {
						tp = true;
					}
					titleWidth = font.getStringWidth(pt) / 1000 * fontSize;
					float x = currentX + (((w - margin - titleWidth) / 2));
					if ("r".equals(align)) {
						x = currentX + w - margin - titleWidth;
					} else if ("l".equals(align)) {
						x = currentX + margin;
					}
					if (pageContentMinHeight > (currentY - titleHeight)) {
						content.endText();
						if (drawLines) {
							drawColumnLines(columns, width, startXPos, startYPos, currentY);
						}
						createPageAndChangePos();
						renderTitleForPage();
						currentY = createTableHeader(columns, headerHeight, width, startXPos, currentYPos, headerLines,
								drawLines, fontSize);
						content.beginText();
						content.setFont(font, fontSize);
						startYPos = currentY;
					}
					content.moveTextPositionByAmount(x, currentY - titleHeight);
					content.drawString(pt);
					content.endText();
					currentX += w;
				}
				textPresent = tp;
				currentY -= titleHeight;
				currentX = startXPos;
				setValue = false;
			}
			currentY -= rowSpacing;
			if (borderBottom == 1) {
				content.drawLine(startXPos, currentY, startXPos + width, currentY);
			}
		}
		if (drawLines) {
			drawColumnLines(columns, width, startXPos, startYPos, currentY);
		}
		return currentY;
	}

	private void drawColumnLines(ArrayList<HashMap<String, String>> columns, float width, float startXPos,
			float startYPos, float currentY) throws Exception {
		int s = columns.size();
		float currentX = startXPos;
		content.drawLine(startXPos, startYPos, startXPos, currentY);
		for (int i = 0; i < s; i++) {
			HashMap<String, String> column = columns.get(i);
			float margin = 0;
			float w = Float.parseFloat(column.get("width"));
			content.drawLine(currentX + w + margin, startYPos, currentX + w + margin, currentY);
			currentX += (margin + w);
		}
		content.drawLine(startXPos, currentY, startXPos + width, currentY);
	}

	public void getImages(List<String> images) throws Exception {
		pdImages.clear();
		if (images != null) {
			int s = images.size();
			for (int i = 0; i < s; i++) {
				// InputStream in = new FileInputStream(new
				// File(System.getProperty("catalina.home") + File.separator +
				// "../"+InitProjectServlet.context+File.separator+filePath));
				// System.out.println(System.getProperty("catalina.home") + File.separator +
				// "../"+InitProjectServlet.context+File.separator+images.get(i));
				BufferedImage io = null;
				if (useContext) {
					io = ImageIO.read(new File(System.getProperty("catalina.home") + File.separator + "../"
							+ InitProjectServlet.context + File.separator + images.get(i)));
				} else {
					io = ImageIO.read(
							new File(System.getProperty("catalina.home") + File.separator + "../" + images.get(i)));
				}
				//// pdImages.add(new PDJpeg(document,io));
			}
		}
	}

	public void drawImage(int imageId, float xPos, float yPos, float width, float height) throws Exception {
		content.drawXObject(pdImages.get(imageId), xPos, yPos, width, height);
	}

	public PDPageContentStream getContent() {
		return content;
	}

	public float getCurrentX() {
		return currentXPos;
	}

	public float getCurrentY() {
		return currentYPos;
	}

	public void closeDocument() throws Exception {
		document.close();
	}

	public void closeContent() throws Exception {
		content.close();
	}

	public float getContentWidth() {
		return pageContentMaxWidth - pageMarginLeft;
	}

	public void saveDocument(OutputStream stream) throws Exception {
		document.save(stream);
	}

	public void drawLine(float startX, float startY, float endX, float endY) throws Exception {
		content.drawLine(startX, startY, endX, endY);
	}

	public void changeYPosBy(float height) {
		currentYPos -= height;
	}

	public void printPageNumbers(String leftMessage) throws Exception {
		int n = document.getNumberOfPages();
		PDFont font = PDType1Font.TIMES_ROMAN;
		int fontSize = 8;
		for (int i = 0; i < n; i++) {
			PDPage p = (PDPage) document.getDocumentCatalog().getPages().get(i);
			PDPageContentStream content = new PDPageContentStream(document, p, true, false);
			float width = pageContentMinWidth;
			float titleWidth = font.getStringWidth(leftMessage) / 1000 * fontSize;
			float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
			content.beginText();
			content.setFont(font, fontSize);
			content.moveTextPositionByAmount(pageMarginLeft + 5, (pageContentMinHeight - 15) - titleHeight);
			content.drawString(leftMessage);
			content.endText();
			String pageNo = "Page " + (i + 1) + " of " + n;
			titleWidth = font.getStringWidth(pageNo) / 1000 * fontSize;
			content.beginText();
			content.setFont(font, fontSize);
			content.moveTextPositionByAmount(pageContentMaxWidth - 100, (pageContentMinHeight - 15) - titleHeight);
			content.drawString(pageNo);
			content.endText();
			content.close();
		}
	}

	public void writeSimpleDocument(OutputStream stream) throws Exception {
		ArrayList<String> imgs = new ArrayList<String>();
		imgs.add("/web/images/comp_logo.png");
		initializeDocument(36, 36, 36, 36, true, 0, imgs, PDRectangle.A4);
		// header starts
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
		// header ends

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
		cy = createTableFooter(c, v, 20, getPageWidth() - getPageMarginLeft() - getPageMarginRight(),
				getPageMarginLeft(), getCurrentYPos());
		// drawImage(currentXPos+100,currentYPos-100);
		closeContent();
		saveDocument(stream);
		closeDocument();
	}
}