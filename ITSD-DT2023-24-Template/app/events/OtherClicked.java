package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import logic.PlayerGameLogic;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 *
 * {
 *   messageType = “otherClicked”
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//点别的地方
		//判断switch case
		//如果目前玩家执行操作，直接跳出
		if (gameState.gameLock) {
			return;
		}
		PlayerGameLogic.clearAllTile(out);
	}

}


