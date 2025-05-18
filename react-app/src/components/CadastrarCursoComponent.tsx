import React, { useState } from "react";
import { Curso } from "../interfaces/Curso";

function CadastrarCurso() {
    // Estados para formulário de curso
    const [nomeCurso, setNomeCurso] = useState("");
    const [valorMensalidade, setValorMensalidade] = useState("");
    const [periodo, setPeriodo] = useState("");
    const [qtdeMaterias, setQtdeMaterias] = useState("");
    const [cursoInterface, setCursoInterface] = useState(null);
    const [msgCurso, setMsgCurso] = useState(null);
    const [loading, setLoading] = useState(false);
    const [sucessoCadastro, setSucessoCadastro] = useState(false);

    // Validação de formulário
    const camposObrigatoriosPreenchidos = nomeCurso && valorMensalidade;

    // Função para cadastrar curso
    const handleCadastrarCurso = async () => {
        if (!camposObrigatoriosPreenchidos) {
            setMsgCurso('Preencha todos os campos obrigatórios antes de cadastrar.');
            return;
        }

        setLoading(true);
        setMsgCurso(null);

        try {
            const response = await fetch("http://localhost:8080/api/cursos/adicionarCurso", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    nomeCurso,
                    valorMensalidade: parseFloat(valorMensalidade),
                    periodo,
                    qtdeMaterias: qtdeMaterias ? parseInt(qtdeMaterias) : 0
                }),
            });

            if (response.ok) {
                const responseData = await response.json();
                console.log("Dados do curso recebidos:", responseData);

                // Ajuste aqui dependendo da estrutura da resposta
                const cursoData = responseData.data || responseData;

                const curso = {
                    idCurso: cursoData.id,
                    nomeCurso: cursoData.nomeCurso,
                    valorMensalidade: cursoData.valorMensalidade,
                    periodo: cursoData.periodo,
                    qtdeMaterias: cursoData.qtdeMaterias
                };

                setCursoInterface(curso);
                setSucessoCadastro(true);
                setMsgCurso("Curso '" + curso.nomeCurso + "' cadastrado com sucesso!");
            } else {
                const errorText = await response.text();
                setMsgCurso('Erro ao cadastrar curso: ' + (errorText || response.statusText || "Erro desconhecido"));
                console.error("Erro na resposta do curso:", errorText);
            }
        } catch (error) {
            setMsgCurso("Erro ao cadastrar curso: " + error.message);
            console.error("Exceção ao cadastrar curso:", error);
        } finally {
            setLoading(false);
        }
    };

    // Função para reiniciar o formulário
    const handleLimparFormulario = () => {
        setNomeCurso("");
        setValorMensalidade("");
        setPeriodo("");
        setQtdeMaterias("");
        setCursoInterface(null);
        setMsgCurso(null);
        setSucessoCadastro(false);
    };

    return (
        <div className="container" style={{marginTop: "80px", marginBottom: "20px"}}>
            <h2 className="mb-4" style={{textAlign: "center"}}>Cadastro de Curso</h2>

            <div className="card p-4 shadow-sm mb-4">
                <form>
                    <div className="row">
                        <div className="col-md-6">
                            <div className="mb-3">
                                <label className="form-label">Nome do Curso: <span className="text-danger">*</span></label>
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Nome do Curso"
                                    value={nomeCurso}
                                    onChange={(e) => setNomeCurso(e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </div>
                        </div>
                        <div className="col-md-6">
                            <div className="mb-3">
                                <label className="form-label">Valor da Mensalidade: <span className="text-danger">*</span></label>
                                <input
                                    type="number"
                                    className="form-control"
                                    placeholder="Mensalidade"
                                    value={valorMensalidade}
                                    onChange={(e) => setValorMensalidade(e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-md-6">
                            <div className="mb-3">
                                <label className="form-label">Período:</label>
                                <select
                                    className="form-select"
                                    value={periodo}
                                    onChange={(e) => setPeriodo(e.target.value)}
                                    disabled={loading}
                                >
                                    <option value="">Selecione um período</option>
                                    <option value="Matutino">Matutino</option>
                                    <option value="Vespertino">Vespertino</option>
                                    <option value="Noturno">Noturno</option>
                                    <option value="Integral">Integral</option>
                                    <option value="EAD">EAD</option>
                                </select>
                            </div>
                        </div>
                        <div className="col-md-6">
                            <div className="mb-3">
                                <label className="form-label">Quantidade de Matérias:</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    placeholder="Quantidade de Matérias"
                                    value={qtdeMaterias}
                                    onChange={(e) => setQtdeMaterias(e.target.value)}
                                    disabled={loading}
                                    min="0"
                                />
                            </div>
                        </div>
                    </div>

                    <div className="d-flex gap-2 mt-3">
                        <button
                            type="button"
                            className="btn btn-primary"
                            onClick={handleCadastrarCurso}
                            disabled={!camposObrigatoriosPreenchidos || loading}
                        >
                            {loading ? (
                                <>
                                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                    Cadastrando...
                                </>
                            ) : "Cadastrar Curso"}
                        </button>

                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={handleLimparFormulario}
                            disabled={loading}
                        >
                            Limpar
                        </button>
                    </div>

                    {msgCurso && (
                        <div className={`alert ${sucessoCadastro ? "alert-success" : "alert-danger"} mt-3`}>
                            {msgCurso}
                        </div>
                    )}
                </form>
            </div>

            {cursoInterface && (
                <div className="card mt-4 p-3">
                    <h5>Curso Cadastrado</h5>
                    <div className="table-responsive">
                        <table className="table table-bordered">
                            <tbody>
                            <tr>
                                <th style={{width: "200px"}}>ID</th>
                                <td>{cursoInterface.idCurso}</td>
                            </tr>
                            <tr>
                                <th>Nome</th>
                                <td>{cursoInterface.nomeCurso}</td>
                            </tr>
                            <tr>
                                <th>Mensalidade</th>
                                <td>R$ {cursoInterface.valorMensalidade.toFixed(2)}</td>
                            </tr>
                            {cursoInterface.periodo && (
                                <tr>
                                    <th>Período</th>
                                    <td>{cursoInterface.periodo}</td>
                                </tr>
                            )}
                            {cursoInterface.qtdeMaterias > 0 && (
                                <tr>
                                    <th>Quantidade de Matérias</th>
                                    <td>{cursoInterface.qtdeMaterias}</td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>

                    {sucessoCadastro && (
                        <div className="d-flex justify-content-end mt-3">
                            <button
                                type="button"
                                className="btn btn-success"
                                onClick={handleLimparFormulario}
                            >
                                Cadastrar Novo Curso
                            </button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default CadastrarCurso;