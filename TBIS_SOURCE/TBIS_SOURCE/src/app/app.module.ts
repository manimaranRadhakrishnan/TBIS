import { NgModule, CUSTOM_ELEMENTS_SCHEMA, APP_INITIALIZER, NO_ERRORS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LocationStrategy,CommonModule, PathLocationStrategy } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';


//Services
import { LoaderInterceptor } from './services/loader.interceptor';
import { LoaderService } from './services/loader.service';
import { LoginService } from './services/login.service';
import { AdminGuard } from './admin/admin.guard';
import { NavService } from './services/nav.service';

//Component
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import {LogoutComponent} from './logout/logout.component';

//Module
import { ConfirmDialogModule } from './components/confirm-dialog/confirm-dialog.module';
import { CountdownModule } from 'ngx-countdown';

import { BodyComponent } from './body/body.component';
import { SidenavComponent } from './sidenav/sidenav.component';
import {MatTableModule} from '@angular/material/table';
import { NgSelectConfig, NgSelectModule } from '@ng-select/ng-select';
//Directive
import { NumericValidatorDirective } from './directives/numeric.directive';
import { NgSlimScrollModule, SLIMSCROLL_DEFAULTS } from 'ngx-slimscroll';
import { AppConfig } from './services/appconfig.service';
import { ReportComponent } from './report/report.component';
import { ReportFilterControlService } from './report/reportfilter-control.service';
import { SublevelMenuComponent } from './sidenav/sublevel-menu.component';
import { LocationComponent } from './features/masters/location/location.component';
import { PartmasterComponent } from './features/masters/partmaster/partmaster.component';
import { PackingtypesComponent } from './features/masters/packingtypes/packingtypes.component';
import { WarehouseComponent } from './features/masters/warehouse/warehouse.component';
import { CustomerComponent } from './features/masters/customer/customer.component';
import { EmployeemasterComponent } from './features/masters/employeemaster/employeemaster.component';
import { MatSortModule } from '@angular/material/sort';
import { RolesComponent } from './features/roles/roles.component';
import { PartrequestComponent } from './features/transactions/partrequest/partrequest.component';
import { GateentryComponent } from './features/transactions/gateentry/gateentry.component';
import { DispatchComponent } from './features/transactions/dispatch/dispatch.component';
import { ReceiptsComponent } from './features/transactions/receipts/receipts.component';
import { UsersComponent } from './features/masters/users/users.component';
import { PaginationModule } from './components/app-pagination/app-pagination.module';
import { ToastrModule } from 'ngx-toastr';
import { NgxEditorModule } from 'ngx-editor';
import { MailtemplateComponent } from './features/masters/mailtemplate/mailtemplate.component';
import { AcknowledgementComponent } from './features/transactions/acknowledgement/acknowledgement.component';
import { CardsComponent } from './features/masters/cards/cards.component';
import { WmsconfigurationComponent } from './features/masters/wmsconfiguration/wmsconfiguration.component';
import { ProfilepageComponent } from './features/masters/profilepage/profilepage.component';
import { TransporterComponent } from './features/masters/transporter/transporter.component';
import { MaterialModule } from './material-module';
import { VendorComponent } from './features/masters/vendor/vendor.component';
import { LinespaceComponent } from './features/masters/linespace/linespace.component';
import { SublocationComponent } from './features/masters/sublocation/sublocation.component';
import { LinerackComponent } from './features/masters/linerack/linerack.component';
import { UnloaddocmasterComponent } from './features/masters/unloaddocmaster/unloaddocmaster.component';
import { DeliverylocationComponent } from './features/masters/deliverylocation/deliverylocation.component';
import { ColorCodeComponent } from './features/masters/colorcode/colorcode.component';
import { AsnentryComponent } from './features/transactions/asnentry/asnentry.component';
import { PicklistdispatchComponent } from './features/transactions/picklistdispatch/picklistdispatch.component';
import { PicklistentryComponent } from './features/transactions/picklistentry/picklistentry.component';
import { StockmovementComponent } from './features/transactions/stockmovement/stockmovement.component';
import { GinentryComponent } from './features/transactions/ginentry/ginentry.component';
import { MergeasnComponent } from './features/transactions/mergeasn/mergeasn.component';
import { StockledgerComponent } from './features/transactions/stockledger/stockledger.component';
import { AsngateentyComponent } from './features/transactions/asngateenty/asngateenty.component';
import { AsndispatchComponent } from './features/transactions/asndispatch/asndispatch.component';
import { TruckrequestplanComponent } from './features/transactions/truckrequestplan/truckrequestplan.component';
import { PlantsComponent } from './features/masters/plants/plants.component';
import { PlantdocksComponent } from './features/masters/plantdocks/plantdocks.component';
import { PicklistUploadComponent } from './features/transactions/picklistupload/picklistupload.component';
import { AsnMovementComponent } from './features/transactions/asnmovement/asnmovement.component';
import { DecimalPlacesDirective } from './directives/decimal.directive';
import { FloatNumberValidatorDirective } from './directives/floatnumber.directive';
import { SideBarValidatorDirective } from './directives/sidebar.directive';
import { ShorthaulComponent } from './features/transactions/shorthaul/shorthaul.component';
import { DockComponent } from './features/largedisplay/dock/dock.component';
import { FifoboardComponent } from './features/largedisplay/fifoboard/fifoboard.component';
import { EsopComponent } from './features/largedisplay/esop/esop.component';
import { ReportdataComponent } from './features/masters/reportdata/reportdata.component';
import { LoadingComponent } from './features/transactions/loading/loading.component';
import { AlphaNumericValidatorDirective } from './directives/alpha-numeric.directive';
import { EsopmasterComponent } from './features/masters/esopmaster/esopmaster.component';
import { EsoptocComponent } from './features/masters/esoptoc/esoptoc.component';
import { AsninboundComponent } from './features/transactions/asninbound/asninbound.component';
import { FormsComponent } from './features/masters/forms/forms.component';
import { CqdesignationComponent } from './features/masters/cqdesignation/cqdesignation.component';
import { CqdepartmentComponent } from './features/masters/cqdepartment/cqdepartment.component';
import { CqapplicationtypeComponent } from './features/masters/cqapplicationtype/cqapplicationtype.component';
import { CqassetcategoryComponent } from './features/masters/cqassetcategory/cqassetcategory.component';
import { CqassetcategorybudgetComponent } from './features/masters/cqassetcategorybudget/cqassetcategorybudget.component';
import { CqassetsubcategoryComponent } from './features/masters/cqassetsubcategory/cqassetsubcategory.component';
import { CqassetsubcategorybudgetComponent } from './features/masters/cqassetsubcategorybudget/cqassetsubcategorybudget.component';


