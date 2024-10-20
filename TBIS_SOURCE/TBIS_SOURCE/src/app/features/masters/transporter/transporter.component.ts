import { Component,Input,OnInit,AfterViewInit, ViewChild } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '../../../services/common.service';
import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-transporter',
  standalone: false,
  templateUrl: './transporter.component.html',
  styleUrl: './transporter.component.css'
})
export class TransporterComponent  implements OnInit,AfterViewInit{

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    vehicleData:any={};
    documentData:any={};
    vdocumentData:any={};
    form: any = {};
    action:string="view";

    showVehiclesDialog:boolean=false;
    showDocumentsDialog:boolean=false;
    showVDocumentsDialog:boolean=false;
    selectedCustomerId:number=0
    linespacemaster:any=[];
    customerlinespace:any=[];
    transportvehicleslist:any=[];
    isVehicleDisabled:boolean=false;

    transportVehicleDetails:any=[];
    transportDocumentDetails:any=[];
    transportVDocumentDetails:any=[];
    employeeDetails:any=[];
    customerbillingcycles:any=[];
    documenttypes:any=[];
    vdocumenttypes:any=[];
    unloaddocs:any=[];
    customerCards:any=[];
    preview:string="";
    docPreview:string="";
    vdocPreview:string="";
    rptDParam:string="2,8"
    eventsSubject: Subject<void> = new Subject<void>();
    isDisabled:boolean=true;
    noImage='assets/dist/img/no_img.png';
    locations:any=[];
    vehicletypemasters:any=[];
    sublocations:any=[];
    
