import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { GateEntryAjax } from '../../../models/transaction';
import { DatePipe } from '@angular/common';
import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-picklistentry',
  standalone: false,
  templateUrl: './picklistentry.component.html',
  styleUrl: './picklistentry.component.css'
})
export class PicklistentryComponent implements OnInit,AfterViewInit{

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
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
  rptParam:string="2"
  myDate = new Date();
  enableAdd:boolean=false;
  plants:any=[];
  plantDocks:any=[];
  customerDetails:any=[];
  showTab:number=1;
  showUploadDlg=false;
  showUploadStatusDlg=false;
  stepvalue:string="1";
  pwarehouseId:number=1;
  plocationId:number=1;
  psubloactionId:number=1;
  employeeDetails:any=[];
  parentPickListId:string='';

  showUploadSpareDlg=false;
  enableAmend:boolean=false;
  
  @ViewChild('report', { static: false })
  report!: ReportComponent;   

  eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;

constructor(
private WmsGV: AppGlobal,
private service:TransactionService, 
private toastr:ToastrService) { }

ngOnInit(): void {
  this.userRoleId=this.WmsGV.roleId;
  this.clearData();
  this.getAjaxPlants('plants');
  this.getAjaxPlantDocks('docks');
  this.getAjaxCustomers('customer');
  this.getAjaxEmployee('employee');
}



ngAfterViewInit() {
  
  if(!this.masterObj){
    this.masterData={    
     pickListId:0,
     scheduleNo :'',
     supplyDate:'',
     supplyTime:'',
     statusId:'',
     userId:'',
     unloadingDoc:0,
     usageLocation:0,
     processStatusId:0,
     pickedById:0,
     stagedById:0,
     kanbanAttachedById:0,
     kanbanTapingById:0,
     scanById:0,
     loadingSupervisorById:0,
     docHandoverById:0,
     docAuditById:0,
     vehicleAssignedById:0,
     parentPickListId:0,
     part:{
      partid: "0",
      partno:"",
      partdescription: "",
      customerpartcode: "",
      repackqty: "",
      obinqty: 0,
      loadingtype: 0,
      onoofpackinginpallet: 0,
      qty:0,
      kanbanNo:''
     }
  }
  }
  
}

openDlg() {
  this.showUploadDlg=true;
}
closeUploadModel() {
  this.showUploadDlg=false;
}

openStatusDlg() {
  this.showUploadStatusDlg=true;
}
closeUploadStatusModel() {
  this.showUploadStatusDlg=false;
}

getAjaxEmployee(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.employeeDetails=rdata.data;
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
                this.masterData.unloadingDocId=this.customerDetail.udc_id;
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
                  repackqty: "",
                  obinqty: 0,
                  loadingtype: 0,
                  onoofpackinginpallet: 0,
                  qty:0,
                  kanbanNo:''
                 };
                this.getCustomerParts(this.customerDetail.customerid);
              }
  
             
          }
       },
      error: (e) => console.error(e),
  })
  
  }
  getAjaxCustomers(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.customerDetails=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }

getAjaxPlants(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.plants=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })

}

getAjaxPlantDocks(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.plantDocks=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })

}


getCustomerParts(customerId:number):void{
  this.customerPartDetails=[];
  let contactsparam='picklistshowparts&customerid='+customerId;
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
  this.masterData.part.qty=this.masterData.part.repackqty;
  this.masterData.part.noofbins=this.masterData.part.repackqty/this.masterData.part.obinqty;
  
  this.stepvalue=this.masterData.part.qty;
}
onChangeQuantity(event:any):void{
  if(this.masterData.part.qty!=""){
    this.masterData.part.noofbins=this.masterData.part.qty/this.masterData.part.obinqty;
  }else{
    this.masterData.part.noofbins="";
  }
}
onChangeAmendQuantity(i:number):void{
  if(this.savedCardDetails[i].qty!=""){
    this.savedCardDetails[i].binQty=this.savedCardDetails[i].qty/(this.customerPartDetails.find((x:any)=>x.partid==this.savedCardDetails[i].partId)).obinqty;
  }else{
    this.savedCardDetails[i].binQty="";
  }
}
shouldDisableButton(i:number) {
  let uLocAmend = this.savedCardDetails[i].usageLocation;
  if (uLocAmend){
    return true;
  }else{
    return false;
  }
}

