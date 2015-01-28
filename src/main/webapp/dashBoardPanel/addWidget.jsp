<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>


<h2> <bean:message key="title.addWidget" bundle="DASH_BOARD_RESOURCES"/></h2>

<ul>
	<li>
		<html:link page="/dashBoardManagement.do?method=viewDashBoardPanel" paramId="dashBoardId" paramName="dashBoard" paramProperty="externalId">
			<bean:message key="link.back" bundle="MYORG_RESOURCES"/>
		</html:link>
	</li>
</ul>


<table>
<bean:size id="numberOfWidgets" name="widgets"/>

<logic:iterate id="widget" name="widgets" indexId="col">
	<bean:define id="widgetClassName" name="widget" property="name"/>
	<tr>
		<td style="width: 250px;">${widget.newInstance().widgetName}</td>
		<td style="width: 40px">
			<html:link styleId="add-${widgetClassName}" page="/dashBoardManagement.do?method=addWidget&dashBoardWidgetClass=${widgetClassName}" paramId="dashBoardId" paramName="dashBoard" paramProperty="externalId">
				<bean:message key="link.add" bundle="MYORG_RESOURCES"/>
			</html:link>
		</td>
		<logic:equal name="col" value="1">
			<td id="description" rowspan="<%= numberOfWidgets.toString() %>" style="vertical-align: top; padding-left: 25px;">
			
			</td>
		</logic:equal>
	</tr>
</logic:iterate> 
</table>
	
<script type="text/javascript">
	$("a[id^=\"add-\"]").mouseenter(function() { 
		var id = $(this).attr('id');
		var controllerClass = id.substring(4, id.length);
		<%= "$.getJSON(\"" + request.getContextPath() + "/dashBoardManagement.do?method=controllerDescription&dashBoardWidgetClass=\" + controllerClass,function(data, textStatus) {dealWith(data)})" %>
	}); 

function dealWith(data) {

$("#description").empty();
$("#description").append("<div><span><strong>" + data['name'] + "</strong></span>: <p>" + data['description'] + "</p></div>");

}
		
</script>
