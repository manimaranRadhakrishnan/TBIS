import { AfterViewInit, Component,Input, OnInit, ViewChild  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { ReportComponent } from '../../../report/report.component';
import { TransactionService } from '../../../services/transaction.service';

@Component({
  selector: 'app-loading',
  standalone: false,
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.css'
})
export class LoadingComponent implements OnInit,AfterViewInit {
  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";
  doActionPicklistId:string="0";
  showGoodsVehicleDlg=false;
  invoiceLoadingData:any={};
  employeeDetails:any=[];
  // showGateEntryDlg=false;
  // showSrvDlg=false;

  @ViewChild('report', { static: false })
  report!: ReportComponent;  
 
  eventsSubject: Subject<void> = new Subject<void>();
constructor(
  private WmsGV: AppGlobal,
  private service:TransactionService,
  private toastr:ToastrService) { }

  ngAfterViewInit(): void {}
  ngOnInit(): void {
    this.getAjaxEmployee('employee');
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

cancelAdd(){
this.action="view";
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
this.getLodingData(event);
}

getLodingData(event: any):void{
  let contactsparam='pickloading&picklistid='+event.picklistid;
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
            this.invoiceLoadingData.loadingSupervisorId=rdata.data[0].loadingsupervisorid;
            this.invoiceLoadingData.docHandoverId=rdata.data[0].dochandoverid;
            this.invoiceLoadingData.docAuditId=rdata.data[0].docauditid;
            this.openVehicleDlg();
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
     
}

openVehicleDlg() {
this.showGoodsVehicleDlg=true;
}
closeVehicleDlgModel() {
this.showGoodsVehicleDlg=false;
}

invoiceLoadingAssign(invoicepicklistid:any){
  let ivdata:any={};
  ivdata.pickListId=invoicepicklistid;
  ivdata.loadingSupervisorId=this.invoiceLoadingData.loadingSupervisorId;
  ivdata.docHandoverId=this.invoiceLoadingData.docHandoverId;
  ivdata.docAuditId=this.invoiceLoadingData.docAuditId;
  ivdata.userId=this.WmsGV.userId;
  this.service.addInvoiceLoadingUpdate(ivdata).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.closeVehicleDlgModel();
            this.report.getReportData(true);
        } else {
            this.toastr.warning(rdata.message);
        }
    },    
    error: (err) => { this.toastr.error(err.message) },  
    
    })

 
}


// openGateEntryDlg() {
// this.showGateEntryDlg=true;
// }
// closeGateEntryModel() {
// this.showGateEntryDlg=false;
// }

// openSrvDlg() {
// this.showSrvDlg=true;
// }
// closeSrvModel() {
// this.showSrvDlg=false;
// }

// fileChangeGateEvent(fileInput: any) {
// let allowedFileTypes='log,txt,xlsx';
// if (fileInput.target!.files && fileInput.target.files[0]) {
//     let ext=fileInput.target.files[0].name.split('.').pop();
//     // Size Filter Bytes
//     const max_size = 1000*1024;
//     let allowed_types:any=[];
//     let ty=allowedFileTypes.split(',');
//     if(ty.indexOf(ext)==-1){
//     this.toastr.error("Upload valid file format");
//     return;
//     }
//     this.service.uploadGateEntry(fileInput.target.files[0]).subscribe({
//       next: (rdata) => {
//           if (rdata.isSuccess) {
//               this.toastr.success(rdata.message);
//               this.closeGateEntryModel();
//               this.report.getReportData(true);
//           } else {
//               this.toastr.warning(rdata.message);
//           }
//       },    
//       error: (err) => { this.toastr.error(err.message) },  
      
//       })
    
// }
    
// }

// fileChangeSrvEvent(fileInput: any) {
// let allowedFileTypes='log,txt,xlsx';
// if (fileInput.target!.files && fileInput.target.files[0]) {
//     let ext=fileInput.target.files[0].name.split('.').pop();
//     // Size Filter Bytes
//     const max_size = 1000*1024;
//     let allowed_types:any=[];
//     let ty=allowedFileTypes.split(',');
//     if(ty.indexOf(ext)==-1){
//     this.toastr.error("Upload valid file format");
//     return;
//     }
//     this.service.uploadSrv(fileInput.target.files[0]).subscribe({
//       next: (rdata) => {
//           if (rdata.isSuccess) {
//               this.toastr.success(rdata.message);
//               this.closeSrvModel();
//               this.report.getReportData(true);
//           } else {
//               this.toastr.warning(rdata.message);
//           }
//       },    
//       error: (err) => { this.toastr.error(err.message) },  
      
//       })
    
// }
    
// }

}
