package com.cissol.core.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

//import org.codehaus.jackson.JsonEncoding;
//import org.codehaus.jackson.JsonFactory;
//import org.codehaus.jackson.JsonGenerator;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import jakarta.ws.rs.core.MultivaluedMap;

public class AutoCompleteUtil {
	public static String ajaxQuery = "select ajaxid,query,field,maxrows,orderby,params from ajaxqueries where ajaxid=?";

	public static String getResultJson(MultivaluedMap<String, String> parameters, OutputStream out) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String result = "";
		String query = "";
		try {
//			JsonFactory factory=new JsonFactory();
//			JsonGenerator generator=factory.createJsonGenerator(out,JsonEncoding.UTF8);
			JsonFactory jsonFactory = new JsonFactory();
			JsonGenerator generator = jsonFactory.createGenerator(out, JsonEncoding.UTF8);
			conn = DatabaseUtil.getConnection();
			stmt = conn.createStatement();
			query = getAjaxQuery(conn, parameters);
			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();
			int cs = rsmd.getColumnCount();
			generator.writeStartArray();
			while (rs.next()) {
				generator.writeStartObject();
				generator.writeStringField("id", rs.getString(1));
				generator.writeStringField("label", rs.getString(2));
				generator.writeStringField("value", rs.getString(2));
				for (int i = 3; i <= cs; i++) {
					generator.writeStringField(rsmd.getColumnLabel(i), rs.getString(i));
				}
				generator.writeEndObject();
			}
			generator.writeEndArray();
			rs.close();
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

	public static String getAjaxQuery(Connection conn, MultivaluedMap<String, String> parameters) {
		String query = "";
		String orderby = "";
		String field = "";
		PreparedStatement ajaxStatement = null;
		ResultSet rs = null;
		//// String ajaxQueryReplaced="";
		try {
			ajaxStatement = conn.prepareStatement(ajaxQuery);
			String paramId = parameters.getFirst("id");
			ajaxStatement.setString(1, paramId);
			rs = ajaxStatement.executeQuery();
			while (rs.next()) {
				String queryParam = rs.getString("params");
				query = rs.getString("query");
				orderby = rs.getString("orderby");
				field = rs.getString("field");
				int limit = rs.getInt("maxrows");
				//// String execQuery=rs.getString("query");
				if (queryParam != null) {
					String[] queryParams = queryParam.split(",");
					int ps = queryParams.length;
					for (int p = 0; p < ps; p++) {
						System.out.println(queryParams[p] + " " + parameters.getFirst(queryParams[p]));
						query = query.replaceAll("\\$\\(" + queryParams[p] + "\\)",
								parameters.getFirst(queryParams[p]));
					}
				}
				String term = parameters.getFirst("term");
				if (term != null) {
					term = term.trim();
				}
				if (query.indexOf(" where ") == -1) {
					query += " where " + field + " like '" + term + "%'";
				} else {
					query += " and " + field + " like '" + term + "%'";
				}
				query += " order by " + orderby;
				if (limit != -1) {
					query += " limit 0," + limit;
				}
			}
			ajaxStatement.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return query;
	}
}