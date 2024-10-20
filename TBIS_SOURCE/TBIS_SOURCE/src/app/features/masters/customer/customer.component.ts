import { Component,Input,OnInit,AfterViewInit, ViewChild } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { PartMasterDDL } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '../../../services/common.service';
import { ReportComponent } from '../../../report/report.component';
// import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-customer',
  standalone: false,
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css'
})

export class CustomerComponent implements OnInit,AfterViewInit {
    // @ViewChild(ReportComponent)
    // report: ReportComponent | undefined;

    status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    contactData:any={};
    contractData:any={};
    documentData:any={};
    softwareData:any={};
    manpowerData:any={};
    addressData:any={};
    partData:any={};
    spaceData:any={};
    rackData:any={};
    form: any = {};
    action:string="view";
    showContactsDialog:boolean=false;
    showContractsDialog:boolean=false;
    showPartsDialog:boolean=false;
    // showLineSpaceDialog:boolean=false;
    showLineRackDialog:boolean=false;
    showDocumentsDialog:boolean=false;
    showSoftwaresDialog:boolean=false;
    showManpowersDialog:boolean=false;
    showAddressDialog:boolean=false;
    showCellDetailsDialog:boolean=false;
    selectedCustomerId:number=0
    partmaster!:PartMasterDDL[];
    linespacemaster:any=[];
    linerackmaster:any=[];
    manpowercategories:any=[];
    customerlinespace:any=[];
    customerlinerack:any=[];
    customerContactDetails:any=[];
    customerContractDetails:any=[];
    customerDocumentDetails:any=[];
    customerSoftwareDetails:any=[];
    customerManpowerDetails:any=[];
    customerCompAddressDetails:any=[];
    customerPartDetails:any=[];
    customerSpaceDetails:any=[];
    customerRackDetails:any=[];
    employeeDetails:any=[];
    customerbillingcycles:any=[];
    documenttypes:any=[];
    unloaddocs:any=[];
    customerCards:any=[];
    preview:string="";
    docPreview:string="";
    eventsSubject: Subject<void> = new Subject<void>();
    isDisabled:boolean=true;
    noImage='assets/dist/img/no_img.png';
    locations:any=[];
    sublocations:any=[];
    @ViewChild('linespacereport', { static: false })
    linespacereport!: ReportComponent;   
    @ViewChild('customerpartreport', { static: false })
    customerpartreport!: ReportComponent;
    @ViewChild('linerackreport', { static: false })
    linerackreport!: ReportComponent;   
    @ViewChild('customercontactreport', { static: false })
    customercontactreport!: ReportComponent;
    @ViewChild('customercontractreport', { static: false })
    customercontractreport!: ReportComponent;   
    @ViewChild('customerdocumentreport', { static: false })
    customerdocumentreport!: ReportComponent;
    @ViewChild('customersoftwarereport', { static: false })
    customersoftwarereport!: ReportComponent;   
    @ViewChild('customeraddressreport', { static: false })
    customeraddressreport!: ReportComponent;
    
    @ViewChild('customermanpowerreport', { static: false })
    customermanpowerreport!: ReportComponent;

    usageTypes:any=[];
    customers:any=[];
    colorCodes:any=[];
    cellDetails:any=[];
    cellData:any={};
    spaceDetails:any = [];
    active:boolean=false;
    startCell:number=0;
    endCell:number=0;
    startRow:number=0;
    endRow:number=0;
    minLineNo:number=0;
    maxLineNo:number=0;
    minColumnNo:number=0;
    maxColumnNo:number=0;
    customerPartMapDetails:any=[];
    selectedPartDetails:any=[];
    spaceDetail:any={};
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
        this.masterData.tatinMin='0';
        this.masterData.vehicleType=0;
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
        this.masterData.supplymaxRotation=0;
        this.masterData.customerUserId=0;
        this.masterData.profileImage="";
        this.masterData.locationId=null;
        this.masterData.sublocationId="";
    }
    this.cellData.startCell=0;
    this.cellData.endCell=0;
    this.cellData.startRow=0;
    this.cellData.endRow=0;
    this.cellData.customerid=0;
    this.cellData.colorCode="";
    this.cellData.lineUsageId=0;
    this.cellData.partId=0;
    this.cellData.fifoOrder=0;
    this.cellData.fromLineSpaceId=0;
    this.cellData.toLineSpaceId=0;
    this.cellData.maxBins=0;
    this.cellData.partSpaceName=''; 
    this.cellData.subLocationId=0;

    
}

  ngOnInit(): void {
    this.getPartMasterData('partmaster');
    this.getLocationAjax('location');
    this.getSubLocationAjax('sublocation'); 
    this.getCardConfigAjax('cardmaster');
    // this.getLineSpaceAjax();
    this.getLineRackAjax();
   this.getAjaxEmployee('employee');
   this.getAjaxBilling('billingcycle');
   this.getAjaxDocumentType('documenttype');
   this.getAjaxUnload('unloaddoc');
   this.getLineUsageData('lineusage');  
   this.getAjaxManpowerCategory('manpowercategory');  
  }

  getAjaxManpowerCategory(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data!=null && rdata.data.length>0) {
                this.manpowercategories=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }

  getAjaxDocumentType(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data!=null && rdata.data.length>0) {
                this.documenttypes=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  
  getAjaxEmployee(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data!=null && rdata.data.length>0) {
                this.employeeDetails=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  
  getAjaxBilling(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data!=null && rdata.data.length>0) {
                this.customerbillingcycles=rdata.data;
            }
         },
        error: (e) => console.error(e),
    })
  }
  
