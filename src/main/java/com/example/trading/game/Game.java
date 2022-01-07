package com.example.trading.game;

/**
 * Game manages the round count and status for our service
 * needed for the calculation of item and resource prices
 * needed for the saving of item and resource histories
 */
public class Game {
    private static int currentRound = 0;
    private static String status = null;

    private static String currentGameId = null;

    public static void updateRoundCount(int newCount) {
        currentRound = newCount;
    }

    public static void updateStatus(String newStatus) {
        status = newStatus;
    }

    public static void updateGameId(String newGame) {
        currentGameId = newGame;
    }

    public static int getCurrentRound() {
        return currentRound;
    }

    public static String getCurrentStatus() {
        return status;
    }

    public static String getCurrentGameId() {
        return currentGameId;
    }
}
