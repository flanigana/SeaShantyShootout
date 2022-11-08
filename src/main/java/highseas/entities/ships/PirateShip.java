package highseas.entities.ships;

import highseas.grid.Coord;

public class PirateShip extends Ship {

	/**
	 * An extention of Ship that is mostly for classification purposes
	 * @param id
	 * @param coord
	 */
	PirateShip(int id, Coord coord) {
		super(id, coord);
		power = 1;
	}

	@Override
	public String toString() {
		return "PirateShip " + super.toString();
	}

}
