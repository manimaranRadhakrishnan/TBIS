import { Component,Input,OnInit,AfterViewInit,ViewChild  } from '@angular/core';
import { AppGlobal } from './../../services/appglobal.service';
import { Router } from '@angular/router';
import { AuthService } from './../../services/auth.service';
import { TokenStorageService } from './../../services/token-storage.service';
import { MastersService } from './../../services/masters.service';
import { FormsModule } from '@angular/forms';
import { ConfirmDialogService } from './../../components/confirm-dialog/confirm-dialog.service';
import { Subject } from 'rxjs';
import { PartMasterDDL } from './../../models/masters';
import { ToastrService } from 'ngx-toastr';
import {SelectionModel} from '@angular/cdk/collections';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import { TodoItemFlatNode, TodoItemNode } from '../../models/treedata';
import { TreeDataService } from '../../services/datatree.service';

@Component({
  selector: 'app-roles',
  standalone: false,
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.css'
})
export class RolesComponent {
    status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    contactData:any={};
    partData:any={};
    form: any = {};
    action:string="view";
    parentMenus:any=[];
    selectedMenus:any=[];    
    treeData:TodoItemNode[]=[];
    partmaster!:PartMasterDDL[];
    eventsSubject: Subject<void> = new Subject<void>();
    // -----------------------------------------------start tree checkbox---------------------------------------------------------------------------------


  /** Map from flat node to nested node. This helps us finding the nested node to be modified */
  flatNodeMap = new Map<TodoItemFlatNode, TodoItemNode>();

  /** Map from nested node to flattened node. This helps us to keep the same object for selection */
  nestedNodeMap = new Map<TodoItemNode, TodoItemFlatNode>();

  /** A selected parent node to be inserted */
  selectedParent: TodoItemFlatNode | null = null;

  /** The new item's name */
  newItemName = '';

  treeControl: FlatTreeControl<TodoItemFlatNode>;

  treeFlattener: MatTreeFlattener<TodoItemNode, TodoItemFlatNode>;

  dataSource: MatTreeFlatDataSource<TodoItemNode, TodoItemFlatNode>;

  /** The selection for checklist */
  checklistSelection = new SelectionModel<TodoItemFlatNode>(true /* multiple */);


// -----------------------------------------------end tree checkbox---------------------------------------------------------------------------------

    constructor(
      private GlobalVariable: AppGlobal,
      private router: Router, 
      private authService: AuthService, 
      private tokenStorage: TokenStorageService,
      private service:MastersService,
      private toastr:ToastrService,
      private roleService:TreeDataService,
      // private database: ChecklistDatabase
      ) { 
        this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel,
          this.isExpandable, this.getChildren);
        this.treeControl = new FlatTreeControl<TodoItemFlatNode>(this.getLevel, this.isExpandable);
        this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
    
