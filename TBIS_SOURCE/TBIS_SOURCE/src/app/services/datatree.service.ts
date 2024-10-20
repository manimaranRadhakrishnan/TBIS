import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment.uat';
import { AppGlobal } from './appglobal.service';
import { AppConfig } from './appconfig.service';
import { TodoItemNode } from '../models/treedata';

const API_KEY = environment.apiaccessKey;

const TREE_DATA:any = [];


@Injectable({
  providedIn: 'root'
})

export class ChecklistDatabase {
  dataChange = new BehaviorSubject<TodoItemNode[]>([]);

  get data(): TodoItemNode[] { return this.dataChange.value; }

  constructor() {
    this.initialize();
  }

  initialize() {
    // Build the tree nodes from Json object. The result is a list of `TodoItemNode` with nested
    //     file node as children.
    const data = this.buildFileTree(TREE_DATA, 0);

    // Notify the change.
    this.dataChange.next(data);
  }

  /**
   * Build the file structure tree. The `value` is the Json object, or a sub-tree of a Json object.
   * The return value is the list of `TodoItemNode`.
   */
  buildFileTree(obj: {[key: string]: any}, level: number): TodoItemNode[] {
    return Object.keys(obj).reduce<TodoItemNode[]>((accumulator, key) => {
      const item = obj[key];
      const node = new TodoItemNode();
      node.label = obj[key].lable;
      node.id = obj[key].id;
      node.isChecked=  obj[key].isChecked;
      node.claimId=  obj[key].claimId;
      node.isPlanType=  obj[key].isPlanType;

      if (item != null) {
        if (typeof item === 'object'  && item.children!= undefined) { 
       

          node.children = this.buildFileTree(item.children, level + 1);
        } else {
          node.label = item.lable;
        }
      }

      return accumulator.concat(node);
    }, []);
  }

  /** Add an item to to-do list */
  insertItem(parent: TodoItemNode, lable: string) {
    if (parent.children) {
      parent.children.push({label: lable} as TodoItemNode);
      this.dataChange.next(this.data);
    }
  }

  updateItem(node: TodoItemNode, lable: string) {
    node.label = lable;
    this.dataChange.next(this.data);
  }
}
@Injectable({
  providedIn: 'root'
})

export class TreeDataService {
    AUTH_API:string="";
    httpKeyOption:any={};
    constructor(private appConfig: AppConfig, private gVaraible: AppGlobal, private http: HttpClient) {
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
    getParentMenus(roleId:number): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/role/menus?code='+roleId, this.httpKeyOption);
    }
    getRole(roleId:number): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/role?code='+roleId, this.httpKeyOption);
    }
    updateRoleMaster(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'users/role', obj, this.httpKeyOption);
    }

}
