package net.sourceforge.atunes.gui.views.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ToolBarFilterPanel extends JPanel {

    private static final long serialVersionUID = 1801321624657098000L;

    private PopUpButton filterButton;
    private JTextField filterTextField;
    private JButton clearFilterButton;

    public ToolBarFilterPanel() {
        super(new GridBagLayout());
        addContent();
    }

    private void addContent() {
        filterButton = new PopUpButton(StringUtils.getString(I18nUtils.getString("FILTER"), "..."), PopUpButton.BOTTOM_RIGHT);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(filterButton, c);
        filterTextField = new JTextField(20);
        filterTextField.setToolTipText(I18nUtils.getString("FILTER_TEXTFIELD_TOOLTIP"));
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        add(filterTextField, c);
        clearFilterButton = new JButton(ImageLoader.getImage(ImageLoader.UNDO));
        clearFilterButton.setToolTipText(I18nUtils.getString("CLEAR_FILTER_BUTTON_TOOLTIP"));
        c.gridx = 2;
        add(clearFilterButton, c);
    }

    /**
     * @return the filterButton
     */
    public PopUpButton getFilterButton() {
        return filterButton;
    }

    /**
     * @return the filterTextField
     */
    public JTextField getFilterTextField() {
        return filterTextField;
    }

}
