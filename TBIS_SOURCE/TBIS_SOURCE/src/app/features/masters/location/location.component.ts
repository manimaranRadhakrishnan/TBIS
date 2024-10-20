import { Component,Input,OnInit,AfterViewInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
@Component({
  selector: 'app-location',
  standalone: false,
  templateUrl: './location.component.html',
  styleUrl: './location.component.css'
})



export class LocationComponent implements OnInit,AfterViewInit {

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
        this.masterData.locationId=""
        this.masterData.locationName="";
        this.masterData.locationShortCode="";
        this.masterData.isActive=1;
  
    }
}

  ngOnInit(): void {
    this.clearData();
  }
  clearData(){
    this.masterData={
      locationId: '',
      locationName: '',                
      locationShortCode: '',
      isActive: 1
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
          this.getLocationById(event.locationid.toString());
      }
  }
  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
    }



updateLocation(): void {

if (this.IsNullorEmpty(this.masterData.locationName)) {
  this.toastr.info("Please enter location Name");
    return;
}
if (this.IsNullorEmpty(this.masterData.locationShortCode)) {
  this.toastr.info("Please enter location short code");
    return;
}

  let data:any={};
   data.locationId=this.masterData.locationId==""?"0":this.masterData.locationId;
   data.locationName=this.masterData.locationName;
   data.locationShortCode=this.masterData.locationShortCode;
   data.isActive=this.masterData.isActive=='1'? this.masterData.isActive:0;
   data.userId=this.gVar.userId;


   this.service.updateLocation(data).subscribe({
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

getLocationById(typeid: any): void {

  this.service.getLocationById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {  
          this.masterData={};         
          this.masterData.locationId=rdata.result.locationId;
          this.masterData.locationName=rdata.result.locationName;
          this.masterData.locationShortCode=rdata.result.locationShortCode;
          this.masterData.isActive=rdata.result.isActive;
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    // errorHandler 

  });
  
}
    
}