package highseas.entities.island;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import highseas.grid.Coord;
import highseas.utilities.IdGen;

public class IslandFactory {
	private IdGen generator;

	private Logger infoLogger = LogManager.getLogger("InfoLogger");

	public IslandFactory(IdGen generator) {
		this.generator = generator;
	}

	public Island create(Coord coord) {
		Island island = new Island(generator.getNextVal(), coord);
		infoLogger.trace("Creating: " + island);
		return island;
	}
}
