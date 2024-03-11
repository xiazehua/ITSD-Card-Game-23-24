package logic;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

import java.util.ArrayList;
import java.util.Map;

/**
 * 设置敌方的AI行动逻辑
 */
public class EnemyAiLogic {
    private GameState gameState;
    private ActorRef out;

    public EnemyAiLogic(GameState gameState, ActorRef out) {
        this.gameState = gameState;
        this.out = out;
    }

    public void action() {
        //todo : 写死法力值
        gameState.players[1].setMana(9);
        //AI取牌
        if(gameState.Player2CurrentCardSend < gameState.Player2Cards.size()){
            gameState.Player2currentCards.add(gameState.Player2Cards.get(gameState.Player2CurrentCardSend));
            gameState.Player2CurrentCardSend++;
        }
        try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

        //遍历场上所有单位
        ArrayList<Integer> remID = new ArrayList<>();
        for (Map.Entry<Integer, Unit> entry : gameState.Player2unitList.entrySet()){
            System.out.println("=======AI:action:Unit:id:"+entry.getValue().getId());
            remID.add(gameState.unitControl(out, entry.getValue()));
        }

        for(Integer rem : remID){if(rem != -1) {gameState.Player2unitList.remove(rem);}}

        try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

        //以本体单位锁定对面的最近目标
        Unit target = gameState.findUnits(gameState.Player2unitList.get(10));
        if (target == null)return;
        Tile t = BasicObjectBuilders.loadTile(target.getPosition().getTilex(), target.getPosition().getTiley());
        try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

        ArrayList<Card> rememberID = new ArrayList<>();

        //遍历当前发的牌
//        for(Card card : gameState.Player2currentCards){
//            //如果是召唤牌，则判断当前位置是否有单位，且在本体的攻击范围内进行召唤
//            if(card.isCreature() && card.getManacost() < gameState.players[1].getMana()){
//                rememberID.add(card);
//                Tile validTile = gameState.getValidTile(target, gameState.Player2unitList.get(10));
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley());}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() + 2);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley());}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 2);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() + 4);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley() - 1);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
//                if(gameState.getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley() + 1);}
//                //System.out.println(validTile.getTilex() + " " + validTile.getTiley());
//                gameState.cardUse(out, card, validTile, target);
//            }
//
//            //如果是法术牌小于当前mana就打出
//            if(!card.isCreature() && card.getManacost() < gameState.players[1].getMana()){
//                rememberID.add(card);
//                gameState.cardUse(out, card, t, target);
//            }
//        }

//        for(Card DelCard : rememberID){gameState.Player2currentCards.remove(DelCard);}

        try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
    }

}
