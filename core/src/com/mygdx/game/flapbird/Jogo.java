package com.mygdx.game.flapbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends ApplicationAdapter {
    SpriteBatch batch;
    // variaveis que vai guarda as texturas
    Texture passaro;
    Texture fundo;

    // variaveis que vão guardar a altura e a largura do dispositivo
    private float larguradispositivo;
    private float alturadispositivo;

    // variaveis que vão guardar  a movimentção sendo ela no eixo x ou y
    private  int movimentaX = 0;
    private  int movimentaY = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();

        fundo = new Texture("fundo.png");//pegando a textura para criar
        passaro = new Texture("passaro1.png");// ||


        alturadispositivo = Gdx.graphics.getHeight();// declarando que altura e e a mesma do dispositivo
        larguradispositivo = Gdx.graphics.getWidth();// declarando que largura e e a mesma do dispositivo
    }

    @Override
    public void render()
    {
        batch.begin();
        batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);// rederizando o fundo do game na cena
        batch.draw(passaro,movimentaX,movimentaY);// rederizando o passaro na cena
        movimentaX ++;// fazendo se mover para frente na cena quando inicia a aplicação 

        batch.end();

    }

    @Override
    public void dispose() {

    }
}
