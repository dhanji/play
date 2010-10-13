/** Entry point **/
$(document).ready(function() {
  $('.message').live('mouseenter', function() {
    $(this).addClass('message-border-hover');
  }).live('mouseleave', function() {
    $(this).removeClass('message-border-hover');
  }).live('click', function() {
    var popover = $('#popover');
    popover.fadeIn();

    return false; // stop bubbling
  });

  // Dismiss popover by clicking anywhere.
  $('body').live('click', function() {
    $('#popover').fadeOut('fast');
  })
});

jQuery.fn.center = function () {
  this.css("top", ( $(window).height() - this.height() ) / 2 + $(window).scrollTop() - 56 + "px");
  this.css("left", ( $(window).width() - this.width() ) / 2 + $(window).scrollLeft() + "px");
  return this;
};