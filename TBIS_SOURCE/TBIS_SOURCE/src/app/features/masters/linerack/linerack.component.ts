import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { LineSpace } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-linerack',
  standalone: false,
  templateUrl: './linerack.component.html',
  styleUrl: './linerack.component.css'
})
export class LinerackComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    linespace!:LineSpace[];
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private GlobalVariable: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }



    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.lineRackId="";
        this.masterData.lineSpaceId=null;
        this.masterData.lineRackCode="";
        this.masterData.customerStorageArea="";
        this.masterData.goodStockAreaLength="";
        this.masterData.goodStockAreaWidth="";
        this.masterData.goodStockAreaHeight="";
        this.masterData.areaM2="";
        this.masterData.areaM3="";
        this.masterData.noofPartStorageBays="";
        this.masterData.active=true;
  
    }
}

    ngOnInit(): void {
        this.getAjaxData('linespace');
     }

     clearData(){
        this.masterData={
            lineSpaceId: null,                
            lineRackCode: '',
            customerStorageArea: '',
            goodStockAreaLength: '',
            goodStockAreaWidth: '',
            goodStockAreaHeight: '',
            noofPartStorageBays: '',
            areaM2:"",
            areaM3:"",
            active:true,
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
            this.getLineRackById(event.linerackid.toString());
        }
      }

 
    getAjaxData(ajaxId:string):void{
      this.service.getAjaxDropDown(ajaxId).subscribe({
          next: (rdata) => {
              if (rdata.data) {
                  this.linespace=[];
                  rdata.data.forEach((element:any) => {
                      this.linespace.push(element);
                  });
              }
           },
          error: (e) => console.error(e),
          complete: () => console.info('complete') 
      })
  
  }







updateLineRack(): void {


if (this.IsNullorEmpty(this.masterData.lineRackCode)) {
    this.toastr.info("Code Required");
    return;
}

if (this.IsNullorEmpty(this.masterData.startLine)) {
    this.toastr.info("Start Line required");
    return;
}
if (this.IsNullorEmpty(this.masterData.endLine)) {
    this.toastr.info("End Line required");
    return;
}
if (this.IsNullorEmpty(this.masterData.startCol)) {
    this.toastr.info("Start Column required");
    return;
}
if (this.IsNullorEmpty(this.masterData.endCol)) {
    this.toastr.info("End Column required");
    return;
}
if (this.IsNullorEmpty(this.masterData.noofPartStorageBays)) {
    this.toastr.info("End Column required");
    return;
}

  let data:any={};
   data.lineRackId=this.masterData.lineRackId==""?"0":this.masterData.lineRackId;
   data.lineRackCode=this.masterData.lineRackCode;
   data.startLine=this.masterData.startLine;
   data.endLine=this.masterData.endLine;
   data.startCol=this.masterData.startCol;
   data.endCol=this.masterData.endCol;
   data.noofPartStorageBays=this.masterData.noofPartStorageBays;
   data.areaM2=0;
   data.areaM3=0;
   data.isActive=this.masterData.active;
//    data.userId=this.GlobalVariable.userId;

   this.service.updateLineRack(data).subscribe({
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

getLineRackById(typeid: any): void {
    this.service.getLineRackById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
              this.masterData={};         
              this.masterData.lineRackId=rdata.result.lineRackId;       
            //   this.masterData.lineSpaceId=rdata.result.lineSpaceId+"";
              this.masterData.lineRackCode=rdata.result.lineRackCode;
            //   this.masterData.customerStorageArea=rdata.result.customerStorageArea;
            //   this.masterData.goodStockAreaLength=rdata.result.goodStockAreaLength;
            //   this.masterData.goodStockAreaWidth=rdata.result.goodStockAreaWidth;
            //   this.masterData.goodStockAreaHeight=rdata.result.goodStockAreaHeight;
              this.masterData.noofPartStorageBays=rdata.result.noofPartStorageBays;
            //   this.masterData.areaM2=rdata.result.areaM2;
            //   this.masterData.areaM3=rdata.result.areaM3;
              this.masterData.startLine=rdata.result.startLine;
              this.masterData.endLine=rdata.result.endLine;
              this.masterData.startCol=rdata.result.startCol;
              this.masterData.endCol=rdata.result.endCol;
              this.masterData.active=rdata.result.isActive;
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
    if (value == undefined || value == "") {
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

