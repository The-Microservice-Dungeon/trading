package dungeon.trading.core.exceptions;

public class PlayerMoneyTooLowException extends RuntimeException {
    public PlayerMoneyTooLowException(String player, int needed) {
        super("Player '" + player + "' does not have the needed " + needed + " money-units.");
    }
}
