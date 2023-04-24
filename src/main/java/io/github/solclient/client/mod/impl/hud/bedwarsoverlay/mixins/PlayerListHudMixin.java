/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.mixins;

import java.util.List;

import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.*;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Shadow private Text header;

    @Shadow private Text footer;

    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "renderScoreboardObjective",
            at = @At(
                    value="INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal=1
            ),
            cancellable = true
    )
    public void renderCustomScoreboardObjective(
            ScoreboardObjective objective, int y, String player, int startX, int endX, PlayerListEntry playerEntry, CallbackInfo ci
    ) {
        if (!BedwarsMod.instance.isEnabled()) {
            return;
        }
        BedwarsGame game = BedwarsMod.instance.getGame().orElse(null);
        if (game == null) {
            return;
        }
        BedwarsPlayer bedwarsPlayer = game.getPlayer(playerEntry.getProfile().getName()).orElse(null);
        if (bedwarsPlayer == null) {
            return;
        }
        ci.cancel();
        String render;
        int color = 16777215;
        if (!bedwarsPlayer.isAlive()) {
            if (bedwarsPlayer.isDisconnected()) {
                return;
            }
            int tickTillLive = Math.max(0, bedwarsPlayer.getTickAlive() - this.client.inGameHud.getTicks());
            float secondsTillLive = tickTillLive / 20f;
            render = String.format("%.2f", secondsTillLive) + "s";
        } else {
            int health = objective.getScoreboard().getPlayerScore(player, objective).getScore();
            color = new Colour(50,205,50).lerp(new Colour(215, 0, 64), 1 - (health / 20f)).getValue();
            render = String.valueOf(health);
        }
        // Health
        this.client.textRenderer.drawWithShadow(
                render,
                (float)(endX - this.client.textRenderer.getStringWidth(render)),
                (float) y,
                color
        );

    }

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(PlayerListEntry playerEntry, CallbackInfoReturnable<String> cir) {
        if (!BedwarsMod.instance.isEnabled()) {
            return;
        }
        BedwarsGame game = BedwarsMod.instance.getGame().orElse(null);
        if (game == null || !game.isStarted()) {
            return;
        }
        BedwarsPlayer player = game.getPlayer(playerEntry.getProfile().getName()).orElse(null);
        if (player == null) {
            return;
        }
        cir.setReturnValue(player.getTabListDisplay());
    }

    @ModifyVariable(method = "render", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"))
    public List<PlayerListEntry> overrideSortedPlayers(List<PlayerListEntry> original) {
        if (!BedwarsMod.instance.inGame()) {
            return original;
        }
        List<PlayerListEntry> players = BedwarsMod.instance.getGame().get().getTabPlayerList(original);
        if (players == null) {
            return original;
        }
        return players;
    }

    @Inject(method = "setHeader", at = @At("HEAD"), cancellable = true)
    public void changeHeader(Text header, CallbackInfo ci) {
        if (!BedwarsMod.instance.inGame()) {
            return;
        }
        this.header = BedwarsMod.instance.getGame().get().getTopBarText();
        ci.cancel();
    }

    @Inject(method = "setFooter", at = @At("HEAD"), cancellable = true)
    public void changeFooter(Text header, CallbackInfo ci) {
        if (!BedwarsMod.instance.inGame()) {
            return;
        }
        this.footer = BedwarsMod.instance.getGame().get().getBottomBarText();
        ci.cancel();
    }

}
