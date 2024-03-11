package logic.effect;

import akka.actor.ActorRef;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CardEffectLogic {

    private final Map<String, Supplier<CardEffect>> effectMap = new HashMap<>();
    private Card card;
    private Unit targetUnit;
    private GameState gameState;
    private ActorRef out;
    private Tile tile;

    public CardEffectLogic(Card card, Unit targetUnit, Tile tile, GameState gameState, ActorRef out) {
        this.card = card;
        this.targetUnit = targetUnit;
        this.gameState = gameState;
        this.out = out;
        this.tile = tile;
        initializeEffectMap();
    }

    private void initializeEffectMap() {
        effectMap.put(BAD_OMEN, () -> new BadOmenCard(card, targetUnit, tile, gameState, out));
        effectMap.put(GLOOM_CHASER, () -> new GloomChaserCard(card, targetUnit, tile, gameState, out));
        effectMap.put(SHADOW_WATCHER, () -> new ShadowWatcherCard(card, targetUnit, tile, gameState, out));
        effectMap.put(SHADOWDANCER, () -> new ShadowdancerCard(card, targetUnit, tile, gameState, out));
        // 为每个卡牌名称添加对应的工厂方法到Map中
        // effectMap.put(GLOOM_CHASER, () -> new GloomChaserCard(card, targetUnit, tile, gameState, out));
        // effectMap.put(ROCK_PULVERISER, () -> new RockPulveriserCard(card, targetUnit, tile, gameState, out));
        // 以此类推，为其他卡牌也添加相应的实现
    }

    public CardEffect apply(String cardName) {
        // 使用Map查找并获取对应的CardEffect实例
        Supplier<CardEffect> effectSupplier = effectMap.get(cardName);
        if (effectSupplier != null) {
            return effectSupplier.get();
        } else {
            // 返回一个默认的CardEffect实例或处理未知卡牌的逻辑
            return new CardEffect(card, targetUnit, tile, gameState, out);
        }
    }

    // 你可以移除之前的各个cardEffectLogic方法，因为它们的逻辑已经被移到了initializeEffectMap中

    // 卡牌名称常量
    public static final String BAD_OMEN = "Bad Omen";
    public static final String GLOOM_CHASER = "Gloom Chaser";
    public static final String ROCK_PULVERISER = "Rock Pulveriser";
    public static final String SHADOW_WATCHER = "Shadow Watcher";
    public static final String NIGHTSORROW_ASSASSIN = "Nightsorrow Assassin";
    public static final String BLOODMOON_PRIESTESS = "Bloodmoon Priestess";
    public static final String SHADOWDANCER = "Shadowdancer";
    public static final String HORN_OF_THE_FORSAKEN = "Horn of the Forsaken";
    public static final String WRAITHLING_SWARM = "Wraithling Swarm";
    public static final String DARK_TERMINUS = "Dark Terminus";
}
