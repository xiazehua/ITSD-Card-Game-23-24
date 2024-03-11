package logic.effect;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.BackgroundLogic;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * 这个类是不Bad Omen的效果类
 */
public class BadOmenCard extends CardEffect {

    public BadOmenCard(Card card, Unit targetUnit, Tile tile, GameState gameState, ActorRef out) {
        super(card, targetUnit, tile,gameState, out);
        setBackgroundLogicEnum(BackgroundLogic.BackgroundLogicEnum.DEATH);
    }

    public boolean applyEffect() {
        return super.applyEffect();
    }

    /**
     * 这张卡的长久效果
     */
    public boolean longEffect() {
        // 每当触发的时候伤害++
        selfUnit.setAttack(selfUnit.getAttack() + 1);
        // 更新前端UI
        BasicCommands.setUnitAttack(out, selfUnit, selfUnit.getAttack());
        return true;
    }

    public String getName() {
        return "Bad Omen";
    }
}
