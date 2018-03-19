/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.StringTokenizer;

/**
 *
 * @author Prashu
 */
public class Util {
     public static void mySleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
    public static void myWait(Object obj) {
        println("waiting");
        try {
            obj.wait();
        } catch (InterruptedException e) {
        }
    }
    public static boolean lessThan(int A[], int B[]) {
        for (int j = 0; j < A.length; j++)
            if (A[j] > B[j]) return false;
        for (int j = 0; j < A.length; j++)
            if (A[j] < B[j]) return true;
        return false;
    }
    public static int maxArray(int A[]) {
        int v = A[0];
        for (int i=0; i<A.length; i++)
            if (A[i] > v) v = A[i];
        return v;
    }
    public static String writeArray(int A[]){
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < A.length; j++)
            s.append(String.valueOf(A[j])).append(" ");
        return s.toString();
    }
   public static void readArray(String s, int A[]) {
        StringTokenizer st = new StringTokenizer(s);
        for (int j = 0; j < A.length; j++)
            A[j] = Integer.parseInt(st.nextToken());
    }
    public static int searchArray(int A[], int x) {
        for (int i = 0; i < A.length; i++)
            if (A[i] == x) return i;
        return -1;
    }
    public static void println(String s){
        if (Symbols.debugFlag) {
            System.out.println(s);
            System.out.flush();
        }
    }
}