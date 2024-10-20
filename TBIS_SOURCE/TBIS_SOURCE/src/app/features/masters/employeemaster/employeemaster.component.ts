import { Component,Input,OnInit,AfterViewInit, ViewChild} from '@angular/core';
import { UserService } from '../../../services/user.service';
import { Subject } from 'rxjs';
import { EmployeeRolesDDL } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';
import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-employeemaster',
  standalone: false,
  templateUrl: './employeemaster.component.html',
  styleUrl: './employeemaster.component.css'
})
export class EmployeemasterComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    employeeroles!: EmployeeRolesDDL[];
    preview:string="";
    eventsSubject: Subject<void> = new Subject<void>();
    @ViewChild('userlocation', { static: false })
    userlocation!: ReportComponent;
    
    constructor(
    private service:UserService,
    private toastr:ToastrService) { }



    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.employeeId="";
        this.masterData.employeeName="";
        this.masterData.address1="";
        this.masterData.address2="";
        this.masterData.city="";
        this.masterData.email="";
        this.masterData.mobileNo="";
        this.masterData.phone="";
        this.masterData.userName="";
        this.masterData.password="";
        this.masterData.roleId="";
        this.masterData.roleName="";
        this.masterData.employeeType="";
        this.masterData.employeeStatus="Active";
        this.masterData.profileImage=""
  
    }
}

  ngOnInit(): void {
    this.getAjaxData('roles'); 
  }

  getAjaxData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.employeeroles=[];
                rdata.data.forEach((element:any) => {
                    this.employeeroles.push(element);
                });
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })

  }
  clearData(){
    this.masterData={
      employeeId:'',
      employeeName:'',
      address1:'',
      address2:'',
      city:'',
      email:'',
      mobileNo:'',
      phone:'',
      userName:'',
      password:'',
      roleId:'',
      roleName:'',
      employeeType:'',
      employeeStatus:"Active"
    };
    // this.userlocation.defaultparam="empuserid";
    // this.userlocation.defaultparamvalue="0";
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
        this.getEmployeeMasterById(event.em_code.toString());
        this.userlocation.defaultparam="empuserid";
        this.userlocation.defaultparamvalue=this.masterData.userId;

    }
  }
  selectDock(event: any){
    if(event){
      if(this.IsNullorEmpty(this.masterData.userId) || this.masterData.userId==0){
        this.toastr.info("Please save the employee to map the location");
        return;      
      }
      let data:any={};
      data.locationId=event.locationid;
      data.warehouseId=event.warehousid;
      data.udcId=event.udc_id;
      data.subLocationId=event.sublocationid;
      data.userId=this.masterData.userId;
      if(event.selected==0){
        this.service.removeEmpLocation(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
   
              } else {
                  this.toastr.warning(rdata.message);
              }
            },   
            error: (err) => { this.toastr.error(err.message) } 
          })
      }else{
      this.service.addEmpLocation(data).subscribe({
       next: (rdata) => {
           if (rdata.isSuccess) {

           } else {
               this.toastr.warning(rdata.message);
           }
         },   
         error: (err) => { this.toastr.error(err.message) } 
       })
      }
    }
  }
  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "") {
        return true;
    }
    return false;;
    }


updateEmpMaster(): void {

if (this.IsNullorEmpty(this.masterData.employeeName)) {
  this.toastr.info("Please enter employee name");
    return;
}
if (this.IsNullorEmpty(this.masterData.address1)) {
  this.toastr.info("Please enter Department");
  return;
}

if (this.IsNullorEmpty(this.masterData.city)) {
  this.toastr.info("Please enter Designation");
  return;
}
if (this.IsNullorEmpty(this.masterData.email)) {
  this.toastr.info("Please enter email");
  return;
}
if (this.IsNullorEmpty(this.masterData.mobileNo)) {
  this.toastr.info("Please enter mobile");
  return;
}



if (this.IsNullorEmpty(this.masterData.roleId)) {
  this.toastr.info("Please enter employee type");
  return;
}


if (this.IsNullorEmpty(this.masterData.employeeStatus)) {
  this.toastr.info("Please select status");
  return;
}

  let data:any={};
   data.employeeId=this.masterData.employeeId==""?"0":this.masterData.employeeId;
   data.employeeName=this.masterData.employeeName;
   data.address1=this.masterData.address1;
   data.address2=this.masterData.address2;
   data.city=this.masterData.city;
   data.email=this.masterData.email;
   data.mobileNo=this.masterData.mobileNo;
   data.phone=this.masterData.mobileNo;
   data.userName=this.masterData.email;
   data.password="admin@123";
   data.roleId=this.masterData.roleId;
   data.roleName=this.masterData.roleName;
   data.employeeType=1;
   data.employeeStatus=this.masterData.employeeStatus;
   data.userId=this.masterData.userId;
   data.customerUserId=this.masterData.customerUserId;
   data.profileImage=this.masterData.profileImage;
   this.service.updateEmpMaster(data).subscribe({
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
getEmployeeMasterById(typeid: any): void {
  this.service.getEmployeeMasterById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {
          this.masterData={};         
                this.masterData.employeeId=rdata.result.employeeId;
                this.masterData.employeeName=rdata.result.employeeName;
                this.masterData.address1=rdata.result.address1;
                this.masterData.address2=rdata.result.address2;
                this.masterData.city=rdata.result.city;
                this.masterData.email=rdata.result.email;
                this.masterData.mobileNo=rdata.result.mobileNo;
                this.masterData.phone=rdata.result.phone;
                this.masterData.userName=rdata.result.userName;
                this.masterData.password=rdata.result.password;
                this.masterData.roleId=rdata.result.roleId;
                this.masterData.roleName=rdata.result.roleName;
                this.masterData.employeeType=rdata.result.employeeType;
                this.masterData.employeeStatus=rdata.result.employeeStatus;
                this.masterData.profileImage=rdata.result.profileImage;
                this.preview='data:image/png;base64, '+rdata.result.profileImage;
                this.masterData.userId=rdata.result.userId;

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
      

      fileChangeEvent(fileInput: any) {
        let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';
        if (fileInput.target.files && fileInput.target?.files[0]) {
          let ext=fileInput.target.files[0].name.split('.').pop();
         const max_size = 1000*1024;
         let allowed_types:any=[];
         let ty=allowedFileTypes.split(',');
          if(ty.indexOf(ext)==-1){
            this.toastr.error("Upload valid file format");
            return;
          }
         const reader = new FileReader();
         reader.onload = (e: any) => {
               let imgBase64Path = e.target.result;
               this.preview=imgBase64Path;
               imgBase64Path = imgBase64Path.replace('data:image/png;base64,','')
               .replace('data:image/jpeg;base64,','')
               .replace('data:application/pdf;base64,','')
               .replace('data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,','')
               .replace('data:application/msword;base64,','');
               this.masterData.profileImage=imgBase64Path;
         };
      
         reader.readAsDataURL(fileInput.target.files[0]);
      }
         
      }
      updateLocationMap():void{

      }
      clearSelectedLocations():void{

      }
}

  