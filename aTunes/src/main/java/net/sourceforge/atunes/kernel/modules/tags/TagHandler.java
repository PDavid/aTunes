package net.sourceforge.atunes.kernel.modules.tags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.EditTagAction.EditTagSources;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public class TagHandler extends AbstractHandler {

	private static TagHandler instance;
	
    /** The edit tag dialog controller. */
    private Map<EditTagSources, EditTagDialogController> editTagDialogControllerMap;

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
	
    /**
     * Gets the edits the tag dialog controller.
     * 
     * @return the edits the tag dialog controller
     */
    public EditTagDialogController getEditTagDialogController(EditTagSources sourceOfEditTagDialog) {
        if (editTagDialogControllerMap == null) {
            editTagDialogControllerMap = new HashMap<EditTagSources, EditTagDialogController>();
        }

        if (!editTagDialogControllerMap.containsKey(sourceOfEditTagDialog)) {
            boolean arePrevNextButtonsShown = sourceOfEditTagDialog != EditTagSources.NAVIGATOR;
            editTagDialogControllerMap.put(sourceOfEditTagDialog, new EditTagDialogController(new EditTagDialog(GuiHandler.getInstance().getFrame().getFrame(), arePrevNextButtonsShown)));
        }
        return editTagDialogControllerMap.get(sourceOfEditTagDialog);
    }

	public void editFiles(EditTagSources navigator, List<AudioFile> asList) {
		getEditTagDialogController(navigator).editFiles(asList);
	}
}
