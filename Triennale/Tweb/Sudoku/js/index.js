"use strict";

(function (){
//aggiungo i listener e controllo se ci sono messaggi
window.onload=function() {
	addListener();
	checkMessage($("container"),$$("button")[0]);
};

//funzione che aggiunge i listener
function addListener(){
	$("play").onclick=play;
	$("logout").onclick=logout;
	$("ranking").onclick=ranking;
}

//funzione che gestisce il click del bottone play
function play(){
	var container = $("container");
	var levels = ["Easy","Medium","Hard","Back"];
	var button;

	//rimuovo tutti i bottoni
	removeButtons();

	//aggiungo i bottoni per la scelta del livello difficoltà
	for (var i = 0; i < levels.length; i++) {
		button=createButton(levels[i]);
		container.appendChild(button);
	}

	button.onclick=back;
}

//funzione che rimuove i bottoni
function removeButtons(){
	var buttons=$$("button");

	for (var i = 0; i < buttons.length; i++) {
		buttons[i].remove();
	}

	if($("message")){
		$("message").remove();
	}
}

//funzione che crea un bottone con etichetta e id la stringa scelta
function createButton(str){
	var button=document.createElement("button");
	
	button.innerHTML=str;
	button.id=str.toLowerCase();
	button.onclick=startGame;

	return button;
}

//funzione che gestisce il click del bottone back
function back(){
	var container = $("container");
	var buttons = ["Play","Ranking", "Logout"];
	var button;

	//rimuovo i bottoni
	removeButtons();

	//mostro i bottoni iniziali
	for (var i = 0; i < buttons.length; i++) {
		button=createButton(buttons[i]);
		container.appendChild(button);
	}

	//riaggiungo i listener
	addListener();
}

//funzione che gestisce il click di un bottone del livello di difficoltà
function startGame(){
	//imposto un cookie con il livello di difficoltà
	setCookie("level", this.id, 1);
	window.location.href="sudoku.php";
}

function logout(){
	window.location.href="logout.php";
}

function ranking(){
	window.location.href="ranking.php";
}
})();