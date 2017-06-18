package nikpack;

import jdk.nashorn.internal.ir.Block;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nikbird on 18.06.2017.
 */
public class Worker implements Runnable, Iterable<String> {

    private Path filePath;
    private BlockingQueue<String> words;

    public Worker(Path filePath, BlockingQueue<String> words) {
        this.filePath = filePath;
        this.words = words;
    }

    @Override
    public Iterator<String> iterator() {
        try {
            return new Scanner(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyIterator();
        }
    }

    /**
     * Выбираем из файла все токены, разделенные пробелами,
     * и добавляем в очередь
     */
    @Override
    public void run() {
         for(String word: this) {
             try {
                 words.put(word);
                 Thread.sleep(500);
             } catch (InterruptedException e) {
                 e.printStackTrace();
                 break;
             }
         }
    }

}
