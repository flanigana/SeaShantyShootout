package highseas.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import highseas.entities.Entity;
import highseas.entities.island.Island;
import highseas.entities.ships.Ship;
import highseas.grid.GridCoord;

public class EventController {

	private Logger actionLogger = LogManager.getLogger("ActionLogger");
	private Logger infoLogger = LogManager.getLogger("InfoLogger");

	public Entity processEvent(GridCoord gridC) {
		List<Entity> ships = gridC.getEntities().stream().filter(e -> e instanceof Ship).collect(Collectors.toList());
		Entity winner = ships.get(0);
		if (ships.size() > 1) {
			winner = processBattle(gridC, ships);
		}

		Optional<Entity> island = gridC.getEntities().stream().filter(e -> e instanceof Island).findAny();
		if (island.isPresent()) {
			actionLogger.info("{} looted {}", winner, island.get());
			winner.setPower(winner.getPower() + island.get().getPower());
		}

		return winner;
	}

	private Entity processBattle(GridCoord gridC, Collection<Entity> ships) {
		double totalPower = ships.stream().mapToDouble(Entity::getPower).reduce((p1, p2) -> p1 + p2).getAsDouble();
		double winningPower = ThreadLocalRandom.current().nextDouble(totalPower);
		Entity winner = null;
		for (Entity ship : ships) {
			winningPower -= ship.getPower();
			if (winningPower <= 0) {
				winner = ship;
			}
		}
		Collection<Entity> losers = ships;
		losers.remove(winner);
		actionLogger.info("{} has sunk {}", winner, losers);
		winner.setPower((winner.getPower() + totalPower) / 2);
		return winner;
	}
}
