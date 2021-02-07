package com.oleg348.files_downloader.services.URLs_provider;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class FilesURLsFromFileProvider implements FilesURLsProvider {

    private Path filePath;

    public FilesURLsFromFileProvider() {
    }

    public FilesURLsFromFileProvider(String filePath) throws InvalidPathException {
        this.filePath = Paths.get(filePath);
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }

    @Override
    public void setFilePath(String path) throws IllegalArgumentException {
        if (path == null)
            throw new IllegalArgumentException("path can't be null");

        setFilePath(Paths.get(path));
    }

    @Override
    public void setFilePath(Path path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("Path can't be null");
        }
        File file = path.toFile();
        if (!file.exists()) {
            throw new IllegalArgumentException("Provided file doesn't exist");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Provided file path is not file.");
        }

        filePath = path;
    }

    @Override
    public List<URL> getFilesURLs() throws IllegalStateException, FilesURLsProviderException {
        if (filePath == null) {
            throw new IllegalStateException("File to load from wasn't set");
        }

        try {
            return Files.readAllLines(filePath).stream().map(urlString -> {
                try {
                    return urlString == null || urlString == "" ? null : new URL(urlString);
                } catch (MalformedURLException e) {
                    return null;
                }
            }).filter(uri -> uri != null).collect(Collectors.toList());
        } catch (IOException | SecurityException ex) {
            throw new FilesURLsProviderException("URIs file access error", ex);
        }
    }

}
