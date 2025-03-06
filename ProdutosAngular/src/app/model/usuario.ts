export class Usuario {
  public id: number;

  public login: string;

  public senha: string;

  public token: string;

  public imagem: string;

  public nome: string;

  public linkedin: string;

  public whatsapp: string;

  public endereco: string;

  constructor(
    id: number,
    login: string,
    senha: string,
    token: string,
    imagem: string,
    nome: string,
    linkedin: string,
    whatsapp: string,
    endereco: string,
  ) {
    this.id = id;
    this.login = login;
    this.senha = senha;
    this.token = token;
    this.imagem = imagem;
    this.nome = nome;
    this.linkedin = linkedin;
    this.whatsapp = whatsapp;
    this.endereco = endereco;
  }
}