        // database.dataChange.subscribe((data:any) => {
        //   this.dataSource.data = data;
        //        });
      }
  
  
  
      ngAfterViewInit() {
  
        if(!this.masterObj){
          this.masterData={};
          this.masterData.roleId="";
          this.masterData.roleName="";
          this.masterData.roleDescription="";
          this.masterData.parentRoleId="";
          this.masterData.active=true;
      }
  }
  get f() { return this.form.controls; }

    IsNullorEmpty(value: any): boolean {
    if (value == undefined || value == "") {
        return true;
    }
    return false;;
    }


    cancelAdd(){
      this.masterData={
              roleId: '',                
              roleName: '',
              roleDescription: '',
              parentRoleId: '',
              active: true,
      };
      this.action="view";
    } 

    addNew(){
      this.masterData={
        roleId: '',                
              roleName: '',
              roleDescription: '',
              parentRoleId: '',
              active: true,
      };
      this.action="add";
    } 

    doAction(event: any){
      if(event){
          this.action="Edit";
          this.getRole(event.role_id.toString());
      }
    }
    buildTreeData(){
      this.dataSource.data =[];
      this.checklistSelection.clear();
      this.treeData=[];
      this.parentMenus.forEach((element:any) => {
        const node = new TodoItemNode();
        node.label = element.menuName;
        node.id = element.menuId;
        node.isChecked=  element.selected;
        node.claimId=  element.parentId;
        node.isPlanType=  element.menuCaption;        
       
        if(element.parentId==0){
          this.treeData.push(node);
        }else{
          this.treeData.forEach((m:any)=>{
            if(m.id==element.parentId){
              if(m.children==undefined){
                m.children=[];
              }
              m.children.push(node);
            }else{
                if(m.children!=undefined){
                    let m1:any=m.children.find((x:any)=>x.id==element.parentId);
                    if(m1){
                      if(m1.children==undefined){
                        m1.children=[];
                      }
                      m1.children.push(node);
                    }
                }
              
            }
          });
           
        }
      });
      this.dataSource.data = this.treeData;
    }
    getParentRoleMenus(roleId:string){
      this.roleService.getParentMenus(Number(roleId)).subscribe( rdata => {
        if (rdata.result) {
              this.parentMenus=rdata.result.roleMenus;
              this.buildTreeData();
        } else {
            this.toastr.error(rdata.message);     
            // this.setFocus();                      
        }

    },
  (        err: { message: any; }) => {
    this.toastr.error(err.message);
        
    });
    }
    getRole(roleId:string){
      this.roleService.getRole(Number(roleId)).subscribe( rdata => {
        if (rdata.result) {
          this.masterData.roleId=rdata.result.roleId;
          this.masterData.roleName=rdata.result.roleName;
          this.masterData.roleDescription=rdata.result.roleDescription;
          this.masterData.parentRoleId=rdata.result.parentRoleId;
          this.masterData.parentRoleName=rdata.result.parentRoleName;
          this.parentMenus=rdata.result.roleMenus;
          this.buildTreeData();
          this.checkAll();
        } else {
            this.toastr.error(rdata.message);     
            // this.setFocus();                      
        }

    },
  (        err: { message: any; }) => {
    this.toastr.error(err.message);
        
    });
    }
    updateRole(): void {
      this.selectedMenus=[];
      // alert(JSON.stringify(this.checklistSelection.selected));
      this.checklistSelection.selected.forEach((element:any)=>{
          let menu:any={};
          menu.menuId=element.id;
          menu.menuName=element.lable;
          menu.parentId=element.claimId;
          menu.menuCaption=element.isPlanType;
          menu.selected=true;
        
          this.selectedMenus.push(menu);
      });
      if (this.IsNullorEmpty(this.masterData.roleName)) {
        this.toastr.info("Please enter Role Name");
        return;
      }
      
      if (this.IsNullorEmpty(this.masterData.parentRoleId)) {
        this.toastr.info("Please select parent role Vendor Code");
        return;
      }

      if (this.selectedMenus.length==0) {
        this.toastr.info("Please select role menus");
        return;
      }
        let data:any={};
         data.roleId=this.masterData.roleId==""?"0":this.masterData.roleId;
         data.roleName=this.masterData.roleName;
         data.roleDescription=this.masterData.roleDescription;
         data.parentRoleId=this.masterData.parentRoleId;
        //  data.active=this.masterData.active;
         data.userId=this.GlobalVariable.userId;
         data.roleMenus=this.selectedMenus;
      this.roleService.updateRoleMaster(data).subscribe(
       
        (    rdata: { isSuccess: any; message: string;result:any }) => {
              if (rdata.isSuccess) {
                  this.toastr.success(rdata.message);
                  this.cancelAdd();
                  this.action="view";
                 // this.rpt.getReportData();
              } else {
                this.toastr.warning(rdata.message);
              }
          },
        (    err: { message: any; }) => {
              this.toastr.error(err.message);
               
          }
      );}


      // -----------------------------------------------start tree checkbox---------------------------------------------------------------------------------


checkAll(){
  for (let i = 0; i < this.treeControl.dataNodes.length; i++) {

  if(this.treeControl.dataNodes[i].isChecked)
      this.checklistSelection.toggle(this.treeControl.dataNodes[i]);
    // this.treeControl.expand(this.treeControl.dataNodes[i])
  }
}

 GetCheckAll(){
  // this.toastr.info(JSON.stringify(this.dataSource.data));
  // console.log(JSON.stringify(this.dataSource.data));
    // if( this.treeFlattener.flattenNodes[0].check) console.log(this.treeControl.dataNodes[i].id);

   
  // for (let i = 0; i < this.treeControl.dataNodes.length; i++) {
   
  //   if(this.treeControl.dataNodes[i].isChecked) console.log(this.treeControl.dataNodes[i].id);

  // if(this.treeControl.dataNodes[i].isChecked){
  //   console.log('---------------------------------------------');
  //     console.log(this.treeControl.dataNodes[i].id);
  //     console.log(this.treeControl.dataNodes[i].claimId);

  // }
 // }
}

getLevel = (node: TodoItemFlatNode) => node.level;

isExpandable = (node: TodoItemFlatNode) => node.expandable;

