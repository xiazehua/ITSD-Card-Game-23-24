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
import utils.StaticConfFiles;

/**
 * 实现卡牌幽灵追踪者的效果
 */
public class GloomChaserCard extends CardEffect {
    public GloomChaserCard(Card card, Unit targetUnit, Tile t, GameState gameState, ActorRef out) {
        super(card, targetUnit, t, gameState, out);
        setBackgroundLogicEnum(BackgroundLogic.BackgroundLogicEnum.BIRTH);
    }

    @Override
    public boolean longEffect() {
        // 每当一个单位被召唤到场上追加一个幽灵
        // 获取最新添加的友军单位
        System.out.println("====================longEffect=========================");
        Tile unitTile = PlayerGameLogic.getUnitTile(gameState.newUnit);
        // 获取这个生物后面是否有单位
        Unit tileNowUnit = gameState.getTileNowUnit(unitTile.getTilex() - 1, unitTile.getTiley());
        Tile waithlingPos = BasicObjectBuilders.loadTile(unitTile.getTilex() - 1, unitTile.getTiley());
        // 如果没有单位生成一个幽灵
        if (tileNowUnit == null) {
            int id = gameState.Player1unitList.size() +10000;
            // 生成幽灵
            Unit unit1 = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, id, Unit.class);
            unit1.setPositionByTile(waithlingPos);
            System.out.println("===========================unit1.setId:"+id);
            unit1.setId(id);
            unit1.setAttack(1);
            unit1.setHealth(1);
            unit1.setMaxhealth(1);
            // 在前端绘制该生物的生命值,攻击力
            BasicCommands.drawUnit(out, unit1, waithlingPos);
            PlayerGameLogic.sleep(100);
            BasicCommands.setUnitHealth(out, unit1, 1);
            PlayerGameLogic.sleep(100);
            BasicCommands.setUnitAttack(out, unit1, 1);
            // 添加到 Player1unitList 当中
            gameState.Player1unitList.put(id, unit1);
        }
        return true;
    }


}
