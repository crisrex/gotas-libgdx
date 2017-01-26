package com.cescristorey.pmdm.gotas;


import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Gotas extends ApplicationAdapter {
   private Texture imagenGota;
   private Texture imagenCubo;
   private Sound sonicoCaidaGota;
   private Music musicaLluvia;
   private SpriteBatch batch;
   private OrthographicCamera camara;
   private Rectangle cubo;
   private Array<Rectangle> gotasLluvia;
   private long tiempoCaidaUltimaGota;

   @Override
   public void create() {
      // carga las imágenes de las gotas de lluvia y del cubo, cada una de 64x64 píxeles
      imagenGota = new Texture(Gdx.files.internal("droplet.png"));
      imagenCubo = new Texture(Gdx.files.internal("bucket.png"));

      // carga de sonido para la caída de la gota y la música de fondo
      sonicoCaidaGota = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
      musicaLluvia = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

      // se aplica que la música se repita en bucle, comienza la reproducción de la música de fondo
      musicaLluvia.setLooping(true);
      musicaLluvia.play();

      // crea la cámara ortográfica y el lote de sprites
      camara = new OrthographicCamera();
      camara.setToOrtho(false, 800, 480);
      batch = new SpriteBatch();

      // crea un rectángulo (clase Rectangle) para representar lógicamente el cubo
      cubo = new Rectangle();
      cubo.x = 800 / 2 - 64 / 2; // centra el cubo horizontal
      cubo.y = 20; // esquina inferior izquierda del cubo estará a 20 píxeles del límite inferior
      cubo.width = 64;
      cubo.height = 64;

      // crea el vector de gotas y crea la primera gota
      gotasLluvia = new Array<Rectangle>();
      creaGotaLluvia();
   }

   private void creaGotaLluvia() {
      Rectangle gotaLluvia = new Rectangle();
      gotaLluvia.x = MathUtils.random(0, 800-64);
      gotaLluvia.y = 480;
      gotaLluvia.width = 64;
      gotaLluvia.height = 64;
      gotasLluvia.add(gotaLluvia);
      tiempoCaidaUltimaGota = TimeUtils.nanoTime();
   }

   @Override
   public void render() {
      // limpia la pantalla con un color azul oscuro. Los argumentos RGB de la función glClearcColor están en el rango entre 0 y 1
      Gdx.gl.glClearColor(0, 0, 0.2f, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      // ordenada a la cámara actualizar sus matrices
      camara.update();

      // indica al lote de sprites que se represente en las coordenadas específicas de la cámara
      batch.setProjectionMatrix(camara.combined);

      // comienza un nuevo proceso y dibuja el cubo y las gotas
      batch.begin();
      batch.draw(imagenCubo, cubo.x, cubo.y);
      for(Rectangle gotaLluvia: gotasLluvia) {
         batch.draw(imagenGota, gotaLluvia.x, gotaLluvia.y);
      }
      batch.end();

      // lectura de entrada
      if(Gdx.input.isTouched()) {
         Vector3 posicionTocada = new Vector3();
         posicionTocada.set(Gdx.input.getX(), Gdx.input.getY(), 0);
         camara.unproject(posicionTocada);
         cubo.x = posicionTocada.x - 64 / 2;
      }
      if(Gdx.input.isKeyPressed(Keys.LEFT)) cubo.x -= 200 * Gdx.graphics.getDeltaTime();
      if(Gdx.input.isKeyPressed(Keys.RIGHT)) cubo.x += 200 * Gdx.graphics.getDeltaTime();

      // nos aseguramos de que el cubo permanezca entre los límites de la pantalla
      if(cubo.x < 0) cubo.x = 0;
      if(cubo.x > 800 - 64) cubo.x = 800 - 64;

      // comprueba si ha pasado un segundo desde la última gota, para crear una nueva
      if(TimeUtils.nanoTime() - tiempoCaidaUltimaGota > 1000000000) creaGotaLluvia();

      // recorre las gotas y borra aquellas que hayan llegado al suelo (límite inferior de la pantalla) o toquen el cubo, en ese caso se reproduce sonido.
      Iterator<Rectangle> iter = gotasLluvia.iterator();
      while(iter.hasNext()) {
         Rectangle gotaLluvia = iter.next();
         gotaLluvia.y -= 200 * Gdx.graphics.getDeltaTime();
         if(gotaLluvia.y + 64 < 0) iter.remove();
         if(gotaLluvia.overlaps(cubo)) {
            sonicoCaidaGota.play();
            iter.remove();
         }
      }
   }

   @Override
   public void dispose() {
      // liberamos todos los recursos
      imagenGota.dispose();
      imagenCubo.dispose();
      sonicoCaidaGota.dispose();
      musicaLluvia.dispose();
      batch.dispose();
   }
}
