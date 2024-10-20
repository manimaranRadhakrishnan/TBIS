import { Component,Input,OnInit,AfterViewInit } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Warehouse } from '../../../models/masters';

@Component({
  selector: 'app-sublocation',
  standalone: false,
  templateUrl: './sublocation.component.html',
  styleUrl: './sublocation.component.css'
})

export class SublocationComponent implements OnInit,AfterViewInit {

  status = 'start';
    @Input() masterObj: any;
    masterData:any={};
    cellData:any={};
    form: any = {};
    action:string="view";
    werehouse!:Warehouse[];
    eventsSubject: Subject<void> = new Subject<void>();
    spaceDetails:any = [];
    showCellDetailsDialog:boolean=false;
    usageTypes:any=[];
    customers:any=[];
    colorCodes:any=[];
    cellDetails:any=[];
    active:boolean=false;
    startCell:number=0;
    endCell:number=0;
    startRow:number=0;
    endRow:number=0;
    spaceDetail:any={};
    
  constructor(
    private gVar: AppGlobal,
    private service:MastersService,
    private toastr:ToastrService) { }


    ngAfterViewInit() {

      if(!this.masterObj){
        this.masterData={};
        this.masterData.subLocationId="";
        this.masterData.warehouseId=null;
        this.masterData.subLocationName="";
        this.masterData.subLocationShortCode="";
        this.masterData.areaInSqFeet="";
        this.masterData.areaInM2="";
        this.masterData.areaInM3="";
        this.masterData.customerStorageArea=0;
        this.masterData.active=true;
        this.masterData.slLength=0;
        this.masterData.slWidth=0;
        this.masterData.slHeight=0;

    
    }
    this.cellData.startCell=0;
    this.cellData.endCell=0;
    this.cellData.startRow=0;
    this.cellData.endRow=0;
    this.cellData.customerid=0;
    this.cellData.colorCode="";
    this.cellData.lineUsageId=0;
  
  }

  ngOnInit(): void {
    this.getlist();
  }

  getlist() {
    this.getAjaxData('warehouse');
    this.getCustomerData('customer');
    this.getLineUsageData('lineusage');
    // this.getColorCodes('colorcode');
  }

  clearData(){
    this.masterData={
      subLocationId: '',
      warehouseId: null,
      subLocationName: '',
      subLocationShortCode: '',
      areaInSqFeet: '',
      areaInM2: '',
      areaInM3: '',
      customerStorageArea: 0,
      active: true,
      slLength:0,
      slWidth:0,
      slHeight:0
};
this.cellData={startCell:0,
  endCell:0,
  startRow:0,
  endRow:0,
  customerid:0,
  colorCode:"",
  lineUsageId:0
}
  this.cellDetails={
          spaceId: 0,
          subLocationId:0,
          spaceName: "",
          colorCode: "",
          lineNo: 0,
          columnNo: 0,
          lineUsageId: 0,
          lineSpaceId:0,
          customerid: 0,
          partId: 0,
          userId: 0
  }
   }
   down(b:any,e:any) {
    if(e.button!=0){
      return;
    }
    e.preventDefault();
    if(this.active){
      for(let i=0;i<this.masterData.slRows;i++){
        for(let j=0;j<this.masterData.slColumns;j++){
          this.spaceDetails[i].columns[j].isChecked=false;
        }
      }  
      return;
    }
    for(let i=0;i<this.masterData.slRows;i++){
      for(let j=0;j<this.masterData.slColumns;j++){
        this.spaceDetails[i].columns[j].isChecked=false;
      }
    }
    this.startCell=b.columnNo;  
    this.startRow=b.lineNo;
     this.active = true
    //  if (this.active)
    //    b.isChecked = !b.isChecked
   }

 
   over(b:any,e:any) {
    e.preventDefault();  
    if(this.active){
    let endCell=b.columnNo;  
    let endRow=b.lineNo;
    console.log(this.endCell+" "+this.endRow+" "+this.startCell+" "+this.startRow);
    console.log("Start Row::"+(this.masterData.slRows-this.startRow)+" end row::"+(this.masterData.slRows-endRow));
     for(let i=this.masterData.slRows-endRow;i<=this.masterData.slRows-this.startRow;i++){
        for(let j=this.startCell-1;j<endCell;j++){
          console.log("i :: "+i+" j::"+j);
          this.spaceDetails[i].columns[j].isChecked=true;
        }
     }
    }
   }

