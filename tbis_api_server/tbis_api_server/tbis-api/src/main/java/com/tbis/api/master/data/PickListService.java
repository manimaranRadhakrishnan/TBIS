package com.tbis.api.master.data;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.format.CellGeneralFormatter;
import org.apache.poi.ss.format.CellNumberFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.InvoiceDetails;
import com.tbis.api.master.model.LineSpacePartConfig;
import com.tbis.api.master.model.LoadingDetails;
import com.tbis.api.master.model.PickListDetail;
import com.tbis.api.master.model.PickListMaster;
import com.tbis.api.master.model.PickWorkFlowInput;
import com.tbis.api.master.model.ScanDetails;
import com.tbis.api.master.model.VehicleDetails;

public class PickListService {
		private static final String insertQuery="INSERT INTO picklistmaster(scheduleno, customerid, supplydate, supplytime, unloadingdoc, usagelocation,statusid, createdby, createddate, tsid,locationid,sublocationid,warehouseid,parentpicklistid) VALUES(?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()),?,?,?,?);"; 
		private static final String modifyQuery="UPDATE picklistmaster SET scheduleno=?, customerid=?, supplydate=?, supplytime=?, unloadingdoc=?, usagelocation=?,statusid=?, updatedby=?, updateddate=now(), tsid=getcurrenttsid(now()) WHERE picklistid=?";
		private static final String select="SELECT p.picklistid, p.scheduleno, p.customerid, p.supplydate, p.supplytime,p.unloadingdoc, p.usagelocation,p.statusid,p.parentpicklistid,"
				+ "p.vehicleno,p.picked_by,p.staged_by,p.kanban_attached_by,p.kanban_tapping_by,p.scan_by,p.loading_supervised_by,p.doc_handoverby_id,"
				+ "p.dock_audit_by,p.vehicle_assigned_by,ifnull (pu.user_name,'--') pickedByName, ifnull (su.user_name,'--') stagedByName, "
				+ "ifnull (kau.user_name,'--') kanbanAttachedByName, ifnull (ktu.user_name,'--') kanbanTapingByName, "
				+ "ifnull (scu.user_name,'--') scanByName, ifnull (lsu.user_name,'--') loadingSupervisorByName, "
				+ "ifnull (dhu.user_name,'--') docHandoverByName, ifnull (dau.user_name,'--') docAuditByName, "
				+ "ifnull (vau.user_name,'--') vehicleAssignedByName FROM picklistmaster p "
				+ "left join user_master pu on pu.user_id =p.picked_by "
				+ "left join user_master su on su.user_id =p.staged_by "
				+ "left join user_master kau on kau.user_id =p.kanban_attached_by "
				+ "left join user_master ktu on ktu.user_id =p.kanban_tapping_by "
				+ "left join user_master scu on scu.user_id =p.scan_by "
				+ "left join user_master lsu on lsu.user_id =p.loading_supervised_by "
				+ "left join user_master dhu on dhu.user_id =p.doc_handoverby_id "
				+ "left join user_master dau on dau.user_id =p.dock_audit_by "
				+ "left join user_master vau on vau.user_id =p.vehicle_assigned_by WHERE picklistid=?;";
		
		private static final String picklistStagingQuery="SELECT p.picklistid, p.scheduleno, p.customerid, p.supplydate, p.supplytime,p.unloadingdoc, p.usagelocation,"
				+ "p.vehicleno,p.picked_by,p.staged_by,p.kanban_attached_by,p.kanban_tapping_by,p.scan_by,p.loading_supervised_by,p.doc_handoverby_id,"
				+ "p.dock_audit_by,p.vehicle_assigned_by,ifnull (pu.user_name,'--') pickedByName, ifnull (su.user_name,'--') stagedByName, "
				+ "ifnull (kau.user_name,'--') kanbanAttachedByName, ifnull (ktu.user_name,'--') kanbanTapingByName, "
				+ "ifnull (scu.user_name,'--') scanByName, ifnull (lsu.user_name,'--') loadingSupervisorByName, "
				+ "ifnull (dhu.user_name,'--') docHandoverByName, ifnull (dau.user_name,'--') docAuditByName, "
				+ "ifnull (vau.user_name,'--') vehicleAssignedByName FROM picklistmaster p "
				+ "left join user_master pu on pu.user_id =p.picked_by "
				+ "left join user_master su on su.user_id =p.staged_by "
				+ "left join user_master kau on kau.user_id =p.kanban_attached_by "
				+ "left join user_master ktu on ktu.user_id =p.kanban_tapping_by "
				+ "left join user_master scu on scu.user_id =p.scan_by "
				+ "left join user_master lsu on lsu.user_id =p.loading_supervised_by "
				+ "left join user_master dhu on dhu.user_id =p.doc_handoverby_id "
				+ "left join user_master dau on dau.user_id =p.dock_audit_by "
				+ "left join user_master vau on vau.user_id =p.vehicle_assigned_by WHERE p.statusid=?;";
		
		private static final String insertDtlQuery="INSERT INTO picklistdetail(picklistid, kanbanno, partid, supplydate, supplytime, unloadingdoc, usagelocation, spq, perbinqty, qty_received, noofpackackes, stagingstate, createdby, createddate, tsid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));"; 
		private static final String modifyDtlQuery="UPDATE picklistdetail set picklistid=?, kanbanno=?, partid=?, supplydate=?, supplytime=?, unloadingdoc=?, usagelocation=?, spq=?, perbinqty=?, qty_received=?, noofpackackes=?, stagingstate=?, createdby=?, createddate=now(), tsid=getcurrenttsid(now()) WHERE picklistidetailid=?;";
		private static final String selectDtl="SELECT picklistidetailid, picklistid, kanbanno, d.partid, supplydate, supplytime, unloadingdoc, usagelocation, d.spq qty, perbinqty binqty, qty_received, noofpackackes, stagingstate,partdescription partname,p.partno partno,p.repackqty FROM picklistdetail d inner join part_master p on d.partid=p.partid WHERE picklistid=?";
		
		private static final String insertImportData="INSERT INTO picklistimport(vendorcode, supplydate, supplytime, partno,qty,unloadingdoc,usageloc,kanbanno,createdby,createddate) VALUES (?,?,?,?,?,?,?,?,?,now());";
		
		private static final String insertSelectedPart="insert into selectedpicklist(picklistdataid,userid,customerid)values(?,?,?)";
		private static final String removeSelectedPart="delete from selectedpicklist where picklistdataid=? and userid=? and customerid=? ";
		private static final String clearAllSelectedPart="delete from selectedpicklist where userid=? ";
		
		private static final String insertSelectedVehiclePart="insert into selectedassignvehiclelist(picklistidetailid,userid,customerid)values(?,?,?)";
		private static final String removeSelectedVehiclePart="delete from selectedassignvehiclelist where picklistidetailid=? and userid=? and customerid=? ";
		private static final String clearAllSelectedVehiclePart="delete from selectedassignvehiclelist where userid=? ";
		
		private static final String selectedCustomers="select distinct customerid from selectedpicklist  where userid=? ";
		private static final String insPickListParts="INSERT INTO picklistdetail(picklistid, kanbanno, partid, supplydate, supplytime, "
				+ "unloadingdoc, usagelocation, spq, perbinqty, qty_received, noofpackackes, stagingstate,picklistdataid, "
				+ "createdby, createddate, tsid) "
				+ "SELECT ?,kanbanno,p.partid,str_to_date(a.supplydate,'%d-%b-%Y'),a.supplytime,a.unloadingdoc,a.usageloc,a.qty,a.qty/p.binqty,0,0,0,a.piclistdataid,?,now(),getcurrenttsid(now())  FROM picklistimport a inner join  `part_master` p on p.partno=a.partno inner join customer_master c on c.customer_erp_code=a.vendorcode inner join plants pl on left(a.usageloc,2)=pl.plantshorname inner join selectedpicklist sp on sp.picklistdataid=a.piclistdataid and sp.userid=? and sp.customerid=? and a.picklistid=0 ";
		private static final String insertPickMasterQuery="INSERT INTO picklistmaster(scheduleno, customerid, supplydate, supplytime, statusid, createdby, createddate, tsid) VALUES(getcurrenttsid(now()),?,now(),DATE_FORMAT(now(),'%H:%m'),2,?,now(),getcurrenttsid(now()));";
		private static final String updatePickListId="update picklistimport a inner join selectedpicklist sp on sp.picklistdataid=a.piclistdataid set picklistid=? where sp.userid=? and sp.customerid=? and a.picklistid=0";
		
		//old picklist stock detail for FIFO
		private static final String getStockDetail="select asnid, asndetailid, s.partid, tbisscancode, customerscancode, processid, "
				+ "s.linespacepartconfigid, d.picklistid, stockmovementdetailid,partspacename,allocatedbins,usedspace,"
				+ "fifoorder fifoorder,fromlineno,tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,"
				+ "ifnull(r.linerackcode,'') linerackcode,ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,"
				+ "ifnull(fromlinespaceid,0) fromlinespaceid,ifnull(tolinespaceid,0) tolinespaceid,"
				+ "'' fromlinespacecode,'' tolinespacecode,d.picklistidetailid "
				+ "from picklistdetail d  "
				+ "inner join stockmovementdetail s on s.partid=? "
				+ "inner join linespacepartconfig c on s.linespacepartconfigid=c.linespacepartconfigid "
				+ "left join linerack r on r.linerackid=c.linerackid "
				+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid where s.picklistid=0 and d.picklistidetailid=? "
				+ "order by stockmovementdetailid  limit 0,?";

		//new picklist stock detail - get only the linespace
		private static final String getStockDetailLineSpace="select s.partid, "
				+ "s.linespacepartconfigid, d.picklistid, partspacename,allocatedbins,usedspace,"
				+ "fifoorder fifoorder,fromlineno,tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,"
				+ "ifnull(r.linerackcode,'') linerackcode,ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,"
				+ "ifnull(fromlinespaceid,0) fromlinespaceid,ifnull(tolinespaceid,0) tolinespaceid,"
				+ "'' fromlinespacecode,'' tolinespacecode,d.picklistidetailid,count('x') totalavailablestock  "
				+ "from picklistdetail d  "
				+ "inner join stockmovementdetail s on s.partid=? "
				+ "inner join linespacepartconfig c on s.linespacepartconfigid=c.linespacepartconfigid "
				+ "left join linerack r on r.linerackid=c.linerackid "
				+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid where s.picklistid in (0,?) and d.picklistidetailid=? "
				+ "group by s.partid, "
				+ "s.linespacepartconfigid, d.picklistid,partspacename,allocatedbins,usedspace,"
				+ "fifoorder,fromlineno,tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,"
				+ "ifnull(r.linerackcode,''),ifnull(rc.linerackcompartmentname,''),"
				+ "ifnull(fromlinespaceid,0),ifnull(tolinespaceid,0),d.picklistidetailid ";

		private static final String getScannedStockDetail="select asnid, asndetailid, s.partid, tbisscancode, customerscancode, processid, "
				+ "s.linespacepartconfigid, d.picklistid, stockmovementdetailid,partspacename,allocatedbins,usedspace,"
				+ "fifoorder fifoorder,fromlineno,tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,"
				+ "ifnull(r.linerackcode,'') linerackcode,ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,"
				+ "ifnull(fromlinespaceid,0) fromlinespaceid,ifnull(tolinespaceid,0) tolinespaceid,"
				+ "'' fromlinespacecode,'' tolinespacecode,d.picklistidetailid "
				+ "from picklistdetail d  "
				+ "inner join stockmovementdetail s on s.partid=? "
				+ "inner join linespacepartconfig c on s.linespacepartconfigid=c.linespacepartconfigid "
				+ "left join linerack r on r.linerackid=c.linerackid "
				+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid where s.picklistid=? and d.picklistidetailid=? ";

		//piclist part scan
		private static final String scanPickListPartDetail="select asnid, asndetailid, s.partid, tbisscancode, customerscancode, processid, "
				+ "s.linespacepartconfigid, d.picklistid, stockmovementdetailid,partspacename,allocatedbins,usedspace,"
				+ "fifoorder fifoorder,fromlineno,tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,"
				+ "ifnull(r.linerackcode,'') linerackcode,ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,"
				+ "ifnull(fromlinespaceid,0) fromlinespaceid,ifnull(tolinespaceid,0) tolinespaceid,"
				+ "'' fromlinespacecode,'' tolinespacecode,d.picklistidetailid "
				+ "from picklistdetail d  "
				+ "inner join stockmovementdetail s on s.partid=? "
				+ "inner join linespacepartconfig c on s.linespacepartconfigid=c.linespacepartconfigid "
				+ "left join linerack r on r.linerackid=c.linerackid "
				+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid "
				+ "where s.picklistid=0 and d.picklistidetailid=? and (s.tbisscancode=? or s.customerscancode=?) ";

