import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { SubLocation } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cqassetsubcategory',
  standalone: false,
  templateUrl: './cqassetsubcategory.component.html',
  styleUrl: './cqassetsubcategory.component.css'
})

export class CqassetsubcategoryComponent implements OnInit,AfterViewInit {

    status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    assetcategorylist:any=[];
  
    eventsSubject: Subject<void> = new Subject<void>();
    constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }
  
    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.cwascId="";
        this.masterData.cwacId="";
        this.masterData.cwascName="";
        this.masterData.cwascShort="";
        this.masterData.cwascIsSystem=true;
        this.masterData.active=true;  
        }
    }
  
    ngOnInit(): void {
        
    this.getAjaxAssetCategoryData('assetcategorylist');
    }

    getAjaxAssetCategoryData(ajaxId:string):void{
        this.service.getAjaxDropDown(ajaxId).subscribe({
            next: (rdata) => {
                if (rdata.data) {
                    this.assetcategorylist=[];
                    rdata.data.forEach((element:any) => {
                        this.assetcategorylist.push(element);
                    });
                }
             },
            error: (e) => console.error(e),
            // complete: () => console.info('complete') 
        })
    }
  
  clearData(){
    this.masterData={
        cwascId: '',
        cwacId:'',
        cwascName: '',
        cwascShort: '',
        cwascIsSystem:'',
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
            this.getAssetSubCategoryById(event.cwasc_id.toString());
        }
    }
  
  updateAssetSubCategoryMaster(): void {
    if (this.IsNullorEmpty(this.masterData.cwascName)) {
        this.toastr.info("Dock Name required !");
        return;
    }
  
    if (this.IsNullorEmpty(this.masterData.cwascShort)) {
        this.toastr.info("Desig Short required !");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.cwascIsSystem)) {
        this.toastr.info("Is System required !");
        return;
    }
  
    let data:any={};
    data.cwascId=this.masterData.cwascId==""?"0":this.masterData.cwascId;
    data.cwacId=this.masterData.cwacId;
    data.cwascName=this.masterData.cwascName;
    data.cwascShort=this.masterData.cwascShort;
    data.cwascIsSystem=this.masterData.cwascIsSystem;
    data.userId=this.gVar.userId;
  //   data.active=this.masterData.active;
    this.service.updateAssetSubCategoryMaster(data).subscribe({
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
  getAssetSubCategoryById(typeid: any): void {
  
    this.service.getAssetSubCategoryById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.cwascId=rdata.result.cwascId;
                this.masterData.cwacId=rdata.result.cwacId+"";       
                this.masterData.cwascName=rdata.result.cwascName;
                this.masterData.cwascShort=rdata.result.cwascShort;
                this.masterData.cwascIsSystem=rdata.result.cwascIsSystem;
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
  
  

