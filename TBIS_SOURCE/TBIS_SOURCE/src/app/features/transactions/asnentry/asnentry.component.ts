import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { GateEntryAjax } from '../../../models/transaction';
import { DatePipe } from '@angular/common';
import { toNumber } from 'lodash';

@Component({
  selector: 'app-asnentry',
  standalone: false,
  templateUrl: './asnentry.component.html',
  styleUrl: './asnentry.component.css'
  
})
export class AsnentryComponent implements OnInit,AfterViewInit{
  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";
  gateEntry!:GateEntryAjax[];
  scannedValue:string="";
  cardScannedDetails:any=[];
  savedCardDetails:any=[];
  vehicleDet:any=[];
  activeTab:string="scan";
  primaryCustomerId:number=0;
  userRoleId:string="1";
  isDisabled:boolean=false;
  customers!:any[];
  customerDetail:any={};
  customerPartDetails:any=[];
  asnBintagDetails:any=[];
  filterAsnBintagDetails:any=[];
  asnPalletDetails:any=[];
  transitLocations:any=[];
  rptParam:string="2"
  myDate = new Date();
  enableAdd:boolean=true;
  stepvalue:string="1";
  showTab:number=1;
  vehicletypemasters:any=[];
eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;
  totalVehicleSpace:number=0;
  vehicleHeight:number=0;
  totalVehicleSpaceOccupied=0;
  totalPallets:number=0;
  clubItemPalletRemainingSpace=0;
  clubItemBalance:number=0;
  totalVehicleWeight:number=0;
  totalvehicleWeightOccupied:number=0;
  currentVehicleSpace:number=0;
  currentVehicleWeight:number=0;
  exclusiveClubItemBalance:any=[];
  showAsnPartDlg=false;

constructor(
private WmsGV: AppGlobal,
private service:TransactionService, 
private toastr:ToastrService) { }

ngOnInit(): void {
  this.userRoleId=this.WmsGV.roleId;
  this.clearData();
  this.getAjaxWareHouse('warehouse');
  this.getTransporter();
  this.getVehicleTypeMasterAjax('vehicletypes');

}

openAsnPartDlg() {
  this.showAsnPartDlg=true;
}
closeAsnPartModel() {
  this.showAsnPartDlg=false;
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

getAsnPalletData(asnid:any):void{
  let inputsparam='asnpalletdetail&asnid='+asnid;
  this.asnPalletDetails=[];
  this.service.getAjaxDropDown(inputsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
            this.asnPalletDetails=rdata.data;
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
     
}


ngAfterViewInit() {
  
  if(!this.masterObj){
    this.masterData={    
     transId :0,
     asnId:0,
     vechicleNo :'',
     supplyDate:'',
     supplyTime:'',
     driverName:'',
     driverMobile:'',
     tripType:2,
     asnStatus:0,
     statusId:'',
     userId:'',
     totalCardScanned:0,
     returnPack:'0',
     returnPackQty:0,
     transitType:1,
     locationName:'',
     subLocationShortCode:'',
     customerName:'',
     transitLocationId:'0',
     vehicleTypeId:'0',
     noOfPallet:0,
     partsAdd:0,
     part:{
      partid: "0",
      partno:"",
      partdescription: "",
      customerpartcode: "",
      spq: "",
      binqty: 0,
      loadingtype: 0,
      noofpackinginpallet: 0,
      qty:0,
      packageId:1,
      packingShortName:"BIN",
      pallets:0,
      remainingPalletBins:0
     },
     vehicleInfo:{
      vehiclesize:"",
      vehiclecapacity:"",
      licencexpiry:"",
      rcexpiry:"",
      fitnessexpiry:""
     }
  }
  }
  
}

getTransporter():void{
  this.service.getAjaxDropDown("lgtransporter").subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.vehicleDet=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

getAjaxData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data && rdata.data.length>0) {
              this.customerDetail=rdata.data[0];
              if (this.customerDetail!=null && this.customerDetail.customerid>0){
                this.masterData.customerId=this.customerDetail.customerid;
                this.masterData.customerName=this.customerDetail.customername;
                this.masterData.locationId=this.customerDetail.locationid;
                this.masterData.unLoadingDocId=this.customerDetail.udc_id;
                this.masterData.subloactionId=this.customerDetail.primarysublocationid;
                this.masterData.warehouseId=this.customerDetail.warehousid;
                this.masterData.transitLocationId=this.customerDetail.warehousid+"";
                this.masterData.subLocationShortCode=this.customerDetail.sublocationshortcode;
                this.masterData.locationName=this.customerDetail.locationname;
                this.masterData.dockName=this.customerDetail.udcname;
                this.masterData.driverMobile='';
                this.masterData.tripType=2;
                this.masterData.driverName='';
                this.masterData.vechicleNo='';
                this.masterData.ewayBillNo='';
                this.masterData.rgpNo='';
                this.masterData.invoiceNo='';
                this.masterData.deliveryNoteNo='';
                this.masterData.asnStatus='';
                this.masterData.asnid=0;
                this.masterData.part={
                  partid: 0,
                  partno:"",
                  partdescription: "",
                  customerpartcode: "",
                  spq: "",
                  binqty: 0,
                  loadingtype: 0,
                  noofpackinginpallet: 0,
                  qty:0,
                  packageId:1,
                  packingShortName:"BIN",
                  remainingPalletBins:0,
                  pallets:0,
                  exclusiveClubNo:0
                 };
                 this.masterData.vehicleInfo={
                  vehiclesize:"",
                  vehiclecapacity:"",
                  licencexpiry:"",
                  rcexpiry:"",
                  fitnessexpiry:""
                 };
                this.getCustomerParts(this.customerDetail.customerid);
              }
  
             
          }
       },
      error: (e) => console.error(e),
  })
  
  }

