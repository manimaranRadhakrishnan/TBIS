import { Component,Input,ViewChild, ElementRef  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Observable, Subject, scan } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { GateEntryAjax } from '../../../models/transaction';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-partrequest',
  standalone: false,
  templateUrl: './partrequest.component.html',
  styleUrl: './partrequest.component.css'
})
export class PartrequestComponent {
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
    @ViewChild('scannedValueCtl') scanFocusElement!: ElementRef;
    isDisabled:boolean=false;
    customers!:any[];
    
   
  eventsSubject: Subject<void> = new Subject<void>();
  overlay: boolean=false;

constructor(private confirmDialogService: ConfirmDialogService,
  private WmsGV: AppGlobal,
  private service:TransactionService, 
  private toastr:ToastrService) { }


  ngAfterViewInit() {
    if(!this.masterObj){
      this.masterData={    
       transId :0,
       asnId:0,
       vechicleNo :'',
       driverName:'',
       driverMobile:'',
       asnStatus:0,
       statusId:'',
       userId:'',
       totalCardScanned:0,
       returnPack:'0',
       returnPackQty:0,
       transitType:1
    }
  }
}

confirmDialog(message: any, showyesno: any): any {
  if (!showyesno) {
      this.confirmDialogService.showMessage(message, () => { });
  } else {
      this.confirmDialogService.confirmThis("confirm",message, () => {
          //alert('Yes clicked');
      }, () => {
          //alert('No clicked');
      });
  }  
}

getAjaxData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.customers=rdata.data;
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })

}

ngOnInit(): void {
   this.getGateEntryData('vechicle');
   this.getAjaxData('customer');
}

setFocus() {
  if(this.scanFocusElement !=undefined){
    setTimeout(() => this.scanFocusElement.nativeElement.focus(), 0);
  }
}

getGateEntryData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.vehicleDet=[];
              // this.vehicleDet=rdata.data;
              rdata.data.forEach((element:any) => {
                  this.vehicleDet.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}
loadDetails(value:string){
  let vh:any=this.gateEntry.find((x:any)=>x.transid==value);
  this.masterData.driverName=vh.drivername;
  this.masterData.driverMobile=vh.drivermobile;
  this.getScannedPartsByTransId();
}
parseAndAddCard(e:any){
  if((e.ctrlKey || e.metaKey) && (e.keyCode == 68 || e.keyCode == 74 || e.keyCode == 77 || e.keyCode == 109 || e.keyCode == 106 || e.keyCode == 98)){
    //do nothing
  }else if(e.key!='Enter' && e.key!='Tab'){
    return;
  }  
  e.preventDefault();
  this.scannedValue=this.scannedValue.replace("\t","");
  let len:number=this.scannedValue.length;
  if(this.savedCardDetails.find((x:any)=>x.scanData==this.scannedValue)){
    this.scannedValue="";
    this.setFocus();
    this.toastr.info("Card already scanned ");
    return;
  }
  if(len==68 || len==76){
    let parsedData:any={
      scanData:"",
    partNo:"",
    vendorCode:"",
    userLocation:"",
    qty:"",
    serialNo:"",
    transId:0
    }
    parsedData.scanData=this.scannedValue;
    if(len==68){
      parsedData.partNo=this.scannedValue.substring(11,25);
      parsedData.vendorCode=this.scannedValue.substring(26,30);
      parsedData. userLocation=this.scannedValue.substring(32,36);
      parsedData. qty=this.scannedValue.substring(37,43);
      parsedData.serialNo=this.scannedValue.substring(64,68);
      parsedData.transId=this.masterData.transId;
    }else if(len==76){
      parsedData.partNo=this.scannedValue.substring(14,28);
      parsedData.vendorCode=this.scannedValue.substring(30,34);
      parsedData.userLocation=this.scannedValue.substring(37,41);
      parsedData.qty=this.scannedValue.substring(43,49);
      parsedData.serialNo=this.scannedValue.substring(72,76);
      parsedData.transId=this.masterData.transId;
    }
    
    
  this.service.updateVechicleEntryCardScan(parsedData).subscribe(
   
    (    rdata: { isSuccess: any; message: string;result:any }) => {
          if (rdata.isSuccess) {
              this.savedCardDetails=rdata.result;
              this.masterData.totalCardScanned=this.savedCardDetails.length;
          } else {
              this.confirmDialogService.showMessage(rdata.message, () => {});
          }
      },
    (    err: { message: any; }) => {
          this.confirmDialog(err.message, false);
           
      }
  );
   
   
  }else{
    this.confirmDialog("Invalid code. Try again with correct label ", false);
    this.scannedValue="";
    this.setFocus();
    
  }
  this.scannedValue="";
  this.setFocus();
}
removeScannedValue(i:number){
  if(this.savedCardDetails.length>i){
    let input={
      scanId:this.savedCardDetails[i].scanId,
      transId:this.masterData.transId
    }
    this.service.deleteVechicleEntryCardScan(input).subscribe(
   
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.savedCardDetails=rdata.result;
                this.masterData.totalCardScanned=this.savedCardDetails.length;
            } else {
                this.confirmDialogService.showMessage(rdata.message, () => {});
            }
        },
      (    err: { message: any; }) => {
            this.confirmDialog(err.message, false);
             
        }
    );
    
  }
}
clearData(){
  this.masterData={
    transId :'',
    statusId:'',
    userId:'',
    totalCardScanned:0,
    asnId:0,
    asnNo:'',
    customerId:'', 
    supplyDate:'',
    supplyTime:'',
    unLoadingDocId:'',
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
  };
 }
  cancelAdd(){
   this.clearData();
   this.action="view";
   this.isDisabled=true;
   
  } 
  addNew(){
      this.clearData();
      this.action="add";
      this.isDisabled=false;
  } 

  get f() { return this.form.controls; }

  IsNullorEmpty(value: any): boolean {
  if (value == undefined || value == "") {
      return true;
  }
  return false;;
  }

