package com.quickdraw.database.quickdrawdb;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Matthew on 11/8/16.
 */

public class User {

    private String name;
    private float balance;
    private float[] history = new float[5];
    private String[] times = {" ", " ", " ", " ", " "};
    private String pin;

    public User() {

    }

    public User(String name, float balance, String pin) {
        this.name = name;
        this.balance = balance;
        this.pin = pin;
    }


    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float newBalance) {
        balance = newBalance;
    }

    public float[] getHistory() {
        return history;
    }

    public String[] getTimes() {
        return times;
    }

    public String getPin() { return pin; }

    public void addTransaction(float amount) {
        for (int i = history.length - 1; i > 0; i--) {
            history[i] = history[i-1];
        }
        history[0] = amount;
    }

    public void addTime(String time) {
        for (int i = times.length - 1; i > 0; i--) {
            times[i] = times[i-1];
        }
        times[0] = time;
    }

}
