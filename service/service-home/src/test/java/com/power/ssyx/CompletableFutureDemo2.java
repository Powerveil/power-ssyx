package com.power.ssyx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Powerveil
 * @Date 2023/10/6 10:54
 * <p>
 * supplyAsync方法不支持返回值
 */
public class CompletableFutureDemo2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        System.out.println("main begin...");

        // CompletableFuture创建异步对象
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getName());
            int value = 1024;
            System.out.println("vaule：" + value);
            return value;
        }, executorService);
        Integer value = future.get();
        System.out.println(value);
        System.out.println("main begin...");

    }
}
