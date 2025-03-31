import React, { useState, useEffect } from 'react';

function ListarUsuarios() {
    const [listaUsuarios, setUsuarios] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch("http://localhost:8080/api/usuarios/listarUsuarios") // URL do backend
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Erro ao buscar os itens");
                }
                return response.json();
            })
            .then((data) => {
                setUsuarios(data);
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
                setUsuarios(listaUsuarios.filter((user) => user.id !== id));
            })
            .catch((error) => {
                console.error('Erro ao excluir o usuário:', error);
                setError('Erro ao excluir o usuário.');
            });
    };

    return (
        <div style={{ marginBottom: "20px", marginTop: "80px" }}>
            <h2>Lista de Usuarios</h2>
            <table>
                <thead>
                <tr>
                    <th>Login</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {listaUsuarios.map((item) => (
                    <tr key={item.id}>
                        <td>{item.login}</td>
                        <td>
                            <button
                                onClick={() => handleDelete(item.id)}
                                style={{ color: 'red' }}>
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

export default ListarUsuarios;