package dev.huskuraft.effortless.renderer;

import dev.huskuraft.effortless.math.Quaternionf;
import dev.huskuraft.effortless.math.Vector3d;

public interface Camera {

    Vector3d position();

    Quaternionf rotation();

    float eyeHeight();

}
