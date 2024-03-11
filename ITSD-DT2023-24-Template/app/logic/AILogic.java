package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 主要是我方AI的使用逻辑类
 */
public class AILogic {
    private GameState gameState;
    private ActorRef out;

    public AILogic(GameState gameState, ActorRef out) {
        this.gameState = gameState;
        this.out = out;
    }

    // 行动
    public void action() {
        // 获取所有我方实体
        BasicCommands.addPlayer1Notification(out, "我现在发起进攻了", 1);
        System.out.println("attack01");
        gameState.Player1unitList.forEach((index, unit) -> {
            // 对最近范围内的敌人发动进攻
            // 获取所有能攻击的实体
            List<Unit> enemys = PlayerGameLogic.getEnemys(unit, gameState);
            System.out.println("num"+index);
            System.out.println("attack02");
            System.out.println("totalEnemyNums"+enemys.size());
            // 对它发动进攻
            if (enemys.size() > 0) {
                // 选择第一个敌人
                Unit enemy = enemys.get(0);
                // 发动攻击
                System.out.println("enemyHealth"+enemy.getHealth());
                PlayerGameLogic.attack(out, gameState, unit, enemy);
                
            }
        });
        
    }

}
