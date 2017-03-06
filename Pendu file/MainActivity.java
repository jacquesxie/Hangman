package com.jacquesxie.hangman;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout container;
    private Button btn_send;
    private TextView lettre_tapees;
    private ImageView image;
    private EditText et_letter;
    private String word;
    private int found;
    private int error;
    private List<Character> listOfLetters = new ArrayList<>();
    private boolean win;
    private List<String> wordList = new ArrayList<>();

    MediaPlayer mySound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        // permet de mettre la musique
        mySound = MediaPlayer.create(this, R.raw.song);
        mySound.start();
*/
        container = (LinearLayout) findViewById(R.id.word_container);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_letter = (EditText) findViewById(R.id.et_letter);
        image = (ImageView) findViewById(R.id.iv_pendu);
        lettre_tapees = (TextView) findViewById(R.id.tv_lettre_tapees);

        initGame();
        btn_send.setOnClickListener(this);
    }

    public void initGame(){

        word = generateWord();
        win = false;
        error = 0;
        found = 0;
        listOfLetters = new ArrayList<>();
        lettre_tapees.setText("");
        // image par défaut
        image.setBackgroundResource(R.drawable.first);

        container.removeAllViews();

        for(int i = 0; i < word.length(); i++){
            TextView oneLetter = (TextView) getLayoutInflater().inflate(R.layout.textview, null);
            container.addView(oneLetter);
        }
    }

    // un message pop pup
    @Override
    public void onClick(View view) {
        //Toast.makeText(getApplicationContext(), "Salut", Toast.LENGTH_SHORT).show();
        String letterFromInput = et_letter.getText().toString().toUpperCase();
        // pour ne pas laisser la lettre qd on sélectionne
        et_letter.setText("");

        if(letterFromInput.length() > 0){
            if(!letterAlreadyUsed(letterFromInput.charAt(0), listOfLetters)){
                listOfLetters.add(letterFromInput.charAt(0));
                // vérifie les lettres du mot
                checkIfLetterIsInWord(letterFromInput, word);
            }
            // la partie est gagné
            if(found == word.length()){
                win = true;
                createDialog(win);
            }
            //la lettre est pas dans le mot
            if(!word.contains(letterFromInput)){
                error++;
            }
            // les différents cas pour le pendu
            setImage(error);
            // la partie est perdu
            if(error == 6){
                win = false;
                createDialog(win);
            }
            //Affichage des lettres entrées
            showAllLetters(listOfLetters);


        }
    }

    public boolean letterAlreadyUsed(char a, List<Character> ListOfLetters ){

        for(int i = 0; i < listOfLetters.size(); i++){
            //Test pour ne pas entrer deux fois la même lettre
            if(listOfLetters.get(i) == a){
                Toast.makeText(getApplicationContext(), "You already used this letter!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public void checkIfLetterIsInWord(String letter, String word){

        for(int i = 0; i < word.length(); i++){
            if(letter.equals(String.valueOf(word.charAt(i)))){
                TextView tv = (TextView) container.getChildAt(i);
                tv.setText(String.valueOf(word.charAt(i)));
                found++;
            }
        }
    }

    public void showAllLetters(List<Character> listofLetters){
        String chaine = "";

        for(int i = 0; i < listOfLetters.size(); i++){
            chaine += listofLetters.get(i) + "\n";
        }
        if(!chaine.equals("")){
            lettre_tapees.setText(chaine);
        }
    }

    public void setImage(int error){

        switch(error){
            case 1 :
                image.setBackgroundResource(R.drawable.second);
                break;
            case 2 :
                image.setBackgroundResource(R.drawable.third);
                break;
            case 3 :
                image.setBackgroundResource(R.drawable.fourth);
                break;
            case 4 :
                image.setBackgroundResource(R.drawable.fifth);
                break;
            case 5 :
                image.setBackgroundResource(R.drawable.sixth);
                break;
            case 6 :
                image.setBackgroundResource(R.drawable.seventh);
                break;
        }
    }

    public void createDialog(boolean win){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You win!");

        if(!win){
            builder.setTitle("GAME OVER");
            builder.setMessage("The answer was : " + word);
        }
        builder.setPositiveButton(getResources().getString(R.string.play_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initGame();
            }
        });
        builder.create().show();
    }

    // récupérer les mots lignes par lignes dans el fichier txt
    public List<String> getListOfWords(){

        try{
            BufferedReader buffer = new BufferedReader(new InputStreamReader(getAssets().open("pendu_liste.txt")));
            String line;
            while((line = buffer.readLine()) != null){
                wordList.add(line);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return wordList;
    }

    //génération aléatoire pour les mots
    public String generateWord(){
        wordList = getListOfWords();
        int random = (int) (Math.floor(Math.random() * wordList.size()));
        String word = wordList.get(random).trim();
        return word;
    }

}
