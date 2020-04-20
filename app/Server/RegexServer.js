const bodyParser = require('body-parser');
const express = require('express');
const HashMap = require('hashmap')


const app = express();
app.use(bodyParser.urlencoded({limit:'50mb',extended: true}));

const listMusic = ['waka.mp3','fellgood.mp3', "babygirl.mp3"];

/** Fonction main, ctach un requête POST **/
app.post('/', async function(req,res) {

    if(req.body.input == null) res.status(400).send('Aucun signal d\'entrée');
    else {
        let input = req.body.input;     //String retourner par le serveur de Reco
        let running = req.body.running; //String contenant le titre de la musique en cours sinon null

        /** On va tester ici si l'utilisateur veut lancer une musique **/
        if(input.includes('écout') || input.includes('lanc') ) {
            let music = await findMusic(input);
            if(music == null) {res.status(404).send('Musique non trouver');}

            else {res.status(200).json({cmd:0,music:music});}

        } //else {if(running == null) {res.status(404).send('Aucune musique en cours');}}

        /** PAUSE **/
        if(input.includes('pause')) {
            res.status(200).json({cmd:2}); //TODO a modifier
        }

        /** Volume **/
        if(input.includes('son') || input.includes('volume')) {
            /** Augmentation du volume**/
            if(input.includes('monte') || input.includes('augmente')) {
                res.status(201).send('+1');
            }
            /** Reduction du volume**/
            if(input.includes('baisse') || input.includes('réduit') || input.includes('diminue')) {
                res.status(201).send('-1');
            }
        }

        /** Avancer ou reculer la musique **/
        //TODO trop chiant

        /** STOP **/
         if(input.includes('arrête la musique')) {
            res.status(200).json({cmd:1});
         }

    }
});

app.listen(3212, () => {
    console.log("Regular expression service starting on port : 3212");
    init();
});

/** Fonction permettant de chercher un music dans un hashmap et si un correspondance est trouver elle retourne sa value **/
function findMusic(input) {
    input = input.toLowerCase();
	return new Promise(resolve => {
    	var s;

        if(input.includes("baby")) {s = listMusic[2];}
        if(input.includes("waka")) {s = listMusic[0];}
        if(input.includes("feel")) {s = listMusic[1];}
        if(input.includes("good")) {s = listMusic[1];}

        resolve(s);
	});
}
/** Initialise les musiques disponibles **/
function init() {
}