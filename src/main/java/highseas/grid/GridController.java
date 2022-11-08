package highseas.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import highseas.entities.Entity;
import highseas.entities.ships.Ship;

public class GridController {
	private Set<Coord> vacancies;
	private Map<Coord, Set<Coord>> adjCoords;
	private List<List<GridCoord>> grid;
	private double percentageShrink;
	private int currentRadius;
	private Coord nextShrinkCenter;

	/**
	 * Creates a GridController of size size * size Manages and returns vacancies
	 * and adjacency for the ShipController as well as keeping track of where ships
	 * are
	 * 
	 * @param size             the width and height of the grid
	 * @param shrinkPercentage percentage to shrink the grid by each time
	 */
	public GridController(int size, double shrinkPercentage) {
		vacancies = new HashSet<>();
		grid = new ArrayList<>();
		adjCoords = new HashMap<>();
		for (int i = 0; i < size; i++) {
			List<GridCoord> row = new ArrayList<>();
			for (int j = 0; j < size; j++) {
				GridCoord c = new GridCoord(i, j);
				vacancies.add(c);
				row.add(c);
				Set<Coord> selfCoords = new HashSet<>();
				selfCoords.add(c);
				adjCoords.put(c, selfCoords);
			}
			grid.add(row);
		}

		populateAdjCoords();

		percentageShrink = shrinkPercentage;
		currentRadius = size / 2;
		updateShrinkCenter(true);

	}

	/**
	 * Creates an adjacency list (adjCoords) for each possible move from a Coord.
	 * Each Coord is adjacent to the tile above, below, to the left, and to the
	 * right of it, as well as itself.
	 * 
	 * @param x width of the space
	 * @param y height of the space
	 */
	private void populateAdjCoords() {
		// populating every row except bottom for adjacency list
		for (int i = 0; i < grid.size() - 1; i++) {
			for (int j = 0; j < grid.size(); j++) {
				Coord c = grid.get(i).get(j);
				Coord belowC = grid.get(i + 1).get(j);
				adjCoords.get(c).add(belowC);
			}
		}

		// populating every row except top for adjacency list
		for (int i = 1; i < grid.size(); i++) {
			for (int j = 0; j < grid.size(); j++) {
				Coord c = grid.get(i).get(j);
				Coord aboveC = grid.get(i - 1).get(j);
				adjCoords.get(c).add(aboveC);
			}
		}

		// populating every column except right for adjacency list
		for (int i = 0; i < grid.size(); i++) {
			for (int j = 0; j < grid.size() - 1; j++) {
				Coord c = grid.get(i).get(j);
				Coord rightC = grid.get(i).get(j + 1);
				adjCoords.get(c).add(rightC);
			}
		}

		// populating every column except left for adjacency list
		for (int i = 0; i < grid.size(); i++) {
			for (int j = 1; j < grid.size(); j++) {
				Coord c = grid.get(i).get(j);
				Coord leftC = grid.get(i).get(j - 1);
				adjCoords.get(c).add(leftC);
			}
		}
	}

	/**
	 * Gets a random vacant Coord from vacancies, if there is one.
	 * 
	 * @return An Optional Coord, in case there are none.
	 */
	public Optional<Coord> getRandomVacant() {
		if (vacancies.isEmpty()) {
			return Optional.empty();
		}
		return vacancies.stream().skip(ThreadLocalRandom.current().nextInt(vacancies.size())).findFirst();
	}

	/**
	 * Finds a valid adjacent Coord at random for a given Coord. (Not optional cause
	 * it can always return itself)
	 * 
	 * @param coord Coord to get adjacency for
	 * @return A random adjacent Coord
	 */
	public Coord getRandomAdjacent(Coord coord) {
		Set<Coord> adj = adjCoords.get(coord);
		return adj.stream().skip(ThreadLocalRandom.current().nextInt(adj.size())).findFirst().get();
	}

	/**
	 * Adds a vacant Coord to the vacancies
	 * 
	 * @param coord Coord to be added
	 */
	public void addVacant(Coord coord) {
		vacancies.add(coord);
	}

	/**
	 * Removes a vacant Coord from vacancies
	 * 
	 * @param coord Coord to be removed
	 */
	public void removeVacant(Coord coord) {
		vacancies.remove(coord);
	}

	/**
	 * Adds a ship to the Grid, in a valid spot.
	 * 
	 * @param entity Ship to be added
	 */
	public void addEntity(Entity entity) {
		GridCoord gridC = getGridCoord(entity.getCoord());
		gridC.addEntity(entity);
		removeVacant(gridC);
	}

	/**
	 * Removes a ship from the Grid, freeing its spot.
	 * 
	 * @param ship Ship to be removed
	 */
	public void removeEntity(Ship ship) {
		GridCoord gridC = getGridCoord(ship.getCoord());
		gridC.removeEntity(ship);
		if (gridC.isEmpty()) {
			addVacant(gridC);
		}
	}

	/**
	 * Returns a GridCoord from a given Coord
	 * 
	 * @param coord Coord given
	 * @return Corresponding GridCoord
	 */
	private GridCoord getGridCoord(Coord coord) {
		return grid.get(coord.getX()).get(coord.getY());
	}

	/**
	 * Gets a list of all GridCoords where there are multiple Entites on one space
	 * 
	 * @return A List of collisions
	 */
	public List<GridCoord> getCollisions() {
		return grid.stream().flatMap(l -> l.stream()).filter(g -> g.hasCollision()).collect(Collectors.toList());
	}

