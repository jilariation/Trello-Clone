package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.jilly.trelloclone.service.UploadService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {
    private final RestTemplate rest;
    @Value("${upload-server.url}")
    private String serverUrl;

    public static final String UPLOAD_API_PREFIX = "/api/v1/uploads";

    @Override
    public ResponseEntity<Set<String>> upload(List<MultipartFile> files) {
        List<byte[]> fileBytes = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                fileBytes.add(file.getBytes());
            } catch (IOException e) {
                log.error("Uploading failed with error: {}", e.getMessage());
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<byte[]>> requestEntity = new HttpEntity<>(fileBytes, headers);

        try {
            ResponseEntity<Set<String>> response = rest.exchange(serverUrl + UPLOAD_API_PREFIX,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    });

            if (response.getStatusCode().is2xxSuccessful()) {
                return response;
            }

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

            if (response.hasBody()) {
                return responseBuilder.body(response.getBody());
            }

            return responseBuilder.build();

        } catch (Exception e) {
            log.error("Uploading failed with error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptySet());
        }
    }
}

