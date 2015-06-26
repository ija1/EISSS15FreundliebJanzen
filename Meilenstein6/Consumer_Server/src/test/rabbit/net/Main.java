package test.rabbit.net;

import test.rabbit.net.*;


public class Main {
 public static void main(String[] args) {
 // start Consumer Thread 
 Thread consumerThread = new Thread(new SampleConsumer());
 consumerThread.start();
 }
}