   onMouseEnter(e:any) {
    let getData =  e.target.getAttribute("title");
    console.log("mouse enter : "+getData);
    const list: HTMLCollectionOf<Element> = document.getElementsByClassName("spacemaster");
    console.log("mouse enter : "+list);
    if(getData!="\rNot Mapped\r"){
      for(let i=0;i<=list.length;i++){        
        if(list[i]){
          list[i].classList.remove('zoomBox');
          if(list[i].getAttribute("title")==getData){
            list[i].classList.add('zoomBox');
          }
        }
      }
    }
  }
 
   up(b:any,e:any) {
    if(!this.active){
      e.preventDefault();
      return;
    }
    this.endCell=b.columnNo;  
    if(this.startRow>b.lineNo){
      this.endRow=this.startRow;
      this.startRow=b.lineNo;
    }else{
      this.endRow=b.lineNo;
    }  
    console.log(this.endCell+" "+this.endRow+" "+this.startCell+" "+this.startRow);
    this.cellData.startCell=this.startCell;
    this.cellData.endCell=this.endCell;
    this.cellData.startRow=this.startRow;
    this.cellData.endRow=this.endRow;
    //  this.active = false
    for(let i=this.masterData.slRows-this.endRow;i<=this.masterData.slRows-this.startRow;i++){
        for(let j=this.startCell-1;j<this.endCell;j++){
          this.spaceDetails[i].columns[j].isChecked=true;
        }
     }
     this.showCellDetailsDialog=true;
   }
    cancelAdd(){
        this.clearData();
        this.action="view";
        this.getlist();
      }
      closeCellDetailsModel(){
        this.showCellDetailsDialog=false;
        this.active=false;
        for(let i=0;i<this.masterData.slRows;i++){
          for(let j=0;j<this.masterData.slColumns;j++){
            this.spaceDetails[i].columns[j].isChecked=false;
          }
        } 
        this.cellData={
            lineUsageId:null,
            colorCode:'',
            customerid:null
        }
      }       
      addNew(){
        this.clearData();
          this.action="add";
        } 
      doAction(event: any){
        if(event){
          this.action="Edit";
            this.getSubLocationById(event.sublocationid.toString());
        }
      }

    getAjaxData(ajaxId:string):void{
      this.service.getAjaxDropDown(ajaxId).subscribe({
          next: (rdata) => {
              if (rdata.data) {
                  this.werehouse=[];
                  rdata.data.forEach((element:any) => {
                      this.werehouse.push(element);
                  });
              }
           },
          error: (e) => console.error(e),
          // complete: () => console.info('complete') 
      })
  
  }

