import { AfterViewInit, Component,Input, OnInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-cards',
  standalone: false,
  templateUrl: './cards.component.html',
  styleUrl: './cards.component.css'
})
export class CardsComponent implements OnInit,AfterViewInit {
  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
   
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private WmsGV: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }
    ngAfterViewInit(): void {
      if(!this.masterObj){
        this.masterData={};
        this.clearData();
      }
    }
    ngOnInit(): void {
        this.clearData();
    }
   
  clearData(){
    this.masterData={
      cardId:'',
      cardName:'',
      cardDataLength:'',
      partNoStart:'',
      vendorStart:'',
      userLocationStart:'',
      qtyStart:'',
      slnoStart:'',
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
        this.getCardDetailsById(event.cardid.toString());
    }
  }


get f() { return this.form.controls; }

IsNullorEmpty(value: any): boolean {
if (value == undefined || value == "") {
    return true;
}
return false;;
}


updateCardMaster(): void {

if (this.IsNullorEmpty(this.masterData.cardName)) {
  this.toastr.info("Card Name required");
  return;
}
if (this.IsNullorEmpty(this.masterData.cardDataLength)) {
  this.toastr.info("Data Length required");
  return;
}

if (this.IsNullorEmpty(this.masterData.partNoStart)) {
  this.toastr.info("Part No required");
  return;
}
if (this.IsNullorEmpty(this.masterData.vendorStart)) {
  this.toastr.info("Vendor required");
  return;
}
if (this.IsNullorEmpty(this.masterData.userLocationStart)) {
  this.toastr.info("Location required");
  return;
}

if (this.IsNullorEmpty(this.masterData.qtyStart)) {
  this.toastr.info("Quantity required");
  return;
}

if (this.IsNullorEmpty(this.masterData.slNoStart)) {
  this.toastr.info("Sl.No required");
  return;
}

let data:any={};
data.cardId=this.masterData.cardId==""?"0":this.masterData.cardId;
data.cardName=this.masterData.cardName;
data.cardDataLength=this.masterData.cardDataLength;
data.partNoStart=this.masterData.partNoStart;
data.vendorStart=this.masterData.vendorStart;
data.userLocationStart=this.masterData.userLocationStart;
data.qtyStart=this.masterData.qtyStart;
data.slNoStart=this.masterData.slNoStart;
data.userId=this.WmsGV.userId;


this.service.updateCardMaster(data).subscribe({
  next: (rdata) => {
      if (rdata.isSuccess) {
          this.toastr.success(rdata.message);
          this.cancelAdd();
          this.action="view";
      } else {
          this.toastr.warning(rdata.message);
      }
    },     // nextHandler
    error: (err) => { this.toastr.error(err.message) },    // errorHandler 
  
  })
}

getCardDetailsById(typeid: any): void {
  this.service.getCardMasterById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {
          this.masterData={};         
          this.masterData.cardId=rdata.result.cardId;
          this.masterData.cardName=rdata.result.cardName;
          this.masterData.cardDataLength=rdata.result.cardDataLength;
          this.masterData.partNoStart=rdata.result.partNoStart;
          this.masterData.vendorStart=rdata.result.vendorStart;
          this.masterData.userLocationStart=rdata.result.userLocationStart;
          this.masterData.qtyStart=rdata.result.qtyStart;
          this.masterData.slNoStart=rdata.result.slNoStart;
          this.masterData.userName=rdata.result.userName;
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    // errorHandler 

  });
}

}
