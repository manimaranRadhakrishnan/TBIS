import { Component,Input,OnInit,AfterViewInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import { MastersService } from '../../../services/masters.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';
import { PackingTypesDDL, UnloadDocDDL } from '../../../models/masters';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-partmaster',
  standalone: false,
  templateUrl: './partmaster.component.html',
  styleUrl: './partmaster.component.css'
})

export class PartmasterComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    partlocationData:any={};
    partAssemblyData:any={};
    finishgoodsparts:any={};
    partDispatchType:any={};
    form: any = {};
    action:string="view";
    packingtype!: PackingTypesDDL[];
    unloaddocs!: UnloadDocDDL[];
    locations:any=[];
    deliverylocations:any=[];
    deliveryPlantsDDL!:any[] 
    deliveryDocksDDL!:any[];

    partDeliveryLocationDetails:any=[];
    partAssemblyDetails:any=[];
    showPartAssemblyDlg:boolean=false;
    showPartDLocationDlg:boolean=false;
    customerDetails:any=[];

    eventsSubject: Subject<void> = new Subject<void>();
     constructor(private confirmDialogService: ConfirmDialogService,
    private GlobalVariable: AppGlobal,
    private router: Router, 
    private authService: AuthService, 
    private tokenStorage: TokenStorageService,
    private service:MastersService,
    private toastr:ToastrService) { }



    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.partId="";
        this.masterData.partNo="";
        this.masterData.customerPartNo="";
        this.masterData.partDescription="";
        this.masterData.packingTypeId="";
        this.masterData.spq="";
        this.masterData.noOfStack="";
        this.masterData.noOfPackingInPallet="";
        this.masterData.packingHeight="";
        this.masterData.packingWidth="";
        this.masterData.packingLength="";
        this.masterData.packingWeight="";
        this.masterData.palletHeight="";
        this.masterData.palletWidth="";
        this.masterData.palletLength="";
        this.masterData.palletWeight="";
        this.masterData.dispatchType='1';
        this.masterData.loadingType='1';
        this.masterData.rePack="";
        this.masterData.m2="";
        this.masterData.m3="";
        this.masterData.unloadDockId="";
        this.masterData.active=true;
        this.masterData.binQty='';
        this.masterData.locationId=null;
        this.masterData.customerId=null;
        this.masterData.reqInspection=false;
        this.masterData.deliveryLocationId=null;
        this.masterData.plantName='';
        this.masterData.unloadingDock='';
        this.masterData.repackQty='';
        this.masterData.partWeight='';
        this.masterData.palletWeightWithPart='';
        this.masterData.opackingTypeId=null;
        this.masterData.onoOfStack='';
        this.masterData.oBinQty='';
        this.masterData.pNoOfStack='';
        this.masterData.opalletLength='';
        this.masterData.opalletWidth='';
        this.masterData.opalletHeight='';
        this.masterData.opalletWeight='';
        this.masterData.opalletWeightWithProduct='';
        this.masterData.customerId='';
        this.masterData.onoOfPackingInPallet='';
      }

}


  ngOnInit(): void {
    this.getAjaxData('packing');
    this.getAjaxData1('unloaddoc');
    this.getAjaxData2('warehouse');
    this.getAjaxData3('deliverylocation');
   this.getAjaxCustomers('customer');
    this.getDeliveryPlant();
    this.getDeliveryDock();
    this.clearPartLocationData();
    this.clearPartAssemblyData();
  }

  getList():void{
    this.getDeliveryLocationDetails();
    this.getPartAssemblyDetails();
    this.getAjaxPartGoodsFinish('finishgoodspart');
  }

  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "") {
        return true;
    }
    return false;
    }

getAjaxPartGoodsFinish(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.finishgoodsparts=rdata.data;
            }
            },
        error: (e) => console.error(e),
    })
}

getAjaxCustomers(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.customerDetails=rdata.data;
            }
            },
        error: (e) => console.error(e),
    })
    }
