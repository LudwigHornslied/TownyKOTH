package xyz.ludwicz.townykoth.util;

import org.bukkit.Location;

public class BlockCoord {
	
	private int x;
	private int y;
	private int z;
	
	private BlockCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public static BlockCoord parseCoord(Location location) {
		return parseCoord(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public static BlockCoord parseCoord(int x, int y, int z) {
		return new BlockCoord(x, y, z);
	}

	@Override
	public int hashCode() {

		int result = 18;
		result = 27 * result + x;
		result = 27 * result + y;
		result = 27 * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this)
			return true;
		if (!(obj instanceof BlockCoord))
			return false;

		BlockCoord o = (BlockCoord) obj;
		return this.x == o.getX() && this.y == o.getY() && this.z == o.getZ();
	}
}
