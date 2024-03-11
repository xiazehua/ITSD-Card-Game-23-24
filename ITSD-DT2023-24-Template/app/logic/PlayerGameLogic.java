package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 这是项目常见的一些操作
 */
public class PlayerGameLogic {

    // 卡牌id的前缀
    public static int cardIdPre = 1000;

    /**
     * 清除全场的高亮
     */
    public static void clearAllTile(ActorRef out) {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 5; j++) {
                Tile tile = BasicObjectBuilders.loadTile(i, j);
                BasicCommands.drawTile(out, tile, 0);
            }
        }
    }

    /**
     * 设置当前线程的暂停时间
     */
    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void delAllCard(ActorRef out, GameState gameState) {
        // 删除之前的所有卡牌
        for (int i = 0; i < gameState.Player1currentCards.size(); i++) {
            BasicCommands.deleteCard(out, i + 1);
        }
    }

    /**
     * 重新绘制所有卡牌
     */
    public static void redrawAllCard(ActorRef out, GameState gameState) {
        for (int i = 0; i < gameState.Player1currentCards.size(); i++) {
            BasicCommands.drawCard(out, gameState.Player1currentCards.get(i), i + 1, 0);
        }
    }

    /**
     * 判断场上所有可以放置的地方
     */
    public static void highlight(ActorRef out, GameState gameState) {
        // 获取单位的移动范围
        ArrayList<Tile> moveArea = getArea((gameState.Player1unitList.get(0)));
        // 将他们的移动范围都点亮
        moveArea.forEach(tile -> {
            // 点亮石板
            BasicCommands.drawTile(out, tile, 1);
        });
    }

    /**
     * 判断该目标位友方
     */
    public static boolean isFriend(GameState gameState, Tile tile) {
        for (int i = 0; i < gameState.Player1unitList.size(); i++) {
            if (gameState.Player1unitList.get(i).getPosition().getTilex() == tile.getTilex()
                && gameState.Player1unitList.get(i).getPosition().getTiley() == tile.getTiley()){
                return true;
            }
        }
        return false;
    }


    /**
     * 判断该目标位友方
     */
    public static boolean isFriend(GameState gameState, Unit unit) {
        for (int i = 0; i < gameState.Player1unitList.size(); i++) {
            if (gameState.Player1unitList.get(i).equals(unit)) return true;
        }
        return false;
    }

    /**
     * 获取单位脚下的tile
     */
    public static Tile getUnitTile(Unit unit) {
        return BasicObjectBuilders.loadTile(unit.getPosition().getTilex(), unit.getPosition().getTiley());
    }


    /**
     * 标红敌方目标
     * @param out
     * @param gameState
     */
    public static void highlightEnemy(ActorRef out, GameState gameState) {
        System.out.println("=================highlightEnemy=======================");
        for (int i = 0; i < gameState.Player2unitList.size(); i++) {
            Unit unit = gameState.Player2unitList.get(i);
            BasicCommands.drawTile(out, getUnitTile(unit), 2);
        }
    }

    /**
     * 标记友方目标
     * @param out
     * @param gameState
     */
    public static void highlightFriend(ActorRef out, GameState gameState) {
        for (int i = 0; i < gameState.Player1unitList.size(); i++) {
            Unit unit = gameState.Player1unitList.get(i);
            System.out.println("Player1unitList"+ unit.getId());
            System.out.println("==================highlightFriend===================");
            BasicCommands.drawTile(out, getUnitTile(unit), 1);
        }
    }

    /**
     * 获取当前可移动范围
     * @param u
     * @return
     */
    public static ArrayList<Tile> getArea(Unit u) {
        //获取单位的周围可移动坐标
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        int xtile = u.getPosition().getTilex();
        int ytile = u.getPosition().getTiley();

        System.out.println(xtile + " " + ytile);

        if (xtile + 1 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile + 1, ytile));
        }
        if (xtile + 2 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile + 2, ytile));
        }
        if (ytile + 1 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile, ytile + 1));
        }
        if (ytile + 2 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile, ytile + 2));
        }

        if (xtile - 1 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile - 1, ytile));
        }
        if (xtile - 2 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile - 2, ytile));
        }
        if (ytile - 1 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile, ytile - 1));
        }
        if (ytile - 2 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile, ytile - 2));
        }

        if (xtile + 1 < 9 && ytile + 1 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile + 1, ytile + 1));
        }
        if (xtile - 1 >= 0 && ytile - 1 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile - 1, ytile - 1));
        }
        if (xtile - 1 >= 0 && ytile + 1 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile - 1, ytile + 1));
        }
        if (xtile + 1 < 9 && ytile - 1 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile + 1, ytile - 1));
        }

        for (Tile t : tiles) {
            System.out.println(t.getTilex() + " " + t.getTiley());
        }

        return tiles;// 可移动范围的tile列表
    }

    /**
     * 获取卡牌的id
     */
    public static int getCardId(Card card) {
        return cardIdPre + card.getId();
    }

    /**
     * 获取攻击范围内所有的敌人
     * 这个方法主要用于AI自动攻击
     */
    public static List<Unit> getEnemys(Unit unit, GameState gameState) {
        ArrayList<Unit> units = new ArrayList<>();
        System.out.println("=====================getEnemys===========================");
        // 根据unit获取坐标
        Tile unitTile = getUnitTile(unit);
        // 获取攻击范围, 以unitTile为中心 + 1
        List<Tile> attackArea = getAttackArea(unitTile);
        // 获取攻击范围内的敌人
        gameState.Player2unitList.forEach((integer, u) -> {
            for (Tile tile : attackArea) {
                if (u.getPosition().getTilex() == tile.getTilex() && u.getPosition().getTiley() == tile.getTiley()) {
                    units.add(u);
                }
            }
        });
        return units;
    }

    /**
     * 获取普通目标的攻击范围，非人物本身
     * @param tile
     * @return
     */
    public static List<Tile> getAttackArea(Tile tile) {
        ArrayList<Tile> attackArea = new ArrayList<>();
        attackArea.add(BasicObjectBuilders.loadTile(tile.getTilex() + 1, tile.getTiley()));
        attackArea.add(BasicObjectBuilders.loadTile(tile.getTilex() - 1, tile.getTiley()));
        attackArea.add(BasicObjectBuilders.loadTile(tile.getTilex(), tile.getTiley() + 1));
        attackArea.add(BasicObjectBuilders.loadTile(tile.getTilex(), tile.getTiley() - 1));
        return attackArea;
    }

    /**
     * 进行攻击
     * @param out
     * @param gameState
     * @param unit
     * @param enemy
     */
    public static void attack(ActorRef out, GameState gameState, Unit unit, Unit enemy) {
        // 攻击
        enemy.setHealth(enemy.getHealth() - unit.getAttack());
        // 播放动画
        BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
        sleep(100);
        // 更新敌人的生命值
        BasicCommands.setUnitHealth(out, enemy, enemy.getHealth());
        System.out.println("attack:Health"+enemy.getHealth());
        gameState.getPlayer2().setHealth(enemy.getHealth());
        BasicCommands.setPlayer2Health(out, gameState.getPlayer2());
        sleep(100);
        // 判断敌人的生命值是否低于0
        if (enemy.getHealth() < 0) {
            // 删除敌人
            gameState.Player2unitList.remove(enemy.getId());
            // 播放死亡效果
            BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.death);
            
            sleep(100);
            BasicCommands.deleteUnit(out, enemy);
            sleep(100);
            // 添加到死亡池当中
            if (!gameState.getDeathPool().contains(enemy)) {
                gameState.getDeathPool().add(enemy);
                sleep(100); // 不要改这段代码虽然我也不知道为什么，但是加了延迟真有用
            }
        }
    }
}
