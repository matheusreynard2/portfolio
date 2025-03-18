import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SobreTab1Component } from './sobreTab1.component';

describe('SobreComponent', () => {
  let component: SobreTab1Component;
  let fixture: ComponentFixture<SobreTab1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SobreTab1Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SobreTab1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
