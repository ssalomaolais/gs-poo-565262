package br.com.fiap.gs.dominio.componente;

public class PontoAlagamento extends ComponenteTerritorial {

    private double nivelRisco;

    public PontoAlagamento(Long id, String nome, String regiao, double nivelRisco) {
        super(id, nome, regiao);
        this.nivelRisco = nivelRisco;
    }

    @Override
    public String verificarStatus() {
        String statusAtual;
        if (nivelRisco >= 80) {
            statusAtual = "CRITICO";
        } else if (nivelRisco >= 50) {
            statusAtual = "ALERTA";
        } else if (nivelRisco >= 20) {
            statusAtual = "ATENCAO";
        } else {
            statusAtual = "NORMAL";
        }
        setStatus(statusAtual);
        return statusAtual;
    }

    public double getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(double nivelRisco) { this.nivelRisco = nivelRisco; }
}