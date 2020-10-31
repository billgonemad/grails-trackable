<div class="trackable-widget">
    <g:form url="[controller: 'trackable', action: 'track']">
        <input type="hidden" name="tracked" value="${userTracked ? 'true' : 'false'}">
        <div class="btn-group btn-group-toggle" data-toggle="buttons">
            <label class="btn btn-success ${userTracked ? 'active' : ''}">
                <input type="radio" name="${trackable.class.name}-${trackable.id}-options" value="true" class="tracakble-btn"
                       id="${trackable.class.name}-${trackable.id}-option1" autocomplete="off"<g:if test="${userTracked}">checked</g:if>> ${message(code: 'trackable.tracked.btn', default: 'Done')}
            </label>
            <label class="btn btn-light ${userTracked ? '' : 'active'}">
                <input type="radio" name="${trackable.class.name}-${trackable.id}-options" value="false" class="tracakble-btn"
                       id="${trackable.class.name}-${trackable.id}-option2" autocomplete="off" <g:if test="${!userTracked}">checked</g:if>> ${message(code: 'trackable.untracked.btn', default: 'Not Done')}
            </label>
        </div>
    </g:form>
</div>