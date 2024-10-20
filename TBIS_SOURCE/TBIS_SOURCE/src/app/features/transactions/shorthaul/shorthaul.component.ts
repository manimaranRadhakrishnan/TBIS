import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { GateEntryAjax } from '../../../models/transaction';
import { DatePipe } from '@angular/common';
import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-shorthaul',
  standalone: false,
  templateUrl: './shorthaul.component.html',
  styleUrl: './shorthaul.component.css'
})
export class ShorthaulComponent implements OnInit,AfterViewInit{
  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  shorthaul:any={};
  form: any = {};
  action:string="view";
  gateEntry!:GateEntryAjax[];
  scannedValue:string="";
  cardScannedDetails:any=[];
  savedCardDetails:any=[];
  vehicleDet:any=[];
  activeTab:string="scan";
  primaryCustomerId:number=0;
  userRoleId:string="1";
  isDisabled:boolean=false;
  customers!:any[];
  customerDetail:any={};
  customerPartDetails:any=[];
  transitLocations:any=[];
  rptParam:string="0"
  myDate = new Date();
  enableAdd:boolean=false;
  showVehicleDlg=false;
  shorthaulvehicleslist:any=[];
  doActionPicklistId:string="0";

eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;
  @ViewChild('selectedreport', { static: false })
  selectedreport!: ReportComponent;
  @ViewChild('report', { static: false })
  report!: ReportComponent;
constructor(
private WmsGV: AppGlobal,
private service:TransactionService, 
private toastr:ToastrService) { }

ngOnInit(): void {
  this.userRoleId=this.WmsGV.roleId;
  this.clearData();
  this.getAjaxWareHouse('warehouse');
}



ngAfterViewInit() {
  
  if(!this.masterObj){
    this.masterData={    
     transId :0,
     asnId:0,
     vechicleNo :'',
     supplyDate:'',
     supplyTime:'',
     driverName:'',
     driverMobile:'',
     asnStatus:0,
     statusId:'',
     userId:'',
     totalCardScanned:0,
     returnPack:'0',
     returnPackQty:0,
     transitType:1,
     locationName:'',
     subLocationShortCode:'',
     customerName:'',
     transitLocationId:'0',
     part:{
      partid: "0",
      partno:"",
      partdescription: "",
      customerpartcode: "",
      spq: "",
      binqty: 0,
      loadingtype: 0,
      noofpackinginpallet: 0,
      qty:0
     }
  }
  }
  
}

openVehicleDlg() {
  this.showVehicleDlg=true;
  this.getAjaxVehicles('allvehicle');
}
closeVehicleDlgModel() {
  this.showVehicleDlg=false;
}


getAjaxVehicles(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.shorthaulvehicleslist=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}


getAjaxData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data && rdata.data.length>0) {
              this.customerDetail=rdata.data[0];
              if (this.customerDetail!=null && this.customerDetail.customerid>0){
                this.masterData.customerId=this.customerDetail.customerid;
                this.masterData.customerName=this.customerDetail.customername;
                this.masterData.locationId=this.customerDetail.locationid;
                this.masterData.unLoadingDocId=this.customerDetail.udc_id;
                this.masterData.subloactionId=this.customerDetail.primarysublocationid;
                this.masterData.warehouseId=this.customerDetail.warehousid;
                this.masterData.transitLocationId=this.customerDetail.warehousid+"";
                this.masterData.subLocationShortCode=this.customerDetail.sublocationshortcode;
                this.masterData.locationName=this.customerDetail.locationname;
                this.masterData.dockName=this.customerDetail.udcname;
                this.masterData.driverMobile='';
                this.masterData.driverName='';
                this.masterData.vechicleNo='';
                this.masterData.ewayBillNo='';
                this.masterData.invoiceNo='';
                this.masterData.asnStatus='';
                this.masterData.asnid=0;
                this.masterData.part={
                  partid: 0,
                  partno:"",
                  partdescription: "",
                  customerpartcode: "",
                  spq: "",
                  binqty: 0,
                  loadingtype: 0,
                  noofpackinginpallet: 0,
                  qty:0
                 };
                this.getCustomerParts(this.customerDetail.customerid);
              }
  
             
          }
       },
      error: (e) => console.error(e),
  })
  
  }

