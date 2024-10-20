import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SideNavToggle } from '../models/ui-modules';
import { AppGlobal } from '../services/appglobal.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-body',
  templateUrl: './body.component.html',
  styleUrls: ['./body.component.scss']
})


export class BodyComponent {

  @Input() collapsed = false;
  @Input() screenWidth = 0;
  @Output() sideBarToggleEvent: EventEmitter<SideNavToggle> = new EventEmitter();
  @Input() showHeaderEvent: EventEmitter<boolean> = new EventEmitter();
  @Input() isLogIn =false;
  @Input() isOnline =false;
  userName:string|undefined;
  userEmail:string|undefined;
  profileImage:string | undefined;
  userLocations:  any|[];
  userSublocations: any|[];
  userDocks: any|[];
  selectedDockId:string="1";
  selectedLocationId:string="4";
  selectedSubLocationId:string="1";
  constructor(
    private WmsGV: AppGlobal,
    private authService: AuthService,  
    ) { }
  
  getBodyClass(): string {
    let styleClass = '';
   
    if(this.collapsed && this.screenWidth > 768) {
      styleClass = 'body-trimmed';
    } else if(this.collapsed && this.screenWidth <= 768 && this.screenWidth > 0) {
      styleClass = 'body-md-screen'
    }
    if (!this.isLogIn){
      styleClass=""
    }else{
      this.userName=this.WmsGV.displayName;
      this.userEmail=this.WmsGV.userEmail;
      this.userLocations= this.WmsGV.aLocation!;
      this.WmsGV.defaultLocationId="1";
      //this.userSublocations=this.WmsGV.aSubLocation;
      //this.userDocks=this.WmsGV.aDocks;
      this.selectedDockId=this.WmsGV.defaultDockId+"";
      this.selectedLocationId="1";
      this.selectedSubLocationId=this.WmsGV.defaultSubLocationId+"";
      this.changeLocation();
      this.changeSubLocation();
      if(this.WmsGV.profileImage!=undefined){
        this.profileImage='data:image/png;base64, '+this.WmsGV.profileImage;
      }
    }
    return styleClass;
  }
  toggleCollapse(){
    this.sideBarToggleEvent.next({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }
  changeLocation():void{
    this.userSublocations=this.userLocations.find((x:any)=>x.locationid==this.selectedLocationId).sublocations;
  }
  changeSubLocation():void{
    this.userDocks=this.userSublocations.find((x:any)=>x.sublocationid==this.selectedSubLocationId).docks;
  }

}