
<%@ page import="org.nanocan.plates.Plate" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'plate.label', default: 'Plate')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-plate" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="navbar">
        <div class="navbar-inner">
            <div class="container">
                <ul class="nav">
                    <g:render template="/templates/navmenu"></g:render>
                    <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                                          args="[entityName]"/></g:link></li>
                </ul>
            </div>
        </div>
    </div>
		<div id="list-plate" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="barcode" title="${message(code: 'plate.barcode.label', default: 'Barcode')}" />
					
						<g:sortableColumn property="name" title="${message(code: 'plate.name.label', default: 'Name')}" />

						<g:sortableColumn property="format" title="${message(code: 'plate.format.label', default: 'Format')}" />
					
						<g:sortableColumn property="replicate" title="${message(code: 'plate.replicate.label', default: 'Replicate')}" />

					</tr>
				</thead>
				<tbody>
				<g:each in="${plateInstanceList}" status="i" var="plateInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${plateInstance.id}">${fieldValue(bean: plateInstance, field: "barcode")}</g:link></td>
					
						<td><g:link action="show" id="${plateInstance.id}">${fieldValue(bean: plateInstance, field: "name")}</g:link></td>

						<td>${fieldValue(bean: plateInstance, field: "format")}</td>
					
						<td>${fieldValue(bean: plateInstance, field: "replicate")}</td>

					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${plateInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
