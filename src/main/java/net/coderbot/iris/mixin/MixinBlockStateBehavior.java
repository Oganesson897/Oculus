package net.coderbot.iris.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.coderbot.iris.block_rendering.BlockRenderingSettings;

/**
 * This Mixin implements support for the ambientOcclusionLevel value. This injection point was chosen because it's
 * called by all rendering mods (so we don't need a ton of injection points), but at the same time, it's very rarely
 * overridden or injected to (because it just dispatches to a method on BlockBehavior).
 *
 * <p>Compatibility considerations:</p>
 *
 * <ul>
 *     <li>Vanilla: Calls the relevant method in its block / fluid renderers</li>
 *     <li>Sodium / FRAPI / FREX / etc: Calls this method in relevant renderers</li>
 *     <li>Better Foliage: Unusually, Better Foliage injects into this method, but it merely redirects the call to
 *         getShadeBrightness. By using a priority of 990, we apply before Better Foliage, allowing its redirect to
 *         succeed.</li>
 *     <li>Content mods: Most content mods override the method in BlockBehavior, which doesn't cause issues with
 *         this method. </li>
 * </ul>
 */
@Mixin(value = BlockStateContainer.StateImplementation.class, priority = 990)
public abstract class MixinBlockStateBehavior {
	@Shadow
	public abstract Block getBlock();

	/**
	 * @author IMS
	 * @reason ambientOcclusionLevel support. Semantically, we're completely changing the meaning of the method.
	 */
	@Overwrite
	@SuppressWarnings("deprecation")
	public float getAmbientOcclusionLightValue() {
		float originalValue = this.getBlock().getAmbientOcclusionLightValue(((IBlockState) this));
		float aoLightValue = BlockRenderingSettings.INSTANCE.getAmbientOcclusionLevel();
		return 1.0F - aoLightValue * (1.0F - originalValue);
	}
}
