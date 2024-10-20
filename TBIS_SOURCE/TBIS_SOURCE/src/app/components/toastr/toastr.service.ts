import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Injectable() export class AdToastrService {
    private subject = new Subject<any>();
    constructor(private toastr: ToastrService) { }

    showMessage(rdata:any){
        if(rdata.ValidationMessages.length>0){
          if(rdata.ValidationMessages instanceof Array){
            this.toastr.error(rdata.ValidationMessages[0])
          }else{
            this.toastr.error(rdata.ValidationMessages);
          }
          return;
        }
        if(rdata.Warning){
          this.toastr.warning(rdata.Warning);
        }
    }    
}
