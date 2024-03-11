package structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import akka.actor.ActorRef;
import commands.BasicCommands;
import logic.CardLogic;
import structures.basic.*;
import utils.BasicObjectBuilders;
import logic.PlayerGameLogic;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 *
 * @author Dr. Richard McCreadie
 */
public class GameState {

	public int unitStausList[][] = {
		{1, 0, 0, 1}, {1, 2, 3, 1}, {1, 3, 3, 2}, {1, 5, 4, 2}, {1, 6, 1, 4}, {1, 8, 3, 3}, {1, 9, 5, 4},{1, 11, 1, 1},
		{2, 0, 4, 2}, {2, 1, 0, 3}, {2, 2, 1, 5}, {2, 3, 3, 2}, {2, 5, 5, 4}, {2, 6, 1, 1}, {2, 7, 3, 10}
	};

	public boolean gameInitalised = false;

    public boolean something = false;

    public Tile currentTile = new Tile();

    public Unit currentUnit = new Unit();
    // 友军添加的最新的newUnit
    public Unit newUnit;

    public Card currentCard;

    public int currentMaxMana = 2;

    public int Player1CurrentCardGet = 3;

    public int Player1CurrentCardSend = 3;

    public int Player2CurrentCardSend = 0;

    public Player[] players = new Player[]{new Player(), new Player()};

    public HashMap<Integer, Unit> Player1unitList = new HashMap<>();
    public HashMap<Integer, Unit> Player2unitList = new HashMap<>();

    public List<Card> Player1Cards = OrderedCardLoader.getPlayer1Cards(1);
    public List<Card> Player2Cards = OrderedCardLoader.getPlayer2Cards(1);
    public ArrayList<Card> Player1currentCards = new ArrayList<>();
    public ArrayList<Card> Player2currentCards = new ArrayList<>();

    public boolean ifPlayer1Attacked = false;
    public boolean ifPlayer2Attacked = false;

    public boolean ifUnitdied = false;
    public boolean ifSummoned = false;

    //锁定：gameState加一个boolean lock作为最高优先级，所有子方法都执行前进行一个判断，只要这个锁触发，就直接跳出
    public boolean gameLock;
    public boolean player1IsMove = false;

    private List<Unit> deathPool = new ArrayList<>();

    public List<Unit> getDeathPool() {
        return deathPool;
    }

    public void setDeathPool(List<Unit> deathPool) {
        this.deathPool = deathPool;
    }

    public void addDeathPool(Unit unit) {
        deathPool.add(unit);
    }

    /**
     * 合作者的代码
     * @param out
     * @param attacker
     * @param target
     * @param t
     * @param whichPlayer
     * @return
     */
    public Integer unitAttack(ActorRef out, Unit attacker, Unit target, Tile t, boolean whichPlayer){
        System.out.println("unitAttack");
        //记录一次攻击行为
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        //播放主动攻击方的动画

        BasicCommands.setUnitHealth(out, target, target.getHealth() - attacker.getAttack());
        getPlayer1().setHealth(target.getHealth() - attacker.getAttack());
        //减少目标的血量
        System.out.println("=================unitAttack:Health"+getPlayer1().getHealth());
        getPlayer1().setHealth(target.getHealth() - attacker.getAttack());
        BasicCommands.setPlayer1Health(out, getPlayer1());
        //如果当前目标是英雄本体
        System.out.println("unitAttack:Hero1:"+target.getId());
        System.out.println("unitAttack:whichPlayer"+whichPlayer);
        if(target.getId() == 10){
            if(whichPlayer){
                players[1].setHealth(players[1].getHealth() - attacker.getAttack());
                BasicCommands.setPlayer1Health(out, players[1]);
            }else{
                players[0].setHealth(players[0].getHealth() - attacker.getAttack());
                BasicCommands.setPlayer1Health(out, players[0]);
            }
        }
        if(players[0].getHealth() < 0){BasicCommands.addPlayer1Notification(out, "AI WIN", 1); gameLock = true;}
        if(players[1].getHealth() < 0){BasicCommands.addPlayer1Notification(out, "PLAYER WIN", 1); gameLock = true;}

        //判断目标是否死亡
        if(target.getHealth() <= 0){
            BasicCommands.playUnitAnimation(out, target, UnitAnimationType.death);
            System.out.println(target.getId() + "死亡");
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            BasicCommands.deleteUnit(out, target);
            ifUnitdied = true;
            return (Integer)target.getId();
        }

        //播放目标反击动画
       // BasicCommands.playUnitAnimation(out, target, UnitAnimationType.attack);
       // try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        //减少攻击者血量
       // BasicCommands.setUnitHealth(out, attacker, attacker.getHealth() - target.getAttack());
        //attacker.setHealth(attacker.getHealth() - target.getAttack());
        
        //如果当前攻击者是英雄本体
        System.out.println("unitAttack:Hero2:"+target.getId());
        System.out.println("unitAttack:whichPlayer"+whichPlayer);
        if(target.getId() == 10){
            if(whichPlayer){
                players[0].setHealth(players[0].getHealth() - target.getAttack());
                BasicCommands.setPlayer1Health(out, players[0]);
            }else{
                players[1].setHealth(players[1].getHealth() - target.getAttack());
                BasicCommands.setPlayer1Health(out, players[1]);
            }
        }
        if(players[0].getHealth() < 0){BasicCommands.addPlayer1Notification(out, "AI WIN", 1); gameLock = true;}
        if(players[1].getHealth() < 0){BasicCommands.addPlayer1Notification(out, "PLAYER WIN", 1); gameLock = true;}

        //判断攻击者是否死亡
        if(attacker.getHealth() <= 0){
            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.death);
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println(attacker.getId() + "死亡");
            BasicCommands.deleteUnit(out, attacker);
            ifUnitdied = true;
            return (Integer)attacker.getId();
        }

