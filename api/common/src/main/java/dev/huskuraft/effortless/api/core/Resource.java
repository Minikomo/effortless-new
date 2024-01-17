package dev.huskuraft.effortless.api.core;

import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.platform.PlatformReference;

public interface Resource extends PlatformReference {

    static Resource decompose(String value, String separator) {
        try {
            return of(value.split(separator)[0], value.split(separator)[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(e + "for value: " + value);
        }
    }

    static Resource decompose(String value) {
        return decompose(value, ":");
    }

    static Resource vanilla(String path) {
        return of("minecraft", path);
    }

    static Resource of(String namespace, String path) {
        return Entrance.getInstance().getPlatform().newResource(namespace, path);
    }

    String getNamespace();

    String getPath();

    default String getString() {
        return getNamespace() + ":" + getPath();
    }

}