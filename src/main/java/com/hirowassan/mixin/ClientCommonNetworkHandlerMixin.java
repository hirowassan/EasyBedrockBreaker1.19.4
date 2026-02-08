package com.hirowassan.mixin;

import com.hirowassan.EasyBedrockBreaker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void delayPackets(Packet<?> packet, CallbackInfo ci) {
        if (EasyBedrockBreaker.isDelayingPackets() && Arrays.stream(EasyBedrockBreaker.blockedPackets).anyMatch(c -> c.isInstance(packet))) {
            EasyBedrockBreaker.delayPacket(packet);
            ci.cancel();
        }
    }

}
