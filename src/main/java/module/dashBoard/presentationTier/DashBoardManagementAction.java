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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.dashBoard.domain.DashBoardColumnBean;
import module.dashBoard.domain.DashBoardPanel;
import module.dashBoard.domain.DashBoardWidget;
import module.dashBoard.servlet.WidgetRegistry;
import module.dashBoard.widgets.WidgetController;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.servlet.PortalLayoutInjector;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.base.BaseAction;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonObject;

@Mapping(path = "/dashBoardManagement")
@StrutsApplication(bundle = "DashBoardResources", path = "dashboard", titleKey = "title.dashboard", accessGroup = "logged",
        hint = "Utilities")
/**
 * 
 * @author João Neves
 * @author Bruno Santos
 * @author Paulo Abrantes
 * 
 */
public class DashBoardManagementAction extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(DashBoardManagementAction.class.getName());

    static {
        RequestChecksumFilter.registerFilterRule(httpServletRequest -> !(httpServletRequest.getRequestURI().endsWith(
                "/dashBoardManagement.do")
                && httpServletRequest.getQueryString() != null && (httpServletRequest.getQueryString().contains("method=order")
                || httpServletRequest.getQueryString().contains("method=requestWidgetHelp")
                || httpServletRequest.getQueryString().contains("method=removeWidgetFromColumn")
                || httpServletRequest.getQueryString().contains("method=viewWidget")
                || httpServletRequest.getQueryString().contains("method=editWidget")
                || httpServletRequest.getQueryString().contains("method=editOptions") || httpServletRequest.getQueryString()
                .contains("method=controllerDescription"))));
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
        String realPath = "/dashBoardManagement.do?method=viewDashBoardPanel&dashBoardId=" + panel.getExternalId();
        forward.setPath(realPath + "&" + GenericChecksumRewriter.CHECKSUM_ATTRIBUTE_NAME + "="
                + GenericChecksumRewriter.calculateChecksum(request.getContextPath() + realPath, request.getSession()));
        return forward;
    }

    public ActionForward viewWidget(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        User currentUser = Authenticate.getUser();

        if (!checkPanelUser(widget.getDashBoardPanel(), currentUser)) {
            return null;
        }

        widget.getWidgetController().doView(new WidgetRequest(request, response, widget, currentUser));

        PortalLayoutInjector.skipLayoutOn(request);

        return forwardToWidget(widget, request, response);
    }

    public ActionForward editWidget(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        User currentUser = Authenticate.getUser();

        if (!checkPanelUser(widget.getDashBoardPanel(), currentUser)) {
            return null;
        }

        widget.getWidgetController().doEdit(new WidgetRequest(request, response, widget, currentUser));

        PortalLayoutInjector.skipLayoutOn(request);

        return forwardToWidget(widget, request, response);
    }

    public ActionForward editOptions(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {
        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        User currentUser = Authenticate.getUser();

        if (!checkPanelUser(widget.getDashBoardPanel(), currentUser)) {
            return null;
        }

        widget.getWidgetController().doEditOptions(new WidgetRequest(request, response, widget, currentUser));

        PortalLayoutInjector.skipLayoutOn(request);

        return forwardToWidget(widget, request, response);
    }

    private static boolean checkPanelUser(DashBoardPanel panel, User currentUser) {
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
        return new ActionForward(WidgetBodyResolver.getBodyFor(widget.getWidgetController().getClass()));
    }

    public ActionForward viewDashBoardPanel(final DashBoardPanel panel, final HttpServletRequest request,
            final HttpServletResponse response) {
        request.setAttribute("dashBoard", panel);
        return forward("/dashBoardPanel/viewDashBoard.jsp");
    }

    @EntryPoint
    public ActionForward viewDashBoardPanel(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        User user = Authenticate.getUser();
        DashBoardPanel panel = user.getUserDashBoard();
        if (panel == null) {
            panel = init(user);
        }
        return viewDashBoardPanel(panel, request, response);
    }

    @Atomic
    private DashBoardPanel init(User user) {
        if (user.getUserDashBoard() != null) {
            return user.getUserDashBoard();
        }
        return new DashBoardPanel(user);
    }

    public ActionForward removeWidgetFromColumn(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        JsonObject jsonObject = new JsonObject();
        try {
            DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
            widget.delete();
            jsonObject.addProperty("status", "OK");
        } catch (Exception e) {
            jsonObject.addProperty("status", "NOT_OK");
        }
        writeJsonReply(response, jsonObject);
        return null;
    }

    public ActionForward prepareAddWidget(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        DashBoardPanel panel = getDomainObject(request, "dashBoardId");
        request.setAttribute("dashBoard", panel);
        request.setAttribute("widgets", WidgetRegistry.getAvailableWidgets(panel, Authenticate.getUser()));
        return forward("/dashBoardPanel/addWidget.jsp");
    }

    @SuppressWarnings("unchecked")
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
        return widget.getWidgetController().doSubmit(new WidgetRequest(request, response, widget, Authenticate.getUser()));
    }

    public ActionForward requestWidgetHelp(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {

        DashBoardWidget widget = getDomainObject(request, "dashBoardWidgetId");
        DashBoardPanel panel = widget.getDashBoardPanel();

        JsonObject jsonObject = new JsonObject();

        if (panel.isAccessibleToCurrentUser()) {
            jsonObject.addProperty("helpText", widget.getWidgetController().getHelp());
        }

        writeJsonReply(response, jsonObject);
        return null;
    }

    @SuppressWarnings("unchecked")
    public ActionForward controllerDescription(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws IOException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {

        String className = request.getParameter("dashBoardWidgetClass");
        Class<? extends WidgetController> controllerClass = (Class<? extends WidgetController>) Class.forName(className);
        JsonObject jsonObject = new JsonObject();

        if (controllerClass != null) {
            WidgetController controller = controllerClass.newInstance();
            jsonObject.addProperty("description", controller.getWidgetDescription());
            jsonObject.addProperty("name", WidgetRegistry.getNameForWidget(controllerClass));
        }

        writeJsonReply(response, jsonObject);
        return null;
    }

    protected void writeJsonReply(HttpServletResponse response, JsonObject jsonObject) throws IOException {
        byte[] jsonReply = jsonObject.toString().getBytes();
        final OutputStream outputStream = response.getOutputStream();
        response.setContentType("text");
        response.setContentLength(jsonReply.length);
        outputStream.write(jsonReply);
        outputStream.flush();
        outputStream.close();
    }
}
