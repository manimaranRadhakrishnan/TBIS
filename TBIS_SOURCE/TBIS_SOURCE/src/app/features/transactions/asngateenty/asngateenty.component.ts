import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Subject } from 'rxjs';
import { TransactionService } from '../../../services/transaction.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { ReportComponent } from '../../../report/report.component';


@Component({
  selector: 'app-asngateenty',
  standalone: false,
  templateUrl: './asngateenty.component.html',
  styleUrl: './asngateenty.component.css'
})
export class AsngateentyComponent implements OnInit {
  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";
  userRoleId:string="1";
  isDisabled:boolean=false;
  rptParam:string="3,6"
  myDate = new Date();
  showGateEntryDialog=false;
  vehicleNo:string="";

  @ViewChild('report', { static: false })
  report!: ReportComponent; 


  eventsSubject: Subject<void> = new Subject<void>();
overlay: boolean=false;

constructor(
private WmsGV: AppGlobal,
private service:TransactionService, 
private router: Router, 
private toastr:ToastrService) { }

ngOnInit(): void {
  this.userRoleId=this.WmsGV.roleId;
  if(this.userRoleId!="3"){
    this.rptParam="7,8"
  }else{
    this.rptParam="3,6"
  }
    if(!this.masterObj){
      this.masterData={};
      this.masterData.asnId=0;
      this.masterData.asnStatusId=0;
      this.masterData.transitLoad=false;
      this.masterData.lastAsnStatusId="0";
      this.masterData.vechicleNo="";
    }
  
  }

  doAction(event: any){
    this.clearData();
    if(event){
      let ansUpdateId=5;
      if(event.asnstatus=="3"){
        ansUpdateId=5;
      }else if(event.asnstatus=="6"){
        ansUpdateId=7;
      }else if(event.asnstatus=="7"){
        ansUpdateId=7;
      }else if(event.asnstatus=="8"){
        ansUpdateId=9;
      }

          this.masterData.asnId=event.asnid;
          this.masterData.asnStatusId=ansUpdateId;
          this.masterData.transitLoad=false;
          this.masterData.lastAsnStatusId=event.asnstatus;
          this.masterData.vechicleNo=event.vechicleno;
          this.showParts()
    }
  }

  showParts(){
    this.showGateEntryDialog=true;
  }
  cancelAdd(){
    this.clearData();
    if(this.userRoleId!="3"){
      this.rptParam="7,8"
    }else{
      this.rptParam="3,6"
    }
    this.action="view";
  } 
  updateGateEntryStatus():void{
    let data={
      asnId: this.masterData.asnId,
      asnStatusId:this.masterData.asnStatusId,
      lastAsnStatusId: this.masterData.lastAsnStatusId,
      transitLoad:false,
      vechicleNo:this.masterData.vechicleNo,
      userId:0,
    }
    if(this.masterData.vechicleNo==""){
      this.toastr.warning("Vehicle Details Not Found");
      return;        
    }

    this.service.updateGateEntryStatus(data).subscribe({
      next: (rdata) => {
          if (rdata.isSuccess) {
              this.toastr.success(rdata.message);
              this.closeVehicleModel();
              this.cancelAdd();
              this.report.getReportData(true);
              // this.router.navigate(['/asngateentry']);
          } else {
              this.toastr.warning(rdata.message);
          }
        },   
        error: (err) => { this.toastr.error(err.message) } 
      })
   

  }

  closeVehicleModel(){
    this.clearData();
    this.showGateEntryDialog=false;
  }
  clearData() {
    this.masterData={
      asnId:0,
      asnStatusId:0,
      lastAsnStatusId:0,
      transitLoad:false,
      vechicleNo:'',
      userId:0
    };
   
  }
 
}
