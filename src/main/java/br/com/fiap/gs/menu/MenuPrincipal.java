package br.com.fiap.gs.menu;

import br.com.fiap.gs.dominio.componente.PontoAlagamento;
import br.com.fiap.gs.dominio.sensor.SensorChuva;
import br.com.fiap.gs.dominio.sensor.SensorNivelAgua;
import br.com.fiap.gs.dominio.sensor.SensorUmidade;
import br.com.fiap.gs.entidade.Alerta;
import br.com.fiap.gs.entidade.Medicao;
import br.com.fiap.gs.excecao.DadosInvalidosException;
import br.com.fiap.gs.excecao.FonteIndisponivelException;
import br.com.fiap.gs.servico.MonitoramentoServico;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
public class MenuPrincipal implements CommandLineRunner {

    private final MonitoramentoServico servico;

    private final SensorChuva sensorChuva = new SensorChuva("SC-01");
    private final SensorNivelAgua sensorNivelAgua = new SensorNivelAgua("SNA-01");
    private final SensorUmidade sensorUmidade = new SensorUmidade("SU-01");
    private final PontoAlagamento pontoA = new PontoAlagamento(1L, "Varzea do Rio", "Sul", 0.0);
    private final PontoAlagamento pontoB = new PontoAlagamento(2L, "Baixada Central", "Norte", 0.0);

