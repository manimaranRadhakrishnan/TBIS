export interface Location {
    locationName:string;
	locationId:number|0;
	locationShortCode:string;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface Warehouse {
    warehousId:number|0;
	locationId:number|0;
	warehouseName:string;
	warehouseShortCode:string;
	address:string;
	address2?:string;
	city:string;
	district:string;
	state:string;
	phone:string;
	mobile:string;
	email:string;
	gstin:string;
	latitute:number|0;
	longitude:number|0;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface CardDetails {
	cardId:number|0;
	cardName:string;
	cardDataLength:number|0;
	partnoStart:string;
	vendorStart:string;
	userLocationStart:string;
	qtyStart:string;
	slnoStart:string;
	userId:number|0;
  }

  
  export interface EmployeeMasters{
	employeeId:number|0;
	employeeName:string;
	address1:string;
	address2:string;
	city:string;
	email:string;
	mobileNo:string;
	phone:string;
	userName:string;
	password:string;
	roleId:number|0;
	employeeType:number|1;
	employeeStatus:string;
	userId:number|0;
 }

 
 export interface EmployeeRolesDDL {
   role_id:number|0;
   role_name:string;
 }
 

  export interface LineRack {
	lineRackId:number|0;
	lineSpaceId:number|0;
	lineRackCode:string;
	customerStorageArea:number|0;
	goodStockAreaLength:number|0;
	goodStockAreaWidth:number|0;
	noofPartStorageBays:number|0;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface WarehouseDDL {
	warehousid:number|0;
	warehousename:string;
  }

   export interface SubLocationDDL {
	sublocationid:number|0;
	sublocationname:string;
  }

  export interface LocationDDL {
	locationid:number|0;
	locationname:string;
  }


  export interface LineRackDDL {
	linerackid:number|0;
	linerackcode:string;
  }


  export interface LineSpace {
	lineSpaceId:number|0;
	subLocationId:number|0;
	lineSpaceCode:string;
	customerStorageArea:number|0;
	goodStockAreaLength:number|0;
	goodStockAreaWidth:number|0;
	beforeInspectionAreaWidth:number|0;
	beforeInspectionAreaLength:number|0;
	afterInspectionAreaWidth:number|0;
	afterInspectionAreaLength:number|0;
	allowedStackHeight:number|0;
	noofPartStorageBays:number|0;
	isActive:boolean|true;
	userId:number|0
  }

  export interface LineSpaceDDL {
	linespaceid:number|0;
	linespacecode:string;
  }


  export interface PackingTypes {
	packingTypeId:number|0;
	packingName:string;
	packingShortName:string;
	packingHeight:number|0;
	packingWidth:number|0;
	packingLength:number|0;
	packingWeight:number|0;
	M2:number|0;
	M3:number|0;
	isActive:boolean|true;
	userId:number|0;

  }

  export interface PackingTypesDDL {
	packingtypeid:number|0;
	packingname:string;
  }

  export interface PartKitMaster {
	partKitMasterId:number|0;
	partIdEndUser:number|0;
	partIdCustomer:number|0;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface PartMaster {
	partId:number|0;
	partNo:string;
	partDescription:string;
	packingTypeId:number|0;
	spq:number|0;
	noOfStack:number|0;
	noOfPackingInPallet:number|0;
	packingHeight:number|0;
	packingWidth:number|0;
	packingLength:number|0;
	packingWeight:number|0;
	palletHeight:number|0;
	palletWidth:number|0;
	palletLength:number|0;
	palletWeight:number|0;
	dispatchType:number|0;
	loadingType:number|0;
	rePack:number|0;
	m2:number|0;
	m3:number|0;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface PartMasterDDL {
	partid:number|0;
	partno:string;
  }

  export interface InsidentDDL {
	incidentid:number|0;
	incidentname:string;
  }

  export interface PickListDetail {
	pickListiDetailId:Number|0;
	pickListId:Number|0;
	kanBanNo:string;
	partId:number|0;
	supplyDate:string;
	supplyTime:string;
	unLoadingDoc:string;
	usageLocation:string;
	spq:number|0;
	perBinQty:number|0;
	qtyReceived:number|0;
	noOfPackackes:number|0;
	stagIngState:number|0;
	userId:number|0;
  }


  export interface PickListMaster {
	pickListId:number|0;
	scheduleNo:string;
	customerId:number|0;
	supplyDate:string;
	supplyTime:string;
	statusId:number|0;
	userId:number|0;
  }

  export interface PickListMasterDDL {
	picklistid:number|0;
	scheduleno:string;
  }


  export interface PickWorkFlowLog {
	pickListDetailId:number|0;
	pickListWorkFlowId:number|0;
	statusId:number|0;
	workFlowHandleBy:number|0;
	notes:string;
	userId:number|0;
  }

  export interface PickWorkFlowsPpolLog {
	pickListId:number|0;
	vendorCode:string;
	challanNo:string;
	challanDate:string;
	invoiceNo:string;
	invoiceDate:string;
	exciseAmount:string;
	salesTax:string;
	consumableIndigator:string;
	scheduleNo:string;
	itemCode:string;
	qtyShipped:string;
	poNo:string;
	five7fe2No:string;
	perBinQty:string;
	remarks:string;
	batchNo:string;
	gstNo:string;
	hsnCode:string;
	cGst:string;
	sGst:string;
	iGst:string;
	eWayBillNo:string;
	iTnNo:string;
	spoolFilePath:string;
	userId:number|0;
  }


  export interface ReportDtl {
    reportId:number|0;
	columnName:string;
	columnTitle:string;
	columnSum:number|0;
	columnWidth:number|0;
	columnType:string;
	columnAlign:string;
	columnLink:string;
	closingOperator:string;
	closingCondition:string;
	columnOrder:number|0;
	linkParameters:string;
	paramColumns:string;
	columnFormatter:string;
	imagePath:string;
	sumColumnField:string;
	columnFrozen:number|0;
	isOperation:number|0;
	searchColumnField:string;
	pdfColumnWidth:number|0;
	exportColumnOrder:number|0;
	hideOnRepeat:number|0;
	userId:number|0;

  }

  export interface ReportHdr {
	reportId:number|0;
	reportName:string;
	reportTitle:string;
	headerQuery:string;
	openingBalance:number|0;
	openBalQuery:string;
	parameters:string;
	reportQuery:string;
	broughtForward:number|0;
	printClosing:number|0;
	titleForAll:number|0;
	broughtFwdText:string;
	bfColumn:string;
	paramTypes:string;
	defaultSort:string;
	defaultSortOrder:string;
	footerColumns:number|0;
	reportPdfPaper:number|0;
	reportPdfOrient:number|0;
	reportRecordLines:number|0;
	reportKeyColumns:string;
	userId:number|0;
  }

  export interface RolesMaster {
    roleId:number|0;
	roleName:string;
	roleDescription:string;
	isSystem:boolean|true;
	parentId:number|0;
	isActive:number|0;
	userId:number|0;
  }

  export interface RolesModules {
	roleModuleId:number|0;
	roleId:string;
	roleMenuId:string;
	userId:number;
  }

  export interface RoleColumnMap {
    rcmRoleId:number|0;
	rcmReportId:number|0;
	rcmColumnName:string;
	rcmMenuId:number|0;
	dummy:number|0;
	userId:number|0;
  }

  export interface RoleMenuMap {
	roleId:number|0;
	menuId:number|0;
	userId:number|0;
  }

  export interface RoleOperationMap {
	romRoleId:number|0;
	romSoId:number|0;
	romMenuId:number|0;
	userId:number|0;
  }

  export interface SubLocation {
	subLocationId:number|0;
	warehouseId:number|0;
	subLocationName:string;
	subLocationShortCode:string;
	areaInSqFeet:number|0;
	areaInM2:number|0;
	customerStorageArea:number|0;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface TranBillSeqNo {
	branchCode:number|0;
	tranName:string;
	tranId:number|0;
	seqNo:number|0;
	userId:number|0;
  }

  export interface TranSeqNo {
	tranId:number|0;
	tranName:string;
	seqNo:number|0;
	userId:number|0;
  }

  export interface UnloadDocMaster {
	udcId:number|0;
	udcName:string;
	subLocationId:number|0;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface UnloadDocDDL {
	udc_id:number|0;
	udcname:string;
  }


  export interface WmsColorCode {
	colorCodeId:number|0;
	colorName:string;
	isSystem:boolean|true;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface WmsConfiguration {
	configId:number|0;
	configurationName:string;
	configurationValue:string;
	configurationValueType:number|0;
	isSystem:boolean|true;
	userId:number|0;
  }

  export interface WmsDocumentType {
	documentTypeId:number|0;
	documentName:string;
	isSystem:boolean|true;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface WmsIncidentCategoryMaster {
	incidentCategoryId:number|0;
	incidentCategoryName:string;
	isSystem:boolean|true;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface Wms_IncidentMaster {
	incidentId:number|0;
	incidentName:string;
	incidentCatergoryId:number|0;
	isSystem:boolean|true;
	isActive:boolean|true;
	userId:number|0;

  }

  export interface WmsMenus {
	menuId:number|0;
	menuName:string;
	menuParentId:number|0;
	internalOrExternal:number|0;
	menuDisplayOrder:number|0;
	isSystem:boolean|true;
	userId:number|0;
  }

  export interface WmsMenuActions {
	menuActionId:number|0;
	menuId:string;
	actionId:number|0;
	actionAvail:number|0;
	isSystem:boolean|true;
	userId:number|0;
  }

  export interface WmsStatusMaster {
	statusId:number|0;
	statusName:string;
	moduleId:number|0;
	isSystem:boolean|true;
	isActive:boolean|true;
	userId:number|0;
  }

  export interface WmsStatusMasterDDL {
	statusid:number|0;
	statusname:string;
  }
  export interface PackingCategoryDDL{
	packingcategoryid:number|0;
	packingcategoryname:string;
  }

  export interface WmsUserTypes {
	userId:number|0;
	userTypeName:string;
	isSystem:boolean|true;
	isActive:boolean|true;
  }

  export interface AsnMaster {
	asnId:number|0;
	asnNo:string;
	customerId:number|0;
	supplyDate:string;
	supplyTime:string;
	unLoadingDocId:number|0;
	asnStatus:number|0;
	vechicleNo:string;
	driverName:string;
	driverMobile:string;
	ewayBillNo:string;
	invoiceNo:string;
	directorTransit:boolean|true;
	transitasnId:number|0;
	gateInDateTime:number|0;
  }

  