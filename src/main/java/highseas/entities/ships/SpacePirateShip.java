package highseas.entities.ships;

import highseas.grid.Coord;

public class SpacePirateShip extends Ship {

	SpacePirateShip(int id, Coord coord) {
		super(id, coord);
		power = 2;
	}

	@Override
	public String toString() {
		return "SpacePirateShip " + super.toString();
	}

}