getAjaxWareHouse(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.transitLocations=rdata.data;
          }
       },
      error: (e) => console.error(e),
  });
 
this.service.getAjaxDropDown(ajaxId).subscribe({
    next: (rdata) => {
        if (rdata.data && rdata.data.length>0) {
            this.customerDetail=rdata.data[0];
            if (this.customerDetail!=null && this.customerDetail.customerid>0){
              this.masterData.customerId=this.customerDetail.customerid;
              this.masterData.customerName=this.customerDetail.customername;
              this.masterData.locationId=this.customerDetail.locationid;
              this.masterData.unLoadingDocId=this.customerDetail.udc_id;
              this.masterData.subloactionId=this.customerDetail.primarysublocationid;
              this.masterData.warehouseId=this.customerDetail.warehousid;
              this.masterData.transitlocationId=this.customerDetail.locationid;
              this.masterData.subLocationShortCode=this.customerDetail.sublocationshortcode;
              this.masterData.locationName=this.customerDetail.locationname;
              this.masterData.dockName=this.customerDetail.udcname;
              this.masterData.transitLocationId=this.customerDetail.warehousid+"";
              this.masterData.driverMobile='';
              this.masterData.tripType=2;
              this.masterData.driverName='';
              this.masterData.vechicleNo='';
              this.masterData.ewayBillNo='';
              this.masterData.rgpNo='';
              this.masterData.invoiceNo='';
              this.masterData.deliveryNoteNo='';
              this.masterData.asnStatus='';
              this.masterData.asnid=0
              this.masterData.primaryDcokid=this.customerDetail.udc_id;
              this.masterData.parts=[];
              this.masterData.vehicleInfo=[];
              this.getCustomerParts(this.customerDetail.customerid);
            }

           
        }
     },
    error: (e) => console.error(e),
})

}



getCustomerParts(customerId:number):void{
  this.customerPartDetails=[];
  let contactsparam='asnentrycusparts&customerid='+customerId;
  this.service.getAjaxDropDown(contactsparam).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.customerPartDetails=rdata.data;
          }
       },
      error: (e) => console.error(e),
  })
}

