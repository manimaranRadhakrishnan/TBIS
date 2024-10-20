import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AppGlobal } from '../services/appglobal.service';
import { AppConfig } from '../services/appconfig.service';

const API_KEY = environment.apiaccessKey;
@Injectable({
  providedIn: 'root'
})
export class TransactionService {
 
    AUTH_API:string="";
    httpKeyOption:any={};

    constructor(private appConfig: AppConfig, private gVar: AppGlobal, private http: HttpClient) {
      this.AUTH_API=this.appConfig.apiBaseUrl+this.appConfig.apiPath; 
     }
     
     getHeader(){
      this.httpKeyOption = {
           headers: new HttpHeaders({
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*',
              'X-AppName': 'TBISApp',
              'accept':'*/*',
              'accessKey': API_KEY,
              'X-Frame-Options':"DENY",
              'Authorization':"Bearer "+this.gVar.accesstoken
           })
       };
       return this.httpKeyOption;
   }

     getAjaxDropDown(ajaxId:string): Observable<any> {
          return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'ajax?ro=true&ic=0&id='+ajaxId, this.getHeader());
    }

    getVechicleEntryById(transId:string): Observable<any> {
      
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'vehicleintrance?code='+transId, this.getHeader());
    }

    updateVechicleEntry(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'vehicleintrance', obj, this.getHeader());
    }
    updateVechicleEntryScan(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/scan', obj, this.getHeader());
    }
    updateVechicleEntryCardScan(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/scancard', obj, this.getHeader());
    }
    deleteVechicleEntryCardScan(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/deletescancard', obj, this.getHeader());
    }
    updateAsn(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/asn', obj, this.getHeader());
    }
    updateAsnEntry(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster', obj, this.getHeader());
    }
    updateGateEntryStatus(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/gateentry', obj, this.getHeader());
    }
    getScanDetailsById(transId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/scan?code='+transId, this.getHeader());
    }
    getPartRequestById(asnid:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster?code='+asnid, this.getHeader());
    }

    getASNBinTags(asnid:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/asnbins?code='+asnid, this.getHeader());
    }

    getAsnInfotById(asnid:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/asninfo?code='+asnid, this.getHeader());
    }

    getAsnBarcode(asnid:string): Observable<any> {

      const httpKeyOption1 = {
        headers: new HttpHeaders({
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            'X-AppName': 'TBISApp',
            'accept':'*/*',
            'X-Frame-Options':"DENY",
            'Authorization':"Bearer "+this.gVar.accesstoken
              }),
        responseType:'arraybuffer' as any
    }
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/generateqrcode?id='+asnid, httpKeyOption1);
    }


     updateRequestedPart(obj:any): Observable<any> {
     
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster', obj, this.getHeader());
      }
      getAsnDetailByScanData(asnid:string,scandata:string): Observable<any> {
        let obj:any={
          asnId:asnid,
          asnNo:scandata
        }
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/asndetail', obj, this.getHeader());
      }
      getGinCardDetailByScanData(asnid:string,scandata:string): Observable<any> {
        let obj:any={
          asnId:asnid,
          scanData:scandata
        }
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/asnscancard', obj, this.getHeader());
      }
      getGinScannedCardDetail(asnid:string): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/asngindetail?code='+asnid,  this.getHeader());
      }
      getstockMovementCardDetailByScanData(processId:number,scandata:string,partId:number,initialScan:number,customerId:number): Observable<any> {
        let obj:any={
          processId:processId,
          scanData:scandata,
          partId:partId,
          openingHoldQty:initialScan,
          vendorId:customerId
        }
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/stockmovementscan', obj, this.getHeader());
      }
      updateAsnDispatch(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/dispatch', obj, this.getHeader());
      }

      updateAsnGinReceipt(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/updateginreceipt', obj, this.getHeader());
      }
      updateStockMovement(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/updatestockmovement', obj, this.getHeader());
      }
      getPickListBarcode(stockid:string): Observable<any> {
        const httpKeyOption1 = {
          headers: new HttpHeaders({
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*',
              'X-AppName': 'TBISApp',
              'accept':'*/*',
              'X-Frame-Options':"DENY",
              'Authorization':"Bearer "+this.gVar.accesstoken
                }),
          responseType:'arraybuffer' as any
      }
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/generatestockqrcode?id='+stockid, httpKeyOption1);
      }  
      updateAsnAcknoledgement(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/acknowledge', obj, this.getHeader());
      }

      getAsnDispatchDetailById(asnid:string): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/asndispatchdetail?code='+asnid,  this.getHeader());
      }
      updateAsnReceipt(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/receipt', obj, this.getHeader());
      }
      getDispatchCardScan(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/scancarddispatch', obj, this.getHeader());
      }
      
      updateGateEntry(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/gateentrystatus', obj, this.getHeader());
      }

      updateInsidentMaster(obj: any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transaction/asnincident', obj, this.getHeader());
      }

      upload(file:any):Observable<any> { 
        const formData = new FormData();  
        formData.append("file", file, file.name); 
        this.httpKeyOption = {
          headers: new HttpHeaders({
             'Access-Control-Allow-Origin': '*',
             'X-AppName': 'TBISApp',
             'accept':'*/*',
             'accessKey': API_KEY,
             'X-Frame-Options':"DENY",
             'Authorization':"Bearer "+this.gVar.accesstoken
          })
      };
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/uploadpicklist', formData,this.httpKeyOption); 
      } 


      addPickListPart(obj:any): Observable<any> {
     
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/addpart', obj, this.getHeader());
      }
      addVehiclePickListPart(obj:any): Observable<any> {
     
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/addvehiclepart', obj, this.getHeader());
      }
      addVehicleUpdate(obj:any): Observable<any> {
     
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/shorthaulvehicle', obj, this.getHeader());
      }
      addInvoiceUpdate(obj:any): Observable<any> {
     
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/invoicedetail', obj, this.getHeader());
      }
      addPcikLists(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/addpicklists', obj, this.getHeader());
      }
      getPickListById(picklistid:string): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist?code='+picklistid, this.getHeader());
      }
      updateAmendPickList(picklistid:string): Observable<any> {
        let obj:any={
          pickListId:picklistid,
        }
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/updateamend', obj, this.getHeader());
      }
      updatePickListEntry(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist', obj, this.getHeader());
      }
      updatePickListStatus(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/updateprocessstatus', obj, this.getHeader());
      }

      getAsnPartStockMovementAllocation(asnDetailId:number,partId:number,usageId:number): Observable<any> {
        let obj:any={
          asnDetailId:asnDetailId,
          partId:partId,
          lineUsageId:usageId
        }
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/spaceallocation', obj, this.getHeader());
      }  
      updateAsnStockMovementEntry(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/updateasnstockmovement', obj, this.getHeader());
      }
      getAsnPalletPartStockMovementAllocation(asnId:number,usageId:number): Observable<any> {
        let obj:any={
          asnId:asnId,
          lineUsageId:usageId
        }
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/palletspaceallocation', obj, this.getHeader());
      }
      updateAsnPalletStockMovementEntry(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/updatepalletstockmovemnetdetail', obj, this.getHeader());
      }

      getAsnPartStockMovementSpaceAllocation(asnDetailId:number,partId:number,usageId:number,outwardBin:number): Observable<any> {
        let obj:any={
          asnDetailId:asnDetailId,
          partId:partId,
          lineUsageId:usageId,
          asnBinQty:outwardBin
        }
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/stockmovementspaceallocation', obj, this.getHeader());
      }
      getFifoDetail(customerid:any,usagetypeid:any): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/fifoboard?customerid='+customerid+'&usageid='+usagetypeid, this.getHeader());
      }

      uploadGateEntry(file:any):Observable<any> { 
  
        const formData = new FormData();  
        formData.append("file", file, file.name); 
        this.httpKeyOption = {
          headers: new HttpHeaders({
             'Access-Control-Allow-Origin': '*',
             'X-AppName': 'TBISApp',
             'accept':'*/*',
             'accessKey': API_KEY,
             'X-Frame-Options':"DENY",
             'Authorization':"Bearer "+this.gVar.accesstoken
          })
      };
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/uploadgateentry', formData,this.httpKeyOption); 
      } 
      
      uploadSrv(file:any):Observable<any> { 
  
        const formData = new FormData();  
        formData.append("file", file, file.name); 
        this.httpKeyOption = {
          headers: new HttpHeaders({
             'Access-Control-Allow-Origin': '*',
             'X-AppName': 'TBISApp',
             'accept':'*/*',
             'accessKey': API_KEY,
             'X-Frame-Options':"DENY",
             'Authorization':"Bearer "+this.gVar.accesstoken
          })
      };
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/uploadsrvgenerated', formData,this.httpKeyOption); 
      } 

      uploadInvUpload(file:any):Observable<any> { 
  
        const formData = new FormData();  
        formData.append("file", file, file.name); 
        this.httpKeyOption = {
          headers: new HttpHeaders({
             'Access-Control-Allow-Origin': '*',
             'X-AppName': 'TBISApp',
             'accept':'*/*',
             'accessKey': API_KEY,
             'X-Frame-Options':"DENY",
             'Authorization':"Bearer "+this.gVar.accesstoken
          })
      };
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/uploadinvoice', formData,this.httpKeyOption); 
      } 
      updateAsnPartsAdd(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/updateasnpartsadd',obj, this.getHeader());
      }
      
      addInvoiceLoadingUpdate(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/loadingupdate', obj, this.getHeader());
      }
      
      checkAbnormalPalletScan(asnId:string): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/checkinwardpallet?asnId='+asnId, this.getHeader());
      }
      
      
      uploadAsnPart(file:any):Observable<any> { 
        const formData = new FormData();  
        formData.append("file", file, file.name); 
        this.httpKeyOption = {
          headers: new HttpHeaders({
             'Access-Control-Allow-Origin': '*',
             'X-AppName': 'TBISApp',
             'accept':'*/*',
             'accessKey': API_KEY,
             'X-Frame-Options':"DENY",
             'Authorization':"Bearer "+this.gVar.accesstoken
          })
        };
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/uploadasnparts', formData,this.httpKeyOption); 
      } 

      uploadAsnInbound(file:any):Observable<any> { 
        const formData = new FormData();  
        formData.append("file", file, file.name); 
        this.httpKeyOption = {
          headers: new HttpHeaders({
             'Access-Control-Allow-Origin': '*',
             'X-AppName': 'TBISApp',
             'accept':'*/*',
             'accessKey': API_KEY,
             'X-Frame-Options':"DENY",
             'Authorization':"Bearer "+this.gVar.accesstoken
          })
        }; 
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/uploadinbounddata', formData,this.httpKeyOption); 
      } 

      assignInboundVehicleUpdate(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster/inboundvehicleassign', obj, this.getHeader());
      }
      
      // Spare upload
      spareUpload(file:any):Observable<any> { 
        const formData = new FormData();  
        formData.append("file", file, file.name); 
        this.httpKeyOption = {
          headers: new HttpHeaders({
             'Access-Control-Allow-Origin': '*',
             'X-AppName': 'TBISApp',
             'accept':'*/*',
             'accessKey': API_KEY,
             'X-Frame-Options':"DENY",
             'Authorization':"Bearer "+this.gVar.accesstoken
          })
      };
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'picklist/uploadspares', formData,this.httpKeyOption); 
      } 
  
}
