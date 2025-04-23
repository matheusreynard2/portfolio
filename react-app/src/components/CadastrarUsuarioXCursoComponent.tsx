import React, { useState } from "react";
import { Usuario } from "../interfaces/Usuario";
import { Curso } from "../interfaces/Curso"
import axios from 'axios';

function CadastrarUsuarioXCurso() {

    const [login, setLogin] = useState("");
    const [senha, setSenha] = useState("");

    const [usuarioInterface, setUsuarioInterface] = useState<Usuario | null>(null);
    const [cursoInterface, setCursoInterface] = useState<Curso | null>(null);

    const [nomeCurso, setNomeCurso] = useState("");
    const [valorMensalidade, setValorMensalidade] = useState("");
    const [msgCadastro, setMsgCadastro] = useState(null);
    const [msgRelacao, setMsgRelacao] = useState(null);
    const camposPreenchidos = login && senha && nomeCurso && valorMensalidade;

    const handleRegister = () => {

        if (!camposPreenchidos) {
            setMsgCadastro('Preencha todos os campos antes de cadastrar.');
            return;
        }

        const usuarioRequest = fetch("http://localhost:8080/api/usuarios/adicionarUsuarioReact", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({login, senha}),
        });

        const cursoRequest = fetch("http://localhost:8080/api/cursos/adicionarCurso", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({nomeCurso, valorMensalidade}),
        });

        Cadastrar(usuarioRequest, cursoRequest)

    }

    function Cadastrar(usuarioRequest, cursoRequest) {

        Promise.all([usuarioRequest, cursoRequest])
            .then(async ([usuarioResponse, cursoResponse]) => {
                if (usuarioResponse.status === 201 && cursoResponse.status === 201) {
                    const usuarioData = await usuarioResponse.json();
                    const cursoData = await cursoResponse.json();

                    const usuario = {
                        idUsuario: usuarioData.usuario.id,
                        loginUsuario: usuarioData.usuario.login,
                        senhaUsuario: usuarioData.usuario.senha,
                    };

                    const curso = {
                        idCurso: cursoData.id,
                        nomeCurso: cursoData.nomeCurso,
                        valorMensalidade: cursoData.valorMensalidade,
                    };

                    setUsuarioInterface(usuario);
                    setCursoInterface(curso);
                    setMsgCadastro("Usuário '" + usuario.loginUsuario + "' e curso '"+ curso.nomeCurso +"' cadastrados com sucesso!");

                    // Chama o endpoint para adicionar a relação entre usuário e curso
                    return axios.post("http://localhost:8080/api/usuariocurso/adicionarUsuarioCurso", {
                        usuario: usuarioData.usuario, // Usa o estado `usuarioInterface`
                        curso: cursoData,    // Usa o estado `cursoInterface`
                    });
                } else {
                    // Trata erros nas respostas
                    const usuarioErrorMsg = usuarioResponse.statusText || "Erro desconhecido";
                    const cursoErrorMsg = cursoResponse.statusText || "Erro desconhecido";

                    if (usuarioResponse.status !== 201) {
                        setMsgCadastro('Erro ao cadastrar usuário: ' + usuarioErrorMsg);
                    }
                    if (cursoResponse.status !== 201) {
                        setMsgCadastro('Erro ao cadastrar curso:' + cursoErrorMsg);
                    }
                }
            })
        .then((response) => {
            // Verifica se a criação da relação foi bem-sucedida
            if (response && response.status === 201) {
                setMsgRelacao("Relação Usuário X Curso criada com sucesso!");
            } else {
                setMsgRelacao("Erro ao criar relação.");
            }
        })
        .catch((error) => {
            // Trata erros gerais
            setMsgCadastro("Erro ao cadastrar usuário e curso. Erro: " + error.message);
            setMsgRelacao("Erro ao criar relação. Erro: " + error.message);
            console.error(error);
        });
    }

    return (
        <div className="container" style={{marginTop: "80px", marginBottom: "20px"}}>
            <h2 className="mb-4" style={{textAlign: "center"}}>Cadastro de Usuário e Curso</h2>

            <form className="card p-4 shadow-sm">
                <div className="row">
                    <div className="col-md-6">
                        <h5>Dados do Usuário</h5>
                        <div className="mb-3">
                            <label className="form-label">Login:</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Login"
                                value={login}
                                onChange={(e) => setLogin(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Senha:</label>
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Senha"
                                value={senha}
                                onChange={(e) => setSenha(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="col-md-6">
                        <h5>Dados do Curso</h5>
                        <div className="mb-3">
                            <label className="form-label">Nome do Curso:</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Nome do Curso"
                                value={nomeCurso}
                                onChange={(e) => setNomeCurso(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Valor da Mensalidade:</label>
                            <input
                                type="number"
                                className="form-control"
                                placeholder="Mensalidade"
                                value={valorMensalidade}
                                onChange={(e) => setValorMensalidade(e.target.value)}
                                required
                            />
                        </div>
                    </div>
                </div>

                <button
                    type="button"
                    className="btn btn-primary mt-3"
                    onClick={handleRegister}
                    disabled={!camposPreenchidos}
                >
                    Cadastrar
                </button>

                {msgCadastro && <div className="alert alert-info mt-3">{msgCadastro}</div>}
            </form>

            {/* Exibição dos dados adicionados */}
            {usuarioInterface && (
                <div className="card mt-4 p-3">
                    <h5>Usuário Adicionado</h5>
                    <p><strong>ID:</strong> {usuarioInterface.idUsuario}</p>
                    <p><strong>Login:</strong> {usuarioInterface.loginUsuario}</p>
                    <p><strong>Senha:</strong> {usuarioInterface.senhaUsuario}</p>
                </div>
            )}

            {cursoInterface && (
                <div className="card mt-4 p-3">
                    <h5>Curso Adicionado</h5>
                    <p><strong>ID:</strong> {cursoInterface.idCurso}</p>
                    <p><strong>Nome:</strong> {cursoInterface.nomeCurso}</p>
                    <p><strong>Mensalidade:</strong> R$ {cursoInterface.valorMensalidade}</p>
                </div>
            )}

            {msgRelacao && <div className="alert alert-info mt-3">{msgRelacao}</div>}
        </div>
    );
}

export default CadastrarUsuarioXCurso;