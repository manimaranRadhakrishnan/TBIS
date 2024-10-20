import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.uat';
import { AppGlobal } from './appglobal.service';
import { AppConfig } from './appconfig.service';

const API_KEY = environment.apiaccessKey;
@Injectable({
  providedIn: 'root'
})


export class CommonService {
   
    AUTH_API:string="";
    httpKeyOption:any={};
    constructor(private appConfig: AppConfig, private gVaraible: AppGlobal, private http: HttpClient) {
      //console.log(gVaraible.accesstoken);
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


    //Contact
    getEntityContactById(contactId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/contact?code='+contactId, this.httpKeyOption);
    }
    updateEntityContact(obj:any): Observable<any> { 
		
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/contact', obj, this.httpKeyOption);
    }

    //Contracts
    getEntityContractsById(contactId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/contract?code='+contactId, this.httpKeyOption);
    }
    updateEntityContracts(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/contract', obj, this.httpKeyOption);
    }

    //Documents
    getEntityDocumentsById(documentId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/documents?code='+documentId, this.httpKeyOption);
    }
    updateEntityDocuments(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/documents', obj, this.httpKeyOption);
    }

     //Address
     getEntityAddressById(addressId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/address?code='+addressId, this.httpKeyOption);
    }
    updateEntityAddress(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'common/address', obj, this.httpKeyOption);
    }

  
}