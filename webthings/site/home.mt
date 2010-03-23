<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link rel="stylesheet" href="style/stuff.css"/>

  <script type="text/javascript" src="js/jquery-1.4.1.min.js"></script>
  <script type="text/javascript" src="js/stuff.js"></script>
</head>
<body>

  <div class="header">
    <div class="titlebar">
      <div class="title">stuff</div>
      <div class="slogan">task tracking ftw!!!</div>
    </div>
  </div>

  <div class="searchbox">

    <div class="mascot">
      <img src="images/mascot_48.png">  
      <div id="debug">Hello there!</div>
    </div>

    <div class="search-border">

      <div id="search">
        <span id="search-icon"></span>
        <input id="search-input" type="text">
      </div>

      <div class="plus-btn">
        <img id="donetasks" title="Done tasks" src="images/archives.png">
        <img id="newtask" title="New task" src="images/issue.png">
      </div>

    </div>

    <div class="search-border labels-border">
      <h2>Labels</h2>

      <div class="big-label blue">work</div>
      <div class="big-label blue">play</div>
      <div class="big-label orange">groceries</div>
      <div class="big-label blue">social blogging</div>
      <div class="big-label">girlfriend</div>

    </div>
  </div>

  <div id="content">
    <h2>Next</h2>

    @foreach{task : tasks}
    <div id="task_@{task.id}" class="todo tweet @{task.completedOn != null ? 'done' : ''}">
      <input class="todo-check" type="checkbox" @{task.completedOn != null ? 'checked' : ''}/><span class="text">@{task.text}</span>
      @comment{<div class="due">Feb 23</div>}
    </div>
    @end{}

    @comment{<div class="sticker-fave"></div> <!-- disabled for now -->}
  </div>


  <div id="dialog" class="dialog">
    <div class="background"></div>
    <div class="input">
      <div id="dlg-todo-text" class="topregion" contentEditable="true"></div>
      <div class="botregion">
        <div class="label-label">Labels: </div>

        <div class="dlg-label-container">
	  <input id="dlg-label-text" type="text" class="dlg-label-text">
	</div>
      </div>
    </div>
    <div class="button-container">
      <div class="counter">140</div>
      <div id="dialog-post" class="button">Done</div>
      <div id="dialog-discard" class="button">Discard</div>
    </div>
  </div>

</body>
</html>
