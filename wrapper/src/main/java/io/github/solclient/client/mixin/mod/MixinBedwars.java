package io.github.solclient.client.mixin.mod;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsGame;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMod;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsPlayer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class MixinBedwars {

    @Mixin(PlayerListHud.class)
    public static class MixinBedwarsPlayerListHud {

        @Shadow private Text header;

        @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
        public void getPlayerName(PlayerListEntry playerEntry, CallbackInfoReturnable<String> cir) {
            if (!BedwarsMod.getInstance().isEnabled()) {
                return;
            }
            BedwarsGame game = BedwarsMod.getInstance().getGame().orElse(null);
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
            if (!BedwarsMod.getInstance().inGame()) {
                return original;
            }
            List<PlayerListEntry> players = BedwarsMod.getInstance().getGame().get().getTabPlayerList(original);
            if (players == null) {
                return original;
            }
            return players;
        }

        @Inject(method = "setHeader", at = @At("HEAD"), cancellable = true)
        public void changeHeader(Text header, CallbackInfo ci) {
            if (!BedwarsMod.getInstance().inGame()) {
                return;
            }
            this.header = BedwarsMod.getInstance().getGame().get().getTopBarText();
        }

    }

}
