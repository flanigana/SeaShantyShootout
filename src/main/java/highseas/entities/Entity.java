package highseas.entities;

import highseas.grid.Coord;

public abstract class Entity {

	protected int id;
	protected Coord coord;
	protected double power;
	
	protected Entity(int id, Coord coord) {
		this.id = id;
		this.coord = coord;
	}
	
	public Coord getCoord() {
		return coord;
	}
	
	public int getId() {
		return id;
	}
	
	public void setCoord(Coord coord) {
		this.coord = coord;
	}
	
	public void setPower(double power) {
		this.power = power;
	}
	
	public double getPower() {
		return power;
	}
}
