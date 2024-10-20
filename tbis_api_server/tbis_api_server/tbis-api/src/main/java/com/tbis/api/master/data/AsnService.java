package com.tbis.api.master.data;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringJoiner;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cissol.core.model.ApiResult;
import com.cissol.core.util.DatabaseUtil;
import com.tbis.api.common.util.FileUtil;
import com.tbis.api.common.util.MailSendUtil;
import com.tbis.api.master.model.ASNBinTag;
import com.tbis.api.master.model.ASNPartMovement;
import com.tbis.api.master.model.AsnIncidentLog;
import com.tbis.api.master.model.AsnMaster;
import com.tbis.api.master.model.CustomerBarCode;
import com.tbis.api.master.model.CustomerSoftwares;
import com.tbis.api.master.model.EmailInput;
import com.tbis.api.master.model.GateEntryInput;
import com.tbis.api.master.model.LineSpacePartConfig;
import com.tbis.api.master.model.PackingType;
import com.tbis.api.master.model.PickListMaster;
import com.tbis.api.master.model.ScanDetails;
import com.tbis.api.master.model.ScanHeader;

public class AsnService {
	
	private static final String insertQuery = "INSERT INTO asnmaster(asnno, customerid, supplydate, supplytime, unloadingdocid, asnstatus, vechicleno, drivername, drivermobile, ewaybillno, invoiceno, directortransit, transitasnid, gateindatetime, createdby, createddate, tsid,returnpack,returnpackqty,sublocationid,warehouseid,locationid,transitlocationid,deliverynoteno,vehicletypeid,rgpno,filledsize,filledcapacity,triptype,noofpallet,partsadd)VALUES( getcurrenttsid(now()),?,?,?,?,?,?,?,?,?,?,?,?,now(),?,now(),getcurrenttsid(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	private static final String modifyQuery = "UPDATE asnmaster set asnno=?, customerid=?, supplydate=?, supplytime=?, unloadingdocid=?, asnstatus=?, vechicleno=?, drivername=?, drivermobile=?, ewaybillno=?, invoiceno=?, directortransit=?, transitasnid=?,  updatedby=?,updateddate=now(),tsid=getcurrenttsid(now()),deliverynoteno=?,vehicletypeid=?,rgpno=?,filledsize=?,filledcapacity=?,triptype=?,noofpallet=?,tripid=case when ?=0 then tripid else ? end where asnid=?;";
	private static final String select = "SELECT asnid, asnno, a.customerid, supplydate, supplytime, unloadingdocid, asnstatus, vechicleno, drivername, drivermobile, ewaybillno, invoiceno,deliverynoteno, directortransit, transitasnid,gateindatetime,cardsissued,cardsreceived,carddispatched,cardsacknowledged,returnpack,returnpackqty,a.transitlocationid,w.warehousename transitlocation,a.sublocationid,a.locationid,a.warehouseid,c.customer_erp_code, c.customername,w.warehouseshortcode,w.warehousename,l.locationname,l.locationshortcode,s.sublocationname,s.sublocationshortcode,u.udcname,c.primarydockid,a.cardsconfirmed,a.dispatchconfirmed,a.vehicletypeid,vtm.vehicletypename,vtm.`length`,vtm.capacity,vtm.height,vtm.width,a.rgpno,a.filledsize,a.filledcapacity,stockmovedfromdock,lineusageid,triptype,noofpallet,tripid,a.partsadd,c.manualpalletnumbering FROM asnmaster a inner join customer_master c on a.customerid=c.customerId inner join sublocation s on a.sublocationid=s.sublocationid inner JOIN warehouse w on a.warehouseid=w.warehousid inner join location l on a.locationid=l.locationid  inner join unload_doc_master u on c.primarydockid=u.udc_id left join vehicle_type_master vtm on vtm.vehicletypeid =a.vehicletypeid where asnid=?;";
	private static final String insMaster="INSERT INTO asnmaster(asnno, customerid, supplydate, supplytime, unloadingdocid, asnstatus, vechicleno, drivername, drivermobile, ewaybillno, invoiceno,deliverynoteno, directortransit, transitasnid, gateindatetime, createdby, createddate, tsid,cardsissued,returnpack,returnpackqty) "
			+ "SELECT getcurrenttsid(now()) asnno, ifnull(c.customerId,?), date(now()) supplydate, DATE_FORMAT(now(),'%h:%i %p'),1,?,v.vechicleno,v.drivername,v.drivermobile,'','','',0,v.transid,now(),1,now(),getcurrenttsid(now()),count('x') cardsissued,v.returnpack,v.returnpackqty "
			+ "FROM  vechicleintrans v "			
			+ "left join scandetails s on v.transid=s.transid "
			+ "left join customer_master c on c.customer_erp_code=s.vendorcode " 
			+ "where v.transid=? "
			+ "group by c.customerId, v.vechicleno,v.drivername,v.drivermobile,v.returnpack,v.returnpackqty ";
	private static final String customerTripCount="select count('x') ctrip from asnmaster a inner join (select vechicleno,customerid,supplydate from asnmaster a where asnid = ?) b on (a.supplydate=b.supplydate and a.customerid=b.customerid);";
	private static final String vehicleTripCount="select count('x') vtrip from asnmaster a inner join (select vechicleno , customerid,supplydate from asnmaster a where asnid = ?) b on ( a.supplydate=b.supplydate and a.vechicleno=b.vechicleno);";
	private static final String updateTripcount="update asnmaster set customertripcount=?,vehicletripcount=? where asnid=?";
	
	
	private static final String insDetail="INSERT INTO asndetail(asnid, partid, packageid, spq, binqty, isactive, createdby, createddate, tsid, received_spq, received_bin_qty,received_status,scanid)"
			+ "SELECT distinct ? asnno, p.partid,1,s.qty,s.qty,1,1,now(),getcurrenttsid(now()),0,0,0,s.scanid FROM scandetails s inner join part_master p on s.partno=p.partno inner join customer_master c on c.customer_erp_code=s.vendorcode inner join vechicleintrans v on v.transid=s.transid where s.transid=?";

	private static final String updateVehicleTrans="update vechicleintrans set statusid=2 where transid=? ";
	
	private static final String updateVehicle="update transporter t inner join vechicleintrans v on v.vechicleno=t.vechicleno  set vechiclestatus=? where transid=? ";
	
	private static final String updateVehicleAsnReceipt="update transporter t inner join asnmaster v on v.vechicleno=t.vechicleno  set vechiclestatus=? where asnid=?";
	
	private static final String asnLog ="INSERT INTO asnlog(asnid, scanid, statusid, createdby, createddate,tsid)"
			+ "SELECT  ? asnno,scanid, ?,createdby, now(),getcurrenttsid(now()) FROM  asndetail  where asnid=?";
	
	private static final String asnDetail="select a.asnid,a.asndetailid,s.userlocation,s.serialno,s.partno,c.customerid,c.customername,p.partid,p.partdescription,a.spq,a.binqty FROM scandetails s  inner join asndetail a on s.scanid=a.scanid "
			+ "inner join asnmaster am on am.asnid=a.asnid and am.asnid=? inner join customer_master c on c.customerId=am.customerid "
			+ "inner join part_master p on p.partid=a.partid where s.scandata=? and received_status=0 ";

	private static final String asnAllDetail="select a.asnid,a.asndetailid,s.userlocation,s.serialno,s.partno,c.customerid,c.customername,p.partid,p.partdescription,a.spq,a.binqty FROM scandetails s  inner join asndetail a on s.scanid=a.scanid "
			+ "inner join asnmaster am on am.asnid=a.asnid inner join customer_master c on c.customerId=am.customerid "
			+ "inner join part_master p on p.partid=a.partid where s.scandata=? and received_status in (0,3) and acknowledged=1 and readytodispatch=1 ";


	private static final String updateCardConfirm="update asndetail set cardconfirmed=1,cardconfirmedtime=now() where asnid=?  and asndetailid=?";
	private static final String updateCardConfirmNoFlow="update asndetail set cardconfirmed=1,cardconfirmedtime=now() where asnid=?";
	private static final String updateCardConfirmStatus="update asndetail set received_status=3 where asnid=?  and cardconfirmed=0";
	private static final String updateCardConfirmMasterStatus="update asnmaster a inner join (SELECT sum(case when received_status=3 then 1 else 0 end) notreadytodispatch,count('x') totalparts FROM `asndetail` WHERE asnid=? ) b on a.asnid=? "
			+ " set a.asnstatus= 11,a.cardsconfirmed=b.totalparts-b.notreadytodispatch  ";

	
	
	private static final String updateAcknoledgement="update asndetail set acknowledged=1,acknowledgetime=now() where asnid=?  and asndetailid=?";
	private static final String updateAcknoledgeStatus="update asndetail set received_status=3,supplyexception=1 where asnid=?  and acknowledged=0";
	private static final String updateAsnAckMasterStatus="update asnmaster a inner join (SELECT sum(case when received_status=3 then 1 else 0 end) notreadytodispatch,count('x') totalparts FROM `asndetail` WHERE asnid=? ) b on a.asnid=? "
			+ " set a.asnstatus= 4,a.cardsacknowledged=b.totalparts-b.notreadytodispatch  ";

	
	private static final String updateDispatchConfirm="update asndetail set dispatchconfirmed=1,dispatchconfirmedtime=now() where asnid=? and asndetailid=? ";
	private static final String updateDispatchConfirmNoFlow="update asndetail set dispatchconfirmed=1,dispatchconfirmedtime=now() where asnid=? ";
	private static final String updateDispatchSupplyConfirmStatus="update asnmaster a inner join (SELECT sum(case when dispatchconfirmed=1 then 1 else 0 end) notreadytodispatch,count('x') totalparts FROM `asndetail` WHERE asnid=? ) b on a.asnid=? "
			+ " set a.asnstatus= 12,a.dispatchconfirmed=b.totalparts-b.notreadytodispatch  ";
	
	private static final String updateDispatchReady="update asndetail set readytodispatch=1,readynesstime=now() where asndetailid=?";
	
	private static final String updateDispatch="update asndetail set pickspq=?,pickbinqty=?,received_status=1,incidentid=?,dispatchtime=now(),deliveryasnid=?,deliveryvehicleno=?,customertripcount=?,vehicletripcount=? where asnid=? and asndetailid=? ";
	private static final String updateDispatchStatus="update asndetail set received_status=3,supplyexception=1 where asnid=? and received_status=0";
	private static final String updateDispatchSupplyStatus="update asnmaster a inner join asndetail d on a.asnid=d.asnid and a.customerid=? set noofsupplyexceptions=noofsupplyexceptions+1 where received_status=3 and supplyexception=1 ";
	private static final String getVehicleDetails="select vechicleno,customertripcount,vehicletripcount from asnmaster where asnid=?";
	
//	private static final String updateAsnStatus="update asnmaster a inner join (SELECT sum(case when received_status=1 then 1 else 0 end) dispatched, "
//			+ "sum(case when received_status=3 then 1 else 0 end) readytodispatch,count('x') totalparts FROM `asndetail` WHERE asnid=? ) b on a.asnid=? "
//			+ " set a.asnstatus= case when b.readytodispatch>0 then 5 when b.dispatched=b.totalparts then 6 end,a.carddispatched=b.dispatched  ";

	private static final String updateAsnStatus="update asnmaster a inner join (SELECT sum(case when received_status in (1,2) then 1 else 0 end) dispatched, "
			+ "sum(case when received_status=3 then 1 else 0 end) readytodispatch,count('x') totalparts FROM `asndetail` WHERE asnid=? ) b on a.asnid=? "
			+ " set a.asnstatus= case when asnstatus=10 then 10 else 6 end,a.carddispatched=b.dispatched  ";

	private static final String updateReceipt="update asndetail set received_spq=?,received_bin_qty=?,received_status=2,receiveddatetime=now() where asnid=? and asndetailid=? ";
	
	private static final String updateReceiptException="update asndetail a "
			+ "inner join asnmaster m on a.asnid=m.asnid "
			+ "inner join customer_master c on m.customerid=c.customerid "
			+ "set tatexception=case when TIMESTAMPDIFF(MINUTE, a.createddate, a.receiveddatetime)>c.tatinmin then 1 else 0 end,"
			+ "qtyexception=case when received_status=2 and received_spq!=spq then 1 else 0 end "
			+ " where a.asnid=? ";
	
//	private static final String updateAsnRecdStatus="update asnmaster a inner join (SELECT sum(case when received_status=2 then 1 else 0 end) received, "
//			+ "sum(case when received_status in (0,1,3) then 1 else 0 end) dispatched,count('x') totalparts FROM `asndetail` WHERE asnid=? ) b on a.asnid=? "
//			+ " set a.asnstatus= case when b.received>0 and b.dispatched>0 then 9 when b.received=b.totalparts then 10 end,a.cardsreceived=b.received  ";

	private static final String updateAsnRecdStatus="update asnmaster a inner join (SELECT sum(case when received_status=2 then 1 else 0 end) received, "
			+ "sum(case when received_status in (0,1,3) then 1 else 0 end) dispatched,count('x') totalparts FROM `asndetail` WHERE asnid=? ) b on a.asnid=? "
			+ " set a.asnstatus= 10,a.cardsreceived=b.received  ";

	private static final String asnDispatchedDetail="select a.asnid,a.asndetailid,s.userlocation,s.scandata,s.serialno,s.partno,c.customerid,c.customername,p.partid,p.partdescription,a.spq,a.binqty FROM scandetails s  inner join asndetail a on s.scanid=a.scanid "
			+ "inner join asnmaster am on am.asnid=a.asnid and am.asnid=? inner join customer_master c on c.customerId=am.customerid "
			+ "inner join part_master p on p.partid=a.partid where a.received_status=1";

	private static final String asnDispatchedCardDetail="select a.asnid,a.asndetailid,s.userlocation,s.scandata,s.serialno,s.partno,c.customerid,c.customername,p.partid,p.partdescription,a.spq,a.binqty,c.customer_erp_code FROM scandetails s  inner join asndetail a on s.scanid=a.scanid "
			+ "inner join asnmaster am on am.asnid=a.asnid inner join customer_master c on c.customerId=am.customerid "
			+ "inner join part_master p on p.partid=a.partid where a.received_status=1 and s.scandata=?";

	private static final String asnDispatchConfirmedCardDetail="select a.asnid,a.asndetailid,s.userlocation,s.scandata,s.serialno,s.partno,c.customerid,c.customername,p.partid,p.partdescription,a.spq,a.binqty,c.customer_erp_code FROM scandetails s  inner join asndetail a on s.scanid=a.scanid "
			+ "inner join asnmaster am on am.asnid=a.asnid inner join customer_master c on c.customerId=am.customerid "
			+ "inner join part_master p on p.partid=a.partid where a.dispatchconfirmed=1 and a.received_status=1 and s.scandata=?";

	private static final String updateCustomerTransactionSummary="replace into parttatsummary(transdate,customerid,partid,totalasn,totalcardconfirm,totalaccepted,totaldispatch,totaldispatchconfirm,totalreceipts,totaltimedelay,totalqtymismatch,totalsupplydelay,userlocation) "
			+ "SELECT a.supplydate,a.customerid,d.partid,count(d.partid) totalasn, sum(case when d.cardconfirmed=1 then 1 else 0 end) totalcardconfirm,sum(case when d.acknowledged=1 then 1 else 0 end) totalaccepted,sum(case when d.received_status in (1,2) then 1 else 0 end) totaldispatch, sum(case when d.dispatchconfirmed=1 then 1 else 0 end) totaldipatchconfirm,sum(case when d.received_status=2 then 1 else 0 end) totalreceived, sum(d.tatexception) tat,sum(d.qtyexception) qty,sum(case when d.noofsupplyexceptions>c.supplymaxrotation then 1 else 0 end),s.userlocation FROM asnmaster a "
			+ " inner join asndetail d on a.asnid=d.asnid  "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join scandetails s on d.scanid=s.scanid"
			+ " where a.supplydate=date(now()) and a.customerid=? "
			+ " group by a.supplydate,a.customerid,d.partid,c.supplymaxrotation,s.userlocation";
	
	private static final String updateCustomerSummaryOnAdd="replace into parttatsummary(transdate,customerid,partid,totalasn,totalaccepted,totaldispatch,totalreceipts,totaltimedelay,totalqtymismatch,totalsupplydelay,userlocation) "
			+ "SELECT a.supplydate,a.customerid,d.partid,count(d.partid) totalasn,sum(case when d.acknowledged=1 then 1 else 0 end) totalaccepted,sum(case when d.received_status in (1,2) then 1 else 0 end) totaldispatch,sum(case when d.received_status=2 then 1 else 0 end) totalreceived, sum(d.tatexception) tat,sum(d.qtyexception) qty,sum(case when d.noofsupplyexceptions>c.supplymaxrotation then 1 else 0 end),s.userlocation FROM asnmaster a "
			+ " inner join asndetail d on a.asnid=d.asnid  "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join scandetails s on d.scanid=s.scanid"
			+ " inner join asnmaster m on m.asnid=? "
			+ " where a.supplydate=date(now()) and a.customerid=m.customerid "
			+ " group by a.supplydate,a.customerid,d.partid,c.supplymaxrotation,s.userlocation";
	
	private static final String asnReadonly ="select a.asndetailid,s.userlocation,s.serialno,s.partno,c.customerid,c.customername, p.partid,p.partdescription,a.spq,a.binqty,"
			+ " case when a.acknowledged=0 and a.binqty>0 then 'Requested' "
			+ " when a.acknowledged=1 and a.pickbinqty =0  then 'Acknowledged' "
			+ " when a.acknowledged=1 and a.pickbinqty>0  and a.received_bin_qty=0  then 'Delivered' "
			+ " when  a.pickbinqty >0 and a.received_bin_qty>0    then 'Received' "
			+ " Else ' ' end acknoledgeStatus, am.asnstatus,ws.statusname,c.customer_erp_code,ifnull(cm.customerpartid,0) customerpartid,ifnull(a.incidentid,0) incidentid,ifnull(n.incidentname,'') incidentname,a.received_bin_qty FROM scandetails s inner join asndetail a on s.scanid=a.scanid inner join asnmaster am on am.asnid=a.asnid and am.asnid=? "
			+ " inner join customer_master c on c.customerId=am.customerid left join customer_parts_line_map cm on cm.customerid=c.customerid and cm.partid=a.partid inner join part_master p on p.partid=a.partid inner join wms_statusmaster ws on am.asnstatus=ws.statusid "
			+ " left join wms_incidentmaster n on n.incidentid=a.incidentid ";

	private static final String asnPartsEmailContent="select am.asnno,date_format(am.createddate,'%d-%m-%Y %h:%i %p')transdate,a.asndetailid,s.userlocation,s.serialno,s.partno,p.partdescription,a.spq,a.binqty,"
			+ " case when a.acknowledged=0 and a.binqty>0 then 'Requested' "
			+ " when a.acknowledged=1 and a.pickbinqty =0 and  a.supplyexception=0 then 'Acknowledged' "
			+ "	when a.acknowledged=1 and a.pickbinqty =0 and  a.supplyexception=1 then 'Card Fail' "
			+ " when a.acknowledged=1 and a.pickbinqty>0  and a.received_bin_qty=0  then 'Delivered' "
			+ " when a.dispatchconfirmed=1 and a.pickbinqty>0  and a.received_bin_qty=0 and  a.supplyexception=0 then 'Delivered' "
			+ " when a.dispatchconfirmed=1 and a.pickbinqty>0  and a.received_bin_qty=0 and  a.supplyexception=1 then 'Delivered After Card Fail' "
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=0 and  a.supplyexception=0 then 'Received' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=0 and  a.supplyexception=0 then 'Received-Short/Excess' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=1 and  a.supplyexception=0 then 'Received-Short/Excess and TAT' " 
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=1 and  a.supplyexception=0 then 'Received-TAT' "
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=1 and  a.supplyexception=1 then 'Received-CRP' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=0 and  a.supplyexception=1 then 'Received-Short/Excess and CRP' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=1 and  a.supplyexception=1 then 'Received-Short/Excess and CRP and TAT' "
			+ " Else ' ' end acknowledgeStatus, "
			+ " case when a.acknowledged=1 then a.binqty else 0 end ackqty,"
			+ " case when a.pickbinqty>0 then a.pickbinqty else 0 end dispqty,"
			+ " ifnull(a.incidentid,0) incidentid,ifnull(n.incidentname,'') incidentname,a.received_bin_qty,case when a.received_status=2 then (a.received_spq-a.spq) else 0 end shortexcess,am.vechicleno ,a.deliveryvehicleno FROM scandetails s inner join asndetail a on s.scanid=a.scanid inner join asnmaster am on am.asnid=a.asnid "
			+ " inner join customer_master c on c.customerId=am.customerid inner join part_master p on p.partid=a.partid inner join wms_statusmaster ws on am.asnstatus=ws.statusid "
			+ " left join wms_incidentmaster n on n.incidentid=a.incidentid where  a.asndetailid in (<<asnids>>) and  a.received_status=2 and ( a.tatexception=1 ) ";

	private static final String asnShortExcessEmailContent="select am.asnno,date_format(am.createddate,'%d-%m-%Y %h:%i %p')transdate,a.asndetailid,s.userlocation,s.serialno,s.partno,p.partdescription,a.spq,a.binqty,"
			+ " case when a.acknowledged=0 and a.binqty>0 then 'Requested' "
			+ " when a.acknowledged=1 and a.pickbinqty =0 and  a.supplyexception=0 then 'Acknowledged' "
			+ "	when a.acknowledged=1 and a.pickbinqty =0 and  a.supplyexception=1 then 'Card Fail' "
			+ " when a.acknowledged=1 and a.pickbinqty>0  and a.received_bin_qty=0  then 'Delivered' "
			+ " when a.dispatchconfirmed=1 and a.pickbinqty>0  and a.received_bin_qty=0 and  a.supplyexception=0 then 'Delivered' "
			+ " when a.dispatchconfirmed=1 and a.pickbinqty>0  and a.received_bin_qty=0 and  a.supplyexception=1 then 'Delivered After Card Fail' "
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=0 and  a.supplyexception=0 then 'Received' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=0 and  a.supplyexception=0 then 'Received-Short/Excess' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=1 and  a.supplyexception=0 then 'Received-Short/Excess and TAT' " 
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=1 and  a.supplyexception=0 then 'Received-TAT' "
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=1 and  a.supplyexception=1 then 'Received-CRP' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=0 and  a.supplyexception=1 then 'Received-Short/Excess and CRP' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=1 and  a.supplyexception=1 then 'Received-Short/Excess and CRP and TAT' "
			+ " Else ' ' end acknowledgeStatus, "
			+ " case when a.acknowledged=1 then a.binqty else 0 end ackqty,"
			+ " case when a.pickbinqty>0 then a.pickbinqty else 0 end dispqty,"
			+ " ifnull(a.incidentid,0) incidentid,ifnull(n.incidentname,'') incidentname,a.received_bin_qty,case when a.received_status=2 then (a.received_spq-a.spq) else 0 end shortexcess,am.vechicleno ,a.deliveryvehicleno FROM scandetails s inner join asndetail a on s.scanid=a.scanid inner join asnmaster am on am.asnid=a.asnid  "
			+ " inner join customer_master c on c.customerId=am.customerid inner join part_master p on p.partid=a.partid inner join wms_statusmaster ws on am.asnstatus=ws.statusid "
			+ " left join wms_incidentmaster n on n.incidentid=a.incidentid where a.asndetailid in (<<asnids>>) and a.received_status=2 and ( a.qtyexception=1) ";

	private static final String asnTatPartsEmailContent="select am.asnno,date_format(am.createddate,'%d-%m-%Y %h:%i %p')transdate,a.asndetailid,s.userlocation,s.serialno,s.partno,p.partdescription,a.spq,a.binqty,"
			+ " case when a.acknowledged=0 and a.binqty>0 then 'Requested' "
			+ " when a.acknowledged=1 and a.pickbinqty =0 and  a.supplyexception=0 then 'Acknowledged' "
			+ "	when a.acknowledged=1 and a.pickbinqty =0 and  a.supplyexception=1 then 'Card Fail' "
			+ " when a.acknowledged=1 and a.pickbinqty>0  and a.received_bin_qty=0  then 'Delivered' "
			+ " when a.dispatchconfirmed=1 and a.pickbinqty>0  and a.received_bin_qty=0 and  a.supplyexception=0 then 'Delivered' "
			+ " when a.dispatchconfirmed=1 and a.pickbinqty>0  and a.received_bin_qty=0 and  a.supplyexception=1 then 'Delivered After Card Fail' "
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=0 and  a.supplyexception=0 then 'Received' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=0 and  a.supplyexception=0 then 'Received-Short/Excess' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=1 and  a.supplyexception=0 then 'Received-Short/Excess and TAT' " 
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=1 and  a.supplyexception=0 then 'Received-TAT' "
			+ " when a.received_bin_qty>0  and a.qtyexception=0 and tatexception=1 and  a.supplyexception=1 then 'Received-CRP' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=0 and  a.supplyexception=1 then 'Received-Short/Excess and CRP' "
			+ " when a.received_bin_qty>0  and a.qtyexception=1 and tatexception=1 and  a.supplyexception=1 then 'Received-Short/Excess and CRP and TAT' "
			+ " Else ' ' end acknowledgeStatus, "
			+ " case when a.acknowledged=1 then a.binqty else 0 end ackqty,"
			+ " case when a.pickbinqty>0 then a.pickbinqty else 0 end dispqty,"
			+ " ifnull(a.incidentid,0) incidentid,ifnull(n.incidentname,'') incidentname,a.received_bin_qty,case when a.received_status=2 then (a.received_spq-a.spq) else 0 end shortexcess,am.vechicleno ,a.deliveryvehicleno FROM scandetails s inner join asndetail a on s.scanid=a.scanid inner join asnmaster am on am.asnid=a.asnid  "
			+ " inner join customer_master c on c.customerId=am.customerid inner join part_master p on p.partid=a.partid inner join wms_statusmaster ws on am.asnstatus=ws.statusid "
			+ " left join wms_incidentmaster n on n.incidentid=a.incidentid where c.customerid=? and a.received_status=3 and (a.supplyexception=1 and noofsupplyexceptions>=c.supplymaxrotation) ";

	private static final String gateEntryUpdate ="update asnmaster set asnstatus=? where asnid=?";

	private static final String asnLogEntry ="INSERT INTO asnlog(asnid, scanid, statusid, createdby, createddate,tsid)"
			+ "(?,0, ?,1,?, now(),getcurrenttsid(now()))";
	
	private static final String emailAddress=" select customername,email from customer_master where customerid=?";
		
	
	private static final String insAsnDetail="INSERT INTO asndetail(asnid, partid, packageid, spq, binqty,noofpallet,noofremainpalletbin, "
			+ "isactive, createdby, createddate, tsid, received_spq, received_bin_qty,received_status,scanid,palletno)"
			+ "values(?,?,?,?,?,?,?,1,?,now(),getcurrenttsid(now()),0,0,0,0,?)";
	
	private static final String asnPartDetail="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,d.noofpallet,d.noofremainpalletbin,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sublocationshortcode,d.spq qty,d.binqty requestedqty ,c.customer_erp_code,p.packingtypeid,f.packingshortname,d.palletno "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join  packingtypes f on f.packingtypeid=p.packingtypeid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " where a.asnid=? ";
	private static final String asnBinTags="SELECT d.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,p.binqty,p.loadingtype,p.packingtypeid,f.packingshortname,a2.palletno FROM asnmaster a "
			+ " inner join asndetail d on a.asnid=d.asnid inner join  part_master p on p.partid=d.partid inner join  packingtypes f on f.packingtypeid=p.packingtypeid inner join asnscancodes a2  on (a2.asnid=a.asnid and a2.asndetailid=d.asndetailid) "
			+ " where a.asnid=?";
	private static final String asnPartDetailScan="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sublocationshortcode,d.spq qty,d.binqty requestedqty ,c.customer_erp_code,scode.tbisscancode,scode.customerscancode,scode.palletno,pcode.palletscancode "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ " where a.asnid=? and (scode.tbisscancode=? or scode.customerscancode=?) ";

	private static final String asnPartDetailPalletScan="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sublocationshortcode,pal.binqty*p.binqty qty,pal.binqty requestedqty ,c.customer_erp_code,scode.tbisscancode,scode.customerscancode,scode.palletno,pcode.palletscancode "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid "
			+ " inner join asnpallets pal on pal.asnid=a.asnid and pal.asndetailid=d.asndetailid and pal.partid=d.partid and pal.palletno=pcode.palletno "
			+ " inner join asnscancodes scode on scode.asnid=d.asnid and scode.asndetailid =pal.asndetailid and scode.palletno=pcode.palletno "
			+ " where a.asnid=? and pcode.palletscancode=?";

	private static final String asnPartDetailPalletGinDetail="SELECT distinct a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sublocationshortcode,pal.binqty*p.binqty qty,pal.binqty requestedqty ,c.customer_erp_code,scode.palletno,pcode.palletscancode,pcode.linespacepartconfigid,scode.processid "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid "
			+ " inner join asnpallets pal on pal.asnid=a.asnid and pal.asndetailid=d.asndetailid and pal.partid=d.partid and pal.palletno=pcode.palletno "
			+ " inner join asnscancodes scode on scode.asnid=d.asnid and scode.asndetailid =pal.asndetailid and scode.palletno=pcode.palletno "
			+ " where a.asnid=? and scode.processid in (7,6)";

	private static final String getPartSinglePalletStorageSpaceDetail="select distinct c.linespacepartconfigid,partspacename,allocatedbins,usedspace,fifoorder fifoorder,fromlineno,"
			+ "tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,ifnull(r.linerackcode,'') linerackcode,"
			+ "ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,fromlinespaceid,tolinespaceid,c.lineusageid,lu.usagename,c.spaceoccupation,currentfifoorder "
			+ "from linespacepartconfig c "
			+ "left join linerack r on r.linerackid=c.linerackid "
			+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid "
			+ "inner join lineusagemast lu on lu.usageid=c.lineusageid "
			+ "inner join linespaceparts lp on lp.linespacepartconfigid=c.linespacepartconfigid "
			+ "where c.linespacepartconfigid=? ";

	private static final String asnPartNonClubDetail="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sublocationshortcode,d.spq qty,d.binqty requestedqty ,c.customer_erp_code"
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " where a.asnid=? and p.loadingtype=? and p.exclusiveclubno=? order by d.asndetailid asc";

	private static final String asnPartClubDetail="SELECT "
			+ " sum(d.binqty) requestedqty ,max(p.noofpackinginpallet) noofpackinginpallet,p.exclusiveclubno,count('x') noofitems "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " where a.asnid=? and p.loadingtype=2 group by p.exclusiveclubno having sum(d.binqty)>0";

	private static final String asnPalletDetail="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ " p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sublocationshortcode,c.customer_erp_code,d.binqty,d.palletno "
			+ " FROM  asnpallets d inner join asnmaster a on a.asnid=d.asnid  "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " where d.asnid=? order by d.palletno";

	private static final String asnManualPalletNoClubDetail="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sublocationshortcode,d.spq qty,d.binqty requestedqty ,c.customer_erp_code,d.palletno "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " where a.asnid=? order by d.asndetailid asc";

