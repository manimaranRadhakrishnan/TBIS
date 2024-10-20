import { animate, keyframes, style, transition, trigger } from '@angular/animations';
import { Component, Output, EventEmitter, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { fadeInOut, INavbarData } from './helper';
import { navbarData } from './nav-data';
import { UserService } from '../services/user.service';
import { TokenStorageService } from '../services/token-storage.service';

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss'],
  animations: [
    fadeInOut,
    trigger('rotate', [
      transition(':enter', [
        animate('1000ms', 
          keyframes([
            style({transform: 'rotate(0deg)', offset: '0'}),
            style({transform: 'rotate(2turn)', offset: '1'})
          ])
        )
      ])
    ])
  ]
})
export class SidenavComponent implements OnInit {

  @Output() onToggleSideNav: EventEmitter<SideNavToggle> = new EventEmitter();
  collapsed = false;
  screenWidth = 0;
  navData = navbarData;
  multiple: boolean = false;

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.screenWidth = window.innerWidth;
    if(this.screenWidth <= 768 ) {
      this.collapsed = false;
      this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
    }
  }

  constructor(
    public router: Router,
    private tokenStorageService: TokenStorageService, 
    private userService:UserService) {}

  ngOnInit(): void {
      this.screenWidth = window.innerWidth;
      this.getUserMenus();

  }

  toggleCollapse(): void {
    this.collapsed = !this.collapsed;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }

  closeSidenav(): void {
    this.collapsed = false;
    this.onToggleSideNav.emit({collapsed: this.collapsed, screenWidth: this.screenWidth});
  }

  handleClick(item: INavbarData): void {
    this.shrinkItems(item);
    item.expanded = !item.expanded
    console.log(item.routeLink);
  }

  getActiveClass(data: INavbarData): string {
    return this.router.url.includes(data.routeLink) ? 'active' : '';
  }

  shrinkItems(item: INavbarData): void {
    if (!this.multiple) {
      for(let modelItem of this.navData) {
        if (item !== modelItem && modelItem.expanded) {
          modelItem.expanded = false;
        }
      }
    }
  }

  constructMenu(data:any){
    var menudata=this.tokenStorageService.getMenu();
    var tmpitems:any=[];
    if(menudata){
    menudata.data.forEach((element:any) => {
      var obj:any={};
      obj.id=element.menu_id;
      obj.label=element.menu_caption;
      obj.icon=element.menu_icon_class;
      if(element.menu_link!="" && element.menu_link!="#"){
        obj.routeLink=element.menu_link;
      }
      
      obj.items=undefined;
    if(element.parent_id==0){
      tmpitems.push(obj);
    }else{
      tmpitems.forEach((m:any)=>{
        if(m.id==element.parent_id){
          if(m.items==undefined){
            m.items=[];
          }
          m.items.push(obj);
        }else{
            if(m.items!=undefined){
                let m1:any=m.items.find((x:any)=>x.id==element.parent_id);
                if(m1){
                  if(m1.items==undefined){
                    m1.items=[];
                  }
                  m1.items.push(obj);
                }
            }
          
        }
      });
       
    }
    });
    //const menu: Menu =  tmpitems
    //this.menu.next(menu);
    this.navData=tmpitems;
    }
}


getUserMenus(){
  var menudata=this.tokenStorageService.getMenu();
  if(menudata){
    this.constructMenu(menudata);
  }else{
    this.userService.getUserMenus().subscribe(
      rdata => {
          if (rdata.data) {
            this.tokenStorageService.saveMenu(rdata);
            this.constructMenu(rdata);
          }
      },
      err => {
          //this.confirmDialogService.showMessage(err.message, () => { });
      }
  );
  }
  
}
}