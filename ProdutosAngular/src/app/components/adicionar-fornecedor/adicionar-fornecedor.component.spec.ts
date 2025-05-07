import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdicionarFornecedorComponent } from './adicionar-fornecedor.component';

describe('AdicionarFornecedorComponent', () => {
  let component: AdicionarFornecedorComponent;
  let fixture: ComponentFixture<AdicionarFornecedorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdicionarFornecedorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdicionarFornecedorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
