package com.oleg348.files_downloader.services.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

import com.google.common.util.concurrent.RateLimiter;

import org.springframework.stereotype.Service;

@Service
public class ThrottlingFileDownloaderImpl implements ThrottlingFileDownloader {

    @Override
    public void startDownloading(URL fileUrl, Path downloadedFilePath, int maxSpeed, FileDownloadingCallback callback)
            throws IllegalArgumentException
    {
        if (fileUrl == null)
            throw new IllegalArgumentException("file url can't be null");
        if (downloadedFilePath == null)
            throw new IllegalArgumentException("File path to save can't be null");
        if (maxSpeed < 1)
            throw new IllegalArgumentException("Download speed can't be less than 1 KB/s");
        if (callback == null)
            throw new IllegalArgumentException("Callback can't be null");

        InputStream urlStream = null;
        FileOutputStream fos = null;

        try {
            URLConnection connection = fileUrl.openConnection();
            final long fileSize = connection.getContentLengthLong();
            urlStream = connection.getInputStream();
            connection.setReadTimeout(1000);

            fos = new FileOutputStream(downloadedFilePath.toString());

            final int maxBytesToRead = maxSpeed * 1024;
            int curStreamPosition = 0;
            RateLimiter throttler = RateLimiter.create(maxBytesToRead);
            byte[] buffer = new byte[1024];
            long lastNotifyTime = System.currentTimeMillis();
            int currentSpeed = 0;
            while (true) {
                throttler.acquire(buffer.length);
                
                try {
                    int bytesRead = urlStream.read(buffer);
                    if (bytesRead == -1) {
                        fos.flush();
                        fos.close();
                        callback.onFinished(fileUrl, downloadedFilePath);
                        return;
                    }

                    fos.write(buffer, 0, bytesRead);
                    curStreamPosition += bytesRead;
                    currentSpeed += bytesRead;
                } catch (SocketTimeoutException ste) {}

                long curTime = System.currentTimeMillis();
                if (curTime - lastNotifyTime >= 1000) {
                    callback.onDownloadStateChange(fileUrl, currentSpeed, curStreamPosition, fileSize);
                    lastNotifyTime = curTime;
                    currentSpeed = 0;
                }
            }
        } catch (IOException e) {
            callback.onError(fileUrl, e.getMessage());
        }
        finally {
            try {
                if (urlStream != null)
                    urlStream.close();
                if (fos != null)
                    fos.close();
            }
            catch (IOException ioe) {}
        }        
    }
}