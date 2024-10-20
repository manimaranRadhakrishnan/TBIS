
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppGlobal } from './appglobal.service';
import { AppConfig } from './appconfig.service';

 

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*','X-Frame-Options':"DENY" })
};
 
@Injectable({
  providedIn: 'root'
})
export class AuthService {
    encryptdecryptMobile: string="";
    encryptdecryptCode: string="";
    AUTH_API:string="";
    constructor(private gVar: AppGlobal, private appConfig:AppConfig, private http: HttpClient) { }
    result = new Observable<any>();
    
    submit(user:any): Observable<any> {
        this.AUTH_API = this.appConfig.apiBaseUrl  + this.appConfig.apiPath + 'users/';
        const httpOtpSubmit = {
            headers: new HttpHeaders({
                'Content-Type': 'application/x-www-form-urlencoded',
                'Access-Control-Allow-Origin': '*',
                'device_os': this.gVar.deviceOs,
                'X-Frame-Options':"DENY"
            })
        };
        let body = new URLSearchParams();
        body.set('username', user.username);
        body.set('password', user.password);
        return this.http.post(this.AUTH_API + 'login',body.toString(), httpOtpSubmit);
    }
    logout(): Observable<any> {
        this.AUTH_API = this.appConfig.apiBaseUrl  + this.appConfig.apiPath + 'users/';
        const logoutOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*',
                'X-Access-Token': this.gVar.accesstoken,
                'X-Frame-Options':"DENY"
            })
        };
        return this.http.get(this.AUTH_API + 'logout', logoutOptions);
    }

    getAjaxDropDown(ajaxId:string): Observable<any> {
        let httpKeyOption = { 
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'X-AppName': 'TBISApp',
                'accept':'*/*',
                'X-Frame-Options':"DENY",
                'Authorization':"Bearer "+this.gVar.accesstoken
                  })
          };
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'ajax?ro=true&ic=0&id='+ajaxId, httpKeyOption);
    }

    
}
