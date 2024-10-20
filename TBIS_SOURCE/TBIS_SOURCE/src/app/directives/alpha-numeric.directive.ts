import { Directive, ElementRef, HostListener } from '@angular/core';


@Directive({
    selector: 'input[alphaNumericOnly]',
    standalone:false,
})
export class AlphaNumericValidatorDirective {
    constructor(private _el: ElementRef) { }

    @HostListener('input', ['$event']) onInputChange(event:any) {
        const initalValue = this._el.nativeElement.value;
        this._el.nativeElement.value = initalValue.replace(/[^0-9A-Za-z]*/g, '');
        if (initalValue !== this._el.nativeElement.value) {
            event.stopPropagation();
        }
    }
}
