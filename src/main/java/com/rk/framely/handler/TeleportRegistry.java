package com.rk.framely.handler;

import com.rk.framely.tileentity.TileEntityTeleporter;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;

import java.util.*;

public class TeleportRegistry {

    private static HashMap<UUID, Entry> entries = new HashMap<UUID, Entry>();

    public static boolean registerTeleporter(TileEntityTeleporter tile) {
        Entry entry = getEntry(tile.getUuid());
        boolean registered = false;
        if(entry.teleporter[0].equals(Pos.NULL)) {
            entry.teleporter[0] = tile.getPosition();
            registered = true;
        } else if(entry.teleporter[1].equals(Pos.NULL)) {
            entry.teleporter[1] = tile.getPosition();
            registered = true;
        }
        return registered;
    }

    public static void unregisterTeleporter(TileEntityTeleporter tile) {
        Entry entry = getEntry(tile.getUuid());
        if(entry.teleporter[0].equals(tile.getPosition())) {
            entry.teleporter[0] = Pos.NULL;
        } else if(entry.teleporter[1].equals(tile.getPosition())) {
            entry.teleporter[1] = Pos.NULL;
        }

        if(entry.teleporter[0].equals(Pos.NULL) && entry.teleporter[1].equals(Pos.NULL)) {
            entries.remove(tile.getUuid());
        }
    }

    public static Pos getDestination(TileEntityTeleporter tile) {
        Entry entry = getEntry(tile.getUuid());
        if(entry.teleporter[0].equals(tile.getPosition())) {
            return entry.teleporter[1];
        } else {
            return entry.teleporter[0];
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

        private Pos[] teleporter = new Pos[] {
                Pos.NULL,
                Pos.NULL
        };
    }
}
