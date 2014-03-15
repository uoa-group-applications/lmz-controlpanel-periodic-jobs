<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h2>Periodic Jobs</h2>

<ul class="dashboard-list">
	<c:forEach items="${model.entries}" var="pair">
		<li>
			<h4>${pair.key}
			<c:if test="${!(pair.value.enabled)}">
				- <span class="text-error">disabled</span>
			</c:if>
			</h4>

			<p>Type: ${pair.value.type}<br/>
			Status: ${pair.value.status}</p>

			<c:if test="${! empty pair.value.logs}">
				<div class="controls-row">
				<table class="table table-condensed table-bordered span6 periodic-jobs-log">
					<thead>
						<tr><th>Start</th><th>Finish</th><th>Message</th></tr>
					</thead>
			        <c:forEach items="${pair.value.logs}" var="log">
						<tr><td>${log.start}</td><td>${log.finish}</td><td class="error">${log.message}</td></tr>
			        </c:forEach>
				</table>
				</div>
			</c:if>
		</li>
	</c:forEach>
</ul>
