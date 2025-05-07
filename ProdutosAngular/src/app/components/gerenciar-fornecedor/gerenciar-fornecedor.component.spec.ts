import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GerenciarFornecedorComponent } from './gerenciar-fornecedor.component';

describe('GerenciarFornecedorComponent', () => {
  let component: GerenciarFornecedorComponent;
  let fixture: ComponentFixture<GerenciarFornecedorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GerenciarFornecedorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GerenciarFornecedorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
