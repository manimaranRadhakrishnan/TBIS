import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { LocationDDL } from '../../../models/masters';

@Component({
  selector: 'app-warehouse',
  standalone: false,
  templateUrl: './warehouse.component.html',
  styleUrl: './warehouse.component.css'
})

export class WarehouseComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    location!:LocationDDL[];
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }


    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.warehousId="";
        this.masterData.locationId=null;
        this.masterData.warehouseName="";
        this.masterData.warehouseShortCode="";
        this.masterData.address="";
        this.masterData.address2="";
        this.masterData.city="";
        this.masterData.district="";
        this.masterData.state="";
        this.masterData.phone="";
        this.masterData.mobile="";
        this.masterData.email="";
        this.masterData.gstin="";
        this.masterData.latitute="";
        this.masterData.longitude="";
        this.masterData.active=true;
  
    }
  }

  ngOnInit(): void {
    this.getAjaxData('location');
  }

  clearData(){
    this.masterData={
      warehousId: '',
      locationId: null,
      warehouseName: '',
      warehouseShortCode: '',
      address: '',
      address2: '',
      city: '',
      district: '',
      state: '',
      phone: '',
      mobile: '',
      email: '',
      gstin: '',
      latitute: '',
      longitude: '',
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
        this.getWarehouseById(event.warehousid.toString());
    }
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
        // complete: () => console.info('complete') 
    })

  }

  get f() { return this.form.controls; }
    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "") {
        return true;
    }
      return false;;
    }


updateWarehouse(): void {

  if (this.IsNullorEmpty(this.masterData.locationId)) {
      this.toastr.info("Location required !");
      return;
  }
  if (this.IsNullorEmpty(this.masterData.warehouseName)) {
      this.toastr.info("Warehouse Name required !");
      return;
  }
  if (this.IsNullorEmpty(this.masterData.warehouseShortCode)) {
    this.toastr.info("Short Code required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.address)) {
    this.toastr.info("Please enter Address");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.city)) {
    this.toastr.info("Please enter City");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.district)) {
    this.toastr.info("Please enter District");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.state)) {
    this.toastr.info("Please enter State");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.phone)) {
    this.toastr.info("Please enter Phone");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.mobile)) {
    this.toastr.info("Please enter Mobile");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.email)) {
    this.toastr.info("Please enter Email");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.gstin)) {
    this.toastr.info("Please enter GSTIN");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.latitute)) {
    this.masterData.latitute=0.000;
  }
  if (this.IsNullorEmpty(this.masterData.longitude)) {
    this.masterData.longitude=0.000;
  }

  let data:any={};
   data.warehousId=this.masterData.warehousId==""?"0":this.masterData.warehousId;
   data.locationId=this.masterData.locationId;
   data.warehouseName=this.masterData.warehouseName;
   data.warehouseShortCode=this.masterData.warehouseShortCode;
   data.address=this.masterData.address;
   data.address2=this.masterData.address2;
   data.city=this.masterData.city;
   data.district=this.masterData.district;
   data.state=this.masterData.state;
   data.phone=this.masterData.phone;
   data.mobile=this.masterData.mobile;
   data.email=this.masterData.email;
   data.gstin=this.masterData.gstin;
   data.latitute=this.masterData.latitute;
   data.longitude=this.masterData.longitude;
   data.active=this.masterData.active;
   data.userId=this.gVar.userId;
   this.service.updateWarehouse(data).subscribe({
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

getWarehouseById(typeid: any): void {

  this.service.getWarehouseById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {
          this.masterData={};    
          this.masterData.warehousId=rdata.result.warehousId;     
          this.masterData.locationId=rdata.result.locationId+"";
          this.masterData.warehouseName=rdata.result.warehouseName;
          this.masterData.warehouseShortCode=rdata.result.warehouseShortCode;
          this.masterData.address=rdata.result.address;
          this.masterData.address2=rdata.result.address2;
          this.masterData.city=rdata.result.city;
          this.masterData.district=rdata.result.district;
          this.masterData.state=rdata.result.state;
          this.masterData.phone=rdata.result.phone;
          this.masterData.mobile=rdata.result.mobile;
          this.masterData.email=rdata.result.email;
          this.masterData.gstin=rdata.result.gstin;
          this.masterData.latitute=rdata.result.latitute;
          this.masterData.longitude=rdata.result.longitude;
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

      numberOnly(event: { which: any; keyCode: any; }): boolean {
      const charCode = (event.which) ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
          return false;
      }
      return true;

      }

      
}
