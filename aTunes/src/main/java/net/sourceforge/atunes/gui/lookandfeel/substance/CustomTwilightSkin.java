package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.skin.TwilightSkin;

public class CustomTwilightSkin extends TwilightSkin implements CustomSubstanceSkin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4543287686553802647L;

	public CustomTwilightSkin() {
		super();
	}
	
	@Override
	public Paint getPaintForColorMutableIcon(Component component, boolean isSelected) {
    	if (isSelected) {
    		return component.getForeground();    		
    	} else {
    		Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.HEADER).getUltraLightColor();    		
    		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);
    	}
	}
	
	@Override
	public Paint getPaintForSpecialControls() {
		Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.HEADER).getUltraLightColor();    		
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);
	}
	
}
