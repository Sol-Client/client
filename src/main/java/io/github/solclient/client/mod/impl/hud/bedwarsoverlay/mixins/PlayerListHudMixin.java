package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.mixins;

import java.util.List;

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
