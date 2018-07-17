"use strict";
//Codice javascript per il puzzle

//variabili globali per tenere traccia della cella vuota
var empty_x,empty_y;

window.onload=setUpGame;

//setupGame imposta il puzzle, seleziona tutti i quadrati, li riposiziona, imposta il background di ognuno,
//setta i primi quadrati spostabili e aggiunge l'evento onclick sul bottone shuffle
function setUpGame(){
	var squares = document.querySelectorAll("#puzzlearea div");
	var i = 0;

    for (var row = 0; row < 4; row++) {
      for(var column = 0; column < 4 && i < 15; column++){
      	squares[i].style.left = column * 100 + 'px';
      	squares[i].style.top= row*100 + 'px';
      	squares[i].style.backgroundPosition= column * (-100) + 'px ' + row* (-100) + 'px';
      	i++;
      }
    }

    squares[11].className="movable";
    squares[11].onclick=moves;
    squares[14].className="movable";
    squares[14].onclick=moves;
    $('controls').onclick=randPuzzle;
    empty_x='300px';
    empty_y='300px';
}

/*sposta il quadrato cliccato, scambia la posizione con quella della casella vuota.
il parametro rand è utilizzato per evitare di eseguire wincheck*/

function moves(square){
	move(this,false);
}

function move(square,rand){
	var swp;
	
	removeMove();

	if(square.style.left==empty_x){
		swp=square.style.top;
		square.style.top=empty_y;
		empty_y=swp;
	} else {
		swp=square.style.left;
		square.style.left=empty_x;
		empty_x=swp;
	}

	addMove();
	
	if(!rand) {winCheck();}
}

//removeMove seleziona i quadrati spostabili e rimuove la classe movable
//e l'evento onclick
function removeMove(){
	var squares = document.querySelectorAll(".movable");
	for (var i = 0; i < squares.length; i++) {
		squares[i].classList.remove("movable");
		squares[i].onclick=null;
	}
}

/*addMove aggiunge la classe movable e l'evento onclick solo ai quadrati adiacenti alla casella vuota.
L'if dentro il for è complesso, verica se la casella esaminata, si trova sulla stessa colonna della casella
vuota o sulla stessa riga, in quel caso controlla se si trova rispettivamente sopra o sotto oppure a destra o
a sinistra della casella vuota.*/
function addMove(){
	var empty_c=parseInt(empty_x);
	var empty_r=parseInt(empty_y);
	var squares = document.querySelectorAll("#puzzlearea div");

	for(var i=0; i<squares.length; i++){
		if(((parseInt(squares[i].style.left) == empty_c)&&
				(parseInt(squares[i].style.top) == empty_r+100||
					parseInt(squares[i].style.top) == empty_r-100))||
		   ((parseInt(squares[i].style.top) == empty_r)&&
				(parseInt(squares[i].style.left) == empty_c+100||
					parseInt(squares[i].style.left) == empty_c-100))){

			squares[i].className="movable";
			squares[i].onclick=moves;
		}
	}
}

//randPuzzle effettua da 50 fino a un massimo di 1050 spostamenti a caso, scegliendo solo le caselle spostabili.
function randPuzzle(){
	var squares;
	var rand = parseInt(Math.random() * 1000)+50;
	var m;

	for(var i=0; i<rand; i++){
	 	squares = document.querySelectorAll(".movable");
		m=parseInt(Math.random() * squares.length);
		move(squares[m],true);
	}
}

//winCheck controlla se il giocatore ha vinto, verificando se ogni casella si trova nella posizione corretta
//cioè quella iniziale
function winCheck(){
	var squares = document.querySelectorAll("#puzzlearea div");
	var win = true;	
	var i = 0;

    for (var row = 0; row < 4; row++) {
      for(var column = 0; column < 4 && i < 15; column++){
      	if(parseInt(squares[i].style.left) != column * 100 || parseInt(squares[i].style.top) != row*100){
      		win=false;
      		break;
      	}
      	i++;
      }
    }
    
    if(win) {alert("Hai Vinto!!!!");}
}