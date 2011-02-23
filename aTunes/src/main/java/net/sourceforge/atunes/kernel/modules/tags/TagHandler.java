package net.sourceforge.atunes.kernel.modules.tags;

import java.util.List;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public class TagHandler extends AbstractHandler {

	private static TagHandler instance;
	
	public static TagHandler getInstance() {
		if (instance == null) {
			instance = new TagHandler();
		}
		return instance;
	}
	
	@Override
	public void applicationStarted(List<AudioObject> playList) {
	}

	@Override
	public void applicationFinish() {
	}

	@Override
	public void applicationStateChanged(ApplicationState newState) {
	}

	@Override
	protected void initHandler() {
	}

	public void editFiles(Album a) {
		new EditTitlesDialogController(new EditTitlesDialog(GuiHandler.getInstance().getFrame().getFrame())).editFiles(a);
	}
}
