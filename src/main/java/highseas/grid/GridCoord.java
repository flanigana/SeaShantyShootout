package highseas.grid;

import java.util.HashSet;
import java.util.Set;

import highseas.entities.Entity;
import highseas.entities.island.Island;
import highseas.entities.ships.PirateShip;
import highseas.entities.ships.SpacePirateShip;

public class GridCoord extends Coord {

	private Set<Entity> entities;
	private boolean deadZone;

	/**
	 * Creates a GridCoord, an advanced Coord that can contain multiple ships
	 * 
	 * @param x
	 * @param y
	 */
	public GridCoord(int x, int y) {
		super(x, y);
		entities = new HashSet<>();
	}

	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	public void removeEntity(Entity s) {
		entities.remove(s);
	}

	public boolean isEmpty() {
		return entities.isEmpty();
	}

	public boolean hasPirate() {
		for (Entity ship : entities) {
			return ship instanceof PirateShip;
		}
		return false;
	}

	public boolean hasSpacePirate() {
		for (Entity ship : entities) {
			return ship instanceof SpacePirateShip;
		}
		return false;
	}

	public boolean hasIsland() {
		for (Entity ship : entities) {
			return ship instanceof Island;
		}
		return false;
	}

	/**
	 * @return Returns if there are multiple entities on the same tile, AKA a
	 *         collision
	 */
	public boolean hasCollision() {
		return entities.size() > 1;
	}

	public Set<Entity> getEntities() {
		return entities;
	}

	public boolean isDeadZone() {
		return deadZone;
	}

	public void setDeadZone(boolean deadZone) {
		this.deadZone = deadZone;
		entities = new HashSet<>();
	}

}
