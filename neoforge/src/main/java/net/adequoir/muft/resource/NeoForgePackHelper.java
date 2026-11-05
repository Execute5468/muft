package net.adequoir.muft.resource;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.api.resource.PackState;
import net.adequoir.muft.api.resource.PackStorage;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.ArrayList;

@NotNullByDefault
@EventBusSubscriber(modid = Muft.MOD_ID)
public final class NeoForgePackHelper extends PackHelper {
    private static final ArrayList<PackStorage.Entry> packEntries = new ArrayList<>();

    protected @Override void register(PackStorage storage) {
        packEntries.addAll(storage.entries());
    }

    @SubscribeEvent
    public static void initialize(AddPackFindersEvent event) {
        for (var entry: packEntries) {
            if (entry.type() != event.getPackType()) continue;

            try {
                Muft.LOGGER.info("Registering pack {}", entry.id());
                event.addPackFinders(
                    entry.id().withPrefix("resourcepacks/"),
                    entry.type(),
                    entry.translationKey(),
                    entry.state() == PackState.DISABLED ? PackSource.FEATURE : PackSource.DEFAULT,
                    entry.state() == PackState.FORCED,
                    Pack.Position.TOP
                );
            } catch (Exception e) {
                Muft.LOGGER.info("Failed to register pack {}", entry.id());
                throw new RuntimeException(e);
            }
        }
    }
}