		private static final String selectPartDtl="select picklistidetailid, picklistid, kanbanno, d.partid, supplydate, supplytime, unloadingdoc, usagelocation, d.spq qty, perbinqty binqty, qty_received, noofpackackes, stagingstate,partdescription partname,p.partno partno,p.repackqty,p.exclusiveclubno FROM picklistdetail d inner join part_master p on d.partid=p.partid WHERE d.picklistidetailid=?";
		private static final String selectChildParts="select partid,partdescription from part_master where finalpartid=? ";
		private static final String updatePickListStatus="update picklistmaster set statusid=?,picked_by=?, pickingcompletedtime=now(), tsid=getcurrenttsid(now()) where picklistid=? ";
		private static final String updatePickListStock="update stockmovementdetail s inner join linespacepartconfig c on s.linespacepartconfigid=c.linespacepartconfigid set s.picklistid=?,c.usedspace=c.usedspace-1,s.lineusageid=2 where s.stockmovementdetailid=? ";
		
		private static final String insUploadMaster="INSERT INTO picklistmaster (scheduleno, customerid, supplydate, supplytime, sublocationid, warehouseid, locationid, usagelocation,statusid, createdby, createddate, tsid) VALUES(?,?,?,?,?,?,?,?,11,?,now(),getcurrenttsid(now()));";
		private static final String insUploadDetails="INSERT INTO picklistdetail (picklistid, kanbanno, partid, supplydate, supplytime, unloadingdoc, usagelocation, spq, perbinqty, qty_received, noofpackackes, stagingstate, picklistdataid, createdby, createddate, tsid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,now(),getcurrenttsid(now()));";
		private static final String selectDistinctPickList="SELECT distinct c.customerid,pl.plantid,date_format(str_to_date(a.supplydate,'%d-%b-%Y'),'%Y-%m-%d') supplydate,a.supplytime,left(a.unloadingdoc,2) usageloc,c.primarysublocationid,s.warehouseid,w.locationid from picklistimport a "
				+ "inner join customer_master c on c.customer_erp_code=a.vendorcode "
				+ "inner join sublocation s on s.sublocationid=c.primarysublocationid "
				+ "inner join warehouse w on w.warehousid=s.warehouseid "
				+ "inner join plants pl on left(a.unloadingdoc,2)=pl.plantshorname and a.picklistid=0";
		private static final String insPickListPartsNew="INSERT INTO picklistdetail(picklistid, kanbanno, partid, supplydate, supplytime, "
				+ "unloadingdoc, usagelocation, spq, perbinqty, qty_received, noofpackackes, stagingstate,picklistdataid, "
				+ "createdby, createddate, tsid) "
				+ "SELECT ?,kanbanno,p.partid,str_to_date(a.supplydate,'%d-%b-%Y'),"
				+ "a.supplytime,a.unloadingdoc,a.usageloc,a.qty,a.qty/p.obinqty,0,0,0,a.piclistdataid,?,now(),getcurrenttsid(now())  FROM picklistimport a inner join  `part_master` p on p.partno=a.partno inner join customer_master c on c.customer_erp_code=a.vendorcode inner join plants pl on left(a.unloadingdoc,2)=pl.plantshorname where c.customerid=? and date_format(str_to_date(a.supplydate,'%d-%b-%Y'),'%Y-%m-%d')=? and a.supplytime=? and pl.plantid=? and a.picklistid=0 "
				+ "and not exists (select pd.kanbanno,cm.customer_erp_code from picklistdetail pd inner join picklistmaster pm on pm.picklistid =pd.picklistid inner join customer_master cm on cm.customerId=? where pd.kanbanno=a.kanbanno and cm.customer_erp_code=a.vendorcode) ";
		private static final String updatePickListIdNew="update picklistimport a inner join customer_master c on c.customer_erp_code=a.vendorcode inner join plants pl on left(a.unloadingdoc,2)=pl.plantshorname set a.picklistid=? where c.customerid=? and date_format(str_to_date(a.supplydate,'%d-%b-%Y'),'%Y-%m-%d')=? and a.supplytime=? and pl.plantid=? and a.picklistid=0";
		private static final String updateSVehicleDtl ="update picklistdetail set vehicleno=?, vehicleid=?,tripid=? where picklistid=?";
		private static final String updateSVehicleHdr ="update picklistmaster set vehicle_assigned_by =? ,vehicleno=? ,statusid=?,tripid=? where picklistid=?";
		private static final String updateInvoiceDtl ="update picklistdetail set invoiceno=?,unloadnumber=?,ewaybillno=? where picklistidetailid=?";
		
		private static final String insertImportGateEntryData="insert into gateentryimport(vendorcode,partcode,billno,pono,unloadno,vehicleno,msilentrytime,msilexittime,psrvqty,srvno) values (?,?,?,?,?,?,?,?,?,?);";
		private static final String insertImportSrvData="insert into srvimport(vendorcode,challanno,challandate,partno,partname,unloadno,entrydate,srvno,srvdate,invno,exciseamt,cgstamt,sgstamt,igstamt,qty,`57f2no`,status) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		
		private static final String shVehicleRequested="INSERT INTO shvehicleallocation (tripdate, vechicleno, tripno, triptype, statusid,created_by, created_date,tsid) VALUES(now(), ?, 1 ,2, ?, ?,now(),getcurrenttsid(now()));";
		private static final String shVehicleRequestUpdate="update shvehicleallocation set  vechicleno=?, tripno=1, statusid=?,update_by, updated_date=now() where tripid=?";
		
		private static final String updateProcessStatus="update picklistmaster set picked_by=?,kanban_attached_by=?,staged_by=? ,processstatusid=? where picklistid=?";
		
//		private static final String selectPartDtlKanban="SELECT picklistidetailid, d.picklistid, kanbanno, d.partid, supplydate, supplytime, unloadingdoc, usagelocation, d.spq qty, perbinqty binqty, qty_received, noofpackackes, stagingstate,partdescription partname,p.partno partno,p.repackqty,p.exclusiveclubno "
//				+ "FROM picklistdetail d inner join part_master p on d.partid=p.partid "
//				+ "WHERE d.picklistid=? and p.partno=? and d.usagelocation=? and supplydate=str_to_date(?,'%d/%m/%Y') and str_to_date(supplytime,'%H:%i:%s')=str_to_date(?,'%h:%i %p')"; 
//		
		private static final String selectPartDtlKanban="SELECT picklistidetailid, d.picklistid, kanbanno, d.partid, supplydate, supplytime, unloadingdoc, usagelocation, d.spq qty, "
				+ "perbinqty binqty, qty_received, noofpackackes, stagingstate,partdescription partname,p.partno partno,p.repackqty,p.exclusiveclubno "
				+ "FROM picklistdetail d "
				+ "inner join part_master p on d.partid=p.partid "
				+ "WHERE d.picklistid=? and p.partno=? and d.usagelocation=? and supplydate=str_to_date(?,'%d/%m/%Y') and str_to_date(supplytime,'%H:%i:%s')=str_to_date(?,'%h:%i %p')"; 

		private static final String selectPartBinScan="SELECT picklistidetailid, d.picklistid, kanbanno, d.partid, "
				+ "supplydate, supplytime, unloadingdoc, usagelocation, d.spq qty, perbinqty binqty, "
				+ "qty_received, noofpackackes, stagingstate,partdescription partname,p.partno partno,"
				+ "p.repackqty,s.stockmovementdetailid FROM picklistdetail d "
				+ "inner join part_master p on p.partid=? "
				+ "inner join stockmovementdetail s on s.lineusageid=2 and s.tbisscancode=? and s.picklistid=? "
				+ "and p.partid=s.partid and s.picklistid=d.picklistidetailid and d.stagingstate=0 and s.processid=0 where d.picklistidetailid=?";
		
//		private static final String clearPartBinScan="update stockmovementdetail set processid = 0 where picklistid = ?";
		private static final String clearPartBinScan="update stockmovementdetail set processid = 0 where picklistid in (select picklistidetailid from picklistdetail where picklistid=?)";

		private static final String updatePickListDetail="update picklistdetail set qty_received=qty_received+1 where picklistidetailid=? ";
		private static final String updatePickListDetailState="update picklistdetail set stagingstate=1 where picklistidetailid=? and perbinqty=qty_received ";
		private static final String updateStockMovementDetailProcessStatus="update stockmovementdetail set processid=1 where stockmovementdetailid=?";
		
		private static final String pickListDetailIds="select picklistidetailid from picklistdetail where picklistid=?";
		private static final String reducePartDetailGoodStock="update part_stock_detail s inner join picklistdetail p on s.partid=p.partid and p.picklistid=? inner join picklistmaster pm on pm.picklistid=p.picklistid and pm.sublocationid=s.sublocationid set s.goodstock=s.goodstock-p.spq,stockout=stockout+p.spq where p.picklistidetailid=? ";
		private static final String reducePartDetailChildGoodStock="update part_stock_detail s inner join picklistdetail p on p.picklistid=? inner join part_master pa on pa.finalpartid=p.partid and pa.exclusiveclubno>0 and s.partid=pa.partid inner join picklistmaster pm on pm.picklistid=p.picklistid and pm.sublocationid=s.sublocationid set s.goodstock=s.goodstock-p.spq,stockout=stockout+p.spq where p.picklistidetailid=? ";
		private static final String updatePickListKanbanStatus="update picklistmaster set statusid=17,scan_by=?, kanbanscanedtime=now(), tsid=getcurrenttsid(now()) where picklistid=? ";

		private static final String selectKanbanList="SELECT p.picklistid, p.scheduleno, p.customerid, p.supplydate, p.supplytime,p.unloadingdoc, p.usagelocation,"
				+ "p.vehicleno,p.picked_by,p.staged_by,p.kanban_attached_by,p.kanban_tapping_by,p.scan_by,p.loading_supervised_by,p.doc_handoverby_id,"
				+ "p.dock_audit_by,p.vehicle_assigned_by,ifnull (pu.user_name,'--') pickedByName, ifnull (su.user_name,'--') stagedByName, "
				+ "ifnull (kau.user_name,'--') kanbanAttachedByName, ifnull (ktu.user_name,'--') kanbanTapingByName, "
				+ "ifnull (scu.user_name,'--') scanByName, ifnull (lsu.user_name,'--') loadingSupervisorByName, "
				+ "ifnull (dhu.user_name,'--') docHandoverByName, ifnull (dau.user_name,'--') docAuditByName, "
				+ "ifnull (vau.user_name,'--') vehicleAssignedByName,c.customer_erp_code,c.customername "
				+ "FROM picklistmaster p "
				+ "inner join customer_master c on p.customerid=c.customerid "
				+ "inner join warehouse w on w.warehousid = p.warehouseid "
				+ "inner join sublocation sl on p.sublocationid = sl.sublocationid "
				+ "left join user_master pu on pu.user_id =p.picked_by "
				+ "left join user_master su on su.user_id =p.staged_by "
				+ "left join user_master kau on kau.user_id =p.kanban_attached_by "
				+ "left join user_master ktu on ktu.user_id =p.kanban_tapping_by "
				+ "left join user_master scu on scu.user_id =p.scan_by "
				+ "left join user_master lsu on lsu.user_id =p.loading_supervised_by "
				+ "left join user_master dhu on dhu.user_id =p.doc_handoverby_id "
				+ "left join user_master dau on dau.user_id =p.dock_audit_by "
				+ "left join user_master vau on vau.user_id =p.vehicle_assigned_by "
				+ "WHERE p.statusid=? and p.warehouseid =? and p.sublocationid =? ";
		private static final String kanbanPendingCount="select ifnull(sum(case when p.statusid !=17 then 1 else 0 end),0) pendingcnt,ifnull(sum(case when p.statusid =17 then 1 else 0 end),0) completedcnt  from picklistmaster p inner join picklistmaster p1 on p1.vehicleno=p.vehicleno  and p1.picklistid =? where p.statusid>=11 and p.statusid <=17";
		private static final String updatePickListLoadingStatus="update picklistmaster p inner join picklistmaster p1 on p1.vehicleno=p.vehicleno  and p.statusid =17 and p1.picklistid =? set p.statusid=18, p.tsid=getcurrenttsid(now()) ";
		private static final String insertImportInvUpload="insert into invoiceimport(vendorcode,itemcode,kanbanno,invoiceno,unloadno,ewaybillno) values (?,?,?,?,?,?);";
		private static final String updateLoadingDtl ="update picklistmaster set loading_supervised_by=?,doc_handoverby_id=?,dock_audit_by=? where picklistid =?";

