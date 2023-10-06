package com.power.ssyx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Powerveil
 * @Date 2023/10/6 11:27
 * <p>
 * 任务的组合
 */
public class CompletableFutureDemo5 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // CompletableFuture创建异步对象
        //1 任务1 返回结果 1024
        CompletableFuture<Integer> futureA = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "：begin...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int value = 1024;
            System.out.println("任务1：" + value);
            System.out.println(Thread.currentThread().getName() + "：end...");
            return value;
        }, executorService);

        //1 任务2 返回结果 200
        CompletableFuture<Integer> futureB = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "：begin...");
            int value = 200;
            System.out.println("任务2：" + value);
            System.out.println(Thread.currentThread().getName() + "：end...");
            return value;
        }, executorService);

        CompletableFuture<Void> all = CompletableFuture.allOf(futureA, futureB);
        all.get();
        System.out.println("over...");

    }
}
