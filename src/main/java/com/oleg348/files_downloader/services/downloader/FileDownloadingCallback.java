package com.oleg348.files_downloader.services.downloader;

import java.net.URL;
import java.nio.file.Path;

public interface FileDownloadingCallback {
    /**
     * Raised when file downloading state changed.
     * @param fileUrl          Downloading file URL.
     * @param newSpeed         New file downloading speed in bytes.
     * @param downloadedSize   Current downloaded file size in bytes.
     * @param totalSizeCurrent Total file size in bytes.
     */
    void onDownloadStateChange(URL fileUrl, long newSpeed, long downloadedSize, long totalSize);
    
    /**
     * Raised when file downloading is successfully finished.
     * @param fileUrl  Downloaded file URL.
     * @param filePath Saved file path.
     */
    void onFinished(URL fileUrl, Path filePath);

    /**
     * Raised when file downloading is interrupted with error.
     * 
     * @param fileUrl      Downloaded file URL.
     * @param errorMessage Error description.
     */
    void onError(URL fileUrl, String errorMessage);
}
