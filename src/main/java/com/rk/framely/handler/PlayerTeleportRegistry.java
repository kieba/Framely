package com.rk.framely.handler;

import com.rk.framely.tileentity.TileEntityFrameTeleporter;
import com.rk.framely.tileentity.TileEntityPlayerTeleporter;
import com.rk.framely.util.Pair;
import com.rk.framely.util.Pos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class PlayerTeleportRegistry {

    private static HashMap<UUID, Entry> entries = new HashMap<UUID, Entry>();

    public static boolean registerFrameTeleporter(TileEntityPlayerTeleporter tile) {
        Entry entry = getEntry(tile.getUuid());
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

    public static void unregisterFrameTeleporter(TileEntityPlayerTeleporter tile) {
        Entry entry = getEntry(tile.getUuid());
        if(entry.pos[0].equals(tile.getPosition())) {
            entry.pos[0] = Pos.NULL;
            entry.dimension[0] = null;
        } else if(entry.pos[1].equals(tile.getPosition())) {
            entry.pos[1] = Pos.NULL;
            entry.dimension[1] = null;
        }

        if(entry.pos[0].equals(Pos.NULL) && entry.pos[1].equals(Pos.NULL)) {
            entries.remove(tile.getUuid());
        }
    }

    public static Pair<Pos, World> getDestination(TileEntityPlayerTeleporter tile) {
        Entry entry = getEntry(tile.getUuid());
        if(entry.pos[0].equals(tile.getPosition())) {
            return new Pair<Pos, World>(entry.pos[1], entry.dimension[1]);
        } else {
            return new Pair<Pos, World>(entry.pos[0], entry.dimension[0]);
        }
    }

    private static Entry getEntry(UUID uuid) {
        Entry entry = entries.get(uuid);
        if(entry == null) {
            entry = new Entry();
            entries.put(uuid, entry);
        }
        return entry;
    }

    private static class Entry {

        private Pos[] pos = new Pos[]{
                Pos.NULL,
                Pos.NULL
        };

        private World[] dimension = new World[]{null, null};
    }
}
