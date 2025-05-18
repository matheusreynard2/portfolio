import React, { useState } from "react";
import { Usuario } from "../interfaces/Usuario";

function CadastrarUsuario() {
    // Estados para formulário de usuário
    const [login, setLogin] = useState("");
    const [senha, setSenha] = useState("");
    const [usuarioInterface, setUsuarioInterface] = useState(null);
    const [msgUsuario, setMsgUsuario] = useState(null);
    const [usuarioCadastrado, setUsuarioCadastrado] = useState(false);
    const [loadingUsuario, setLoadingUsuario] = useState(false);

    // Validação de formulário
    const camposUsuarioPreenchidos = login && senha;

    // Função para cadastrar usuário
    const handleCadastrarUsuario = async () => {
        if (!camposUsuarioPreenchidos) {
            setMsgUsuario('Preencha todos os campos do usuário antes de cadastrar.');
            return;
        }

        setLoadingUsuario(true);
        setMsgUsuario(null);

        try {
            const response = await fetch("http://localhost:8080/api/usuarios/adicionarUsuarioReact", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ login, senha }),
            });

            console.log("Status da resposta:", response.status);

            if (response.ok) {
                const responseData = await response.json();
                console.log("Dados recebidos:", responseData);

                // Ajuste aqui para extrair corretamente os dados dependendo da estrutura da resposta
                const usuarioData = responseData.data || responseData;

                const usuario = {
                    idUsuario: usuarioData.id,
                    loginUsuario: usuarioData.login,
                    senhaUsuario: usuarioData.senha,
                };

                setUsuarioInterface(usuario);
                setUsuarioCadastrado(true);
                setMsgUsuario("Usuário '" + usuario.loginUsuario + "' cadastrado com sucesso!");
            } else {
                const errorText = await response.text();
                setMsgUsuario('Erro ao cadastrar usuário: ' + (errorText || response.statusText || "Erro desconhecido"));
                console.error("Erro na resposta:", errorText);
            }
        } catch (error) {
            setMsgUsuario("Erro ao cadastrar usuário: " + error.message);
            console.error("Exceção ao cadastrar usuário:", error);
        } finally {
            setLoadingUsuario(false);
        }
    };

    // Função para reiniciar o formulário
    const handleLimparFormulario = () => {
        setLogin("");
        setSenha("");
        setUsuarioInterface(null);
        setMsgUsuario(null);
        setUsuarioCadastrado(false);
    };

    return (
        <div className="container" style={{ marginTop: "30px", marginBottom: "20px" }}>
            <h2 className="mb-4" style={{ textAlign: "center" }}>Cadastro de Usuário</h2>

            <div className="card p-4 shadow-sm mb-4">
                <div className="row">
                    <div className="col-md-6">
                        <div className="mb-3">
                            <label className="form-label">Login:</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Login"
                                value={login}
                                onChange={(e) => setLogin(e.target.value)}
                                disabled={usuarioCadastrado || loadingUsuario}
                                required
                            />
                        </div>
                    </div>
                    <div className="col-md-6">
                        <div className="mb-3">
                            <label className="form-label">Senha:</label>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Senha"
                                value={senha}
                                onChange={(e) => setSenha(e.target.value)}
                                disabled={usuarioCadastrado || loadingUsuario}
                                required
                            />
                        </div>
                    </div>
                </div>

                <div className="d-flex gap-2">
                    <button
                        type="button"
                        className="btn btn-primary"
                        onClick={handleCadastrarUsuario}
                        disabled={!camposUsuarioPreenchidos || usuarioCadastrado || loadingUsuario}
                    >
                        {loadingUsuario ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                Cadastrando...
                            </>
                        ) : "Cadastrar Usuário"}
                    </button>

                    {usuarioCadastrado && (
                        <button
                            type="button"
                            className="btn btn-success"
                            onClick={handleLimparFormulario}
                        >
                            Cadastrar Novo Usuário
                        </button>
                    )}

                    {!usuarioCadastrado && (
                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={handleLimparFormulario}
                            disabled={loadingUsuario}
                        >
                            Limpar
                        </button>
                    )}
                </div>

                {msgUsuario && (
                    <div className={`alert ${usuarioCadastrado ? "alert-success" : "alert-info"} mt-3`}>
                        {msgUsuario}
                    </div>
                )}

                {usuarioInterface && (
                    <div className="card mt-3 p-3 bg-light">
                        <h5>Usuário Cadastrado</h5>
                        <div className="table-responsive">
                            <table className="table table-bordered">
                                <tbody>
                                <tr>
                                    <th style={{ width: "150px" }}>ID</th>
                                    <td>{usuarioInterface.idUsuario}</td>
                                </tr>
                                <tr>
                                    <th>Login</th>
                                    <td>{usuarioInterface.loginUsuario}</td>
                                </tr>
                                <tr>
                                    <th>Senha</th>
                                    <td>{usuarioInterface.senhaUsuario}</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default CadastrarUsuario;