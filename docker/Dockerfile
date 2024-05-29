# Verwenden einer existierenden Maven-Version, die mit JDK 19 kompatibel ist
FROM maven:3.3-jdk-8 AS builder

COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Führe Maven aus, um das Projekt zu bauen
RUN mvn clean package

# Verwenden eines Tomcat-Servers, der mit JDK 19 kompatibel ist
FROM tomcat:10-jre8-openjdk-bullseye

# Kopiere die gebaute WAR-Datei in das Tomcat-Webapp-Verzeichnis
COPY --from=builder /target/EShop-1.0.0.war /usr/local/tomcat/webapps/

# Optional: Konfigurationsdateien für Tomcat kopieren
COPY ./conf/tomcat-users.xml /usr/local/tomcat/conf/

# Lege fest, welcher Port vom Container verfügbar gemacht werden soll
EXPOSE 8080
