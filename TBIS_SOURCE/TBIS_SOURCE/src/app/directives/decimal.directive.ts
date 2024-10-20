import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[decimalPlaces]'
})
export class DecimalPlacesDirective {
  constructor(private el: ElementRef) {}

  @HostListener('input', ['$event']) onInput(event: any) {
    const value = event.target.value;
    const decimalIndex = value.indexOf('.');

    if (decimalIndex !== -1 && value.length - decimalIndex > 4) {
      event.target.value = value.substring(0, decimalIndex + 4);
    }
  }
}