import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockledgerComponent } from './stockledger.component';

describe('StockledgerComponent', () => {
  let component: StockledgerComponent;
  let fixture: ComponentFixture<StockledgerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StockledgerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StockledgerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
