import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { GateEntryAjax } from '../../../models/transaction';
import { DatePipe } from '@angular/common';
import { toNumber } from 'lodash';

@Component({
  selector: 'app-asnmovement',
  standalone: false,
  templateUrl: './asnmovement.component.html',
  styleUrl: './asnmovement.component.css'
  
})
export class AsnMovementComponent implements OnInit,AfterViewInit{
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
  asnBintagDetails:any=[];
  transitLocations:any=[];
  rptParam:string="10"
  myDate = new Date();
  enableAdd:boolean=false;
  stepvalue:string="1";
  showTab:number=1;
eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;
  totalVehicleSpace:number=0;
  vehicleHeight:number=0;
  totalVehicleSpaceOccupied=0;
  totalPallets:number=0;
  clubItemPalletRemainingSpace=0;
  clubItemBalance:number=0;
  totalVehicleWeight:number=0;
  totalvehicleWeightOccupied:number=0;
  currentVehicleSpace:number=0;
  currentVehicleWeight:number=0;
  selectedPartDetail:any={};
  spaceAllocationDetails:any=[];
  palletSpaceAllocationDetails:any=[];

constructor(
private WmsGV: AppGlobal,
private service:TransactionService, 
private toastr:ToastrService) { }

ngOnInit(): void {
  this.userRoleId=this.WmsGV.roleId;
  this.clearData();
  this.getAjaxWareHouse('warehouse');
  this.getTransporter();

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
      qty:0,
      packageId:1,
      packingShortName:"BIN",
      pallets:0,
      remainingPalletBins:0
     },
     vehicleInfo:{
      vehiclesize:"",
      vehiclecapacity:"",
      licencexpiry:"",
      rcexpiry:"",
      fitnessexpiry:""
     }
  }
  }
  
}

getTransporter():void{
  this.service.getAjaxDropDown("lgtransporter").subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.vehicleDet=rdata.data;
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
                this.masterData.deliveryNoteNo='';
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
                  qty:0,
                  packageId:1,
                  packingShortName:"BIN",
                  remainingPalletBins:0,
                  pallets:0
                 };
                 this.masterData.vehicleInfo={
                  vehiclesize:"",
                  vehiclecapacity:"",
                  licencexpiry:"",
                  rcexpiry:"",
                  fitnessexpiry:""
                 };
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
  });
 
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
              this.masterData.deliveryNoteNo='';
              this.masterData.asnStatus='';
              this.masterData.asnid=0;
              this.masterData.primaryDcokid=this.customerDetail.udc_id;
              this.masterData.parts=[];
              this.masterData.vehicleInfo=[];
            }

           
        }
     },
    error: (e) => console.error(e),
})

}

clearGrid(){

  this.masterData.part.qty="";
  this.masterData.part.packingShortName="";
  this.masterData.part.noofbins="";
  this.masterData.part.packageId="";
  this.stepvalue="";
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
  deliveryNoteNo:'',
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
  primaryDcokid:0,
  part:{
    partid: 0,
    partno:"",
    partdescription: "",
    customerpartcode: "",
    spq: "",
    binqty: 0,
    loadingtype: 0,
    noofpackinginpallet: 0,
    qty:0,
    packageId:1,
    packingShortName:'BIN'
   },
   vehicleInfo:{
    vehiclesize:"",
    vehiclecapacity:"",
    licencexpiry:"",
    rcexpiry:"",
    fitnessexpiry:""
   }


};
this.savedCardDetails=[];
  if(this.customerDetail && this.customerDetail.customerid){
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
    if (this.WmsGV.roleId=='5'){  // Stores
      if(event.asnstatus.toString()=='10'){
        if(event.asnstatus.toString()=='10'){
          this.action="Edit";
          this.getASNMasterById(event.asnid.toString());
        }else{
          this.action="show";
          this.getASNMasterById(event.asnid.toString());
        } 
      }
    } else{
        this.action="show";
        this.getASNMasterById(event.asnid.toString());
    }
  }

 
}

