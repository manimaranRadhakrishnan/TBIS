import { ReportFilterBase } from './reportfilter-base';

export class AutoComplete extends ReportFilterBase<string> {
  override controlType = 'autocomplete';
}
 
 