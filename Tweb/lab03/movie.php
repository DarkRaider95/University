<!DOCTYPE html>
<!--Tommaso Toscano Corso B Es 3 esercizio php gestione file e get-->
<?php
	#apro i vari file utilizzando come path la variabile $_GET["film"] con  la funzione glob prendo i percorsi dei file delle recensioni
	$movie=$_GET["film"];
	$infofile=file($movie."/info.txt",FILE_IGNORE_NEW_LINES);
	$overviewfile=file($movie."/overview.txt",FILE_IGNORE_NEW_LINES);
	$reviewfiles=glob($movie."/review*.txt");

	#funzione per gestire il cambio di colonna per le recensioni
	function column($length, $i){
		if($i==intval($length/2)){
			$str="</div>\n<div class='columns'>";
		} else {
			$str="";
		}
		return $str;
	}

	#controllo rating
	function rating($infofile){
		if($infofile>=60){
			$img="freshbig.png";
		} else {
			$img="rottenbig.png";
		}

		return $img;
	}
?>
<html lang="en">
	<head>
		<title>Rancid Tomatoes</title>

		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="movie.css" type="text/css" rel="stylesheet" />
		<link href="http://courses.cs.washington.edu/courses/cse190m/11sp/homework/2/rotten.gif"  rel="shortcut icon"/>
	</head>

	<body>
		<div id="banner">
			<img src="http://www.cs.washington.edu/education/courses/cse190m/11sp/homework/2/banner.png" alt="Rancid Tomatoes" />
		</div>
		<!--inserisco il nome e l'anno del film, i quali sono stati estratti dal file info.txt -->
		<h1><?=$infofile[0]."(".$infofile[1].")"?></h1>
		<div id="text">
			<div id="overview">
				<div>
					<img src="<?=$movie."/"?>overview.png" alt="general overview" />
				</div>

				<dl>
					<?php
						#con questo for stampo le informazioni prelevate dal file overview.txt
						for ($i=0; $i < count($overviewfile) ; $i++) {
						#separo titolo da contenuto di ogni sezione di overview
						$data=explode(":", $overviewfile[$i]);
					?>
					<!--stampo titolo e contenuto-->
					<dt><?=$data[0]?></dt>
					<dd><?=$data[1]?></dd>
					<?php
						}
					?>
				</dl>
			</div>
			<div id="rewiew">
				<div id="backrotten">
																						<!--cambio immagine in base al rating e stampo la percentuale-->
					<img src="http://www.cs.washington.edu/education/courses/cse190m/11sp/homework/2/<?=rating($infofile[2])?>" alt="Rotten"/><?=$infofile[2]?>%
				</div>
				<div class="columns">
					<?php
						#for per stampare le recensioni
						$length=count($reviewfiles);
						for ($i=0; $i < $length; $i++) {
							#estraggo le informazioni da ogni file review 
							$reviewfile=file($reviewfiles[$i],FILE_IGNORE_NEW_LINES);
							#scelgo l'immagine in base alla seconda riga di ogni file review
							if(strcmp($reviewfile[1], "FRESH") == 0){
								$img="fresh.gif";
							} else{
								$img="rotten.gif";
							}
					?>
					<p class="reviews">
						<img src="http://www.cs.washington.edu/education/courses/cse190m/11sp/homework/2/<?=$img?>" alt="Rotten" />
						<!--stampo la recensione-->
						<q><?=$reviewfile[0]?></q>
					</p>
					<p class="critic">
						<img src="http://www.cs.washington.edu/education/courses/cse190m/11sp/homework/2/critic.gif" alt="Critic" />
						<!--stampo autore-->
						<?=$reviewfile[2]?><br/>
						<?=$reviewfile[3]?>
					</p>
					<!--controllo colonna-->
					<?=column($length-1,$i);?>
					<?php		
						}
					?>
				</div>
			</div>
			<!--stampo il numero di recensioni-->
			<div id="botbar"><p>(1-<?=$length.") of ".$length?></p></div>	
		</div>
		<div id="validator">
			<a href="http://validator.w3.org/check/referer"><img src="http://www.cs.washington.edu/education/courses/cse190m/12sp/homework/4/w3c-html.png" alt="Validate HTML"/></a><br/>
			<a href="http://jigsaw.w3.org/css-validator/check/referer"><img src="http://jigsaw.w3.org/css-validator/images/vcss" alt="Valid CSS!" /></a>
		</div>
	</body>
</html>
