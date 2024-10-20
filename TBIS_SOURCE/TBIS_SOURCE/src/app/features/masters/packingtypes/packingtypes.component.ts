import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { PackingCategoryDDL } from '../../../models/masters';

@Component({
  selector: 'app-packingtypes',
  standalone: false,
  templateUrl: './packingtypes.component.html',
  styleUrl: './packingtypes.component.css'
})
export class PackingtypesComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    packingcategory!: PackingCategoryDDL[];
     
    // @ViewChild('frm') frm!: NgForm;
    // @ViewChild(ReportComponent, {static : true}) rpt : ReportComponent;
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }

    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.packingTypeId="";
        this.masterData.packingName="";
        this.masterData.packingShortName="";
        this.masterData.packingHeight="";
        this.masterData.packingWidth="";
        this.masterData.packingLength="";
        this.masterData.packingWeight="";
        this.masterData.m2="";
        this.masterData.m3="";
        this.masterData.sbqty="";
        this.masterData.active=true;
        this.masterData.packingCategory="BOX";
        this.masterData.packingColor="#333333";
  
    }
    }

  ngOnInit(): void {
    this.getAjaxData('packingcategory');
  }

  getAjaxData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.packingcategory=rdata.data;
            }
         },
        error: (e) => console.error(e),
        complete: () => console.info('complete') 
    })
}

clearData(){
    this.masterData={
        packingTypeId:'',
        packingName: '',                
        packingShortName: '',
        packingHeight: '',
        packingWidth: '',
        packingLength: '',
        packingWeight: '',
        m2: '',
        m3: '',
        sbqty:'',
        active: true,
        packingCategory: "BOX",
        packingColor:'#333333',
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
        this.getPackingTypesById(event.packingtypeid.toString());
    }
  }

 


updatePackingTypes(): void {

    if (this.IsNullorEmpty(this.masterData.packingName)) {
        this.toastr.info("Please enter packing name");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.packingShortName)) {
        this.toastr.info("Please enter packing short name");
        return;
    }

  let data:any={};
   data.packingTypeId=this.masterData.packingTypeId=="" ? "0":this.masterData.packingTypeId;
   data.packingName=this.masterData.packingName;
   data.packingShortName=this.masterData.packingShortName;
   data.packingHeight=this.masterData.packingHeight;
   data.packingWidth=this.masterData.packingWidth;
   data.packingLength=this.masterData.packingLength;
   data.packingWeight=this.masterData.packingWeight;
   data.m2=this.masterData.m2 ?? " ";
   data.m3=this.masterData.m3 ?? " ";
   data.active=this.masterData.active;
   data.userId=this.gVar.userId;
   data.packingCategory=this.masterData.packingCategory;
   data.packingColor=this.masterData.packingColor;
   this.service.updatePackingTypes(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.cancelAdd();
            this.action="view";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })

}
getPackingTypesById(typeid: any): void {
    this.service.getPackingTypesById(typeid).subscribe({
        next: (rdata) => {
            if (rdata.result) {
                this.masterData={};         
                this.masterData.packingTypeId=rdata.result.packingTypeId;
                this.masterData.packingName=rdata.result.packingName;
                this.masterData.packingShortName=rdata.result.packingShortName;
                this.masterData.packingHeight=rdata.result.packingHeight;
                this.masterData.packingWidth=rdata.result.packingWidth;
                this.masterData.packingLength=rdata.result.packingLength;
                this.masterData.packingWeight=rdata.result.packingWeight;
                this.masterData.m2=rdata.result.m2;
                this.masterData.m3=rdata.result.m3;
                this.masterData.sbqty=rdata.result.sbqty;
                this.masterData.active=rdata.result.active;
                this.masterData.packingCategory=rdata.result.packingCategory;
                this.masterData.packingColor=rdata.result.packingColor;
            } else {
              this.toastr.warning(rdata.status);                
          }
      }, 
      error: (err) => { this.toastr.warning(err);  },    
    
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

      get f() { return this.form.controls; }

      IsNullorEmpty(value: any): boolean {
      if (value == undefined || value == "" ) {
          return true;
      }
      return false;;
      }
    

  
}
