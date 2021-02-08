package com.oleg348.files_downloader;

import java.net.URL;
import java.nio.file.Path;

import com.oleg348.files_downloader.model.FilesDownloaderModel;
import com.oleg348.files_downloader.services.downloader.FileDownloadingCallback;
import com.oleg348.files_downloader.ui.FilesDownloaderView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilesDownloaderApplication implements CommandLineRunner {

	private final FilesDownloaderView view;
	private final FilesDownloaderModel filesDownloaderModel;

	public static void main(String[] args) {
		SpringApplication.run(FilesDownloaderApplication.class, args);
	}

	@Autowired
	public FilesDownloaderApplication(
		FilesDownloaderView view,
		FilesDownloaderModel filesDownloaderModel
	) {
		super();

		this.view = view;
		this.filesDownloaderModel = filesDownloaderModel;
	}

	@Override
	public void run(String... args) {
		if (args.length < 1) {
			view.showError("Invalid amount of input parameters. Must be 1 at least");
			return;
		}

		try {
			filesDownloaderModel.loadFilesURLs(args[0]);

			int threadsAmount = 5;
			if (args.length > 1) {
				threadsAmount = Integer.parseInt(args[1]);
			}
			filesDownloaderModel.setMaxThreadsAmount(threadsAmount);

			int  maxSpeed = 1000;
			if (args.length > 2) {
				 maxSpeed = Integer.parseInt(args[2]);
			}
			filesDownloaderModel.setMaxDownloadSpeed(maxSpeed);

			if (args.length > 3) {
				filesDownloaderModel.setFilesLoadingPath(args[3]);
			}
		} catch (NumberFormatException nfe) {
			view.showError(nfe.getMessage());
			return;
		}
		catch (IllegalArgumentException ipe) {
			view.showError(ipe.getMessage());
			return;
		}

		filesDownloaderModel.startDownloading(getCallback());
	}

	private FileDownloadingCallback getCallback() {
		return new FileDownloadingCallback() {

				@Override
				public void onFinished(URL fileUrl, Path filePath) {
					view.finishDownloading(fileUrl);
				}

				@Override
				public void onDownloadStateChange(URL fileUrl, long newSpeed, long downloadedSize, long totalSize) {
					view.updateDownloadingState(fileUrl, newSpeed, downloadedSize, totalSize);
				}

				@Override
				public void onError(URL fileUrl, String errorMessage) {
					view.downloadError(fileUrl, errorMessage);
				}
			};
	}
}
