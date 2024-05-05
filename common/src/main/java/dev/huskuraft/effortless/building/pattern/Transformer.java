package dev.huskuraft.effortless.building.pattern;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.huskuraft.effortless.api.math.BoundingBox3d;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.building.BuildStage;
import dev.huskuraft.effortless.building.operation.TransformableOperation;
import dev.huskuraft.effortless.building.operation.batch.BatchOperation;
import dev.huskuraft.effortless.building.pattern.array.ArrayTransformer;
import dev.huskuraft.effortless.building.pattern.mirror.MirrorTransformer;
import dev.huskuraft.effortless.building.pattern.raidal.RadialTransformer;
import dev.huskuraft.effortless.building.pattern.randomize.ItemRandomizer;
import dev.huskuraft.effortless.building.session.BatchBuildSession;

public abstract class Transformer {

    public static final int MAX_NAME_LENGTH = 255;

    public static final BoundingBox3d POSITION_BOUND = new BoundingBox3d(
            Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE,
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE
    );

    protected final UUID id;
    protected final Text name;

    public Transformer(UUID id, Text name) {
        this.id = id;
        this.name = name;
    }

    public static List<Transformer> getDefaultTransformers() {
        return Stream.of(
                List.of(ArrayTransformer.DEFAULT),
                List.of(MirrorTransformer.DEFAULT_X, MirrorTransformer.DEFAULT_Y, MirrorTransformer.DEFAULT_Z),
                List.of(RadialTransformer.DEFAULT),
                ItemRandomizer.getDefaultItemRandomizers()
        ).flatMap(List::stream).collect(Collectors.toList());
    }

    public abstract BatchOperation transform(TransformableOperation operation);

    public final UUID getId() {
        return id;
    }

    public final Text getName() {
        return name;
    }

    public abstract Transformers getType();

    public abstract Stream<Text> getSearchableTags();

    public abstract boolean isValid();

    public abstract boolean isIntermediate();

    public abstract Transformer withName(Text name);

    public abstract Transformer withId(UUID id);

    public Transformer withRandomId() {
        return withId(UUID.randomUUID());
    }

    public Transformer finalize(BatchBuildSession session, BuildStage stage) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transformer that)) return false;

        if (!Objects.equals(id, that.id)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }


}
