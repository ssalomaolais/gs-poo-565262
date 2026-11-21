package br.com.fiap.gs.dominio.previsao;

import br.com.fiap.gs.entidade.Medicao;
import java.util.List;

public abstract class EstrategiaPrevisao {

    public abstract double prever(List<Medicao> medicoes);

    protected double calcularMedia(List<Medicao> medicoes) {
        if (medicoes == null || medicoes.isEmpty()) {
            return 0.0;
        }
        return medicoes.stream()
                .mapToDouble(Medicao::getValor)
                .average()
                .orElse(0.0);
    }
}