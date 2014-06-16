package module.dashBoard.domain;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class DashBoardDomainException extends DomainException {

    private static final long serialVersionUID = 5952493567467569931L;

    private DashBoardDomainException(String key, String... args) {
        super(Status.PRECONDITION_FAILED, "resources.DashBoardResources", key, args);
    }

    public static DashBoardDomainException permissionDenied() {
        return new DashBoardDomainException("error.permission.denied");
    }

    public static DashBoardDomainException illegalAccessToWidget() {
        return new DashBoardDomainException("error.ilegal.access.to.widget");
    }

}
