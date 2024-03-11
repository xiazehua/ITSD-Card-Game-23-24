package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 *
 * {
 *   messageType = “initalize”
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change

		gameState.gameInitalised = true;

		gameState.something = true;

		//CommandDemo.executeDemo(out);

		// 初始化卡组的id属性
		initCards(out, gameState, message);

		// draw tile
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j++) {
				Tile tile = BasicObjectBuilders.loadTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
			}
		}

		// setPlayer1Health
		Player humanPlayer = new Player(20, 0);
		gameState.players[0] = humanPlayer;
		BasicCommands.setPlayer1Health(out, gameState.players[0]);

		// setPlayer2Health
		Player aiPlayer = new Player(20, 0);
		gameState.players[1] = aiPlayer;
		BasicCommands.setPlayer2Health(out, gameState.players[1]);

		// drawPlayer1Unit
		// 初始化角色1的id
		int initPlayerId = 0;
		gameState.currentTile = BasicObjectBuilders.loadTile(1, 2);
		gameState.Player1unitList.put(0, BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, initPlayerId, Unit.class));
		gameState.Player1unitList.get(0).setPositionByTile(gameState.currentTile);
		BasicCommands.drawUnit(out, gameState.Player1unitList.get(0), gameState.currentTile);

		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		// setUnitAttack
		BasicCommands.setUnitAttack(out, gameState.Player1unitList.get(0), 2);
		gameState.Player1unitList.get(0).setAttack(2);

		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		// setUnitHealth
		BasicCommands.setUnitHealth(out, gameState.Player1unitList.get(0), 20);
		gameState.Player1unitList.get(0).setHealth(20);
		gameState.Player1unitList.get(0).setMaxhealth(20);
		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

		//drawPlayer2Unit
		// 初始化角色2的id
		int initPlayer2Id = 10;
		gameState.currentTile = BasicObjectBuilders.loadTile(7, 2);
		gameState.Player2unitList.put(initPlayer2Id, BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, initPlayer2Id, Unit.class));
		gameState.Player2unitList.get(initPlayer2Id).setPositionByTile(gameState.currentTile);
		BasicCommands.drawUnit(out, gameState.Player2unitList.get(initPlayer2Id), gameState.currentTile);

		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

		//setattack
		BasicCommands.setUnitAttack(out, gameState.Player2unitList.get(initPlayer2Id), 4);
		gameState.Player2unitList.get(initPlayer2Id).setAttack(4);

		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

		// setUnitHealth
		BasicCommands.setUnitHealth(out, gameState.Player2unitList.get(initPlayer2Id), 20);
		gameState.Player2unitList.get(initPlayer2Id).setHealth(20);
		gameState.Player2unitList.get(initPlayer2Id).setMaxhealth(20);
		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

		//set players mana
		gameState.players[0].setMana(9);
		gameState.currentMaxMana = 9;
		BasicCommands.setPlayer1Mana(out, gameState.players[0]);
		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

		//send 3 cards to players 这里还出现了bug
		BasicCommands.drawCard(out, gameState.Player1Cards.get(0), 1, 0);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.drawCard(out, gameState.Player1Cards.get(1), 2, 0);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.drawCard(out, gameState.Player1Cards.get(2), 3, 0);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		gameState.Player1currentCards.add(gameState.Player1Cards.get(0));
		gameState.Player1currentCards.add(gameState.Player1Cards.get(1));
		gameState.Player1currentCards.add(gameState.Player1Cards.get(2));


		//send 3 cards to Ai
		gameState.Player2currentCards.add(gameState.Player2Cards.get(1));
		gameState.Player2currentCards.add(gameState.Player2Cards.get(2));
		gameState.Player2currentCards.add(gameState.Player2Cards.get(3));
		gameState.Player2CurrentCardSend += 3;
		BasicCommands.addPlayer1Notification(out, "AI的血量为4！", 1);

	}


	// 初始化cards
	private void initCards(ActorRef out, GameState gameState, JsonNode message) {
		int maxId = 0;
		for (int i = 0; i < gameState.Player1Cards.size(); i++) {
			gameState.Player1Cards.get(i).setId(i);
		}
		for (int i = maxId; i < gameState.Player2Cards.size(); i++) {
			gameState.Player2Cards.get(i).setId(i);
		}
	}
}


