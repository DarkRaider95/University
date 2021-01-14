<!DOCTYPE html>
<!--pezzo di pagina superiore-->
<html>
	<!-- MFN0634 TWeb Lab05 (Kevin Bacon) -->
	<head>
		<title>My Movie Database (MyMDb)</title>
		<meta charset="utf-8" />
		
		<!-- Links to provided files.  Do not edit or remove these links -->
		<link href="http://www.cs.washington.edu/education/courses/cse190m/12sp/homework/5/favicon.png" type="image/png" rel="shortcut icon" />
		<script src="http://www.cs.washington.edu/education/courses/cse190m/12sp/homework/5/provided.js" type="text/javascript"></script>

		<!-- Link to your CSS file that you should edit -->
		<link href="bacon.css" type="text/css" rel="stylesheet" />
	</head>

	<body>
		<div id="frame">
			<div id="banner">
				<a href="index.php"><img src="http://www.cs.washington.edu/education/courses/cse190m/12sp/homework/5/mymdb.png" alt="banner logo" /></a>
				My Movie Database
				<?php
					#controllo se l'utente è loggato in quel caso mostro il link di logout
    				if (!isset($_SESSION)) session_start();
				
					if(isset($_SESSION['name'])){
						?><a id="logout" href="logout.php">Logout</a><?php
					}
				?>
			</div>

			<div id="main">
				<?php
					#controllo se c'è un messaggio da stampare
					if(isset($_SESSION['FLASH'])){
						?><div id="flash"><?=$_SESSION['FLASH']?></div><?php
						unset($_SESSION['FLASH']);
					}
				?>
				<!-- your HTML output follows -->
