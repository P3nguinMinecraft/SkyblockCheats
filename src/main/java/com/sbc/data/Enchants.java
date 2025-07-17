package com.sbc.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Enchants {
    ABSORB(1),
    ANGLER(5),
    ARCANE(5),
    BANE_OF_ARTHROPODS(5),
    BIG_BRAIN(5),
    BLAST_PROTECTION(5),
    BLESSING(5),
    CASTER(5),
    CAYENNE(5),
    CHAMPION(1),
    CHANCE(3),
    CHARM(5),
    CLEAVE(5),
    COMPACT(1),
    CORRUPTION(5),
    COUNTER_STRIKE(5),
    CRITICAL(5),
    CUBISM(5),
    CULTIVATING(1),
    DEDICATION(3),
    DELICATE(5),
    DEPTH_STRIDER(3),
    DIVINE_GIFT(3),
    DRAGON_HUNTER(5),
    DRAGON_TRACER(5),
    DRAIN(3),
    EFFICIENCY(5),
    ENDER_SLAYER(5),
    EXECUTE(5),
    EXPERIENCE(3),
    EXPERTISE(1),
    FEATHER_FALLING(10),
    FEROCIOUS_MANA(10),
    FIRE_ASPECT(2),
    FIRE_PROTECTION(3),
    FIRST_STRIKE(4),
    FLAME(2),
    FOREST_PLEDGE(5),
    FORTUNE(3),
    FRAIL(5),
    GIANT_KILLER(5),
    GREAT_SPOOK(1),
    GREEN_THUMB(5),
    GROWTH(5),
    HARDENED_MANA(10),
    HARVESTING(5),
    HECATOMB(1),
    ICE_COLD(5),
    IMPALING(3),
    INFINITE_QUIVER(10),
    KNOCKBACK(2),
    LAPIDARY(5),
    LETHALITY(5),
    LIFE_STEAL(3),
    LOOTING(3),
    LUCK(5),
    LUCK_OF_THE_SEA(5),
    LURE(5),
    MAGNET(5),
    MANA_STEAL(3),
    MANA_VAMPIRE(10),
    OVERLOAD(5),
    PALEONTOLOGIST(5),
    PESTERMINATOR(5),
    PIERCING(1),
    PISCARY(5),
    POWER(5),
    PRISMATIC(5),
    PROJECTILE_PROTECTION(5),
    PROSECUTE(5),
    PROSPERITY(5),
    PROTECTION(5),
    PUNCH(2),
    QUANTUM(5),
    QUICK_BITE(5),
    RAINBOW(1),
    REFLECTION(5),
    REJUVENATE(5),
    REPLENISH(1),
    RESPIRATION(3),
    RESPITE(5),
    SCAVENGER(5),
    SCUBA(5),
    SHARPNESS(5),
    SILK_TOUCH(1),
    SMALL_BRAIN(5),
    SMARTY_PANTS(5),
    SMELTING_TOUCH(1),
    SMITE(5),
    SMOLDERING(5),
    SNIPE(3),
    SPIKED_HOOK(5),
    STEALTH(1),
    STRONG_MANA(10),
    SUGAR_RUSH(3),
    SUNDER(5),
    TABASCO(3),
    THORNS(3),
    THUNDERBOLT(5),
    THUNDERLORD(5),
    TIDAL(3),
    TITAN_KILLER(5),
    TOXOPHILITE(1),
    TRANSYLVANIAN(5),
    TRIPLE_STRIKE(4),
    TRUE_PROTECTION(1),
    TURBO_CACTI(5),
    TURBO_CANE(5),
    TURBO_CARROT(5),
    TURBO_COCOA(5),
    TURBO_MELON(5),
    TURBO_MUSHROOMS(5),
    TURBO_POTATO(5),
    TURBO_PUMPKIN(5),
    TURBO_WARTS(5),
    TURBO_WHEAT(5),
    VAMPIRISM(5),
    VENOMOUS(5),
    VICIOUS(5),
    ULTIMATE_BANK(5),
    ULTIMATE_BOBBIN_TIME(5),
    ULTIMATE_CHIMERA(5),
    ULTIMATE_COMBO(5),
    ULTIMATE_DUPLEX(5),
    ULTIMATE_FATAL_TEMPO(5),
    ULTIMATE_FIRST_IMPRESSION(5),
    ULTIMATE_FLASH(5),
    ULTIMATE_FLOWSTATE(3),
    ULTIMATE_HABANERO_TACTICS(5),
    ULTIMATE_INFERNO(5),
    ULTIMATE_LAST_STAND(5),
    ULTIMATE_LEGION(5),
    ULTIMATE_MISSILE(5),
    ULTIMATE_NO_PAIN_NO_GAIN(5),
    ULTIMATE_ONE_FOR_ALL(1),
    ULTIMATE_REFRIGERATE(5),
    ULTIMATE_REND(5),
    ULTIMATE_SOUL_EATER(5),
    ULTIMATE_SWARM(5),
    ULTIMATE_THE_ONE(5),
    ULTIMATE_ULTIMATE_JERRY(5),
    ULTIMATE_ULTIMATE_WISE(5),
    ULTIMATE_WISDOM(5);

    private final int maxAnvilLevel;

    Enchants(int maxAnvilLevel) {
        this.maxAnvilLevel = maxAnvilLevel;
    }

    public int getMaxAnvilLevel() {
        return maxAnvilLevel;
    }

    public boolean isUnderMax(int level) {
        return level < maxAnvilLevel;
    }

    private static final Map<String, Enchants> LOOKUP = new HashMap<>();

    static {
        for (Enchants enchant : values()) {
            LOOKUP.put(enchant.name().toLowerCase(), enchant);
        }
    }

    public static List<String> getAllEnchantNames() {
        return Arrays.stream(values()).map(Enum::name).toList();
    }

    public static Enchants fromString(String name) {
        return LOOKUP.get(name.toLowerCase());
    }

    public static boolean underMax(String name, int level) {
        Enchants type = fromString(name);
        return type != null && type.isUnderMax(level);
    }
}
