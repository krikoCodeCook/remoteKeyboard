$(document).ready(function() {
  var i = 1;
  var sleepInterval;
  var preventSleepDisabled = false;
  var shortTextLength = 120;
  var newText;

  if (!preventSleepDisabled)
    sleepInterval = setInterval(preventSleep, 15000);
  
  $('#submit').click(function(){
    var text = $('#text').val();

    if (text.length !== 0) {
      var data = $('#form').serialize();
    
      $.ajax({
        type: 'POST',
        url: './index.html',
        data: data,
        success: function(){
          $('#history-table > tbody:first').prepend(formatHistory(text));
          $('#history-no').css('display', 'none');
          $('#text').val('');
          $('#text').focus();
        }
      });
    }
    
    return false;
  });

  $("#history-table tbody tr").live("click", function(){ 
    var oldText = $('#text').val();
    newText = $(this).attr('fullText');

    if (oldText === "")
      $('#text').val(newText);
    else if (oldText !== newText) {
      $('#history-confirmation-modal').modal();
    }
  });

  $("#history-confirmation-yes").click(function() {
    $('#text').val(newText);
    $('#history-confirmation-modal').modal('hide');
  });

  $("#history-confirmation-no").click(function() {
    $('#history-confirmation-modal').modal('hide');
  });

  $("#clear").click(function() {
    $('#text').val('');
  });

  $('#history-toggle').click(function() {
    $('#history-content').slideToggle();

    if ($('#history-toggle').html() == "-")
      $('#history-toggle').html("+");
    else
      $('#history-toggle').html("-");
  });

  function preventSleep() {
    $.ajax({
      url: 'no-sleep',
      type: 'GET'
    }).done(function() {
      i++;
      
      if (i>8){
        clearInterval(sleepInterval);
      } 
    });
  }

  function formatHistory(text) {
    var shortText;
    var minutes;
    var d;
  
    if (text.length > shortTextLength)
      shortText = text.substring(0, shortTextLength-3) + "...";
    else
      shortText = text;
    
    // replace new lines with a space
    shortText = shortText.replace(/(\r\n|\n|\r)/gm," ");

    // get current time and date
    d = new Date();

    // if minutes is a single digit prepend 0
    if (d.getMinutes().toString().length == 1)
      minutes = '0'+d.getMinutes();
    else
      minutes = d.getMinutes();
  
    // format date and time "1.1.1970 00:00"
    d = d.getDate()+'.'+d.getMonth()+'.'+d.getFullYear()+' '+d.getHours()+':'+minutes;

    return '<tr fullText="'+text+'"><td style="width:15%;">'+d+'</td><td>'+shortText+'</td></tr>';
  }

});