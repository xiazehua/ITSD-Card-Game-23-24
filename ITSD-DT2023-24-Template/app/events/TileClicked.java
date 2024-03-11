package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.PlayerGameLogic;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

import java.util.Objects;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 *
 * {
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	private Unit selectUnit;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (gameState.gameLock) {
			return;
		}
		// 获取该位置信息
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		// 判断该位置是否有角色
		Unit unit = gameState.getTileNowUnit(tilex, tiley);
		// 判断该位置是否有敌人
		Unit enemies = gameState.getEnemies(tilex, tiley);
		// 使用tilex, tiley 获取 tile对象
		Tile tile;
		if (!Objects.isNull(enemies)) {
			// 有敌人就往后退一位
			tile = BasicObjectBuilders.loadTile(tilex - 1, tiley);
		} else tile = BasicObjectBuilders.loadTile(tilex, tiley);

		// 判断是否有卡牌被选中
		if (!Objects.isNull(gameState.currentCard)) {
			// 法术牌直接应用
			Unit targetUnit;
			if (enemies == null) {
				targetUnit = unit;
			} else {
				targetUnit = enemies;
			}
			gameState.cardUse(out, gameState.currentCard, tile, targetUnit);
			// 清除当前选中的卡牌
			gameState.currentCard = null;
		}

		// 判断该位置是否有角色
		if (!Objects.isNull(unit)) {
			if (unit.equals(selectUnit)) {
				gameState.closeTile(out, unit);
				selectUnit = null;
				return;
			}
			selectUnit = unit;
			gameState.preMove(out, unit, tile);
			return;
		}

		if (!Objects.isNull(selectUnit)) {
			if (gameState.player1IsMove) {
				// 对前端发送消息
				BasicCommands.addPlayer1Notification(out, "本回合你已经移动过了！", 1);
				return;
			}
			boolean move = gameState.move(out, selectUnit, tile);
			BasicCommands.addPlayer1Notification(out, "判断是否移动成功:"+move, 1);
			// 判断是否移动成功
			if (move) {
				gameState.player1IsMove = true;
				// 移动成功之后如果有敌人对敌人发起进攻
				if (!Objects.isNull(enemies)) {
					PlayerGameLogic.attack(out,gameState, selectUnit, enemies);
					
					selectUnit = null;
					gameState.player1IsMove = true;
					return;
				}
				selectUnit = null;
            }
        }
	}

}
