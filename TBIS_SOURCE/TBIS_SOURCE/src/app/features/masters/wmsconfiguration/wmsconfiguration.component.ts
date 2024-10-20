import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-wmsconfiguration',
  standalone: false,
  templateUrl: './wmsconfiguration.component.html',
  styleUrl: './wmsconfiguration.component.css'
})


export class WmsconfigurationComponent implements OnInit,AfterViewInit {

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
        this.masterData.configId="";
        this.masterData.configurationName="";
        this.masterData.configurationValue="";
        this.masterData.configurationValueType="";
        this.masterData.IsActive="Active";
    }
}



  ngOnInit(): void {
    //console.log(this.masterData);
  }

  clearData(){
    this.masterData={
        configId: '',                
        configurationName: '',
        configurationValue:'',
        configurationValueType:'',
        IsActive: 'Active'
    };

}

cancelAdd(){
   this.clearData();
   this.action="view";
  } 
doAction(event: any){

    if(event){
        this.action="Edit";
        this.getWmsConfigById(event.config_id.toString());
    }
   
}

  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
    }




updateWmsConfiguration(): void {
if (this.IsNullorEmpty(this.masterData.configurationValue)) {
    this.toastr.info("Please enter Configuration Value");
    return;
}


let data:any={};
   data.configId=this.masterData.configId==""?"0":this.masterData.configId;
   data.configurationName=this.masterData.configurationName;
   data.configurationValue=this.masterData.configurationValue;
   data.configurationValueType=this.masterData.configurationValueType;
   data.isSystem=this.masterData.isSystem;

   this.service.updateWmsConfiguration(data).subscribe({
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
getWmsConfigById(typeid: any): void {

    this.service.getWmsConfigById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.configId=rdata.result.configId;       
                this.masterData.configurationName=rdata.result.configurationName;
                this.masterData.configurationValue=rdata.result.configurationValue;
                this.masterData.configurationValueType=rdata.result.configurationValueType;
                this.masterData.IsActive=rdata.result.isActive;
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
      numberOnly(event: { which: any; keyCode: any; }): boolean {
      const charCode = (event.which) ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
          return false;
      }
      return true;

      }
   
}

