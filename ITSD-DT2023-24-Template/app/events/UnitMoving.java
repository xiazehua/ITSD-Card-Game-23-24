package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Indicates that a unit instance has started a move.
 * The event reports the unique id of the unit.
 *
 * {
 *   messageType = “unitMoving”
 *   id = <unit id>
 * }
 * 这个是移动之后调用的处理器，在移动的中间进行计算的处理器
 * @author Dr. Richard McCreadie
 *
 */
public class UnitMoving implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

	}


}
