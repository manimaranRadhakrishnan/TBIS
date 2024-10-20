import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.uat';
import { AppGlobal } from '../services/appglobal.service';
import { AppConfig } from '../services/appconfig.service';
 
const API_KEY = environment.apiaccessKey;
@Injectable({
  providedIn: 'root'
})
export class UserService {
  AUTH_API:string="";
  httpKeyOption:any={};
    constructor(private appConfig: AppConfig, private gVar: AppGlobal, private http: HttpClient) {
      this.AUTH_API=this.appConfig.apiBaseUrl+this.appConfig.apiPath; 
     }
     getHeader(){
      this.httpKeyOption = {
           headers: new HttpHeaders({
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*',
              'X-AppName': 'TBISApp',
              'accept':'*/*',
              'accessKey': API_KEY,
              'X-Frame-Options':"DENY",
              'Authorization':"Bearer "+this.gVar.accesstoken
           })
       };
       return this.httpKeyOption;
   }
    
    getUserMenus(): Observable<any> {
          return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'ajax?ic=0&id=usermenu&ro=true', this.getHeader());
    }
    getRoleList(): Observable<any> {
            return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'mdm/masters/allRoles?roleId=1', this.getHeader());
    } 
    getUserList(): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'mdm/masters/allUsers?userId=-1', this.getHeader());
    }
    updateUser(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'mdm/masters/manageUser', obj, this.getHeader());
    }
    updateRole(data:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'mdm/masters/manageRole', data, this.getHeader());
    }
    getRoleMenus(roleid:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'mdm/masters/roleMenus?roleId='+roleid, this.getHeader());
    }
    // User Profile Service
    updateProfile(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/updateprofile', obj, this.httpKeyOption);
    }
    getEmployeeMasterById(empId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/employee?code='+empId, this.httpKeyOption);
    }
    updateEmpMaster(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/employee', obj, this.httpKeyOption);
    }
    addEmpLocation(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/userlocation', obj, this.httpKeyOption);
    }    
    removeEmpLocation(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/removeuserlocation', obj, this.httpKeyOption);
    }
    getAjaxDropDown(ajaxId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'ajax?ro=true&ic=0&id='+ajaxId, this.httpKeyOption);
    }
}
