package com.example.trading;

import com.example.trading.player.Player;
import com.example.trading.player.PlayerRepository;
import com.example.trading.player.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayerServiceTests {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceTests(PlayerService service, PlayerRepository repository) {
        this.playerService = service;
        this.playerRepository = repository;
    }

    @Test
    public void playerCreationTest() {
        Integer newPlayerId = this.playerService.createPlayer(200);
        Optional<Player> player = this.playerRepository.findById(newPlayerId);
        assertEquals(newPlayerId, player.get().getPlayerId());
    }

    @Test
    public void playerGetMoneyTest() {
        Integer newPlayerId = this.playerService.createPlayer(200);
        Integer moneyAmount = this.playerService.getCurrentMoneyAmount(newPlayerId);
        assertEquals(200, moneyAmount);
    }

    @Test
    public void playerReduceMoneyTest() {
        Integer newPlayerId = this.playerService.createPlayer(200);
        Integer newMoneyAmount = this.playerService.reduceMoney(newPlayerId, 150);
        assertEquals(50, newMoneyAmount);
    }

    @Test
    public void playerReduceTooMuchMoneyTest() {
        Integer newPlayerId = this.playerService.createPlayer(200);
        assertThrows(
                RuntimeException.class,
                () -> this.playerService.reduceMoney(newPlayerId, 250)
        );
    }

    @Test
    public void playerAddMoneyTest() {
        Integer newPlayerId = this.playerService.createPlayer(200);
        Integer newMoneyAmount = this.playerService.addMoney(newPlayerId, 150);
        assertEquals(350, newMoneyAmount);
    }

}
