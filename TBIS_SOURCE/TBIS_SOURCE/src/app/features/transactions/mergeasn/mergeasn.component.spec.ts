import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MergeasnComponent } from './mergeasn.component';

describe('MergeasnComponent', () => {
  let component: MergeasnComponent;
  let fixture: ComponentFixture<MergeasnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MergeasnComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MergeasnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