updateItems(){
  if(this.masterData.part==undefined || this.masterData.part.partno==undefined || this.masterData.part.partno==''){
    this.toastr.info("Select the part");
    return;
  }
  if(this.masterData.part==undefined || this.masterData.part.partno==undefined || this.masterData.part.partno==''){
    this.toastr.info("Select the part");
    return;
  }
  if(this.masterData.part.qty==0 || this.masterData.part.qty=='' || this.masterData.part.qty==undefined){
    this.toastr.info("Enter the Qty");
    return;
  }
  if((this.masterData.part.qty%this.masterData.part.repackqty)>0){
    this.toastr.info("Qty must be in multiples of SPQ");
    return;
  }
  if(this.IsNullorEmpty(this.masterData.part.kanbanNo) || this.masterData.part.kanbanNo=='' || this.masterData.part.kanbanNo==undefined){
    this.toastr.info("Enter the Kanban");
    return;
  }
  if(this.IsNullorEmpty(this.masterData.part.unLoc) || this.masterData.part.unLoc=='' || this.masterData.part.unLoc==undefined){
    this.toastr.info("Enter the unload dock");
    return;
  }
  if(this.IsNullorEmpty(this.masterData.part.usLoc) || this.masterData.part.usLoc=='' || this.masterData.part.usLoc==undefined){
    this.toastr.info("Enter the usage location");
    return;
  }
  let partDetail:any={
    partId:this.masterData.part.partid,
    partNo:this.masterData.part.partno,
    partName:this.masterData.part.partdescription,
    customerPartCode:this.masterData.part.customerpartcode,
    spq:this.masterData.part.repackqty,
    binQty:this.masterData.part.noofbins,
    qty:this.masterData.part.qty,
    kanbanNo:this.masterData.part.kanbanNo==undefined?"":this.masterData.part.kanbanNo,
    unLoadingDoc:this.masterData.part.unLoc==undefined?"":this.masterData.part.unLoc,
    usageLocation:this.masterData.part.usLoc,
  }
  this.savedCardDetails.push(partDetail);
  this.masterData.part={
    partid: "0",
    partno:"",
    partdescription: "",
    customerpartcode: "",
    repackqty: "",
    obinqty: 0,
    loadingtype: 0,
    noofpackinginpallet: 0,
    qty:0,
    kanbanNo:'',
    unLoc:'',
    usLoc:'',
   }

}


removeScannedValue(i:number){
if(this.savedCardDetails.length>i){
  let input={
    scanId:this.savedCardDetails[i].scanId,
    transId:this.masterData.transId
  }
  this.savedCardDetails.splice(i,1);
}

}

clearData(){
let pipe = new DatePipe('en-US');
let gateDate:any=pipe.transform(new Date(),'yyyy-MM-dd');
let gateTime:any=pipe.transform(new Date(),'hh:mm');
this.stepvalue="1";
this.enableAmend=false;
this.customerPartDetails=[];
this.masterData={
  pickListId:0,
  scheduleNo :'',
  supplyDate:'',
  supplyTime:'',
  statusId:'',
  userId:'',
  unloadingDoc:0,
  usageLocation:0,
  processStatusId:0,
  pickedById:0,
  stagedById:0,
  kanbanAttachedById:0,
  kanbanTapingById:0,
  scanById:0,
  loadingSupervisorById:0,
  docHandoverById:0,
  docAuditById:0,
  vehicleAssignedById:0,
  parentPickListId:0,
  part:{
   partid: "0",
   partno:"",
   partdescription: "",
   customerpartcode: "",
   repackqty: "",
   obinqty: 0,
   loadingtype: 0,
   onoofpackinginpallet: 0,
   qty:0,
   kanbanNo:''
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
  this.clearData();
  if(event){
      this.action="show";
      this.getPickListMasterById(event.picklistid.toString());
  }
}
updateAmendPickList(pickListId: any){
  this.service.updateAmendPickList(pickListId).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
          this.toastr.success(rdata.message);
          let amdata={
            picklistid: pickListId
          }
          this.doAction(amdata);
        }else {
          this.toastr.warning(rdata.message);                
        }
    }, 
    error: (err) => { this.toastr.warning(err);  },    
  });
}