onSelectPart(partid:any):void{
  if(partid==null){
    this.masterData.part={
      partid: null,
      partno:"",
      partdescription: "",
      customerpartcode: "",
      spq: "",
      binqty: 0,
      loadingtype: 0,
      noofpackinginpallet: 0,
      qty:0,
      packageId:1,
      packingShortName:"BIN",
      exclusiveClubNo:0
     }
    //  this.calculatePallets(); 
  }else{
    this.masterData.part=JSON.parse(JSON.stringify(this.customerPartDetails.find((x:any)=>x.partid==partid)));
    console.info(JSON.stringify(this.masterData.part) );
    this.masterData.part.qty=this.masterData.part.spq;
    this.masterData.part.packingshortname=this.masterData.part.packingshortname;
    this.masterData.part.noofbins=this.masterData.part.spq/this.masterData.part.binqty;
    this.masterData.part.packageId=this.masterData.part.packageId;
    if(this.masterData.part.exclusiveclubno>0){
      if(this.exclusiveClubItemBalance.find((x:any)=>x.clubno==this.masterData.part.exclusiveclubno)==null){
        let clubDetail={
            clubno:this.masterData.part.exclusiveclubno,
            balance:0,
            totalbins:0,
            noofpallets:0
        }
        this.exclusiveClubItemBalance.push(clubDetail);
      }
    }
    this.calculatePallets();  
    this.stepvalue=this.masterData.part.qty;
  }

}
calculatePallets():void{
  console.log(this.masterData.part.loadingtype);
  this.clubItemBalance=this.clubItemPalletRemainingSpace;
  if(this.masterData.part.loadingtype==1){
    this.masterData.part.pallets=Math.floor(this.masterData.part.noofbins/this.masterData.part.noofpackinginpallet);
    if(this.masterData.part.noofbins%this.masterData.part.noofpackinginpallet>0){
      this.masterData.part.pallets=this.masterData.part.pallets+1;
    }
  }else{
    if(this.masterData.part.exclusiveclubno==0){
      let nonAdjustedBinCount=0;
      if(this.clubItemBalance>0){      
          if(this.masterData.part.noofbins>this.clubItemBalance){
            nonAdjustedBinCount=this.masterData.part.noofbins-this.clubItemBalance;
            this.clubItemBalance=0;  
            let remainingBins=nonAdjustedBinCount%this.masterData.part.noofpackinginpallet;
            this.masterData.part.pallets=Math.floor(this.masterData.part.noofbins/this.masterData.part.noofpackinginpallet);
            this.masterData.part.remainingPalletBins=0;
            if(remainingBins>0){
              this.masterData.part.pallets=this.masterData.part.pallets+1;
              this.masterData.part.remainingPalletBins=this.masterData.part.noofpackinginpallet-remainingBins;
              this.clubItemBalance=this.masterData.part.noofpackinginpallet-remainingBins
            }              
          }else{
            this.clubItemBalance=this.clubItemBalance-this.masterData.part.noofbins;
            nonAdjustedBinCount=0
            this.masterData.part.pallets=0;
            this.masterData.part.remainingPalletBins=0;
          }
      }else{
        let remainingBins=this.masterData.part.noofbins%this.masterData.part.noofpackinginpallet;
        this.masterData.part.pallets=Math.floor(this.masterData.part.noofbins/this.masterData.part.noofpackinginpallet);
        this.masterData.part.remainingPalletBins=0;
        if(remainingBins>0){
          this.masterData.part.pallets=this.masterData.part.pallets+1;
          this.masterData.part.remainingPalletBins=this.masterData.part.noofpackinginpallet-remainingBins;
          this.clubItemBalance=this.masterData.part.noofpackinginpallet-remainingBins
        }
      }
    }else{
      let club=this.exclusiveClubItemBalance.find((x:any)=>x.clubno==this.masterData.part.exclusiveclubno);
      club.balance=this.masterData.part.noofbins%this.masterData.part.noofpackinginpallet;
      club.noofbins=this.masterData.part.noofbins;
      club.noofpallets=Math.floor(this.masterData.part.noofbins/this.masterData.part.noofpackinginpallet);
      let remainingBins=club.noofbins%this.masterData.part.noofpackinginpallet;
      if(remainingBins>0){
        club.noofpallets=club.noofpallets+1;
      }
      let firstItem:any=undefined;
      this.savedCardDetails.forEach((element:any) => {
          if(element.exclusiveClubNo==this.masterData.part.exclusiveclubno){
              if(firstItem==undefined){
                firstItem=element;
              }
              club.noofbins=club.noofbins+element.binQty;
              club.noofpallets=Math.floor(club.noofbins/this.masterData.part.noofpackinginpallet);
              let remainingBins=club.noofbins%this.masterData.part.noofpackinginpallet;
              if(remainingBins>0){
                club.noofpallets=club.noofpallets+1;
              }
          }
      });
      if(firstItem!=undefined){
        firstItem.pallets=club.noofpallets;
        this.masterData.part.pallets=0;
      }else{
        this.masterData.part.pallets=club.noofpallets;
      }
    }
  }
}
validateVehicleSpace():void{
  this.currentVehicleSpace=this.totalVehicleSpace-this.totalVehicleSpaceOccupied;
  this.currentVehicleWeight=this.totalVehicleWeight-this.totalvehicleWeightOccupied;
  let noOfStack=1;
  if(this.masterData.part.pnoofstack>1){
    for(let i=this.masterData.part.pnoofstack;i>=1;i--){
      if(this.vehicleHeight>=(this.masterData.part.palletheight*i)){
        noOfStack=i;
      }else{
        break;
      }  
    }
  }
  this.masterData.part.noofstack=noOfStack;
  if(this.currentVehicleWeight<this.masterData.part.packingweightwithpart*this.masterData.part.pallets){
    this.toastr.warning("Weight exceeds the limit.Please review the Qty");
    return;
  }
  if(this.currentVehicleSpace<(this.masterData.part.palletlength/1000*this.masterData.part.palletwidth/1000)*(this.masterData.part.pallets/noOfStack)){
    this.toastr.warning("Space exceeds the limit.Please review the Qty");
    return;
  }
  this.currentVehicleWeight=this.currentVehicleWeight-this.masterData.part.packingweightwithpart*this.masterData.part.pallets;
  this.currentVehicleSpace=this.currentVehicleSpace-(this.masterData.part.palletlength/1000*this.masterData.part.palletwidth/1000)*(this.masterData.part.pallets/noOfStack);
}
onSelectVehicle(vechicleNo:string):void{
  if(vechicleNo!=null){
    this.masterData.vehicleInfo=this.vehicleDet.find((x:any)=>x.vehicleno==vechicleNo);
    this.totalVehicleSpace=this.masterData.vehicleInfo.vehiclem2;
    this.totalVehicleSpaceOccupied=0;
    this.vehicleHeight=this.masterData.vehicleInfo.vehicleheight;
    this.totalVehicleWeight=this.masterData.vehicleInfo.vehiclecapacity;
  }
  else{
    this.masterData.vehicleInfo={
      vehiclesize:"",
      vehiclecapacity:"",
      licencexpiry:"",
      rcexpiry:"",
      fitnessexpiry:""
    };
    this.totalVehicleSpace=0;
    this.totalVehicleSpaceOccupied=0;
    this.vehicleHeight=0;
    this.totalVehicleWeight=0;
  }
}

