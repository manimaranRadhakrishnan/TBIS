package com.cissol.core.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

//import org.codehaus.jackson.JsonEncoding;
//import org.codehaus.jackson.JsonFactory;
//import org.codehaus.jackson.JsonGenerator;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

public class AjaxResponseUtil {
	public static String ajaxQuery = "select id,query,columns,parameters,widths,captions,datatypes,alignments,maxrows,orderby,title,columnnames from ajax where id in ('#ids#')";
	public static String reportHeader = "select a.report_id,a.report_name,a.report_title,a.parameters,b.default_ajax_id,b.values_to,b.value_type,b.ajax_url from report_hdr a inner join report_defaults b on a.report_id=b.report_id where a.report_id=?";

	public static String getResultJson(MultivaluedMap<String, String> parameters, OutputStream out) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String result = "";
		//// String query="";
		HashMap<String, String> queries = null;
		try {
//			JsonFactory factory=new JsonFactory();
//			JsonGenerator generator=factory.createJsonGenerator(out,JsonEncoding.UTF8);

			JsonFactory jsonFactory = new JsonFactory();
			JsonGenerator generator = jsonFactory.createGenerator(out, JsonEncoding.UTF8);

			conn = DatabaseUtil.getConnection();
			stmt = conn.createStatement();
			System.out.println(" Coming inside the statement .....");
			generator.writeStartObject();
			queries = getAjaxQuery(conn, parameters, generator);
			generator.writeFieldName("data");
			String ids[] = parameters.getFirst("id").split(",");
			String isObject = parameters.getFirst("ro");
			if (isObject == null) {
				isObject = "false";
			}
			int s = ids.length;
			if (s > 1) {
				generator.writeStartObject();
			}
			for (int j = 0; j < s; j++) {
				rs = stmt.executeQuery(queries.get(ids[j]));
				rsmd = rs.getMetaData();
				if (s > 1) {
					generator.writeFieldName(ids[j]);
				}
				generator.writeStartArray();
				while (rs.next()) {
					if ("true".equals(isObject)) {
						generator.writeStartObject();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							generator.writeStringField(rsmd.getColumnLabel(i), rs.getString(i));
						}
						generator.writeEndObject();
					} else {
						generator.writeStartArray();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							generator.writeString(rs.getString(i));
						}
						generator.writeEndArray();
					}
				}
				generator.writeEndArray();
				rs.close();
			}
			if (s > 1) {
				generator.writeEndObject();
			}
			generator.writeEndObject();
			generator.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	public static HashMap<String, String> getAjaxQuery(Connection conn, MultivaluedMap<String, String> parameters,
			JsonGenerator generator) {
		HashMap<String, String> queries = new HashMap<String, String>();
		String query = "";
		String orderby = "";
		Statement ajaxStatement = null;
		ResultSet rs = null;
		String ajaxQueryReplaced = "";
		try {
			ajaxStatement = conn.createStatement();
			String conSearch = parameters.getFirst("cs");
			if ("1".equals(conSearch)) {
				conSearch = "%";
			} else {
				conSearch = "";
			}
			String paramId = parameters.getFirst("id").replaceAll(",", "','");
			System.out.println(paramId + " " + ajaxQuery);
			ajaxQueryReplaced = ajaxQuery.replaceAll("#ids#", paramId);
			rs = ajaxStatement.executeQuery(ajaxQueryReplaced);
			while (rs.next()) {
				String queryParam = rs.getString("parameters");
				String columns = rs.getString("columns");
				String id = rs.getString("id");
				query = rs.getString("query");
				orderby = rs.getString("orderby");
				if (parameters.getFirst("ic").equals("1")) {
					String width = rs.getString("widths");
					String caption = rs.getString("captions");
					String alignment = rs.getString("alignments");
					String columnNames = rs.getString("columnnames");
					String[] widths = width.split(",");
					String[] captions = caption.split(",");
					String[] alignments = alignment.split(",");
					String[] cNames = columnNames.split(",");
					int s = widths.length;
					generator.writeStringField("title", rs.getString("title"));
					generator.writeFieldName("columns");
					generator.writeStartArray();
					for (int j = 0; j < s; j++) {
						generator.writeStartObject();
						generator.writeStringField("n", captions[j]);
						generator.writeNumberField("w", Integer.parseInt(widths[j]));
						generator.writeStringField("a", alignments[j]);
						generator.writeStringField("cn", cNames[j]);
						generator.writeEndObject();
					}
					generator.writeEndArray();
				}
				int limit = rs.getInt("maxrows");
				//// String execQuery=rs.getString("query");
				if (queryParam != null) {
					String[] queryParams = queryParam.split(",");
					int ps = queryParams.length;
					for (int p = 0; p < ps; p++) {
						query = query.replaceAll("\\$\\(" + queryParams[p] + "\\)",
								parameters.getFirst(queryParams[p]));
					}
				}
				String[] column = columns.split(",");
				int cs = column.length;
				for (int c = 0, ci = 1; c < cs; c++, ci++) {
					if (parameters.getFirst("F" + ci) != null) {
						if (query.toLowerCase().indexOf("where") != -1) {
							query += " and " + column[c] + " like '" + conSearch
									+ parameters.getFirst("F" + ci).replaceAll("'", "''") + "%' ";
						} else {
							query += " where " + column[c] + " like '" + conSearch
									+ parameters.getFirst("F" + ci).replaceAll("'", "''") + "%' ";
						}
					}
				}
				if (parameters.getFirst("udw") != null) {
					query += parameters.getFirst("udw");
				}
				if (DatabaseUtil.databaseType == 0) {
					query += " order by " + orderby;
					if (limit != -1) {
						query += " limit 0," + limit;
					}
				} else if (DatabaseUtil.databaseType == 3) {
					query += " order by " + orderby;
					if (limit != -1) {
						query += " limit " + limit;
					}
				} else if (DatabaseUtil.databaseType == 2) {
					query = query.replaceFirst("select ", "select top " + limit);
				} else if (DatabaseUtil.databaseType == 1) {
					if (query.toLowerCase().indexOf("where") != -1) {
						query += " and rownum<=" + limit;
					} else {
						query += " where rownum<=" + limit;
					}
					query += " order by " + orderby;
				}
				if (DatabaseUtil.databaseType != 0 && DatabaseUtil.databaseType != 3) {
					query += " order by " + orderby;
				}
				System.out.println(query);
				queries.put(id, query);
			}
			ajaxStatement.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queries;
	}

	public static String getReportDetails(String reportId, OutputStream out) {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String result = "";
		//// String query="";
		String parameters = "";
		try {
//			JsonFactory factory=new JsonFactory();
//			JsonGenerator generator=factory.createJsonGenerator(out);
			JsonFactory jsonFactory = new JsonFactory();
			JsonGenerator generator = jsonFactory.createGenerator(out, JsonEncoding.UTF8);
			conn = DatabaseUtil.getConnection();
			stmt = conn.createStatement();
			generator.writeStartObject();
			pstmt = conn.prepareStatement(reportHeader);
			pstmt.setString(1, reportId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				generator.writeStringField("title", rs.getString("report_title"));
				generator.writeStringField("name", rs.getString("report_name"));
				generator.writeStringField("defaultsAjax", rs.getString("default_ajax_id"));
				generator.writeStringField("valuesTo", rs.getString("values_to"));
				generator.writeStringField("valueType", rs.getString("value_type"));
				generator.writeStringField("url", rs.getString("ajax_url"));
				parameters = rs.getString("parameters");
			}
			rs.close();
			pstmt.close();
			String getParametersQuery = "select param_name,param_type,param_ajax_id,ajax_params,ajax_url,is_tag,param_values_from,param_caption from report_common_parameters where param_name in ('"
					+ parameters.replaceAll(",", "','") + "')";
			rs = stmt.executeQuery(getParametersQuery);
			rsmd = rs.getMetaData();
			generator.writeFieldName("params");
			generator.writeStartArray();
			while (rs.next()) {
				generator.writeStartArray();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					generator.writeString(rs.getString(i));
				}
				generator.writeEndArray();
			}
			generator.writeEndArray();
			generator.writeEndObject();
			generator.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	public static void writeField(JsonGenerator generator, String value, String tag) throws Exception {
		generator.writeStartObject();
		generator.writeStringField("v", value);
		if (tag != null) {
			generator.writeStringField("t", tag);
		}
		generator.writeEndObject();
	}

	public static void writeField(JsonGenerator generator, String fieldName, String value, String tag)
			throws Exception {
		generator.writeFieldName(fieldName);
		generator.writeStartObject();
		generator.writeStringField("v", value);
		if (tag != null) {
			generator.writeStringField("t", tag);
		}
		generator.writeEndObject();
	}

	public static String getServerLocation(UriInfo uriInfo) {
		return uriInfo.getAbsolutePath().toString().replace(uriInfo.getBaseUri().getPath(), "")
				.replace(uriInfo.getPath().replaceFirst("/", ""), "");
	}
}