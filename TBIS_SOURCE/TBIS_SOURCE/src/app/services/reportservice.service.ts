import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppGlobal } from './appglobal.service';
import { AppConfig } from './appconfig.service';

 
@Injectable({
  providedIn: 'root'
})
export class ReportService  {
    XLsExportReportData(inputData: any) {
      throw new Error('Method not implemented.');
    }
    
    constructor(private appConfig: AppConfig, private gVar: AppGlobal, private http: HttpClient) { 
        this.AUTH_API=this.appConfig.apiBaseUrl+this.appConfig.apiPath; 
    }
    result = new Observable<any>();
    AUTH_API:string="";
    
    getHeader(){
       // console.log("Access Token "+this.gVar.accesstoken)
        const httpHeaer = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'X-Frame-Options':"DENY",
                'Authorization':"Bearer "+this.gVar.accesstoken
            })
        };
        return httpHeaer;
    }
    getReportMeta(reportid:string,roleid:string): Observable<any> {
        const httpKeyOption1 = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'X-AppName': 'TBISApp',
                'accept':'*/*',
                'X-Frame-Options':"DENY",
                'Authorization':"Bearer "+this.gVar.accesstoken
                  })
        };
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'report/metadata?id='+reportid, httpKeyOption1);
    }
    exportReport(reportid:string,roleid:string,type:string,params:string): Observable<any> {
        if(type=='csv'){
        const httpKeyOption1 = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'X-AppName': 'TBISApp',
                'accept':'*/*',
                'X-Frame-Options':"DENY",
                'Authorization':"Bearer "+this.gVar.accesstoken
                  }),
            responseType:'arraybuffer' as any
        };
        
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'report/writexls?'+params, httpKeyOption1);
    }else {
        const httpKeyOption1 = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'X-AppName': 'TBISApp',
                'accept':'*/*',
                'X-Frame-Options':"DENY",
                'Authorization':"Bearer "+this.gVar.accesstoken
                  }),
            responseType:'arraybuffer' as any
        };
        
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'report/writepdf?id='+reportid, httpKeyOption1);
    }
    }
   
    getReportData(reportid:string): Observable<any> {
        // 
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'report/data?'+reportid, this.getHeader());
    }
   
    getReportColumns(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'reports/reportColumns',obj, this.getHeader());
    }

    getReportAttachment(obj:any): Observable<Blob> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Accept': 'application/json'});
        return this.http.post<Blob>(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'reports/getImage', obj, {headers: headers, responseType: 'blob' as 'json' });
    }
    getReportWithPDFAttachment(obj:any): Observable<any> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Accept': 'application/json'});
        return this.http.post<any>(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'reports/getPdf', obj, {headers: headers });
    }
    getReportAttachment_DOC(obj:any): Observable<any> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Accept': 'application/json'});
        return this.http.post<any>(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'reports/getDocument', obj, {headers: headers });
    }
    getDataSourceList(sourceId:string): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'reports/reportSourceData/'+sourceId);
    }

    fileuploadservice(obj:any): Observable<any> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'      
          });
        return this.http.post<any>(this.appConfig.apiBaseUrl+this.appConfig.apiPath + '/mdm/common/uploadFiles', obj, {headers: headers });
    }
    getPickListBarcode(stockid:string): Observable<any> {
        const httpKeyOption1 = {
          headers: new HttpHeaders({
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*',
              'X-AppName': 'TBISApp',
              'accept':'*/*',
              'X-Frame-Options':"DENY",
              'Authorization':"Bearer "+this.gVar.accesstoken
                }),
          responseType:'arraybuffer' as any
      }
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/generatestockqrcode?id='+stockid, httpKeyOption1);
      }  
}
