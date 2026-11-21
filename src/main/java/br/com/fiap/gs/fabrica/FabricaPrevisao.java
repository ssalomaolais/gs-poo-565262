package br.com.fiap.gs.fabrica;

import br.com.fiap.gs.dominio.previsao.EstrategiaPrevisao;
import br.com.fiap.gs.dominio.previsao.PrevisaoComparacao;
import br.com.fiap.gs.dominio.previsao.PrevisaoMediaMovel;
import br.com.fiap.gs.excecao.DadosInvalidosException;
import org.springframework.stereotype.Component;

@Component
public class FabricaPrevisao {

    public EstrategiaPrevisao criar(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "MEDIA_MOVEL" -> new PrevisaoMediaMovel(5);
            case "COMPARACAO" -> new PrevisaoComparacao();
            default -> throw new DadosInvalidosException("Estrategia de previsao desconhecida: " + tipo);
        };
    }
}