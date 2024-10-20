import { Component,  OnInit  } from '@angular/core';
import { LoginService } from '../services/login.service';
import { TokenStorageService } from '../services/token-storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css'],
})

export class LogoutComponent implements OnInit{
    menus:any;
    constructor(
      private login:LoginService,
      private tokenStorageService: TokenStorageService, 
      private router: Router
      ) { }
  

    ngOnInit(): void {
      
      this.login.logout();
      this.tokenStorageService.signOut();
    
            //this.router.navigate(['/home']);
            window.location.reload();
 
  }
    
  
}
