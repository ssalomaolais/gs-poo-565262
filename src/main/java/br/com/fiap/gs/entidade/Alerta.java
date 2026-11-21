package br.com.fiap.gs.entidade;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nivel;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String tipoSensor;

    @Column(nullable = false)
    private double valorMedicao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Alerta() {}

    public Alerta(String nivel, String descricao, String tipoSensor, double valorMedicao) {
        this.nivel = nivel;
        this.descricao = descricao;
        this.tipoSensor = tipoSensor;
        this.valorMedicao = valorMedicao;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNivel() { return nivel; }
    public String getDescricao() { return descricao; }
    public String getTipoSensor() { return tipoSensor; }
    public double getValorMedicao() { return valorMedicao; }
    public LocalDateTime getDataHora() { return dataHora; }
}