onSelectVehicleType(vechicletypeId:any):void{
  if(vechicletypeId==null){
    this.totalVehicleSpace=0;
    this.totalVehicleWeight=0;
  }else{
    var vhtype=this.vehicletypemasters.find((x:any)=>x.vehicletypeid==vechicletypeId);
    this.totalVehicleSpace=(vhtype.length);
    // this.totalVehicleSpaceOccupied=0;
    // this.vehicleHeight=vhtype.height;
    this.totalVehicleWeight=vhtype.capacity;
  }  
  
}

clearGrid(){

  this.masterData.part.qty="";
  this.masterData.part.packingShortName="";
  this.masterData.part.noofbins="";
  this.masterData.part.packageId="";
  this.stepvalue="1";
}
onChangeQuantity(event:any):void{
  if(this.masterData.part.qty!=""){
    this.masterData.part.noofbins=this.masterData.part.qty/this.masterData.part.binqty;
    this.calculatePallets();
    this.validateVehicleSpace();
  }else{
    this.masterData.part.noofbins="";
    this.masterData.part.remainingPalletBins=0;
    this.masterData.part.pallets=0;
  }
  // if(this.masterData.vehicleTypeId==undefined || this.masterData.vehicleTypeId==undefined || this.masterData.vehicleTypeId==0){
  //   this.toastr.info("Select the Vehicle Type");
  //   return;
  // }
}

onChangeBinTagPartNo(event:any):void{
  console.log(event.target.value);
  console.log(event.target.value.length);
  this.asnBintagDetails=this.filterAsnBintagDetails.filter((x:any)=>x.partNo.includes(event.target.value));
}
onChangeBinTagPalletNo(event:any):void{
  console.log(event.target.value);
  console.log(event.target.value.length);
  this.asnBintagDetails=this.filterAsnBintagDetails.filter((x:any)=>String(x.palletNo).includes(event.target.value));
}