getAjaxData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.packingtype=rdata.data;
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })

}

getAjaxData1(ajaxId:string):void{
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
getAjaxData2(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.locations=rdata.data;
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })

}

getAjaxData3(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.deliverylocations=rdata.data;
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })
}

getDeliveryPlant():void{
    this.service.getAjaxDropDown("plants").subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.deliveryPlantsDDL=rdata.data;
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })
}


getDeliveryDock():void{
    this.service.getAjaxDropDown("docks").subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.deliveryDocksDDL=rdata.data;
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })
}

getDeliveryLocationDetails(){
    let contactsparam='deliverylocationdtl&&partid='+this.masterData.partId;
    this.service.getAjaxDropDown(contactsparam).subscribe({
        next: (rdata) => {
            if (rdata.data) {
              this.partDeliveryLocationDetails=rdata.data;
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })
  }

  getPartAssemblyDetails(){
    let contactsparam='partsassemblydtl&partid='+this.masterData.partId;
    this.service.getAjaxDropDown(contactsparam).subscribe({
        next: (rdata) => {
            if (rdata.data) {
              this.partAssemblyDetails=rdata.data;
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })
  }

  showDelivery() {
    this.showPartDLocationDlg=true;
  }
  closePartDLocationDlg() {
       this.showPartDLocationDlg=false;
  }



removeDeliveryLocation(transid: number) {
    let data:any={};
    data.partDeliveryDockId=transid;
    data.partId=this.masterData.partId;
    data.dLocationId=this.partlocationData.dLocationId;
    data.plantId=this.partlocationData.plantId;
    data.plantDockId=this.partlocationData.plantDockId;
    data.isActive=true;
    data.userId=this.GlobalVariable.userId;
 
    this.service.updatePartDeliveryLocation(data).subscribe({
     next: (rdata) => {
         if (rdata.isSuccess) {
             this.toastr.success(rdata.message);
             this.getDeliveryLocationDetails();
             this.action="Edit";
         } else {
             this.toastr.warning(rdata.message);
         }
     },    
     error: (err) => { this.toastr.error(err.message) },  
     
     }) 
}


showAssembly() {
        this.showPartAssemblyDlg=true;
    }
closeAssemblyDlg() {
    this.showPartAssemblyDlg=false;
}

removePartAssembly(transid: number) {
    let data:any={};
    data.partassemblyId=transid;
    data.assemblyPartname=this.partAssemblyData.assemblyPartname;
    data.assemblyPartcode=this.partAssemblyData.assemblyPartcode;
    data.qty=this.partAssemblyData.qty;
    data.primaryPartid=this.masterData.partId;
    data.isActive=true;
    data.userId=this.GlobalVariable.userId;
 
    this.service.updatePartAssembly(data).subscribe({
     next: (rdata) => {
         if (rdata.isSuccess) {
             this.toastr.success(rdata.message);
             this.getPartAssemblyDetails();
             this.action="Edit";
         } else {
             this.toastr.warning(rdata.message);
         }
     },    
     error: (err) => { this.toastr.error(err.message) },  
     
     }) 
 
}


updatePartMaster(): void {

if (this.IsNullorEmpty(this.masterData.customerPartNo)) {
    this.toastr.info("Please enter Customer Part No");
    return;
}
if (this.IsNullorEmpty(this.masterData.partNo)) {
    this.toastr.info("Please enter Alternate Part No");
    return;
}
if (this.IsNullorEmpty(this.masterData.partDescription)) {
    this.toastr.info("Please enter Part Description");
    return;
}

if (this.IsNullorEmpty(this.masterData.dispatchType)) {
    this.toastr.info("Please select Dispatch Type");
    return;
}
if (this.IsNullorEmpty(this.masterData.locationId)) {
    this.toastr.info("Please select Warehouse Location");
    return;
}
if (this.IsNullorEmpty(this.masterData.customerId)) {
    this.toastr.info("select Customer");
    return;
}
if (this.IsNullorEmpty(this.masterData.packingTypeId)) {
    this.toastr.info("Please Select packing");
    return;
}

if (this.IsNullorEmpty(this.masterData.spq)) {
    this.toastr.info("Please enter packing inward SPQ");
    return;
}
if (this.IsNullorEmpty(this.masterData.binQty)) {
    this.toastr.info("Please enter SPQ In Packing");
    return;
}
if (this.IsNullorEmpty(this.masterData.noOfPackingInPallet)) {
    this.toastr.info("Please enter No.of Packing on pallet");
    return;
}
if (this.IsNullorEmpty(this.masterData.partWeight)) {
    this.toastr.info("Please enter Weight Per Part in Kg");
    return;
}

if (this.IsNullorEmpty(this.masterData.noOfStack)) {
    this.toastr.info("Please enter Bin Stack Permit");
    return;
}

if (this.IsNullorEmpty(this.masterData.opackingTypeId)) {
    this.toastr.info("Please select Outward Packing");
    return;
}
if (this.IsNullorEmpty(this.masterData.repackQty)) {
    this.toastr.info("Please enter packing outward spq");
    return;
}
if (this.IsNullorEmpty(this.masterData.onoOfStack)) {
    this.toastr.info("Please enter Bin Stack Permit");
    return;
}
if (this.IsNullorEmpty(this.masterData.oBinQty)) {
    this.toastr.info("Please enter Outward Bin Qty");
    return;
}
if (this.IsNullorEmpty(this.masterData.onoOfPackingInPallet)) {
    this.toastr.info("Please enter Outward No.of BIN in pallet");
    return;
}
if (this.IsNullorEmpty(this.masterData.pNoOfStack)) {
    this.toastr.info("Please enter Pallet Stack Permit");
    return;
}
if (this.IsNullorEmpty(this.masterData.customerId)) {
    this.toastr.info("Customer Required");
    return;
}


  let data:any={};
   data.partId=this.masterData.partId==""?"0":this.masterData.partId;
   data.customerPartNo=this.masterData.customerPartNo;
   data.partNo=this.masterData.partNo;
   data.partDescription=this.masterData.partDescription;
   data.packingTypeId=this.masterData.packingTypeId;
   data.spq=this.masterData.spq;
   data.noOfStack=this.masterData.noOfStack;
   data.noOfPackingInPallet=this.masterData.noOfPackingInPallet;
   data.packingHeight=this.masterData.packingHeight;
   data.packingWidth=this.masterData.packingWidth;
   data.packingLength=this.masterData.packingLength;
   data.packingWeight=this.masterData.packingWeight;
   data.palletHeight=this.masterData.palletHeight;
   data.palletWidth=this.masterData.palletWidth;
   data.palletLength=this.masterData.palletLength;
   data.palletWeight=this.masterData.palletWeight;
   data.dispatchType=this.masterData.dispatchType;
   data.loadingType=this.masterData.loadingType;
   data.rePack=this.masterData.rePack;
   data.m2=this.masterData.m2;
   data.m3=this.masterData.m3;
   data.unloadDockId=this.masterData.unloadDockId;
   data.binQty=this.masterData.binQty;
   data.locationId=this.masterData.locationId;
   data.customerId=this.masterData.customerId;
   data.reqInspection=this.masterData.reqInspection;
   data.deliveryLocationId=this.masterData.deliveryLocationId;
   data.plantName=this.masterData.plantName;
   data.unloadingDock=this.masterData.unloadingDock;
   data.repackQty=this.masterData.repackQty;
   data.opackingWeight=this.masterData.partWeight;
   data.packingWeightWithPart=this.masterData.palletWeightWithPart;
   data.opackingTypeId=this.masterData.opackingTypeId;
   data.onoOfStack=this.masterData.onoOfStack;
   data.oBinQty=this.masterData.oBinQty;
   data.pNoOfStack=this.masterData.pNoOfStack;
   data.opalletLength=this.masterData.opalletLength;
   data.opalletWidth=this.masterData.opalletWidth;
   data.opalletHeight=this.masterData.opalletHeight;
   data.opalletWeight=this.masterData.opalletWeight;
   data.opackingWeightWithPart=this.masterData.opalletWeightWithProduct;
   data.active=this.masterData.active;
   data.userId=this.GlobalVariable.userId;
   data.customerId=this.masterData.customerId;
   data.onoOfPackingInPallet=this.masterData.onoOfPackingInPallet;

   this.service.updatePartMaster(data).subscribe({
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

updateDeliveryLocations(): void {
    if (this.IsNullorEmpty(this.partlocationData.dLocationId)) {
        this.toastr.info("Please select Location");
        return;
    }
    if (this.IsNullorEmpty(this.partlocationData.plantId)) {
        this.toastr.info("Please select Plant");
        return;
    }
    if (this.IsNullorEmpty(this.partlocationData.plantDockId)) {
        this.toastr.info("Please select Dock");
        return;
    }
    
      let data:any={};
       data.partDeliveryDockId=this.partlocationData.partDeliveryDockId==""?"0":this.partlocationData.partDeliveryDockId;
       data.partId=this.masterData.partId;
       data.dLocationId=this.partlocationData.dLocationId;
       data.plantId=this.partlocationData.plantId;
       data.plantDockId=this.partlocationData.plantDockId;
       data.isActive=true;
       data.userId=this.GlobalVariable.userId;
    
       this.service.updatePartDeliveryLocation(data).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.clearPartLocationData();
                this.closePartDLocationDlg();
                this.getDeliveryLocationDetails();
                this.action="Edit";
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        }) 
    
}

updateAssemblyParts(): void {
    if (this.IsNullorEmpty(this.partAssemblyData.assemblyPartname)) {
        this.toastr.info("Select Location");
        return;
    }

    if (this.IsNullorEmpty(this.partAssemblyData.qty) || this.partAssemblyData.qty==0) {
        this.toastr.info("QTY Required");
        return;
    }

    let data:any={};
     data.partassemblyId=this.partAssemblyData.partassemblyId==""?"0":this.masterData.partId;
     data.assemblyPartname=this.partAssemblyData.assemblyPartname;
     data.assemblyPartcode=this.partAssemblyData.assemblyPartcode;
     data.qty=this.partAssemblyData.qty;
     data.primaryPartid=this.masterData.partId;
     data.isActive=true;
     data.userId=this.GlobalVariable.userId;
  
     this.service.updatePartAssembly(data).subscribe({
      next: (rdata) => {
          if (rdata.isSuccess) {
              this.toastr.success(rdata.message);
              this.clearPartAssemblyData();
              this.closeAssemblyDlg();
              this.getPartAssemblyDetails();
              this.action="Edit";
          } else {
              this.toastr.warning(rdata.message);
          }
      },    
      error: (err) => { this.toastr.error(err.message) },  
      
      }) 
  
}


getPartMasterById(typeid: any): void {

    this.service.getPartMasterById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};         
                this.masterData.partId=rdata.result.partId;
                this.masterData.partNo=rdata.result.partNo;
                this.masterData.customerPartNo=rdata.result.customerPartNo;
                this.masterData.partDescription=rdata.result.partDescription;
                this.masterData.packingTypeId=rdata.result.packingTypeId+"";
                this.masterData.spq=rdata.result.spq;
                this.masterData.noOfStack=rdata.result.noOfStack;
                this.masterData.noOfPackingInPallet=rdata.result.noOfPackingInPallet;
                this.masterData.packingHeight=rdata.result.packingHeight;
                this.masterData.packingWidth=rdata.result.packingWidth;
                this.masterData.packingLength=rdata.result.packingLength;
                this.masterData.packingWeight=rdata.result.packingWeight;
                this.masterData.palletHeight=rdata.result.palletHeight;
                this.masterData.palletWidth=rdata.result.palletWidth;
                this.masterData.palletLength=rdata.result.palletLength;
                this.masterData.palletWeight=rdata.result.palletWeight;
                this.masterData.dispatchType=rdata.result.dispatchType;
                this.masterData.loadingType=rdata.result.loadingType;
                this.masterData.rePack=rdata.result.rePack;
                this.masterData.m2=rdata.result.m2;
                this.masterData.m3=rdata.result.m3;
                this.masterData.active=rdata.result.active;
                this.masterData.unloadDockId=rdata.result.unloadDockId+"";
                this.masterData.binQty=rdata.result.binQty;
                this.masterData.locationId=rdata.result.locationId+"";
                this.masterData.customerId=rdata.result.customerId+"";
                this.masterData.reqInspection=rdata.result.reqInspection;
                this.masterData.deliveryLocationId=rdata.result.deliveryLocationId+"";
                this.masterData.plantName=rdata.result.plantName;
                this.masterData.unloadingDock=rdata.result.unloadingDock;
                this.masterData.repackQty=rdata.result.repackQty;
                this.masterData.partWeight=rdata.result.opackingWeight;
                this.masterData.palletWeightWithPart=rdata.result.packingWeightWithPart;
                this.masterData.opackingTypeId=rdata.result.opackingTypeId+'';
                this.masterData.onoOfStack=rdata.result.onoOfStack;
                this.masterData.oBinQty=rdata.result.oBinQty;
                this.masterData.pNoOfStack=rdata.result.pNoOfStack;
                this.masterData.opalletLength=rdata.result.opalletLength;
                this.masterData.opalletWidth=rdata.result.opalletWidth;
                this.masterData.opalletHeight=rdata.result.opalletHeight;
                this.masterData.opalletWeight=rdata.result.opalletWeight;
                this.masterData.opalletWeightWithProduct=rdata.result.opackingWeightWithPart;
                this.partDispatchType=rdata.result.dispatchType;
                this.masterData.onoOfPackingInPallet=rdata.result.onoOfPackingInPallet;
                this.getList();
     
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
   

        cancelAdd(){
            this.clearData();
            this.action="view";
        } 

     clearPartLocationData(){
      
        this.partlocationData={
            partDeliveryDockId:0,
            partId:0,
            dLocationId:0,
            plantId:0,
            plantDockId:0
        }
     }  
     clearPartAssemblyData(){
        this.partAssemblyData={
            partassemblyId:0,
            assemblyPartname:"",
            assemblyPartcode:"",
            primaryPartid:"",
            qty:0
            
        }
     } 

      clearData(){
        this.masterData={
                partId: '',
                customerPartNo:'',
                partNo: '',
                partDescription: '',
                packingTypeId: null,
                spq: '',
                noOfStack: '',
                noOfPackingInPallet: '',
                packingHeight: '',
                packingWidth: '',
                packingLength: '',
                packingWeight: '',
                palletHeight: '',
                palletWidth: '',
                palletLength: '',
                palletWeight: '',
                dispatchType: '1',
                loadingType: '1',
                rePack: '',
                m2: '',
                m3: '',
                active: true,
                unloadDockId:null,
                binQty:'',
                locationId:null,
                customerId:null,
                reqInspection:false,
                deliveryLocationId:null,
                plantName:'',
                unloadingDock:'',
                repackQty:0,

                partWeight:'',
                palletWeightWithPart:'',
                opackingTypeId:null,
                onoOfStack:'',
                oBinQty:'',
                pNoOfStack:'',
                opalletLength:'',
                opalletWidth:'',
                opalletHeight:'',
                opalletWeight:'',
                opalletWeightWithProduct:'',
                onoOfPackingInPallet:'',


        };

      }
      
      addNew(){
        this.clearData();
        this.action="add";
      } 
      doAction(event: any){
        if(event){
            this.action="Edit";
            this.getPartMasterById(event.partid.toString());
        }
      }

}

