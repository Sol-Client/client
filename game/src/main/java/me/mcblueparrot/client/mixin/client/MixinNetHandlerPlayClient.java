package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.ServerChangeEvent;
import me.mcblueparrot.client.events.EntityDamageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Shadow
    private Minecraft gameController;

    @Shadow
    private WorldClient clientWorldController;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(S3FPacketCustomPayload payload, CallbackInfo callback) {
        Client.INSTANCE.bus.post(payload); // Post as normal event object
    }

    @Inject(method = "handleJoinGame", at = @At("RETURN"))
    public void handleJoinGame(S01PacketJoinGame packetIn, CallbackInfo callback) {
        Client.INSTANCE.onServerChange(gameController.getCurrentServerData());
    }


    @Inject(method = "handleEntityStatus", at = @At("RETURN"))
    public void handleEntityStatus(S19PacketEntityStatus packetIn, CallbackInfo callback) {
        if(packetIn.getOpCode() == 2) {
            Client.INSTANCE.bus.post(new EntityDamageEvent(packetIn.getEntity(clientWorldController)));
        }
    }

}
