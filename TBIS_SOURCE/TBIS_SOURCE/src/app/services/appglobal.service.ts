import { Injectable } from '@angular/core';
@Injectable({
    providedIn: 'root'
})
export class AppGlobal {
    deviceId!: string;
    deviceType!: string;
    deviceOs!: string;
    userId!:string;
    roleId!:string;
    displayName!: string;
    active!: string;
    userEmail:string="";
    userMobile:string="";
    userType!: string;
    accesstoken!:string;
    profileImage!:string;
    aLocation:any=[];
    aSubLocation:any=[];
    aDocks:any=[];
    defaultDockId:string="1";
    defaultLocationId:string="1";
    defaultSubLocationId:string="1";
}
