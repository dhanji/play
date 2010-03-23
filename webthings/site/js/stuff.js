/**
 * Stuff, by Dhanji.
 */

/**
 * The flip() plugin is a sensible alternative to toggle()
 */
jQuery.fn.flip = function(data, fn, flipFn) {
  return this.each(function() {
    var is = $(this).data(data);
    if (is) {
      $(this).data(data, false);
      flipFn();
    } else {
      $(this).data(data, true);
      fn();
    }
  });
}

// The main entry point
$(document).ready(function() {

  dialog = $('#dialog');
  ctrl = {};
  dialog.data('self', ctrl);

  /**
   * Open the dialog with the given task.
   * task is optional, if none is given,
   * a new task is assumed.
   */
  ctrl.open = function(task) {
    dialog.addClass("anim-pulse");
    dialog.fadeIn();

    // If we're editing a task, slurp the task content + set it.
    textbox = $('#dlg-todo-text');
    if (task) {
      textbox.text(task.text());
      dialog.data('task', task);
    }

    textbox.focus();
  };

  /**
   * Saves the current task in the edit dialog.
   */
  ctrl.save = function() {
    dialog.fadeOut();

    text = $('#dlg-todo-text').text();
    task = dialog.data('task');

    if (task){
      // now update the task itself...
      task.edit(text);

      // reset dialog state...
      $('#dlg-todo-text').text('');
      dialog.data('task', null);

      return false;
    }

    // OTHERWISE: This must be a new task...
    // send rpc to server.
    itemId = nextId();
    send({
      id: itemId,
      rpc: 'new_todo',
      text: text
    });

    // insert into dom.
    item = [];
    item.push('<div class="todo tweet"><input class="todo-check" type="checkbox"/>');
    item.push('<span class="text">');
    item.push(text);
    item.push('</span></div>');
    newTask = $(item.join(''));
    $('#content').append(newTask);
    newTask.data('id', itemId);
    TaskController(newTask);

    // need to setup dynamic data so we remember how to act on this.
    //newTodo;
    $('#dlg-todo-text').text('');
  };

  /**
   * Closes + resets dialog state for the next time it's opened.
   */
  ctrl.close = function() {
    dialog.fadeOut(function() {
      $('#dlg-todo-text').text('');
      $('#dlg-label-text').text('');
    });
  };


  /**
   * Detect all comma-separated tokens in the labels input box
   * and convert them into dom labels.
   */
  ctrl.labels = function() {
    labels = $('#dlg-label-text');
    text = labels.attr('value').split(/[;,]/);

    if (text.length > 1) {
      event.preventDefault();
      labels.before('<div class="slurped-label">' + text[0] + '</div>');
      labels.attr('value','');
    }
  }



  // -----------------------------------
  // Setup click handlers for the dialog
  // -----------------------------------

  $('#newtask').click(function() {
    dialog.data('self').open();
  });
  
  $('#dialog-post').click(function() {
    dialog.data('self').save();
  });
  
  $('#dialog-discard').click(function() {
    dialog.data('self').close();
  });

  $('#dlg-label-text').keydown(function() {
    dialog.data('self').labels();
  });

  $('.slurped-label').live('click', function() {
    $(this).remove();
  });


  // Init server message pump
  $(document).data('pump', []);
  $(document).data('idGen', 1);


  // Init the rest.
  init();

  // Fire the server pump timer periodically
  setInterval(sendBatch, 4000);
});


function init() {
  $('.todo').live('dblclick', function(event) {
    event.preventDefault(); // disallow selection 
    // Open dialog with the clicked task item.
    $('#dialog').data('self').open($(this).data('self'));
  });


  // Set up to-do item handlers -- the good stuff
  $('.todo-check').live('click', function() {
    var div = $(this).parent();
    var task = div.data('self');
    div.flip('done', function() {
      task.done(true);
    }, function() {
      task.done(false);
    });
  });


  /**
   * Task class object + init.
   */
  $('.todo').each(function() {
    var task = $(this);

    // slurp id & done state from html.
    task.data('id', task.attr('id').substring(5)); // snip off the task_ part of the id
    task.data('done', task.hasClass('done'));
    TaskController(task);
  });
}
  
/**
 * Constructor for a task div's controller.
 */
function TaskController(task) {
  ctrl = {};
  task.data('self', ctrl);

  /**
   * Sets task to done or undone.
   */
  ctrl.done = function(isDone) {
    if (isDone) {
      task.addClass('done');
      send({
        id: task.data('id'),
        rpc: 'done'
      });
    } else {
      task.removeClass('done');
      send({
        id: task.data('id'),
        rpc: 'undone'
      });
    }
  };

  /**
   * Sets the current text as specified.
   */
  ctrl.edit = function(text) {
    $('.text', task).text(text);
    send({
      id: task.data('id'),
      rpc: 'edit_task',
      text: text
    });
  };

  /**
   * Getter for task item text.
   */
  ctrl.text = function() {
    return $('.text', task).text();
  };

  return ctrl;
}


// ----------------------------
// Utils and rpc infrastructure
// ----------------------------

/**
 * An id generator. The server should tell us the "real"
 * ids, so this is just a temporary sequence.
 */
function nextId() {
  var id = $(document).data('idGen') + new Date().getTime();
  $(document).data('idGen', id);
  return id;
}

/**
 * An async ajax(!) interface, enqueues, then
 * flushes ops. Gives the appearance of instant
 * operation. takes a map of key-value pairs.
 */
function send(data) {
  var queue = $(document).data('pump');
  queue.push(data);
}

// pulls rpcs off the global pump and sends them
function sendBatch() {
  var queue = $(document).data('pump');

  // Run thru the queue of ops and concatenate them.
  var queueLength = queue.length;

  // Do nothing if there are no ops.
  if (queueLength == 0) {
    return;
  }

  var data = {};
  for (var i = 0; i < queueLength; i++) {
    var op = queue[i];
    rpcId = nextId();

    // TODO intelligently compose ops here.
    for (var attr in op) {
      data[attr + '_' + rpcId] = op[attr];
    }
  }

  // flush queue.
  $(document).data('pump', []);

  // Send this wonderful stuff to the server!
  $.ajax({
    type: 'POST',
    url: '/pump',
    data: data,
    success: function(msg) {
      log('Send OK: ' + msg);
    },
    failure: function() {
      log('failed');
    }
  });
}

// Logging utility func, just writes to a div
function log(log) {
  $('#debug').append(log + "<br/>");
}
