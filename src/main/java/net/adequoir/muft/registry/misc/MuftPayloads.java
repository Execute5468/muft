package net.adequoir.muft.registry.misc;

import net.adequoir.muft.network.DiscPressSelectPayload;
import net.adequoir.muft.network.PocketJukeboxItemSelectedPayload;
import net.adequoir.muft.network.PocketJukeboxPlayPayload;
import net.adequoir.muft.network.PocketJukeboxResetPayload;

import static net.adequoir.muft.network.api.PayloadRegistrar.*;


public final class MuftPayloads {
    static {
        registerC2S(PocketJukeboxItemSelectedPayload.ID, PocketJukeboxItemSelectedPayload::write, PocketJukeboxItemSelectedPayload::read);
        registerC2S(PocketJukeboxResetPayload.ID, PocketJukeboxResetPayload::write, PocketJukeboxResetPayload::read);
        registerS2C(PocketJukeboxPlayPayload.ID, PocketJukeboxPlayPayload::write, PocketJukeboxPlayPayload::read);
        registerC2S(DiscPressSelectPayload.ID, DiscPressSelectPayload::write, DiscPressSelectPayload::read);
    }

    public static void init() {}

    private MuftPayloads() {}
}
