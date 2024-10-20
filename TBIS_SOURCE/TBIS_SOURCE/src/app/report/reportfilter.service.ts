import { Injectable } from '@angular/core';

import { Dropdown } from './filter-dropdown';
import { ReportFilterBase } from './reportfilter-base';
import { Textbox } from './filter-textbox';
import { of } from 'rxjs';

@Injectable()
export class ReportfilterService {

  // TODO: get from a remote source of question metadata
  getFilters() {

    const filters: ReportFilterBase<string>[] = [

      new Dropdown({
        key: 'brave',
        label: 'Bravery Rating',
        options: [
          {key: 'solid',  value: 'Solid'},
          {key: 'great',  value: 'Great'},
          {key: 'good',   value: 'Good'},
          {key: 'unproven', value: 'Unproven'}
        ],
        order: 3
      }),

      new Textbox({
        key: 'firstName',
        label: 'First name',
        value: 'Bombasto',
        required: true,
        order: 1
      }),

      new Textbox({
        key: 'emailAddress',
        label: 'Email',
        type: 'email',
        order: 2
      }),
      new Textbox({
        key: 'emailAddress1',
        label: 'Eadfdfmail',
        type: 'email',
        order: 2
      })
    ];

    return of(filters.sort((a, b) => a.order - b.order));
  }
}


/*
Copyright Google LLC. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at https://angular.io/license
*/