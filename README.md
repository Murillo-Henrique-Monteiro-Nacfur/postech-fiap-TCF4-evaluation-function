# Evaluation Function - Serverless Architecture

Este projeto consiste em uma **Google Cloud Function** desenvolvida com **Java 21** e **Quarkus**, utilizando compilação nativa (**GraalVM**) para otimização de "cold starts" e consumo de memória.

A função é responsável por receber avaliações de serviços, persisti-las em um banco de dados PostgreSQL e, caso a nota seja baixa (inferior a 5), publicar um aviso de urgência em um tópico do **Google Pub/Sub**.

---

## 🚀 Fluxo da Aplicação

A requisição entra na função através de um gatilho HTTP (gerenciado pelo framework Funqy do Quarkus). O fluxo de processamento é o seguinte:

1.  **Entrada:** A função recebe um JSON (`EvaluationDTO`) contendo a descrição e a nota.
2.  **Persistência:** O caso de uso `CreateEvaluationUseCase` valida os dados e salva a avaliação no banco de dados **PostgreSQL**.
3.  **Análise de Regra de Negócio:**
    *   Se a **nota for >= 5**: O processamento encerra com sucesso (HTTP 200).
    *   Se a **nota for < 5**: O caso de uso `EnviaAvisoDeUrgenciaUseCase` é acionado.
4.  **Notificação (Caminho de Urgência):**
    *   O sistema busca os e-mails dos administradores no banco de dados.
    *   Uma mensagem formatada é enviada para o **Google Pub/Sub** através do `GooglePubSubGateway`.

---

## 🛠️ Arquitetura e Tecnologias

*   **Framework:** Quarkus (Superfast Subatomic Java).
*   **Linguagem:** Java 21.
*   **Build Nativo:** GraalVM / Mandrel.
*   **Banco de Dados:** PostgreSQL (via Hibernate Panache).
*   **Mensageria:** Google Cloud Pub/Sub.
*   **Cloud Provider:** Google Cloud Platform (Cloud Run / Cloud Functions Gen 2).

---

## ⚙️ Configuração (application.properties)

O arquivo `application.properties` contém configurações cruciais para o funcionamento em ambiente Serverless e Nativo:

### Peculiaridades de Runtime e Nativo
Como estamos rodando em uma imagem nativa, certas classes e recursos precisam ser declarados explicitamente para que o GraalVM os inclua no binário final.

*   **`quarkus.native.additional-build-args`**: Argumentos passados para o compilador nativo.
*   **`quarkus.hibernate-orm.database.generation`**: Controla a criação de schemas (geralmente desligado em produção).
*   **Configurações de Pub/Sub**:
    *   `quarkus.google.cloud.project-id`: ID do projeto GCP.
    *   `pubsub.topic.name`: Nome do tópico para onde os alertas são enviados.

### Tratamento de Erros (Interceptor)
Implementamos um **Interceptor** (`FunctionExceptionHandlerInterceptor`) para gerenciar o comportamento de retry da Cloud Function:
*   **Erros de Negócio (`EvaluationFunctionException`):** São logados e a função retorna sucesso (ACK). **Motivo:** Dados inválidos não serão corrigidos com retentativas.
*   **Erros de Infraestrutura:** A exceção é relançada (NACK), forçando o GCP a tentar processar a mensagem novamente.

---

## 🐳 Docker e Build Nativo

O processo de build utiliza um `Dockerfile` multi-estágio para gerar um container extremamente leve e rápido.

### Passos do Docker (`docker build`):
1.  **Estágio de Build (Maven + GraalVM):**
    *   Utiliza uma imagem base com Maven e GraalVM (Mandrel).
    *   Executa `mvn package -Pnative`.
    *   Isso compila o código Java diretamente para código de máquina (binário Linux), eliminando a necessidade de uma JVM completa no runtime.
2.  **Estágio Runtime (Distroless/Micro):**
    *   Utiliza uma imagem base minimalista (ex: `ubi-micro` ou `distroless`).
    *   Copia apenas o binário gerado no estágio anterior.
    *   **Resultado:** Uma imagem Docker muito pequena (geralmente < 100MB) que inicia em milissegundos.

---

## 🚀 Deploy Contínuo (CI/CD)

O deploy é totalmente automatizado utilizando **Google Cloud Build**.

### Gatilho (Trigger)
Existe um gatilho configurado no GCP conectado a este repositório.

1.  **Push na branch `master`**:
    *   O desenvolvedor faz um commit/push para a branch principal.
2.  **Cloud Build Trigger**:
    *   O GCP detecta a alteração e inicia o pipeline definido no arquivo `cloudbuild.yaml` (ou configuração inline).
3.  **Build & Deploy**:
    *   O Cloud Build executa o comando Docker para criar a imagem nativa.
    *   A imagem é enviada para o **Google Container Registry (GCR)** ou **Artifact Registry**.
    *   O serviço **Cloud Run / Cloud Functions** é atualizado com a nova imagem.

    *   Este repositório inclui um `cloudbuild.yaml` que o trigger pode usar. Ele define `options.logging: CLOUD_LOGGING_ONLY` (necessário quando o trigger usa um `serviceAccount`) e contém os steps de build/push da imagem. Por padrão **não** executa as migrations — se quiser que as migrations rodem durante o build, posso re-adicionar a etapa do Flyway (e aí será necessário conceder ao Cloud Build acesso ao Secret Manager, por exemplo com `roles/secretmanager.secretAccessor`).
---

## 🗄️ Migrações de Banco de Dados (Flyway)

Este repositório inclui suporte a **migrations** com **Flyway**. A estratégia adotada é rodar as migrações durante o workflow do github actions / cloud build, antes do deploy da nova versão da aplicação.

- **Vantagens:**
  * Migrações são aplicadas automaticamente durante o pipeline CI/CD.
  * Garante que o banco de dados esteja sempre atualizado com a versão da aplicação.
  * Evita a necessidade de rodar migrações manualmente em produção.
- Permite versionar as mudanças de esquema junto com o código-fonte.
- **Configuração do Flyway:**
  * As migrations estão na pasta `src/main/resources/db/migration`.
  * O Flyway é configurado para usar variáveis de ambiente para conexão com o banco que devem ser criada no repositorio do github nas secrets
    - `FLYWAY_URL`
    - `FLYWAY_USER`
    - `FLYWAY_PASSWORD`
  - No workflow do github actions, há um step que executa o comando:
    ```bash
    ./mvnw flyway:migrate
    ```
  - Isso aplica todas as migrations pendentes antes de prosseguir com o build e deploy.
---

---

## 🧪 Como Rodar Testes

O projeto utiliza **JUnit 5**, **Mockito** e **AssertJ**.

```bash
# Rodar testes unitários
./mvnw test
```

Os testes cobrem:
*   Lógica dos Use Cases.
*   Gateways (com mocks de infraestrutura).
*   Interceptors de exceção.
*   Serialização de DTOs.
