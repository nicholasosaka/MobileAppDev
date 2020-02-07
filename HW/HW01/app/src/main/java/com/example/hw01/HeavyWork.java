package com.example.hw01;

import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HeavyWork implements Runnable{
    public static final int STATUS_START = 0;
    public static final int STATUS_FINISH = 1;
    public static final String MAX = "MAX";
    public static final String MIN = "MIN";
    public static final String AVG = "AVG";

    public static int count;

    public HeavyWork(int n) {
        count = n;
    }

    static final int COUNT = 900000;
    static ArrayList<Double> getArrayNumbers(int n){
        ArrayList<Double> returnArray = new ArrayList<>();

        for (int i=0; i<n; i++){
            returnArray.add(getNumber());
        }

        return returnArray;
    }

    static double getNumber(){
        double num = 0;
        Random rand = new Random();
        for(int i=0;i<COUNT; i++){
            num = num + rand.nextDouble();
        }
        return num / ((double) COUNT);
    }

    @Override
    public void run() {
        Log.d("HANDLER", "Starting Work");
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        MainActivity.handler.sendMessage(startMessage);


        ArrayList<Double> nums = getArrayNumbers(count);

        //
        HashMap<String, Double> map = new HashMap<String, Double>();
        map.put(MAX, findMax(nums));
        map.put(MIN, findMin(nums));
        map.put(AVG, findAvg(nums));

        Message finishMessage = new Message();
        finishMessage.what = STATUS_FINISH;
        finishMessage.obj = map;
        MainActivity.handler.sendMessage(finishMessage);
    }

    private double findAvg(ArrayList<Double> doubles) {
        double avg = 0.0;

        for(double d : doubles){
            avg += d;
        }

        avg /= doubles.size();

        return avg;
    }

    private double findMax(ArrayList<Double> doubles) {
        double max = Double.MIN_VALUE;

        for(double d : doubles){
            if(d > max) max = d;
        }

        return max;
    }

    private double findMin(ArrayList<Double> doubles) {
        double min = Double.MAX_VALUE;

        for(double d : doubles){
            if(d < min) min = d;
        }

        return min;
    }

}