getChildren = (node: TodoItemNode): TodoItemNode[] => node.children;

hasChild = (_: number, _nodeData: TodoItemFlatNode) => _nodeData.expandable;

hasNoContent = (_: number, _nodeData: TodoItemFlatNode) => _nodeData.label === '';

/**
 * Transformer to convert nested node to flat node. Record the nodes in maps for later use.
 */
transformer = (node: TodoItemNode, level: number) => {
  const existingNode = this.nestedNodeMap.get(node);
  const flatNode = existingNode && existingNode.label === node.label
      ? existingNode
      : new TodoItemFlatNode();
  flatNode.label = node.label;
  flatNode.level = level;
  flatNode.id=node.id;
   flatNode.isChecked = node.isChecked;
   flatNode.claimId = node.claimId;
   flatNode.isPlanType = node.isPlanType;
  flatNode.expandable = !!node.children;
  this.flatNodeMap.set(flatNode, node);
  this.nestedNodeMap.set(node, flatNode);
  return flatNode;
}

/** Whether all the descendants of the node are selected. */
descendantsAllSelected(node: TodoItemFlatNode): boolean {
  const descendants = this.treeControl.getDescendants(node);
  const descAllSelected = descendants.every(child =>
   this.checklistSelection.isSelected(child))
  return descAllSelected;
}

/** Whether part of the descendants are selected */
descendantsPartiallySelected(node: TodoItemFlatNode): boolean {
  const descendants = this.treeControl.getDescendants(node);
  const result = descendants.some(child => this.checklistSelection.isSelected(child));
  return result && !this.descendantsAllSelected(node);
}

/** Toggle the to-do item selection. Select/deselect all the descendants node */
todoItemSelectionToggle(node: TodoItemFlatNode): void {

  this.checklistSelection.toggle(node);
  const descendants = this.treeControl.getDescendants(node);
  this.checklistSelection.isSelected(node)
    ? this.checklistSelection.select(...descendants)
    : this.checklistSelection.deselect(...descendants);

  // Force update for the parent
  descendants.every(child =>
    this.checklistSelection.isSelected(child)
  );
  this.checkAllParentsSelection(node);
}

/** Toggle a leaf to-do item selection. Check all the parents to see if they changed */
todoLeafItemSelectionToggle(node: TodoItemFlatNode): void {
 
  this.checklistSelection.toggle(node);
  node.isChecked ?  node.isChecked=false:node.isChecked=true; 

  this.checkAllParentsSelection(node); 
  // this.toastr.success("Submited " + JSON.stringify(node));

}

/* Checks all the parents when a leaf node is selected/unselected */
checkAllParentsSelection(node: TodoItemFlatNode): void {
  let parent: TodoItemFlatNode | null = this.getParentNode(node);
  while (parent) {
    this.checkRootNodeSelection(parent);
    parent = this.getParentNode(parent);
  }
}

/** Check root node checked state and change it accordingly */
checkRootNodeSelection(node: TodoItemFlatNode): void {
  const nodeSelected = this.checklistSelection.isSelected(node);
  const descendants = this.treeControl.getDescendants(node);
  const descAllSelected = descendants.every(child =>
    this.checklistSelection.isSelected(child)
  );
  if (nodeSelected && !descAllSelected) {
    this.checklistSelection.deselect(node);
  } else if (!nodeSelected && descAllSelected) {
    this.checklistSelection.select(node);
  }
}

/* Get the parent node of a node */
getParentNode(node: TodoItemFlatNode): TodoItemFlatNode | null {
  const currentLevel = this.getLevel(node);

  if (currentLevel < 1) {
    return null;
  }

  const startIndex = this.treeControl.dataNodes.indexOf(node) - 1;

  for (let i = startIndex; i >= 0; i--) {
    const currentNode = this.treeControl.dataNodes[i];

    if (this.getLevel(currentNode) < currentLevel) {
      return currentNode;
    }
  }
  return null;
}

/** Select the category so we can insert the new item. */
addNewItem(node: TodoItemFlatNode) {
  const parentNode = this.flatNodeMap.get(node);
  // this.database.insertItem(parentNode!, '');
  this.treeControl.expand(node);
}

/** Save the node to database */
saveNode(node: TodoItemFlatNode, itemValue: string) {
  const nestedNode = this.flatNodeMap.get(node);
  // this.database.updateItem(nestedNode!, itemValue);
}

// -----------------------------------------------end tree checkbox---------------------------------------------------------------------------------
      
      
}
