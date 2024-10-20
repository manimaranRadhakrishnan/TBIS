import {Directive, ElementRef, HostListener ,Input} from '@angular/core';

@Directive({
  selector: "[numeric]"
})

export class NumericDirective {
  @Input("decimals") decimals: any = 0;
  @Input("negative") negative: any = 0;

  private checkAllowNegative(value: string) {
    if (this.decimals <= 0) {
      return RegExp(new RegExp(/^-?\d+$/)).exec(String(value));
    } else {
      let regExpString = "^-?\\s*((\\d+(\\.\\d{0," +
        this.decimals +
        "})?)|((\\d*(\\.\\d{1," +
        this.decimals +
        "}))))\\s*$";
      return RegExp(new RegExp(regExpString)).exec(String(value));
    }
  }

  private check(value: string) {
    if (this.decimals <= 0) {
      return RegExp(new RegExp(/^\d+$/)).exec(String(value));
    } else {
      let regExpString = "^\\s*((\\d+(\\.\\d{0," +
        this.decimals +
        "})?)|((\\d*(\\.\\d{1," +
        this.decimals +
        "}))))\\s*$";
      return RegExp(new RegExp(regExpString)).exec(String(value));
    }
  }

  private run(oldValue: string) {
    setTimeout(() => {
      let currentValue: string = this.el.nativeElement.value;
      let allowNegative = this.negative > 0;

      if (allowNegative) {
        if (!["", "-"].includes(currentValue) &&
          !this.checkAllowNegative(currentValue)) {
          this.el.nativeElement.value = oldValue;
        }
      } else if (currentValue !== "" && !this.check(currentValue)) {
          this.el.nativeElement.value = oldValue;
        }
    });
  }

  constructor(private el: ElementRef) { }

  @HostListener("keydown", ["$event"])
  onKeyDown(event: KeyboardEvent) {
    this.run(this.el.nativeElement.value);
  }

  @HostListener("paste", ["$event"])
  onPaste(event: ClipboardEvent) {
    this.run(this.el.nativeElement.value);
  }
}
