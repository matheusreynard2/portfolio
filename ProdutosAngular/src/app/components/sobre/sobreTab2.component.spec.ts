import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SobreTab2Component } from './sobreTab2.component';

describe('Sobre1Component', () => {
  let component: SobreTab2Component;
  let fixture: ComponentFixture<SobreTab2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SobreTab2Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SobreTab2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
