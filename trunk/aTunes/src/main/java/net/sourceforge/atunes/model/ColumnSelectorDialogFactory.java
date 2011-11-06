package net.sourceforge.atunes.model;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ColumnSelectorDialogFactory implements IColumnSelectorDialogFactory, ApplicationContextAware {

	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public IColumnSelectorDialog createDialog() {
		return this.context.getBean(IColumnSelectorDialog.class);
	}

}
