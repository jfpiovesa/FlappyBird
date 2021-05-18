package com.mygdx.game.flapbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class Jogo extends ApplicationAdapter {

    private SpriteBatch batch;

    private Texture[] passaros;
    private Texture fundo;
    private Texture canoAlto;
    private Texture canoBaixo;

    BitmapFont textPontuacao;

    private boolean passouCano = false;

    private Random random;

    private int pontos = 0;
    private int gravidade = 0;

    private float variacao = 0;
    private float posicaoInicialVerticalPassaro = 0;
    private float posicaoCanoHorizontal;
    private float posicaoCanoVertical;
    private float larguradispositivo;
    private float alturadispositivo;
    private float espacoEntreCanos;


    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;
    private Rectangle retaguloCanoCima;
    private Rectangle retanguloBaixo;


    @Override
    public void create() {
        inicializarObjetos();
        inicializaTexuras();

    }

    private void inicializarObjetos() {
        random = new Random();
        batch = new SpriteBatch();

        alturadispositivo = Gdx.graphics.getHeight();// declarando que altura e e a mesma do dispositivo
        larguradispositivo = Gdx.graphics.getWidth();// declarando que largura e e a mesma do dispositivo
        posicaoInicialVerticalPassaro = alturadispositivo / 2;
        posicaoCanoHorizontal = larguradispositivo;
        espacoEntreCanos = 350;

        textPontuacao = new BitmapFont();
        textPontuacao.setColor(Color.WHITE);
        textPontuacao.getData().setScale(10);


    }

    private void inicializaTexuras() {

        fundo = new Texture("fundo.png");//pegando a textura para criar

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        canoAlto = new Texture("cano_topo.png");
        canoBaixo = new Texture("cano_baixo.png");


    }

    @Override
    public void render() {

        verificaEstadojogo();
        desenharTexturas();
        detectarColisao();
        validarPontos();


    }

    private void detectarColisao() {

        circuloPassaro.set(50f + passaros[0].getWidth(),
                posicaoInicialVerticalPassaro + passaros[0].getHeight() ,
                passaros[0].getWidth() / 2);

        retanguloBaixo.set(posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());

        retaguloCanoCima.set(posicaoCanoHorizontal, alturadispositivo / 2 - canoAlto.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth(), canoAlto.getHeight());

        boolean bateuCanoCima = Intersector.overlaps(circuloPassaro, retaguloCanoCima);
        boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloBaixo);
        if (bateuCanoBaixo || bateuCanoCima) {
            Gdx.app.log("Log", "colideu");
        }
    }

    private void validarPontos() {
        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {
            if (!passouCano) {
                pontos++;
                passouCano = true;
            }

        }

    }

    private void verificaEstadojogo() {
        posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 300;
        if (posicaoCanoHorizontal < -canoBaixo.getHeight()) {
            posicaoCanoHorizontal = larguradispositivo;
            posicaoCanoVertical = random.nextInt(400) - 200;
            passouCano = false;
        }
        boolean toqueTela = Gdx.input.justTouched();// bool pra verificar toque
        if (Gdx.input.justTouched()) {
            gravidade = -25;

        }
        if (posicaoInicialVerticalPassaro > 0 || toqueTela) {
            posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
        }

        variacao += Gdx.graphics.getDeltaTime() * 10;

        if (variacao > 3) // variação para animação do passaro
        {
            variacao = 0;
        }

        gravidade++;

    }

    private void desenharTexturas() {
        batch.begin();

        batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);// rederizando o fundo do game na cena
        batch.draw(passaros[(int) variacao], 50, posicaoInicialVerticalPassaro);// rederizando o passaro na cena
        batch.draw(canoBaixo, posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
        batch.draw(canoAlto, posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);
        textPontuacao.draw(batch, String.valueOf(pontos), larguradispositivo / 2, alturadispositivo - 100);

        batch.end();

    }

    @Override
    public void dispose() {

    }


}
