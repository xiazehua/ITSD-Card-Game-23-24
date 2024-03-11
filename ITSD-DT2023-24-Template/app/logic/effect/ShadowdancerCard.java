package logic.effect;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.BackgroundLogic;
import logic.PlayerGameLogic;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

public class ShadowdancerCard extends CardEffect {
    public ShadowdancerCard(Card card, Unit targetUnit, Tile t, GameState gameState, ActorRef out) {
        super(card, targetUnit, t, gameState, out);
        setBackgroundLogicEnum(BackgroundLogic.BackgroundLogicEnum.DEATH);
    }

    @Override
    public boolean longEffect() {
        // 对敌方头像造成1点伤害，并为自己恢复1点生命值。
        selfUnit.setMaxhealth(selfUnit.getMaxhealth() + 1);
        BasicCommands.setUnitHealth(out, selfUnit, selfUnit.getMaxhealth());
        PlayerGameLogic.sleep(100);
        Player player1 = gameState.getPlayer1();
        player1.setHealth(player1.getHealth() - 1);
        BasicCommands.setPlayer1Health(out, player1);
        PlayerGameLogic.sleep(100);
        return true;
    }
}
