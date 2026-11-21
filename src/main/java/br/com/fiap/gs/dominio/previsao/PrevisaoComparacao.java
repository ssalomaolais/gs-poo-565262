package br.com.fiap.gs.dominio.previsao;

import br.com.fiap.gs.entidade.Medicao;
import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    @Override
    public double prever(List<Medicao> medicoes) {
        double mediaGeral = super.calcularMedia(medicoes);
        if (medicoes == null || medicoes.size() < 2) {
            return mediaGeral;
        }
        double ultimo = medicoes.get(medicoes.size() - 1).getValor();
        double penultimo = medicoes.get(medicoes.size() - 2).getValor();
        double tendencia = ultimo - penultimo;
        return Math.round((mediaGeral + tendencia) * 10.0) / 10.0;
    }
}