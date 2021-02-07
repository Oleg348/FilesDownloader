package com.oleg348.files_downloader.services.URLs_provider;

import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

public interface FilesURLsProvider {

    /**
     * Get current path of file that contains URLs.
     * @return File path. Or null if it wasn't set.
     */
    Path getFilePath();

    /**
     * Set file that contains URLs.
     * @param path
     * @throws IllegalArgumentException if @param path is null or invalid.
     */
    void setFilePath(String path) throws IllegalArgumentException;

    /**
     * Set file that contains URLs.
     * @param path
     * @throws IllegalArgumentException file path string is invalid.
     */
    void setFilePath(Path path) throws IllegalArgumentException;

    /**
     * Load files URLs from provided file.
     * @return List of valid URLs that contained in file.
     * @throws IllegalStateException File wasn't provided.
     * @throws FilesURLsProviderException Some internal provider error.
     */
    List<URL> getFilesURLs() throws IllegalStateException, FilesURLsProviderException;
}