        return -1;
    }


    /**
     * 此方法是移动的具体实现
     *
     * @param out 用于发送消息给前端
     * @param u   移动的角色
     * @param t   移动的目标石砖
     */
    public boolean move(ActorRef out, Unit u, Tile t) {
        System.out.println("move");
        // 判断是不是在移动范围内
        if (!ifMove(t, u)) {
            BasicCommands.addPlayer1Notification(out, "请重新选择移动的瓷砖，该目标不在移动范围内！", 1);
            return false;
        }
        // 在前端执行移动
        BasicCommands.moveUnitToTile(out, u, t);
        closeTile(out, u);
        // 改变该角色的位置
        u.setPositionByTile(t);
        currentTile = t;
        currentUnit = u;
        return true;
    }

    // 判断目标瓷砖的位置是不是在移动范围内
    public boolean ifMove(Tile tragetTile, Unit u) {
        ArrayList<Tile> tankArea = getTankArea(u);

        for (Tile tile : tankArea) {
            if (tile.getTilex() == tragetTile.getTilex()
                    && tile.getTiley() == tragetTile.getTiley()) {
                return true;
            }
            if (tile.getTilex() + 1 == tragetTile.getTilex()
                    && tile.getTiley() == tragetTile.getTiley()) {
                return true;
            }
            if (tile.getTilex() - 1 == tragetTile.getTilex()
                    && tile.getTiley() == tragetTile.getTiley()) {
                return true;
            }
            if (tile.getTilex() == tragetTile.getTilex()
                    && tile.getTiley() - 1 == tragetTile.getTiley()) {
                return true;
            }
            if (tile.getTilex() == tragetTile.getTilex()
                    && tile.getTiley() + 1 == tragetTile.getTiley()) {
                return true;
            }
        }
        return false;
    }

    public void preMove(ActorRef out, Unit u, Tile t) {
        System.out.println("preMove");
        // 点亮当前移动的地板(p), 获取所有可以移动的地板
        ArrayList<Tile> area = PlayerGameLogic.getArea(u);
        // 点亮石板
        for (Tile tile : area) {
            BasicCommands.drawTile(out, tile, 1);
        }
    }

    public void closeTile(ActorRef out, Unit u) {
        ArrayList<Tile> area = PlayerGameLogic.getArea(u);
        // 确认移动完后，遍历把所有的点亮的地图熄灭
        for (Tile tile : area) {
            BasicCommands.drawTile(out, tile, 0);
        }
    }

    // 获取石板上的单位, 这里只能获取玩家一操作的角色，玩家2默认是AI
    public Unit getTileNowUnit(int x, int y) {
        AtomicReference<Unit> unit = new AtomicReference<>();
        // 获取石板上的单位
        Player1unitList.forEach((index, u) -> {
            if (u.getPosition().getTilex() == x && u.getPosition().getTiley() == y) {
                unit.set(u);
            }
        });

        // 获取石板上的单位
//		Player2unitList.forEach((index, u) -> {
//			if (u.getPosition().getTilex() == x && u.getPosition().getTiley() == y) {
//				unit.set(u);
//			}
//		});
        return unit.get();
    }

    public Unit getEnemies(int x, int y) {
        AtomicReference<Unit> unit = new AtomicReference<>();
        // 获取石板上的单位
        Player2unitList.forEach((index, u) -> {
            if (u.getPosition().getTilex() == x && u.getPosition().getTiley() == y) {
                unit.set(u);
            }
        });
        return unit.get();
    }

    public Unit getEnemies(Tile tile) {
        AtomicReference<Unit> unit = new AtomicReference<>();
        // 获取石板上的单位
        Player2unitList.forEach((index, u) -> {
            if (u.getPosition().getTilex() == tile.getTilex() && u.getPosition().getTiley() == tile.getTiley()) {
                unit.set(u);
            }
        });
        return unit.get();
    }

    public void cardUse(ActorRef out, int cardPosition, Tile t, Unit target) {
        cardUse(out, Player1currentCards.get(cardPosition), t, target);
    }

    // 执行卡牌
    public void cardUse(ActorRef out, Card c, Tile t, Unit target) {
        // 使用卡牌效果
        CardLogic logic = new CardLogic(c, target, t,this, out);
        // 使用卡牌
        // 使用之后销毁
        if (logic.effect()) logic.destroy();
    }

    //获取嘲讽
    public ArrayList<Tile> getTankArea(Unit u) {
        //获取单位嘲讽坐标
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        Position unitPosition = u.getPosition();
        int xtile = unitPosition.getTilex();
        int ytile = unitPosition.getTiley();

        tiles.add(BasicObjectBuilders.loadTile(xtile, ytile));
        if (xtile - 1 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile - 1, ytile));
        }
        if (ytile - 1 >= 0) {
            tiles.add(BasicObjectBuilders.loadTile(xtile, ytile - 1));
        }
        if (xtile + 1 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile + 1, ytile));
        }
        if (ytile + 1 < 9) {
            tiles.add(BasicObjectBuilders.loadTile(xtile, ytile + 1));
        }
        return tiles;// 嘲讽范围的tile列表

    }

    //获取攻击
    public ArrayList<Tile> getAttackArea(Unit u, HashMap<Integer, Unit> enemyUnitList) {
        // 获取单位的攻击坐标
        // 所有可移动坐标=可攻击目标
        ArrayList<Tile> tiles = PlayerGameLogic.getArea(u);
        int[] rangedID = {1, 2, 3}; // id for 远程
        for (int i : rangedID) {
            if (u.getId() == i) { // 如果unit的id属于远程
                for (Integer key : enemyUnitList.keySet()) { //将敌方所有场上的unit遍历
                    int xtile = enemyUnitList.get(key).getPosition().getTilex();
                    int ytile = enemyUnitList.get(key).getPosition().getTiley();
                    Tile tmp = new Tile(); // 储存为tile
                    tmp.setTilex(xtile);
                    tmp.setTiley(ytile);
                    tiles.add(tmp); // 添加进可攻击的列表
                }
            }
        }

        return tiles; // 可攻击范围的tile列表
    }

    //判断当前是否为嘲讽范围
    public boolean ifTanked(ActorRef out, Tile t) {
        if (Player2unitList.get(1) == null) {
            return false;
        }

        ArrayList<Tile> tileset = getTankArea(Player2unitList.get(1));

        for (int i = 0; i < tileset.size(); i++) {
            if (t.getTilex() == tileset.get(i).getTilex() && t.getTiley() == tileset.get(i).getTiley()) {
                return true;
            }
        }

        return false;
    }

    //bad_omen技能
    public void bad_omenCheck(ActorRef out) {
        //检测bad_omen是否活着
        if (Player1unitList.get(1) == null) {
            return;
        }

        // 获取单位攻击力
        int attack = Player1unitList.get(1).getAttack();

        //当前攻击力+1
        attack++;
        BasicCommands.setUnitAttack(out, Player1unitList.get(1), attack);
        Player1unitList.get(0).setAttack(attack);
    }

	//shadow_wathcer技能
	public void shadow_watcherCheck(ActorRef out){
		//检测shadow_watcher是否活着
		if(Player1unitList.get(3) == null){return;}

		// 获取单位攻击力
        int attack = Player1unitList.get(3).getAttack();

		//当前攻击力+1
		attack++;
		BasicCommands.setUnitAttack(out, Player1unitList.get(3), attack);
		Player1unitList.get(3).setAttack(attack);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		//获取单位体力
		int health = Player1unitList.get(3).getHealth();

		//当前体力+1,体力最大值+1
		health++;
		BasicCommands.setUnitHealth(out, Player1unitList.get(3), health);
		Player1unitList.get(3).setHealth(health);
		Player1unitList.get(3).setMaxhealth(Player1unitList.get(3).getMaxhealth() + 1);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}

	//gloom_chaser技能
	public void gloom_chaserCheck(ActorRef out){
		//检测gloom_chaser是否活着
		if(Player1unitList.get(2) == null){return;}

		//获取可放置幽魂的位置
		Tile validTile = BasicObjectBuilders.loadTile(Player1unitList.get(2).getPosition().getTilex(), Player1unitList.get(2).getPosition().getTiley());
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley());}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() + 2);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley());}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 2);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() + 4);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley() - 1);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex(), validTile.getTiley() - 1);}
		if(getUnits(validTile) != null){validTile = BasicObjectBuilders.loadTile(validTile.getTilex() + 1, validTile.getTiley() + 1);}

		//创造一个幽魂
		Player1unitList.put(11, BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 10, Unit.class));
		Player1unitList.get(11).setPositionByTile(validTile);
		BasicCommands.drawUnit(out, Player1unitList.get(11), validTile);

		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

		//设置幽魂的数值
		// setUnitAttack
		BasicCommands.setUnitAttack(out, Player1unitList.get(11), 1);
		Player1unitList.get(0).setAttack(1);

		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

		// setUnitHealth
		BasicCommands.setUnitHealth(out, Player1unitList.get(11), 1);
		Player1unitList.get(10).setHealth(1);
		Player1unitList.get(10).setMaxhealth(1);
		try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
	}

	//AI
	public Integer unitControl(ActorRef out, Unit u){

		//锁定目标
		Unit target = findUnits(u);
		Integer remid = -1;
		try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

		//扫描攻击范围
		Tile v = checkAttackArea(u);
		try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}

		if(v.getTilex() != -1 && v.getTiley() != -1 && !ifPlayer2Attacked && target != null){
			BasicCommands.drawTile(out, v, 2);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("================unitControl:unitAttack:01=====================");
			remid = unitAttack(out, u, target, v, false);
			BasicCommands.drawTile(out, v, 0);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			return remid;
		}

		//计算移动距离并输出最合理移动点
		Tile m = calculateMoveDistance(u, target);
		try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
		Tile unitSelf = BasicObjectBuilders.loadTile(u.getPosition().getTilex(), u.getPosition().getTiley());

		//判断邻近嘲讽并执行移动
		if(!ifneighbour(unitSelf)){
			BasicCommands.drawTile(out, m, 1);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			move(out, u, m);
			BasicCommands.drawTile(out, m, 0);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}

		//判断攻击，能攻击到就执行攻击，不能攻击到，遍历到下一个单位
		v = checkAttackArea(u);
		if(v != null && target != null && ifPlayer2Attacked){
			BasicCommands.drawTile(out, v, 2);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("================unitControl:unitAttack:01=====================");
			remid = unitAttack(out, u, target, v, false);
			BasicCommands.drawTile(out, v, 0);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			ifPlayer2Attacked = true;
			return remid;
		}

		return remid;
	}

	public Tile checkAttackArea(Unit u){
		ArrayList<Tile> validPosition = new ArrayList<Tile>();

		validPosition = getAttackArea(u, Player1unitList);

		for (Map.Entry<Integer, Unit> entry : Player1unitList.entrySet()){
			for(Tile v : validPosition){
				if(v.getTilex() == entry.getValue().getPosition().getTilex() && v.getTiley() == entry.getValue().getPosition().getTiley()){
					return v;
				}
			}
		}

		Tile v = BasicObjectBuilders.loadTile(-1, -1);
		return v;
	}

	public Unit getUnits(Tile p){
		Tile temp;
		for (Map.Entry<Integer, Unit> entry : Player1unitList.entrySet()){
			temp = BasicObjectBuilders.loadTile(entry.getValue().getPosition().getTilex(), entry.getValue().getPosition().getTiley());
			if(p.getTilex() == temp.getTilex() && p.getTiley() == temp.getTiley()){return entry.getValue();}
		}

		for (Map.Entry<Integer, Unit> entry : Player2unitList.entrySet()){
			temp = BasicObjectBuilders.loadTile(entry.getValue().getPosition().getTilex(), entry.getValue().getPosition().getTiley());
			if(p.getTilex() == temp.getTilex() && p.getTiley() == temp.getTiley()){return entry.getValue();}
		}

		return null;
	}

	public Unit findUnits(Unit u){
        if (u == null) return null;

		//如果嘲讽单位在邻近直接锁定嘲讽单位
		Tile unitSelf = BasicObjectBuilders.loadTile(u.getPosition().getTilex(),
                u.getPosition().getTiley());
		if(ifneighbour(unitSelf)){
			return Player1unitList.get(7);
		}

		int totalDistance = 0;
		int minDistance = 9999;
		Unit finalUnit = null;

		for (Map.Entry<Integer, Unit> entry : Player1unitList.entrySet()){
			totalDistance = Math.abs(u.getPosition().getTilex() - entry.getValue().getPosition().getTilex()) +
			Math.abs(u.getPosition().getTiley() - entry.getValue().getPosition().getTiley());
			if(minDistance > totalDistance){minDistance = totalDistance; finalUnit = entry.getValue();}
		}

		return finalUnit;
	}

	public Tile getValidTile(Unit target, Unit unitSelf){
		ArrayList<Tile> unitSelfTile = getAttackArea(unitSelf, null);

		int totalDistance = 0;
		int minDistance = 9999;
		Tile finalTile = null;

		for (Tile t : unitSelfTile){
			totalDistance = Math.abs(t.getTilex() - target.getPosition().getTilex()) +
			Math.abs(t.getTiley() - target.getPosition().getTiley());
			if(minDistance > totalDistance){minDistance = totalDistance; finalTile = t;}
		}
		return finalTile;
	}

	public Tile calculateMoveDistance(Unit u, Unit target){
		int currentDistance = 0;
		int minDistance = 9999;
		Tile finaltile = new Tile();
		ArrayList<Tile> tileset = PlayerGameLogic.getArea(u);
		for(int i = 0; i < tileset.size(); i++){
			currentDistance = Math.abs(tileset.get(i).getTilex() - target.getPosition().getTilex()) + Math.abs(tileset.get(i).getTiley() - target.getPosition().getTiley());
			if(!checkUnitsExistence(tileset.get(i)) && currentDistance < minDistance){minDistance = currentDistance; finaltile = tileset.get(i);}
		}

		return finaltile;
	}

	public boolean checkUnitsExistence(Tile t){
		for (Map.Entry<Integer, Unit> entry : Player1unitList.entrySet()){
			if(t.getTilex() == entry.getValue().getPosition().getTilex() && t.getTiley() == entry.getValue().getPosition().getTiley()){return true;}
		}

		return false;
	}

	public boolean ifneighbour(Tile t){
		if(Player1unitList.get(7) == null){return false;}

		ArrayList<Tile> tileset = getTankArea(Player2unitList.get(7));

		for(int i = 0; i < tileset.size(); i++){
			if(t.getTilex() == tileset.get(i).getTilex() && t.getTiley() == tileset.get(i).getTiley()){
				return true;
			}
		}

		return false;
	}

    public boolean isGameInitalised() {
        return gameInitalised;
    }

    public void setGameInitalised(boolean gameInitalised) {
        this.gameInitalised = gameInitalised;
    }

    public boolean isSomething() {
        return something;
    }

    public void setSomething(boolean something) {
        this.something = something;
    }

    public Player getPlayer1() {
        return players[0];
    }

    public Player getPlayer2() {
        return players[1];
    }

}
