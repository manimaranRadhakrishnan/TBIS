import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-linespace',
  standalone: false,
  templateUrl: './linespace.component.html',
  styleUrl: './linespace.component.css'
})
export class LinespaceComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    sublocations:any=[];
    usageArea:any=[];
     
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }
    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.lineSpaceId="";
        this.masterData.subLocationId="";
        this.masterData.lineSpaceCode="";
        this.masterData.customerStorageArea="";
        this.masterData.goodStockAreaLength="";
        this.masterData.goodStockAreaWidth="";
        this.masterData.beforeInspectionAreaWidth="";
        this.masterData.beforeInspectionAreaLength="";
        this.masterData.afterInspectionAreaWidth="";
        this.masterData.afterInspectionAreaLength="";
        this.masterData.allowedStackHeight="";
        this.masterData.noofPartStorageBays="";
        this.masterData.lineHeight=0;
        this.masterData.lineWidth=0;
        this.masterData.lineLength=0;
        this.masterData.areaM2=0;
        this.masterData.areaM3=0;
        this.masterData.lineUsageId='1';
        this.masterData.isActive= true;
    }
    }

    ngOnInit(): void {
        this.getAjaxData('sublocation');
        this.getAjaxDataUsage('lineusage')
    }

  
        getAjaxData(ajaxId:string):void{
            this.service.getAjaxDropDown(ajaxId).subscribe({
                next: (rdata) => {
                    if (rdata.data) {
                        this.sublocations=rdata.data;
                    }
                },
                error: (e) => console.error(e),
                complete: () => console.info('complete') 
            })

        }

        getAjaxDataUsage(ajaxId:string):void{
            this.service.getAjaxDropDown(ajaxId).subscribe({
                next: (rdata) => {
                    if (rdata.data) {
                        this.usageArea=rdata.data;
                    }
                },
                error: (e) => console.error(e),
                complete: () => console.info('complete') 
            })

        }
        clearData(){
            this.masterData={
                lineSpaceId:'',
                subLocationId: null,                
                lineSpaceCode: '',
                customerStorageArea: '',
                goodStockAreaLength: '',
                goodStockAreaWidth: '',
                beforeInspectionAreaWidth: '',
                beforeInspectionAreaLength: '',
                afterInspectionAreaWidth: '',
                afterInspectionAreaLength: '',
                allowedStackHeight: 1,
                noofPartStorageBays: 0,
                lineHeight:0,
                lineWidth:0,
                lineLength:0,
                areaM2:0,
                areaM3:0,
                lineUsageId:'1',
                isActive: true
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
                this.getLineSpaceById(event.linespaceid.toString());
            }
        }
 


updateLineSpace(): void {

if (this.IsNullorEmpty(this.masterData.subLocationId)) {
    this.toastr.info("Select Sub Location");
    return;
}

if (this.IsNullorEmpty(this.masterData.lineSpaceCode)) {
    this.toastr.info("Space Code required");
    return;
}

if (this.IsNullorEmpty(this.masterData.lineUsageId)) {
    this.toastr.info("Please select Usage");
    return;
}

if (this.IsNullorEmpty(this.masterData.lineLength)) {
    this.toastr.info("Length required");
    return;
}

if (this.IsNullorEmpty(this.masterData.lineWidth)) {
    this.toastr.info("Width required");
    return;
}

if (this.IsNullorEmpty(this.masterData.lineHeight)) {
    this.toastr.info("Height required");
    return;
}


if (this.IsNullorEmpty(this.masterData.goodStockAreaLength)) {
    this.toastr.info("Please enter Part Storage area length");
    return;
}

if (this.IsNullorEmpty(this.masterData.goodStockAreaWidth)) {
    this.toastr.info("Please enter Part Storage area width");
    return;
}
if (this.IsNullorEmpty(this.masterData.beforeInspectionAreaLength)) {
    this.toastr.info("Please enter before inspection area length");
    return;
}
if (this.IsNullorEmpty(this.masterData.beforeInspectionAreaWidth)) {
    this.toastr.info("Please enter before inspection area width");
    return;
}
if (this.IsNullorEmpty(this.masterData.afterInspectionAreaLength)) {
    this.toastr.info("Please enter after inspection area length");
    return;
}
if (this.IsNullorEmpty(this.masterData.afterInspectionAreaWidth)) {
    this.toastr.info("Please enter after inspection area width");
    return;
}
if (this.IsNullorEmpty(this.masterData.allowedStackHeight)) {
    this.toastr.info("Please enter allowed stack height");
    return;
}


  let data:any={};
   data.lineSpaceId=this.masterData.lineSpaceId==""?"0":this.masterData.lineSpaceId;
   data.subLocationId=this.masterData.subLocationId;
   data.lineSpaceCode=this.masterData.lineSpaceCode;
   data.customerStorageArea=this.masterData.customerStorageArea;
   data.goodStockAreaLength=this.masterData.goodStockAreaLength;
   data.goodStockAreaWidth=this.masterData.goodStockAreaWidth;
   data.beforeInspectionAreaWidth=this.masterData.beforeInspectionAreaWidth;
   data.beforeInspectionAreaLength=this.masterData.beforeInspectionAreaLength;
   data.afterInspectionAreaWidth=this.masterData.afterInspectionAreaWidth;
   data.afterInspectionAreaLength=this.masterData.afterInspectionAreaLength;
   data.allowedStackHeight=1;
   data.noofPartStorageBays=0;
   data.isActive=this.masterData.isActive;
   data.lineHeight=this.masterData.lineHeight;
   data.lineWidth=this.masterData.lineWidth;
   data.lineLength=this.masterData.lineLength;
   data.areaM2=this.masterData.areaM2;
   data.areaM3=this.masterData.areaM3;
   data.lineUsageId=this.masterData.lineUsageId;

//    data.userId=this.gVar.userId;
   this.service.updateLineSpace(data).subscribe({
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
getLineSpaceById(typeid: any): void {
    this.service.getLineSpaceById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};
                this.masterData.lineSpaceId=rdata.result.lineSpaceId;         
                this.masterData.subLocationId=rdata.result.subLocationId+"";
                this.masterData.lineSpaceCode=rdata.result.lineSpaceCode;
                this.masterData.customerStorageArea=rdata.result.customerStorageArea;
                this.masterData.goodStockAreaLength=rdata.result.goodStockAreaLength;
                this.masterData.goodStockAreaWidth=rdata.result.goodStockAreaWidth;
                this.masterData.beforeInspectionAreaWidth=rdata.result.beforeInspectionAreaWidth;
                this.masterData.beforeInspectionAreaLength=rdata.result.beforeInspectionAreaLength;
                this.masterData.afterInspectionAreaWidth=rdata.result.afterInspectionAreaWidth;
                this.masterData.afterInspectionAreaLength=rdata.result.afterInspectionAreaLength;
                this.masterData.allowedStackHeight=rdata.result.allowedStackHeight;
                this.masterData.noofPartStorageBays=rdata.result.noofPartStorageBays;
                this.masterData.isActive=rdata.result.isActive;
                this.masterData.lineHeight=rdata.result.lineHeight;
                this.masterData.lineWidth=rdata.result.lineWidth;
                this.masterData.lineLength=rdata.result.lineLength;
                this.masterData.areaM2=rdata.result.areaM2;
                this.masterData.areaM3=rdata.result.areaM3;
                this.masterData.lineUsageId=rdata.result.lineUsageId+"";
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
  
    
      get f() { return this.form.controls; }

      IsNullorEmpty(value: any): boolean {
      if (value == undefined || value == "" ) {
          return true;
      }
      return false;
      }
}

