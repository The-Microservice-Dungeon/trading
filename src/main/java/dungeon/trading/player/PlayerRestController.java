package dungeon.trading.player;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerRestController {
    @Autowired
    private PlayerService playerService;

    @GetMapping("/balances")
    public ResponseEntity<?> getAllPlayerBalances() {
        JSONArray balances = this.playerService.getAllCurrentPlayerBalances();
        return new ResponseEntity<JSONArray>(balances, HttpStatus.OK);
    }

    @GetMapping("/balances/{round}")
    public ResponseEntity<?> getPlayerBalancesForRound(@PathVariable int round) {
        try {
            JSONArray balances = this.playerService.getPlayerBalancesForRound(round);
            return new ResponseEntity<JSONArray>(balances, HttpStatus.OK);
        } catch (Exception e) {
            String errorMsg = "There are no balanceHistories for round " + round + " saved.";
            return new ResponseEntity<String>(errorMsg, HttpStatus.NOT_FOUND);
        }
    }
}
