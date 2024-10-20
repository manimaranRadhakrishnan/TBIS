import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AppGlobal } from '../services/appglobal.service';
import { AppConfig } from '../services/appconfig.service';


const API_KEY = environment.apiaccessKey;
const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*','X-Frame-Options':"DENY" })
};




@Injectable({
  providedIn: 'root'
})
export class HomeService {

  AUTH_API:string="";
  httpKeyOption:any={};
  constructor(private appConfig: AppConfig, private gVaraible: AppGlobal, private http: HttpClient) {   
    this.AUTH_API=this.appConfig.apiBaseUrl+this.appConfig.apiPath; 
    this.httpKeyOption = { 
      headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'X-AppName': 'TBISApp',
          'accept':'*/*',
          'accessKey': API_KEY,
          'X-Frame-Options':"DENY",
          'Authorization':"Bearer "+this.gVaraible.accesstoken
            })
    };
   }
  

  // Dashboard Summary
  getDashboardSummary(fromDate:string,toDate:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'dashboard?fromdate='+fromDate+'&todate='+toDate, this.httpKeyOption);
  }

  // Dashboard Summary
  getDashboardDetail(fromDate:string,toDate:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'dashboard/detail?fromdate='+fromDate+'&todate='+toDate, this.httpKeyOption);
  }
  getTbisDashboard(fromDate:string,toDate:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'dashboard/tbisdash?fromdate='+fromDate+'&todate='+toDate, this.httpKeyOption);
  }

}
