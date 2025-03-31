import React, { useState } from "react";

function CadastrarUsuarioXCurso() {

    const [login, setLogin] = useState("");
    const [senha, setSenha] = useState("");
    const [nomeCurso, setNomeCurso] = useState("");
    const [valorMensalidade, setValorMensalidade] = useState("");
    const [msgCadastro, setMsgCadastro] = useState(null);
    const [msgRelacao, setMsgRelacao] = useState(null);
    const [usuarioAdicionado, setUsuarioAdicionado] = useState(null);
    const [cursoAdicionado, setCursoAdicionado] = useState(null);

    const handleRegister = () => {

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

                    console.log(usuarioData)

                    setUsuarioAdicionado(usuarioData);
                    setCursoAdicionado(cursoData);
                    setMsgCadastro("Usuário '" + usuarioData.usuario.login + "' e curso '"+ cursoData.nomeCurso +"' cadastrados com sucesso!");

                    // Chama o endpoint para adicionar a relação entre usuário e curso
                    return fetch("http://localhost:8080/api/usuarios/adicionarUsuarioCurso", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({
                                usuario: {
                                    id: usuarioData.usuario.id,
                                    login: usuarioData.usuario.login,
                                    senha: usuarioData.usuario.senha
                                },
                                curso: {
                                    id: cursoData.id,
                                    nomeCurso: cursoData.nomeCurso,
                                    valorMensalidade: cursoData.valorMensalidade
                                }})
                        });
                } else {
                    const usuarioErrorMsg = await usuarioResponse.text();
                    const cursoErrorMsg = await cursoResponse.text();

                    if (usuarioResponse.status !== 201) {
                        setMsgCadastro("Erro ao cadastrar usuário: " + usuarioErrorMsg);
                    }
                    if (cursoResponse.status !== 201) {
                        setMsgCadastro("Erro ao cadastrar curso: " + cursoErrorMsg);
                    }
                }
            })
            .then((response) => {
                if (response && response.status === 201) {
                    setMsgRelacao("Relação Usuário X Curso criada com sucesso!");
                } else {
                    setMsgRelacao("Erro ao criar relação.");
                }
            })
            .catch((error) => {
                setMsgCadastro("Erro ao cadastrar usuário e curso.");
                setMsgRelacao("Erro ao criar relação.");
                console.error(error);
            })
    };

    return (
        <div>
            <h2>Cadastro de Usuário e Curso</h2>

            {/* Tabela de dados do usuário */}
            <table border="1" cellPadding="5" cellSpacing="0" style={{ marginBottom: "20px", marginTop: "40px" }}>
                <thead>
                <tr>
                    <th colSpan="2">Dados do Usuário</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Login:</td>
                    <td>
                        <input
                            type="text"
                            placeholder="Login"
                            value={login}
                            onChange={(e) => setLogin(e.target.value)}
                        />
                    </td>
                </tr>
                <tr>
                    <td>Senha:</td>
                    <td>
                        <input
                            type="password"
                            placeholder="Senha"
                            value={senha}
                            onChange={(e) => setSenha(e.target.value)}
                        />
                    </td>
                </tr>
                </tbody>
            </table>

            {/* Tabela de dados do curso */}
            <table border="1" cellPadding="5" cellSpacing="0" style={{ marginBottom: "20px" }}>
                <thead>
                <tr>
                    <th colSpan="2">Dados do Curso</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Nome:</td>
                    <td>
                        <input
                            type="text"
                            placeholder="Nome"
                            value={nomeCurso}
                            onChange={(e) => setNomeCurso(e.target.value)}
                        />
                    </td>
                </tr>
                <tr>
                    <td>Mensalidade:</td>
                    <td>
                        <input
                            type="text"
                            placeholder="Mensalidade"
                            value={valorMensalidade}
                            onChange={(e) => setValorMensalidade(e.target.value)}
                        />
                    </td>
                </tr>
                </tbody>
            </table>

            <button onClick={handleRegister}>
                Cadastrar
            </button>

            {msgCadastro && <p>{msgCadastro}</p>}

            {/* Exibição dos dados do usuário adicionado */}
            {usuarioAdicionado && (
                <table border="1" cellPadding="5" cellSpacing="0" style={{ marginTop: "20px" }}>
                    <thead>
                    <tr>
                        <th colSpan="2">Usuário Adicionado</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>ID:</td>
                        <td>{usuarioAdicionado.usuario.id}</td>
                    </tr>
                    <tr>
                        <td>Login:</td>
                        <td>{usuarioAdicionado.usuario.login}</td>
                    </tr>
                    </tbody>
                </table>
            )}

            {/* Exibição dos dados do curso adicionado */}
            {cursoAdicionado && (
                <table border="1" cellPadding="5" cellSpacing="0" style={{ marginTop: "20px" }}>
                    <thead>
                    <tr>
                        <th colSpan="2">Curso Adicionado</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>ID:</td>
                        <td>{cursoAdicionado.id}</td>
                    </tr>
                    <tr>
                        <td>Nome:</td>
                        <td>{cursoAdicionado.nomeCurso}</td>
                    </tr>
                    <tr>
                        <td>Mensalidade:</td>
                        <td>{cursoAdicionado.valorMensalidade}</td>
                    </tr>
                    </tbody>
                </table>
            )}

            {msgRelacao && <p>{msgRelacao}</p>}
        </div>
    );
}

export default CadastrarUsuarioXCurso;