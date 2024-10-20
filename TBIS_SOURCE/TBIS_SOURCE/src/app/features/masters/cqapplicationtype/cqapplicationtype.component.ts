import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { SubLocation } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cqapplicationtype',
  standalone: false,
  templateUrl: './cqapplicationtype.component.html',
  styleUrl: './cqapplicationtype.component.css'
})

export class CqapplicationtypeComponent implements OnInit,AfterViewInit {

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
        this.masterData.cwaId="";
        this.masterData.cwaDesc="";
        this.masterData.cwaIsSystem=true;
        this.masterData.cwaFormA=false;
        this.masterData.cwaFormC=false;
        this.masterData.cwaFormI=false;
        this.masterData.cwaFormG=false;
        this.masterData.active=true;  
        }
    }
  
    ngOnInit(): void {}
  
  clearData(){
    this.masterData={
        cwaId: '',
        cwaDesc: '',
        cwaIsSystem:'',
        cwaFormA:false,
        cwaFormC:false,
        cwaFormI:false,
        cwaFormG:false,
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
            this.getApplicationTypeById(event.cwa_id.toString());
        }
    }
  
  updateApplicationTypeMaster(): void {
    if (this.IsNullorEmpty(this.masterData.cwaDesc)) {
        this.toastr.info("Dock Name required !");
        return;
    }
  
    if (this.IsNullorEmpty(this.masterData.cwaIsSystem)) {
        this.toastr.info("Is System required !");
        return;
    }
  
    let data:any={};
    data.cwaId=this.masterData.cwaId==""?"0":this.masterData.cwaId;
    data.cwaDesc=this.masterData.cwaDesc;
    data.cwaIsSystem=this.masterData.cwaIsSystem;
    data.cwaFormA=this.masterData.cwaFormA;
    data.cwaFormC=this.masterData.cwaFormC;
    data.cwaFormI=this.masterData.cwaFormI;
    data.cwaFormG=this.masterData.cwaFormG;
    data.userId=this.gVar.userId;
  //   data.active=this.masterData.active;
    this.service.updateApplicationTypeMaster(data).subscribe({
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
  getApplicationTypeById(typeid: any): void {
  
    this.service.getApplicationTypeById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.cwaId=rdata.result.cwaId;       
                this.masterData.cwaDesc=rdata.result.cwaDesc;
                this.masterData.cwaIsSystem=rdata.result.cwaIsSystem;
                this.masterData.cwaFormA=rdata.result.cwaFormA;
                this.masterData.cwaFormC=rdata.result.cwaFormC;
                this.masterData.cwaFormI=rdata.result.cwaFormI;
                this.masterData.cwaFormG=rdata.result.cwaFormG;
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
  
  