    @ViewChild('vehicleattachmentreport', { static: false })
    vehicleattachmentreport!: ReportComponent;   
    @ViewChild('transportdocumentreport', { static: false })
    transportdocumentreport!: ReportComponent;
    @ViewChild('vehicledocumentreport', { static: false })
    vehicledocumentreport!: ReportComponent;   


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
        this.masterData.barCodeConfigId='1';
        this.masterData.bankAccountNo="";
        this.masterData.ifscCode="";
        this.masterData.bankName="";
        this.masterData.contractStartDate="2024-01-01";
        this.masterData.contractEndDate="2024-12-01";
        this.masterData.primarySubLocationId=null;
        this.masterData.kycVerified=true;
        this.masterData.approveBy="1";
        this.masterData.approvedDate="2024-01-01";
        this.masterData.active=true;
        this.masterData.tatinMin=0;
        this.masterData.supplymaxRotation=0;
        this.masterData.customerUserId=0;
        this.masterData.profileImage="";
        this.masterData.locationId=null;
        this.masterData.sublocationId="";
    }

    
}

  ngOnInit(): void {
   this.getLocationAjax('location');
   this.getSubLocationAjax('sublocation'); 
   this.getCardConfigAjax('cardmaster');
   this.getLineSpaceAjax();
   this.getAjaxEmployee('employee');
   this.getAjaxBilling('billingcycle');
   this.getAjaxDocumentType('documenttype');
   this.getAjaxVDocumentType('documenttype');
   this.getAjaxUnload('unloaddoc');
   this.getVehicleTypeMasterAjax('vehicletypes');
  }

  // onChangeEntity(id:any):void{
  //   if(id=="1"){
  //     this.isVehicleDisabled=false;
  //     this.documentData.entityId=this.masterData.vechicleId;
  //     this.documentData.entityTypeId=2;
  //   }else{
  //     this.isVehicleDisabled=true;
  //     this.documentData.entityTypeId=3;
  //   }
  // }

  onChangeVehicle(id:any):void{
    this.vdocumentData.entityId=id;
  }

  onChangeVehicleTypes(id:any):void{
    console.log(id);
    console.log(this.vehicletypemasters);
    let selVehicleData:any=JSON.parse(JSON.stringify(this.vehicletypemasters.find((x:any)=>x.vehicletypeid==id)));
    
    this.vehicleData.vehicleCapacity=selVehicleData.capacity;
    this.vehicleData.vehicleAxix=selVehicleData.axel;
    this.vehicleData.vehicleHeight=selVehicleData.height;
    this.vehicleData.vehicleWidth=selVehicleData.width;
    this.vehicleData.vehicleLength=selVehicleData.length;
  }
  

  getAjaxDocumentType(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                console.error(rdata.data);
                this.documenttypes=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }  
  getAjaxVDocumentType(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                console.error(rdata.data);
                this.vdocumenttypes=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  
  getAjaxEmployee(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.employeeDetails=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  
  getAjaxBilling(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.customerbillingcycles=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  
getLocationAjax(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.locations=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

getVehicleTypeMasterAjax(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.vehicletypemasters=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

getCardConfigAjax(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.customerCards=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

getSubLocationAjax(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.sublocations=rdata.data;
          }
       },
      error: (e) => console.error(e)
  })

}

getLineSpaceAjax(){
  let contactsparam='linespace'
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
            this.linespacemaster=rdata.data;
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}





getLists():void{
  this.getTransportVehicles();
  this.getAjaxVehicleList();
}

getAjaxVehicleList():void{
  let inputsparam='transportvehicle&transporterid='+this.masterData.vechicleId;
  this.service.getAjaxDropDown(inputsparam).subscribe({
    next: (rdata) => {
        if (rdata.data) {
            console.error(rdata.data);
            this.transportvehicleslist=rdata.data;
        }
     },
    error: (e) => console.error(e),
  })
}

getTransportVehicles():void{
  let inputsparam='transportvehicles&vehicleid='+this.masterData.vechicleId;
  this.service.getAjaxDropDown(inputsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.transportVehicleDetails=[];
              rdata.data.forEach((element:any) => {
                  this.transportVehicleDetails.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}


getAjaxUnload(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.unloaddocs=rdata.data;
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })

}

get f() { return this.form.controls; }
  IsNullorEmpty(value: any): boolean {
  if (value == undefined || value == "" || value.trim()=="") {
      return true;
  }
  return false;;
}

updateTransporter(): void {

  if (this.IsNullorEmpty(this.masterData.transporterName)) {
    this.toastr.info("Please enter transporter name");
      return;
  }
  if (this.IsNullorEmpty(this.masterData.transporterCode)) {
    this.toastr.info("Please enter transporter Code");
      return;
  }  
  if (this.IsNullorEmpty(this.masterData.address)) {
    this.toastr.info("Address Required");
    return;
  }

  if (this.IsNullorEmpty(this.masterData.city)) {
    this.toastr.info("City required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.district)) {
    this.toastr.info("District required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.state)) {
    this.toastr.info("State required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.gstIn)) {
    this.toastr.info("GST required !");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.locationId)) {
    this.toastr.info("Location required");
    return;
  }
  
    let data:any={};
     data.vechicleId=this.masterData.vechicleId==""?"0":this.masterData.vechicleId;
     data.transporterCode=this.masterData.transporterCode;
     data.transporterName=this.masterData.transporterName;
     data.vechicleStatus=this.masterData.vechicleStatus;
     data.address=this.masterData.address;
     data.city=this.masterData.city;
     data.district=this.masterData.district;
     data.state=this.masterData.state;
     data.gstIn=this.masterData.gstIn;
     data.lat=this.masterData.lat;
     data.lang=this.masterData.lang;
     data.locationId=this.masterData.locationId;
     data.isActive=this.masterData.isActive;
     data.userId=this.gVar.userId;
     this.service.updateTranporter(data).subscribe({
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


closeVehiclesModel(){
  this.showVehiclesDialog=false;
}

closeDocumentsModel(){
  this.showDocumentsDialog=false;
}
closeVDocumentsModel(){
  this.showVDocumentsDialog=false;
}

clearVehicleData(){
    this.vehicleData={
      vehicleId:0,
      vechicleId:0,
      vehicleNo:'',
      vehicleSize:'',
      vehicleCapacity:'',
      vehicleAxix:'',
      userId:0
    }
}

  clearDocumentData(){
    this.documentData={
      documentId:0,
      vechicleId:0,
      userId:0
    }
    this.docPreview="";
  }
  clearVDocumentData(){
    this.vdocumentData={
      documentId:0,
      vechicleId:0,
      userId:0
    }
    this.vdocPreview="";
  }
 
 showVehicles(){
   this.clearVehicleData();
   this.showVehiclesDialog=true;
 }
 

showDocuments(){
  this.clearDocumentData();
  this.showDocumentsDialog=true;
  this.isVehicleDisabled=false;
  this.documentData.entityId=this.masterData.vechicleId;
  this.documentData.entityTypeId=2;
}
showVDocuments(){
  this.clearVDocumentData();
  this.showVDocumentsDialog=true;
  this.isVehicleDisabled=true;
  this.vdocumentData.entityId=0;
  this.vdocumentData.entityTypeId=3;
}

getTransporterById(vehicleId: any): void {
  this.service.getTranporterById(vehicleId).subscribe({
    next: (rdata) => {
        if (rdata.result) {
          this.masterData={};         
          this.masterData.vechicleId=rdata.result.vechicleId;
          this.masterData.transporterCode=rdata.result.transporterCode;
          this.masterData.transporterName=rdata.result.transporterName;
          this.masterData.vechicleSize=rdata.result.vechicleSize;
          this.masterData.vechicleStatus=rdata.result.vechicleStatus;
          this.masterData.address=rdata.result.address;
          this.masterData.city=rdata.result.city;
          this.masterData.district=rdata.result.district;
          this.masterData.state=rdata.result.state;
          this.masterData.gstIn=rdata.result.gstIn;
          this.masterData.lat=rdata.result.lat;
          this.masterData.lang=rdata.result.lang;
          this.masterData.locationId=rdata.result.locationId+"";
          this.masterData.isActive=rdata.result.isActive;
          this.getLists();
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
        vechicleId: '',             
        transporterCode: '',
        transporterName:'',
        vechicleStatus:'',
        address: '',
        city: '',
        district: '',
        state: '',
        gstIn: '',
        lat: '',
        lang: '',
        locationId:null,
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
          this.getTransporterById(event.vechicleid.toString());
      }
    }
    doVehicleAction(event: any){
      if(event){
        this.removeVehicles(event.vehicleid.toString());;
      }
    }
    doTransportDocumentAction(event: any){
      if(event){
          this.removeTransportDocuments(event.documentid.toString());
      }
    }
    doVehicleDocumentAction(event: any){
      if(event){
          this.removeVehicleDocuments(event.documentid.toString());
      }
    }

    updateTransportVehicle(): void {

          if (this.IsNullorEmpty(this.vehicleData.vehicleNo)) {
            this.toastr.info("Please enter Vehicle No.");
            return;
          }

          let data:any={};
           data.vehicleId=this.vehicleData.vehicleId==""?"0":this.vehicleData.vehicleId;
           data.transportId=this.masterData.vechicleId;
           data.vehicleNo=this.vehicleData.vehicleNo;
           data.vechicleSize=this.vehicleData.vehicleSize;
           data.vehicleCapacity=this.vehicleData.vehicleCapacity;
           data.vehicleAxix=this.vehicleData.vehicleAxix;
           data.vehicleHeight=this.vehicleData.vehicleHeight;
           data.vehicleWidth=this.vehicleData.vehicleWidth;
           data.vehicleLength=this.vehicleData.vehicleLength;
           data.m2=this.vehicleData.vehicleHeight*this.vehicleData.vehicleWidth;
           data.m3=this.vehicleData.vehicleHeight*this.vehicleData.vehicleWidth*this.vehicleData.vehicleLength;
           data.active=true;
           data.userId=this.gVar.userId;

           this.service.updateTransportVehicle(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    this.closeVehiclesModel();
                    this.getTransportVehicles();
                    this.vehicleattachmentreport.getReportData(true);
                    this.action="Edit";
                    this.getTransporterById(this.masterData.vechicleId.toString());
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
        
      }

      removeVehicles(id:any) {
        let data:any={};
           data.vehicleId=id;
           data.userId=this.gVar.userId;
           this.service.updateTransportVehicle(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    this.getTransportVehicles();
                    this.vehicleattachmentreport.getReportData(true);
                    this.action="Edit";
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
      }

      
    updateTransportDocument(): void {

        if (this.IsNullorEmpty(this.documentData.documentNo)) {
          this.toastr.info("Please enter document number");
          return;
        }

        if (this.IsNullorEmpty(this.documentData.documentType)) {
          this.toastr.info("Please enter document type");
          return;
        }

        let data:any={};
         data.documentId=this.documentData.documentId==""?"0":this.documentData.documentId;
         data.entityId=this.documentData.entityId;
         data.entityTypeId=this.documentData.entityTypeId;
         data.documentType=this.documentData.documentType;
         data.documentNo=this.documentData.documentNo;
         data.document=this.documentData.documentPath;
         data.documentName=this.documentData.documentName;
         data.validFrom=this.documentData.validFrom;
         data.validTo=this.documentData.validTo;
         data.active=true;
         data.userId=this.gVar.userId;

         this.commonService.updateEntityDocuments(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
                  this.toastr.success(rdata.message);
                  this.closeDocumentsModel();
                  this.transportdocumentreport.getReportData(true);
                  this.action="Edit";
                  this.getTransporterById(this.masterData.vechicleId.toString());
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 
      
    }

    updateTransportVDocument(): void {

      if (this.IsNullorEmpty(this.vdocumentData.documentNo)) {
        this.toastr.info("Please enter document number");
        return;
      }

      if (this.IsNullorEmpty(this.vdocumentData.documentType)) {
        this.toastr.info("Please enter document type");
        return;
      }

      let data:any={};
       data.documentId=this.vdocumentData.documentId==""?"0":this.vdocumentData.documentId;
       data.entityId=this.vdocumentData.entityId;
       data.entityTypeId=this.vdocumentData.entityTypeId;
       data.documentType=this.vdocumentData.documentType;
       data.documentNo=this.vdocumentData.documentNo;
       data.document=this.vdocumentData.documentPath;
       data.documentName=this.vdocumentData.documentName;
       data.validFrom=this.vdocumentData.validFrom;
       data.validTo=this.vdocumentData.validTo;
       data.active=true;
       data.userId=this.gVar.userId;

       this.commonService.updateEntityDocuments(data).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closeVDocumentsModel();
                this.vehicledocumentreport.getReportData(true);
                this.action="Edit";
                this.getTransporterById(this.masterData.vechicleId.toString());
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        }) 
    
  }

  removeTransportDocuments(id:any) {
      let data:any={};
         data.documentId=id;
         data.entityId=this.masterData.vechicleId;
         data.userId=this.gVar.userId;
         this.commonService.updateEntityDocuments(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
                  this.toastr.success(rdata.message);
                  this.transportdocumentreport.getReportData(true);
                  this.action="Edit";
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 
    }

    
    removeVehicleDocuments(id:any) {
      let data:any={};
         data.documentId=id;
         data.entityId=this.masterData.vechicleId;
         data.userId=this.gVar.userId;
         this.commonService.updateEntityDocuments(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
                  this.toastr.success(rdata.message);
                  this.vehicledocumentreport.getReportData(true);
                  this.action="Edit";
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 
    }


docChangeEvent(fileInput: any) {
  let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';
  if (fileInput.target?.files && fileInput.target.files[0]) {
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
         this.docPreview=imgBase64Path;
         imgBase64Path = imgBase64Path.replace('data:image/png;base64,','')
         .replace('data:image/jpeg;base64,','')
         .replace('data:application/pdf;base64,','')
         .replace('data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,','')
         .replace('data:application/msword;base64,','');
         this.documentData.documentName=fileInput.target.files[0].name;
         this.documentData.documentPath=imgBase64Path;
   };

   reader.readAsDataURL(fileInput.target.files[0]);
}
}

vdocChangeEvent(fileInput: any) {
  let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';
  if (fileInput.target?.files && fileInput.target.files[0]) {
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
         this.vdocPreview=imgBase64Path;
         imgBase64Path = imgBase64Path.replace('data:image/png;base64,','')
         .replace('data:image/jpeg;base64,','')
         .replace('data:application/pdf;base64,','')
         .replace('data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,','')
         .replace('data:application/msword;base64,','');
         this.vdocumentData.documentName=fileInput.target.files[0].name;
         this.vdocumentData.documentPath=imgBase64Path;
   };

   reader.readAsDataURL(fileInput.target.files[0]);
  }
}
   
}
