/**
 * Nano by Dhanji.
 */

$(document).ready(function() {

  dialog = $('#dialog');
  //Setup click handlers for the dialog
  $('#newtweet').click(function() {
    dialog.addClass("anim-pulse");
    dialog.fadeIn();
  });
  
  // TODO(dhanji): handle server side!
  $('#dialog-post').click(function() {
    dialog.fadeOut();
  });
  
  $('#dialog-discard').click(function() {
    dialog.fadeOut();
  });

  tweet = {
    author: 'The Dude',
    text: 'Watch as the evil decepticons battle the valiant autobots for control of their home planet of cybertron',
    embed: 
    '<object width="425" height="344"><param name="movie" value="http://www.youtube.com/v/_EBUXVZcb2M&hl=en_US&fs=1&"></param><param name="allowFullScreen" value="true"></param><param name="allowscriptaccess" value="always"></param><embed src="http://www.youtube.com/v/_EBUXVZcb2M&hl=en_US&fs=1&" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="425" height="344"></embed></object>'
  };


  // Set up live event handlers for tweets.
  $('.tweet-reply').live('click', function() {
    dialog.addClass("anim-pulse");
    dialog.fadeIn();
  });
  
  $('.tweet .slider').live('click', function(event) {
    // find containing tweet
    tweet = $(event.currentTarget).parent();    
    $('.gutter', tweet).slideToggle("fast");
    $('.more', tweet).slideToggle("fast");
  });
  
  $('a.conversation').live('click', function() {
    $('#popover').addClass("anim-pulse");
    $('#popover').fadeIn();
  });
  
  $('a.retweet').live('click', function() {
    dialog.addClass('anim-pulse');
    dialog.fadeIn();
  });
  
  $('a.favorite').live('click', function(event) {
    // Find containing more box and append the star to it.
    more = $(event.currentTarget).parents('.tweet');
    log("working..")
    log(more)
    
    more.after('<div class="sticker-fave"></div>');
    
    sticker = $('div.sticker-fave', more);
    sticker.addClass('anim-pulse');
    sticker.fadeIn();
  });
  
  // TESST BEGINS HERE
  $('#content').prepend(makeTweet(tweet)).prepend(makeTweet(tweet));
  tweet.embed = null;
  $('#content').prepend(makeTweet(tweet)).prepend(makeTweet(tweet));
});

function log(log) {
  $('#debug').text(log);
}

function makeTweet(tweet) {
  var tweetBuilder = [];
  var i = 0;
  tweetBuilder[i++] = ' <div id="tweet-template" class="tweet-box"><div class="avatar"><img src="dj.jpg"/>\
  </div><div class="tweet"><div class="topline"><span class="author">';

  tweetBuilder[i++] = tweet.author;
  tweetBuilder[i++] = '</span><div class="icons"><a class="tweet-reply" href="#"><div class="icon-replies"></div></a></div><div<div class="text">';
        
  tweetBuilder[i++] = tweet.text;
  tweetBuilder[i++] = '</div><div class="meta"><div class="label-container">\
            <span>transformers</span>\
            <span>cool</span>\
            <span>thingsilike</span></div></div>\
          <div class="slider"></div>\
          <div class="gutter">\
              <a href="#">http://www.youtube.com/watch?v=_EBUXVZcb2M</a>';
  tweetBuilder[i++] = tweet.embed;
  tweetBuilder[i++] = '</div>\
          <div class="more">\
            <div class="info">From Tweetie.</div>\
            <div class="action"><img src="images/comments.png"/><a class="conversation" href="#">Conversation</a></div>\
            <div class="action"><img src="images/comments.png"/><a class="retweet" href="#">Retweet</a></div>\
            <div class="action"><img src="images/star.png"/><a class="favorite" href="#">Favorite</a></div>\
          </div></div></div>';
  return tweetBuilder.join('');
}
