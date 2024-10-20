import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { SubLocation } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-unloaddocmaster',
  standalone: false,
  templateUrl: './unloaddocmaster.component.html',
  styleUrl: './unloaddocmaster.component.css'
})
export class UnloaddocmasterComponent implements OnInit,AfterViewInit {

    status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    sublocation!:SubLocation[];
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }
    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.udcId="";
        this.masterData.udcName="";
        this.masterData.subLocationId="";
        this.masterData.active=true;  
        }
    }

    ngOnInit(): void {
        this.getAjaxData('sublocation');
    }

  
    getAjaxData(ajaxId:string):void{
      this.service.getAjaxDropDown(ajaxId).subscribe({
          next: (rdata) => {
              if (rdata.data) {
                  this.sublocation=[];
                  rdata.data.forEach((element:any) => {
                      this.sublocation.push(element);
                  });
              }
           },
          error: (e) => console.error(e),
          complete: () => console.info('complete') 
      })
  }

  clearData(){
    this.masterData={
        udcId: '',
        udcName: '',
        subLocationId: '',
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
            this.getUnloadDocById(event.udc_id.toString());
        }
    }

updateUnloadDocMaster(): void {
    if (this.IsNullorEmpty(this.masterData.udcName)) {
        this.toastr.info("Dock Name required !");
        return;
    }

    if (this.IsNullorEmpty(this.masterData.subLocationId)) {
        this.toastr.info("Sublocation required !");
        return;
    }

    let data:any={};
    data.udcId=this.masterData.udcId==""?"0":this.masterData.udcId;
    data.udcName=this.masterData.udcName;
    data.subLocationId=this.masterData.subLocationId;
    data.userId=this.gVar.userId;
    data.active=this.masterData.active;
    this.service.updateUnloadDocMaster(data).subscribe({
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
getUnloadDocById(typeid: any): void {

    this.service.getUnloadDocById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.udcId=rdata.result.udcId;       
                this.masterData.udcName=rdata.result.udcName;
                this.masterData.subLocationId=rdata.result.subLocationId+"";
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

