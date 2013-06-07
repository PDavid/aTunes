/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.gui.views.controls.CustomStatusBar;
import net.sourceforge.atunes.gui.views.controls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.NextButton;
import net.sourceforge.atunes.gui.views.controls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.SecondaryToggleControl;
import net.sourceforge.atunes.gui.views.controls.StopButton;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumnModel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IListCellRendererCode;
import net.sourceforge.atunes.model.ITableCellRendererCode;
import net.sourceforge.atunes.model.ITreeCellRendererCode;
import net.sourceforge.atunes.utils.Logger;

import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.utils.ShadowPopupBorder;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceConstants;

/**
 * Substance look and feel
 * 
 * @author alex
 * 
 */
public final class SubstanceLookAndFeel extends AbstractLookAndFeel {

	/**
	 * Name of look and feel
	 */
	public static final String SUBSTANCE = "Substance";

	private Object menuBarUI;

	/** The map of skin names and class names */
	private final Map<String, String> skins = setMapOfSkins();

	/** The default skin */
	private static final String DEFAULT_SKIN = "aTunes New";

	private Dimension playButtonSize;

	private ComponentOrientation componentOrientation;

	private IBeanFactory beanFactory;

	/**
	 * Sets the list of skins.
	 * 
	 * @return the map< string, string>
	 */
	private Map<String, String> setMapOfSkins() {
		Map<String, String> result = new HashMap<String, String>();

		result.put("BusinessBlackSteel",
				"org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel");
		result.put("Creme",
				"org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel");
		result.put("Business",
				"org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel");
		result.put("BusinessBlueSteel",
				"org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
		result.put("CremeCoffee",
				"org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel");
		result.put("Sahara",
				"org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel");
		result.put("Moderate",
				"org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
		result.put("OfficeSilver2007",
				"org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel");
		result.put("Nebula",
				"org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel");
		result.put("NebulaBrickWall",
				"org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel");
		result.put("Autumn",
				"org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel");
		result.put("MistSilver",
				"org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel");
		result.put("MistAqua",
				"org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel");
		result.put("DustCoffee",
				"org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel");
		result.put("Dust",
				"org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
		result.put("Raven",
				"org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel");
		result.put("ChallengerDeep",
				"org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel");
		result.put("EmeraldDusk",
				"org.pushingpixels.substance.api.skin.SubstanceEmeraldDuskLookAndFeel");
		// Custom Twilight look and feel
		result.put("Twilight",
				"net.sourceforge.atunes.gui.lookandfeel.substance.CustomTwilightLookAndFeel");
		result.put("OfficeBlue2007",
				"org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel");
		result.put("Gemini",
				"org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel");
		result.put("Magellan",
				"org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel");
		result.put("GraphiteAqua",
				"org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
		result.put("GraphiteGlass",
				"org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel");
		result.put("Graphite",
				"org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");

		/*
		 * custom skins
		 */
		result.put(
				"aTunes Blue",
				"net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceATunesBlueLookAndFeel");
		result.put(
				"aTunes Dark",
				"net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceATunesDarkLookAndFeel");
		result.put(
				"aTunes Gray",
				"net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceATunesGrayLookAndFeel");
		result.put(
				"aTunes New",
				"net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceATunesNewLookAndFeel");

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
	public void initializeLookAndFeel(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.playButtonSize = beanFactory.getBean("playButtonSize",
				Dimension.class);
		this.componentOrientation = beanFactory.getBean(IControlsBuilder.class)
				.getComponentOrientation();
		AnimationConfigurationManager.getInstance().setTimelineDuration(0);
		UIManager
				.put(org.pushingpixels.substance.api.SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND,
						SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL);

		// Disable rounded corners as causes problems with early 1.6.0 virtual
		// machines
		UIManager
				.put(org.pushingpixels.substance.api.SubstanceLookAndFeel.WINDOW_ROUNDED_CORNERS,
						Boolean.FALSE);

		if (!getOsManager().isMacOsX()) {
			// Avoid custom window decoration in mac os to draw window controls
			// at left
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		} else {
			// This is a trick to draw menu at Mac OS X menu bar
			// Taken from
			// http://www.pushing-pixels.org/2008/07/13/swing-applications-and-mac-os-x-menu-bar.html
			// However original method does this with other components, but
			// doing it causes exceptions in Substance classes
			this.menuBarUI = UIManager.get("MenuBarUI");
		}
	}

	@Override
	public void setLookAndFeel(final String skin) {
		try {
			if (this.skins.containsKey(skin)) {
				UIManager.setLookAndFeel(this.skins.get(skin));
			} else {
				UIManager.setLookAndFeel(this.skins.get(DEFAULT_SKIN));
			}

			if (getOsManager().isMacOsX()) {
				UIManager.put("MenuBarUI", this.menuBarUI);
			}

			// Get border color
			GuiUtils.setBorderColor(org.pushingpixels.substance.api.SubstanceLookAndFeel
					.getCurrentSkin()
					.getActiveColorScheme(DecorationAreaType.GENERAL)
					.getMidColor());
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
		List<String> result = new ArrayList<String>(this.skins.keySet());
		Collections.sort(result, new SkinsComparator());
		return result;
	}

	@Override
	public TreeCellRenderer getTreeCellRenderer(
			final ITreeCellRendererCode<?, ?> code) {
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
	public TableCellRenderer getTableCellRenderer(
			final ITableCellRendererCode<?, ?> code) {
		return new SubstanceLookAndFeelTableCellRenderer(code);
	}

	@Override
	public TableCellRenderer getTableHeaderCellRenderer(final IColumnModel model) {
		SubstanceLookAndFeelTableHeaderCellRenderer renderer = this.beanFactory
				.getBean(SubstanceLookAndFeelTableHeaderCellRenderer.class);
		renderer.bindToModel(model);
		return renderer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ListCellRenderer getListCellRenderer(
			final IListCellRendererCode<?, ?> code) {
		return new SubstanceLookAndFeelListCellRenderer(code);
	}

	@Override
	public boolean isDialogUndecorated() {
		return getOsManager().isMacOsX() ? false : true;
	}

	@Override
	public void putClientProperties(final JComponent c) {
		if (c instanceof SecondaryToggleControl
				|| c instanceof SecondaryControl || c instanceof MuteButton
				|| c instanceof StopButton) {
			c.putClientProperty(
					org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY,
					new RoundRectButtonShaper());
		} else if (c instanceof NextButton) {
			c.putClientProperty(
					org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY,
					this.componentOrientation.isLeftToRight() ? new LeftConcaveButtonShaper(
							this.playButtonSize.height)
							: new RightConcaveButtonShaper(
									this.playButtonSize.height));
		} else if (c instanceof PlayPauseButton) {
			c.putClientProperty(
					org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY,
					new CircleButtonShaper());
		} else if (c instanceof PreviousButton) {
			c.putClientProperty(
					org.pushingpixels.substance.api.SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY,
					this.componentOrientation.isLeftToRight() ? new RightConcaveButtonShaper(
							this.playButtonSize.height)
							: new LeftConcaveButtonShaper(
									this.playButtonSize.height));
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
		org.pushingpixels.substance.api.SubstanceLookAndFeel
				.setFontPolicy(new CustomFontPolicy(baseFont));
	}

	@Override
	public Font getDefaultFont() {
		return org.pushingpixels.substance.api.SubstanceLookAndFeel
				.getFontPolicy().getFontSet(SUBSTANCE, null).getControlFont();
	}

	@Override
	public Color getPaintForSpecialControls() {
		if (org.pushingpixels.substance.api.SubstanceLookAndFeel
				.getCurrentSkin() instanceof ICustomSubstanceSkin) {
			return ((ICustomSubstanceSkin) org.pushingpixels.substance.api.SubstanceLookAndFeel
					.getCurrentSkin()).getPaintForSpecialControls();
		} else {
			Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel
					.getCurrentSkin()
					.getActiveColorScheme(DecorationAreaType.NONE)
					.getForegroundColor();
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), 180);
		}
	}

	@Override
	public Color getPaintForDisabledSpecialControls() {
		if (org.pushingpixels.substance.api.SubstanceLookAndFeel
				.getCurrentSkin() instanceof ICustomSubstanceSkin) {
			return ((ICustomSubstanceSkin) org.pushingpixels.substance.api.SubstanceLookAndFeel
					.getCurrentSkin()).getPaintForDisabledSpecialControls();
		} else {
			Color c = org.pushingpixels.substance.api.SubstanceLookAndFeel
					.getCurrentSkin()
					.getActiveColorScheme(DecorationAreaType.NONE)
					.getForegroundColor();
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), 140);
		}
	}

	@Override
	public Color getPaintForColorMutableIcon(final Component component,
			final boolean isSelected) {
		if (org.pushingpixels.substance.api.SubstanceLookAndFeel
				.getCurrentSkin() instanceof ICustomSubstanceSkin) {
			return ((ICustomSubstanceSkin) org.pushingpixels.substance.api.SubstanceLookAndFeel
					.getCurrentSkin()).getPaintForColorMutableIcon(component,
					isSelected);
		} else {
			return component.getForeground();
		}
	}

	@Override
	public boolean supportsCustomFontSettings() {
		return true;
	}

	@Override
	public JScrollPane getTableScrollPane(final JTable table) {
		return customizeScrollPane(super.getTableScrollPane(table));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public JScrollPane getListScrollPane(final JList list) {
		return customizeScrollPane(super.getListScrollPane(list));
	}

	@Override
	public JScrollPane getTreeScrollPane(final JTree tree) {
		return customizeScrollPane(super.getTreeScrollPane(tree));
	}

	@Override
	public JScrollPane getScrollPane(final Component component) {
		return customizeScrollPane(super.getScrollPane(component));
	}

	private JScrollPane customizeScrollPane(final JScrollPane scrollPane) {
		// scrollPane.setBorder(BorderFactory.createEmptyBorder());
		return scrollPane;
	}

	@Override
	public void customizeSplitPane(final JSplitPane splitPane) {
		splitPane.setDividerSize(3);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setUI(new BasicSplitPaneUI());
	}

	@Override
	public void customizeStatusBar(final CustomStatusBar customStatusBar) {
		// Use BasicStatusBarUI to avoid border added by
		// SubstanceStatusBarUI
		customStatusBar.setUI(new BasicStatusBarUI());
	}

	@Override
	public Font getSuggestedFont() {
		return new Font("Lucida Grande", Font.PLAIN, 11);
	}
}
