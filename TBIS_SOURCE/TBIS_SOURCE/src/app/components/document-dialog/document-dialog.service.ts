import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable() export class DocumentDialogService {
    private subject = new Subject<any>();
    private queryParams:any;
    private paramValues:any;
    private reportid:string="";
    private imageType:string="";
    private queryid:string="";
    showMessage(message: string, okFn: () => void): any {
        this.showMessageDialog(message, okFn);
    }
    showMessageDialog(message: string, okFn: () => void): any {
        const that = this;
        this.subject.next({
            type: 'alert',
            text: message,
            okFn(): any {
                that.subject.next(""); // This will close the modal
            },
        });

    }
 
    
    getMessage(): Observable<any> {
        return this.subject.asObservable();
    }
    setQueryParams(reportid:string,paramName:string,paramData:any,queryid:string){
        this.queryParams=paramName;
        this.paramValues=paramData;
        this.reportid=reportid;
        this.queryid=queryid;
    }
    getQueryParamName(){
        return this.queryParams;
    }
    getQueryParamValue(){
        return this.paramValues;
    }
    getReportID(){
        return this.reportid;
    }
    getQueryID(){
        return this.queryid;
    }
}
