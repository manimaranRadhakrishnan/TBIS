import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { GateEntryAjax } from '../../../models/transaction';
import { DatePipe } from '@angular/common';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { result, toNumber } from 'lodash';
@Component({
  selector: 'app-stockmovement',
  standalone: false,
  templateUrl: './stockmovement.component.html',
  styleUrl: './stockmovement.component.css'
})
export class StockmovementComponent implements OnInit,AfterViewInit {

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="edit";
  userRoleId:string="1";
  customerDetails:any=[];
  employeeDetails:any=[];
  savedCardDetails:any=[];
  finalPartDetails:any=[];
  assembyPartDetails:any=[];
  customerPartDetails:any=[];
  toUsageTypes:any=[];
  fromUsageTypes:any=[];
  allUsageTypes:any=[];
  rptParam:string="5"
  myDate = new Date();
  eventsSubject: Subject<void> = new Subject<void>();
  overlay: boolean=false;
  scannedValue:string="";
  successfulScans:any=[];
  selectedPartDetail:any={};
  spaceAllocationDetails:any=[];
  showTab:number=1;
  showFinalPart:boolean=false;
  selectedCustomer:any={};
  barCodeValue:string="";
  excludedBarCodeValue:string="";
  constructor(private confirmDialogService: ConfirmDialogService,
    private WmsGV: AppGlobal,
    private service:TransactionService,
    private toastr:ToastrService) { }
  ngAfterViewInit(): void {
   this.getAjaxCustomers('customer');
   this.getAjaxEmployee('employee');
   this.getAjaxUsageTypes('stockmovementusage');
   this.getAjaxLineUsageTypes('lineusage');
   let pipe = new DatePipe('en-US');
   let gateDate:any=pipe.transform(new Date(),'yyyy-MM-dd');
    if(!this.masterObj){
      this.masterData={};
      this.masterData.transId="";
      this.masterData.asnId="";
      this.masterData.asnNo="";
      this.masterData.customerId=''; 
      this.masterData.supplyDate=gateDate;
      this.masterData.supplyTime="";
      this.masterData.unLoadingDocId='';
      this.masterData.asnStatus='';
      this.masterData.vechicleNo="";
      this.masterData.driverName="";
      this.masterData.driverMobile="";
      this.masterData.ewayBillNo="";
      this.masterData.invoiceNo="";
      this.masterData.directorTransit=true;
      this.masterData.transitasnId=0;
      this.masterData.gateInDateTime="";
      this.masterData.userId="";
      this.masterData.cardsIssued=0;
      this.masterData.cardsAcknowledged=0;
      this.masterData.cardsDispatched=0;
      this.masterData.cardsReceived=0;
      this.masterData.subLocationId=0;
      this.masterData.moveFromId=0;
      this.masterData.moveFromName=0;
      this.masterData.moveToId=0;
      this.masterData.moveToName=0;
      this.masterData.processById=0;
      this.masterData.partId=0;
    }
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
  getAjaxUsageTypes(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.fromUsageTypes=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  getAjaxLineUsageTypes(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.allUsageTypes=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  ngOnInit(): void {
   
  }
  moveFromSelected():void{
      let s=this.fromUsageTypes.find((x:any)=>x.usageid==this.masterData.moveFromId);
      this.toUsageTypes=[];
      if(this.masterData.moveFromId==6){
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==7));
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==4));
      }else if(this.masterData.moveFromId==7){
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==4));
      }else if(this.masterData.moveFromId==4){
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==8));
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==9));
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==10));
      }else if(this.masterData.moveFromId==9){
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==8));
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==10));
      }else if(this.masterData.moveFromId==10){
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==8));
        this.toUsageTypes.push(this.allUsageTypes.find((x:any)=>x.usageid==9));
      }      
      this.masterData.moveFromName=s.usagename;
  }
  moveToSelected():void{
    let s=this.toUsageTypes.find((x:any)=>x.usageid==this.masterData.moveToId);
    this.masterData.moveToName=s.usagename;
  }
  parseAndAddCard(e:any){
    if((e.ctrlKey || e.metaKey)  && (e.keyCode == 68 || e.keyCode == 74 || e.keyCode == 77 || e.keyCode == 109 || e.keyCode == 106 || e.keyCode == 98)){
      //do nothing
    }else if(e.key!='Enter' && e.key!='Tab'){
      return;
    }  

    e.preventDefault(); 
    if(this.masterData.moveFromId==4){
      if(this.selectedCustomer.barcodeconfigid==1 && this.scannedValue.startsWith("PAL")){
        this.toastr.info("Please scan BIN to move from Part/Assembly Area to FG Storage Area");  
        return;
      }else if(this.selectedCustomer.barcodeconfigid!=1 && !this.scannedValue.startsWith("PAL")){
        this.toastr.info("Please scan PALLET to move from Part/Assembly Area to FG Storage Area for this customer");  
        return;
      }
      if(this.savedCardDetails.length>0){
        // this.toastr.info("Cannot scan more than one part for after inspection");
        // return;
      }
    }else if(this.masterData.moveFromId == 7){
      if(!this.scannedValue.startsWith("PAL")){
        this.toastr.info("Please scan PALLET to move from Before Inspection Area to Part/Assembly Area");  
        return;
      }
    }
    this.scannedValue=this.scannedValue.replace("\t",""); 
    if(this.successfulScans.indexOf(this.scannedValue)!=-1){
      this.toastr.info("Card already scanned");
      return;
    }
    let initialScan=0;
    if(this.masterData.moveFromId==4 && this.successfulScans.length==0){
        initialScan=1;
    }
    this.service.getstockMovementCardDetailByScanData(this.masterData.moveFromId,this.scannedValue,this.masterData.partId,initialScan,this.masterData.customerId).subscribe(
      (rdata:any) => {
            if (rdata.result) {
              if(this.masterData.moveFromId==4){
                if(rdata.result.finalPart!=undefined && rdata.result.finalPart && initialScan==1){
                  this.finalPartDetails.push(rdata.result.finalPart);
                  this.assembyPartDetails=rdata.result.assemblyPartDetails;
                  if(this.finalPartDetails[0].exclusiveClubNo==0){
                    this.showFinalPart=true;
                  }
                }
              }
                let partDetail:any={};
                // if(rdata.result.scannedPartDetails.length>0){
                //     partDetail=rdata.result.scannedPartDetails[0];
                // }
                for(let i=0;i<rdata.result.scannedPartDetails.length;i++){
                  let partDetail=rdata.result.scannedPartDetails[i];
                  if(partDetail.transId!=undefined && partDetail.transId!=0){
                    if(this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId )){
                      this.scannedValue="";
                      let row=this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId );
                      let binqty:number=row.requestedBinQty==undefined?0:Number(row.requestedBinQty);
                      binqty++;
                      this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId ).requestedBinQty=binqty;
                      let qty:number=(row.qty==undefined?0:Number(row.qty));
                      qty=qty+Number(row.binQty);
                      this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId ).qty=qty;
                    }else{
                      partDetail.moveFromName=this.masterData.moveFromName;
                      partDetail.moveToName=this.masterData.moveToName;
                      this.savedCardDetails.push(partDetail);   
                    }              
                    // this.successfulScans.push(partDetail.customerScanCode);
                    this.successfulScans.push(partDetail.scanData);
                    if(this.selectedCustomer.barcodeconfigid==1 && this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId )){
                      if(this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId ).customerBarCodes==undefined){
                        this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId ).customerBarCodes=[];
                      }
                      this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId ).customerBarCodes.push({
                        binNo:this.savedCardDetails.find((x:any)=>x.transId==partDetail.transId ).customerBarCodes.length+1,
                        barCode:partDetail.scanData
                      });
                    }
                    this.masterData.totalCardScanned=this.successfulScans.length;   
                    this.scannedValue="";          
                  }else{
                    this.toastr.info("Invalid code. Try again");      
                    this.scannedValue="";                       
                  }
                }              
            }else{
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
  clearData(){
    let pipe = new DatePipe('en-US');
   let gateDate:any=pipe.transform(new Date(),'yyyy-MM-dd');
    this.masterData={};
    this.masterData.transId="";
    this.masterData.asnId="";
    this.masterData.asnNo="";
    this.masterData.customerId=''; 
    this.masterData.supplyDate=gateDate;
    this.masterData.supplyTime="";
    this.masterData.unLoadingDocId='';
    this.masterData.asnStatus='';
    this.masterData.vechicleNo="";
    this.masterData.driverName="";
    this.masterData.driverMobile="";
    this.masterData.ewayBillNo="";
    this.masterData.invoiceNo="";
    this.masterData.directorTransit=true;
    this.masterData.transitasnId=0;
    this.masterData.gateInDateTime="";
    this.masterData.userId="";
    this.masterData.cardsIssued=0;
    this.masterData.cardsAcknowledged=0;
    this.masterData.cardsDispatched=0;
    this.masterData.cardsReceived=0;
    this.masterData.subLocationId=0;
    this.masterData.moveFromId=0;
    this.masterData.moveFromName=0;
    this.masterData.moveToId=0;
    this.masterData.moveToName=0;
    this.masterData.processById=0;
    this.masterData.partId=0;
  }
  moveParts(tab:number):void{
    this.showTab=tab;

}

  updateStockTransfer() {
    if (this.savedCardDetails.length==0) {
      this.toastr.info("Please scan the cards");
      return;
    }
    if(this.masterData.moveFromId==0){
      this.toastr.info("Select move from");
      return;
    }
    if(this.masterData.moveToId==0){
      this.toastr.info("Select move to");
      return;
    }
    
    if(this.masterData.customerId==0){
      this.toastr.info("Select Customer");
      return;
    }
    // if(this.masterData.moveFromId==4 && this.masterData.moveToId==8){
    //   for(let i=0;i<this.savedCardDetails.length;i++){
    //     let totalEnterQty=parseInt(this.savedCardDetails[i].goodQty)+parseInt(this.savedCardDetails[i].badQty)+parseInt(this.savedCardDetails[i].holdQty);
    //     if(this.savedCardDetails[i].qty!=totalEnterQty){
    //       this.toastr.info("Qty is Mismatched. Check Row "+(i+1) + " with Part No: "+this.savedCardDetails[i].partNo);
    //       return;
    //     }
    //   }
    // }
    if(this.masterData.moveToId==8){
      if(this.selectedCustomer.barcodeconfigid==1){
        for(let i=0;i<this.savedCardDetails.length;i++){
          if(this.savedCardDetails[i].customerBarCodes.length!=this.savedCardDetails[i].outwardBin){
            this.toastr.info("Barcode details not matched for part no["+this.savedCardDetails[i].partNo+"]");
            return;
          }
        }
      }else if(this.selectedCustomer.barcodeconfigid!=1){
        if(this.showFinalPart){
          if(this.finalPartDetails[0].customerBarCodes.length!=this.finalPartDetails[0].outwardBin){
            this.toastr.info("Customer barcode details not matched for part no["+this.finalPartDetails[0].partNo+"]");
            return;
          }
        }else{
          for(let i=0;i<this.savedCardDetails.length;i++){
            if(this.savedCardDetails[i].customerBarCodes.length!=this.savedCardDetails[i].outwardBin){
              this.toastr.info("Customer barcode details not matched for part no["+this.savedCardDetails[i].partNo+"]");
              return;
            }
          }
        }
      }
    }

    let input={
      asnId:this.masterData.asnId,
      supplyDate:this.masterData.supplyDate,
      customerId:this.masterData.customerId,
      fromProcessId:this.masterData.moveFromId,
      toProcessId:this.masterData.moveToId,
      employeeId:this.masterData.employeeId,
      asnDetail:this.savedCardDetails,
      scannedParts:this.successfulScans,
      finalPartDetail:this.finalPartDetails.length>0?this.finalPartDetails[0]:undefined
    }
    this.service.updateStockMovement(input).subscribe(
     
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message)
                this.savedCardDetails=[];
                this.successfulScans=[];
                this.finalPartDetails=[];
                this.assembyPartDetails=[];
                this.showTab=1;
                this.masterData.customerId=0;
                this.masterData.moveFromId=0;
                this.masterData.moveToId=0;
                // if (input.fromProcessId==4 && input.toProcessId==8){
                //   this.DownloadBarCode(rdata.result);
                // }
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
  
  doAction(event: any){
    this.clearData();
    if(event){
      if (event.asnstatus.toString()=='5') {  // Customer
            this.action="Edit";
    
       }else{
            this.action="show";
    
        }
      }
    }
  
    getCustomerParts(customerId:number):void{
      this.customerPartDetails=[];
      let contactsparam='picklistcustparts&customerid='+customerId;
      if(this.masterData.moveFromId==6 && this.masterData.moveToId==7){
        contactsparam='asncustomerparts&customerid='+customerId;
      }
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
    }
    onSelectCustomer(customerId:any):void{
      this.selectedCustomer=this.customerDetails.find((x:any)=>x.customerId==customerId);
      this.getCustomerParts(customerId);

    }
    onChangeQuantity(event:any,index:number):void{
        if(!this.showFinalPart){
          if(this.savedCardDetails[index].outwardBinQty>0){
            this.savedCardDetails[index].outwardBin=this.savedCardDetails[index].goodQty/this.savedCardDetails[index].outwardBinQty;
          }else{
            this.savedCardDetails[index].outwardBin=this.savedCardDetails[index].goodQty;
          }
          //let partDetail=this.customerBarcodeDetails.find((x:any)=>x.partId==this.savedCardDetails[index].partId)
          //if(partDetail!=undefined){
          if(this.selectedCustomer.barcodeconfigid!=1){
            this.savedCardDetails[index].customerBarCodes=[];           
          }
          //}
        }
        this.savedCardDetails[index].holdQty=(toNumber(this.savedCardDetails[index].qty)+toNumber(this.savedCardDetails[index].openingHoldQty))-(toNumber(this.savedCardDetails[index].goodQty)+toNumber(this.savedCardDetails[index].badQty));
    }    
    calculateFinalProduct():void{
       let finalProductQty=0;
       if(this.finalPartDetails.length==0) return;
       let finalPart=this.finalPartDetails[0];
       for(let i=0;i<this.savedCardDetails.length;i++){
          let scanDetail=this.savedCardDetails[i];
          let finalPartQty=this.savedCardDetails[i].goodQty/this.savedCardDetails[i].finalPartQty;
          if(finalProductQty!=0 && finalProductQty>finalPartQty){
            finalProductQty=finalPartQty;
          }else if(finalProductQty==0){
            finalProductQty=finalPartQty;
          }
       }
       let outwardBinQty=finalProductQty/finalPart.outwardBinQty;
       this.finalPartDetails[0].outwardBin=outwardBinQty;
       this.finalPartDetails[0].goodQty=finalProductQty;
       this.finalPartDetails[0].customerBarCodes=[];
    }
    showSpaceAllocation(index:number):void{
      if(index>=0 && index<this.savedCardDetails.length){
        this.selectedPartDetail=this.savedCardDetails[index];
        this.showTab=3;
        // alert(JSON.stringify(this.selectedPartDetail));
        this.service.getAsnPartStockMovementSpaceAllocation(this.selectedPartDetail.transId,this.selectedPartDetail.partId,this.masterData.moveToId,this.selectedPartDetail.outwardBin).subscribe({
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
    showFinalPartSpaceAllocation(index:number):void{
      if(index>=0 && index<this.finalPartDetails.length){
        this.selectedPartDetail=this.finalPartDetails[index];
        this.showTab=3;
        // alert(JSON.stringify(this.selectedPartDetail));
        this.service.getAsnPartStockMovementSpaceAllocation(0,this.selectedPartDetail.partId,this.masterData.moveToId,this.selectedPartDetail.outwardBin).subscribe({
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
    showCustomerBarCode(index:number):void{
      if(index>=0 && index<this.savedCardDetails.length){
        this.selectedPartDetail=this.savedCardDetails[index];
        this.showTab=4;
      }
    }
    parseAndAddBarCode(e:any){
      if((e.ctrlKey || e.metaKey)  && (e.keyCode == 68 || e.keyCode == 74 || e.keyCode == 77 || e.keyCode == 109 || e.keyCode == 106 || e.keyCode == 98)){
        //do nothing
      }else if(e.key!='Enter' && e.key!='Tab'){
        return;
      }  
  
      e.preventDefault(); 
      // if(this.barCodeValue.length!=this.selectedCustomer.carddatalength){
      //     this.toastr.info("Invalid Card");
      //     return;  
      // }
      let pstart=this.selectedCustomer.partnostart.split("-")[0];
      let pend=this.selectedCustomer.partnostart.split("-")[1];

      let partNo=this.barCodeValue.substring(0,11).trim();
      // for thr bidadi
      // let partNo=this.barCodeValue.substring(0,12).trim();

      let bcode=this.barCodeValue;
      this.barCodeValue="";
      if(this.showFinalPart){
        if(this.finalPartDetails[0].partNo!=partNo){
          this.toastr.info("Part number not matched with barcode part number");
            return;
        }
        if(this.finalPartDetails[0].customerBarCodes.length==this.selectedPartDetail.outwardBin){
          this.toastr.info("Outward Bin reaches the limit");
            return;
        }
        if(this.finalPartDetails[0].customerBarCodes.find((x:any)=>x.barCode==bcode)!=undefined){
          this.toastr.info("Barcode already scanned");
            return;
        }
        this.finalPartDetails[0].customerBarCodes.push({
          binNo:this.finalPartDetails[0].customerBarCodes.length+1,
          barCode:bcode
        });
      }else{
        /** commented for bidhuthi */
        console.log(this.selectedPartDetail.partNo);
        console.log(partNo);
        if(this.selectedPartDetail.partNo!=partNo){
          this.toastr.info("Part number not matched with barcode part number");
            return;
        }
        if(this.selectedPartDetail.customerBarCodes.length==this.selectedPartDetail.outwardBin){
          this.toastr.info("Outward Bin reaches the limit");
            return;
        }
        if(this.selectedPartDetail.customerBarCodes.find((x:any)=>x.barCode==bcode)!=undefined){
          this.toastr.info("Barcode already scanned");
            return;
        }
        this.selectedPartDetail.customerBarCodes.push({
          binNo:this.selectedPartDetail.customerBarCodes.length+1,
          barCode:bcode
        });
      }
    }
    showBadQualtiyBinScan(index:number):void{
      if(index>=0 && index<this.savedCardDetails.length){
        this.selectedPartDetail=this.savedCardDetails[index];
        this.showTab=5;
      }
    }
    parseAndAddExcludedBarCode(e:any){
      if((e.ctrlKey || e.metaKey)  && (e.keyCode == 68 || e.keyCode == 74 || e.keyCode == 77 || e.keyCode == 109 || e.keyCode == 106 || e.keyCode == 98)){
        //do nothing
      }else if(e.key!='Enter' && e.key!='Tab'){
        return;
      }  
  
      e.preventDefault(); 
      // if(this.barCodeValue.length!=this.selectedCustomer.carddatalength){
      //     this.toastr.info("Invalid Card");
      //     return;  
      // }
      let bcode=this.excludedBarCodeValue;
      this.excludedBarCodeValue="";
      if(this.selectedPartDetail.excludedBarCodes==undefined){
        this.selectedPartDetail.excludedBarCodes=[];
      }
      if(this.selectedPartDetail.excludedBarCodes.find((x:any)=>x.barCode==bcode)!=undefined){
        this.toastr.info("Barcode already scanned");
          return;
      }
      let barcode=this.selectedPartDetail.customerBarCodes.find((x:any)=>x.barCode==bcode);
      if(barcode!=undefined){
        this.selectedPartDetail.excludedBarCodes.push({
          binNo:barcode.binNo,
          barCode:bcode
        });
        if(barcode!=undefined){
          const index = this.selectedPartDetail.customerBarCodes.indexOf(barcode);
          if (index != -1) {
            this.selectedPartDetail.customerBarCodes.splice(index, 1);  
          }
        }
      }else{
        this.toastr.info("Barcode not scanned in the part details");
          return;
      }
    }
    removeExcludedBarCode(index:number){
      this.selectedPartDetail.customerBarCodes.splice(this.selectedPartDetail.excludedBarCodes[index].binNo-1,0,{
          binNo:this.selectedPartDetail.excludedBarCodes[index].binNo,
          barCode:this.selectedPartDetail.excludedBarCodes[index].barCode        
      });
      this.selectedPartDetail.excludedBarCodes.splice(index,1);
    }
    DownloadBarCode(data:string): void {
 
      this.service.getPickListBarcode(data).subscribe((response: any) => {
        this.downloadFile(response,'pdf');
      });
      
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
