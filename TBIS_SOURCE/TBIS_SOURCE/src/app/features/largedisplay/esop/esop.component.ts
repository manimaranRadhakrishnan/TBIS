import { AfterViewInit, Component,Input, OnInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-esop',
  standalone: false,
  templateUrl: './esop.component.html',
  styleUrl: './esop.component.css'
})
export class EsopComponent implements OnInit,AfterViewInit {
  action:string="view";
  detailData:any={};

  ngAfterViewInit(): void {
  }
  ngOnInit(): void {
  }
}
