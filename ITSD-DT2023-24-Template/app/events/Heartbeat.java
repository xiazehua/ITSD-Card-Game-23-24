package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.routes;
import logic.BackgroundLogic;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game
 * Actor, which you can use to trigger game logic.
 *
 * {
 *   String messageType = “heartbeat”
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class Heartbeat implements EventProcessor{

	// 监听卡牌对象死亡时的事件
	private int deathPoolSize = 0;
	private boolean isInit = true;
	// 监听卡牌对象创建时的事件
	private int unitPoolSize = 0;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// 初始化操作
		if (isInit) {
			deathPoolSize = gameState.getDeathPool().size();
			unitPoolSize = gameState.Player1unitList.size();
			isInit = false;
		}
		// 单位死亡发生效果
		if (deathPoolSize < gameState.getDeathPool().size()) {
			BackgroundLogic.getInstance()
					.apply(BackgroundLogic.BackgroundLogicEnum.DEATH);
			deathPoolSize = gameState.getDeathPool().size();
		}
		// 单位被添加的时候触发
		if (unitPoolSize != gameState.Player1unitList.size()) {
			BackgroundLogic.getInstance()
					.apply(BackgroundLogic.BackgroundLogicEnum.BIRTH);
			unitPoolSize = gameState.Player1unitList.size();
		}
		// 常规状态下
		BackgroundLogic.getInstance()
				.apply(BackgroundLogic.BackgroundLogicEnum.COMM);
	}
}
