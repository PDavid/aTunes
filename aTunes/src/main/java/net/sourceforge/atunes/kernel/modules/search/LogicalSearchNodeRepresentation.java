package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.ISearchNodeRepresentation;

/**
 * Search node representation
 * 
 * @author alex
 * 
 */
public class LogicalSearchNodeRepresentation implements
		ISearchNodeRepresentation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8595183464497489598L;

	String operator;

	List<ISearchNodeRepresentation> children;

	/**
	 * For serialization
	 */
	public LogicalSearchNodeRepresentation() {
	}

	/**
	 * @param node
	 */
	protected LogicalSearchNodeRepresentation(final LogicalSearchNode node) {
		this.operator = node.getOperator().getClass().getName();
		this.children = new ArrayList<ISearchNodeRepresentation>();
		for (ISearchNode child : node.getChildren()) {
			this.children.add(child.getRepresentation());
		}
	}

	@Override
	public ISearchNode createSearchQuery(final IBeanFactory beanFactory) {
		LogicalSearchNode node = new LogicalSearchNode();
		node.setOperator(beanFactory.getBeanByClassName(this.operator,
				ILogicalSearchOperator.class));
		for (ISearchNodeRepresentation child : this.children) {
			node.addChild(child.createSearchQuery(beanFactory));
		}
		return node;
	}
}
