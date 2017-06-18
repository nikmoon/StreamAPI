package nikpack;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by nikbird on 18.06.2017.
 */
public class Reporter extends Thread implements Iterator<String> {

    private BlockingQueue<String> words;
    private String nextWord;
    private int sum;

    public static Integer toPositiveInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid token: [" + s + "]");
            return null;
        }
    }

    public Reporter(BlockingQueue<String> words) {
        this.words = words;
        nextWord = "";
        sum = 0;
    }

    public int summarize(int value) {
        sum += value;
        return sum;
    }

    @Override
    public void run() {
        Stream<String> stream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(this, Spliterator.NONNULL), false);
        stream.map(val -> toPositiveInt(val))
                .filter(i -> i != null)
                .map(i -> summarize(i))
                .forEach(sum -> System.out.println("current sum = " + sum));
    }

    @Override
    public boolean hasNext() {
        try {
            nextWord = words.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return nextWord != "" ? true : false;
    }

    @Override
    public String next() {
        return nextWord;
    }
}
