package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.PlayerGameLogic;
import structures.GameState;
import structures.basic.Card;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 *
 * {
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class  CardClicked implements EventProcessor{
	// 当前选中的卡牌
	private Card currentCard;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// 这个是卡牌在操作区域的坐标
		int handPosition = message.get("position").asInt();
		System.out.println("processEvent:handPosition:"+handPosition);
		
		currentCard = gameState.Player1currentCards.get(handPosition - 1);
		System.out.println("GameState"+gameState.Player1currentCards.get(handPosition - 1).getId());
		if (currentCard.equals(gameState.currentCard)) {
			// 如果当前卡牌已经被选中，再次点击取消选中
			BasicCommands.drawCard(out, currentCard, handPosition, 0);
			gameState.currentCard = null;
			currentCard = null;
			// 取消选择区域
			PlayerGameLogic.clearAllTile(out);
			return;
		}
		// 设置当前选中的卡牌
		gameState.currentCard = currentCard;
		// 表现这张卡牌被选中的效果
		BasicCommands.drawCard(out, gameState.currentCard, handPosition, 1);
		// 取消其他卡牌被选中的效果
		for (Card player1currentCard : gameState.Player1currentCards) {
			if (!player1currentCard.equals(gameState.currentCard)) {
				BasicCommands.drawCard(out, player1currentCard, gameState.Player1currentCards.indexOf(player1currentCard) + 1, 0);
			}
		}
		// 判断这张牌是不是生物卡
		if (currentCard.isCreature()) {
			// 点亮周围所有可放置的地方
			PlayerGameLogic.highlight(out, gameState);
			// 给前端发送提示
			BasicCommands.addPlayer1Notification(out, "你选择的是可放置的生物类卡牌!", 3);
		} else {
			// 对所有敌方目标的脚底进行标红
			if (currentCard.getBigCard().getAttack() > 0) {
				PlayerGameLogic.highlightEnemy(out, gameState);
			}
			// 对友方进行标记
			else {
				PlayerGameLogic.highlightFriend(out, gameState);
			}
			BasicCommands.addPlayer1Notification(out, "你选择的是法术的卡组!", 3);
		}
	}
}
