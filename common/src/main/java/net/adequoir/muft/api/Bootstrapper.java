package net.adequoir.muft.api;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.adequoir.muft.api.registry.RegistrarFactory;
import net.adequoir.muft.api.resource.PackHelper;

public abstract class Bootstrapper {
    private static final Bootstrapper INSTANCE;

    public static Bootstrapper instance() { return INSTANCE; }

    static {
        var implName = Bootstrapper.class.getName() + "Impl";
        try {
            var klass = Class.forName(implName);
            if (!Bootstrapper.class.isAssignableFrom(klass)) {
                throw new IllegalStateException("Class " + klass + " does not extend " + Bootstrapper.class);
            }

            var constructor = klass.getDeclaredConstructor();
            try {
                INSTANCE = (Bootstrapper) constructor.newInstance();
                Muft.LOGGER.info("Successfully instantiated Bootstrapper {}", implName);
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate BootstrapperImpl", e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find BootstrapperImpl class", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find default constructor for BootstrapperImpl class", e);
        }
    }

    public abstract LoaderUtil loaderUtil();
    public abstract RegistrarFactory registrarFactory();
    public abstract PayloadRegistrar payloadRegistrar();
    public abstract ServerNetworkHelper serverNetworkHelper();
    public abstract PackHelper packHelper();
    public abstract CreativeMenuEntries creativeMenuEntries();

    protected Bootstrapper() {}
}