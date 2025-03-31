import React from "react";
import { BrowserRouter as Router, Routes, Route} from "react-router-dom";
import ListarUsuariosPage from "./pages/ListarUsuariosPage.jsx";
import NavBar from "./components/navBar.jsx";
import Inicio from "./pages/Inicio.jsx";
import CadastrarUsuarioXCursoPage from "./pages/CadastrarUsuarioXCursoPage.jsx";
import ListarCursosPage from "./pages/ListarCursosPage.jsx";

function App() {
    return (
        <div className="app-container">
            <Router>
                <NavBar/>
                <div className="content">
                    <Routes>
                        <Route path="/" element={<Inicio/>}/>
                        <Route path="/listarCursos" element={<ListarCursosPage/>}/>
                        <Route path="/listarUsuarios" element={<ListarUsuariosPage/>}/>
                        <Route path="/cadastrarUsuarioXCurso" element={<CadastrarUsuarioXCursoPage/>}/>
                    </Routes>
                </div>
            </Router>
        </div>
    );
}

export default App;