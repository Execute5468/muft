package net.adequoir.muft.item;

import net.adequoir.muft.registry.MuftItems;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

public final class TrackDiscs {

    public static Item get(@Nullable DyeColor color) {
        return switch (color) {
            case WHITE -> MuftItems.WHITE_TRACK_DISC;
            case LIGHT_GRAY -> MuftItems.LIGHT_GRAY_TRACK_DISC;
            case GRAY -> MuftItems.GRAY_TRACK_DISC;
            case BLACK -> MuftItems.BLACK_TRACK_DISC;
            case BROWN -> MuftItems.BROWN_TRACK_DISC;
            case RED -> MuftItems.RED_TRACK_DISC;
            case ORANGE -> MuftItems.ORANGE_TRACK_DISC;
            case YELLOW -> MuftItems.YELLOW_TRACK_DISC;
            case LIME -> MuftItems.LIME_TRACK_DISC;
            case GREEN -> MuftItems.GREEN_TRACK_DISC;
            case CYAN -> MuftItems.CYAN_TRACK_DISC;
            case LIGHT_BLUE ->  MuftItems.LIGHT_BLUE_TRACK_DISC;
            case BLUE -> MuftItems.BLUE_TRACK_DISC;
            case PURPLE -> MuftItems.PURPLE_TRACK_DISC;
            case MAGENTA -> MuftItems.MAGENTA_TRACK_DISC;
            case PINK -> MuftItems.PINK_TRACK_DISC;
            case null -> MuftItems.TRACK_DISC;
        };
    }

    private TrackDiscs() {}
}
