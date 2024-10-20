import { Injectable } from '@angular/core';
import { AppGlobal } from './appglobal.service';
import { LoginService } from './login.service';

const TOKEN_KEY = 'auth-token';
const OTPTOKEN_KEY = 'auth-o-token';
const USER_KEY = 'auth-user';
const USER_DEVICE_ID = 'auth-deviceid';
const USER_KEYMENUS = 'user-menu';
const IS_ACTIVE = 'user-isactive';
const USER_REF_ID="ref_id";
const PROFILE_IMAGE="profile-image";
const USER_LOCATION="user-location"

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  static getMenu: any;

    constructor(private gVar: AppGlobal,private login:LoginService) { }

    signOut(): void {
        window.localStorage.clear();
        this.gVar.deviceId="";
        this.gVar.deviceType="";
        this.gVar.deviceOs="";
        this.gVar.userId="";
        this.gVar.roleId="";
        this.gVar.displayName="";
        this.gVar.active="";
        this.gVar.userEmail="";
        this.gVar.userMobile="";
        this.gVar.userType="";
        this.gVar.accesstoken="";
        this.gVar.profileImage="";
        this.gVar.aLocation=[];
        this.login.logout();

  }
    public saveRefId(token: string): void {
        window.localStorage.removeItem(USER_REF_ID);
        window.localStorage.setItem(USER_REF_ID, token);
    }

    public getRefId(): string {
        const serializableState: string | any = localStorage.getItem(USER_REF_ID);
        return serializableState !== null || serializableState === undefined ? serializableState : undefined;
    }
    public setActive(token: string): void {
        window.localStorage.removeItem(IS_ACTIVE);
        window.localStorage.setItem(IS_ACTIVE, token);
    }

    public getActive(): string {
        const serializableState: string | any = localStorage.getItem(IS_ACTIVE);
        return serializableState !== null || serializableState === undefined ? serializableState : undefined;
    }
  public saveToken(token: string): void {
      window.localStorage.removeItem(TOKEN_KEY);
      window.localStorage.setItem(TOKEN_KEY, token);
  }

    public getToken(): string {
        const serializableState: string | any = localStorage.getItem(TOKEN_KEY);
        return serializableState !== null || serializableState === undefined ? serializableState : undefined;
  }
    public saveOtpToken(token: string): void {
        window.localStorage.removeItem(OTPTOKEN_KEY);
        window.localStorage.setItem(OTPTOKEN_KEY, token);
    }

    public getOtpToken(): string {
        const serializableState: string | any = localStorage.getItem(OTPTOKEN_KEY);
        return serializableState !== null || serializableState === undefined ? serializableState : undefined;
    }
  public saveUser(user: any): void {
      window.localStorage.removeItem(USER_KEY);
      window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

    public getUser(): any {
        const serializableState: string | any = localStorage.getItem(USER_KEY);
        return serializableState !== null || serializableState === undefined ? JSON.parse(serializableState) : undefined;
    }
    
  public saveUserLocation(userLocations: any): void {
    window.localStorage.removeItem(USER_LOCATION);
    window.localStorage.setItem(USER_LOCATION, JSON.stringify(userLocations));
}

  public getUserLocation(): any {
      const serializableState: string | any = localStorage.getItem(USER_LOCATION);
      return serializableState !== null || serializableState === undefined ? JSON.parse(serializableState) : undefined;
  }
    public saveDeviceID(deviceId:string): void {
        window.localStorage.removeItem(USER_DEVICE_ID);
        window.localStorage.setItem(USER_DEVICE_ID, deviceId);
    }

    public getDeviceID(): any {
        const serializableState: string | any = localStorage.getItem(USER_DEVICE_ID);
        return serializableState !== null || serializableState === undefined ? serializableState : undefined;
    }
    public saveMenu(data:any): void {
        window.localStorage.removeItem(USER_KEYMENUS);
        window.localStorage.setItem(USER_KEYMENUS, JSON.stringify(data));
    }

    public getMenu(): any {
        const serializableState: string | any = localStorage.getItem(USER_KEYMENUS);
        return serializableState !== null || serializableState === undefined ? JSON.parse(serializableState) : undefined;
    }
}
