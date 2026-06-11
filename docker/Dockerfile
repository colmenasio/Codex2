# Build stage
FROM gradle:7.6-jdk11 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean war --no-daemon

# Runtime stage
FROM tomcat:9.0.82-jdk11-openjdk-slim
LABEL maintainer="your-email@example.com"
LABEL version="1.0.0"

# Remove default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy built WAR file
COPY --from=builder /app/build/libs/app.war /usr/local/tomcat/webapps/ROOT.war

# Copy custom Tomcat configuration
COPY conf/server.xml /usr/local/tomcat/conf/server.xml
COPY conf/setenv.sh /usr/local/tomcat/bin/setenv.sh

# Create logs directory
RUN mkdir -p /usr/local/tomcat/logs && \
    chmod +x /usr/local/tomcat/bin/setenv.sh

# Expose ports
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# Run Tomcat
CMD ["catalina.sh", "run"]
