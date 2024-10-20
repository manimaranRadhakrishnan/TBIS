import { Injectable } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-toastr',
  templateUrl: './toastr.component.html',
  styleUrls: ['./toastr.component.css']
})
@Injectable({ providedIn: 'root' })
export class ToastrComponent implements OnInit {

  // constructor() { }
  name = 'Angular Toastr';
  constructor(private toastr: ToastrService) { }

  showSuccess() {
    this.toastr.success('Hello world!', 'Toastr fun!', {
      timeOut: 200000
    });
  }
  showError() {
    this.toastr.error('everything is broken', 'Major Error', {
      timeOut: 3000
    });
  }
  showWarning() {
    this.toastr.warning('everything is broken', 'Major Error', {
      timeOut: 3000
    });
  }
  showInfo() {
    this.toastr.info('everything is broken', 'Major Error', {
      timeOut: 3000
    });
  }
  error(rdata:any){
    if(rdata.ErrorMessage && rdata.ErrorMessage!=""){
      this.toastr.error(rdata.ErrorMessage);
      return;
    }else if(rdata.ValidationMessages && rdata.ValidationMessages.length>0){
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
  ngOnInit(): void {
  }

}
