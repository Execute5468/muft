package net.adequoir.muft.api.registry;

import net.minecraft.core.Registry;
import org.jetbrains.annotations.Nullable;

public final class Registrars {
    private static @Nullable RegistrarFactory registrarFactory;

    public static <T> Registrar<T> create(String namespace, Registry<T> registry) {
        if (registrarFactory != null) return registrarFactory.create(namespace, registry);
        throw new IllegalStateException("RegistrarFactory has not been bootstrapped!");
    }

    public static void bootstrap(RegistrarFactory factory) {
        if (registrarFactory == null) { registrarFactory = factory; return; }
        throw new IllegalStateException("RegistrarFactory already bootstrapped!");
    }
}
