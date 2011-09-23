/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.utils.ClipboardFacade;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Common popup menu for text components with cut, copy, paste... options
 * 
 * @author fleax
 */
public class EditionPopUpMenu extends JPopupMenu {

    private static final long serialVersionUID = -3103254879422133063L;

    /**
     * Text component associated to this menu
     */
    private JTextComponent textComponent;

    /**
     * Default constructor
     */
    public EditionPopUpMenu(JTextComponent textComponent) {
        super();
        this.textComponent = textComponent;
        addPopUpMenu();
    }

    /**
     * Adds popup menu with edition options
     */
    private void addPopUpMenu() {
        final Action cutAction = new CutAction(this.textComponent);
        final Action copyAction = new CopyAction(this.textComponent);
        final Action pasteAction = new PasteAction(this.textComponent);
        final Action deleteAction = new DeleteAction(this.textComponent);
        final Action selectAllAction = new SelectAllAction(this.textComponent);
        add(cutAction);
        add(copyAction);
        add(pasteAction);
        add(deleteAction);
        add(new JSeparator());
        add(selectAllAction);

        this.textComponent.addMouseListener(new TextComponentMouseAdapter(copyAction, deleteAction, selectAllAction, pasteAction, cutAction));
    }

    private final class TextComponentMouseAdapter extends MouseAdapter {
        private final Action copyAction;
        private final Action deleteAction;
        private final Action selectAllAction;
        private final Action pasteAction;
        private final Action cutAction;

        private TextComponentMouseAdapter(Action copyAction, Action deleteAction, Action selectAllAction, Action pasteAction, Action cutAction) {
            this.copyAction = copyAction;
            this.deleteAction = deleteAction;
            this.selectAllAction = selectAllAction;
            this.pasteAction = pasteAction;
            this.cutAction = cutAction;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (GuiUtils.isSecondaryMouseButton(e) && e.getComponent().isEnabled()) {
                // Cut and delete if text selected and component editable
                boolean textSelected = EditionPopUpMenu.this.textComponent.getSelectionStart() < EditionPopUpMenu.this.textComponent.getSelectionEnd();
                cutAction.setEnabled(textSelected && EditionPopUpMenu.this.textComponent.isEditable());
                deleteAction.setEnabled(textSelected && EditionPopUpMenu.this.textComponent.isEditable());

                // Copy if text selected
                copyAction.setEnabled(textSelected);

                // Paste if clipboard contains text and component editable
                pasteAction.setEnabled(Context.getBean(ClipboardFacade.class).clipboardContainsText() && EditionPopUpMenu.this.textComponent.isEditable());

                // Select all if text field contains text
                selectAllAction.setEnabled(EditionPopUpMenu.this.textComponent.getText().length() > 0);

                // Show menu
                show(EditionPopUpMenu.this.textComponent, e.getX(), e.getY());
            }
        }
    }

    /**
     * Cut action
     */
    private static class CutAction extends AbstractAction {

        private static final long serialVersionUID = -499875218309361915L;

        /**
         * Text component associated
         */
        private JTextComponent textComponent;

        public CutAction(JTextComponent textComponent) {
            super(I18nUtils.getString("CUT"));
            this.textComponent = textComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = this.textComponent.getText();
            String selectedText = text.substring(this.textComponent.getSelectionStart(), this.textComponent.getSelectionEnd());
            String nonSelectedText = StringUtils.getString(text.substring(0, this.textComponent.getSelectionStart()), text.substring(this.textComponent.getSelectionEnd()));
            Context.getBean(ClipboardFacade.class).copyToClipboard(selectedText);
            this.textComponent.setText(nonSelectedText);
        }
    }

    /**
     * Copy action
     */
    private static class CopyAction extends AbstractAction {

        private static final long serialVersionUID = -2366530850960686780L;

        /**
         * Text component associated
         */
        private JTextComponent textComponent;

        public CopyAction(JTextComponent textComponent) {
            super(I18nUtils.getString("COPY"));
            this.textComponent = textComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        	Context.getBean(ClipboardFacade.class).copyToClipboard(this.textComponent.getSelectedText());
        }
    }

    /**
     * Paste action
     */
    private static class PasteAction extends AbstractAction {

        private static final long serialVersionUID = -7600198128040448381L;

        /**
         * Text component associated
         */
        private JTextComponent textComponent;

        public PasteAction(JTextComponent textComponent) {
            super(I18nUtils.getString("PASTE"));
            this.textComponent = textComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = this.textComponent.getText();
            String copiedText = Context.getBean(ClipboardFacade.class).getClipboardContent();
            String newText = StringUtils.getString(text.substring(0, this.textComponent.getSelectionStart()), copiedText, text.substring(this.textComponent.getSelectionEnd()));
            this.textComponent.setText(newText);
        }
    }

    /**
     * Delete action
     */
    private static class DeleteAction extends AbstractAction {

        private static final long serialVersionUID = 802324331526664365L;

        /**
         * Text component associated
         */
        private JTextComponent textComponent;

        public DeleteAction(JTextComponent textComponent) {
            super(I18nUtils.getString("DELETE"));
            this.textComponent = textComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = this.textComponent.getText();
            String nonSelectedText = StringUtils.getString(text.substring(0, this.textComponent.getSelectionStart()), text.substring(this.textComponent.getSelectionEnd()));
            this.textComponent.setText(nonSelectedText);
        }
    }

    /**
     * Select all action
     */
    private static class SelectAllAction extends AbstractAction {

        private static final long serialVersionUID = 9194366269589907916L;

        /**
         * Text component associated
         */
        private JTextComponent textComponent;

        public SelectAllAction(JTextComponent textComponent) {
            super(I18nUtils.getString("SELECT_ALL"));
            this.textComponent = textComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.textComponent.setSelectionStart(0);
            this.textComponent.setSelectionEnd(this.textComponent.getText().length());
        }
    }

}
