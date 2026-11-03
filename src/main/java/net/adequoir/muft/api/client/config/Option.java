package net.adequoir.muft.api.client.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Option {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Enum {
        OptionType type();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Int {
        int min();

        int max();

        String unit();
    }

    private Option() {}
}
