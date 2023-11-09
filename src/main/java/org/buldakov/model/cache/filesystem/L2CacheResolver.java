package org.buldakov.model.cache.filesystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Класс, отвечающий за работу L2 кэша (работа с файловой системой).
 */
public class L2CacheResolver {

    private static final String CACHE_FILE_PATH = "cache_file.txt";

    private static final String CACHE_ELEMENTS_SEPARATOR = ";";

    private static final String CACHE_FORMAT = "%s:%s" + CACHE_ELEMENTS_SEPARATOR;

    private final FileOutputStream outputStream = new FileOutputStream(CACHE_FILE_PATH, true);

    public L2CacheResolver() throws IOException {
        this.initializeCacheFile();
    }

    /**
     * Метод инициализации кэш файла.
     *
     * @throws IOException В случае неудачного создания файла.
     */
    private void initializeCacheFile() throws IOException {
        Path cacheFilePath = Path.of(CACHE_FILE_PATH);
        if (Files.notExists(cacheFilePath)) Files.createFile(cacheFilePath);
    }

    /**
     * Метод записи данных в кэш файл.
     *
     * @param key Ключ кэша.
     * @param data Значение кэша.
     * @throws IOException В случае неудачной записи.
     */
    public void writeData(String key, Object data) throws IOException {
        this.outputStream.write(CACHE_FORMAT.formatted(key, data).getBytes(StandardCharsets.UTF_8));
        this.outputStream.flush();
        System.out.println("L2 Cache log: в L2 cache был записан элемент с ключом " + key);
    }

    /**
     * Метод чтения данных из кэш файла.
     *
     * @return Массив строк, где строка - пара ключа и значения, отформатированная
     * согласно константе формата.
     *
     * @throws IOException В случае неудачного чтения.
     */
    public String[] readData() throws IOException {
        try (FileInputStream fis = new FileInputStream(CACHE_FILE_PATH)) {
            int data;
            StringBuilder sb = new StringBuilder();
            while ((data = fis.read()) != -1) {
                sb.append((char) data);
            }
            return sb.toString().split(CACHE_ELEMENTS_SEPARATOR);
        }
    }
}