	/**
	 * Shrinks the valid grid zone
	 * 
	 * @param percentageShrink percentage to shrink current zone by
	 * @return a Set of entities that were present in the removed grid coordinates
	 */
	public Set<Entity> shrinkGrid() {
		Set<Entity> removedEntities = new HashSet<>();
		currentRadius = (int) (currentRadius - (currentRadius * percentageShrink));
		removedEntities.addAll(shrinkGridRows());
		removedEntities.addAll(shrinkGridColumns());
		updateShrinkCenter(false);
		return removedEntities;
	}

	/**
	 * Shrinks the columns of the grid
	 * 
	 * @return a Set of entities that were present in the removed grid coordinates
	 */
	private Set<Entity> shrinkGridColumns() {
		Set<Entity> removedEntities = new HashSet<>();
		int leftPos = nextShrinkCenter.getX() - currentRadius;
		int rightPos = nextShrinkCenter.getX() + currentRadius;

		while (leftPos >= 0) {
			removedEntities.addAll(removeColumn(leftPos));
			leftPos--;
		}
		while (rightPos < grid.size()) {
			removedEntities.addAll(removeColumn(rightPos));
			rightPos++;
		}
		return removedEntities;
	}

	/**
	 * Removes a single column from the grid
	 * 
	 * @param pos position of the column to remove
	 * @return a Set of entities that were present in the removed grid coordinates
	 */
	private Set<Entity> removeColumn(int pos) {
		Set<Entity> removedEntities = new HashSet<>();
		List<GridCoord> column = grid.get(pos);
		for (GridCoord coord : column) {
			removedEntities.addAll(killGridCoord(coord));
		}
		return removedEntities;
	}

	/**
	 * Shrinks the rows of the grid
	 * 
	 * @return a Set of entities that were present in the removed grid coordinates
	 */
	private Set<Entity> shrinkGridRows() {
		Set<Entity> removedEntities = new HashSet<>();

		for (List<GridCoord> column : grid) {
			int topPos = nextShrinkCenter.getY() - currentRadius;
			int bottomPos = nextShrinkCenter.getY() + currentRadius;

			while (topPos >= 0) {
				removedEntities.addAll(removeRowInColumn(column, topPos));
				topPos--;
			}
			while (bottomPos < grid.size()) {
				removedEntities.addAll(removeRowInColumn(column, bottomPos));
				bottomPos++;
			}
		}
		return removedEntities;
	}

	/**
	 * Removes a row in each column from the grid
	 * 
	 * @param row the row to remove the column from
	 * @param pos position of the column to remove
	 * @return a Set of entities that were present in the removed grid coordinates
	 */
	private Set<Entity> removeRowInColumn(List<GridCoord> column, int pos) {
		GridCoord coord = column.get(pos);
		return killGridCoord(coord);
	}

	/**
	 * Sets the deadZone flag in the GridCoord and removes entities from the coord
	 * 
	 * @param coord the GridCoord to set as dead zone
	 * @return a Set of entities that were present in the removed grid coordinate
	 */
	private Set<Entity> killGridCoord(GridCoord coord) {
		removeVacant(coord);
		removeAdjCoord(coord);
		Set<Entity> removedEntities = coord.getEntities();
		coord.setDeadZone(true);
		return removedEntities;
	}

	/**
	 * Updates the adjacency list around the given coordinate's
	 * 
	 * @param coord coordinate being removed from the grid
	 */
	private void removeAdjCoord(GridCoord coord) {
		Set<Coord> adjToCoord = adjCoords.get(coord).stream().collect(Collectors.toSet());
		adjToCoord.forEach(c -> {
			adjCoords.get(c).remove(coord);
		});
	}

	/**
	 * Generates a the next center point for the grid to shrink around
	 */
	private void updateShrinkCenter(boolean first) {
		Optional<GridCoord> shrinkCenter = getShrinkCenter(first);
		if (shrinkCenter.isPresent()) {
			nextShrinkCenter = shrinkCenter.get();
		}
	}

	/**
	 * Retrieves a random coordinate within the grid that is not a dead zone to use
	 * for the next grid shrink center
	 * 
	 * @return a non-dead-zone coordinate in the grid
	 */
	public Optional<GridCoord> getShrinkCenter(boolean first) {
		Set<GridCoord> allCoords = grid.stream().flatMap(l -> l.stream()).filter(c -> !c.isDeadZone())
				.collect(Collectors.toSet());

		if (!first) {
			int nextRadius = (int) (currentRadius - (currentRadius * percentageShrink));
			int currentLeft = nextShrinkCenter.getX() - currentRadius;
			int currentRight = nextShrinkCenter.getX() + currentRadius;
			int currentTop = nextShrinkCenter.getY() - currentRadius;
			int currentBottom = nextShrinkCenter.getY() + currentRadius;
			allCoords = allCoords.stream().filter(c -> {
				int nextLeft = c.getX() - nextRadius;
				int nextRight = c.getX() + nextRadius;
				int nextTop = c.getY() - nextRadius;
				int nextBottom = c.getY() + nextRadius;
				if (nextLeft >= currentLeft && nextRight <= currentRight && nextTop >= currentTop
						&& nextBottom <= currentBottom) {
					return true;
				} else {
					return false;
				}
			}).collect(Collectors.toSet());
		}
		return allCoords.stream().skip(ThreadLocalRandom.current().nextInt(allCoords.size())).findFirst();
	}

	public List<List<GridCoord>> getGrid() {
		return grid;
	}

	public Coord getNextShrinkCenter() {
		return nextShrinkCenter;
	}

}
