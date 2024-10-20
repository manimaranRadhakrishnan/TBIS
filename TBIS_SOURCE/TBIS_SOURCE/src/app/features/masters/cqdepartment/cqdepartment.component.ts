import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { SubLocation } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cqdepartment',
  standalone: false,
  templateUrl: './cqdepartment.component.html',
  styleUrl: './cqdepartment.component.css'
})

export class CqdepartmentComponent implements OnInit,AfterViewInit {

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
        this.masterData.cwdDeptId="";
        this.masterData.cwdDeptName="";
        this.masterData.cwdDeptShort="";
        this.masterData.cwdIsSystem=true;
        this.masterData.active=true;  
        }
    }
  
    ngOnInit(): void {}
  
  clearData(){
    this.masterData={
        cwdDeptId: '',
        cwdDeptName: '',
        cwdDeptShort: '',
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
            this.getDepartmentById(event.cwd_dept_id.toString());
        }
    }
  
  updateDepartmentMaster(): void {
    if (this.IsNullorEmpty(this.masterData.cwdDeptName)) {
        this.toastr.info("Dock Name required !");
        return;
    }
  
    if (this.IsNullorEmpty(this.masterData.cwdDeptShort)) {
        this.toastr.info("Desig Short required !");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.cwdIsSystem)) {
        this.toastr.info("Is System required !");
        return;
    }
  
    let data:any={};
    data.cwdDeptId=this.masterData.cwdDeptId==""?"0":this.masterData.cwdDeptId;
    data.cwdDeptName=this.masterData.cwdDeptName;
    data.cwdDeptShort=this.masterData.cwdDeptShort;
    data.cwdIsSystem=this.masterData.cwdIsSystem;
    data.userId=this.gVar.userId;
  //   data.active=this.masterData.active;
    this.service.updateDepartmentMaster(data).subscribe({
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
  getDepartmentById(typeid: any): void {
  
    this.service.getDepartmentById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};  
                this.masterData.cwdDeptId=rdata.result.cwdDeptId;       
                this.masterData.cwdDeptName=rdata.result.cwdDeptName;
                this.masterData.cwdDeptShort=rdata.result.cwdDeptShort+"";
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
  
  