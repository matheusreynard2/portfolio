export class Usuario {
  public idUsuario: number;

  public login: string;

  public senha: string;

  public token: string;

  public imagem: string;

  public email: string;

  constructor(
    idUsuario: number,
    login: string,
    senha: string,
    token: string,
    imagem: string,
    email: string
  ) {
    this.idUsuario = idUsuario;
    this.login = login;
    this.senha = senha;
    this.token = token;
    this.imagem = imagem;
    this.email = email
  }
}