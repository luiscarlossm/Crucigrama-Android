package com.example.myapplication2;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CrosswordActivity extends AppCompatActivity {

    GridLayout target; // CW's GridLayout
    int[][] mtxId; // Matrix that contains each CharField's Id by (row, col)
    int[][] mtxChr; // Matrix that contains each character that should go in each (row,col) [CW Solution]
    int mtxSize; // Number of rows/columns of the whole matrix, including inactive cells
    int wordCount; // Number of words in the CW
    private int cellSize; // Height/Width of each cell
    FocusInfo focusInfo; // Contains info about focused cells
    Crucigrama crucigrama; // Object that contains all the words of current CW


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crossword);
        Intent i = getIntent();
        String level = i.getStringExtra("level");

//        Toast.makeText(this, level, Toast.LENGTH_SHORT).show();

        target = findViewById(R.id.myGrid);
        cellSize = evenWidth(target);
        wordCount = 4;

        crucigrama = new Crucigrama(wordCount);
        mtxSize = 7;
        mtxChr = new int[mtxSize][mtxSize];
        mtxId = new int[mtxSize][mtxSize];

        focusInfo = new FocusInfo();
        focusInfo.setFocusedOrientation(0);

        //Prueba
        final Palabra palabra1 = new Palabra(1, "CASA",
                "Lugar donde viven las personas", 1, 4, 1);
        final Palabra palabra2 = new Palabra(2, "PANAL",
                "Lugar donde viven las abejas", 2, 1, 0);
        final Palabra palabra3 = new Palabra(3, "CAS",
                "Fruta verde que se usa para hacer fresco y helados", 4, 3, 0);
        final Palabra palabra4 = new Palabra(4, "APIO",
                "Verdura larga, crujiente y verde, de bajas calorias", 1, 1, 1);

        crucigrama.insertPalabra(palabra1);
        crucigrama.insertPalabra(palabra2);
        crucigrama.insertPalabra(palabra3);
        crucigrama.insertPalabra(palabra4);

        // All this happens once the Grid size is already set
        target.post(new Runnable() {
            @Override
            public void run() {
                setCellSize();
                fillBlank();
                for (int i = 0; i < crucigrama.getLast(); i++) {
                    placeWord(crucigrama.palabraAt(i));
                }
                createKeyboard();
            }
        });


    }

    // Sets the cell size
    private void setCellSize() {
        this.cellSize = evenWidth(this.target);
    }

    // Sets a cell active (BG color, clickable, assigns listeners)
    public void activateCell(int fila, int columna, char letra) {

        CharField myEdtxt = findViewById(mtxId[fila][columna]);
        myEdtxt.setFocusable(true);
        myEdtxt.setText(String.valueOf(letra));
        myEdtxt.setFocusableInTouchMode(true);
        myEdtxt.setClickable(true);
        myEdtxt.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
        myEdtxt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    Toast.makeText(MainActivity.this, String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();
                    setWordFocus(v.getId());
                } else {
                    removeWordFocus(v.getId());
                }
            }
        });

        myEdtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isFocused()) {

                    removeWordFocus(v.getId());
                    switchOrientation();
                    setWordFocus(v.getId());
                }
            }
        });

    }

    // Change color of cell acordding to focus level)
    public void setFocusColor(int fila, int columna, int focus_lvl) {
        CharField myEdtxt = findViewById(mtxId[fila][columna]);
        switch (focus_lvl) {
            case 0:
                myEdtxt.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
                break;
            case 1:
                myEdtxt.setBackground(ContextCompat.getDrawable(this, R.drawable.focused_lvl_1));
                break;
            case 2:
                myEdtxt.setBackground(ContextCompat.getDrawable(this, R.drawable.focused_lvl_2));
                break;
        }
    }

    // Inverts the focus orientation
    public void switchOrientation() {
        if (this.focusInfo.getFocusedOrientation() == Palabra.HORIZONTAL) {
            this.focusInfo.setFocusedOrientation(Palabra.VERTICAL);
        } else {
            this.focusInfo.setFocusedOrientation(Palabra.HORIZONTAL);
        }
//        Toast.makeText(this, String.valueOf(this.focusedOrientation), Toast.LENGTH_SHORT).show();
    }

    // Sets the respective word focus
    public void setWordFocus(int id) {
        CharField edTxt = findViewById(id);
        this.focusInfo.setCharFocusedId(id);
        Palabra palabra = crucigrama.palabraAt(edTxt.getCol(), edTxt.getRow(), this.focusInfo.getFocusedOrientation());
        if (palabra == null) {
            switchOrientation();
            palabra = crucigrama.palabraAt(edTxt.getCol(), edTxt.getRow(), this.focusInfo.getFocusedOrientation());
        }
        if (palabra != null) {
            int head_row = palabra.getHeadRow();
            int head_col = palabra.getHeadColumn();
            int col = head_col;
            int row = head_row;
            int i = 0;
            this.focusInfo.setWordFocusedId(palabra.getIdx());
            while (i < palabra.getLength()) {
                if (this.focusInfo.getFocusedOrientation() == Palabra.HORIZONTAL) {
                    col = head_col + i;
                } else {
                    row = head_row + i;
                }
                setFocusColor(row, col, 1);
                i++;
            }
            setFocusColor(edTxt.getRow(), edTxt.getCol(), 2);

        } else {
            switchOrientation();
//            Toast.makeText(this, "ERROR 1", Toast.LENGTH_SHORT).show();
        }


    }

    // Removes word focus
    public void removeWordFocus(int id) {
        CharField edTxt = findViewById(id);
        Palabra palabra = crucigrama.palabraAt(edTxt.getCol(), edTxt.getRow(), this.focusInfo.getFocusedOrientation());
        if (palabra != null) {
            int head_row = palabra.getHeadRow();
            int head_col = palabra.getHeadColumn();
            int col = head_col;
            int row = head_row;
            int i = 0;
            while (i < palabra.getLength()) {
                if (this.focusInfo.getFocusedOrientation() == Palabra.HORIZONTAL) {
                    col = head_col + i;
                } else {
                    row = head_row + i;
                }
                setFocusColor(row, col, 0);
                i++;
            }
        } else {
//            Toast.makeText(this, "ERROR 2", Toast.LENGTH_SHORT).show();
        }
    }

    // Sets the char in the corresponding (row,col) of the solution matrix
    public void setOnCharMtx(int fila, int columna, Palabra palabra, int idx) {
//        Toast.makeText(this, String.valueOf(idx), Toast.LENGTH_SHORT).show();
        this.mtxChr[fila][columna] = palabra.getPalabra().charAt(idx);
    }

    // Sets a word in its corresponding cell in the grid
    public void placeWord(Palabra palabra) {
        int fila_h = palabra.getHeadRow();
        int columna_h = palabra.getHeadColumn();
        int idx = 0;
        int fila = fila_h;
        int columna = columna_h;
        char letra;


        linkWord(palabra);
//        Toast.makeText(this, String.valueOf(palabra.getLength()), Toast.LENGTH_SHORT).show();

        while (idx < palabra.getLength()) {

            letra = palabra.getPalabra().charAt(idx);
            setOnCharMtx(fila, columna, palabra, idx);
            activateCell(fila, columna, letra);

            idx++;
            if (palabra.getOrientacion() == Palabra.VERTICAL) {
                fila = fila_h + idx;
            } else {
                columna = columna_h + idx;
            }


        }
    }

    // Calculates the cell size, to distribute evenly according to parent width and the matrix size
    public int evenWidth(GridLayout gl) {
//        Toast.makeText(this, String.valueOf(gl.getWidth()), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, String.valueOf(gl.getColumnCount()), Toast.LENGTH_SHORT).show();

        return gl.getWidth() / gl.getColumnCount();
    }

    // Initializes all cells blank and inactive
    public void fillBlank() {

        int newId;
        for (int i = 0; i < this.mtxSize; i++) {
            for (int j = 0; j < this.mtxSize; j++) {

                CharField myTxt = new CharField(this);
                GridLayout.LayoutParams nlparams = new GridLayout.LayoutParams(
                        GridLayout.spec(i),
                        GridLayout.spec(j));

                nlparams.setMargins(1, 1, 1, 1);
                nlparams.setGravity(Gravity.CENTER);
                newId = ViewCompat.generateViewId();
                myTxt.setId(newId);
                this.mtxId[i][j] = newId;
                myTxt.setPos(i, j);
                myTxt.setLayoutParams(nlparams);
                myTxt.setBackground(null);
                myTxt.setText("");
                myTxt.setWidth(this.cellSize);
                myTxt.setHeight(this.cellSize);
                myTxt.setGravity(Gravity.CENTER);
                myTxt.setFocusable(false);
                myTxt.setCursorVisible(false);

                target.addView(myTxt);

            }
        }
    }

    // Assigns a word to its corresponding cell
    public void linkWord(Palabra palabra) {
        int row_h = palabra.getHeadRow();
        int col_h = palabra.getHeadColumn();
        int col = col_h;
        int row = row_h;
        CharField charAux;
        for (int i = 0; i < palabra.getLength(); i++) {
            if (palabra.getOrientacion() == Palabra.HORIZONTAL) {
                col = col_h + i;
            } else {
                row = row_h + i;
            }
            charAux = findViewById(mtxId[row][col]);
            charAux.setLinkedWordId(palabra.getIdx());
        }

    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////

    public int keySize(GridLayout kb) {
//        Toast.makeText(this, String.valueOf(kb.getWidth() / (kb.getColumnCount()) * 3), Toast.LENGTH_SHORT).show();
        return kb.getWidth() / (kb.getColumnCount() * 3);
    }

    public void createKeyboard() {

        LinearLayout keyboardLine;

        int[] lineas = {R.id.linea1,R.id.linea2,R.id.linea3};

        int maxCol = 10;
        int maxRow = 3;

        String[] abc = new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L", "Ñ",
                "Z", "X", "C", "V", "B", "N", "M", " ", " ", " "};

        for (int i=0; i<lineas.length; i++){
            keyboardLine = findViewById(lineas[i]);
            for (int j=0; j<maxCol;j++) {
                Button myBtn = new Button(getApplicationContext());
                LinearLayout.LayoutParams nlparams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                myBtn.setText(abc[i*maxCol+j]);
                myBtn.setLayoutParams(nlparams);
                myBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setLetter(v);
                    }
                });
                keyboardLine.addView(myBtn);
            }
        }
    }

    public void setLetter(View v){
        String letter = ((Button)v).getText().toString();
        CharField myEdt = findViewById(focusInfo.getCharFocusedId());
        myEdt.setText(letter);
    }

    public void nextFocus(){

    }
}