<?php 
#la pagina delle classifiche
require_once("/php/common.php");
ensure_logged_in();
require_once("top.html");
?>
		<link href="./css/ranking.css" type="text/css" rel="stylesheet" />
		<script src="./js/mylib.js" type="text/javascript"></script>
		<script src="./js/ranking.js" type="text/javascript"></script>
	</head>

	<body>
		<h1>RANKING</h1>

		<div id="container">
			<ul>
				<li id="easy" class="clicked">Easy</li>
				<li id="medium" class="unclicked">Medium</li>
				<li id="hard" class="unclicked">Hard</li>
			</ul>
			<div id="page">
				<span>Page: </span>
				<select id="pages">
				
				</select>
			</div>
			<div id="content">
			</div>
		</div>

		<button id="back">Back</button>
<?php
require("bottom.html");
?>