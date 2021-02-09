package com.oleg348.files_downloader.model;

import com.oleg348.files_downloader.services.URLs_provider.FilesURLsProvider;
import com.oleg348.files_downloader.services.URLs_provider.FilesURLsProviderException;
import com.oleg348.files_downloader.services.downloader.FileDownloadingCallback;
import com.oleg348.files_downloader.services.downloader.ThrottlingFileDownloader;
import com.oleg348.helpers.SutContainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;

public class FilesDownloaderModelTests {

    private static class ModelSutContainer extends SutContainer<FilesDownloaderModelImpl> {

        public FilesURLsProvider urlsProvider;
        public ThrottlingFileDownloader fileDownloader;

        public ModelSutContainer() {
            super();

            urlsProvider = mock(FilesURLsProvider.class);
            fileDownloader = mock(ThrottlingFileDownloader.class);
        }

        @Override
        protected FilesDownloaderModelImpl createSut() {
            return new FilesDownloaderModelImpl(urlsProvider, fileDownloader);
        }

    }

    private static ModelSutContainer getContainer() {
        return new ModelSutContainer();
    }

    private static FilesDownloaderModelImpl getSut() {
        return getContainer().getSut();
    }

    @Test
    public void loadFilesURLs_throws_IllegalArgumentException_if_URLs_provider_setFilePath_throws_IllegalArgumentException() {
        ModelSutContainer container = getContainer();
        doThrow(IllegalArgumentException.class).when(container.urlsProvider).setFilePath(anyString());

        FilesDownloaderModelImpl sut = container.getSut();
        final String validFilePath = "test.txt";

        assertThrows(IllegalArgumentException.class, () -> sut.loadFilesURLs(validFilePath));
        verify(container.urlsProvider).setFilePath(validFilePath);
    }

    @Test
    public void loadFilesURLs_throws_IllegalArgumentException_if_URLs_provider_getFilesURLs_throws_FilesURLsProviderException()
            throws IllegalStateException, FilesURLsProviderException {
        ModelSutContainer container = getContainer();
        when(container.urlsProvider.getFilesURLs()).thenThrow(FilesURLsProviderException.class);

        FilesDownloaderModelImpl sut = container.getSut();
        final String validFilePath = "test.txt";

        assertThrows(IllegalArgumentException.class, () -> sut.loadFilesURLs(validFilePath));
        verify(container.urlsProvider).setFilePath(validFilePath);
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0 })
    public void setMaxThreadsAmount_throws_IllegalArgumentException_if_threads_amount_is_negative_or_zero(
            int invalidThreadsAmount) {
        FilesDownloaderModelImpl sut = getSut();

        assertThrows(IllegalArgumentException.class, () -> sut.setMaxThreadsAmount(invalidThreadsAmount));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0 })
    public void setMaxDownloadSpeed_throws_IllegalArgumentException_if_threads_amount_is_negative_or_zero(
            int invalidDownloadSpeed) {
        FilesDownloaderModelImpl sut = getSut();

        assertThrows(IllegalArgumentException.class, () -> sut.setMaxDownloadSpeed(invalidDownloadSpeed));
    }

    private static String[] getInvalidFolderPaths() {
        return new String[] { null, "", "file.txt" };
    }

    @ParameterizedTest
    @MethodSource("getInvalidFolderPaths")
    public void setFilesLoadingPath_throws_IllegalArgumentException_if_loading_path_is_not_folder(
            String invalidFilePath) {
        FilesDownloaderModelImpl sut = getSut();

        assertThrows(IllegalArgumentException.class, () -> sut.setFilesLoadingPath(invalidFilePath));
    }

    private static FileDownloadingCallback getCallbackMock() {
        return mock(FileDownloadingCallback.class);
    }

    @Test
    public void startDownloading_throws_IllegalStateException_if_loadFilesURLs_wasnt_called() {
        FilesDownloaderModelImpl sut = getSut();

        assertThrows(IllegalStateException.class, () -> sut.startDownloading(getCallbackMock()));
    }

    private static void createDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
            dir.deleteOnExit();
        }
    }

    @Test
    public void startDownloading_calls_startDownloading_with_right_parameters()
            throws IllegalStateException, MalformedURLException, FilesURLsProviderException
    {
        ModelSutContainer container = getContainer();
        URL fileUrl = new URL("https://google.com/file1.f");
        when(container.urlsProvider.getFilesURLs()).thenReturn(Arrays.asList(fileUrl));
        FilesDownloaderModelImpl sut = container.getSut();

        final String urlsFilePath = "file.txt";
        final int threadsAmount = 5;
        final int maxDownloadSpeed = 500;
        final String downloadPathString = "Downloads";
        createDir(downloadPathString);
        final Path downloadedFilePath = Paths.get(downloadPathString, "file1.f");
        sut.loadFilesURLs(urlsFilePath);
        sut.setMaxThreadsAmount(threadsAmount);
        sut.setMaxDownloadSpeed(maxDownloadSpeed);
        sut.setFilesLoadingPath(downloadPathString);

        sut.startDownloading(getCallbackMock());

        verify(container.fileDownloader, after(20))
            .startDownloading(
                eq(fileUrl),
                eq(downloadedFilePath),
                eq(maxDownloadSpeed),
                any()
            );
    }

    // public void startDownloading_runs_only_max_threads_downloads_simultaneously()
    //     throws MalformedURLException, IllegalStateException, FilesURLsProviderException
    // {
    //     ModelSutContainer container = getContainer();
    //     URL fileUrl1 = new URL("https://google.com/file1.f");
    //     URL fileUrl2 = new URL("https://google.com/file2.f");
    //     when(container.urlsProvider.getFilesURLs())
    //         .thenReturn(Arrays.asList(fileUrl1, fileUrl2));

    //     Boolean secondFileDownloadingStarted = false;
    //     doAnswer(inv -> {
    //         return null;
    //     })
    //     .when(container.fileDownloader).startDownloading(fileUrl1, any(), anyInt(), any());
    //     doAnswer(inv -> {
    //         return null;
    //     }).when(container.fileDownloader).startDownloading(fileUrl2, any(), anyInt(), any());
    //     FilesDownloaderModelImpl sut = container.getSut();

    //     final String urlsFilePath = "file.txt";
    //     final int threadsAmount = 5;
    //     final int maxDownloadSpeed = 500;
    //     final String downloadPathString = "Downloads";
    //     createDir(downloadPathString);
    //     sut.loadFilesURLs(urlsFilePath);
    //     sut.setMaxThreadsAmount(threadsAmount);
    //     sut.setMaxDownloadSpeed(maxDownloadSpeed);
    //     sut.setFilesLoadingPath(downloadPathString);

    //     sut.startDownloading(getCallbackMock());

    //     verify(container.fileDownloader, after(20)).startDownloading(eq(fileUrl1), any(), anyInt(), any());
    // }
}
