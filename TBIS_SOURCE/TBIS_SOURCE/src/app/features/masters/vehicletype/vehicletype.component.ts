import { Component,Input,OnInit,AfterViewInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-vehicletype',
  standalone: false,
  templateUrl: './vehicletype.component.html',
  styleUrl: './vehicletype.component.css'
})
export class VehicletypeComponent {

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
        this.masterData.vehicleTypeId=""
        this.masterData.vehicleTypeName="";
        this.masterData.length=0;
        this.masterData.width=0;
        this.masterData.height=0;
        this.masterData.capacity=0;
        this.masterData.axel=1;
        this.masterData.isActive=true;
  
    }
}

  ngOnInit(): void {
    this.clearData();
  }
  clearData(){
    this.masterData={
      vehicleTypeId: '',
      vehicleTypeName: '',
      length: 0,
      width: 0,
      height: 0,
      capacity: 0,
      axel: 1,
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
          this.getVehicleTypeMasterById(event.vehicletypeid.toString());
      }
  }
  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
    }



updateVehicleTypeMaster(): void {

if (this.IsNullorEmpty(this.masterData.vehicleTypeName)) {
  this.toastr.info("Please enter vehicle type name");
    return;
}
if (this.IsNullorEmpty(this.masterData.axel)) {
  this.toastr.info("Please select Axel");
    return;
}

  let data:any={};
   data.vehicleTypeId=this.masterData.vehicleTypeId==""?"0":this.masterData.vehicleTypeId;
   data.vehicleTypeName=this.masterData.vehicleTypeName;
   data.length=this.masterData.length;
   data.width=this.masterData.width;
   data.height=this.masterData.height;
   data.capacity=this.masterData.capacity;
   data.axel=this.masterData.axel;
   data.isActive=this.masterData.isActive;
   data.userId=this.gVar.userId;


   this.service.updateVehicleTypeMaster(data).subscribe({
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

getVehicleTypeMasterById(typeid: any): void {

  this.service.getVehicleTypeMasterById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {  
          this.masterData={};         
          this.masterData.vehicleTypeId=rdata.result.vehicleTypeId;
          this.masterData.vehicleTypeName=rdata.result.vehicleTypeName;
          this.masterData.length=rdata.result.length;
          this.masterData.width=rdata.result.width;
          this.masterData.height=rdata.result.height;
          this.masterData.capacity=rdata.result.capacity;
          this.masterData.axel=rdata.result.axel;
          this.masterData.isActive=rdata.result.isActive;
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    // errorHandler 

  });
  
}
 
}
