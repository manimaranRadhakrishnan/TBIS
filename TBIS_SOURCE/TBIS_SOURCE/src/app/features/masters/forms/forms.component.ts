import { AfterViewInit, Component,Input, OnDestroy, OnInit  } from '@angular/core';
import { AppGlobal } from '../../../services/appglobal.service';
import { MastersService } from '../../../services/masters.service';
import { Subject, Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Editor } from 'ngx-editor';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-forms',
  standalone: false,
  templateUrl: './forms.component.html',
  styleUrl: './forms.component.css'
})

export class FormsComponent implements OnInit,AfterViewInit,OnDestroy {
  navigationSubscription;  
  private eventsSubscription: Subscription = new Subscription;
  @Input() masterObj: any;

  masterData:any={};
  action:string="view";
  enableFormA:boolean=false;
  enableFormC:boolean=false;
  enableFormI:boolean=false;
  enableFormG:boolean=false;

  // -----------------start temp------------------
  divisionList:any=[];
  baranchList:any=[];
  assetCatList:any=[];
  typeOfTransList:any=[];
  appRelatingList:any=[];
  advDesignationList:any=[];
  vfyDesignationList:any=[];
  apvDesignationList:any=[];
  warehouseList:any=[];
  typeOfContractList:any=[];
  itAssetGuidelineList:any=[];
  // editorAppRelatingDesc!: Editor;
  // editorAssetCatDesc!: Editor;
  // editorAdvisorComment!:Editor;
  // editorVerifyComment!:Editor;
  // editorApprovalComment!:Editor;
  assetCardDetails:any=[];
  advisorCardDetails:any=[];
  verifyCardDetails:any=[];
  approvalCardDetails:any=[];
  contractDocCardDetails:any=[];
  formIdFromRoute: any;
  assetCardDetailsI:any=[];
  assetCardDetailsFI:any=[];
  // -----------------end temp------------------

  constructor(
    private gVar: AppGlobal,
    private route: ActivatedRoute,
    private router: Router, 
    private toastr:ToastrService) {
      this.navigationSubscription = this.router.events.subscribe((e: any) => {
        if (e instanceof NavigationEnd) {
          this.ngOnInit();
        }
      });
    }

  ngAfterViewInit(): void {
    if(!this.masterObj){
      this.masterData={
        asset:{
          assetDataDesc:"",
          assetDataQty: "",
          assetDataQuoteValue: "",
          assetDataYear: "",
          assetDataAnnual: "",
          assetDataMonthly: "",
        },
        advisor:{
          advDesignation:null,
          advDate: "",
          advName: "",
          advSign: "",
          advComment: "",
        },
        verify:{
          vfyDesignation:null,
          vfyDate: "",
          vfyName: "",
          vfySign: "",
          vfyComment: "",
        },
        approval:{
          apvDesignation:null,
          apvDate: "",
          apvName: "",
          apvSign: "",
          apvComment: "",
        },
        contractDoc:{
          contractDocName:"",
        },
        assetI:{
          assetDataDescI:"",
          assetDataQtyI: "",
          assetDataQuoteValueI: "",
          assetDataYearI: "",
          assetDataAnnualI: "",
          assetDataMonthlyI: "",
        },
        assetFI:{
          assetDataDescFI:"",
          assetDataQuoteValueFI: "",
          assetDataYearFI: "",
          assetDataAnnualFI: "",
          assetDataMonthlyFI: "",
        },
      }
    }
  }
  ngOnInit(): void {
    const routeParams = this.route.snapshot.paramMap;
    this.formIdFromRoute = routeParams.get('formid'); 

    this.clearData();

    if(this.formIdFromRoute){
      if(this.formIdFromRoute=="A"){
        this.enableFormA=true;
      }else if(this.formIdFromRoute=="C"){
        this.enableFormC=true;
      }else if(this.formIdFromRoute=="I"){
        this.enableFormI=true;
      }else if(this.formIdFromRoute=="G"){
        this.enableFormG=true;
      }
    }

    // this.editorAppRelatingDesc = new Editor();
    // this.editorAssetCatDesc = new Editor();
    // this.editorAdvisorComment = new Editor();
    // this.editorVerifyComment = new Editor();
    // this.editorApprovalComment = new Editor();
  }  
  ngOnDestroy(): void {
    if (this.navigationSubscription) {  
      this.navigationSubscription.unsubscribe();
    }
      this.eventsSubscription?this.eventsSubscription.unsubscribe():"";
    // this.editorAppRelatingDesc.destroy();
    // this.editorAssetCatDesc.destroy();
    // this.editorAdvisorComment.destroy();
    // this.editorVerifyComment.destroy();
    // this.editorApprovalComment.destroy();
  }