getPickListMasterById(pickListId: any): void {
  this.service.getPickListById(pickListId).subscribe({
    next: (rdata) => {
        if (rdata.result) {
            this.clearData();    
            this.masterData.pickListId=rdata.result.pickListId;
            this.masterData.scheduleNo=rdata.result.scheduleNo;
            this.masterData.customerId=rdata.result.customerId+"";
            this.masterData.supplyDate=rdata.result.supplyDate;
            this.masterData.supplyTime=rdata.result.supplyTime;
            this.masterData.unloadingDoc=rdata.result.unloadingDoc+"";
            this.masterData.usageLocation=rdata.result.usageLocation+"";
            this.masterData.statusId=rdata.result.statusId;
            this.masterData.userId=rdata.result.userId;

            
            this.masterData.vehicleNo=rdata.result.vehicleNo;

            // this.masterData.pickedById=rdata.result.pickedById;
            this.masterData.pickedById=rdata.result.pickedById==0?null:rdata.result.pickedById+'';
            this.masterData.pickedByName=rdata.result.pickedByName;

            this.masterData.stagedById=rdata.result.stagedById==0?null:rdata.result.stagedById+'';
            this.masterData.stagedByName=rdata.result.stagedByName;

            this.masterData.kanbanAttachedById=rdata.result.kanbanAttachedById==0?null:rdata.result.kanbanAttachedById+'';
            this.masterData.kanbanAttachedByName=rdata.result.kanbanAttachedByName;

            this.masterData.kanbanTapingById=rdata.result.kanbanTapingById+'';
            this.masterData.kanbanTapingByName=rdata.result.kanbanTapingByName;

            this.masterData.scanById=rdata.result.scanById+'';
            this.masterData.scanByName=rdata.result.scanByName;

            this.masterData.loadingSupervisorById=rdata.result.loadingSupervisorById+'';
            this.masterData.loadingSupervisorByName=rdata.result.loadingSupervisorByName;

            this.masterData.docHandoverById=rdata.result.docHandoverById+'';
            this.masterData.docHandoverByName=rdata.result.docHandoverByName;

            this.masterData.docAuditById=rdata.result.docAuditById+'';
            this.masterData.docAuditByName=rdata.result.docAuditByName;

            this.masterData.vehicleAssignedById=rdata.result.vehicleAssignedById+'';
            this.masterData.vehicleAssignedByName=rdata.result.vehicleAssignedByName;

            this.savedCardDetails=rdata.result.pickListDetail;
            this.showTab=1;
            this.masterData.processStatusId=rdata.result.processStatusId;
            this.masterData.parentPickListId=rdata.result.parentPickListId;
            this.getCustomerParts(this.masterData.customerId);

            if(this.masterData.statusId==25){
              this.enableAmend=true;
            }
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    

  });
}

updateAsn(): void {

  if (this.IsNullorEmpty(this.masterData.scheduleNo)) {
    this.toastr.info("Schedule No required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.supplyDate)) {
    this.toastr.info("Supply Date required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.supplyTime)) {
    this.toastr.info("Supply Time required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.usageLocation)) {
    this.toastr.info("Plant required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.customerId)) {
    this.toastr.info("Vendor required !");
    return;
  }
  if (this.IsNullorEmpty(this.savedCardDetails)) {
    this.toastr.info("Parts required !");
    return;
  }

  if(this.enableAmend){
    this.masterData.parentPickListId = this.masterData.pickListId;
    this.masterData.pickListId="";
  }

  let data={
    pickListId:this.masterData.pickListId==""?"0":this.masterData.pickListId,
    parentPickListId:this.masterData.parentPickListId==0?"0":this.masterData.parentPickListId,
    scheduleNo:this.masterData.scheduleNo,
    customerId:this.masterData.customerId,
    supplyDate:this.masterData.supplyDate,
    supplyTime:this.masterData.supplyTime,
    unloadingDoc:this.masterData.unloadingDoc,
    usageLocation:this.masterData.usageLocation,
    subLocationId:this.psubloactionId,
    locationId:this.plocationId,
    warehouseId:this.pwarehouseId,

    // processStatusId:this.masterData.processStatusId,
    // pickedById:this.masterData.pickedById,
    // stagedById:this.masterData.stagedById,
    // kanbanAttachedById:this.masterData.kanbanAttachedById,
    // kanbanTapingById:this.masterData.kanbanTapingById,
    // scanById:this.masterData.scanById,
    // loadingSupervisorById:this.masterData.loadingSupervisorById,
    // docHandoverById:this.masterData.docHandoverById,
    // docAuditById:this.masterData.docAuditById,
    // vehicleAssignedById:this.masterData.vehicleAssignedById,

	  userId:0,
    statusId:11,
    pickListDetail:this.savedCardDetails
  }


  // alert(JSON.stringify(data));
  //Role Based
  this.service.updatePickListEntry(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            let edata={
              picklistid: rdata.result
            }
            this.doAction(edata);
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
  this.showTab=2;
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
                this.closeUploadModel();
                this.report.getReportData(true);
            } else {
                this.toastr.warning(rdata.message);
                this.closeUploadModel();
                this.report.getReportData(true);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        })
      
  }
      
}

onChangeProcessId(statusId:number){
  this.masterData.processStatusId=statusId;
}

updateProcessStatus(): void {
  let data={
    pickListId:this.masterData.pickListId,
    processStatusId:this.masterData.processStatusId,
    pickedById:this.masterData.pickedById==null?0:this.masterData.pickedById,
    stagedById:this.masterData.stagedById==null?0:this.masterData.stagedById,
    kanbanAttachedById:this.masterData.kanbanAttachedById==null?0:this.masterData.kanbanAttachedById,
    // statusId:11,
    // kanbanTapingById:this.masterData.kanbanTapingById,
    // scanById:this.masterData.scanById,
    // loadingSupervisorById:this.masterData.loadingSupervisorById,
    // docHandoverById:this.masterData.docHandoverById,
    // docAuditById:this.masterData.docAuditById,
    // vehicleAssignedById:this.masterData.vehicleAssignedById,
  }
  // alert(JSON.stringify(data));
  //Role Based
  this.service.updatePickListStatus(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.closeUploadStatusModel();
            let edata={
              picklistid: data.pickListId
            }
            this.doAction(edata);
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
}

// spares codes 

openSpareDlg() {
  this.showUploadSpareDlg=true;
}
closeUploadSpareModel() {
  this.showUploadSpareDlg=false;
}

SparefileChangeEvent(fileInput: any) {
  let allowedFileTypes='log,txt,xlsx';
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
      this.service.spareUpload(fileInput.target.files[0]).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closeUploadSpareModel();
                this.report.getReportData(true);
            } else {
                this.toastr.warning(rdata.message);
                this.closeUploadSpareModel();
                this.report.getReportData(true);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
      })
  } 
}


}

