//package com.example.trading.kafka;
//
//import com.example.trading.item.ItemService;
//import com.example.trading.player.PlayerService;
//import com.example.trading.resource.ResourceService;
//import com.example.trading.station.PlanetService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//
//import java.util.UUID;
//
//public class KafkaConsumers {
//    @Autowired
//    private PlayerService playerService;
//
//    @Autowired
//    private PlanetService planetService;
//
//    @Autowired
//    private ResourceService resourceService;
//
//    @Autowired
//    private ItemService itemService;
//
//    @KafkaListener(topics = "player joined")
//    public void listenToPlayerCreation(String msg) {
//        UUID player = this.playerService.createPlayer(200);
////        confirm message?
//    }
//
//    @KafkaListener(topics = "spawn created")
//    public void listenToSpawnCreation(String msg) {
//        UUID planet = this.planetService.createNewPlanet(planetId, planetType);
//    }
//
//    @KafkaListener(topics = "spacestation created")
//    public void listenToStationCreation(String msg) {
//        UUID planet = this.planetService.createNewPlanet(plnetId, planetType);
//    }
//
//    @KafkaListener(topics = "resource created")
//    public void listenToResourceCreation(String msg) {
//        UUID resource = this.resourceService.createResource(name, price);
//    }
//
//    @KafkaListener(topics = "item created")
//    public void listenToItemCreation(String msg) {
//        UUID item = this.itemService.createItem(name, desc, type, price);
//    }
//
//    @KafkaListener(topics = "round started")
//    public void listenToRoundStart(String msg) {
//
//    }
//
//    @KafkaListener(topics = "round ended")
//    public void listenToRoundEnd(String msg) {
//
//    }
//
//    @KafkaListener(topics = "game ended")
//    public void listenToGameEnd(String msg) {
//
//    }
//}
