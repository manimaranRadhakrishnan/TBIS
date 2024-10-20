import { Directive, ElementRef, HostListener } from '@angular/core';


@Directive({
    selector: 'li[menusidebar],a[menusidebar]',
    standalone:false,
})
export class SideBarValidatorDirective {
    constructor(private _el: ElementRef) { }

    @HostListener('click', ['$event']) onClick(event:any) {
        const button:any = document.querySelector('#sidebarbtn');
        if (button) {
            button.click();
        }
    }
}
