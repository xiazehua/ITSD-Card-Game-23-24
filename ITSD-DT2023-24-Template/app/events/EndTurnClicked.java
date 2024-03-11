package events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.AILogic;
import logic.EnemyAiLogic;
import logic.PlayerGameLogic;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 *
 * {
 *   messageType = “endTurnClicked”
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		//结束当前玩家的操作
		gameState.gameLock = true;
		// 清除角色1 可能被高亮的方块
		PlayerGameLogic.clearAllTile(out);
		BasicCommands.addPlayer1Notification(out, "我方进行进攻", 3);
		System.out.println("attack");
		// 已方单位发动进攻
		selfAi(out, gameState, message);

		// 发送消息正在等待AI 执行操作
		BasicCommands.addPlayer1Notification(out, "正在等待AI 执行操作", 3);
		enemyAi(out, gameState, message);

		//触发endturn解锁
		gameState.gameLock = false;

		// 将玩家1设置为未移动过
		gameState.player1IsMove = false;

		// 补充手牌
		send(out, gameState, message);
		// 补充蓝量
		addMana(out, gameState, message);
	}

	// 补充蓝量
	private void addMana(ActorRef out, GameState gameState, JsonNode message) {
		// 增加蓝量
		if (gameState.currentMaxMana < 9) {
			gameState.currentMaxMana++;
			gameState.getPlayer1().setMana(gameState.currentMaxMana);
			// 在前端绘制蓝量
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());
			PlayerGameLogic.sleep(100);
		}
	}

	// 我方ai自动攻击
	private void selfAi(ActorRef out, GameState gameState, JsonNode message) {
		AILogic aiLogic = new AILogic(gameState, out);
		aiLogic.action();
	}

	// 敌方ai 进行操作
	private void enemyAi(ActorRef out, GameState gameState, JsonNode message) {
		EnemyAiLogic enemyAiLogic = new EnemyAiLogic(gameState, out);
		enemyAiLogic.action();
	}

	// 发牌操作
	private void send(ActorRef out, GameState gameState, JsonNode message) {
		// 发牌
		System.out.println("==========gameState.Player1Cards.size()");
		if (gameState.Player1CurrentCardSend < gameState.Player1Cards.size() && gameState.Player1CurrentCardSend <= 6) {
			Card card = gameState.Player1Cards.get(gameState.Player1CurrentCardSend - 1);
			gameState.Player1currentCards.add(card);
			gameState.Player1CurrentCardSend++;
			// 在前端绘制手牌
			BasicCommands.drawCard(out, card, gameState.Player1currentCards.size(), 0);
			PlayerGameLogic.sleep(100);
			// 发送消息
			BasicCommands.addPlayer1Notification(out, "发牌", 1);
		}
	}

}
