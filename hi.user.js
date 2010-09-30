// ==UserScript==
// @name          MovieBin
// @namespace     http://wideplay.com
// @description   My newzbin imdb script.
// @include       http://*.newzbin.com/*
// @require       http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js
// ==/UserScript==

$(document).ready(function() {
  // Titles.
  var rowRef = $('.odd tr,.even tr');
//  targetRef = $('.odd tr:last-child,.even tr:last-child');


  rowRef.each(function() {
    var thisRef = $(this);

    var imdbLinkRef = thisRef.next().find('a').next().next().next();
    if (imdbLinkRef.length == 0)
      return this;
    
    var imdbUrl = imdbLinkRef.attr('href').substring(4);

    if (imdbUrl && imdbUrl != null) {
      GM_xmlhttpRequest({
        method: "GET",
        url: imdbUrl,

        onload: function(response) {
          var imdbScrapeRef = $(response.responseText);

          var ratingRef = imdbScrapeRef.find('.starbar-meta b').html();

          var newContent = $('<tr><td class="center"></td><td colspan="2">' + ratingRef
              + '</td><td class="fileSize" style="text-align:left;">'
              + $('h5:contains("Plot:")', imdbScrapeRef).next().html() + '</td></tr>');
          newContent.find('a').remove();

          thisRef.parent()
              .append(newContent);
      }});
    }
  });
});