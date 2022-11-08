package highseas;

import javax.swing.JApplet;
import javax.swing.JFrame;

import highseas.entities.island.IslandController;
import highseas.entities.island.IslandFactory;
import highseas.entities.ships.ShipController;
import highseas.entities.ships.ShipFactory;
import highseas.event.EventController;
import highseas.grid.GridController;
import highseas.gui.SSGui;
import highseas.utilities.AtomicIdGen;
import highseas.utilities.IdGen;

public class Demo {
	public static void main(String[] args) {
		int gridSize = 100;
		int numPirates = 1000;
		int numIslands = 200;
		IdGen generator = AtomicIdGen.createDefaultIdGen();

		GridController gridController = new GridController(gridSize, 0.15);
		ShipFactory factory = new ShipFactory(generator);
		ShipController shipController = new ShipController(gridController, factory);
		IslandFactory islandFactory = new IslandFactory(generator);
		IslandController islandController = new IslandController(gridController, islandFactory);
		EventController eventController = new EventController();

		JFrame frame = new JFrame();
		frame.setTitle("Sea Shanty Shoot-out");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new JApplet();
		SSGui gui = new SSGui(gridSize, gridController);
		applet.getContentPane().add(gui);
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);

		SeaController seaController = new SeaController(shipController, islandController, gridController,
				eventController, gui);
		seaController.runSimulation(numPirates, numIslands);
	}
}
