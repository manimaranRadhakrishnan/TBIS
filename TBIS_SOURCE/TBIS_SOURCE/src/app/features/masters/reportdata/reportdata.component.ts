import { Component,Input,OnInit,AfterViewInit, OnDestroy, ViewChild  } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import { AppGlobal } from '../../../services/appglobal.service';
import { AuthService } from '../../../services/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import { MastersService } from '../../../services/masters.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MY_FORMATS } from '../../../home/home.component';
import { ReportComponent } from '../../../report/report.component';

@Component({
  selector: 'app-reportdata',
  standalone: false,
  templateUrl: './reportdata.component.html',
  styleUrl: './reportdata.component.css'
})
export class ReportdataComponent implements OnInit,AfterViewInit  {
  status = 'start';
  action:string="view";
  reportId="";
  reportName="Part Request Status Report";
  eventsSubject: Subject<void> = new Subject<void>();
  navigationSubscription;
  masterData:any={};

  today = new Date();
  
  fromdate:any = this.today;
  todate:any = this.today;
  month:any = this.today.getMonth();
  year:any = this.today.getFullYear();
  day:any = this.today.getDate();
  
  dateFilter = new FormGroup({
    start: new FormControl(new Date(this.year, this.month, this.day)),
    end: new FormControl(new Date(this.year, this.month, this.day)),
  });
  
  @ViewChild('reportdata', { static: false })
  reportdata!: ReportComponent;   

  constructor(private confirmDialogService: ConfirmDialogService,
    private GlobalVariable: AppGlobal,
    private route: ActivatedRoute,
    private router: Router, 
    private authService: AuthService, 
    private tokenStorage: TokenStorageService,
    private service:MastersService) { 
      this.navigationSubscription = this.router.events.subscribe((e: any) => {
        if (e instanceof NavigationEnd) {
          this.ngOnInit();
        }
      });
    }



ngAfterViewInit() { }

refreshData(dstart:any,dend:any){
  this.fromdate=dstart;
  this.todate=dend;
 }

refreshReport():void {
  this.reportdata.getReportData(true);
}


ngOnInit(): void { 
  const routeParams = this.route.snapshot.paramMap;
  const reportIdFromRoute = routeParams.get('reportid');
  this.dateFilter = new FormGroup({
    start: new FormControl(new Date(this.year, this.month, this.day)),
    end: new FormControl(new Date(this.year, this.month, this.day)),
  });
  
  this.fromdate=this.today;
  this.todate=this.today;

  if(reportIdFromRoute){
    this.reportId = reportIdFromRoute;
    if(reportIdFromRoute=='60'){
      this.reportName="Branch";
    }else if(reportIdFromRoute=='62'){
      this.reportName="Stock";
    }else if(reportIdFromRoute=='89'){
      this.reportName="Asn Report";
    }else if(reportIdFromRoute=='90'){
      this.reportName="Asn Detail";
    }else if(reportIdFromRoute=='91'){
      this.reportName="Pick List";
    }else if(reportIdFromRoute=='92'){
      this.reportName="Pick List Detail";
    }else if(reportIdFromRoute=='93'){
      this.reportName="Longhaul Trip";
    }else if(reportIdFromRoute=='94'){
      this.reportName="Shorthaul Trip";
    }
   
  }
}



}