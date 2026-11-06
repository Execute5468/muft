package net.adequoir.muft.api;

import net.adequoir.muft.FabricLoaderUtil;
import net.adequoir.muft.api.item.CreativeMenuEntries;
import net.adequoir.muft.api.network.PayloadRegistrar;
import net.adequoir.muft.api.network.ServerNetworkHelper;
import net.adequoir.muft.api.registry.RegistrarFactory;
import net.adequoir.muft.api.resource.PackHelper;
import net.adequoir.muft.item.FabricCreativeMenuEntries;
import net.adequoir.muft.network.FabricPayloadRegistrar;
import net.adequoir.muft.network.FabricServerNetworkHelper;
import net.adequoir.muft.registry.FabricRegistrar;
import net.adequoir.muft.resource.FabricPackHelper;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public class BootstrapperImpl extends Bootstrapper {
    public @Override LoaderUtil loaderUtil() { return new FabricLoaderUtil(); }
    public @Override RegistrarFactory registrarFactory() { return FabricRegistrar.FACTORY; }
    public @Override PayloadRegistrar payloadRegistrar() { return new FabricPayloadRegistrar(); }
    public @Override ServerNetworkHelper serverNetworkHelper() { return new FabricServerNetworkHelper(); }
    public @Override PackHelper packHelper() { return new FabricPackHelper(); }
    public @Override CreativeMenuEntries creativeMenuEntries() { return new FabricCreativeMenuEntries(); }
}