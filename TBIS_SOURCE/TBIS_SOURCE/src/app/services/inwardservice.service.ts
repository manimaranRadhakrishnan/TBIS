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


export class InwardService {
    AUTH_API:string="";
    httpKeyOption:any={};
    constructor(private appConfig: AppConfig, private gVar: AppGlobal, private http: HttpClient) {
      this.AUTH_API=this.appConfig.apiBaseUrl+this.appConfig.apiPath; 
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
     }
     //Pick List Master
    getPickListMasterById(pickListId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklistmaster?code='+pickListId, this.httpKeyOption);
    }
    updatePickListMaster(obj:any): Observable<any> {

      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklistmaster', obj, this.httpKeyOption);
    }
    //Pick List Detail
    getPickListById(typeid:string): Observable<any> {

      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'pickListdetail?code='+typeid, this.httpKeyOption);
    }
    updatePickListDetail(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'pickListdetail', obj, this.httpKeyOption);
    }
    
    updateASNMaster(obj:any): Observable<any> { 
        
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster', obj, this.httpKeyOption);
    }
  
}