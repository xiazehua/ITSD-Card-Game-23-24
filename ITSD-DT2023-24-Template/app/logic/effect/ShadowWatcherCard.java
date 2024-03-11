package logic.effect;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.BackgroundLogic;
import logic.PlayerGameLogic;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

public class ShadowWatcherCard extends CardEffect {

    public ShadowWatcherCard(Card card, Unit targetUnit, Tile t, GameState gameState, ActorRef out) {
        super(card, targetUnit, t, gameState, out);
        setBackgroundLogicEnum(BackgroundLogic.BackgroundLogicEnum.DEATH);
    }

    @Override
    public boolean longEffect() {
        // 每当触发的时候伤害++ 血量++
        selfUnit.setAttack(selfUnit.getAttack() + 1);
        selfUnit.setMaxhealth(selfUnit.getMaxhealth() + 1);
        // 更新前端UI
        BasicCommands.setUnitAttack(out, selfUnit, selfUnit.getAttack());
        PlayerGameLogic.sleep(100);
        BasicCommands.setUnitHealth(out, selfUnit, selfUnit.getMaxhealth());
        PlayerGameLogic.sleep(100);
        return true;
    }
}