		private static final String insPickListPartsNewSpare="INSERT INTO picklistdetail(picklistid, kanbanno, partid, supplydate, supplytime, "
				+ "unloadingdoc, usagelocation, spq, perbinqty, qty_received, noofpackackes, stagingstate,picklistdataid, "
				+ "createdby, createddate, tsid) "
				+ "SELECT ?,kanbanno,p.partid,str_to_date(a.supplydate,'%Y-%m-%d'),"
				+ "a.supplytime,a.unloadingdoc,a.usageloc,a.qty,a.qty/p.obinqty,0,0,0,a.sparedataid,?,now(),getcurrenttsid(now())  FROM spareimportdata a inner join  `part_master` p on p.partno=a.partno inner join customer_master c on c.customer_erp_code=a.vendorcode inner join plants pl on left(a.unloadingdoc,2)=pl.plantshorname where c.customerid=? and date_format(str_to_date(a.supplydate,'%Y-%m-%d'),'%Y-%m-%d')=? and a.supplytime=? and pl.plantid=? and a.picklistid=0 "
				+ "and not exists (select pd.kanbanno,cm.customer_erp_code from picklistdetail pd inner join picklistmaster pm on pm.picklistid =pd.picklistid inner join customer_master cm on cm.customerId=? where pd.kanbanno=a.kanbanno and cm.customer_erp_code=a.vendorcode) ";
		private static final String updatePickListIdNewSpare="update spareimportdata a inner join customer_master c on c.customer_erp_code=a.vendorcode inner join plants pl on left(a.unloadingdoc,2)=pl.plantshorname set a.picklistid=? where c.customerid=? and date_format(str_to_date(a.supplydate,'%Y-%m-%d'),'%Y-%m-%d')=? and a.supplytime=? and pl.plantid=? and a.picklistid=0";
		private static final String selectDistinctPickListSpare="SELECT distinct c.customerid,pl.plantid,date_format(str_to_date(a.supplydate,'%Y-%m-%d'),'%Y-%m-%d') supplydate,ifnull(a.supplytime,'') supplytime,left(a.unloadingdoc,2) usageloc,c.primarysublocationid,s.warehouseid,w.locationid from spareimportdata a "
				+ "inner join customer_master c on c.customer_erp_code=a.vendorcode "
				+ "inner join sublocation s on s.sublocationid=c.primarysublocationid "
				+ "inner join warehouse w on w.warehousid=s.warehouseid "
				+ "inner join plants pl on left(a.unloadingdoc,2)=pl.plantshorname and a.picklistid=0";
		private static final String insertImportSpareData="INSERT INTO spareimportdata(forname, supplydate,reviseddidate,vendorcode,partno,qty,qtydispatched,qtyrejected,unloadingdoc,kanbanno, attention, pricechangeflag, batchcode, price,createdby,createddate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now());";
		
		private static final String deletePicklistImportQuery=" delete from picklistimport pl where pl.kanbanno in (select kanbanno from picklistdetail) and pl.picklistid=0";
		private static final String deleteSparesImportQuery=" delete from spareimportdata pl where pl.kanbanno in (select kanbanno from picklistdetail) and pl.picklistid=0";
		
