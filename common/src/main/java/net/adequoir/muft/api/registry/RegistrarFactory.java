package net.adequoir.muft.api.registry;

import net.minecraft.core.Registry;

@FunctionalInterface
public interface RegistrarFactory {
    <T> Registrar<T> create(String namespace, Registry<T> registry);
}
