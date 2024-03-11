package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.effect.CardEffect;
import logic.effect.CardEffectLogic;
import structures.GameState;
import structures.basic.BigCard;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * 卡牌效果的逻辑实现
 */
public class CardLogic {
    private Unit targetUnit;
    private BigCard card;
    private GameState gameState;
    private ActorRef out;
    private Card originCard;
    private CardEffectLogic logic;
    private Tile tile;

    public CardLogic(Card card, Unit targetUnit,
                     Tile t,GameState gameState, ActorRef out) {
        this.targetUnit = targetUnit;
        this.card = card.getBigCard();
        this.gameState = gameState;
        this.out = out;
        this.originCard = card;
        this.tile = t;
        logic = new CardEffectLogic(card, targetUnit,t, gameState, out);
    }

    // 判断该Card 是否为攻击效果
    public boolean isHurt() {
        return card.getAttack() > 0;
    }

    public boolean attack() {
        if (isHurt() && !PlayerGameLogic.isFriend(gameState, targetUnit)) {
            int attack = card.getAttack();
            int health = targetUnit.getHealth();
            int newHealth = health - attack;
            targetUnit.setHealth(newHealth);
            BasicCommands.setUnitHealth(out, targetUnit, newHealth);
            BasicCommands.addPlayer1Notification(out,
                    "攻击成功"+ "造成了"+attack+"点伤害",
                    1);
            return true;
        } else {
            BasicCommands.addPlayer1Notification(out,
                    "攻击失败, 不能攻击友方！",
                    1);
            return false;
        }
    }

    public boolean gain() {
        if (card.getHealth() >= 0) {
            int health = targetUnit.getHealth();
            int newHealth = health + card.getHealth();
            targetUnit.setHealth(newHealth);
            BasicCommands.setUnitHealth(out, targetUnit, newHealth);
            BasicCommands.addPlayer1Notification(out,
                    "治疗成功"+ "恢复了"+card.getHealth()+"点生命",
                    1);
            return true;
        } else {
            BasicCommands.addPlayer1Notification(out,
                    "该卡是效果类卡牌，无法使用！",
                    1);
            return false;
        }
    }

    public boolean effect() {
        // 判定是否有法力执行 -> 没法力直接无效 return
        if (originCard.getManacost() > gameState.players[0].getMana()) {
            // 告诉玩家：你并没有可以执行攻击的法力值
            BasicCommands.addPlayer1Notification(out, "你并没有可以执行攻击的法力值", 1);
            return false;
        }
        CardEffect cardEffect = logic.apply(originCard.getCardname());
        // 添加卡牌效果到背后执行
        BackgroundLogic.getInstance()
                .put(PlayerGameLogic.getCardId(originCard), cardEffect::longEffect,
                        cardEffect.getBackgroundLogicEnum());
        return cardEffect.applyEffect();
    }

    // 销毁这张卡牌
    public void destroy() {
        // 减少法术值
        gameState.players[0].setMana(gameState.players[0].getMana() - originCard.getManacost());
        PlayerGameLogic.delAllCard(out, gameState);
        gameState.Player1currentCards.remove(originCard);
        // 重新绘制所有卡牌
        PlayerGameLogic.redrawAllCard(out, gameState);
        gameState.currentCard = null;
        // 清除所有的高亮
        PlayerGameLogic.clearAllTile(out);
    }

}
