import { AfterViewInit, Component,Input, OnInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { TransactionService } from '../../../services/transaction.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-fifoboard',
  standalone: false,
  templateUrl: './fifoboard.component.html',
  styleUrl: './fifoboard.component.css'
})
export class FifoboardComponent implements OnInit {
  action:string="view";
  detailData:any={};
  customerSpaceDetails:any=[];
  customerDetails:any=[];
  selectedCustomerId:string="51";
  selectedFifoType:string="7";

  currentDate:string="";
  constructor(
    private WmsGV: AppGlobal,
    private service:TransactionService, 
    private toastr:ToastrService) { }
    
   ngOnInit(): void {
    let pipe = new DatePipe('en-US');
    let gateDate:any=pipe.transform(new Date(),'dd-MM-yyyy');
    let gateTime:any=pipe.transform(new Date(),'hh:mm');
    this.currentDate=gateDate+' '+gateTime;
    this.getAjaxCustomers('customer');
    this.getCustomerSpace(this.selectedCustomerId,this.selectedFifoType);
  }
  
  counter(i: number) {
    return new Array(i);
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
getAllocatedSpace(allocatedBins:number,lastFilledSpace:number,usedSpace:number,index:number){
    if(index+1<=usedSpace){
        return true;
    }else{
      return false;
    }
}
  getCustomerSpace(customerId:string,usageId:string):void{
    this.customerSpaceDetails=[];
    let contactsparam='asncustomerparts&customerid='+customerId;
    this.service.getFifoDetail(customerId,usageId).subscribe({
        next: (rdata) => {
            this.customerSpaceDetails=rdata;
          },
        error: (e) => console.error(e),
    })
  }
}
