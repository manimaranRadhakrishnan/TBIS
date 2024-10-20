package com.tbis.api.master.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.DashboardDetail;
import com.tbis.api.master.model.DashboardSummary;
import com.tbis.api.master.model.LongHaul;
import com.tbis.api.master.model.OverFlowData;
import com.tbis.api.master.model.ScanDetails;
import com.tbis.api.master.model.TBISDashboard;
import com.tbis.api.master.model.VendorFillRate;

public class DashboardService {
	
	
	/* TBIS */
	
	private static final String longHaulCount="select count('x') requested,"
			+ "sum(case when l.statusid=3 then 1 else 0 end) alloted,"
			+ "sum(l.tripno) trips,sum(case when l.delayhours>0 then 1 else 0 end) delays "
			+ "from asnmaster a left join lhvehicleallocation l on l.tripid=a.tripid "
			+ "inner join customer_master cm on cm.customerId =a.customerid ";

	private static final String shortHaulCount="select count('x') requested,"
			+ "sum(case when l.statusid=3 then 1 else 0 end) alloted,"
			+ "sum(tripno) trips,sum(case when delayhours>0 then 1 else 0 end) delays "
			+ "from picklistmaster a left join shvehicleallocation l on l.tripid=a.tripid "
			+ "inner join customer_master cm on cm.customerId =a.customerid ";

	private static final String asnCount="select count(distinct a.customerid) requested,"
			+ "sum(ifnull(l.tripno,0)) trips,sum(case when ifnull(l.delayhours,0)>0 then 1 else 0 end) tat "
			+ "from asnmaster a left join lhvehicleallocation l on l.tripid=a.tripid "
			+ "inner join customer_master cm on cm.customerId =a.customerid ";

	private static final String asnIncidentCount="select count(a.asnid) incidents "
			+ "from asnincidientlog al "
			+ "inner join asnmaster a on a.asnid =al.asnid "
			+ "inner join customer_master cm on cm.customerId =a.customerid ";

	private static final String pickListCount="select count(distinct a.picklistid) requested,"
			+ "sum(ifnull(l.tripno,0)) trips,sum(case when ifnull(l.delayhours,0)>0 then 1 else 0 end) tat "
			+ "from picklistmaster a left join shvehicleallocation l on l.tripid=a.tripid "
			+ "inner join customer_master cm on cm.customerId =a.customerid ";

	private static final String pickListIncidentCount="select distinct 0 incidents "
			+ "from asnincidientlog al  "
			+ "inner join asnmaster a on a.asnid =al.asnid "
			+ "inner join customer_master cm on cm.customerId =a.customerid ";

	private static final String overflowItems="select pm.customerpartcode,count('x') stock "
			+ "from asnscancodes ac "
			+ "inner join asnmaster a on a.asnid =ac.asnid "
			+ "inner join asndetail d on ac.asndetailid=d.asndetailid "
			+ "inner join part_master pm on pm.partid=d.partid "
			+ "inner join customer_master cm on cm.customerId =a.customerid "
			+ "where processid=6 ";

	private static final String getASNWidget="select supplydate,cm.customername,ws.statusname  from asnmaster a "
			+ "inner join customer_master cm  on (cm.customerId=a.customerid) "
			+ "inner join wms_statusmaster ws ON (ws.statusid=a.asnstatus) "
			+ "where asnstatus <> 10";
	
	/* TBIS Completed */

	private static final String getCustomerTransactionSummary="select sum(totalasn) totalasn,"
			+ "sum(totalcardconfirm) totalcardconfirm,"
			+ "sum(totalaccepted) totalaccepted,"
			+ "sum(totaldispatchconfirm) totaldispatchconfirm,"
			+ "sum(totaldispatch) totaldispatch,"
			+ "sum(totalreceipts) totalreceipts,"
			+ "sum(totaltimedelay) totaltimedelay,"
			+ "sum(totalqtymismatch) totalqtymismatch,"
			+ "sum(totalsupplydelay) totalsupplydelay,parts,customers,shortage,excess  "
			+ "from parttatsummary p inner join customer_master c on c.customerid=p.customerid "
			+ "  ";
	private static final String getVendorSupplyDetailsQry ="select c.customerId,c.customer_erp_code,c.customername,sum(totalasn) totalasn,sum(totalaccepted) totalaccepted,sum(totaldispatch) totaldispatch, sum(totalreceipts) totalreceipts, sum(totalcardconfirm) totalcardconfirm, sum(totaldispatchconfirm) totaldispatchconfirm, sum(totalaccepted-totalreceipts+(totalAsn-totalaccepted)) totalpending,min(p.transdate) trandate "
			+ " from customer_master c left join parttatsummary p on c.customerid=p.customerid  left join part_master pm on p.partid=pm.partid left join user_master u "
			+ " on u.role_id=3 and u.user_id=?  and u.user_id=c.userid where case when ?=3 then u.user_id else ? end =? and p.transdate>=str_to_date(?,'%d-%m-%Y') and p.transdate<=str_to_date(?,'%d-%m-%Y') "
			+ " group by c.customerId,c.customer_erp_code,c.customername order by trandate desc";

