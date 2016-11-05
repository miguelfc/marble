<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">

    $(function() {
        
    	Object.size = function(obj) {
    	    var size = 0, key;
    	    for (key in obj) {
    	        if (obj.hasOwnProperty(key)) size++;
    	    }
    	    return size;
    	};
    	
    	
        var modules = {};
        <c:forEach var="module" items="${modules}">
        modules["${module.name}"] = {};
        modules["${module.name}"]["simpleName"] = "${module.simpleName}";
        modules["${module.name}"]["operations"] = {};
        <c:forEach var="operation" items="${module.operations}">
        modules["${module.name}"]["operations"]["${operation.key}"] = "${operation.value}";
        </c:forEach>
        modules["${module.name}"]["parameters"] = {};
        <c:forEach var="parameter" items="${module.parameters}">
        modules["${module.name}"]["parameters"]["${parameter.key}"] = "${parameter.value}";
        </c:forEach>
        </c:forEach>

        var $moduleSelect = $("#modules-select");
        // Fill up the main select options
        $moduleSelect.append(function() {
            var output = '<option value="">Select a module...</option>';
            $.each(modules, function(key, value) {
                output += '<option value="' + key + '">' + value["simpleName"] + '</option>';
            });
            return output;
        });
        
        $moduleSelect.change(function() {
            if (!$(this).val()) {
                $("#operations-div").empty();
                $("#parameters-div").empty();
            }
            else {
	            // Create the operation select
	            $("#operations-div").empty().append('<fieldset><legend>Operation</legend> '+
	            '<select name="operation" id="operations-select" class="form-control"></select></fieldset>');
	            var $operationSelect = $("#operations-select");
	            $operationSelect.empty().append(function() {
	                var output = '<option value="">Select an operation...</option>';
	                $.each(modules[$moduleSelect.val()]["operations"], function(key, value) {
	                    output += '<option value="' + key + '">' + value + '</option>';
	                });
	                return output;
	            });
	            
	         	// Select first option if only one is present
	            if ($operationSelect.find("option").size() == 2) {
	                $operationSelect.find("option:eq(1)").prop('selected', true).change();
	            }
	         	
	            if(Object.size(modules[$moduleSelect.val()]["parameters"]))
                {
		         	// Create the parameters configuration
		         	
		            $("#parameters-div").empty().append(function() {
		                var output = '<fieldset><legend>Custom Parameters</legend>';
		                $.each(modules[$moduleSelect.val()]["parameters"], function(key, value) {
		                    output += '<div class="form-group">';
		                    output += '<label for="' + key + '">' + value + '</label>';
		                    output += '<input id="' + key + '" name="parameters[' + key + ']" class="form-control" type="text">';
		                    output += '<p class="help-block">' + value + '</p>';
		                    output += '</div>';
		                });
		                output += '</fieldset>';
		                return output;
		            });
                }
	            else {
	                $("#parameters-div").empty();
	            }
            }
        });
        
        // Select first option if only one is present
        if ($moduleSelect.find("option").size() == 2) {
            $moduleSelect.find("option:eq(1)").prop('selected', true).change();
        }
    });
</script>