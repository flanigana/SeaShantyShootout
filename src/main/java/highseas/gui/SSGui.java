package highseas.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JPanel;

import highseas.grid.Coord;
import highseas.grid.GridController;
import highseas.grid.GridCoord;

public class SSGui extends JPanel {
	private static final long serialVersionUID = -7580170626829977505L;
	private int cellSize;
	private GridController gridController;

	public SSGui(int gridSize, GridController gridControl) {
		gridController = gridControl;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double height = screenSize.getHeight();

		cellSize = (int) (height / gridSize) - 1;
		setPreferredSize(new Dimension(gridSize * cellSize, gridSize * cellSize));
		setBackground(new Color(7, 48, 79));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		drawGrid(g2D);
	}

	private void drawGrid(Graphics2D g2D) {
		List<List<GridCoord>> grid = gridController.getGrid();
		for (int i = 0; i < grid.size(); i++) {
			List<GridCoord> row = grid.get(i);
			for (int j = 0; j < row.size(); j++) {
				int x = i * cellSize;
				int y = j * cellSize;

				GridCoord cell = row.get(j);
				if (cell.hasIsland()) {
					g2D.setColor(new Color(222, 204, 40));
					g2D.fillRect(y, x, cellSize, cellSize);
				}
				if (cell.isDeadZone()) {
					g2D.setColor(new Color(0, 0, 0));
					g2D.fillRect(y, x, cellSize, cellSize);
				} else if (cell.hasPirate()) {
					g2D.setColor(new Color(0, 201, 20));
					g2D.fillOval(y, x, cellSize, cellSize);
				} else if (cell.hasSpacePirate()) {
					g2D.setColor(new Color(171, 17, 97));
					g2D.fillOval(y, x, cellSize, cellSize);
				}
			}
		}
		drawNextShrinkCenter(g2D, new Color(255, 115, 0));
	}

	private void drawNextShrinkCenter(Graphics2D g2D, Color color) {
		Coord nextCenter = gridController.getNextShrinkCenter();
		g2D.setColor(color);
		int x = nextCenter.getX() * cellSize;
		int y = nextCenter.getY() * cellSize;
		g2D.fillOval(y, x, cellSize, cellSize);
	}

	public void updateDisplay() {
		repaint();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
