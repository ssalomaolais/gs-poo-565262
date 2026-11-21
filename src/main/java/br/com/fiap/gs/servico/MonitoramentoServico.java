package br.com.fiap.gs.servico;

import br.com.fiap.gs.dominio.dados.DadosTerritorio;
import br.com.fiap.gs.dominio.previsao.EstrategiaPrevisao;
import br.com.fiap.gs.dominio.sensor.Sensor;
import br.com.fiap.gs.entidade.Alerta;
import br.com.fiap.gs.entidade.Medicao;
import br.com.fiap.gs.excecao.DadosInvalidosException;
import br.com.fiap.gs.excecao.FonteIndisponivelException;
import br.com.fiap.gs.fabrica.FabricaPrevisao;
import br.com.fiap.gs.repositorio.AlertaRepositorio;
import br.com.fiap.gs.repositorio.MedicaoRepositorio;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MonitoramentoServico {

    private final MedicaoRepositorio medicaoRepositorio;
    private final AlertaRepositorio alertaRepositorio;
    private final FabricaPrevisao fabricaPrevisao;
    private DadosTerritorio dadosTerritorio;

    public MonitoramentoServico(MedicaoRepositorio medicaoRepositorio,
                                 AlertaRepositorio alertaRepositorio,
                                 FabricaPrevisao fabricaPrevisao) {
        this.medicaoRepositorio = medicaoRepositorio;
        this.alertaRepositorio = alertaRepositorio;
        this.fabricaPrevisao = fabricaPrevisao;
        this.dadosTerritorio = new DadosTerritorio(30.0, 60.0, 90.0);
    }

    public Medicao registrarLeitura(Sensor sensor, String identificador) {
        if (!sensor.verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor " + identificador + " nao esta funcionando.");
        }
        double valor = sensor.lerValor();
        if (valor < 0) {
            throw new DadosInvalidosException("Valor de medicao invalido: " + valor);
        }
        Medicao medicao = new Medicao(sensor.getTipo(), identificador, valor);
        medicaoRepositorio.save(medicao);
        verificarEEmitirAlerta(sensor.getTipo(), identificador, valor);
        return medicao;
    }

    private void verificarEEmitirAlerta(String tipo, String identificador, double valor) {
        String nivel = null;
        String descricao = null;
        if (valor >= dadosTerritorio.getLimiarCritico()) {
            nivel = "CRITICO";
            descricao = "Valor " + valor + " ultrapassou limiar critico (" + dadosTerritorio.getLimiarCritico() + ") no sensor " + identificador;
        } else if (valor >= dadosTerritorio.getLimiarAlerta()) {
            nivel = "ALERTA";
            descricao = "Valor " + valor + " ultrapassou limiar de alerta (" + dadosTerritorio.getLimiarAlerta() + ") no sensor " + identificador;
        } else if (valor >= dadosTerritorio.getLimiarAtencao()) {
            nivel = "ATENCAO";
            descricao = "Valor " + valor + " ultrapassou limiar de atencao (" + dadosTerritorio.getLimiarAtencao() + ") no sensor " + identificador;
        }
        if (nivel != null) {
            alertaRepositorio.save(new Alerta(nivel, descricao, tipo, valor));
        }
    }

    public double rodarPrevisao(String tipoEstrategia, String tipoSensor) {
        EstrategiaPrevisao estrategia = fabricaPrevisao.criar(tipoEstrategia);
        List<Medicao> medicoes = medicaoRepositorio.findByTipoSensorOrderByDataHoraDesc(tipoSensor);
        return estrategia.prever(medicoes);
    }

    public List<Medicao> listarMedicoesPorSensor(String identificador) {
        return medicaoRepositorio.findByIdentificadorSensorOrderByDataHoraDesc(identificador);
    }

    public List<Alerta> listarAlertas() {
        return alertaRepositorio.findAll();
    }

    public List<Alerta> listarAlertasPorNivel(String nivel) {
        return alertaRepositorio.findByNivelOrderByDataHoraDesc(nivel);
    }

    public DadosTerritorio getDadosTerritorio() { return dadosTerritorio; }

    public void atualizarLimiares(double atencao, double alerta, double critico) {
        dadosTerritorio = new DadosTerritorio(atencao, alerta, critico);
    }
}