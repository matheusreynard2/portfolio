import React, { useState, useEffect } from 'react';
import { Curso } from "../interfaces/Curso"
import axios from 'axios';

function ListarCursos() {
    const [listaCursos, setCursos] = useState<Curso[]>([]);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState(null);

    useEffect(() => {
        // URL do backend
        const url = "http://localhost:8080/api/cursos/listarCursos";

        axios
            .get(url)
            .then((response) => {

                console.log(response.data)
                // Suponha que o backend retorna um array de objetos com muitos campos
                const data = response.data;

                // Mapeia os dados para a interface simplificada
                const cursosMapeados: Curso[] = data.map(
                    (curso: any) => ({
                        idCurso: curso.id,
                        nomeCurso: curso.nomeCurso,
                        valorMensalidade: curso.valorMensalidade, // Apenas os campos desejados
                    })
                );

                // Atualiza o estado com os dados mapeados
                setCursos(cursosMapeados);
                setLoading(false);
            })
            .catch((error) => {
                console.error("Erro ao buscar os itens:", error);
                setMsg("Erro ao carregar dados.");
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Carregando...</p>;

    const handleDelete = (id) => {
        fetch("http://localhost:8080/api/cursos/deletarCurso/" + id, {
            method: 'DELETE',
        })
            .then((response) => {
                if (!response.ok && response.status === 404) {
                    setMsg('Curso relacionado com usuário. Exclua o usuário primeiro.');
                    throw new Error('Erro ao excluir o curso.');
                }
                setCursos(listaCursos.filter((curso) => curso.idCurso !== id));
            })
            .catch((error) => {
                if (error.status === 404) {
                    setMsg('Curso relacionado com usuário. Exclua o usuário primeiro.');
                }
                console.error('Erro ao excluir o curso:', error);
            });
    };

    return (
        <div className="container" style={{marginTop: "80px", marginBottom: "20px"}}>
            <h2 className="mb-4" style={{textAlign: "center"}}>Lista de Cursos</h2>

            {msg && <div className="alert alert-info">{msg}</div>}

            <div className="card p-4 shadow-sm">
                <table className="table table-striped table-bordered">
                    <thead className="table-dark">
                    <tr>
                        <th>Nome</th>
                        <th>Mensalidade</th>
                        <th>Ação</th>
                    </tr>
                    </thead>
                    <tbody>
                    {listaCursos.map((item) => (
                        <tr key={item.idCurso}>
                            <td>{item.nomeCurso}</td>
                            <td>R$ {item.valorMensalidade}</td>
                            <td>
                                <button
                                    onClick={() => handleDelete(item.idCurso)}
                                    className="btn btn-sm btn-outline-danger"
                                    title="Excluir curso"
                                >
                                    X
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default ListarCursos;