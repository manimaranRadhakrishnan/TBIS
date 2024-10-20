import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cqdesignation',
  standalone: false,
  templateUrl: './cqdesignation.component.html',
  styleUrl: './cqdesignation.component.css'
})

export class CqdesignationComponent implements OnInit,AfterViewInit {

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";

  eventsSubject: Subject<void> = new Subject<void>();
  constructor(
  private gVar: AppGlobal,
  private service:MastersService,
  private toastr:ToastrService) { }

  ngAfterViewInit() {
    if(!this.masterObj){
      this.masterData={};
      this.masterData.cwdDesigId="";
      this.masterData.cwdDesigName="";
      this.masterData.cwdDesigShort="";
      this.masterData.cwdIsSystem=true;
      this.masterData.active=true;  
      }
  }

  ngOnInit(): void {}

clearData(){
  this.masterData={
      cwdDesigId: '',
      cwdDesigName: '',
      cwdDesigShort: '',
      cwdIsSystem:'',
      active: true
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
          this.action="Edit";
          this.getDesignationById(event.cwd_desig_id.toString());
      }
  }

updateDesignationMaster(): void {
  if (this.IsNullorEmpty(this.masterData.cwdDesigName)) {
      this.toastr.info("Dock Name required !");
      return;
  }

  if (this.IsNullorEmpty(this.masterData.cwdDesigShort)) {
      this.toastr.info("Desig Short required !");
      return;
  }
  if (this.IsNullorEmpty(this.masterData.cwdIsSystem)) {
      this.toastr.info("Is System required !");
      return;
  }

  let data:any={};
  data.cwdDesigId=this.masterData.cwdDesigId==""?"0":this.masterData.cwdDesigId;
  data.cwdDesigName=this.masterData.cwdDesigName;
  data.cwdDesigShort=this.masterData.cwdDesigShort;
  data.cwdIsSystem=this.masterData.cwdIsSystem;
  data.userId=this.gVar.userId;
//   data.active=this.masterData.active;
  this.service.updateDesignationMaster(data).subscribe({
      next: (rdata) => {
          if (rdata.isSuccess) {
              this.toastr.success(rdata.message);
              this.cancelAdd();
              this.action="view";
          } else {
              this.toastr.warning(rdata.message);
          }
      },    
      error: (err) => { this.toastr.error(err.message) },  
      
      }) 
  
}
getDesignationById(typeid: any): void {

  this.service.getDesignationById(typeid).subscribe({
      next: (rdata) => {
          if (rdata.result) {
              this.masterData={};  
              this.masterData.cwdDesigId=rdata.result.cwdDesigId;       
              this.masterData.cwdDesigName=rdata.result.cwdDesigName;
              this.masterData.cwdDesigShort=rdata.result.cwdDesigShort+"";
              this.masterData.cwdIsSystem=rdata.result.cwdIsSystem;
              this.masterData.active=rdata.result.active;
          } else {
              this.toastr.warning(rdata.status);                
          }
      }, 
      error: (err) => { this.toastr.warning(err);  },    // errorHandler 
      });
}


reloadPage(): void {
    window.location.reload();
}

get f() { return this.form.controls; }

IsNullorEmpty(value: any): boolean {
if (value == undefined || value == "" ) {
  return true;
}
return false;;
}
numberOnly(event: { which: any; keyCode: any; }): boolean {
const charCode = (event.which) ? event.which : event.keyCode;
if (charCode > 31 && (charCode < 48 || charCode > 57)) {
  return false;
}
return true;

}
 

}

