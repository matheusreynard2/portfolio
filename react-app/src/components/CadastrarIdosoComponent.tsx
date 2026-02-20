import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import { API_BASE } from "../config/api";
import { estados, fetchCidades, enderecoVazio, enderecoParaPayload, EnderecoForm } from "../interfaces/endereco.ts";

const CAMPOS_OBRIGATORIOS_IDOSO = ["nome", "cpf", "dataNascimento", "telefone", "email"] as const;

function CadastrarIdosoComponent() {
    const [nome, setNome] = useState("");
    const [cpf, setCpf] = useState("");
    const [dataNascimento, setDataNascimento] = useState("");
    const [telefone, setTelefone] = useState("");
    const [email, setEmail] = useState("");
    const [ativo, setAtivo] = useState(true);
    const [contatoEmergencia, setContatoEmergencia] = useState("");
    const [origem, setOrigem] = useState<EnderecoForm>(enderecoVazio());
    const [destino, setDestino] = useState<EnderecoForm>(enderecoVazio());
    const [cidadesOrigem, setCidadesOrigem] = useState<{ id: number; nome: string }[]>([]);
    const [cidadesDestino, setCidadesDestino] = useState<{ id: number; nome: string }[]>([]);
    const [loading, setLoading] = useState(false);
    const [mensagem, setMensagem] = useState<string | null>(null);
    const [erros, setErros] = useState<Record<string, boolean>>({});
    const [mostrarPopupSucesso, setMostrarPopupSucesso] = useState(false);
    const refNome = useRef<HTMLInputElement>(null);
    const refCpf = useRef<HTMLInputElement>(null);
    const refDataNascimento = useRef<HTMLInputElement>(null);
    const refTelefone = useRef<HTMLInputElement>(null);
    const refEmail = useRef<HTMLInputElement>(null);
    const refsCampo: Record<string, React.RefObject<HTMLInputElement | null>> = {
        nome: refNome,
        cpf: refCpf,
        dataNascimento: refDataNascimento,
        telefone: refTelefone,
        email: refEmail,
    };

    useEffect(() => {
        if (!origem.estado) {
            setCidadesOrigem([]);
            return;
        }
        fetchCidades(origem.estado).then(setCidadesOrigem).catch(() => setCidadesOrigem([]));
    }, [origem.estado]);

    useEffect(() => {
        if (!destino.estado) {
            setCidadesDestino([]);
            return;
        }
        fetchCidades(destino.estado).then(setCidadesDestino).catch(() => setCidadesDestino([]));
    }, [destino.estado]);

    const limparFormulario = () => {
        setNome("");
        setCpf("");
        setDataNascimento("");
        setTelefone("");
        setEmail("");
        setAtivo(true);
        setContatoEmergencia("");
        setOrigem(enderecoVazio());
        setDestino(enderecoVazio());
    };

    const popularDadosTeste = () => {
        setErros({});
        setMensagem(null);
        setNome("Maria Silva");
        setCpf("123.456.789-00");
        setDataNascimento("1950-05-15");
        setTelefone("(11) 98765-4321");
        setEmail("maria.silva@email.com");
        setContatoEmergencia("(11) 91234-5678");
        setOrigem({
            id: 0,
            logradouro: "Rua das Flores",
            numero: "100",
            estado: "SP",
            cidade: "São Paulo",
        });
        setDestino({
            id: 0,
            logradouro: "Av. Paulista",
            numero: "1000",
            estado: "SP",
            cidade: "São Paulo",
        });
    };

    const validar = (): boolean => {
        const vals = { nome: nome.trim(), cpf: cpf.trim(), dataNascimento: dataNascimento.trim(), telefone: telefone.trim(), email: email.trim() };
        const novosErros: Record<string, boolean> = {};
        for (const campo of CAMPOS_OBRIGATORIOS_IDOSO) {
            if (!vals[campo]) novosErros[campo] = true;
        }
        setErros(novosErros);
        if (Object.keys(novosErros).length > 0) {
            const primeiro = CAMPOS_OBRIGATORIOS_IDOSO.find((c) => novosErros[c]);
            if (primeiro) {
                setTimeout(() => refsCampo[primeiro]?.current?.focus(), 0);
            }
            return false;
        }
        return true;
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setMensagem(null);
        if (!validar()) return;
        setLoading(true);

        try {
            const dto = {
                nome,
                cpf,
                dataNascimento,
                telefone,
                email,
                ativo,
                contatoEmergencia,
                enderecoOrigem: enderecoParaPayload(origem) ?? null,
                enderecoDestino: enderecoParaPayload(destino) ?? null,
            };
            await axios.post(`${API_BASE}/idosos`, dto);
            limparFormulario();
            setMostrarPopupSucesso(true);
        } catch (error) {
            setMensagem("Erro ao cadastrar idoso.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <form className="id_form_cadastrar" onSubmit={handleSubmit} noValidate>
                <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
                    <h2 className="mb-0">Cadastrar Idoso</h2>
                    <div className="d-flex align-items-center gap-2 flex-wrap">
                        <button
                            type="button"
                            className="btn btn-outline-secondary"
                            onClick={popularDadosTeste}
                            disabled={loading}
                            title="Preenche o formulário com dados de teste"
                        >
                            Popular com dados de teste
                        </button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            Cadastrar Idoso
                        </button>
                        {loading && <span className="text-muted small">Carregando...</span>}
                    </div>
                </div>
                {mensagem && (
                    <div className="alert alert-danger">{mensagem}</div>
                )}
                <div className="row">
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Nome: <span className="text-danger">*</span></label>
                        <input
                            ref={refNome}
                            type="text"
                            className={`form-control ${erros.nome ? "is-invalid" : ""}`}
                            value={nome}
                            onChange={(e) => { setNome(e.target.value); setErros((p) => ({ ...p, nome: false })); }}
                            disabled={loading}
                            required
                        />
                        {erros.nome && <div className="invalid-feedback">Preencha o nome.</div>}
                    </div>
                    <div className="col-md-6 mb-3">
                        <label className="form-label">CPF: <span className="text-danger">*</span></label>
                        <input
                            ref={refCpf}
                            type="text"
                            className={`form-control ${erros.cpf ? "is-invalid" : ""}`}
                            value={cpf}
                            onChange={(e) => { setCpf(e.target.value); setErros((p) => ({ ...p, cpf: false })); }}
                            disabled={loading}
                            required
                        />
                        {erros.cpf && <div className="invalid-feedback">Preencha o CPF.</div>}
                    </div>
                </div>

                <div className="row">
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Data de Nascimento: <span className="text-danger">*</span></label>
                        <input
                            ref={refDataNascimento}
                            type="date"
                            className={`form-control ${erros.dataNascimento ? "is-invalid" : ""}`}
                            value={dataNascimento}
                            onChange={(e) => { setDataNascimento(e.target.value); setErros((p) => ({ ...p, dataNascimento: false })); }}
                            disabled={loading}
                            max={new Date().toISOString().split("T")[0]}
                            required
                        />
                        {erros.dataNascimento && <div className="invalid-feedback">Preencha a data de nascimento.</div>}
                    </div>
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Telefone: <span className="text-danger">*</span></label>
                        <input
                            ref={refTelefone}
                            type="text"
                            className={`form-control ${erros.telefone ? "is-invalid" : ""}`}
                            value={telefone}
                            onChange={(e) => { setTelefone(e.target.value); setErros((p) => ({ ...p, telefone: false })); }}
                            disabled={loading}
                        />
                        {erros.telefone && <div className="invalid-feedback">Preencha o telefone.</div>}
                    </div>
                </div>

                <div className="row">
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Email: <span className="text-danger">*</span></label>
                        <input
                            ref={refEmail}
                            type="email"
                            className={`form-control ${erros.email ? "is-invalid" : ""}`}
                            value={email}
                            onChange={(e) => { setEmail(e.target.value); setErros((p) => ({ ...p, email: false })); }}
                            disabled={loading}
                        />
                        {erros.email && <div className="invalid-feedback">Preencha o e-mail.</div>}
                    </div>
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Contato de Emergência:</label>
                        <input
                            type="text"
                            className="form-control"
                            value={contatoEmergencia}
                            onChange={(e) => setContatoEmergencia(e.target.value)}
                            disabled={loading}
                        />
                    </div>
                </div>

                <hr className="my-4" />
                <h5 className="mb-3">Endereço de origem</h5>
                <div className="row">
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Logradouro (origem):</label>
                        <input
                            type="text"
                            className="form-control"
                            value={origem.logradouro}
                            onChange={(e) => setOrigem((p) => ({ ...p, logradouro: e.target.value }))}
                            disabled={loading}
                        />
                    </div>
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Número (origem):</label>
                        <input
                            type="text"
                            className="form-control"
                            value={origem.numero}
                            onChange={(e) => setOrigem((p) => ({ ...p, numero: e.target.value }))}
                            disabled={loading}
                        />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Estado (origem):</label>
                        <select
                            className="form-select"
                            value={origem.estado}
                            onChange={(e) => setOrigem((p) => ({ ...p, estado: e.target.value, cidade: "" }))}
                            disabled={loading}
                        >
                            <option value="">Selecione</option>
                            {estados.map((e) => (
                                <option key={e.sigla} value={e.sigla}>{e.nome}</option>
                            ))}
                        </select>
                    </div>
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Cidade (origem):</label>
                        <select
                            className="form-select"
                            value={origem.cidade}
                            onChange={(e) => setOrigem((p) => ({ ...p, cidade: e.target.value }))}
                            disabled={loading || !origem.estado}
                        >
                            <option value="">{origem.estado ? "Selecione" : "Selecione o estado"}</option>
                            {cidadesOrigem.map((c) => (
                                <option key={c.id} value={c.nome}>{c.nome}</option>
                            ))}
                        </select>
                    </div>
                </div>

                <h5 className="mb-3 mt-3">Endereço de destino</h5>
                <div className="row">
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Logradouro (destino):</label>
                        <input
                            type="text"
                            className="form-control"
                            value={destino.logradouro}
                            onChange={(e) => setDestino((p) => ({ ...p, logradouro: e.target.value }))}
                            disabled={loading}
                        />
                    </div>
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Número (destino):</label>
                        <input
                            type="text"
                            className="form-control"
                            value={destino.numero}
                            onChange={(e) => setDestino((p) => ({ ...p, numero: e.target.value }))}
                            disabled={loading}
                        />
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Estado (destino):</label>
                        <select
                            className="form-select"
                            value={destino.estado}
                            onChange={(e) => setDestino((p) => ({ ...p, estado: e.target.value, cidade: "" }))}
                            disabled={loading}
                        >
                            <option value="">Selecione</option>
                            {estados.map((e) => (
                                <option key={e.sigla} value={e.sigla}>{e.nome}</option>
                            ))}
                        </select>
                    </div>
                    <div className="col-md-6 mb-3">
                        <label className="form-label">Cidade (destino):</label>
                        <select
                            className="form-select"
                            value={destino.cidade}
                            onChange={(e) => setDestino((p) => ({ ...p, cidade: e.target.value }))}
                            disabled={loading || !destino.estado}
                        >
                            <option value="">{destino.estado ? "Selecione" : "Selecione o estado"}</option>
                            {cidadesDestino.map((c) => (
                                <option key={c.id} value={c.nome}>{c.nome}</option>
                            ))}
                        </select>
                    </div>
                </div>

                <hr className="my-4" />
                <div className="mb-3">
                    <div className="form-check">
                        <input
                            className="form-check-input"
                            type="checkbox"
                            checked={ativo}
                            onChange={(e) => setAtivo(e.target.checked)}
                            disabled={loading}
                            id="idosoAtivo"
                        />
                        <label className="form-check-label" htmlFor="idosoAtivo">
                            Ativo
                        </label>
                    </div>
                </div>
            </form>

            {mostrarPopupSucesso && (
                <div className="modal fade show d-block" style={{ backgroundColor: "rgba(0,0,0,0.5)" }} tabIndex={-1} role="dialog">
                    <div className="modal-dialog modal-dialog-centered" role="document">
                        <div className="modal-content">
                            <div className="modal-header border-0">
                                <h5 className="modal-title text-success">Cadastro realizado</h5>
                                <button type="button" className="btn-close" aria-label="Fechar" onClick={() => setMostrarPopupSucesso(false)} />
                            </div>
                            <div className="modal-body">Idoso cadastrado com sucesso.</div>
                            <div className="modal-footer border-0">
                                <button type="button" className="btn btn-primary" onClick={() => setMostrarPopupSucesso(false)}>OK</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default CadastrarIdosoComponent;
