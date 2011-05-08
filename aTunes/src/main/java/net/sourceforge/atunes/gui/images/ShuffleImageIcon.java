package net.sourceforge.atunes.gui.images;

import java.awt.Polygon;

import javax.swing.ImageIcon;

public class ShuffleImageIcon {

	public static ImageIcon getIcon() {
		Polygon p = new Polygon();
		p.addPoint(1, 3);
		p.addPoint(12, 3);
		p.addPoint(12, 5);
		p.addPoint(3, 5);
		p.addPoint(3, 8);
		p.addPoint(17, 8);
		p.addPoint(17, 15);
		p.addPoint(6, 15);
		p.addPoint(6, 13);
		p.addPoint(15, 13);
		p.addPoint(15, 10);
		p.addPoint(1, 10);
		
		Polygon p3 = new Polygon();
		p3.addPoint(12, 1);
		p3.addPoint(16, 4);
		p3.addPoint(12, 7);

		Polygon p4 = new Polygon();
		p4.addPoint(2, 14);
		p4.addPoint(6, 11);
		p4.addPoint(6, 17);

		
		return IconGenerator.generateIcon(18, 18, p, p3, p4);
	}
}
