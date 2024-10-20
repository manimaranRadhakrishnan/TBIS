package com.cissol.core.services.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.glassfish.jersey.uri.UriComponent;

import com.cissol.core.model.User;
import com.cissol.core.report.ExportReport;
import com.cissol.core.report.WritePDFReport;
import com.cissol.core.util.AjaxResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

@Path("report")
public class ReportResource {
	@Context
	HttpServletRequest req;
	@Context
	HttpServletResponse res;
	@Context 
	UriInfo uriInfo;
	@Context
	SecurityContext securityContext;
	
	private static final SimpleDateFormat formatyyyyMMdd = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

	@Path("/metadata")
	@GET
	@Produces("application/json")
	public StreamingOutput getMetaData() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				PrintWriter p = new PrintWriter(out);
				MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(), true);
				User user = (User) securityContext.getUserPrincipal();
				if (user != null) {
					queryParams.add("login_id", user.getUserName());
					queryParams.add("user_id", String.valueOf(user.getUserId()));
					queryParams.add("user_email", user.getEmailId());
				}
				ExportReport.getReportMetaData(queryParams, p);
			}
		};
	}

	@Path("/params")
	@GET
	@Produces("application/json")
	public StreamingOutput getReportParams() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				PrintWriter p = new PrintWriter(out);
				MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(), true);
				User user = (User) securityContext.getUserPrincipal();
				if (user != null) {
					queryParams.add("login_id", user.getUserName());
					queryParams.add("user_id", String.valueOf(user.getUserId()));
					queryParams.add("user_email", user.getEmailId());
				}
				ExportReport.getReportParameters(queryParams, p);
			}
		};
	}

	@Path("/reportparams")
	@GET
	@Produces("application/json")
	public StreamingOutput getReportParameters() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				try {
					//PrintWriter p = new PrintWriter(out);
					MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(),
							true);
					AjaxResponseUtil.getReportDetails(queryParams.getFirst("id"), out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	@Path("/data")
	@GET
	@Produces("application/json")
	public StreamingOutput getReportData() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				try {
					// PrintWriter p=new PrintWriter(out);
					MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(),
							true);
					User user = (User) securityContext.getUserPrincipal();
					if (user != null) {
						queryParams.add("login_id", user.getUserName());
						queryParams.add("user_id", String.valueOf(user.getUserId()));
						queryParams.add("user_email", user.getEmailId());
					}
					ExportReport.getReportData(queryParams, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	@Path("/writepdf")
	@GET
	@Produces("application/pdf")
	public StreamingOutput getPDF() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				try {
					MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(),
							true);
					User user = (User) securityContext.getUserPrincipal();
					if (user != null) {
						queryParams.add("login_id", user.getUserName());
						queryParams.add("user_id", String.valueOf(user.getUserId()));
						queryParams.add("user_email", user.getEmailId());
					}
					if (queryParams.getFirst("exportname") != null && !"".equals(queryParams.getFirst("exportname"))) {
						res.addHeader("Content-Disposition", "attachment;filename=" + queryParams.getFirst("exportname")
								+ "_" + formatyyyyMMdd.format(new Date()) + ".pdf");
					} else {
						res.addHeader("Content-Disposition",
								"attachment;filename=Report_" + queryParams.getFirst("id") + ".pdf");
					}
					WritePDFReport pdf = new WritePDFReport();
					pdf.writeSimpleDocument(queryParams, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	@Path("/writecsv")
	@GET
	@Produces("text/csv")
	public StreamingOutput getCSV() {
		return new StreamingOutput() {

			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				try {
					MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(uriInfo.getRequestUri(),
							true);
					User user = (User) securityContext.getUserPrincipal();
					if (user != null) {
						queryParams.add("login_id", user.getUserName());
						queryParams.add("user_id", String.valueOf(user.getUserId()));
						queryParams.add("user_email", user.getEmailId());
					}
					if (queryParams.getFirst("exportname") != null && !"".equals(queryParams.getFirst("exportname"))) {
						res.addHeader("Content-Disposition", "attachment;filename=" + queryParams.getFirst("exportname")
								+ "_" + formatyyyyMMdd.format(new Date()) + ".csv");
					} else {
						res.addHeader("Content-Disposition",
								"attachment;filename=export_" + queryParams.getFirst("id") + ".csv");
					}
					ExportReport.writeCSVDocument(queryParams, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
}