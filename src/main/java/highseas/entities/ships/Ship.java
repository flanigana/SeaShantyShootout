package highseas.entities.ships;

import highseas.entities.Entity;
import highseas.grid.Coord;

public abstract class Ship extends Entity{
	/**
	 * An entity that has a location and power, a value that is used when judging which ship can win in combat
	 * @param id
	 * @param coord	A location that represents a position in 2D space
	 */
	Ship(int id, Coord coord) {
		super(id, coord);
	}

	void setXCoord(int xCoord) {
		this.coord.setX(xCoord);
	}
	
	void setYCoord(int yCoord) {
		this.coord.setY(yCoord);
	}

	public int getXCoord() {
		return coord.getX();
	}

	public int getYCoord() {
		return coord.getY();
	}
	
	@Override
	public String toString() {
		return "[id=" + id + ", power= " + power + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ship other = (Ship) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
