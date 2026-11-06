package net.adequoir.muft.registry;

public final class RegistryLoader implements
        MuftComponentTypes,
        MuftBlocks,
        MuftItems,
        MuftRecipeSerializers,
        MuftSHandlers,
        MuftSoundEvents,
        MuftStats
{
    static {
        MuftComponentTypes.init();
        MuftItems.init();
        MuftBlocks.init();
        MuftRecipeSerializers.init();
        MuftSHandlers.init();
        MuftSoundEvents.init();
        MuftStats.init();
    }

    public static void init() {}
    private RegistryLoader() {}
}