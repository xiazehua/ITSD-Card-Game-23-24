package logic;

import logic.fun.Func;

import java.util.HashMap;
import java.util.Map;

public class BackgroundLogic {

    public enum BackgroundLogicEnum {
        // 死亡  // 出生  // 常规状态
        DEATH,  BIRTH, COMM
    }

    private Map<Integer, Func> deathMap = new HashMap<>();
    private Map<Integer, Func> commMap = new HashMap<>();
    private Map<Integer, Func> birthMap = new HashMap<>();

    private static BackgroundLogic instance;

    public void put(Integer key, Func func, BackgroundLogicEnum backgroundLogicEnum) {
        if (backgroundLogicEnum == BackgroundLogicEnum.DEATH)
            deathMap.put(key, func);
        else if (backgroundLogicEnum == BackgroundLogicEnum.BIRTH)
            birthMap.put(key, func);
        else if (backgroundLogicEnum == BackgroundLogicEnum.COMM)
            commMap.put(key, func);
    }

    // 删除
    public void remove(Integer key) {
        deathMap.remove(key);
        commMap.remove(key);
        birthMap.remove(key);
    }

    public void apply(BackgroundLogicEnum backgroundLogicEnum) {
        if (backgroundLogicEnum == BackgroundLogicEnum.DEATH) {
            for (Map.Entry<Integer, Func> entry : deathMap.entrySet()) {
                entry.getValue().apply();
            }
        } else if (backgroundLogicEnum == BackgroundLogicEnum.BIRTH) {
            for (Map.Entry<Integer, Func> entry : birthMap.entrySet()) {
                entry.getValue().apply();
            }
        } else if (backgroundLogicEnum == BackgroundLogicEnum.COMM) {
            for (Map.Entry<Integer, Func> entry : commMap.entrySet()) {
                entry.getValue().apply();
            }
        }
    }

    public static BackgroundLogic getInstance() {
        if (instance == null) {
            instance = new BackgroundLogic();
        }
        return instance;
    }


}
