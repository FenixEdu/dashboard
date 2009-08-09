package module.dashBoard.domain;

import java.util.Comparator;

import module.dashBoard.widgets.WidgetController;
import myorg.domain.exceptions.DomainException;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.Transaction;

public class DashBoardWidget extends DashBoardWidget_Base {

    public static final Comparator<DashBoardWidget> IN_COLUMN_COMPARATOR = new Comparator<DashBoardWidget>() {

	@Override
	public int compare(DashBoardWidget widget1, DashBoardWidget widget2) {
	    return Integer.valueOf(widget1.getOrderInColumn()).compareTo(widget2.getOrderInColumn());
	}

    };

    private WidgetController instance;

    public DashBoardWidget(Class<? extends WidgetController> widgetClass) {
	super();
	setDashBoardController(DashBoardController.getInstance());
	setWidgetClassName(widgetClass.getName());
	getWidgetController().init(this);
	setOrderInColumn(0);
    }

    public WidgetController getWidgetController() {
	if (getDashBoardColumn() != null && !getDashBoardPanel().isAccessibleToCurrentUser()) {
	    throw new DomainException("error.ilegal.access.to.widget");
	}
	if (instance == null) {
	    initializeWidget();
	}
	return instance;
    }

    private synchronized void initializeWidget() {
	try {
	    instance = (WidgetController) Class.forName(getWidgetClassName()).newInstance();
	    instance.init(this);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Service
    public void delete() {
	removeDashBoardColumn();
	removeDashBoardController();
	getWidgetController().kill(this);
	Transaction.deleteObject(this);
    }

    public <T extends DomainObject> T getStateObject() {
	return getStateObjectId() != null ? (T) AbstractDomainObject.fromExternalId(getStateObjectId()) : null;
    }

    public void setStateObject(DomainObject domainObject) {
	setStateObjectId(domainObject.getExternalId());
    }

    public boolean isEditionModeSupported() {
	return getWidgetController().isEditionModeSupported();
    }

    public DashBoardPanel getDashBoardPanel() {
	return getDashBoardColumn().getDashBoardPanel();
    }

    @Service
    public static DashBoardWidget newWidget(Class<? extends WidgetController> widgetClass) {
	return new DashBoardWidget(widgetClass);
    }

}
