const speech = require('@google-cloud/speech');
const fs = require('fs');
const bodyParser = require('body-parser');
const express = require('express');


const app = express();
app.use(bodyParser.urlencoded({limit:'50mb',extended: true}))

app.post('/', async function(req,res) {

    let filename = "input.wav";
    let dataWav = req.body.wav;

    let transcription = await main(dataWav);

    console.log(transcription);
    res.send(transcription);

    res.on('finish', function() {
    		removeFile(filename);
    });

    res.on('error', function() {
    		removeFile(filename);
    });
});

app.listen(3211, () => {
    console.log("Recognition service starting on port : 3211");
});

async function main(dataWav) {

  // The name of the audio file to transcribe
  const fileName = 'input.wav';

  await saveFile(dataWav,fileName);

  // Creates a client
  const client = new speech.SpeechClient();


  // Reads a local audio file and converts it to base64
  const file = fs.readFileSync(fileName);
  const audioBytes = file.toString('base64');

  // The audio file's encoding, sample rate in hertz, and BCP-47 language code
  const audio = {
    content: audioBytes,
  };
  const config = {
    encoding: 'LINEAR16',
    sampleRateHertz: 16000,
    languageCode: 'fr-FR',
  };
  const request = {
    audio: audio,
    config: config,
  };

  // Detects speech in the audio file
  const [response] = await client.recognize(request);
  const transcription = response.results
    .map(result => result.alternatives[0].transcript)
    .join('\n');

  console.log(`Transcription: ${transcription}`);
  return transcription;
}

/** Permet de sauvegarder les wav venant du client **/
function saveFile(buffer, nameFile) {
  return new Promise(resolve => {
    let dataWav = buffer.replace('data:audio/wav; codecs=opus;base64,', '');
      fs.writeFile(nameFile, dataWav, {encoding: 'base64'}, async function(err) {
          //console.log('File created');
      resolve(true);
      });
  });
}

/** Permet de supprimer les wav contenu sur le serveur **/
function removeFile(listnameFile) {
  listnameFile.forEach(element => fs.unlinkSync(PATH+element+".wav"))
}
