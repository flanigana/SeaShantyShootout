package highseas.entities.ships;

import java.util.Optional;

import highseas.grid.Coord;
import highseas.grid.GridController;

public class ShipController {

	private ShipFactory factory;
	private GridController gridController;

	/**
	 * Creates a ShipController from a gridController and a factory
	 * 
	 * @param gridC   a GridController that manages vacancies and the locations of
	 *                ships
	 * @param factory a factory that creates PirateShips and SpacePirateShips
	 */
	public ShipController(GridController gridC, ShipFactory factory) {
		this.gridController = gridC;
		this.factory = factory;
	}

	/**
	 * Spawns a ship at a random Coord in the GridController
	 * 
	 * @param type Type of ship to be created
	 * @return
	 */
	public Optional<Ship> spawnShip(ShipType type) {
		Optional<Coord> coord = gridController.getRandomVacant();
		if (coord.isPresent()) {
			Ship ship = factory.create(coord.get(), type);
			gridController.addEntity(ship);
			return Optional.of(ship);
		}
		return Optional.empty();
	}

	public void moveShip(Ship ship) {
		Coord prevCoord = ship.getCoord();
		Coord nextCoord = gridController.getRandomAdjacent(prevCoord);
		gridController.removeEntity(ship);
		ship.setCoord(nextCoord);
		gridController.addEntity(ship);
	}

}
