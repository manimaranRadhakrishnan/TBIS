export interface CustomerContact {
    contactId:number|0;
	customerId:number|0;
	contactPersonName:string;
	contactPersonDesignation:string;
	contactEmail:string;
	contactPhone:string;
	contactMobile:string;
	isActive:boolean|true;
	userId:number;
  }

  export interface CustomerContracts{
    contractId:number|0;
	customerId:number|0;
	contractNo:string;
	poNo:string;
	startDate:string;
	endDate:string;
	isActive:boolean|true;
	userId:number;
  }

  export interface CustomerDocuments{
    documentId:number|0;
	customerId:number|0;
	documentType:string;
	documentNo:string;
	documentPath:string;
	validFrom:string;
	validTo:string;
	isActive:boolean|0;
	userId:number;
  }

  export interface CustomerMaster{
	customerId:number|0;
	customerErpCode:string;
	customerName:string;
	address:string;
	address2?:string;
	city:string;
	district:string;
	state:string;
	phone:string;
	mobile:string;
	email:string;
	gstIn:string;
	lat:number|0;
	lang:number|0;
	barCodeConfigId:number|0;
	bankAccountNo:string;
	ifscCode:string;
	bankName:string;
	contractStartDate:string|'2024-01-01';
	contractEndDate:string|'2024-01-01';
	primarySubLocationId:number|0;
	kycVerified:boolean|true;
	approveBy:number|0;
	approvedDate:string;
	isActive:boolean|true;
	userId:number|0;
	tatinMin:number|0;
    supplymaxRotation:number|0;
  }

  export interface CustomerDDL {
	customerId:number|0;
	customername:string;
  }


  export interface CustomerPartsLineMap{
    customerPartId:number|0;
	customerId:number|0;
	partId:number|0;
	customerPartCode:string;
	barCodeRequired:boolean|true;
	lineSpaceId:number|0;
	lineLotId:number|0;
	lineRackId:number|0;
	unLoadDocId:number|0;
	isActive:Boolean|true;
	userId:number|0;
  }
