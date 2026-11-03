package net.adequoir.muft.client.config.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntOption {
    int min();

    int max();

    String unit();
}