    public MenuPrincipal(MonitoramentoServico servico) {
        this.servico = servico;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
                processarOpcao(opcao, scanner);
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Opcao invalida. Digite um numero.");
            } catch (DadosInvalidosException e) {
                System.out.println("[ERRO DE DADOS] " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println("[SENSOR INDISPONIVEL] " + e.getMessage());
            }
        }
        scanner.close();
        System.out.println("Sistema encerrado.");
    }

    private void exibirMenu() {
        System.out.println("\n=== MONITORAMENTO DE ALAGAMENTOS ===");
        System.out.println("1 - Verificar sensores");
        System.out.println("2 - Rodar previsoes");
        System.out.println("3 - Ver historico");
        System.out.println("4 - Gerenciar limiares");
        System.out.println("5 - Status completo dos pontos");
        System.out.println("6 - Exportar historico para CSV");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }

    private void processarOpcao(int opcao, Scanner scanner) {
        switch (opcao) {
            case 1 -> verificarSensores();
            case 2 -> rodarPrevisoes(scanner);
            case 3 -> verHistorico(scanner);
            case 4 -> gerenciarLimiares(scanner);
            case 5 -> statusCompleto();
            case 6 -> exportarHistoricoCSV(scanner);
            case 0 -> {}
            default -> System.out.println("[AVISO] Opcao nao reconhecida.");
        }
    }

    private void verificarSensores() {
        System.out.println("\n--- Leituras dos Sensores ---");
        Medicao mChuva = servico.registrarLeitura(sensorChuva, sensorChuva.getIdentificador());
        System.out.printf("Chuva: %.1f mm/h%n", mChuva.getValor());
        Medicao mNivel = servico.registrarLeitura(sensorNivelAgua, sensorNivelAgua.getIdentificador());
        System.out.printf("Nivel agua: %.1f cm%n", mNivel.getValor());
        Medicao mUmidade = servico.registrarLeitura(sensorUmidade, sensorUmidade.getIdentificador());
        System.out.printf("Umidade: %.1f %%%n", mUmidade.getValor());
        pontoA.setNivelRisco(mNivel.getValor() / 3.0);
        pontoB.setNivelRisco(mChuva.getValor() / 1.2);
    }

    private void rodarPrevisoes(Scanner scanner) {
        System.out.println("\nEstrategia: 1-Media Movel  2-Comparacao");
        System.out.print("Escolha: ");
        String escolha = scanner.nextLine().trim();
        String estrategia = escolha.equals("2") ? "COMPARACAO" : "MEDIA_MOVEL";
        System.out.println("\n--- Previsoes ---");
        double prevChuva = servico.rodarPrevisao(estrategia, "CHUVA_MM_H");
        System.out.printf("Chuva prevista: %.1f mm/h%n", prevChuva);
        double prevNivel = servico.rodarPrevisao(estrategia, "NIVEL_AGUA_CM");
        System.out.printf("Nivel agua previsto: %.1f cm%n", prevNivel);
        double prevUmidade = servico.rodarPrevisao(estrategia, "UMIDADE_PORCENTO");
        System.out.printf("Umidade prevista: %.1f %%%n", prevUmidade);
    }

    private void verHistorico(Scanner scanner) {
        System.out.println("\n1-Historico por sensor  2-Alertas");
        System.out.print("Escolha: ");
        String escolha = scanner.nextLine().trim();
        if (escolha.equals("1")) {
            System.out.println("\n--- Medicoes SC-01 ---");
            List<Medicao> medicoes = servico.listarMedicoesPorSensor("SC-01");
            if (medicoes.isEmpty()) {
                System.out.println("Sem registros.");
            } else {
                medicoes.forEach(m -> System.out.printf("[%s] %s: %.1f%n", m.getDataHora(), m.getTipoSensor(), m.getValor()));
            }
        } else {
            System.out.println("\n--- Alertas ---");
            List<Alerta> alertas = servico.listarAlertas();
            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta registrado.");
            } else {
                alertas.forEach(a -> System.out.printf("[%s] %s - %s%n", a.getDataHora(), a.getNivel(), a.getDescricao()));
            }
        }
    }

    private void gerenciarLimiares(Scanner scanner) {
        System.out.println("\nLimiares atuais:");
        System.out.printf("Atencao: %.1f | Alerta: %.1f | Critico: %.1f%n",
                servico.getDadosTerritorio().getLimiarAtencao(),
                servico.getDadosTerritorio().getLimiarAlerta(),
                servico.getDadosTerritorio().getLimiarCritico());
        System.out.println("Novo limiar de ATENCAO:");
        double atencao = Double.parseDouble(scanner.nextLine().trim());
        System.out.println("Novo limiar de ALERTA:");
        double alerta = Double.parseDouble(scanner.nextLine().trim());
        System.out.println("Novo limiar CRITICO:");
        double critico = Double.parseDouble(scanner.nextLine().trim());
        servico.atualizarLimiares(atencao, alerta, critico);
        System.out.println("Limiares atualizados.");
    }

    private void statusCompleto() {
        System.out.println("\n--- Status dos Pontos de Alagamento ---");
        System.out.printf("%s (%s): %s | Risco: %.1f%%%n",
                pontoA.getNome(), pontoA.getRegiao(), pontoA.verificarStatus(), pontoA.getNivelRisco());
        System.out.printf("%s (%s): %s | Risco: %.1f%%%n",
                pontoB.getNome(), pontoB.getRegiao(), pontoB.verificarStatus(), pontoB.getNivelRisco());
        System.out.println("\nSensores:");
        System.out.printf("SC-01 ativo: %b%n", sensorChuva.verificarFuncionamento());
        System.out.printf("SNA-01 ativo: %b%n", sensorNivelAgua.verificarFuncionamento());
        System.out.printf("SU-01 ativo: %b%n", sensorUmidade.verificarFuncionamento());
    }

    private void exportarHistoricoCSV(Scanner scanner) {
        System.out.println("\nExportar: 1-Medicoes  2-Alertas  3-Ambos");
        System.out.print("Escolha: ");
        String escolha = scanner.nextLine().trim();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        if (escolha.equals("1") || escolha.equals("3")) {
            String arquivo = "medicoes_" + timestamp + ".csv";
            List<Medicao> medicoes = new java.util.ArrayList<>(servico.listarMedicoesPorSensor("SC-01"));
            medicoes.addAll(servico.listarMedicoesPorSensor("SNA-01"));
            medicoes.addAll(servico.listarMedicoesPorSensor("SU-01"));
            try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
                pw.println("id,tipoSensor,identificadorSensor,valor,dataHora");
                for (Medicao m : medicoes) {
                    pw.printf("%s,%s,%s,%.2f,%s%n",
                            m.getId() != null ? m.getId() : "",
                            m.getTipoSensor(),
                            m.getIdentificadorSensor(),
                            m.getValor(),
                            m.getDataHora());
                }
                System.out.println("Medicoes exportadas: " + arquivo + " (" + medicoes.size() + " registros)");
            } catch (IOException e) {
                System.out.println("[ERRO] Falha ao gravar CSV de medicoes: " + e.getMessage());
            }
        }
        if (escolha.equals("2") || escolha.equals("3")) {
            String arquivo = "alertas_" + timestamp + ".csv";
            List<Alerta> alertas = servico.listarAlertas();
            try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
                pw.println("id,nivel,tipoSensor,valorMedicao,descricao,dataHora");
                for (Alerta a : alertas) {
                    String descSanitizada = a.getDescricao().replace(",", ";").replace("\n", " ");
                    pw.printf("%s,%s,%s,%.2f,%s,%s%n",
                            a.getId() != null ? a.getId() : "",
                            a.getNivel(),
                            a.getTipoSensor(),
                            a.getValorMedicao(),
                            descSanitizada,
                            a.getDataHora());
                }
                System.out.println("Alertas exportados: " + arquivo + " (" + alertas.size() + " registros)");
            } catch (IOException e) {
                System.out.println("[ERRO] Falha ao gravar CSV de alertas: " + e.getMessage());
            }
        }
        if (!escolha.equals("1") && !escolha.equals("2") && !escolha.equals("3")) {
            System.out.println("[AVISO] Opcao invalida. Nenhum arquivo gerado.");
        }
    }
}