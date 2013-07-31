package net.sourceforge.atunes.kernel.modules.search;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.ISearchNodeRepresentation;
import net.sourceforge.atunes.model.ISearchOperator;

/**
 * @author alex
 * 
 */
public class SearchRuleRepresentation implements ISearchNodeRepresentation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7249890492370285688L;

	String field;

	String operator;

	String value;

	/**
	 * Default constructor for serialization
	 */
	public SearchRuleRepresentation() {
	}

	/**
	 * @param rule
	 */
	protected SearchRuleRepresentation(final SearchRule rule) {
		this.field = rule.getField().getClass().getName();
		this.operator = rule.getOperator().getClass().getName();
		this.value = rule.getValue();
	}

	@Override
	public ISearchNode createSearchQuery(final IBeanFactory beanFactory) {
		return new SearchRule(beanFactory.getBeanByClassName(this.field,
				ISearchField.class), beanFactory.getBeanByClassName(
				this.operator, ISearchOperator.class), this.value);
	}
}
