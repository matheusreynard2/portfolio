import React from "react";
import { BrowserRouter as Router, Routes, Route} from "react-router-dom";
import ListarUsuariosPage from "./pages/ListarUsuariosPage.js";
import NavBar from "./components/navBar.js";
import CadastrarUsuarioXCursoPage from "./pages/CadastrarUsuarioXCursoPage.js";
import ListarCursosPage from "./pages/ListarCursosPage.js";
import ListarUsuarioCursoPage from "./pages/ListarUsuarioCursoPage.js";
import Chat1Page from "./pages/Chat1Page";
import Chat2Page from "./pages/Chat2Page";
import {WebSocketProvider} from './contexts/WebSocketContext.tsx';

function App() {
  return (
      <div className="container" style={{marginTop: "80px", marginBottom: "20px"}}>
          <h1 style={{textAlign: "center"}}>Bem-vindo</h1>
          <Router>
              <NavBar/>
              <div className="content">
                  <WebSocketProvider>
                  <Routes>
                      <Route path="/listarCursos" element={<ListarCursosPage/>}/>
                      <Route path="/listarUsuarios" element={<ListarUsuariosPage/>}/>
                      <Route path="/cadastrarUsuarioXCurso" element={<CadastrarUsuarioXCursoPage/>}/>
                      <Route path="/listarUsuarioCurso" element={<ListarUsuarioCursoPage/>}/>
                      <Route path="/chat1" element={<Chat1Page/>}/>
                      <Route path="/chat2" element={<Chat2Page/>}/>
                  </Routes>
                  </WebSocketProvider>
              </div>
          </Router>
      </div>
  );
}

export default App;