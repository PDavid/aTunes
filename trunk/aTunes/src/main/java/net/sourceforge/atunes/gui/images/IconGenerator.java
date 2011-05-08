package net.sourceforge.atunes.gui.images;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

class IconGenerator {

	/**
	 * Creates an image icon drawing a list of shapes
	 * @param width
	 * @param height
	 * @param shapes
	 * @return
	 */
	protected static final ImageIcon generateIcon(int width, int height, Shape... shapes) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintFor(null));
        for (Shape s : shapes) {
        	g.fill(s);
        }
        return new ImageIcon(bi);
	}
}