updateItems(){

  console.log("vehicle "+this.masterData.vehicleTypeId);
  if(this.masterData.vehicleTypeId==undefined || this.masterData.vehicleTypeId==undefined || this.masterData.vehicleTypeId==0){
    this.toastr.info("Select the Vehicle Type");
    return;
  }
  if(this.masterData.part==undefined || this.masterData.part.partno==undefined || this.masterData.part.partno==''){
    this.toastr.info("Select the part");
    return;
  }
  if(this.masterData.part.qty==0 || this.masterData.part.qty=='' || this.masterData.part.qty==undefined){
    this.toastr.info("Enter the Qty");
    return;
  }
  if((this.masterData.part.qty%this.masterData.part.spq)>0){
    this.toastr.info("Qty must be in multiples of SPQ");
    return;
  }
  if(this.savedCardDetails.find((x:any)=>x.partId==this.masterData.part.partid)){
    this.toastr.info("Part already added ");
    return;
  }
  let partDetail:any={
    partId:this.masterData.part.partid,
    partNo:this.masterData.part.partno,
    partName:this.masterData.part.partdescription,
    packingShortName:this.masterData.part.packingshortname,
    customerPartCode:this.masterData.part.customerpartcode,
    spq:this.masterData.part.spq,
    binQty:this.masterData.part.noofbins,
    loadingType:this.masterData.part.loadingtype,
    noOfPackingInPallet:this.masterData.part.noofpackinginpallet,
    qty:this.masterData.part.qty,
    subLocationShortCode:this.masterData.part.sublocationshortcode,
    packageId:this.masterData.part.packageId,
    serialNo:this.masterData.part.serialNo,
    pallets:this.masterData.part.pallets,
    remainingPalletBins:this.masterData.part.remainingPalletBins,
    noOfStack:this.masterData.part.noofstack,
    packingWeightWithPart:this.masterData.part.packingweightwithpart,
    palletLength:this.masterData.part.palletlength,
    palletWidth:this.masterData.part.palletwidth,
    exclusiveClubNo:this.masterData.part.exclusiveclubno
  }
  this.clubItemPalletRemainingSpace=this.clubItemBalance;
  this.totalVehicleSpaceOccupied+=toNumber(((this.totalVehicleSpace-this.totalVehicleSpaceOccupied)-this.currentVehicleSpace).toFixed(2));
  this.totalVehicleSpaceOccupied=toNumber(this.totalVehicleSpaceOccupied.toFixed(2));
  this.totalvehicleWeightOccupied+=(this.totalVehicleWeight-this.totalvehicleWeightOccupied)-this.currentVehicleWeight;
  console.log(partDetail.packingShortName);
  this.savedCardDetails.push(partDetail);
  // calculate pallet count
  this.masterData.noOfPallet=this.savedCardDetails.reduce((sum:any,x:any)=>sum+x.pallets,0);
  // end pallet count
  this.masterData.part={
    partid: "0",
    partno:"",
    partdescription: "",
    customerpartcode: "",
    spq: "",
    binqty: 0,
    loadingtype: 0,
    noofpackinginpallet: 0,
    qty:0,
    packageId:1,
    packingShortName:"BIN",
    exclusiveClubNo:0
   }
  

}


removeScannedValue(i:number){
if(this.savedCardDetails.length>i){
 let vpackingWeightWithPart = this.savedCardDetails[i].packingWeightWithPart == undefined ?1: this.savedCardDetails[i].packingWeightWithPart;
 let vpallets = this.savedCardDetails[i].pallets == undefined ?1: this.savedCardDetails[i].pallets;
 let vpalletLength = this.savedCardDetails[i].palletLength == undefined ?1: this.savedCardDetails[i].palletLength;
 let vpalletWidth = this.savedCardDetails[i].palletWidth == undefined ?1: this.savedCardDetails[i].palletWidth;
 let vnoOfStack = this.savedCardDetails[i].noOfStack == undefined ?1: this.savedCardDetails[i].noOfStack;

  // this.totalvehicleWeightOccupied=this.totalvehicleWeightOccupied-this.savedCardDetails[i].packingWeightWithPart*this.savedCardDetails[i].pallets;
  // this.totalVehicleSpaceOccupied=this.totalVehicleSpaceOccupied-(this.savedCardDetails[i].palletLength/1000*this.savedCardDetails[i].palletWidth/1000)*(this.savedCardDetails[i].pallets/this.savedCardDetails[i].noOfStack);
  this.totalvehicleWeightOccupied=this.totalvehicleWeightOccupied-(vpackingWeightWithPart*vpallets);
  this.totalVehicleSpaceOccupied=this.totalVehicleSpaceOccupied-((vpalletLength/1000)*(vpalletWidth/1000)*vnoOfStack);

  this.savedCardDetails.splice(i,1);
  
  // calculate pallet count
  this.masterData.noOfPallet=this.savedCardDetails.reduce((sum:any,x:any)=>sum+x.pallets,0);
  // end pallet count

  //clear selected part
  this.onSelectPart(null);
   //end clear selected part
  
}
}

