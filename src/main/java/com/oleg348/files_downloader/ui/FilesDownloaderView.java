package com.oleg348.files_downloader.ui;

import java.net.URL;

public interface FilesDownloaderView {
    /**
     * Shows error to user.
     * @param errorMessage
     */
    void showError(String errorMessage);

    /**
     * Update state of downloading file.
     * @param fileUrl file that is downloading.
     * @param downloadingSpeed downloading speed in bytes.
     * @param downloadedSize current downloaded size in bytes.
     * @param totalSize total file size in bytes.
     */
    void updateDownloadingState(URL fileUrl, long downloadingSpeed, long downloadedSize, long totalSize);

    /**
     * Notify user that file was successfully downloaded.
     * @param fileUrl
     */
    void finishDownloading(URL fileUrl);

    /**
     * Notify user that file downloading fails.
     * @param fileUrl
     * @param errorMessage
     */
    void downloadError(URL fileUrl, String errorMessage);
}
