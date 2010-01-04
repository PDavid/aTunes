package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Component;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
import net.sourceforge.atunes.utils.GuiUtils;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.utils.LafConstants;
import org.jvnet.substance.api.SubstanceConstants;
import org.jvnet.substance.api.renderers.SubstanceDefaultListCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;


public class SubstanceLookAndFeel extends LookAndFeel {
	
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
            getLogger().internalError(e);
        } catch (InstantiationException e) {
        	getLogger().internalError(e);
        } catch (IllegalAccessException e) {
        	getLogger().internalError(e);
        } catch (UnsupportedLookAndFeelException e) {
        	getLogger().internalError(e);
        }
	}
	
	@Override
	public String getDefaultSkin() {
		return DEFAULT_SKIN;
	}
	
	@Override
	public List<String> getSkins() {
        List<String> result = new ArrayList<String>(skins.keySet());
        Collections.sort(result, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        return result;
    }
	
	@Override
	public TreeCellRenderer getTreeCellRenderer(final TreeCellRendererCode code) {
		return new SubstanceDefaultTreeCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3830003466764008228L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				return code.getComponent(c, tree, value, sel, expanded, leaf, row, hasFocus);
			}
		};
	}
	
	/**
	 * Returns a new TableCellRenderer executing given code (default implementation)
	 * @param code
	 * @return
	 */
	public TableCellRenderer getTableCellRenderer(final TableCellRendererCode code) {
		return new SubstanceDefaultTableCellRenderer() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 2844251523912028654L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
			}
		};		
	}
	
	/**
	 * Returns a new TableCellRenderer executing given code (default implementation)
	 * @param code
	 * @return
	 */
	public TableCellRenderer getTableHeaderCellRenderer(final TableCellRendererCode code) {
		return new SubstanceDefaultTableHeaderCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
			}
		};		
	}

	
	@Override
	public ListCellRenderer getListCellRenderer(final ListCellRendererCode code) {
		return new SubstanceDefaultListCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2572603555660744197L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				return code.getComponent(c, list, value, index, isSelected, cellHasFocus);
			}
		};
	}
	
	@Override
	public boolean isDialogUndecorated() {
		return true;
	}
	
	@Override
	public void putClientProperties(JComponent c) {
		if (c instanceof SecondaryControl || 
			c instanceof MuteButton || 
			c instanceof StopButton) {
	        c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new RoundRectButtonShaper());
		} else if (c instanceof NextButton) {
	        c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, 
	        		GuiUtils.getComponentOrientation().isLeftToRight() ? new LeftConcaveButtonShaper(
	                PlayerControlsPanel.PLAY_BUTTON_SIZE.height) : new RightConcaveButtonShaper(PlayerControlsPanel.PLAY_BUTTON_SIZE.height));
		} else if (c instanceof PlayPauseButton) {
	        c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new CircleButtonShaper());
		} else if (c instanceof PreviousButton) {
	        c.putClientProperty(org.jvnet.substance.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, 
	        		GuiUtils.getComponentOrientation().isLeftToRight() ? new RightConcaveButtonShaper(
	                PlayerControlsPanel.PLAY_BUTTON_SIZE.height) : new LeftConcaveButtonShaper(PlayerControlsPanel.PLAY_BUTTON_SIZE.height));
		}
	}
	
	@Override
	public boolean isCustomPlayerControlsSupported() {
		return true;
	}
}
