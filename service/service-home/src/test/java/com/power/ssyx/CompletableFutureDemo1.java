package com.power.ssyx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Powerveil
 * @Date 2023/10/6 10:47
 * <p>
 * runAsync方法不支持返回值
 */
public class CompletableFutureDemo1 {
    public static void main(String[] args) throws InterruptedException {
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        System.out.println("main begin...");


        // CompletableFuture创建异步对象
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getName());
            int result = 1024;
            System.out.println("result：" + result);
        }, executorService);
//        if (voidCompletableFuture.isDone()) {
//            System.out.println("Helo");
//        }
        System.out.println("main over...");
        Thread.sleep(100);
//        if (voidCompletableFuture.isDone()) {
//            System.out.println("Helo");
//        }

    }
}
