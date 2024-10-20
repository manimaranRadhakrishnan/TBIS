import { Component,Input,OnInit,AfterViewInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Editor } from 'ngx-editor';


@Component({
  selector: 'app-esopmaster',
  standalone: false,
  templateUrl: './esopmaster.component.html',
  styleUrl: './esopmaster.component.css'
})


export class EsopmasterComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    preview:string="";
    werehouses:any=[];
    sopsourcetypes:any=[];
    soptypes:any=[];
    editorDesc!: Editor;
    editorDescLoc!: Editor;
    textareaConfig = { 'editable': true, 'spellcheck': true, 'height': 'auto', 'minHeight': '100px', };

    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }


    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.sopId="";
        this.masterData.sopName="";
        this.masterData.sopNameLocal="";
        this.masterData.sopTypeid=null;
        this.masterData.sopSourceTypeid=null;
        this.masterData.sopDesc="";
        this.masterData.sopDescLocal="";
        this.masterData.warehouseId=null;
        this.masterData.sopIcon="";
        this.masterData.isActive=true;
    }
}

  ngOnInit(): void {
    this.clearData();
    this.editorDesc = new Editor();
    this.editorDescLoc = new Editor();
    this.getAjaxWarehouseData('warehouse');
    this.getAjaxSourceTypesData('sopsourcetypes');
    this.getAjaxSOPTypesData('soptypes');
  }

  ngOnDestroy(): void {
    this.editorDesc.destroy();
    this.editorDescLoc.destroy();
  }

  getAjaxWarehouseData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.werehouses=[];
                rdata.data.forEach((element:any) => {
                    this.werehouses.push(element);
                });
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })
}
getAjaxSourceTypesData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.sopsourcetypes=[];
              rdata.data.forEach((element:any) => {
                  this.sopsourcetypes.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}
getAjaxSOPTypesData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.soptypes=[];
              rdata.data.forEach((element:any) => {
                  this.soptypes.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}

  clearData(){
    this.masterData={
      sopId: '',
      sopName: '',                
      sopNameLocal: '',
      sopTypeid: null,
      sopSourceTypeid: null,
      sopDesc: '',
      sopDescLocal: '',
      warehouseId: null,
      sopIcon:'',
      isActive: true
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
          this.getEsopMasterById(event.sopid.toString());
      }
  }
  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
    }



updateEsopMaster(): void {

// this.toastr.info(this.masterData.sopDesc);
// return;

if (this.IsNullorEmpty(this.masterData.sopName)) {
  this.toastr.info("Please enter SOP Name");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopNameLocal)) {
  this.toastr.info("Please enter SOP Name Local");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopTypeid)) {
  this.toastr.info("Please select SOP Type");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopDesc)) {
  this.toastr.info("Please enter SOP Description");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopDescLocal)) {
  this.toastr.info("Please enter SOP Description Local");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopIcon)) {
  this.toastr.info("Please Choose Thumbnail");
    return;
}

  let data:any={};
   data.sopId=this.masterData.sopId==""?"0":this.masterData.sopId;
   data.sopName=this.masterData.sopName;
   data.sopNameLocal=this.masterData.sopNameLocal;
   data.sopTypeid=this.masterData.sopTypeid;
   data.sopSourceTypeid=this.masterData.sopSourceTypeid;
   data.sopDesc=this.masterData.sopDesc;
   data.sopDescLocal=this.masterData.sopDescLocal;
   data.warehouseId=this.masterData.warehouseId==null?0:this.masterData.warehouseId;
   data.sopIcon=this.masterData.sopIcon;
   data.active=this.masterData.isActive;
   data.userId=this.gVar.userId;


   this.service.updateEsopMaster(data).subscribe({
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

getEsopMasterById(typeid: any): void {

  this.service.getEsopMasterById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {  
          this.masterData={};         
          this.masterData.sopId=rdata.result.sopId;
          this.masterData.sopName=rdata.result.sopName;
          this.masterData.sopNameLocal=rdata.result.sopNameLocal;
          this.masterData.sopTypeid=rdata.result.sopTypeid+'';
          this.masterData.sopSourceTypeid=rdata.result.sopSourceTypeid+'';
          this.masterData.sopDesc=rdata.result.sopDesc;
          this.masterData.sopDescLocal=rdata.result.sopDescLocal;
          this.masterData.warehouseId=rdata.result.warehouseId==0?null:rdata.result.warehouseId+'';
          this.masterData.isActive=rdata.result.active;
          this.masterData.sopIcon=rdata.result.sopIcon;
          this.preview='data:image/png;base64, '+rdata.result.sopIcon;
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    // errorHandler 
  });
}

fileChangeEvent(fileInput: any) {
  let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';
  if (fileInput.target.files && fileInput.target?.files[0]) {
    let ext=fileInput.target.files[0].name.split('.').pop();
   const max_size = 1000*1024;
   let allowed_types:any=[];
   let ty=allowedFileTypes.split(',');
    if(ty.indexOf(ext)==-1){
      this.toastr.error("Upload valid file format");
      return;
    }
   const reader = new FileReader();
   reader.onload = (e: any) => {
         let imgBase64Path = e.target.result;
         this.preview=imgBase64Path;
         imgBase64Path = imgBase64Path.replace('data:image/png;base64,','')
         .replace('data:image/jpeg;base64,','')
         .replace('data:application/pdf;base64,','')
         .replace('data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64,','')
         .replace('data:application/msword;base64,','');
         this.masterData.sopIcon=imgBase64Path;
   };
   reader.readAsDataURL(fileInput.target.files[0]);
}
}
    
}