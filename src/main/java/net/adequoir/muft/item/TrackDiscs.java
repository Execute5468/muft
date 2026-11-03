package net.adequoir.muft.item;

import net.adequoir.muft.registry.MuftItems;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

public final class TrackDiscs {

    public static Item get(@Nullable DyeColor color) {
        return switch (color) {
            case WHITE -> MuftItems.WHITE_TRACK_DISC.get();
            case LIGHT_GRAY -> MuftItems.LIGHT_GRAY_TRACK_DISC.get();
            case GRAY -> MuftItems.GRAY_TRACK_DISC.get();
            case BLACK -> MuftItems.BLACK_TRACK_DISC.get();
            case BROWN -> MuftItems.BROWN_TRACK_DISC.get();
            case RED -> MuftItems.RED_TRACK_DISC.get();
            case ORANGE -> MuftItems.ORANGE_TRACK_DISC.get();
            case YELLOW -> MuftItems.YELLOW_TRACK_DISC.get();
            case LIME -> MuftItems.LIME_TRACK_DISC.get();
            case GREEN -> MuftItems.GREEN_TRACK_DISC.get();
            case CYAN -> MuftItems.CYAN_TRACK_DISC.get();
            case LIGHT_BLUE ->  MuftItems.LIGHT_BLUE_TRACK_DISC.get();
            case BLUE -> MuftItems.BLUE_TRACK_DISC.get();
            case PURPLE -> MuftItems.PURPLE_TRACK_DISC.get();
            case MAGENTA -> MuftItems.MAGENTA_TRACK_DISC.get();
            case PINK -> MuftItems.PINK_TRACK_DISC.get();
            case null -> MuftItems.TRACK_DISC.get();
        };
    }

    private TrackDiscs() {}
}
