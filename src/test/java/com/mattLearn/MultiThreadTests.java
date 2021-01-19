package com.mattLearn;

import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Matt
 * @date 2021/1/19 16:27
 */

// 测试 多线程实现方法1： 继承 thread 接口，重写 run 方法
class MyThread extends Thread{
    private final int tid;

    public MyThread(int tid){
        this.tid = tid;
    }

    @Override
    public void run() {
        try{
            for(int i = 0; i < 10; ++i){
                Thread.sleep(1000); // 暂停 1s
                System.out.printf("%d : %d%n", tid, i);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void myTest(){
        for(int i = 0; i < 10; ++i){
            new MyThread(i).start();
        }
    }
}

// 测试实现多线程的方法2， 实现 runnable 接口，实现 run 方法
class MyTest2{
    public static void myTest2(){
        for(int i = 0; i < 10; ++i){
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        for(int j = 0; j < 10; ++j) {
                            Thread.sleep(1000);
                            // 内部类中获取外部参数必须是 final 的
                            System.out.printf("T2: %d : %d \n", finalI, j);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 注意这里是 .start(), 而不是 .run() !!
            }).start();
        }
    }
}


// 测试 synchronize 的使用方法，防止线程间的冲突问题
class Test3{
    private static final Object obj = new Object();

    public static void testSynchronized1() {
        synchronized (obj) {
            try {
                for (int j = 0; j < 10; ++j) {
                    Thread.sleep(1000);
                    System.out.printf("T3 %d%n", j);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2() {
        synchronized (obj) {
            try {
                for (int j = 0; j < 10; ++j) {
                    Thread.sleep(1000);
                    System.out.printf("T4 %d%n", j);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized() {
        for (int i = 0; i < 10; ++i) {
            // 因为 testSynchronized1 和 testSynchronized2 共同拥有一个锁，
            // 所以他们在执行的时候会将函数中的内容执行完毕才去执行另一个函数
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }
}

// blockingQueue<E> 线程安全的数据结构
// 生产者消费者模式
class Consumer implements Runnable{

    private BlockingQueue<String> q;
    public Consumer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while(true){
                System.out.println(Thread.currentThread().getName()+": " + q.take());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable{
    private BlockingQueue<String> q;
    public Producer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try{
            for(int i = 0; i < 100; i++){
                Thread.sleep(100);
                q.put(String.valueOf(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
/**
 * BlockingQueue 可以让两个consumer 按照顺序的读取 queue 中的元素
 *
 * 示例中的输出为：
 * Consumer1: 0
 * Consumer2: 1
 * Consumer1: 2
 * Consumer2: 3
 * Consumer1: 4
 * Consumer2: 5
 * Consumer1: 6
 * Consumer2: 7
 * ... all output are in order.
 *
 */
class TestBlockQueue{
    // 构造函数中需要指明 blockingQueue 的容量
    public static BlockingQueue<String> q = new ArrayBlockingQueue<>(10);
    public static void test(){
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();
    }
}

// 测试普通的 queue
class TestThreadUnsafeQueue{
    Queue<String> queue = new ArrayDeque<>();
    {
        for(int i = 0; i < 100; i++){
            queue.offer(String.valueOf(i));
        }
    }

    Runnable consumer1 = () ->{
        while(true){
            if(!queue.isEmpty()) {
                System.out.println("Consumer1: " + queue.poll());
            }
        }
    };

    Runnable consumer2 = () ->{
        while(true){
            if(!queue.isEmpty()){
                System.out.println("Consumer2: "+ queue.poll());
            }
        }
    };

    public static void test(){
        TestThreadUnsafeQueue t = new TestThreadUnsafeQueue();
      //  new Thread(t.producer).start();
        new Thread(t.consumer1).start();
        new Thread(t.consumer2).start();

        /** 现象：
         * Consumer1: 0
         * Consumer1: 2
         * Consumer1: 3
         * Consumer1: 4
         * Consumer1: 5
         * Consumer2: 1
         * Consumer2: 7
         * ...
         */
    }


}

// note: java class 内部的类不允许声明为 public 权限，if so:
// java: class TestThreadLocal is public, should be declared in a file named TestThreadLocal.java
class TestThreadLocal{
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    private static int userID;

    public static void test(){
        for(int i = 0; i < 10; i++){
            final int finalI = i;
            new Thread(() ->{
                try{
                    threadLocal.set(finalI);
                    Thread.sleep(1000);
                    System.out.println("ThreadLocal is:" + threadLocal.get());  // 不同线程的 id 不同
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }


    /**
     * 10个线程全部输出：
     * UserID is:9
     * UserID is:9
     * UserID is:9
     * UserID is:9
     * ....
     *
     * 解释：所有的线程共享一个 静态变量，而 使用 threadlocal 后让每一个 thread 拥有了一个独立的变量副本
     *
     */
    public static void testNoLocalVariable(){
        for(int i = 0; i < 10; i++){
            final int finalI = i;
            new Thread(() ->{
                try{
                    userID = finalI;
                    Thread.sleep(1000);
                    System.out.println("UserID is:" + userID);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

class TestExecutor{
    static void test(){
        //ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit( ()->{
            for(int i = 0; i < 10; i++){
                try {
                    Thread.sleep(1000);
                    System.out.println("Test executorService1: " + i);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        service.submit( ()->{
            for(int i = 0; i < 10; i++){
                try {
                    Thread.sleep(1000);
                    System.out.println("Test executorService2: " + i);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        // Note 两个任务都执行完毕后，service 不会自动停止

        // 可以使用 shutdown 函数。
        // 当 service 中的任务都执行完毕后，service 会 shutdown
        service.shutdown();
    }
}

class TestAtomicCounter{
    private static int counter = 0;
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    static void test(){
        for(int i = 0; i < 10; i++){
            new Thread(()->{
                try {
                    // 测试结果：不加 sleep 的时候 counter 打印到了 100， 加了的时候打到 96
                    // 出现不到 100 的原因是，普通变量的读写操作不是原子性的，也就是可能出现两个线程同时读取，然后同时写回去的情况，
                    Thread.sleep(100);
                    for(int j = 0; j < 10; j++){
                        counter++;
                        System.out.println(counter);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }).start();
        }
    }

    static void test2(){
        for(int i = 0; i < 10; i++){
            new Thread(()->{
                try {
                    Thread.sleep(100);
                    for(int j = 0; j < 10; j++){
                        // 使用 atomic 类型的变量后，就总是可以加到 100！！
                        System.out.println(atomicInteger.incrementAndGet());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }).start();
        }
    }
}

class TestFuture{
    static void testFuture() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();
        Future<String> future = service.submit(()->{
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "1";
        });

        // 这里future 的get 需要 try catch 异常
        // 使用 future 还可以获取线程中的异常。
        // 总的来说， future 就是用于线程间通讯的一个工具（返回值，异常捕获. ...）
        System.out.println(future.get());

        try {
            for(int i = 0; i < 10; i++){
                Thread.sleep(100);
                System.out.println("Hello, message after the \"future\"!!");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}


public class MultiThreadTests {
    public static void main(String[] args) {
        // MyThread.myTest();
        // MyTest2.myTest2();
        // Test3.testSynchronized();
        //TestBlockQueue.test();
        //TestThreadUnsafeQueue.test();
        //TestThreadLocal.test();
        //TestThreadLocal.testNoLocalVariable();
      //  TestExecutor.test();
       // TestAtomicCounter.test2();
        try {
            TestFuture.testFuture();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}



