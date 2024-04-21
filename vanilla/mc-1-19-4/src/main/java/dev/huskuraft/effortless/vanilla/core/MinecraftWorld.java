package dev.huskuraft.effortless.vanilla.core;

import java.util.UUID;

import dev.huskuraft.effortless.api.core.BlockPosition;
import dev.huskuraft.effortless.api.core.BlockState;
import dev.huskuraft.effortless.api.core.DimensionType;
import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.core.ResourceKey;
import dev.huskuraft.effortless.api.core.World;
import dev.huskuraft.effortless.api.core.WorldBorder;
import net.minecraft.world.level.Level;

public class MinecraftWorld implements World {

    private final Level reference;

    MinecraftWorld(Level reference) {
        this.reference = reference;
    }

    public static World ofNullable(Level reference) {
        return reference == null ? null : new MinecraftWorld(reference);
    }

    @Override
    public Level referenceValue() {
        return reference;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return MinecraftPlayer.ofNullable(reference.getPlayerByUUID(uuid));
    }

    @Override
    public BlockState getBlockState(BlockPosition blockPosition) {
        return MinecraftBlockState.ofNullable(reference.getBlockState(MinecraftConvertor.toPlatformBlockPosition(blockPosition)));
    }

    @Override
    public boolean setBlockState(BlockPosition blockPosition, BlockState blockState) {
        return reference.setBlockAndUpdate(MinecraftConvertor.toPlatformBlockPosition(blockPosition), blockState.reference());
    }

    @Override
    public boolean isClient() {
        return reference.isClientSide();
    }

    @Override
    public ResourceKey<World> getDimensionId() {
        return new MinecraftResourceKey<>(reference.dimension());
    }

    @Override
    public DimensionType getDimensionType() {
        return new MinecraftDimensionType(reference.dimensionType());
    }

    @Override
    public WorldBorder getWorldBorder() {
        return new MinecraftWorldBorder(reference.getWorldBorder());
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof MinecraftWorld world && reference.equals(world.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }

}
