package net.adequoir.muft.api;

import net.adequoir.muft.NeoForgeLoaderUtil;
import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.adequoir.muft.api.registry.RegistrarFactory;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.item.NeoForgeCreativeMenuEntries;
import net.adequoir.muft.network.NeoForgePayloadRegistrar;
import net.adequoir.muft.network.NeoForgeServerNetworkHelper;
import net.adequoir.muft.registry.NeoForgeRegistrar;
import net.adequoir.muft.resource.NeoForgePackHelper;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public class BootstrapperImpl extends Bootstrapper {
    public @Override LoaderUtil loaderUtil() { return new NeoForgeLoaderUtil(); }
    public @Override RegistrarFactory registrarFactory() { return NeoForgeRegistrar.FACTORY; }
    public @Override PayloadRegistrar payloadRegistrar() { return new NeoForgePayloadRegistrar(); }
    public @Override ServerNetworkHelper serverNetworkHelper() { return new NeoForgeServerNetworkHelper(); }
    public @Override PackHelper packHelper() { return new NeoForgePackHelper(); }
    public @Override CreativeMenuEntries creativeMenuEntries() { return new NeoForgeCreativeMenuEntries(); }
}