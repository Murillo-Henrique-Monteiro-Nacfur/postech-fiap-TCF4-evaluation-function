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

Este repositório inclui suporte a **migrations** com **Flyway**. A estratégia adotada é rodar as migrações no momento do deploy (pipeline do Cloud Build) para garantir que as mudanças no schema ocorram antes do deploy da imagem.

- **Como funciona no pipeline:** o `cloudbuild.yaml` tem um step que executa o container oficial `gcr.io/flyway/flyway` apontando para `src/main/resources/db/migration` e executa `migrate` usando credenciais obtidas do **Secret Manager**.

- **Setup de segredos (exemplo):**

```bash
# Crie secrets no Secret Manager
gcloud secrets create DB_URL --data-file=- <<<"jdbc:postgresql://HOST:5432/DBNAME"
gcloud secrets create DB_USER --data-file=- <<<"myuser"
gcloud secrets create DB_PASS --data-file=- <<<"mypassword"

# Dê permissão ao service account do Cloud Build se necessário
gcloud secrets add-iam-policy-binding DB_PASS --member=serviceAccount:PROJECT_NUMBER@cloudbuild.gserviceaccount.com --role=roles/secretmanager.secretAccessor
```

**Observação (Cloud Run):** no seu ambiente atual a Cloud Run está usando o secret `db-postgresql-application-password` para a variável `DB_PASSWORD`. Se quiser manter esse nome, adicione também esse secret ao Secret Manager e garanta que o Cloud Build tenha acesso a ele (ex.: `db-postgresql-application-password`).

Após fazer deploy, associe explicitamente o secret ao serviço (se não estiver configurado):

```bash
# Associa o secret ao serviço Cloud Run (bind no runtime)
gcloud run services update evaluation-function --region=southamerica-east1 --update-secrets DB_PASSWORD=projects/$PROJECT_ID/secrets/db-postgresql-application-password:latest
```


- **Observações:**
  * Teste as migrações em um ambiente de staging antes de rodar em produção.
  * Faça backups/snapshots do banco antes de executar migrações destrutivas.
  * A pasta de migrations está em `src/main/resources/db/migration`.

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
