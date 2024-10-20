import { Component,Input,OnInit,AfterViewInit, OnDestroy  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Editor } from 'ngx-editor';

@Component({
  selector: 'app-mailtemplate',
  standalone: false,
  templateUrl: './mailtemplate.component.html',
  styleUrl: './mailtemplate.component.css'
})
export class MailtemplateComponent implements OnInit,AfterViewInit,OnDestroy {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    form: any = {};
    action:string="view";
    editor!: Editor;
    textareaConfig = { 'editable': true, 'spellcheck': true, 'height': 'auto', 'minHeight': '100px', };
     
    eventsSubject: Subject<void> = new Subject<void>();
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }



    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.mailTemplateId="";
        this.masterData.mailTemplatename="";
        this.masterData.mailSubject="";
        this.masterData.mailContent="";
        this.masterData.attachData=0;
        this.masterData.isActive=true;
  
    }
  }


  ngOnInit(): void { 
    this.editor = new Editor();
  }

  ngOnDestroy(): void {
    this.editor.destroy();
  }
  clearData(){
    this.masterData={
      mailTemplateId:'',
      mailTemplatename: '',                
      mailSubject: '',
      mailContent: ''
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
        this.getMailTemplateById(event.mailtemplateid.toString());
    }
  }
 

updateMailTemplate(): void {

if (this.IsNullorEmpty(this.masterData.mailTemplatename)) {
    this.toastr.info("Please enter mail template name");
    return;
}
if (this.IsNullorEmpty(this.masterData.mailSubject)) {
    this.toastr.info("Please enter mail subject");
    return;
}
if (this.IsNullorEmpty(this.masterData.mailContent)) {
    this.toastr.info("Please enter mail content");
    return;
}

  let data:any={};
   data.mailTemplateId=this.masterData.mailTemplateId==""?"0":this.masterData.mailTemplateId;
   data.mailTemplatename=this.masterData.mailTemplatename;
   data.mailSubject=this.masterData.mailSubject;
   data.mailContent=this.masterData.mailContent;
   data.attachData=this.masterData.attachData;
   data.isActive=this.masterData.isActive;
   this.service.updateMailTemplate(data).subscribe({
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
getMailTemplateById(typeid: any): void {

  this.service.getEmailTemplateById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {
          this.masterData={};         
          this.masterData.mailTemplateId=rdata.result.mailTemplateId;
          this.masterData.mailTemplatename=rdata.result.mailTemplatename;
          this.masterData.mailSubject=rdata.result.mailSubject;
          this.masterData.mailContent=rdata.result.mailContent;
          this.masterData.attachData=rdata.result.attachData;
          this.masterData.isActive=rdata.result.isActive;
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
