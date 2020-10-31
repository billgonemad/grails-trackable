//= require jquery
//= require_self

$(function() {
   $('div.trackable-widget input.tracakble-btn').change(function() {
       var $form = $(this).closest('form');
       var value = $(this).val();
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