import React, { useState, useEffect } from 'react';
import { Usuario } from "../interfaces/Usuario";
import axios from 'axios';

function ListarUsuarios() {
    // eslint-disable-next-line no-mixed-operators
    const [listaUsuarios, setUsuarios] = useState<Usuario>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        // URL do backend
        const url = "http://localhost:8080/api/usuarios/listarUsuarios";

        axios
            .get(url)
            .then((response) => {
                // Suponha que o backend retorna um array de objetos com muitos campos
                const data = response.data;

                // Mapeia os dados para a interface simplificada
                const usuariosMapeados: Usuario[] = data.map(
                    (usuario: any) => ({
                        idUsuario: usuario.id,
                        loginUsuario: usuario.login,
                        nomeUsuario: usuario.nome, // Apenas os campos desejados
                    })
                );

                // Atualiza o estado com os dados mapeados
                setUsuarios(usuariosMapeados);
                setLoading(false);
            })
            .catch((error) => {
                console.error("Erro ao buscar os itens:", error);
                setError("Erro ao carregar dados.");
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Carregando...</p>;
    if (error) return <p>{error}</p>;

    const handleDelete = (id) => {
        fetch("http://localhost:8080/api/usuarios/deletarUsuario/" + id, {
            method: 'DELETE',
        })
            .then((response) => {
                if (!response.ok) {
                    setError("Erro ao excluir o usuário.");
                    throw new Error('Erro ao excluir o usuário');
                }
                setUsuarios(listaUsuarios.filter((user) => user.idUsuario !== id));
            })
            .catch((error) => {
                console.error('Erro ao excluir o usuário:', error);
                setError('Erro ao excluir o usuário.');
            });
    };

    return (
        <div className="container" style={{marginTop: "80px", marginBottom: "20px"}}>
            <h2 className="mb-4" style={{textAlign: "center"}}>Lista de Usuários</h2>

            <div className="card p-4 shadow-sm">
                <table className="table table-striped table-bordered">
                    <thead className="table-dark">
                    <tr>
                        <th>Login</th>
                        <th>Ação</th>
                    </tr>
                    </thead>
                    <tbody>
                    {listaUsuarios.map((item) => (
                        <tr key={item.idUsuario}>
                            <td>{item.loginUsuario}</td>
                            <td>
                                <button
                                    onClick={() => handleDelete(item.idUsuario)}
                                    className="btn btn-sm btn-outline-danger"
                                    title="Excluir usuário"
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

export default ListarUsuarios;