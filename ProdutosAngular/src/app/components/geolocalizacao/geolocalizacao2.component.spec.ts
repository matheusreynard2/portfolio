import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Geolocalizacao2Component } from './geolocalizacao2.component';

describe('Geolocalizacao2Component', () => {
  let component: Geolocalizacao2Component;
  let fixture: ComponentFixture<Geolocalizacao2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Geolocalizacao2Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Geolocalizacao2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
