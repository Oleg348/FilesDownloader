package com.oleg348.files_downloader;

import com.oleg348.files_downloader.model.FilesDownloaderModel;
import com.oleg348.files_downloader.services.downloader.FileDownloadingCallback;
import com.oleg348.files_downloader.ui.FilesDownloaderView;
import com.oleg348.helpers.SutContainer;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesDownloaderApplicationTests {

	private static final class AppSutContainer extends SutContainer<FilesDownloaderApplication> {
		public FilesDownloaderView view;
		public FilesDownloaderModel model;

		public AppSutContainer() {
			super();

			view = mock(FilesDownloaderView.class);
			model = mock(FilesDownloaderModel.class);
		}

		@Override
		protected FilesDownloaderApplication createSut() {
			return new FilesDownloaderApplication(view, model);
		}
	}

	private static AppSutContainer getContainer() {
		return new AppSutContainer();
	}

	@Test
	public void shows_error_if_there_is_no_input_parameters() {
		AppSutContainer container = getContainer();
		FilesDownloaderApplication sut = container.getSut();

		sut.run();

		verify(container.view).showError(notNull());
	}

	private static String[] runWithValidParameters(FilesDownloaderApplication sut) {
		String[] args = new String[] { "test.txt", "2", "500", "Downloads/" };
		sut.run(args);
		return args;
	}

	@Test
	public void shows_error_model_loadFilesURLs_throws_IllegalArgumentException() {
		AppSutContainer container = getContainer();
		doThrow(IllegalArgumentException.class).when(container.model).loadFilesURLs(anyString());

		FilesDownloaderApplication sut = container.getSut();
		String[] args = runWithValidParameters(sut);

		verify(container.model).loadFilesURLs(args[0]);
		verify(container.view).showError(any());
	}

	@Test
	public void shows_error_model_setMaxThreadsAmount_throws_IllegalArgumentException() {
		AppSutContainer container = getContainer();
		doThrow(IllegalArgumentException.class).when(container.model).setMaxThreadsAmount(anyInt());

		FilesDownloaderApplication sut = container.getSut();
		String[] args = runWithValidParameters(sut);

		verify(container.model).setMaxThreadsAmount(Integer.parseInt(args[1]));
		verify(container.view).showError(any());
	}

	@Test
	public void shows_error_model_setMaxDownloadSpeed_throws_IllegalArgumentException() {
		AppSutContainer container = getContainer();
		doThrow(IllegalArgumentException.class).when(container.model).setMaxDownloadSpeed(anyInt());

		FilesDownloaderApplication sut = container.getSut();
		String[] args = runWithValidParameters(sut);

		verify(container.model).setMaxDownloadSpeed(Integer.parseInt(args[2]));
		verify(container.view).showError(any());
	}

	@Test
	public void shows_error_model_setFilesLoadingPath_throws_IllegalArgumentException() {
		AppSutContainer container = getContainer();
		doThrow(IllegalArgumentException.class).when(container.model).setFilesLoadingPath(anyString());

		FilesDownloaderApplication sut = container.getSut();
		String[] args = runWithValidParameters(sut);

		verify(container.model).setFilesLoadingPath(args[3]);
		verify(container.view).showError(any());
	}

	@Test
	public void calls_startDownloading_if_input_is_valid() {
		AppSutContainer container = getContainer();

		FilesDownloaderApplication sut = container.getSut();
		runWithValidParameters(sut);

		verify(container.model).startDownloading(notNull());
	}

	@Test
	public void file_download_callback_verify_view_calls() throws MalformedURLException {
		AppSutContainer container = getContainer();

		final URL fileUrl = new URL("https://google.com/file.txt");
		final String error = "test error";
		final int speed = 1;
		final int sizeDownloaded = 2;
		final int totalSize = 3;
		final Path downloadedFilePath = Paths.get("Downloads/");
		doAnswer(invocation -> {
			FileDownloadingCallback callback = (FileDownloadingCallback)invocation.getArguments()[0];
			callback.onDownloadStateChange(fileUrl, speed, sizeDownloaded, totalSize);
			callback.onFinished(fileUrl, downloadedFilePath);
			callback.onError(fileUrl, error);
			return null;
		}).when(container.model).startDownloading(any());

		FilesDownloaderApplication sut = container.getSut();
		runWithValidParameters(sut);

		verify(container.view).updateDownloadingState(fileUrl, speed, sizeDownloaded, totalSize);
		verify(container.view).finishDownloading(fileUrl);
		verify(container.view).downloadError(fileUrl, error);
	}
}