	private static final String insAsnScanCodes="insert into asnscancodes(asnid,asndetailid,tbisscancode,customerscancode,palletno)values(?,?,?,?,?)";
	
	
	private static final String insAsnPallets="insert into asnpallets(asnid,asndetailid,partid,palletno,binqty,loadingtype)values(?,?,?,?,?,?)";

	private static final String insAsnPalletCodes="insert into asnpalletscancodes(asnid,palletscancode,palletno)values(?,?,?)";
	
	private static final String getPalletCount="select count(distinct partid) parts from asnpallets where asnid=? and palletno=? ";
	
	private static final String updateasnGateIn="update asnmaster set asnstatus=5 ,customergateintime=now(),updatedby=?,updateddate=now(),tsid=getcurrenttsid(now()) where asnid=? and vechicleno=?";
	//private static final String updateasnGateOut="update asnmaster set asnstatus=6, customenrgateouttime=now(),updatedby=?,updateddate=now(),tsid=getcurrenttsid(now()) where asnid=? and vechicleno=? ";
	private static final String updateasnTWHGateIn="update asnmaster set asnstatus=7, transitgateintime=now(),updatedby=?,updateddate=now(),tsid=getcurrenttsid(now()) where asnid=? and vechicleno=?";
	private static final String updateasnTWHGateOut="update asnmaster set asnstatus=8 ,transitgateouttime=now(),updatedby=?,updateddate=now(),tsid=getcurrenttsid(now()) where asnid=? and vechicleno=?";
	private static final String updateasnWHGateIn="update asnmaster set asnstatus=9 ,gateindatetime=now(),updatedby=?,updateddate=now(),tsid=getcurrenttsid(now()) where asnid=? and vechicleno=?";
	
	private static final String insertStockMovement="insert into stockmovementheader(customerid,fromusageid,tousageid,movedby,moveddate)values(?,?,?,?,?)";
	private static final String insertStockDetail="insert into part_stock_detail(partid,customerid,sublocationid,totalstock,receivedstock)values(?,?,?,?,?)";
	private static final String updateStockDetail="update part_stock_detail set totalstock=totalstock+?,receivedstock=receivedstock+? where partid=? and customerid=? and sublocationid=?";
	private static final String insertAsnStockDetail="insert into asn_stock_detail(asndetailid,partid,processdate,processstype,stock)values(?,?,now(),?,?)";
	private static final String updateGinReceiptStock="update asndetail set received_spq=received_spq+?,receivedstock=receivedstock+?,received_bin_qty=received_bin_qty+?,received_status=2,receiveddatetime=now() where asnid=? and asndetailid=? ";
	private static final String updateGinReceiptPalletStock="update asnpallets set receivedbinqty=? where asnid=? and asndetailid=? and palletno=? ";
	private static final String updateAsnReceived="update asnmaster set asnstatus=10,updatedby=?,updateddate=now(),tsid=getcurrenttsid(now()) where asnid=? ";
	private static final String updateScanProcess="update asnscancodes set processid=?,lineusageid=? where asndetailid=? and palletno=?";
	private static final String updateStockMovementProcess="update asnscancodes set processid=? where tbisscancode=?";
	private static final String asnPartDetailStockMovementScan="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sl.sublocationid,sublocationshortcode,d.spq qty,d.binqty requestedqty ,"
			+ "c.customer_erp_code,scode.tbisscancode,scode.customerscancode,scode.palletno,pcode.palletscancode,p.repackqty,p.obinqty,"
			+ "pcode.linespacepartconfigid,pstk.holdstock,p.finalpartid,p.finalpartqty,p.exclusiveclubno  "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ " left join part_stock_detail pstk on pstk.partid=p.partid and pstk.sublocationid=sl.sublocationid "
			+ " where (trim(scode.tbisscancode)=trim(?) or trim(pcode.palletscancode)=trim(?)) and scode.processid=? and a.customerid =?";
	
	private static final String mobileAsnPartDetailStockMovementScan="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sl.sublocationid,sublocationshortcode,d.spq qty,d.binqty requestedqty ,"
			+ "c.customer_erp_code,scode.tbisscancode,scode.customerscancode,scode.palletno,pcode.palletscancode,p.repackqty,p.obinqty,pcode.linespacepartconfigid "
			+ " FROM asnmaster a "
			+ " inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join warehouse w on w.warehousid = a.warehouseid "
			+ " inner join unload_doc_master udm on a.unloadingdocid = udm.udc_id "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ " where (trim(scode.tbisscancode)=trim(?) or trim(pcode.palletscancode)=trim(?)) and scode.processid=? "
			+ " and a.warehouseid = ? and a.sublocationid = ? and a.unloadingdocid = ? and a.customerid =?";
	
	private static final String finalPart="SELECT p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ " p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet, "
			+ " p.repackqty,p.obinqty,p.exclusiveclubno,c.primarysublocationid "
			+ " FROM `part_master` p "
			+ " inner join customer_master c on c.customerId=p.customerid "
			+ " where p.partid=?";
	private static final String assemblyPartDetails="SELECT p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ " p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet, "
			+ " p.repackqty,p.obinqty,p.exclusiveclubno,c.primarysublocationid "
			+ " FROM `part_master` p "
			+ " inner join customer_master c on c.customerId=p.customerid "
			+ " where p.finalpartid=?";
	
	/* Asn Incident Log */
	private static final String insertAsnIncidentQuery="INSERT INTO asnincidientlog(asnid, incidentid, scwcomments, documentpath, createdby, createddate, tsid) VALUES (?,?,?,?,?,now(),getcurrenttsid(now()));"; 
	private static final String selectAsnIncident="select asnincidentlogid ,asnid, incidentid, scwcomments, documentpath from asnincidientlog where asnid = ?";
	private static final String deleteAsnIncidentQuery="delete from asnincidientlog where asnincidentlogid=?;";
	private static final String getWorkFlowStatusQry ="select ifnull(configuration_value,'0') configuration_value  from wms_configuration where configuration_name ='TBIS-ConfirmationFlow';";
	private static final String getPartStorageSpace="select c.linespacepartconfigid,partspacename,allocatedbins,usedspace,fifoorder fifoorder,fromlineno,"
			+ "tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,ifnull(r.linerackcode,'') linerackcode,"
			+ "ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,fromlinespaceid,tolinespaceid,'' fromlinespacecode,'' tolinespacecode,c.lineusageid,lu.usagename "
			+ "from linespacepartconfig c "
			+ "left join linerack r on r.linerackid=c.linerackid "
			+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid "
			+ "inner join lineusagemast lu on lu.usageid=c.lineusageid "
			+ "inner join linespaceparts lp on lp.linespacepartconfigid=c.linespacepartconfigid "
			+ "where lp.partid=? and (c.usedspace=0 or c.lastfilledspace<c.allocatedbins) and c.lineusageid=? order by currentfifoorder ";
	
	private static final String asnPartDetailMovementScan="SELECT a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sl.sublocationid,sublocationshortcode,d.spq qty,d.binqty requestedqty ,c.customer_erp_code,scode.tbisscancode,scode.customerscancode,scode.palletno,pcode.palletscancode  "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ " where d.asndetailid=? and p.partid=? and scode.processid=2 ";
	
	private static final String asnPartDetailMovementScanSinglePallet="SELECT distinct a.asnid,d.asndetailid,p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ "p.spq,p.binqty,p.loadingtype,p.noofpackinginpallet,sl.sublocationid,sublocationshortcode,d.spq qty,d.binqty requestedqty ,c.customer_erp_code,scode.palletno,pcode.palletscancode  "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ " where d.asndetailid=? and p.partid=? and scode.processid=0 and pcode.palletscancode=? ";

	private static String insertGoodStockDetails="insert into stockmovementdetail(asnid,asndetailid,partid,tbisscancode,customerscancode,processid,linespacepartconfigid,lineusageid,stockmovementid)"
			+ "values(?,?,?,?,?,?,?,?,?)";
	private static String updateLineSpaceUsedSpace="update linespacepartconfig set usedspace=usedspace+? where linespacepartconfigid=? ";

	private static String updateFifoCurrentOrder="update linespacepartconfig set currentfifoorder=currentfifoorder+? where partid=? and fifoorder=? and lineusageid=?";
	private static String updateFifoNextOrder="update linespacepartconfig set currentfifoorder=currentfifoorder-1 where partid=? and fifoorder!=? and lineusageid=?";
	
	private static String selectMaxFifoOrder="select max(fifoorder) fifoorder from linespacepartconfig where partid=? and lineusageid=?";
	
	private static final String updateAsnStockMovementProcess="update asnscancodes set processid=? where asndetailid=?";
	private static final String updateAsnStockMovementStatus="update asnmaster set stockmovedfromdock=1,lineusageid=? where asnid=? ";
	private static final String updateAsnReceivedStatus="update asnpalletscancodes set receivedstatus=2 where asnid =? and receivedstatus=0 ";
	private static final String checkAsnReceivedStatus="select * from asnpalletscancodes where asnid =? and receivedstatus=2 ";
	private static final String insertAsnPalletIncidentQuery="INSERT INTO asnincidientlog(asnid, incidentid, scwcomments, createdby, createddate, tsid) VALUES (?,6,?,?,now(),getcurrenttsid(now()));";
	
	//Before Inspection
	private static final String asnPalletMovement="SELECT ifnull(pa.primarypartid,p.partid) partid, "
			+ "GROUP_CONCAT(distinct p.partid) partids,count(distinct scode.palletno) qty "
			+ "FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ "inner join  `part_master` p on p.partid=d.partid "
			+ "inner join customer_master c on c.customerId=a.customerid "
			+ "inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ "inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ "inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ "inner join asnpallets pal on pal.asndetailid=d.asndetailid and pal.partid=d.partid and pcode.palletno=pal.palletno "
			+ "left join partassembly pa on pa.assemblypartcode=p.partno  "
			+ "where a.asnid=? and scode.processid=2 group by ifnull(pa.primarypartid,p.partid)";

	private static final String asnPallets="SELECT pal.palletno,pcode.palletscancode,max(p.partid) partid  "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ " inner join asnpallets pal on pal.asndetailid=d.asndetailid and pal.partid=d.partid and pcode.palletno=pal.palletno "
			+ " where d.asnid=? and scode.processid=2 and d.partid in (<<partids>>) group by pal.palletno,pcode.palletscancode ";
	
	private static final String getPartPalletStorageSpace="select distinct c.linespacepartconfigid,partspacename,allocatedbins,usedspace,fifoorder fifoorder,fromlineno,"
			+ "tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,ifnull(r.linerackcode,'') linerackcode,"
			+ "ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,fromlinespaceid,tolinespaceid,c.lineusageid,lu.usagename,c.spaceoccupation,currentfifoorder "
			+ "from linespacepartconfig c "
			+ "left join linerack r on r.linerackid=c.linerackid "
			+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid "
			+ "inner join lineusagemast lu on lu.usageid=c.lineusageid "
			+ "inner join linespaceparts lp on lp.linespacepartconfigid=c.linespacepartconfigid "
			+ "where (c.usedspace=0 or c.lastfilledspace<c.allocatedbins) and lp.partid in (<<partids>>) and c.lineusageid=? order by currentfifoorder ";

	
	/** Single Pallet Storage Space **/
	private static final String asnSinglePalletMovement="SELECT ifnull(pa.primarypartid,p.partid) partid, "
			+ "GROUP_CONCAT(distinct p.partid) partids,1 qty,pcode.palletno,pcode.palletscancode "
			+ "FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ "inner join  `part_master` p on p.partid=d.partid "
			+ "inner join customer_master c on c.customerId=a.customerid "
			+ "inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ "inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ "inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ "inner join asnpallets pal on pal.asndetailid=d.asndetailid and pal.partid=d.partid and pcode.palletno=pal.palletno "
			+ "left join partassembly pa on pa.assemblypartcode=p.partno  "
			+ "where a.asnid=? and scode.processid=0 and pcode.palletscancode=? group by ifnull(pa.primarypartid,p.partid),pcode.palletno,pcode.palletscancode";

	private static final String asnSinglePallets="SELECT pal.palletno,pcode.palletscancode,max(p.partid) partid  "
			+ " FROM asnmaster a inner join asndetail d on a.asnid=d.asnid "
			+ " inner join  `part_master` p on p.partid=d.partid "
			+ " inner join customer_master c on c.customerId=a.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " inner join asnscancodes scode on scode.asndetailid=d.asndetailid "
			+ " inner join asnpalletscancodes pcode on pcode.asnid=a.asnid and pcode.palletno=scode.palletno "
			+ " inner join asnpallets pal on pal.asndetailid=d.asndetailid and pal.partid=d.partid and pcode.palletno=pal.palletno "
			+ " where d.asnid=? and scode.processid=0 and d.partid in (<<partids>>) group by pal.palletno,pcode.palletscancode ";
	
	private static final String getPartSinglePalletStorageSpace="select distinct c.linespacepartconfigid,partspacename,allocatedbins,usedspace,fifoorder fifoorder,fromlineno,"
			+ "tolineno,fromcol,tocol,c.linerackid,c.linerackcompartmentid,ifnull(r.linerackcode,'') linerackcode,"
			+ "ifnull(rc.linerackcompartmentname,'') linerackcompartmentname,fromlinespaceid,tolinespaceid,c.lineusageid,lu.usagename,c.spaceoccupation,currentfifoorder "
			+ "from linespacepartconfig c "
			+ "left join linerack r on r.linerackid=c.linerackid "
			+ "left join linerackcompartment rc on rc.linerackcompartmentid=c.linerackcompartmentid "
			+ "inner join lineusagemast lu on lu.usageid=c.lineusageid "
			+ "inner join linespaceparts lp on lp.linespacepartconfigid=c.linespacepartconfigid "
			+ "where (c.usedspace=0 or c.lastfilledspace<c.allocatedbins) and lp.partid in (<<partids>>) and c.lineusageid=? order by currentfifoorder ";
	/** Single Pallet Storage Space End**/
	private static final String updatePalletMovement="update asnscancodes set processid=? where asnid=? and palletno=?";
	private static final String updatePartDetailReceivedStock="update part_stock_detail p inner join asndetail d on p.partid=d.partid and d.asnid=? "
			+ "inner join  asnscancodes a on a.asndetailid=d.asndetailid inner join part_master pm on pm.partid=d.partid "
			+ "set p.receivedstock=p.receivedstock-(pm.spq)";
			
	private static final String updateAsnReceivedStock="update asndetail d "
			+ "inner join  asnscancodes a on a.asndetailid=d.asndetailid and d.asnid=? inner join part_master pm on pm.partid=d.partid "
			+ "set d.receivedstock=d.receivedstock-(pm.spq)";
					
	private static final String updatePartDetailOverflowStock="update part_stock_detail p inner join asndetail d on p.partid=d.partid and d.asnid=? " 
					+" inner join  asnscancodes a on a.asndetailid=d.asndetailid and a.palletno=? inner join part_master pm on pm.partid=d.partid" 
					+" set p.overflowlocation=p.overflowlocation+(pm.spq)";

	private static final String updateAsnDetailOverflowStock="update asndetail d "+
					" inner join  asnscancodes a on a.asndetailid=d.asndetailid and d.asnid=? and a.palletno=? inner join part_master pm on pm.partid=d.partid" 
					+" set d.overflowlocation=d.overflowlocation+(pm.spq)"; 

	private static final String updatePartDetailPreInspectionStock="update part_stock_detail p inner join asndetail d on p.partid=d.partid and d.asnid=? "+ 
					" inner join  asnscancodes a on a.asndetailid=d.asndetailid and a.palletno=? inner join part_master pm on pm.partid=d.partid "+ 
					" set p.preinspection=p.preinspection+(pm.spq) ";

	private static final String updateAsnDetailPreInspectionStock="update asndetail d "
					+" inner join  asnscancodes a on a.asndetailid=d.asndetailid and d.asnid=? and a.palletno=? inner join part_master pm on pm.partid=d.partid" 
					+" set d.preinspection=d.preinspection+(pm.spq) "; 

	private static final String updatePalletLineSpaceConfig="update asnpalletscancodes set linespacepartconfigid=?,receivedstatus=1 where asnid=? and palletno=? ";
	private static String updatePalletLineSpaceUsedSpace="update linespacepartconfig set usedspace=usedspace+? where linespacepartconfigid=? ";
	private static String updatePalletLineSpaceLastFilled="update linespacepartconfig set lastfilledspace=lastfilledspace+case when lastfilledspace<allocatedbins then 1 else 0 end,lastfilledpartid=? where linespacepartconfigid=? ";

	private static final String updatePartDetailOverflowGinStock="update part_stock_detail p "
			+ "inner join asndetail d on p.partid=d.partid and d.asnid=? " 
			+" inner join  asnpallets  a on a.asnid =d.asnid and a.asndetailid=d.asndetailid  and a.partid=d.partid and a.palletno=? "
			+ "inner join part_master pm on pm.partid=d.partid" 
			+" set p.overflowlocation=p.overflowlocation+(a.receivedbinqty*pm.spq) where pm.partid=? ";

	private static final String updateAsnDetailOverflowGinStock="update asndetail d "+
				" inner join  asnpallets  a on a.asnid =d.asnid and a.asndetailid=d.asndetailid  and a.partid=d.partid and d.asnid=? and a.palletno=? "
				+ "inner join part_master pm on pm.partid=d.partid" 
				+" set d.overflowlocation=d.overflowlocation+(a.receivedbinqty*pm.spq) where pm.partid=? "; 
	
	private static final String updatePartDetailPreInspectionGinStock="update part_stock_detail p inner join asndetail d on p.partid=d.partid and d.asnid=? "+ 
				" inner join  asnpallets  a on a.asnid =d.asnid and a.asndetailid=d.asndetailid  and a.partid=d.partid and a.palletno=? "
				+ "inner join part_master pm on pm.partid=d.partid "+ 
				" set p.preinspection=p.preinspection+(a.receivedbinqty*pm.spq) where pm.partid=? ";
	
	private static final String updateAsnDetailPreInspectionGinStock="update asndetail d "
				+" inner join  asnpallets  a on a.asnid =d.asnid and a.asndetailid=d.asndetailid  and a.partid=d.partid and d.asnid=? and a.palletno=? "
				+ "inner join part_master pm on pm.partid=d.partid" 
				+" set d.preinspection=d.preinspection+(a.receivedbinqty*pm.spq) where pm.partid=? "; 

	private static final String asnPartDetailStockDetail="SELECT p.partid,p.partno,p.partdescription,p.customerpartcode,"
			+ " repackqty spq,obinqty binqty,sl.sublocationid,sublocationshortcode,c.customer_erp_code "
			+ " FROM `part_master` p  "
			+ " inner join customer_master c on c.customerId=p.customerid "
			+ " inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ " where p.partid=? ";

//	private static final String getPartFifoSpaceDetail="select l.fifoorder,l.partspacename,l.linespacepartconfigid,pm.partid ,pm.partdescription,pm.customerpartcode,lineusageid,usagename,l.fromlineno ,l.tolineno ,l.fromcol,"
//			+ "l.tocol,case when usageid=7 then  l.allocatedbins else ceil(l.allocatedbins/pm.onoofpackinginpallet) end allocatedbins,"
//			+ "case when usageid=7 then l.usedspace else ceil(l.usedspace/pm.onoofpackinginpallet) end usedspace,"
//			+ "l.lastfilledspace,l.spaceoccupation,"
//			+ "case when usageid=7 then pm.pnoofstack else 1 end pnoofstack,"
//			+ "case when usageid=7 then  l.usedspace*pm.onoofpackinginpallet*pm.spq else l.usedspace*pm.repackqty end stock "
//			+ "from linespacepartconfig l "
//			+ "inner join linespaceparts p on l.linespacepartconfigid =p.linespacepartconfigid "
//			+ "inner join part_master pm on pm.partid =p.partid "
//			+ "inner join lineusagemast u on u.usageid =l.lineusageid where usageid=? and l.customerid=? order by partno,fifoorder,fromlineno ,fromcol,usageid  ";
	
	
	private static final String getPartFifoSpaceDetail="select l.fifoorder,l.partspacename,l.linespacepartconfigid,GROUP_CONCAT(' ',pm.partid) partid,GROUP_CONCAT(' ',pm.partdescription) partdescription,"
			+ "GROUP_CONCAT(' ',pm.customerpartcode) customerpartcode,"
			+ "lineusageid,usagename,l.fromlineno,l.tolineno,l.fromcol,l.tocol,case when usageid=7 then l.allocatedbins else ceil(l.allocatedbins/max(pm.onoofpackinginpallet)) end allocatedbins,"
			+ "case when usageid=7 then l.usedspace else ceil(l.usedspace/max(pm.onoofpackinginpallet)) end usedspace,l.lastfilledspace,l.spaceoccupation,"
			+ "case when usageid=7 then max(pm.pnoofstack) else 1 end pnoofstack,case when usageid=7 then l.usedspace*max(pm.noofpackinginpallet)*max(pm.spq) else l.usedspace*max(pm.repackqty) end stock "
			+ "from linespacepartconfig l "
			+ "inner join linespaceparts p on l.linespacepartconfigid =p.linespacepartconfigid "
			+ "inner join part_master pm on pm.partid =p.partid "
			+ "inner join lineusagemast u on u.usageid =l.lineusageid "
			+ "where usageid=? and l.customerid=? "
			+ "group by l.fifoorder,l.partspacename,l.linespacepartconfigid,lineusageid,usagename,l.fromlineno ,l.tolineno ,l.fromcol,"
			+ "l.tocol,l.allocatedbins,l.usedspace,l.lastfilledspace,l.spaceoccupation,usageid "
			+ "order by customerpartcode,fifoorder,fromlineno ,fromcol,usageid ";

//	private static final String getPartFifoSpaceDetail="select l.fifoorder,l.partspacename,l.linespacepartconfigid,GROUP_CONCAT(pm.partid) partid,GROUP_CONCAT(pm.partdescription) partdescription,"
//			+ "GROUP_CONCAT(pm.customerpartcode) customerpartcode,lineusageid,usagename,l.fromlineno,l.tolineno,l.fromcol,"
//			+ "l.tocol,case when usageid=7 then  l.allocatedbins else ceil(l.allocatedbins/max(pm.onoofpackinginpallet)) end allocatedbins,"
//			+ "case when usageid=7 then l.usedspace else ceil(l.usedspace/max(pm.onoofpackinginpallet)) end usedspace,"
//			+ "l.lastfilledspace,l.spaceoccupation,"
//			+ "case when usageid=7 then max(pm.pnoofstack) else 1 end pnoofstack,"
//			+ "case when usageid=7 then  l.usedspace*max(pm.noofpackinginpallet)*max(pm.spq) else l.usedspace*max(pm.repackqty) end stock "
//			+ "from linespacepartconfig l "
//			+ "inner join linespaceparts p on l.linespacepartconfigid =p.linespacepartconfigid "
//			+ "inner join part_master pm on pm.partid =p.partid "
//			+ "inner join lineusagemast u on u.usageid =l.lineusageid where usageid=? and l.customerid=? "
//			+ "group by l.fifoorder,l.partspacename,l.linespacepartconfigid,lineusageid,usagename,l.fromlineno ,l.tolineno ,l.fromcol,"
//			+ "l.tocol,l.allocatedbins,l.usedspace,l.lastfilledspace,l.spaceoccupation,usageid "
//			+ "order by customerpartcode,fifoorder,fromlineno ,fromcol,usageid  ";
	
	private static final String lhVehicleRequested="INSERT INTO lhvehicleallocation (tripdate, vechicleno, tripno, triptype, statusid,created_by, created_date,tsid) VALUES(?, ?, 1 ,?, ?, ?,now(),getcurrenttsid(now()));";
	private static final String lhVehicleRequestUpdate="update lhvehicleallocation set  vechicleno=?, tripno=1, statusid=?,update_by, updated_date=now() where tripid=?";
	private static final String updateGateVerifciationState = "UPDATE asnmaster set asnstatus=9, gateentrycomment=?,gateentrystatus=?,gateentryupdatedby=? where asnid=?";
	
	private static final String delAsnDetail="delete from asndetail where asnid=?";
	private static final String delAsnScanCodes="delete from asnscancodes where asnid=?";
	private static final String delAsnPallets="delete from asnpallets where asnid=?";
	private static final String delAsnPalletCodes="delete from asnpalletscancodes where asnid=?";
	
	private static final String updateAsnPartsAddQuery="update asnmaster set partsadd=? where asnid=?";
	
	private static final String checkInwardPalletScan="select * from asnpalletscancodes a where asnid = ? and receivedstatus = 0";
	private static final String insertImportAsnPartsData="insert into asnpartsimport(asnrefno, supplydate, supplytime, vendorcode, vendorpartno, partno, partname, spq, binno, binqrbarcode, palletno) values (?,?,?,?,?,?,?,?,?,?,?);";
	
	// Asn Inbound 
	private static final String selectInboundDataQuery="select i.inboundid, i.containerno ,i.actualinbounddate,i.planinbounddate,i.inboundno,i.customerid from inbounddataimport i inner join inboundfileimport d on d.inboundid =i.inboundid inner join wms_statusmaster ws on ws.statusid = d.statusid where asnstatusid=23 and i.containerno=? and i.inboundid =? group by i.inboundid ,i.containerno ,i.actualinbounddate,i.planinbounddate,i.inboundno,i.customerid";
	private static final String insertInboundAsnMaster="INSERT INTO asnmaster (asnno, customerid, supplydate, asnstatus, vechicleno,createdby ,createddate, tsid,locationid,sublocationid,warehouseid) "
			+ "select getcurrenttsid(now()) asnno,i.customerid,CURRENT_DATE() supplydate,9 asnstatus, ? vechicleno,? createdby,now() createddate,getcurrenttsid(now()) tsid,? locationid,? sublocationid,? warehouseid from inbounddataimport i inner join inboundfileimport d on d.inboundid =i.inboundid inner join wms_statusmaster ws on ws.statusid = d.statusid where asnstatusid=23 and i.containerno=? and i.inboundid =? and i.customerid=? group by i.inboundid ,i.containerno ,i.actualinbounddate,i.planinbounddate,i.inboundno,i.customerid,locationid,sublocationid,warehouseid";
	private static final String insertInboundAsnDetail="INSERT INTO asndetail (asnid, partid, packageid, spq, binqty, pickspq, pickbinqty, createdby, createddate, tsid)"
			+ " select ? asnid,pm.partid,1 packageid,sum(i.totalqty) spq,sum(i.noofbox) binqty,sum(i.totalqty) pickspq,sum(i.noofbox) pickbinqty,? createdby,now() createddate,getcurrenttsid(now()) tsid from inbounddataimport i inner join part_master pm on i.wisepno =pm.partno where asnstatusid=23 and i.containerno=? and i.inboundid =? and i.customerid =? "
			+ "group by asnid,partid,packageid,i.noofbox,i.totalqty ";
	private static final String insertInboundAsnPallet="INSERT INTO asnpallets (asnid, asndetailid, palletno, partid, binqty, loadingtype) "
			+ "select a.asnid asnid,a.asndetailid,i.palletno,pm.partid,sum(i.noofbox) binqty,2 loadingtype from inbounddataimport i inner join part_master pm on i.wisepno =pm.partno inner join asndetail a on a.asnid=? and a.partid=pm.partid where asnstatusid=23 and i.containerno=? and i.inboundid =? and i.customerid =? "
			+ "group by asnid,asndetailid,palletno,partid,i.noofbox";
//	private static final String insertInboundAsnPalletScan="INSERT INTO asnscancodes (asnid, asndetailid, tbisscancode, customerscancode, palletno) select a.asnid asnid,a.asndetailid,'' tbisscancode,'' customerscancode,i.palletno from inbounddataimport i inner join part_master pm on i.wisepno =pm.partno inner join asndetail a on a.asnid=? and a.partid=pm.partid where asnstatusid=23 and i.containerno=? and i.inboundid =? and i.customerid =?";
	private static final String insertInboundAsnPalletScanCode="INSERT INTO asnpalletscancodes (asnid, palletscancode, palletno, linespacepartconfigid) "
			+ "select ? asnid,concat('PAL ',i.caseno) palletscancode,i.palletno,0 linespacepartconfigid "
			+ "from inbounddataimport i inner join part_master pm on i.wisepno =pm.partno where asnstatusid=23 and i.containerno=? and i.inboundid =? and i.customerid =? "
			+ "group by asnid,palletscancode,palletno,linespacepartconfigid";
	private static final String getAsnScanCodes="select a.asnid asnid,a.asndetailid,i.palletno,pm.partid,pm.customerpartcode,c.customer_erp_code,sum(i.noofbox) binqty,sum(i.totalqty) totalqty,i.qtybox,2 loadingtype,sublocationshortcode "
			+ "from inbounddataimport i "
			+ "inner join part_master pm on i.wisepno =pm.partno inner join asndetail a on a.asnid=? and a.partid=pm.partid "
			+ "inner join customer_master c on c.customerid=pm.customerid inner join sublocation sl on c.primarysublocationid=sl.sublocationid "
			+ "where asnstatusid=23 and i.containerno=? and i.inboundid =? and i.customerid =? "
			+ "group by asnid,asndetailid,palletno,partid,customerpartcode,customer_erp_code,i.noofbox,i.totalqty,qtybox,sublocationshortcode";	

	private static final String insertExcludedStockmovementBins="insert into stockmovementexcludedbins(stockmovementid,binno,scancode,created_by, created_date,tsid)values(?,?,?,?,now(),getcurrenttsid(now()))";
	
	public ApiResult<AsnMaster> manageAsnMaster(AsnMaster l) {
		if (l.getAsnId() == 0) {
			return addAsnMaster(l);
		} else {
			return modifyAsnMaster(l);
		}
	}
	public ApiResult<AsnMaster> addScannedAsn(ScanDetails l) {
		String WorkFlowType= getWorkFlowStatus();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		int ctripcount=1;
		int vtripcount=1;
		
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			
			
			
			pstmt = conn.prepareStatement(insMaster,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getVendorId());
			pstmt.setInt(2, l.getAsnStatus());
			pstmt.setLong(3, l.getTransId());			
			pstmt.executeUpdate();
			rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				code=rs.getString(1);
			}
			rs.close();
			pstmt.close();
			pstmt = conn.prepareStatement(insDetail);
			pstmt.clearParameters();
			pstmt.setString(1, code);	
			pstmt.setLong(2, l.getTransId());			
			pstmt.executeUpdate();
			pstmt.close();
			
//			pstmt = conn.prepareStatement(asnLog);
//			pstmt.clearParameters();
//			pstmt.setString(1, code);	
//			pstmt.setLong(2, 2);	
//			pstmt.setString(3, code);	
//			pstmt.executeUpdate();
//			pstmt.close();
			
