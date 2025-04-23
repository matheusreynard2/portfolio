import React, { useState, useEffect } from 'react';
import { UsuarioCurso } from "../interfaces/UsuarioCurso";
import axios from "axios";

function ListarUsuarioCurso() {
    const [lista, setLista] = useState<UsuarioCurso>([]);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState(null);

    useEffect(() => {
        // URL do backend
        const url = "http://localhost:8080/api/usuariocurso/listarUsuarioCurso";

        // Requisição com Axios
        axios
            .get(url) // Tipagem explícita no retorno da resposta
            .then((response) => {
                // Atribui os dados diretamente ao estado
                setLista(response.data);
                setLoading(false);
            })
            .catch((error) => {
                console.error("Erro ao buscar os itens:", error);
                setMsg("Erro ao carregar dados.");
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Carregando...</p>;

    return (
        <div className="container" style={{marginTop: "80px", marginBottom: "20px"}}>
            <div className="row">
                <div className="col">
                    <h2 className="mb-4" style={{textAlign: "center"}}>Lista de Usuários</h2>

                    {msg && <div className="alert alert-info">{msg}</div>}

                    <div className="card p-4 shadow-sm">
                        <table className="table table-striped table-bordered">
                            <thead className="table-dark">
                            <tr>
                                <th>Associação</th>
                                <th>Usuário</th>
                                <th>Curso</th>
                            </tr>
                            </thead>
                            <tbody>
                            {lista.map((item) => (
                                <tr key={item.idUsuarioCurso}>
                                    <td>{item.idUsuarioCurso}</td>
                                    <td>{item.nomeUsuario}</td>
                                    <td>{item.nomeCurso}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ListarUsuarioCurso;