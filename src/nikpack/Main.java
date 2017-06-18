package nikpack;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        useStreamAPI(Paths.get("resources"));
    }

    /**
     * Получение данных из нескольких файлов и параллельная обработка.
     * По возможности используется stream API
     *
     * @param dirPath
     */
    public static void useStreamAPI(Path dirPath) {

        // очередь, в которую обработчики файлов сохраняют считанные слова,
        // а поток отчета их забирает
        final BlockingQueue<String> words = new ArrayBlockingQueue<String>(200);

        // в заданном каталоге находим файлы с раширением .txt
        // и сохраняем их в список
        List<Path> textPaths = new ArrayList<>();
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath, "*.txt")) {
            dirStream.forEach(textPaths::add);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // создаем поток для обработки значений, получаемых из файлов
        Reporter reporter = new Reporter(words);
        reporter.start();


        // создаем пул потоков и запускаем его в работу
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        textPaths.stream()
                .map(path -> new Worker(path, words))
                .forEach(threadPool::submit);

        // завершаем работу ExecutorService
        threadPool.shutdown();
        try {
            // ждем завершения уже запущенных потоков задач
            threadPool.awaitTermination(1, TimeUnit.MINUTES);
            if (!threadPool.isTerminated()) {
                System.err.println("Error: have freeze threads!");
            }

            // завершаем поток вывода отчета
            words.put("");
            reporter.join(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
