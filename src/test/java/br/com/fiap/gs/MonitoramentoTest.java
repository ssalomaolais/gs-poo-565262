package br.com.fiap.gs;

import br.com.fiap.gs.dominio.componente.PontoAlagamento;
import br.com.fiap.gs.dominio.dados.DadosTerritorio;
import br.com.fiap.gs.dominio.previsao.PrevisaoMediaMovel;
import br.com.fiap.gs.dominio.sensor.SensorChuva;
import br.com.fiap.gs.entidade.Medicao;
import br.com.fiap.gs.excecao.DadosInvalidosException;
import br.com.fiap.gs.excecao.FonteIndisponivelException;
import br.com.fiap.gs.fabrica.FabricaPrevisao;
import br.com.fiap.gs.repositorio.AlertaRepositorio;
import br.com.fiap.gs.repositorio.MedicaoRepositorio;
import br.com.fiap.gs.servico.MonitoramentoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoramentoTest {

    @Mock
    private MedicaoRepositorio medicaoRepositorio;

    @Mock
    private AlertaRepositorio alertaRepositorio;

    private MonitoramentoServico servico;
    private FabricaPrevisao fabricaPrevisao;

    @BeforeEach
    void configurar() {
        fabricaPrevisao = new FabricaPrevisao();
        servico = new MonitoramentoServico(medicaoRepositorio, alertaRepositorio, fabricaPrevisao);
    }

    @Test
    void deveRegistrarLeituraEPersistirMedicao() {
        SensorChuva sensor = new SensorChuva("SC-TESTE");
        when(medicaoRepositorio.save(any(Medicao.class))).thenAnswer(inv -> inv.getArgument(0));
        when(alertaRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Medicao resultado = servico.registrarLeitura(sensor, "SC-TESTE");

        assertNotNull(resultado);
        assertEquals("CHUVA_MM_H", resultado.getTipoSensor());
        verify(medicaoRepositorio, times(1)).save(any(Medicao.class));
    }

    @Test
    void deveLancarFonteIndisponivelQuandoSensorDesativado() {
        SensorChuva sensor = new SensorChuva("SC-OFF");
        sensor.setAtivo(false);

        assertThrows(FonteIndisponivelException.class,
                () -> servico.registrarLeitura(sensor, "SC-OFF"));

        verify(medicaoRepositorio, never()).save(any());
    }

    @Test
    void deveLancarDadosInvalidosParaLimiarNegativo() {
        assertThrows(DadosInvalidosException.class,
                () -> new DadosTerritorio(-1.0, 60.0, 90.0));
    }

    @Test
    void deveLancarDadosInvalidosParaEstrategiaDesconhecida() {
        assertThrows(DadosInvalidosException.class,
                () -> fabricaPrevisao.criar("INVALIDA"));
    }

    @Test
    void deveCalcularPrevisaoMediaMovelCorretamente() {
        Medicao m1 = new Medicao("CHUVA_MM_H", "SC-01", 10.0);
        Medicao m2 = new Medicao("CHUVA_MM_H", "SC-01", 20.0);
        Medicao m3 = new Medicao("CHUVA_MM_H", "SC-01", 30.0);
        List<Medicao> medicoes = Arrays.asList(m1, m2, m3);
        when(medicaoRepositorio.findByTipoSensorOrderByDataHoraDesc("CHUVA_MM_H"))
                .thenReturn(medicoes);

        double resultado = servico.rodarPrevisao("MEDIA_MOVEL", "CHUVA_MM_H");

        assertEquals(20.0, resultado, 0.01);
        verify(medicaoRepositorio, times(1)).findByTipoSensorOrderByDataHoraDesc("CHUVA_MM_H");
    }

    @Test
    void deveRetornarZeroParaPrevisaoSemMedicoes() {
        when(medicaoRepositorio.findByTipoSensorOrderByDataHoraDesc("NIVEL_AGUA_CM"))
                .thenReturn(Collections.emptyList());

        double resultado = servico.rodarPrevisao("MEDIA_MOVEL", "NIVEL_AGUA_CM");

        assertEquals(0.0, resultado, 0.001);
    }

    @Test
    void deveVerificarStatusPontoAlagamentoCritico() {
        PontoAlagamento ponto = new PontoAlagamento(1L, "Baixada", "Sul", 85.0);

        String status = ponto.verificarStatus();

        assertEquals("CRITICO", status);
        assertEquals("CRITICO", ponto.getStatus());
    }

    @Test
    void deveVerificarStatusPontoAlagamentoNormal() {
        PontoAlagamento ponto = new PontoAlagamento(2L, "Morro", "Norte", 10.0);

        String status = ponto.verificarStatus();

        assertEquals("NORMAL", status);
    }

    @Test
    void deveLancarDadosInvalidosParaLimiarAlertaMenorQueAtencao() {
        assertThrows(DadosInvalidosException.class,
                () -> new DadosTerritorio(60.0, 30.0, 90.0));
    }

    @Test
    void devePrevisaoMediaMovelUsarJanelaCorretamente() {
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(2);
        Medicao m1 = new Medicao("CHUVA_MM_H", "SC-01", 10.0);
        Medicao m2 = new Medicao("CHUVA_MM_H", "SC-01", 20.0);
        Medicao m3 = new Medicao("CHUVA_MM_H", "SC-01", 30.0);

        double resultado = previsao.prever(Arrays.asList(m1, m2, m3));

        assertEquals(25.0, resultado, 0.01);
    }
}