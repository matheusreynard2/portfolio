package com.prodify.produto_service.shared.utils;

import java.util.List;
import java.util.function.Function;

/*─────────────────────────────────────────────────────────────*
 *  MappingThread   —   genérica (qualquer origem ➜ destino)
 *─────────────────────────────────────────────────────────────*/
public class PartirListaEmDuasThread<S,D> extends Thread {
    private final List<S>               origem;      // fatia da lista-fonte
    private final List<D>               destino;     // lista concorrente destino
    private final Function<? super S, ? extends D> mapperFn;

    public PartirListaEmDuasThread(List<S> origem,
                  List<D> destino,
                  Function<? super S, ? extends D> mapperFn,
                  String nomeThread) {
        this.origem    = origem;
        this.destino   = destino;
        this.mapperFn  = mapperFn;
        setName(nomeThread);
    }

    @Override
    public void run() {
        for (S item : origem) {
            destino.add(mapperFn.apply(item));  // grava na lista sincronizada
        }
    }
}

/* ======== COMO USAR A CLASSE =================

/* lista destino thread-safe 
List<ProdutoDTO> destino =
        Collections.synchronizedList(new ArrayList<>(full.size()));

/* função de mapeamento Produto ➜ ProdutoDTO 
Function<Produto, ProdutoDTO> prodMapper = p -> mapper.map(p, ProdutoDTO.class);

/* instância das duas threads genéricas 
Thread t1 = new MappingThread<>(left , destino, prodMapper, "Mapper-L");
Thread t2 = new MappingThread<>(right, destino, prodMapper, "Mapper-R");
t1.start();
t2.start();

/* aguarda ambas concluírem 
t1.join();
t2.join();

 */