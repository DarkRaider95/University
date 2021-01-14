"use strict";

(function (){

window.onload=function() {
	//faccio la richiesta per capire quante pagine sono necessarie per mostrare la classifica
	askPages("easy");
	addListener();
	//richiedo i dati della classifica
	searchEntries("easy",0);
};

//funzione che effettua la richiesta ajax per il numero di pagine
function askPages(level){
	new Ajax.Request("./php/countPages.php",
		{
			method: "post",
			parameters: {levels:level},
			onSuccess: fillPages,
			onFailure: ajaxFailure
		}
	);
}

//funzione che riempie la select con il numero di pagine
function fillPages(ajax){
	var data = JSON.parse(ajax.responseText);

	if(data.status){
		//calcolo quante pagine
		var pages=Math.ceil(data.count/30);
		var select=$("pages");

		//se ci sono pagine aggiungo i numeri
		if(pages > 0){
			for (var i = 1; i <= pages; i++) {
				var option=document.createElement("option");
				option.innerHTML=i;
				select.appendChild(option);
			}
		} else {
			removeContent($("page"));
		}
	} else {
		removeContent($("page"));
	}
}

//funzione che aggiunge i listener
function addListener(){
	var buttons = $$("li");

	for (var i = 1; i < buttons.length; i++) {
		buttons[i].onclick=changeTab;
	}

	if($("pages")){
		$("pages").onchange=changePage;
	}

	$("back").onclick=back;
}

//funzione che cambia scheda
function changeTab(){
	changeButStat();
	this.className="clicked";
	this.onclick=undefined;
	refreshPages(this.id);
	removeContent($("content"));
	searchEntries(this.id,0);
}

//funzione che cambia lo stato dei bottoni
function changeButStat(){
	var tab = document.getElementsByClassName("clicked")[0];

	tab.onclick=changeTab;
	tab.className="unclicked";
}

//funzione che cambia la pagina quindi aggiorna la tabella da mostrare
function changePage(){
	var level = document.getElementsByClassName("clicked")[0].id;
	var page = this.options[this.selectedIndex].value;
	var start = (parseInt(page,10)-1)*30;
	removeContent($("content"));
	searchEntries(level,start);
}

//funzione che rimuove il contenuto da un oggetto
function removeContent(container){
	
	while(container.children.length > 0){
		container.children[0].remove();
	}
}

//funzione che ricalcola il numero di pagine
function refreshPages(level){
	//se c'è la select rimuovo i numeri presenti se no creo la select
	if($("pages")){
		var options = $$("option");
	
		for (var i = 0; i < options.length; i++) {
			options[i].remove();
		}	
	} else {
		var select = document.createElement("select");
		var span = document.createElement("span");

		select.id="pages";
		select.onchange=changePage;
		span.innerHTML="Page: ";
		
		$("page").appendChild(span);
		$("page").appendChild(select);
	}
	//richiedo il numero di pagine e aggiorno la select
	askPages(level);
}

//funzione che effettua la richiesta ajax per i dati da inserire nella classifica
function searchEntries(level,starts){
	new Ajax.Request("./php/rankings.php",
		{
			method: "post",
			parameters: {levels:level,start:starts},
			onSuccess: printTable,
			onFailure: ajaxFailure
		}
	);
}

//funzione che stampa la classifica
function printTable(ajax){
	var data = JSON.parse(ajax.responseText);

	//se ci sono i dati li mostro se no mostro il messaggio di errore
	if(data.values){
		//creo la tabella e inserisco i dati
		var table=document.createElement("table");
		var tr,td,values;

		var start = parseInt(data.start,10);
		var th=createTable(table);

		for (var i = 0; i < data.entries.length; i++,start++) {
			tr=document.createElement("tr");
			table.appendChild(tr);

			//estraggo i valori dai dati della risposta ajax
			values = extractData(data.entries[i],start+1);
			
			for (var j = 0; j < th; j++) {
				td=document.createElement("td");
				
				td.innerHTML=values[j];
				
				tr.appendChild(td);
			}
		}
	} else {
		var div = document.createElement("div");
		var p = document.createElement("p");

		p.innerHTML=data.error;
		div.appendChild(p);
		$("content").appendChild(div);
	}
}

//funzione che crea una tabella
function createTable(table){
	var headers = ["Position","Username","Time"];
	var thn=3;
	
	$("content").appendChild(table);

	for (var i = 0; i < thn; i++) {
		var th=document.createElement("th");
		th.innerHTML=headers[i];
		table.appendChild(th);
	}

	return thn;
}

//funzione che estrae i dati
function extractData(entries,position){
	var values=[];

	values.push(position);
	values.push(entries["username"]);
	values.push(showTime(entries["times"]));

	return values;
}

//funzione che calcola il tempo in ore minuti e secondi partendo da un tempo espresso in secondi
function showTime(time){
	var h,m,s;
	
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

	return h+":"+m+":"+s;
}

function back(){
	window.location.href="index.php";
}
})();