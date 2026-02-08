package com.hirowassan;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class EasyBedrockBreaker implements ModInitializer {
	public static final String MOD_ID = "easybedrockbreaker";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static ArrayList<Packet<?>> delayedPackets = new ArrayList<>();

	private static KeyBinding activateKey;

	public static final Class[] blockedPackets = {
			PlayerActionC2SPacket.class,
			PlayerInputC2SPacket.class,
			PlayerInteractBlockC2SPacket.class,
			PlayerInteractItemC2SPacket.class,
			UpdateSelectedSlotC2SPacket.class
	};

	@Override
	public void onInitialize() {
		activateKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.hirowassan.delayBlockPackets", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "category.hirowassan.easybedrockbreaker"));
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (!activateKey.isPressed()) releasePackets();
		});

		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
			if (isDelayingPackets()) {
				MinecraftClient client = MinecraftClient.getInstance();
				int screenWidth = client.getWindow().getScaledWidth();
				int screenHeight = client.getWindow().getScaledHeight();

				String text = "Delaying Packets";
				int textWidth = client.textRenderer.getWidth(text);

				// ホットバーの上部（画面中央下寄り）に表示
				int x = (screenWidth - textWidth) / 2;
				int y = screenHeight - 70; // ホットバーの上

				// TextRendererを使ってテキストを描画
				client.textRenderer.drawWithShadow(matrixStack, text, x, y, 0xFFFF55); // 黄色
			}
		});

		LOGGER.info("Hello Fabric world!");
	}

	public static boolean isDelayingPackets() {
		return activateKey.isPressed();
	}

	public static void delayPacket(Packet<?> p) {
		delayedPackets.add(p);
	}

	public static void clearPackets() {
		delayedPackets.clear();
	}

	private void releasePackets() {
		for (Packet<?> packet : delayedPackets) {
			MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
		}
		delayedPackets.clear();
	}
}