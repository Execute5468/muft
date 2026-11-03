package net.adequoir.muft.client.config.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EnumOption {
    Type type();

    enum Type { SLIDER, CYCLIC }
}
