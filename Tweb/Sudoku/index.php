<?php 
#index il menu principale
require_once("/php/common.php");
ensure_logged_in();
require_once("top.html");
?>

		<link href="./css/index.css" type="text/css" rel="stylesheet" />
		<script src="./js/mylib.js" type="text/javascript"></script>
		<script src="./js/index.js" type="text/javascript"></script>
	</head>

	<body>
		<h1>SUDOKU</h1>

		<div id="container">
			<button id="play">Play</button>
			<button id="ranking">Ranking</button>
			<button id="logout">Log Out</button>
		</div>
<?php
require("bottom.html");
?>