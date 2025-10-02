export interface RelatorioFinanceiroDTO {
    codigo: string;
    descricao: string;
    colunas: string[];
    linhas: Array<Record<string, any>>;
    paisagem: boolean;
} 