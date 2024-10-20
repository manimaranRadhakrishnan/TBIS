import { Component, HostListener,  OnInit } from '@angular/core';
import { HashLocationStrategy, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenStorageService } from './services/token-storage.service';
import { AppConfig } from './services/appconfig.service';
import { AppGlobal } from './services/appglobal.service';
import { DeviceDetectorService } from 'ngx-device-detector';
import { v4 as uuidv4 } from 'uuid';
import { AppIdleService } from './services/app-idle.service';
import { AuthService } from './services/auth.service';
import { LoaderService } from './services/loader.service';
import { LoginService } from './services/login.service';
import { UserService } from './services/user.service';
import { BehaviorSubject } from 'rxjs';
import { NavItem,SideNavToggle} from './models/ui-modules';
import { SlimScrollOptions } from 'ngx-slimscroll';




@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
    providers: [
        AppIdleService,
        PathLocationStrategy,
        {provide: LocationStrategy, useClass: HashLocationStrategy},
    ],
     
})
export class AppComponent implements OnInit  {

    menu!: [];
    menus!:[];  
    isOnline: boolean;
    isLogIn:boolean = false;
    showAdminBoard:boolean = false;
    username: string | undefined;
    usershortName: string | undefined;
    rolename: string | undefined;
    deviceType: string | undefined;
    deviceDetails: string | undefined;
    idleTimerLeft!: string;
    timeRemain: number | undefined;
    currentMenuCaption:string=" ";
    isLoggedIn: BehaviorSubject<boolean> = this.login.isLoggedIn;
    isLoading: BehaviorSubject<boolean> = this.loader.isLoading;
    ctitle = 'angular-sidebar-menu';
    currentUrl="";
    currentRole = "ADMIN";
     currentSearch?: string;
    inputSearchFocus = false;
    mainNavigationOpened = true;
    navItems: NavItem[] = [];
    opts!: SlimScrollOptions;
    isSideNavCollapsed = false;
    screenWidth:number = 0;
    loginLocations:any=[];
    
    @HostListener('window: unload', ['$event']) unloadHandler(event: any) { 
      this.logout();
    } ;

    @HostListener('window: load', ['$event']) loadHandler(event: any) { 
      localStorage.setItem("load",document.visibilityState + " --"+new Date().getSeconds()+"--"+this.router.routerState);
    } ;
 
      
    constructor(
         private route: ActivatedRoute,
         private login:LoginService,
         private loader: LoaderService,
         private authService: AuthService, 
         private appIdle: AppIdleService, 
         private wmscv: AppGlobal, 
         private router: Router, 
         private tokenStorageService: TokenStorageService, 
         private deviceService: DeviceDetectorService, 
         private userService:UserService  ,
         private pathLocationStrategy: PathLocationStrategy,
         private appConfig:AppConfig
         ) { 
            const basePath =  pathLocationStrategy.getBaseHref();
            const absolutePathWithParams = pathLocationStrategy.path();
            this.currentUrl=absolutePathWithParams;
            const hashPath = window.location.hash;
            this.isOnline = false;
          }
  
    
    ngOnInit(): void {

        this.updateOnlineStatus();
        window.addEventListener('online',  this.updateOnlineStatus.bind(this));
        window.addEventListener('offline', this.updateOnlineStatus.bind(this));
        
        this.opts = new SlimScrollOptions({
            position:'right',
            barWidth: "8",
            barBorderRadius:"8",
            alwaysVisible:true,
            visibleTimeout: 1000,
            alwaysPreventDefaultScroll: true,
           
          });
        this.username = "";
        this.screenWidth=window.innerWidth;
        this.wmscv.deviceType = this.deviceService.getDeviceInfo().browser;
        this.wmscv.deviceOs = this.deviceService.getDeviceInfo().os;
        this.isLogIn = !!this.tokenStorageService.getToken();
        
        // initTimer(this.appConfig.getIdleTime, 1);
         if (this.isLogIn) {
             this.login.login();
             const user = this.tokenStorageService.getUser();
             const userLocations=this.tokenStorageService.getUserLocation();
             this.wmscv.deviceId = this.tokenStorageService.getDeviceID();
             if (!this.wmscv.deviceId) {
                 this.wmscv.deviceId = uuidv4();
                 this.tokenStorageService.saveDeviceID(this.wmscv.deviceId);
             }
             this.wmscv.userId= user.result.userId;
             this.wmscv.roleId= user.result.roleId;
             this.wmscv.displayName= user.result.userName;
             this.wmscv.active= user.result.isActive;
             this.wmscv.userEmail= user.result.userEmail;
             this.wmscv.userMobile= user.result.userMobile;
             this.wmscv.userType= "USER";
             this.wmscv.accesstoken= "token";
             this.username = this.wmscv.displayName;
             this.rolename = user.result.roleName;
             this.usershortName=this.wmscv.displayName.substring(0,1);
             this.wmscv.accesstoken=user.result.token;
             this.wmscv.profileImage=user.result.profileImage;
             this.wmscv.aLocation=userLocations;
         } else {
             this.login.logout();
             this.wmscv.deviceId = uuidv4();
             this.tokenStorageService.saveDeviceID(this.wmscv.deviceId);
         }
        
   
      window.addEventListener('storage', (event) => {
          if (event.storageArea == localStorage) {
              if (this.isLogIn) {
                  let token = localStorage.getItem('auth-token');
                  if (token == undefined) {
                      //alert("Ooops.... Session expired.");
                      this.isLogIn = false;
                      this.login.logout();
                        this.tokenStorageService.signOut();
                        AppIdleService.runTimer = false;
                        this.router.navigate(['/login']);
                  }
              }
                  
              }
          });
    }
    
    logout():void{
        if(this.isLogIn){
            this.authService.logout().subscribe({
                error: (err) => { },    // errorHandler 
                next: (data) => { },     // nextHandler
            });
            this.isLogIn=false;
            this.login.logout();
            this.tokenStorageService.signOut();
            AppIdleService.runTimer=false;
            this.router.navigate(['/login']);
        }

    }


    onToggleSideNav(data: SideNavToggle): void {
        this.screenWidth = data.screenWidth;
        this.isSideNavCollapsed = data.collapsed;
    }

    private updateOnlineStatus(): void {
        this.isOnline = window.navigator.onLine;
        console.info(`isOnline=[${this.isOnline}]`);
    }
    
}






