import { AfterViewInit, Component,Input, OnInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-dock',
  standalone: false,
  templateUrl: './dock.component.html',
  styleUrl: './dock.component.css'
})
export class DockComponent implements OnInit,AfterViewInit {
  action:string="view";
  detailData:any={};
  currentDate = new Date();
  displaydockData:any=[];
  displaydockcountData:any=[];

  constructor(
    private gVar: AppGlobal,
    private toastr:ToastrService,
    private service:MastersService,
    ) { }


  ngAfterViewInit(): void {
  }
  ngOnInit(): void {
    this.getAjaxDockData('displaydock');
    this.getAjaxDockCountData('displaydockcount');
  }


  getAjaxDockData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.displaydockData=rdata.data[0];
            }
            },
        error: (e) => console.error(e),
    })
    }; 

    getAjaxDockCountData(ajaxId:string):void{
      this.service.getAjaxDropDown(ajaxId).subscribe({
          next: (rdata) => {
              if (rdata.data) {
                  this.displaydockcountData=rdata.data[0];
              }
              },
          error: (e) => console.error(e),
      })
    };
}
