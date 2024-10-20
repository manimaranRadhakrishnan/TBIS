import { Component,Input,OnInit,AfterViewInit,ViewChild, ElementRef  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { InsidentDDL } from '../../../models/masters';

@Component({
  selector: 'app-ginentry',
  standalone: false,
  templateUrl: './ginentry.component.html',
  styleUrl: './ginentry.component.css'
})
export class GinentryComponent implements OnInit,AfterViewInit {

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  insidentData:any={};
  form: any = {};
  action:string="view";
  scannedValue:string='';
  scannedPartDetails:any=[];
  asnIncidentLogDetails:any=[];
  successfulScans:any=[];
  successfulPalletScans:any=[];
  insidents!:InsidentDDL[];
  savedCardDetails:any=[];
  abnormalDetails:any=[];
  rptParam:string="9";
  showInsidentDialog:boolean=false;
  showCheckAbnormalDialog:boolean=false;
  preview:string="";
  ginScannedPartDetails:any=[];
  @ViewChild('scannedValueCtl') scanFocusElement: ElementRef | undefined;

  eventsSubject: Subject<void> = new Subject<void>();
  constructor(private confirmDialogService: ConfirmDialogService,
    private WmsGV: AppGlobal,
    private service:TransactionService,
    private toastr:ToastrService) { }
    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.transId="";
        this.masterData.asnId="";
        this.masterData.asnNo="";
        this.masterData.customerId=''; 
        this.masterData.supplyDate="";
        this.masterData.supplyTime="";
        this.masterData.unLoadingDocId='';
        this.masterData.asnStatus='';
        this.masterData.vechicleNo="";
        this.masterData.driverName="";
        this.masterData.driverMobile="";
        this.masterData.ewayBillNo="";
        this.masterData.invoiceNo="";
        this.masterData.deliveryNoteNo="";
        this.masterData.directorTransit=true;
        this.masterData.transitasnId=0;
        this.masterData.gateInDateTime="";
        this.masterData.userId="";
        this.masterData.cardsIssued=0;
        this.masterData.cardsAcknowledged=0;
        this.masterData.cardsDispatched=0;
        this.masterData.cardsReceived=0;
        this.masterData.subLocationId=0;
        this.masterData.abnComments="";
    }

  }

  moveParts() {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.clearData();
    this.getInsidentMasterData('incident');
  }

  getInsidentMasterData(ajaxId:string):void{
      this.service.getAjaxDropDown(ajaxId).subscribe({
          next: (rdata) => {
              if (rdata.data) {
                  this.insidents=[];
                  rdata.data.forEach((element:any) => {
                      this.insidents.push(element);
                  });
              }
           },
          error: (e) => console.error(e),
          // complete: () => console.info('complete') 
      })
  
  }

  get f() { return this.form.controls; }
  IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
  }

  updateInsidentMaster(): void {
      if (this.IsNullorEmpty(this.insidentData.insidentId)) {
          this.toastr.info("Please Select Incident Name");
          return;
      }
      let data:any={};
       data.asnId=this.insidentData.asnId==""?"0":this.insidentData.asnId;
       data.asnId=this.masterData.asnId;
       data.insidentId=this.insidentData.insidentId;
       data.isActive=1;
    this.service.updateInsidentMaster(data).subscribe(
    
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closeInsidentModel();
                this.getASNMasterById(this.masterData.asnId.toString());
                // this.cancelAdd();
                this.action="Edit";
               // this.rpt.getReportData();
            } else {
                this.toastr.warning(rdata.message);
            }
        },
      (    err: { message: any; }) => {
            this.toastr.error(err.message);
             
        }
    );
  }
  clearAbnormalItems(){

    this.abnormalDetails={
      asnId:0,
      incidentId:0,
      asnIncidentLogId:0,
      scwComments:'',
      documentPath:'',
    },
    this.preview='';
  }

  clearData(){
    this.masterData={
      transId :'',
      statusId:'',
      userId:'',
      totalCardScanned:0,
      totalPalletScanned:0,
      asnId:'',
      asnNo:'',
      customerId:'', 
      supplyDate:'',
      supplyTime:'',
      unLoadingDocId:'',
      asnStatus:'',
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
    };
   }

  cancelAdd(){
    this.clearData();
    this.action="view";

   } 
   checkAbnormalPalletScan(){
    if(this.ginScannedPartDetails.length==0) {
      this.toastr.info("Please scan the cards");
      this.setFocus();
      return;
    }
    this.service.checkAbnormalPalletScan(this.masterData.asnId).subscribe(
     
      (rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.showCheckAbnormalDialog=true;
                // this.toastr.success(rdata.message)
            } else {
                this.updateAsn(true);
                // this.toastr.error(rdata.message)
            }
        },
      (    err: { message: any; }) => {
            this.toastr.error(err.message)
             
        }
    );
   }

  closeCheckAbnormalModel(){
    this.masterData.abnComments='';
    this.showCheckAbnormalDialog=false;
  }

  closeInsidentModel(){
    this.insidentData.insidentId='';
    this.showInsidentDialog=false;
    this.clearAbnormalItems();
  }
  
  showInsident(){
    this.showInsidentDialog=true;
  }

   setFocus() {
    if(this.scanFocusElement !=undefined){
      setTimeout(() => this.scanFocusElement!.nativeElement.focus(), 0);
    }
  }
  parseAndAddCard(e:any){
    if((e.ctrlKey || e.metaKey)  && (e.keyCode == 68 || e.keyCode == 74 || e.keyCode == 77 || e.keyCode == 109 || e.keyCode == 106 || e.keyCode == 98)){
      //do nothing
    }else if(e.key!='Enter' && e.key!='Tab'){
      return;
    }  
    e.preventDefault(); 

    if(!this.scannedValue.startsWith("PAL")){
      this.toastr.info("Please scan PALLET");  
      return;
    }

    this.scannedValue=this.scannedValue.replace("\t",""); 
    if(this.successfulScans.indexOf(this.scannedValue)!=-1){
      this.toastr.info("Card already scanned");
      return;
    }
    if(this.successfulPalletScans.indexOf(this.scannedValue)!=-1){
      this.toastr.info("Pallet already scanned");
      return;
    }
    this.service.getGinCardDetailByScanData(this.masterData.asnId,this.scannedValue).subscribe(
      rdata => {
            if (rdata.result) {
                if(rdata.result.isPallet){
                  if(rdata.result.partCount==1){
                    for(let i=0;i<rdata.result.scannedPartDetails.length;i++){
                      let partDetail=rdata.result.scannedPartDetails[i];
                    if(partDetail.transId!=undefined && partDetail.transId!=0){
                      if(this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber)){
                        this.scannedValue="";
                        let row=this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber);
                        let binqty:number=row.requestedBinQty==undefined?0:Number(row.requestedBinQty);
                        binqty++;
                        this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber).requestedBinQty=binqty;
                        let qty:number=(row.qty==undefined?0:Number(row.qty));
                        qty=qty+Number(row.binQty);
                        this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber).qty=qty;
                        }else{
                          partDetail.qty=partDetail.binQty;
                          partDetail.requestedBinQty="1";
                          partDetail.lineSpaceConfig=rdata.result.lineSpaceConfig;
                          partDetail.moveToOverflowLocation=rdata.result.moveToOverflowLocation;
                          this.scannedPartDetails.push(partDetail);   
                        }              
                      
                      }
                      this.successfulScans.push(partDetail.customerScanCode);
                      this.successfulScans.push(partDetail.scanData);
                      if(this.successfulPalletScans.indexOf(partDetail.palletScanCode)==-1){
                        this.successfulPalletScans.push(partDetail.palletScanCode);
                      }
                      this.masterData.totalCardScanned=this.successfulScans.length/2;   
                      this.masterData.totalPalletScanned=this.successfulPalletScans.length;
                      this.scannedValue="";          
                      this.setFocus();
                    }
                  }else{
                    for(let i=0;i<rdata.result.scannedPartDetails.length;i++){
                      let partDetail=rdata.result.scannedPartDetails[i];
                    // if(partDetail.transId!=undefined && partDetail.transId!=0){
                    //   if(this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNo==partDetail.palletNo)){
                    //     }else{
                    //       partDetail.requestedBinQty="0";
                    //       partDetail.qty=0;
                    //       this.scannedPartDetails.push(partDetail);   
                    //       if(this.successfulPalletScans.indexOf(partDetail.palletScanCode)==-1){
                    //         this.successfulPalletScans.push(partDetail.palletScanCode);
                    //       }    
                    //     }              
                      
                    //   }
                    if(partDetail.transId!=undefined && partDetail.transId!=0){
                      if(this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber)){
                        this.scannedValue="";
                        let row=this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber);
                        let binqty:number=row.requestedBinQty==undefined?0:Number(row.requestedBinQty);
                        binqty++;
                        this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber).requestedBinQty=binqty;
                        let qty:number=(row.qty==undefined?0:Number(row.qty));
                        qty=qty+Number(row.binQty);
                        this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber).qty=qty;
                        }else{
                          partDetail.qty=partDetail.binQty;
                          partDetail.requestedBinQty="1";
                          partDetail.lineSpaceConfig=rdata.result.lineSpaceConfig;
                          partDetail.moveToOverflowLocation=rdata.result.moveToOverflowLocation;
                          this.scannedPartDetails.push(partDetail);   
                        }              
                      
                      }
                      this.successfulScans.push(partDetail.customerScanCode);
                      this.successfulScans.push(partDetail.scanData);
                      if(this.successfulPalletScans.indexOf(partDetail.palletScanCode)==-1){
                        this.successfulPalletScans.push(partDetail.palletScanCode);
                      }
                      this.masterData.totalCardScanned=this.successfulScans.length/2;   
                      this.masterData.totalPalletScanned=this.successfulPalletScans.length;
                      this.scannedValue="";          
                      this.setFocus();
                  }
                  this.masterData.totalCardScanned=this.successfulScans.length/2;   
                  this.masterData.totalPalletScanned=this.successfulPalletScans.length;
                  this.scannedValue="";          
                  this.setFocus();
                }
                }else{
                  let partDetail:any={};
                  if(rdata.result.scannedPartDetails.length>0){
                      partDetail=rdata.result.scannedPartDetails[0];
                  }
                  if(partDetail.transId!=undefined && partDetail.transId!=0){
                    if(this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber)){
                      this.scannedValue="";
                      let row=this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber);
                      let binqty:number=row.requestedBinQty==undefined?0:Number(row.requestedBinQty);
                      binqty++;
                      this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber).requestedBinQty=binqty;
                      let qty:number=(row.qty==undefined?0:Number(row.qty));
                      qty=qty+Number(row.binQty);
                      this.scannedPartDetails.find((x:any)=>x.transId==partDetail.transId && x.palletNumber==partDetail.palletNumber).qty=qty;
                    }else{
                      this.scannedPartDetails.push(partDetail);   
                    }              
                    this.successfulScans.push(partDetail.customerScanCode);
                    this.successfulScans.push(partDetail.scanData);
                    if(this.successfulPalletScans.indexOf(partDetail.palletScanCode)==-1){
                      this.successfulPalletScans.push(partDetail.palletScanCode);
                    }
                    this.masterData.totalCardScanned=this.successfulScans.length/2;   
                    this.scannedValue="";          
                    this.setFocus();
                  }else{
                    this.setFocus();
                    this.toastr.info("Invalid code. Try again");      
                    this.scannedValue="";                       
                  }
              }
            } else {
                this.setFocus();
                this.toastr.info(rdata.message); 
                this.scannedValue="";                          
            }
  
        },
      (        err: { message: any; }) => {
            this.toastr.error(err.message);
            this.toastr.info();
        }
    );
  }
  getGinScannedDetail(){
    this.service.getGinScannedCardDetail(this.masterData.asnId).subscribe(
      rdata => {
            if (rdata.result) {
              this.ginScannedPartDetails=rdata.result.scannedPartDetails;
            } else {
                this.setFocus();
                // this.toastr.info(rdata.message); 
            }
  
        },
      (        err: { message: any; }) => {
            this.toastr.error(err.message);
            this.toastr.info();
        }
    );
  }
  doAction(event: any){

    if(event){
      if(event.asnstatus.toString()=='9'){
          this.action="Edit";
          this.getASNMasterById(event.asnid.toString());
      } else{
          this.action="show";
          this.getASNMasterById(event.asnid.toString());
      }
    }
    
  }

  getLists():void{
    this.getAsnIncidentLog();
    this.getGinScannedDetail();
  }
  
  getAsnIncidentLog():void{
    let inputparams='asnincidentlog&asnid='+this.masterData.asnId;
    this.service.getAjaxDropDown(inputparams).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.asnIncidentLogDetails=[];
                rdata.data.forEach((element:any) => {
                    this.asnIncidentLogDetails.push(element);
                });
            }
         },
        error: (e) => console.error(e),
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
              this.masterData.warehouseId=rdata.result.warehouseId;
              this.masterData.transitLocationId=rdata.result.transitlocationId;
              this.masterData.dockName=rdata.result.dockName;
              this.savedCardDetails=rdata.result.asnDetail;
              this.getLists();
          } else {
            this.toastr.warning(rdata.status);                
        }
    }, 
    error: (err) => { this.toastr.warning(err);  },    
  
    });
  }
  updateAsn(submit:boolean): void {

    // if(this.scannedPartDetails.length!=this.masterData.cardsAcknowledged){
    //     this.toastr.info("Number of cards scanned  is less than the number of cards acknowledged["+this.masterData.cardsAcknowledged+"]");
    //     this.setFocus();
    //     return;
    // }

    if (!submit && this.scannedPartDetails.length==0) {
      this.toastr.info("Please scan the cards");
      this.setFocus();
      return;
    }
    if(submit && this.ginScannedPartDetails.length==0) {
      this.toastr.info("Please scan the cards");
      this.setFocus();
      return;
    }
    if(submit && this.showCheckAbnormalDialog){
      if (this.IsNullorEmpty(this.masterData.abnComments)) {
        this.toastr.info("Comment Required");
        return;
       }
    }

    let input={
      asnId:this.masterData.asnId,
      customerId:this.masterData.customerId,
      subloactionId:this.masterData.subLocationId,
      asnDetail:this.scannedPartDetails,
      asnStatus:submit?10:9,
      scwComments:this.masterData.abnComments,
    }
    this.service.updateAsnGinReceipt(input).subscribe(
     
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message)
                this.scannedPartDetails=[];
                if(!submit){
                  this.getGinScannedDetail();
                }else{
                  this.action="view";
                  this.successfulScans=[];
                  this.successfulPalletScans=[];
                  this.ginScannedPartDetails=[];
                  this.showCheckAbnormalDialog=false;  
                }
            } else {
                this.confirmDialogService.showMessage(rdata.message, () => {});
                this.toastr.error(rdata.message)
            }
        },
      (    err: { message: any; }) => {
            this.toastr.error(err.message)
             
        }
    );
  }
  updateIncident():void{
    if(this.abnormalDetails.incidentId==undefined || this.abnormalDetails.incidentId==undefined || this.abnormalDetails.incidentId==0){
      this.toastr.info("Select the Incident");
      return;
    } 
    if(this.abnormalDetails.documentPath==undefined || this.abnormalDetails.documentPath==undefined || this.abnormalDetails.documentPath==''){
      this.toastr.info("Select the Document");
      return;
    }
    let input={
      asnIncidentLogId:this.abnormalDetails.asnIncidentLogId==""?"0":this.abnormalDetails.asnIncidentLogId,
      asnId:this.masterData.asnId,
      // customerId:this.masterData.customerId,
      // subloactionId:this.masterData.subLocationId,
      // asnDetail:this.scannedPartDetails,
      incidentId:this.abnormalDetails.incidentId,
      scwComments:this.abnormalDetails.scwComments,
      document:this.abnormalDetails.documentPath,
      documentName:this.abnormalDetails.documentName,
      // documentPath:"/img",
    }
    this.service.updateInsidentMaster(input).subscribe(
     
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message)
                this.scannedPartDetails=[];
                this.successfulScans=[];
                this.closeInsidentModel();
                this.action="Edit";
                this.getLists();
            } else {
                this.confirmDialogService.showMessage(rdata.message, () => {});
                this.toastr.error(rdata.message)
            }
        },
      (    err: { message: any; }) => {
            this.toastr.error(err.message)
             
        }
    );

  }
    fileChangeEvent(fileInput: any) {
      let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';
      if (fileInput.target?.files && fileInput.target.files[0]) {
        let ext=fileInput.target.files[0].name.split('.').pop();
       const max_size = 1000*1024;
       let allowed_types:any=[];
       let ty=allowedFileTypes.split(',');
        if(ty.indexOf(ext)==-1){
          this.toastr.error("Upload valid file format");
          return;
        }
       const reader = new FileReader();
       reader.onload = (e: any) => {
             let imgBase64Path = e.target.result;
             this.preview=imgBase64Path;
             imgBase64Path = imgBase64Path.replace('data:image/png;base64,','')
             .replace('data:image/jpeg;base64,','')
             .replace('data:application/pdf;base64,','')
             .replace('data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,','')
             .replace('data:application/msword;base64,','');
             this.abnormalDetails.documentName=fileInput.target.files[0].name;
             this.abnormalDetails.documentPath=imgBase64Path;
       };
    
       reader.readAsDataURL(fileInput.target.files[0]);
    }
       
    }
}
