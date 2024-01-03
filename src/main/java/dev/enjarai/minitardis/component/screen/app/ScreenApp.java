package dev.enjarai.minitardis.component.screen.app;

import com.mojang.serialization.Codec;
import dev.enjarai.minitardis.block.console.ConsoleScreenBlockEntity;
import dev.enjarai.minitardis.component.TardisControl;
import dev.enjarai.minitardis.data.RandomAppLootFunction;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import net.minecraft.loot.context.LootContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public interface ScreenApp {
    Codec<ScreenApp> CODEC = ScreenAppType.REGISTRY.getCodec().dispatch(ScreenApp::getType, ScreenAppType::codec);

    AppView getView(TardisControl controls);

    /**
     * Draw the icon of the application to the provided canvas, the canvas provided is limited to the available area.
     * THIS IS CALLED OFF-THREAD, DON'T INTERACT WITH THE WORLD IF AT ALL POSSIBLE.
     */
    void drawIcon(TardisControl controls, ConsoleScreenBlockEntity blockEntity, DrawableCanvas canvas);

    default boolean canBeUninstalled() {
        return true;
    }

    default Text getName() {
        var id = getId();
        return Text.translatable("mini_tardis.app." + id.getNamespace() + "." + id.getPath());
    }

    /**
     * Append extra lines to the tooltip entry of this app when loaded onto a floppy.
     * This can be used to display extra information on the persistent state of the app.
     */
    default void appendTooltip(List<Text> tooltip) {
    }

    /**
     * Use this function to initialize data when this app is spawned in a loot floppy.
     */
    default void applyLootModifications(LootContext context, RandomAppLootFunction lootFunction) {
    }

    ScreenAppType<?> getType();

    default Identifier getId() {
        return ScreenAppType.REGISTRY.getKey(getType())
                .orElseThrow(() -> new IllegalStateException("Illegal unregistered screen app detected, a SWAT unit is being dispatched to your location."))
                .getValue();
    }
}
