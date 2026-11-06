package net.adequoir.muft.api.registry;

import net.adequoir.muft.api.Bootstrapper;
import net.minecraft.core.Registry;

public final class Registrars {
    private static final RegistrarFactory FACTORY = Bootstrapper.instance().registrarFactory();

    public static <T> Registrar<T> create(String namespace, Registry<T> registry) { return FACTORY.create(namespace, registry); }
}
