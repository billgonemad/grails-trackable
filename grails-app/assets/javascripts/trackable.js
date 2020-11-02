//= require jquery
//= require bootstrap-switch-button.min
//= require_self

$(function() {
   $('div.trackable-widget input.trackable-switch').change(function() {
       var $form = $(this).closest('form');
       var value = $(this).is(':checked').toString();
       $('input[name="tracked"]', $form).val(value);

       $.ajax({
           type: 'POST',
           url: $form.attr('action'),
           data: $form.serialize(),
           success: function(response) {
               console.log(response);
           }
       })
   })
});