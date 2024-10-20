import { Component, OnInit, ViewChild, AfterViewInit,Input, ElementRef, HostListener, OnDestroy, OnChanges, SimpleChanges, EventEmitter, Output } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import { AppGlobal } from '../services/appglobal.service';
import { ReportService } from '../services/reportservice.service';
import { ConfirmDialogService } from '../components/confirm-dialog/confirm-dialog.service';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import { TokenStorageService } from '../services/token-storage.service';
import { ReportFilterBase } from './reportfilter-base';
import { ReportFilterControlService } from './reportfilter-control.service';
import { Textbox } from './filter-textbox';
import { Dropdown } from './filter-dropdown';
import { ReportfilterService } from './reportfilter.service'
import { DatePipe } from '@angular/common'
import { SlimScrollOptions } from 'ngx-slimscroll';
import { FilterItems, ReportColumns,ReportActions } from '../models/ui-modules';
import { Observable, Subscription } from 'rxjs';
import { MastersService } from '../services/masters.service';
import { HttpRequest,HttpClient } from '@angular/common/http';
import { AppConfig } from '../services/appconfig.service';
import { LoginService } from '../services/login.service';
import { FormControl, FormGroup } from '@angular/forms';
 
const ELEMENT_DATA: any[] = [] ;

@Component({
  selector: 'report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss'],
  providers: [ ReportfilterService,DatePipe ]
  
})
export class ReportComponent  implements OnInit,AfterViewInit,OnChanges,OnDestroy{

    AUTH_API :string | undefined;

