package com.example.trading.round;

public class Round {
    private static int currentRound = 0;
    private static String status = null;

    public static void updateRoundCount(int newCount) {
        currentRound = newCount;
    }

    public static void updateStatus(String newStatus) {
        status = newStatus;
    }

    public static int getCurrentRound() {
        return currentRound;
    }

    public static String getCurrentStatus() {
        return status;
    }
}
