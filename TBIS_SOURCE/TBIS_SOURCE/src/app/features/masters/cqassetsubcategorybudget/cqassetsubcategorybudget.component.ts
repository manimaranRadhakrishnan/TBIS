import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { SubLocation } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cqassetsubcategorybudget',
  standalone: false,
  templateUrl: './cqassetsubcategorybudget.component.html',
  styleUrl: './cqassetsubcategorybudget.component.css'
})

export class CqassetsubcategorybudgetComponent implements OnInit,AfterViewInit {

    status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    assetsubcategorylist:any=[];
    werehouseslist:any=[];
  
    eventsSubject: Subject<void> = new Subject<void>();
    constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }
  
    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.cwascbId="";
        this.masterData.cwascId=null;
        this.masterData.cwascbWarehouseId=null;
        this.masterData.cwascbBudget="";
        this.masterData.cwascbBudgetUsed="";
        this.masterData.cwascbIsSystem=true;
        this.masterData.active=true;  
        }
    }
  
    ngOnInit(): void {
        this.getAjaxAssetSubCategoryData('assetsubcatlist');
        this.getAjaxWarehouseData('warehouse');
    }

    getAjaxAssetSubCategoryData(ajaxId:string):void{
        this.service.getAjaxDropDown(ajaxId).subscribe({
            next: (rdata) => {
                if (rdata.data) {
                    this.assetsubcategorylist=[];
                    rdata.data.forEach((element:any) => {
                        this.assetsubcategorylist.push(element);
                    });
                }
             },
            error: (e) => console.error(e),
            // complete: () => console.info('complete') 
        })
    }

    
    getAjaxWarehouseData(ajaxId:string):void{
        this.service.getAjaxDropDown(ajaxId).subscribe({
            next: (rdata) => {
                if (rdata.data) {
                    this.werehouseslist=[];
                    rdata.data.forEach((element:any) => {
                        this.werehouseslist.push(element);
                    });
                }
             },
            error: (e) => console.error(e),
            // complete: () => console.info('complete') 
        })
    }
  
  clearData(){
    this.masterData={
        cwascbId: '',
        cwascId:null,
        cwascbWarehouseId:null,
        cwascbBudget: '',
        
        cwascbBudgetUsed: '',
        cwascbIsSystem:'',
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
            this.getAssetSubCategoryBudgetById(event.cwascb_id.toString());
        }
    }
  
  updateAssetSubCategoryBudgetMaster(): void {
    if (this.IsNullorEmpty(this.masterData.cwascbBudget)) {
        this.toastr.info("Dock Name required !");
        return;
    }
  
    if (this.IsNullorEmpty(this.masterData.cwascbBudgetUsed)) {
        this.toastr.info("Desig Short required !");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.cwascbIsSystem)) {
        this.toastr.info("Is System required !");
        return;
    }
  
    let data:any={};
    data.cwascbId=this.masterData.cwascbId==""?"0":this.masterData.cwascbId;
    data.cwascId=this.masterData.cwascId;
    data.cwascbWarehouseId=this.masterData.cwascbWarehouseId;
    data.cwascbBudget=this.masterData.cwascbBudget;
    data.cwascbBudgetUsed=this.masterData.cwascbBudgetUsed;
    data.cwascbIsSystem=this.masterData.cwascbIsSystem;
    data.userId=this.gVar.userId;
  //   data.active=this.masterData.active;
    this.service.updateAssetSubCategoryBudgetMaster(data).subscribe({
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
  getAssetSubCategoryBudgetById(typeid: any): void {
  
    this.service.getAssetSubCategoryBudgetById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.cwascbId=rdata.result.cwascbId;
                this.masterData.cwascId=rdata.result.cwascId+'';
                this.masterData.cwascbWarehouseId=rdata.result.cwascbWarehouseId+'';       
                this.masterData.cwascbBudget=rdata.result.cwascbBudget;
                this.masterData.cwascbBudgetUsed=rdata.result.cwascbBudgetUsed;
                this.masterData.cwascbIsSystem=rdata.result.cwascbIsSystem;
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
  
  

