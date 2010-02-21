/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Component;
import java.awt.Font;
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

import net.sourceforge.atunes.gui.lookandfeel.ListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.GuiUtils;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.utils.LafConstants;
import org.jvnet.lafwidget.utils.ShadowPopupBorder;
import org.jvnet.substance.api.SubstanceConstants;
import org.jvnet.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;
import org.jvnet.substance.fonts.FontPolicy;
import org.jvnet.substance.fonts.FontSet;

public class SubstanceLookAndFeel extends LookAndFeel {

    private static class CustomFontPolicy implements FontPolicy {
		private final Font baseFont;

		private CustomFontPolicy(Font baseFont) {
			this.baseFont = baseFont;
		}

		@Override
		public FontSet getFontSet(String arg0, UIDefaults arg1) {
		    return new FontSet() {

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
		    };
		}
	}

	private static class SubstanceLookAndFeelListCellRenderer extends
			SubstanceDefaultListCellRenderer {
		private final ListCellRendererCode code;
		/**
		 * 
		 */
		private static final long serialVersionUID = 2572603555660744197L;

		private SubstanceLookAndFeelListCellRenderer(ListCellRendererCode code) {
			this.code = code;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    return code.getComponent(c, list, value, index, isSelected, cellHasFocus);
		}
	}

	private static class SubstanceLookAndFeelTableHeaderCellRenderer extends
			SubstanceDefaultTableHeaderCellRenderer {
		private final TableCellRendererCode code;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private SubstanceLookAndFeelTableHeaderCellRenderer(
				TableCellRendererCode code) {
			this.code = code;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		    return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
		}
	}

	private static class SubstanceLookAndFeelTableCellRenderer extends
			SubstanceDefaultTableCellRenderer {
		private final TableCellRendererCode code;
		/**
		 * 
		 */
		private static final long serialVersionUID = 2844251523912028654L;

		private SubstanceLookAndFeelTableCellRenderer(TableCellRendererCode code) {
			this.code = code;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		    return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
		}
	}

	private static class SubstanceLookAndFeelTreeCellRenderer extends
			SubstanceDefaultTreeCellRenderer {
		private final TreeCellRendererCode code;
		/**
		 * 
		 */
		private static final long serialVersionUID = 3830003466764008228L;

		private SubstanceLookAndFeelTreeCellRenderer(TreeCellRendererCode code) {
			this.code = code;
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		    Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		    return code.getComponent(c, tree, value, sel, expanded, leaf, row, hasFocus);
		}
	}

