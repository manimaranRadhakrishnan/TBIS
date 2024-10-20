import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-truckrequestplan',
  standalone: false,
  templateUrl: './truckrequestplan.component.html',
  styleUrl: './truckrequestplan.component.css'
})
export class TruckrequestplanComponent implements OnInit,AfterViewInit{

  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";
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
  transitLocations:any=[];
  rptParam:string="2"
  myDate = new Date();
  enableAdd:boolean=false;

eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;
  ngAfterViewInit(): void {
    throw new Error('Method not implemented.');
  }
  constructor(
    private WmsGV: AppGlobal,
    private service:TransactionService, 
    private toastr:ToastrService) { }
    
    
    ngOnInit(): void {
      this.userRoleId=this.WmsGV.roleId;
      this.clearData();
      // this.getAjaxWareHouse('warehouse');
    }

  clearData() {
    throw new Error('Method not implemented.');
  }


}
