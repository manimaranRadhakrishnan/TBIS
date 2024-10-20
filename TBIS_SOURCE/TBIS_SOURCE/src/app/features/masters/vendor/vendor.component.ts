import { Component,Input,OnInit,AfterViewInit,ViewChild  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { PartMasterDDL } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '../../../services/common.service';

@Component({
  selector: 'app-vendor',
  standalone: false,
  templateUrl: './vendor.component.html',
  styleUrl: './vendor.component.css'
})
export class VendorComponent implements OnInit,AfterViewInit {
  

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  contactData:any={};
  partData:any={};
  form: any = {};
  action:string="view";
  showContactsDialog:boolean=false;
  showPartsDialog:boolean=false;
  selectedCustomerId:number=0
  partmaster!:PartMasterDDL[];
  customerContactDetails:any=[];
  customerPartDetails:any=[];
   preview:string="";
  eventsSubject: Subject<void> = new Subject<void>();
  isDisabled:boolean=true;

constructor(
  private gVar: AppGlobal,
  private service:MastersService,
  private commonService:CommonService,
  private toastr:ToastrService) { }


  ngAfterViewInit() {

    if(!this.masterObj){
      this.masterData={};
      this.masterData.customerId="";
      this.masterData.customerErpCode="";
      this.masterData.customerName="";
      this.masterData.address="";
      this.masterData.address2="";
      this.masterData.city="";
      this.masterData.district="";
      this.masterData.state="";
      this.masterData.phone="";
      this.masterData.mobile="";
      this.masterData.email="";
      this.masterData.gstIn="";
      this.masterData.lat="";
      this.masterData.lang="";
      this.masterData.barCodeConfigId="";
      this.masterData.bankAccountNo="";
      this.masterData.ifscCode="";
      this.masterData.bankName="";
      this.masterData.contractStartDate="2024-01-01";
      this.masterData.contractEndDate="2024-12-01";
      this.masterData.primarySubLocationId="";
      this.masterData.kycVerified=true;
      this.masterData.approveBy="1";
      this.masterData.approvedDate="2024-01-01";
      this.masterData.active=true;
      this.masterData.tatinMin=0;
      this.masterData.supplymaxRotation=0;
      this.masterData.customerUserId=0;
      this.masterData.profileImage="";

  }
}


ngOnInit(): void {
  this.getPartMasterData('partmaster');  
}

getPartMasterData(ajaxId:string):void{

  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.partmaster=[];
              rdata.data.forEach((element:any) => {
                  this.partmaster.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      complete: () => console.info('complete') 
  })

}

getLists():void{
this.getCustomerContacts();
this.getCustomerParts();
}

getCustomerContacts():void{
let contactsparam='contacts&customerid='+this.masterData.customerId;
this.service.getAjaxDropDown(contactsparam).subscribe({
    next: (rdata) => {
        if (rdata.data) {
            this.customerContactDetails=[];
            rdata.data.forEach((element:any) => {
                this.customerContactDetails.push(element);
            });
        }
     },
    error: (e) => console.error(e),
    complete: () => console.info('complete') 
})
}

getCustomerParts():void{
let contactsparam='customerparts&customerid='+this.masterData.customerId
this.service.getAjaxDropDown(contactsparam).subscribe({
    next: (rdata) => {
        if (rdata.data) {
            this.customerPartDetails=[];
            rdata.data.forEach((element:any) => {
                this.customerPartDetails.push(element);
            });
        }
     },
    error: (e) => console.error(e),
    complete: () => console.info('complete') 
})
}

clearData(){
  this.masterData={
    customerId: '',                
    customerErpCode: '',
    customerName: '',
    address: '',
    address2: '',
    city: '',
    district: '',
    state: '',
    phone: '',
    mobile: '',
    email: '',
    gstIn: '',
    lat: '',
    lang: '',
    barCodeConfigId: '',
    bankAccountNo: '',
    ifscCode: '',
    bankName: '',
    contractStartDate: '',
    contractEndDate: '',
    primarySubLocationId: '',
    kycVerified: true,
    approveBy: '',
    approvedDate: '',
    active: true,
    tatinMin:"",
    supplymaxRotation:""
};
}

  cancelAdd(){
    this.clearData();
    this.action="view";
  } 

  addNew(){
    this.clearData();
    this.action="add";
    this.isDisabled=false;
  } 

  doAction(event: any){
    if(event){
        this.action="Edit";
        this.getCustomerById(event.customerid.toString());
    }
  }



updateCustomer(): void {


if (this.IsNullorEmpty(this.masterData.customerName)) {
  this.toastr.info("Please enter Vendor Name");
  return;
}

if (this.IsNullorEmpty(this.masterData.customerErpCode)) {
  this.toastr.info("Please enter Vendor Code");
  return;
}

if (this.IsNullorEmpty(this.masterData.address)) {
  this.toastr.info("Please enter Address");
  return;
}

if (this.IsNullorEmpty(this.masterData.email)) {
  this.toastr.info("Please enter Email");
  return;
}

if (this.IsNullorEmpty(this.masterData.phone)) {
  this.toastr.info("Please enter mobile");
  return;
}



let data:any={};
 data.customerId=this.masterData.customerId==""?"0":this.masterData.customerId;
 data.customerErpCode=this.masterData.customerErpCode;
 data.customerName=this.masterData.customerName;
 data.address=this.masterData.address;
 data.address2=this.masterData.address2;
 data.city=this.masterData.city;
 data.district=this.masterData.district;
 data.state=this.masterData.state;
 data.phone=this.masterData.phone;
 data.mobile=this.masterData.phone;
 data.email=this.masterData.email;
 data.gstIn=this.masterData.gstIn;
 data.lat=this.masterData.lat;
 data.lang=this.masterData.lang;
 data.barCodeConfigId=this.masterData.barCodeConfigId;
 data.bankAccountNo=this.masterData.bankAccountNo;
 data.ifscCode=this.masterData.ifscCode;
 data.bankName=this.masterData.bankName;
 data.contractStartDate=this.masterData.contractStartDate;
 data.contractEndDate=this.masterData.contractEndDate;
 data.primarySubLocationId=this.masterData.primarySubLocationId;
 data.kycVerified=this.masterData.kycVerified;
 data.approveBy=this.masterData.approveBy;
 data.approvedDate=this.masterData.approvedDate;
 data.active=this.masterData.active;
 data.userId=this.gVar.userId;
 data.tatinMin=this.masterData.tatinMin;
 data.supplymaxRotation=this.masterData.supplymaxRotation;
 data.customerUserId=this.masterData.customerUserId;
 data.profileImage=this.masterData.profileImage;

 this.service.updateCustomer(data).subscribe({
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


closeContactsModel(){
this.showContactsDialog=false;
}
closePartsModel(){
this.showPartsDialog=false;
}
showContacts(){
this.contactData.contactPersonName='';
this.contactData.contactEmail='';
this.contactData.contactMobile='';
this.contactData.isSms=false;
this.contactData.isMail=false;
this.selectedCustomerId=this.masterData.customerId;
 this.showContactsDialog=true;
}

showParts(){
this.partData.partId=null;
this.selectedCustomerId=this.masterData.customerId;
this.showPartsDialog=true;
this.partmaster;

}


getCustomerById(customerId: any): void {
  this.service.getCustomerById(customerId).subscribe(
    rdata => {
          if (rdata.result) {
              this.masterData={};    
              this.masterData.customerId=rdata.result.customerId;
              this.masterData.customerErpCode=rdata.result.customerErpCode;
              this.masterData.customerName=rdata.result.customerName;
              this.masterData.address=rdata.result.address;
              this.masterData.address2=rdata.result.address2;
              this.masterData.city=rdata.result.city;
              this.masterData.district=rdata.result.district;
              this.masterData.state=rdata.result.state;
              this.masterData.phone=rdata.result.phone;
              this.masterData.mobile=rdata.result.phone;
              this.masterData.email=rdata.result.email;
              this.masterData.gstIn=rdata.result.gstIn;
              this.masterData.lat=rdata.result.lat;
              this.masterData.lang=rdata.result.lang;
              this.masterData.barCodeConfigId=rdata.result.barCodeConfigId;
              this.masterData.bankAccountNo=rdata.result.bankAccountNo;
              this.masterData.ifscCode=rdata.result.ifscCode;
              this.masterData.bankName=rdata.result.bankName;
              this.masterData.contractStartDate=rdata.result.contractStartDate;
              this.masterData.contractEndDate=rdata.result.contractEndDate;
              this.masterData.primarySubLocationId=rdata.result.primarySubLocationId;
              this.masterData.kycVerified=rdata.result.kycVerified;
              this.masterData.approveBy=rdata.result.approveBy;
              this.masterData.approvedDate=rdata.result.approvedDate;
              this.masterData.active=rdata.result.active;
              this.masterData.tatinMin=rdata.result.tatinMin;
              this.masterData.supplymaxRotation=rdata.result.supplymaxRotation;
              this.masterData.customerUserId=rdata.result.customerUserId;
              this.masterData.profileImage=rdata.result.profileImage;
              this.preview='data:image/png;base64, '+rdata.result.profileImage;
              this.getLists();
          } else {  
              this.toastr.warning(rdata.message);             
          }

      },
    (        err: { message: any; }) => {
          this.toastr.error(err.message);
          
      }
  );
}


 


    updateCustomerContact(): void {

      if (this.IsNullorEmpty(this.contactData.contactPersonName)) {
          this.toastr.info("Please enter Contact Person Name");
          return;
      }
      
      if (this.IsNullorEmpty(this.contactData.contactEmail)) {
        this.toastr.info("Please enter Contact Email");
        return;
      }
      
      if (this.IsNullorEmpty(this.contactData.contactMobile)) {
        this.toastr.info("Please enter Contact Phone");
        return;
      }
      
      
        let data:any={};
         data.contactId=this.contactData.contactId==""?"0":this.contactData.contactId;
         data.customerId=this.masterData.customerId;
         data.contactPersonName=this.contactData.contactPersonName;
         data.contactEmail=this.contactData.contactEmail;
         data.contactMobile=this.contactData.contactMobile;
         data.isSms=this.contactData.isSms;
         data.isMail=this.contactData.isMail;
         data.userId=this.gVar.userId;

         this.commonService.updateEntityContact(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closeContactsModel();
                this.getCustomerById(this.masterData.customerId.toString());
                this.action="Edit";
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 
      
    }

    updateCustomerPartsLineMap(isActive:boolean=true): void {

      if (this.IsNullorEmpty(this.partData.partId)) {
          this.toastr.info("Please Select Part Name");
          return;
      }
      
      
        let data:any={};
         data.customerPartId=this.partData.customerPartId==""?"0":this.partData.customerPartId;
         data.customerId=this.masterData.customerId;
         data.partId=this.partData.partId;
         data.customerPartCode="";
         data.barCodeRequired=0;
         data.lineSpaceId=0;
         data.lineLotId=0;
         data.lineRackId=0;
         data.unLoadDocId=0;
         data.isActive=1;


         this.service.updateCustomerPartsLineMap(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closePartsModel();
                this.getCustomerById(this.masterData.customerId.toString());
                this.action="Edit";
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 
   
    }


    
removeParts(id:number,partId:number) {
    this.partData.customerPartId=id;
    this.partData.partId=partId;
    this.updateCustomerPartsLineMap(false);
}
removeContacts(id:any) {
    this.toastr.info(id);
}
fileChangeEvent(fileInput: any) {
  let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';
if (fileInput.target.files && fileInput.target.files[0]) {
  var ext=fileInput.target.files[0].name.split('.').pop();
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
get f() { return this.form.controls; }

  IsNullorEmpty(value: any): boolean {
  if (value == undefined || value == "" ) {
      return true;
  }
  return false;;
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
