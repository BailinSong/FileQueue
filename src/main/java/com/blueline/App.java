package com.blueline;

import com.google.code.fqueue.FQueue;
import com.google.code.fqueue.FSQueue;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        FQueue queue;
        queue = new FQueue("db");
        queue.add("abc".getBytes());
        System.out.println(new String(queue.poll()));



    }
}