	private static final String getPartDetailsQry ="select pm.partid,pm.partno,pm.partdescription,sum(totalasn) totalasn,sum(totalaccepted) totalaccepted,sum(totaldispatch) totaldispatch, sum(totalreceipts) totalreceipts, sum(totalcardconfirm) totalcardconfirm, sum(totaldispatchconfirm) totaldispatchconfirm,sum(totalaccepted-totalreceipts) totalpending "
			+ " from parttatsummary p inner join customer_master c on c.customerid=p.customerid inner join part_master pm on p.partid=pm.partid left join user_master u "
			+ " on u.role_id=3 and u.user_id=?  and u.user_id=c.userid where case when ?=3 then u.user_id else ? end =? and p.transdate>=str_to_date(?,'%d-%m-%Y') and p.transdate<=str_to_date(?,'%d-%m-%Y') and p.totaldispatch>0 "
			+ " group by pm.partid,pm.partno,pm.partdescription having sum(totalaccepted-totaldispatchconfirm)>0 ";

	public ApiResult<DashboardSummary> getDashboardSummary(String fromDate,String toDate,int roleId,int userId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<DashboardSummary> result = new ApiResult<DashboardSummary>();
		DashboardSummary l = new DashboardSummary();
		try {
			conn = DatabaseUtil.getConnection();
			StringBuffer s=new StringBuffer(100);
			s.append(getCustomerTransactionSummary);
			if(roleId==3) {
				s.append(" and c.userid=?");
				s.append(" inner join (select count('x') parts,1 customers,customerid from customer_parts_line_map m group by customerid) part on c.customerid=part.customerid ");
				s.append(" left join (select ifnull(sum(case when floor(received_spq-spq)> 0 then floor(received_spq-spq) else 0 end),0) excess,ifnull(sum(case when floor(received_spq-spq)< 0 then floor(received_spq-spq) else 0 end),0) shortage ,a.customerid from asndetail ad inner join asnmaster a on a.asnid =ad.asnid WHERE qtyexception=1 and ad.received_status=2 and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y') group by customerid) asn on c.customerid=asn.customerid ");
			}else {
				s.append(" inner join (select count('x') customers from customer_master) cust on 1=1 ");
				s.append(" inner join (select count('x') parts from part_master ) part on 1=1 ");
				s.append(" left join (select ifnull(sum(case when floor(received_spq-spq)> 0 then floor(received_spq-spq) else 0 end),0) excess,ifnull(sum(case when floor(received_spq-spq)< 0 then floor(received_spq-spq) else 0 end),0) shortage from asndetail ad inner join asnmaster a on a.asnid =ad.asnid WHERE qtyexception=1 and ad.received_status=2 and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')) asn on 1=1 ");
			}
			s.append(" where transdate>=str_to_date(?,'%d-%m-%Y') and transdate<=str_to_date(?,'%d-%m-%Y') ");
			s.append("group by parts,customers,shortage,excess");
			if("".equals(fromDate)) {
				SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
				fromDate=format.format(new Date());
				toDate=fromDate;
			}
			pstmt = conn.prepareStatement(s.toString());
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
				pstmt.setString(4, fromDate);
				pstmt.setString(5, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
				pstmt.setString(3, fromDate);
				pstmt.setString(4, toDate);
			}
			rs = pstmt.executeQuery();
			if(rs.next()) {
				l.setTotalAsn(rs.getInt("totalasn"));
				l.setTotalAccepted(rs.getInt("totalaccepted"));
				l.setTotalDispatch(rs.getInt("totaldispatch"));
				l.setTotalReceipts(rs.getInt("totalreceipts"));
				l.setTotalTatException(rs.getInt("totaltimedelay"));
				l.setTotalQtyException(rs.getInt("totalqtymismatch"));
				l.setTotalSupplyException(rs.getInt("totalsupplydelay"));
				l.setTotalCardconfirm(rs.getInt("totalcardconfirm"));
				l.setTotalDispatchconfirm(rs.getInt("totaldispatchconfirm"));
				l.setTotalGaps(rs.getInt("totalsupplydelay")+rs.getInt("totalqtymismatch")+rs.getInt("totaltimedelay"));
				l.setTotalVendors(rs.getInt("customers"));
				l.setTotalParts(rs.getInt("parts"));
				l.setTotalShortage(rs.getInt("shortage"));
				l.setTotalExcess(rs.getInt("excess"));
				result.isSuccess=true;
				result.result = l;
			}else {
				result.isSuccess=false;
				result.result = null;
				result.message="Invalid card detail";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.isSuccess = false;
			result.message = e.getMessage();
			result.result = null;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<DashboardDetail> getVendorSupplyDetails(String fromDate,String toDate,int roleId,int userId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<DashboardDetail> result = new ApiResult<DashboardDetail>();
		DashboardDetail detail=new DashboardDetail();
		ArrayList<VendorFillRate> vendorDetail =new ArrayList<VendorFillRate>();
		ArrayList<VendorFillRate> partDetail =new ArrayList<VendorFillRate>();
		try {
			conn = DatabaseUtil.getConnection();
			StringBuffer s=new StringBuffer(100);
			s.append(getVendorSupplyDetailsQry);
			if("".equals(fromDate)) {
				SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
				fromDate=format.format(new Date());
				toDate=fromDate;
			}
			pstmt = conn.prepareStatement(s.toString());
			pstmt.setInt(1, userId);
			pstmt.setInt(2, roleId);
			pstmt.setInt(3, userId);
			pstmt.setInt(4, userId);
			pstmt.setString(5, fromDate);
			pstmt.setString(6, toDate);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				VendorFillRate l = new VendorFillRate();
				l.setCustomerId(rs.getInt("customerId"));
				l.setCustomerCode(rs.getString("customer_erp_code"));
				l.setCustomerName(rs.getString("customername"));
				l.setTotalAsn(rs.getInt("totalasn"));
				l.setTotalConfirmed(rs.getInt("totalcardconfirm"));
				l.setTotalAccepted(rs.getInt("totalaccepted"));
				l.setTotalDispatch(rs.getInt("totaldispatch"));
				l.setTotalDispatchConfirmed(rs.getInt("totaldispatchconfirm"));
				l.setTotalReceipts(rs.getInt("totalreceipts"));
				l.setTotalPending(rs.getInt("totalpending"));
				l.setFillRate(0.00);
				vendorDetail.add(l);
			}
			rs.close();
			pstmt.close();
			s.setLength(0);
			s.append(getPartDetailsQry);
			if("".equals(fromDate)) {
				SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
				fromDate=format.format(new Date());
				toDate=fromDate;
			}
			pstmt = conn.prepareStatement(s.toString());
			pstmt.setInt(1, userId);
			pstmt.setInt(2, roleId);
			pstmt.setInt(3, userId);
			pstmt.setInt(4, userId);
			pstmt.setString(5, fromDate);
			pstmt.setString(6, toDate);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				VendorFillRate l = new VendorFillRate();
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setPartNo(rs.getString("partno"));
				l.setTotalAsn(rs.getInt("totalasn"));
				l.setTotalAccepted(rs.getInt("totalaccepted"));
				l.setTotalDispatch(rs.getInt("totaldispatch"));
				l.setTotalReceipts(rs.getInt("totalreceipts"));
				l.setTotalPending(rs.getInt("totalpending"));
				l.setFillRate(0.00);
				partDetail.add(l);
			}
			detail.setVendorDetail(vendorDetail);
			detail.setPartDetail(partDetail);
			result.isSuccess=true;
			result.result = detail;
			
		} catch (Exception e) {
			e.printStackTrace();
			result.isSuccess = false;
			result.message = e.getMessage();
			result.result = null;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<TBISDashboard> getLongShorHaulSummary(String fromDate,String toDate,int roleId,int userId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<TBISDashboard> result = new ApiResult<TBISDashboard>();
		TBISDashboard l = new TBISDashboard();
		LongHaul longHaul= new LongHaul();
		LongHaul shortHaul= new LongHaul();
		LongHaul asnData= new LongHaul();
		LongHaul deliveryData= new LongHaul();
		ArrayList<OverFlowData> overflowData=new ArrayList<OverFlowData>();

		try {
			conn = DatabaseUtil.getConnection();
			StringBuffer s=new StringBuffer(100);

			if("".equals(fromDate)) {
				SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
				fromDate=format.format(new Date());
				toDate=fromDate;
			}
			
			s.append(longHaulCount);
			if(roleId==3) {
				s.append(" where cm.userid=? and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}else {
				s.append(" where a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}
			
			System.out.print(":::::::1::::::  " + s.toString());
			System.out.print(":::::::2::::::  " + roleId);
			System.out.print(":::::::3::::::  " + userId);
			System.out.print("::::::::4:::::  " + fromDate);
			System.out.print(":::::::5::::::  " + toDate);
			
			pstmt = conn.prepareStatement(s.toString());
			pstmt.clearParameters();
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
			}
			rs = pstmt.executeQuery();
			if(rs.next()) {
				longHaul.setTotalRequested(rs.getInt("requested"));
				longHaul.setTotalAlloted(rs.getInt("alloted"));
				longHaul.setTotalTrips(rs.getInt("trips"));
				longHaul.setTotalDelays(rs.getInt("delays"));
			}
			rs.close();
			pstmt.close();
			l.setLongHaul(longHaul);
			s.setLength(0);
			s.append(shortHaulCount);
			if(roleId==3) {
				s.append(" where cm.userid=? and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}else {
				s.append(" where a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}
			pstmt = conn.prepareStatement(s.toString());
			pstmt.clearParameters();
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
			}
			rs = pstmt.executeQuery();
			if(rs.next()) {
				shortHaul.setTotalRequested(rs.getInt("requested"));
				shortHaul.setTotalAlloted(rs.getInt("alloted"));
				shortHaul.setTotalTrips(rs.getInt("trips"));
				shortHaul.setTotalDelays(rs.getInt("delays"));
			}
			rs.close();
			pstmt.close();
			l.setShortHaul(shortHaul);
			s.setLength(0);
			s.append(asnCount);
			if(roleId==3) {
				s.append(" where cm.userid=? and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}else {
				s.append(" where a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}
			pstmt = conn.prepareStatement(s.toString());
			pstmt.clearParameters();
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
			}
			rs = pstmt.executeQuery();
			if(rs.next()) {
				asnData.setTotalCustomers(rs.getInt("requested"));
				asnData.setTotalTats(rs.getInt("tat"));
				asnData.setTotalTrips(rs.getInt("trips"));
			}
			rs.close();
			pstmt.close();
			s.setLength(0);
			s.append(asnIncidentCount);
			if(roleId==3) {
				s.append(" where cm.userid=? and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}else {
				s.append(" where a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}
			pstmt = conn.prepareStatement(s.toString());
			pstmt.clearParameters();
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
			}
			rs = pstmt.executeQuery();
			if(rs.next()) {
				asnData.setTotalAbnormalities(rs.getInt("incidents"));
			}
			rs.close();
			pstmt.close();
			l.setAsnData(asnData);
			s.setLength(0);
			s.append(pickListCount);
			if(roleId==3) {
				s.append(" where cm.userid=? and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}else {
				s.append(" where a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}
			pstmt = conn.prepareStatement(s.toString());
			pstmt.clearParameters();
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
			}
			rs = pstmt.executeQuery();
			if(rs.next()) {
				deliveryData.setTotalCustomers(rs.getInt("requested"));
				deliveryData.setTotalTats(rs.getInt("tat"));
				deliveryData.setTotalTrips(rs.getInt("trips"));
			}
			rs.close();
			pstmt.close();
			s.setLength(0);
			s.append(pickListIncidentCount);
			if(roleId==3) {
				s.append(" where cm.userid=? and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}else {
				s.append(" where a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y')");
			}
			pstmt = conn.prepareStatement(s.toString());
			pstmt.clearParameters();
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
			}
			rs = pstmt.executeQuery();
			if(rs.next()) {
				deliveryData.setTotalAbnormalities(rs.getInt("incidents"));
			}
			rs.close();
			pstmt.close();
			l.setDeliveryData(deliveryData);
			s.setLength(0);
			s.append(overflowItems);
			if(roleId==3) {
				s.append(" and cm.userid=? and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y') ");
			}else {
				s.append(" and a.supplydate>=str_to_date(?,'%d-%m-%Y') and a.supplydate<=str_to_date(?,'%d-%m-%Y') ");
			}
			s.append(" group by pm.customerpartcode order by stock desc limit 0,4 ");
			pstmt = conn.prepareStatement(s.toString());
			pstmt.clearParameters();
			if(roleId==3) {
				pstmt.setInt(1,userId);
				pstmt.setString(2, fromDate);
				pstmt.setString(3, toDate);
			}else {
				pstmt.setString(1, fromDate);
				pstmt.setString(2, toDate);
			}
			rs = pstmt.executeQuery();
			while(rs.next()) {
				OverFlowData d=new OverFlowData();
				d.setCustomerName(rs.getString("customerpartcode"));
				d.setTotalCount(rs.getInt("stock"));
				overflowData.add(d);
			}
			rs.close();
			pstmt.close();
			l.setOverflowData(overflowData);
			result.isSuccess=true;
			result.result = l;
		} catch (Exception e) {
			e.printStackTrace();
			result.isSuccess = false;
			result.message = e.getMessage();
			result.result = null;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	public static DashboardService getInstance() {
		return new DashboardService();
	}
}
