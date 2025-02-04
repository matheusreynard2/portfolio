export class Usuario {
  public id: number;

  public login: string;

  public senha: string;

  public token: string;

  constructor(
    id: number,
    login: string,
    senha: string,
    token: string,
  ) {
    this.id = id;
    this.login = login;
    this.senha = senha;
    this.token = token;
  }
}
