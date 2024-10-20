 
 export interface NavItem {
    displayName: string;
    disabled?: boolean;
    iconName: string;
    route?: string;
    children?: NavItem[];
    menuId?:string;
  }

  export interface FilterItems{
    paramName:string;
    paramValue:string;
  }

  export interface ListValue
  {
      id : string;
      key: string;
      value: string;
  }
  export interface ReportParams{
    paramName:string;
    paramValue:string;
  }
  export interface ReportFilters
  {
      reportFilterCaption:string;
      reportFilterName:string;
      reportFilterType:string;
      datasourceId:number|0;
      filterInputValues?:string|"";
      reportFilterOrder:number|0;
      lovList :ListValue[]
  }
  export interface ReportColumns{
    reportid: number|0;
    columnTitle: string;
    columnWidth: string;
    columnAlign: string;
    columnDataType: string;
    allowToShowinUI: number|0
    allowToExport: number|0
    formatter: string;
    dataSourceColumnName: string;
    menuCaption: string;
    columnLink: string;
    columnLinkParams:string;
    columnParams: string;
  }

  export interface ReportActions{
    name:string;
    type: string;
    action: string;
    paramColumns?:string|""
    transType?: string |""
  }
  export interface ReportMetaData{
    reportActions:ReportActions[];
    reportColumns:ReportColumns[];
    reportSource: string;
    filters: ReportFilters[];
  }

  
export interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}

export interface ISlimScrollOptions {
  position?: 'left' | 'right';
  barBackground?: string;
  barOpacity?: string;
  barWidth?: string;
  barBorderRadius?: string;
  barMargin?: string;
  gridBackground?: string;
  gridOpacity?: string;
  gridWidth?: string;
  gridBorderRadius?: string;
  gridMargin?: string;
  alwaysVisible?: boolean;
  visibleTimeout?: number;
  alwaysPreventDefaultScroll?: boolean;
}


