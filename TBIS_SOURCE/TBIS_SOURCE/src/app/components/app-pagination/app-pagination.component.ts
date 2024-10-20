import { Component , Input , OnChanges , Output , EventEmitter} from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './app-pagination.component.html'
})

export class PaginationComponent implements OnChanges {

    @Input() totalRecords = 0;
    @Input() recordsPerPage = 0;

    @Output() onPageChange: EventEmitter<number> = new EventEmitter();
    @Output() onChangeRecPerPage: EventEmitter<number> = new EventEmitter();

    public pages: number [] = [];
    activePage: number=1;
    listcount:number=25;
    pcount:number=0;
    ngOnChanges(): any {
      const pageCount = this.getPageCount();
      this.pcount=pageCount;
      this.pages = this.getArrayOfPage(1,pageCount);
      this.activePage =0;
      this.onPageChange.emit(1);
    }

    private  getPageCount(): number {
      let totalPage = 0;

      if (this.totalRecords > 0 && this.recordsPerPage > 0) {
        const pageCount = this.totalRecords / this.recordsPerPage;
        const roundedPageCount = Math.floor(pageCount);

        totalPage = roundedPageCount < pageCount ? roundedPageCount + 1 : roundedPageCount;
      }

      return totalPage;
    }

    private getArrayOfPage(starting:number,pageCount: number): number [] {
      const pageArray = [];
      var plen=(pageCount-this.activePage);
      if (pageCount > 0) {

          for(let i = 0 ; i < (plen>5?5:plen==0?1:plen) ; i++) {
            pageArray.push(i+starting);
          }
      }

      return pageArray;
    }

    onClickPage(pageNumber: number,page:string): void {
      if(page=="next"){
        if(pageNumber>this.activePage){
          this.pages = this.getArrayOfPage(this.activePage,this.pcount);
        }
      }else if(page=="previous"){
        if(pageNumber<this.activePage){
          this.pages = this.getArrayOfPage(pageNumber,this.pcount);
        }
      }
        if (pageNumber >= 1 && pageNumber <= this.pcount) {
            this.activePage = pageNumber;
            this.onPageChange.emit(this.activePage);
        }
        
    }
    RefreshList(e:any){
      this.listcount=e.target.value;
      this.onChangeRecPerPage.emit(this.listcount);
      
    }
}


