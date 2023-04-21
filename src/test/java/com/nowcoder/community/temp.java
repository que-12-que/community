package com.nowcoder.community;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主线程等待子线程执行完成再执行
 */
public class temp {
    public static void main(String[] args) {
        double scores[] = {74, 72.2, 76.2, 91.6, 83.2, 83.1, 85, 92.5, 68, 82.8, 72.6, 81.2, 78.6,
                            82.4, 87, 84.2, 80.4, 60, 71.8, 82, 76.8, 79, 83.2, 84.2, 84, 84.2, 78.2,
                            83, 84.2, 89.2, 67, 85.6, 82, 83.6, 83.8, 79.4, 88.8, 86, 80.8, 84.4,82,
                            85, 84.8, 86.6, 90, 83.6, 88.4, 84.8, 80.2, 88.8, 88, 84.8, 88.4, 89.6};
        int len = scores.length;
        System.out.println("scores.length:" + len);
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum = sum + scores[i]/10 - 5;
        }
        System.out.println("sum:" + sum);
        System.out.println("gpa:" + sum/len);
        HashMap map = new HashMap();
//        Integer a = new Integer(100);
//        Integer b = new Integer(100);
//        System.out.println(a==b);
//        Integer c = 200;
//        Integer d = 200;
//        System.out.println(c==d);
//        ExecutorService service = Executors.newFixedThreadPool(3);
//        final CountDownLatch latch = new CountDownLatch(3);
//        for (int i = 0; i < 3; i++) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        System.out.println("子线程" + Thread.currentThread().getName() + "开始执行");
//                        Thread.sleep((long) (Math.random() * 10));
//                        System.out.println("子线程"+Thread.currentThread().getName()+"执行完成");
//                        latch.countDown();//当前线程调用此方法，则计数减一
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            service.execute(runnable);
//        }
//
//        try {
//            System.out.println("主线程"+Thread.currentThread().getName()+"等待子线程执行完成...");
//            latch.await();//阻塞当前线程，直到计数器的值为0
//            System.out.println("主线程"+Thread.currentThread().getName()+"开始执行...");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}