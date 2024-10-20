import { Component,  ElementRef,  OnDestroy,  OnInit, ViewChild } from '@angular/core';
import { DocumentDialogService } from './document-dialog.service';
import { pdfDefaultOptions } from 'ngx-extended-pdf-viewer';
import { ReportService } from '../../services/reportservice.service'
import { ConfirmDialogService } from '../confirm-dialog/confirm-dialog.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
    selector: 'app-document-dialog',
    templateUrl: 'document-dialog.component.html',
    styleUrls: ['document-dialog.component.scss']
})


export class DocumentDialogComponent implements OnInit,OnDestroy {
    message: any;
    filetype:string="";
    imagedatatypes=['.jpeg','.jpg','.png','.bmp','jpeg','jpg','png','bmp'];
    base64Src:any;
    @ViewChild("myCanvas", { static: false })
  canvas!: ElementRef;
    constructor(private docService: DocumentDialogService,
        private reportService:ReportService,
        private confirmDialogService:ConfirmDialogService,
        private sanitizer:DomSanitizer
        ) {pdfDefaultOptions.assetsFolder = 'bleeding-edge'; }
 
    viewer = 'google';  
    docContent:any; 
    public src!: Blob;
    imagesrc:any;
    objectUrl:any;
    imagedatatype:string="";
    ngOnDestroy(): void {
      this.imagesrc;
    }
    ngOnInit(): any {
        this.docService.getMessage().subscribe(message => {
            this.message = message;
            this.filetype="";
            if(this.message){
                if(this.docService.getQueryParamValue().ImageType){
                  this.imagedatatype=this.docService.getQueryParamValue().ImageType;
                  if(this.imagedatatype.indexOf(".")==-1){
                    this.imagedatatype="."+this.imagedatatype;
                  }
                  if(this.imagedatatypes.indexOf(this.docService.getQueryParamValue().ImageType.toLowerCase())!=-1){
                    this.filetype="image";
                  }else if(this.docService.getQueryParamValue().ImageType.toLowerCase()==".pdf"){
                    this.filetype="pdf";
                  }else if(this.docService.getQueryParamValue().ImageType.toLowerCase()==".base64"){
                    this.filetype="base64";
                  }else{
                    this.filetype="doc";
                  }
                  this.getAttachment();
                }else{
                  this.message.okFn();
                  this.confirmDialogService.showMessage("Image type not available", () => { });
                }
            }
        });
    }
    
        blobToBase64(blob: Blob) {
        return new Promise((resolve, _) => {
          const reader = new FileReader();
          reader.onloadend = () => resolve(reader.result);
          reader.readAsDataURL(blob);
        });
      }
     
    getAttachment(){
        let paramName=this.docService.getQueryParamName().split(',');
        let paramValue=this.docService.getQueryParamValue();
        let inputData:any={};
        inputData.ReportId=this.docService.getReportID();
        inputData.RoleId=1;
        inputData.page=1;
        inputData.rows=-1;
        inputData.sortField="";
        inputData.sortOrder="";
        inputData.searchText="";
        inputData.imageType=this.imagedatatype.replace('.','').toLocaleLowerCase();
        inputData.QueryID=this.docService.getQueryID();
        inputData.reportParams=[];
         
         
          paramName.forEach((element: string | number) => {
            let obj:any={};
            obj.ParamName=element;
            obj.ParamValue=paramValue[element]+"";
            inputData.reportParams.push(obj);
          });
          
          
          let blob = null; // <= your blob object goes here
          
          
          if(this.filetype=="image" || this.filetype=="pdf"){
            this.reportService.getReportAttachment(inputData).subscribe(
              rdata => {
                  if (rdata) {
                    let url = window.URL.createObjectURL(rdata);
                    if(this.filetype=="image"){
                      this.blobToBase64(rdata).then(base64String => this.imagesrc=base64String );
                     
                    }else if(this.filetype=="base64"){
                      this.blobToBase64(rdata).then(base64String => this.imagesrc=base64String );
                    }else{
                      this.src=rdata;
                    }
                  }
              },
              err => {
                  this.confirmDialogService.showMessage(err.message, () => { });
              }
            );
          }else if(this.filetype=="doc"  || this.filetype=="base64"){
            this.reportService.getReportAttachment_DOC(inputData).subscribe(
              rdata => {
                  if (rdata) {
                    
                      if( this.filetype=="base64"){
                        this.imagesrc="data:image/jpeg;base64,"+rdata.base64String;
                      }else{
                        this.base64Src=rdata.base64String;
                      }
                  }
              },
              err => {
                  this.confirmDialogService.showMessage(err.message, () => { });
              }
            );
          }
      }
      _base64ToArrayBuffer(base64: string) {
        let binary_string = base64.replace(/\\n/g, '');
        binary_string =  window.atob(base64);
        let len = binary_string.length;
        let bytes = new Uint8Array( len );
        for (let i = 0; i < len; i++)        {
            bytes[i] = binary_string.charCodeAt(i);
        }
        return bytes.buffer;
    }
    CloseBtn(){
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl;
      this.message.okFn();
      this.message=false;
    }
}