getAjaxWareHouse(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.transitLocations=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
this.service.getAjaxDropDown(ajaxId).subscribe({
    next: (rdata) => {
        if (rdata.data && rdata.data.length>0) {
            this.customerDetail=rdata.data[0];
            if (this.customerDetail!=null && this.customerDetail.customerid>0){
              this.masterData.customerId=this.customerDetail.customerid;
              this.masterData.customerName=this.customerDetail.customername;
              this.masterData.locationId=this.customerDetail.locationid;
              this.masterData.unLoadingDocId=this.customerDetail.udc_id;
              this.masterData.subloactionId=this.customerDetail.primarysublocationid;
              this.masterData.warehouseId=this.customerDetail.warehousid;
              this.masterData.transitlocationId=this.customerDetail.locationid;
              this.masterData.subLocationShortCode=this.customerDetail.sublocationshortcode;
              this.masterData.locationName=this.customerDetail.locationname;
              this.masterData.dockName=this.customerDetail.udcname;
              this.masterData.transitLocationId=this.customerDetail.warehousid+"";
              this.masterData.driverMobile='';
              this.masterData.driverName='';
              this.masterData.vechicleNo='';
              this.masterData.ewayBillNo='';
              this.masterData.invoiceNo='';
              this.masterData.asnStatus='';
              this.masterData.asnid=0;
              this.masterData.parts=[];
              this.getCustomerParts(this.customerDetail.customerid);
            }

           
        }
     },
    error: (e) => console.error(e),
})

}



getCustomerParts(customerId:number):void{
  this.customerPartDetails=[];
  let contactsparam='asncustomerparts&customerid='+customerId;
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.customerPartDetails=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

onSelectPart(partid:any):void{
  this.masterData.part=this.customerPartDetails.find((x:any)=>x.partid==partid);
  this.masterData.part.qty=this.masterData.part.spq;
  this.masterData.part.noofbins=this.masterData.part.spq/this.masterData.part.binqty;
}
onChangeQuantity(event:any):void{
  if(this.masterData.part.qty!=""){
    this.masterData.part.noofbins=this.masterData.part.qty/this.masterData.part.binqty;
  }else{
    this.masterData.part.noofbins="";
  }
}

updateItems(){
  if(this.savedCardDetails.find((x:any)=>x.partId==this.masterData.part.partid)){
    this.toastr.info("Part already added ");
    return;
  }
  let partDetail:any={
    partId:this.masterData.part.partid,
    partNo:this.masterData.part.partno,
    partName:this.masterData.part.partdescription,
    customerPartCode:this.masterData.part.customerpartcode,
    spq:this.masterData.part.spq,
    binQty:this.masterData.part.noofbins,
    loadingType:this.masterData.part.loadingtype,
    noOfPackingInPallet:this.masterData.part.noofpackinginpallet,
    qty:this.masterData.part.qty,
    subLocationShortCode:this.masterData.part.sublocationshortcode
  }
  this.savedCardDetails.push(partDetail);
  this.masterData.part={
    partid: "0",
    partno:"",
    partdescription: "",
    customerpartcode: "",
    spq: "",
    binqty: 0,
    loadingtype: 0,
    noofpackinginpallet: 0,
    qty:0
   }

}


removeScannedValue(i:number){
if(this.savedCardDetails.length>i){
  let input={
    scanId:this.savedCardDetails[i].scanId,
    transId:this.masterData.transId
  }
 
  
}
}
clearData(){
let pipe = new DatePipe('en-US');
let gateDate:any=pipe.transform(new Date(),'yyyy-MM-dd');
let gateTime:any=pipe.transform(new Date(),'hh:mm');
this.masterData={
  transId :'',
  statusId:'',
  userId:'',
  totalCardScanned:0,
  asnId:0,
  asnNo:'',
  customerId:'', 
  supplyDate:gateDate,
  supplyTime:gateTime,
  unLoadingDocId:0,
  asnStatus:'2',
  vechicleNo:'',
  driverName:'',
  driverMobile:'',
  ewayBillNo:'',
  invoiceNo:'',
  directorTransit:true,
  transitasnId:0,
  gateInDateTime:'',
  cardsIssued:0,
  cardsAcknowledged:0,
  cardsDispatched:0,
  cardsReceived:0,
  locationName:'',
  subLocationShortCode:'',
  customerName:'',
  locationId:0,
  warehouseId:0,
  sublocationId:0,
  part:{
    partid: 0,
    partno:"",
    partdescription: "",
    customerpartcode: "",
    spq: "",
    binqty: 0,
    loadingtype: 0,
    noofpackinginpallet: 0,
    qty:0
   }
};
this.savedCardDetails=[];
  if(this.customerDetail && this.customerDetail.customerid){
    this.getCustomerParts(this.customerDetail.customerid);
  }
}
cancelAdd(){
 this.clearData();
 this.savedCardDetails=[];
 this.action="view";
 this.isDisabled=true;
 
} 
addNew(){
    this.clearData();
    this.getAjaxData('asncustomerdetail');
    this.action="add";
} 

get f() { return this.form.controls; }

IsNullorEmpty(value: any): boolean {
if (value == undefined || value == "") {
    return true;
}
return false;;
}

doAction(event: any){
  this.doActionPicklistId=event.picklistid;
  this.shorthaul.assignVehicleNo=null;
  this.openVehicleDlg();
  // let picked:any={};
  // picked.scheduleNo=event.picklistidetailid;
  // picked.statusId=event.selected==1?1:2;
  // picked.customerId=event.customerid;
  // this.service.addVehiclePickListPart(picked).subscribe({
  //   next: (rdata) => {
  //       if (rdata.isSuccess) {
  //           this.toastr.success(rdata.message);
  //           this.selectedreport.getReportData(true);
  //       } else {
  //           this.toastr.warning(rdata.message);
  //       }
  //   },    
  //   error: (err) => { this.toastr.error(err.message) },  
    
  //   })

}



vehicleAssign(vehiclepicklistid: any){
  
  if(this.IsNullorEmpty(this.shorthaul.assignVehicleNo) || this.shorthaul.assignVehicleNo=='' || this.shorthaul.assignVehicleNo==undefined){
    this.toastr.info("select vehicle");
    return;
  }

  let picked:any={};
  picked.pickListId=vehiclepicklistid;
  picked.vehicleNo=this.shorthaul.assignVehicleNo;
  picked.vehicleTransId=1;
  picked.userId=this.WmsGV.userId;
  this.service.addVehicleUpdate(picked).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.closeVehicleDlgModel();
            this.report.getReportData(true);
            this.selectedreport.getReportData(true);
        } else {
            this.toastr.warning(rdata.message);
        }
    },    
    error: (err) => { this.toastr.error(err.message) },  
    
    })
}


