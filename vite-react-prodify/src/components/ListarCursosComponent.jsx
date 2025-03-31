import React, { useState, useEffect } from 'react';

function ListarCursos() {
    const [listaCursos, setCursos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState(null);

    useEffect(() => {
        fetch("http://localhost:8080/api/cursos/listarCursos") // URL do backend
            .then((response) => {
                if (!response.ok) {
                    setMsg("Erro ao acessar endpoint.");
                    throw new Error("Erro ao acessar endpoint");
                }
                return response.json();
            })
            .then((data) => {
                setCursos(data);
                setLoading(false);
            })
            .catch((error) => {
                console.error("Erro ao carregar os dados.", error);
                setMsg("Erro ao carregar os dados.");
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
                setCursos(listaCursos.filter((curso) => curso.id !== id));
            })
            .catch((error) => {
                if (error.status === 404) {
                    setMsg('Curso relacionado com usuário. Exclua o usuário primeiro.');
                }
                console.error('Erro ao excluir o curso:', error);
            });
    };

    return (
        <div style={{ marginBottom: "20px", marginTop: "80px" }}>
            <h2>Lista de Cursos</h2>
            {msg && <p>{msg}</p>}
            <table>
                <thead>
                <tr>
                    <th>Nome</th>
                    <th>Mensalidade</th>
                </tr>
                </thead>
                <tbody>
                {listaCursos.map((item) => (
                    <tr key={item.id}>
                        <td>{item.nomeCurso}</td>
                        <td>R$ {item.valorMensalidade}</td>
                        <td>
                            <button
                                onClick={() => handleDelete(item.id)}
                                style={{color: 'red'}}>
                                X
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default ListarCursos;