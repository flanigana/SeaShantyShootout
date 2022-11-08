package highseas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import highseas.entities.Entity;
import highseas.entities.island.Island;
import highseas.entities.island.IslandController;
import highseas.entities.ships.PirateShip;
import highseas.entities.ships.Ship;
import highseas.entities.ships.ShipController;
import highseas.entities.ships.ShipType;
import highseas.entities.ships.SpacePirateShip;
import highseas.event.EventController;
import highseas.grid.GridController;
import highseas.gui.SSGui;

public class SeaController {

	ShipController shipController;
	GridController gridController;
	EventController eventController;
	IslandController islandController;
	Collection<Entity> entities;
	SSGui gui;

	private Logger infoLogger = LogManager.getLogger("InfoLogger");

	/**
	 * Creates a SeaController responsible for running the simulation.
	 * 
	 * @param shipController
	 * @param islandController
	 * @param gridController
	 * @param eventController
	 * @param gui
	 */
	public SeaController(ShipController shipController, IslandController islandController,
			GridController gridController, EventController eventController, SSGui gui) {
		this.shipController = shipController;
		this.islandController = islandController;
		this.gridController = gridController;
		this.eventController = eventController;
		this.gui = gui;
		entities = new ArrayList<>();
	}

	/**
	 * Starts the entirety of the simulation and runs until the simulation ends.
	 * 
	 * @param numShips   number of pirate ships to spawn at simulation start
	 * @param numIslands number of islands to spawn
	 */
	public void runSimulation(int numShips, int numIslands) {
		int turn = 0;
		for (int i = 0; i < numShips; i++) {
			Optional<Ship> ship = shipController.spawnShip(ShipType.PIRATE);
			if (ship.isPresent()) {
				entities.add(ship.get());
			}
		}

		for (int i = 0; i < numIslands; i++) {
			Optional<Island> island = islandController.spawnIsland();
			if (island.isPresent()) {
				entities.add(island.get());
			}

		}

		while (pirateShipsRemaining() > 1 && turn++ < 20000) {
			infoLogger.info("Turn " + turn + ": ");
			if (turn % 100 == 0) {
				Set<Entity> removedEntities;
				removedEntities = gridController.shrinkGrid();
				entities.removeAll(removedEntities);
				infoLogger.info("The following were lost to the abyss: {}", removedEntities);
				if (pirateShipsRemaining() == 1) {
					break;
				}
			}

			if (turn % 5 == 0) {
				Optional<Ship> ship = shipController.spawnShip(ShipType.SPACE_PIRATE);
				if (ship.isPresent()) {
					entities.add(ship.get());
				}
			}
			processTurn(turn);

			gui.updateDisplay();
		}

		gui.updateDisplay();
		infoLogger.info("Winning Pirates:");
		printWinners();
	}

	/**
	 * Processes the logic of a single turn including ship movement, battles, and
	 * entity removal
	 * 
	 * @param turn current turn number
	 */
	public void processTurn(int turn) {
		entities.stream().filter(s -> s instanceof Ship).map(s -> (Ship) s).forEach(shipController::moveShip);
		gridController.getCollisions().forEach(g -> {
			Set<Entity> collidingEntities = g.getEntities().stream().collect(Collectors.toSet());
			Entity remainingEntity = eventController.processEvent(g);
			collidingEntities.forEach(s -> {
				if (!s.equals(remainingEntity) && s instanceof Ship) {
					g.removeEntity(s);
					entities.remove(s);
				}
			});

		});
	}

	/**
	 * Retrieves the number of remaining standard pirate ships
	 * 
	 * @return number of standard pirate ships remaining
	 */
	public long pirateShipsRemaining() {
		return entities.stream().filter(s -> s instanceof PirateShip).count();
	}

	/**
	 * Retrieves the number of remaining space pirate ships
	 * 
	 * @return number of space pirate ships remaining
	 */
	public long spacePiratesRemaining() {
		return entities.stream().filter(s -> s instanceof SpacePirateShip).count();
	}

	/**
	 * Prints the winners of the simulation to the logger
	 */
	public void printWinners() {
		entities.stream().filter(s -> s instanceof PirateShip).findFirst().ifPresentOrElse(infoLogger::info,
				() -> infoLogger.info("Space Pirates"));
		infoLogger.info("Space Pirates Remaining: " + spacePiratesRemaining());
	}
}
