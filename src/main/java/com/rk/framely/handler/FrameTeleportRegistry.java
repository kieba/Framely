package com.rk.framely.handler;

import com.rk.framely.tileentity.TileEntityTeleporter;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pair;
import com.rk.framely.util.Pos;
import net.minecraft.world.World;

import java.util.*;

public class FrameTeleportRegistry {

    private static HashMap<UUID, LinkEntry> entries = new HashMap<UUID, LinkEntry>();

    public static boolean register(TileEntityTeleporter tile) {
        LinkEntry entry = getEntry(tile.getUuid());
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
        LinkEntry entry = getEntry(tile.getUuid());
        if(entry.pos[0].equals(tile.getPosition())) {
            entry.pos[0] = Pos.NULL;
            entry.dimension[0] = null;
        } else if(entry.pos[1].equals(tile.getPosition())) {
            entry.pos[1] = Pos.NULL;
            entry.dimension[1] = null;
        }
        removeIfEmpty(entry, tile.getUuid());
    }

    public static Pair<Pos, World> getDestination(TileEntityTeleporter tile) {
        LinkEntry entry = getEntry(tile.getUuid());
        if(entry.pos[0].equals(tile.getPosition())) {
            return new Pair<Pos, World>(entry.pos[1], entry.dimension[1]);
        } else {
            return new Pair<Pos, World>(entry.pos[0], entry.dimension[0]);
        }
    }

    private static void removeIfEmpty(LinkEntry e, UUID uuid) {
        if(e.pos[0].equals(Pos.NULL) && e.pos[1].equals(Pos.NULL)) {
            entries.remove(uuid);
        }
    }

    public static void clear() {
        entries.clear();
    }

    private static LinkEntry getEntry(UUID uuid) {
        LinkEntry entry = entries.get(uuid);
        if(entry == null) {
            entry = new LinkEntry();
            entries.put(uuid, entry);
        }
        return entry;
    }

    private static class LinkEntry {

        private Pos[] pos = new Pos[] {
                Pos.NULL,
                Pos.NULL
        };

        private World[] dimension = new World[] {null, null};
    }
}
