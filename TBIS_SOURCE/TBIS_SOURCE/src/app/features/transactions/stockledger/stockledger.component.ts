import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-stockledger',
  standalone: false,
  templateUrl: './stockledger.component.html',
  styleUrl: './stockledger.component.css'
})
export class StockledgerComponent implements OnInit {
  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";
  userRoleId:string="1";
  isDisabled:boolean=false;
  rptParam:string="3,6"
  myDate = new Date();
  showGateEntryDialog=false;
  vehicleNo:string="";
  customerDetails:any=[];
  customerPartDetails:any=[];
  eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;
@ViewChild('report', { static: false })
report!: ReportComponent;

constructor(
private WmsGV: AppGlobal,
private service:TransactionService, 
private router: Router, 
private toastr:ToastrService) { }

ngAfterViewInit(): void {
  this.getAjaxCustomers('customer');
}
ngOnInit(): void {
  this.userRoleId=this.WmsGV.roleId;
  if(this.userRoleId!="3"){
    this.rptParam="7,8"
  }else{
    this.rptParam="3,6"
  }
    if(!this.masterObj){
      this.masterData={};
      this.masterData.customerId=0;
      this.masterData.partId=0;
    }
  
  }

  doAction(event: any){
    this.clearData();
  }

  showParts(){
    this.showGateEntryDialog=true;
  }
  cancelAdd(){
    this.clearData();
    if(this.userRoleId!="3"){
      this.rptParam="7,8"
    }else{
      this.rptParam="3,6"
    }
    this.action="view";
  } 
  updateGateEntryStatus():void{
    let data={
      asnId: this.masterData.asnId,
      asnStatusId:this.masterData.asnStatusId,
      lastAsnStatusId: this.masterData.lastAsnStatusId,
      transitLoad:false,
      vechicleNo:this.masterData.vechicleNo,
      userId:0,
    }
    if(this.masterData.vechicleNo==""){
      this.toastr.warning("Vehicle Details Not Found");
      return;        
    }

    this.service.updateGateEntryStatus(data).subscribe({
      next: (rdata) => {
          if (rdata.isSuccess) {
              this.toastr.success(rdata.message);
              this.closeVehicleModel();
              this.cancelAdd();
              this.router.navigate(['/asngateentry']);
          } else {
              this.toastr.warning(rdata.message);
          }
        },   
        error: (err) => { this.toastr.error(err.message) } 
      })
   

  }

  closeVehicleModel(){
    this.clearData();
    this.showGateEntryDialog=false;
  }
  clearData() {
    this.masterData={
      customerId:0,
      partId:0
    };
   
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
  onSelectCustomer(customerId:any):void{
    this.getCustomerParts(customerId);
  }
  refreshReport():void{
    if(this.masterData.customerId==undefined || this.masterData.customerId==0){
      this.toastr.info("Select a customer");
      return;
    }
    if(this.masterData.partId==undefined || this.masterData.partId==0){
      this.masterData.partId=0;
    }
    this.report.passedParameter="&customerid="+this.masterData.customerId+"&partid="+this.masterData.partId;
    this.report.getReportData(true);
  }  
}

