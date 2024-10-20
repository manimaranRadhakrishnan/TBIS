import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { UserService } from '../../../services/user.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-profilepage',
  standalone: false,
  templateUrl: './profilepage.component.html',
  styleUrl: './profilepage.component.css'
})
export class ProfilepageComponent implements OnInit,AfterViewInit {

    status = 'start';
        @Input() masterObj: any;
        masterData:any={};
        form: any = {};
        action:string="add";
        preview:string="";
        
    
    eventsSubject: Subject<void> = new Subject<void>();
     constructor(
    private gVar: AppGlobal,
    private service:UserService,
    private toastr:ToastrService) { }

    ngOnInit(): void {
       this.clearData();
    }

    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.userId=this.gVar.userId;
        this.masterData.userName=this.gVar.displayName;
        this.masterData.profileImage=this.gVar.profileImage;
        this.preview='data:image/png;base64, '+this.gVar.profileImage;
        this.masterData.oldPassword="";
        this.masterData.newPassword="";
        this.masterData.confirmPassword="";
        

    }
  }
  clearData(){
    this.masterData={
        oldPassword:"",
        newPassword:"",
        confirmPassword:""
    };
  }
 
  updatePassword(): void {

    if (this.IsNullorEmpty(this.masterData.oldPassword)) {
        this.toastr.info("Please enter Old Password");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.newPassword)) {
        this.toastr.info("Please enter New Password");
        return;
    }
    if (this.IsNullorEmpty(this.masterData.confirmPassword)) {
        this.toastr.info("Please enter confirmPassword");
        return;
    }
    if (this.masterData.confirmPassword !=this.masterData.newPassword) {
        this.toastr.info("New Password and Confirm password not matched");
        return;

    }

      let data:any={};
       data.userId=this.masterData.userId==""?"0":this.masterData.userId;
       data.oldPassword=this.masterData.oldPassword;
       data.password=this.masterData.newPassword;
       data.profileImage=this.masterData.profileImage;
       this.service.updateProfile(data).subscribe({
        next: (rdata) => {
            if (rdata.isSuccess) {
                this.toastr.success(rdata.message);
            } else {
                this.toastr.warning(rdata.message);
            }
        },    
        error: (err) => { this.toastr.error(err.message) },  
        
        }) 
    }


fileChangeEvent(fileInput: any) {
    let allowedFileTypes='png,jpg,jpeg,pdf,PNG,JPG,JPEG,PDF';
    if (fileInput.target!.files && fileInput.target.files[0]) {
        let ext=fileInput.target.files[0].name.split('.').pop();
        // Size Filter Bytes
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
            this.masterData.profileImage=imgBase64Path;
        };
    
        reader.readAsDataURL(fileInput.target.files[0]);
    }
        
}    
get f() { return this.form.controls; }

IsNullorEmpty(value: any): boolean {
if (value == undefined || value == "") {
    return true;
}
return false;;
}


}