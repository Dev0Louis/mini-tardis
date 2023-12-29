package dev.enjarai.minitardis.item;

import com.google.common.collect.ImmutableList;
import dev.enjarai.minitardis.MiniTardis;
import dev.enjarai.minitardis.component.screen.app.ScreenApp;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FloppyItem extends Item implements PolymerItem {
    public static final PolymerModelData MODEL = PolymerResourcePackUtils.requestModel(Items.IRON_INGOT, MiniTardis.id("item/floppy"));

    public FloppyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        for (var app : getApps(stack)) {
            tooltip.addAll(app.getName().getWithStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            app.appendTooltip(tooltip);
        }
    }

    public static List<ScreenApp> getApps(ItemStack stack) {
        if (stack.isEmpty()) return List.of();

        var nbt = stack.getOrCreateNbt();
        if (nbt.contains("apps", NbtElement.LIST_TYPE)) {
            var list = ImmutableList.<ScreenApp>builder();
            for (var appElement : nbt.getList("apps", NbtElement.COMPOUND_TYPE)) {
                ScreenApp.CODEC.decode(NbtOps.INSTANCE, appElement).result().ifPresent(app -> list.add(app.getFirst()));
            }
            return list.build();
        } else {
            return List.of();
        }
    }

    public static void addApp(ItemStack stack, ScreenApp app) {
        var nbt = stack.getOrCreateNbt();

        NbtList appsList;
        if (!nbt.contains("apps", NbtElement.COMPOUND_TYPE)) {
            appsList = nbt.getList("apps", NbtElement.COMPOUND_TYPE);
        } else {
            appsList = new NbtList();
        }

        ScreenApp.CODEC.encodeStart(NbtOps.INSTANCE, app).result().ifPresent(appsList::add);

        nbt.put("apps", appsList);
    }

    public static boolean removeApp(ItemStack stack, int index) {
        var nbt = stack.getOrCreateNbt();

        NbtList appsList;
        if (!nbt.contains("apps", NbtElement.COMPOUND_TYPE)) {
            appsList = nbt.getList("apps", NbtElement.COMPOUND_TYPE);
        } else {
            appsList = new NbtList();
        }

        if (appsList.size() > index) {
            appsList.remove(index);
            nbt.put("apps", appsList);
            return true;
        }

        return false;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return MODEL.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return MODEL.value();
    }
}
