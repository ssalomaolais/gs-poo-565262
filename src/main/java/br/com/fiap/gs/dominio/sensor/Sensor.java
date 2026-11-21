package br.com.fiap.gs.dominio.sensor;

public interface Sensor {
    double lerValor();
    boolean verificarFuncionamento();
    String getTipo();
}