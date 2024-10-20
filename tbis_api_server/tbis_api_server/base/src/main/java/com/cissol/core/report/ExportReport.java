package com.cissol.core.report;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cissol.core.util.DatabaseUtil;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.bytecode.opencsv.CSVWriter;
import jakarta.ws.rs.core.MultivaluedMap;

public class ExportReport {
	public static final String reportHeader = "SELECT report_id,report_name,report_title,header_query,opening_balance,openbal_query,parameters,report_query,brought_forward,print_closing,title_for_all,brought_fwdtext,bf_column,param_types,default_sort,default_sort_order,footer_columns,report_pdf_paper,report_pdf_orient,report_record_lines,report_key_columns,default_search FROM report_hdr where report_id=?";
	public static final String reportDetail = "SELECT r.report_id,r.column_name,r.column_title,r.column_sum,r.column_width,r.column_type,r.column_align,r.column_link,r.closing_operator,r.closing_condition,r.link_parameters,r.param_columns,r.column_formatter,r.image_path,r.sum_column_field,r.column_frozen,r.is_operation,r.search_column_field,r.pdf_column_width,r.hide_on_repeat,r.is_operation,r.isprefix,r.issuffix,r.prefixfields,r.suffixfields,r.showtitle  FROM report_dtl r inner join role_column_map rm on r.report_id=rm.rcm_report_id and r.column_name=rm.rcm_column_name inner join users u on u.role_id=rm.rcm_role_id where r.report_id=? and u.user_name=? order by r.column_order";
	public static final String reportParams = "SELECT report_title,param_id,param_name,param_caption,param_condition,param_input_type,param_ajax_id,param_keys,param_values,param_type,param_skip_value,param_ajax_columns from report_params_config c inner join report_hdr h on c.report_id=h.report_id where c.report_id=?";
	private static final SimpleDateFormat formatddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat formatyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
	private static final DecimalFormat formatNumber = new DecimalFormat("####.00");
	

