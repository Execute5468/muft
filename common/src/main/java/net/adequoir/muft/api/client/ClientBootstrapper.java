package net.adequoir.muft.api.client;

import net.adequoir.muft.Muft;
import net.adequoir.muft.api.client.network.ClientNetworkHelper;

public abstract class ClientBootstrapper {
    private static final ClientBootstrapper INSTANCE;

    static {
        var rootName = ClientBootstrapper.class.getName();
        var implName = rootName + "Impl";
        try {
            var klass = Class.forName(implName);
            if (!ClientBootstrapper.class.isAssignableFrom(klass)) {
                throw new IllegalStateException("Class " + klass + " does not extend " + rootName);
            }

            var constructor = klass.getDeclaredConstructor();
            try {
                INSTANCE = (ClientBootstrapper) constructor.newInstance();
                Muft.LOGGER.info("Successfully instantiated ClientBootstrapper {}", implName);
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate BootstrapperImpl", e);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find BootstrapperImpl class", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find default constructor for BootstrapperImpl class", e);
        }
    }

    public static ClientBootstrapper instance() { return INSTANCE; }

    public abstract ClientNetworkHelper clientNetworkHelper();
}
