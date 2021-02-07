package com.oleg348.files_downloader.model;

import com.oleg348.files_downloader.services.downloader.FileDownloadingCallback;

public interface FilesDownloaderModel {
    /**
     * Load files URLs to download from provided file.
     * @param filePath File to load from.
     * @throws IllegalArgumentException Invalid file path.
     */
    void loadFilesURLs(String filePath) throws IllegalArgumentException;

    /**
     * Set threads amount to use to download all the files.
     * @param threads Threads amount.
     * @throws IllegalArgumentException {@code threads} is < 1.
     */
    void setMaxThreadsAmount(int threads) throws IllegalArgumentException;

    /**
     * Set file download speed in KB/s.
     * @param speed New download speed.
     * @throws IllegalArgumentException {@code speed} < 1.
     */
    void setMaxDownloadSpeed(int speed) throws IllegalArgumentException;

    /**
     * Set directory to save downloaded files.
     * @param dirPath Path to directory.
     * @throws IllegalArgumentException Invalid path.
     */
    void setFilesLoadingPath(String dirPath) throws IllegalArgumentException;

    /**
     * Start download all the files.
     * @param callback
     * @throws IllegalStateException {@code callback} is null.
     */
    void startDownloading(FileDownloadingCallback callback) throws IllegalStateException;
}
