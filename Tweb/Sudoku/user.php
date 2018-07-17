<?php
#pagina di login

require_once("/php/common.php");
require_once("top.html");

#se l'utente è loggato allora redirect alla home
if (isset($_SESSION["name"])){
	redirect("./index.php");
} else {?>
		<link href="./css/login.css" type="text/css" rel="stylesheet" />
		<script src="./js/mylib.js" type="text/javascript"></script>
		<script src="./js/login.js" type="text/javascript"></script>
	</head>

	<body>
		<h1>Log in</h1>
		
		<div id="form">
			<ul>
				<li id="logtab" class="button clicked">Login</li>
				<li id="regtab" class="button unclicked">Register</li>
			</ul>
			<strong>Name:</strong><br><input type="text" name="username" id="user"/><br>
			<strong>Password:</strong><br><input type="password" name="password" id="pass"/><br>
			<input id="logbut" type="button" value="Login" />
		</div>
<?php
}
require("bottom.html");
?>