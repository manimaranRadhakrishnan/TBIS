import { Component, OnInit,  ViewChild } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';
import { timer } from 'rxjs';
import { Router } from '@angular/router';
import { AppGlobal } from '../services/appglobal.service';
import { ConfirmDialogService } from '../components/confirm-dialog/confirm-dialog.service';
import { CountdownComponent } from 'ngx-countdown';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit  {
    status = 'start';
    @ViewChild('countdown')
    counter!: CountdownComponent;
    

    form: any = {};
    isLoggedIn = false;
    isLoginFailed = false;
    errorMessage = '';
    isOTPGenerated = false;
    submitted = false;
    smstimer= "";
    retrytimer = "";
    source = timer(0);
    loginLocations:any=[];
     public showResendBtn: boolean = false;
     

    constructor(
        private confirmDialogService: ConfirmDialogService,
        private gvTbis: AppGlobal, 
        private router: Router, 
        private authService: AuthService, 
        private tokenStorage: TokenStorageService,
        private toastr:ToastrService,
        ) { }
    
   
    ngOnInit(): void {
         
        if (this.tokenStorage.getToken()) {
            this.isLoggedIn = true;
            this.router.navigate(['home']);

        } else {
            this.isLoggedIn = false;
            const box = document.getElementById(
                'body',
              ) as HTMLDivElement | null;
            box?.classList.remove("body");
        }
    }

    
    get f() { return this.form.controls; }
    IsNullorEmpty(value:any): boolean {
        if (value == undefined || value == "") {
            return true;
        }
        return false;;
    }
    onSubmit(): void {

        this.tokenStorage.setActive("true");
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        
        this.gvTbis.userType= "USER";
        this.gvTbis.accesstoken= "token";
        this.tokenStorage.saveToken("token");
            
         if (this.IsNullorEmpty(this.form.username)) {
            this.toastr.info("Please enter user name");
             return;
         }
         if (this.IsNullorEmpty(this.form.password)) {
            this.toastr.info("Please enter password");
             return;
         }
         this.authService.submit(this.form).subscribe(
             rdata => {
                 if (rdata.isSuccess) {
                         this.tokenStorage.setActive("true");
                         this.isLoginFailed = false;
                         this.isLoggedIn = true;
                         this.gvTbis.userId= rdata.result.userId;
                         this.gvTbis.roleId= rdata.result.roleId;
                         this.gvTbis.displayName= rdata.result.name;
                         this.gvTbis.active= "1";
                         this.gvTbis.userEmail= rdata.result.emailId;
                         this.gvTbis.userMobile= rdata.result.mobileNo;
                         this.gvTbis.userType= "USER";
                         this.gvTbis.accesstoken= rdata.result.token;
                         this.gvTbis.profileImage=rdata.result.profileImage;
                         this.tokenStorage.saveToken(rdata.result.token);
                         this.tokenStorage.saveUser(rdata);
                         this.getLoginLocations();
                  } else {
                    this.toastr.info("Incorrect username or password");
                 }

             },
             err => {
                this.toastr.error(err.msg);
             }
         );
    }
    
  reloadPage(): void {
    window.location.reload();
  }
  getLoginLocations():void{
    this.loginLocations=[];
    let contactsparam='loginlocations';
    this.authService.getAjaxDropDown(contactsparam).subscribe({
        next: (rdata) => {
            // this.gvTbis.aLocation=[];
            if (rdata !=null && rdata.data) {
                this.loginLocations=rdata.data;
                let userLocations:any=[];
                if (this.loginLocations!=null && this.loginLocations.length>0){
                    for (let i=0;i<this.loginLocations.length;i++) {
                            let loc=this.loginLocations[i];
                            // Warehouse
                            if(userLocations.find((x:any)=>x.locationid==loc.warehousid)==undefined){
                                    let location={
                                        locationid:loc.warehousid,
                                        locationname:loc.warehousename,
                                        sublocations:[]
                                    };
                                    userLocations.push(location);
                            }
                            // SubLocation
                            if(userLocations.find((x:any)=>x.locationid==loc.warehousid).sublocations.find((y:any)=>y.sublocationid==loc.sublocationid)==undefined){
                                let sublocation={
                                    sublocationid:loc.sublocationid,
                                    sublocationname:loc.sublocationname,
                                    docks:[]
                                };
                                userLocations.find((x:any)=>x.locationid==loc.warehousid).sublocations.push(sublocation);
                            }
                            // Dock
                            if(userLocations.find((x:any)=>x.locationid==loc.warehousid).sublocations.find((y:any)=>y.sublocationid==loc.sublocationid).docks.find((y:any)=>y.udc_id==loc.udc_id)==undefined){
                                let dock={
                                    udc_id:loc.udc_id,
                                    udcname:loc.udcname
                                };
                                userLocations.find((x:any)=>x.locationid==loc.warehousid).sublocations.find((y:any)=>y.sublocationid==loc.sublocationid).docks.push(dock);
                            }
                    }

                }
                this.tokenStorage.saveUserLocation(userLocations);
                this.router.navigate(["home"]);
                this.reloadPage();
                window.location.href='home';
            }
         },
        error: (e) => console.error(e),
    })
  }
     
    numberOnly(event:any): boolean {
        const charCode = (event.which) ? event.which : event.keyCode;
        if (charCode > 31 && (charCode < 48 || charCode > 57)) {
            return false;
        }
        return true;

    }
}
