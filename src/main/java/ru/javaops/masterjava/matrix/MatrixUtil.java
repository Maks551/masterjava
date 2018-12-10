package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {
    private static final Lock LOCK = new ReentrantLock();
    private static byte COUNT = 0;
    private static final byte THREADS_COUNT = 10;

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];


        for (int i = 0; i < matrixSize; i = i + matrixSize/ THREADS_COUNT) {
            executor.submit(getTaskR(matrixSize, i, matrixA, matrixB, matrixC));
        }
        while (COUNT != THREADS_COUNT) {
            Thread.sleep(100);
        }
        COUNT = 0;

        return matrixC;
    }

    private static Runnable getTaskR(int matrixSize, int n, int[][] matrixA, int[][] matrixB, int[][] matrixC) {
        return () -> {
            int size = matrixSize / THREADS_COUNT + n;
            for (int i = n; i < size; i++) {
                try {
                    for (int j = 0; ; j++) {
                        int sum = 0;
                        for (int k = 0; k < matrixSize; k++) {
                            sum += matrixA[i][k] * matrixB[k][j];
                        }
                        matrixC[i][j] = sum;
                    }
                } catch (IndexOutOfBoundsException ignored) { }
            }
            LOCK.lock();
            COUNT++;
            LOCK.unlock();
        };
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
