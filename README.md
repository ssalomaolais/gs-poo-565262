# Monitoramento de Alagamentos — GS POO 2026.2

**Aluna:** Lais Salomao | **RM:** 565262 | Individual | FIAP — Turma fevereiro

---

## Problema e publico beneficiado

Chuvas intensas causam alagamentos em pontos vulneraveis de territorios urbanos e periurbanos,
colocando em risco moradores, prestadores de servicos comunitarios e infraestrutura publica.
O sistema monitora sensores distribuidos, registra medicoes, emite alertas e oferece previsoes
de tendencia para apoiar a tomada de decisao de equipes de defesa civil e gestao territorial.

---

## Decisoes tecnicas

**Factory Method (FabricaPrevisao):** centraliza a criacao das estrategias de previsao.
Em vez de blocos if/else espalhados pelo servico ou no menu, a fabrica recebe uma string
de configuracao e devolve a implementacao correta de EstrategiaPrevisao. Isso isola a logica
de instanciacao, facilita adicionar novas estrategias sem alterar o servico, e torna o codigo
aderente ao principio aberto/fechado. Se o tipo informado nao existe, a propria fabrica lanca
DadosInvalidosException, mantendo a validacao centralizada.

**Hierarquia abstrata (ComponenteTerritorial / EstrategiaPrevisao):** o metodo abstrato
verificarStatus() forca cada subclasse a implementar sua propria logica de classificacao de
risco, enquanto a classe pai gerencia o estado compartilhado. EstrategiaPrevisao fornece
calcularMedia() como operacao base chamada explicitamente pelas subclasses via super(),
evitando duplicacao sem expor a implementacao como publica.

**Excecoes especializadas:** DadosInvalidosException (RuntimeException) e
FonteIndisponivelException (RuntimeException) comunicam falhas de dominio sem forcar
tratamento obrigatorio em cada camada, mas garantindo mensagens claras ao usuario via
captura no menu. Nenhum catch fica vazio.

---

## Reflexao critica

O projeto demonstra que orientacao a objetos nao e apenas uma tecnica de organizacao de codigo:
e uma maneira de modelar responsabilidades. A hierarquia de ComponenteTerritorial reflete a
realidade de que pontos de alagamento tem logica de risco propria, diferente de outros possiveis
componentes territoriais (estacoes de bombeamento, represas). A interface Sensor faz com que o
servico nao dependa de implementacoes especificas, o que significa que adicionar um SensorPressao
ou um SensorVazao no futuro nao exige alterar MonitoramentoServico.

A decisao de usar H2 em memoria simplifica a entrega, mas evidencia um limite real: os dados
desaparecem ao encerrar o sistema. Em producao, o Spring Data JPA facilitaria a troca para
PostgreSQL ou MySQL apenas mudando a configuracao, sem alterar repositorios ou servicos.

A camada de previsao e deliberadamente simples: media movel e comparacao de tendencia sao
heuristicas, nao modelos preditivos. Em um contexto real, esses algoritmos seriam substituidos
por modelos hidrologicos ou de aprendizado de maquina. A estrutura de EstrategiaPrevisao como
classe abstrata, porem, ja prepara o sistema para essa evolucao sem quebrar o resto do codigo.

A maior dificuldade foi equilibrar a riqueza do dominio (sensores, territorios, alertas) com
a restricao de zero dependencias externas alem das tres permitidas. Isso exigiu simular leituras
de sensor com Random em vez de integrar APIs de telemetria, o que e apropriado para um prototipo
academico mas seria o primeiro ponto a evoluir em um produto real.

---

## Uso de IA


A IA foi utilizada para:
- Geracao do README com reflexao critica e secao de uso de IA.

Todo o codigo foi revisado e validado pela aluna para garantir aderencia aos requisitos da GS.

---

## Divisao de tarefas

Entrega individual. Todas as decisoes de arquitetura, dominio e testes foram realizadas
e revisadas por Lais Salomao (RM 565262).

---

## Como executar

### Pre-requisitos
- Java 21 ou superior
- Maven 3.8+

### Passos

```bash
cd projeto
mvn clean package -q
java -jar target/monitoramento-alagamento-1.0.0.jar
```

Ou pelo Maven diretamente:

```bash
mvn spring-boot:run
```

O sistema inicia o menu interativo no terminal. Use as opcoes numericas para navegar.

### Executar testes

```bash
mvn test
```

Todos os testes rodam sem banco externo (H2 em memoria, Mockito para repositorios).

---

## Estrutura do projeto

```
src/
  main/java/br/com/fiap/gs/
    dominio/
      componente/   ComponenteTerritorial, PontoAlagamento
      sensor/       Sensor (interface), SensorChuva, SensorNivelAgua, SensorUmidade
      dados/        DadosTerritorio
      previsao/     EstrategiaPrevisao, PrevisaoMediaMovel, PrevisaoComparacao
    excecao/        DadosInvalidosException, FonteIndisponivelException
    entidade/       Medicao, Alerta
    repositorio/    MedicaoRepositorio, AlertaRepositorio
    fabrica/        FabricaPrevisao
    servico/        MonitoramentoServico
    menu/           MenuPrincipal
    AplicacaoMonitoramento.java
  test/java/br/com/fiap/gs/
    MonitoramentoTestes.java
```