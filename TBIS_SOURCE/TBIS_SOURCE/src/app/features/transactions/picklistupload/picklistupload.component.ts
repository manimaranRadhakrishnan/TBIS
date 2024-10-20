import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { GateEntryAjax } from '../../../models/transaction';
import { DatePipe } from '@angular/common';
import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-picklistupload',
  standalone: false,
  templateUrl: './picklistupload.component.html',
  styleUrl: './picklistupload.component.css'
})
export class PicklistUploadComponent implements OnInit,AfterViewInit{
  status = 'start';
  @Input() masterObj: any;

  form: any = {};

  userRoleId:string="1";
  invoiceData:any={};
  showInvoiceDlg=false;
  doInvoicePicklistDetailId:string="0";
  showInvUploadDlg=false;

eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;
  @ViewChild('selectedinvoicereport', { static: false })
  selectedinvoicereport!: ReportComponent;
constructor(
private WmsGV: AppGlobal,
private service:TransactionService, 
private toastr:ToastrService) { }

ngOnInit(): void {
  this.userRoleId=this.WmsGV.roleId;
  
}

ngAfterViewInit() {}

openInvoiceDlg() {
  // this.clearInvoice();
  this.showInvoiceDlg=true;
}
closeInvoiceDlgModel() {
  this.showInvoiceDlg=false;
}

openInvUploadDlg() {
  this.showInvUploadDlg=true;
}
closeInvUploadModel() {
  this.showInvUploadDlg=false;
}

clearInvoice(){
  this.invoiceData={
    invoiceNo:'',
    ewayBillNo:'',
    consignementNo:''
  }
}

doInvoiceAction(event: any){
  this.doInvoicePicklistDetailId=event.picklistidetailid;
  this.getLineSpaceData(event);
}

getLineSpaceData(event: any):void{
  let contactsparam='picklistdtlno&picklistidetailid='+event.picklistidetailid;
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
            this.invoiceData.invoiceNo=rdata.data[0].invoiceno;
            this.invoiceData.ewayBillNo=rdata.data[0].ewaybillno;
            this.invoiceData.consignementNo=rdata.data[0].consignmentno;
            this.openInvoiceDlg();
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
     
}

invoiceAssign(invoicepicklistdetailid:any){
  let ivdata:any={};
  ivdata.pickListDetailId=invoicepicklistdetailid;
  ivdata.invoiceNo=this.invoiceData.invoiceNo;
  ivdata.ewayBillNo=this.invoiceData.ewayBillNo;
  ivdata.consignementNo=this.invoiceData.consignementNo;
  ivdata.userId=this.WmsGV.userId;
  this.service.addInvoiceUpdate(ivdata).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.closeInvoiceDlgModel();
            this.selectedinvoicereport.getReportData(true);
        } else {
            this.toastr.warning(rdata.message);
        }
    },    
    error: (err) => { this.toastr.error(err.message) },  
    
    })

 
}

get f() { return this.form.controls; }

IsNullorEmpty(value: any): boolean {
if (value == undefined || value == "") {
    return true;
}
return false;;
}


fileChangeInvUploadEvent(fileInput: any) {
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
      this.service.uploadInvUpload(fileInput.target.files[0]).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closeInvUploadModel();
                this.selectedinvoicereport.getReportData(true);
                // this.report.getReportData(true);
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        })
  }
}



}

