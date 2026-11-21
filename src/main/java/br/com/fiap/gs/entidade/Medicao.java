package br.com.fiap.gs.entidade;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicao")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoSensor;

    @Column(nullable = false)
    private String identificadorSensor;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Medicao() {}

    public Medicao(String tipoSensor, String identificadorSensor, double valor) {
        this.tipoSensor = tipoSensor;
        this.identificadorSensor = identificadorSensor;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTipoSensor() { return tipoSensor; }
    public String getIdentificadorSensor() { return identificadorSensor; }
    public double getValor() { return valor; }
    public LocalDateTime getDataHora() { return dataHora; }
}