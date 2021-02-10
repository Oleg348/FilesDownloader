package com.oleg348.files_downloader.model;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.oleg348.files_downloader.services.URLs_provider.FilesURLsProvider;
import com.oleg348.files_downloader.services.URLs_provider.FilesURLsProviderException;
import com.oleg348.files_downloader.services.downloader.FileDownloadingCallback;
import com.oleg348.files_downloader.services.downloader.ThrottlingFileDownloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilesDownloaderModelImpl implements FilesDownloaderModel {
    private final FilesURLsProvider filesURIsProvider;
    private final ThrottlingFileDownloader fileDownloader;

    private Semaphore semaphore = createSemaphore(1);
    private int maxDownloadSpeed = 500;
    private Path filesLoadingPath = Paths.get(System.getProperty("user.home") + "/Downloads/");
    private List<URL> filesURLs;

    @Autowired
    public FilesDownloaderModelImpl(FilesURLsProvider filesURLsProvider, ThrottlingFileDownloader fileDownloader) {
        super();

        if (filesURLsProvider == null)
            throw new IllegalArgumentException("filesURLsProvider can't be null");
        if (fileDownloader == null)
            throw new IllegalArgumentException("fileDownloader can't be null");

        this.filesURIsProvider = filesURLsProvider;
        this.fileDownloader = fileDownloader;
    }

    private static Semaphore createSemaphore(int permits) {
        return new Semaphore(permits, true);
    }

    private FileDownloadingCallback getWrappedCallback(FileDownloadingCallback callback) {
        return new FileDownloadingCallback() {

            @Override
            public void onDownloadStateChange(URL fileUrl, long newSpeed, long downloadedSize, long totalSize) {
                callback.onDownloadStateChange(fileUrl, newSpeed, downloadedSize, totalSize);
            }

            @Override
            public void onFinished(URL fileUrl, Path filePath) {
                callback.onFinished(fileUrl, filePath);
                semaphore.release();
            }

            @Override
            public void onError(URL fileUrl, String errorMessage) {
                callback.onError(fileUrl, errorMessage);
                semaphore.release();
            }
        };
    }

    private void runDownloadThread(URL fileUrl, int maxDownloadSpeed, Path savePath, FileDownloadingCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fileDownloader.startDownloading(fileUrl,
                        Paths.get(savePath.toString(), Paths.get(fileUrl.getPath()).getFileName().toString()),
                        maxDownloadSpeed, getWrappedCallback(callback));
            }
        }).start();
    }

    @Override
    public void loadFilesURLs(String filePath) throws IllegalArgumentException {
        filesURIsProvider.setFilePath(filePath);

        try {
            filesURLs = filesURIsProvider.getFilesURLs();
        } catch (FilesURLsProviderException e) {
            throw new IllegalArgumentException("Load file error", e);
        }
    }

    @Override
    public void setMaxThreadsAmount(int threads) throws IllegalArgumentException {
        if (threads <= 0)
            throw new IllegalArgumentException("Threads amount can't be negative or zero");

        semaphore = createSemaphore(threads);
    }

    @Override
    public void setMaxDownloadSpeed(int speed) throws IllegalArgumentException {
        if (speed <= 0)
            throw new IllegalArgumentException("Download speed can't be negative or zero");

        maxDownloadSpeed = speed;
    }

    @Override
    public void setFilesLoadingPath(String dirPath) throws IllegalArgumentException {
        if (dirPath == null)
            throw new IllegalArgumentException("dirPath can't be null");
            
        Path path = Paths.get(dirPath);
        if (!path.toFile().isDirectory()) {
            throw new IllegalArgumentException("Provided downloading path must be a directory");
        }

        this.filesLoadingPath = path;
    }

    @Override
    public void startDownloading(FileDownloadingCallback callback) throws IllegalStateException {
        if (filesURLs == null)
            throw new IllegalStateException("loadFilesURLs wasn't called");

        for (URL url : filesURLs) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            runDownloadThread(url, maxDownloadSpeed, filesLoadingPath, callback);
        }
    }
    
}
