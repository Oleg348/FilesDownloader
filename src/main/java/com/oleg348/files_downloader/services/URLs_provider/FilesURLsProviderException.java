package com.oleg348.files_downloader.services.URLs_provider;

public class FilesURLsProviderException extends Exception {
    private static final long serialVersionUID = 1612458201;
    
    public FilesURLsProviderException() {
        super();
    }

    public FilesURLsProviderException(String message) {
        super(message);
    }

    public FilesURLsProviderException(String message, Throwable inner) {
        super(message, inner);
    }
}
