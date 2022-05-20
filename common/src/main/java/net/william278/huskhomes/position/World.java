package net.william278.huskhomes.position;

import java.util.UUID;

/**
 * Represents a world on a server
 */
public class World {

    /**
     * The name of this world, as defined by the world directory name
     */
    public String name;

    /**
     * UUID of this world, as defined by the {@code uid.dat} file in the world directory
     */
    public UUID uuid;

    public World(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

}
