package com.cissol.core.report;

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

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.cissol.core.util.DatabaseUtil;

//import jakarta.imageio.ImageIO;
import jakarta.ws.rs.core.MultivaluedMap;

public class WritePDFReport extends WritePDF {
	private static final SimpleDateFormat formatddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat formatyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	ArrayList<HashMap<String, String>> titles = new ArrayList<HashMap<String, String>>();

	public void renderTitleForPage() throws Exception {
		createTitle(getPageMarginLeft(), getCurrentYPos(), titles);
		// drawLine(getPageMarginLeft(),getCurrentYPos()-5,getPageWidth()-getPageMarginRight(),getCurrentYPos()-5);
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
		String reportQuery = "";
		String queryParams = "";
		String headerQuery = "";
		String reportTitle = "";
		String reportName = "";
		String paramTypes = "";
		String primaryColumns = "";
		String footerQuery = "";
		String openingBalanceQuery = "";
		String defaultSort = "";
		String defaultSortOrder = "";
		int reportPDFPage = 0;
		int reportPDFOrient = 0;
		int writeRecordLines = 0;
		ArrayList<HashMap<String, String>> columns = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> searchFields = new HashMap<String, String>();
		HashMap<String, String> hideColumns = new HashMap<String, String>();
		int totalRows = 0;
		boolean search = false;
		boolean footer = false;
		try {
			String reportId = parameters.getFirst("id");
			String userName = parameters.getFirst("login_id");
			hdr = con.prepareStatement(ExportReport.reportHeader);
			dtl = con.prepareStatement(
					ExportReport.reportDetail.replace("order by r.column_order", "order by r.export_column_order"));
			hdr.setString(1, reportId);
			rs = hdr.executeQuery();
			if (rs.next()) {
				reportQuery = rs.getString("report_query");
				openingBalanceQuery = rs.getString("openbal_query");
				headerQuery = rs.getString("header_query");
				reportTitle = rs.getString("report_title");
				reportName = rs.getString("report_name");
				queryParams = rs.getString("parameters");
				paramTypes = rs.getString("param_types");
				reportPDFPage = rs.getInt("report_pdf_paper");
				reportPDFOrient = rs.getInt("report_pdf_orient");
				defaultSort = rs.getString("default_sort");
				defaultSortOrder = rs.getString("default_sort_order");
				writeRecordLines = rs.getInt("report_record_lines");
				primaryColumns = rs.getString("report_key_columns");
				if ("1".equals(rs.getString("footer_columns"))) {
					footer = true;
				}
			}
			rs.close();
			hdr.close();
			if (parameters.getFirst("_search") != null) {
				search = Boolean.parseBoolean(parameters.getFirst("_search"));
			}
			String[] queryParam = queryParams.split(",");
			String[] paramType = paramTypes.split(",");
			int ps = queryParam.length;
			for (int p = 0; p < ps; p++) {
				String f = queryParam[p];
				String t = paramType[p];
				String v = parameters.getFirst(queryParam[p]);
				if ("date".equals(t) && v != null) {
					Date d = formatddMMyyyy.parse(v);
					v = formatyyyyMMdd.format(d);
				}
				reportQuery = reportQuery.replaceAll("\\$\\(" + f + "\\)", v);
				headerQuery = headerQuery.replaceAll("\\$\\(" + f + "\\)", v);
				if (openingBalanceQuery != null) {
					openingBalanceQuery = openingBalanceQuery.replaceAll("\\$\\(" + f + "\\)", v);
				}
			}
			String[] keyColumns = null;
			int kc = 0;
			if (primaryColumns != null && !"".equals(primaryColumns)) {
				keyColumns = primaryColumns.split(",");
				kc = keyColumns.length;
				for (int p = 0; p < kc; p++) {
					hideColumns.put(keyColumns[p], "");
				}
			}
			hdr = con.prepareStatement(ExportReport.reportParams);
			hdr.setString(1, reportId);
			rs = hdr.executeQuery();
			while (rs.next()) {
				String paramName = rs.getString("param_name");
				String paramSkipValue = rs.getString("param_skip_value");
				String paramInpType = rs.getString("param_type");
				String paramCondition = rs.getString("param_condition");
				String v = parameters.getFirst(paramName);
				String v1 = parameters.getFirst(paramName + "_code");
				if ((v1 != null && paramSkipValue.equals(v1)) || paramSkipValue.equals(v)) {
					reportQuery = reportQuery.replaceAll("\\$\\(" + paramName + "\\)", "");
					headerQuery = headerQuery.replaceAll("\\$\\(" + paramName + "\\)", "");
					if (openingBalanceQuery != null) {
						openingBalanceQuery = openingBalanceQuery.replaceAll("\\$\\(" + paramName + "\\)", "");
					}
					continue;
				}
				if ("DATE".equals(paramInpType) && v != null) {
					Date d = formatddMMyyyy.parse(v);
					v = formatyyyyMMdd.format(d);
				}
//				System.out.println(paramSkipValue+" "+v+" "+v1+" "+paramName+" "+paramCondition);
				paramCondition = paramCondition.replaceAll("\\$\\(" + paramName + "\\)", v);
				if (v1 != null && !paramSkipValue.equals(v1)) {
					paramCondition = paramCondition.replaceAll("\\$\\(" + paramName + "_code\\)", v1);
				}
				reportQuery = reportQuery.replaceAll("\\$\\(" + paramName + "\\)", paramCondition);
				headerQuery = headerQuery.replaceAll("\\$\\(" + paramName + "\\)", paramCondition);
				if (openingBalanceQuery != null) {
					openingBalanceQuery = openingBalanceQuery.replaceAll("\\$\\(" + paramName + "\\)", paramCondition);
				}
			}
			rs.close();
			hdr.close();
			dtl.setString(1, reportId);
			dtl.setString(2, userName);
			rs = dtl.executeQuery();
			while (rs.next()) {
				HashMap<String, String> c = new HashMap<String, String>();
				c.put("COLUMN_WIDTH", rs.getString("pdf_column_width"));
				c.put("COLUMN_NAME", rs.getString("column_name"));
				c.put("COLUMN_TITLE", rs.getString("column_title"));
				c.put("COLUMN_ALIGN", rs.getString("column_align"));
				c.put("COLUMN_TYPE", rs.getString("column_type"));
				c.put("COLUMN_SUM", rs.getString("column_sum"));
				c.put("COLUMN_LINK", rs.getString("column_link"));
				c.put("CLOSING_OPERATOR", rs.getString("closing_operator"));
				c.put("CLOSING_CONDITION", rs.getString("closing_condition"));
				c.put("LINK_PARAMETERS", rs.getString("link_parameters"));
				c.put("PARAM_COLUMNS", rs.getString("param_columns"));
				c.put("COLUMN_FORMATTER", rs.getString("column_formatter"));
				c.put("SUM_COLUMN_FIELD", rs.getString("sum_column_field"));
				c.put("IS_OPERATION", rs.getString("is_operation"));
				c.put("HIDE_ON_REPEAT", rs.getString("hide_on_repeat"));
				searchFields.put(rs.getString("column_name"), rs.getString("search_column_field"));
				if ("0".equals(rs.getString("pdf_column_width"))) {
					c.put("COLUMN_HIDDEN", "true");
				}
				columns.add(c);
			}
			rs.close();
			dtl.close();
			int s = columns.size();
			int whereIndex = reportQuery.toLowerCase().lastIndexOf("where");
			int groupByIndex = reportQuery.toLowerCase().lastIndexOf("group by");
			StringBuffer querySearchWhere = new StringBuffer(100);
			if (search) {
				String and = " ";
				for (int i = 0; i < s; i++) {
					HashMap<String, String> c = columns.get(i);
					String p = parameters.getFirst(c.get("COLUMN_NAME"));
					if (p != null) {
						querySearchWhere.append(and);
						querySearchWhere.append(searchFields.get(c.get("COLUMN_NAME")));
						querySearchWhere.append(" like '");
						querySearchWhere.append(p);
						querySearchWhere.append("%'");
						and = " and ";
					}
				}
				// reportQuery+=querySearchWhere.toString();
			}
			if (querySearchWhere.length() > 0) {
				boolean ir = false;
				if (reportQuery.indexOf("<<SFW>>") != -1) {
					reportQuery = reportQuery.replaceAll("<<SFW>>", " where " + querySearchWhere.toString());
					ir = true;
				}
				if (reportQuery.indexOf("<<SF>>") != -1) {
					reportQuery = reportQuery.replaceAll("<<SF>>", " and " + querySearchWhere.toString());
					ir = true;
				}
				if (!ir) {
					if (whereIndex < groupByIndex && groupByIndex >= 0) {
						reportQuery += " having ";
					} else if (whereIndex < 0) {
						reportQuery += " where ";
					} else {
						reportQuery += " and ";
					}
					reportQuery += querySearchWhere.toString();
				}
			} else {
				reportQuery = reportQuery.replaceAll("<<SFW>>", "");
				reportQuery = reportQuery.replaceAll("<<SF>>", "");
			}
			if (defaultSort != null && !"".equals(defaultSort)) {
				reportQuery += " order by " + defaultSort + " " + defaultSortOrder;
			}
			int fromIndex = reportQuery.toLowerCase().indexOf(" from");
			String fromQuery = reportQuery.substring(fromIndex);
			if (footer) {
				StringBuilder sd = new StringBuilder(200);
				int groupbyIndex = fromQuery.lastIndexOf("group by ");
				String sumQuery = fromQuery;
				if (groupbyIndex != -1) {
					sumQuery = fromQuery.substring(0, groupbyIndex);
				}
				sd.append("select ");
				String comma = "";
				for (int i = 0; i < s; i++) {
					HashMap<String, String> c = columns.get(i);
					if ("1".equals(c.get("COLUMN_SUM"))) {
						sd.append(comma);
						sd.append("sum(");
						sd.append(c.get("SUM_COLUMN_FIELD"));
						sd.append(") ");
						sd.append(c.get("COLUMN_NAME"));
						comma = ",";
					}
				}
				sd.append(" ");
				sd.append(sumQuery);
				footerQuery = sd.toString();
			}
			// ArrayList<String> imgs=new ArrayList<String>();
			ArrayList<HashMap<String, String>> reportColumns = new ArrayList<HashMap<String, String>>();
			// imgs.add("/web/images/comp_logo.png");
			s = columns.size();
			float calculatedPageWidth = 30;
			for (int i = 0; i < s; i++) {
				HashMap<String, String> c = columns.get(i);
				if (!"1".equals(c.get("IS_OPERATION")) && !"true".equals(c.get("COLUMN_HIDDEN"))) {
					calculatedPageWidth += Float.parseFloat(c.get("COLUMN_WIDTH"));
					HashMap<String, String> c1 = new HashMap<String, String>();
					c1.put("margin", "2");
					c1.put("width", c.get("COLUMN_WIDTH"));
					c1.put("caption", c.get("COLUMN_TITLE"));
					c1.put("caption1", "");
					c1.put("name", c.get("COLUMN_NAME"));
					c1.put("align", c.get("COLUMN_ALIGN").substring(0, 1));
					c1.put("format", c.get("COLUMN_FORMATTER"));
					c1.put("hideonrepeat", c.get("HIDE_ON_REPEAT"));
					reportColumns.add(c1);
				}
			}
			float pdfWidth = 0f;
			float pdfHeight = 0f;
			if (reportPDFOrient == 1) {
				if (reportPDFPage == 1) {
		    		pdfWidth=PDRectangle.A4.getHeight();   //PDFPage.PAGE_SIZE_A4.getHeight();
		    		pdfHeight=PDRectangle.A4.getWidth();
				} else if (reportPDFPage == 2) {
					pdfWidth=PDRectangle.LETTER.getHeight();   //PDFPage.PAGE_SIZE_A4.getHeight();
		    		pdfHeight=PDRectangle.LETTER.getWidth();
				} else {
					pdfWidth=PDRectangle.A4.getHeight();   //PDFPage.PAGE_SIZE_A4.getHeight();
		    		pdfHeight=PDRectangle.A4.getWidth();
				}
			} else {
				if (reportPDFPage == 1) {
		    		pdfHeight=PDRectangle.A4.getHeight();
		    		pdfWidth=PDRectangle.A4.getWidth();
				} else if (reportPDFPage == 2) {
					pdfHeight=PDRectangle.A4.getHeight();
		    		pdfWidth=PDRectangle.A4.getWidth();
				} else {
					pdfHeight=PDRectangle.A4.getHeight();
		    		pdfWidth=PDRectangle.A4.getWidth();
				}
			}
			if (calculatedPageWidth < pdfWidth) {
				HashMap<String, String> c1 = reportColumns.get(reportColumns.size() - 1);
				float width = Float.parseFloat(c1.get("width"));
				width += (pdfWidth - calculatedPageWidth);
				c1.put("width", String.valueOf(width));
				calculatedPageWidth = pdfWidth;
			}
			System.out.println(pdfHeight + " " + pdfWidth + " " + calculatedPageWidth);
			initializeDocument(15, 15, 15, 30, false, 0, null, new PDRectangle(calculatedPageWidth, pdfHeight));
			stmt = con.createStatement();
			rs = stmt.executeQuery(headerQuery);
			if (rs.next()) {
				ResultSetMetaData md = rs.getMetaData();
				int cc = md.getColumnCount();
				for (int i = 1; i <= cc; i++) {
					HashMap<String, String> title = new HashMap<String, String>();
					if (i == 1) {
						title.put("size", "14");
					} else {
						title.put("size", "10");
					}
					title.put("title", rs.getString(md.getColumnLabel(i)));
					title.put("width", String.valueOf(getContentWidth()));
					title.put("align", "c");
					title.put("font", "b");
					titles.add(title);
				}
			}
			rs.close();
			stmt.close();
			renderTitleForPage();
			ArrayList<HashMap<String, String>> titles = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> title = new HashMap<String, String>();

			/*
			 * titles.clear(); title=new HashMap<String,String>(); title.put("size", "10");
			 * title.put("title", reportTitle); title.put("width",
			 * String.valueOf(getContentWidth())); title.put("align", "c");
			 * title.put("font", "b"); titles.add(title);
			 * 
			 * createTitle(getCurrentXPos(),getCurrentYPos(),titles);
			 */
			drawLine(getPageMarginLeft(), getCurrentYPos() - 5, getPageWidth() - getPageMarginRight(),
					getCurrentYPos() - 5);
			changeYPosBy(5);

			float cy = createTableHeader(reportColumns, 25, getPageWidth() - getPageMarginLeft() - getPageMarginRight(),
					getPageMarginLeft(), getCurrentYPos(), 1);
			setCurrentYPos(cy);
			stmt = con.createStatement();
			rs = stmt.executeQuery(reportQuery);
			cy = createTableContent(reportColumns, rs, 25, 2,
					getPageWidth() - getPageMarginLeft() - getPageMarginRight(), getPageMarginLeft(), getCurrentYPos(),
					1, true, 10, writeRecordLines, hideColumns, keyColumns);
			setCurrentYPos(cy);
			rs.close();
			stmt.close();
			if (footer) {
				HashMap<String, String> footerValues = new HashMap<String, String>();
				stmt = con.createStatement();
				rs = stmt.executeQuery(footerQuery);
				if (rs.next()) {
					for (int i = 0; i < s; i++) {
						HashMap<String, String> c = columns.get(i);
						if ("1".equals(c.get("COLUMN_SUM"))) {
							footerValues.put(c.get("COLUMN_NAME"), rs.getString(c.get("COLUMN_NAME")));
						}
					}
				}
				rs.close();
				stmt.close();
				cy = createTableFooter(reportColumns, footerValues, 25,
						getPageWidth() - getPageMarginLeft() - getPageMarginRight(), getPageMarginLeft(),
						getCurrentYPos());
			}
			closeContent();
			printPageNumbers(formatddMMyyyy.format(new Date()));
			saveDocument(out);
			closeDocument();
			document = null;
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
}