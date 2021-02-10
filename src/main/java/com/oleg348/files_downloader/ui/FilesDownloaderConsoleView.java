package com.oleg348.files_downloader.ui;

import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class FilesDownloaderConsoleView implements FilesDownloaderView {
    // private int currentCaretLineIndex = 0;
    // private Map<URL, Integer>  urlsLineIndexes = new HashMap<>();

    // private static String padRight(String source, int length) {
    //     return String.format( "%-" + length + "s", source);
    // }

    // private void putCaretOnLine(int lineIndex) {
    //     int steps = lineIndex - currentCaretLineIndex;
    //     if (steps != 0) {
    //         String caretMover = null;
    //         if (steps < 0) {
    //             caretMover = "\u001b[1A\r";
    //             steps *= -1;
    //         }
    //         else {
    //             caretMover = "\n\r";
    //         }

    //         StringBuilder sb = new StringBuilder();
    //         for (int i = 0; i < steps; i++) {
    //             sb.append(caretMover);
    //         }

    //         System.out.print(sb.toString());
    //         currentCaretLineIndex = lineIndex;
    //     }
    //     else {
    //         System.out.print("\r");
    //     }
    // }

    // private int getURLIndex(URL fileUrl) {
    //     Integer fileIndex = urlsLineIndexes.get(fileUrl);

    //     if (fileIndex == null) {
    //         int curSize = urlsLineIndexes.size();
    //         urlsLineIndexes.put(fileUrl, curSize);
    //         return curSize;
    //     }
    //     else {
    //         return fileIndex;
    //     }
    // }

    // private void putCaretOnFileIndex(URL fileUrl) {
    //     int urlLine = getURLIndex(fileUrl);
    //     putCaretOnLine(urlLine);
    // }

    private void changeURLDownloadString(URL fileUrl, String s) {
        // putCaretOnFileIndex(fileUrl);
        System.out.println(new StringBuilder().append(s).toString());
    }

    @Override
    public void showError(String errorMessage) {
        System.out.print(errorMessage);
    }

    @Override
    public void updateDownloadingState(URL fileUrl, long downloadingSpeed, long downloadedSize, long totalSize) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileUrl)
            .append(" | ")
            .append(String.format("%.2f", (downloadedSize * 100f) / totalSize))
            .append("% | ")
            .append(downloadingSpeed / 1024)
            .append(" KB/s")
            ;
        changeURLDownloadString(fileUrl, sb.toString());
    }

    @Override
    public void finishDownloading(URL fileUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileUrl)
            .append(" | Загружен.")
            ;
        changeURLDownloadString(fileUrl, sb.toString());
    }

    @Override
    public void downloadError(URL fileUrl, String errorMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileUrl)
            .append(" | Ошибка загрузки: ")
            .append(errorMessage)
            ;
        changeURLDownloadString(fileUrl, sb.toString());
    }
    
}
