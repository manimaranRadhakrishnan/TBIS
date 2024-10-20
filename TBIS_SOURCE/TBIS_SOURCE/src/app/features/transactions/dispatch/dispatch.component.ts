import { Component,Input,OnInit,AfterViewInit,ViewChild, ElementRef  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { InsidentDDL } from '../../../models/masters';

@Component({
  selector: 'app-dispatch',
  standalone: false,
  templateUrl: './dispatch.component.html',
  styleUrl: './dispatch.component.css'
})
export class DispatchComponent implements OnInit,AfterViewInit {

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  insidentData:any={};
  form: any = {};
  action:string="view";
  scannedValue:string='';
  scannedPartDetails:any=[];
  insidents!:InsidentDDL[];
  showInsidentDialog:boolean=false;
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
    if (value == undefined || value == "") {
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
                this.getAsnInfotById(this.masterData.asnId.toString());
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

  closeInsidentModel(){
    this.insidentData.insidentId='';
    this.showInsidentDialog=false;
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
    this.scannedValue=this.scannedValue.replace("\t",""); 
    this.service.getAsnDetailByScanData("0",this.scannedValue).subscribe(
      rdata => {
            if (rdata.result) {
                if(rdata.result.transId!=undefined && rdata.result.transId!=0){
                  if(this.scannedPartDetails.find((x:any)=>x.transId==rdata.result.transId)){
                    this.setFocus();
                    this.toastr.info("Card already scanned");
                    this.scannedValue="";
                    return;
                  }              
                  this.scannedPartDetails.push(rdata.result);   
                  this.masterData.totalCardScanned=this.scannedPartDetails.length;   
                  this.scannedValue="";          
                  this.setFocus();
                }else{
                  this.setFocus();
                  this.toastr.info("Invalid code. Try again");      
                  this.scannedValue="";                       
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
  doAction(event: any){

    if(event){
      if(event.asnstatus.toString()=='4' && this.WmsGV.roleId=='3'){
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
                // this.setFocus();                      
            }
  
        },
      (        err: { message: any; }) => {
        this.toastr.error(err.message);
            
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
                this.masterData.gateInDateTime=rdata.result.gateInDateTime.split(' ',1);
                this.masterData.userId=rdata.result.userId;
                this.masterData.cardsIssued=rdata.result.cardsIssued;
                this.masterData.cardsAcknowledged=rdata.result.cardsAcknowledged;
                this.masterData.cardsDispatched=rdata.result.cardsDispatched;
                this.masterData.cardsReceived=rdata.result.cardsReceived;
                this.masterData.returnPack=rdata.result.returnPack;
                this.masterData.returnPackQty=rdata.result.returnPackQty;
                this.scannedPartDetails=[];
            } else {
                this.toastr.error(rdata.message);                  
            }
  
        },
      (        err: { message: any; }) => {
        this.toastr.error(err.message);
            
        }
    );
  }
  updateAsn(): void {

    // if(this.scannedPartDetails.length!=this.masterData.cardsAcknowledged){
    //     this.toastr.info("Number of cards scanned  is less than the number of cards acknowledged["+this.masterData.cardsAcknowledged+"]");
    //     this.setFocus();
    //     return;
    // }

    if (this.scannedPartDetails.length==0) {
      this.toastr.info("Please scan the cards");
      this.setFocus();
      return;
    }
    
    let input={
      asnId:this.masterData.asnId,
      customerId:this.masterData.customerId,
      asnDetail:this.scannedPartDetails
    }
    this.service.updateAsnDispatch(input).subscribe(
     
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message)
                this.scannedPartDetails=[];
                this.action="view";
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

}