getASNBinTags(asnId: any): void {
  this.service.getASNBinTags(asnId).subscribe({
    next: (rdata) => {
      if (rdata) {
          this.asnBintagDetails=rdata;
      }
   },
  error: (err) => { this.toastr.warning(err);  },    

  });
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
            this.masterData.deliveryNoteNo=rdata.result.deliveryNoteNo;
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
            this.masterData.warehouseId=rdata.result.warehouseId+'';
            this.masterData.transitLocationId=rdata.result.transitlocationId+'';
            this.masterData.dockName=rdata.result.dockName;
            this.savedCardDetails=rdata.result.asnDetail;
            this.masterData.primaryDcokid=rdata.result.primaryDcokid;
            this.masterData.stockMovedFromDock=rdata.result.stockMovedFromDock;
            this.masterData.processId=rdata.result.toProcessId;
            if(this.masterData.asnStatus==10 && this.masterData.stockMovedFromDock==1){
               this.action="show";
            }
            this.getASNBinTags(asnId);
            this.showPalletSpaceAllocation();
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    

  });
}
showPalletSpaceAllocation():void{
    this.service.getAsnPalletPartStockMovementAllocation(this.masterData.asnId,7).subscribe({
      next: (rdata) => {
          if (rdata.result) {
            this.palletSpaceAllocationDetails=rdata.result.scannedPartDetails;
          } else {
            this.toastr.warning(rdata.status);                
        }
    }, 
    error: (err) => { this.toastr.warning(err);  },    
  
    });
}
showSpaceAllocation(index:number):void{
  if(index>=0 && index<this.savedCardDetails.length){
    this.selectedPartDetail=this.savedCardDetails[index];
    this.showTab=3;
    // alert(JSON.stringify(this.selectedPartDetail));
    this.service.getAsnPartStockMovementAllocation(this.selectedPartDetail.transId,this.selectedPartDetail.partId,this.masterData.processId).subscribe({
      next: (rdata) => {
          if (rdata.result) {
            this.spaceAllocationDetails=rdata.result.scannedPartDetails;
          } else {
            this.toastr.warning(rdata.status);                
        }
    }, 
    error: (err) => { this.toastr.warning(err);  },    
  
    });
    }else{
    this.selectedPartDetail={};
  }
}
updateAsn(): void {
  let data={
    asnId:this.masterData.asnId==""?"0":this.masterData.asnId,
    asnNo:this.masterData.asnNo,
    customerId:this.masterData.customerId,
    supplyDate:this.masterData.supplyDate,
    supplyTime:this.masterData.supplyTime,
    unLoadingDocId:this.masterData.unLoadingDocId,
    subloactionId:this.masterData.subloactionId,
	  warehouseId:this.masterData.warehouseId,
	  loactionId:this.masterData.locationId,
	  transitlocationId:this.masterData.transitLocationId,
    asnStatus:this.masterData.asnStatus,
    vechicleNo:this.masterData.vechicleNo,
    driverName:this.masterData.driverName,
    driverMobile:this.masterData.driverMobile,
    ewayBillNo:this.masterData.ewayBillNo,
    invoiceNo:this.masterData.invoiceNo,
    deliveryNoteNo:this.masterData.deliveryNoteNo,
    directorTransit:this.masterData.directorTransit,
    transitasnId:0,
    primaryDcokid:this.masterData.primaryDcokid,
    gateInDateTime:'',
    userId:0,
    cardsIssued:0,
    cardsReceived:0,
    cardsDispatched:0,
    cardsAcknowledged:0,
    returnPack:'0',
    returnPackQty:0,
	  dockShortName:"",
	  sublocationShortName:"",
	  warehouseShortName:"",
	  locationShortName:"",
	  dockName:"",
	  customerName:"",
	  customerCode:"",
    asnDetail:this.savedCardDetails,
    toProcessId:this.masterData.processId
  }
  //Role Based
  if(this.masterData.processId=='7'){
    this.service.updateAsnPalletStockMovementEntry(data).subscribe({
      next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success("ASN Stock Movement Successful");
            this.action="view";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
  }else{
    this.service.updateAsnStockMovementEntry(data).subscribe({
      next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success("ASN Stock Movement Successful");
            this.action="view";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
  }
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


DownloadBarCode(data:any): void {
 
  this.service.getAsnBarcode(data).subscribe((response: any) => {
    this.downloadFile(response,'pdf');
  });
  
}

moveParts(tab:number):void{
     this.showTab=tab;

}

downloadFile(data: BlobPart,type:string) {
  //Download type xls

  let contentType = 'application/octet-stream';
  //Download type: CSV
  let contentType2 = 'text/csv';
  if(type=='pdf'){
    contentType="application/pdf";
  }
  const blob = new Blob([data], { type: contentType });
  const url = window.URL.createObjectURL(blob);
  //Open a new window to download
  // window.open(url); 

  //Download by dynamically creating a tag
  const a = document.createElement('a');
  const fileName = "report1";
  a.href = url;
  // a.download = fileName;
  if(type=='pdf'){
    a.download = fileName + '.pdf';
  }else{
    a.download = fileName + '.xlsx';
  }
  a.click();
  window.URL.revokeObjectURL(url);
}


}
