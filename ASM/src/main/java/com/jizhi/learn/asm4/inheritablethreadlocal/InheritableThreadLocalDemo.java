package com.jizhi.learn.asm4.inheritablethreadlocal;

public class InheritableThreadLocalDemo {


    private static MyInheritableThreadLocal<Context> threadLocal = new MyInheritableThreadLocal<Context>();

    public void method(){

        Context context = new Context();
        context.cache.addLast("luojun");

        threadLocal.set(context);

        System.out.println("parent : " + threadLocal.get() + " " + threadLocal + " " + threadLocal.get().cache);

        new Thread(new Runnable() {
            public void run() {
                System.out.println(threadLocal.get() + " " + threadLocal + " " + threadLocal.get().cache);
                threadLocal.get().cache.addLast("luojun2");
            }
        }).start();


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("parent : " + threadLocal.get() + " " + threadLocal + " " + threadLocal.get().cache);
    }

    public static void main(String[] args) {
        InheritableThreadLocalDemo inheritableThreadLocalDemo = new InheritableThreadLocalDemo();
        inheritableThreadLocalDemo.method();
    }
}
