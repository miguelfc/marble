<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
    $(function() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
    });

    function getTime() {
        var currentDate = new Date();
        var currentTime = ('0' + currentDate.getHours()).slice(-2) + ':'
                + ('0' + (currentDate.getMinutes() + 1)).slice(-2) + ':'
                + ('0' + (currentDate.getSeconds() + 1)).slice(-2);
        return currentTime;
    };

    /*
    Disabled, waiting for future development.
    function sendCommand(command) {
        $.ajax({
            type : "PUT",
            data : '{"command": "' + command + '"}',
            contentType: "application/json",
            url : "<c:url value="rest/execution/${execution.id}/command" />"
        }).done(
                function() {
                    notification = '<div class="alert alert-success alert-dismissable"><button type="button" class="close" data-dismiss="alert" '+
                    'aria-hidden="true">�</button><i class="fa fa-crosshairs"></i> Command <'+command+'> sent.</div>';
                    $($.parseHTML(notification)).appendTo("#notifications");
        }).fail(
                function() {
                    notification = '<div class="alert alert-danger alert-dismissable"><button type="button" class="close" data-dismiss="alert"  '+
                    'aria-hidden="true">�</button><i class="fa fa-crosshairs"></i> Command <'+command+'> could not be sent.</div>';
                    $($.parseHTML(notification)).appendTo("#notifications");
                });

    };
    
    $("#send-command-stop").click(function() {
        sendCommand("stop");
    });
     */
    (function refreshLog() {
        $
                .ajax({
                    dataType : "json",
                    url : "<c:url value="rest/execution/${execution.id}" />"
                })
                .done(
                        function(data) {
                            $("#execution-log").html(data.log);
                            $("#execution-status").html(data.status);
                            $("#execution-name").html(data.moduleParameters.name);
                            $("#execution-type").html(data.type);
                            $("#execution-created-at").html(data.createdAt);
                            $("#execution-updated-at").html(data.updatedAt);
                            $("#execution-module").html(/\.([^\.]+)$/.exec(data.moduleParameters.module)[1]);
                            $("#execution-operation").html(data.moduleParameters.operation);
                            console.error(data.moduleParameters.parameters);
                            var parametersString = "<ul>";
                            for (var parameter in data.moduleParameters.parameters) {
                            	parametersString += "<li><strong>" + parameter + "</strong>:<br/>";
                            	parametersString += data.moduleParameters.parameters[parameter] + "</li>"; 
                            }
                            parametersString += "</ul>";
                            $("#execution-parameters").html(parametersString);

                            $("#updated").removeClass("alert-warning");
                            $("#updated").addClass("alert-success");
                            $("#updated").html(
                                    "<i class='fa fa-clock-o'></i> Updated at "
                                            + getTime() + ".");
                            if (data.plot !== null) {
                                if (!($("#view-plot-button").length > 0)) {
                                    $("#actions-table tbody:first")
                                            .prepend(
                                                    '<tr><td><a id="view-plot-button" href="<c:url value="plot/'+data.plot.id+'"/>"'
                                                            + 'class="btn btn-default btn-block"><i class="fa fa-bar-chart-o"></i> '
                                                            + '<spring:message code="execution_view.actions.view_plot" /></a></td></tr>');
                                }
                            }
                        }).fail(
                        function() {
                            $("#updated").removeClass("alert-success");
                            $("#updated").addClass("alert-warning");
                            $("#updated").html(
                                    "<i class='fa fa-clock-o'></i> Update failed at "
                                            + getTime() + ".");
                        }).always(function() {
                    // Schedule the next request when the current one's complete
                    setTimeout(refreshLog, 5000);
                });
    })();
</script>