/**
 * 
 * Javascript that deals with the generation of the dashboard.
 * There are a few conventions between this script and the viewDashBoard JSP,
 * so be careful when changing any element classes or ids.
 * 
 * 11/08/2009 Paulo Abrantes
 * 
 * @param numberOfColumns: Number of columns of the dashboard
 * @param yesLabel: The label for the yes button in the "Do you wish to delete the widget" dialog
 * @param noLabel: The label for the no button in the "Do you wish to delete the widget" dialog
 * @param removeMessage: The label that will be shown in the dialog text.
 * @param removeTitle: The label that will be shown on the title of the dialog (it supports formating to insert the name of the widget in it, example "widget {0} deletion")
 * @param orderURL: The url for the ordering
 * @param closeURL: The url for closing the widget
 * @param helpURL: The url for showing the help
 * @param viewUrl: the url that will call the widget doView binding
 * @param editURL: the url that will call the widget doEdit binding
 * @param optionsURL: the url that will call the widget doEditOptions binding
 * @param removeErrorMessage: The message that is displayed when widget deletion AJAX request failed
 * @param loadError: The message that is displayed if a widget ajax request had some kind of error (might have been error server side, timeout, etc)
 */
var globalLoadViewWidget; // we need this because loadViewWidgetBody() should only received the widgetContent and be able to reload it
var globalLoadError;

function startDashBoard(numberOfColumns, yesLabel, noLabel, removeMessage, removeTitle, orderURL, closeURL, helpURL, viewURL, editURL, optionsURL, removeErrorMessage, loadError) {
	globalLoadViewWidget = viewURL;
	globalLoadError = loadError;
	
	$(function() {
	$(".column").sortable({
			handle: '.portlet-header',
			cursor: 'move',
			connectWith: '.column',
			opacity: 0.9,
			stop: function(event, ui) {

				var modification = "";

				for (i=0; i < numberOfColumns; i++) {
					portletContainersArray = $("#column-" + i).find(".portlet");
					portletsStrings = "";
						jQuery.each(portletContainersArray, function() {
							portletsStrings = portletsStrings + $(this).attr('id') + ' ';
						});
					modification = modification + i + ":" + portletsStrings;
					if (i + 1 < numberOfColumns) {
						modification = modification + ",";
					}

				}
				$.get(orderURL, { ordering: modification });
			}
		});

		$(".portlet").addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
		.find(".portlet-header")
			.addClass("ui-widget-header ui-corner-top").end()
		.find(".portlet-content");

		$(".portlet-header .glyphicon-remove").click(function() {
			var widget = $(this).parent().parent();
			var widgetName = getWidgetName(widget);
			var title = formatString(removeTitle,[widgetName])
		
			$.alerts.okButton = yesLabel; 
			$.alerts.cancelButton = noLabel; 
			$.alerts.overlayOpacity= 0.4;
			$.alerts.overlayColor= '#333';
			jConfirm(removeMessage, 
					title,function(userInput) {
				  if(userInput) {
					  $.getJSON(closeURL,
						 	    { dashBoardWidgetId: widget.attr('id') },
						    	function(data) { 
									if(data['status'] == 'OK') {
							 	   		 widget.fadeOut();  
							 	   		/* widget.children(".portlet-content").slideUp("slow", function callback() { widget.fadeOut(); }); */
						 	    	} else {
						 	    		$("#dashBoardMessageContainer").empty();
										$("#dashBoardMessageContainer").append('<div class="errorBox">' + removeErrorMessage + '</div>');
						 	    	}});
						 			
			      }
		        });

		});
				
		$(".portlet-header .glyphicon-pencil").click(function() {
			widgetCall(editURL, $(this).parent().next());
		});
				
		$(".portlet-header .glyphicon-wrench").click(function () {
			widgetCall(optionsURL, $(this).parent().next());
		});
		
		$(".portlet-header .glyphicon-question-sign").click(function() {
			var widget = $(this).parent().parent();
			$.getJSON(helpURL,
				 	    { dashBoardWidgetId: widget.attr('id') },
				    	function(data) { 
				 	    	$("#dashBoardMessageContainer").empty();
							$("#dashBoardMessageContainer").append('<div class="highlightBox alert alert-warning" style="display: none;"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + data['helpText'] + ' </div>')
							.find(".highlightBox").slideDown("fast").end();
				 	    }
				 	    );
		});

		$(".portlet-content").each( function(index, widgetContent) {
			loadViewWidgetBody(widgetContent);
		});
   
		$(".column").disableSelection();
		
		
	});
}

function widgetCall(callUrl, widgetContent) {
	var widgetId = $(widgetContent).parent().attr('id');

	$.ajax({
		url: callUrl,
		data: { dashBoardWidgetId: widgetId, timeout: 6000 },
		success: function(data) {
			if (data) {
				$(widgetContent).empty();
				$(widgetContent).append(data);
				$(widgetContent).children('form').ajaxForm(function() {
					 loadViewWidgetBody($(widgetContent));
				});
			}
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) {
			$(widgetContent).empty();
			$(widgetContent).append('<div class="errorBox">' + loadError + '</div>');
		}
	});
}

function loadViewWidgetBody(widgetContent) {
	
	var widgetId =  $(widgetContent).parent().attr('id')

	$.ajax({ 
        url: globalLoadViewWidget, 
        data: { dashBoardWidgetId: widgetId },
        success: function(data) { 
        		if (data.length > 0) {
        			$(widgetContent).empty();
					$(widgetContent).append(data);
					
        		}
        		else {
        			$(widgetContent).empty();
        			$(widgetContent).append('<div class="errorBox">' + globalLoadError + '</div>');
        		}
		}, 
		error: function (XMLHttpRequest, textStatus, errorThrown) { 
			$(widgetContent).empty();
			$(widgetContent).append('<div class="errorBox">' + globalLoadError + '</div>');
		} 

	});

}
function getWidgetName(widget) {
	return widget.children(".portlet-header").children(".widgetName").text();
}

function formatString(string, formatTokens) {
	var text = string;
	for (i=0; i < formatTokens.length ; i++) {
		text = text.replace(new RegExp("\\{" + i + "\\}"),formatTokens[i]);
	}
	return text;
}