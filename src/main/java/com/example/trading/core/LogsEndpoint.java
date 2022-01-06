package com.example.trading.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Endpoint(id = "logs")
@Slf4j
public class LogsEndpoint {
    private final String logFilePath;

    @Autowired
    public LogsEndpoint(@Value("${logging.file.path}/spring.log") String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @ReadOperation(produces = MediaType.TEXT_PLAIN_VALUE)
    public String logs() {
        Path path = Paths.get(this.logFilePath);

        try {
            Stream<String> lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));
            lines.close();

            return data;
        } catch (IOException e) {
            log.error("Could not read log file", e);
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