	public static void getReportMetaData(MultivaluedMap<String, String> parameters, PrintWriter out) {
		Connection con = null;
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
		ArrayList<HashMap<String, String>> columns = new ArrayList<HashMap<String, String>>();
		try {
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createJsonGenerator(out);
			generator.writeStartObject();
			con = DatabaseUtil.getConnection();
			String reportId = parameters.getFirst("id").replace("undefined", "");
			String userName = parameters.getFirst("login_id");
			hdr = con.prepareStatement(reportHeader);
			dtl = con.prepareStatement(reportDetail);
			hdr.setInt(1, Integer.parseInt(reportId));
			rs = hdr.executeQuery();
			if (rs.next()) {
				reportTitle = rs.getString("report_title");
				// generator.writeStringField("caption", reportTitle);
				generator.writeStringField("sortname", rs.getString("default_sort"));
				generator.writeStringField("sortorder", rs.getString("default_sort_order"));
				generator.writeStringField("defaultsearch", rs.getString("default_search"));
				if ("1".equals(rs.getString("footer_columns"))) {
					generator.writeBooleanField("footerrow", true);
					generator.writeBooleanField("userDataOnFooter", true);
				}
			}
			rs.close();
			hdr.close();
			dtl.setInt(1, Integer.parseInt(reportId));
			dtl.setString(2, userName);
			rs = dtl.executeQuery();
			generator.writeFieldName("colNames");
			generator.writeStartArray();
			while (rs.next()) {
				HashMap<String, String> c = new HashMap<String, String>();
				c.put("COLUMN_WIDTH", rs.getString("column_width"));
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
				c.put("IMAGE_PATH", rs.getString("image_path"));
				if ("0".equals(rs.getString("column_width"))) {
					c.put("COLUMN_HIDDEN", "true");
				}
				if ("0".equals(rs.getString("column_frozen"))) {
					c.put("COLUMN_FROZEN", "false");
				} else {
					c.put("COLUMN_FROZEN", "true");
				}
				// is_operation,r.isprefix,r.issuffix,r.prefixfields,r.suffixfields,r.showtitle
				c.put("COLUMN_OPERATION", rs.getString("is_operation"));
				c.put("COLUMN_PREFIX", rs.getString("isprefix"));
				c.put("COLUMN_SUFFIX", rs.getString("issuffix"));
				c.put("COLUMN_PREFIX_FIELDS", rs.getString("prefixfields"));
				c.put("COLUMN_SUFFIX_FIELDS", rs.getString("suffixfields"));
				c.put("COLUMN_SHOW_TITLE", rs.getString("showtitle"));
				columns.add(c);
				generator.writeString(rs.getString("column_title"));
			}
			generator.writeEndArray();
			rs.close();
			dtl.close();
			int s = columns.size();
			generator.writeFieldName("columns");
			generator.writeStartArray();
			for (int i = 0; i < s; i++) {
				HashMap<String, String> c = columns.get(i);
				generator.writeString(c.get("COLUMN_NAME"));
			}
			generator.writeEndArray();
			generator.writeFieldName("colModel");
			generator.writeStartArray();
			for (int i = 0; i < s; i++) {
				HashMap<String, String> c = columns.get(i);
				generator.writeStartObject();
				generator.writeStringField("name", c.get("COLUMN_NAME"));
				generator.writeStringField("index", c.get("COLUMN_NAME"));
				generator.writeNumberField("width", Integer.parseInt(c.get("COLUMN_WIDTH")));
				generator.writeStringField("align", c.get("COLUMN_ALIGN"));
				if ("image".equals(c.get("COLUMN_FORMATTER"))) {
					generator.writeBooleanField("sortable", false);
					generator.writeStringField("formatter", c.get("COLUMN_FORMATTER"));
					generator.writeStringField("link", c.get("COLUMN_LINK"));
					generator.writeStringField("parameters", c.get("LINK_PARAMETERS"));
					generator.writeStringField("columns", c.get("PARAM_COLUMNS"));
					generator.writeBooleanField("search", false);
				} else if ("statusimage".equals(c.get("COLUMN_FORMATTER"))) {
					generator.writeBooleanField("sortable", false);
					generator.writeStringField("formatter", c.get("COLUMN_FORMATTER"));
					generator.writeBooleanField("search", true);
				} else {
					generator.writeBooleanField("sortable", true);
					if ("date".equals(c.get("COLUMN_FORMATTER"))) {
						generator.writeStringField("formatter", c.get("COLUMN_FORMATTER"));
						generator.writeFieldName("formatoptions");
						generator.writeStartObject();
						generator.writeStringField("newformat", "d-m-Y");
						generator.writeEndObject();
					} else if ("datetime".equals(c.get("COLUMN_FORMATTER"))) {
						generator.writeStringField("formatter", "date");
						generator.writeFieldName("formatoptions");
						generator.writeStartObject();
						generator.writeStringField("srcformat", "Y-m-d H:i:s");
						generator.writeStringField("newformat", "d-m-Y H:i");
						generator.writeEndObject();
					} else {
						generator.writeStringField("formatter", c.get("COLUMN_FORMATTER"));
					}
					generator.writeStringField("searchtype", c.get("COLUMN_TYPE"));
					generator.writeBooleanField("search", true);
				}
				generator.writeStringField("image", c.get("IMAGE_PATH"));
				if ("true".equals(c.get("COLUMN_HIDDEN"))) {
					generator.writeBooleanField("hidden", true);
				}
				if ("true".equals(c.get("COLUMN_FROZEN"))) {
					generator.writeBooleanField("frozen", true);
				}
				generator.writeStringField("operation", c.get("COLUMN_OPERATION"));
				generator.writeStringField("prefix", c.get("COLUMN_PREFIX"));
				generator.writeStringField("suffix", c.get("COLUMN_SUFFIX"));
				generator.writeStringField("prefixfields", c.get("COLUMN_PREFIX_FIELDS"));
				generator.writeStringField("suffixfields", c.get("COLUMN_SUFFIX_FIELDS"));
				generator.writeStringField("showtitle", c.get("COLUMN_SHOW_TITLE"));
				generator.writeEndObject();
			}
			generator.writeEndArray();
			generator.writeEndObject();
			generator.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
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
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
	}

	public static void getReportParameters(MultivaluedMap<String, String> parameters, PrintWriter out) {
		Connection con = null;
		PreparedStatement hdr = null;
		PreparedStatement dtl = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createJsonGenerator(out);
			con = DatabaseUtil.getConnection();
			String reportId = parameters.getFirst("id").replace("undefined", "");
			hdr = con.prepareStatement(reportParams);
			hdr.setInt(1, Integer.parseInt(reportId));
			rs = hdr.executeQuery();
			generator.writeStartArray();
			while (rs.next()) {
				generator.writeStartObject();
				generator.writeStringField("reporttitle", rs.getString("report_title"));
				generator.writeStringField("paramid", rs.getString("param_id"));
				generator.writeStringField("paramname", rs.getString("param_name"));
				generator.writeStringField("paramcaption", rs.getString("param_caption"));
				generator.writeStringField("paraminputtype", rs.getString("param_input_type"));
				generator.writeStringField("paramkeys", rs.getString("param_keys"));
				generator.writeStringField("paramvalues", rs.getString("param_values"));
				generator.writeStringField("paramajaxid", rs.getString("param_ajax_id"));
				generator.writeStringField("paramtype", rs.getString("param_type"));
				generator.writeStringField("paramajaxcolumns", rs.getString("param_ajax_columns"));
				generator.writeEndObject();
			}
			rs.close();
			hdr.close();
			generator.writeEndArray();
			generator.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
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
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
	}

	public static String getReportData(MultivaluedMap<String, String> parameters, OutputStream out) {
		return getReportData(parameters, out, false, null, null);
	}

	public static String getReportData(MultivaluedMap<String, String> parameters, OutputStream out, boolean returnQuery,
			ArrayList<HashMap<String, String>> columns, HashMap<String, String> searchFields) {
		Connection con = null;
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
		String openingBalanceQuery = "";
		String retValue = "Success";
		HashMap<String, String> hideColumns = new HashMap<String, String>();
		// ArrayList<HashMap<String,String>> columns=new
		// ArrayList<HashMap<String,String>>();
		// HashMap<String,String> searchFields=new HashMap<String,String>();
		int page = 1;
		int rows = 50;
		String sortIndex = "";
		String sortOrder = "";
		String multipleSearch = "";
		String dynamicSearch = "";
		int totalRows = 0;
		boolean search = false;
		boolean footer = false;
		try {
			if (columns == null) {
				columns = new ArrayList<HashMap<String, String>>();
			}
			if (searchFields == null) {
				searchFields = new HashMap<String, String>();
			}
			multipleSearch = parameters.getFirst("filters");
			dynamicSearch = parameters.getFirst("dsf");
			if (dynamicSearch == null) {
				dynamicSearch = "";
			}
			if (!returnQuery) {
				page = Integer.parseInt(parameters.getFirst("page"));
				rows = Integer.parseInt(parameters.getFirst("rows"));
				sortIndex = parameters.getFirst("sidx");
				sortOrder = parameters.getFirst("sord");
			}
			if (parameters.getFirst("_search") != null) {
				search = Boolean.parseBoolean(parameters.getFirst("_search"));
			}
			con = DatabaseUtil.getConnection();
			String reportId = parameters.getFirst("id").replace("undefined", "");
			String userName = parameters.getFirst("login_id");
			hdr = con.prepareStatement(reportHeader);
			dtl = con.prepareStatement(reportDetail);
			hdr.setInt(1, Integer.parseInt(reportId));
			rs = hdr.executeQuery();
			if (rs.next()) {
				reportQuery = rs.getString("report_query");
				openingBalanceQuery = rs.getString("openbal_query");
				headerQuery = rs.getString("header_query");
				reportTitle = rs.getString("report_title");
				reportName = rs.getString("report_name");
				queryParams = rs.getString("parameters");
				paramTypes = rs.getString("param_types");
				primaryColumns = rs.getString("report_key_columns");
				if ("1".equals(rs.getString("footer_columns"))) {
					footer = true;
				}
			}
			rs.close();
			hdr.close();
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
			hdr = con.prepareStatement(reportParams);
			hdr.setInt(1, Integer.parseInt(reportId));
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
			dtl.setInt(1, Integer.parseInt(reportId));
			dtl.setString(2, userName);
			rs = dtl.executeQuery();
			while (rs.next()) {
				HashMap<String, String> c = new HashMap<String, String>();
				c.put("COLUMN_WIDTH", rs.getString("column_width"));
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
				c.put("HIDE_ON_REPEAT", rs.getString("hide_on_repeat"));
				searchFields.put(rs.getString("column_name"), rs.getString("search_column_field"));
				if ("0".equals(rs.getString("column_width"))) {
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
			StringBuffer queryMultipleSearch = new StringBuffer(100);
			if (multipleSearch != null && !"".equals(multipleSearch)) {
				ObjectMapper obj = new ObjectMapper();
				Map<String, Object> m = obj.readValue(multipleSearch, new TypeReference<Map<String, Object>>() {
				});
				String groupOp = (String) m.get("groupOp");
				ArrayList groupFields = (ArrayList) m.get("rules");
				int rulesize = groupFields.size();
				for (int x = 0; x < rulesize; x++) {
					Map row = (Map) groupFields.get(x);
					String op = (String) row.get("op");
					if (x == 0) {
						queryMultipleSearch.append(" ( ");
					} else {
						queryMultipleSearch.append(" ");
						queryMultipleSearch.append(groupOp);
						queryMultipleSearch.append(" ");
					}
					queryMultipleSearch.append(searchFields.get((String) row.get("field")));
					queryMultipleSearch.append(" ");
					if ("eq".equals(op)) {
						queryMultipleSearch.append("='");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("ne".equals(op)) {
						queryMultipleSearch.append("!='");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("lt".equals(op)) {
						queryMultipleSearch.append("<'");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("gt".equals(op)) {
						queryMultipleSearch.append(">'");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("le".equals(op)) {
						queryMultipleSearch.append("<='");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("ge".equals(op)) {
						queryMultipleSearch.append(">='");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("bw".equals(op)) {
						queryMultipleSearch.append(" like '");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("%'");
					} else if ("bn".equals(op)) {
						queryMultipleSearch.append(" not like '");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("%'");
					} else if ("ew".equals(op)) {
						queryMultipleSearch.append(" like '%");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("en".equals(op)) {
						queryMultipleSearch.append(" not like '%");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("'");
					} else if ("cn".equals(op)) {
						queryMultipleSearch.append(" like '%");
						queryMultipleSearch.append((String) row.get("data"));
						queryMultipleSearch.append("%'");
					} else if ("in".equals(op)) {
						String data = (String) row.get("data");
						data = data.replaceAll(",", "','");
						queryMultipleSearch.append(" in ('");
						queryMultipleSearch.append(data);
						queryMultipleSearch.append("')");
					} else if ("ni".equals(op)) {
						String data = (String) row.get("data");
						data = data.replaceAll(",", "','");
						queryMultipleSearch.append(" not in ('");
						queryMultipleSearch.append(data);
						queryMultipleSearch.append("')");
					}
				}
				if (rulesize > 0) {
					queryMultipleSearch.append(")");
				}
			}
			if (queryMultipleSearch.length() > 0) {
				if (querySearchWhere.length() > 0) {
					querySearchWhere.append(" and ");
				}
				querySearchWhere.append(queryMultipleSearch.toString());
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
			if (dynamicSearch.length() > 0) {
				if (whereIndex < 0) {
					reportQuery = reportQuery.replaceAll("<<DSF>>", " where " + dynamicSearch.toString());
				} else {
					reportQuery = reportQuery.replaceAll("<<DSF>>", " and " + dynamicSearch.toString());
				}
			} else {
				reportQuery = reportQuery.replaceAll("<<DSF>>", "");
			}
			System.out.println(reportQuery);
			if (returnQuery) {
				return reportQuery;
			}
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createJsonGenerator(out, JsonEncoding.UTF8);
			generator.writeStartObject();
			stmt = con.createStatement();
			int fromIndex = reportQuery.toLowerCase().indexOf(" from");
			String fromQuery = reportQuery.substring(fromIndex);
			String reportCountQuery = "select count('x') cnt " + fromQuery;
			if (whereIndex < groupByIndex && groupByIndex >= 0) {
				reportCountQuery = "select count('x') cnt from (select 'x'  " + fromQuery + ") a ";
			}
			System.out.println(" count query " + reportCountQuery);
			rs = stmt.executeQuery(reportCountQuery);
			if (rs.next()) {
				totalRows = rs.getInt("cnt");
			}
			rs.close();
			reportQuery += " order by " + sortIndex + " " + sortOrder;
			int totalPages = 0;
			if (totalRows > 0) {
				totalPages = (int) Math.ceil(totalRows / (double) rows);
			} else {
				totalPages = 0;
			}
			int start = (rows * page) - rows;
			if (DatabaseUtil.databaseType == 3) {
				reportQuery += " offset " + start + " limit " + rows;
			} else {
				reportQuery += " limit " + start + "," + rows;
			}
			System.out.println(" report query " + reportQuery);
			generator.writeStringField("page", String.valueOf(page));
			generator.writeNumberField("total", totalPages);
			generator.writeNumberField("records", totalRows);
			generator.writeFieldName("rows");
			generator.writeStartArray();
			rs = stmt.executeQuery(reportQuery);
			while (rs.next()) {
				generator.writeStartObject();
				boolean hideColumn = true;
				for (int p = 0; p < kc; p++) {
					if (!hideColumns.get(keyColumns[p]).equals(rs.getString(keyColumns[p]))) {
						hideColumns.put(keyColumns[p], rs.getString(keyColumns[p]));
						hideColumn = false;
					}
				}
				for (int i = 0; i < s; i++) {
					HashMap<String, String> c = columns.get(i);
					if (!"image".equals(c.get("COLUMN_FORMATTER"))) {
						if (hideColumn && "1".equals(c.get("HIDE_ON_REPEAT"))) {
							generator.writeStringField(c.get("COLUMN_NAME"), "");
							continue;
						}
						generator.writeStringField(c.get("COLUMN_NAME"), rs.getString(c.get("COLUMN_NAME")));
					}
				}
				generator.writeEndObject();
			}
			rs.close();
			generator.writeEndArray();
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
//	        	System.out.println(sumQuery);
				generator.writeFieldName("userdata");
				generator.writeStartObject();
				rs = stmt.executeQuery(sd.toString());
				if (rs.next()) {
					for (int i = 0; i < s; i++) {
						HashMap<String, String> c = columns.get(i);
						if ("1".equals(c.get("COLUMN_SUM"))) {
							if(rs.getString(c.get("COLUMN_NAME"))!=null){
								generator.writeStringField(c.get("COLUMN_NAME"), formatNumber.format( rs.getDouble(c.get("COLUMN_NAME"))));
							}else {
								generator.writeStringField(c.get("COLUMN_NAME"), rs.getString(c.get("COLUMN_NAME")));
							}
						}
					}
				}
				rs.close();
				generator.writeEndObject();
			}
			generator.writeEndObject();
			generator.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
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
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return retValue;
	}

	public static void writeCSVDocument(MultivaluedMap<String, String> parameters, OutputStream out) throws Exception {
		Connection con = null;
		try {
			con = DatabaseUtil.getConnection();
			writeCSVDocument(con, parameters, out);
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

	public static void writeCSVDocument(Connection con, MultivaluedMap<String, String> parameters, OutputStream out)
			throws Exception {
		PreparedStatement hdr = null;
		PreparedStatement dtl = null;
		Statement stmt = null;
		ResultSet rs = null;
		String reportQuery = "";
		ArrayList<HashMap<String, String>> columns = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> searchFields = new HashMap<String, String>();
		int totalRows = 0;
		boolean search = false;
		boolean footer = false;
		try {
			reportQuery = getReportData(parameters, null, true, columns, searchFields);
			int s = columns.size();
			StringBuffer reMakeQuery = new StringBuffer(100);
			reMakeQuery.append("select ");
			String comma = "";
			for (int i = 0; i < s; i++) {
				HashMap<String, String> c = columns.get(i);
				if (!"true".equals(c.get("COLUMN_HIDDEN")) && !"image".equals(c.get("COLUMN_FORMATTER"))) {
					reMakeQuery.append(comma);
					reMakeQuery.append(c.get("COLUMN_NAME"));
					reMakeQuery.append(" \"");
					reMakeQuery.append(c.get("COLUMN_TITLE"));
					reMakeQuery.append("\" ");
					comma = ",";
				}
				columns.add(c);
			}
			reMakeQuery.append(" from (");
			reMakeQuery.append(reportQuery);
			reMakeQuery.append(") report ");
			stmt = con.createStatement();
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));
			rs = stmt.executeQuery(reMakeQuery.toString());
			writer.writeAll(rs, true);
			writer.close();
			rs.close();
			stmt.close();
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
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
	}
}
