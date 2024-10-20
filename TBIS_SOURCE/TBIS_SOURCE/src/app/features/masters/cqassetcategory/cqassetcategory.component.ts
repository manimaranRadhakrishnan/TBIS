import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { SubLocation } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cqassetcategory',
  standalone: false,
  templateUrl: './cqassetcategory.component.html',
  styleUrl: './cqassetcategory.component.css'
})

export class CqassetcategoryComponent implements OnInit,AfterViewInit {

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
        this.masterData.cwacId="";
        this.masterData.cwacName="";
        this.masterData.cwacShort="";
        this.masterData.cwacIsSystem=true;
        this.masterData.active=true;  
        }
    }
  
    ngOnInit(): void {}
  
  clearData(){
    this.masterData={
        cwacId: '',
        cwacName: '',
        cwacShort: '',
        cwacIsSystem:'',
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
            this.getAssetCategoryById(event.cwac_id.toString());
        }
    }
  
  updateAssetCategoryMaster(): void {
    if (this.IsNullorEmpty(this.masterData.cwacName)) {
        this.toastr.info("Dock Name required !");
        return;
    }
  
    if (this.IsNullorEmpty(this.masterData.cwacShort)) {
        this.toastr.info("Desig Short required !");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.cwacIsSystem)) {
        this.toastr.info("Is System required !");
        return;
    }
  
    let data:any={};
    data.cwacId=this.masterData.cwacId==""?"0":this.masterData.cwacId;
    data.cwacName=this.masterData.cwacName;
    data.cwacShort=this.masterData.cwacShort;
    data.cwacIsSystem=this.masterData.cwacIsSystem;
    data.userId=this.gVar.userId;
  //   data.active=this.masterData.active;
    this.service.updateAssetCategoryMaster(data).subscribe({
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
  getAssetCategoryById(typeid: any): void {
  
    this.service.getAssetCategoryById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.cwacId=rdata.result.cwacId;       
                this.masterData.cwacName=rdata.result.cwacName;
                this.masterData.cwacShort=rdata.result.cwacShort+"";
                this.masterData.cwacIsSystem=rdata.result.cwacIsSystem;
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
  
  