  clearData(){
    this.masterData={};
    this.action="view";
    this.enableFormA=false;
    this.enableFormC=false;
    this.enableFormI=false;
    this.enableFormG=false;  

    this.divisionList=[];
    this.baranchList=[];
    this.assetCatList=[];
    this.typeOfTransList=[];
    this.appRelatingList=[];
    this.advDesignationList=[];
    this.vfyDesignationList=[];
    this.apvDesignationList=[];
    this.warehouseList=[];
    this.typeOfContractList=[];
    this.itAssetGuidelineList=[];

    this.assetCardDetails=[];
    this.advisorCardDetails=[];
    this.verifyCardDetails=[];
    this.approvalCardDetails=[];
    this.contractDocCardDetails=[];
    this.assetCardDetailsI=[];
    this.assetCardDetailsFI=[];

    this.masterData.asset={
      assetDataDesc:"",
      assetDataQty: "",
      assetDataQuoteValue: "",
      assetDataYear: "",
      assetDataAnnual: "",
      assetDataMonthly: "",
    };

    this.masterData.advisor={
      advDesignation:null,
      advDate: "",
      advName: "",
      advSign: "",
      advComment: "",
    };

    this.masterData.verify={
      vfyDesignation:null,
      vfyDate: "",
      vfyName: "",
      vfySign: "",
      vfyComment: "",
    };

    this.masterData.approval={
      apvDesignation:null,
      apvDate: "",
      apvName: "",
      apvSign: "",
      apvComment: "",
    };

    this.masterData.contractDoc={
      contractDocName:"",
    };    

    this.masterData.assetI={
      assetDataDescI:"",
      assetDataQtyI: "",
      assetDataQuoteValueI: "",
      assetDataYearI: "",
      assetDataAnnualI: "",
      assetDataMonthlyI: "",
    };  

    this.masterData.assetFI={
      assetDataDescFI:"",
      assetDataQuoteValueFI: "",
      assetDataYearFI: "",
      assetDataAnnualFI: "",
      assetDataMonthlyFI: "",
    };

  }

  // updateFormA(): void {this.toastr.info("Submit Form A");}
  // updateFormC(): void {this.toastr.info("Submit Form C");}
  // updateFormI(): void {this.toastr.info("Submit Form I");}
  // updateFormG(): void {this.toastr.info("Submit Form G");}

  updateSubmitForm(){
    let data:any={};
    data.formType=this.formIdFromRoute;
    this.toastr.info("Submit Form "+data.formType)
  }

