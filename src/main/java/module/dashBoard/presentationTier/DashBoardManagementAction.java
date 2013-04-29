/*
 * @(#)DashBoardManagementAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Paulo Abrantes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Dashboard Module.
 *
 *   The Dashboard Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Dashboard Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Dashboard Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.dashBoard.presentationTier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.dashBoard.WidgetRegister;
import module.dashBoard.domain.DashBoardColumnBean;
import module.dashBoard.domain.DashBoardPanel;
import module.dashBoard.domain.DashBoardWidget;
import module.dashBoard.domain.UserDashBoardPanel;
import module.dashBoard.widgets.WidgetController;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.bennu.core.util.BundleUtil;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter.ChecksumPredicate;
import pt.ist.fenixWebFramework.servlets.json.JsonObject;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.fenixframework.FenixFramework;

@Mapping(path = "/dashBoardManagement")
/**
 * 
 * @author João Neves
 * @author Bruno Santos
 * @author Paulo Abrantes
 * 
 */
public class DashBoardManagementAction extends ContextBaseAction {

    private static Logger logger = LoggerFactory.getLogger(DashBoardManagementAction.class.getName());

    static {
        RequestChecksumFilter.registerFilterRule(new ChecksumPredicate() {
            @Override
            public boolean shouldFilter(HttpServletRequest httpServletRequest) {
                return !(httpServletRequest.getRequestURI().endsWith("/dashBoardManagement.do")
                        && httpServletRequest.getQueryString() != null && (httpServletRequest.getQueryString().contains(
                        "method=order")
                        || httpServletRequest.getQueryString().contains("method=requestWidgetHelp")
                        || httpServletRequest.getQueryString().contains("method=removeWidgetFromColumn")
                        || httpServletRequest.getQueryString().contains("method=viewWidget")
                        || httpServletRequest.getQueryString().contains("method=editWidget")
                        || httpServletRequest.getQueryString().contains("method=editOptions") || httpServletRequest
                        .getQueryString().contains("method=controllerDescription")));
            }
        });
    }

    public ActionForward order(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        DashBoardPanel panel = getDomainObject(request, "dashBoardId");

        List<DashBoardColumnBean> beans = new ArrayList<DashBoardColumnBean>();
        String modification = request.getParameter("ordering");
        String[] columns = modification.split(",");

        for (String column : columns) {
            String[] values = column.split(":");
            int columnIndex = Integer.valueOf(values[0]);
            DashBoardColumnBean dashBoardColumnBean = new DashBoardColumnBean(columnIndex);
            if (values.length == 2) {
                dashBoardColumnBean.setWidgets(getWidgets(values[1]));
            }
            beans.add(dashBoardColumnBean);
        }

        panel.edit(beans);
        return null;
    }

    private List<DashBoardWidget> getWidgets(String column) {
        List<DashBoardWidget> widgetsInColumn = new ArrayList<DashBoardWidget>();
        for (String externalId : column.substring(0, column.length()).split(" ")) {
            DashBoardWidget widget = FenixFramework.getDomainObject(externalId);
            widgetsInColumn.add(widget);
        }
        return widgetsInColumn;
    }

    public static ActionForward forwardToDashBoard(WidgetRequest request) {
        return forwardToDashBoard(request.getPanel(), request.getRequest());
    }

    public static ActionForward forwardToDashBoard(DashBoardPanel panel, HttpServletRequest request) {
        ActionForward forward = new ActionForward();
        forward.setRedirect(true);
        String realPath =
                "/dashBoardManagement.do?method=viewDashBoardPanel&dashBoardId=" + panel.getExternalId() + "&" + CONTEXT_PATH
                        + "=" + getContext(request).getPath();
        forward.setPath(realPath + "&" + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "="
                + GenericChecksumRewriter.calculateChecksum(request.getContextPath() + realPath));
        return forward;
    }

    public ActionForward viewWidget(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        User currentUser = UserView.getCurrentUser();

        if (!checkPanelUser((UserDashBoardPanel) widget.getDashBoardPanel(), currentUser)) {
            return null;
        }

        widget.getWidgetController().doView(new WidgetRequest(request, response, widget, currentUser));

        return forwardToWidget(widget, request, response);
    }

