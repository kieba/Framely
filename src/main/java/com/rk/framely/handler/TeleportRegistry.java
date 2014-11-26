package com.rk.framely.handler;

import com.rk.framely.tileentity.TileEntityTeleporter;
import com.rk.framely.util.Pair;
import com.rk.framely.util.Pos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class TeleportRegistry {

    private static HashMap<UUID, LinkEntry> playerRegistry = new HashMap<UUID, LinkEntry>();
    private static HashMap<UUID, LinkEntry> frameRegistry = new HashMap<UUID, LinkEntry>();

    public static boolean register(TileEntityTeleporter tile) {
        LinkEntry entry = getEntry(getRegistry(tile), tile);
        boolean registered = false;
        if(entry.pos[0].equals(Pos.NULL)) {
            entry.pos[0] = tile.getPosition();
            entry.dimension[0] = tile.getWorldObj();
            registered = true;
        } else if(entry.pos[1].equals(Pos.NULL)) {
            entry.pos[1] = tile.getPosition();
            entry.dimension[1] = tile.getWorldObj();
            registered = true;
        }
        return registered;
    }

    public static void unregister(TileEntityTeleporter tile) {
        HashMap<UUID, LinkEntry> registry = getRegistry(tile);
        LinkEntry entry = getEntry(registry, tile);
        if(entry.pos[0].equals(tile.getPosition())) {
            entry.pos[0] = Pos.NULL;
            entry.dimension[0] = null;
        } else if(entry.pos[1].equals(tile.getPosition())) {
            entry.pos[1] = Pos.NULL;
            entry.dimension[1] = null;
        }
        if(entry.pos[0].equals(Pos.NULL) && entry.pos[1].equals(Pos.NULL)) {
            registry.remove(tile.getUuid());
        }
    }

    public static Pair<Pos, World> getDestination(TileEntityTeleporter tile) {
        LinkEntry entry = getEntry(getRegistry(tile), tile);
        if(entry.pos[0].equals(tile.getPosition())) {
            return new Pair<Pos, World>(entry.pos[1], entry.dimension[1]);
        } else {
            return new Pair<Pos, World>(entry.pos[0], entry.dimension[0]);
        }
    }

    private static LinkEntry getEntry(HashMap<UUID, LinkEntry> registry, TileEntityTeleporter tile) {
        LinkEntry entry = registry.get(tile.getUuid());
        if(entry == null) {
            entry = new LinkEntry();
            registry.put(tile.getUuid(), entry);
        }
        return entry;
    }

    private static HashMap<UUID, LinkEntry> getRegistry(TileEntityTeleporter tile) {
        return (tile.getType() == TileEntityTeleporter.TeleporterType.Frame ? frameRegistry : playerRegistry);
    }

    public static void clear() {
        frameRegistry.clear();
        playerRegistry.clear();
    }

    private static class LinkEntry {

        private Pos[] pos = new Pos[]{
                Pos.NULL,
                Pos.NULL
        };

        private World[] dimension = new World[]{null, null};
    }
}
