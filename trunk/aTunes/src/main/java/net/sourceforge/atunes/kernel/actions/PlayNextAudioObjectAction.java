package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

public class PlayNextAudioObjectAction extends Action {

    private static final long serialVersionUID = 2012440550238196002L;

    PlayNextAudioObjectAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PlayerHandler.getInstance().playNextAudioObject(false);
    }

    @Override
    public String getCommandName() {
        return "next";
    }

}
