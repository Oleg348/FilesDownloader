package com.oleg348.files_downloader.services.downloader;

import java.net.URL;
import java.nio.file.Path;

public interface ThrottlingFileDownloader {
    /**
     * Start synchronously download file.
     * 
     * @param fileUrl file URL to download from.
     * @param downloadedFilePath File path to save.
     * @param maxSpeed Max download speed in KB/s.
     * @param callback callback.
     * @throws IllegalArgumentException If
     * <p>
     * {@code fileUrl} is null;
     * <p>
     * {@code downloadedFilePath} is null;
     * <p>
     * {@code maxSpeed} is less than 1;
     * <p>
     * {@code callback} is null;
     */
    void startDownloading(URL fileUrl, Path downloadedFilePath, int maxSpeed, FileDownloadingCallback callback)
        throws IllegalArgumentException;
}