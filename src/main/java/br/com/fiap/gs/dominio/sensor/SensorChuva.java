package br.com.fiap.gs.dominio.sensor;

import br.com.fiap.gs.excecao.FonteIndisponivelException;
import java.util.Random;

public class SensorChuva implements Sensor {

    private final String identificador;
    private boolean ativo;
    private final Random random = new Random();

    public SensorChuva(String identificador) {
        this.identificador = identificador;
        this.ativo = true;
    }

    @Override
    public double lerValor() {
        if (!ativo) {
            throw new FonteIndisponivelException("Sensor de chuva " + identificador + " indisponivel.");
        }
        return Math.round(random.nextDouble() * 120 * 10.0) / 10.0;
    }

    @Override
    public boolean verificarFuncionamento() { return ativo; }

    @Override
    public String getTipo() { return "CHUVA_MM_H"; }

    public String getIdentificador() { return identificador; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}