	private static class SkinsComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
		    return o1.toLowerCase().compareTo(o2.toLowerCase());
		}
	}

	/** The map of skin names and class names */
    private static Map<String, String> skins = setMapOfSkins();

    /** The default skin */
    private static final String DEFAULT_SKIN = "aTunes Blue";

    /**
     * Sets the list of skins.
     * 
     * @return the map< string, string>
     */
    private static Map<String, String> setMapOfSkins() {
        Map<String, String> result = new HashMap<String, String>();

        result.put("BusinessBlackSteel", "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
        result.put("Creme", "org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
        result.put("Business", "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
        result.put("BusinessBlueSteel", "org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
        result.put("CremeCoffee", "org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel");
        result.put("Sahara", "org.jvnet.substance.skin.SubstanceSaharaLookAndFeel");
        result.put("Moderate", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel");
        result.put("OfficeSilver2007", "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel");
        result.put("Nebula", "org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
        result.put("NebulaBrickWall", "org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel");
        result.put("Autumn", "org.jvnet.substance.skin.SubstanceAutumnLookAndFeel");
        result.put("MistSilver", "org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel");
        result.put("MistAqua", "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel");
        result.put("DustCoffee", "org.jvnet.substance.skin.SubstanceDustCoffeeLookAndFeel");
        result.put("Dust", "org.jvnet.substance.skin.SubstanceDustLookAndFeel");
        result.put("RavenGraphite", "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");
        result.put("RavenGraphiteGlass", "org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
        result.put("Raven", "org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
        result.put("Magma", "org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
        result.put("ChallengerDeep", "org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel");
        result.put("EmeraldDusk", "org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel");
        result.put("Twilight", "org.jvnet.substance.skin.SubstanceTwilightLookAndFeel");
        result.put("OfficeBlue2007", "org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel");
        result.put("Gemini", "org.jvnet.substance.api.skin.SubstanceGeminiLookAndFeel");
        result.put("Magellan", "org.jvnet.substance.api.skin.SubstanceMagellanLookAndFeel");
        result.put("GraphiteAqua", "org.jvnet.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");

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
        return "Substance";
    }

    @Override
    public String getDescription() {
        return "Substance Look And Feel";
    }

    @Override
    public void initializeLookAndFeel() {
        UIManager.put(LafWidget.ANIMATION_KIND, LafConstants.AnimationKind.NONE);
        UIManager.put(org.jvnet.substance.SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    @Override
    public void setLookAndFeel(String skin) {
        try {
            if (skins.containsKey(skin)) {
                UIManager.setLookAndFeel(skins.get(skin));
            } else {
                UIManager.setLookAndFeel(skins.get(DEFAULT_SKIN));
            }

            // Get border color
            GuiUtils.setBorderColor(org.jvnet.substance.SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getMidColor());

        } catch (ClassNotFoundException e) {
        	new Logger().internalError(e);
        } catch (InstantiationException e) {
        	new Logger().internalError(e);
        } catch (IllegalAccessException e) {
        	new Logger().internalError(e);
        } catch (UnsupportedLookAndFeelException e) {
        	new Logger().internalError(e);
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
    public TreeCellRenderer getTreeCellRenderer(final TreeCellRendererCode code) {
        return new SubstanceLookAndFeelTreeCellRenderer(code);
    }

    /**
     * Returns a new TableCellRenderer executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    public TableCellRenderer getTableCellRenderer(final TableCellRendererCode code) {
        return new SubstanceLookAndFeelTableCellRenderer(code);
    }

    /**
     * Returns a new TableCellRenderer executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    public TableCellRenderer getTableHeaderCellRenderer(final TableCellRendererCode code) {
        return new SubstanceLookAndFeelTableHeaderCellRenderer(code);
    }

    @Override
    public ListCellRenderer getListCellRenderer(final ListCellRendererCode code) {
        return new SubstanceLookAndFeelListCellRenderer(code);
    }

    @Override
    public boolean isDialogUndecorated() {
        return true;
    }

    @Override
    public void putClientProperties(JComponent c) {
        if (c instanceof SecondaryControl || c instanceof MuteButton || c instanceof StopButton) {
            c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new RoundRectButtonShaper());
        } else if (c instanceof NextButton) {
            c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, GuiUtils.getComponentOrientation().isLeftToRight() ? new LeftConcaveButtonShaper(
                    PlayerControlsPanel.PLAY_BUTTON_SIZE.height) : new RightConcaveButtonShaper(PlayerControlsPanel.PLAY_BUTTON_SIZE.height));
        } else if (c instanceof PlayPauseButton) {
            c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new CircleButtonShaper());
        } else if (c instanceof PreviousButton) {
            c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, GuiUtils.getComponentOrientation().isLeftToRight() ? new RightConcaveButtonShaper(
                    PlayerControlsPanel.PLAY_BUTTON_SIZE.height) : new LeftConcaveButtonShaper(PlayerControlsPanel.PLAY_BUTTON_SIZE.height));
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
        org.jvnet.substance.SubstanceLookAndFeel.setFontPolicy(new CustomFontPolicy(baseFont));
    }

    @Override
    public Font getDefaultFont() {
        return org.jvnet.substance.SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null).getControlFont();
    }
}
