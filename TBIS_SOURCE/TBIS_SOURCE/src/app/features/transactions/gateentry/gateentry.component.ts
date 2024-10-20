import { Component,Input,OnInit,AfterViewInit,ViewChild, ElementRef  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { UnloadDocAjax } from '../../../models/transaction';
import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-gateentry',
  standalone: false,
  templateUrl: './gateentry.component.html',
  styleUrl: './gateentry.component.css'
})
export class GateentryComponent {

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";
  unloadDock!:UnloadDocAjax[];

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
        let pipe = new DatePipe('en-US');
        let gateTime:any=pipe.transform(new Date(),'hh:mm a');
        this.masterData.gateInDateTime=gateTime

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

  ngOnInit(): void {
 
   this.getUnloadDocData('unloaddoc');

  }


  getUnloadDocData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.unloadDock=[];
                rdata.data.forEach((element:any) => {
                    this.unloadDock.push(element);
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
  addNew(){
      this.clearData();
      this.action="add";
  } 

  doAction(event: any){
    if(event){
      if(event.asnstatus.toString()=='2'  && this.WmsGV.roleId !='3'){
          this.action="Add";
          this.getASNMasterById(event.asnid.toString());
      }else if (event.asnstatus.toString()=='3' || event.asnstatus.toString()=='5' || event.asnstatus.toString()=='6'){
        if(this.WmsGV.roleId != '3' ){
          this.confirmDialog("Vehicle at Vendor Location",false); 
          return;
        }else{
           this.action="Add";
           this.getASNMasterById(event.asnid.toString());
        }
      } else if(event.asnstatus.toString()=='8' && this.WmsGV.roleId=='3'){
          this.confirmDialog("Vehicle at In Transit",false); 
          return;
      } else if(event.asnstatus.toString()=='9' || event.asnstatus.toString()=='10'){
          this.confirmDialog("Part Request Completed",false); 
          return;
      } else if(event.asnstatus.toString()=='4' ){
          this.confirmDialog("Dispatch Not Ready",false); 
          return;
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
                this.masterData.asnStatus=rdata.result.asnStatus;
                this.masterData.cardsIssued=rdata.result.cardsIssued;
                this.masterData.cardsAcknowledged=rdata.result.cardsAcknowledged;
                this.masterData.cardsDispatched=rdata.result.cardsDispatched;
                this.masterData.cardsReceived=rdata.result.cardsReceived;
                let pipe = new DatePipe('en-US');
                let gateTime:any=pipe.transform(new Date(),'hh:mm a');
                this.masterData.gateInDateTime=gateTime
                this.action="show"
            } else {
                this.confirmDialog(rdata.message, false);     
            }
  
        },
      (        err: { message: any; }) => {
            this.confirmDialog(err.message, false);
            
        }
    );
  }
  getVechicleEntryById(transId: any): void {
    this.service.getVechicleEntryById(transId).subscribe(
      rdata => {
            if (rdata.result) {
                this.masterData={};         
                this.masterData.transId=rdata.result.transId;
                this.masterData.vechicleNo=rdata.result.vechicleNo;
                this.masterData.driverName=rdata.result.driverName;; 
                this.masterData.driverMobile=rdata.result.driverMobile;
                this.masterData.statusId=rdata.result.statusId;
                this.masterData.customerId=rdata.result.customerId;
                this.masterData.vechicleNo=rdata.result.vechicleNo;
                this.masterData.driverName=rdata.result.driverName;
                this.masterData.driverMobile=rdata.result.driverMobile;
                this.masterData.userId=rdata.result.userId;
                this.masterData.asnStatus=rdata.result.asnStatus;
                this.masterData.cardsIssued=rdata.result.cardsIssued;
                this.masterData.cardsAcknowledged=rdata.result.cardsAcknowledged;
                this.masterData.cardsDispatched=rdata.result.cardsDispatched;
                this.masterData.cardsReceived=rdata.result.cardsReceived;
                let pipe = new DatePipe('en-US');
                let gateTime:any=pipe.transform(new Date(),'hh:mm a');
            } else {
                this.confirmDialog(rdata.message, false);                
            }
  
        },
      (        err: { message: any; }) => {
            this.confirmDialog(err.message, false);
            
        }
    );
  }

  updateVechicleEntry(): void {

    if (this.IsNullorEmpty(this.masterData.vechicleNo)) {
        this.toastr.info("Please enter Vechicle No");
        return;
    }
    
    if (this.IsNullorEmpty(this.masterData.driverName)) {
      this.toastr.info("Please enter Contact Email");
      return;
    }
    if (this.IsNullorEmpty(this.masterData.driverMobile)) {
      this.toastr.info("Please enter Contact Phone");
      return;
    }
    
    
      let data:any={};

      data.asnId=this.masterData.asnId;
      data.asnNo=this.masterData.asnNo;
      data.customerId=this.masterData.customerId;
      data.vechicleNo=this.masterData.vechicleNo;
      data.driverName=this.masterData.driverName;
      data.driverMobile=this.masterData.driverMobile;
      data.userId=this.WmsGV.userId;
      data.gateInDateTime=this.masterData.gateInDateTime;
      data.asnStatus=(this.masterData.asnStatus==5 ||this.masterData.asnStatus==6) ? 8 :3 ;

      this.service.updateGateEntry(data).subscribe(
      (    rdata: { isSuccess: any; message: string;result:any }) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.cancelAdd();
                this.action="view";
            } else {
                this.toastr.warning(rdata.message);
            }
        },
      (    err: { message: any; }) => {
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
                this.masterData.vechicleNo=rdata.result.vechicleNo;
                this.masterData.driverName=rdata.result.driverName;
                this.masterData.driverMobile=rdata.result.driverMobile;
                this.masterData.userId=rdata.result.userId;
                this.masterData.asnStatus=rdata.result.asnStatus;
                this.masterData.cardsIssued=rdata.result.cardsIssued;
                this.masterData.cardsAcknowledged=rdata.result.cardsAcknowledged;
                this.masterData.cardsDispatched=rdata.result.cardsDispatched;
                this.masterData.cardsReceived=rdata.result.cardsReceived;
                let pipe = new DatePipe('en-US');
                let gateTime:any=pipe.transform(new Date(),'hh:mm a');
                this.masterData.gateInDateTime=gateTime
               
            } else {
                this.confirmDialog(rdata.message, false);     
            }
  
        },
      (        err: { message: any; }) => {
            this.confirmDialog(err.message, false);
            
        }
    );
  }
}