		public ApiResult<PickListMaster> managePickList(PickListMaster input){
			if(input.getPickListId()==0) {
				return addPickListMaster(input);
			}else {
				return modifyPickListMaster(input);
			}
		}
		public ApiResult<PickListMaster> addPickListMaster(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertQuery,PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.clearParameters();
				pstmt.setString(1, l.getScheduleNo());
				pstmt.setLong(2, l.getCustomerId());
				pstmt.setString(3, l.getSupplyDate());
				pstmt.setString(4, l.getSupplyTime());
				pstmt.setInt(5, l.getUnloadingDoc());
				pstmt.setInt(6, l.getUsageLocation());
				pstmt.setLong(7, l.getStatusId());
				pstmt.setInt(8,l.getUserId());
				pstmt.setInt(9,l.getLocationId());
				pstmt.setInt(10,l.getSubLocationId());
				pstmt.setInt(11,l.getWarehouseId());
				pstmt.setLong(12,l.getParentPickListId());
				pstmt.executeUpdate();
				rs=pstmt.getGeneratedKeys();
				if(rs.next()){
					code=rs.getString(1);
					l.setPickListId(Long.parseLong(code));
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				pstmt=conn.prepareStatement(insertDtlQuery);
				for(int i=0;i<l.getPickListDetail().size();i++) {
					PickListDetail d=l.getPickListDetail().get(i);
					pstmt.clearParameters();
					pstmt.setLong(1, l.getPickListId());
					pstmt.setString(2, d.getKanbanNo());
					pstmt.setLong(3, d.getPartId());
					pstmt.setString(4, l.getSupplyDate());
					pstmt.setString(5, l.getSupplyTime());
					pstmt.setString(6, d.getUnLoadingDoc());
					pstmt.setString(7, d.getUsageLocation());
					pstmt.setInt(8, d.getQty());
					pstmt.setInt(9, d.getBinQty());
					pstmt.setInt(10, 0);
					pstmt.setInt(11, 0);
					pstmt.setDouble(12, 0);
					pstmt.setInt(13,l.getUserId());
					pstmt.executeUpdate();
				}
				pstmt.close();
				pstmt=null;
				
				String sql =" update picklistmaster p inner join picklistmaster pm on pm.parentpicklistid =p.picklistid set p.statusid=26 where pm.picklistid=? ";
				pstmt=conn.prepareStatement(sql);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				
				conn.commit();
				result.isSuccess=true;
				result.message="pick list master added successfully";
				result.result=l.getPickListId();
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<PickListMaster> modifyPickListMaster(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(modifyQuery);
				pstmt.clearParameters();
				pstmt.setString(1, l.getScheduleNo());
				pstmt.setLong(2, l.getCustomerId());
				pstmt.setString(3, l.getSupplyDate());
				pstmt.setString(4, l.getSupplyTime());
				pstmt.setInt(5, l.getUnloadingDoc());
				pstmt.setInt(6, l.getUsageLocation());
				pstmt.setLong(7, l.getStatusId());
				pstmt.setInt(8,l.getUserId());
				pstmt.setLong(9,l.getPickListId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="pick list master  modified successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<PickListMaster> getPickList(long pickListId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PickListMaster> result=new ApiResult<PickListMaster>();
			PickListMaster l=new PickListMaster();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(select);
				pstmt.setLong(1, pickListId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPickListId(rs.getLong("picklistid"));
					l.setScheduleNo(rs.getString("scheduleno"));
					l.setCustomerId(rs.getLong("customerid"));
					l.setSupplyDate(rs.getString("supplydate"));
					l.setSupplyTime(rs.getString("supplytime"));
					l.setUnloadingDoc(rs.getInt("unloadingdoc"));
					l.setUsageLocation(rs.getInt("usagelocation"));
					l.setVehicleNo(rs.getString("vehicleno"));
					l.setPickedById(rs.getInt("picked_by"));
					l.setPickedByName(rs.getString("pickedByName"));
					l.setStagedById(rs.getInt("staged_by"));
					l.setStagedByName(rs.getString("stagedByName"));
					l.setKanbanAttachedById(rs.getInt("kanban_attached_by"));
					l.setKanbanAttachedByName(rs.getString("kanbanAttachedByName"));
					l.setKanbanTapingById(rs.getInt("kanban_tapping_by"));
					l.setKanbanTapingByName(rs.getString("kanbanTapingByName"));
					l.setScanById(rs.getInt("scan_by"));
					l.setScanByName(rs.getString("scanByName"));
					l.setLoadingSupervisorById(rs.getInt("loading_supervised_by"));
					l.setLoadingSupervisorByName(rs.getString("loadingSupervisorByName"));
					l.setDocHandoverById(rs.getInt("doc_handoverby_id"));
					l.setDocHandoverByName(rs.getString("docHandoverByName"));
					l.setDocAuditById(rs.getInt("dock_audit_by"));
					l.setDocAuditByName(rs.getString("docAuditByName"));
					l.setVehicleAssignedById(rs.getInt("vehicle_assigned_by"));
					l.setVehicleAssignedByName(rs.getString("vehicleAssignedByName"));
					l.setStatusId(rs.getInt("statusid"));
					l.setParentPickListId(rs.getLong("parentpicklistid"));
				}
				rs.close();
				pstmt=null;
				pstmt=conn.prepareStatement(selectDtl);
				pstmt.setLong(1, pickListId);
				rs=pstmt.executeQuery();
				ArrayList<PickListDetail> pickListDetail=new ArrayList<PickListDetail>();
				while(rs.next()){
					PickListDetail d=new PickListDetail();
					d.setPickListiDetailId(rs.getLong("picklistidetailid"));
					d.setPickListId(rs.getLong("picklistid"));
					d.setKanbanNo(rs.getString("kanbanno"));
					d.setPartId(rs.getLong("partid"));
					d.setPartName(rs.getString("partname"));
					d.setPartNo(rs.getString("partno"));
					d.setSupplyDate(rs.getString("supplydate"));
					d.setSupplyTime(rs.getString("supplytime"));
					d.setUnLoadingDoc(rs.getString("unloadingdoc"));
					d.setUsageLocation(rs.getString("usagelocation"));
					d.setQty(rs.getInt("qty"));
					d.setBinQty(rs.getInt("binqty"));
					d.setQtyReceived(rs.getInt("qty_received"));
					d.setNoOfPackackes(rs.getInt("noofpackackes"));
					d.setStagIngState(rs.getInt("stagingstate"));
					d.setSpq(rs.getInt("repackqty"));
					pickListDetail.add(d);
				}
				l.setPickListDetail(pickListDetail);
				result.result=l;
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<ArrayList<PickListMaster>> getPickListStaging(long statusId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			PreparedStatement pstmt1=null;
			ResultSet rs1=null;
			Connection conn=null;
			ApiResult<ArrayList<PickListMaster>> result=new ApiResult<ArrayList<PickListMaster>>();
			ArrayList<PickListMaster> pickListStaging=new ArrayList<PickListMaster>();
			PickListMaster l=new PickListMaster();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(picklistStagingQuery);
				pstmt.setLong(1, statusId);
				rs=pstmt.executeQuery();
				while(rs.next()){
					l.setPickListId(rs.getLong("picklistid"));
					l.setScheduleNo(rs.getString("scheduleno"));
					l.setCustomerId(rs.getLong("customerid"));
					l.setSupplyDate(rs.getString("supplydate"));
					l.setSupplyTime(rs.getString("supplytime"));
					l.setUnloadingDoc(rs.getInt("unloadingdoc"));
					l.setUsageLocation(rs.getInt("usagelocation"));
					l.setVehicleNo(rs.getString("vehicleno"));
					l.setPickedById(rs.getInt("picked_by"));
					l.setPickedByName(rs.getString("pickedByName"));
					l.setStagedById(rs.getInt("staged_by"));
					l.setStagedByName(rs.getString("stagedByName"));
					l.setKanbanAttachedById(rs.getInt("kanban_attached_by"));
					l.setKanbanAttachedByName(rs.getString("kanbanAttachedByName"));
					l.setKanbanTapingById(rs.getInt("kanban_tapping_by"));
					l.setKanbanTapingByName(rs.getString("kanbanTapingByName"));
					l.setScanById(rs.getInt("scan_by"));
					l.setScanByName(rs.getString("scanByName"));
					l.setLoadingSupervisorById(rs.getInt("loading_supervised_by"));
					l.setLoadingSupervisorByName(rs.getString("loadingSupervisorByName"));
					l.setDocHandoverById(rs.getInt("doc_handoverby_id"));
					l.setDocHandoverByName(rs.getString("docHandoverByName"));
					l.setDocAuditById(rs.getInt("dock_audit_by"));
					l.setDocAuditByName(rs.getString("docAuditByName"));
					l.setVehicleAssignedById(rs.getInt("vehicle_assigned_by"));
					l.setVehicleAssignedByName(rs.getString("vehicleAssignedByName"));
					pstmt1=conn.prepareStatement(selectDtl);
					pstmt1.setLong(1, rs.getLong("picklistid"));
					rs1=pstmt1.executeQuery();
					ArrayList<PickListDetail> pickListDetail=new ArrayList<PickListDetail>();
					while(rs1.next()){
						PickListDetail d=new PickListDetail();
						d.setPickListiDetailId(rs1.getLong("picklistidetailid"));
						d.setPickListId(rs1.getLong("picklistid"));
						d.setKanbanNo(rs1.getString("kanbanno"));
						d.setPartId(rs1.getLong("partid"));
						d.setPartName(rs1.getString("partname"));
						d.setPartNo(rs1.getString("partno"));
						d.setSupplyDate(rs1.getString("supplydate"));
						d.setSupplyTime(rs1.getString("supplytime"));
						d.setUnLoadingDoc(rs1.getString("unloadingdoc"));
						d.setUsageLocation(rs1.getString("usagelocation"));
						d.setQty(rs1.getInt("qty"));
						d.setBinQty(rs1.getInt("binqty"));
						d.setQtyReceived(rs1.getInt("qty_received"));
						d.setNoOfPackackes(rs1.getInt("noofpackackes"));
						d.setStagIngState(rs1.getInt("stagingstate"));
						d.setSpq(rs1.getInt("repackqty"));
						pickListDetail.add(d);
					}
					l.setPickListDetail(pickListDetail);
					pickListStaging.add(l);	
				}
				rs.close();
				rs1.close();
				result.result=pickListStaging;
				
//				pstmt=null;
//				pstmt=conn.prepareStatement(selectDtl);
//				pstmt.setLong(1, pickListId);
//				rs=pstmt.executeQuery();
//				ArrayList<PickListDetail> pickListDetail=new ArrayList<PickListDetail>();
//				while(rs.next()){
//					PickListDetail d=new PickListDetail();
//					d.setPickListiDetailId(rs.getLong("picklistidetailid"));
//					d.setPickListId(rs.getLong("picklistid"));
//					d.setKanbanNo(rs.getString("kanbanno"));
//					d.setPartId(rs.getLong("partid"));
//					d.setPartName(rs.getString("partname"));
//					d.setPartNo(rs.getString("partno"));
//					d.setSupplyDate(rs.getString("supplydate"));
//					d.setSupplyTime(rs.getString("supplytime"));
//					d.setUnLoadingDoc(rs.getString("unloadingdoc"));
//					d.setUsageLocation(rs.getString("usagelocation"));
//					d.setQty(rs.getInt("qty"));
//					d.setBinQty(rs.getInt("binqty"));
//					d.setQtyReceived(rs.getInt("qty_received"));
//					d.setNoOfPackackes(rs.getInt("noofpackackes"));
//					d.setStagIngState(rs.getInt("stagingstate"));
//					d.setSpq(rs.getInt("repackqty"));
//					pickListDetail.add(d);
//				}
//				l.setPickListDetail(pickListDetail);
//				result.result=l;
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<ArrayList<PickListDetail>> getPickListPartDetail(long pickListDetailId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<ArrayList<PickListDetail>> result=new ApiResult<ArrayList<PickListDetail>>();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectPartDtl);
				pstmt.setLong(1, pickListDetailId);
				rs=pstmt.executeQuery();
				int noOfBins=0;
				long partId=0;
				int exclusiveClubNo=0;
				if(rs.next()){
					noOfBins=rs.getInt("binqty");
					partId=rs.getLong("partid");
					exclusiveClubNo=rs.getInt("exclusiveclubno");
					
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				ArrayList<Long> partIds=new ArrayList<Long>();
				if(exclusiveClubNo>0) {
					pstmt=conn.prepareStatement(selectChildParts);
					pstmt.setLong(1, partId);
					rs=pstmt.executeQuery();
					while(rs.next()){
						partIds.add(rs.getLong("partid"));
					}
					rs.close();
					pstmt.close();
					pstmt=null;
				}else {
					partIds.add(partId);
				}
				ArrayList<PickListDetail> pickListBinDetail=new ArrayList<PickListDetail>();
				int index=0;
				int availableBin = 0;
				for(long part:partIds) {
					System.out.println(getStockDetail);
					pstmt=conn.prepareStatement(getStockDetail);
					pstmt.setLong(1, part);
					pstmt.setLong(2, pickListDetailId);
					pstmt.setLong(3, noOfBins);
					rs=pstmt.executeQuery();
					int insertIndex=0;
					while(rs.next()) {
						PickListDetail d = new PickListDetail();
						LineSpacePartConfig lp=new LineSpacePartConfig();
						lp.setAllocatedBins(rs.getInt("allocatedbins"));
						lp.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						lp.setPartSpaceName(rs.getString("partspacename"));
						lp.setUsedSpace(rs.getInt("usedspace"));
						lp.setFifoOrder(rs.getInt("fifoorder"));
						lp.setFromLineNo(rs.getInt("fromlineno"));
						lp.setToLineNo(rs.getInt("tolineno"));
						lp.setFromCol(rs.getInt("fromcol"));
						lp.setToCol(rs.getInt("tocol"));
						lp.setLineRackId(rs.getInt("linerackid"));
						lp.setLineRackCode(rs.getString("linerackcode"));
						lp.setLineRackCompartmentId(rs.getInt("linerackcompartmentid"));
						lp.setLineRackCompartmentName(rs.getString("linerackcompartmentname"));
						lp.setFromLineSpaceId(rs.getInt("fromlinespaceid"));
						lp.setFromLineSpaceName(rs.getString("fromlinespacecode"));
						lp.setToLineSpaceId(rs.getInt("tolinespaceid"));
						lp.setToLineSpaceName(rs.getString("tolinespacecode"));
						d.setLineSpacePartConfig(lp);
						d.setPickListId(rs.getLong("picklistid"));
						d.setScanCode(rs.getString("tbisscancode"));
						d.setPickListiDetailId(rs.getLong("picklistidetailid"));
						d.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						d.setStockMovementDetailId(rs.getLong("stockmovementdetailid"));
						d.setBinQty(noOfBins);
						if(index>0 && exclusiveClubNo>0) {
							pickListBinDetail.add(insertIndex+index,d);
						}else {
							pickListBinDetail.add(d);
						}
						availableBin = availableBin + 1;
						insertIndex=insertIndex+index+1;
					}		
					rs.close();
					pstmt.close();
					pstmt=null;
					index++;
				}
				if(exclusiveClubNo>0 && partIds.size()>0) {
					availableBin = availableBin / partIds.size();
				} 
				
				result.result=pickListBinDetail;
				result.message=availableBin+"";
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<ArrayList<PickListDetail>> getPickListPartLineSpaceDetail(long pickListDetailId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<ArrayList<PickListDetail>> result=new ApiResult<ArrayList<PickListDetail>>();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectPartDtl);
				pstmt.setLong(1, pickListDetailId);
				rs=pstmt.executeQuery();
				int noOfBins=0;
				long partId=0;
				int exclusiveClubNo=0;
				long pickListId=0;
				if(rs.next()){
					noOfBins=rs.getInt("binqty");
					partId=rs.getLong("partid");
					exclusiveClubNo=rs.getInt("exclusiveclubno");
					pickListId=rs.getLong("picklistid");
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				ArrayList<Long> partIds=new ArrayList<Long>();
				if(exclusiveClubNo>0) {
					pstmt=conn.prepareStatement(selectChildParts);
					pstmt.setLong(1, partId);
					rs=pstmt.executeQuery();
					while(rs.next()){
						partIds.add(rs.getLong("partid"));
					}
					rs.close();
					pstmt.close();
					pstmt=null;
				}else {
					partIds.add(partId);
				}
				int availableBin=0;
				ArrayList<PickListDetail> pickListBinDetail=new ArrayList<PickListDetail>();				
				for(long part:partIds) {
					System.out.println(getStockDetailLineSpace);
					pstmt=conn.prepareStatement(getStockDetailLineSpace);
					pstmt.setLong(1, part);
					pstmt.setLong(2,pickListDetailId);
					pstmt.setLong(3, pickListDetailId);
					rs=pstmt.executeQuery();
					int totalBins=0;					
					int allocatedBins=noOfBins;
					while(rs.next()) {
						totalBins=rs.getInt("totalavailablestock");
						PickListDetail d = new PickListDetail();
						LineSpacePartConfig lp=new LineSpacePartConfig();
						lp.setAllocatedBins(rs.getInt("allocatedbins"));
						lp.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						lp.setPartSpaceName(rs.getString("partspacename"));
						lp.setUsedSpace(rs.getInt("usedspace"));
						lp.setFifoOrder(rs.getInt("fifoorder"));
						lp.setFromLineNo(rs.getInt("fromlineno"));
						lp.setToLineNo(rs.getInt("tolineno"));
						lp.setFromCol(rs.getInt("fromcol"));
						lp.setToCol(rs.getInt("tocol"));
						lp.setLineRackId(rs.getInt("linerackid"));
						lp.setLineRackCode(rs.getString("linerackcode"));
						lp.setLineRackCompartmentId(rs.getInt("linerackcompartmentid"));
						lp.setLineRackCompartmentName(rs.getString("linerackcompartmentname"));
						lp.setFromLineSpaceId(rs.getInt("fromlinespaceid"));
						lp.setFromLineSpaceName(rs.getString("fromlinespacecode"));
						lp.setToLineSpaceId(rs.getInt("tolinespaceid"));
						lp.setToLineSpaceName(rs.getString("tolinespacecode"));
						d.setLineSpacePartConfig(lp);
						d.setPickListId(rs.getLong("picklistid"));
						d.setPickListiDetailId(rs.getLong("picklistidetailid"));
						d.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						d.setBinQty(noOfBins);
						if(totalBins>=allocatedBins) {							
							d.setAvailableBin(allocatedBins);
							allocatedBins=0;
						}else {
							allocatedBins=allocatedBins-totalBins;
							d.setAvailableBin(totalBins);							
						}
						availableBin=availableBin+d.getAvailableBin();
						pickListBinDetail.add(d);
						if(allocatedBins==0) break;
					}		
					rs.close();
					pstmt.close();
					pstmt=null;
				}
				result.result=pickListBinDetail;
				result.message=availableBin+"";
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<ArrayList<PickListDetail>> getScannedPickListPartDetail(long pickListDetailId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<ArrayList<PickListDetail>> result=new ApiResult<ArrayList<PickListDetail>>();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectPartDtl);
				pstmt.setLong(1, pickListDetailId);
				rs=pstmt.executeQuery();
				int noOfBins=0;
				long partId=0;
				int exclusiveClubNo=0;
				long pickListId=0;
				if(rs.next()){
					noOfBins=rs.getInt("binqty");
					partId=rs.getLong("partid");
					exclusiveClubNo=rs.getInt("exclusiveclubno");
					pickListId=rs.getLong("picklistid");
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				ArrayList<Long> partIds=new ArrayList<Long>();
				if(exclusiveClubNo>0) {
					pstmt=conn.prepareStatement(selectChildParts);
					pstmt.setLong(1, partId);
					rs=pstmt.executeQuery();
					while(rs.next()){
						partIds.add(rs.getLong("partid"));
					}
					rs.close();
					pstmt.close();
					pstmt=null;
				}else {
					partIds.add(partId);
				}
				ArrayList<PickListDetail> pickListBinDetail=new ArrayList<PickListDetail>();
//				int index=0;
				int availableBin = 0;
				for(long part:partIds) {
					pstmt=conn.prepareStatement(getScannedStockDetail);
					pstmt.setLong(1, part);
					pstmt.setLong(2, pickListDetailId);
					pstmt.setLong(3, pickListDetailId);
					rs=pstmt.executeQuery();
//					int insertIndex=0;
					while(rs.next()) {
						PickListDetail d = new PickListDetail();
						LineSpacePartConfig lp=new LineSpacePartConfig();
						lp.setAllocatedBins(rs.getInt("allocatedbins"));
						lp.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						lp.setPartSpaceName(rs.getString("partspacename"));
						lp.setUsedSpace(rs.getInt("usedspace"));
						lp.setFifoOrder(rs.getInt("fifoorder"));
						lp.setFromLineNo(rs.getInt("fromlineno"));
						lp.setToLineNo(rs.getInt("tolineno"));
						lp.setFromCol(rs.getInt("fromcol"));
						lp.setToCol(rs.getInt("tocol"));
						lp.setLineRackId(rs.getInt("linerackid"));
						lp.setLineRackCode(rs.getString("linerackcode"));
						lp.setLineRackCompartmentId(rs.getInt("linerackcompartmentid"));
						lp.setLineRackCompartmentName(rs.getString("linerackcompartmentname"));
						lp.setFromLineSpaceId(rs.getInt("fromlinespaceid"));
						lp.setFromLineSpaceName(rs.getString("fromlinespacecode"));
						lp.setToLineSpaceId(rs.getInt("tolinespaceid"));
						lp.setToLineSpaceName(rs.getString("tolinespacecode"));
						d.setLineSpacePartConfig(lp);
						d.setPickListId(rs.getLong("picklistid"));
						d.setScanCode(rs.getString("tbisscancode"));
						d.setPickListiDetailId(rs.getLong("picklistidetailid"));
						d.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						d.setStockMovementDetailId(rs.getLong("stockmovementdetailid"));
						d.setBinQty(noOfBins);
//						if(index>0 && exclusiveClubNo>0) {
//							pickListBinDetail.add(insertIndex+index,d);
//						}else {
							pickListBinDetail.add(d);
//						}
						availableBin = availableBin + 1;
//						insertIndex=insertIndex+index+1;
					}		
					rs.close();
					pstmt.close();
					pstmt=null;
//					index++;
				}
				result.result=pickListBinDetail;
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}

		public ApiResult<PickListDetail> getPickListPartScanDetail(PickListDetail d){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PickListDetail> result=new ApiResult<PickListDetail>();
			try{
				result.isSuccess=true;
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectPartDtl);
				pstmt.setLong(1, d.getPickListiDetailId());
				rs=pstmt.executeQuery();
				int noOfBins=0;
				long partId=0;
				int exclusiveClubNo=0;
				if(rs.next()){
					noOfBins=rs.getInt("binqty");
					partId=rs.getLong("partid");
					exclusiveClubNo=rs.getInt("exclusiveclubno");
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				ArrayList<Long> partIds=new ArrayList<Long>();
				if(exclusiveClubNo>0) {
					pstmt=conn.prepareStatement(selectChildParts);
					pstmt.setLong(1, partId);
					rs=pstmt.executeQuery();
					while(rs.next()){
						partIds.add(rs.getLong("partid"));
					}
					rs.close();
					pstmt.close();
					pstmt=null;
				}else {
					partIds.add(partId);
				}
				PickListDetail pickListBinDetail=null;
				for(long part:partIds) {
					System.out.println(scanPickListPartDetail);
					pstmt=conn.prepareStatement(scanPickListPartDetail);
					pstmt.setLong(1, part);
					pstmt.setLong(2, d.getPickListiDetailId());
					pstmt.setString(3, d.getScanCode());
					pstmt.setString(4, d.getScanCode());
					rs=pstmt.executeQuery();
					if(rs.next()) {
						pickListBinDetail = new PickListDetail();
						LineSpacePartConfig lp=new LineSpacePartConfig();
						lp.setAllocatedBins(rs.getInt("allocatedbins"));
						lp.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						lp.setPartSpaceName(rs.getString("partspacename"));
						lp.setUsedSpace(rs.getInt("usedspace"));
						lp.setFifoOrder(rs.getInt("fifoorder"));
						lp.setFromLineNo(rs.getInt("fromlineno"));
						lp.setToLineNo(rs.getInt("tolineno"));
						lp.setFromCol(rs.getInt("fromcol"));
						lp.setToCol(rs.getInt("tocol"));
						lp.setLineRackId(rs.getInt("linerackid"));
						lp.setLineRackCode(rs.getString("linerackcode"));
						lp.setLineRackCompartmentId(rs.getInt("linerackcompartmentid"));
						lp.setLineRackCompartmentName(rs.getString("linerackcompartmentname"));
						lp.setFromLineSpaceId(rs.getInt("fromlinespaceid"));
						lp.setFromLineSpaceName(rs.getString("fromlinespacecode"));
						lp.setToLineSpaceId(rs.getInt("tolinespaceid"));
						lp.setToLineSpaceName(rs.getString("tolinespacecode"));
						pickListBinDetail.setLineSpacePartConfig(lp);
						pickListBinDetail.setPickListId(rs.getLong("picklistid"));
						pickListBinDetail.setScanCode(rs.getString("tbisscancode"));
						pickListBinDetail.setPickListiDetailId(rs.getLong("picklistidetailid"));
						pickListBinDetail.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
						pickListBinDetail.setStockMovementDetailId(rs.getLong("stockmovementdetailid"));
						pickListBinDetail.setBinQty(1);
					}		
					rs.close();
					pstmt.close();
					pstmt=null;
					if(pickListBinDetail!=null) {
						pstmt=conn.prepareStatement(updatePickListStock);
						pstmt.clearParameters();
						pstmt.setLong(1, pickListBinDetail.getPickListiDetailId());
						pstmt.setLong(2, pickListBinDetail.getStockMovementDetailId());
						pstmt.executeUpdate();
						break;
					}
				}
				if(pickListBinDetail==null) {
					result.isSuccess=false;
					result.message="Invalid scan code/Part detail mismatch";
				}
				result.result=pickListBinDetail;
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<PickListDetail> managePickListDetail(PickListDetail input){
			if(input.getPickListiDetailId()==0) {
				return addPickListDetail(input);
			}else {
				return modifyPickListDetail(input);
			}
		}
		
		public ApiResult<PickListDetail> addPickListDetail(PickListDetail l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertDtlQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getPickListId());
				pstmt.setString(2, l.getKanbanNo());
				pstmt.setLong(3, l.getPartId());
				pstmt.setString(4, l.getSupplyDate());
				pstmt.setString(5, l.getSupplyTime());
				pstmt.setString(6, l.getUnLoadingDoc());
				pstmt.setString(7, l.getUsageLocation());
				pstmt.setInt(8, l.getSpq());
				pstmt.setInt(9, l.getPerBinQty());
				pstmt.setInt(10, l.getQtyReceived());
				pstmt.setInt(11, l.getNoOfPackackes());
				pstmt.setDouble(12, l.getStagIngState());
				pstmt.setInt(13,l.getUserId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Pick List Detail added successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<PickListDetail> modifyPickListDetail(PickListDetail l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(modifyDtlQuery);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getPickListId());
				pstmt.setString(2, l.getKanbanNo());
				pstmt.setLong(3, l.getPartId());
				pstmt.setString(4, l.getSupplyDate());
				pstmt.setString(5, l.getSupplyTime());
				pstmt.setString(6, l.getUnLoadingDoc());
				pstmt.setString(7, l.getUsageLocation());
				pstmt.setInt(8, l.getSpq());
				pstmt.setInt(9, l.getPerBinQty());
				pstmt.setInt(10, l.getQtyReceived());
				pstmt.setInt(11, l.getNoOfPackackes());
				pstmt.setDouble(12, l.getStagIngState());
				pstmt.setInt(13,l.getUserId());
				pstmt.setLong(14,l.getPickListiDetailId());
				pstmt.executeUpdate();
				conn.commit();
				result.isSuccess=true;
				result.message="Pick List Detail modified successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<PickListDetail> getPickListDetail(long pickListiDetailId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PickListDetail> result=new ApiResult<PickListDetail>();
			PickListDetail l=new PickListDetail();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectPartDtl);
				pstmt.setLong(1, pickListiDetailId);
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPickListiDetailId(rs.getLong("picklistidetailid"));
					l.setPickListId(rs.getLong("picklistid"));
					l.setKanbanNo(rs.getString("kanbanno"));
					l.setPartId(rs.getLong("partid"));
					l.setSupplyDate(rs.getString("supplydate"));
					l.setSupplyTime(rs.getString("supplytime"));
					l.setUnLoadingDoc(rs.getString("unloadingdoc"));
					l.setUsageLocation(rs.getString("usagelocation"));
					l.setSpq(rs.getInt("spq"));
					l.setPerBinQty(rs.getInt("perbinqty"));
					l.setQtyReceived(rs.getInt("qty_received"));
					l.setNoOfPackackes(rs.getInt("noofpackackes"));
					l.setStagIngState(rs.getInt("stagingstate"));
				}
				result.result=l;
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<PickListMaster> readPickListFile(int userId,String fileLocation){
			Connection conn=null;
			PreparedStatement pstmt=null;
			PreparedStatement bstmt=null;
			PreparedStatement gstmt=null;
			PreparedStatement pInsMaster=null;
			PreparedStatement pistmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				result.isSuccess=true;
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(insertImportData);
				Scanner scanner = new Scanner(new File(fileLocation));
				
				int i=0;
				String vendorName="";
				String requestDate="";
				while (scanner.hasNextLine()) {
					i++;
					String data=scanner.nextLine();
					System.out.println(data);
					if(i>6) {
						String[] itemData=data.split("\\s+");
						System.out.println(itemData.length);
						if(itemData.length==8) {
							pstmt.clearParameters();
							pstmt.setString(1, itemData[0]);
							pstmt.setString(2, itemData[1]);
							pstmt.setString(3, itemData[2]);
							pstmt.setString(4, itemData[3]);
							pstmt.setString(5, itemData[4]);
							pstmt.setString(6, itemData[5]);
							pstmt.setString(7, itemData[6]);
							pstmt.setString(8, itemData[7]);
							pstmt.setInt(9,userId);
							pstmt.addBatch();
						}else {
							result.isSuccess=false;
							result.message="Parsing the file failed at row"+i;
							break;
						}
					}else if(i==1) {
						vendorName=data;							
					}else if(i==3) {
						requestDate=data;
					}
				}			
				scanner.close();
				if(!result.isSuccess) {
					conn.rollback();
					return result;
				}
				pstmt.executeBatch();
				pstmt.close();
				pstmt=null;
				pistmt=conn.prepareStatement(deletePicklistImportQuery);
//				pistmt.clearParameters();
//				pistmt.setInt(1, Integer.parseInt(id));
//				pistmt.setInt(2, Integer.parseInt(id));
				System.out.println(pistmt);
				pistmt.executeUpdate();
				pistmt.close();
				
				bstmt=conn.prepareStatement(insPickListPartsNew,PreparedStatement.RETURN_GENERATED_KEYS);
				gstmt=conn.prepareStatement(updatePickListIdNew);
				pInsMaster=conn.prepareStatement(insUploadMaster,PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt=conn.prepareStatement(selectDistinctPickList);
				rs=pstmt.executeQuery();
				ArrayList<PickListMaster> m=new ArrayList<PickListMaster>();
				while(rs.next()) {
					PickListMaster p=new PickListMaster();
					p.setCustomerId(rs.getLong("customerid"));
					p.setSupplyDate(rs.getString("supplydate"));
					p.setSupplyTime(rs.getString("supplytime"));
					p.setSubLocationId(rs.getInt("primarysublocationid"));
					p.setWarehouseId(rs.getInt("warehouseid"));
					p.setLocationId(rs.getInt("locationid"));
					p.setUsageLocation(rs.getInt("plantid"));
					m.add(p);
						
				}
				rs.close();
				if(m.size()==0) {
					try{
						if(conn!=null){
							conn.rollback();
							result.isSuccess=false;
							result.message="All these picklist parts from e-nagare were imported already";
							return result;
						}
					}catch(SQLException esql){
						esql.printStackTrace();
					}
				}
				int index = 1;
				for(PickListMaster p:m) {
					String id="0";
					String did="0";
					
//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
//					LocalDateTime localdatetime = LocalDateTime.parse(new Date.now(), formatter);
					 DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					 String scheduleAutoNo = dateFormat.format(new Date()).toString()+index;
					System.out.println(scheduleAutoNo+":::::::::::::::@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@::::::::::::::::::"+dateFormat.format(new Date())+index);
					
					pInsMaster.clearParameters();
					pInsMaster.setString(1, scheduleAutoNo);
					pInsMaster.setLong(2,p.getCustomerId());
					pInsMaster.setString(3,p.getSupplyDate());
					pInsMaster.setString(4,p.getSupplyTime());
					pInsMaster.setInt(5,p.getSubLocationId());					
					pInsMaster.setInt(6,p.getWarehouseId());
					pInsMaster.setInt(7,p.getLocationId());
					pInsMaster.setInt(8,p.getUsageLocation());
					pInsMaster.setInt(9,userId);
					pInsMaster.executeUpdate();
					rs=pInsMaster.getGeneratedKeys();
					if(rs.next()) {
						id=rs.getString(1);
					}
					rs.close();
					bstmt.clearParameters();
					bstmt.setString(1, id);
					bstmt.setInt(2, userId);
					bstmt.setLong(3,p.getCustomerId());
					bstmt.setString(4,p.getSupplyDate());
					bstmt.setString(5,p.getSupplyTime());
					bstmt.setLong(6,p.getUsageLocation());
					bstmt.setLong(7,p.getCustomerId());
					bstmt.executeUpdate();
					rs=bstmt.getGeneratedKeys();
					if(rs.next()) {
						did=rs.getString(1);						
					}
//					else {
//						try{
//							if(conn!=null){
//								conn.rollback();
//								result.isSuccess=false;
//								result.message="These list are already exist";
//								return result;
//							}
//						}catch(SQLException esql){
//							esql.printStackTrace();
//						}
//					}
					rs.close();
					
					gstmt.clearParameters();
					gstmt.setString(1, id);
					gstmt.setLong(2,p.getCustomerId());
					gstmt.setString(3,p.getSupplyDate());
					gstmt.setString(4,p.getSupplyTime());
					gstmt.setLong(5,p.getUsageLocation());
					gstmt.executeUpdate();
					
					index++;
				}
				conn.commit();
				result.isSuccess=true;
				result.message="Pick List Detail added successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(pInsMaster!=null) {
						pInsMaster.close();
					}
					if(bstmt!=null){
						bstmt.close();
					}
					if(gstmt!=null){
						gstmt.close();
					}
					if(pistmt!=null){
						pistmt.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<PickListMaster> selectPickListPart(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				if(l.getStatusId()==1) {
					pstmt=conn.prepareStatement(insertSelectedPart);
					pstmt.clearParameters();
					pstmt.setString(1, l.getScheduleNo());
					pstmt.setInt(2,l.getUserId());
					pstmt.setLong(3, l.getCustomerId());
					pstmt.executeUpdate();
					result.message="Part added to pick list";
				}else if(l.getStatusId()==2) {
					pstmt=conn.prepareStatement(removeSelectedPart);
					pstmt.clearParameters();
					pstmt.setString(1, l.getScheduleNo());
					pstmt.setInt(2,l.getUserId());
					pstmt.setLong(3, l.getCustomerId());
					pstmt.executeUpdate();
					result.message="Part removed from pick list";
				}else if(l.getStatusId()==3) {
					pstmt=conn.prepareStatement(clearAllSelectedPart);
					pstmt.clearParameters();
					pstmt.setInt(1,l.getUserId());
					pstmt.executeUpdate();
					result.message="Selected parts cleared from pick list";
				}
				conn.commit();
				result.isSuccess=true;								
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		

		public ApiResult<PickListMaster> selectVehiclePickListPart(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				if(l.getStatusId()==1) {
					pstmt=conn.prepareStatement(insertSelectedVehiclePart);
					pstmt.clearParameters();
					pstmt.setString(1, l.getScheduleNo());
					pstmt.setInt(2,l.getUserId());
					pstmt.setLong(3, l.getCustomerId());
					pstmt.executeUpdate();
					result.message="Vehicle added to pick list";
				}else if(l.getStatusId()==2) {
					pstmt=conn.prepareStatement(removeSelectedVehiclePart);
					pstmt.clearParameters();
					pstmt.setString(1, l.getScheduleNo());
					pstmt.setInt(2,l.getUserId());
					pstmt.setLong(3, l.getCustomerId());
					pstmt.executeUpdate();
					result.message="Vehicle removed from pick list";
				}else if(l.getStatusId()==3) {
					pstmt=conn.prepareStatement(clearAllSelectedVehiclePart);
					pstmt.clearParameters();
					pstmt.setInt(1,l.getUserId());
					pstmt.executeUpdate();
					result.message="Selected Vehicle cleared from pick list";
				}
				conn.commit();
				result.isSuccess=true;								
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<PickListMaster> addSelectedPickList(PickListMaster l) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			PreparedStatement bstmt = null;
			PreparedStatement gstmt = null;
			ResultSet rs = null;
			ApiResult result = new ApiResult();
			String code = "0";
			ArrayList<Long> customers=new ArrayList<Long>();
			try {
				conn = DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(insertPickMasterQuery,PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.clearParameters();			
				bstmt=conn.prepareStatement(insPickListParts);
				gstmt = conn.prepareStatement(selectedCustomers);
				gstmt.setLong(1, l.getUserId());				
				rs = gstmt.executeQuery();
				while(rs.next()) {
					customers.add(rs.getLong("customerid"));
				}
				gstmt.close();
				rs.close();
				gstmt=conn.prepareStatement(updatePickListId);
				for(long cust:customers) {
					long pcode=0;
					pstmt.clearParameters();
					pstmt.setLong(1, cust);
					pstmt.setInt(2,l.getUserId());
					pstmt.executeUpdate();
					rs=pstmt.getGeneratedKeys();
					if(rs.next()) {
						pcode=rs.getLong(1);
					}
					rs.close();
					bstmt.clearParameters();
					bstmt.setLong(1, pcode);
					bstmt.setInt(2, l.getUserId());
					bstmt.setInt(3, l.getUserId());
					bstmt.setLong(4,cust);
					bstmt.executeUpdate();
					
					gstmt.clearParameters();
					gstmt.setLong(1, pcode);
					gstmt.setInt(2, l.getUserId());
					gstmt.setLong(3,cust);
					gstmt.executeUpdate();
				}
				conn.commit();
				result.isSuccess = true;
				result.message = "PickList(s) created successfully";
			} catch (Exception e) {
				try {
					if (conn != null) {
						conn.rollback();
					}
				} catch (SQLException esql) {
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess = false;
				result.message = e.getMessage();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
					if (pstmt != null) {
						pstmt.close();
					}
				} catch (SQLException esql) {
					esql.printStackTrace();
				}
			}
			return result;
		}	
		public ApiResult<PickListMaster> updatePickListPartStockDetail(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(updatePickListStatus);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getStatusId());
				pstmt.setInt(2,l.getUserId());
				pstmt.setLong(3,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;
//				pstmt=conn.prepareStatement(updatePickListStock);
//				for(int i=0;i<l.getPickListDetail().size();i++) {
//					PickListDetail d=l.getPickListDetail().get(i);
//					pstmt.clearParameters();
//					pstmt.setLong(1, d.getPickListId());
//					pstmt.setLong(2, d.getStockMovementDetailId());
//					pstmt.executeUpdate();
//				}
				conn.commit();
				result.isSuccess=true;
				result.message="pick list updated successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		
		public ApiResult<VehicleDetails> updateShorthaulVehicle(VehicleDetails l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
					pstmt = conn.prepareStatement(shVehicleRequested,PreparedStatement.RETURN_GENERATED_KEYS);
					pstmt.clearParameters();
					pstmt.setString(1, l.getVehicleNo());
					pstmt.setInt(2, 13);
					pstmt.setInt(3, l.getUserId());
					pstmt.executeUpdate();
					rs=pstmt.getGeneratedKeys();
					if(rs.next()){
						l.setTripId(rs.getLong(1));
					}
					rs.close();
					pstmt.close();
				
				
				pstmt=conn.prepareStatement(updateSVehicleDtl);
				pstmt.clearParameters();
				pstmt.setString(1,l.getVehicleNo());
				pstmt.setLong(2, l.getVehicleTransId());
				pstmt.setLong(3, l.getTripId());
				pstmt.setLong(4,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;
				
				pstmt=conn.prepareStatement(updateSVehicleHdr);
				pstmt.clearParameters();
				pstmt.setLong(1, l.getUserId());
				pstmt.setString(2,l.getVehicleNo());
				pstmt.setInt(3,12);
				pstmt.setLong(4,l.getTripId());
				pstmt.setLong(5,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;
				
				conn.commit();
				result.isSuccess=true;
				result.message="Vehicle Alloted successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		
		public ApiResult<InvoiceDetails> updateInvoiceDetail(InvoiceDetails l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(updateInvoiceDtl);
				pstmt.clearParameters();
				pstmt.setString(1, l.getInvoiceNo());
				pstmt.setString(2,l.getConsignementNo());
				pstmt.setString(3,l.getEwayBillNo());
				pstmt.setLong(4,l.getPickListDetailId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;
				
				conn.commit();
				result.isSuccess=true;
				result.message="Document Details Updated successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<PickListMaster> readPickListGateEntryFile(int userId,String fileLocation){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				result.isSuccess=true;
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				
				
				pstmt=conn.prepareStatement("truncate table gateentryimport");
				pstmt.executeUpdate();
				pstmt.close();
				
				
				pstmt=conn.prepareStatement(insertImportGateEntryData);
				
				
				FileInputStream inputStream = new FileInputStream(fileLocation);
				XSSFWorkbook wb = new XSSFWorkbook(new File(fileLocation));
				XSSFSheet sheet = wb.getSheetAt(0);
				
				Iterator ite = sheet.rowIterator();
				int j=0;
				while(ite.hasNext()) {
					j++;
                    Row row = (Row) ite.next(); 
                    System.out.println("Row value fetched..! ");
                    Iterator<Cell> cellIterator = row.cellIterator();
                    System.out.println("Cell Iterator invoked..! ");
                    int index=1;
                    while(cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        System.out.println("getting cell value..! "+ cell +"---"+cell.getCellType()+" "+j+" "+index+" "+cell.getCellStyle().getDataFormat());
                        
                        switch(cell.getCellType()) { 
                        case STRING: //handle string columns
                        	pstmt.setString(index, cell.getStringCellValue());                                                                                     
                            break;
                        case NUMERIC: //handle double data
                        	if (DateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								String dateformatstring = "";
								System.out.println("sssssssssssssssssssssssssssss "+cell.getCellStyle().getDataFormat());
								if (cell.getCellStyle().getDataFormat() == 15) {
									dateformatstring = "yyyy-MM-dd"; 
								} else if (cell.getCellStyle().getDataFormat() == 22) {
									dateformatstring = "yyyy-MM-dd hh:mm"; 
								}
								String formattedvalue = new CellDateFormatter(dateformatstring).format(date);
								pstmt.setString(index, formattedvalue);
                            }else {
                            	pstmt.setInt(index, (int) cell.getNumericCellValue());
                            }
                            break;
                        case BLANK: //handle blank data
                            pstmt.setString(index, null);
                            break;
                        }
                        index++;
                        if(index>10) {
                        	break;
                        }
                    }
                    if(j>1) {
                    	pstmt.addBatch();
                    }
	            }
				
						
				if(!result.isSuccess) {
					conn.rollback();
					return result;
				}
				pstmt.executeBatch();
				pstmt.close();
				
				String sql =" update picklistdetail p inner join gateentryimport  s on s.unloadno =p.unloadnumber set p.ponumber  =s.pono, p.msilentrytime=s.msilentrytime ,p.msilexittime =s.msilexittime ,p.srvnumber =s.srvno ";
				pstmt=conn.prepareStatement(sql);
				pstmt.executeUpdate();
				pstmt.close();
				
				conn.commit();
				result.isSuccess=true;
				result.message="Gate Entry Detail added successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		
		public ApiResult<PickListMaster> readPickListSrvFile(int userId,String fileLocation){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				result.isSuccess=true;
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				
				pstmt=conn.prepareStatement("truncate table srvimport;");
				pstmt.executeUpdate();
				pstmt.close();
				
				pstmt=conn.prepareStatement(insertImportSrvData);
				
				
				FileInputStream inputStream = new FileInputStream(fileLocation);
				XSSFWorkbook wb = new XSSFWorkbook(new File(fileLocation));
				XSSFSheet sheet = wb.getSheetAt(0);
				
				Iterator ite = sheet.rowIterator();
				int j=0;
				while(ite.hasNext()) {
					j++;
                    Row row = (Row) ite.next(); 
                    System.out.println("Row value fetched..! ");
                    Iterator<Cell> cellIterator = row.cellIterator();
                    System.out.println("Cell Iterator invoked..! ");
                    int index=1;
                    while(cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        System.out.println("getting cell value..! "+ cell +"---"+cell.getCellType()+" "+j+" "+index+""+cell.getCellStyle().getDataFormat());
                        
                        switch(cell.getCellType()) { 
                        case STRING: //handle string columns
                        	pstmt.setString(index, cell.getStringCellValue());                                                                                     
                            break;
                        case NUMERIC: //handle double data
                        	if (DateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								String dateformatstring = "";
								System.out.println("sssssssssssssssssssssssssssss "+cell.getCellStyle().getDataFormat());
								if (cell.getCellStyle().getDataFormat() == 15) {
									dateformatstring = "yyyy-MM-dd"; 
								} else if (cell.getCellStyle().getDataFormat() == 22) {
									dateformatstring = "yyyy-MM-dd hh:mm"; 
								}
								String formattedvalue = new CellDateFormatter(dateformatstring).format(date);
								pstmt.setString(index, formattedvalue);
                            }else {
                            	pstmt.setInt(index, (int) cell.getNumericCellValue());
                            }
                            break;
                        case BLANK: //handle blank data
                            pstmt.setString(index, null);
                            break;
                        }
                        index++;
                        if(index>17) {
                        	break;
                        }
                    }
                    if(j>1) {
                    	pstmt.addBatch();
                    }
	            }
						
				if(!result.isSuccess) {
					conn.rollback();
					return result;
				}
				pstmt.executeBatch();
				pstmt.close();
				
				
				String sql =" update picklistdetail p inner join srvimport s on s.srvno =p.srvnumber set  p.examount =s.exciseamt ,p.cgst=s.cgstamt ,p.sgst=s.sgstamt ,p.igst=s.igstamt ,p.fsft=s.`57f2no` ,p.srvcomments=s.status,p.srvqty=s.qty";
				pstmt=conn.prepareStatement(sql);
				pstmt.executeUpdate();
				pstmt.close();

				conn.commit();
				result.isSuccess=true;
				result.message="SRV Uploaded successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<PickListMaster> updateProcessStatus(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(updateProcessStatus);
				pstmt.clearParameters();
				pstmt.setInt(1,l.getPickedById());
				pstmt.setInt(2,l.getKanbanAttachedById());
				pstmt.setInt(3,l.getStagedById());
				pstmt.setInt(4,l.getProcessStatusId());
				pstmt.setLong(5,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;
				
				
				
				conn.commit();
				result.isSuccess=true;
				result.message="Picklist Status Updated successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<PickListDetail> getPickListDetailFromKanbanQR(PickListDetail scan) {
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PickListDetail> result=new ApiResult<PickListDetail>();
			PickListDetail l=new PickListDetail();
			try{
				result.isSuccess=false;				
				conn=DatabaseUtil.getConnection();
				//parse qr code
				System.out.println(scan.getScanCode()+" "+scan.getPickListId());
				String lines[] = scan.getScanCode().split("\\r?\\n");
				String partNo="";
				String supplyDateTime="";
				String usageLocation="";
				if(lines.length>=27) {
					partNo=lines[2].trim();
					supplyDateTime=lines[20].trim();
					usageLocation=lines[24].trim();
					System.out.println("::IN IF LINE LENTH 27:::: "+partNo+" DT : "+supplyDateTime+" LOC: "+usageLocation);
				}else {
					result.isSuccess=false;
					result.message="Invalid Kanban Scan";
					result.result=null;
					return result;	
				}
//				System.out.println("::OUT IF LINE LENTH 27:::: "+ partNo+" "+supplyDateTime+" "+usageLocation+" "+supplyDateTime.substring(0,10)+" "+supplyDateTime.substring(11));
				System.out.println(selectPartDtlKanban);
				pstmt=conn.prepareStatement(selectPartDtlKanban);
				pstmt.setLong(1, scan.getPickListId());
				pstmt.setString(2, partNo);
				pstmt.setString(3, usageLocation);
				pstmt.setString(4, supplyDateTime.substring(0,10));
				pstmt.setString(5, supplyDateTime.substring(11));
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPickListiDetailId(rs.getLong("picklistidetailid"));
					l.setPickListId(rs.getLong("picklistid"));
					l.setKanbanNo(rs.getString("kanbanno"));
					l.setPartId(rs.getLong("partid"));
					l.setSupplyDate(rs.getString("supplydate"));
					l.setSupplyTime(rs.getString("supplytime"));
					l.setUnLoadingDoc(rs.getString("unloadingdoc"));
					l.setUsageLocation(rs.getString("usagelocation"));
					l.setSpq(rs.getInt("qty"));
					l.setPerBinQty(rs.getInt("binqty"));
					l.setQtyReceived(rs.getInt("qty_received"));
					l.setNoOfPackackes(rs.getInt("noofpackackes"));
					l.setStagIngState(rs.getInt("stagingstate"));
					l.setPartName(rs.getString("partname"));
					l.setPartNo(rs.getString("partno"));
					l.setBinQty(rs.getInt("repackqty"));
					l.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
					result.isSuccess=true;					
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				rs=null;
				if(result.isSuccess && l.getExclusiveClubNo()>0) {
					l.setChildParts(new ArrayList<ScanDetails>());
					pstmt=conn.prepareStatement(selectChildParts);
					pstmt.setLong(1, l.getPartId());
					rs=pstmt.executeQuery();
					while(rs.next()){
						ScanDetails s=new ScanDetails();
						s.setPartId(rs.getInt("partid"));
						s.setPartName(rs.getString("partdescription"));
						l.getChildParts().add(s);
					}					
				}
				result.result=l;
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;		
			
		}
		public ApiResult<PickListDetail> getPickListDetailFromPartId(PickListDetail scan) {
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PickListDetail> result=new ApiResult<PickListDetail>();
			PickListDetail l=new PickListDetail();
			try{
				result.isSuccess=false;				
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				//parse qr code
//				System.out.println(selectPartBinScan);
				pstmt=conn.prepareStatement(selectPartBinScan);
				pstmt.setLong(1, scan.getPartId());
				pstmt.setString(2, scan.getScanCode());
				pstmt.setLong(3,scan.getPickListiDetailId());
				pstmt.setLong(4, scan.getPickListiDetailId());
				rs=pstmt.executeQuery();
				if(rs.next()){
					l.setPickListiDetailId(rs.getLong("picklistidetailid"));
					l.setPickListId(rs.getLong("picklistid"));
					l.setKanbanNo(rs.getString("kanbanno"));
					l.setPartId(rs.getLong("partid"));
					l.setSupplyDate(rs.getString("supplydate"));
					l.setSupplyTime(rs.getString("supplytime"));
					l.setUnLoadingDoc(rs.getString("unloadingdoc"));
					l.setUsageLocation(rs.getString("usagelocation"));
					l.setSpq(rs.getInt("qty"));
					l.setPerBinQty(rs.getInt("binqty"));
					l.setQtyReceived(rs.getInt("qty_received"));
					l.setNoOfPackackes(rs.getInt("noofpackackes"));
					l.setStagIngState(rs.getInt("stagingstate"));
					l.setStockMovementDetailId(rs.getLong("stockmovementdetailid"));
					l.setPartName(rs.getString("partname"));
					l.setPartNo(rs.getString("partno"));
					l.setBinQty(rs.getInt("repackqty"));

					result.isSuccess=true;
				}
				pstmt.close();
				pstmt=null;
				if(result.isSuccess) {
					pstmt=conn.prepareStatement(updateStockMovementDetailProcessStatus);
					pstmt.setLong(1, l.getStockMovementDetailId());
					pstmt.executeUpdate();
					pstmt.close();
					pstmt=null;

				}
				if(result.isSuccess && scan.getUpdateStockDetail()) {
					pstmt=conn.prepareStatement(updatePickListDetail);
					pstmt.setLong(1, l.getPickListiDetailId());
					pstmt.executeUpdate();
					pstmt.close();
					pstmt=null;
					
					pstmt=conn.prepareStatement(updatePickListDetailState);
					pstmt.setLong(1, l.getPickListiDetailId());
					pstmt.executeUpdate();
					pstmt.close();
					pstmt=null;
				}
				result.result=l;
				conn.commit();
			}catch(Exception e){
				try {
				if(conn!=null) {
					conn.rollback();
				}
				}catch(Exception ee) {
					ee.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;		
			
		}
		public ApiResult<PickListDetail> clearPickListDetailFromPartId(PickListDetail scan) {
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<PickListDetail> result=new ApiResult<PickListDetail>();
			PickListDetail l=new PickListDetail();
			try{
				result.isSuccess=false;				
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(clearPartBinScan);
				pstmt.setLong(1,scan.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;
				result.isSuccess=true;
				result.message="Picklist Status Updated successfully";
				conn.commit();
			}catch(Exception e){
				try {
				if(conn!=null) {
					conn.rollback();
				}
				}catch(Exception ee) {
					ee.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;		
			
		}
		public ApiResult<PickListMaster> updateKanbanProcessStatus(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			PreparedStatement pstmtStock=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				ArrayList<Long> pickListDetails=new ArrayList<Long>();
				pstmt=conn.prepareStatement(pickListDetailIds);
				pstmt.setLong(1, l.getPickListId());
				rs=pstmt.executeQuery();
				while(rs.next()) {
					pickListDetails.add(rs.getLong("picklistidetailid"));
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				rs=null;
				pstmt=conn.prepareStatement(reducePartDetailGoodStock);
				pstmtStock=conn.prepareStatement(reducePartDetailChildGoodStock);
				for(long detailid:pickListDetails) {
					pstmt.clearParameters();
					pstmt.setLong(1,l.getPickListId());
					pstmt.setLong(2,detailid);
					pstmt.executeUpdate();
	
					pstmtStock.clearParameters();
					pstmtStock.setLong(1,l.getPickListId());
					pstmtStock.setLong(2,detailid);
					pstmtStock.executeUpdate();
					
				}
				pstmt.close();
				pstmt=null;
				pstmtStock.close();
				pstmtStock=null;
				pstmt=conn.prepareStatement(updatePickListKanbanStatus);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getUserId());
				pstmt.setLong(2,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;

				int completedCount=0;
				int pendingCount=0;
				pstmt=conn.prepareStatement(kanbanPendingCount);
				pstmt.setLong(1,l.getPickListId());
				rs=pstmt.executeQuery();
				if(rs.next()) {
					completedCount=rs.getInt("completedcnt");
					pendingCount=rs.getInt("pendingcnt");
				}
				rs.close();
				pstmt.close();
				pstmt=null;
				
				if(pendingCount==0 && completedCount>0) {
					pstmt=conn.prepareStatement(updatePickListLoadingStatus);
					pstmt.clearParameters();
					pstmt.setLong(1,l.getPickListId());
					pstmt.executeUpdate();
					pstmt.close();
					pstmt=null;
				}
				conn.commit();
				result.isSuccess=true;
				result.message="Picklist Status Updated successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(pstmtStock!=null){
						pstmtStock.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		public ApiResult<ArrayList<PickListMaster>> getPickKanbanList(int statusId,long warehouseId,long subLocationId,long udcId){
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			Connection conn=null;
			ApiResult<ArrayList<PickListMaster>> result=new ApiResult<ArrayList<PickListMaster>>();
			ArrayList<PickListMaster> r=new ArrayList<PickListMaster>();
			try{
				conn=DatabaseUtil.getConnection();
				pstmt=conn.prepareStatement(selectKanbanList);
				pstmt.setLong(1, statusId);
				pstmt.setLong(2, warehouseId);
				pstmt.setLong(3, subLocationId);
//				pstmt.setLong(4, udcId);
				rs=pstmt.executeQuery();
				while(rs.next()){
					PickListMaster l=new PickListMaster();
					l.setPickListId(rs.getLong("picklistid"));
					l.setScheduleNo(rs.getString("scheduleno"));
					l.setCustomerId(rs.getLong("customerid"));
					l.setSupplyDate(rs.getString("supplydate"));
					l.setSupplyTime(rs.getString("supplytime"));
					l.setUnloadingDoc(rs.getInt("unloadingdoc"));
					l.setUsageLocation(rs.getInt("usagelocation"));
					l.setVehicleNo(rs.getString("vehicleno"));
					l.setPickedById(rs.getInt("picked_by"));
					l.setPickedByName(rs.getString("pickedByName"));
					l.setStagedById(rs.getInt("staged_by"));
					l.setStagedByName(rs.getString("stagedByName"));
					l.setKanbanAttachedById(rs.getInt("kanban_attached_by"));
					l.setKanbanAttachedByName(rs.getString("kanbanAttachedByName"));
					l.setKanbanTapingById(rs.getInt("kanban_tapping_by"));
					l.setKanbanTapingByName(rs.getString("kanbanTapingByName"));
					l.setScanById(rs.getInt("scan_by"));
					l.setScanByName(rs.getString("scanByName"));
					l.setLoadingSupervisorById(rs.getInt("loading_supervised_by"));
					l.setLoadingSupervisorByName(rs.getString("loadingSupervisorByName"));
					l.setDocHandoverById(rs.getInt("doc_handoverby_id"));
					l.setDocHandoverByName(rs.getString("docHandoverByName"));
					l.setDocAuditById(rs.getInt("dock_audit_by"));
					l.setDocAuditByName(rs.getString("docAuditByName"));
					l.setVehicleAssignedById(rs.getInt("vehicle_assigned_by"));
					l.setVehicleAssignedByName(rs.getString("vehicleAssignedByName"));
					l.setCustomerCode(rs.getString("customer_erp_code"));
					l.setCustomerName(rs.getString("customername"));
					r.add(l);
				}
				rs.close();
				pstmt=null;
				for(PickListMaster l:r) {				
					pstmt=conn.prepareStatement(selectDtl);
					pstmt.setLong(1, l.getPickListId());
					rs=pstmt.executeQuery();
					ArrayList<PickListDetail> pickListDetail=new ArrayList<PickListDetail>();
					while(rs.next()){
						PickListDetail d=new PickListDetail();
						d.setPickListiDetailId(rs.getLong("picklistidetailid"));
						d.setPickListId(rs.getLong("picklistid"));
						d.setKanbanNo(rs.getString("kanbanno"));
						d.setPartId(rs.getLong("partid"));
						d.setPartName(rs.getString("partname"));
						d.setPartNo(rs.getString("partno"));
						d.setSupplyDate(rs.getString("supplydate"));
						d.setSupplyTime(rs.getString("supplytime"));
						d.setUnLoadingDoc(rs.getString("unloadingdoc"));
						d.setUsageLocation(rs.getString("usagelocation"));
						d.setQty(rs.getInt("qty"));
						d.setBinQty(rs.getInt("binqty"));
						d.setQtyReceived(rs.getInt("qty_received"));
						d.setNoOfPackackes(rs.getInt("noofpackackes"));
						d.setStagIngState(rs.getInt("stagingstate"));
						d.setSpq(rs.getInt("repackqty"));
						pickListDetail.add(d);
					}
					l.setPickListDetail(pickListDetail);
				}
				result.result=r;
			}catch(Exception e){
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
				result.result=null;
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(rs!=null){
						rs.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<PickListMaster> readPickListInvUploadFile(int userId,String fileLocation){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				result.isSuccess=true;
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				
				pstmt=conn.prepareStatement("truncate table invoiceimport");
				pstmt.executeUpdate();
				pstmt.close();
				
				pstmt=conn.prepareStatement(insertImportInvUpload);
				
				FileInputStream inputStream = new FileInputStream(fileLocation);
				XSSFWorkbook wb = new XSSFWorkbook(new File(fileLocation));
				XSSFSheet sheet = wb.getSheetAt(0);
				
				Iterator ite = sheet.rowIterator();
				int j=0;
				while(ite.hasNext()) {
					j++;
                    Row row = (Row) ite.next(); 
                    System.out.println("Row value fetched..! ");
                    Iterator<Cell> cellIterator = row.cellIterator();
                    System.out.println("Cell Iterator invoked..! ");
                    int index=1;
                    while(cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        System.out.println("getting cell value..! "+ cell +"---"+cell.getCellType()+" "+j+" "+index+""+cell.getCellStyle().getDataFormat());
                        
                        switch(cell.getCellType()) { 
                        case STRING: //handle string columns
                        	pstmt.setString(index, cell.getStringCellValue());                                                                                     
                            break;
                        case NUMERIC: //handle double data
                        	if (DateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								String dateformatstring = "";
								System.out.println("sssssssssssssssssssssssssssss "+cell.getCellStyle().getDataFormat());
								if (cell.getCellStyle().getDataFormat() == 15) {
									dateformatstring = "yyyy-MM-dd"; 
								} else if (cell.getCellStyle().getDataFormat() == 22) {
									dateformatstring = "yyyy-MM-dd hh:mm"; 
								}
								String formattedvalue = new CellDateFormatter(dateformatstring).format(date);
								pstmt.setString(index, formattedvalue);
                            }else {
                            	pstmt.setInt(index, (int) cell.getNumericCellValue());
                            }
                            break;
                        case BLANK: //handle blank data
                            pstmt.setString(index, null);
                            break;
                        }
                        index++;
                        if(index>6) {
                        	break;
                        }
                    }
                    if(j>1) {
                    	pstmt.addBatch();
                    }
	            }
						
				if(!result.isSuccess) {
					conn.rollback();
					return result;
				}
				pstmt.executeBatch();
				pstmt.close();

				String sql =" update picklistdetail p inner join invoiceimport  s on s.kanbanno =p.kanbanno  set p.unloadnumber =s.unloadno , p.invoiceno  =s.invoiceno , p.ewaybillno =s.ewaybillno";
				pstmt=conn.prepareStatement(sql);
				pstmt.executeUpdate();
				pstmt.close();
				
				conn.commit();
				result.isSuccess=true;
				result.message="Invoice Details Uploaded successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		
		public ApiResult<LoadingDetails> updateLoadingDetail(LoadingDetails l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				pstmt=conn.prepareStatement(updateLoadingDtl);
				pstmt.clearParameters();
				pstmt.setInt(1, l.getLoadingSupervisorId());
				pstmt.setInt(2,l.getDocHandoverId());
				pstmt.setInt(3,l.getDocAuditId());
				pstmt.setLong(4,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=null;	
				
				System.out.println("::::::::DOCK AUDIT ID:::::::: "+ l.getDocAuditId());
				if(l.getDocAuditId() != 0) {
					String sql =" update picklistmaster set statusid=20 where picklistid =? ";
					pstmt=conn.prepareStatement(sql);
					pstmt.clearParameters();
					pstmt.setLong(1,l.getPickListId());
					pstmt.executeUpdate();
					pstmt.close();
				}
				
				conn.commit();
				result.isSuccess=true;
				result.message="Loading Details Updated successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}

		public ApiResult<PickListMaster> readPickListSpareFile(int userId,String fileLocation){
			Connection conn=null;
			PreparedStatement pstmt=null;
			PreparedStatement bstmt=null;
			PreparedStatement gstmt=null;
			PreparedStatement pInsMaster=null;
			PreparedStatement pistmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				result.isSuccess=true;
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);
				
				
				pstmt=conn.prepareStatement(insertImportSpareData);
				
				FileInputStream inputStream = new FileInputStream(fileLocation);
				XSSFWorkbook wb = new XSSFWorkbook(new File(fileLocation));
				XSSFSheet sheet = wb.getSheetAt(0);
				
				Iterator ite = sheet.rowIterator();
				int j=0;
				while(ite.hasNext()) {
					j++;
	                Row row = (Row) ite.next(); 
	                System.out.println("Row value fetched..! ");
	                Iterator<Cell> cellIterator = row.cellIterator();
	                System.out.println("Cell Iterator invoked..! ");
	                int index=1;
	                while(cellIterator.hasNext()) {
	                    Cell cell = cellIterator.next();
	                    System.out.println("getting cell value..! "+ cell +"--- "+cell.getCellType()+" "+j+" "+index+" "+cell.getCellStyle().getDataFormat());
	                    
	                    switch(cell.getCellType()) { 
	                    case STRING: //handle string columns
	                    	pstmt.setString(index, cell.getStringCellValue());                                                                                     
	                        break;
	                    case NUMERIC: //handle double data
	                    	if (DateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								String dateformatstring = "";
								System.out.println("sssssssssssssssssssssssssssss "+cell.getCellStyle().getDataFormat());
								if (cell.getCellStyle().getDataFormat() == 14) {
									dateformatstring = "yyyy-MM-dd"; 
								} else if (cell.getCellStyle().getDataFormat() == 18) {
									dateformatstring = "hh:mm"; 
								}
								String formattedvalue = new CellDateFormatter(dateformatstring).format(date);
								pstmt.setString(index, formattedvalue);
	                        }else {
	                        	pstmt.setInt(index, (int) cell.getNumericCellValue());
	                        }
	                        break;
	                    case BLANK: //handle blank data
	                        pstmt.setString(index, null);
	                        break;
	                    }
	                    index++;
	                    if(index>14) {
	                    	pstmt.setInt(index, userId);
	                    	break;
	                    }
	                }
	                if(j>1) {
	                	pstmt.addBatch();
	                }
	            }
						
				if(!result.isSuccess) {
					conn.rollback();
					return result;
				}
				pstmt.executeBatch();
				pstmt.close();
				

				pistmt=conn.prepareStatement(deleteSparesImportQuery);
//				pistmt.clearParameters();
//				pistmt.setInt(1, Integer.parseInt(id));
//				pistmt.setInt(2, Integer.parseInt(id));
				System.out.println(pistmt);
				pistmt.executeUpdate();
				pistmt.close();
				
				
				pstmt=null;
				bstmt=conn.prepareStatement(insPickListPartsNewSpare);
				gstmt=conn.prepareStatement(updatePickListIdNewSpare);
				pInsMaster=conn.prepareStatement(insUploadMaster,PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt=conn.prepareStatement(selectDistinctPickListSpare);
				rs=pstmt.executeQuery();
				ArrayList<PickListMaster> m=new ArrayList<PickListMaster>();
				while(rs.next()) {
					PickListMaster p=new PickListMaster();
					p.setCustomerId(rs.getLong("customerid"));
					p.setSupplyDate(rs.getString("supplydate"));
					p.setSupplyTime(rs.getString("supplytime"));
					p.setSubLocationId(rs.getInt("primarysublocationid"));
					p.setWarehouseId(rs.getInt("warehouseid"));
					p.setLocationId(rs.getInt("locationid"));
					p.setUsageLocation(rs.getInt("plantid"));
					m.add(p);
						
				}
				rs.close();
				
				if(m.size()==0) {
					try{
						if(conn!=null){
							conn.rollback();
							result.isSuccess=false;
							result.message="All these picklist parts from spares were imported already";
							return result;
						}
					}catch(SQLException esql){
						esql.printStackTrace();
					}
				}
				
				int index = 1;
				for(PickListMaster p:m) {
					String id="0";
					
//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
//					LocalDateTime localdatetime = LocalDateTime.parse(new Date.now(), formatter);
					 DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					 String scheduleAutoNo = dateFormat.format(new Date()).toString()+index;
					System.out.println(scheduleAutoNo+":::::::::::::::@@@@@::::::::::::::::::"+p.getSupplyDate().toString());
					
					pInsMaster.clearParameters();
					pInsMaster.setString(1, scheduleAutoNo);
					pInsMaster.setLong(2,p.getCustomerId());
					pInsMaster.setString(3,p.getSupplyDate());
					pInsMaster.setString(4,p.getSupplyTime());
					pInsMaster.setInt(5,p.getSubLocationId());					
					pInsMaster.setInt(6,p.getWarehouseId());
					pInsMaster.setInt(7,p.getLocationId());
					pInsMaster.setInt(8,p.getUsageLocation());
					pInsMaster.setInt(9,userId);
					pInsMaster.executeUpdate();
					rs=pInsMaster.getGeneratedKeys();
					if(rs.next()) {
						id=rs.getString(1);
					}
					rs.close();
					System.out.println("::::::::AAAAAAAAAAAAAAAAA:::::::");
					bstmt.clearParameters();
					bstmt.setString(1, id);
					bstmt.setInt(2, userId);
					bstmt.setLong(3,p.getCustomerId());
					bstmt.setString(4,p.getSupplyDate());
					bstmt.setString(5,p.getSupplyTime());
					bstmt.setLong(6,p.getUsageLocation());
					bstmt.setLong(7,p.getCustomerId());
					bstmt.executeUpdate();

					System.out.println("::::::::BBBBBBBBBBBBBBBB:::::::");
					gstmt.clearParameters();
					gstmt.setString(1, id);
					gstmt.setLong(2,p.getCustomerId());
					gstmt.setString(3,p.getSupplyDate());
					gstmt.setString(4,p.getSupplyTime());
					gstmt.setLong(5,p.getUsageLocation());
					gstmt.executeUpdate();
					System.out.println("::::::::CCCCCCCCCCCCCCCCCCC:::::::");
					
					index++;
				}
				
				conn.commit();
				result.isSuccess=true;
				result.message="Spares added successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}
					if(pInsMaster!=null) {
						pInsMaster.close();
					}
					if(bstmt!=null){
						bstmt.close();
					}
					if(gstmt!=null){
						gstmt.close();
					}
					if(pistmt!=null){
						pistmt.close();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		public ApiResult<PickListMaster> updateAmendPickList(PickListMaster l){
			Connection conn=null;
			PreparedStatement pstmt=null;
			ResultSet rs=null;
			ApiResult result=new ApiResult();
			String code="0";
			try{
				conn=DatabaseUtil.getConnection();
				conn.setAutoCommit(false);

				String sql =" update picklistmaster set statusid=25 where picklistid =? ";
				pstmt=conn.prepareStatement(sql);
				pstmt.clearParameters();
				pstmt.setLong(1,l.getPickListId());
				pstmt.executeUpdate();
				pstmt.close();
				
				conn.commit();
				result.isSuccess=true;
				result.message="Picklist Amended successfully";
			}catch(Exception e){
				try{
					if(conn!=null){
						conn.rollback();
					}
				}catch(SQLException esql){
					esql.printStackTrace();
				}
				e.printStackTrace();
				result.isSuccess=false;
				result.message=e.getMessage();
			}finally{
				try{
					if(conn!=null){
						conn.close();
					}
					if(pstmt!=null){
						pstmt.close();
					}				
				}catch(SQLException esql){
					esql.printStackTrace();
				}
			}
			return result;
		}
		
		
		public static PickListService getInstance(){		
			 return new PickListService();
		}
	}