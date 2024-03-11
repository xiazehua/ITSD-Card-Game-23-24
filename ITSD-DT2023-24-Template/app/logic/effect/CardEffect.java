package logic.effect;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.BackgroundLogic;
import logic.PlayerGameLogic;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class CardEffect {

    protected Card card;
    protected Unit targetUnit;
    protected GameState gameState;
    protected ActorRef out;
    protected Tile tile;
    protected BackgroundLogic.BackgroundLogicEnum backgroundLogicEnum = BackgroundLogic.BackgroundLogicEnum.COMM;
    protected Unit selfUnit;

    public CardEffect(Card card, Unit targetUnit,Tile t,
                       GameState gameState, ActorRef out) {
        this.card = card;
        this.targetUnit = targetUnit;
        this.gameState = gameState;
        this.out = out;
        this.tile = t;
    }

    /**
     * 刚开始应用的效果
     */
   public boolean applyEffect() {
       Card c = getCard();
       if (!c.getIsCreature()) {
           return true;
       }
       Tile t = getTile();
       GameState gameState = getGameState();
       ActorRef out = getOut();
       // 召唤该生物
       Unit unit1 = BasicObjectBuilders.loadUnit(c.getUnitConfig(), c.getId(), Unit.class);
       unit1.setPositionByTile(t);
       // 对该生物进行绘制
       System.out.println("===============applyEffect:gameState.Player1unitList.size()"+gameState.Player1unitList.size());
       unit1.setId(gameState.Player1unitList.size());
       unit1.setAttack(c.getBigCard().getAttack());
       unit1.setHealth(c.getBigCard().getHealth());
       unit1.setMaxhealth(c.getBigCard().getHealth());
       // 前端绘制界面
       // 在前端绘制该生物的生命值,攻击力
       BasicCommands.drawUnit(out, unit1, t);
       PlayerGameLogic.sleep(100);
       BasicCommands.setUnitHealth(out, unit1, c.getBigCard().getHealth());
       PlayerGameLogic.sleep(100);
       BasicCommands.setUnitAttack(out, unit1, c.getBigCard().getAttack());
       // 添加到 Player1unitList 当中
       if (unit1 != null) {
           gameState.Player1unitList.put(PlayerGameLogic.getCardId(card), unit1);
           selfUnit = gameState.Player1unitList.get(PlayerGameLogic.getCardId(card));
           gameState.newUnit = gameState.Player1unitList.get(PlayerGameLogic.getCardId(card));
       }
       // 消耗该角色的蓝条
       gameState.getPlayer1().setMana(gameState.getPlayer1().getMana() - c.getManacost());
       gameState.currentMaxMana = gameState.getPlayer1().getMana();
       // 重新绘制蓝条
       BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());
       return true;
   }

    /**
     * 这张卡的长久效果
     */
    public boolean longEffect() {
        return true;
    }

    /**
     * 这张卡的名称
     */
    public String getName() {
        return "";
    }

    public Card getCard() {
        return card;
    }

    public Unit getTargetUnit() {
        return targetUnit;
    }

    public GameState getGameState() {
        return gameState;
    }

    public ActorRef getOut() {
        return out;
    }

    public Tile getTile() {
        return tile;
    }

    public BackgroundLogic.BackgroundLogicEnum getBackgroundLogicEnum() {
        return backgroundLogicEnum;
    }

    public void setBackgroundLogicEnum(BackgroundLogic.BackgroundLogicEnum backgroundLogicEnum) {
        this.backgroundLogicEnum = backgroundLogicEnum;
    }
}
