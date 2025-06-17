import { CnaeSecundarioDTO } from './CnaeSecundarioDTO';
import { QuadroSocietarioDTO } from './QuadroSocietarioDTO';
import { FornecedorDTO } from './FornecedorDTO';

export interface DadosEmpresaDTO {
    cnpj: string;
    razaoSocial: string;
    nomeFantasia: string;
    cnaeFiscal: string;
    codigoNaturezaJuridica: string;
    descricaoNaturezaJuridica: string;
    situacaoCadastral: string;
    dataSituacaoCadastral: string;
    dataInicioAtividade: string;
    porte: string;
    capitalSocial: string;
    
    // Endereço
    logradouro: string;
    numero: string;
    complemento: string;
    bairro: string;
    cep: string;
    municipio: string;
    uf: string;
    pais: string;
    
    // Telefones
    dddTelefone1: string;
    telefone1: string;
    dddTelefone2: string;
    telefone2: string;
    email: string;
    
    // Listas
    cnaesSecundarios: CnaeSecundarioDTO[];
    qsa: QuadroSocietarioDTO[];
    
    // Relacionamento
    fornecedor?: FornecedorDTO;
} 