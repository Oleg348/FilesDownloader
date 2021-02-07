package com.oleg348.files_downloader.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.oleg348.files_downloader.services.URLs_provider.FilesURLsFromFileProvider;
import com.oleg348.files_downloader.services.URLs_provider.FilesURLsProvider;
import com.oleg348.files_downloader.services.URLs_provider.FilesURLsProviderException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FilesURlsProviderTests {
    private FilesURLsProvider getSut() {
        return new FilesURLsFromFileProvider();
    }

    @Test
    public void getFilesURLs_throws_IllegalStateException_if_file_paths_wasnt_set() {
        FilesURLsProvider sut = getSut();

        assertThrows(IllegalStateException.class, () -> sut.getFilesURLs());
    }

    private static String getInexistentFilePath() {
        File inexistentFile = new File("__inexistent__.txt");
        if (inexistentFile.exists())
            inexistentFile.delete();
        return inexistentFile.getAbsolutePath();
    }

    private static String getFolderPath() {
        File folder = new File("Test/");
        return folder.getAbsolutePath();
    }

    private static String[] getInvalidFilePaths() {
        return new String[] { null, "", getInexistentFilePath(), getFolderPath() };
    }

    @ParameterizedTest
    @MethodSource("getInvalidFilePaths")
    public void setFilePath_throws_IllegalArgumentException_if_path_string_is_invalid_or_is_not_path_to_file(
            String filePathString) {
        FilesURLsProvider sut = getSut();

        assertThrows(IllegalArgumentException.class, () -> sut.setFilePath(filePathString));
    }

    private static void saveFile(String[] lines, String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        for (int i = 0; i < lines.length; i++) {
            fw.write(lines[i]);
            fw.write(System.lineSeparator());
        }
        fw.close();
    }

    @Test
    public void getFilesURLs_loads_only_valid_URLs()
            throws IOException, IllegalStateException, FilesURLsProviderException
    {
        final String filePath = System.getProperty("java.io.tmpdir") + "valid_file.txt";
        final String validFileURL = "https://google.com/file.txt";
        String[] URLs = new String[] { "", "invalid url", validFileURL };

        saveFile(URLs, filePath);

        FilesURLsProvider sut = getSut();
        sut.setFilePath(filePath);

        List<URL> loadedURLs = sut.getFilesURLs();
        assertEquals(1, loadedURLs.size());
        assertEquals(validFileURL, loadedURLs.get(0).toString());
    }
}