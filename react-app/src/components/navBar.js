import React from "react";
import { Link } from "react-router-dom";
import './navBar.css';

function NavBar() {
    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
            <div className="container-fluid">
                <a className="navbar-brand" href="/listarCursos">MENU</a>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                        data-bs-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false"
                        aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
                    <div className="navbar-nav">
                        <Link className="nav-link" to="/listarCursos">Listar Cursos</Link>
                        <Link className="nav-link" to="/listarUsuarios">Listar Usuários</Link>
                        <Link className="nav-link" to="/cadastrarUsuarioXCurso">Cadastrar Usuário X Curso</Link>
                        <Link className="nav-link" to="/listarUsuarioCurso">Listar Usuário X Curso</Link>
                        <Link className="nav-link" to="/chat1">CHAT 1</Link>
                        <Link className="nav-link" to="/chat2">CHAT 2</Link>
                    </div>
                </div>
            </div>
        </nav>
    );
}

export default NavBar;