import { ReportFilterBase } from './reportfilter-base';

export class Textbox extends ReportFilterBase<string> {
  override controlType = 'textbox';
}
 