doAction(event: any){

  if(event){
    if(event.asnstatus.toString()=='2'){
        this.action="Edit";
        this.getASNMasterById(event.asnid.toString());
    } else{
        this.action="show";
        this.getAsnInfotById(event.asnid.toString());
    }
  }
}

getAsnInfotById(asnId: any): void {
  this.service.getAsnInfotById(asnId).subscribe(
    rdata => {
          if (rdata.result) {
              this.masterData={};         
              this.masterData.asnId=rdata.result.asnId;
              this.masterData.asnNo=rdata.result.asnNo;
              this.masterData.customerId=rdata.result.customerId;
              this.masterData.vechicleNo=rdata.result.vechicleNo;
              this.masterData.driverName=rdata.result.driverName;
              this.masterData.driverMobile=rdata.result.driverMobile;
              this.masterData.userId=rdata.result.userId;
              this.masterData.cardsIssued=rdata.result.cardsIssued;
              this.masterData.cardsAcknowledged=rdata.result.cardsAcknowledged;
              this.masterData.returnPack=rdata.result.returnPack;
              this.masterData.returnPackQty=rdata.result.returnPackQty;
              
              
              this.savedCardDetails=rdata.result.asnDetail; 
              
              
              this.action="show"
          } else {
              this.confirmDialog(rdata.message, false);     
              this.scannedValue="";                      
          }

      },
    (        err: { message: any; }) => {
          this.confirmDialog(err.message, false);
          
      }
  );
}
getASNMasterById(asnId: any): void {
  this.service.getPartRequestById(asnId).subscribe(
    rdata => {
          if (rdata.result) {
              this.masterData={};         
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
              if(rdata.result.gateInDateTime.split(' ').length>1){
                this.masterData.gateInDateTime=rdata.result.gateInDateTime.split(' ')[1];
              }else{
                this.masterData.gateInDateTime="";
              }
              this.masterData.userId=rdata.result.userId;
              this.masterData.returnPack=rdata.result.returnPack;
              this.masterData.returnPackQty=rdata.result.returnPackQty;
             
          } else {
              this.confirmDialog(rdata.message, false);                
          }

      },
    (        err: { message: any; }) => {
          this.confirmDialog(err.message, false);
          
      }
  );
}
getScannedPartsByTransId(){
  this.service.getScanDetailsById(this.masterData.transId).subscribe((rdata:any) => {
    if (rdata.result) {
        this.savedCardDetails=rdata.result;
        this.activeTab='scan';
    } else {
        this.confirmDialog(rdata.message, false);                
    }
  });

}
updateScanDetails(): void {


  if (this.savedCardDetails.length==0) {
    this.confirmDialog("Please scan the cards", false);
    this.setFocus();
    return;
  }
  
  this.service.getScanDetailsById(this.masterData.transId).subscribe((rdata:any) => {
    if (rdata.result) {   
    this.savedCardDetails=rdata.result;
    this.activeTab='asn';
    } else {
        this.confirmDialog(rdata.message, false);                
    }
    });
  }

