<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>

<%@page import="org.fenixedu.bennu.core.i18n.BundleUtil"%>

<script src="<%= request.getContextPath()%>/bennu-renderers/js/jquery.alerts.js" type="text/javascript"></script>
<script src="<%= request.getContextPath()%>/bennu-renderers/js/alertHandlers.js" type="text/javascript"></script>
<script src="<%= request.getContextPath()%>/bennu-renderers/js/jquery-ui.js" type="text/javascript"></script>
<script src="<%= request.getContextPath()%>/javaScript/jquery.form.js" type="text/javascript"></script>

<%@page import="module.dashBoard.presentationTier.WidgetBodyResolver"%>

<h2> <bean:message key="title.dashboard" bundle="DASH_BOARD_RESOURCES"/> </h2>

<bean:size id="numberOfColumns" name="dashBoard" property="dashBoardColumns"/>
<bean:define id="dashBoardId" name="dashBoard" property="externalId" type="java.lang.String"/>

<ul>
	<li>
		<html:link page="/dashBoardManagement.do?method=prepareAddWidget" paramId="dashBoardId"  paramName="dashBoard" paramProperty="externalId">
			<bean:message key="link.add" bundle="MYORG_RESOURCES"/>
		</html:link>
	</li>
</ul>


<script  type="text/javascript" src="<%=  request.getContextPath() + "/javaScript/dashboard.js"%>"></script>
<script  type="text/javascript">

<%
	String title = BundleUtil.getString("resources.DashBoardResources","title.removeWidget");
%>

startDashBoard(<%= numberOfColumns %>, 
				   '<bean:message key="true" bundle="MYORG_RESOURCES"/>', 
				   '<bean:message key="false" bundle="MYORG_RESOURCES"/>',
				   '<bean:message key="message.removeWidget" bundle="DASH_BOARD_RESOURCES"/>',
				   '<%= title %>', 
				   '<%= request.getContextPath() + "/dashBoardManagement.do?method=order&dashBoardId="+ dashBoardId %>',
				   '<%= request.getContextPath() + "/dashBoardManagement.do?method=removeWidgetFromColumn" %>',
				   '<%= request.getContextPath() + "/dashBoardManagement.do?method=requestWidgetHelp" %>',
				   '<%= request.getContextPath() + "/dashBoardManagement.do?method=viewWidget" %>',
				   '<%= request.getContextPath() + "/dashBoardManagement.do?method=editWidget" %>',
				   '<%= request.getContextPath() + "/dashBoardManagement.do?method=editOptions" %>',
				   '<bean:message key="error.removeWidget" bundle="DASH_BOARD_RESOURCES"/>',
				   '<bean:message key="error.loadingWidget" bundle="DASH_BOARD_RESOURCES"/>');
</script>

<div id="dashBoardMessageContainer">
	<html:messages id="message" message="true" bundle="DASH_BOARD_RESOURCES">
		<div class="errorBox"> <bean:write name="message" /> </div>
	</html:messages>
</div>


<div id="dashboard" class="mtop15">

	<logic:iterate id="column" indexId="index" name="dashBoard" property="orderedColumns">
		<div id="<%= "column-" + index %>" class="column">
			<bean:define id="columnId" name="column" property="externalId"/>
			<logic:iterate id="widget" name="column" property="orderedWidgets" type="module.dashBoard.domain.DashBoardWidget">
				<bean:define id="widget" name="widget" toScope="request" type="module.dashBoard.domain.DashBoardWidget"/>
				<bean:define id="widgetId" name="widget" property="externalId" type="java.lang.String" />
				<div id="<%= widgetId %>" class="portlet panel panel-default">
					<div class="portlet-header panel-heading" style="cursor: move;">
						<logic:equal name="widget" property="closable" value="true">
							<span class="glyphicon glyphicon-remove" title="<bean:message key="icon.title.close" bundle="DASH_BOARD_RESOURCES"/>"></span>
						</logic:equal>
						<logic:equal name="widget" property="optionsModeSupported" value="true">
							<span class="glyphicon glyphicon-wrench" title="<bean:message key="icon.title.options" bundle="DASH_BOARD_RESOURCES"/>"></span>
						</logic:equal>
						<logic:equal name="widget" property="editionModeSupported" value="true">
							<span class="glyphicon glyphicon-pencil" title="<bean:message key="icon.title.edit" bundle="DASH_BOARD_RESOURCES"/>"></span>
						</logic:equal>
						<logic:equal name="widget" property="helpModeSupported" value="true">
							<span class="glyphicon glyphicon-question-sign" title="<bean:message key="icon.title.help" bundle="DASH_BOARD_RESOURCES"/>"></span>
						</logic:equal>
						<span class="widgetName">${widget.widgetController.widgetName}</span>
					</div>
					<div class="portlet-content panel-body">
						<div style="text-align: center">
							<bean:message key="label.widget.loading" bundle="DASH_BOARD_RESOURCES"/>
						</div>
					</div>
				</div>
			</logic:iterate>
		</div>
	</logic:iterate>
</div>

<div class="clear"></div>

<style>
.column {
	margin-left: 5px;
	margin-right: 5px;
	width: 30%;
	float: left;
    padding-bottom: 100px;
}
.glyphicon {
	float: right;
	cursor: pointer;
}
.glyphicon:empty {
	width: 17px;
}
</style>
