package com.mygdx.game.flapbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
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
    //para guardar as  texturas  que vão ser renderizadas
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoAlto;
    private Texture canoBaixo;
    private Texture GameOver;

    BitmapFont textPontuacao;// para mostrar texto de pontos
    BitmapFont textRenicia; // para mostrar remiciar jogo
    BitmapFont textMelhorPontuacao;//para mostra melhor pontuação

    private boolean passouCano = false;//  virificar se passou pelo cano se verdadeiro ou falso

    private Random random;// var para random

    private int pontuacaoMaxima = 0;
    private int pontos = 0;// var de pontos
    private int gravidade = 0;// var de gravidade
    private int estadojogo = 0;// var de estado do jogo

    private float variacao = 0; // variação da animação
    private float posicaoInicialVerticalPassaro = 0;// posição do passaro  na vertical
    private float posicaoCanoHorizontal; //posição cano horizontal
    private float posicaoCanoVertical; //posição cano vertical
    private float larguradispositivo;// a largura do dispositivo
    private float alturadispositivo;// a altura do dispositivo
    private float espacoEntreCanos;// espaço entre os canos
    private float posicaoHorizontalPassaro = 0;


    private ShapeRenderer shapeRenderer;
    //  vars de colisão
    private Circle circuloPassaro;
    private Rectangle retaguloCanoCima;
    private Rectangle retanguloBaixo;
