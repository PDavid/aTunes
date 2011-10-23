/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Paint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.Logger;

import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.utils.ShadowPopupBorder;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceConstants;
import org.pushingpixels.substance.api.fonts.FontPolicy;
import org.pushingpixels.substance.api.fonts.FontSet;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public final class SubstanceLookAndFeel extends AbstractLookAndFeel {

    public static final String SUBSTANCE = "Substance";

    private Object menuBarUI;
    
	private static final class CustomFontPolicy implements FontPolicy {
        private final class CustomFontSet implements FontSet {
			private FontUIResource windowTitleFont = new FontUIResource(baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 1f));
			private FontUIResource titleFont = new FontUIResource(baseFont.deriveFont((float) baseFont.getSize()));
			private FontUIResource smallFont = new FontUIResource(baseFont.deriveFont(baseFont.getSize() - 1f));
			private FontUIResource messageFont = new FontUIResource(baseFont.deriveFont(baseFont.getSize() - 1f));
			private FontUIResource menuFont = new FontUIResource(baseFont.deriveFont((float) baseFont.getSize()));
			private FontUIResource controlFont = new FontUIResource(baseFont.deriveFont((float) baseFont.getSize()));

			@Override
			public FontUIResource getWindowTitleFont() {
			    return windowTitleFont;
			}

			@Override
			public FontUIResource getTitleFont() {
			    return titleFont;
			}

			@Override
			public FontUIResource getSmallFont() {
			    return smallFont;
			}

			@Override
			public FontUIResource getMessageFont() {
			    return messageFont;
			}

			@Override
			public FontUIResource getMenuFont() {
			    return menuFont;
			}

			@Override
			public FontUIResource getControlFont() {
			    return controlFont;
			}
		}

		private final Font baseFont;

        private CustomFontPolicy(Font baseFont) {
            this.baseFont = baseFont;
        }

        @Override
        public FontSet getFontSet(String arg0, UIDefaults arg1) {
            return new CustomFontSet();
        }
    }

    private static final class SubstanceLookAndFeelListCellRenderer extends SubstanceDefaultListCellRenderer {
        private final AbstractListCellRendererCode code;

        private static final long serialVersionUID = 2572603555660744197L;

        private SubstanceLookAndFeelListCellRenderer(AbstractListCellRendererCode code) {
            this.code = code;
        }

        @Override
        public JComponent getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        	JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            return code.getComponent(c, list, value, index, isSelected, cellHasFocus);
        }
    }

    private static final class SubstanceLookAndFeelTableHeaderCellRenderer extends SubstanceDefaultTableHeaderCellRenderer {
        private final AbstractTableCellRendererCode code;

        private static final long serialVersionUID = 1L;

        private SubstanceLookAndFeelTableHeaderCellRenderer(AbstractTableCellRendererCode code) {
            this.code = code;
        }

        @Override
        public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
        }
    }

    private static final class SubstanceLookAndFeelTableCellRenderer extends SubstanceDefaultTableCellRenderer {
        private final AbstractTableCellRendererCode code;

        private static final long serialVersionUID = 2844251523912028654L;

        private SubstanceLookAndFeelTableCellRenderer(AbstractTableCellRendererCode code) {
            this.code = code;
        }

        @Override
        public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
        }
    }

    private static final class SubstanceLookAndFeelTreeCellRenderer extends SubstanceDefaultTreeCellRenderer {
        private final AbstractTreeCellRendererCode code;

        private static final long serialVersionUID = 3830003466764008228L;

        private SubstanceLookAndFeelTreeCellRenderer(AbstractTreeCellRendererCode code) {
            this.code = code;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        	JComponent c = (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            return code.getComponent(c, tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

    private static final class SkinsComparator implements Comparator<String>, Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 8396385246074192265L;

		@Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    }

    /** The map of skin names and class names */
    private Map<String, String> skins = setMapOfSkins();

    /** The default skin */
    private static final String DEFAULT_SKIN = "aTunes Blue";

    /**
     * Sets the list of skins.
     * 
     * @return the map< string, string>
     */
    private Map<String, String> setMapOfSkins() {
        Map<String, String> result = new HashMap<String, String>();

        result.put("BusinessBlackSteel", "org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel");
        result.put("Creme", "org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel");
        result.put("Business", "org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel");
        result.put("BusinessBlueSteel", "org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
        result.put("CremeCoffee", "org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel");
        result.put("Sahara", "org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel");
        result.put("Moderate", "org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
        result.put("OfficeSilver2007", "org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel");
        result.put("Nebula", "org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel");
        result.put("NebulaBrickWall", "org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel");
        result.put("Autumn", "org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel");
        result.put("MistSilver", "org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel");
        result.put("MistAqua", "org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel");
        result.put("DustCoffee", "org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel");
        result.put("Dust", "org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
        result.put("Raven", "org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel");
        result.put("ChallengerDeep", "org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel");
        result.put("EmeraldDusk", "org.pushingpixels.substance.api.skin.SubstanceEmeraldDuskLookAndFeel");
        // Custom Twilight look and feel
        result.put("Twilight", "net.sourceforge.atunes.gui.lookandfeel.substance.CustomTwilightLookAndFeel");
        result.put("OfficeBlue2007", "org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel");
        result.put("Gemini", "org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel");
        result.put("Magellan", "org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel");
        result.put("GraphiteAqua", "org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
        result.put("GraphiteGlass", "org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel");
        result.put("Graphite", "org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");

        /*
         * custom skins
         */
        result.put("aTunes Blue", "net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceATunesBlueLookAndFeel");
        result.put("aTunes Dark", "net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceATunesDarkLookAndFeel");
        result.put("aTunes Gray", "net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceATunesGrayLookAndFeel");

        return result;
    }

    @Override
    public String getName() {
        return SUBSTANCE;
    }

    @Override
    public String getDescription() {
        return "Substance Look And Feel";
    }

    @Override
    public void initializeLookAndFeel() {
        AnimationConfigurationManager.getInstance().setTimelineDuration(0);
        UIManager.put(org.pushingpixels.substance.api.SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL);

        if (!osManager.isMacOsX()) {
        	// Avoid custom window decoration in mac os to draw window controls at left
        	JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        } else {
        	// This is a trick to draw menu at Mac OS X menu bar
        	// Taken from http://www.pushing-pixels.org/2008/07/13/swing-applications-and-mac-os-x-menu-bar.html
        	// However original method does this with other components, but doing it causes exceptions in Substance classes
        	menuBarUI = UIManager.get("MenuBarUI");
        }
    }

    @Override
    public void setLookAndFeel(String skin) {
        try {
            if (skins.containsKey(skin)) {
                UIManager.setLookAndFeel(skins.get(skin));
            } else {
                UIManager.setLookAndFeel(skins.get(DEFAULT_SKIN));
            }

            if (osManager.isMacOsX()) {
            	UIManager.put("MenuBarUI", menuBarUI);
            }
            
            // Get border color
            GuiUtils.setBorderColor(org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.GENERAL).getMidColor());

        } catch (ClassNotFoundException e) {
            Logger.error(e);
        } catch (InstantiationException e) {
            Logger.error(e);
        } catch (IllegalAccessException e) {
            Logger.error(e);
        } catch (UnsupportedLookAndFeelException e) {
            Logger.error(e);
        }
    }

    @Override
    public String getDefaultSkin() {
        return DEFAULT_SKIN;
    }

    @Override
    public List<String> getSkins() {
        List<String> result = new ArrayList<String>(skins.keySet());
        Collections.sort(result, new SkinsComparator());
        return result;
    }

    @Override
    public TreeCellRenderer getTreeCellRenderer(final AbstractTreeCellRendererCode code) {
        return new SubstanceLookAndFeelTreeCellRenderer(code);
    }

    /**
     * Returns a new TableCellRenderer executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    @Override
    public TableCellRenderer getTableCellRenderer(final AbstractTableCellRendererCode code) {
        return new SubstanceLookAndFeelTableCellRenderer(code);
    }

    /**
     * Returns a new TableCellRenderer executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    @Override
    public TableCellRenderer getTableHeaderCellRenderer(final AbstractTableCellRendererCode code) {
        return new SubstanceLookAndFeelTableHeaderCellRenderer(code);
    }

    @Override
    public ListCellRenderer getListCellRenderer(final AbstractListCellRendererCode code) {
        return new SubstanceLookAndFeelListCellRenderer(code);
    }

    @Override
    public boolean isDialogUndecorated() {
    	  return osManager.isMacOsX() ? false: true;
    }

    @Override
    public void putClientProperties(JComponent c) {
        if (c instanceof SecondaryControl || c instanceof MuteButton || c instanceof StopButton) {
            c.putClientProperty(org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new RoundRectButtonShaper());
        } else if (c instanceof NextButton) {
            c.putClientProperty(org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY,
                    GuiUtils.getComponentOrientation().isLeftToRight() ? new LeftConcaveButtonShaper(PlayerControlsPanel.PLAY_BUTTON_SIZE.height) : new RightConcaveButtonShaper(
                            PlayerControlsPanel.PLAY_BUTTON_SIZE.height));
        } else if (c instanceof PlayPauseButton) {
            c.putClientProperty(org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new CircleButtonShaper());
        } else if (c instanceof PreviousButton) {
            c.putClientProperty(org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY,
                    GuiUtils.getComponentOrientation().isLeftToRight() ? new RightConcaveButtonShaper(PlayerControlsPanel.PLAY_BUTTON_SIZE.height) : new LeftConcaveButtonShaper(
                            PlayerControlsPanel.PLAY_BUTTON_SIZE.height));
        }
    }

    @Override
    public boolean isCustomPlayerControlsSupported() {
        return true;
    }

    @Override
    public Border getShadowBorder() {
        return ShadowPopupBorder.getInstance();
    }

    @Override
    public void initializeFonts(final Font baseFont) {
        org.pushingpixels.substance.api.SubstanceLookAndFeel.setFontPolicy(new CustomFontPolicy(baseFont));
    }

    @Override
    public Font getDefaultFont() {
        return org.pushingpixels.substance.api.SubstanceLookAndFeel.getFontPolicy().getFontSet(SUBSTANCE, null).getControlFont();
    }        
    
    @Override
    public Paint getPaintForSpecialControls() {
    	if (org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin() instanceof ICustomSubstanceSkin) {
    		return ((ICustomSubstanceSkin)org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin()).getPaintForSpecialControls();
    	} else {
    		Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.NONE).getForegroundColor();
    		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 180);
    	}
    }

    @Override
    public Paint getPaintForDisabledSpecialControls() {
    	if (org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin() instanceof ICustomSubstanceSkin) {
    		return ((ICustomSubstanceSkin)org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin()).getPaintForDisabledSpecialControls();
    	} else {
    		Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.NONE).getForegroundColor();
    		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 140);
    	}
    }

    @Override
    public Paint getPaintForColorMutableIcon(Component component, boolean isSelected) {
    	if (org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin() instanceof ICustomSubstanceSkin) {
    		return ((ICustomSubstanceSkin)org.pushingpixels.substance.api.SubstanceLookAndFeel.getCurrentSkin()).getPaintForColorMutableIcon(component, isSelected);
    	} else {
    		return component.getForeground();    		
    	}
    }
    
    @Override
    public boolean supportsCustomFontSettings() {
    	return true;
    }    
}
