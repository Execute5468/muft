package net.adequoir.muft.registry;

public final class RegistryLoader implements
        MuftComponentTypes,
        MuftBlocks,
        MuftItems,
        MuftRecipeSerializers,
        MuftSHandlers,
        MuftSoundEvents
{
    static {
        MuftComponentTypes.init();
        MuftBlocks.init();
        MuftItems.init();
        MuftRecipeSerializers.init();
        MuftSHandlers.init();
        MuftSoundEvents.init();
    }

    public static void init() {}
    private RegistryLoader() {}
}