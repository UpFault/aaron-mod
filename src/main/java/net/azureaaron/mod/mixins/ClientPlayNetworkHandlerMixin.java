package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.events.ParticleSpawnEvent;
import net.azureaaron.mod.events.PlaySoundEvent;
import net.azureaaron.mod.events.TeamUpdateEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	
	@WrapWithCondition(method = "onPlayerRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;stop()V", ordinal = 0))
	private boolean aaronMod$onWorldChange(MusicTracker musicTracker) {
		return !AaronModConfigManager.get().stopSoundsOnWorldChange;
	}
	
	@Inject(method = "onPlaySound", at = @At("HEAD"))
	private void aaronMod$onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
		PlaySoundEvent.EVENT.invoker().onPlaySound(packet);
	}
	
	@Inject(method = "onTeam", at = @At("HEAD"))
	private void aaronMod$onTeamUpdate(TeamS2CPacket packet, CallbackInfo ci) {
		TeamUpdateEvent.EVENT.invoker().onTeamUpdate(packet);
	}
	
	@Inject(method = "onParticle", at = @At("HEAD"))
	private void aaronMod$onParticleSpawn(ParticleS2CPacket packet, CallbackInfo ci) {
		ParticleSpawnEvent.EVENT.invoker().onParticleSpawn(packet);
	}
}
