package com.example.trading.player;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerRestController {
    @Autowired
    private PlayerService playerService;

    @GetMapping("/balances")
    public ResponseEntity<?> getAllPlayerBalances() {
        JSONArray balances = this.playerService.getAllPlayerBalances();
        return new ResponseEntity<JSONArray>(balances, HttpStatus.OK);
    }
}
