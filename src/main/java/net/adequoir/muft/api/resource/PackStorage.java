package net.adequoir.muft.api.resource;

import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PackStorage {
    private final ArrayList<Entry> packEntries = new ArrayList<>();
    private final List<Entry> entriesView = Collections.unmodifiableList(packEntries);
    private final String namespace;

    PackStorage(String namespace) { this.namespace = namespace; }

    public List<Entry> entries() { return entriesView; }
    public String namespace() { return namespace; }

    public void registerResource(String path) { registerResource(path, PackState.DISABLED); }
    public void registerResource(String path, PackState state) { register(ResourceType.CLIENT_RESOURCES, path, state); }

    public void registerData(String path) { registerData(path, PackState.DISABLED); }
    public void registerData(String path, PackState state) { register(ResourceType.SERVER_DATA, path, state); }

    private void register(ResourceType type, String path, PackState state) {
        packEntries.add(new Entry(type, Identifier.of(namespace, path), state));
    }

    public record Entry(ResourceType type, Identifier id, PackState state, Text translationKey) {
        Entry(ResourceType type, Identifier id, PackState state) { this(type, id, state, toTranslationKey(id)); }

        private static Text toTranslationKey(Identifier id) {
            return Text.translatable("resourcePack." + id.toTranslationKey() + ".name");
        }
    }
}