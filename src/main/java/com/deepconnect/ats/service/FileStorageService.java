package com.deepconnect.ats.service;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.model.AppFile;
import com.deepconnect.ats.repository.AppFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/** Stores uploaded files on the local disk under {@code ats.storage.root}; metadata lives in ats_app_file. */
@Service
@RequiredArgsConstructor
public class FileStorageService {

    public static final String RESUME_ROOT_PATH = "resume/original";
    public static final String RESUME_ATTACHMENT_PATH = "resume/attachment";

    private final AppFileRepository appFileRepository;

    @Value("${ats.storage.root:./uploads}")
    private String storageRoot;

    public Map<String, Object> upload(Long userId, MultipartFile file, String path) {
        String fileName = Paths.get(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename())
                .getFileName().toString();
        AppFile appFile = appFileRepository.save(
                AppFile.builder().fileName(fileName).filePath(path).uploadUserId(userId).build());
        try {
            Path target = resolve(appFile);
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            appFileRepository.delete(appFile);
            throw new InternalServerException("Failed to store file: " + e.getMessage());
        }
        Map<String, Object> rsp = new LinkedHashMap<>();
        rsp.put("uid", appFile.getId());
        rsp.put("file_name", appFile.getFileName());
        return rsp;
    }

    public AppFile getMeta(Long id) {
        return appFileRepository.findById(id).orElseThrow(() -> new InternalServerException("File not found"));
    }

    public Path getPath(AppFile appFile) {
        Path p = resolve(appFile);
        if (!Files.isRegularFile(p)) {
            throw new InternalServerException("File not found");
        }
        return p;
    }

    private Path resolve(AppFile f) {
        Path root = Paths.get(storageRoot).toAbsolutePath().normalize();
        Path p = root.resolve(f.getFilePath()).resolve(String.valueOf(f.getId())).normalize();
        if (!p.startsWith(root)) {
            throw new InternalServerException("Invalid file path");
        }
        return p;
    }
}
