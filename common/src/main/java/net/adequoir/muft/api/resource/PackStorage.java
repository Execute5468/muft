package net.adequoir.muft.api.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public final class PackStorage {
    private final ArrayList<Entry> packEntries = new ArrayList<>();
    private final List<Entry> entriesView = Collections.unmodifiableList(packEntries);
    private final String namespace;

    PackStorage(String namespace) { this.namespace = namespace; }

    public List<Entry> entries() { return entriesView; }
    public String namespace() { return namespace; }

    public void registerResource(String path) { registerResource(path, PackState.DISABLED); }
    public void registerResource(String path, PackState state) { register(PackType.CLIENT_RESOURCES, path, state); }

    public void registerData(String path) { registerData(path, PackState.DISABLED); }
    public void registerData(String path, PackState state) { register(PackType.SERVER_DATA, path, state); }

    private void register(PackType type, String path, PackState state) {
        packEntries.add(new Entry(type, ResourceLocation.fromNamespaceAndPath(namespace, path), state));
    }

    public record Entry(PackType type, ResourceLocation id, PackState state, Component translationKey) {
        Entry(PackType type, ResourceLocation id, PackState state) { this(type, id, state, toTranslationKey(id)); }

        private static Component toTranslationKey(ResourceLocation id) {
            return Component.translatable("resourcePack." + id.toLanguageKey() + ".name");
        }
    }
}