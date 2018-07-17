<?php include("top.html"); ?>
<!--Tommaso Toscano Corso B Es 4 esercizio php form, pagina gestione submit match-->
<?php
#funzione che ricerca le informazioni relative all'utente che fa la ricerca
function findme($singles){
	foreach ($singles as $single) {
		list($name,$gender,$age,$type,$OS,$low,$up)=explode(",",$single);
		if(strcmp($_GET["Name"], $name)==0){
			return $single;
		}
	}
}

#funzione che riconosce se il tipo dell'utente corrisponde con l'attuale persona presa in esame
function righttype($utype, $type){
	for ($i=0; $i < 4; $i++) { 
		if(strcmp($utype[$i], $type[$i])==0){
			return true;
		}
	}
	return false;
}

#funzione che controlla la preferenza del gender
function gendercheck($uSG, $gender){
	if(strcmp($uSG, "B") == 0){
		return true;
	} else if (strcmp($uSG, $gender)==0){
		return true;
	} else{
		return false;
	}
}

#funzione che crea un'array di match cioè di persone che rispettano i parametri ricercati dall'utente
function matches(){
	#estrazione informazioni dal file singles.txt e estrazione dati utente
	$singles=file("singles.txt",FILE_IGNORE_NEW_LINES);
	$user=findme($singles);
	list($uname,$ugender,$uage,$utype,$uOS,$ulow,$uup,$uSG)=explode(",",$user);

	#controllo quali utenti rispettano i parametri
	foreach ($singles as $single) {
		list($name,$gender,$age,$type,$OS,$low,$up,$SG)=explode(",",$single);
		
		#non faccio il test sull'utente stesso
		if(strcmp($uname, $name)!=0){
			if(gendercheck($uSG, $gender)){
				if($age>=$ulow and $age<=$uup){
					if(strcmp($uOS, $OS)==0){
						if(righttype($utype,$type)){
							$matches[]=$single;
						}
					}
				}
			}
		}
	}

	#se non ci sono match restituisco false
	if(isset($matches)) return $matches;
	else return false;
}
?>

<h1>Matches for <?=$_GET["Name"]?></h1>

<?php
#estraggo i match e se ci sono match stampo la lista, altrimenti forever alone
$matches=matches();
if($matches){
	#per ogni match stampo le sue informazioni
	foreach ($matches as $match) {
 		list($name,$gender,$age,$type,$OS,$low,$up)=explode(",",$match);
?>

<div class="match">
	<img src="http://courses.cs.washington.edu/courses/cse190m/12sp/homework/4/user.jpg" alt="User Image">
	<p><?=$name?></p>
	<ul>
		<li><strong>gender:</strong><?=$gender?></li>
		<li><strong>Age:</strong><?=$age?></li>
		<li><strong>Type:</strong><?=$type?></li>
		<li><strong>OS:</strong><?=$OS?></li>
	</ul>
</div>

 <?php
 	}
} else {
?>
	<img src="forever_alone.png" alt="FOREVER ALONE">
<?php
}
?>

<?php include("bottom.html"); ?>