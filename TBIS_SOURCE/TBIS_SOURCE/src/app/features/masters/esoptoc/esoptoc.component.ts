import { Component,Input,OnInit,AfterViewInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-esoptoc',
  standalone: false,
  templateUrl: './esoptoc.component.html',
  styleUrl: './esoptoc.component.css'
})

export class EsoptocComponent implements OnInit,AfterViewInit {
  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    preview:string="";
    sopmasters:any=[];
    sopsourcetypes:any=[];
    soptypes:any=[];
    soptopicparents:any=[];

    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }


    ngAfterViewInit() {
      if(!this.masterObj){
        this.masterData={};
        this.masterData.sopTocid='';
        this.masterData.sopId=null;
        this.masterData.sopTopicName="";
        this.masterData.sopTopicNameLocal="";
        this.masterData.sopTypeid=null;
        this.masterData.sopSourceTypeid=null;
        this.masterData.sopTopicParentid=null;
        this.masterData.sopTopicOrder="";
        this.masterData.sopSourceUrl="";
        this.masterData.sopSourceUrlData="";
        this.masterData.isActive=true;
    }
}

  ngOnInit(): void {
    this.clearData();
    this.getAjaxSOPData('sopmasters');
    this.getAjaxSourceTypesData('sopsourcetypes');
    this.getAjaxSOPTypesData('soptypes');
    this.getAjaxSOPTOCParentData('soptopicparents');
  }

getAjaxSOPData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.sopmasters=[];
              rdata.data.forEach((element:any) => {
                  this.sopmasters.push(element);
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
getAjaxSOPTOCParentData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.soptopicparents=[];
              rdata.data.forEach((element:any) => {
                  this.soptopicparents.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })
}

  clearData(){
    this.masterData={
      sopTocid:'',
      sopId: null,
      sopTopicName: '',                
      sopTopicNameLocal: '',
      sopTypeid: null,
      sopSourceTypeid: null,
      sopTopicParentid: null,
      sopTopicOrder: '',
      sopSourceUrl:'',
      sopSourceUrlData:'',
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
          this.getEsopTocById(event.soptocid.toString());
      }
  }
  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "" ) {
        return true;
    }
    return false;;
    }



updateEsopToc(): void {

if (this.IsNullorEmpty(this.masterData.sopId)) {
  this.toastr.info("Please select SOP Name");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopTopicName)) {
  this.toastr.info("Please enter Topic Name");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopTopicNameLocal)) {
  this.toastr.info("Please enter Topic Name Local");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopTypeid)) {
  this.toastr.info("Please select SOP Type");
    return;
}
if (this.IsNullorEmpty(this.masterData.sopSourceTypeid)) {
  this.toastr.info("Please select SOP Source Type");
    return;
}
if(this.masterData.sopTopicParentid){
  if (this.IsNullorEmpty(this.masterData.sopTopicOrder)) {
    this.toastr.info("Please enter Topic Order");
      return;
  }
}
if(this.masterData.sopSourceTypeid && this.masterData.sopSourceTypeid !=4){
  if (this.IsNullorEmpty(this.masterData.sopSourceUrl)) {
    this.toastr.info("Please choose file");
      return;
  }
}

if(this.masterData.sopSourceTypeid==4){
  this.masterData.sopSourceUrl="";
  this.masterData.sopSourceUrlData="";
}


  let data:any={};
   data.sopTocid=this.masterData.sopTocid==""?"0":this.masterData.sopTocid;
   data.sopId=this.masterData.sopId;
   data.sopTopicName=this.masterData.sopTopicName;
   data.sopTopicNameLocal=this.masterData.sopTopicNameLocal;
   data.sopTypeid=this.masterData.sopTypeid;
   data.sopSourceTypeid=this.masterData.sopSourceTypeid;
   data.sopTopicParentid=this.masterData.sopTopicParentid==null?0:this.masterData.sopTopicParentid;
   data.sopTopicOrder=this.masterData.sopTopicOrder;
   data.sopSourceUrl=this.masterData.sopSourceUrl;
   data.sopSourceUrlData=this.masterData.sopSourceUrlData;
   data.active=this.masterData.isActive;
   data.userId=this.gVar.userId;


   this.service.updateEsopToc(data).subscribe({
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

getEsopTocById(typeid: any): void {

  this.service.getEsopTocById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {  
          this.masterData={};
          this.masterData.sopTocid=rdata.result.sopTocid;        
          this.masterData.sopId=rdata.result.sopId+'';
          this.masterData.sopTopicName=rdata.result.sopTopicName;
          this.masterData.sopTopicNameLocal=rdata.result.sopTopicNameLocal;
          this.masterData.sopTypeid=rdata.result.sopTypeid+'';
          this.masterData.sopSourceTypeid=rdata.result.sopSourceTypeid+'';
          this.masterData.sopTopicParentid=rdata.result.sopTopicParentid==0?null:rdata.result.sopTopicParentid+'';
          this.masterData.sopTopicOrder=rdata.result.sopTopicOrder;
          this.masterData.isActive=rdata.result.active;
          this.masterData.sopSourceUrl=rdata.result.sopSourceUrl;
          this.masterData.sopSourceUrlData=rdata.result.sopSourceUrlData;
          this.preview='data:image/png;base64, '+rdata.result.sopSourceUrlData;
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    // errorHandler 
  });
}

fileChangeEvent(fileInput: any) {
  let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';

  if(this.masterData.sopSourceTypeid==1){
    allowedFileTypes='mp3,mp4,MP3,MP4';
  }else if(this.masterData.sopSourceTypeid==2){
    allowedFileTypes='pdf,PDF';
  }else if(this.masterData.sopSourceTypeid==3){
    allowedFileTypes='ppt,pptx,PPT,PPTX';
  }

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
         this.masterData.sopSourceUrl=fileInput.target.files[0].name;
         this.masterData.sopSourceUrlData=imgBase64Path;
   };
   reader.readAsDataURL(fileInput.target.files[0]);
}
}
    
}