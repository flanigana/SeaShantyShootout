package highseas.entities.ships;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import highseas.grid.Coord;
import highseas.utilities.IdGen;

public class ShipFactory {
	private IdGen generator;
	
	private Logger infoLogger = LogManager.getLogger("InfoLogger");
	
	public ShipFactory(IdGen generator) {
		this.generator = generator;
	}
	/**
	 * Crates a ship at the specified Coord with specified type
	 * @param coord	Location to be created
	 * @param type	Type of ship (Pirate or SpacePirate)
	 * @return	the Ship
	 */
	public Ship create(Coord coord, ShipType type) {
		Ship ship;
		switch (type) {
		case PIRATE:
			ship = new PirateShip(generator.getNextVal(), coord);
			break;
		default :
			ship = new SpacePirateShip(generator.getNextVal(), coord);
		}
		infoLogger.trace("Creating: " + ship);
		return ship;
	}
}
