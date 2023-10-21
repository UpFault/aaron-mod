package net.azureaaron.mod.mixins;

import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.events.MouseInputEvent;
import net.azureaaron.mod.features.MouseGuiPositioner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

@Mixin(Mouse.class)
public class MouseMixin implements MouseGuiPositioner {
	
	@Shadow @Final private MinecraftClient client;
	@Shadow private double x;
	@Shadow private double y;
    private double aaronMod$guiX;
	private double aaronMod$guiY;

	@Inject(method = "onMouseButton", at = @At("HEAD"))
	private void aaronMod$onMouseButton(@Arg(ordinal = 0) int button, @Arg(ordinal = 1) int action, @Arg(ordinal = 2) int mods) {
        MouseInputEvent.EVENT.invoker().onMouseInput(button, action, mods);
    }
	
	@Inject(method = "lockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;x:D", ordinal = 0, shift = At.Shift.BEFORE))
	private void aaronMod$lockXPos() {
		this.aaronMod$guiX = this.x;
	}
	
	@Inject(method = "lockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;y:D", ordinal = 0, shift = At.Shift.BEFORE))
	private void aaronMod$lockYPos() {
		this.aaronMod$guiY = this.y;		
	}
		
	@Redirect(method = "unlockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;x:D", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void aaronMod$unlockXPos(Mouse mouse, double centreX) {
		if(Config.resetCursorPosition && client.currentScreen instanceof GenericContainerScreen) {
			this.x = this.aaronMod$guiX;
		} else {
			this.x = centreX;
		}
	}
	
	@Redirect(method = "unlockCursor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;y:D", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void aaronMod$unlockYPos(Mouse mouse, double centreY) {
		if(Config.resetCursorPosition && client.currentScreen instanceof GenericContainerScreen) {
			this.y = this.aaronMod$guiY;
		} else {
			this.y = centreY;
		}
	}
	
	@Inject(method = "unlockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V", ordinal = 0, shift = At.Shift.AFTER))
	private void aaronMod$correctCursorPosition() {
		if(Config.resetCursorPosition && client.currentScreen instanceof GenericContainerScreen) GLFW.glfwSetCursorPos(this.client.getWindow().getHandle(), this.aaronMod$guiX, this.aaronMod$guiY);
	}

	@Override
	public void reset() {
		this.x = this.client.getWindow().getWidth() / 2;
		this.y = this.client.getWindow().getHeight() /2;
	}
}