  getCustomerData(ajaxId:string):void{
    this.service.getAjaxDropDown(ajaxId).subscribe({
        next: (rdata) => {
            if (rdata.data) {
                this.customers=[];
                rdata.data.forEach((element:any) => {
                    this.customers.push(element);
                });
            }
         },
        error: (e) => console.error(e),
        // complete: () => console.info('complete') 
    })

}

getLineUsageData(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.usageTypes=[];
              rdata.data.forEach((element:any) => {
                  this.usageTypes.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })

}

getColorCodes(ajaxId:string):void{
  this.service.getAjaxDropDown(ajaxId).subscribe({
      next: (rdata) => {
          if (rdata.data) {
              this.colorCodes=[];
              rdata.data.forEach((element:any) => {
                  this.colorCodes.push(element);
              });
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })

}
showCustomerDetail(b:any,e:any){
  e.preventDefault();
  this.getSpaceData('spacedetail',b.spaceId);
  return false;
}
getSpaceData(ajaxId:string,spaceId:any):void{
  this.service.getAjaxDropDown(ajaxId+'&spaceid='+spaceId).subscribe({
      next: (rdata) => {
          if (rdata.data && rdata.data.length>0) {
              this.spaceDetail=rdata.data[0];
              if(window.confirm("Do you want to remove the mapping for the "+this.spaceDetail.customername)){
                let spaceData={
                  customerid:this.spaceDetail.customerid,
                  subLocationId:this.spaceDetail.sublocationid
                }                
                this.service.removeSpaceAllocation(spaceData).subscribe({
                  next: (rdata) => {
                      if (rdata.isSuccess) {
                        this.service.getSubLocationSpace(this.masterData.subLocationId).subscribe({
                          next: (sdata) => {
                              if (sdata) {
                                      this.closeCellDetailsModel();
                                      this.spaceDetails=[];
                                      for (let _i = this.masterData.slRows; _i >=1 ; _i--) {
                                        let tmp:any = [];
                                          for (let _j = 1; _j <= this.masterData.slColumns; _j++) {
                                            tmp.push(sdata.find((x:any)=>x.lineNo==_i && x.columnNo==_j));
                                          }                            
                                          this.spaceDetails.push({
                                              columns: tmp
                                          });
                                       }
                              } else {
                                this.toastr.warning(rdata.status);                
                            }
                        }, 
                        error: (err) => { this.toastr.warning(err);  },    
                      
                        });      
                      } else{
                        this.toastr.warning(rdata.message);                
                      }
                    },   
                    error: (err) => { this.toastr.error(err.message) } 
                  });
              }
          }else{
              this.spaceDetail={};
          }
       },
      error: (e) => console.error(e),
      // complete: () => console.info('complete') 
  })

}

updateSubLocation(): void {

if (this.IsNullorEmpty(this.masterData.warehouseId)) {
    this.toastr.info("Please Select Warehouse");
    return;
}

if (this.IsNullorEmpty(this.masterData.subLocationName)) {
    this.toastr.info("Please enter Sub Location Name");
    return;
}

if (this.IsNullorEmpty(this.masterData.subLocationShortCode)) {
  this.toastr.info("Please enter Sub Location Short Code");
  return;
}
if (this.IsNullorEmpty(this.masterData.slHeight)) {
  this.toastr.info("Please enter Area Height");
  return;
}

if (this.IsNullorEmpty(this.masterData.uWidth)) {
  this.toastr.info("Please enter Area Width");
  return;
}
if (this.IsNullorEmpty(this.masterData.uLength)) {
  this.toastr.info("Please enter Area Length");
  return;
}


  let data:any={};
   data.subLocationId=this.masterData.subLocationId==""?"0":this.masterData.subLocationId;
   data.warehouseId=this.masterData.warehouseId;
   data.subLocationName=this.masterData.subLocationName;
   data.subLocationShortCode=this.masterData.subLocationShortCode;
   data.slLength=this.masterData.slLength;
   data.slWidth=this.masterData.slWidth;
   data.slHeight=this.masterData.slHeight;
   data.areaInSqFeet=0;
   data.areaInM2=0;
   data.areaInM3=0;
   data.customerStorageArea=0;
   data.isActive=this.masterData.active;
   data.uLength=this.masterData.uLength;
   data.uWidth=this.masterData.uWidth;
   data.slRows=this.masterData.slRows;
   data.slColumns=this.masterData.slColumns;
  //  data.userId=this.gVar.userId;

   this.service.updateSubLocation(data).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
            this.toastr.success(rdata.message);
            this.cancelAdd();
            this.action="view";
        } else {
            this.toastr.warning(rdata.message);
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })

}
getSubLocationById(typeid: any): void {

  this.service.getSubLocationById(typeid).subscribe({
    next: (rdata) => {
        if (rdata.result) {
            this.masterData={};         
            this.masterData.subLocationId=rdata.result.subLocationId;     
                this.masterData.warehouseId=rdata.result.warehouseId+"";
                this.masterData.subLocationName=rdata.result.subLocationName;
                this.masterData.subLocationShortCode=rdata.result.subLocationShortCode;
                this.masterData.areaInSqFeet=rdata.result.areaInSqFeet;
                this.masterData.areaInM2=rdata.result.areaInM2;
                this.masterData.areaInM3=rdata.result.areaInM3;
                this.masterData.slWidth=rdata.result.slWidth;
                this.masterData.slLength=rdata.result.slLength;
                this.masterData.slHeight=rdata.result.slHeight;
                this.masterData.customerStorageArea=rdata.result.customerStorageArea;
                this.masterData.active=rdata.result.isActive;
                this.masterData.uLength=rdata.result.uLength;
                this.masterData.uWidth=rdata.result.uWidth;
                this.masterData.slRows=rdata.result.slRows;
                this.masterData.slColumns=rdata.result.slColumns;
                this.spaceDetails=[];
                this.service.getSubLocationSpace(typeid).subscribe({
                  next: (sdata) => {
                      if (sdata) {
                              this.spaceDetails=[];
                              for (let _i = rdata.result.slRows; _i >=1; _i--) {
                                let tmp:any = [];
                                  for (let _j = 1; _j <= rdata.result.slColumns; _j++) {
                                    tmp.push(sdata.find((x:any)=>x.lineNo==_i && x.columnNo==_j));
                                  }                            
                                  this.spaceDetails.push({
                                      columns: tmp
                                  });
                               }
                      } else {
                        this.toastr.warning(rdata.status);                
                    }
                }, 
                error: (err) => { this.toastr.warning(err);  },    
              
                });
        } else {
          this.toastr.warning(rdata.status);                
      }
  }, 
  error: (err) => { this.toastr.warning(err);  },    

  });
}
showCellDetails(col:any):void{
  this.cellDetails=col;
  this.showCellDetailsDialog=true;
}
updateLineCellDetail():void{
  if (this.cellData.lineUsageId!=13){
    this.cellData.customerid=0;
  }
  if (this.IsNullorEmpty(this.cellData.lineUsageId)) {
    this.cellData.colorCode="";
  }

  this.cellData.subLocationId=this.masterData.subLocationId;
  this.service.checkSpaceAllocation(this.cellData).subscribe({
    next: (rdata) => {
        if (rdata.isSuccess) {
          this.service.updateSpaceAllocation(this.cellData).subscribe({
            next: (rdata) => {
                if (rdata.isSuccess) {
                  this.service.getSubLocationSpace(this.masterData.subLocationId).subscribe({
                    next: (sdata) => {
                        if (sdata) {
                                this.closeCellDetailsModel();
                                this.spaceDetails=[];
                                for (let _i = this.masterData.slRows; _i >=1 ; _i--) {
                                  let tmp:any = [];
                                    for (let _j = 1; _j <= this.masterData.slColumns; _j++) {
                                      tmp.push(sdata.find((x:any)=>x.lineNo==_i && x.columnNo==_j));
                                    }                            
                                    this.spaceDetails.push({
                                        columns: tmp
                                    });
                                 }
                        } else {
                          this.toastr.warning(rdata.status);                
                      }
                  }, 
                  error: (err) => { this.toastr.warning(err);  },    
                
                  });      
                } else{
                  this.toastr.warning(rdata.message);                
                }
              },   
              error: (err) => { this.toastr.error(err.message) } 
            })        
        } else if(rdata.message=="Space already mapped"){
            if(confirm("Space already mapped. Do you want to overwrite the space allocation?")){
              this.service.updateSpaceAllocation(this.cellData).subscribe({
                next: (rdata) => {
                    if (rdata.isSuccess) {
                      this.service.getSubLocationSpace(this.masterData.subLocationId).subscribe({
                        next: (sdata) => {
                            if (sdata) {
                                    this.closeCellDetailsModel();
                                    this.spaceDetails=[];
                                    for (let _i = this.masterData.slRows; _i>=1 ; _i--) {
                                      let tmp:any = [];
                                        for (let _j = 1; _j <= this.masterData.slColumns; _j++) {
                                          tmp.push(sdata.find((x:any)=>x.lineNo==_i && x.columnNo==_j));
                                        }                            
                                        this.spaceDetails.push({
                                            columns: tmp
                                        });
                                     }
                            } else {
                              this.toastr.warning(rdata.status);                
                          }
                      }, 
                      error: (err) => { this.toastr.warning(err);  },    
                    
                      });      
                    } else{
                      this.toastr.warning(rdata.message);                
                    }
                  },   
                  error: (err) => { this.toastr.error(err.message) } 
                })
            
            }
        }else{
          this.toastr.warning(rdata.message);                
        }
      },   
      error: (err) => { this.toastr.error(err.message) } 
    })
}
    reloadPage(): void {
            window.location.reload();
    }

      numberOnly(event: { which: any; keyCode: any; }): boolean {
      const charCode = (event.which) ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
          return false;
      }
      return true;

      }


      get f() { return this.form.controls; }

      IsNullorEmpty(value: any): boolean {
      if (value == undefined || value == "" ) {
          return true;
      }
      return false;;
      }

  
      onChangeUsageType(e:any){
        let lineUsageData:any=JSON.parse(JSON.stringify(this.usageTypes.find((x:any)=>x.usageid==e.usageid)));
        this.cellData.colorCode=lineUsageData.colorcode;
      }
}
