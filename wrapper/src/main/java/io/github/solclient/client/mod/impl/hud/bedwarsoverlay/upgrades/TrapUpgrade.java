package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrapUpgrade extends TeamUpgrade {

    private final static Pattern[] REGEX = {
            Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (.+) Trap\\s*$"),
            Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (.+) Trap\\s*$"),
            Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (.+) Trap\\s*$"),
            Pattern.compile("Trap was set (off)!"),
    };

    private final List<TrapType> traps = new ArrayList<>(3);

    public TrapUpgrade() {
        super("trap", REGEX);
    }

    @Override
    protected void onMatch(TeamUpgrade upgrade, Matcher matcher) {
        if (matcher.group(1).equals("off")) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("Trap went off"));
            // Trap went off
            traps.remove(0);
            return;
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("Trap died"));
        traps.add(TrapType.getFuzzy(matcher.group(1)));
    }

    public boolean canPurchase() {
        return traps.size() < 3;
    }

    @Override
    public int getPrice(BedwarsMode mode) {
        switch (traps.size()) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
        };
        return 0;
    }


    public enum TrapType {
        ITS_A_TRAP,
        COUNTER_OFFENSIVE,
        ALARM,
        MINER_FATIGUE
        ;

        public static TrapType getFuzzy(String s) {
            s = s.toLowerCase(Locale.ROOT);
            if (s.contains("miner")) {
                return MINER_FATIGUE;
            }
            if (s.contains("alarm")) {
                return ALARM;
            }
            if (s.contains("counter")) {
                return COUNTER_OFFENSIVE;
            }
            return ITS_A_TRAP;
        }
    }
}
