import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-plantdocks',
  standalone: false,
  templateUrl: './plantdocks.component.html',
  styleUrl: './plantdocks.component.css'
})
export class PlantdocksComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    deliverylocations:any=[];
     
    // @ViewChild('frm') frm!: NgForm;
    // @ViewChild(ReportComponent, {static : true}) rpt : ReportComponent;
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }

    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.dDockId="";
        this.masterData.dockName="";
        this.masterData.dockShortName="";
        this.masterData.dLocationId=null;
        this.masterData.isActive=true;
  
    }
    }

  ngOnInit(): void {
    this.getAjaxData('deliverylocation');
  }

  getAjaxData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.deliverylocations=rdata.data;
            }
         },
        error: (e) => console.error(e),
        complete: () => console.info('complete') 
    })
}

clearData(){
    this.masterData={
      dDockId:'',
      dockName: '',                
      dockShortName: '',
      dLocationId: null,
      isActive: true,
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
        this.getPlantDocksById(event.ddockid.toString());
    }
  }

 
updatePlantDocks(): void {

    if (this.IsNullorEmpty(this.masterData.dockName)) {
        this.toastr.info("plant Dock name required !");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.dockShortName)) {
        this.toastr.info("plant Dock short name required !");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.dLocationId)) {
        this.toastr.info("Select delivery location");
        return;
    }

  let data:any={};
   data.dDockId=this.masterData.dDockId=="" ? "0":this.masterData.dDockId;
   data.dockName=this.masterData.dockName;
   data.dockShortName=this.masterData.dockShortName;
   data.dLocationId=this.masterData.dLocationId;
   data.isActive=this.masterData.isActive;
   data.userId=this.gVar.userId;
   this.service.updatePlantDocks(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.cancelAdd();
            this.action="view";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })

}
getPlantDocksById(typeid: any): void {
    this.service.getPlantDocksById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};         
                this.masterData.dDockId=rdata.result.dDockId;
                this.masterData.dockName=rdata.result.dockName;
                this.masterData.dockShortName=rdata.result.dockShortName;
                this.masterData.dLocationId=rdata.result.dLocationId+'';
                this.masterData.isActive=rdata.result.isActive;
            } else {
              this.toastr.warning(rdata.status);                
          }
      }, 
      error: (err) => { this.toastr.warning(err);  },    
    
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
      return false;;
      }
}
