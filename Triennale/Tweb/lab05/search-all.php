<?php
#pagina che fa la ricerca dei film di un attore
require_once("common.php");
ensure_logged_in();
require_once("top.php");
?>

<h1>Results for <?=$_GET['firstname']." ".$_GET['lastname']?></h1>
<p>Films with <?=$_GET['firstname']." ".$_GET['lastname']?></p>
<?=print_films($_GET['firstname'],$_GET['lastname'], false)?>

<?php require_once("bottom.html"); ?>