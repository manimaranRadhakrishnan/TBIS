import { Component,Input,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { TokenStorageService } from '../../../services/token-storage.service';
import { MastersService } from '../../../services/masters.service';
import { ConfirmDialogService } from '../../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-users',
  standalone: false,
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements AfterViewInit{
  status = 'start';
  @Input() masterObj: any;
  masterData:any={};
  form: any = {};
  action:string="view";

  eventsSubject: Subject<void> = new Subject<void>();
  constructor(private confirmDialogService: ConfirmDialogService,
  private GlobalVariable: AppGlobal,
  private router: Router, 
  private authService: AuthService, 
  private tokenStorage: TokenStorageService,
  private service:MastersService) { }

  ngAfterViewInit() {
    console.log("After Init");
  }

  confirmDialog(message: any, showyesno: any): any {
    if (!showyesno) {
        this.confirmDialogService.showMessage(message, () => { });
    } else {
        this.confirmDialogService.confirmThis("confirm",message, () => {
            //alert('Yes clicked');
        }, () => {
            //alert('No clicked');
        });
    }  
}

  
  reloadPage(): void {
    window.location.reload();
  }


}
