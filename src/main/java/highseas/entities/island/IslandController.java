package highseas.entities.island;

import java.util.Optional;

import highseas.grid.Coord;
import highseas.grid.GridController;

public class IslandController {

	GridController gridController;
	IslandFactory islandFactory;

	public IslandController(GridController gridController, IslandFactory islandFactory) {
		this.gridController = gridController;
		this.islandFactory = islandFactory;
	}

	public Optional<Island> spawnIsland() {
		Optional<Coord> coord = gridController.getRandomVacant();
		if (coord.isPresent()) {
			Island island = islandFactory.create(coord.get());
			gridController.addEntity(island);
			return Optional.of(island);
		}
		return Optional.empty();
	}
}
