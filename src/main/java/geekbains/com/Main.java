package geekbains.com;

import java.util.Arrays;

public class Main {

    private static final Object obj = new Object();
    static final int size = 10000000;
    static final int h = size / 2;
    static float[] arr = new float[size];

    public static void main(String[] args) throws InterruptedException {
        Arrays.fill(arr, 1);
        recountArrayStepByStep(arr);
        Arrays.fill(arr, 1);
        recountArrayParallel(arr);
    }

    static void recountArrayStepByStep(float[] arr) {
        long res = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        System.out.print("Прямой просчет массива занял ");
        System.out.print(System.currentTimeMillis() - res);
        System.out.println(" мсек.");
    }

    static void recountArrayParallel(float[] arr) throws InterruptedException {
        long res_cut = System.currentTimeMillis();
        long res_glue;
        // рубим массив на две части
        float[] arr1 = new float[h];
        System.arraycopy(arr, 0, arr1, 0, h);
        float[] arr2 = new float[h];
        System.arraycopy(arr, h, arr2, 0, h);
        res_cut = System.currentTimeMillis() - res_cut;
        System.out.println("Разбивка = " + res_cut + " мсек.");
        // считаем
        ThreadCounter threadOne = new ThreadCounter(arr1);
        ThreadCounter threadTwo = new ThreadCounter(arr2);
        threadOne.start();
        threadTwo.start();
        threadOne.join();
        threadTwo.join();
        // склеиваем обратно в один
        res_glue = System.currentTimeMillis();
        System.arraycopy(arr1, 0, arr, 0, h);
        System.arraycopy(arr2, 0, arr, h, h);
        res_glue = System.currentTimeMillis() - res_glue;
        System.out.println("Склейка = " + res_glue + " мсек.");
        System.out.println("Параллельный просчет в сумме занял " +
                (res_cut + res_glue + threadOne.res_time + threadTwo.res_time) + " мсек.");
    }

    // первый поток
    private static class ThreadCounter extends Thread {
        float[] array;
        public long res_time;

        public ThreadCounter(float[] arr) {
            this.array = arr;
        }

        @Override
        public void run() {
            res_time = System.currentTimeMillis();
            for (int i = 0; i < h; i++) {
                //synchronized (obj) {
                arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
                //}
            }
            res_time = System.currentTimeMillis() - res_time;
            System.out.println("Просчет=" + res_time);
        }
    }

}
