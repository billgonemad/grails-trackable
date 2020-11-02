<div class="trackable-widget">
%{--    <span>${label}</span>--}%
    <g:form url="[controller: 'trackable', action: 'track']">
        <input type="hidden" name="tracked" value="${userTracked ? 'true' : 'false'}">
        <input type="hidden" name="trackedId" value="${trackable.id}"/>
        <input type="hidden" name="trackedClass" value="${trackable.class.name}"/>
        <input type="checkbox" name="${trackable.class.name}-${trackable.id}-tracked"
               data-toggle="switchbutton" data-onstyle="success" data-offstyle="danger" data-onlabel="${doneLabel}" data-offlabel="${notDoneLabel}"
               ${userTracked ? 'checked' : ''} class="trackable-switch"/>
    </g:form>
</div>