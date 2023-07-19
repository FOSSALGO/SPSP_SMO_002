/*
 *  Jangan lupa SUBSCRIBE, Like, Share, dan Comment Channel Kami
 *  https://www.youtube.com/@FOSALGO
 *  Tetap Semangat...!!! Semua orang Bisa Belajar Coding
 *  Salam Hormat dari kami
 *  Sugiarto Cokrowibowo
 *  FOSALGO
 *  Terimakasih
 */
package smo;

import java.util.Random;

public class Algorithm {

    private static Random random = new Random();

    public static double randomBetweenDouble(double lower, double upper) {
        if (lower > upper) {
            double temp = lower;
            lower = upper;
            upper = temp;
        }
        return (random.nextDouble() * (upper - lower)) + lower;
    }

    public static int randomBetweenInteger(int lower, int upper) {
        if (lower > upper) {
            int temp = lower;
            lower = upper;
            upper = temp;
        }
        return random.nextInt(upper + 1 - lower) + lower;
    }

    public static int[][] mergeOperations(int[][] operation1, int[][] operation2) {
        int[][] operation = null;
        if (//validation
                operation1 != null
                && operation2 != null
                && operation1.length == operation2.length
                && operation1[0].length == operation2[0].length) {
            operation = new int[operation1.length][operation1[0].length];
            for (int i = 0; i < operation.length; i++) {
                for (int j = 0; j < operation[i].length; j++) {
                    if (operation1[i][j] == operation2[i][j]) {
                        operation[i][j] = operation1[i][j];
                    } else {
                        int selisih = Math.abs(operation1[i][j] - operation2[i][j]);
                        if (selisih == 2) {
                            operation[i][j] = 0;
                        } else if (selisih == 1) {
                            operation[i][j] = operation1[i][j] + operation2[i][j];
                        }
                    }
                }
            }
        }
        return operation;
    }

}
