/*
Nirav Patel
INSY 4306 - 002
ID: 1001519288
 */
package language.Threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import static language.Threads.Main.EOF;

public class Main {

    public static final String EOF = "EOF"; 
    public static void main(String[] args) {
        List<String> buffer = new ArrayList<String>();

        ReentrantLock bufferlock = new ReentrantLock();
        OurProducer producer  = new OurProducer(buffer,ThreadColor.ANSI_GREEN, bufferlock);
        OurConsumer consumer1 = new OurConsumer(buffer,ThreadColor.ANSI_BLUE, bufferlock);
        OurConsumer consumer2 = new OurConsumer(buffer,ThreadColor.ANSI_PURPLE, bufferlock);
        OurConsumer consumer3 = new OurConsumer(buffer,ThreadColor.ANSI_RED, bufferlock);

        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
        new Thread(consumer3).start();
    }
}


class OurProducer implements Runnable{
    private List<String> buffer;
    private String color;
    private ReentrantLock bufferlock;

    public OurProducer(List<String> buffer, String color, ReentrantLock buffeerlock){
        this.buffer = buffer;
        this.color = color;
        this.bufferlock = buffeerlock;
    }

    @Override
    public void run() {
        Random random  = new Random();
        String [] strings = {"BruceWayne", "ClarkKent", "Racecar", "Batman", "TonyStark",
                "BruceBanner", "Marvel", "SteveRogers", "RedSkull", "Aquaman", "Superman", "Spiderman",
                "PeterParker", "DoctorStrange", "Rocket"};

        for (String words: strings){
            try{
                System.out.println(color+"Adding..." + words);
                bufferlock.lock();
                buffer.add(words);
                bufferlock.unlock();
                Thread.sleep(random.nextInt(1000));
            }catch (InterruptedException e){
                System.out.println("Producer was interrupted");
            }
        }

        System.out.println("adding EOF and exiting...");
        bufferlock.lock();
        buffer.add("EOF");
        bufferlock.unlock();
    }
}

class OurConsumer implements Runnable{
    private List<String> buffer;
    private String color;
    private  ReentrantLock bufferlock;

    public OurConsumer(List<String> buffer, String color, ReentrantLock bufferlock){
        this.buffer = buffer;
        this.color = color;
        this.bufferlock = bufferlock;
    }

    @Override
    public void run() {
        while(true){
            bufferlock.lock();
            if (buffer.isEmpty()){
                bufferlock.unlock();
                continue;
            }
            if(buffer.get(0).equals(EOF)){
                System.out.println(color + "Exiting");
                bufferlock.unlock();
                break;
            }else{
                String word = buffer.remove(0);
                //System.out.println("Removed: " + word);
                int length = word.length();
                String reversed = new StringBuilder(word).reverse().toString();
                System.out.println(color + "Length: " + length + "   Reversed: " + reversed +"\n");
            }
            bufferlock.unlock();
        }
    }
}