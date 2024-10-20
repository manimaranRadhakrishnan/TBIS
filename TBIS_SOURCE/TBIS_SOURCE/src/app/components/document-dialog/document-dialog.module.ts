import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {DocumentDialogComponent} from './document-dialog.component';
import {DocumentDialogService} from './document-dialog.service';
// import { NgxDocViewerModule } from 'ngx-doc-viewer';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
// import { PdfViewerModule } from 'ng2-pdf-viewer';


@NgModule({
    declarations: [
        DocumentDialogComponent
    ],
    imports: [
        BrowserModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        NgxExtendedPdfViewerModule,
    ],
    exports: [
        DocumentDialogComponent
    ],
    providers: [
       DocumentDialogService
    ]
})
export class DocumentDialogModule
{
}
