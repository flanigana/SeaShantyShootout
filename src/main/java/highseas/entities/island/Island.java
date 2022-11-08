package highseas.entities.island;

import highseas.entities.Entity;
import highseas.grid.Coord;

public class Island extends Entity {

	protected Island(int id, Coord coord) {
		super(id, coord);
		this.power = 1;
	}
	
	@Override
	public String toString() {
		return "Island [id=" + id + ", power=" + power + "]";
	}

	public void accumulatePower() {
		power++;
	}

}
