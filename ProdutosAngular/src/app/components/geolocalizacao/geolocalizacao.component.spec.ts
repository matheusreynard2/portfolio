import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeolocalizacaoComponent } from './geolocalizacao.component';

describe('GeolocalizacaoComponent', () => {
  let component: GeolocalizacaoComponent;
  let fixture: ComponentFixture<GeolocalizacaoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeolocalizacaoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeolocalizacaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
