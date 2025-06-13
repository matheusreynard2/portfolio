export class Usuario {
  public idUsuario: number;

  public login: string;

  public senha: string;

  public token: string;

  public imagem?: string;

  public email: string;

  constructor(
    idUsuario: number,
    login: string,
    senha: string,
    token: string,
    email: string,
    imagem?: string
  ) {
    this.idUsuario = idUsuario;
    this.login = login;
    this.senha = senha;
    this.token = token;
    this.email = email;
    this.imagem = imagem;
  }
}