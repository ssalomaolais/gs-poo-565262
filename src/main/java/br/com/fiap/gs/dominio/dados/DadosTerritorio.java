package br.com.fiap.gs.dominio.dados;

import br.com.fiap.gs.excecao.DadosInvalidosException;

public class DadosTerritorio {

    private double limiarAtencao;
    private double limiarAlerta;
    private double limiarCritico;

    public DadosTerritorio(double limiarAtencao, double limiarAlerta, double limiarCritico) {
        setLimiarAtencao(limiarAtencao);
        setLimiarAlerta(limiarAlerta);
        setLimiarCritico(limiarCritico);
    }

    public double getLimiarAtencao() { return limiarAtencao; }
    public double getLimiarAlerta() { return limiarAlerta; }
    public double getLimiarCritico() { return limiarCritico; }

    public void setLimiarAtencao(double valor) {
        if (valor < 0) {
            throw new DadosInvalidosException("Limiar de atencao nao pode ser negativo: " + valor);
        }
        this.limiarAtencao = valor;
    }

    public void setLimiarAlerta(double valor) {
        if (valor <= limiarAtencao) {
            throw new DadosInvalidosException("Limiar de alerta deve ser maior que o de atencao.");
        }
        this.limiarAlerta = valor;
    }

    public void setLimiarCritico(double valor) {
        if (valor <= limiarAlerta) {
            throw new DadosInvalidosException("Limiar critico deve ser maior que o de alerta.");
        }
        this.limiarCritico = valor;
    }
}