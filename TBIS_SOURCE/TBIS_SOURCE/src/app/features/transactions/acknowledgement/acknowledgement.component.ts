import { Component,Input,OnInit,AfterViewInit,ViewChild, ElementRef  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-acknowledgement',
  standalone: false,
  templateUrl: './acknowledgement.component.html',
  styleUrl: './acknowledgement.component.css'
})
export class AcknowledgementComponent {
  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";
  scannedValue:string='';
  scannedPartDetails:any=[];
  @ViewChild('scannedValueCtl') scanFocusElement: ElementRef | undefined;
  eventsSubject: Subject<void> = new Subject<void>();
  constructor(private confirmDialogService: ConfirmDialogService,
    private WmsGV: AppGlobal,
    private router: Router, 
    private authService: AuthService, 
    private tokenStorage: TokenStorageService,
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
        this.masterData.directorTransit=true;
        this.masterData.transitasnId=0;
        this.masterData.gateInDateTime="";
        this.masterData.userId="";
        this.masterData.cardsIssued=0;
        this.masterData.cardsAcknowledged=0;
        this.masterData.cardsDispatched=0;
        this.masterData.cardsReceived=0;
    }
  }

  // confirmDialog(message: any, showyesno: any): any {
  //   if (!showyesno) {
  //       this.confirmDialogService.showMessage(message, () => { });
  //   } else {
  //       this.confirmDialogService.confirmThis("confirm",message, () => {
  //           //alert('Yes clicked');
  //       }, () => {
  //           //alert('No clicked');
  //       });
  //   }  
  // }
  
    ngOnInit(): void {
      this.clearData();
    }
  
    setFocus() {
      if(this.scanFocusElement !=undefined){
        setTimeout(() => this.scanFocusElement!.nativeElement.focus(), 0);
      }
    }
    clearData(){
      this.masterData={
        transId :'',
        statusId:'',
        userId:'',
        totalCardScanned:0,
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
  
    parseAndAddCard(e:any){
      if((e.ctrlKey || e.metaKey)  && (e.keyCode == 68 || e.keyCode == 74 || e.keyCode == 77 || e.keyCode == 109 || e.keyCode == 106 || e.keyCode == 98)){
        //do nothing
      }else if(e.key!='Enter' && e.key!='Tab'){
        return;
      }  
      e.preventDefault();    
      this.scannedValue=this.scannedValue.replace("\t","");
      this.service.getAsnDetailByScanData(this.masterData.asnId,this.scannedValue).subscribe(
        rdata => {
              if (rdata.result) {
                  if(rdata.result.transId!=undefined && rdata.result.transId!=0){
                    if(this.scannedPartDetails.find((x:any)=>x.transId==rdata.result.transId)){
                      this.scannedValue="";
                      this.setFocus();
                      this.toastr.info("Card already scanned ");
                     
                      return;
                    }              
                    this.scannedPartDetails.push(rdata.result);   
                    this.masterData.totalCardScanned=this.scannedPartDetails.length;   
                    this.scannedValue="";     
                    this.setFocus();     
                  }else{
                    this.toastr.info("Invalid code. Try again");                  
                    this.scannedValue="";
                    this.setFocus();           
                  }
              } else {
                  this.toastr.error(rdata.message);     
                  this.scannedValue="";           
                  this.setFocus();
              }
    
          },
        (        err: { message: any; }) => {
              this.toastr.error(err.message);
              this.setFocus();
          }
      );
    }
    doAction(event: any){
      if(event){
        if((event.asnstatus.toString()=='2' || event.asnstatus.toString()=='3') && this.WmsGV.roleId =='3'){
            this.action="Edit";
            this.getASNMasterById(event.asnid.toString());
          
        } else{
            this.action="show";
            this.getAsnInfotById(event.asnid.toString());
        }
      }
    }
    getASNMasterById(asnId: any): void {
      this.service.getPartRequestById(asnId).subscribe(
        rdata => {
              if (rdata.result) {
                  this.masterData={};         
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
                  this.masterData.gateInDateTime=rdata.result.gateInDateTime.split(' ',1);
                  this.masterData.userId=rdata.result.userId;
                  this.masterData.cardsIssued=rdata.result.cardsIssued;
                  this.masterData.cardsAcknowledged=rdata.result.cardsAcknowledged;
                  this.masterData.cardsDispatched=rdata.result.cardsDispatched;
                  this.masterData.cardsReceived=rdata.result.cardsReceived;
                  this.masterData.returnPack=rdata.result.returnPack;
                  this.masterData.returnPackQty=rdata.result.returnPackQty;
                  this.scannedPartDetails=[]; 
                  this.setFocus();
              } else {
                  this.toastr.error(rdata.message);                
              }
    
          },
        (        err: { message: any; }) => {
              this.toastr.error(err.message);
              
          }
      );
    }
    getAsnInfotById(asnId: any): void {
      this.service.getAsnInfotById(asnId).subscribe(
        rdata => {
              if (rdata.result) {
                  this.masterData={};         
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
                  this.masterData.gateInDateTime=rdata.result.gateInDateTime.split(' ',1);
                  this.masterData.userId=rdata.result.userId;
                  this.masterData.cardsIssued=rdata.result.cardsIssued;
                  this.masterData.cardsAcknowledged=rdata.result.cardsAcknowledged;
                  this.masterData.cardsDispatched=rdata.result.cardsDispatched;
                  this.masterData.cardsReceived=rdata.result.cardsReceived;
                  this.scannedPartDetails=rdata.result.asnDetail;   
                  this.masterData.returnPack=rdata.result.returnPack;
                  this.masterData.returnPackQty=rdata.result.returnPackQty;
                  this.action="show"
              } else {
                  this.toastr.error(rdata.message);     
                  this.scannedValue="";                      
              }
    
          },
        (        err: { message: any; }) => {
              this.toastr.error(err.message);
              
          }
      );
    }
    updateAsnAcknoledge(): void {
  
      if(this.scannedPartDetails.length!=this.masterData.cardsIssued){
        this.toastr.info("Number of cards scanned  is less than the number of cards requested["+this.masterData.cardsIssued+"]");
        return;
      }
      
      if (this.scannedPartDetails.length==0) {
        this.setFocus();
        this.toastr.info("Please scan the cards");
        return;
      }
      
      let input={
        asnId:this.masterData.asnId,
        customerId:this.masterData.customerId,
        asnDetail:this.scannedPartDetails
      }
      this.service.updateAsnAcknoledgement(input).subscribe(
       
        (    rdata: { isSuccess: any; message: string;result:any }) => {
              if (rdata.isSuccess) {
                  // this.confirmDialogService.showMessage(rdata.message, () => {});
                  this.toastr.info(rdata.message);
                  this.scannedPartDetails=[];
                  this.action="view";
              } else {
                this.toastr.error(rdata.message);
                  // this.confirmDialogService.showMessage(rdata.message, () => {});
              }
          },
        (    err: { message: any; }) => {
              // this.confirmDialog(err.message, false);
              this.toastr.error(err.message);
               
          }
      );
      }
  
  }

