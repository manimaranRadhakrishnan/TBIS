import { Component, Input, OnInit,  AfterViewInit } from '@angular/core';
import { Subject } from 'rxjs';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-colorcode',
  standalone: false,
  templateUrl: './colorcode.component.html',
  styleUrl: './colorcode.component.css'
})


export class ColorCodeComponent implements OnInit,AfterViewInit {

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
        this.masterData.colorCodeId="";
        this.masterData.colorName="";
        this.masterData.active=true;
  
    }
}




  ngOnInit(): void {
    this.clearData();
  }

  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
    }


updateWmsColorCode(): void {

if (this.IsNullorEmpty(this.masterData.colorName)) {
    this.toastr.info("Please enter colorName");
    return;
}

  let data:any={};
  data.colorCodeId=this.masterData.colorCodeId==""?"0":this.masterData.colorCodeId;
   data.colorName=this.masterData.colorName;
   data.active=this.masterData.active;
   data.CreatedBy=this.GlobalVariable.userId;

   this.service.updateWmsColorCode(data).subscribe({
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
getWmsColorCodeById(typeid: any): void {

  this.service.getWmsColorCodeById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {
          this.masterData={};        
          this.masterData.colorCodeId=rdata.result.colorCodeId; 
          this.masterData.colorName=rdata.result.colorName;
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

      numberOnly(event: { which: any; keyCode: any; }): boolean {
      const charCode = (event.which) ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
          return false;
      }
      return true;

      }
    clearData(){
      this.masterData={
        colorCodeId: '',                
        colorName: '',
        active: true,
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
            this.getWmsColorCodeById(event.colorcodeid.toString());
        }
      }
}