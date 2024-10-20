import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ReportFilterBase } from './reportfilter-base';

@Injectable()
export class ReportFilterControlService {
  constructor() { }

  toFormGroup(filters: ReportFilterBase<string>[] ) {
    const group: any = {};

    filters.forEach(filter => {
      if(filter.controlType=="autocomplete1"){
         
      }else{
        group[filter.key] = filter.required ? new FormControl(filter.value || '', Validators.required)
        : new FormControl(filter.value || '');
      }
      
    });
    return new FormGroup(group);
  }
}