// son do jogo
    Sound somVoando;
    Sound somColisao;
    Sound somPontuacao;

    Preferences preferencias;

    @Override
    public void create() {
        inicializarObjetos();// metodo para inicializar os objetos
        inicializaTexuras();// metodo para inicializar as texturas

    }

    private void inicializarObjetos() {

        batch = new SpriteBatch();
        random = new Random();//random

        alturadispositivo = Gdx.graphics.getHeight();// declarando que altura e e a mesma do dispositivo
        larguradispositivo = Gdx.graphics.getWidth();// declarando que largura e e a mesma do dispositivo
        posicaoInicialVerticalPassaro = alturadispositivo / 2;// para pegar a metade da altura do dispositivo
        posicaoCanoHorizontal = larguradispositivo;//possição do cano sera a largura do dispositivo
        espacoEntreCanos = 350;

        textPontuacao = new BitmapFont();// pegando a texto de pontos
        textPontuacao.setColor(Color.WHITE);// deixando ele com  cor branca
        textPontuacao.getData().setScale(10);// determinado tamanho do texto

        textRenicia = new BitmapFont();// pegando a texto de pontos
        textRenicia.setColor(Color.GREEN);// deixando ele com  cor branca
        textRenicia.getData().setScale(3);// determinado tamanho do texto

        textMelhorPontuacao = new BitmapFont();// pegando a texto de pontos
        textMelhorPontuacao.setColor(Color.RED);// deixando ele com  cor branca
        textMelhorPontuacao.getData().setScale(3);// determinado tamanho do texto

        // pegando as colisões
        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retaguloCanoCima = new Rectangle();
        retanguloBaixo = new Rectangle();

        // pegando  som do audios
        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));


        preferencias = Gdx.app.getPreferences("flappyBird");
        pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);// para gardar maior pontuação

    }

    private void inicializaTexuras() {

        fundo = new Texture("fundo.png");//pegando a textura para criar

        // pondo as pngs do passaro em um arrey de texturas para que forme animação na cena
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        // pegando texturas do cano
        canoAlto = new Texture("cano_topo_maior.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        GameOver = new Texture("game_over.png");


    }

    @Override
    public void render() {

        verificaEstadojogo();// verificando estados do jogo
        desenharTexturas();// renderizando as texuras ou desenhando na cena
        detectarColisao();// detectar as colisões  dos objetos na cena
        validarPontos();// validando pontos quando passsa entre os canos


    }

    private void detectarColisao() {

        circuloPassaro.set(50 + passaros[0].getWidth() / 2,
                posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2,
                passaros[0].getWidth() / 2);// pondo colisao no passaro

        retanguloBaixo.set(posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());// pondo colisão nos canos

        retaguloCanoCima.set(posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoAlto.getWidth(), canoAlto.getHeight());// pondo colisão nos canos

        boolean bateuCanoCima = Intersector.overlaps(circuloPassaro, retaguloCanoCima);// verificando a colisão entre cano e passaro
        boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloBaixo);// verificando a colisão entre cano e passaro
        if (bateuCanoBaixo || bateuCanoCima) {
            Gdx.app.log("Log", "colideu");
            // se o estado  for 1 dispara som da colisão e muda estado do jogo para 2
            if (estadojogo == 1) {
                somColisao.play();// dispara som de colisão
                estadojogo = 2;// estado do jogo
            }
        }
    }

    private void validarPontos() {
        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {// se passar pelo cano
            if (!passouCano) {// for diferente passouCano
                pontos++; // soma pontos
                passouCano = true;// e passouCano é verdadeiro
                somPontuacao.play();// dispara som de pontuação
            }

        }
        variacao += Gdx.graphics.getDeltaTime() * 10;// velocidade da variação de pngs do passaro para a animação

        if (variacao > 3) // variação para animação do passaro
        {
            variacao = 0; // determinado que sera 0
        }

    }

    private void verificaEstadojogo() {

        boolean toqueTela = Gdx.input.justTouched();// bool pra verificar toque
        // estado  0 , se tocar na tela gravidade  do passaro puxa pra baixo, e muda estado do jogo pra 1 e dispara som de voando
        if (estadojogo == 0) {
            if (Gdx.input.justTouched()) {
                gravidade = -15;
                estadojogo = 1;
                somVoando.play();
            }

        }
        // estado do jogo  1 ativa gravidade e dispara som de voando, e faz que o cano comece a se movimentar,
        else if (estadojogo == 1) {
            if (toqueTela) {
                gravidade = -15;
                somVoando.play();
            }
            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;// velocidade do cano
            if (posicaoCanoHorizontal < -canoBaixo.getHeight()) {
                posicaoCanoHorizontal = larguradispositivo; // posição cano na horrizontal é igual a largura
                posicaoCanoVertical = random.nextInt(400) - 200;// posição vertical fica randomicamente mudando de valores
                passouCano = false;
            }
            if (posicaoInicialVerticalPassaro > 0 || toqueTela)
                posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;


            gravidade++;// almentando a gravidade

        }
        // estado do jogo  2 , faz que  se coliu com os canos, motre a melhor pontuação e resete o game se tocar na tela novamente
        else if (estadojogo == 2) {
            if (pontos > pontuacaoMaxima) // se pontuação  for  maior  que pontuação maxima, potuanção maxima  vai ser igual a pontos
            {
                pontuacaoMaxima = pontos;
                preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
            }

            posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500; // efeito de colisãp quando acontecer

            if (toqueTela) // se tocou na tela, reseta o estado do jogo como pontuação, posição do passaro e canos
            {
                estadojogo = 0;// estado do jogo
                pontos = 0;// pontos
                gravidade = 0;// garvidade
                posicaoHorizontalPassaro = 0;
                posicaoInicialVerticalPassaro = alturadispositivo / 2;
                posicaoCanoHorizontal = larguradispositivo;
            }
        }


    }

    private void desenharTexturas() {
        batch.begin();// coemeçando

        batch.draw(fundo, 0, 0, larguradispositivo, alturadispositivo);// rederizando o fundo do game na cena
        batch.draw(passaros[(int) variacao], 50, posicaoInicialVerticalPassaro);// rederizando o passaro na cena
        batch.draw(canoBaixo, posicaoCanoHorizontal, alturadispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);//renderizando cano na cena e calculando conforme o tamanho da tela
        batch.draw(canoAlto, posicaoCanoHorizontal, alturadispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);//renderizando cano na cena e calculando conforme o tamanho da tela
        textPontuacao.draw(batch, String.valueOf(pontos), larguradispositivo / 2, alturadispositivo - 100);// renderizando os pontos na tela  cada vez que passar entre os canos

        // se o estado do jogo for  2 renderiza na tela a pngs  na tela dando infomação sobre o estatus que jogo se encotra
        if (estadojogo == 2) {
            batch.draw(GameOver, larguradispositivo / 2 - GameOver.getWidth() / 2, alturadispositivo / 2);
            textRenicia.draw(batch, "Toque  na tela para reiniciar!", larguradispositivo / 2 - 200, alturadispositivo / 2 - GameOver.getHeight() / 2);
            textMelhorPontuacao.draw(batch, "Sua melhor pontuação  é : " + pontuacaoMaxima + " Pontos", larguradispositivo / 2 - 300, alturadispositivo / 2 - GameOver.getHeight() * 2);
        }


        batch.end();// terminado

    }

    @Override
    public void dispose() {

    }


}
