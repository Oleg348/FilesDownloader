package com.oleg348.files_downloader.services;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.oleg348.files_downloader.services.downloader.FileDownloadingCallback;
import com.oleg348.files_downloader.services.downloader.ThrottlingFileDownloader;
import com.oleg348.files_downloader.services.downloader.ThrottlingFileDownloaderImpl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ThrottlingFileDownloaderTests {

    private static ThrottlingFileDownloader getSut() {
        return new ThrottlingFileDownloaderImpl();
    }

    private static final String _100kbFilePath = System.getProperty("java.io.tmpdir") + "\\100kb.bin";
    private static final File _100kbFile = new File(_100kbFilePath);
    private static URL _100kbFileURL;

    private static final int validMaxSpeed = 1;
    private static final Path validDownloadedFilePath = Paths.get(System.getProperty("java.io.tmpdir"), "test.bin");
    private static final FileDownloadingCallback validCallback = mock(FileDownloadingCallback.class);

    static {
        try {
            _100kbFileURL = _100kbFile.toURI().toURL();
        } catch (MalformedURLException e) {}
    }

    @BeforeAll
    public static void startup() throws IOException {
        _100kbFile.createNewFile();

        RandomAccessFile raf = new RandomAccessFile(_100kbFile, "rw");
        raf.setLength(1024 * 100);
        raf.close();
    }

    @AfterAll
    public static void shutdown() {
        _100kbFile.delete();
        new File(validDownloadedFilePath.toAbsolutePath().toString()).delete();
    }

    @Test
    public void startDownloading_throws_IllegalArgumentException_if_fileUrl_is_null() {
        ThrottlingFileDownloader sut = getSut();

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.startDownloading(null, validDownloadedFilePath, validMaxSpeed, validCallback)
        );
    }

    @Test
    public void startDownloading_throws_IllegalArgumentException_if_downloadedFilePath_is_null() {
        ThrottlingFileDownloader sut = getSut();

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.startDownloading(_100kbFileURL, null, validMaxSpeed, validCallback)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void startDownloading_throws_IllegalArgumentException_if_maxSpeed_is_less_than_1(int invalidSpeed) {
        ThrottlingFileDownloader sut = getSut();

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.startDownloading(_100kbFileURL, validDownloadedFilePath, invalidSpeed, validCallback)
        );
    }

    @Test
    public void startDownloading_throws_IllegalArgumentException_if_callback_is_null() {
        ThrottlingFileDownloader sut = getSut();

        assertThrows(
            IllegalArgumentException.class,
            () -> sut.startDownloading(_100kbFileURL, null, validMaxSpeed, null)
        );
    }

    @Test
    public void startDownloading_downloads_100kb_file_with_speed_33kb_not_faster_than_3sec_and_faster_than_5_sec() {
        ThrottlingFileDownloader sut = getSut();

        long startTime = System.currentTimeMillis();
        sut.startDownloading(_100kbFileURL, validDownloadedFilePath, 33, validCallback);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        assertTrue(duration >= 3000 && duration <= 5000);
    }

    @Test
    public void startDownloading_calls_callback_file_downloading_update_and_finish_for_success_downloaded_file() {
        ThrottlingFileDownloader sut = getSut();

        FileDownloadingCallback callback = mock(FileDownloadingCallback.class);
        sut.startDownloading(_100kbFileURL, validDownloadedFilePath, 50, callback);

        verify(callback, atLeast(1)).onDownloadStateChange(
            eq(_100kbFileURL),
            longThat(s -> s <= 51 * 1024),
            longThat(p -> p >= 0),
            eq(1024L * 100)
        );
        verify(callback).onFinished(_100kbFileURL, validDownloadedFilePath);
    }

    private static File getInexistentFile() {
        File inexistentFile = new File("__inexistent__.txt");
        if (inexistentFile.exists())
            inexistentFile.delete();
        return inexistentFile;
    }

    @Test
    public void startDownloading_calls_callback_on_error_if_file_is_invalid() throws MalformedURLException {
        ThrottlingFileDownloader sut = getSut();

        URL inexistentFileURL = getInexistentFile().toURI().toURL();
        FileDownloadingCallback callback = mock(FileDownloadingCallback.class);
        sut.startDownloading(inexistentFileURL, validDownloadedFilePath, 50, callback);

        verify(callback).onError(eq(inexistentFileURL), notNull());
    }
}
