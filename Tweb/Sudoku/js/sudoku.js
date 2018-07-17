"use strict";

(function (){

/*variabili globali per capire quale casella si può scivere, 
quante caselle sono piene e il tempo trascorso in secondi*/
var boxclicked=undefined;
var filled;
var time;

window.onload=setUpGame;

//variabile che conterrà il timer
var mytime;

//funzione che crea la matrice, riempie la tabella, gestisce l'input da tastiera e mostra il timer
function setUpGame(){
	var sudoku = genSudoku();
	filled=0;
	time=0;
	fillTable(sudoku);
	addListener();
	showTime(time);
	startTime();
}

function addListener(){
	window.onkeydown=writeNum;
	$("pause").onclick=pause;
	$("back").onclick=back;
}

//funzione che fa partire il timer
function startTime(){
	mytime=setInterval(function() { time++; showTime(time);}, 1000);
}

//funzione che ferma il timer
function stopTime(){
	clearInterval(mytime);
}

//funzione che crea la matrice la riempie con numeri validi casuali e ne cancella alcuni a caso
function genSudoku(){
	var sudoku = [];

	for(var i = 0; i < 9; i++){
		sudoku[i] = [];
	}

	genRanSudoku(sudoku);
	
	//scommentare se si vuole vedere nella console la tabella originale
	//console.log("Sudoku complete: "+sudoku);
	
	ranTable(sudoku);

	return sudoku;
}

/*funzione che genera una lista di 9 liste di numeri da 1 a 9.
 Le liste verranno utilizzate per le righe, le colonne e i quadrati*/
function genAvailables(){
	var availables = [];

	for(var i = 0; i < 9; i++){
		availables[i] = [];
		replenishList(availables[i]);
	}

	return availables;
}

//funzione che riempie una lista con numeri da 1 a 9
function replenishList(available){
	for (var i = 0; i < 9; i++) {
		available.push(i+1);
	}
}

/*funzione che genera una lista di 81 liste vuote 
per memorizzare i tentavi fatti su una casella*/
function genTrieds(){
	var tried = [];

	for (var i = 0; i < 81; i++) {
		tried[i]=[];
	}
	return tried;
}

//la funzione che genera una tabella 9x9 che rispetta i vincoli del Sudoku
function genRanSudoku(sudoku,trieds,squares){
	//variabili per tenere traccia degli indici nelle liste
	var index,square,sindex,tindex,rindex,cindex,tried;

	//una variabile booleana per memorizzare se c'è stato conflitto o no
	var conflict;
	
	//lista che contiene i numeri da 1 a 9 da provare nella casella corrente
	var nlist=[];
	
	//lista di 9 liste con numeri da 1 a 9 per verificare i vincoli
	var squares = genAvailables();
	var rows = genAvailables();
	var cols = genAvailables();

	//lista di 81 liste vuote che memorizzerà i tentativi effettuati su una casella
	var trieds = genTrieds();

	//ciclo la tabella 9x9 casella per casella, riga per riga
	for (var row = 0; row < 9; row++) {
		for (var col = 0; col < 9; col++) {
			//riempio la lista per la casella corrente
			replenishList(nlist);

			/*verifico in quale quadrato la casella corrente si trova
			 per poter tracciare il quadrato corretto nella lista squares*/
			square=findSquare(row+1,col+1)-1;
			conflict = true;

			//indice della lista tried va da 0 a 80
			tried=(row*9)+col;

			do{
				/*Scelgo un numero a caso come indice della lista dei numeri da 1 a 9
				 della casella corrente*/
				index = parseInt(Math.random() * nlist.length-1,10);
				
				/*Con la funzione indexOf verifico in ogni lista se il numero scelto 
				 è ancora disponibile*/
				sindex = squares[square].indexOf(nlist[index]);
				rindex = rows[row].indexOf(nlist[index]);
				cindex = cols[col].indexOf(nlist[index]);

				//Nella lista tried invece verifico che non ci sia
				tindex = trieds[tried].indexOf(nlist[index]);

				/*Se il numero scelto è presente nelle liste del quadrato, della riga e della colonna
				 e invece non è presente nella lista dei numeri provati, allora il numero può essere
				 inserito nella tabella.
				 Altrimenti il numero viene inserito nella lista dei provati 
				 e tolto da quella che contiene i numeri ancora da provare*/

				if (sindex != -1 && rindex != -1 && cindex != -1 && tindex == -1) {
					conflict = false;
					//console.log(nlist);
				} else {
					//console.log("bad element: "+ nlist[index] + " square index: "+ sindex+" row index: "+ rindex+" col index: "+ cindex+ " tried index: "+ tindex);
					//console.log("trieds: "+trieds[tried]);
					trieds[tried].push(nlist[index]);
					nlist.splice(index,1);			
				}				

			//il ciclo termina se ho finito i numeri possibili o se non c'è stato conflitto
			} while(nlist.length > 0 && conflict); 

			/*Se ci sono ancora numeri nella lista dei disponibili,
			significa che non c'è stato conflitto, allora tolgo il numero scelto
			dalla lista della riga, della colonna e del quadrato
			e lo aggiungo invece alla lista di quelli provati.
			Altrimenti significa che gli ho provati tutti e sono costretto a tornare indietro
			e provare un'altra combinazione di numeri*/

			if(nlist.length > 0){
				//Inserisco il numero nella tabella
				squares[square].splice(sindex,1);
				rows[row].splice(rindex,1);
				cols[col].splice(cindex,1);
				trieds[tried].push(nlist[index]);
				sudoku[row][col]=nlist[index];
				nlist=[];
			} else {
				//Torno indietro
				//svuoto la lista dei provati
				trieds[tried]=[];
				
				//Decremento gli indici di colonna e riga della matrice
				if(col==0){
					col=8;
					if(row>0){row-=1;}
				} else {
					col--;
				}

				//ricalcolo indice del quadrato e della lista dei provati
				square=findSquare(row+1,col+1)-1;
				tried=(row*9)+col;

				//metto il numero nella lista dei provati
				trieds[tried].push(sudoku[row][col]);

				//rimetto il numero nella lista del quadrato, della riga e della colonna
				squares[square].push(sudoku[row][col]);
				rows[row].push(sudoku[row][col]);
				cols[col].push(sudoku[row][col]);

				//Decremento gli indici ancora di uno perchè il for incrementa di uno un'altra volta
				if(col==0){
					col=8;
					if(row>0){row-=1;}
				} else {
					col--;
				}
			}
		}
	}
}

//funzione che elimina a random alcuni numeri della tabella in base al livello di difficoltà
function ranTable(sudoku){
	var row,col,square,boxcount,sqrow,sqcol;
	var level = getCookie("level");
	
	//nel caso il cookie non sia settato o sia corrotto imposto il livello medio
	if(!level || !level.match(/(easy|medium|hard)/)){
		level="medium";
		setCookie("level", "medium", 1);
	}

	//console.log(level);

	square=0;
    
    //per ogni quadrato, rimuovo un certo numero di numeri in base alla difficoltà
	while(square < 9) {
		//quanti numeri togliere in base al livello
		boxcount = box(level);

		//offset riga colonna
		sqrow= parseInt(square/3,10) * 3;
		sqcol= (square%3) * 3;

		//rimuovo numeri
		while(boxcount > 0){
			//scelgo un numero a caso nel quadrato
			row=parseInt(Math.random() * 3,10)+sqrow;
			col=parseInt(Math.random() * 3,10)+sqcol;
			
			//lo rimuovo se non è già stato rimosso
			if(sudoku[row][col]){
				sudoku[row][col]=undefined;
				boxcount--;
			}
		}
		square++;
	}
}

//funzione che restituisce quanti numeri togliere in base al livello
function box(level){
	var base, ran;
	
	if(level == "easy"){
		base = 5;
		ran = 2;
	} else if (level == "medium"){
		base = 5;
		ran = 3;
	} else {
		base = 6;
		ran = 2;
	}

	return parseInt(Math.random() * ran,10)+base;
}


//funzione che date le coordinate riga e colonna determina in che quadrato la casella si trova
function findSquare(row, col){
	var x=row/3;
	var y=col/3;
	var sq;
	
	if(x<=1){
		if(col%3==0){
			sq=parseInt(y,10);
		} else{
			sq=parseInt(y,10)+1;
		}
	} else if(x<=2){
		if(col%3==0){
			sq=parseInt(y,10)+3;
		} else{
			sq=parseInt(y,10)+4;
		}
	} else{
		if(col%3==0){
			sq=parseInt(y,10)+6;
		} else{
			sq=parseInt(y,10)+7;
		}
	}
	//console.log("row: "+row+" col: "+col+" square: "+sq);
	return sq;
}

//funzione che riempie la tabella con i numeri calcolati
function fillTable(sudoku){
	var rows=document.getElementsByTagName("tr");
	var cols;
		
	for(var i = 0; i < rows.length; i++){
		cols=rows[i].getElementsByTagName("td");

		for (var j = 0; j < cols.length; j++) {
			cols[j].id=i.toString()+j;
			
			//scelgo se la casella sarà modificabile o no
			if(sudoku[i][j]){
				cols[j].innerHTML=sudoku[i][j];
				cols[j].className="fixed";
				filled++;
			} else {
				cols[j].onclick=modify;
				cols[j].className="modifiable";
				cols[j].innerHTML="";
			}
		}
	}
}

//funzione che attiva la modifica di una casella
function modify(){
	var row=parseInt(this.id[0],10);
	var col=parseInt(this.id[1],10);

	//se qualche casella era selezionata allora coloro di bianco le caselle
	if(boxclicked){
		whiteSquares();	
	}
	//coloro le caselle che sono nella stessa riga e colonna della casella cliccata
	colorSquares(row,col);
	boxclicked=this;
}

//funzione che scrive un numero nella casella cliccata
function writeNum(event){
	//estraggo il codice ascii
	var num= event.charCode || event.which || event.keyCode;
	
	//console.log("num: "+num+" event.which: "+event.which+" event.keycode: "+event.keyCode);
	
	//verifico che il tasto premuto sia un numero e che ci sia una casella evidenziata
	if(((num >= 49 && num <= 57) || (num >= 97 && num <= 105)) && boxclicked){
		//se la casella è vuota allora incremento il numero di caselle riempite
		if(boxclicked.innerHTML==""){
			filled++;
		}
		//inserisco il numero, coloro di bianco le caselle e verifico se il giocatore ha vinto
		boxclicked.innerHTML=getNum(num);
		whiteSquares();
		boxclicked=undefined;
		winCheck();
	} else if(num==46 && boxclicked){
		/*se è stato premuto il tasto canc e una casella è selezionata
		allora rimuovo un numero se presente e coloro di bianco le caselle*/
		if(boxclicked.innerHTML!=""){
			filled--;
		}
		boxclicked.innerHTML="";
		whiteSquares();
		boxclicked=undefined;
	}
}

//funzione che calcola quale numero è stato digitato partendo dal codice ascii
function getNum(num){
	if(num>=97){
		num=num-97+49;
	}

	return String.fromCharCode(num);
}

//funzione che colora i quadrati della riga e della colonna della casella corrente
function colorSquares(row,col){
	var rows=document.getElementsByTagName("tr");
	var cols;
		
	for(var i = 0; i < rows.length; i++){
		cols=rows[i].getElementsByTagName("td");

		for (var j = 0; j < cols.length; j++) {
			if(i==row || j==col){
				cols[j].className+=" active";
			}
		}
	}
}

//funzione che colora di bianco le caselle attive
function whiteSquares(){
	var actives=document.querySelectorAll(".active");
	//console.log("actives: "+actives.length);
	for(var i = 0; i < actives.length; i++){
		if(actives[i].className=="fixed active"){
			actives[i].className="fixed";
		} else {
			actives[i].className="modifiable";
		}
	}
}

//funzione che effettua il controllo di vittoria se tutte le caselle sono piene
function winCheck(){
	if(filled == 81){
		//se la tabella è valida allora mando le statistiche al server e mostro il messaggio di vittoria
		if(gridCheck()){
			sendStatistics(time);
			displayMsg("You Win", ["Restart","Home"], [restart,home]);
			stopTime();
		} else {
		//se la tabella non è valida allora la faccio tremare
			document.getElementById("sud_table").shake();
		}
	}
}

//funzione che controlla se la tabella compilata dall'utente è valida
function gridCheck(){
	var squares = genAvailables();
	var rows = genAvailables();
	var cols = genAvailables();
	var tr,td,square,sindex,rindex,cindex;

	//controllo casella per casella riga per riga se i numeri inseriti sono validi
	for (var row = 0; row < 9; row++) {
		tr=document.getElementsByTagName("tr");
		//estraggo una riga dalla tabella
		td=getRow(tr[row]);
		
		for (var col = 0; col < 9; col++) {
			
			square=findSquare(row+1,col+1)-1;
			sindex = squares[square].indexOf(td[col]);
			rindex = rows[row].indexOf(td[col]);
			cindex = cols[col].indexOf(td[col]);

			/*se il numero che sto esaminando è presente in tutte le liste allora
			 allora il numero è valido e posso rimuoverlo dalle liste*/
			if (sindex != -1 && rindex != -1 && cindex != -1) {
				squares[square].splice(sindex,1);
				rows[row].splice(rindex,1);
				cols[col].splice(cindex,1);		
			} else {
				return false;
			}
		}
	}

	return true;
}

//funzione che estrae dalla tabella una riga di numeri
function getRow(tr){
	var row;
	var elements =[];
		
	row=tr.getElementsByTagName("td");

	for (var i = 0; i < row.length; i++) {
		elements.push(parseInt(row[i].innerHTML,10));
	}

	return elements;
}

//funzione che mostra il tempo
function showTime(time){
	var timer = $("time");
	var h,m,s;
	
	//calcolo ore minuti e secondi
	s=time%60;
	m=parseInt(time/60,10)%60;
	h=parseInt(time/3600,10)%3600;

	if(h<10){
		h='0'+h;
	}

	if(m<10){
		m='0'+m;
	}

	if(s<10){
		s='0'+s;
	}
	//aggiorno il timer
	timer.innerHTML=h+":"+m+":"+s;
}

//funzione che mostra un messaggio in un riquadro e associa a ogni bottone una funzione
function displayMsg(msg,buttons,functions){
	var background=document.createElement("div");
	var div=document.createElement("div");
	var h1 = document.createElement("h1");
	var but;

	
	background.id="background";
	div.id="message";

	h1.innerHTML=msg;
	div.appendChild(h1);

	//creo i bottoni e associo l'etichetta e la funzione
	for (var i = 0; i < buttons.length; i++) {
		console.log(buttons[i]);
		but=document.createElement("button");
		but.innerHTML=buttons[i];
		but.onclick=functions[i];
		div.appendChild(but);
	}

	document.body.appendChild(div);
	document.body.appendChild(background);
}

//funzione che mette in pausa e mostra un messaggio
function pause(){
	displayMsg("Pause",["Resume","Restart","Home"], [resume,restart,home]);
	stopTime();
}

//funzione che toglie il messaggio e riprende il gioco
function resume(){
	$("background").remove();
	$("message").remove();
	startTime();
}

//funzione che resetta il gioco
function restart(){
	$("background").remove();
	$("message").remove();
	stopTime();
	setUpGame();
}

//funzione che esce dal gioco
function home(){
	window.location.href="index.php";
}

//funzione che gestisce la pressione del tasto back
function back(){
	stopTime();
	displayMsg("Are you sure?",["Yes","No"], [home,resume]);
}

//funzione che fa una richiesta ajax per inviare le statistiche della partità
function sendStatistics(time){
	var user=getCookie("user");
	var level=getCookie("level");

	new Ajax.Request("./php/statistics.php",
		{
			method: "post",
			parameters: {username:user, times:time, levels:level},
			onSuccess: ajaxOK,
			onFailure: ajaxFailure,
			onException: ajaxFailure
		}
	);
}

function ajaxOK(ajax){
	var data = JSON.parse(ajax.responseText);
	console.log(data);
}
})();