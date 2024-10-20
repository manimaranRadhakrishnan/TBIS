import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { SubLocation } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cqassetcategorybudget',
  standalone: false,
  templateUrl: './cqassetcategorybudget.component.html',
  styleUrl: './cqassetcategorybudget.component.css'
})

export class CqassetcategorybudgetComponent implements OnInit,AfterViewInit {

    status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    werehouses:any=[];
  
    eventsSubject: Subject<void> = new Subject<void>();
    constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }
  
    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.cwacbId="";
        this.masterData.cwacbPeriod="";
        this.masterData.cwacbBudget="";
        this.masterData.cwacbBudgetUsed="";
        this.masterData.cwacbWarehouseId=null;
        this.masterData.active=true;  
        }
    }
  
    ngOnInit(): void {
        this.getAjaxWarehouseData('warehouse');
    }

    getAjaxWarehouseData(ajaxId:string):void{
        this.service.getAjaxDropDown(ajaxId).subscribe({
            next: (rdata) => {
                if (rdata.data) {
                    this.werehouses=[];
                    rdata.data.forEach((element:any) => {
                        this.werehouses.push(element);
                    });
                }
             },
            error: (e) => console.error(e),
            // complete: () => console.info('complete') 
        })
    }
  
  clearData(){
    this.masterData={
        cwacbId: '',
        cwacbPeriod: '',
        cwacbBudget: '',
        cwacbBudgetUsed:'',
        cwacbWarehouseId:null,
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
            this.getAssetCategoryBudgetById(event.cwacb_id.toString());
        }
    }
  
  updateAssetCategoryBudgetMaster(): void {
    if (this.IsNullorEmpty(this.masterData.cwacbPeriod)) {
        this.toastr.info("Period required !");
        return;
    }
  
    if (this.IsNullorEmpty(this.masterData.cwacbBudget)) {
        this.toastr.info("Budget required !");
        return;
    }
  
    let data:any={};
    data.cwacbId=this.masterData.cwacbId==""?"0":this.masterData.cwacbId;
    data.cwacbPeriod=this.masterData.cwacbPeriod;
    data.cwacbBudget=this.masterData.cwacbBudget;
    data.cwacbBudgetUsed=this.masterData.cwacbBudgetUsed;
    data.cwacbWarehouseId=this.masterData.cwacbWarehouseId;
    data.userId=this.gVar.userId;
  //   data.active=this.masterData.active;
    this.service.updateAssetCategoryBudgetMaster(data).subscribe({
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
  getAssetCategoryBudgetById(typeid: any): void {
  
    this.service.getAssetCategoryBudgetById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.cwacbId=rdata.result.cwacbId;       
                this.masterData.cwacbPeriod=rdata.result.cwacbPeriod;
                this.masterData.cwacbBudget=rdata.result.cwacbBudget;
                this.masterData.cwacbBudgetUsed=rdata.result.cwacbBudgetUsed;
                this.masterData.cwacbWarehouseId=rdata.result.cwacbWarehouseId+"";
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
  
  