    public ActionForward editWidget(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        User currentUser = UserView.getCurrentUser();

        if (!checkPanelUser((UserDashBoardPanel) widget.getDashBoardPanel(), currentUser)) {
            return null;
        }

        widget.getWidgetController().doEdit(new WidgetRequest(request, response, widget, currentUser));

        return forwardToWidget(widget, request, response);
    }

    public ActionForward editOptions(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        User currentUser = UserView.getCurrentUser();

        if (!checkPanelUser((UserDashBoardPanel) widget.getDashBoardPanel(), currentUser)) {
            return null;
        }

        widget.getWidgetController().doEditOptions(new WidgetRequest(request, response, widget, currentUser));

        return forwardToWidget(widget, request, response);
    }

    private static boolean checkPanelUser(UserDashBoardPanel panel, User currentUser) {
        if (panel.getUser() != currentUser) {
            if (logger.isWarnEnabled()) {
                logger.warn("Current user (" + (currentUser != null ? currentUser.getUsername() : "null")
                        + ") is not the owner of the Panel (" + panel.getUser().getUsername() + ")");
            }
            return false;
        }
        return true;
    }

    public static ActionForward forwardToWidget(WidgetRequest request) {
        return forwardToWidget(request.getWidget(), request.getRequest(), request.getResponse());
    }

    private static ActionForward forwardToWidget(DashBoardWidget widget, final HttpServletRequest request,
            final HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        request.setAttribute("widget", widget);
        WidgetLayoutContext context = new WidgetLayoutContext(widget);
        return context.forward();
    }

    public ActionForward viewDashBoardPanel(final DashBoardPanel panel, final HttpServletRequest request,
            final HttpServletResponse response) {
        request.setAttribute("dashBoard", panel);
        return forward(request, "/dashBoardPanel/viewDashBoard.jsp");
    }

    public ActionForward viewDashBoardPanel(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        DashBoardPanel panel = getDomainObject(request, "dashBoardId");
        return viewDashBoardPanel(panel, request, response);
    }

    public ActionForward removeWidgetFromColumn(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        JsonObject jsonObject = new JsonObject();
        try {
            DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
            widget.delete();
            jsonObject.addAttribute("status", "OK");
        } catch (Exception e) {
            jsonObject.addAttribute("status", "NOT_OK");
        }
        writeJsonReply(response, jsonObject);
        return null;
    }

    public ActionForward prepareAddWidget(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        DashBoardPanel panel = getDomainObject(request, "dashBoardId");
        request.setAttribute("dashBoard", panel);
        request.setAttribute("widgets", WidgetRegister.getAvailableWidgets(panel, UserView.getCurrentUser()));
        return forward(request, "/dashBoardPanel/addWidget.jsp");
    }

    public ActionForward addWidget(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        DashBoardPanel panel = getDomainObject(request, "dashBoardId");
        String widgetClassName = request.getParameter("dashBoardWidgetClass");
        Class<? extends WidgetController> className = null;
        try {
            className = (Class<? extends WidgetController>) Class.forName(widgetClassName);
            DashBoardWidget widget = DashBoardWidget.newWidget(className);
            panel.addWidgetToColumn(0, widget);
        } catch (Exception e) {
            addMessage(request, "error.addingWidget");
            e.printStackTrace();
            return viewDashBoardPanel(panel, request, response);
        }

        return forwardToDashBoard(panel, request);
    }

    public ActionForward widgetSubmition(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        return widget.getWidgetController().doSubmit(new WidgetRequest(request, response, widget, UserView.getCurrentUser()));
    }

    public ActionForward requestWidgetHelp(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {

        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        DashBoardPanel panel = widget.getDashBoardPanel();

        JsonObject jsonObject = new JsonObject();

        if (panel.isAccessibleToCurrentUser()) {
            jsonObject.addAttribute("helpText", widget.getWidgetController().getHelp());
        }

        writeJsonReply(response, jsonObject);
        return null;
    }

    public ActionForward controllerDescription(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws IOException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {

        String className = request.getParameter("dashBoardWidgetClass");
        Class<? extends WidgetController> controllerClass = (Class<? extends WidgetController>) Class.forName(className);
        JsonObject jsonObject = new JsonObject();

        if (controllerClass != null) {
            WidgetController controller = controllerClass.newInstance();
            jsonObject.addAttribute("description", controller.getWidgetDescription());
            jsonObject.addAttribute("name", BundleUtil.getLocalizedNamedFroClass(controllerClass));
        }

        writeJsonReply(response, jsonObject);
        return null;
    }
}