    @ViewChild(MatPaginator)
  paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild(MatTable, { read: ElementRef }) private matTableRef!: ElementRef;
    @Input() filter!: ReportFilterBase<string>;
    @Input() filters: ReportFilterBase<string>[] | null = [];
    @Input() creportid!: string;
    @Input() commonFilter!: any[];
    @Input() reportHit: any;
    @Input() groupreport: any;
    @Input() fromdate:any;
    @Input() todate:any;
    @Input() defaultparam:string="";
    @Input() defaultparamvalue:string="";
    @Input() extraparams:string="";
    @Output() onEditClcked = new EventEmitter<any>();
    private eventsSubscription: Subscription = new Subscription;
    positionFilter = new FormControl();
  reprocessData:any;
  repostingremarks!:string;
    @Input() events!: Observable<void>;
selectedBankId :string="";
  showUploadModel:boolean=false;
  showCamsPostingModel:boolean=false;
  showcamspostingbtn:boolean=false;
  showpaymentpostingbtn:boolean=false;
  pressed = false;
  currentResizeIndex: number=0;
  startX: number=0;
  startWidth: number=100;
  isResizingRight: boolean=false;
  resizableMousemove!: () => void;
  resizableMouseup!: () => void;
  masterData:any;
  mode:string="view";
  masterMenu:string="";
  viewReport:boolean=true;
  contentLoaded:boolean=false;
  form: any = {};
  username="";
  reportMetaData:any;
  filterValue:any;
  action="view";
  reportTitle="";
  reportId="";
  displayedColumns: string[] = [];
  columnActions: any = [];
  dataSource=new MatTableDataSource(ELEMENT_DATA);
  reportSource:any;
  actionButtons:any=[];
  totalRows:number=0;
  recordsPerPage:number=20;
  activePage = 1;
 showFilter=false;
 divHeight:number=450;
 opts!: SlimScrollOptions;
 IsCombinedReport=false;
 navigationSubscription;
 keyword = 'value';
 passedParameter:string="";
 lastAppliedParams:string="";
//  today = new Date();
//     month:any = this.today.getMonth();
//     year:any = this.today.getFullYear();
//     day:any = this.today.getDate();
//  dateFilter = new FormGroup({
//   start: new FormControl(new Date(this.year, this.month, this.day)),
//   end: new FormControl(new Date(this.year, this.month, this.day)),
// });
// @Input() showReportFilterDate:boolean=true;
  constructor(
    private wmsGV :AppGlobal,private confirmDialogService: ConfirmDialogService,
    private reportService:ReportService,
    private route: ActivatedRoute,
    private qcs: ReportFilterControlService,
    public datepipe: DatePipe ,
    private router:Router,
    private token:TokenStorageService,
    private masterService:MastersService,
    private http:HttpClient,
    private login:LoginService,
    private tokenStorageService: TokenStorageService,
    private appConfig:AppConfig
  ) {
    
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
    if (e instanceof NavigationEnd) {
      this.ngOnInit();
    }
  });
 } 
 onExportClicked(type:string){
  if(type=='csv'){
    this.reportService.exportReport(this.reportId,'1',type,this.lastAppliedParams).subscribe((response: any) => {
      this.downloadFile(response,type);
    });
  }else if(type=='pdf'){
    this.reportService.exportReport(this.reportId,'1',type,this.lastAppliedParams).subscribe((response: any) => {
      this.downloadFile(response,type);
    });
  }
 }
 
 closeModel(){
   this.showUploadModel=false;
 }
 closeCamsPostingModel(){
  this.showCamsPostingModel=false;
  this.reprocessData={};
  this.repostingremarks="";
}
  showUploadDlg(data:any){
    this.selectedBankId=data.SL_No;
    this.showUploadModel=true;
  }
  showCamsPostingDlg(data:any,c:any){
    this.reprocessData={};
    this.reprocessData.data=data;
    this.reprocessData.column=c;
    this.repostingremarks="";
    this.showCamsPostingModel=true;
    this.refreshProcessButtonState(this.reprocessData.data);
  }
  refreshProcessButtonState(data:any){
    this.showpaymentpostingbtn=true;
    this.showcamspostingbtn=true;
    if(data['PaymentStatus']=='Y' && data['CamsStatus']!='Y')
      this.showcamspostingbtn=false;
    if(data['PaymentStatus']!='Y')
      this.showpaymentpostingbtn=false;
  }
 fileChangeEvent(fileInput: any) {
  if (fileInput.target.files && fileInput.target.files[0]) {
       var data:any={};
       data.fileType="branch";
       data.fileID=this.selectedBankId;
       data.file=fileInput.target.files[0];
       var ext =  fileInput.target.files[0].name.split('.').pop();
       if(ext=="XLS" || ext=="XLSX" ||ext=="xls" || ext=="xlsx"){
       }else{
        this.confirmDialogService.showMessage("Upload valid xlsx file", () => { });
        return false;
       };
       const formData = new FormData();

  for (const file of fileInput.target.files) {
    formData.append("file", file);
    formData.append("fileType", "branch");
    formData.append("fileID", this.selectedBankId);
  }

  this.AUTH_API = this.appConfig.apiBaseUrl + this.appConfig.apiPath + '/api/mdm/';
  const uploadReq = new HttpRequest('POST', this.AUTH_API + 'masters/uploadFiles', formData, {
    reportProgress: false,
  });
  
  const upload$ = this.http.post(this.AUTH_API + 'masters/uploadFiles', formData, {
    reportProgress: true,
    
    observe: 'events'
})
.pipe(
   
);
let uploadProgress=0;
// upload$.subscribe(
//   (res => {
//     if(res["body"]){
//       var r=res["body"];
//       if(r.isSuccess){
//         this.confirmDialogService.showMessage(r.successDto.successMessage, () => { });
//         this.closeModel();
//       }else{
//         this.confirmDialogService.showMessage(r.errorDto.message, () => { });
//       }
      
//     }
     
//   })
//   ,
// (err) => console.log(err))

/*event => {
  
  if (event.type == HttpEventType.UploadProgress) {
     uploadProgress = Math.round(100 * (event.loaded / event.total));
     if(uploadProgress==100){
      this.confirmDialogService.showMessage("Successfully uploaded", () => { });
      this.closeModel();
     }
  }
})*/
 /*this.http.request(uploadReq).subscribe(event => {
     
    this.closeModel();
  },
    (error: HttpErrorResponse) => {
      this.confirmDialogService.showMessage(error.message, () => { });
    });  

      */

       
        
  }
  return true;
}
  ngOnChanges(changes: SimpleChanges): void {
    //console.log(this.commonFilter);
    //console.log(this.events);
    if(changes['reportHit']){
      if(!changes['reportHit'].firstChange && changes['reportHit'].currentValue!=changes['reportHit'].previousValue){
        this.getReportData(false);
      }
    }
  }
  ngOnDestroy() {
    if (this.navigationSubscription) {  
        this.navigationSubscription.unsubscribe();
   }
    this.eventsSubscription?this.eventsSubscription.unsubscribe():"";
  }

  ngOnInit(): void {
    this.viewReport=true;
        this.mode="view";
        this.masterMenu="";
    const routeParams = this.route.snapshot.paramMap;
    const reportIdFromRoute = routeParams.get('reportid');
    this.opts = new SlimScrollOptions({
       
      barWidth: "8",
      barBorderRadius:"20",
      alwaysVisible:true,
      visibleTimeout: 1000,
      alwaysPreventDefaultScroll: true,
      
    });
    if(reportIdFromRoute){
      this.reportId = reportIdFromRoute;
    }
    else{
      this.reportId = this.creportid;
      this.divHeight=250;
      this.IsCombinedReport=false;
    }
     
    this.reportMetaData=[];
    this.displayedColumns=[];
    this.columnActions=[];
    this.filters=[];
    this.actionButtons=[];
    this.reportSource={};
    this.dataSource.data=[];
    this.filterValue="";
    this.getReportMeta();
    this.username=this.wmsGV.userEmail;
    this.opts = new SlimScrollOptions({
      position:'right',
      barWidth: "8",
      barBorderRadius:"20",
      alwaysVisible:true,
      visibleTimeout: 1000,
      alwaysPreventDefaultScroll: true,
      
    });
     
  }
   
  ngAfterViewInit() {
    //var previouscolumnSort=this.dataSource.sort.active;
    //var previousColumnSortOrder=this.dataSource.sortingDataAccessor.
    this.dataSource.paginator = this.paginator;
      this.sort.sortChange.subscribe(() => {
        //this.paginator.pageIndex = 0;
        //if(this.sort.active!=previouscolumnSort  || )
        this.getReportData(false);}
        );

    this.onResize();
  }
   
  get f() { 
    return this.form.controls; 
}
  get isValid() { return this.form.controls[this.filter.key].valid; }
  getDefaultDate(type:string='today'){
    let currentDate=new Date();
    let result=currentDate;
    if(type=="today"){
      result= currentDate;
    }else if(type=="previousday"){
      let previousDate=currentDate;
      previousDate.setDate(currentDate.getDate()-1);
      result=previousDate;
    }else if(type=="oneweek"){
      let previousDate=currentDate;
      previousDate.setDate(currentDate.getDate()-7);
      result= previousDate;
    }else if (type=="month"){
      let previousDate=currentDate;
      previousDate.setDate(1);
      result= previousDate;
    }
    
     return this.datepipe.transform(result, 'yyyy-MM-dd');
                      
  }
  getReportMeta(){
    var roleid="1";
    if(!this.groupreport){
      roleid=this.wmsGV.roleId;
    }
    //var rdata=JSON.parse('{"isSuccess":true,"data":{"filters":null,"sortname":"pam_name","sortorder":"asc","colNames":["BranchCode","Branch","PatientCode","IP No","Ward","RoomNo","Name","Gender","AdmissionDate","Guardian","ContactNo","SponsorCode","Corporate Name","Consulting Dr","Doctor","Referral","ReferredBy","RefCode","Discharge Status","Discharge Date"],"colModel":[{"name":"pam_branchcode","index":"pam_branchcode","width":0,"align":"right","sortable":true,"formatter":"integer","searchtype":"integer","search":true,"hidden":true},{"name":"branchname","index":"branchname","width":0,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true,"hidden":true},{"name":"pam_code","index":"pam_code","width":0,"align":"left","sortable":true,"formatter":"integer","searchtype":"integer","search":true,"hidden":true},{"name":"pam_no","index":"pam_no","width":0,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true,"hidden":true},{"name":"wm_wardname","index":"wm_wardname","width":0,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true,"hidden":true},{"name":"rm_roomname","index":"rm_roomname","width":0,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true,"hidden":true},{"name":"pam_name","index":"pam_name","width":250,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"pam_gender","index":"pam_gender","width":100,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"admissiondate","index":"admissiondate","width":150,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"pam_guardian_name","index":"pam_guardian_name","width":200,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"pam_mobileno","index":"pam_mobileno","width":150,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"pam_sponsor_code","index":"pam_sponsor_code","width":0,"align":"right","sortable":true,"formatter":"integer","searchtype":"integer","search":true,"hidden":true},{"name":"icm_name","index":"icm_name","width":150,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"pam_consulting_dr","index":"pam_consulting_dr","width":0,"align":"left","sortable":true,"formatter":"","searchtype":"integer","search":true,"hidden":true},{"name":"dr_name","index":"dr_name","width":150,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"pam_referral_dr","index":"pam_referral_dr","width":0,"align":"left","sortable":true,"formatter":"integer","searchtype":"integer","search":true,"hidden":true},{"name":"referraldr","index":"referraldr","width":0,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true,"hidden":true},{"name":"pam_ref_code","index":"pam_ref_code","width":0,"align":"left","sortable":true,"formatter":"integer","searchtype":"integer","search":true,"hidden":true},{"name":"dischargestatus","index":"dischargestatus","width":200,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true},{"name":"dischargedate","index":"dischargedate","width":200,"align":"left","sortable":true,"formatter":"text","searchtype":"text","search":true}]}}');
    
    
    this.reportService.getReportMeta(this.reportId,roleid).subscribe(
        rdata => {
          this.reportMetaData=[];
          this.displayedColumns=[];
          this.columnActions=[];
          this.filters=[];
          this.actionButtons=[];
          
            //if (rdata.isSuccess) {
                this.reportMetaData=rdata;//rdata.successDto.response;
                this.reportMetaData.reportdetail={};
                if(this.reportMetaData && this.reportMetaData.colNames.length>0){
                  //this.reportTitle=this.reportMetaData.colNames[0].menuCaption;
                  this.reportMetaData.reportdetail.defaultSortField=this.reportMetaData.colModel[0].name;
                  this.reportMetaData.reportColumns=[];
                  var index=0;
                  this.reportMetaData.colModel.forEach((element:any) => {
                    if(element.width>0){
                      this.displayedColumns.push(element.name);
                    }
                    if(element.operation!=undefined && element.operation=="1"){
                      this.columnActions.push(element);
                    }else if(element.width>0){
                        var obj:any={};
                        obj.dataSourceColumnName=element.name;
                        obj.allowsort=true;
                        obj.columnWidth=element.width;
                        obj.columnTitle=this.reportMetaData.colNames[index];
                        obj.columnDataType=element.searchtype;
                        obj.formatter=element.formatter;
                        if(element.formatter=='statusimage'){
                          obj.format=[];
                          obj.formatCondition=[];
                          let formattingValue:any=element.image.split("~");
                          for(let fv of formattingValue){
                              obj.format.push(fv.split(";")[0]);
                              obj.formatCondition.push(fv.split(";")[1]);
                          }
                        }
                        this.reportMetaData.reportColumns.push(obj);
                    }
                    


                    //this.displayedColumns.push(element.columnTitle);
                    index++;
                  });
                  var params=undefined;
                  var paramDataTypes=undefined;
                  if(this.reportMetaData.parameters!=undefined && this.reportMetaData.parameters!=null && this.reportMetaData.parameters!=""){
                    params=this.reportMetaData.parameters.split(",");
                  }
                  if(this.reportMetaData.paramtypes!=undefined && this.reportMetaData.paramtypes!=null && this.reportMetaData.paramtypes!=""){
                    paramDataTypes=this.reportMetaData.paramtypes.split(",");
                  }
                   /*this.reportMetaData.reportActions.forEach((element:any)=> {
                    if(element.type=="column"){
                      this.displayedColumns.push(element.name);
                      this.columnActions.push(element);
                    }else{
                      this.actionButtons.push(element);
                    }
                  }); */
                  if(params!=undefined && params.length>0){
                    this.reportMetaData.filters=[];
                      for(var i=0;i<params.length;i++){
                        var obj:any={};
                        obj.datasourceId=0;
                        obj.filterInputValues="today";
                        obj.lovList=0;
                        obj.reportFilterCaption=params[i];
                        obj.reportFilterName=params[i];
                        obj.reportFilterOrder=i;
                         obj.reportFilterType=paramDataTypes[i];
                        this.reportMetaData.filters.push(obj);
                      }
                  }
                  
                  if(this.reportMetaData.filters!=null){
                  this.reportMetaData.filters.forEach((element:any) => {
                    this.showFilter=this.IsCombinedReport ?false:true;
                    if(element.reportFilterType=="text"){
                      this.filters!.push( new Textbox({
                        key: element.reportFilterName.toLowerCase(),
                        label: element.reportFilterCaption,
                        type: 'textbox',
                        order: element.reportFilterOrder,
                        
                      }))
                    } else if(element.reportFilterType=="date"){
                      
                      this.filters!.push( new Textbox({
                        key: element.reportFilterName.toLowerCase(),
                        label: element.reportFilterCaption,
                        type: 'date',
                        order: element.reportFilterOrder,
                        value: "" //this.getDefaultDate(element.filterInputValues.toLowerCase()).toString()
                      }))
                      //this.datepipe.transform(this.data.subject.next_followUp_date, 'yyyy-MM-dd')
                    } else if(element.reportFilterType=="dropdown"){
                      var option:any=[];
                      if(element.filterInputValues && element.filterInputValues!=""){
                        var op=element.filterInputValues.split(';');
                        op.forEach((e:any) => {
                          var val:any={};
                          val.key=e.split(':')[1];
                          val.value=e.split(':')[0];
                          option.push(val);
                        });
                      }
                      this.filters!.push( new Dropdown({
                        key: element.reportFilterName.toLowerCase(),
                        label: element.reportFilterCaption,
                        options: option,
                        order: element.reportFilterOrder
                      }))
                    } else if(element.reportFilterType=="autocomplete"){
                      var option:any=[];
                      if(element.filterInputValues && element.filterInputValues!=""){
                        var op=element.filterInputValues.split(';');
                        op.forEach((e:any) => {
                          var val:any={};
                          val.key=e.split(':')[0];
                          val.value=e.split(':')[0];
                          option.push(val);
                        });
                      }else{
                        option=element.lovList;
                      }
                      this.filters!.push( new Dropdown({
                        key: element.reportFilterName.toLowerCase(),
                        label: element.reportFilterCaption,
                        options: option,
                        order: element.reportFilterOrder
                      }))
                    } else{
                      this.filters!.push( new Textbox({
                        key: element.reportFilterName.toLowerCase(),
                        label: element.reportFilterCaption,
                        type: 'textbox',
                        order: element.reportFilterOrder,
                        
                      }))
                    }
                  });
                }
                  
                  //this.filters.sort((a, b) => a.order - b.order);
                  this.form = this.qcs.toFormGroup(this.filters as ReportFilterBase<string>[]);
                  if(!this.showFilter && !this.IsCombinedReport){
                      this.getReportData(false);
                  }
                }
                  
            /*}else {
              if(rdata.errorDto.errorCode=="gmsE009"){
                this.confirmDialogService.showMessage(rdata.errorDto.message, () => { });
                 
                this.tokenStorageService.signOut();
                this.router.navigate(['/login']);
                window.location.reload();
              }
              
            }*/
        },
        err => {
            this.confirmDialogService.showMessage(err.message, () => { });
        }
    );
  }
  getBankList(){
    /*this.service.getBankList(this.masterObj).subscribe(
        rdata => {
            if (rdata.isSuccess) {
                this.banksdata=rdata.successDto.response;
            }
            
        });*/
}
  handleClick(event: any, type:string) {
    //console.log("handle click"+type);
    if(type=="exportToExcel()"){
      //this.exportToExcel();
      this.download();
    }else if(type=="addNewBankData()"){
        this.viewReport=false;
        this.mode="new";
        this.masterMenu="bank";
        this.masterData=undefined;
    }else if(type=="EditBankData()"){
      this.viewReport=false;
      this.mode="edit";
      this.masterMenu="branch";
    }else if(type=="addNewBankBranchData()"){
      this.viewReport=false;
      this.mode="new";
      this.masterMenu="branch";
      this.masterData=undefined;
  }else if(type=="EditBankBranchData()"){
    this.viewReport=false;
    this.mode="edit";
    this.masterMenu="branch";
  }else if(type=="addNewSchemeData()"){
    this.viewReport=false;
    this.mode="new";
    this.masterMenu="scheme";
    this.masterData=undefined;
  }else if(type=="reProcess()"){
    this.confirmDialogService.showMessage("Procedure / Service not configured", () => { });
  }else if(type=="exportallbankbranch"){
    this.exportallbankbranch();
  }     
}

  exportToExcel(){
    var inputData:any={};
    inputData.ReportId=this.reportId;
    inputData.RoleId=Number(this.wmsGV.roleId);
    inputData.page=1;
    inputData.rows=-1;
    inputData.sortField="";
    inputData.sortOrder="";
    inputData.searchText=this.filterValue?this.filterValue:"";
    inputData.reportParams=[];
    if(!this.form){
      return;
    }
    if(this.form.getRawValue()){
      var d=this.form.getRawValue();
      this.reportMetaData.filters.forEach((element:any) => {
        var obj:any={};
        obj.ParamName=element.reportFilterName;
        obj.ParamValue=d[element.reportFilterName];
        inputData.reportParams.push(obj);
      });
    }
    
    
// this.reportService.XLsExportReportData(inputData).subscribe(
//   (  blob: any) => {
//     this.downloadFile(blob!);
//   },
//   (  error: { message: any; }) => {
//     console.log(error.message);
//     console.log("Something went wrong while downloading");
//   });
  }
  download() {
    var inputData:any={};
    inputData.ReportId=this.reportId;
    inputData.RoleId=Number(this.wmsGV.roleId);
    inputData.page=1;
    inputData.rows=-1;
    inputData.sortField="";
    inputData.sortOrder="";
    inputData.searchText=this.filterValue?this.filterValue:"";
    inputData.reportParams=[];
    if(!this.form){
      return;
    }
    
    if(this.form.getRawValue()){
      var d=this.form.getRawValue();
      this.reportMetaData.filters.forEach((element: { reportFilterName: string; }) => {
         
        var obj:any={};
        obj.ParamName=element.reportFilterName;
        obj.ParamValue=d[element.reportFilterName.toLowerCase()];
        inputData.reportParams.push(obj);
      });
    }
    //this.fileService.downloadFile().subscribe(response => {
		// this.reportService.XLsExportReportData1(inputData).subscribe((response: any) => { //when you use stricter type checking
		// 	//var blob = new Blob(response);
    //   var url = window.URL.createObjectURL(response);
    //   //let pwa = window.open(url);
    //   const a = document.createElement('a');
    //   a.href = url;
    //   a.download = this.reportTitle + '.xlsx';
    //   a.click();
    //   window.URL.revokeObjectURL(url);

    //   /*let blob:any = new Blob([response]);
		// 	const url = window.URL.createObjectURL(blob);
		// 	this._FileSaverService.save(blob, 'employees.xlsx');*/
		// //}), error => console.log('Error downloading the file'),
		// }), (error: any) => {console.log('Error downloading the file') ;this.confirmDialogService.showMessage("Error downloading the file", () => { });} , //when you use stricter type checking
    //              () => console.info('File downloaded successfully');
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
  
  getReportData(start:any){
    
    var inputData:any={};
    inputData.ReportId=this.reportId;
    inputData.RoleId=Number(this.wmsGV.roleId);
    inputData.page=this.activePage;
    inputData.rows=this.recordsPerPage;
    
    try{
      if(start){
        inputData.sortField=this.reportMetaData.sortname?this.reportMetaData.sortname:this.sort.active;
        
        const sortState: any = {active: inputData.sortField, direction: 'desc'};
        this.sort.active = sortState.active;
        this.sort.direction = sortState.direction;
        
        //this.sort.sortChange.emit(sortState);
      }else{
        //inputData.sortField=this.sort.active;
      }
      
    
      inputData.sord=this.sort.direction;
    }catch{}
    inputData.searchText=this.filterValue?this.filterValue:"";
    inputData.reportParams=[];
    if(this.IsCombinedReport){
      var v=this.commonFilter.find(x=>x.ParamName=="filtervalue");
      if(v)
        inputData.searchText=v.ParamValue!=undefined?v.ParamValue:"";
        if(this.reportMetaData.filters!=null){
        this.reportMetaData.filters.forEach((element:any) => {
          var obj:any={};
          obj.ParamName=element.reportFilterName;
          var v=this.commonFilter.find(x=>x.ParamName==element.reportFilterName.toLowerCase());
          if(v){
            obj.ParamValue=v.ParamValue;
          }else{
            obj.ParamValue="";
          }
          
          inputData.reportParams.push(obj);
        });
      }
    }else{
      if(!this.form){
        return;
      }
      if(this.form.getRawValue()){
        var d=this.form.getRawValue();
        if(this.reportMetaData.filters!=null){
          this.reportMetaData.filters.forEach((element:any) => {
           if(element.reportFilterType=="date"){
            var obj:any={};
            obj.ParamName=element.reportFilterName;
            obj.ParamValue= this.datepipe.transform(d[element.reportFilterName.toLowerCase()] , 'dd/MM/yyyy');
            inputData.reportParams.push(obj);
           }else{
            var obj:any={};
            obj.ParamName=element.reportFilterName;
            obj.ParamValue=d[element.reportFilterName.toLowerCase()];
            inputData.reportParams.push(obj);
           }
            
          });
        }
        
      }
    }
    if(!this.IsCombinedReport){
      if(inputData.reportParams.find((x:any)=>x.ParamName=="todate")){
        //var date1 = this.datepipe.transform(inputData.reportParams.find(x=>x.ParamName=="fromdate").ParamValue, 'yyyy-MM-dd');
        //var date2 = this.datepipe.transform(inputData.reportParams.find(x=>x.ParamName=="etodate").ParamValue, 'yyyy-MM-dd');
        var date1 =  inputData.reportParams.find((x:any)=>x.ParamName=="fromdate").ParamValue ;
        var date2 =  inputData.reportParams.find((x:any)=>x.ParamName=="todate").ParamValue ;

        if(date1>date2){
          this.confirmDialogService.showMessage("To date should be greater than from date", () => { });
          this.showFilter=true;
          return;
        }
      }
    }
    var inputString:string='id='+this.reportId+'&branchcode=1&nd=1684952071224&rows='+this.recordsPerPage+'&page='+this.activePage+this.passedParameter;
    if(inputData.sortField)    
      inputString=inputString+'&sidx='+inputData.sortField
    else{
      inputString=inputString+'&sidx='+this.reportMetaData.sortname;
      inputString=inputString+'&sord='+this.reportMetaData.sortorder;
    }
    if(inputData.sord)
      inputString=inputString+'&sord='+inputData.sord
      inputData.reportParams.forEach((element:any) => {
        inputString=inputString+'&'+element.ParamName+'='+element.ParamValue;
      });
      let search:boolean=false;
      for(let c of this.reportMetaData.reportColumns){
        if(c.searchText!=undefined && c.searchText!=""){
          inputString=inputString+"&"+c.dataSourceColumnName+"="+c.searchText;
          search=true;
        }
      }
      if(search){
        inputString=inputString+"&_search=true";
      }else{
        inputString=inputString+"&_search=false";
      }
      if(this.fromdate!=undefined && this.fromdate!=""){
        let pipe = new DatePipe('en-IN');
        let start:any=pipe.transform(this.fromdate,'yyyy-MM-dd');
        inputString=inputString+"&fromdate="+start;
      }
      if(this.todate!=undefined && this.todate!=""){
        let pipe = new DatePipe('en-IN');
        let end:any=pipe.transform(this.todate,'yyyy-MM-dd');
        inputString=inputString+"&todate="+end;
      }
      if(this.defaultparam!=undefined && this.defaultparam!=""){
        inputString=inputString+"&"+this.defaultparam+"="+this.defaultparamvalue;
      }
      if(this.extraparams!=undefined && this.extraparams!=""){
        inputString=inputString+"&"+this.extraparams;
      }
      this.lastAppliedParams=inputString;
    this.reportService.getReportData(inputString).subscribe(
        rdata => {
            //if (rdata.isSuccess) {
              this.reportSource=rdata.rows;
              this.dataSource=new MatTableDataSource(rdata.rows);
              //var tlength=rdata.successDto.response.length>0?rdata.successDto.response[0].TOTALPAGE:undefined;
              //if(!tlength)
              //  tlength=rdata.successDto.response.length>0?rdata.successDto.response[0].TotalPage:0;
              this.totalRows=rdata.records;
              if(this.dataSource.paginator){
                this.dataSource.paginator!.length=this.totalRows;
                this.dataSource.paginator = this.paginator;
              }
            //}
        },
        err => {
            this.confirmDialogService.showMessage(err.message, () => { });
        }
    );
  }
   
  getFormttedData(c:any,element:any){
        if(c.formatter=='statusimage'){
            if(c.formatCondition.indexOf(element[c.dataSourceColumnName])!=-1){}
            if(c.format.length>c.formatCondition.indexOf(element[c.dataSourceColumnName])){
              return c.format[c.formatCondition.indexOf(element[c.dataSourceColumnName])];
            }else{
              return '';
            }
        }else{
          return '';
        }
  }
  applyFilter() {

    this.dataSource.filter = this.filterValue.trim().toLowerCase();
    // console.log(this.dataSource.filter);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
     
  
  displayActivePage(activePageNumber: number): void {
    if(this.activePage != (activePageNumber)){
      this.activePage = activePageNumber;
      this.getReportData(false);
    }
  }
  setReordsPerPage(activeCount: number):void{
    this.recordsPerPage=activeCount;
    this.activePage=1;
    this.getReportData(false);
  }
  topSearchRecord(){
    this.closeFilter();
    this.activePage=1;
    this.getReportData(false);
  }
  SearchRecord(){
    this.closeFilter();
    this.activePage=1;
    this.getReportData(true);
  }
  search(){
      this.topSearchRecord();    
  }
  showReportFilter(){
    this.showFilter=true;
  }
  closeFilter(){
    this.showFilter=false;
  }
  

  setDisplayedColumns() {
    this.reportMetaData.reportColumns.forEach((column: { index: any; field: string; }, index: number | number) => {
      column.index = index;
      this.displayedColumns[index] = column.field;
    });
  }

  onResizeEnd(event: any, columnName: string): void {
		if (event.edges.right) {
			const cssValue = event.rectangle.width + 'px';
			const columnElts = document.getElementsByClassName('mat-column-' + columnName);
			for (let i = 0; i < columnElts.length; i++) {
				const currentEl = columnElts[i] as HTMLDivElement;
				currentEl.style.width = cssValue;
			}
		}
	}
  @HostListener("window:resize")
  onResize() {  
      if(!this.IsCombinedReport)
      setTimeout(() => {
        this.divHeight = window.innerHeight - 230;  
      });
  }
  runReport(data:FilterItems[]){
    var inputData:any={};
    inputData.ReportId=this.reportId;
    inputData.RoleId=Number(this.wmsGV.roleId);
    inputData.page=this.activePage;
    inputData.rows=this.recordsPerPage;
    inputData.sortField="";
    inputData.sortOrder="";
    inputData.searchText=this.filterValue?this.filterValue:"";
    inputData.reportParams=[];
    if(!this.form){
      return;
    }
    if(this.form.getRawValue()){
      var d=this.form.getRawValue();
      this.reportMetaData.filters.forEach((element: { reportFilterName: string | number; }) => {
         
        var obj:any={};
        obj.ParamName=element.reportFilterName;
        obj.ParamValue=d[element.reportFilterName];
        inputData.reportParams.push(obj);
      });
      if(!this.IsCombinedReport){
        if(inputData.reportParams.find((x: { ParamName: string; })=>x.ParamName=="enddate")){
          var date1 = this.datepipe.transform(inputData.reportParams.find((x: { ParamName: string; })=>x.ParamName=="startdate").ParamValue, 'yyyy-MM-dd');
          var date2 = this.datepipe.transform(inputData.reportParams.find((x: { ParamName: string; })=>x.ParamName=="enddate").ParamValue, 'yyyy-MM-dd');

          if(date1!>date2!){
            this.confirmDialogService.showMessage("End date should be greater than start date", () => { });
            this.showFilter=true;
            return;
          }
        }
      }
    }
    

    this.reportService.getReportData(inputData).subscribe(
        rdata => {
            if (rdata.isSuccess) {
              this.reportSource=rdata.successDto.response;
              this.dataSource=new MatTableDataSource(rdata.successDto.response);
              this.totalRows=rdata.successDto.response.length>0?rdata.successDto.response[0].TotalPage:0;
              if(this.dataSource.paginator){
                this.dataSource.paginator.length=this.totalRows;
                this.dataSource.paginator = this.paginator;
              }
              
              
            }
        },
        err => {
            this.confirmDialogService.showMessage(err.message, () => { });
        }
    );
  }
  LinkAction(col: { columnLink: string; },element: any){
    var methodName=col.columnLink.split('~');
    // this[methodName[0]](methodName[1],col,element); by me
  }
  showImage(queryid: any,col: { columnLinkParams: any; dataSourceColumnName: any; },element: any){
   
    if(!col.columnLinkParams){
      return;
    }
    var colValue=this.reportMetaData.reportColumns.find((x: { dataSourceColumnName: any; })=>x.dataSourceColumnName==col.dataSourceColumnName);
    
    // if(colValue){
    //   this.docService.setQueryParams(this.reportId,colValue.columnLinkParams,element,queryid);
    //   this.docService.showMessage("show", () => { });
    // }
  }
  OpenAnotherReport(queryid: any,col: any,element: any){
    console.log("open another report");
  }
  close(){
    this.viewReport=true;
    this.mode="view";
    this.masterMenu="";
    this.SearchRecord();
  }

  reProcess(data:any,c:any){
    var processData:any={};
    processData.transtype=c.transType;
    processData.transno=data[c.paramColumns].toString();
    processData.transstatus="P";
    processData.userId=this.wmsGV.userId;
    processData.remarks="";
    processData.oldpaymentstatus="";
    processData.processType="C";
  //   this.masterService.reprocessTrans(processData).subscribe(
  //     (      rdata: { isSuccess: any; successDto: { response: { message: string; }; }; errorDto: { message: string; }; }) => {
  //         if (rdata.isSuccess) {
  //             this.confirmDialogService.showMessage(rdata.successDto.response.message, () => { });
  //             data["processtransno"]=data[c.paramColumns].toString();
  //         } else {
  //             this.confirmDialogService.showMessage(rdata.errorDto.message, () => { });
  //         }
  //     },
  //     (      err: { message: string; }) => {
  //         this.confirmDialogService.showMessage(err.message, () => { });
  //     }
  // );
    //this.confirmDialogService.showMessage("Procedure / Service not configured", () => { });
  }
  reProcessPurchaseStatus(type:string){

    var processData:any={};
    processData.transtype=this.reprocessData.column.transType;
    processData.transno=this.reprocessData.data[this.reprocessData.column.paramColumns].toString();
    processData.transstatus="P";
    processData.userId=this.wmsGV.userId;
    processData.remarks=this.repostingremarks;
    processData.oldpaymentstatus=this.reprocessData.data["PaymentStatus"].toString();
    processData.processType=type;
    if(this.repostingremarks==null || this.repostingremarks==undefined || this.repostingremarks==""){
      this.confirmDialogService.showMessage("remark is required", () => { });
      this.refreshProcessButtonState(this.dataSource.data.find(x=>x[this.reprocessData.column.paramColumns]==processData.transno));

      return;
    }
  //   this.masterService.reprocessTrans(processData).subscribe(
  //     (      rdata: { isSuccess: any; successDto: { response: { message: string; code: string; }; }; errorDto: { message: string; }; }) => {
  //         if (rdata.isSuccess) {
  //             this.confirmDialogService.showMessage(rdata.successDto.response.message, () => { });
  //             if(rdata.successDto.response.code=="1"){
  //               if(type=="C"){
  //                 this.dataSource.data.find(x=>x[this.reprocessData.column.paramColumns]==processData.transno)['CamsStatus']='Y'
  //               }else{
  //                 this.dataSource.data.find(x=>x[this.reprocessData.column.paramColumns]==processData.transno)['PaymentStatus']='Y'
  //               }
  //               this.refreshProcessButtonState(this.dataSource.data.find(x=>x[this.reprocessData.column.paramColumns]==processData.transno));
  //               this.repostingremarks="";
  //             }
              
  //         } else {
  //             this.confirmDialogService.showMessage(rdata.errorDto.message, () => { });
  //         }
  //     },
  //     (      err: { message: string; }) => {
  //         this.confirmDialogService.showMessage(err.message, () => { });
  //     }
  // );
    //this.confirmDialogService.showMessage("Procedure / Service not configured", () => { });
  }
  handleColumnClick(event: any, c:any,data:any) {
    //console.log("column click"+type);
    if(c.name=="Process" || c.name=="Edit" || c.name=="View"){
        this.EditData(c,data);
    }else if(c.action=="reProcess"){
      this.reProcess(data,c);
    }else if(c.action=="reProcessPayment"){
      this.showCamsPostingDlg(data,c);
    }else if(c.action=="downloaddocument" || c.action=="downloadxml" || c.action=="downloadcontractform"){
      this.downloadDocuments(data,c);
    }else if(c.name=="Print"){
      this.DownloadBarCode(data);
    }else {
      // this[c.action](data); //by me
    }
  }
  handleRowSelect(event: any, c:any,data:any) {
    //console.log("column click"+type);
    if(c.name=="Select"){
        data.selected=event.target.checked?1:0;
        this.EditData(c,data);
    }
  }
  EditData(c:any,data:any){
    this.onEditClcked.emit(data);
    //console.log("edit data");
    // var menudata=this.token.getMenu();
    //   if(menudata.successDto.response){
    //     var m=menudata.successDto.response.find((x: { encryptedReportId: string; })=>x.encryptedReportId==this.reportId);
    //     if(m){
    //       if(m.menuId==30){
    //         this.masterMenu="bank";
    //       }else if(m.menuId==37){
    //         this.masterMenu="branch";
    //       }else if(m.menuId==40){
    //         this.masterMenu="schemeapproval";
    //       }else if(m.menuId==29){
    //         this.masterMenu="scheme";
    //       }
    //     }
    //   }
    //this.router.navigateByUrl(c.link,data);
    // this.viewReport=false;
    // this.masterData=data;
    // this.mode="edit";    
  }
  DownloadBarCode(data:any): void {
 
    this.reportService.getPickListBarcode(data.stockmovementid).subscribe((response: any) => {
      this.downloadFile(response,'pdf');
    });
    
  }
  downloadDocuments(data:any,c:any){
    var inputData:any={};
    inputData.ReportId=this.reportId;
    inputData.RoleId=Number(this.wmsGV.roleId);
    inputData.page=1;
    inputData.rows=-1;
    inputData.sortField="";
    inputData.sortOrder="";
    inputData.searchText="";
    inputData.imagetype= "";
    inputData.QueryID= c.queryId;
    inputData.reportParams=[];
 
        var obj:any={};
        obj.ParamName=c.paramColumns;
        obj.ParamValue=data[c.paramColumns].toString();
        inputData.reportParams.push(obj);
 
    var filename="document."+c.outputFormat;
		// this.reportService.DownloadDocuments(inputData).subscribe((response: any) => { //when you use stricter type checking
		// 	//var blob = new Blob(response);
    //   var url = window.URL.createObjectURL(response);
    //   //let pwa = window.open(url);
    //   const a = document.createElement('a');
    //   a.href = url;
    //   a.download = filename;
    //   a.click();
    //   window.URL.revokeObjectURL(url);
		// }), (error: any) => {console.log('Error downloading the file') ;this.confirmDialogService.showMessage("Error downloading the file", () => { });} , //when you use stricter type checking
    //              () => console.info('File downloaded successfully');
  }
  exportallbankbranch(){
    var inputData:any={};
    inputData.ReportId=this.reportId;
    inputData.RoleId=Number(this.wmsGV.roleId);
    inputData.page=this.activePage;
    inputData.rows=-2;
    inputData.sortField="";
    inputData.sortOrder="";
    inputData.searchText=this.filterValue?this.filterValue:"";
    inputData.reportParams=[];
    
    // this.reportService.BankBranchAllDataExport(inputData).subscribe((response: any) => { //when you use stricter type checking
    //   var url = window.URL.createObjectURL(response);
    //   const a = document.createElement('a');
    //   a.href = url;
    //   a.download = this.reportTitle + '.xlsx';
    //   a.click();
    //   window.URL.revokeObjectURL(url);
		// }), (error: any) => {console.log('Error downloading the file') ;this.confirmDialogService.showMessage("Error downloading the file", () => { });} , //when you use stricter type checking
    //              () => console.info('File downloaded successfully');

  }
}





//////