getLocationAjax(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data!=null && rdata.data.length>0) {
              this.locations=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

getCardConfigAjax(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data!=null && rdata.data.length>0) {
              this.customerCards=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

getSubLocationAjax(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data!=null && rdata.data.length>0) {
              this.sublocations=rdata.data;
          }
       },
      error: (e) => console.error(e)
  })

}

// getLineSpaceAjax(){
//   let contactsparam='linespace'
//   this.service.getAjaxDropDown(contactsparam).subscribe({
//       next: (rdata) => {
//           if (rdata.data!=null && rdata.data.length>0) {
//             this.linespacemaster=rdata.data;
//           }
//        },
//       error: (e) => console.error(e),
//       // complete: () => console.info('complete') 
//   })
// }

getLineRackAjax(){
  let inputsparam='linerack'
  this.service.getAjaxDropDown(inputsparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
            this.linerackmaster=rdata.data;
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}

getPartMasterData(ajaxId:string):void{
    this.partmaster=[];
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata!=null && rdata.data) {
                  this.partmaster=rdata.data;
                  rdata.data.forEach((element:any) => {
                      this.partmaster.push(element);
                  });
            }
         },
        error: (e) => console.error(e) 
    })
}

getLineSpaceData():void{
  let contactsparam='linespaceloc&customerid='+this.masterData.customerId;
  this.customerlinespace=[];
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
            this.customerlinespace=rdata.data;
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
     
}

getLineRackData():void{
  let contactsparam='linerackloc&customerid='+this.masterData.customerId;
  this.customerlinerack=[];
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
            this.customerlinerack=rdata.data;
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
     
}


getLists():void{
  this.getCustomerContacts();
  this.getCustomerContracts();
  this.getCustomerParts();
  this.getCustomerLineSpace();
  this.getCustomerLineRack();
  this.getCustomerDocuments();
  this.getCustomerSoftwares();
  this.getCustomerManpowers();
  this.getCustomerAddress();
  this.getCustomerSpace();
  this.getCustomerPartDetail(this.masterData.customerId);
}

getCustomerContacts():void{
  let contactsparam='contacts&entityid='+this.masterData.customerId+'&entitytypeid=1';
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
              this.customerContactDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerContactDetails.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}
