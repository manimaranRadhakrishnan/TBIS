import { Component, Input, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HomeService } from '../services/homeservice.service';
import { ConfirmDialogService } from '../components/confirm-dialog/confirm-dialog.service';
import { DatePipe } from '@angular/common';
import { AppGlobal } from '../services/appglobal.service';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';
import { FormControl, FormGroup, NgForm } from '@angular/forms';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import Chart from 'chart.js/auto';
import { MastersService } from '../services/masters.service';


export const MY_FORMATS = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
     monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [DatePipe,{provide: MAT_DATE_FORMATS, useValue: MY_FORMATS},]
})

export class HomeComponent implements OnInit{
    menus:any;
    status = 'start';
    // @Input() masterObj: any;
    masterData:any={};
    detailData:any={};
    picklistdashboarddata:any={};
    today = new Date();
    month:any = this.today.getMonth();
    year:any = this.today.getFullYear();
    day:any = this.today.getDate();
    performanceReportType:string="vendorsupply";
    chart:any;
    piechart:any;
    tableIndex:any = [];
    customerDetails:any=[];
    locations:any=[];
    longHaul:any={};
    shortHaul:any={};
    asnData:any={};
    deliveryData:any={};
    overflowData:any=[];
    asnDashboardData:any=[];


    //subArray:any=[];

    

    dateFilter = new FormGroup({
      start: new FormControl(new Date(this.year, this.month, this.day)),
      end: new FormControl(new Date(this.year, this.month, this.day)),
    });
    constructor(
      private homeService:HomeService,
      private router: Router,
      private route: ActivatedRoute,
      private confirmDialogService:ConfirmDialogService,
      private gVar: AppGlobal,
      private toastr:ToastrService,
      private service:MastersService,
      ) { }
    ngOnInit(): void {
      document.body.classList.remove('login');
      this.masterData.start=this.today;
      this.masterData.end=this.today;
     
      for (let _i = 0; _i < 20; _i++) {
        let tmp = [];
          for (let _j = 1; _j <= 20; _j++) {
            tmp.push(_i * 20 + _j);
          }
    
          this.tableIndex.push({
              subArray: tmp
          });
       }
       this.getAjaxCustomers('customer');
       this.getLocationAjax('location');
       this.getDashboardSummary("","");
       this.getAjaxAsnData('asndatadashboard',"","");
       this.getAjaxPicklistDashboard('picklistdashboard',"","");
       
   }
   ngAfterViewInit() {
    this.longHaul={
      totalRequested:0,
      totalAlloted:0,
      totalTrips:0,
      totalDelays:0,
      totalAbnormalities:0,
      totalTats:0,
      totalCustomers:0
    };
    this.shortHaul={
      totalRequested:0,
      totalAlloted:0,
      totalTrips:0,
      totalDelays:0,
      totalAbnormalities:0,
      totalTats:0,
      totalCustomers:0
    };
    this.asnData={
      totalRequested:0,
      totalAlloted:0,
      totalTrips:0,
      totalDelays:0,
      totalAbnormalities:0,
      totalTats:0,
      totalCustomers:0
    };
    this.deliveryData={
      totalRequested:0,
      totalAlloted:0,
      totalTrips:0,
      totalDelays:0,
      totalAbnormalities:0,
      totalTats:0,
      totalCustomers:0
    };
   }
   getAjaxPicklistDashboard(ajaxId:string,fromDate:string,toDate:string ):void{
    if(fromDate=="" || fromDate==undefined){
      let pipe = new DatePipe('en-US');
      let start:any=pipe.transform(this.masterData.start,'dd-MM-yyyy');
      let end:any=pipe.transform(this.masterData.end,'dd-MM-yyyy');
      fromDate=start;
      toDate=end;
    }
    this.service.getAjaxDropDown(ajaxId+"&role_id="+this.gVar.roleId+"&fromdate="+fromDate+"&todate="+toDate).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.picklistdashboarddata=rdata.data;
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

    getAjaxAsnData(ajaxId:string,fromDate:string,toDate:string ):void{
      if(fromDate=="" || fromDate==undefined){
        let pipe = new DatePipe('en-US');
        let start:any=pipe.transform(this.masterData.start,'dd-MM-yyyy');
        let end:any=pipe.transform(this.masterData.end,'dd-MM-yyyy');
        fromDate=start;
        toDate=end;
      }
      this.service.getAjaxDropDown(ajaxId+"&role_id="+this.gVar.roleId+"&fromdate="+fromDate+"&todate="+toDate).subscribe({
          next: (rdata) => {
              if (rdata.data) {
                  this.asnDashboardData=rdata.data;
              }
              },
          error: (e) => console.error(e),
      })
      }

   refreshData(dstart:any,dend:any){
    let pipe = new DatePipe('en-US');
    let start:any=pipe.transform(dstart,'dd-MM-yyyy');
    let end:any=pipe.transform(dend,'dd-MM-yyyy');
    this.getDashboardSummary(start,end);
    this.getAjaxPicklistDashboard("picklistdashboard",start,end);
    this.getAjaxAsnData("asndatadashboard",start,end);
   }

//  
   getDashboardSummary(fromDate:string,toDate:string ): void {
    if(fromDate=="" || fromDate==undefined){
      let pipe = new DatePipe('en-US');
      let start:any=pipe.transform(this.masterData.start,'dd-MM-yyyy');
      let end:any=pipe.transform(this.masterData.end,'dd-MM-yyyy');
      fromDate=start;
      toDate=end;
    }

    
    this.homeService.getTbisDashboard(fromDate,toDate).subscribe(
      rdata => {
            if (rdata.result) {
              this.longHaul=rdata.result.longHaul;
              this.shortHaul=rdata.result.shortHaul;
              this.asnData=rdata.result.asnData;
              this.deliveryData=rdata.result.deliveryData;
              this.overflowData=rdata.result.overflowData;
          
            } else {
               // this.toastr.warning(rdata.status);                
            }

        },
      (        err: { message: any; }) => {
            // this.toastr.error(err.message);
            
        }
    );

 }

}
