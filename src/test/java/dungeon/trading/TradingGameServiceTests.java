package dungeon.trading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dungeon.trading.game.Game;
import dungeon.trading.game.GameRepository;
import dungeon.trading.game.GameService;
import dungeon.trading.item.ItemRepository;
import dungeon.trading.player.PlayerRepository;
import dungeon.trading.resource.ResourceRepository;
import dungeon.trading.station.StationRepository;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TradingGameServiceTests {

  // It's fine to use field injection in tests
  @Autowired
  GameService gameService;
  @MockBean
  PlayerRepository playerRepository;
  @MockBean
  StationRepository stationRepository;
  @MockBean
  ItemRepository itemRepository;
  @MockBean
  GameRepository gameRepository;
  @MockBean
  ResourceRepository resourceRepository;

  @Test
  @Transactional
  void shouldCreateAndStartGame() {
    var gameId = UUID.randomUUID();
    this.gameService.createNewGame(gameId);

    ArgumentCaptor<Game> gameArgument = ArgumentCaptor.forClass(Game.class);
    verify(gameRepository).save(gameArgument.capture());
    assertThat(gameArgument.getValue().getIsCurrentGame()).isTrue();
    assertThat(gameArgument.getValue().getGameId()).isEqualTo(gameId);
  }

  @Test
  void stopGameShouldClearDatabase() {
    var gameID = UUID.randomUUID();
    var game = this.gameService.createNewGame(gameID);
    when(gameRepository.findByIsCurrentGame(true)).thenReturn(Optional.of(game));
    when(gameRepository.findById(gameID)).thenReturn(Optional.of(game));

    this.gameService.stopGame(gameID);

    // Might be an idea to not simply test verify invocation. But for now this should work
    verify(playerRepository).deleteAll();
    verify(stationRepository).deleteAll();
    verify(itemRepository).deleteAll();
    verify(resourceRepository).deleteAll();
    ArgumentCaptor<Game> gameArgument = ArgumentCaptor.forClass(Game.class);
    verify(gameRepository, times(2)).save(gameArgument.capture());
    assertThat(gameArgument.getAllValues().get(1).getIsCurrentGame()).isFalse();
  }
}
