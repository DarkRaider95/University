<?php include("top.html"); ?>
<!--Tommaso Toscano Corso B Es 4 esercizio php form, pagina gestione submit iscrizione-->
<?php
	/*
	controllo preferenza gender, se le variabili preferenza non sono settate si imposta il sesso opposto come preferenza,
	nel caso si preferisca entrambi si imposta la variabile con "B".	
	*/
	if(isset($_POST["SM"]) and isset($_POST["SF"]) == false){
		$SG="M";
		unset($_POST["SM"]);
	} elseif (isset($_POST["SM"]) == false and isset($_POST["SF"])){
		$SG="F";
		unset($_POST["SF"]);
	} elseif (isset($_POST["SM"]) and isset($_POST["SF"])){
		$SG="B";
		unset($_POST["SM"]);
		unset($_POST["SF"]);
	} else{
		if(strcmp($_POST["Gender"], "M")==0){
			$SG="F";
		}else{
			$SG="M";
		}
	}

	#concatenazione variabile post in unica stringa separata da virgole con aggiunta della preferenza gender
	$fileline=implode(",",$_POST);
	$fileline=$fileline.",".$SG;

	#scrittura linea su file singles.txt
	file_put_contents("singles.txt", $fileline."\r\n", FILE_APPEND);
?>

	<div>
		<h1>Thank you</h1>
		<ul>
		<li>
			Welcome to NerdLuv, <?=$_POST["Name"]?>
		</li>
		<li>
			Now <a href="matches.php">log in to see your matches!</a>
		</li>
	</ul>
	</div>
<?php include("bottom.html"); ?>