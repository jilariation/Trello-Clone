package ru.jilly.trelloclone.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface UploadService {
    ResponseEntity<Set<String>> upload(List<MultipartFile> files);
}
