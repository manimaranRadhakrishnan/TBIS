import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.uat';
import { AppGlobal } from '../services/appglobal.service';
import { AppConfig } from '../services/appconfig.service';

const API_KEY = environment.apiaccessKey;
@Injectable({
  providedIn: 'root'
})


export class MastersService {
   
    AUTH_API:string="";
    httpKeyOption:any={};
    constructor(private appConfig: AppConfig, private gVaraible: AppGlobal, private http: HttpClient) {
      //console.log(gVaraible.accesstoken);
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
    // Location Master
    getLocationById(locationId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'location?code='+locationId, this.httpKeyOption);
    }
    updateLocation(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'location', obj, this.httpKeyOption);
    }


    getWmsConfigById(configId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/master?code='+configId, this.httpKeyOption);
    }
    updateWmsConfiguration(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/master', obj, this.httpKeyOption);
    }
    // Color Code Master not mapped
    getWmsColorCodeById(typeid:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/color?code='+typeid, this.httpKeyOption);
    }
    updateWmsColorCode(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/color', obj, this.httpKeyOption);
    }
    getCardMasterById(empId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/card?code='+empId, this.httpKeyOption);
    }
    updateCardMaster(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/card', obj, this.httpKeyOption);
    }
    getEmailTemplateById(templateId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/mailtemplate?code='+templateId, this.httpKeyOption);
    }

    updateMailTemplate(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'config/mailtemplate', obj, this.httpKeyOption);
    }

    // part master Master
    getPartMasterById(id:string): Observable<any> {
     return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/master?code='+id, this.httpKeyOption);
    }
    updatePartMaster(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/master', obj, this.httpKeyOption);
    }

    updatePartAssembly(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/assembly', obj, this.httpKeyOption);
    }

    updatePartDeliveryLocation(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/partdeliverylocation', obj, this.httpKeyOption);
    }

      //Packing Types
      getPackingTypesById(packingTypeId:string): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/packing?code='+packingTypeId, this.httpKeyOption);
      }
      updatePackingTypes(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/packing', obj, this.httpKeyOption);
      }
     //Part Kit Master getPartKitMasterById
     getPartKitMasterById(partKitMasterId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/kit?code='+partKitMasterId, this.httpKeyOption);
    }
    updatePartKitMaster(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'parts/kit', obj, this.httpKeyOption);
    }

  
     //warehouse
     getWarehouseById(warehousId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/master?code='+warehousId, this.httpKeyOption);
    }
    updateWarehouse(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/master', obj, this.httpKeyOption);
    }
    
    //sub location
    getSubLocationById(subLocationId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/sublocation?code='+subLocationId, this.httpKeyOption);
    }
    getSubLocationSpace(subLocationId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/sublocationspace?code='+subLocationId, this.httpKeyOption);
    }
    updateSubLocation(obj:any): Observable<any> {   
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/sublocation', obj, this.httpKeyOption);
    }
    updateSpaceAllocation(obj:any): Observable<any> {   
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/sublocationspace', obj, this.httpKeyOption);
    }
    checkSpaceAllocation(obj:any): Observable<any> {   
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/checksublocationspace', obj, this.httpKeyOption);
    }
    removeSpaceAllocation(obj:any): Observable<any> {   
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/removesublocationspace', obj, this.httpKeyOption);
    }
    // linerack Master
    getLineSpaceById(typeid:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/linespace?code='+typeid, this.httpKeyOption);
    }
    updateLineSpace(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/linespace', obj, this.httpKeyOption);
    }
    // linerack Master
    getLineRackById(id:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/rack?code='+id, this.httpKeyOption);
    }
    updateLineRack(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/rack', obj, this.httpKeyOption);
    }

    getDeliveryLocationById(dlocationId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/deliverylocation?code='+dlocationId, this.httpKeyOption);
    }
    updateDeliveryLocation(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/deliverylocation', obj, this.httpKeyOption);
    }

    //unload doc master
    getUnloadDocById(udcId:string): Observable<any> {
     
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/uldock?code='+udcId, this.httpKeyOption);
    }
    updateUnloadDocMaster(obj:any): Observable<any> {
      
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/uldock', obj, this.httpKeyOption);
    }

    // Customermaster
    getCustomerById(customerId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/master?code='+customerId, this.httpKeyOption);
    }
    updateCustomer(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/master', obj, this.httpKeyOption);
    }
    

    //Customer Part Line Map
    getCustomerPartsLineMapById(customerPartId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/partsmap?code='+customerPartId, this.httpKeyOption);
    }
    updateCustomerPartsLineMap(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/partsmap', obj, this.httpKeyOption);
    }

    updatePartsLineMap(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/spacemap', obj, this.httpKeyOption);
    }

    //customer line rack
    updateCustomerLineRackMap(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/rackmap', obj, this.httpKeyOption);
    }

    //ASN Master
    getASNMasterById(asnId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'asnmaster?code='+asnId, this.httpKeyOption);
    }
    
    getAjaxDropDown(ajaxId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'ajax?ro=true&ic=0&id='+ajaxId, this.httpKeyOption);
    }

    getDocumentTypeById(documentTypeId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'documentType?code='+documentTypeId, this.httpKeyOption);
    }
    updateDocumentType(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'documentType', obj, this.httpKeyOption);
    }
   // Transporter Master
    getTranporterById(locationId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transporter/master?code='+locationId, this.httpKeyOption);
    }
    updateTranporter(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transporter/master', obj,this.httpKeyOption);
    }

    // Plants Master
    getPlantsById(plantId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/plants?code='+plantId, this.httpKeyOption);
    }
    updatePlants(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/plants', obj,this.httpKeyOption);
    }
    // Plant Docks Master
      getPlantDocksById(plantDockId:string): Observable<any> {
        return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/plantdocks?code='+plantDockId, this.httpKeyOption);
      }
      updatePlantDocks(obj:any): Observable<any> {
        return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'warehouse/plantdocks', obj,this.httpKeyOption);
    }
    

    //Customer Software
    getCustomerSoftwaresById(softwareId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/software?code='+softwareId, this.httpKeyOption);
    }
    updateCustomerSoftwares(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/software', obj, this.httpKeyOption);
    }

    //Customer Manpower
    getCustomerManpowersById(manpowerId:string): Observable<any> {
      return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/customermanpower?code='+manpowerId, this.httpKeyOption);
    }
    updateCustomerManpowers(obj:any): Observable<any> { 
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/customermanpower', obj, this.httpKeyOption);
    }

  // Transporter Vehicle
  getTransportVehicleById(vehicleId:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transporter/transportvehicle?code='+vehicleId, this.httpKeyOption);
  }
  updateTransportVehicle(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transporter/transportvehicle', obj,this.httpKeyOption);
  }
  
  // Vehicle Type Master
  getVehicleTypeMasterById(vehicleTypeId:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transporter/vehicletypemaster?code='+vehicleTypeId, this.httpKeyOption);
  }
  updateVehicleTypeMaster(obj:any): Observable<any> {
      return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'transporter/vehicletypemaster', obj,this.httpKeyOption);
  }
  getCustomerSubLocationSpace(customerId:string,subLocationId:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/customersublocationspace?sublocationid='+subLocationId+'&customerid='+customerId, this.httpKeyOption);
  }
  updateCustomerSpaceAllocation(obj:any): Observable<any> {   
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/updatecustomerspace', obj, this.httpKeyOption);
  }
  removeCustomerSpaceAllocation(obj:any): Observable<any> {   
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/removecustomerspace', obj, this.httpKeyOption);
  }

  //check customer existing space
  checkCustomerSpaceAllocation(obj:any): Observable<any> {   
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'customer/checkallocationspace', obj, this.httpKeyOption);
  }

  // ESOP Master
  getEsopMasterById(esopId:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'managelearn/sopdetail?code='+esopId, this.httpKeyOption);
  }
  updateEsopMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'managelearn/sopmaster', obj, this.httpKeyOption);
  }

  // ESOP TOC
  getEsopTocById(esopTocId:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'managelearn/tocdetail?code='+esopTocId, this.httpKeyOption);
  }
  updateEsopToc(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'managelearn/tocmaster', obj, this.httpKeyOption);
  }

  //Designation
  getDesignationById(id:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/designation?code='+id, this.httpKeyOption);
  }
  updateDesignationMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/designation', obj, this.httpKeyOption);
  }
  //Department
  getDepartmentById(id:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/department?code='+id, this.httpKeyOption);
  }
  updateDepartmentMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/department', obj, this.httpKeyOption);
  }
  //Application Type
  getApplicationTypeById(id:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/applicationtype?code='+id, this.httpKeyOption);
  }
  updateApplicationTypeMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/applicationtype', obj, this.httpKeyOption);
  }
  //Asset Category
  getAssetCategoryById(id:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetcategory?code='+id, this.httpKeyOption);
  }
  updateAssetCategoryMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetcategory', obj, this.httpKeyOption);
  }
  //Asset Category Budget
  getAssetCategoryBudgetById(id:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetcategorybudget?code='+id, this.httpKeyOption);
  }
  updateAssetCategoryBudgetMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetcategorybudget', obj, this.httpKeyOption);
  }
  //Asset Sub-Category
  getAssetSubCategoryById(id:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetsubcategory?code='+id, this.httpKeyOption);
  }
  updateAssetSubCategoryMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetsubcategory', obj, this.httpKeyOption);
  }
  //Asset Sub-Category Budget
  getAssetSubCategoryBudgetById(id:string): Observable<any> {
    return this.http.get(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetsubcategorybudget?code='+id, this.httpKeyOption);
  }
  updateAssetSubCategoryBudgetMaster(obj:any): Observable<any> {
    return this.http.post(this.appConfig.apiBaseUrl+this.appConfig.apiPath + 'masters/assetsubcategorybudget', obj, this.httpKeyOption);
  }
}