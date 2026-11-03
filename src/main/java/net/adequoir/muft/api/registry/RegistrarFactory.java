package net.adequoir.muft.api.registry;

import net.minecraft.registry.Registry;

@FunctionalInterface
public interface RegistrarFactory {
    <T> Registrar<T> create(String namespace, Registry<T> registry);
}
