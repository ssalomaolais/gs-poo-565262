package br.com.fiap.gs.dominio.previsao;

import br.com.fiap.gs.entidade.Medicao;
import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        this.janela = janela;
    }

    @Override
    public double prever(List<Medicao> medicoes) {
        if (medicoes == null || medicoes.isEmpty()) {
            return super.calcularMedia(medicoes);
        }
        List<Medicao> recentes = medicoes.size() > janela
                ? medicoes.subList(medicoes.size() - janela, medicoes.size())
                : medicoes;
        return super.calcularMedia(recentes);
    }
}