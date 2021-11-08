package com.example.trading.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/player/{id}/money")
    public ResponseEntity<Integer> getCurrentPlayerMoneyAmount(@PathVariable int id) {
        try {
            Integer moneyAmount = this.playerService.getCurrentMoneyAmount(id);
            return new ResponseEntity<Integer>(moneyAmount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