getCustomerContracts():void{
  let contractsparam='contracts&entityid='+this.masterData.customerId+'&entitytypeid=1';
  this.service.getAjaxDropDown(contractsparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
              this.customerContractDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerContractDetails.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}
getCustomerDocuments():void{
  let documentsparam='documents&entityid='+this.masterData.customerId+'&entitytypeid=1';
  this.service.getAjaxDropDown(documentsparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
              this.customerDocumentDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerDocumentDetails.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}

getCustomerAddress():void{
  let address_sparam='address&entityid='+this.masterData.customerId+'&entitytypeid=1';
  this.service.getAjaxDropDown(address_sparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
              this.customerCompAddressDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerCompAddressDetails.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}
getCustomerSoftwares():void{
  let softwaresparam='softwares&customerid='+this.masterData.customerId;
  this.service.getAjaxDropDown(softwaresparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
              this.customerSoftwareDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerSoftwareDetails.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}
getCustomerManpowers():void{
  let manpowersparam='manpowers&customerid='+this.masterData.customerId;
  this.service.getAjaxDropDown(manpowersparam).subscribe({
      next: (rdata) => {
          if (rdata!=null && rdata.data) {
              this.customerManpowerDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerManpowerDetails.push(element);
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
      // complete: () => console.info('complete') 
  })
}

getCustomerLineSpace():void{
  let contactsparam='customerlines&customerid='+this.masterData.customerId
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.customerSpaceDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerSpaceDetails.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}

getCustomerLineRack():void{
  let contactsparam='customerlinerack&customerid='+this.masterData.customerId
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.customerRackDetails=[];
              rdata.data.forEach((element:any) => {
                  this.customerRackDetails.push(element);
              });
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
  

updateCustomer(): void {


  if (this.IsNullorEmpty(this.masterData.customerName)) {
    this.toastr.info("Customer Name required! ");
    return;
  }

  if (this.IsNullorEmpty(this.masterData.customerErpCode)) {
    this.toastr.info("Customer Code Required !");
    return;
  }
  
  if (this.IsNullorEmpty(this.masterData.email)) {
    this.toastr.info("Email Required");
    return;
  }

  if (this.IsNullorEmpty(this.masterData.phone)) {
    this.toastr.info("Mobile required !");
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

  if (this.IsNullorEmpty(this.masterData.primarySubLocationId)) {
    this.toastr.info("Sublocation required");
    return;
  }
  if (this.IsNullorEmpty(this.masterData.barCodeConfigId)) {
    this.toastr.info("Barcode Config required");
    return;
  }

  // this.toastr.info(typeof(this.masterData.tatinMin));

  if (this.IsNullorEmpty(this.masterData.tatinMin)) {
    this.toastr.info("TAT Min required");
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
   data.barCodeConfigId=this.masterData.barCodeConfigId+"";
   data.tatinMin=this.masterData.tatinMin;
   data.vehicleType=this.masterData.vehicleType;
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
   data.supplymaxRotation=this.masterData.supplymaxRotation;
   data.customerUserId=this.masterData.customerUserId;
   data.profileImage=this.masterData.profileImage;
   data.locationId=this.masterData.locationId;
   data.primaryDockId=this.masterData.primaryDockId;


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
closeContractsModel(){
  this.showContractsDialog=false;
}
closePartsModel(){
  this.showPartsDialog=false;
}
// closeSpaceModel(){
//   this.showLineSpaceDialog=false;
// }
closeRackModel(){
  this.showLineRackDialog=false;
}
closeDocumentsModel(){
  this.showDocumentsDialog=false;
}
closeSoftwaresModel(){
  this.showSoftwaresDialog=false;
}
closeManpowersModel(){
  this.showManpowersDialog=false;
}
closeAddressModel(){
  this.showAddressDialog=false;
}

clearSpaceData(){
  this.spaceData={
    customerLineSpaceId:0,
    lineSpaceId:"",
    customerId:0,
    isActive:1
  }
}
clearRackData(){
  this.rackData={
    customerLineRackId:0,
    lineRackId:"",
    customerId:0,
    isActive:1
  }
}


clearPartData(){
  this.partData={
    customerPartId:'',
    customerId:'',
    partId:'',
    customerPartCode:"AUTO",
    cardConfigId:1,
    lineSpaceId:0,
    lineLotId:0,
    lineRackId:0,
    maxQty:1,
    isActive:1
  };
}



clearContatsData(){
  this.contactData={
    contactPersonName:'',
    departmentName:'',
    contactEmail:'',
    contactMobile:'',
    isSms:false,
    isMail:false,
    selectedCustomerId:0
  };
}

  clearContractData(){
    this.contractData={
      contractId:0,
      customerId:0,
      contractNo:'CONTRACT',
      startDate:'',
      endDate:'',
      customerSignOff:'',
      tbisSignOff:null,
      warehouseSpace:'',
      poNo:'',
      billingCycle:null,
      creditLimit:'',
      contractType:'',
      userId:0
    }
  }

  clearDocumentData(){
    this.documentData={
      documentId:0,
      customerId:0,
      userId:0
    };
    this.docPreview="";
  }
  clearSoftwareData(){
    this.softwareData={
      softwareId:0,
      customerId:0,
      userId:0
    }
  }
  clearManpowerData(){
    this.manpowerData={
      manpowerId:0,
      customerId:0,
      userId:0
    }
  }
  clearAddressData(){
    this.addressData={
      addressId:0,
      customerId:0,
      userId:0
    }
  }

 showContacts(){
    this.clearContatsData();
    this.showContactsDialog=true;
 }
 showContracts(){
   this.clearContractData();
   this.showContractsDialog=true;
 }
 showParts(){
  this.clearPartData();
  this.getLineSpaceData();
  this.getLineRackData();
  this.showPartsDialog=true;
}

showSpace(){
  this.clearSpaceData();
  // this.showLineSpaceDialog=true;
}
showRack(){
  this.clearRackData();
  this.showLineRackDialog=true;
}

showDocuments(){
  this.clearDocumentData();
  this.showDocumentsDialog=true;
}
showSoftwares(){
  this.clearSoftwareData();
  this.showSoftwaresDialog=true;
}
showManpowers(){
  this.clearManpowerData();
  this.showManpowersDialog=true;
}
showAddress(){
  this.clearAddressData();
  this.showAddressDialog=true;
}


getCustomerById(customerId: any): void {
  this.service.getCustomerById(customerId).subscribe({
    next: (rdata) => {
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
          this.masterData.barCodeConfigId=rdata.result.barCodeConfigId+"";
          this.masterData.tatinMin=rdata.result.tatinMin+"";
          this.masterData.vehicleType=rdata.result.vehicleType;
          this.masterData.bankAccountNo=rdata.result.bankAccountNo;
          this.masterData.ifscCode=rdata.result.ifscCode;
          this.masterData.bankName=rdata.result.bankName;
          this.masterData.contractStartDate=rdata.result.contractStartDate;
          this.masterData.contractEndDate=rdata.result.contractEndDate;
          this.masterData.primarySubLocationId=rdata.result.primarySubLocationId+"";
          this.masterData.kycVerified=rdata.result.kycVerified;
          this.masterData.approveBy=rdata.result.approveBy;
          this.masterData.approvedDate=rdata.result.approvedDate;
          this.masterData.active=rdata.result.active;
          this.masterData.supplymaxRotation=rdata.result.supplymaxRotation;
          this.masterData.customerUserId=rdata.result.customerUserId;
          this.masterData.profileImage=rdata.result.profileImage;
          this.preview='data:image/png;base64, '+rdata.result.profileImage;
          this.masterData.locationId=rdata.result.locationId+"";
          this.masterData.primaryDockId=rdata.result.primaryDockId+"";
          this.getLineSpaceData();
          this.getLineRackData();
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
        this.preview= this.noImage
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
            barCodeConfigId: "1",
            vehicleType:0,
            bankAccountNo: '',
            ifscCode: '',
            bankName: '',
            contractStartDate: '',
            contractEndDate: '',
            primarySubLocationId: null,
            kycVerified: true,
            approveBy: '',
            approvedDate: '',
            active: true,
            tatinMin:"",
            supplymaxRotation:"",
            locationId:null,
            sublocationId:null,
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

         
doContractAction(event: any){
  if(event){
      this.removeContracts(event.contractid.toString());
  }
}
doLineSpaceAction(event: any){
  if(event){
      this.removeSpace(event.customerlinespaceid,event.linespaceid);
  }
}
doLineRackAction(event: any){
  if(event){
      this.removeRack(event.customerlinerackid,event.linerackid);
  }
}
doCustomerPartAction(event: any){
  if(event){
      this.removeParts(event.customerpartid,event.partid);
  }
}
doAddressAction(event: any){
  if(event){
      this.removeAddress(event.addressid.toString());
  }
}
doContactAction(event: any){
  if(event){
      this.removeContacts(event.contactid.toString());
  }
}
doDocumentAction(event: any){
  if(event){
      this.removeDocuments(event.documentid.toString());
  }
}
doSoftwareAction(event: any){
  if(event){
      this.removeSoftwares(event.softwareid.toString());
  }
}
doManpowerAction(event: any){
  if(event){
      this.removeManpowers(event.manpowerid);
  }
}
  


      updateCustomerContact(): void {

        if (this.IsNullorEmpty(this.contactData.contactPersonName)) {
            this.toastr.info("Please enter Contact Person Name");
            return;
        }
        if (this.IsNullorEmpty(this.contactData.departmentName)) {
          this.toastr.info("Please enter department name");
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
           data.entityId=this.masterData.customerId;
           data.entityTypeId=1;
           data.contactPersonName=this.contactData.contactPersonName;
           data.departmentName=this.contactData.departmentName;
           data.contactEmail=this.contactData.contactEmail;
           data.contactMobile=this.contactData.contactMobile;
           data.isSms=this.contactData.isSms;
           data.isMail=this.contactData.isMail;
           data.isActive=true;
           data.userId=this.gVar.userId;

           this.commonService.updateEntityContact(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    this.closeContactsModel();
                    this.getCustomerContacts();
                    this.customercontactreport.getReportData(true);
                    this.action="Edit";
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
      }
      removeContacts(id:any) {

        let data:any={};
        data.contactId=id;
        data.entityId=this.masterData.customerId;
        data.contactPersonName="";
        data.departmentName="";
        data.contactEmail="";
        data.contactMobile="";
        data.isSms=0;
        data.isMail=0;
        data.userId=this.gVar.userId;

        this.commonService.updateEntityContact(data).subscribe({
         next: (rdata) => {
             if (rdata.isSuccess) {
                 this.toastr.success(rdata.message);
                 this.getCustomerContacts();
                 this.customercontactreport.getReportData(true);
                 this.action="Edit";
             } else {
                 this.toastr.warning(rdata.message);
             }
         },    
         error: (err) => { this.toastr.error(err.message) },  
         
         }) 
      }
      updateCustomerContract(): void {


          if (this.IsNullorEmpty(this.contractData.startDate)) {
            this.toastr.info("Please enter start date");
            return;
          }
          
          if (this.IsNullorEmpty(this.contractData.endDate)) {
            this.toastr.info("Please enter end date");
            return;
          }
          if (this.IsNullorEmpty(this.contractData.poNo)) {
            this.toastr.info("Please enter PO number");
            return;
          }
          
          let randomnum = Math.floor(100000 + Math.random() * 900000)

          let data:any={};
           data.contractId=this.contractData.contractId==""?"0":this.contractData.contractId;
           data.entityId=this.masterData.customerId;
           data.entityTypeId=1;
           data.contractNo="CONTRACT" + randomnum;
           data.startDate=this.contractData.startDate;
           data.endDate=this.contractData.endDate;
           data.customerSignOff=this.contractData.customerSignOff;
           data.tbisSignOff=this.contractData.tbisSignOff+'';
           data.warehouseSpace=this.contractData.warehouseSpace;
           data.poNo=this.contractData.poNo;
           data.billingCycle=this.contractData.billingCycle;
           data.creditLimit=this.contractData.creditLimit;
           data.contractType=this.contractData.contractType;
           data.active=true;
           data.userId=this.gVar.userId;

           this.commonService.updateEntityContracts(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    this.closeContractsModel();
                    this.getCustomerContracts();
                    this.customercontractreport.getReportData(true);
                    this.action="Edit";
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
        
      }

      removeContracts(id:any) {
        let data:any={};
           data.contractId=id;
           data.entityId=this.masterData.customerId;
           data.contractNo="0";
           data.startDate="";
           data.endDate="";
           data.customerSignOff="";
           data.tbisSignOff=null;
           data.warehouseSpace="";
           data.poNo="0"; 
           data.billingCycle=null;
           data.creditLimit="";
           data.contractType="";
           data.userId=this.gVar.userId;
           this.commonService.updateEntityContracts(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    this.getCustomerContracts();
                    this.customercontractreport.getReportData(true);
                    this.action="Edit";
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
      }

      
    updateCustomerDocument(): void {


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
         data.entityId=this.masterData.customerId;
         data.entityTypeId=1;
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
                  this.getCustomerDocuments();
                  this.customerdocumentreport.getReportData(true);
                  this.action="Edit";
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 
      
    }

    removeDocuments(id:any) {
      let data:any={};
         data.documentId=id;
         data.entityId=this.masterData.customerId;
         data.userId=this.gVar.userId;
         this.commonService.updateEntityDocuments(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
                  this.toastr.success(rdata.message);
                  this.getCustomerDocuments();
                  this.customerdocumentreport.getReportData(true);
                  this.action="Edit";
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 
    }

  updateCustomerSoftware(): void {

      if (this.IsNullorEmpty(this.softwareData.softwareName)) {
        this.toastr.info("Please enter software name");
        return;
      }
      
      let data:any={};
       data.softwareId=this.softwareData.softwareId==""?"0":this.softwareData.softwareId;
       data.customerId=this.masterData.customerId;
       data.softwareName=this.softwareData.softwareName;
       data.softwareUrl=this.softwareData.softwareUrl;
       data.softwareUserName=this.softwareData.softwareUserName;
       data.softwarePassword=this.softwareData.softwarePassword;
       data.active=true;
       data.userId=this.gVar.userId;

       this.service.updateCustomerSoftwares(data).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closeSoftwaresModel();
                this.getCustomerSoftwares();
                this.customersoftwarereport.getReportData(true);
                this.action="Edit";
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        }) 
    
  }

  updateCustomerManpower(): void {

    if (this.IsNullorEmpty(this.manpowerData.manpowerDetail)) {
      this.toastr.info("Please enter name");
      return;
    }
    
    let data:any={};
     data.manpowerId=this.manpowerData.manpowerId==""?"0":this.manpowerData.manpowerId;
     data.customerId=this.masterData.customerId;
     data.manpowerDetail=this.manpowerData.manpowerDetail;
     data.shiftA=this.manpowerData.shiftA;
     data.shiftB=this.manpowerData.shiftB;
     data.categoryId=this.manpowerData.categoryId;
     data.active=true;
     data.userId=this.gVar.userId;

     this.service.updateCustomerManpowers(data).subscribe({
      next: (rdata) => {
          if (rdata.isSuccess) {
              this.toastr.success(rdata.message);
              this.closeManpowersModel();
              this.getCustomerManpowers();
              this.customermanpowerreport.getReportData(true);
              this.action="Edit";
          } else {
              this.toastr.warning(rdata.message);
          }
      },    
      error: (err) => { this.toastr.error(err.message) },  
      
      }) 
  
}

  removeSoftwares(id:any) {
    let data:any={};
       data.softwareId=id;
       data.customerId=this.masterData.customerId;
       data.userId=this.gVar.userId;
       this.service.updateCustomerSoftwares(data).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.getCustomerSoftwares();
                this.customersoftwarereport.getReportData(true);
                this.action="Edit";
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        }) 
  }

  
  removeManpowers(id:any) {
    let data:any={};
       data.manpowerId=id;
       data.customerId=this.masterData.customerId;
       data.userId=this.gVar.userId;
       this.service.updateCustomerManpowers(data).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.getCustomerManpowers();
                this.customermanpowerreport.getReportData(true);
                this.action="Edit";
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        }) 
  }

  updateCustomerAddress(): void {

    if (this.IsNullorEmpty(this.addressData.caAddress)) {
      this.toastr.info("Please enter address");
      return;
    }
    
    let data:any={};
     data.addressId=this.addressData.addressId==""?"0":this.addressData.addressId;
     data.entityId=this.masterData.customerId;
     data.entityTypeId=1;
     data.address=this.addressData.caAddress;
     data.city=this.addressData.caCity;
     data.district=this.addressData.caDistrict;
     data.state=this.addressData.caState;
     data.lat=this.addressData.caLat;
     data.lang=this.addressData.caLang;
     data.gstIn=this.addressData.caGstIn;
     data.email=this.addressData.caEmail;
     data.pincode=this.addressData.caPincode;
     data.phoneNo=this.addressData.caPhoneNo;
     data.website=this.addressData.caWebsite;
     data.addressType=this.addressData.caAddressType;
     data.active=true;
     data.userId=this.gVar.userId;

     this.commonService.updateEntityAddress(data).subscribe({
      next: (rdata) => {
          if (rdata.isSuccess) {
              this.toastr.success(rdata.message);
              this.closeAddressModel();
              this.getCustomerAddress();
              this.customeraddressreport.getReportData(true);
              this.action="Edit";
          } else {
              this.toastr.warning(rdata.message);
          }
      },    
      error: (err) => { this.toastr.error(err.message) },  
      
      }) 
  
}

  removeAddress(id:any) {
    let data:any={};
       data.addressId=id;
       data.entityId=this.masterData.customerId;
       data.userId=this.gVar.userId;
       this.commonService.updateEntityAddress(data).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.getCustomerAddress();
                this.customeraddressreport.getReportData(true);
                this.action="Edit";
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        }) 
  }

      updateCustomerPartsLineMap(): void {

          if (this.IsNullorEmpty(this.partData.partId)) {
              this.toastr.info("Please Select Part Name");
              return;
          }
          if(this.masterData.barCodeConfigId!="1"){
            if(this.IsNullorEmpty(this.partData.customerPartCode)){
              this.toastr.info("Customer PartCode Required");
              return;
            }
          }else{
            this.partData.cardConfigId=1;
            this.partData.customerPartCode="AUTO";
          }

          if (this.IsNullorEmpty(this.partData.customerlineSpaceId)) {
              this.toastr.info("Line space required. check line space already mapped or not");
              return;
          }
        
          let data:any={};
           data.customerPartId=0;
           data.customerId=this.masterData.customerId;
           data.partId=this.partData.partId;
           data.customerPartCode=this.partData.customerPartCode;
           data.cardConfigId=this.partData.cardConfigId;
           data.lineSpaceId=this.partData.customerlineSpaceId;
           data.lineLotId=0;
           data.lineRackId=0;
           data.maxQty=this.partData.maxQty;
           data.isActive=1;

           this.service.updateCustomerPartsLineMap(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    this.closePartsModel();
                    this.getLineSpaceData();
                    this.getCustomerParts();
                    this.customerpartreport.getReportData(true);
                    this.action="edit";
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
      }

      removeParts(id:number,partId:number) {
        let data:any={};
           data.customerPartId=id;
           data.customerId=this.masterData.customerId;
           data.partId=partId;
           data.customerPartCode=""
           data.cardConfigId="";
           data.lineSpaceId=0;
           data.lineLotId=0;
           data.lineRackId=0;
           data.maxQty=0;
           data.isActive=0;

           this.service.updateCustomerPartsLineMap(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    this.getCustomerParts();
                    this.customerpartreport.getReportData(true);
                    this.action="Edit";
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
        
        // this.toastr.info(id);
     
      }
      updateCustomerLineSpaceMap(): void {

          if (this.IsNullorEmpty(this.spaceData.lineSpaceId)) {
              this.toastr.info("Please Select Line Space");
              return;
          }

          let data:any={};
           data.customerLineSpaceId=this.spaceData.customerSpaceId==""?"0":this.spaceData.customerSpaceId;
           data.customerId=this.masterData.customerId;
           data.lineSpaceId=this.spaceData.lineSpaceId;
           data.startCol=this.spaceData.startCol;
           data.endCol=this.spaceData.endCol;
           data.isActive=1;
           this.service.updatePartsLineMap(data).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                    this.toastr.success(rdata.message);
                    // this.closeSpaceModel();
                    this.getCustomerLineSpace();
                    this.linespacereport.getReportData(true);
                    this.action="Edit";
                } else {
                    this.toastr.warning(rdata.message);
                }
            },    
            error: (err) => { this.toastr.error(err.message) },  
            
            }) 
      }
      removeSpace(id:number,spaceId:number) {

        let data:any={};
         data.customerLineSpaceId=id
         data.customerId=this.masterData.customerId;
         data.lineSpaceId=spaceId;
         data.isActive=0;
         this.service.updatePartsLineMap(data).subscribe({
          next: (rdata) => {
              if (rdata.isSuccess) {
                  this.toastr.success(rdata.message);
                  this.getCustomerLineSpace();
                  this.linespacereport.getReportData(true);
                  this.action="Edit";
              } else {
                  this.toastr.warning(rdata.message);
              }
          },    
          error: (err) => { this.toastr.error(err.message) },  
          
          }) 

}


updateCustomerLineRackMap(): void {

  if (this.IsNullorEmpty(this.rackData.lineRackId)) {
      this.toastr.info("Please Select Line Rack");
      return;
  }

  let data:any={};
   data.customerLineRackId=this.rackData.customerRackId==""?"0":this.rackData.customerRackId;
   data.customerId=this.masterData.customerId;
   data.lineRackId=this.rackData.lineRackId;
   data.isActive=1;
   this.service.updateCustomerLineRackMap(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.closeRackModel();
            this.getCustomerLineRack();
            this.linerackreport.getReportData(true);
            this.action="Edit";
        } else {
            this.toastr.warning(rdata.message);
        }
    },    
    error: (err) => { this.toastr.error(err.message) },  
    
    }) 
}
removeRack(id:number,rackId:number) {

let data:any={};
 data.customerLineRackId=id
 data.customerId=this.masterData.customerId;
 data.lineRackId=rackId;
 data.isActive=0;
 this.service.updateCustomerLineRackMap(data).subscribe({
  next: (rdata) => {
      if (rdata.isSuccess) {
          this.toastr.success(rdata.message);
          this.getCustomerLineRack();
          this.linerackreport.getReportData(true);
          this.action="Edit";
      } else {
          this.toastr.warning(rdata.message);
      }
  },    
  error: (err) => { this.toastr.error(err.message) },  
  
  }) 

}




fileChangeEvent(fileInput: any) {
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
         this.preview=imgBase64Path;
         imgBase64Path = imgBase64Path.replace('data:image/png;base64,','')
         .replace('data:image/jpeg;base64,','')
         .replace('data:application/pdf;base64,','')
         .replace('data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,','')
         .replace('data:application/msword;base64,','');
        //  this.masterData.fileName=fileInput.target.files[0].name;
         this.masterData.profileImage=imgBase64Path;
   };

   reader.readAsDataURL(fileInput.target.files[0]);
}
   
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

down(b:any,e:any) {
  if(e.button!=0){
    return;
  }
  e.preventDefault();
  if(this.active){
    for(let i=0;i<this.spaceDetails.length;i++){
      for(let j=0;j<this.spaceDetails[i].length;j++){
        this.spaceDetails[i].columns[j].isChecked=false;
      }
    }  
    return;
  }
  for(let i=0;i<this.spaceDetails.length;i++){
    for(let j=0;j<this.spaceDetails[i].length;j++){
      this.spaceDetails[i].columns[j].isChecked=false;
    }
  }
  this.startCell=b.columnNo;  
  this.startRow=b.lineNo;
  this.cellData.fromLineSpaceId=b.lineSpaceId;
   this.active = true
  //  if (this.active)
  //    b.isChecked = !b.isChecked
 }

 over(b:any,e:any) {
  e.preventDefault();  
  if(this.active){
  let endCell=b.columnNo;  
  let endRow=b.lineNo;
  let totalRows=this.spaceDetails.length;
  // console.log(this.endCell+" "+this.endRow+" "+this.startCell+" "+this.startRow);
  // console.log((totalRows-(this.endRow-this.minLineNo+1))+" "+(totalRows-(this.startRow-this.minLineNo+1)));
  for(let i=(totalRows-(endRow-this.minLineNo+1));i<=(totalRows-(this.startRow-this.minLineNo+1));i++){
      for(let j=this.startCell-this.minColumnNo;j<endCell-this.minColumnNo+1;j++){
        this.spaceDetails[i].columns[j].isChecked=true;
      }
   }
  }
 }

 up(b:any,e:any) {
  if(!this.active){
    e.preventDefault();
    return;
  }
  this.endCell=b.columnNo;  
  if(this.startRow>b.lineNo){
    this.endRow=this.startRow;
    this.startRow=b.lineNo;
  }else{
    this.endRow=b.lineNo;
  }
  console.log(this.endCell+" "+this.endRow+" "+this.startCell+" "+this.startRow);

  this.cellData.startCell=this.startCell;
  this.cellData.endCell=this.endCell;
  this.cellData.startRow=this.startRow;
  this.cellData.endRow=this.endRow;
  this.cellData.toLineSpaceId=b.lineSpaceId;
  let totalRows=this.spaceDetails.length;
  //  this.active = false
  // alert(this.endCell+" "+this.endRow+" "+this.startCell+" "+this.startRow+" "+b.lineSpaceId);
  // alert(this.cellData.endCell+" "+this.cellData.endRow+" "+this.cellData.startCell+" "+this.cellData.startRow+" "+this.cellData.toLineSpaceId);
  // console.log((totalRows-(this.endRow-this.minLineNo+1))+" "+(totalRows-(this.startRow-this.minLineNo+1)));
  for(let i=(totalRows-(this.endRow-this.minLineNo+1));i<=(totalRows-(this.startRow-this.minLineNo+1));i++){
     for(let j=this.startCell-this.minColumnNo;j<this.endCell-this.minColumnNo+1;j++){
       this.spaceDetails[i].columns[j].isChecked=true;
     }
  }
   this.showCellDetailsDialog=true;
 }
 closeCellDetailsModel(){
  this.showCellDetailsDialog=false;
  this.active=false;
  for(let i=0;i<this.spaceDetails.length;i++){
    for(let j=0;j<this.spaceDetails[i].length;j++){
      this.spaceDetails[i].columns[j].isChecked=false;
    }
  }
  this.clearCellData()
}       
getCustomerSpace():void{
  this.spaceDetails=[];
  this.service.getCustomerSubLocationSpace(this.masterData.customerId,this.masterData.primarySubLocationId).subscribe({
    next: (sdata:any) => {
        if (sdata!=null && sdata.length>0) {
                this.spaceDetails=[];
                this.minLineNo=sdata[0].lineNo;
                this.maxLineNo=sdata[0].lineNo
                this.minColumnNo=sdata[0].columnNo;
                this.maxColumnNo=sdata[0].columnNo;
                for(let s=0;s<sdata.length;s++ ){
                    if(this.minLineNo>sdata[s].lineNo){
                      this.minLineNo=sdata[s].lineNo;
                    }
                    if(this.maxLineNo<sdata[s].lineNo){
                      this.maxLineNo=sdata[s].lineNo;
                    }
                    if(this.minColumnNo>sdata[s].columnNo){
                      this.minColumnNo=sdata[s].columnNo;
                    }
                    if(this.maxColumnNo<sdata[s].columnNo){
                      this.maxColumnNo=sdata[s].columnNo;
                    }
                }
                console.log(this.minLineNo+" "+this.maxLineNo+" "+this.minColumnNo+" "+this.maxColumnNo);
                for (let _i = this.maxLineNo; _i >= this.minLineNo; _i--) {
                  let tmp:any = [];
                    for (let _j = this.minColumnNo; _j <=  this.maxColumnNo; _j++) {
                      //tmp.push(sdata.find((x:any)=>x.lineNo==_i && x.columnNo==_j));
                      let d=sdata.find((x:any)=>x.lineNo==_i && x.columnNo==_j);
                      if(d==null){
                        d= {
                          "spaceId": 0,
                          "subLocationId": 0,
                          "spaceName": "",
                          "colorCode": "",
                          "lineNo": _i,
                          "columnNo": _j,
                          "lineUsageId": 0,
                          "lineSpaceId": 0,
                          "customerid": 0,
                          "partId": 0,
                          "userId": 0,
                          "startCell": 0,
                          "endCell": 0,
                          "startRow": 0,
                          "endRow": 0,
                          "fifoOrder": 0,
                          "maxBins": 0,
                          "fromLineSpaceId": 0,
                          "toLineSpaceId": 0,
                          "partSpaceName": null,
                          "spaceOccupation": 0,
                          "parts": null
                      };
                      tmp.push(d);
                      }else{
                        tmp.push(d);
                      }
                    }                            
                    this.spaceDetails.push({
                        columns: tmp
                    });
                 }
               //  console.log(JSON.stringify(this.spaceDetails));
        } else {
          // this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err:any) => { this.toastr.warning(err);  },    

  });

}
clearCellData(){
  this.cellData.startCell=0;
    this.cellData.endCell=0;
    this.cellData.startRow=0;
    this.cellData.endRow=0;
    this.cellData.customerid=0;
    this.cellData.colorCode="";
    this.cellData.lineUsageId=0;
    this.cellData.partId=0;
    this.cellData.fifoOrder=0;
    this.cellData.fromLineSpaceId=0;
    this.cellData.toLineSpaceId=0;
    this.cellData.maxBins=0;
    this.cellData.partSpaceName=''; 
    this.cellData.subLocationId=0;
    this.cellData.customerid=0;
    this.cellData.spaceOccupation=0;
    this.cellData.parts=[];
    this.selectedPartDetails=[];
}

updateLineCellDetail():void{
  this.cellData.customerid=this.masterData.customerId;
  this.cellData.parts=[];
  this.selectedPartDetails.forEach((element:any) => {
    let part:any={};
    part.partId=element.partid;
    this.cellData.parts.push(part);
  });
  this.service.checkCustomerSpaceAllocation(this.cellData).subscribe({
    next:(rdata)=>{
        if (rdata.isSuccess){
          this.service.updateCustomerSpaceAllocation(this.cellData).subscribe({
            next: (rdata:any) => {
              if (rdata.isSuccess) {
                this.closeCellDetailsModel();
                this.getCustomerSpace();
              } else{
                this.toastr.warning(rdata.message);                
              }
            },   
            error: (err) => { this.toastr.error(err.message) } 
          }) 
        }else{
          this.toastr.warning(rdata.message);
        }
    },
    error: (err:any) => { this.toastr.error(err.message) } 
  })
}

getCustomerPartDetail(customerId:number):void{
  this.customerPartMapDetails=[];
  let contactsparam='picklistcustparts&customerid='+customerId;
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.customerPartMapDetails=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}
getLineUsageData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.usageTypes=[];
              rdata.data.forEach((element:any) => {
                  this.usageTypes.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })

}
onChangeUsageType(e:any){
  let lineUsageData:any=JSON.parse(JSON.stringify(this.usageTypes.find((x:any)=>x.usageid==e.usageid)));

  this.cellData.colorCode=lineUsageData.colorcode;
  this.cellData.subLocationId=this.masterData.primarySubLocationId
  this.cellData.partId=0;
  this.cellData.fifoOrder=0;
  this.cellData.maxBins=0; 
  this.cellData.partSpaceName='';
  this.cellData.parts=[];
  this.selectedPartDetails=[];
  this.cellData.spaceOccupation=0;
}
removeSelectedPart(index:number){
  this.selectedPartDetails.splice(index,1); 
}
addSelectedPart():void{
  if(this.cellData.spaceOccupation==1 && this.selectedPartDetails.length==1){
    this.toastr.warning("Only one part can be added");
    return;
  }else if(this.cellData.spaceOccupation==3){
    this.selectedPartDetails=[];
    this.toastr.warning("Free space applies for all customer parts");
    return;
  }
  this.selectedPartDetails.push(this.customerPartMapDetails.find((x:any)=>x.partid==this.cellData.partId));
}
showCustomerDetail(b:any,e:any){
  e.preventDefault();
  this.getSpaceData('linespacedetail',b.spaceId);
  return false;
}
getSpaceData(ajaxId:string,spaceId:any):void{
  this.service.getAjaxDropDown(ajaxId+'&spaceid='+spaceId).subscribe({
      next: (rdata) => {
          if (rdata.data && rdata.data.length>0) {
              this.spaceDetail=rdata.data[0];
              if(window.confirm("Do you want to remove the mapping for the "+this.spaceDetail.usagename)){
                let spaceData={
                  customerid:this.spaceDetail.customerid,
                  subLocationId:this.spaceDetail.sublocationid,
                  lineUsageId:this.spaceDetail.usageid
                }                
                this.service.removeCustomerSpaceAllocation(spaceData).subscribe({
                  next: (rdata) => {
                      if (rdata.isSuccess) {
                        this.getCustomerSpace();                              
                      } else{
                        this.toastr.warning(rdata.message);                
                      }
                    },   
                    error: (err) => { this.toastr.error(err.message) } 
                  });
              }
          }else{
              this.spaceDetail={};
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })

}

onMouseEnter(e:any) {
  let getData =  e.target.getAttribute("title");
  console.log("mouse enter : "+getData);
  const list: HTMLCollectionOf<Element> = document.getElementsByClassName("spacemaster");
  console.log("mouse enter : "+list);
  if(getData!="Customer Area\r"){
    for(let i=0;i<=list.length;i++){
      if(list[i]){
        list[i].classList.remove('zoomBox');
        if(list[i].getAttribute("title")==getData){
          // list[i].setAttribute("style", "transform:scale(1.5);");
          list[i].classList.add('zoomBox');
        }
      }
    }
  }
}

}