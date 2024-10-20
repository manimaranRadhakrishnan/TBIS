import { Component, Input, OnInit,  AfterViewInit } from '@angular/core';
import { Subject } from 'rxjs';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-documenttype',
  standalone: true,
  imports: [],
  templateUrl: './documenttype.component.html',
  styleUrl: './documenttype.component.css'
})


export class DocumentTypeComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
     
 
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private GlobalVariable: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }



    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.documentTypeId="";
        this.masterData.documentName="";
        this.masterData.IsActive="Active";
  
    }
}




  ngOnInit(): void {
    //console.log(this.masterData);
  }

  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
    }




updateWmsDocumentType(): void {

if (this.IsNullorEmpty(this.masterData.documentName)) {
  this.toastr.info("Please enter Document Name");
    return;
}
  let data:any={};
   data.documentTypeId=this.masterData.documentTypeId==""?"0":this.masterData.documentTypeId;
   data.documentName=this.masterData.documentName;
   data.IsActive=this.masterData.IsActive;
   data.CreatedBy=this.GlobalVariable.userId;
// this.service.updateWmsDocumentType(data).subscribe(
//   (    rdata: { isSuccess: any; successDto: { response: { message: string; }; }; errorDto: { response: { message: string; }; }; }) => {
//         if (rdata.isSuccess) {
//             this.confirmDialogService.showMessage(rdata.successDto.response.message, () => { });
//             this.cancelAdd();
//             this.action="view";
//             this.rpt.getReportData();
//         } else {
//             this.confirmDialogService.showMessage(rdata.errorDto.response.message, () => { });
//         }
//     },
//   (    err: { message: any; }) => {
//         this.confirmDialog(err.message, false);
         
//     }
//);
}
getWmsDocTypeById(typeid: any): void {
    // this.service.getWmsDocTypeById(typeid).subscribe(
    //   rdata => {
    //         if (rdata.isSuccess) {
    //             this.masterData={};  
    //             this.masterData.documentTypeId=rdata.successDto.response[0].documentTypeId;       
    //             this.masterData.documentName=rdata.successDto.response[0].documentName;
    //             this.masterData.IsActive=rdata.successDto.response[0].isActive;
    //         } else {
    //             this.confirmDialog(rdata.status, false);                
    //         }

    //     },
    //   (        err: { message: any; }) => {
    //         this.confirmDialog(err.message, false);
            
    //     }
    // );
}


    reloadPage(): void {
            window.location.reload();
    }

      numberOnly(event: { which: any; keyCode: any; }): boolean {
      const charCode = (event.which) ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
          return false;
      }
      return true;

      }
    //   cancelAdd(){
    //   this.someEvent.next();
    //   }

    cancelAdd(){
        this.masterData={
            documentTypeId: '',                
            documentName: '',
            IsActive: 'Active'
        };
        this.action="view";
      } 
      doAction(event: any){
        if(event){
            if(event.meta){
                if(event.meta=="New"){
                    this.cancelAdd();
                    this.action="New";
                }else if(event.meta=="Edit"){
                    this.cancelAdd();
                    this.action="Edit";
                    this.getWmsDocTypeById(event.data.documentTypeId.toString());
                }
            }
        }
      }
}