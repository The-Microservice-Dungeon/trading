package com.example.trading;

import com.example.trading.player.Player;
import com.example.trading.player.PlayerRepository;
import com.example.trading.player.PlayerService;
import com.example.trading.game.RoundDto;
import com.example.trading.game.GameService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TradingPlayerServiceTests {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;
    private final GameService gameService;

    @Autowired
    public TradingPlayerServiceTests(PlayerService service, PlayerRepository repository, GameService gameService) {
        this.playerService = service;
        this.playerRepository = repository;
        this.gameService = gameService;
    }

    @Test
    @Transactional
    public void playerCreationTest() {
        UUID newPlayerId = this.playerService.createPlayer(200);
        Optional<Player> player = this.playerRepository.findById(newPlayerId);
        assertEquals(newPlayerId, player.get().getPlayerId());
    }

    @Test
    @Transactional
    public void playerGetMoneyTest() {
        UUID newPlayerId = this.playerService.createPlayer(200);
        Integer moneyAmount = this.playerService.getCurrentMoneyAmount(newPlayerId);
        assertEquals(200, moneyAmount);
    }

    @Test
    @Transactional
    public void playerReduceMoneyTest() {
        UUID newPlayerId = this.playerService.createPlayer(200);
        Integer newMoneyAmount = this.playerService.reduceMoney(newPlayerId, 150);
        assertEquals(50, newMoneyAmount);
    }

    @Test
    @Transactional
    public void playerReduceTooMuchMoneyTest() {
        UUID newPlayerId = this.playerService.createPlayer(200);
        assertThrows(
                RuntimeException.class,
                () -> this.playerService.reduceMoney(newPlayerId, 250)
        );
    }

    @Test
    @Transactional
    public void playerAddMoneyTest() {
        UUID newPlayerId = this.playerService.createPlayer(200);
        Integer newMoneyAmount = this.playerService.addMoney(newPlayerId, 150);
        assertEquals(350, newMoneyAmount);
    }

    @Test
    @Transactional
    public void getAllPlayerBalancesTest() {
        UUID newPlayerId = this.playerService.createPlayer(500);

        assertEquals(
                "[{\"balance\":500,\"player-id\":\"" + newPlayerId + "\"}]",
                this.playerService.getAllCurrentPlayerBalances().toString()
        );
    }

    @Test
    @Transactional
    public void getSpecificRoundPlayerBalances() {
        this.gameService.updateRound(new RoundDto(1, "started"));
        UUID newPlayerId = this.playerService.createPlayer(200);
        this.gameService.updateRound(new RoundDto(1, "ended"));
        this.gameService.updateRound(new RoundDto(2, "started"));
        this.playerService.reduceMoney(newPlayerId, 50);
        this.gameService.updateRound(new RoundDto(2, "ended"));

        JSONArray round1 = this.playerService.getPlayerBalancesForRound(1);
        JSONObject playerRound1 = (JSONObject) round1.get(0);
        JSONArray round2 = this.playerService.getPlayerBalancesForRound(2);
        JSONObject playerRound2 = (JSONObject) round2.get(0);

        assertEquals(200, playerRound1.get("balance"));
        assertEquals(150, playerRound2.get("balance"));
    }

}
