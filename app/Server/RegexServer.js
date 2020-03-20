const bodyParser = require('body-parser');
const express = require('express');
const HashMap = require('hashmap')


const app = express();
app.use(bodyParser.urlencoded({limit:'50mb',extended: true}));

const listMusic = new HashMap();

/** Fonction main, ctach un requête POST **/
app.post('/', async function(req,res) {
    if(req.body.input == null) res.status(400).send('Aucun signal d\'entrée');
    else {
        let input = req.body.input;     //String retourner par le serveur de Reco
        let running = req.body.running; //String contenant le titre de la musique en cours sinon null

        /** On va tester ici si l'utilisateur veut lancer une musique **/
        if(input.includes('écout') || input.includes('lanc') ) {
            let cmd = await findMusic(input);
            if(cmd == null) {res.status(404).send('Musique non trouver');}
            else {res.status(200).send(cmd+" play");} //TODO a modifier
        } else {if(running == null) {res.status(404).send('Aucune musique en cours');}}

        /** PAUSE **/
        if(input.includes('pause')) {
            let cmd = await findMusic(running);
            res.status(200).send(cmd+" pause"); //TODO a modifier
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
            let cmd = await findMusic(running);
            res.status(200).send(cmd+" stop");
         }

    }
});

app.listen(3100, () => {
    console.log("Regular expression service starting on port : 3100");
    init();
});

/** Fonction permettant de chercher un music dans un hashmap et si un correspondance est trouver elle retourne sa value **/
function findMusic(input) {
	return new Promise(resolve => {
        listMusic.forEach(function(key, value) {
            for(let title in key) {
                if(input.includes(title)) {
                    //TODO Music found !
                    revolver(listMusic.get(key));
                }
            }
        });
	});
}

/** Initialise les musiques disponibles **/
function init() {
    console.log('Initialisation !');
}