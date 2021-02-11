Запуск через консоль:  
&nbsp;&nbsp;&nbsp; java -jar files_downloader-0.0.1-SNAPSHOT.jar <path_to_files_urls_file> <threads_amount> <max_speed_in_KB_sec> <folder_path_to_put_files>
- threads_amount >= 1 - один поток = один файл (5 по умолчанию);
- max_speed_in_KB_sec >= 1 - ограничение скорости загрузки для одного файла(1000 по умолчанию);
- folder_path_to_put_files - папка для загруженных файлов (<current_user>/downloads/ по умолчанию);