clearData(){
let pipe = new DatePipe('en-US');
let gateDate:any=pipe.transform(new Date(),'yyyy-MM-dd');
let gateTime:any=pipe.transform(new Date(),'hh:mm');
this.stepvalue="1";
this.totalVehicleSpace=0;
this.totalVehicleWeight=0;
this.totalVehicleSpaceOccupied=0;
this.totalvehicleWeightOccupied=0;
this.clubItemPalletRemainingSpace=0;
this.clubItemBalance=0;
this.masterData={
  transId :'',
  statusId:'',
  userId:'',
  totalCardScanned:0,
  asnId:0,
  asnNo:'',
  customerId:'', 
  supplyDate:gateDate,
  supplyTime:gateTime,
  vehicleTypeId:'0',
  noOfPallet:0,
  partsAdd:0,
  unLoadingDocId:0,
  asnStatus:'2',
  vechicleNo:'',
  driverName:'',
  driverMobile:'',
  tripType:2,
  ewayBillNo:'',
  rgpNo:'',
  invoiceNo:'',
  deliveryNoteNo:'',
  directorTransit:true,
  transitasnId:0,
  gateInDateTime:'',
  cardsIssued:0,
  cardsAcknowledged:0,
  cardsDispatched:0,
  cardsReceived:0,
  locationName:'',
  subLocationShortCode:'',
  customerName:'',
  locationId:0,
  warehouseId:0,
  sublocationId:0,
  primaryDcokid:0,
  part:{
    partid: 0,
    partno:"",
    partdescription: "",
    customerpartcode: "",
    spq: "",
    binqty: 0,
    loadingtype: 0,
    noofpackinginpallet: 0,
    qty:0,
    packageId:1,
    packingShortName:'BIN',
    exclusiveClubNo:0
   },
   vehicleInfo:{
    vehiclesize:"",
    vehiclecapacity:"",
    licencexpiry:"",
    rcexpiry:"",
    fitnessexpiry:""
   }


};
this.savedCardDetails=[];
  if(this.customerDetail && this.customerDetail.customerid){
    this.getCustomerParts(this.customerDetail.customerid);
  }
this.asnPalletDetails=[];
this.asnBintagDetails=[];
this.filterAsnBintagDetails=[];
// this.totalVehicleSpaceOccupied=0;
// this.totalvehicleWeightOccupied=0;
// this.totalVehicleSpace=0;
// this.totalVehicleWeight=0;
}
cancelAdd(){
 this.clearData();
 this.savedCardDetails=[];
 this.action="view";
 this.isDisabled=true;
 
} 
addNew(){
    this.clearData();
    this.getAjaxData('asncustomerdetail');
    this.action="add";
    this.enableAdd=true;
} 

get f() { return this.form.controls; }

IsNullorEmpty(value: any): boolean {
if (value == undefined || value == "") {
    return true;
}
return false;;
}

doAction(event: any){
  this.clearData();
  if(event){
    if(this.WmsGV.roleId=='3') {  // Customer
      if(event.asnstatus.toString()=='2' ||event.asnstatus.toString()=='5'){
          this.action="Edit";
          this.getASNMasterById(event.asnid.toString());    
        }else{
            this.action="show";
            this.getASNMasterById(event.asnid.toString());
        }
    }else if(this.WmsGV.roleId=='4' ) { // Transport
      if(event.asnstatus.toString()=='2'){
        this.action="Edit";
        this.getASNMasterById(event.asnid.toString());
      }else{
        this.action="show";
        this.getASNMasterById(event.asnid.toString());
      }
    }else if (this.WmsGV.roleId=='5'){  // Stores
      if(event.asnstatus.toString()=='2' ||event.asnstatus.toString()=='4'){
        if(event.asnstatus.toString()=='2'){
          this.action="Edit";
          this.getASNMasterById(event.asnid.toString());
        }else{
          this.action="show";
          this.getASNMasterById(event.asnid.toString());
        } 
      }
    } else{
        this.action="show";
        this.getASNMasterById(event.asnid.toString());
    }
  }

 
}

getASNBinTags(asnId: any): void {
  this.service.getASNBinTags(asnId).subscribe({
    next: (rdata) => {
      if (rdata) {
          this.asnBintagDetails=rdata;
          this.filterAsnBintagDetails=rdata;
      }
   },
  error: (err) => { this.toastr.warning(err);  },    

  });
}