  updateAssetItems(){
    let assetDetail:any={
      assetDataDesc:this.masterData.asset.assetDataDesc,
      assetDataQty:this.masterData.asset.assetDataQty,
      assetDataQuoteValue:this.masterData.asset.assetDataQuoteValue,
      assetDataYear:this.masterData.asset.assetDataYear,
      assetDataAnnual:this.masterData.asset.assetDataQuoteValue/this.masterData.asset.assetDataYear,
      assetDataMonthly:(this.masterData.asset.assetDataQuoteValue/this.masterData.asset.assetDataYear)/12,
    }
    this.assetCardDetails.push(assetDetail);
    // calculate count
    this.masterData.assetDataQtyTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQty),0);
    this.masterData.assetDataQuoteValueTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQuoteValue),0);
    this.masterData.assetDataAnnualTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataAnnual),0);
    this.masterData.assetDataMonthlyTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataMonthly),0);
    // end count
    this.masterData.asset={
      assetDataDesc:"",
      assetDataQty: "",
      assetDataQuoteValue: "",
      assetDataYear: "",
      assetDataAnnual: "",
      assetDataMonthly: "",
     }
  
  }
  
  removeAssetValue(i:number){
    if(this.assetCardDetails.length>i){
      this.assetCardDetails.splice(i,1);
      // calculate count
      this.masterData.assetDataQtyTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQty),0);
      this.masterData.assetDataQuoteValueTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQuoteValue),0);
      this.masterData.assetDataAnnualTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataAnnual),0);
      this.masterData.assetDataMonthlyTotal=this.assetCardDetails.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataMonthly),0);
      // end count
    }
  }

  
  updateAdvisorItems(){
    let advisorDetail:any={
      advDesignation:this.masterData.advisor.advDesignation,
      advDate:this.masterData.advisor.advDate,
      advName:this.masterData.advisor.advName,
      advSign:this.masterData.advisor.advSign,
      advComment:this.masterData.advisor.advComment,
    }
    this.advisorCardDetails.push(advisorDetail);
    this.masterData.advisor={
      advDesignation:null,
      advDate: "",
      advName: "",
      advSign: "",
      advComment: "",
     }
  
  }
  
  removeAdvisorValue(i:number){
    if(this.advisorCardDetails.length>i){
      this.advisorCardDetails.splice(i,1);
    }
  }

  updateVerifyItems(){
    let verifyDetail:any={
      vfyDesignation:this.masterData.verify.vfyDesignation,
      vfyDate:this.masterData.verify.vfyDate,
      vfyName:this.masterData.verify.vfyName,
      vfySign:this.masterData.verify.vfySign,
      vfyComment:this.masterData.verify.vfyComment,
    }
    this.verifyCardDetails.push(verifyDetail);
    this.masterData.verify={
      vfyDesignation:null,
      vfyDate: "",
      vfyName: "",
      vfySign: "",
      vfyComment: "",
     }
  
  }
  
  removeVerifyValue(i:number){
    if(this.verifyCardDetails.length>i){
      this.verifyCardDetails.splice(i,1);
    }
  }

  updateApprovalItems(){
    let approvalDetail:any={
      apvDesignation:this.masterData.approval.apvDesignation,
      apvDate:this.masterData.approval.apvDate,
      apvName:this.masterData.approval.apvName,
      apvSign:this.masterData.approval.apvSign,
      apvComment:this.masterData.approval.apvComment,
    }
    this.approvalCardDetails.push(approvalDetail);
    this.masterData.approval={
      apvDesignation:null,
      apvDate: "",
      apvName: "",
      apvSign: "",
      apvComment: "",
     }
  
  }
  
  removeApprovalValue(i:number){
    if(this.approvalCardDetails.length>i){
      this.approvalCardDetails.splice(i,1);
    }
  }

    
  updateContractDocItems(){
    let contractDocDetail:any={
      contractDocName:this.masterData.contractDoc.contractDocName,
    }
    this.contractDocCardDetails.push(contractDocDetail);
    this.masterData.contractDoc={
      contractDocName:"",
     }
  
  }
  
  removeContractDocValue(i:number){
    if(this.contractDocCardDetails.length>i){
      this.contractDocCardDetails.splice(i,1);
    }
  }

  
  updateAssetItemsFormI(){
    let assetDetailI:any={
      assetDataDescI:this.masterData.assetI.assetDataDescI,
      assetDataQtyI:this.masterData.assetI.assetDataQtyI,
      assetDataQuoteValueI:this.masterData.assetI.assetDataQuoteValueI,
      assetDataYearI:this.masterData.assetI.assetDataYearI,
      assetDataAnnualI:(this.masterData.assetI.assetDataQtyI*this.masterData.assetI.assetDataQuoteValueI)/3,
      assetDataMonthlyI:((this.masterData.assetI.assetDataQtyI*this.masterData.assetI.assetDataQuoteValueI)/3)/12,
    }
    this.assetCardDetailsI.push(assetDetailI);
    // calculate count
    this.masterData.assetDataQtyTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQtyI),0);
    this.masterData.assetDataQuoteValueTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQuoteValueI),0);
    this.masterData.assetDataAnnualTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataAnnualI),0);
    this.masterData.assetDataMonthlyTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataMonthlyI),0);
    // end count
    this.masterData.assetI={
      assetDataDescI:"",
      assetDataQtyI: "",
      assetDataQuoteValueI: "",
      assetDataYearI: "",
      assetDataAnnualI: "",
      assetDataMonthlyI: "",
     }
  
  }
  
  removeAssetValueFormI(i:number){
    if(this.assetCardDetailsI.length>i){
      this.assetCardDetailsI.splice(i,1);
      // calculate count
      this.masterData.assetDataQtyTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQtyI),0);
      this.masterData.assetDataQuoteValueTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataQuoteValueI),0);
      this.masterData.assetDataAnnualTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataAnnualI),0);
      this.masterData.assetDataMonthlyTotalI=this.assetCardDetailsI.reduce((sum:any,x:any)=>parseInt(sum)+parseInt(x.assetDataMonthlyI),0);
      // end count
    }
  }

    
  updateAssetItemsFormITwo(){
    let assetDetailFI:any={
      assetDataDescFI:this.masterData.assetFI.assetDataDescFI,
      assetDataQuoteValueFI:this.masterData.assetFI.assetDataQuoteValueFI,
      assetDataYearFI:this.masterData.assetFI.assetDataYearFI,
      assetDataAnnualFI:(this.masterData.assetFI.assetDataQuoteValueFI)/3,
      assetDataMonthlyFI:((this.masterData.assetFI.assetDataQuoteValueFI)/3)/12,
    }
    this.assetCardDetailsFI.push(assetDetailFI);
    this.masterData.assetFI={
      assetDataDescFI:"",
      assetDataQuoteValueFI: "",
      assetDataYearFI: "",
      assetDataAnnualFI: "",
      assetDataMonthlyFI: "",
     }
  
  }
  
  removeAssetValueFormITwo(i:number){
    if(this.assetCardDetailsFI.length>i){
      this.assetCardDetailsFI.splice(i,1);
    }
  }

}
