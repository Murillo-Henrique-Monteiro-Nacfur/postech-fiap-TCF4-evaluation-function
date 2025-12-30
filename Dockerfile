# Estágio 1: Build Nativo
FROM vegardit/graalvm-maven:latest-java21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Aumenta memória disponível para o build nativo (necessário para Cloud Build)
ENV MAVEN_OPTS="-Xmx4g -XX:MaxRAMPercentage=75.0"
RUN mvn package -Pnative -DskipTests

# Estágio 2: Imagem final com Debian Slim - leve e comprovadamente compatível
# Usamos debian:12-slim porque provamos durante a depuração que ela contém todas as libs necessárias.
FROM debian:12-slim

WORKDIR /work/

# Copia o executável do estágio de build
COPY --from=build /app/target/*-runner /work/application

# Garante que o executável tenha permissão de execução (boa prática)
RUN chmod +x /work/application

EXPOSE 8080

# Define o comando para iniciar a aplicação quando o contêiner rodar
ENTRYPOINT ["/work/application"]