@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ConfirmDialogModule,
    CountdownModule,
    BrowserAnimationsModule,
    PaginationModule,
    NgSlimScrollModule,
    MatTableModule,
    MatSortModule , 
    MatTableModule,
    NgxEditorModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    NgSelectModule,
    MaterialModule,
    
    ToastrModule.forRoot({
      positionClass :'toast-top-center'
    }),
    

    RouterModule.forRoot([
      { path: 'login', component: LoginComponent }, 
      { path: 'home', component: HomeComponent,canActivate: [AdminGuard] }, 
      { path: 'report/:reportid', component: ReportComponent, canActivate: [AdminGuard] },
      { path: 'location', component: LocationComponent,canActivate: [AdminGuard] }, 
      { path: 'cards', component: CardsComponent,canActivate: [AdminGuard] }, 
      { path: 'transport', component: TransporterComponent,canActivate: [AdminGuard] }, 
      { path: 'warehouse', component: WarehouseComponent,canActivate: [AdminGuard] }, 
      { path: 'sublocation', component: SublocationComponent,canActivate: [AdminGuard] },
      { path: 'linespace', component: LinespaceComponent,canActivate: [AdminGuard] },
      { path: 'linerack', component: LinerackComponent,canActivate: [AdminGuard] },
      { path: 'linebays', component: LinespaceComponent,canActivate: [AdminGuard] },
      { path: 'unloaddoc', component: UnloaddocmasterComponent,canActivate: [AdminGuard] },
      { path: 'colorcode', component: ColorCodeComponent,canActivate: [AdminGuard] },
      { path: 'deliverylocation', component: DeliverylocationComponent,canActivate: [AdminGuard] },
      { path: 'employee', component: EmployeemasterComponent,canActivate: [AdminGuard] },  
      { path: 'partmaster', component: PartmasterComponent,canActivate: [AdminGuard] }, 
      { path: 'packingtype', component: PackingtypesComponent,canActivate: [AdminGuard] }, 
      { path: 'customer', component: CustomerComponent,canActivate: [AdminGuard] }, 
      { path: 'vendor', component: VendorComponent,canActivate: [AdminGuard] }, 
      { path: 'roles', component: RolesComponent ,canActivate: [AdminGuard] },
      { path: 'gateentry', component: GateentryComponent,canActivate: [AdminGuard] },
      { path: 'partrequest', component: PartrequestComponent,canActivate: [AdminGuard] },
      { path: 'acknowledgement', component: AcknowledgementComponent,canActivate: [AdminGuard] },
      { path: 'dispatch', component: DispatchComponent,canActivate: [AdminGuard] },
      { path: 'receipts', component: ReceiptsComponent,canActivate: [AdminGuard] },
      { path: 'users', component: UsersComponent,canActivate: [AdminGuard] },
      { path: 'mailtemplate', component: MailtemplateComponent,canActivate: [AdminGuard] },
      { path: 'config', component: WmsconfigurationComponent,canActivate: [AdminGuard] },
      { path: 'profile', component: ProfilepageComponent,canActivate: [AdminGuard] },
      { path: 'logout', component: LogoutComponent,canActivate: [AdminGuard] },
      {path:'asnentry',component:AsnentryComponent,canActivate:[AdminGuard]},
      {path:'asngateentry',component:AsngateentyComponent,canActivate:[AdminGuard]},
      {path:'asndispatch',component:AsndispatchComponent,canActivate:[AdminGuard]},
      {path:'mergeasn',component:MergeasnComponent,canActivate:[AdminGuard]},
      {path:'picklist',component:PicklistentryComponent,canActivate:[AdminGuard]},
      {path:'grndispatch',component:PicklistdispatchComponent,canActivate:[AdminGuard]},
      {path:'stockmovement',component:StockmovementComponent,canActivate:[AdminGuard]},
      {path:'ginentry',component:GinentryComponent,canActivate:[AdminGuard]},
      {path:'stockledger',component:StockledgerComponent,canActivate:[AdminGuard]},
      {path:'truckrequestplan',component:StockledgerComponent,canActivate:[AdminGuard]},
      {path:'plants',component:PlantsComponent,canActivate:[AdminGuard]},
      {path:'plantdocks',component:PlantdocksComponent,canActivate:[AdminGuard]},
      {path:'picklistupload',component:PicklistUploadComponent,canActivate:[AdminGuard]},
      {path:'asnmovement',component:AsnMovementComponent,canActivate:[AdminGuard]},
      {path:'shorthaul',component:ShorthaulComponent,canActivate:[AdminGuard]},
      {path:'displaydock',component:DockComponent,canActivate:[AdminGuard]},
      {path:'displayfifo',component:FifoboardComponent,canActivate:[AdminGuard]},
      {path:'displayesop',component:EsopComponent,canActivate:[AdminGuard]},
      { path: 'reportdata/:reportid', component: ReportdataComponent, canActivate: [AdminGuard] },
      {path:'loading',component:LoadingComponent,canActivate:[AdminGuard]},
      {path:'esopmaster',component:EsopmasterComponent,canActivate:[AdminGuard]},
      {path:'tocmaster',component:EsoptocComponent,canActivate:[AdminGuard]},
      {path:'asninbound',component:AsninboundComponent,canActivate:[AdminGuard]},
      {path:'formsentry/:formid',component:FormsComponent,canActivate:[AdminGuard]},
      {path:'designation',component:CqdesignationComponent,canActivate:[AdminGuard]},
      {path:'department',component:CqdepartmentComponent,canActivate:[AdminGuard]},
      {path:'applicationtype',component:CqapplicationtypeComponent,canActivate:[AdminGuard]},
      {path:'assetcategory',component:CqassetcategoryComponent,canActivate:[AdminGuard]},
      {path:'assetcategorybudget',component:CqassetcategorybudgetComponent,canActivate:[AdminGuard]},
      {path:'assetsubcategory',component:CqassetsubcategoryComponent,canActivate:[AdminGuard]},
      {path:'assetsubcategorybudget',component:CqassetsubcategorybudgetComponent,canActivate:[AdminGuard]},
      {path: '', redirectTo: 'login', pathMatch: 'full' }  ,
      ],{onSameUrlNavigation: 'reload'}),
    ],
  
    declarations: [
      NumericValidatorDirective,
      DecimalPlacesDirective,
      FloatNumberValidatorDirective,
      SideBarValidatorDirective,
      AlphaNumericValidatorDirective,
        AppComponent,
        AcknowledgementComponent,
        AsnentryComponent,
        AsngateentyComponent,
        AsndispatchComponent,
        BodyComponent,
        CardsComponent,
        CustomerComponent,
        ColorCodeComponent,
        DispatchComponent,
        DeliverylocationComponent,
        EmployeemasterComponent,
        GateentryComponent,  
        GinentryComponent,    
        HomeComponent,
        LoginComponent,
        LogoutComponent,
        LocationComponent,
        LinespaceComponent,
        LinerackComponent,
        MailtemplateComponent,
        MergeasnComponent,
        PackingtypesComponent,
        PartmasterComponent,
        PartrequestComponent,
        ProfilepageComponent,
        PicklistentryComponent,
        PicklistdispatchComponent,
        PlantdocksComponent,
        PlantsComponent,
        PicklistUploadComponent,
        ReportComponent,
        ReceiptsComponent,
        RolesComponent,
        SidenavComponent,
        SublevelMenuComponent,
        SublocationComponent,
        StockmovementComponent,
        StockledgerComponent,
        TransporterComponent,
        TruckrequestplanComponent,
        UsersComponent,
        UnloaddocmasterComponent,        
        VendorComponent,
        WarehouseComponent,
        WmsconfigurationComponent,
        AsnMovementComponent,
        ShorthaulComponent,
        DockComponent,
        FifoboardComponent,
        EsopComponent,
        ReportdataComponent,
        LoadingComponent,
        EsoptocComponent,
        EsopmasterComponent,
        AsninboundComponent,
        FormsComponent,
        CqdesignationComponent,
        CqdepartmentComponent,
        CqapplicationtypeComponent,
        CqassetcategoryComponent,
        CqassetcategorybudgetComponent,
        CqassetsubcategoryComponent,
        CqassetsubcategorybudgetComponent
    ],
  bootstrap: [
    AppComponent,
    ], schemas: [CUSTOM_ELEMENTS_SCHEMA,NO_ERRORS_SCHEMA],
    providers: [{ provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptor, multi: true }, 
      AdminGuard, LoaderService,LoginService,NavService,ReportFilterControlService,
      { provide: SLIMSCROLL_DEFAULTS,useValue: { alwaysVisible : false }  },
      { provide: LocationStrategy, useClass: PathLocationStrategy },
      {
        provide: APP_INITIALIZER,
        multi: true,
        deps: [AppConfig],
        useFactory: (appConfigService: AppConfig) => {
          return () => {
            return appConfigService.loadAppConfig();
          };
        }
      },]
})
export class AppModule {
  
constructor(private config: NgSelectConfig) {
  this.config.notFoundText = 'Not Found';
  this.config.bindValue = 'value';
}
}