getTransId(vno:string){
  let vh:any=this.vehicleDet.find((x:any)=>x.lable==vno);
  if(vh!=undefined && vh.transid!=undefined && Number(vh.transid)>0){
    this.masterData.transId=Number(vh.transid);
  this.service.getVechicleEntryById(vh.transid).subscribe(
    (    rdata: { isSuccess: any; message: string;result:any }) => {
            this.savedCardDetails= rdata.result.scanDetails;          
      },
    (    err: { message: any; }) => {
          this.toastr.error(err.message);
           
      }
  );
    }else{
      this.masterData.transId=0;
    }
}
updateVechicleEntry(): void {

    if (this.IsNullorEmpty(this.masterData.vechicleNo)) {
        this.toastr.info("Please enter Vechicle No");
        return;
    }
    
    // if (this.IsNullorEmpty(this.masterData.driverName)) {
    //   this.toastr.info("Please enter Contact Email");
    //   return;
    // }
    // if (this.IsNullorEmpty(this.masterData.driverMobile)) {
    //   this.toastr.info("Please enter Contact Phone");
    //   return;
    // }
    
      this.masterData.driverName="Driver";
      this.masterData.driverMobile="900000000";

      let data:any={};
       data.transId=this.masterData.transId==""?"0":this.masterData.transId;
       data.vechicleNo=this.masterData.vechicleNo;
       data.driverName=this.masterData.driverName;
       data.driverMobile=this.masterData.driverMobile;
       data.statusId=1;
       data.userId=this.WmsGV.userId;
       data.returnPack=this.masterData.returnPack,
       data.returnPackQty=this.masterData.returnPackQty,

    this.service.updateVechicleEntry(data).subscribe(
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                // this.toastr.success(rdata.message);
                this.masterData.transId= rdata.result.transId
                this.getGateEntryData('vechicle');
                // this.cancelAdd();
                // this.action="view";
            } else {
                this.toastr.warning(rdata.message);
            }
        },
      (    err: { message: any; }) => {
            this.toastr.error(err.message);
             
        }
    );
  }

updateAsn(): void {


    if (this.savedCardDetails.length==0 && this.masterData.asnStatus!=4) {
      this.confirmDialog("Please scan the cards", false);
      return;
    }

    if ((this.masterData.customerId==0 ||this.masterData.customerId=='' || this.masterData.customerId==undefined) && this.masterData.asnStatus==4) {
      this.confirmDialog("Please select the vendor", false);
      return;
    }

    for(let s of this.savedCardDetails){
      if(s.partId==0){
        this.confirmDialog("Part not available ["+s.partNo+"]", false);
        return;
      }
      if(s.vendorId==0){
        this.confirmDialog("Vendor not available ["+s.partNo+"]", false);
        return;
      }
      if(s.customerPartId==0){
        this.confirmDialog("Part not mapped for the Vendor ["+s.partNo+"]", false);
        return;
      }

    }
    let input={
      transId:this.masterData.transId,
      asnStatus: this.masterData.asnStatus,
      vendorId:(this.masterData.customerId==undefined?0:this.masterData.customerId)
    }
    this.service.updateAsn(input).subscribe(
     
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.getGateEntryData('vechicle');
                this.confirmDialogService.showMessage(rdata.message, () => {});
                this.action="view";
            } else {
                this.confirmDialogService.showMessage(rdata.message, () => {});
            }
        },
      (    err: { message: any; }) => {
            this.confirmDialog(err.message, false);
             
        }
    );
    }
    
}
