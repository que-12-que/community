package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueTest {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(20);
        new Thread(new Producer1(queue)).start();
        new Thread(new Constumer1(queue)).start();
        new Thread(new Constumer1(queue)).start();
        new Thread(new Constumer1(queue)).start();
    }
}
class Producer1 implements Runnable{
    private BlockingQueue<Integer> queue;
    public Producer1(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run () {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(10);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" + queue.size() + " 队尾元素：" + queue.peek());
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
class Constumer1 implements Runnable {
    private BlockingQueue<Integer> queue;
    public Constumer1(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run () {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                int t = queue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + queue.size() + " 消费的元素：" + t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
