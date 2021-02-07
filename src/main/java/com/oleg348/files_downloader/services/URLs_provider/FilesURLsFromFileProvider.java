package com.oleg348.files_downloader.services.URLs_provider;

import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class FilesURLsFromFileProvider implements FilesURLsProvider {

    @Override
    public Path getFilePath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFilePath(String path) throws InvalidPathException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFilePath(Path path) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public List<URL> getFilesURLs() throws IllegalStateException, FilesURLsProviderException {
        // TODO Auto-generated method stub
        return null;
    }

}
