
import { Component,Input,OnInit,AfterViewInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Location } from '../../../models/masters';



@Component({
  selector: 'app-deliverylocation',
  standalone: false,
  templateUrl: './deliverylocation.component.html',
  styleUrl: './deliverylocation.component.css'
})



export class DeliverylocationComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    location!:Location[];
     
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }



    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.dLocationId=""
        this.masterData.dLocationName="";
        this.masterData.dLocationShortCode="";
        this.masterData.locationId=null;
        this.masterData.isActive=true;
  
    }
}


ngOnInit(): void {
  this.getAjaxData('location');
}

get f() { return this.form.controls; }

  IsNullorEmpty(value: any): boolean {
  if (value == undefined || value == "" ) {
      return true;
  }
  return false;;
  }
  getAjaxData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.location=[];
                rdata.data.forEach((element:any) => {
                    this.location.push(element);
                });
            }
         },
        error: (e) => console.error(e),
        complete: () => console.info('complete') 
    })

}


updateDeliveryLocation(): void {

if (this.IsNullorEmpty(this.masterData.dLocationName)) {
    this.toastr.info("Please enter delivery location Name");
    return;
}
if (this.IsNullorEmpty(this.masterData.dLocationShortCode)) {
    this.toastr.info("Please enter delivery location short code");
    return;
}

if (this.IsNullorEmpty(this.masterData.locationId)) {
  this.toastr.info("Please enter location");
  return;
}

  let data:any={};
   data.dLocationId=this.masterData.dLocationId==""?"0":this.masterData.dLocationId;
   data.dLocationName=this.masterData.dLocationName;
   data.dLocationShortCode=this.masterData.dLocationShortCode;
   data.locationId=this.masterData.locationId;
   data.isActive=this.masterData.isActive;
   data.userId=this.gVar.userId;

   this.service.updateDeliveryLocation(data).subscribe({
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

getDeliveryLocationById(typeid: any): void {

  this.service.getDeliveryLocationById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {
          this.masterData={};         
                this.masterData.dLocationId=rdata.result.dLocationId;
                this.masterData.dLocationName=rdata.result.dLocationName;
                this.masterData.dLocationShortCode=rdata.result.dLocationShortCode;
                this.masterData.locationId=rdata.result.locationId+"";
                this.masterData.isActive=rdata.result.isActive;
 
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
        dLocationId: '',
        dLocationName: '',                
        dLocationShortCode: '',
        locationId: null,
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
            this.getDeliveryLocationById(event.dlocationid.toString());
        }
      }
}