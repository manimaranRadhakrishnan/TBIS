import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';


@Injectable({
  providedIn: 'root'
})
export class AppConfig {
  private appConfig: any;
  routingtype:any
    constructor(private http: HttpClient, 
    private activatedRoute: ActivatedRoute    ) { 
     
    }
    loadAppConfig() {
      
      //return this.http.get("assets/config.json");
      return this.http.get('assets/config.json')
      .toPromise()
      .then(data => {
        this.appConfig = data;
      });
     }
    get apiBaseUrl() {

      if (!this.appConfig) {
        throw Error('Config file not loaded!');
      }
        return this.appConfig.apiUrl;
    }
    get apiPath() {

      if (!this.appConfig) {
        throw Error('Config file not loaded!');
      }
  
      return this.appConfig.apiPath;
    }
    get apiKey() {

      if (!this.appConfig) {
        throw Error('Config file not loaded!');
      }
  
      return this.appConfig.apikey;
    }
    get getIdleTime() {
  
      if (!this.appConfig) {
        throw Error('Config file not loaded!');
      }
  
      return this.appConfig.idleTime;
    }
    get getAppName() {
  
      if (!this.appConfig) {
        throw Error('Config file not loaded!');
      }
  
      return this.appConfig.appName;
    }
    get getAppVersion() {
  
      if (!this.appConfig) {
        throw Error('Config file not loaded!');
      }
  
      return this.appConfig.appVersion;
    }

    getParameterByName(name:string, url:string) {
      name = name.replace(/[\[\]]/g, '\\$&');
      let regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
          results = regex.exec(url);
      if (!results) return null;
      if (!results[2]) return '';
      return decodeURIComponent(results[2].replace(/\+/g, ' '));
  }
}