getASNMasterById(asnId: any): void {
  this.service.getPartRequestById(asnId).subscribe({
    next: (rdata) => {
        if (rdata.result) {
            this.clearData();    
            this.masterData.asnId=rdata.result.asnId;
            this.masterData.asnNo=rdata.result.asnNo;
            this.masterData.customerId=rdata.result.customerId;
            this.masterData.supplyDate=rdata.result.supplyDate;
            this.masterData.supplyTime=rdata.result.supplyTime;
            this.masterData.unLoadingDocId=rdata.result.unLoadingDocId;
            this.masterData.asnStatus=rdata.result.asnStatus;
            this.masterData.vechicleNo=rdata.result.vechicleNo;
            this.masterData.driverName=rdata.result.driverName;
            this.masterData.driverMobile=rdata.result.driverMobile;
            this.masterData.tripType=rdata.result.tripType;
            this.masterData.ewayBillNo=rdata.result.ewayBillNo;
            this.masterData.rgpNo=rdata.result.rgpNo;
            this.masterData.invoiceNo=rdata.result.invoiceNo;
            this.masterData.deliveryNoteNo=rdata.result.deliveryNoteNo;
            this.masterData.directorTransit=rdata.result.directorTransit;
            this.masterData.transitasnId=rdata.result.transitasnId;
            this.masterData.gateInDateTime="";
            this.masterData.userId=rdata.result.userId;
            this.masterData.returnPack=rdata.result.returnPack;
            this.masterData.returnPackQty=rdata.result.returnPackQty;
            this.masterData.locationName=rdata.result.locationShortName;
            this.masterData.subLocationShortCode=rdata.result.sublocationShortName;
            this.masterData.customerName=rdata.result.customerName;
            this.masterData.locationId=rdata.result.loactionId;
            this.masterData.subLocationId=rdata.result.subloactionId;
            this.masterData.warehouseId=rdata.result.warehouseId+'';
            this.masterData.transitLocationId=rdata.result.transitlocationId+'';
            this.masterData.dockName=rdata.result.dockName;
            this.savedCardDetails=rdata.result.asnDetail;
            this.masterData.primaryDcokid=rdata.result.primaryDcokid;
            this.masterData.vehicleTypeId=rdata.result.vehicleTypeId+"";
            this.masterData.noOfPallet=rdata.result.noOfPallet;
            this.masterData.partsAdd=rdata.result.partsAdd;
            this.masterData.rgpNo=rdata.result.rgpNo;
            this.totalVehicleSpaceOccupied=rdata.result.filledSize;
            this.totalvehicleWeightOccupied=rdata.result.filledCapacity;
            this.onSelectVehicleType(rdata.result.vehicleTypeId);

            if(this.masterData.asnStatus==2 || this.masterData.asnStatus==5 || this.masterData.asnStatus==9){
              if(this.masterData.partsAdd==1){
                this.enableAdd=false;
              }
            }else{
              this.enableAdd=true;
            }
            
            
            // if(this.savedCardDetails?.length>0){
              
            //   this.enableAdd=false;
            // }
            this.getCustomerParts(this.masterData.customerId);
            this.getASNBinTags(asnId);
            this.getAsnPalletData(asnId);
            if(this.masterData.vechicleNo){
              this.onSelectVehicle(this.masterData.vechicleNo);
            }
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    

  });
}

updateAsn(): void {

  // if(this.savedCardDetails==undefined || this.savedCardDetails==undefined || this.savedCardDetails==''){
  //   this.toastr.info("Add the parts");
  //   return;
  // }

  let data={
    asnId:this.masterData.asnId==""?"0":this.masterData.asnId,
    asnNo:this.masterData.asnNo,
    customerId:this.masterData.customerId,
    supplyDate:this.masterData.supplyDate,
    supplyTime:this.masterData.supplyTime,
    unLoadingDocId:this.masterData.unLoadingDocId,
    subloactionId:this.masterData.subloactionId,
	  warehouseId:this.masterData.warehouseId,
	  loactionId:this.masterData.locationId,
	  transitlocationId:this.masterData.transitLocationId,
    asnStatus:this.masterData.asnStatus,
    vechicleNo:this.masterData.vechicleNo,
    driverName:this.masterData.driverName,
    driverMobile:this.masterData.driverMobile,
    tripType:this.masterData.tripType,
    ewayBillNo:this.masterData.ewayBillNo,
    rgpNo:this.masterData.rgpNo,
    invoiceNo:this.masterData.invoiceNo,
    deliveryNoteNo:this.masterData.deliveryNoteNo,
    directorTransit:this.masterData.directorTransit,
    transitasnId:0,
    primaryDcokid:this.masterData.primaryDcokid,
    gateInDateTime:'',
    userId:0,
    cardsIssued:0,
    cardsReceived:0,
    cardsDispatched:0,
    cardsAcknowledged:0,
    returnPack:'0',
    returnPackQty:0,
	  dockShortName:"",
	  sublocationShortName:"",
	  warehouseShortName:"",
	  locationShortName:"",
	  dockName:"",
	  customerName:"",
	  customerCode:"",
    asnDetail:this.savedCardDetails,
    vehicleTypeId:this.masterData.vehicleTypeId,
    noOfPallet:this.masterData.noOfPallet,
    filledSize:this.totalVehicleSpaceOccupied,
    filledCapacity:this.totalvehicleWeightOccupied,
  }
  //Role Based

  if(this.userRoleId=="3"){   // Customer
    this.rptParam="2,3";
    data.asnStatus=this.masterData.asnStatus==3?4:this.masterData.asnStatus==4?5:this.masterData.asnStatus==2?2:6;
    if (data.asnStatus==5){
       
       if (this.IsNullorEmpty(this.masterData.deliveryNoteNo)) {
        this.toastr.info("Delivery Note No Required");
        return;
       }
       if (this.IsNullorEmpty(this.masterData.invoiceNo)) {
        this.toastr.info("Invoice No Required");
        return;
       }
       if (this.IsNullorEmpty(this.masterData.ewayBillNo)) {
        this.toastr.info("Eway bill Required");
        return;
       }
    }
  }else if(this.userRoleId=="4" ){  // Logistics
    if (data.asnStatus==2){
      if (this.IsNullorEmpty(this.masterData.vechicleNo)) {
       this.toastr.info("Vehicle Details Required!");
       return;
      }
      if (!this.masterData.directorTransit && this.IsNullorEmpty(this.masterData.transitLocationId)) {
       this.toastr.info("Transit Location Required");
       return;
      }
      if(this.savedCardDetails==null && this.savedCardDetails.length<=0){
        this.toastr.info("Part Details required");
        return;
      }
   }
    data.asnStatus=3;
  }



  this.service.updateAsnEntry(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success("ASN Created Successfuly");
            this.getASNBinTags(rdata.message);
            this.masterData.asnId=rdata.message;
            this.getASNMasterById(rdata.message.toString());    
            this.action="Edit";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
}



updateGateEntry(data:any): void {
 
  this.service.updateGateEntryStatus(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            // this.action="Edit";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
}


DownloadBarCode(data:any): void {
 
  this.service.getAsnBarcode(data).subscribe((response: any) => {
    this.downloadFile(response,'pdf');
  });
  
}

moveParts():void{
     this.showTab=2;

}

downloadFile(data: BlobPart,type:string) {
  //Download type xls

  let contentType = 'application/octet-stream';
  //Download type: CSV
  let contentType2 = 'text/csv';
  if(type=='pdf'){
    contentType="application/pdf";
  }
  const blob = new Blob([data], { type: contentType });
  const url = window.URL.createObjectURL(blob);
  //Open a new window to download
  // window.open(url); 

  //Download by dynamically creating a tag
  const a = document.createElement('a');
  const fileName = "report1";
  a.href = url;
  // a.download = fileName;
  if(type=='pdf'){
    a.download = fileName + '.pdf';
  }else{
    a.download = fileName + '.xlsx';
  }
  a.click();
  window.URL.revokeObjectURL(url);
}

fileChangeAsnPartEvent(fileInput: any) {

  let allowedFileTypes='log,txt,xlsx';
  if (fileInput.target!.files && fileInput.target.files[0]) {
      let ext=fileInput.target.files[0].name.split('.').pop();
      // Size Filter Bytes
      const max_size = 1000*1024;
      let allowed_types:any=[];
      let ty=allowedFileTypes.split(',');
      if(ty.indexOf(ext)==-1){
      this.toastr.error("Upload valid file format");
      return;
      }
      this.service.uploadAsnPart(fileInput.target.files[0]).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
                this.closeAsnPartModel();
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },
        })
  }
}


}