			pstmt = conn.prepareStatement(updateVehicleTrans);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getTransId());			
			pstmt.executeUpdate();
			pstmt.close();

			pstmt = conn.prepareStatement(updateVehicle);
			pstmt.clearParameters();
			pstmt.setInt(1, 1);
			pstmt.setLong(2, l.getTransId());			
			pstmt.executeUpdate();
			pstmt.close();

			pstmt=conn.prepareStatement(updateCustomerSummaryOnAdd);
			pstmt.setString(1, code);
			pstmt.executeUpdate();
			pstmt.close();
			
			
			pstmt = conn.prepareStatement(customerTripCount);
			pstmt.clearParameters();
			pstmt.setString(1,code);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				ctripcount=rs.getInt("ctrip");
			}
			rs.close();
			pstmt.close();
			
			pstmt = conn.prepareStatement(vehicleTripCount);
			pstmt.clearParameters();
			pstmt.setString(1,code);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				vtripcount=rs.getInt("vtrip");
			}
			rs.close();
			pstmt.close();
			
			pstmt=conn.prepareStatement(updateTripcount);
			pstmt.clearParameters();
			pstmt.setInt(1,ctripcount);
			pstmt.setInt(2,vtripcount);
			pstmt.setString(3, code);
			pstmt.executeUpdate();
			pstmt.close();
			
			
			if(WorkFlowType.equals("0")) {
				System.out.print("Inside Flow:::::::::::::::::"+WorkFlowType);
				pstmt=conn.prepareStatement(updateCardConfirmNoFlow);
				pstmt.setString(1, code);
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(updateCardConfirmStatus);
				pstmt.setString(1, code);
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(updateCardConfirmMasterStatus);
				pstmt.setString(1, code);
				pstmt.setString(2, code);
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.commit();
			result.isSuccess = true;
			result.message = "Part Requested successfully";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	
//	public ApiResult<AsnMaster> addAsnMaster(AsnMaster l) {
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		PreparedStatement bstmt = null;
//		PreparedStatement gstmt = null;
//		ResultSet rs = null;
//		ApiResult result = new ApiResult();
//		String code = "0";
//		try {
//			conn = DatabaseUtil.getConnection();
//			conn.setAutoCommit(false);
//			pstmt = conn.prepareStatement(insertQuery,PreparedStatement.RETURN_GENERATED_KEYS);
//			pstmt.clearParameters();
//			pstmt.setLong(1, l.getCustomerId());
//			pstmt.setString(2, l.getSupplyDate());
//			pstmt.setString(3, l.getSupplyTime());
//			pstmt.setInt(4, l.getUnLoadingDocId());
//			pstmt.setInt(5, 2);
//			pstmt.setString(6, l.getVechicleNo());
//			pstmt.setString(7, l.getDriverName());
//			pstmt.setString(8, l.getDriverMobile());
//			pstmt.setString(9, l.getEwayBillNo());
//			pstmt.setString(10, l.getInvoiceNo());
//			pstmt.setBoolean(11, l.getDirectorTransit());
//			pstmt.setLong(12, l.getTransitasnId());
//			pstmt.setInt(13,l.getUserId());
//			pstmt.setString(14,l.getReturnPack());
//			pstmt.setInt(15,l.getReturnPackQty());
//			pstmt.setInt(16,l.getSubloactionId());
//			pstmt.setInt(17,l.getWarehouseId());
//			pstmt.setInt(18,l.getLoactionId());
//			pstmt.setInt(19,l.getTransitlocationId());
//			pstmt.setString(20,l.getDeliveryNoteNo());
////			pstmt.setInt(20,l.getPrimaryDcokid());
//			pstmt.setInt(21,l.getVehicleTypeId());
//			pstmt.setString(22,l.getRgpNo());
//			pstmt.setDouble(23, l.getFilledSize());
//			pstmt.setDouble(24, l.getFilledCapacity());
//			pstmt.setInt(25,l.getTripType());
//			pstmt.setInt(26, l.getNoOfPallet());
//			pstmt.setInt(27, l.getPartsAdd());
//			pstmt.executeUpdate();
//			rs=pstmt.getGeneratedKeys();
//			if(rs.next()){
//				code=rs.getString(1);
//				l.setAsnId(Long.parseLong(code));
//			}
//			rs.close();
//			pstmt.close();
//			pstmt=conn.prepareStatement(insAsnDetail);
//			for (ScanDetails scanDetails : l.getAsnDetail()) {
//				pstmt.clearParameters();
//				pstmt.setLong(1, l.getAsnId());
//				pstmt.setLong(2, scanDetails.getPartId());
//				pstmt.setLong(3, scanDetails.getPackageId());
//				pstmt.setString(4,scanDetails.getQty());
//				pstmt.setString(5,scanDetails.getBinQty());
//				pstmt.setInt(6, scanDetails.getPallets());
//				pstmt.setInt(7, scanDetails.getRemainingPalletBins());
//				pstmt.setInt(8, l.getUserId());
//				pstmt.addBatch();					
//			}
//			pstmt.executeBatch();
//			pstmt.close();
//			bstmt=conn.prepareStatement(insAsnScanCodes);
//			pstmt=conn.prepareStatement(insAsnPallets);
//			gstmt = conn.prepareStatement(asnPartNonClubDetail);
//			gstmt.setLong(1, l.getAsnId());
//			gstmt.setLong(2, 1);
//			gstmt.setLong(3, 0);
//			rs = gstmt.executeQuery();
//			int palletNo=0;
//			int palletQty=0;
//			int remainingQty=0;
//			int binSno=0;
//			while (rs.next()) {	
//				binSno=0;
//				System.out.println(":::REQUESTED QTY::: " + rs.getInt("requestedqty"));
//				System.out.println(":::NO OF PACKING IN PALLET::: " + rs.getInt("noofpackinginpallet"));
//				int rqty=rs.getInt("requestedqty");
//				palletQty=(rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet"));
//				if(rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet")>0) {
//					palletQty=palletQty+1;
//				}
//				
//				for(int i=0;i<palletQty;i++) {
//					palletNo++;
//					int palletBins=rs.getInt("noofpackinginpallet");
//					if(rqty<rs.getInt("noofpackinginpallet")) {
//						palletBins=rqty;
//					}else {
//						rqty=rqty-rs.getInt("noofpackinginpallet");
//					}					
//					pstmt.clearParameters();
//					pstmt.setLong(1, l.getAsnId());
//					pstmt.setLong(2, rs.getLong("asndetailid"));
//					pstmt.setInt(3, rs.getInt("partid"));
//					pstmt.setInt(4, palletNo);
//					pstmt.setInt(5, palletBins);
//					pstmt.setInt(6, rs.getInt("loadingtype"));
//					pstmt.addBatch();
//					for(int j=0;j<palletBins;j++) {
//						binSno++;
//						String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
//						String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
//						bstmt.clearParameters();
//						bstmt.setLong(1, l.getAsnId());
//						bstmt.setLong(2, rs.getLong("asndetailid"));
//						bstmt.setString(3, tbisScanCode);
//						bstmt.setString(4, customerScanCode);
//						bstmt.setInt(5, palletNo);
//						bstmt.addBatch();
//					}
//				}
//			}
//			pstmt.executeBatch();
//			bstmt.executeBatch();
//			gstmt.close();
//			rs.close();
//			gstmt = conn.prepareStatement(asnPartClubDetail);
//			gstmt.setLong(1, l.getAsnId());
//			rs = gstmt.executeQuery();
//			System.out.println(":::asnPartClubDetail QUERY::: " + asnPartClubDetail);
//			//int clubPacking=0;
//			ArrayList<ScanDetails> clubPackingList=new ArrayList<ScanDetails>();
//			while (rs.next()) {
//				System.out.println(":::NO OF PACKING & REQUEST QTY::: " + rs.getInt("noofpackinginpallet")+" & "+rs.getInt("requestedqty"));
//				ScanDetails d=new ScanDetails();
//				d.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
//				d.setPalletNumber(rs.getInt("noofitems"));
//				palletQty=rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet");
//				remainingQty=rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet");
//				if(remainingQty>0) {
//					palletQty++;
//				}
//				d.setPallets(palletQty);
//				d.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
//				clubPackingList.add(d);
//				//clubPacking=rs.getInt("noofpackinginpallet");
//			}
//			gstmt.close();
//			rs.close();
//			for(ScanDetails excl:clubPackingList) {
//				int clubPacking=excl.getNoOfPackingInPallet();
//				palletQty=excl.getPallets();
//				gstmt = conn.prepareStatement(asnPartNonClubDetail);
//				gstmt.setLong(1, l.getAsnId());
//				gstmt.setLong(2, 2);
//				gstmt.setLong(3, excl.getExclusiveClubNo());
//				rs = gstmt.executeQuery();
//				int currentPallet=0;
//				int remainingBins=0;
//				int currentPalletNo=0;
//				int seriesPalletNo=palletNo;
//				while (rs.next()) {
//					if(excl.getExclusiveClubNo()>0) {
//						currentPalletNo=seriesPalletNo;	
//						palletNo=seriesPalletNo;
//						clubPacking=excl.getNoOfPackingInPallet()/excl.getPalletNumber();
//						currentPallet=0;
//						remainingBins=0;
//						currentPalletNo=0;
//					}
//					int rqty=rs.getInt("requestedqty");
//					binSno=0;
//					boolean filled=false;
//					for(int i=currentPallet;i<palletQty;i++) {
//						if(currentPalletNo!=0) {
//							palletNo=currentPalletNo;
//							currentPalletNo=0;
//						}else {
//							palletNo++;
//						}
//						int palletBins=clubPacking;
//						if(remainingBins>0) {
//							palletBins=remainingBins;
//							remainingBins=0;
//						}
//						if(rqty<palletBins) {
//							remainingBins=palletBins-rqty;
//							palletBins=rqty;
//							filled=true;
//							currentPallet=i;
//							currentPalletNo=palletNo;
//						}else {
//							rqty=rqty-clubPacking;
//							if(rqty==0) {
//								remainingBins=0;
//								currentPallet++;
//							}
//						}					
//						pstmt.clearParameters();
//						pstmt.setLong(1, l.getAsnId());
//						pstmt.setLong(2, rs.getLong("asndetailid"));
//						pstmt.setInt(3, rs.getInt("partid"));
//						pstmt.setInt(4, palletNo);
//						pstmt.setInt(5, palletBins);
//						pstmt.setInt(6, rs.getInt("loadingtype"));
//						pstmt.addBatch();
//						for(int j=0;j<palletBins;j++) {
//							binSno++;
//							String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
//							String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
//							bstmt.clearParameters();
//							bstmt.setLong(1, l.getAsnId());
//							bstmt.setLong(2, rs.getLong("asndetailid"));
//							bstmt.setString(3, tbisScanCode);
//							bstmt.setString(4, customerScanCode);
//							bstmt.setInt(5, palletNo);
//							bstmt.addBatch();
//						}
//						if(filled || rqty==0) break;
//					}
//				}
//				pstmt.executeBatch();
//				gstmt.close();
//				rs.close();			
//				bstmt.executeBatch();
//			}
//			bstmt.close();
////			pstmt=conn.prepareStatement(insAsnScanCodes);
////			gstmt = conn.prepareStatement(asnPartDetail);
////			gstmt.setLong(1, l.getAsnId());
////			rs = gstmt.executeQuery();
////			
////			while (rs.next()) {				
////				int rqty=rs.getInt("requestedqty");
////				for(int i=0;i<rqty;i++) {
////					String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(i+1));
////					String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),i+1);
////					pstmt.clearParameters();
////					pstmt.setLong(1, l.getAsnId());
////					pstmt.setLong(2, rs.getLong("asndetailid"));
////					pstmt.setString(3, tbisScanCode);
////					pstmt.setString(4, customerScanCode);
////					pstmt.addBatch();
////				}
////			}
////			pstmt.executeBatch();
//			pstmt.close();
//			gstmt.close();
//			rs.close();
//			pstmt=conn.prepareStatement(insAsnPalletCodes);
//			gstmt = conn.prepareStatement(asnPalletDetail);
//			gstmt.setLong(1, l.getAsnId());
//			rs = gstmt.executeQuery();
//			int cPalletNo=0;
//			StringBuilder parts=new StringBuilder(100);
//			while (rs.next()) {				
//				if(cPalletNo!=rs.getInt("palletno")) {
//					if(cPalletNo!=0) {
//						pstmt.clearParameters();
//						pstmt.setLong(1, l.getAsnId());
//						pstmt.setString(2, parts.toString());
//						pstmt.setInt(3, cPalletNo);
//						pstmt.addBatch();
//					}
//					cPalletNo=rs.getInt("palletno");
//					parts.setLength(0);
//					parts.append("PAL");
//					parts.append(StringUtils.leftPad(rs.getString("customer_erp_code"), 4,' '));
//					parts.append("N");
//					parts.append(StringUtils.leftPad(rs.getString("palletno"), 4,' '));
//
//				}
//				parts.append("P");
//				parts.append(StringUtils.leftPad(rs.getString("customerpartcode"), 14,' '));
//				parts.append(StringUtils.leftPad(rs.getString("binqty"), 8,'0'));
//				parts.append(StringUtils.leftPad(rs.getString("asndetailid"), 10,'0'));
//			}
//			if(cPalletNo!=0 ) {
//				pstmt.clearParameters();
//				pstmt.setLong(1, l.getAsnId());
//				pstmt.setString(2, parts.toString());
//				pstmt.setInt(3, cPalletNo);
//				pstmt.addBatch();		
//			}
//			pstmt.executeBatch();
//			pstmt.close();
//			gstmt.close();
//			rs.close();
//			conn.commit();
//			result.isSuccess = true;
//			result.message = l.getAsnId()+"";
//		} catch (Exception e) {
//			try {
//				if (conn != null) {
//					conn.rollback();
//				}
//			} catch (SQLException esql) {
//				esql.printStackTrace();
//			}
//			e.printStackTrace();
//			result.isSuccess = false;
//			result.message = e.getMessage();
//		} finally {
//			try {
//				if (conn != null) {
//					conn.close();
//				}
//				if (pstmt != null) {
//					pstmt.close();
//				}
//			} catch (SQLException esql) {
//				esql.printStackTrace();
//			}
//		}
//		return result;
//	}
	
	public ApiResult<AsnMaster> addAsnMaster(AsnMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement bstmt = null;
		PreparedStatement gstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String code = "0";
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertQuery,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getCustomerId());
			pstmt.setString(2, l.getSupplyDate());
			pstmt.setString(3, l.getSupplyTime());
			pstmt.setInt(4, l.getUnLoadingDocId());
			pstmt.setInt(5, 2);
			pstmt.setString(6, l.getVechicleNo());
			pstmt.setString(7, l.getDriverName());
			pstmt.setString(8, l.getDriverMobile());
			pstmt.setString(9, l.getEwayBillNo());
			pstmt.setString(10, l.getInvoiceNo());
			pstmt.setBoolean(11, l.getDirectorTransit());
			pstmt.setLong(12, l.getTransitasnId());
			pstmt.setInt(13,l.getUserId());
			pstmt.setString(14,l.getReturnPack());
			pstmt.setInt(15,l.getReturnPackQty());
			pstmt.setInt(16,l.getSubloactionId());
			pstmt.setInt(17,l.getWarehouseId());
			pstmt.setInt(18,l.getLoactionId());
			pstmt.setInt(19,l.getTransitlocationId());
			pstmt.setString(20,l.getDeliveryNoteNo());
//			pstmt.setInt(20,l.getPrimaryDcokid());
			pstmt.setInt(21,l.getVehicleTypeId());
			pstmt.setString(22,l.getRgpNo());
			pstmt.setDouble(23, l.getFilledSize());
			pstmt.setDouble(24, l.getFilledCapacity());
			pstmt.setInt(25,l.getTripType());
			pstmt.setInt(26, l.getNoOfPallet());
			pstmt.setInt(27, l.getPartsAdd());
			pstmt.executeUpdate();
			rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				code=rs.getString(1);
				l.setAsnId(Long.parseLong(code));
			}
			rs.close();
			pstmt.close();
			pstmt=conn.prepareStatement(insAsnDetail,PreparedStatement.RETURN_GENERATED_KEYS);
			for (ScanDetails scanDetails : l.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setLong(1, l.getAsnId());
				pstmt.setLong(2, scanDetails.getPartId());
				pstmt.setLong(3, scanDetails.getPackageId());
				pstmt.setString(4,scanDetails.getQty());
				pstmt.setString(5,scanDetails.getBinQty());
				pstmt.setInt(6, scanDetails.getPallets());
				pstmt.setInt(7, scanDetails.getRemainingPalletBins());
				pstmt.setInt(8, l.getUserId());
				pstmt.setInt(9, scanDetails.getPalletNumber());
				pstmt.addBatch();									
			}
			pstmt.executeBatch();
			pstmt.close();
			bstmt=conn.prepareStatement(insAsnScanCodes);
			pstmt=conn.prepareStatement(insAsnPallets);
			if(l.getManualPalletNumber()==0) {
				gstmt = conn.prepareStatement(asnPartNonClubDetail);
				gstmt.setLong(1, l.getAsnId());
				gstmt.setLong(2, 1);
				gstmt.setLong(3, 0);
				rs = gstmt.executeQuery();
				int palletNo=0;
				int palletQty=0;
				int remainingQty=0;
				int binSno=0;
				while (rs.next()) {	
					binSno=0;
					System.out.println(":::REQUESTED QTY::: " + rs.getInt("requestedqty"));
					System.out.println(":::NO OF PACKING IN PALLET::: " + rs.getInt("noofpackinginpallet"));
					int rqty=rs.getInt("requestedqty");
					palletQty=(rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet"));
					if(rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet")>0) {
						palletQty=palletQty+1;
					}
					
					for(int i=0;i<palletQty;i++) {
						palletNo++;
						int palletBins=rs.getInt("noofpackinginpallet");
						if(rqty<rs.getInt("noofpackinginpallet")) {
							palletBins=rqty;
						}else {
							rqty=rqty-rs.getInt("noofpackinginpallet");
						}					
						pstmt.clearParameters();
						pstmt.setLong(1, l.getAsnId());
						pstmt.setLong(2, rs.getLong("asndetailid"));
						pstmt.setInt(3, rs.getInt("partid"));
						pstmt.setInt(4, palletNo);
						pstmt.setInt(5, palletBins);
						pstmt.setInt(6, rs.getInt("loadingtype"));
						pstmt.addBatch();
						for(int j=0;j<palletBins;j++) {
							binSno++;
							String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
							String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
							bstmt.clearParameters();
							bstmt.setLong(1, l.getAsnId());
							bstmt.setLong(2, rs.getLong("asndetailid"));
							bstmt.setString(3, tbisScanCode);
							bstmt.setString(4, customerScanCode);
							bstmt.setInt(5, palletNo);
							bstmt.addBatch();
						}
					}
				}
				pstmt.executeBatch();
				bstmt.executeBatch();
				gstmt.close();
				rs.close();
				gstmt = conn.prepareStatement(asnPartClubDetail);
				gstmt.setLong(1, l.getAsnId());
				rs = gstmt.executeQuery();
				System.out.println(":::asnPartClubDetail QUERY::: " + asnPartClubDetail);
				//int clubPacking=0;
				ArrayList<ScanDetails> clubPackingList=new ArrayList<ScanDetails>();
				while (rs.next()) {
					System.out.println(":::NO OF PACKING & REQUEST QTY::: " + rs.getInt("noofpackinginpallet")+" & "+rs.getInt("requestedqty"));
					ScanDetails d=new ScanDetails();
					d.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
					d.setPalletNumber(rs.getInt("noofitems"));
					palletQty=rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet");
					remainingQty=rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet");
					if(remainingQty>0) {
						palletQty++;
					}
					d.setPallets(palletQty);
					d.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
					clubPackingList.add(d);
					//clubPacking=rs.getInt("noofpackinginpallet");
				}
				gstmt.close();
				rs.close();
				for(ScanDetails excl:clubPackingList) {
					int clubPacking=excl.getNoOfPackingInPallet();
					palletQty=excl.getPallets();
					gstmt = conn.prepareStatement(asnPartNonClubDetail);
					gstmt.setLong(1, l.getAsnId());
					gstmt.setLong(2, 2);
					gstmt.setLong(3, excl.getExclusiveClubNo());
					rs = gstmt.executeQuery();
					int currentPallet=0;
					int remainingBins=0;
					int currentPalletNo=0;
					int seriesPalletNo=palletNo;
					while (rs.next()) {
						if(excl.getExclusiveClubNo()>0) {
							currentPalletNo=seriesPalletNo;	
							palletNo=seriesPalletNo;
							clubPacking=excl.getNoOfPackingInPallet()/excl.getPalletNumber();
							currentPallet=0;
							remainingBins=0;
							currentPalletNo=0;
						}
						int rqty=rs.getInt("requestedqty");
						binSno=0;
						boolean filled=false;
						for(int i=currentPallet;i<palletQty;i++) {
							if(currentPalletNo!=0) {
								palletNo=currentPalletNo;
								currentPalletNo=0;
							}else {
								palletNo++;
							}
							int palletBins=clubPacking;
							if(remainingBins>0) {
								palletBins=remainingBins;
	//							remainingBins=0;
							}
							if(rqty<palletBins) {
								remainingBins=palletBins-rqty;
								palletBins=rqty;
								filled=true;
								currentPallet=i;
								currentPalletNo=palletNo;
							}else {
								rqty=rqty-palletBins;
								remainingBins=remainingBins-palletBins;
								if(rqty==0) {
									remainingBins=0;
									currentPallet++;
								}
							}					
							pstmt.clearParameters();
							pstmt.setLong(1, l.getAsnId());
							pstmt.setLong(2, rs.getLong("asndetailid"));
							pstmt.setInt(3, rs.getInt("partid"));
							pstmt.setInt(4, palletNo);
							pstmt.setInt(5, palletBins);
							pstmt.setInt(6, rs.getInt("loadingtype"));
							pstmt.addBatch();
							for(int j=0;j<palletBins;j++) {
								binSno++;
								String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
								String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
								bstmt.clearParameters();
								bstmt.setLong(1, l.getAsnId());
								bstmt.setLong(2, rs.getLong("asndetailid"));
								bstmt.setString(3, tbisScanCode);
								bstmt.setString(4, customerScanCode);
								bstmt.setInt(5, palletNo);
								bstmt.addBatch();
							}
							if(filled || rqty==0) break;
						}
					}
					pstmt.executeBatch();
					gstmt.close();
					rs.close();			
					bstmt.executeBatch();
				}
				bstmt.close();
	//			pstmt=conn.prepareStatement(insAsnScanCodes);
	//			gstmt = conn.prepareStatement(asnPartDetail);
	//			gstmt.setLong(1, l.getAsnId());
	//			rs = gstmt.executeQuery();
	//			
	//			while (rs.next()) {				
	//				int rqty=rs.getInt("requestedqty");
	//				for(int i=0;i<rqty;i++) {
	//					String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(i+1));
	//					String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),i+1);
	//					pstmt.clearParameters();
	//					pstmt.setLong(1, l.getAsnId());
	//					pstmt.setLong(2, rs.getLong("asndetailid"));
	//					pstmt.setString(3, tbisScanCode);
	//					pstmt.setString(4, customerScanCode);
	//					pstmt.addBatch();
	//				}
	//			}
	//			pstmt.executeBatch();
				pstmt.close();
				gstmt.close();
				rs.close();
			}else {
				gstmt = conn.prepareStatement(asnManualPalletNoClubDetail);
				gstmt.setLong(1, l.getAsnId());
				rs = gstmt.executeQuery();
				while(rs.next()) {
					int palletNo=rs.getInt("palletno");
					int binSno=0;				
					int palletBins=rs.getInt("requestedqty");
					pstmt.clearParameters();
					pstmt.setLong(1, l.getAsnId());
					pstmt.setLong(2, rs.getLong("asndetailid"));
					pstmt.setInt(3, rs.getInt("partid"));
					pstmt.setInt(4, palletNo);
					pstmt.setInt(5, palletBins);
					pstmt.setInt(6, rs.getInt("loadingtype"));
					pstmt.executeUpdate();
					for(int j=0;j<palletBins;j++) {
						binSno++;
						String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
						String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
						bstmt.clearParameters();
						bstmt.setLong(1, l.getAsnId());
						bstmt.setLong(2, rs.getLong("asndetailid"));
						bstmt.setString(3, tbisScanCode);
						bstmt.setString(4, customerScanCode);
						bstmt.setInt(5, palletNo);
						bstmt.addBatch();
					}				
					bstmt.executeBatch();
				}
				pstmt.close();
				gstmt.close();
				bstmt.close();
				rs.close();			
			}
			pstmt=conn.prepareStatement(insAsnPalletCodes);
			gstmt = conn.prepareStatement(asnPalletDetail);
			gstmt.setLong(1, l.getAsnId());
			rs = gstmt.executeQuery();
			int cPalletNo=0;
			StringBuilder parts=new StringBuilder(100);
			while (rs.next()) {				
				if(cPalletNo!=rs.getInt("palletno")) {
					if(cPalletNo!=0) {
						pstmt.clearParameters();
						pstmt.setLong(1, l.getAsnId());
						pstmt.setString(2, parts.toString());
						pstmt.setInt(3, cPalletNo);
						pstmt.addBatch();
					}
					cPalletNo=rs.getInt("palletno");
					parts.setLength(0);
					parts.append("PAL");
					parts.append(StringUtils.leftPad(rs.getString("customer_erp_code"), 4,' '));
					parts.append("N");
					parts.append(StringUtils.leftPad(rs.getString("palletno"), 4,' '));

				}
				parts.append("P");
				parts.append(StringUtils.leftPad(rs.getString("customerpartcode"), 14,' '));
				parts.append(StringUtils.leftPad(rs.getString("binqty"), 8,'0'));
				parts.append(StringUtils.leftPad(rs.getString("asndetailid"), 10,'0'));
			}
			if(cPalletNo!=0 ) {
				pstmt.clearParameters();
				pstmt.setLong(1, l.getAsnId());
				pstmt.setString(2, parts.toString());
				pstmt.setInt(3, cPalletNo);
				pstmt.addBatch();		
			}
			pstmt.executeBatch();
			pstmt.close();
			gstmt.close();
			rs.close();
			conn.commit();
			result.isSuccess = true;
			result.message = l.getAsnId()+"";
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
	
	public ApiResult<AsnMaster> modifyAsnMaster(AsnMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement bstmt = null;
		PreparedStatement gstmt = null;
		PreparedStatement dstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			if(l.getAsnStatus()==3) {
				pstmt = conn.prepareStatement(lhVehicleRequested,PreparedStatement.RETURN_GENERATED_KEYS);
				//tripdate, vechicleno, tripno, triptype, statusid,created_by
				pstmt.clearParameters();
				pstmt.setString(1, l.getSupplyDate());
				pstmt.setString(2, l.getVechicleNo());
				pstmt.setLong(3, l.getTripType());
				pstmt.setInt(4, l.getAsnStatus());
				pstmt.setInt(5, l.getUserId());
				pstmt.executeUpdate();
				rs=pstmt.getGeneratedKeys();
				if(rs.next()){
					l.setTripId(rs.getLong(1));
				}
				rs.close();
				pstmt.close();
			}
			pstmt = conn.prepareStatement(modifyQuery);
			pstmt.clearParameters();
			pstmt.setString(1, l.getAsnNo());
			pstmt.setLong(2, l.getCustomerId());
			pstmt.setString(3, l.getSupplyDate());
			pstmt.setString(4, l.getSupplyTime());
			pstmt.setInt(5, l.getUnLoadingDocId());
			pstmt.setInt(6, l.getAsnStatus());
			pstmt.setString(7, l.getVechicleNo());
			pstmt.setString(8, l.getDriverName());
			pstmt.setString(9, l.getDriverMobile());
			pstmt.setString(10, l.getEwayBillNo());
			pstmt.setString(11, l.getInvoiceNo());
			pstmt.setBoolean(12, l.getDirectorTransit());
			pstmt.setLong(13, l.getTransitasnId());
//			pstmt.setString(14, l.getGateInDateTime());
			pstmt.setInt(14,l.getUserId());
			pstmt.setString(15,l.getDeliveryNoteNo());
			pstmt.setInt(16,l.getVehicleTypeId());
			pstmt.setString(17,l.getRgpNo());
			pstmt.setDouble(18, l.getFilledSize());
			pstmt.setDouble(19, l.getFilledCapacity());
			pstmt.setInt(20,l.getTripType());
			pstmt.setInt(21, l.getNoOfPallet());
			pstmt.setLong(22, l.getTripId());
			pstmt.setLong(23, l.getTripId());
			pstmt.setLong(24, l.getAsnId());
			pstmt.executeUpdate();
			
			pstmt.close();
			
			if (l.getPartsAdd()==0) {
				dstmt=conn.prepareStatement(delAsnDetail);
				dstmt.clearParameters();
				dstmt.setLong(1, l.getAsnId());
				dstmt.executeUpdate();
				dstmt.close();
				
				pstmt=conn.prepareStatement(insAsnDetail);
				for (ScanDetails scanDetails : l.getAsnDetail()) {
					pstmt.clearParameters();
					pstmt.setLong(1, l.getAsnId());
					pstmt.setLong(2, scanDetails.getPartId());
					pstmt.setLong(3, scanDetails.getPackageId());
					pstmt.setString(4,scanDetails.getQty());
					pstmt.setString(5,scanDetails.getBinQty());
					pstmt.setInt(6, scanDetails.getPallets());
					pstmt.setInt(7, scanDetails.getRemainingPalletBins());
					pstmt.setInt(8, l.getUserId());
					pstmt.setInt(9, scanDetails.getPalletNumber());
					pstmt.addBatch();					
				}
				pstmt.executeBatch();
				pstmt.close();
				
				dstmt=conn.prepareStatement(delAsnScanCodes);
				dstmt.clearParameters();
				dstmt.setLong(1, l.getAsnId());
				dstmt.executeUpdate();
				dstmt.close();
				
				dstmt=conn.prepareStatement(delAsnPallets);
				dstmt.clearParameters();
				dstmt.setLong(1, l.getAsnId());
				dstmt.executeUpdate();
				dstmt.close();
				
				
				bstmt=conn.prepareStatement(insAsnScanCodes);
				pstmt=conn.prepareStatement(insAsnPallets);
				if(l.getManualPalletNumber()==0) {
					gstmt = conn.prepareStatement(asnPartNonClubDetail);
					gstmt.setLong(1, l.getAsnId());
					gstmt.setLong(2, 1);
					gstmt.setLong(3, 0);
					rs = gstmt.executeQuery();
					int palletNo=0;
					int palletQty=0;
					int remainingQty=0;
					int binSno=0;
					while (rs.next()) {	
						binSno=0;
						System.out.println(":::REQUEST QTY::: " + rs.getInt("requestedqty"));
						System.out.println(":::NO OF PACKING::: " + rs.getInt("noofpackinginpallet"));
						int rqty=rs.getInt("requestedqty");
						palletQty=(rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet"));
						if(rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet")>0) {
							palletQty=palletQty+1;
						}
						
						for(int i=0;i<palletQty;i++) {
							palletNo++;
							int palletBins=rs.getInt("noofpackinginpallet");
							if(rqty<rs.getInt("noofpackinginpallet")) {
								palletBins=rqty;
							}else {
								rqty=rqty-rs.getInt("noofpackinginpallet");
							}					
							pstmt.clearParameters();
							pstmt.setLong(1, l.getAsnId());
							pstmt.setLong(2, rs.getLong("asndetailid"));
							pstmt.setInt(3, rs.getInt("partid"));
							pstmt.setInt(4, palletNo);
							pstmt.setInt(5, palletBins);
							pstmt.setInt(6, rs.getInt("loadingtype"));
							pstmt.addBatch();
							for(int j=0;j<palletBins;j++) {
								binSno++;
								String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
								String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
								bstmt.clearParameters();
								bstmt.setLong(1, l.getAsnId());
								bstmt.setLong(2, rs.getLong("asndetailid"));
								bstmt.setString(3, tbisScanCode);
								bstmt.setString(4, customerScanCode);
								bstmt.setInt(5, palletNo);
								bstmt.addBatch();
							}
						}
					}
					pstmt.executeBatch();
					bstmt.executeBatch();
					gstmt.close();
					rs.close();
					gstmt = conn.prepareStatement(asnPartClubDetail);
					gstmt.setLong(1, l.getAsnId());
					rs = gstmt.executeQuery();
					System.out.println(":::asnPartClubDetail QUERY::: " + asnPartClubDetail);
					//int clubPacking=0;
					ArrayList<ScanDetails> clubPackingList=new ArrayList<ScanDetails>();
					while (rs.next()) {
						System.out.println(":::NO OF PACKING & REQUEST QTY::: " + rs.getInt("noofpackinginpallet")+" & "+rs.getInt("requestedqty"));
						ScanDetails d=new ScanDetails();
						d.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
						d.setPalletNumber(rs.getInt("noofitems"));
						palletQty=rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet");
						remainingQty=rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet");
						if(remainingQty>0) {
							palletQty++;
						}
						d.setPallets(palletQty);
						d.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
						clubPackingList.add(d);
						//clubPacking=rs.getInt("noofpackinginpallet");
					}
					gstmt.close();
					rs.close();
					for(ScanDetails excl:clubPackingList) {
						int clubPacking=excl.getNoOfPackingInPallet();
						palletQty=excl.getPallets();
						gstmt = conn.prepareStatement(asnPartNonClubDetail);
						gstmt.setLong(1, l.getAsnId());
						gstmt.setLong(2, 2);
						gstmt.setLong(3, excl.getExclusiveClubNo());
						rs = gstmt.executeQuery();
						int currentPallet=0;
						int remainingBins=0;
						int currentPalletNo=0;
						int seriesPalletNo=palletNo;
						while (rs.next()) {
							if(excl.getExclusiveClubNo()>0) {
								currentPalletNo=seriesPalletNo;	
								palletNo=seriesPalletNo;
								clubPacking=excl.getNoOfPackingInPallet()/excl.getPalletNumber();
								currentPallet=0;
								remainingBins=0;
								currentPalletNo=0;
							}
							int rqty=rs.getInt("requestedqty");
							binSno=0;
							boolean filled=false;
							for(int i=currentPallet;i<palletQty;i++) {
	/*							if(currentPalletNo!=0) {
									palletNo=currentPalletNo;
									currentPalletNo=0;
								}else {
									palletNo++;
								}
								int palletBins=clubPacking;
								if(remainingBins>0) {
									palletBins=remainingBins;
	//								remainingBins=0;
								}
								if(rqty<palletBins) {
									remainingBins=palletBins-rqty;
									palletBins=rqty;
									filled=true;
									currentPallet=i;
									currentPalletNo=palletNo;
								}else {
									rqty=rqty-clubPacking;
									if(rqty==0) {
										remainingBins=0;
										currentPallet++;
									}
								}*/
								if(currentPalletNo!=0) {
									palletNo=currentPalletNo;
									currentPalletNo=0;
								}else {
									palletNo++;
								}
								int palletBins=clubPacking;
								if(remainingBins>0) {
									palletBins=remainingBins;
	//								remainingBins=0;
								}
								if(rqty<palletBins) {
									remainingBins=palletBins-rqty;
									palletBins=rqty;
									filled=true;
									currentPallet=i;
									currentPalletNo=palletNo;
								}else {
									rqty=rqty-palletBins;
									remainingBins=remainingBins-palletBins;
									if(rqty==0) {
										remainingBins=0;
										currentPallet++;
									}
								}					
								pstmt.clearParameters();
								pstmt.setLong(1, l.getAsnId());
								pstmt.setLong(2, rs.getLong("asndetailid"));
								pstmt.setInt(3, rs.getInt("partid"));
								pstmt.setInt(4, palletNo);
								pstmt.setInt(5, palletBins);
								pstmt.setInt(6, rs.getInt("loadingtype"));
								pstmt.addBatch();
								for(int j=0;j<palletBins;j++) {
									binSno++;
									String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
									String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
									bstmt.clearParameters();
									bstmt.setLong(1, l.getAsnId());
									bstmt.setLong(2, rs.getLong("asndetailid"));
									bstmt.setString(3, tbisScanCode);
									bstmt.setString(4, customerScanCode);
									bstmt.setInt(5, palletNo);
									bstmt.addBatch();
								}
								if(filled || rqty==0) break;
							}
						}
						pstmt.executeBatch();
						gstmt.close();
						rs.close();			
						bstmt.executeBatch();
					}
					bstmt.close();
		//			pstmt=conn.prepareStatement(insAsnScanCodes);
		//			gstmt = conn.prepareStatement(asnPartDetail);
		//			gstmt.setLong(1, l.getAsnId());
		//			rs = gstmt.executeQuery();
		//			
		//			while (rs.next()) {				
		//				int rqty=rs.getInt("requestedqty");
		//				for(int i=0;i<rqty;i++) {
		//					String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(i+1));
		//					String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),i+1);
		//					pstmt.clearParameters();
		//					pstmt.setLong(1, l.getAsnId());
		//					pstmt.setLong(2, rs.getLong("asndetailid"));
		//					pstmt.setString(3, tbisScanCode);
		//					pstmt.setString(4, customerScanCode);
		//					pstmt.addBatch();
		//				}
		//			}
		//			pstmt.executeBatch();
					pstmt.close();
					gstmt.close();
					rs.close();
				}else {
					gstmt = conn.prepareStatement(asnManualPalletNoClubDetail);
					gstmt.setLong(1, l.getAsnId());
					rs = gstmt.executeQuery();
					while(rs.next()) {
						int palletNo=rs.getInt("palletno");
						int binSno=0;				
						int palletBins=rs.getInt("requestedqty");
						pstmt.clearParameters();
						pstmt.setLong(1, l.getAsnId());
						pstmt.setLong(2, rs.getLong("asndetailid"));
						pstmt.setInt(3, rs.getInt("partid"));
						pstmt.setInt(4, palletNo);
						pstmt.setInt(5, palletBins);
						pstmt.setInt(6, rs.getInt("loadingtype"));
						pstmt.executeUpdate();
						for(int j=0;j<palletBins;j++) {
							binSno++;
							String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
							String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
							bstmt.clearParameters();
							bstmt.setLong(1, l.getAsnId());
							bstmt.setLong(2, rs.getLong("asndetailid"));
							bstmt.setString(3, tbisScanCode);
							bstmt.setString(4, customerScanCode);
							bstmt.setInt(5, palletNo);
							bstmt.addBatch();
						}				
						bstmt.executeBatch();
					}
					pstmt.close();
					gstmt.close();
					bstmt.close();
					rs.close();			
				}
				dstmt=conn.prepareStatement(delAsnPalletCodes);
				dstmt.clearParameters();
				dstmt.setLong(1, l.getAsnId());
				dstmt.executeUpdate();
				dstmt.close();
				
				
				pstmt=conn.prepareStatement(insAsnPalletCodes);
				gstmt = conn.prepareStatement(asnPalletDetail);
				gstmt.setLong(1, l.getAsnId());
				rs = gstmt.executeQuery();
				int cPalletNo=0;
				StringBuilder parts=new StringBuilder(100);
				while (rs.next()) {				
					if(cPalletNo!=rs.getInt("palletno")) {
						if(cPalletNo!=0) {
							pstmt.clearParameters();
							pstmt.setLong(1, l.getAsnId());
							pstmt.setString(2, parts.toString());
							pstmt.setInt(3, cPalletNo);
							pstmt.addBatch();
						}
						cPalletNo=rs.getInt("palletno");
						parts.setLength(0);
						parts.append("PAL");
						parts.append(StringUtils.leftPad(rs.getString("customer_erp_code"), 4,' '));
						parts.append("N");
						parts.append(StringUtils.leftPad(rs.getString("palletno"), 4,' '));
	
					}
					parts.append("P");
					parts.append(StringUtils.leftPad(rs.getString("customerpartcode"), 14,' '));
					parts.append(StringUtils.leftPad(rs.getString("binqty"), 8,'0'));
					parts.append(StringUtils.leftPad(rs.getString("asndetailid"), 10,'0'));
				}
				if(cPalletNo!=0 ) {
					pstmt.clearParameters();
					pstmt.setLong(1, l.getAsnId());
					pstmt.setString(2, parts.toString());
					pstmt.setInt(3, cPalletNo);
					pstmt.addBatch();		
				}
				pstmt.executeBatch();
				pstmt.close();
				gstmt.close();
				rs.close();
			}
			conn.commit();
			result.isSuccess = true;
			result.message = l.getAsnId()+"";
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
				if (gstmt != null) {
					gstmt.close();
				}
				if (dstmt != null) {
					dstmt.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

//	public ApiResult<AsnMaster> modifyAsnMaster(AsnMaster l) {
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		PreparedStatement bstmt = null;
//		PreparedStatement gstmt = null;
//		PreparedStatement dstmt = null;
//		ResultSet rs = null;
//		ApiResult result = new ApiResult();
//		try {
//			conn = DatabaseUtil.getConnection();
//			conn.setAutoCommit(false);
//			if(l.getAsnStatus()==3) {
//				pstmt = conn.prepareStatement(lhVehicleRequested,PreparedStatement.RETURN_GENERATED_KEYS);
//				//tripdate, vechicleno, tripno, triptype, statusid,created_by
//				pstmt.clearParameters();
//				pstmt.setString(1, l.getSupplyDate());
//				pstmt.setString(2, l.getVechicleNo());
//				pstmt.setLong(3, l.getTripType());
//				pstmt.setInt(4, l.getAsnStatus());
//				pstmt.setInt(5, l.getUserId());
//				pstmt.executeUpdate();
//				rs=pstmt.getGeneratedKeys();
//				if(rs.next()){
//					l.setTripId(rs.getLong(1));
//				}
//				rs.close();
//				pstmt.close();
//			}
//			pstmt = conn.prepareStatement(modifyQuery);
//			pstmt.clearParameters();
//			pstmt.setString(1, l.getAsnNo());
//			pstmt.setLong(2, l.getCustomerId());
//			pstmt.setString(3, l.getSupplyDate());
//			pstmt.setString(4, l.getSupplyTime());
//			pstmt.setInt(5, l.getUnLoadingDocId());
//			pstmt.setInt(6, l.getAsnStatus());
//			pstmt.setString(7, l.getVechicleNo());
//			pstmt.setString(8, l.getDriverName());
//			pstmt.setString(9, l.getDriverMobile());
//			pstmt.setString(10, l.getEwayBillNo());
//			pstmt.setString(11, l.getInvoiceNo());
//			pstmt.setBoolean(12, l.getDirectorTransit());
//			pstmt.setLong(13, l.getTransitasnId());
////			pstmt.setString(14, l.getGateInDateTime());
//			pstmt.setInt(14,l.getUserId());
//			pstmt.setString(15,l.getDeliveryNoteNo());
//			pstmt.setInt(16,l.getVehicleTypeId());
//			pstmt.setString(17,l.getRgpNo());
//			pstmt.setDouble(18, l.getFilledSize());
//			pstmt.setDouble(19, l.getFilledCapacity());
//			pstmt.setInt(20,l.getTripType());
//			pstmt.setInt(21, l.getNoOfPallet());
//			pstmt.setLong(22, l.getTripId());
//			pstmt.setLong(23, l.getTripId());
//			pstmt.setLong(24, l.getAsnId());
//			pstmt.executeUpdate();
//			
//			pstmt.close();
//			
//			if (l.getPartsAdd()==0) {
//				dstmt=conn.prepareStatement(delAsnDetail);
//				dstmt.clearParameters();
//				dstmt.setLong(1, l.getAsnId());
//				dstmt.executeUpdate();
//				dstmt.close();
//				
//				pstmt=conn.prepareStatement(insAsnDetail);
//				for (ScanDetails scanDetails : l.getAsnDetail()) {
//					pstmt.clearParameters();
//					pstmt.setLong(1, l.getAsnId());
//					pstmt.setLong(2, scanDetails.getPartId());
//					pstmt.setLong(3, scanDetails.getPackageId());
//					pstmt.setString(4,scanDetails.getQty());
//					pstmt.setString(5,scanDetails.getBinQty());
//					pstmt.setInt(6, scanDetails.getPallets());
//					pstmt.setInt(7, scanDetails.getRemainingPalletBins());
//					pstmt.setInt(8, l.getUserId());
//					pstmt.addBatch();					
//				}
//				pstmt.executeBatch();
//				pstmt.close();
//				
//				dstmt=conn.prepareStatement(delAsnScanCodes);
//				dstmt.clearParameters();
//				dstmt.setLong(1, l.getAsnId());
//				dstmt.executeUpdate();
//				dstmt.close();
//				
//				dstmt=conn.prepareStatement(delAsnPallets);
//				dstmt.clearParameters();
//				dstmt.setLong(1, l.getAsnId());
//				dstmt.executeUpdate();
//				dstmt.close();
//				
//				
//				bstmt=conn.prepareStatement(insAsnScanCodes);
//				pstmt=conn.prepareStatement(insAsnPallets);
//				gstmt = conn.prepareStatement(asnPartNonClubDetail);
//				gstmt.setLong(1, l.getAsnId());
//				gstmt.setLong(2, 1);
//				gstmt.setLong(3, 0);
//				rs = gstmt.executeQuery();
//				int palletNo=0;
//				int palletQty=0;
//				int remainingQty=0;
//				int binSno=0;
//				while (rs.next()) {	
//					binSno=0;
//					System.out.println(":::REQUEST QTY::: " + rs.getInt("requestedqty"));
//					System.out.println(":::NO OF PACKING::: " + rs.getInt("noofpackinginpallet"));
//					int rqty=rs.getInt("requestedqty");
//					palletQty=(rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet"));
//					if(rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet")>0) {
//						palletQty=palletQty+1;
//					}
//					
//					for(int i=0;i<palletQty;i++) {
//						palletNo++;
//						int palletBins=rs.getInt("noofpackinginpallet");
//						if(rqty<rs.getInt("noofpackinginpallet")) {
//							palletBins=rqty;
//						}else {
//							rqty=rqty-rs.getInt("noofpackinginpallet");
//						}					
//						pstmt.clearParameters();
//						pstmt.setLong(1, l.getAsnId());
//						pstmt.setLong(2, rs.getLong("asndetailid"));
//						pstmt.setInt(3, rs.getInt("partid"));
//						pstmt.setInt(4, palletNo);
//						pstmt.setInt(5, palletBins);
//						pstmt.setInt(6, rs.getInt("loadingtype"));
//						pstmt.addBatch();
//						for(int j=0;j<palletBins;j++) {
//							binSno++;
//							String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
//							String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
//							bstmt.clearParameters();
//							bstmt.setLong(1, l.getAsnId());
//							bstmt.setLong(2, rs.getLong("asndetailid"));
//							bstmt.setString(3, tbisScanCode);
//							bstmt.setString(4, customerScanCode);
//							bstmt.setInt(5, palletNo);
//							bstmt.addBatch();
//						}
//					}
//				}
//				pstmt.executeBatch();
//				bstmt.executeBatch();
//				gstmt.close();
//				rs.close();
//				gstmt = conn.prepareStatement(asnPartClubDetail);
//				gstmt.setLong(1, l.getAsnId());
//				rs = gstmt.executeQuery();
//				System.out.println(":::asnPartClubDetail QUERY::: " + asnPartClubDetail);
//				//int clubPacking=0;
//				ArrayList<ScanDetails> clubPackingList=new ArrayList<ScanDetails>();
//				while (rs.next()) {
//					System.out.println(":::NO OF PACKING & REQUEST QTY::: " + rs.getInt("noofpackinginpallet")+" & "+rs.getInt("requestedqty"));
//					ScanDetails d=new ScanDetails();
//					d.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
//					d.setPalletNumber(rs.getInt("noofitems"));
//					palletQty=rs.getInt("requestedqty")/rs.getInt("noofpackinginpallet");
//					remainingQty=rs.getInt("requestedqty")%rs.getInt("noofpackinginpallet");
//					if(remainingQty>0) {
//						palletQty++;
//					}
//					d.setPallets(palletQty);
//					d.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
//					clubPackingList.add(d);
//					//clubPacking=rs.getInt("noofpackinginpallet");
//				}
//				gstmt.close();
//				rs.close();
//				for(ScanDetails excl:clubPackingList) {
//					int clubPacking=excl.getNoOfPackingInPallet();
//					palletQty=excl.getPallets();
//					gstmt = conn.prepareStatement(asnPartNonClubDetail);
//					gstmt.setLong(1, l.getAsnId());
//					gstmt.setLong(2, 2);
//					gstmt.setLong(3, excl.getExclusiveClubNo());
//					rs = gstmt.executeQuery();
//					int currentPallet=0;
//					int remainingBins=0;
//					int currentPalletNo=0;
//					int seriesPalletNo=palletNo;
//					while (rs.next()) {
//						if(excl.getExclusiveClubNo()>0) {
//							currentPalletNo=seriesPalletNo;	
//							palletNo=seriesPalletNo;
//							clubPacking=excl.getNoOfPackingInPallet()/excl.getPalletNumber();
//							currentPallet=0;
//							remainingBins=0;
//							currentPalletNo=0;
//						}
//						int rqty=rs.getInt("requestedqty");
//						binSno=0;
//						boolean filled=false;
//						for(int i=currentPallet;i<palletQty;i++) {
//							if(currentPalletNo!=0) {
//								palletNo=currentPalletNo;
//								currentPalletNo=0;
//							}else {
//								palletNo++;
//							}
//							int palletBins=clubPacking;
//							if(remainingBins>0) {
//								palletBins=remainingBins;
//								remainingBins=0;
//							}
//							if(rqty<palletBins) {
//								remainingBins=palletBins-rqty;
//								palletBins=rqty;
//								filled=true;
//								currentPallet=i;
//								currentPalletNo=palletNo;
//							}else {
//								rqty=rqty-clubPacking;
//								if(rqty==0) {
//									remainingBins=0;
//									currentPallet++;
//								}
//							}					
//							pstmt.clearParameters();
//							pstmt.setLong(1, l.getAsnId());
//							pstmt.setLong(2, rs.getLong("asndetailid"));
//							pstmt.setInt(3, rs.getInt("partid"));
//							pstmt.setInt(4, palletNo);
//							pstmt.setInt(5, palletBins);
//							pstmt.setInt(6, rs.getInt("loadingtype"));
//							pstmt.addBatch();
//							for(int j=0;j<palletBins;j++) {
//								binSno++;
//								String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
//								String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
//								bstmt.clearParameters();
//								bstmt.setLong(1, l.getAsnId());
//								bstmt.setLong(2, rs.getLong("asndetailid"));
//								bstmt.setString(3, tbisScanCode);
//								bstmt.setString(4, customerScanCode);
//								bstmt.setInt(5, palletNo);
//								bstmt.addBatch();
//							}
//							if(filled || rqty==0) break;
//						}
//					}
//					pstmt.executeBatch();
//					gstmt.close();
//					rs.close();			
//					bstmt.executeBatch();
//				}
//				bstmt.close();
//	//			pstmt=conn.prepareStatement(insAsnScanCodes);
//	//			gstmt = conn.prepareStatement(asnPartDetail);
//	//			gstmt.setLong(1, l.getAsnId());
//	//			rs = gstmt.executeQuery();
//	//			
//	//			while (rs.next()) {				
//	//				int rqty=rs.getInt("requestedqty");
//	//				for(int i=0;i<rqty;i++) {
//	//					String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),String.valueOf(i+1));
//	//					String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("binqty"), rs.getString("sublocationshortcode"),i+1);
//	//					pstmt.clearParameters();
//	//					pstmt.setLong(1, l.getAsnId());
//	//					pstmt.setLong(2, rs.getLong("asndetailid"));
//	//					pstmt.setString(3, tbisScanCode);
//	//					pstmt.setString(4, customerScanCode);
//	//					pstmt.addBatch();
//	//				}
//	//			}
//	//			pstmt.executeBatch();
//				pstmt.close();
//				gstmt.close();
//				rs.close();
//				
//				dstmt=conn.prepareStatement(delAsnPalletCodes);
//				dstmt.clearParameters();
//				dstmt.setLong(1, l.getAsnId());
//				dstmt.executeUpdate();
//				dstmt.close();
//				
//				
//				pstmt=conn.prepareStatement(insAsnPalletCodes);
//				gstmt = conn.prepareStatement(asnPalletDetail);
//				gstmt.setLong(1, l.getAsnId());
//				rs = gstmt.executeQuery();
//				int cPalletNo=0;
//				StringBuilder parts=new StringBuilder(100);
//				while (rs.next()) {				
//					if(cPalletNo!=rs.getInt("palletno")) {
//						if(cPalletNo!=0) {
//							pstmt.clearParameters();
//							pstmt.setLong(1, l.getAsnId());
//							pstmt.setString(2, parts.toString());
//							pstmt.setInt(3, cPalletNo);
//							pstmt.addBatch();
//						}
//						cPalletNo=rs.getInt("palletno");
//						parts.setLength(0);
//						parts.append("PAL");
//						parts.append(StringUtils.leftPad(rs.getString("customer_erp_code"), 4,' '));
//						parts.append("N");
//						parts.append(StringUtils.leftPad(rs.getString("palletno"), 4,' '));
//	
//					}
//					parts.append("P");
//					parts.append(StringUtils.leftPad(rs.getString("customerpartcode"), 14,' '));
//					parts.append(StringUtils.leftPad(rs.getString("binqty"), 8,'0'));
//					parts.append(StringUtils.leftPad(rs.getString("asndetailid"), 10,'0'));
//				}
//				if(cPalletNo!=0 ) {
//					pstmt.clearParameters();
//					pstmt.setLong(1, l.getAsnId());
//					pstmt.setString(2, parts.toString());
//					pstmt.setInt(3, cPalletNo);
//					pstmt.addBatch();		
//				}
//				pstmt.executeBatch();
//				pstmt.close();
//				gstmt.close();
//				rs.close();
//			}
//			conn.commit();
//			result.isSuccess = true;
//			result.message = l.getAsnId()+"";
//		} catch (Exception e) {
//			try {
//				if (conn != null) {
//					conn.rollback();
//				}
//			} catch (SQLException esql) {
//				esql.printStackTrace();
//			}
//			e.printStackTrace();
//			result.isSuccess = false;
//			result.message = e.getMessage();
//		} finally {
//			try {
//				if (conn != null) {
//					conn.close();
//				}
//				if (pstmt != null) {
//					pstmt.close();
//				}
//				if (gstmt != null) {
//					gstmt.close();
//				}
//				if (dstmt != null) {
//					dstmt.close();
//				}
//			} catch (SQLException esql) {
//				esql.printStackTrace();
//			}
//		}
//		return result;
//	}
//	
	public ApiResult<AsnMaster> updateAsnGateStatus(GateEntryInput l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		String qry = updateasnGateIn;
		System.out.println(":::LAST ASN STATUS ID::: " + l.getLastAsnStatusId());
		if ("3".equals(l.getLastAsnStatusId())) {
			qry=updateasnGateIn;
		}else if ("6".equals(l.getLastAsnStatusId())) {
				qry=updateasnTWHGateOut;
		}else if ("7".equals(l.getLastAsnStatusId())) {
			qry=updateasnTWHGateOut;
		}else if ("8".equals(l.getLastAsnStatusId())) {
			qry=updateasnWHGateIn;
		}
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(qry);
			pstmt.clearParameters();
			pstmt.setInt(1,l.getUserId());
			pstmt.setLong(2, l.getAsnId());
			pstmt.setString(3, l.getVechicleNo());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Gate Entry/Exit updated";
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
	
//	public ApiResult<AsnMaster> getPartRequest(int AsnMasterId) {
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		Connection conn = null;
//		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
//		AsnMaster l = new AsnMaster();
//		try {
//			conn = DatabaseUtil.getConnection();
//			pstmt = conn.prepareStatement(select);
//			pstmt.setInt(1, AsnMasterId);
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				l.setAsnId(rs.getInt("asnid"));
//				l.setAsnNo(rs.getString("asnno"));
//				l.setCustomerId(rs.getInt("customerid"));
//				l.setSupplyDate(rs.getString("supplydate"));
//				l.setSupplyTime(rs.getString("supplytime"));
//				l.setUnLoadingDocId(rs.getInt("unloadingdocid"));
//				l.setAsnStatus(rs.getInt("asnstatus"));
//				l.setVechicleNo(rs.getString("vechicleno"));
//				l.setDriverName(rs.getString("drivername"));
//				l.setDriverMobile(rs.getString("drivermobile"));
//				l.setEwayBillNo(rs.getString("ewaybillno"));
//				l.setInvoiceNo(rs.getString("invoiceno"));
//				l.setDeliveryNoteNo(rs.getString("deliverynoteno"));
//				l.setDirectorTransit(rs.getBoolean("directortransit"));
//				l.setTransitasnId(rs.getInt("transitasnid"));
//				l.setGateIndatetime(rs.getString("gateindatetime"));
//				l.setCardsIssued(rs.getInt("cardsissued"));
//				l.setCardsDispatched(rs.getInt("carddispatched"));
//				l.setCardsReceived(rs.getInt("cardsreceived"));
//				l.setCardsAcknowledged(rs.getInt("cardsacknowledged"));
//				l.setReturnPack(rs.getString("returnpack"));
//				l.setReturnPackQty(rs.getInt("returnpackqty"));
//				l.setWarehouseId(rs.getInt("warehouseid"));
//				l.setSubloactionId(rs.getInt("sublocationid"));
//				l.setLoactionId(rs.getInt("locationid"));
//				l.setLocationShortName(rs.getString("locationshortcode"));
//				l.setWarehouseShortName(rs.getString("warehouseshortcode"));
//				l.setSublocationShortName(rs.getString("sublocationshortcode"));
//				l.setDockName(rs.getString("udcname"));
//				l.setCustomerCode(rs.getString("customer_erp_code"));
//				l.setCustomerName(rs.getString("customername"));
//				l.setTransitlocationId(rs.getInt("transitlocationid"));
//				l.setPrimaryDcokid(rs.getInt("primarydockid"));
//				l.setCardsConfirmed(rs.getInt("cardsconfirmed"));
//				l.setDispatchConfirmed(rs.getInt("dispatchconfirmed"));
//			}
//			l.setAsnDetail(getAsnPartDetail(AsnMasterId));
//			result.result = l;
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.isSuccess = false;
//			result.message = e.getMessage();
//			result.result = null;
//		} finally {
//			try {
//				if (conn != null) {
//					conn.close();
//				}
//				if (pstmt != null) {
//					pstmt.close();
//				}
//				if (rs != null) {
//					rs.close();
//				}
//			} catch (SQLException esql) {
//				esql.printStackTrace();
//			}
//		}
//		return result;
//	}
//	
	
	
	public ApiResult<AsnMaster> getAsnMaster(int AsnMasterId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		AsnMaster l = new AsnMaster();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(select);
			pstmt.setInt(1, AsnMasterId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setAsnId(rs.getInt("asnid"));
				l.setAsnNo(rs.getString("asnno"));
				l.setCustomerId(rs.getInt("customerid"));
				l.setSupplyDate(rs.getString("supplydate"));
				l.setSupplyTime(rs.getString("supplytime"));
				l.setUnLoadingDocId(rs.getInt("unloadingdocid"));
				l.setAsnStatus(rs.getInt("asnstatus"));
				l.setVechicleNo(rs.getString("vechicleno"));
				l.setDriverName(rs.getString("drivername"));
				l.setDriverMobile(rs.getString("drivermobile"));
				l.setEwayBillNo(rs.getString("ewaybillno"));
				l.setInvoiceNo(rs.getString("invoiceno"));
				l.setDeliveryNoteNo(rs.getString("deliverynoteno"));
				l.setDirectorTransit(rs.getBoolean("directortransit"));
				l.setTransitasnId(rs.getInt("transitasnid"));
				l.setGateIndatetime(rs.getString("gateindatetime"));
				l.setCardsIssued(rs.getInt("cardsissued"));
				l.setCardsDispatched(rs.getInt("carddispatched"));
				l.setCardsReceived(rs.getInt("cardsreceived"));
				l.setCardsAcknowledged(rs.getInt("cardsacknowledged"));
				l.setReturnPack(rs.getString("returnpack"));
				l.setReturnPackQty(rs.getInt("returnpackqty"));
				l.setWarehouseId(rs.getInt("warehouseid"));
				l.setSubloactionId(rs.getInt("sublocationid"));
				l.setLoactionId(rs.getInt("locationid"));
				l.setLocationShortName(rs.getString("locationshortcode"));
				l.setWarehouseShortName(rs.getString("warehouseshortcode"));
				l.setSublocationShortName(rs.getString("sublocationshortcode"));
				l.setDockName(rs.getString("udcname"));
				l.setCustomerCode(rs.getString("customer_erp_code"));
				l.setCustomerName(rs.getString("customername"));
				l.setTransitlocationId(rs.getInt("transitlocationid"));
				l.setPrimaryDcokid(rs.getInt("primarydockid"));
				l.setCardsConfirmed(rs.getInt("cardsconfirmed"));
				l.setDispatchConfirmed(rs.getInt("dispatchconfirmed"));
				l.setVehicleTypeId(rs.getInt("vehicletypeid"));
				l.setRgpNo(rs.getString("rgpno"));
				l.setFilledSize(rs.getDouble("filledsize"));
				l.setFilledCapacity(rs.getDouble("filledcapacity"));
				l.setStockMovedFromDock(rs.getInt("stockmovedfromdock"));
				l.setToProcessId(rs.getInt("lineusageid"));
				l.setTripType(rs.getInt("triptype"));
				l.setNoOfPallet(rs.getInt("noofpallet"));
				l.setTripId(rs.getLong("tripid"));
				l.setPartsAdd(rs.getInt("partsadd"));
				l.setManualPalletNumber(rs.getInt("manualpalletnumbering"));
			}
			l.setAsnDetail(getAsnPartDetail(AsnMasterId));
			l.setAsnBinTag(getAsnBinTags(AsnMasterId));
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
	public ArrayList<ScanDetails> getAsnPartDetail(long asnMasterId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ArrayList<ScanDetails> result = new ArrayList<ScanDetails>();
		ScanDetails l = new ScanDetails();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(asnPartDetail);
			pstmt.setLong(1, asnMasterId);
			System.out.println(":::asnPartDetail QUERY::: " + asnPartDetail);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				l = new ScanDetails();
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
//				l.setVendorId(rs.getInt("customerid"));
//				l.setVendorName(rs.getString("customername"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("qty"));
//				l.setUserLocation(rs.getString("userlocation"));
				l.setPartNo(rs.getString("partno"));
//				l.setSerialNo(rs.getString("serialno"));
				l.setBinQty(rs.getString("requestedqty"));
				l.setSubLocationShortCode(rs.getString("sublocationshortcode"));
				l.setLoadingType(rs.getInt("loadingtype"));
				l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
				l.setCustomerPartCode(rs.getString("customerpartcode"));
				l.setSpq(rs.getString("spq"));
//				l.setBinQty(rs.getString("binqty"));
				l.setPackageId(rs.getInt("packingtypeid"));
				l.setPackingShortName(rs.getString("packingshortname"));
				l.setPallets(rs.getInt("noofpallet"));
				l.setRemainingPalletBins(rs.getInt("noofremainpalletbin"));
				l.setPalletNumber(rs.getInt("palletno"));
				result.add(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	public ArrayList<ASNBinTag> getAsnBinTags(long asnMasterId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ArrayList<ASNBinTag> result = new ArrayList<ASNBinTag>();
		ASNBinTag l = new ASNBinTag();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(asnBinTags);
			pstmt.setLong(1, asnMasterId);
			System.out.println(":::asnBinTags QUERY::: " + asnBinTags);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				l = new ASNBinTag();
				l.setAsnId(rs.getLong("asnid"));
				l.setAsnDetailId(rs.getInt("asndetailid"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setPartNo(rs.getString("partno"));
				l.setCustomerPartCode(rs.getString("customerpartcode"));
				l.setAsnBinQty(rs.getInt("binqty"));
				l.setLoadingType(rs.getInt("loadingtype"));
				l.setPackingShortName(rs.getString("packingshortname"));
				l.setPalletNo(rs.getInt("palletno"));
				result.add(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	public ApiResult<ScanDetails> getAsnDetail(long AsnMasterId,String scanData) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ScanDetails> result = new ApiResult<ScanDetails>();
		ScanDetails l = new ScanDetails();
		try {
			conn = DatabaseUtil.getConnection();
			if(AsnMasterId==0) {
				pstmt = conn.prepareStatement(asnAllDetail);
				pstmt.setString(1, scanData);
			}else {
				pstmt = conn.prepareStatement(asnDetail);
				pstmt.setLong(1, AsnMasterId);
				pstmt.setString(2, scanData);
			}
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setVendorId(rs.getInt("customerid"));
				l.setVendorName(rs.getString("customername"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("binqty"));
				l.setUserLocation(rs.getString("userlocation"));
				l.setPartNo(rs.getString("partno"));
				l.setSerialNo(rs.getString("serialno"));
			}
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
	public ApiResult<ArrayList<ScanDetails>> getAsnDispatchDetail(long AsnMasterId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ArrayList<ScanDetails>> result = new ApiResult<ArrayList<ScanDetails>>();
		ArrayList<ScanDetails> dispatchParts = new ArrayList<ScanDetails>();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(asnDispatchedDetail);
			pstmt.setLong(1, AsnMasterId);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ScanDetails l = new ScanDetails();
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setScanData(rs.getString("scandata"));
				l.setVendorId(rs.getInt("customerid"));
				l.setVendorName(rs.getString("customername"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("binqty"));
				l.setUserLocation(rs.getString("userlocation"));
				l.setPartNo(rs.getString("partno"));
				l.setSerialNo(rs.getString("serialno"));
				dispatchParts.add(l);
			}
			result.result = dispatchParts;
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
	public ApiResult<ScanDetails> getAsnDispatchCardDetail(ScanDetails s) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ScanDetails> result = new ApiResult<ScanDetails>();
		ScanDetails l = new ScanDetails();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(asnDispatchedCardDetail);
//			pstmt.setLong(1, s.getTransId());
			pstmt.setString(1, s.getScanData());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setScanData(rs.getString("scandata"));
				l.setVendorId(rs.getInt("customerid"));
				l.setVendorName(rs.getString("customername"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("binqty"));
				l.setReceivedQty(rs.getInt("binqty"));
				l.setUserLocation(rs.getString("userlocation"));
				l.setPartNo(rs.getString("partno"));
				l.setSerialNo(rs.getString("serialno"));
				l.setVendorCode(rs.getString("customer_erp_code"));
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
	public ApiResult<ScanDetails> getAsnDispatchConfirmedCardDetail(ScanDetails s) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ScanDetails> result = new ApiResult<ScanDetails>();
		ScanDetails l = new ScanDetails();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(asnDispatchConfirmedCardDetail);
//			pstmt.setLong(1, s.getTransId());
			pstmt.setString(1, s.getScanData());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setScanData(rs.getString("scandata"));
				l.setVendorId(rs.getInt("customerid"));
				l.setVendorName(rs.getString("customername"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("binqty"));
				l.setReceivedQty(rs.getInt("binqty"));
				l.setUserLocation(rs.getString("userlocation"));
				l.setPartNo(rs.getString("partno"));
				l.setSerialNo(rs.getString("serialno"));
				l.setVendorCode(rs.getString("customer_erp_code"));
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
	
	public ApiResult<AsnMaster> updateAsnCardConfirmed(AsnMaster asn) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(updateCardConfirm);
			for (ScanDetails scanDetails : asn.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setLong(1, asn.getAsnId());
				pstmt.setLong(2, scanDetails.getTransId());
				pstmt.addBatch();					
			}
			pstmt.executeBatch();
			pstmt.close();
			pstmt=conn.prepareStatement(updateCardConfirmStatus);
			pstmt.setLong(1, asn.getAsnId());
			pstmt.executeUpdate();
			pstmt.close();
			pstmt=conn.prepareStatement(updateCardConfirmMasterStatus);
			pstmt.setLong(1, asn.getAsnId());
			pstmt.setLong(2, asn.getAsnId());
			pstmt.executeUpdate();
			pstmt.close();
			pstmt=conn.prepareStatement(updateCustomerTransactionSummary);
			pstmt.setLong(1, asn.getCustomerId());
			pstmt.executeUpdate();
//			asn.setAsnStatus(4);
//			asnLogUpdate(conn,asn);
			conn.commit();
			result.isSuccess = true;
			result.message = "Part Request Card confirmation successful";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	
	public ApiResult<AsnMaster> updateAsnAcknowledge(AsnMaster asn) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(updateAcknoledgement);
			for (ScanDetails scanDetails : asn.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setLong(1, asn.getAsnId());
				pstmt.setLong(2, scanDetails.getTransId());
				pstmt.addBatch();					
			}
			pstmt.executeBatch();
			pstmt.close();
			pstmt=conn.prepareStatement(updateAcknoledgeStatus);
			pstmt.setLong(1, asn.getAsnId());
			pstmt.executeUpdate();
			pstmt.close();
			pstmt=conn.prepareStatement(updateAsnAckMasterStatus);
			pstmt.setLong(1, asn.getAsnId());
			pstmt.setLong(2, asn.getAsnId());
			pstmt.executeUpdate();
			pstmt.close();
			pstmt=conn.prepareStatement(updateCustomerTransactionSummary);
			pstmt.setLong(1, asn.getCustomerId());
			pstmt.executeUpdate();
//			asn.setAsnStatus(4);
//			asnLogUpdate(conn,asn);
			conn.commit();
			result.isSuccess = true;
			result.message = "Part Request Acknowledged successfully";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<AsnMaster> updateAsnDispatch(AsnMaster asn) {
		String WorkFlowType= getWorkFlowStatus();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			String vehicleNo = "";
			int customertripcount=1;
			int vehicletripcount=1;
			pstmt = conn.prepareStatement(getVehicleDetails);
			pstmt.setLong(1, asn.getAsnId());
		
			rs = pstmt.executeQuery();
			if(rs.next()) {
				vehicleNo=rs.getString("vechicleno");
				customertripcount=rs.getInt("customertripcount");
				vehicletripcount=rs.getInt("vehicletripcount");
			}
			pstmt.close();
			
			
			ArrayList<Long> asnIds=new ArrayList<Long>(); 
			pstmt=conn.prepareStatement(updateDispatch);
			for (ScanDetails scanDetails : asn.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setString(1, scanDetails.getQty());
				pstmt.setString(2, scanDetails.getQty());
				pstmt.setInt(3, scanDetails.getIncidentId());
				pstmt.setLong(4, asn.getAsnId());
				pstmt.setString(5, vehicleNo);
				pstmt.setInt(6, customertripcount);
				pstmt.setInt(7, vehicletripcount);
				pstmt.setLong(8, scanDetails.getAsnId());
				pstmt.setLong(9, scanDetails.getTransId());
				
				pstmt.addBatch();					
				if(asnIds.indexOf(scanDetails.getAsnId())==-1) {
					asnIds.add(scanDetails.getAsnId());
				}
			}
			pstmt.executeBatch();
			pstmt.close();
			for (long asnId: asnIds) {
				pstmt=conn.prepareStatement(updateDispatchStatus);
				pstmt.setLong(1, asnId);
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(updateAsnStatus);
				pstmt.setLong(1, asnId);
				pstmt.setLong(2, asnId);
				pstmt.executeUpdate();
				pstmt.close();
			}
			pstmt=conn.prepareStatement(updateDispatchSupplyStatus);
			pstmt.setLong(1, asn.getCustomerId());
			pstmt.executeUpdate();
			pstmt.close();
			pstmt=conn.prepareStatement(updateCustomerTransactionSummary);
			pstmt.setLong(1, asn.getCustomerId());
			pstmt.executeUpdate();
			
			if(WorkFlowType.equals("0")) {
				pstmt=conn.prepareStatement(updateDispatchConfirmNoFlow);
				pstmt.setLong(1, asn.getAsnId());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(updateDispatchSupplyConfirmStatus);
				pstmt.setLong(1, asn.getAsnId());
				pstmt.setLong(2, asn.getAsnId());
				pstmt.executeUpdate();
				pstmt.close();
			}
			pstmt.close();
			conn.commit();			
			pstmt=conn.prepareStatement(emailAddress);
			pstmt.setLong(1,asn.getCustomerId());
			rs=pstmt.executeQuery();
			String customerName="";
			EmailInput em=new EmailInput();
			if(rs.next()) {
				em.setEmail(rs.getString("email"));
				customerName=rs.getString("customername");
			}
			rs.close();
			pstmt.close();
			if(!"".equals(em.getEmail())) {
				StringBuilder emailContent=new StringBuilder(100);
				emailContent.append("Dear  ");
				emailContent.append(customerName);
				emailContent.append("<br><br>Please find below the parts have card failure:<br>");
				emailContent.append("<table border=1><tr>"
						+ "<th>Part Request No</th>"
						+ "<th>Date</th>"
						+ "<th>Vehicle No</th>"
						+ "<th>Part No</th>"
						+ "<th>Part Name</th>"
						+ "<th>Location</th>"
						+ "<th>Requested</th>"
						+ "<th>Accepted</th>"
						+ "<th>Dispatched</th>"
						+ "<th>Received</th>"
						+ "<th>Short/Excess</th>"
						+ "<th>Status</th>"
						+ "</tr>");
				pstmt=conn.prepareStatement(asnTatPartsEmailContent);
				pstmt.setLong(1,asn.getCustomerId());
				rs=pstmt.executeQuery();
				boolean y=false;
				while(rs.next()) {
					y=true;
					emailContent.append("<tr>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("asnno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("transdate"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("vechicleno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("partno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("partdescription"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("userlocation"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("binqty"));
					emailContent.append("</td>");
				
					emailContent.append("<td>");
					emailContent.append(rs.getString("ackqty"));
					emailContent.append("</td>");

					emailContent.append("<td>");
					emailContent.append(rs.getString("dispqty"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("received_bin_qty"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("shortexcess"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("acknowledgestatus"));
					emailContent.append("</td>");
					
					emailContent.append("</tr>");
				}
				emailContent.append("</table><br><br>Thanks, IYM-Team");
				em.setMailContent(emailContent.toString());
				em.setConfigId(5);
				if(y)
					MailSendUtil.sendMailNotification(conn,em );
			}
			result.isSuccess = true;
			result.message = "Part request dispatch updated successfully";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<AsnMaster> updateAsnDispatchReady(ArrayList<Integer> asn) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(updateDispatchReady);
			for (int detailid : asn) {
				pstmt.clearParameters();
				pstmt.setLong(1,detailid );
				pstmt.addBatch();	
			}
			pstmt.executeBatch();
			pstmt.close();
			conn.commit();
//			pstmt=conn.prepareStatement(emailAddress);
//			pstmt.setLong(1,asn.getCustomerId());
//			rs=pstmt.executeQuery();
//			String customerName="";
//			EmailInput em=new EmailInput();
//			if(rs.next()) {
//				em.setEmail(rs.getString("email"));
//				customerName=rs.getString("customername");
//			}
//			rs.close();
//			pstmt.close();
//			if(!"".equals(em.getEmail())) {
//				StringBuilder emailContent=new StringBuilder(100);
//				emailContent.append("Dear  ");
//				emailContent.append(customerName);
//				emailContent.append("<br><br>Please find below the parts have exception:<br>");
//				emailContent.append("<table border=1><tr>"
//						+ "<th>Part Request No</th>"
//						+ "<th>Date</th>"
//						+ "<th>Part No</th>"
//						+ "<th>Part Name</th>"
//						+ "<th>Location</th>"
//						+ "<th>Requested</th>"
//						+ "<th>Accepted</th>"
//						+ "<th>Dispatched</th>"
//						+ "<th>Received</th>"
//						+ "<th>Short/Excess</th>"
//						+ "<th>Status</th>"
//						+ "</tr>");
//				pstmt=conn.prepareStatement(asnPartsEmailContent);
//				pstmt.setLong(1, pAsnId);
//				rs=pstmt.executeQuery();
//				boolean y=false;
//				while(rs.next()) {
//					y=true;
//					emailContent.append("<tr>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("asnno"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("transdate"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("partno"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("partdescription"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("userlocation"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("binqty"));
//					emailContent.append("</td>");
//				
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("ackqty"));
//					emailContent.append("</td>");
//
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("dispqty"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("received_bin_qty"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("shortexcess"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("acknowledgestatus"));
//					emailContent.append("</td>");
//					
//					emailContent.append("</tr>");
//				}
//				emailContent.append("</table><br><br>Thanks, IYM-Team");
//				em.setMailContent(emailContent.toString());
//				em.setConfigId(3);
//				if(y)
//					MailSendUtil.sendMailNotification(conn,em );
//			}
			result.isSuccess = true;
			result.message = "Part dispatch ready status updated successfully";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<AsnMaster> updateAsnDispatchConfirmation(AsnMaster asn) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			long pAsnId=asn.getAsnId();
			ArrayList<Long> asnIds=new ArrayList<Long>();
			pstmt=conn.prepareStatement(updateDispatchConfirm);
			for (ScanDetails scanDetails : asn.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setLong(1, scanDetails.getAsnId());
				pstmt.setLong(2, scanDetails.getTransId());
				pstmt.addBatch();	
				if(asnIds.indexOf(scanDetails.getAsnId())==-1) {
					asnIds.add(scanDetails.getAsnId());
				}
			}
			pstmt.executeBatch();
			pstmt.close();
			for (long asnId: asnIds) {
				pstmt=conn.prepareStatement(updateDispatchSupplyConfirmStatus);
				pstmt.setLong(1, asnId);
				pstmt.setLong(2, asnId);
				pstmt.executeUpdate();
				pstmt.close();
			}
			pstmt=conn.prepareStatement(updateCustomerTransactionSummary);
			pstmt.setLong(1, asn.getCustomerId());
			pstmt.executeUpdate();
			pstmt.close();
			conn.commit();
//			pstmt=conn.prepareStatement(emailAddress);
//			pstmt.setLong(1,asn.getCustomerId());
//			rs=pstmt.executeQuery();
//			String customerName="";
//			EmailInput em=new EmailInput();
//			if(rs.next()) {
//				em.setEmail(rs.getString("email"));
//				customerName=rs.getString("customername");
//			}
//			rs.close();
//			pstmt.close();
//			if(!"".equals(em.getEmail())) {
//				StringBuilder emailContent=new StringBuilder(100);
//				emailContent.append("Dear  ");
//				emailContent.append(customerName);
//				emailContent.append("<br><br>Please find below the parts have exception:<br>");
//				emailContent.append("<table border=1><tr>"
//						+ "<th>Part Request No</th>"
//						+ "<th>Date</th>"
//						+ "<th>Part No</th>"
//						+ "<th>Part Name</th>"
//						+ "<th>Location</th>"
//						+ "<th>Requested</th>"
//						+ "<th>Accepted</th>"
//						+ "<th>Dispatched</th>"
//						+ "<th>Received</th>"
//						+ "<th>Short/Excess</th>"
//						+ "<th>Status</th>"
//						+ "</tr>");
//				pstmt=conn.prepareStatement(asnPartsEmailContent);
//				pstmt.setLong(1, pAsnId);
//				rs=pstmt.executeQuery();
//				boolean y=false;
//				while(rs.next()) {
//					y=true;
//					emailContent.append("<tr>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("asnno"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("transdate"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("partno"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("partdescription"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("userlocation"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("binqty"));
//					emailContent.append("</td>");
//				
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("ackqty"));
//					emailContent.append("</td>");
//
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("dispqty"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("received_bin_qty"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("shortexcess"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("acknowledgestatus"));
//					emailContent.append("</td>");
//					
//					emailContent.append("</tr>");
//				}
//				emailContent.append("</table><br><br>Thanks, IYM-Team");
//				em.setMailContent(emailContent.toString());
//				em.setConfigId(3);
//				if(y)
//					MailSendUtil.sendMailNotification(conn,em );
//			}
			result.isSuccess = true;
			result.message = "Part request dispatch confirmation successfully";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}

	public ApiResult<AsnMaster> updateAsnReceipt(AsnMaster asn) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			long pAsnId=asn.getAsnId();
			ArrayList<Long> asnIds=new ArrayList<Long>();
			StringBuffer strAsnIds=new StringBuffer(100);
			pstmt=conn.prepareStatement(updateReceipt);
			String comma="";
			for (ScanDetails scanDetails : asn.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setInt(1, scanDetails.getReceivedQty());
				pstmt.setInt(2, scanDetails.getReceivedQty());
				pstmt.setLong(3, scanDetails.getAsnId());
				pstmt.setLong(4, scanDetails.getTransId());
				pstmt.addBatch();	
				if(asnIds.indexOf(scanDetails.getAsnId())==-1) {
					asnIds.add(scanDetails.getAsnId());
				}
				strAsnIds.append(comma);
				strAsnIds.append(scanDetails.getTransId());
				comma=",";
			}
			pstmt.executeBatch();
			pstmt.close();
			for (long asnId: asnIds) {
				pstmt=conn.prepareStatement(updateReceiptException);
				pstmt.setLong(1, asnId);
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(updateVehicleAsnReceipt);
				pstmt.setLong(1, 0);
				pstmt.setLong(2, asnId);
				pstmt.executeUpdate();
				pstmt.close();
				pstmt=conn.prepareStatement(updateAsnRecdStatus);
				pstmt.setLong(1, asnId);
				pstmt.setLong(2, asnId);
				pstmt.executeUpdate();
				pstmt.close();
//				asn.setAsnId(asnId);
//				asn.setAsnStatus(10);
//				asnLogUpdate(conn,asn);
			}
			pstmt=conn.prepareStatement(updateCustomerTransactionSummary);
			pstmt.setLong(1, asn.getCustomerId());
			pstmt.executeUpdate();
			pstmt.close();
			conn.commit();
			pstmt=conn.prepareStatement(emailAddress);
			pstmt.setLong(1,asn.getCustomerId());
			rs=pstmt.executeQuery();
			String customerName="";
			EmailInput em=new EmailInput();
			if(rs.next()) {
				em.setEmail(rs.getString("email"));
				customerName=rs.getString("customername");
			}
			rs.close();
			pstmt.close();
			if(!"".equals(em.getEmail())) {
				StringBuilder emailContent=new StringBuilder(100);
				emailContent.append("Dear  ");
				emailContent.append(customerName);
				emailContent.append("<br><br>Please find below the parts have exception with TAT:<br>");
				emailContent.append("<table border=1><tr>"
						+ "<th>Part Request No</th>"
						+ "<th>Date</th>"
						+ "<th>Vehicle No</th>"
						+ "<th>Part No</th>"
						+ "<th>Part Name</th>"
						+ "<th>Location</th>"
						+ "<th>Requested</th>"
						+ "<th>Accepted</th>"
						+ "<th>Dispatched</th>"
						+ "<th>Received</th>"
						+ "<th>Short/Excess</th>"
						+ "<th>Incident Name</th>"
						+ "<th>Status</th>"
						+ "</tr>");
				pstmt=conn.prepareStatement(asnPartsEmailContent.replace("<<asnids>>", strAsnIds.toString()));
//				pstmt.setLong(1, pAsnId);
				rs=pstmt.executeQuery();
				boolean y=false;
				while(rs.next()) {
					y=true;
					emailContent.append("<tr>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("asnno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("transdate"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("vechicleno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("partno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("partdescription"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("userlocation"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("binqty"));
					emailContent.append("</td>");
				
					emailContent.append("<td>");
					emailContent.append(rs.getString("ackqty"));
					emailContent.append("</td>");

					emailContent.append("<td>");
					emailContent.append(rs.getString("dispqty"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("received_bin_qty"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("shortexcess"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("incidentname"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append("TAT Exceed");
					emailContent.append("</td>");
					
					emailContent.append("</tr>");
				}
				emailContent.append("</table><br><br>Thanks, IYM-Team");
				em.setMailContent(emailContent.toString());
				em.setConfigId(3);
				if(y)
					MailSendUtil.sendMailNotification(conn,em );

				emailContent.setLength(0);
				emailContent.append("Dear  ");
				emailContent.append(customerName);
				emailContent.append("<br><br>Please find below the parts have exception with quantity short/excess:<br>");
				emailContent.append("<table border=1><tr>"
						+ "<th>Part Request No</th>"
						+ "<th>Date</th>"
						+ "<th>Vehicle No</th>"
						+ "<th>Part No</th>"
						+ "<th>Part Name</th>"
						+ "<th>Location</th>"
						+ "<th>Requested</th>"
						+ "<th>Accepted</th>"
						+ "<th>Dispatched</th>"
						+ "<th>Received</th>"
						+ "<th>Short/Excess</th>"
						+ "<th>Incident Name</th>"
						+ "<th>Status</th>"
						+ "</tr>");
				pstmt=conn.prepareStatement(asnShortExcessEmailContent.replace("<<asnids>>", strAsnIds.toString()));
//				pstmt.setLong(1, pAsnId);
				rs=pstmt.executeQuery();
				y=false;
				while(rs.next()) {
					y=true;
					emailContent.append("<tr>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("asnno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("transdate"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("vechicleno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("partno"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("partdescription"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("userlocation"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("binqty"));
					emailContent.append("</td>");
				
					emailContent.append("<td>");
					emailContent.append(rs.getString("ackqty"));
					emailContent.append("</td>");

					emailContent.append("<td>");
					emailContent.append(rs.getString("dispqty"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("received_bin_qty"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("shortexcess"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append(rs.getString("incidentname"));
					emailContent.append("</td>");
					
					emailContent.append("<td>");
					emailContent.append("Short/Excess");
					emailContent.append("</td>");
					
					emailContent.append("</tr>");
				}
				emailContent.append("</table><br><br>Thanks, IYM-Team");
				em.setMailContent(emailContent.toString());
				em.setConfigId(4);
				if(y)
					MailSendUtil.sendMailNotification(conn,em );
			}
			result.isSuccess = true;
			result.message = "Part request receipt quantity updated successfully";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	public ApiResult<AsnMaster> getPartRequestInfo(int AsnMasterId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		ArrayList<ScanDetails> asnDetails = new ArrayList<ScanDetails>();
		AsnMaster l = new AsnMaster();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(select);
			pstmt.setInt(1, AsnMasterId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				l.setAsnId(rs.getInt("asnid"));
				l.setAsnNo(rs.getString("asnno"));
				l.setCustomerId(rs.getInt("customerid"));
				l.setSupplyDate(rs.getString("supplydate"));
				l.setSupplyTime(rs.getString("supplytime"));
				l.setUnLoadingDocId(rs.getInt("unloadingdocid"));
				l.setAsnStatus(rs.getInt("asnstatus"));
				l.setVechicleNo(rs.getString("vechicleno"));
				l.setDriverName(rs.getString("drivername"));
				l.setDriverMobile(rs.getString("drivermobile"));
				l.setEwayBillNo(rs.getString("ewaybillno"));
				l.setInvoiceNo(rs.getString("invoiceno"));
				l.setDeliveryNoteNo(rs.getString("deliverynoteno"));
				l.setDirectorTransit(rs.getBoolean("directortransit"));
				l.setTransitasnId(rs.getInt("transitasnid"));
				l.setGateIndatetime(rs.getString("gateindatetime"));
				l.setCardsIssued(rs.getInt("cardsissued"));
				l.setCardsDispatched(rs.getInt("carddispatched"));
				l.setCardsReceived(rs.getInt("cardsreceived"));
				l.setCardsAcknowledged(rs.getInt("cardsacknowledged"));
				l.setReturnPack(rs.getString("returnpack"));
				l.setReturnPackQty(rs.getInt("returnpackqty"));
				l.setDispatchConfirmed(rs.getInt("dispatchconfirmed"));
				l.setCardsConfirmed(rs.getInt("cardsconfirmed"));
			}
			rs.close();
			
			pstmt = conn.prepareStatement(asnReadonly);
			pstmt.setLong(1, AsnMasterId);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ScanDetails s = new ScanDetails();
				s.setTransId(rs.getInt("asndetailid"));
				s.setScanData(rs.getString("partno"));
				s.setVendorId(rs.getInt("customerid"));
				s.setVendorName(rs.getString("customername"));
				s.setPartId(rs.getInt("partid"));
				s.setPartName(rs.getString("partdescription"));
				s.setQty(rs.getString("binqty"));
				s.setUserLocation(rs.getString("userlocation"));
				s.setPartNo(rs.getString("partno"));
				s.setSerialNo(rs.getString("serialno"));
				s.setAckStatus(rs.getString("acknoledgeStatus"));
				s.setVendorCode(rs.getString("customer_erp_code"));
				s.setCustomerPartId(rs.getLong("customerpartid"));
				s.setIncidentId(rs.getInt("incidentid"));
				s.setIncidentName(rs.getString("incidentname"));
				s.setReceivedQty(rs.getInt("received_bin_qty"));
				asnDetails.add(s);
			}
			l.setAsnDetail(asnDetails);
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
	public ApiResult<AsnMaster> getInwardPalletInfo(int asnId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		ArrayList<ScanDetails> asnDetails = new ArrayList<ScanDetails>();
		AsnMaster l = new AsnMaster();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(checkInwardPalletScan);
			pstmt.setInt(1, asnId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result.isSuccess = true;
				result.message = "Pallet Count Mismatch";
			} else {
				result.isSuccess = false;
				result.message = "Can submit";
			}
			rs.close();
			
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
	public void asnLogUpdate(Connection con,AsnMaster m) {
		PreparedStatement pstmt = null;
		try {
			pstmt=con.prepareStatement(asnLogEntry);
			pstmt.setLong(1, m.getAsnId());
			pstmt.setLong(2, m.getAsnStatus());
			pstmt.setLong(3, m.getUserId());
			pstmt.executeUpdate();
			pstmt.close();			
		}catch(Exception e) {
			//do nothing
		}
	}
	public ApiResult<AsnMaster> updateGateEntry(AsnMaster asn) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(gateEntryUpdate);
			pstmt.setLong(1, asn.getAsnStatus());
			pstmt.setLong(2, asn.getAsnId());
			pstmt.executeUpdate();
			pstmt.close();
//			asnLogUpdate(conn,asn);
			conn.commit();
			result.isSuccess = true;
			result.message = "Gate entry status updated successfully";
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
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException esql) {
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	/*12-25	27-30	33-36	38-43	65-68*/
	public static String tbisString(String partCode,String vendorCode,int qty,String unloadDock,int slNo,int asnDetailId) {
		StringBuilder value=new StringBuilder(100);
		value.append(StringUtils.leftPad(partCode, 14,' '));
		value.append(StringUtils.leftPad(vendorCode, 4,' '));
		value.append(StringUtils.leftPad(String.valueOf(qty), 8,'0'));
		value.append(StringUtils.leftPad(unloadDock, 4,' '));
		value.append(StringUtils.leftPad(String.valueOf(asnDetailId), 10,'0'));
		value.append(StringUtils.leftPad(String.valueOf(slNo), 6,'0'));
	    return value.toString();	    
	}

	public static String customerString(String partCode,String vendorCode,int qty,String unloadDock,String slNo) {
		String prefix="[)> 06 9KY0G";
		StringBuilder value=new StringBuilder(100);
		value.append(StringUtils.leftPad(prefix, 10,' '));
		value.append("P");
		value.append(StringUtils.leftPad(partCode, 14,' '));
		value.append("V");
		value.append(StringUtils.leftPad(vendorCode, 4,' '));
		value.append("L");
		value.append(StringUtils.leftPad(unloadDock, 4,' '));
		value.append("Q");
		value.append(StringUtils.leftPad(String.valueOf(qty), 8,'0'));
		value.append(" ");
		value.append(StringUtils.leftPad(String.valueOf(slNo), 4,' '));
	    return value.toString();	    
	}
	public static String tbisOutwardString(String partCode,String vendorCode,int qty,String unloadDock,int slNo,String code) {
		StringBuilder value=new StringBuilder(100);
		value.append(StringUtils.leftPad(partCode, 14,' '));
		value.append(StringUtils.leftPad(vendorCode, 4,' '));
		value.append(StringUtils.leftPad(String.valueOf(qty), 8,'0'));
		value.append(StringUtils.leftPad(unloadDock, 4,' '));
		value.append(StringUtils.leftPad(String.valueOf(code), 6,'0'));
		value.append(StringUtils.leftPad(String.valueOf(slNo), 6,'0'));
	    return value.toString();	    
	}
	
	public ApiResult<ScanHeader> getAsnScanCardDetail(ScanDetails s) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ScanHeader> result = new ApiResult<ScanHeader>();
		ArrayList<ScanDetails> details = new ArrayList<ScanDetails>();
		ScanHeader h=new ScanHeader();
		try {
			conn = DatabaseUtil.getConnection();
			ScanDetails sph=null;
			if(s.getScanData().startsWith("PAL")) {
				h.setIsPallet(true);								
				pstmt = conn.prepareStatement(asnPartDetailPalletScan);
				pstmt.setLong(1, s.getAsnId());
				pstmt.setString(2, s.getScanData());
				ASNPartMovement p=new ASNPartMovement();
				p.setAsnId(s.getAsnId());
				p.setLineUsageId(7);
				p.setPalletScanCode(s.getScanData());
				sph=getSinglePalletStorageSpaceAllocation(p);
			}else {
				pstmt = conn.prepareStatement(asnPartDetailScan);
				pstmt.setLong(1, s.getAsnId());
				pstmt.setString(2, s.getScanData());
				pstmt.setString(3, s.getScanData());
			}
			rs = pstmt.executeQuery();
			int palletNo=0;
			while(rs.next()) {
				ScanDetails l = new ScanDetails();
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("binqty"));
				l.setPartNo(rs.getString("partno"));
				if(h.getIsPallet()) {
					l.setRequestedBinQty(rs.getString("requestedqty"));
				}else {
					l.setRequestedBinQty("1");
				}
				l.setSubLocationShortCode(rs.getString("sublocationshortcode"));
				l.setLoadingType(rs.getInt("loadingtype"));
				l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
				l.setCustomerPartCode(rs.getString("customerpartcode"));
				l.setSpq(rs.getString("spq"));
				l.setBinQty(rs.getString("binqty"));
				l.setCustomerScanCode(rs.getString("customerscancode"));
				l.setScanData(rs.getString("tbisscancode"));
				l.setAsnBinQty(rs.getString("requestedqty"));
				l.setAsnQty(rs.getString("qty"));
				l.setPalletNumber(rs.getInt("palletno"));
				l.setPalletScanCode(rs.getString("palletscancode"));
				palletNo=rs.getInt("palletno");
				details.add(l);
			}
			pstmt.close();
			rs.close();
			if(details.size()>0) {
				if(palletNo!=0) {
					pstmt=conn.prepareStatement(getPalletCount);
					pstmt.setLong(1, s.getAsnId());
					pstmt.setInt(2, palletNo);
					rs=pstmt.executeQuery();
					if(rs.next()) {
						h.setPartCount(rs.getInt("parts"));
					}
				}else {
					h.setPartCount(1);
				}
				h.setScannedPartDetails(details);
				if(sph!=null) {
					h.setLineSpaceConfig(sph.getLineSpaceConfig());
					h.setMoveToOverflowLocation(sph.getMoveToOverflowLocation());
				}
				result.isSuccess=true;
				result.result = h;
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
	public boolean manageStockDetail(Connection conn,AsnMaster n,ScanDetails d,String processType) {
		boolean result=false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt=conn.prepareStatement(updateStockDetail);
			pstmt.setString(1, d.getQty());
			pstmt.setString(2, d.getQty());
			pstmt.setLong(3, d.getPartId());
			pstmt.setLong(4, n.getCustomerId());
			pstmt.setLong(5, n.getSubloactionId());
			int s=pstmt.executeUpdate();
			pstmt.close();
			if(s==0) {
				pstmt=conn.prepareStatement(insertStockDetail);
				pstmt.setLong(1, d.getPartId());
				pstmt.setLong(2, n.getCustomerId());
				pstmt.setLong(3, n.getSubloactionId());
				pstmt.setString(4, d.getQty());
				pstmt.setString(5, d.getQty());
				pstmt.executeUpdate();
				pstmt.close();				
			}
			pstmt=conn.prepareStatement(insertAsnStockDetail);
			pstmt.setLong(1, d.getTransId());
			pstmt.setLong(2, d.getPartId());
			pstmt.setString(3, processType);
			pstmt.setString(4, d.getQty());
			pstmt.executeUpdate();
			pstmt.close();		
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
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
	public ApiResult<AsnMaster> updateGinReceipt(AsnMaster asn) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement gstmt = null;
		PreparedStatement bstmt = null;
		PreparedStatement istmt = null;
		PreparedStatement lineusedspace=null;
		PreparedStatement selectorder=null;
		PreparedStatement updatefifoorder=null;
		ResultSet rs = null;
		ApiResult<AsnMaster> result = new ApiResult<AsnMaster>();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			long pAsnId=asn.getAsnId();
			ArrayList<Long> asnIds=new ArrayList<Long>();
			pstmt=conn.prepareStatement(updateGinReceiptStock);
			gstmt=conn.prepareStatement(updateGinReceiptPalletStock);
			bstmt=conn.prepareStatement(updateScanProcess);
			lineusedspace=conn.prepareStatement(updatePalletLineSpaceUsedSpace);
			updatefifoorder=conn.prepareStatement(updatePalletLineSpaceLastFilled);
			selectorder=conn.prepareStatement(updatePalletLineSpaceConfig);
			ScanDetails sd=null;
			for (ScanDetails scanDetails : asn.getAsnDetail()) {
				sd=scanDetails;
				pstmt.clearParameters();
				pstmt.setString(1, scanDetails.getQty());
				pstmt.setString(2, scanDetails.getQty());
				pstmt.setString(3, scanDetails.getRequestedBinQty());
				pstmt.setLong(4, asn.getAsnId());
				pstmt.setLong(5, scanDetails.getTransId());
				pstmt.executeUpdate();
				gstmt.clearParameters();
				gstmt.setString(1, scanDetails.getRequestedBinQty());
				gstmt.setLong(2, asn.getAsnId());
				gstmt.setLong(3, scanDetails.getTransId());
				gstmt.setLong(4, scanDetails.getPalletNumber());
				gstmt.executeUpdate();
				bstmt.clearParameters();
				bstmt.setLong(1, 2);
				bstmt.setLong(2, 2);
				bstmt.setLong(3, scanDetails.getTransId());
				bstmt.setLong(4, scanDetails.getPalletNumber());
				bstmt.executeUpdate();
				boolean s=manageStockDetail(conn, asn, scanDetails, "1");
				if(!s) {
					throw new Exception("Stock update failed.");
				}
				scanDetails.setAsnId(asn.getAsnId());
				String r=updatePalletGinStockMovemnetDetail(conn,scanDetails);
				if(!r.equals("Success")) {
					throw new Exception("Stock movement failed.");
				}
			}
			pstmt.close();
			if(asn.getAsnDetail()!=null && asn.getAsnDetail().size()>0 && sd!=null && !sd.getMoveToOverflowLocation()) {
				lineusedspace.clearParameters();
				lineusedspace.setInt(1, 1);
				lineusedspace.setInt(2, sd.getLineSpaceConfig().getLineSpacePartConfigId());
				lineusedspace.executeUpdate();

				updatefifoorder.clearParameters();
				updatefifoorder.setInt(1, sd.getPartId());
				updatefifoorder.setInt(2, sd.getLineSpaceConfig().getLineSpacePartConfigId());
				updatefifoorder.executeUpdate();

				selectorder.clearParameters();
				selectorder.setInt(1, sd.getLineSpaceConfig().getLineSpacePartConfigId());
				selectorder.setLong(2, sd.getAsnId());
				selectorder.setInt(3,sd.getPalletNumber());
				selectorder.executeUpdate();
			}
			if(asn.getAsnDetail()!=null && asn.getAsnDetail().size()>0 && sd!=null && sd.getMoveToOverflowLocation()) {
				selectorder.clearParameters();
				selectorder.setInt(1, 0);
				selectorder.setLong(2, sd.getAsnId());
				selectorder.setInt(3,sd.getPalletNumber());
				selectorder.executeUpdate();
			}
			if(asn.getAsnStatus()==10) {
				pstmt=conn.prepareStatement(updateAsnReceived);
				pstmt.clearParameters();
				pstmt.setInt(1,asn.getUserId());
				pstmt.setLong(2, asn.getAsnId());
				pstmt.executeUpdate();
				pstmt.close();
				
				pstmt=conn.prepareStatement(updateAsnStockMovementStatus);
				pstmt.setLong(1, 7);
				pstmt.setLong(2, asn.getAsnId());
				pstmt.executeUpdate();
				pstmt.close();
				
				pstmt=conn.prepareStatement(updateAsnReceivedStatus);
				pstmt.setLong(1, asn.getAsnId());
				pstmt.executeUpdate();
				pstmt.close();
				
				pstmt=conn.prepareStatement(checkAsnReceivedStatus);
				pstmt.setLong(1, asn.getAsnId());
				rs=pstmt.executeQuery();
				if(rs.next()) {
					istmt=conn.prepareStatement(insertAsnPalletIncidentQuery);
					istmt.clearParameters();
					istmt.setLong(1, asn.getAsnId());
					istmt.setString(2, asn.getScwComments());
					istmt.setInt(3,asn.getUserId());
					istmt.executeUpdate();
					istmt.close();
				}
				pstmt.close();
				rs.close();
			}
			conn.commit();
//			pstmt=conn.prepareStatement(emailAddress);
//			pstmt.setLong(1,asn.getCustomerId());
//			rs=pstmt.executeQuery();
//			String customerName="";
//			EmailInput em=new EmailInput();
//			if(rs.next()) {
//				em.setEmail(rs.getString("email"));
//				customerName=rs.getString("customername");
//			}
//			rs.close();
//			pstmt.close();
//			if(!"".equals(em.getEmail())) {
//				StringBuilder emailContent=new StringBuilder(100);
//				emailContent.append("Dear  ");
//				emailContent.append(customerName);
//				emailContent.append("<br><br>Please find below the parts have exception:<br>");
//				emailContent.append("<table border=1><tr>"
//						+ "<th>Part Request No</th>"
//						+ "<th>Date</th>"
//						+ "<th>Part No</th>"
//						+ "<th>Part Name</th>"
//						+ "<th>Location</th>"
//						+ "<th>Requested</th>"
//						+ "<th>Accepted</th>"
//						+ "<th>Dispatched</th>"
//						+ "<th>Received</th>"
//						+ "<th>Short/Excess</th>"
//						+ "<th>Status</th>"
//						+ "</tr>");
//				pstmt=conn.prepareStatement(asnPartsEmailContent);
//				pstmt.setLong(1, pAsnId);
//				rs=pstmt.executeQuery();
//				boolean y=false;
//				while(rs.next()) {
//					y=true;
//					emailContent.append("<tr>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("asnno"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("transdate"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("partno"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("partdescription"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("userlocation"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("binqty"));
//					emailContent.append("</td>");
//				
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("ackqty"));
//					emailContent.append("</td>");
//
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("dispqty"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("received_bin_qty"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("shortexcess"));
//					emailContent.append("</td>");
//					
//					emailContent.append("<td>");
//					emailContent.append(rs.getString("acknowledgestatus"));
//					emailContent.append("</td>");
//					
//					emailContent.append("</tr>");
//				}
//				emailContent.append("</table><br><br>Thanks, IYM-Team");
//				em.setMailContent(emailContent.toString());
//				em.setConfigId(3);
//				if(y)
//					MailSendUtil.sendMailNotification(conn,em );
//			}
			result.isSuccess = true;
			result.message = "Part request receipt quantity updated successfully";
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
				if(gstmt!=null) {
					gstmt.close();
				}
				if(bstmt!=null) {
					bstmt.close();
				}
				if(istmt!=null) {
					istmt.close();
				}
				if (lineusedspace != null) {
					lineusedspace.close();
				}
				if (selectorder!= null) {
					selectorder.close();
				}
				if (updatefifoorder!= null) {
					updatefifoorder.close();
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
	public ApiResult<ScanHeader> getAsnScanCardStockDetail(ScanDetails s) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ScanHeader> result = new ApiResult<ScanHeader>();
		ArrayList<ScanDetails> details = new ArrayList<ScanDetails>();
		ScanHeader h=new ScanHeader();
		long finalPartId=0;
		long asnId=0;
		long asnDetailId=0;
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(asnPartDetailStockMovementScan);
			pstmt.setString(1, s.getScanData());
			pstmt.setString(2, s.getScanData());
			pstmt.setInt(3, s.getProcessId());
			pstmt.setInt(4, s.getVendorId());
//			pstmt.setInt(3, s.getPartId());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ScanDetails l = new ScanDetails();
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("binqty"));
				l.setPartNo(rs.getString("partno"));
				l.setRequestedBinQty("1");
				l.setSubLocationShortCode(rs.getString("sublocationshortcode"));
				l.setLoadingType(rs.getInt("loadingtype"));
				l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
				l.setCustomerPartCode(rs.getString("customerpartcode"));
				l.setSpq(rs.getString("spq"));
				l.setBinQty(rs.getString("binqty"));
				l.setCustomerScanCode(rs.getString("customerscancode"));
				l.setScanData(rs.getString("tbisscancode"));
				l.setAsnBinQty(rs.getString("requestedqty"));
				l.setAsnQty(rs.getString("qty"));
				l.setPalletNumber(rs.getInt("palletno"));
				l.setPalletScanCode(rs.getString("palletscancode"));
				l.setSubLocationId(rs.getInt("sublocationid"));
				l.setRepackQty(rs.getInt("repackqty"));
				l.setOutwardBinQty(rs.getInt("obinqty"));
				l.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
				l.setOpeningHoldQty(rs.getInt("holdstock"));
				l.setFinalPartId(rs.getLong("finalpartid"));
				l.setFinalPartQty(rs.getInt("finalpartqty"));
				l.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
				finalPartId=rs.getLong("finalpartid");
				asnId=rs.getLong("asnid");
				asnDetailId=rs.getLong("asndetailid");
				
				details.add(l);
			}
			pstmt.close();
			rs.close();
			if(details.size()>0) {
				h.setPartCount(1);
				h.setScannedPartDetails(details);
				h.setAssemblyPartDetails(new ArrayList<ScanDetails>());
				if(s.getOpeningHoldQty()>0) {
					pstmt = conn.prepareStatement(finalPart);
					pstmt.setLong(1, finalPartId);
					rs = pstmt.executeQuery();
					if(rs.next()) {
						ScanDetails l = new ScanDetails();
						l.setAsnId(asnId);
						l.setTransId(asnDetailId);
						l.setPartId(rs.getInt("partid"));
						l.setPartName(rs.getString("partdescription"));
						l.setQty(rs.getString("binqty"));
						l.setPartNo(rs.getString("partno"));
						l.setRequestedBinQty("1");
						l.setLoadingType(rs.getInt("loadingtype"));
						l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
						l.setCustomerPartCode(rs.getString("customerpartcode"));
						l.setRepackQty(rs.getInt("repackqty"));
						l.setOutwardBinQty(rs.getInt("obinqty"));
						l.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
						l.setSubLocationId(rs.getInt("primarysublocationid"));
						h.setFinalPart(l);
					}
					pstmt.close();
					rs.close();
					pstmt = conn.prepareStatement(assemblyPartDetails);
					pstmt.setLong(1, finalPartId);
					rs = pstmt.executeQuery();
					if(rs.next()) {
						ScanDetails l = new ScanDetails();
						l.setPartId(rs.getInt("partid"));
						l.setPartName(rs.getString("partdescription"));
						l.setQty(rs.getString("binqty"));
						l.setPartNo(rs.getString("partno"));
						l.setRequestedBinQty("1");
						l.setLoadingType(rs.getInt("loadingtype"));
						l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
						l.setCustomerPartCode(rs.getString("customerpartcode"));
						l.setRepackQty(rs.getInt("repackqty"));
						l.setOutwardBinQty(rs.getInt("obinqty"));
						l.setExclusiveClubNo(rs.getInt("exclusiveclubno"));
						l.setSubLocationId(rs.getInt("primarysublocationid"));
						h.getAssemblyPartDetails().add(l);
					}
					pstmt.close();
					rs.close();
				}
				result.isSuccess=true;
				result.result = h;
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
	

	public ApiResult<ScanHeader> getMobileAsnScanCardStockDetail(ScanDetails s) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ScanHeader> result = new ApiResult<ScanHeader>();
		ArrayList<ScanDetails> details = new ArrayList<ScanDetails>();
		ScanHeader h=new ScanHeader();
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(mobileAsnPartDetailStockMovementScan);
			pstmt.setString(1, s.getScanData());
			pstmt.setString(2, s.getScanData());
			pstmt.setInt(3, s.getProcessId());
			pstmt.setInt(4, s.getWarehouseId());
			pstmt.setInt(5, s.getSubLocationId());
			pstmt.setInt(6, s.getUdcId());
			pstmt.setInt(7, s.getVendorId());
//			pstmt.setInt(3, s.getPartId());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ScanDetails l = new ScanDetails();
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("binqty"));
				l.setPartNo(rs.getString("partno"));
				l.setRequestedBinQty("1");
				l.setSubLocationShortCode(rs.getString("sublocationshortcode"));
				l.setLoadingType(rs.getInt("loadingtype"));
				l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
				l.setCustomerPartCode(rs.getString("customerpartcode"));
				l.setSpq(rs.getString("spq"));
				l.setBinQty(rs.getString("binqty"));
				l.setCustomerScanCode(rs.getString("customerscancode"));
				l.setScanData(rs.getString("tbisscancode"));
				l.setAsnBinQty(rs.getString("requestedqty"));
				l.setAsnQty(rs.getString("qty"));
				l.setPalletNumber(rs.getInt("palletno"));
				l.setPalletScanCode(rs.getString("palletscancode"));
				l.setSubLocationId(rs.getInt("sublocationid"));
				l.setRepackQty(rs.getInt("repackqty"));
				l.setOutwardBinQty(rs.getInt("obinqty"));
				l.setLineSpacePartConfigId(rs.getInt("linespacepartconfigid"));
				details.add(l);
			}
			pstmt.close();
			rs.close();
			if(details.size()>0) {
				h.setPartCount(1);
				h.setScannedPartDetails(details);
				result.isSuccess=true;
				result.result = h;
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
	
	public ApiResult<String> manageStockMovemnetDetail(AsnMaster n) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement gstmt = null;
		PreparedStatement bstmt = null;
		PreparedStatement gstock = null;
		PreparedStatement lineusedspace= null;
		PreparedStatement insertStock=null;
		PreparedStatement insertExcludedBins=null;
		ResultSet rs = null;
		ApiResult<String> result=new ApiResult<String>();
		boolean finalPartAvailable=false;
		boolean exclusivePart=false;
		String code="0";
		try {
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			ScanDetails finalPart=n.getFinalPartDetail();
			if(finalPart!=null) {
				finalPartAvailable=true;
				if(finalPart.getExclusiveClubNo()!=0) {
					exclusivePart=true;
				}
			}
			pstmt=conn.prepareStatement(insertStockMovement,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, n.getCustomerId());
			pstmt.setLong(2, n.getFromProcessId());
			pstmt.setLong(3, n.getToProcessId());
			pstmt.setLong(4, n.getEmployeeId());
			pstmt.setString(5, n.getSupplyDate());
			pstmt.executeUpdate();
			rs=pstmt.getGeneratedKeys();
			if(rs.next()){
				code=rs.getString(1);
			}
			rs.close();
			pstmt.close();
			StringBuilder strQuery=new StringBuilder(100);
			StringBuilder strAsnDetailQuery=new StringBuilder(100);
			strQuery.append("update part_stock_detail set ");
			if(n.getFromProcessId()==1){
				strQuery.append("receivedstock=receivedstock-?");
			}
		    if(n.getFromProcessId()==7)
		    	strQuery.append("preinspection=preinspection-?");
		    if(n.getFromProcessId()==4)
		    	strQuery.append("qualitycheck=qualitycheck-?");
		    if(n.getFromProcessId()==6)
		    	strQuery.append("overflowlocation=overflowlocation-?");
		    if(n.getFromProcessId()==8)
		    	strQuery.append("goodstock=goodstock-?");
		    if(n.getFromProcessId()==9)
		    	strQuery.append("badstock=badstock-?"); 
		    if(n.getFromProcessId()==10)
		    	strQuery.append("holdstock=holdstock-?"); 
		    strQuery.append(" where partid=? and customerid=? and sublocationid=? ");
		    
		    strAsnDetailQuery.append("update asndetail set ");
			if(n.getFromProcessId()==1){
				strAsnDetailQuery.append("receivedstock=receivedstock-?");
			}
		    if(n.getFromProcessId()==7)
		    	strAsnDetailQuery.append("preinspection=preinspection-?");
		    if(n.getFromProcessId()==4)
		    	strAsnDetailQuery.append("qualitycheck=qualitycheck-?");
		    if(n.getFromProcessId()==6)
		    	strAsnDetailQuery.append("overflowlocation=overflowlocation-?");
		    if(n.getFromProcessId()==8)
		    	strAsnDetailQuery.append("goodstock=goodstock-?");
		    if(n.getFromProcessId()==9)
		    	strAsnDetailQuery.append("badstock=badstock-?"); 
		    if(n.getFromProcessId()==10)
		    	strAsnDetailQuery.append("holdstock=holdstock-?"); 
		    strAsnDetailQuery.append(" where partid=? and asndetailid=? ");
		    pstmt=conn.prepareStatement(strQuery.toString());
		    gstmt=conn.prepareStatement(strAsnDetailQuery.toString());
		    bstmt=conn.prepareStatement(insertAsnStockDetail);
		    for (ScanDetails scanDetails : n.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setInt(1, scanDetails.getMovedQty());
				pstmt.setLong(2, scanDetails.getPartId());
				pstmt.setLong(3, n.getCustomerId());
				pstmt.setLong(4, scanDetails.getSubLocationId());
				pstmt.executeUpdate();
				gstmt.clearParameters();
				gstmt.setInt(1, scanDetails.getMovedQty());
				gstmt.setLong(2, scanDetails.getPartId());
				gstmt.setLong(3, scanDetails.getTransId());
				gstmt.executeUpdate();
				bstmt.clearParameters();
				bstmt.setLong(1, scanDetails.getTransId());
				bstmt.setLong(2, scanDetails.getPartId());
				bstmt.setInt(3, n.getFromProcessId());
				bstmt.setString(4, "-"+scanDetails.getMovedQty());
				bstmt.executeUpdate();
		    }		    
		    pstmt.close();
		    gstmt.close();
		    bstmt.close();
		    strQuery.setLength(0);
		    strAsnDetailQuery.setLength(0);
			strQuery.append("update part_stock_detail set ");
			if(n.getToProcessId()==1){
				strQuery.append("receivedstock=receivedstock+?");
			}
		    if(n.getToProcessId()==7)
		    	strQuery.append("preinspection=preinspection+?");
		    if(n.getToProcessId()==4)
		    	strQuery.append("qualitycheck=qualitycheck+?");
		    if(n.getToProcessId()==6)
		    	strQuery.append("overflowlocation=overflowlocation+?");
		    if(n.getToProcessId()==8)
		    	strQuery.append("goodstock=goodstock+?,badstock=badstock+?,holdstock=?");
		    if(n.getToProcessId()==9)
		    	strQuery.append("badstock=badstock+?"); 
		    if(n.getToProcessId()==10)
		    	strQuery.append("holdstock=holdstock+?"); 
		    strQuery.append(" where partid=? and customerid=? and sublocationid=? ");
		    
		    strAsnDetailQuery.append("update asndetail set ");
			if(n.getToProcessId()==1){
				strAsnDetailQuery.append("receivedstock=receivedstock+?");
			}
		    if(n.getToProcessId()==7)
		    	strAsnDetailQuery.append("preinspection=preinspection+?");
		    if(n.getToProcessId()==4)
		    	strAsnDetailQuery.append("qualitycheck=qualitycheck+?");
		    if(n.getToProcessId()==6)
		    	strAsnDetailQuery.append("overflowlocation=overflowlocation+?");
		    if(n.getToProcessId()==8)
		    	strAsnDetailQuery.append("goodstock=goodstock+?,badstock=badstock+?,holdstock=?");
		    if(n.getToProcessId()==9)
		    	strAsnDetailQuery.append("badstock=badstock+?"); 
		    if(n.getToProcessId()==9)
		    	strAsnDetailQuery.append("holdstock=holdstock+?"); 
		    strAsnDetailQuery.append(" where partid=? and asndetailid=? ");
		    pstmt=conn.prepareStatement(strQuery.toString());
		    gstmt=conn.prepareStatement(strAsnDetailQuery.toString());
		    bstmt=conn.prepareStatement(insertAsnStockDetail);
		    lineusedspace=conn.prepareStatement(updateLineSpaceUsedSpace);
		    gstock=conn.prepareStatement(insertGoodStockDetails);
		    insertExcludedBins=conn.prepareStatement(insertExcludedStockmovementBins);
		    if(!finalPartAvailable) {
			    for (ScanDetails scanDetails : n.getAsnDetail()) {
			    	if(n.getFromProcessId()==7 && n.getToProcessId()==4) {
						lineusedspace.clearParameters();
						lineusedspace.setInt(1, -1);
						lineusedspace.setInt(2, scanDetails.getLineSpacePartConfigId());
						lineusedspace.executeUpdate();		    		
			    	}
			    	if(n.getToProcessId()==8) {
						pstmt.clearParameters();
						pstmt.setInt(1, scanDetails.getGoodQty());
						pstmt.setInt(2, scanDetails.getBadQty());
						pstmt.setInt(3, scanDetails.getHoldQty());
						pstmt.setLong(4, scanDetails.getPartId());
						pstmt.setLong(5, n.getCustomerId());
						pstmt.setLong(6, scanDetails.getSubLocationId());
						pstmt.executeUpdate();
						gstmt.clearParameters();
						gstmt.setInt(1, scanDetails.getGoodQty());
						gstmt.setInt(2, scanDetails.getBadQty());
						gstmt.setInt(3, scanDetails.getHoldQty());
						gstmt.setLong(4, scanDetails.getPartId());
						gstmt.setLong(5, scanDetails.getTransId());
						gstmt.executeUpdate();
						bstmt.clearParameters();
						bstmt.setLong(1, scanDetails.getTransId());
						bstmt.setLong(2, scanDetails.getPartId());
						bstmt.setInt(3, n.getToProcessId());
						bstmt.setInt(4, scanDetails.getGoodQty());
						bstmt.executeUpdate();
						bstmt.clearParameters();
						bstmt.setLong(1, scanDetails.getTransId());
						bstmt.setLong(2, scanDetails.getPartId());
						bstmt.setInt(3, 9);
						bstmt.setInt(4, scanDetails.getBadQty());
						bstmt.executeUpdate();
						bstmt.clearParameters();
						bstmt.setLong(1, scanDetails.getTransId());
						bstmt.setLong(2, scanDetails.getPartId());
						bstmt.setInt(3, 10);
						bstmt.setInt(4, scanDetails.getHoldQty());
						bstmt.executeUpdate();
	
						if(scanDetails.getExcludedBarCodes()!=null && scanDetails.getExcludedBarCodes().size()>0) {
							for(CustomerBarCode cb:scanDetails.getExcludedBarCodes()) {
								insertExcludedBins.clearParameters();
								insertExcludedBins.setString(1, code);
								insertExcludedBins.setInt(2, cb.getBinNo());
								insertExcludedBins.setString(3, cb.getBarCode());
								insertExcludedBins.setInt(4, n.getUserId());
								insertExcludedBins.addBatch();
							}
							insertExcludedBins.executeBatch();
			    		}
			    		ASNPartMovement p=new ASNPartMovement();
			    		p.setPartId(scanDetails.getPartId());
			    		p.setLineUsageId(n.getToProcessId());
			    		p.setAsnBinQty(scanDetails.getOutwardBin());
			    		p.setMovementCode(code);
			    		p.setExclusivePartCount(1);
			    		ApiResult<ScanHeader> sh=getPartStockMovementStorageSpaceAllocation(p);
			    		int b=0;
			    		boolean customerBarCodeAvailable=false;
			    		if(scanDetails.getCustomerBarCodes()!=null && scanDetails.getCustomerBarCodes().size()>0) {
			    			customerBarCodeAvailable=true;
			    		}
			    		for(ScanDetails s:sh.result.getScannedPartDetails()) {
			    			if(!s.getMoveToOverflowLocation()) {
								gstock.clearParameters();
								gstock.setLong(1, scanDetails.getAsnId());
								gstock.setLong(2, scanDetails.getTransId());
								gstock.setLong(3, s.getPartId());
								if(customerBarCodeAvailable) {
									gstock.setString(4, scanDetails.getCustomerBarCodes().get(b).getBarCode());
									gstock.setString(5,  scanDetails.getCustomerBarCodes().get(b).getBarCode());
								}else {
									gstock.setString(4, s.getScanData());
									gstock.setString(5, s.getScanData());									
								}
								gstock.setInt(6, scanDetails.getProcessId());
								gstock.setInt(7, s.getLineSpaceConfig().getLineSpacePartConfigId());
								gstock.setInt(8, s.getProcessId());
								gstock.setString(9, code);
								gstock.executeUpdate();								
			    			}
			    			b++;
			    		}
			    	}else {
					pstmt.clearParameters();
					pstmt.setString(1, scanDetails.getQty());
					pstmt.setLong(2, scanDetails.getPartId());
					pstmt.setLong(3, n.getCustomerId());
					pstmt.setLong(4, scanDetails.getSubLocationId());
					pstmt.executeUpdate();
					gstmt.clearParameters();
					gstmt.setString(1, scanDetails.getQty());
					gstmt.setLong(2, scanDetails.getPartId());
					gstmt.setLong(3, scanDetails.getTransId());
					gstmt.executeUpdate();
					bstmt.clearParameters();
					bstmt.setLong(1, scanDetails.getTransId());
					bstmt.setLong(2, scanDetails.getPartId());
					bstmt.setInt(3, n.getToProcessId());
					bstmt.setString(4, scanDetails.getQty());
					bstmt.executeUpdate();
				}
			    }
			}else if(finalPartAvailable && exclusivePart) {
					int exclusivePartCount=n.getAsnDetail().size();
				    for (ScanDetails scanDetails : n.getAsnDetail()) {
				    	if(n.getToProcessId()==8) {
							pstmt.clearParameters();
							pstmt.setInt(1, scanDetails.getGoodQty());
							pstmt.setInt(2, scanDetails.getBadQty());
							pstmt.setInt(3, scanDetails.getHoldQty());
							pstmt.setLong(4, scanDetails.getPartId());
							pstmt.setLong(5, n.getCustomerId());
							pstmt.setLong(6, scanDetails.getSubLocationId());
							pstmt.executeUpdate();
							gstmt.clearParameters();
							gstmt.setInt(1, scanDetails.getGoodQty());
							gstmt.setInt(2, scanDetails.getBadQty());
							gstmt.setInt(3, scanDetails.getHoldQty());
							gstmt.setLong(4, scanDetails.getPartId());
							gstmt.setLong(5, scanDetails.getTransId());
							gstmt.executeUpdate();
							bstmt.clearParameters();
							bstmt.setLong(1, scanDetails.getTransId());
							bstmt.setLong(2, scanDetails.getPartId());
							bstmt.setInt(3, n.getToProcessId());
							bstmt.setInt(4, scanDetails.getGoodQty());
							bstmt.executeUpdate();
							bstmt.clearParameters();
							bstmt.setLong(1, scanDetails.getTransId());
							bstmt.setLong(2, scanDetails.getPartId());
							bstmt.setInt(3, 9);
							bstmt.setInt(4, scanDetails.getBadQty());
							bstmt.executeUpdate();
							bstmt.clearParameters();
							bstmt.setLong(1, scanDetails.getTransId());
							bstmt.setLong(2, scanDetails.getPartId());
							bstmt.setInt(3, 10);
							bstmt.setInt(4, scanDetails.getHoldQty());
							bstmt.executeUpdate();
							if(scanDetails.getExcludedBarCodes()!=null && scanDetails.getExcludedBarCodes().size()>0) {
								for(CustomerBarCode cb:scanDetails.getExcludedBarCodes()) {
									insertExcludedBins.clearParameters();
									insertExcludedBins.setString(1, code);
									insertExcludedBins.setInt(2, cb.getBinNo());
									insertExcludedBins.setString(3, cb.getBarCode());
									insertExcludedBins.addBatch();
								}
								insertExcludedBins.executeBatch();
				    		}
							ASNPartMovement p=new ASNPartMovement();
				    		p.setPartId(scanDetails.getPartId());
				    		p.setLineUsageId(n.getToProcessId());
				    		p.setAsnBinQty(scanDetails.getOutwardBin());
				    		p.setMovementCode(code);
				    		p.setExclusivePartCount(exclusivePartCount);
				    		ApiResult<ScanHeader> sh=getPartStockMovementStorageSpaceAllocation(p);
				    		int b=0;
				    		boolean customerBarCodeAvailable=false;
				    		if(scanDetails.getCustomerBarCodes()!=null && scanDetails.getCustomerBarCodes().size()>0) {
				    			customerBarCodeAvailable=true;
				    		}
						    for(ScanDetails s:sh.result.getScannedPartDetails()) {
				    			if(!s.getMoveToOverflowLocation()) {
									gstock.clearParameters();
									gstock.setLong(1, scanDetails.getAsnId());
									gstock.setLong(2, scanDetails.getTransId());
									gstock.setLong(3, s.getPartId());
									if(customerBarCodeAvailable) {										
										gstock.setString(4, scanDetails.getCustomerBarCodes().get(b).getBarCode());
										gstock.setString(5,  scanDetails.getCustomerBarCodes().get(b).getBarCode());
									}else {
										gstock.setString(4, s.getScanData());
										gstock.setString(5, s.getScanData());
									}
									gstock.setInt(6, 0);
									gstock.setInt(7, s.getLineSpaceConfig().getLineSpacePartConfigId());
									gstock.setInt(8, s.getProcessId());
									gstock.setString(9, code);
									gstock.executeUpdate();
									
									lineusedspace.clearParameters();
									lineusedspace.setInt(1, 1);
									lineusedspace.setInt(2, s.getLineSpaceConfig().getLineSpacePartConfigId());
									lineusedspace.executeUpdate();
				    			}
				    			b++;
				    		}
				    	}						    	
				    }		    		
			}else if(finalPartAvailable && !exclusivePart) {
			    for (ScanDetails scanDetails : n.getAsnDetail()) {
			    	if(n.getToProcessId()==8) {
						pstmt.clearParameters();
						pstmt.setInt(1, 0);
						pstmt.setInt(2, scanDetails.getBadQty());
						pstmt.setInt(3, scanDetails.getHoldQty());
						pstmt.setLong(4, scanDetails.getPartId());
						pstmt.setLong(5, n.getCustomerId());
						pstmt.setLong(6, scanDetails.getSubLocationId());
						pstmt.executeUpdate();
						gstmt.clearParameters();
						gstmt.setInt(1, 0);
						gstmt.setInt(2, scanDetails.getBadQty());
						gstmt.setInt(3, scanDetails.getHoldQty());
						gstmt.setLong(4, scanDetails.getPartId());
						gstmt.setLong(5, scanDetails.getTransId());
						gstmt.executeUpdate();
						bstmt.clearParameters();
						bstmt.setLong(1, scanDetails.getTransId());
						bstmt.setLong(2, scanDetails.getPartId());
						bstmt.setInt(3, n.getToProcessId());
						bstmt.setInt(4, 0);
						bstmt.executeUpdate();
						bstmt.clearParameters();
						bstmt.setLong(1, scanDetails.getTransId());
						bstmt.setLong(2, scanDetails.getPartId());
						bstmt.setInt(3, 9);
						bstmt.setInt(4, scanDetails.getBadQty());
						bstmt.executeUpdate();
						bstmt.clearParameters();
						bstmt.setLong(1, scanDetails.getTransId());
						bstmt.setLong(2, scanDetails.getPartId());
						bstmt.setInt(3, 10);
						bstmt.setInt(4, scanDetails.getHoldQty());
						bstmt.executeUpdate();		
			    	}						    	
			    }
			    
				pstmt.clearParameters();
				pstmt.setInt(1, finalPart.getGoodQty());
				pstmt.setInt(2, 0);
				pstmt.setInt(3, 0);
				pstmt.setLong(4, finalPart.getPartId());
				pstmt.setLong(5, n.getCustomerId());
				pstmt.setLong(6, finalPart.getSubLocationId());
				int up=pstmt.executeUpdate();
				if(up==0) {
					insertStock=conn.prepareStatement(insertStockDetail);
					insertStock.setLong(1, finalPart.getPartId());
					insertStock.setLong(2, n.getCustomerId());
					insertStock.setLong(3, finalPart.getSubLocationId());
					insertStock.setInt(4,finalPart.getGoodQty());
					insertStock.setInt(5, finalPart.getGoodQty());
					insertStock.executeUpdate();
					insertStock.close();				
				}

	    		ASNPartMovement p=new ASNPartMovement();
	    		p.setPartId(finalPart.getPartId());
	    		p.setLineUsageId(n.getToProcessId());
	    		p.setAsnBinQty(finalPart.getOutwardBin());
	    		p.setMovementCode(code);
	    		p.setExclusivePartCount(1);
	    		ApiResult<ScanHeader> sh=getPartStockMovementStorageSpaceAllocation(p);
	    		int b=0;
	    		boolean customerBarCodeAvailable=false;
	    		if(finalPart.getCustomerBarCodes()!=null && finalPart.getCustomerBarCodes().size()>0) {
	    			customerBarCodeAvailable=true;
	    		}
	    		for(ScanDetails s:sh.result.getScannedPartDetails()) {
	    			if(!s.getMoveToOverflowLocation()) {
						gstock.clearParameters();
						gstock.setLong(1, finalPart.getAsnId());
						gstock.setLong(2, finalPart.getTransId());
						gstock.setLong(3, s.getPartId());
						if(customerBarCodeAvailable) {
							gstock.setString(4, finalPart.getCustomerBarCodes().get(b).getBarCode());
							gstock.setString(5,  finalPart.getCustomerBarCodes().get(b).getBarCode());							
						}else {
							gstock.setString(4, s.getScanData());
							gstock.setString(5, s.getScanData());
						}
						gstock.setInt(6, 0);
						gstock.setInt(7, s.getLineSpaceConfig().getLineSpacePartConfigId());
						gstock.setInt(8, s.getProcessId());
						gstock.setString(9, code);
						gstock.executeUpdate();
						
						lineusedspace.clearParameters();
						lineusedspace.setInt(1, 1);
						lineusedspace.setInt(2, s.getLineSpaceConfig().getLineSpacePartConfigId());
						lineusedspace.executeUpdate();
	    			}
	    			b++;
	    		}
		}
		    pstmt.close();
		    gstmt.close();
		    bstmt.close();
			bstmt=conn.prepareStatement(updateStockMovementProcess);
			System.out.println("::second ::::: "+ToStringBuilder.reflectionToString(n));
			for (ScanDetails scanDetails : n.getAsnDetail()) {
				for(int i=0;i<scanDetails.getMovedBinQty();i++) {
					String scanData=scanDetails.getBarCodes().get(i);
					bstmt.clearParameters();
					System.out.println("toprocessid : "+n.getToProcessId()+"\nscandata : "+scanData);
					bstmt.setLong(1, n.getToProcessId());
					bstmt.setString(2, scanData);
					bstmt.executeUpdate();
				}
			}
			conn.commit();
			result.isSuccess=true;
			result.result=code;
			result.message="Stock moved successfully";
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
				if (gstmt != null) {
					gstmt.close();
				}
				if (bstmt != null) {
					bstmt.close();
				}
				if (lineusedspace != null) {
					lineusedspace .close();
				}
				if (gstock != null) {
					gstock.close();
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
	/* Asn Incident Log */
	public ApiResult<AsnIncidentLog> updateAsnIncidentLog(AsnIncidentLog input){
		if(input.getAsnIncidentLogId()==0) {
			return addAsnIncidentLog(input);
		}else {
			return modifyAsnIncidentLog(input);
		}
	}
	public ApiResult<AsnIncidentLog> addAsnIncidentLog(AsnIncidentLog l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			String path= FileUtil.getFilePath("asn", "incident",String.valueOf(l.getAsnId()));
			byte[] decodedBytes = Base64.decodeBase64(l.getDocument());			
			l.setDocumentPath(path+l.getDocumentName());
			FileUtil.writeToFile(decodedBytes,l.getDocumentPath());
			pstmt=conn.prepareStatement(insertAsnIncidentQuery);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getAsnId());
			pstmt.setInt(2, l.getIncidentId());
			pstmt.setString(3, l.getScwComments());
			pstmt.setString(4, l.getDocumentPath());
			pstmt.setInt(5,l.getUserId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Asn Incident Log added successfully";
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
	public ApiResult<AsnIncidentLog> modifyAsnIncidentLog(AsnIncidentLog l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(deleteAsnIncidentQuery);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getAsnIncidentLogId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess=true;
			result.message="Asn Incident Log deleted";
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
	public ApiResult<ArrayList<AsnIncidentLog>> getAsnIncidentLog(long asnId){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		ApiResult<ArrayList<AsnIncidentLog>> result=new ApiResult<ArrayList<AsnIncidentLog>>();
		ArrayList<AsnIncidentLog> incidents=new ArrayList<AsnIncidentLog>();
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(selectAsnIncident);
			pstmt.setLong(1, asnId);
			rs=pstmt.executeQuery();
			while(rs.next()){
				AsnIncidentLog l=new AsnIncidentLog();
				l.setAsnIncidentLogId(rs.getInt("asnincidentlogid"));
				l.setAsnId(rs.getInt("asnid"));
				l.setIncidentId(rs.getInt("incidentid"));
				l.setScwComments(rs.getString("scwcomments"));
				l.setDocument(FileUtil.readFileAsBase64(rs.getString("documentpath")));
//				l.setDocumentPath(rs.getString("documentpath"));			
				incidents.add(l);
			}
			result.result=incidents;
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
	
	private String getWorkFlowStatus() {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		Connection conn=null;
		String result="0";
		try{
			conn=DatabaseUtil.getConnection();
			pstmt=conn.prepareStatement(getWorkFlowStatusQry);
			rs=pstmt.executeQuery();
			if(rs.next()){
				result=rs.getString("configuration_value");
				System.out.print("This is the value of card confirmation"+ result);
			}
		}catch(Exception e){
			e.printStackTrace();
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
	
	public ApiResult<ScanHeader> getPartStorageSpaceAllocation(ASNPartMovement l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult<ScanHeader> result=new ApiResult<ScanHeader>();
		ScanHeader sh=new ScanHeader();
		ArrayList<LineSpacePartConfig> availableSpace=new ArrayList<LineSpacePartConfig>();
		ArrayList<ScanDetails> scanDetails=new ArrayList<ScanDetails>();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(getPartStorageSpace);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getPartId());
			pstmt.setInt(2, l.getLineUsageId());
			rs=pstmt.executeQuery();
			while(rs.next()) {
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
				lp.setLineUsageId(rs.getInt("lineusageid"));
				lp.setLineUsageName(rs.getString("usagename"));
				availableSpace.add(lp);
			}
			rs.close();
			pstmt.close();
			pstmt=null;
			
			int lastCompletedIndex=-1;
			pstmt=conn.prepareStatement(asnPartDetailMovementScan);
			System.out.println(":::asnPartDetailMovementScan QUERY::: " + asnPartDetailMovementScan);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getAsnDetailId());
			pstmt.setLong(2, l.getPartId());
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				ScanDetails sd = new ScanDetails();
				sd.setAsnId(rs.getLong("asnid"));
				sd.setTransId(rs.getInt("asndetailid"));
				sd.setPartId(rs.getInt("partid"));
				sd.setPartName(rs.getString("partdescription"));
				sd.setQty(rs.getString("binqty"));
				sd.setPartNo(rs.getString("partno"));
				sd.setRequestedBinQty("1");
				sd.setSubLocationShortCode(rs.getString("sublocationshortcode"));
				sd.setLoadingType(rs.getInt("loadingtype"));
				sd.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
				sd.setCustomerPartCode(rs.getString("customerpartcode"));
				sd.setSpq(rs.getString("spq"));
				sd.setBinQty(rs.getString("binqty"));
				sd.setCustomerScanCode(rs.getString("customerscancode"));
				sd.setScanData(rs.getString("tbisscancode"));
				sd.setAsnBinQty(rs.getString("requestedqty"));
				sd.setAsnQty(rs.getString("qty"));
				sd.setPalletNumber(rs.getInt("palletno"));
				sd.setPalletScanCode(rs.getString("palletscancode"));
				sd.setSubLocationId(rs.getInt("sublocationid"));
				for(int i=lastCompletedIndex+1;i<availableSpace.size();i++) {
						LineSpacePartConfig c=availableSpace.get(i);
						if(c.getUsedSpace()<c.getAllocatedBins()) {
							sd.setLineSpaceConfig(c);							
							sd.setProcessId(c.getLineUsageId());
							c.setUsedSpace(c.getUsedSpace()+1);
							break;
						}else {
							lastCompletedIndex=i;
						}
				}
				if(sd.getLineSpaceConfig()==null) {
					sd.setMoveToOverflowLocation(true);
					sd.setProcessId(6);
				}
				scanDetails.add(sd);
			}
			rs.close();
			pstmt.close();
			pstmt=null;
			sh.setScannedPartDetails(scanDetails);
			result.isSuccess=true;
			result.result=sh;
			result.message="";
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
	public ApiResult<String> updateStockMovemnetDetail(AsnMaster n) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement gstmt = null;
		PreparedStatement bstmt = null;
		PreparedStatement p1stmt = null;
		PreparedStatement g1stmt = null;
		PreparedStatement b1stmt = null;
		PreparedStatement gstock = null;
		PreparedStatement lineusedspace=null;
		PreparedStatement selectorder=null;
		PreparedStatement updatefifoorder=null;

		ResultSet rs = null;
		ApiResult<String> result=new ApiResult<String>();
		ArrayList<Integer> fifoOrder=new ArrayList<Integer>();
		ArrayList<Integer> bFifoOrder=new ArrayList<Integer>();
		try {
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			StringBuilder strQuery=new StringBuilder(100);
			strQuery.append("update part_stock_detail set ");
			strQuery.append("receivedstock=receivedstock-?");
			strQuery.append(" where partid=? and customerid=? and sublocationid=? ");
			StringBuilder strAsnDetailQuery=new StringBuilder(100);			    
		    strAsnDetailQuery.append("update asndetail set ");
		    strAsnDetailQuery.append("receivedstock=receivedstock-?");
		    strAsnDetailQuery.append(" where partid=? and asndetailid=? ");
		    pstmt=conn.prepareStatement(strQuery.toString());
		    gstmt=conn.prepareStatement(strAsnDetailQuery.toString());
		    bstmt=conn.prepareStatement(insertAsnStockDetail);
		    b1stmt=conn.prepareStatement(updateAsnStockMovementProcess);

		    for (ScanDetails scanDetails : n.getAsnDetail()) {
				pstmt.clearParameters();
				pstmt.setString(1, scanDetails.getQty());
				pstmt.setLong(2, scanDetails.getPartId());
				pstmt.setLong(3, n.getCustomerId());
				pstmt.setLong(4, scanDetails.getSubLocationId());
				pstmt.executeUpdate();
				gstmt.clearParameters();
				gstmt.setString(1, scanDetails.getQty());
				gstmt.setLong(2, scanDetails.getPartId());
				gstmt.setLong(3, scanDetails.getTransId());
				gstmt.executeUpdate();
				bstmt.clearParameters();
				bstmt.setLong(1, scanDetails.getTransId());
				bstmt.setLong(2, scanDetails.getPartId());
				bstmt.setInt(3, 2);
				bstmt.setString(4, "-"+scanDetails.getQty());
				bstmt.executeUpdate();
		    }		    
		    pstmt.close();
		    gstmt.close();
		    bstmt.close();
//		    strQuery.setLength(0);
//		    strAsnDetailQuery.setLength(0);
//			strQuery.append("update part_stock_detail set ");
//			strQuery.append("preinspection=preinspection+?");
//		    strQuery.append(" where partid=? and customerid=? and sublocationid=? ");
//		    
//		    strAsnDetailQuery.append("update asndetail set ");
//			strAsnDetailQuery.append("preinspection=preinspection+?");
//		    strAsnDetailQuery.append(" where partid=? and asndetailid=? ");
//		    pstmt=conn.prepareStatement(strQuery.toString());
//		    gstmt=conn.prepareStatement(strAsnDetailQuery.toString());
//		    bstmt=conn.prepareStatement(insertAsnStockDetail);
//			
//		    for (ScanDetails scanDetails : n.getAsnDetail()) {
//		    	if(scanDetails.getProcessId()==9) {
//					pstmt.clearParameters();
//					pstmt.setString(1, scanDetails.getQty());
//					pstmt.setLong(2, scanDetails.getPartId());
//					pstmt.setLong(3, n.getCustomerId());
//					pstmt.setLong(4, scanDetails.getSubLocationId());
//					pstmt.executeUpdate();
//					gstmt.clearParameters();
//					gstmt.setString(1, scanDetails.getQty());
//					gstmt.setLong(2, scanDetails.getPartId());
//					gstmt.setLong(3, scanDetails.getTransId());
//					gstmt.executeUpdate();
//					bstmt.clearParameters();
//					bstmt.setLong(1, scanDetails.getTransId());
//					bstmt.setLong(2, scanDetails.getPartId());
//					bstmt.setInt(3, scanDetails.getProcessId());
//					bstmt.setString(4, scanDetails.getQty());
//					bstmt.executeUpdate();
//					System.out.println( scanDetails.getScanData()+" "+scanDetails.getProcessId());
//					b1stmt.clearParameters();
//					b1stmt.setLong(1, scanDetails.getProcessId());
//					b1stmt.setLong(2, scanDetails.getTransId());
//					b1stmt.executeUpdate();
//
//		    	}				
//			}		    
//		    pstmt.close();
//		    gstmt.close();
//		    bstmt.close();
//		    b1stmt.close();
		    
		    strQuery.setLength(0);
		    strAsnDetailQuery.setLength(0);
			strQuery.append("update part_stock_detail set ");
			strQuery.append("overflowlocation=overflowlocation+?");
		    strQuery.append(" where partid=? and customerid=? and sublocationid=? ");
		    
		    strAsnDetailQuery.append("update asndetail set ");
			strAsnDetailQuery.append("overflowlocation=overflowlocation+?");
		    strAsnDetailQuery.append(" where partid=? and asndetailid=? ");

			StringBuilder strPartQuery=new StringBuilder(100);
			strPartQuery.append("update part_stock_detail set ");
			strPartQuery.append("goodstock=goodstock+?,preinspection=preinspection+?");
			strPartQuery.append(" where partid=? and customerid=? and sublocationid=? ");
			
			StringBuilder strAsnPartDetailQuery=new StringBuilder(100);			    
			strAsnPartDetailQuery.append("update asndetail set ");
		    strAsnPartDetailQuery.append("goodstock=goodstock+?,preinspection=preinspection+?");
		    strAsnPartDetailQuery.append(" where partid=? and asndetailid=? ");

		    pstmt=conn.prepareStatement(strQuery.toString());
		    gstmt=conn.prepareStatement(strAsnDetailQuery.toString());
		    p1stmt=conn.prepareStatement(strPartQuery.toString());
		    g1stmt=conn.prepareStatement(strAsnPartDetailQuery.toString());
		    bstmt=conn.prepareStatement(insertAsnStockDetail);
			gstock=conn.prepareStatement(insertGoodStockDetails);
			lineusedspace=conn.prepareStatement(updateLineSpaceUsedSpace);
			selectorder=conn.prepareStatement(selectMaxFifoOrder);
			b1stmt=conn.prepareStatement(updateStockMovementProcess);
		    for (ScanDetails scanDetails : n.getAsnDetail()) {
//		    	if(scanDetails.getProcessId()==11) {
		    		ASNPartMovement p=new ASNPartMovement();
		    		p.setPartId(scanDetails.getPartId());
		    		p.setAsnDetailId(scanDetails.getTransId());
		    		p.setLineUsageId(n.getToProcessId());
		    		ApiResult<ScanHeader> sh=getPartStorageSpaceAllocation(p);
		    		int overflowQty=0;
		    		int goodStock=0;
		    		int beforeInspection=0;
		    		for(ScanDetails s:sh.result.getScannedPartDetails()) {
		    			if(s.getMoveToOverflowLocation()) {
		    				overflowQty++;
			    			pstmt.clearParameters();
							pstmt.setString(1, s.getSpq());
							pstmt.setLong(2, s.getPartId());
							pstmt.setLong(3, n.getCustomerId());
							pstmt.setLong(4, s.getSubLocationId());
							pstmt.executeUpdate();
							gstmt.clearParameters();
							gstmt.setString(1, s.getSpq());
							gstmt.setLong(2, s.getPartId());
							gstmt.setLong(3, s.getTransId());
							gstmt.executeUpdate();
							b1stmt.clearParameters();
							b1stmt.setLong(1, 8);
							b1stmt.setString(2, s.getScanData());
							b1stmt.executeUpdate();
//							gstock.clearParameters();
//							gstock.setLong(1, s.getAsnId());
//							gstock.setLong(2, s.getTransId());
//							gstock.setLong(3, s.getPartId());
//							gstock.setString(4, s.getScanData());
//							gstock.setString(5, s.getCustomerScanCode());
//							gstock.setInt(6, s.getProcessId());
//							gstock.setInt(7, 0);
//							gstock.setInt(8, s.getProcessId());
//							gstock.executeUpdate();
		    			}else {
		    				if(scanDetails.getProcessId()==8) {
		    				goodStock++;
		    				}else {
		    					beforeInspection++;
		    				}
		    				p1stmt.clearParameters();
		    				if(scanDetails.getProcessId()==9) {
		    					p1stmt.setInt(1, 0);
		    					p1stmt.setString(2, s.getSpq());
		    				}else {
		    				p1stmt.setString(1, s.getSpq());
		    					p1stmt.setInt(2, 0);
		    				}
							p1stmt.setLong(3, s.getPartId());
							p1stmt.setLong(4, n.getCustomerId());
							p1stmt.setLong(5, s.getSubLocationId());
							p1stmt.executeUpdate();
							g1stmt.clearParameters();
		    				if(scanDetails.getProcessId()==9) {
		    					g1stmt.setInt(1, 0);
		    					g1stmt.setString(2, s.getSpq());
		    				}else {
							g1stmt.setString(1, s.getSpq());
		    					g1stmt.setInt(2, 0);
		    				}
//							g1stmt.setString(1, s.getSpq());
							g1stmt.setLong(3, s.getPartId());
							g1stmt.setLong(4, s.getTransId());
							g1stmt.executeUpdate();
							b1stmt.clearParameters();
							b1stmt.setLong(1, scanDetails.getProcessId());
							b1stmt.setString(2, s.getScanData());
							b1stmt.executeUpdate();
							gstock.clearParameters();
							gstock.setLong(1, s.getAsnId());
							gstock.setLong(2, s.getTransId());
							gstock.setLong(3, s.getPartId());
							gstock.setString(4, s.getScanData());
							gstock.setString(5, s.getCustomerScanCode());
							gstock.setInt(6, scanDetails.getProcessId());
							gstock.setInt(7, s.getLineSpaceConfig().getLineSpacePartConfigId());
							gstock.setInt(8, s.getProcessId());
							gstock.executeUpdate();
							
							lineusedspace.clearParameters();
							lineusedspace.setInt(1, 1);
							lineusedspace.setInt(2, s.getLineSpaceConfig().getLineSpacePartConfigId());
							lineusedspace.executeUpdate();
							if(fifoOrder.indexOf(s.getLineSpaceConfig().getFifoOrder())==-1) {
								fifoOrder.add(s.getLineSpaceConfig().getFifoOrder());
							}
		    			}
		    		}
					bstmt.clearParameters();
					bstmt.setLong(1, scanDetails.getTransId());
					bstmt.setLong(2, scanDetails.getPartId());
					bstmt.setInt(3, 8);
					bstmt.setInt(4, (goodStock*Integer.parseInt(scanDetails.getSpq())));
					bstmt.executeUpdate();

					bstmt.clearParameters();
					bstmt.setLong(1, scanDetails.getTransId());
					bstmt.setLong(2, scanDetails.getPartId());
					bstmt.setInt(3, 6);
					bstmt.setInt(4, (overflowQty*Integer.parseInt(scanDetails.getSpq())));
					bstmt.executeUpdate();
					
					bstmt.clearParameters();
					bstmt.setLong(1, scanDetails.getTransId());
					bstmt.setLong(2, scanDetails.getPartId());
					bstmt.setInt(3, 7);
					bstmt.setInt(4, (beforeInspection*Integer.parseInt(scanDetails.getSpq())));
					bstmt.executeUpdate();
					
					selectorder.clearParameters();
					selectorder.setLong(1,scanDetails.getPartId());
					selectorder.setInt(2, scanDetails.getProcessId());
					rs=selectorder.executeQuery();
					int maxOrder=0;
					if(rs.next()) {
						maxOrder=rs.getInt("fifoorder");
					}
					rs.close();
//					if(maxOrder>0) {
//						maxOrder=maxOrder-1;
//						for(int i=0;i<fifoOrder.size();i++) {
//							updatefifoorder=conn.prepareStatement(updateFifoCurrentOrder);
//							updatefifoorder.setInt(1, maxOrder);
//							updatefifoorder.setLong(2, scanDetails.getPartId());
//							updatefifoorder.setLong(3, fifoOrder.get(i));
//							updatefifoorder.setLong(4, scanDetails.getProcessId());
//							updatefifoorder.executeUpdate();
//							updatefifoorder.close();
//							
//							updatefifoorder=conn.prepareStatement(updateFifoNextOrder);
//							updatefifoorder.setLong(1, scanDetails.getPartId());
//							updatefifoorder.setLong(2, fifoOrder.get(i));
//							updatefifoorder.setLong(3, scanDetails.getProcessId());
//							updatefifoorder.executeUpdate();
//							updatefifoorder.close();
//						}
//						
//					}
						}
						
//			}		    
		    pstmt.close();
		    gstmt.close();
		    p1stmt.close();
		    g1stmt.close();
		    bstmt.close();
		    gstock.close();
		    lineusedspace.close();
		    
			updatefifoorder=conn.prepareStatement(updateAsnStockMovementStatus);
			updatefifoorder.setLong(1, n.getToProcessId());
			updatefifoorder.setLong(2, n.getAsnId());
			updatefifoorder.executeUpdate();
			updatefifoorder.close();

			conn.commit();
			result.isSuccess=true;
			result.message="Stock moved successfully";
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
				if (gstmt != null) {
					gstmt.close();
				}
				if (bstmt != null) {
					bstmt.close();
				}
				if (p1stmt != null) {
					p1stmt.close();
				}
				if (g1stmt != null) {
					g1stmt.close();
				}
				if (bstmt != null) {
					b1stmt.close();
				}
				if (gstock != null) {
					gstock.close();
				}
				if (lineusedspace != null) {
					lineusedspace.close();
				}
				if (selectorder!= null) {
					selectorder.close();
				}
				if (updatefifoorder!= null) {
					updatefifoorder.close();
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
	public ApiResult<ScanHeader> getPalletStorageSpaceAllocation(ASNPartMovement l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		PreparedStatement spaceStmt=null;
		ResultSet rs=null;
		ResultSet spaceResult=null;
		ApiResult<ScanHeader> result=new ApiResult<ScanHeader>();
		ScanHeader sh=new ScanHeader();
		ArrayList<LineSpacePartConfig> availableSpace=new ArrayList<LineSpacePartConfig>();
		ArrayList<ScanDetails> scanDetails=new ArrayList<ScanDetails>();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);

			pstmt=conn.prepareStatement(asnPalletMovement);
			System.out.println(":::asnPalletMovement QUERY::: " + asnPalletMovement);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getAsnId());
			rs=pstmt.executeQuery();			
			while(rs.next()) {
				availableSpace.clear();
				String partids=rs.getString("partids")+","+rs.getString("partid");
				String storageSpace=getPartPalletStorageSpace.replace("<<partids>>", partids);
				spaceStmt=conn.prepareStatement(storageSpace);				
				spaceStmt.clearParameters();
				spaceStmt.setInt(1, l.getLineUsageId());
				spaceResult=spaceStmt.executeQuery();
				while(spaceResult.next()) {
					LineSpacePartConfig lp=new LineSpacePartConfig();
					lp.setAllocatedBins(spaceResult.getInt("allocatedbins"));
					lp.setLineSpacePartConfigId(spaceResult.getInt("linespacepartconfigid"));
					lp.setPartSpaceName(spaceResult.getString("partspacename"));
					lp.setUsedSpace(spaceResult.getInt("usedspace"));
					lp.setFifoOrder(spaceResult.getInt("fifoorder"));
					lp.setFromLineNo(spaceResult.getInt("fromlineno"));
					lp.setToLineNo(spaceResult.getInt("tolineno"));
					lp.setFromCol(spaceResult.getInt("fromcol"));
					lp.setToCol(spaceResult.getInt("tocol"));
					lp.setLineRackId(spaceResult.getInt("linerackid"));
					lp.setLineRackCode(spaceResult.getString("linerackcode"));
					lp.setLineRackCompartmentId(spaceResult.getInt("linerackcompartmentid"));
					lp.setLineRackCompartmentName(spaceResult.getString("linerackcompartmentname"));
					lp.setFromLineSpaceId(spaceResult.getInt("fromlinespaceid"));
//					lp.setFromLineSpaceName(spaceResult.getString("fromlinespacecode"));
					lp.setToLineSpaceId(spaceResult.getInt("tolinespaceid"));
//					lp.setToLineSpaceName(spaceResult.getString("tolinespacecode"));
					lp.setLineUsageId(spaceResult.getInt("lineusageid"));
					lp.setLineUsageName(spaceResult.getString("usagename"));
					lp.setSpaceOccupation(spaceResult.getInt("spaceoccupation"));
					availableSpace.add(lp);
				}
				spaceResult.close();
				String pallets=asnPallets.replace("<<partids>>", partids);
				spaceStmt=conn.prepareStatement(pallets);				
				spaceStmt.clearParameters();
				spaceStmt.setLong(1, l.getAsnId());
				spaceResult=spaceStmt.executeQuery();
				int lastCompletedIndex=-1;
				while(spaceResult.next()) {
					ScanDetails sd = new ScanDetails();
					sd.setPalletNumber(spaceResult.getInt("palletno"));
					sd.setPalletScanCode(spaceResult.getString("palletscancode"));
					sd.setPartId(spaceResult.getInt("partid"));
					scanDetails.add(sd);
					for(int i=lastCompletedIndex+1;i<availableSpace.size();i++) {
						LineSpacePartConfig c=availableSpace.get(i);
						if(c.getUsedSpace()<c.getAllocatedBins()) {
							sd.setLineSpaceConfig(c);			
							sd.setProcessId(c.getLineUsageId());
							c.setUsedSpace(c.getUsedSpace()+1);
							break;
						}else {
							lastCompletedIndex=i;
						}
					}
					if(sd.getLineSpaceConfig()==null) {
						sd.setMoveToOverflowLocation(true);
						sd.setProcessId(6);
					}
				}
			}
			rs.close();
			pstmt.close();
			pstmt=null;
			sh.setScannedPartDetails(scanDetails);
			result.isSuccess=true;
			result.result=sh;
			result.message="";
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
	public ApiResult<String> updatePalletStockMovemnetDetail(AsnMaster n) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement gstmt = null;
		PreparedStatement bstmt = null;
		PreparedStatement p1stmt = null;
		PreparedStatement g1stmt = null;
		PreparedStatement b1stmt = null;
		PreparedStatement gstock = null;
		PreparedStatement lineusedspace=null;
		PreparedStatement selectorder=null;
		PreparedStatement updatefifoorder=null;

		ResultSet rs = null;
		ApiResult<String> result=new ApiResult<String>();
		try {
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			/*
			 * updatePalletMovement
updatePartDetailReceivedStock
updateAsnReceivedStock
updatePartDetailOverflowStock
updateAsnDetailOverflowStock
updatePartDetailPreInspectionStock
updateAsnDetailPreInspectionStock
			 */
			pstmt=conn.prepareStatement(updatePalletMovement);
			gstmt=conn.prepareStatement(updatePartDetailReceivedStock);
			bstmt=conn.prepareStatement(updateAsnReceivedStock);
			p1stmt=conn.prepareStatement(updatePartDetailOverflowStock);
			g1stmt=conn.prepareStatement(updateAsnDetailOverflowStock);
			b1stmt=conn.prepareStatement(updatePartDetailPreInspectionStock);
			gstock=conn.prepareStatement(updateAsnDetailPreInspectionStock);
			lineusedspace=conn.prepareStatement(updatePalletLineSpaceUsedSpace);
			updatefifoorder=conn.prepareStatement(updatePalletLineSpaceLastFilled);
			selectorder=conn.prepareStatement(updatePalletLineSpaceConfig);
			ASNPartMovement p=new ASNPartMovement();
			p.setAsnId(n.getAsnId());
			p.setLineUsageId(7);
			ApiResult<ScanHeader> sph=getPalletStorageSpaceAllocation(p);
			gstmt.clearParameters();
			gstmt.setLong(1, n.getAsnId());
			gstmt.executeUpdate();
			bstmt.clearParameters();
			bstmt.setLong(1, n.getAsnId());
			bstmt.executeUpdate();
			for(ScanDetails s:sph.result.getScannedPartDetails()) {
				if(s.getMoveToOverflowLocation()) {
					pstmt.clearParameters();
					pstmt.setInt(1, 6);
					pstmt.setLong(2, n.getAsnId());
					pstmt.setInt(3,s.getPalletNumber());
					pstmt.executeUpdate();
					
					p1stmt.clearParameters();
					p1stmt.setLong(1, n.getAsnId());
					p1stmt.setInt(2,s.getPalletNumber());
					p1stmt.executeUpdate();

					g1stmt.clearParameters();
					g1stmt.setLong(1, n.getAsnId());
					g1stmt.setInt(2,s.getPalletNumber());
					g1stmt.executeUpdate();
					
				}else {
					pstmt.clearParameters();
					pstmt.setInt(1, 7);
					pstmt.setLong(2, n.getAsnId());
					pstmt.setInt(3,s.getPalletNumber());
					pstmt.executeUpdate();					

					b1stmt.clearParameters();
					b1stmt.setLong(1, n.getAsnId());
					b1stmt.setInt(2,s.getPalletNumber());
					b1stmt.executeUpdate();

					gstock.clearParameters();
					gstock.setLong(1, n.getAsnId());
					gstock.setInt(2,s.getPalletNumber());
					gstock.executeUpdate();

					lineusedspace.clearParameters();
					lineusedspace.setInt(1, 1);
					lineusedspace.setInt(2, s.getLineSpaceConfig().getLineSpacePartConfigId());
					lineusedspace.executeUpdate();

					updatefifoorder.clearParameters();
					updatefifoorder.setInt(1, s.getPartId());
					updatefifoorder.setInt(2, s.getLineSpaceConfig().getLineSpacePartConfigId());
					updatefifoorder.executeUpdate();

					selectorder.clearParameters();
					selectorder.setInt(1, s.getLineSpaceConfig().getLineSpacePartConfigId());
					selectorder.setLong(2, n.getAsnId());
					selectorder.setInt(3,s.getPalletNumber());
					selectorder.executeUpdate();
				}
			}
			updatefifoorder=conn.prepareStatement(updateAsnStockMovementStatus);
			updatefifoorder.setLong(1, n.getToProcessId());
			updatefifoorder.setLong(2, n.getAsnId());
			updatefifoorder.executeUpdate();
			updatefifoorder.close();

			conn.commit();
			result.isSuccess=true;
			result.message="Stock moved successfully";
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
				if (gstmt != null) {
					gstmt.close();
				}
				if (bstmt != null) {
					bstmt.close();
				}
				if (p1stmt != null) {
					p1stmt.close();
				}
				if (g1stmt != null) {
					g1stmt.close();
				}
				if (bstmt != null) {
					b1stmt.close();
				}
				if (gstock != null) {
					gstock.close();
				}
				if (lineusedspace != null) {
					lineusedspace.close();
				}
				if (selectorder!= null) {
					selectorder.close();
				}
				if (updatefifoorder!= null) {
					updatefifoorder.close();
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
	public ApiResult<ScanHeader> getPartStockMovementStorageSpaceAllocation(ASNPartMovement l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		ApiResult<ScanHeader> result=new ApiResult<ScanHeader>();
		ScanHeader sh=new ScanHeader();
		ArrayList<LineSpacePartConfig> availableSpace=new ArrayList<LineSpacePartConfig>();
		ArrayList<ScanDetails> scanDetails=new ArrayList<ScanDetails>();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(getPartStorageSpace);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getPartId());
			pstmt.setInt(2, l.getLineUsageId());
			rs=pstmt.executeQuery();
			while(rs.next()) {
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
				lp.setLineUsageId(rs.getInt("lineusageid"));
				lp.setLineUsageName(rs.getString("usagename"));
				availableSpace.add(lp);
			}
			rs.close();
			pstmt.close();
			pstmt=null;
			
			int lastCompletedIndex=-1;
			pstmt=conn.prepareStatement(asnPartDetailStockDetail);
			System.out.println(":::asnPartDetailStockDetail QUERY::: " + asnPartDetailStockDetail);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getPartId());
			rs=pstmt.executeQuery();
			ScanDetails sd = new ScanDetails();
			if(rs.next()) {
				sd.setPartId(rs.getInt("partid"));
				sd.setPartName(rs.getString("partdescription"));
				sd.setPartNo(rs.getString("partno"));
				sd.setCustomerPartCode(rs.getString("customerpartcode"));
				sd.setSpq(rs.getString("spq"));
				sd.setBinQty(rs.getString("binqty"));
				sd.setSubLocationId(rs.getInt("sublocationid"));
				sd.setCustomerScanCode(rs.getString("customer_erp_code"));
				sd.setSubLocationShortCode(rs.getString("sublocationshortcode"));

			}
			for(int j=0;j<l.getAsnBinQty();j++) {
				String tbisScanCode=tbisOutwardString(sd.getCustomerPartCode(), sd.getCustomerScanCode(), Integer.parseInt(sd.getBinQty()),sd.getSubLocationShortCode() ,j+1,l.getMovementCode());
				ScanDetails s = new ScanDetails();
				s.setPartId(sd.getPartId());
				s.setPartName(sd.getPartName());
				s.setPartNo(sd.getPartNo());
				s.setCustomerPartCode(sd.getCustomerPartCode());
				s.setSpq(sd.getSpq());
				s.setBinQty(sd.getBinQty());
				s.setSubLocationId(sd.getSubLocationId());
				s.setSubLocationShortCode(sd.getSubLocationShortCode());
				s.setScanData(tbisScanCode);
				for(int i=lastCompletedIndex+1;i<availableSpace.size();i++) {
						LineSpacePartConfig c=availableSpace.get(i);
						if(c.getUsedSpace()<c.getAllocatedBins()) {
							s.setLineSpaceConfig(c);			
							s.setProcessId(c.getLineUsageId());
							c.setUsedSpace(c.getUsedSpace()+l.getExclusivePartCount());
							break;
						}else {
							lastCompletedIndex=i;
						}
				}
				if(s.getLineSpaceConfig()==null) {
					s.setMoveToOverflowLocation(true);
					s.setProcessId(6);
				}
				scanDetails.add(s);
			}
			rs.close();
			pstmt.close();
			pstmt=null;
			sh.setScannedPartDetails(scanDetails);
			result.isSuccess=true;
			result.result=sh;
			result.message="";
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
	public ScanDetails getSinglePalletStorageSpaceAllocation(ASNPartMovement l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		PreparedStatement spaceStmt=null;
		ResultSet rs=null;
		ResultSet spaceResult=null;
		ArrayList<LineSpacePartConfig> availableSpace=new ArrayList<LineSpacePartConfig>();
		ScanDetails scanDetails=new ScanDetails();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);

			pstmt=conn.prepareStatement(asnSinglePalletMovement);
			System.out.println(":::asnSinglePalletMovement QUERY::: " + asnSinglePalletMovement);
			pstmt.clearParameters();
			pstmt.setLong(1, l.getAsnId());
			pstmt.setString(2, l.getPalletScanCode());
			rs=pstmt.executeQuery();			
			while(rs.next()) {
				availableSpace.clear();
				String partids=rs.getString("partids")+","+rs.getString("partid");
				String storageSpace=getPartSinglePalletStorageSpace.replace("<<partids>>", partids);
				spaceStmt=conn.prepareStatement(storageSpace);				
				spaceStmt.clearParameters();
				spaceStmt.setInt(1, l.getLineUsageId());
				spaceResult=spaceStmt.executeQuery();
				while(spaceResult.next()) {
					LineSpacePartConfig lp=new LineSpacePartConfig();
					lp.setAllocatedBins(spaceResult.getInt("allocatedbins"));
					lp.setLineSpacePartConfigId(spaceResult.getInt("linespacepartconfigid"));
					lp.setPartSpaceName(spaceResult.getString("partspacename"));
					lp.setUsedSpace(spaceResult.getInt("usedspace"));
					lp.setFifoOrder(spaceResult.getInt("fifoorder"));
					lp.setFromLineNo(spaceResult.getInt("fromlineno"));
					lp.setToLineNo(spaceResult.getInt("tolineno"));
					lp.setFromCol(spaceResult.getInt("fromcol"));
					lp.setToCol(spaceResult.getInt("tocol"));
					lp.setLineRackId(spaceResult.getInt("linerackid"));
					lp.setLineRackCode(spaceResult.getString("linerackcode"));
					lp.setLineRackCompartmentId(spaceResult.getInt("linerackcompartmentid"));
					lp.setLineRackCompartmentName(spaceResult.getString("linerackcompartmentname"));
					lp.setFromLineSpaceId(spaceResult.getInt("fromlinespaceid"));
//					lp.setFromLineSpaceName(spaceResult.getString("fromlinespacecode"));
					lp.setToLineSpaceId(spaceResult.getInt("tolinespaceid"));
//					lp.setToLineSpaceName(spaceResult.getString("tolinespacecode"));
					lp.setLineUsageId(spaceResult.getInt("lineusageid"));
					lp.setLineUsageName(spaceResult.getString("usagename"));
					lp.setSpaceOccupation(spaceResult.getInt("spaceoccupation"));
					availableSpace.add(lp);
				}
				spaceResult.close();
				int lastCompletedIndex=-1;
				scanDetails.setPalletNumber(rs.getInt("palletno"));
				scanDetails.setPalletScanCode(rs.getString("palletscancode"));
				scanDetails.setPartId(rs.getInt("partid"));
				for(int i=lastCompletedIndex+1;i<availableSpace.size();i++) {
					LineSpacePartConfig c=availableSpace.get(i);
					if(c.getUsedSpace()<c.getAllocatedBins()) {
						scanDetails.setLineSpaceConfig(c);			
						scanDetails.setProcessId(c.getLineUsageId());
						break;
					}else {
						lastCompletedIndex=i;
					}
				}
				if(scanDetails.getLineSpaceConfig()==null) {
					scanDetails.setMoveToOverflowLocation(true);
					scanDetails.setProcessId(6);
				}
			}
			rs.close();
			pstmt.close();
			pstmt=null;
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
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
		return scanDetails;
	}
	public ApiResult<ScanHeader> getAsnGinScanDetail(long asnId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = null;
		ApiResult<ScanHeader> result = new ApiResult<ScanHeader>();
		ArrayList<ScanDetails> details = new ArrayList<ScanDetails>();
		ScanHeader h=new ScanHeader();
		try {
			conn = DatabaseUtil.getConnection();
			ScanDetails sph=null;
			h.setIsPallet(true);								
			pstmt = conn.prepareStatement(asnPartDetailPalletGinDetail);
			pstmt.setLong(1, asnId);
			rs = pstmt.executeQuery();
			int palletNo=0;
			while(rs.next()) {
				ScanDetails l = new ScanDetails();
				l.setAsnId(rs.getLong("asnid"));
				l.setTransId(rs.getInt("asndetailid"));
				l.setPartId(rs.getInt("partid"));
				l.setPartName(rs.getString("partdescription"));
				l.setQty(rs.getString("qty"));
				l.setPartNo(rs.getString("partno"));
				l.setRequestedBinQty(rs.getString("requestedqty"));
				l.setSubLocationShortCode(rs.getString("sublocationshortcode"));
				l.setLoadingType(rs.getInt("loadingtype"));
				l.setNoOfPackingInPallet(rs.getInt("noofpackinginpallet"));
				l.setCustomerPartCode(rs.getString("customerpartcode"));
				l.setSpq(rs.getString("spq"));
				l.setBinQty(rs.getString("binqty"));
//				l.setCustomerScanCode(rs.getString("customerscancode"));
//				l.setScanData(rs.getString("tbisscancode"));
				l.setAsnBinQty(rs.getString("requestedqty"));
				l.setAsnQty(rs.getString("qty"));
				l.setPalletNumber(rs.getInt("palletno"));
				l.setPalletScanCode(rs.getString("palletscancode"));
				l.setProcessId(rs.getInt("processid"));
				l.setMoveToOverflowLocation(false);
				if(l.getProcessId()==7) {
					l.setLineSpaceConfig(getLineSpaceConfig(rs.getLong("linespacepartconfigid")));
				}else {
					l.setMoveToOverflowLocation(true);
				}
				details.add(l);
			}
			pstmt.close();
			rs.close();
			if(details.size()>0) {
				h.setScannedPartDetails(details);
				result.isSuccess=true;
				result.result = h;
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
	public LineSpacePartConfig getLineSpaceConfig(long lineSpacePartConfigId){
		Connection conn=null;
		PreparedStatement spaceStmt=null;
		ResultSet spaceResult=null;
		LineSpacePartConfig availableSpace=new LineSpacePartConfig();
		try{
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			spaceStmt=conn.prepareStatement(getPartSinglePalletStorageSpaceDetail);				
			spaceStmt.clearParameters();
			spaceStmt.setLong(1, lineSpacePartConfigId);
			spaceResult=spaceStmt.executeQuery();
			while(spaceResult.next()) {
				LineSpacePartConfig lp=new LineSpacePartConfig();
				lp.setAllocatedBins(spaceResult.getInt("allocatedbins"));
				lp.setLineSpacePartConfigId(spaceResult.getInt("linespacepartconfigid"));
				lp.setPartSpaceName(spaceResult.getString("partspacename"));
				lp.setUsedSpace(spaceResult.getInt("usedspace"));
				lp.setFifoOrder(spaceResult.getInt("fifoorder"));
				lp.setFromLineNo(spaceResult.getInt("fromlineno"));
				lp.setToLineNo(spaceResult.getInt("tolineno"));
				lp.setFromCol(spaceResult.getInt("fromcol"));
				lp.setToCol(spaceResult.getInt("tocol"));
				lp.setLineRackId(spaceResult.getInt("linerackid"));
				lp.setLineRackCode(spaceResult.getString("linerackcode"));
				lp.setLineRackCompartmentId(spaceResult.getInt("linerackcompartmentid"));
				lp.setLineRackCompartmentName(spaceResult.getString("linerackcompartmentname"));
				lp.setFromLineSpaceId(spaceResult.getInt("fromlinespaceid"));
//					lp.setFromLineSpaceName(spaceResult.getString("fromlinespacecode"));
				lp.setToLineSpaceId(spaceResult.getInt("tolinespaceid"));
//					lp.setToLineSpaceName(spaceResult.getString("tolinespacecode"));
				lp.setLineUsageId(spaceResult.getInt("lineusageid"));
				lp.setLineUsageName(spaceResult.getString("usagename"));
				lp.setSpaceOccupation(spaceResult.getInt("spaceoccupation"));
				availableSpace=lp;
			}
			spaceResult.close();
		}catch(Exception e){
			try{
				if(conn!=null){
					conn.rollback();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(spaceResult!=null){
					spaceResult.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return availableSpace;
	}
	public String updatePalletGinStockMovemnetDetail(Connection conn,ScanDetails s) {
		PreparedStatement pstmt = null;
		PreparedStatement gstmt = null;
		PreparedStatement bstmt = null;
		PreparedStatement p1stmt = null;
		PreparedStatement g1stmt = null;
		PreparedStatement b1stmt = null;
		PreparedStatement gstock = null;

		ResultSet rs = null;
		String result="Failed";
		try {
			pstmt=conn.prepareStatement(updatePalletMovement);
			gstmt=conn.prepareStatement(updatePartDetailReceivedStock);
			bstmt=conn.prepareStatement(updateAsnReceivedStock);
			p1stmt=conn.prepareStatement(updatePartDetailOverflowGinStock);
			g1stmt=conn.prepareStatement(updateAsnDetailOverflowGinStock);
			b1stmt=conn.prepareStatement(updatePartDetailPreInspectionGinStock);
			gstock=conn.prepareStatement(updateAsnDetailPreInspectionGinStock);
			if(s.getMoveToOverflowLocation()) {
				pstmt.clearParameters();
				pstmt.setInt(1, 6);
				pstmt.setLong(2, s.getAsnId());
				pstmt.setInt(3,s.getPalletNumber());
				pstmt.executeUpdate();
				
				p1stmt.clearParameters();
				p1stmt.setLong(1, s.getAsnId());
				p1stmt.setInt(2,s.getPalletNumber());
				p1stmt.setInt(3,s.getPartId());
				p1stmt.executeUpdate();

				g1stmt.clearParameters();
				g1stmt.setLong(1, s.getAsnId());
				g1stmt.setInt(2,s.getPalletNumber());
				g1stmt.setInt(3,s.getPartId());
				g1stmt.executeUpdate();
				
			}else {
				pstmt.clearParameters();
				pstmt.setInt(1, 7);
				pstmt.setLong(2, s.getAsnId());
				pstmt.setInt(3,s.getPalletNumber());
				pstmt.executeUpdate();					

				b1stmt.clearParameters();
				b1stmt.setLong(1, s.getAsnId());
				b1stmt.setInt(2,s.getPalletNumber());
				b1stmt.setInt(3,s.getPartId());
				b1stmt.executeUpdate();

				gstock.clearParameters();
				gstock.setLong(1, s.getAsnId());
				gstock.setInt(2,s.getPalletNumber());
				gstock.setInt(3,s.getPartId());
				gstock.executeUpdate();

			}
			result="Success";
		} catch (Exception e) {
			e.printStackTrace();
			result=e.getMessage();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (gstmt != null) {
					gstmt.close();
				}
				if (bstmt != null) {
					bstmt.close();
				}
				if (p1stmt != null) {
					p1stmt.close();
				}
				if (g1stmt != null) {
					g1stmt.close();
				}
				if (bstmt != null) {
					b1stmt.close();
				}
				if (gstock != null) {
					gstock.close();
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
	public ArrayList<LineSpacePartConfig> getLineSpaceFifoDetail(long customerId,int usageTypeId){
		Connection conn=null;
		PreparedStatement spaceStmt=null;
		ResultSet spaceResult=null;
		ArrayList<LineSpacePartConfig> availableSpace=new ArrayList<LineSpacePartConfig>();
		try{
			conn=DatabaseUtil.getConnection();
			spaceStmt=conn.prepareStatement(getPartFifoSpaceDetail);				
			spaceStmt.clearParameters();
			spaceStmt.setLong(1, usageTypeId);
			spaceStmt.setLong(2, customerId);
			spaceResult=spaceStmt.executeQuery();
			while(spaceResult.next()) {
				LineSpacePartConfig lp=new LineSpacePartConfig();
//				lp.setPartId(spaceResult.getInt("partid"));
				lp.setAllocatedBins(spaceResult.getInt("allocatedbins"));
				lp.setLineSpacePartConfigId(spaceResult.getInt("linespacepartconfigid"));
				lp.setPartSpaceName(spaceResult.getString("partspacename"));
				lp.setUsedSpace(spaceResult.getInt("usedspace"));
				lp.setFifoOrder(spaceResult.getInt("fifoorder"));
				lp.setFromLineNo(spaceResult.getInt("fromlineno"));
				lp.setToLineNo(spaceResult.getInt("tolineno"));
				lp.setFromCol(spaceResult.getInt("fromcol"));
				lp.setToCol(spaceResult.getInt("tocol"));
//				lp.setLineRackId(spaceResult.getInt("linerackid"));
//				lp.setLineRackCode(spaceResult.getString("linerackcode"));
//				lp.setLineRackCompartmentId(spaceResult.getInt("linerackcompartmentid"));
//				lp.setLineRackCompartmentName(spaceResult.getString("linerackcompartmentname"));
//				lp.setFromLineSpaceId(spaceResult.getInt("fromlinespaceid"));
				lp.setFromLineSpaceName(spaceResult.getString("partdescription"));
//				lp.setToLineSpaceId(spaceResult.getInt("tolinespaceid"));
				lp.setToLineSpaceName(spaceResult.getString("customerpartcode"));
				lp.setLineUsageId(spaceResult.getInt("lineusageid"));
				lp.setLineUsageName(spaceResult.getString("usagename"));
				lp.setSpaceOccupation(spaceResult.getInt("spaceoccupation"));
				lp.setLastFilledSpace(spaceResult.getInt("lastfilledspace"));
				lp.setpNoOfStack(spaceResult.getInt("pnoofstack"));
				lp.setStock(spaceResult.getInt("stock"));
				availableSpace.add(lp);
			}
			spaceResult.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn!=null){
					conn.close();
				}
				if(spaceResult!=null){
					spaceResult.close();
				}				
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return availableSpace;
	}
	public ApiResult<AsnMaster> updateGateVerificationStatus(AsnMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateGateVerifciationState);
			pstmt.clearParameters();
			pstmt.setString(1, l.getGateEntryComment());
			pstmt.setLong(2, l.getGateEntryStatus());
			pstmt.setInt(3, l.getUserId());
			pstmt.setLong(4, l.getAsnId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = l.getAsnId()+"";
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
	
	public ApiResult<AsnMaster> updateAsnPartsAdd(AsnMaster l) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ApiResult result = new ApiResult();
		try {
			conn = DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateAsnPartsAddQuery);
			pstmt.clearParameters();
			pstmt.setInt(1,l.getPartsAdd());
			pstmt.setLong(2, l.getAsnId());
			pstmt.executeUpdate();
			conn.commit();
			result.isSuccess = true;
			result.message = "Parts Added Confirmed";
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
	
	
	public ApiResult<AsnMaster> readAsnPartsFile(int userId,String fileLocation){
		Connection conn=null;
		PreparedStatement pstmt=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			result.isSuccess=true;
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			
			pstmt=conn.prepareStatement("truncate table asnpartsimport;");
			pstmt.executeUpdate();
			pstmt.close();
			
			pstmt=conn.prepareStatement(insertImportAsnPartsData);
			
			
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
                    if(index>11) {
                    	break;
                    }
                }
                if(j>2) {
                	pstmt.addBatch();
                }
            }
					
			if(!result.isSuccess) {
				conn.rollback();
				return result;
			}
			pstmt.executeBatch();
			pstmt.close();
			
			
//			String sql =" update picklistdetail p inner join srvimport s on s.srvno =p.srvnumber set  p.examount =s.exciseamt ,p.cgst=s.cgstamt ,p.sgst=s.sgstamt ,p.igst=s.igstamt ,p.fsft=s.`57f2no` ,p.srvcomments=s.status,p.srvqty=s.qty";
//			pstmt=conn.prepareStatement(sql);
//			pstmt.executeUpdate();
//			pstmt.close();

			conn.commit();
			result.isSuccess=true;
			result.message="ASN Parts Uploaded Successfully";
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
	

	public ApiResult<AsnMaster> updateAsnInboundVehicleAssign(AsnMaster l){
		Connection conn=null;
		PreparedStatement pstmt=null;
		PreparedStatement pAmstmt=null;
		PreparedStatement pAdstmt=null;
		PreparedStatement pApalstmt=null;
		PreparedStatement pAbinstmt=null;
		PreparedStatement pAPalScstmt=null;
		PreparedStatement pgetAsnDetail=null;
		PreparedStatement pInsAsnScanCodes=null;
		ResultSet rs=null;
		ApiResult result=new ApiResult();
		String code="0";
		try{
			result.isSuccess=true;
			conn=DatabaseUtil.getConnection();
			conn.setAutoCommit(false);
			
			pstmt=conn.prepareStatement(selectInboundDataQuery);
			pAmstmt=conn.prepareStatement(insertInboundAsnMaster,PreparedStatement.RETURN_GENERATED_KEYS);
			pAdstmt=conn.prepareStatement(insertInboundAsnDetail);
			pApalstmt=conn.prepareStatement(insertInboundAsnPallet);
//			pAbinstmt=conn.prepareStatement(insertInboundAsnPalletScan);
			pAPalScstmt=conn.prepareStatement(insertInboundAsnPalletScanCode);
			pgetAsnDetail=conn.prepareStatement(getAsnScanCodes);
			pInsAsnScanCodes=conn.prepareStatement(insAsnScanCodes);
		
			pstmt.clearParameters();
			pstmt.setString(1,l.getContainerNo());
			pstmt.setInt(2, l.getInboundId());
			rs=pstmt.executeQuery();
			
			ArrayList<AsnMaster> m=new ArrayList<AsnMaster>();
			while(rs.next()) {
				AsnMaster p=new AsnMaster();
//				p.setInboundId(rs.getInt("inboundid"));
//				p.setContainerNo(rs.getString("containerno"));
				p.setInboundNo(rs.getString("inboundno"));
				p.setCustomerId(rs.getLong("customerid"));
				m.add(p);
					
			}
			rs.close();
			for(AsnMaster p:m) {
				System.out.println("Customer id"+p.getCustomerId());
				String id="0";
				
				pAmstmt.clearParameters();
				pAmstmt.setString(1, l.getVechicleNo());
				pAmstmt.setInt(2,l.getUserId());
				pAmstmt.setInt(3, l.getLoactionId());
				pAmstmt.setInt(4, l.getSubloactionId());
				pAmstmt.setInt(5, l.getWarehouseId());
				pAmstmt.setString(6,l.getContainerNo());
				pAmstmt.setInt(7, l.getInboundId());
				pAmstmt.setLong(8, p.getCustomerId());
				pAmstmt.executeUpdate();
				rs=pAmstmt.getGeneratedKeys();
				if(rs.next()) {
					id=rs.getString(1);
				}
				System.out.println("asn id"+id);
				rs.close();
				pAdstmt.clearParameters();
				pAdstmt.setString(1, id);
				pAdstmt.setInt(2, l.getUserId());
				pAdstmt.setString(3,l.getContainerNo());
				pAdstmt.setInt(4, l.getInboundId());
				pAdstmt.setLong(5,p.getCustomerId());
				pAdstmt.executeUpdate();
				
				pApalstmt.clearParameters();
				pApalstmt.setString(1, id);
				pApalstmt.setString(2,l.getContainerNo());
				pApalstmt.setInt(3, l.getInboundId());
				pApalstmt.setLong(4,p.getCustomerId());
				pApalstmt.executeUpdate();
				
//				pAbinstmt.clearParameters();
//				pAbinstmt.setString(1, id);
//				pAbinstmt.setString(2,l.getContainerNo());
//				pAbinstmt.setInt(3, l.getInboundId());
//				pAbinstmt.setLong(4,p.getCustomerId());
//				pAbinstmt.executeUpdate();
				
				pAPalScstmt.clearParameters();
				pAPalScstmt.setString(1, id);
				pAPalScstmt.setString(2,l.getContainerNo());
				pAPalScstmt.setInt(3, l.getInboundId());
				pAPalScstmt.setLong(4,p.getCustomerId());
				pAPalScstmt.executeUpdate();

				pgetAsnDetail.clearParameters();
				pgetAsnDetail.setString(1, id);
				pgetAsnDetail.setString(2,l.getContainerNo());
				pgetAsnDetail.setInt(3, l.getInboundId());
				pgetAsnDetail.setLong(4,p.getCustomerId());
				rs=pgetAsnDetail.executeQuery();
				int binSno=0;
				while(rs.next()){
					binSno=0;
					int palletBins=rs.getInt("binqty");
					int palletNo=rs.getInt("palletno");
					for(int j=0;j<palletBins;j++) {
						binSno++;
						String customerScanCode=customerString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("qtybox"), rs.getString("sublocationshortcode"),String.valueOf(binSno));
						String tbisScanCode=tbisString(rs.getString("customerpartcode"), rs.getString("customer_erp_code"), rs.getInt("qtybox"), rs.getString("sublocationshortcode"),binSno,rs.getInt("asndetailid"));
						pInsAsnScanCodes.clearParameters();
						pInsAsnScanCodes.setLong(1, rs.getLong("asnid"));
						pInsAsnScanCodes.setLong(2, rs.getLong("asndetailid"));
						pInsAsnScanCodes.setString(3, tbisScanCode);
						pInsAsnScanCodes.setString(4, customerScanCode);
						pInsAsnScanCodes.setInt(5, palletNo);
						pInsAsnScanCodes.addBatch();
					}
					pInsAsnScanCodes.executeBatch();
				}
				rs.close();
			}
			pstmt.close();
			
			String sql =" update inbounddataimport set asnstatusid=24 where inboundid=? and containerno=? ";
			pstmt=conn.prepareStatement(sql);
			pstmt.clearParameters();
			pstmt.setInt(1, l.getInboundId());
			pstmt.setString(2, l.getContainerNo());
			pstmt.executeUpdate();
			pstmt.close();
			
			conn.commit();
			result.isSuccess=true;
			result.message="Asn Unbound Detail added successfully";
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
				if(pAmstmt!=null) {
					pAmstmt.close();
				}
				if(pAdstmt!=null){
					pAdstmt.close();
				}
				if(pApalstmt!=null){
					pApalstmt.close();
				}
				if(pAbinstmt!=null) {
					pAbinstmt.close();
				}
				if(pAPalScstmt!=null){
					pAPalScstmt.close();
				}
				if(pInsAsnScanCodes!=null){
					pInsAsnScanCodes.close();
				}
				if(pgetAsnDetail!=null){
					pgetAsnDetail.close();
				}
			}catch(SQLException esql){
				esql.printStackTrace();
			}
		}
		return result;
	}
	
	public static AsnService getInstance() {
		return new AsnService();
	}
}
