import { TestBed } from '@angular/core/testing';

import { ProdutoFunctionsService } from './produto-functions.service';

describe('ProdutoFunctionsService', () => {
  let service: ProdutoFunctionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProdutoFunctionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