clearSelectedParts(){
  let picked:any={};
  picked.statusId=3;
  this.service.addVehiclePickListPart(picked).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.selectedreport.getReportData(true);
            this.report.getReportData(true);
        } else {
            this.toastr.warning(rdata.message);
        }
    },    
    error: (err) => { this.toastr.error(err.message) },  
    
    })
}
getASNMasterById(asnId: any): void {
  this.service.getPartRequestById(asnId).subscribe({
    next: (rdata) => {
        if (rdata.result) {
            this.clearData();    
            this.masterData.asnId=rdata.result.asnId;
            this.masterData.asnNo=rdata.result.asnNo;
            this.masterData.customerId=rdata.result.customerId;
            this.masterData.supplyDate=rdata.result.supplyDate;
            this.masterData.supplyTime=rdata.result.supplyTime;
            this.masterData.unLoadingDocId=rdata.result.unLoadingDocId;
            this.masterData.asnStatus=rdata.result.asnStatus;
            this.masterData.vechicleNo=rdata.result.vechicleNo;
            this.masterData.driverName=rdata.result.driverName;
            this.masterData.driverMobile=rdata.result.driverMobile;
            this.masterData.ewayBillNo=rdata.result.ewayBillNo;
            this.masterData.invoiceNo=rdata.result.invoiceNo;
            this.masterData.directorTransit=rdata.result.directorTransit;
            this.masterData.transitasnId=rdata.result.transitasnId;
            this.masterData.gateInDateTime="";
            this.masterData.userId=rdata.result.userId;
            this.masterData.returnPack=rdata.result.returnPack;
            this.masterData.returnPackQty=rdata.result.returnPackQty;
            this.masterData.locationName=rdata.result.locationShortName;
            this.masterData.subLocationShortCode=rdata.result.sublocationShortName;
            this.masterData.customerName=rdata.result.customerName;
            this.masterData.locationId=rdata.result.loactionId;
            this.masterData.subLocationId=rdata.result.subloactionId;
            this.masterData.warehouseId=rdata.result.warehouseId;
            this.masterData.transitLocationId=rdata.result.transitlocationId;
            this.masterData.dockName=rdata.result.dockName;
            this.savedCardDetails=rdata.result.asnDetail;
            if(this.masterData.asnStatus==2 && this.savedCardDetails.length==0){
              this.enableAdd=true;
            }
            this.getCustomerParts(this.masterData.customerId);
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    

  });
}

updatePickLists(): void {
  let data={
    pickListId:0
  }
  //Role Based

  this.service.addPcikLists(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.selectedreport.getReportData(true);
            this.report.getReportData(true);
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
}



updateGateEntry(data:any): void {
 
  this.service.updateGateEntryStatus(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            // this.action="Edit";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
}

moveParts():void{
//todo
}

fileChangeEvent(fileInput: any) {
  let allowedFileTypes='log,txt';
  if (fileInput.target!.files && fileInput.target.files[0]) {
      let ext=fileInput.target.files[0].name.split('.').pop();
      // Size Filter Bytes
      const max_size = 1000*1024;
      let allowed_types:any=[];
      let ty=allowedFileTypes.split(',');
      if(ty.indexOf(ext)==-1){
      this.toastr.error("Upload valid file format");
      return;
      }
      this.service.upload(fileInput.target.files[0]).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        })
      
  }
      
}
}
