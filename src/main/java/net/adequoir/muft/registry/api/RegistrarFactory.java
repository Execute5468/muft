package net.adequoir.muft.registry.api;

import net.minecraft.registry.Registry;

@FunctionalInterface
public interface RegistrarFactory {
    <T> Registrar<T> create(String namespace, Registry<T> registry);
}
