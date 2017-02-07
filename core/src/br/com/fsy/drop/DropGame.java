package br.com.fsy.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class DropGame extends ApplicationAdapter {
	public static BitmapFont font, shadow;

	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Array<Integer> raindropValues;
	private long lastDropTime;

	public static int score;
	public static int raindropWithResult;
	public static int scoreYPosition;
	public static boolean isRaindropWithResultAlive;

	public static float _screenWidth;
	public static float _screenHeight;
	public static float gameWidth;
	public static float gameHeight;

	private static int bucketWidthHeight;
	private static int dropWidthHeight;

	private static int bucketYPosition;

	private static long DROP_RESPAWN_TIME_IN_MILLIS = 1000000000;

	private static int GAME_VELOCITY = 80;
	private static String bucketCalculation;
	private static int number1, number2;
	private static int resultado;
	private static int operacao;

	public static final int MAX_DROPS = 5;

	@Override
	public void create() {
		score = 0;
		raindropWithResult = 0;
		isRaindropWithResultAlive = false;

		_screenWidth = Gdx.graphics.getWidth();
		_screenHeight = Gdx.graphics.getHeight();
		gameWidth = 136;
		gameHeight = _screenHeight / (_screenWidth / gameWidth);
		scoreYPosition = (int) (gameHeight * .9);

		bucketWidthHeight = (int) (gameHeight * .1);
		dropWidthHeight = bucketWidthHeight;

		bucketYPosition = (int) (gameHeight * .1);

		font = new BitmapFont(Gdx.files.internal("text.fnt"));
		font.getData().setScale(gameHeight * 0.0011f, gameHeight * 0.0011f);

		shadow = new BitmapFont(Gdx.files.internal("shadow.fnt"));
		shadow.getData().setScale(gameHeight * 0.0011f, gameHeight * 0.0011f);

		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();

		//This will make sure the camera always shows us an area of our game world that is 800x480 units wide.
		// Think of it as a virtual window into our world. We currently interpret the units as pixels to make our
		// life a little easier. There's nothing preventing us from using other units though, e.g. meters or whatever
		// you have. Cameras are very powerful and allow you to do a lot of things we won't cover in this basic tutorial.
		camera.setToOrtho(false, gameWidth, gameHeight);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.width = bucketWidthHeight;
		bucket.height = bucketWidthHeight;

		bucket.x = (gameWidth/2) - (bucket.width /2); // center the bucket horizontally
		bucket.y = bucketYPosition; // bottom left corner of the bucket is 20 pixels above the bottom screen edge

		// create the raindrops array and spawn the first raindrop
		raindrops = new Array<Rectangle>();
		raindropValues = new Array<Integer>();
		aleatorizaNumeros();
		spawnRaindrop();
	}

	private void aleatorizaNumeros() {
		number1 = MathUtils.random(0,10);
		number2 = MathUtils.random(1,10);
		operacao = MathUtils.random(0,2);
		if (operacao == 0) {
			resultado = number1 + number2;
			bucketCalculation = String.valueOf(number1 + "+" + number2);
		} else if (operacao == 1){
			resultado = number1 - number2;
			bucketCalculation = String.valueOf(number1 + "-" + number2);
		} else if (operacao == 2){
			resultado = number1 * number2;
			bucketCalculation = String.valueOf(number1 + "*" + number2);
		}
		if (raindropValues.contains(resultado, true)){
			isRaindropWithResultAlive = true;
		} else {
			isRaindropWithResultAlive = false;
		}
		//raindropWithResult = MathUtils.random(1, 10);
	}

	private void spawnRaindrop() {
		if (raindropWithResult == 0 && !isRaindropWithResultAlive){
			raindropWithResult = MathUtils.random(1,MAX_DROPS);
		}
		int dropValue;
		if (raindropWithResult == 1 && !isRaindropWithResultAlive){
			isRaindropWithResultAlive = true;
			dropValue = resultado;
		} else {
			do {
				dropValue = MathUtils.random(0, 100);
			} while (dropValue == resultado || raindropValues.contains(dropValue, true));
		}
		raindropValues.add(dropValue);
		Rectangle raindrop = new Rectangle();
		resetRainDropPosition(raindrop);
		raindrop.width = dropWidthHeight;
		raindrop.height = dropWidthHeight;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
		if (!isRaindropWithResultAlive) {
			raindropWithResult -= 1;
		}
	}

	private void resetRainDropPosition(Rectangle raindrop) {
		raindrop.x = MathUtils.random(0, gameWidth - dropWidthHeight);
		raindrop.y = gameHeight;
	}

	@Override
	public void render() {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		batch.begin();
		drawBucket();
		drawDrops();
		drawScore();
		batch.end();

		// process user input
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		//Gdx.app.log("DropGame", "Before unproject: " + touchPos.x + ", " + touchPos.y + ", " + touchPos.z);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - (bucketWidthHeight / 2);
			//Gdx.app.log("DropGame", "After unproject: " + touchPos.x + ", " + touchPos.y + ", " + touchPos.z);
		}
		//if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
		//	bucket.x -= GAME_VELOCITY * Gdx.graphics.getDeltaTime();
		//}
		//if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
		//	bucket.x += GAME_VELOCITY * Gdx.graphics.getDeltaTime();
		//}

		// make sure the bucket stays within the screen bounds
		if(bucket.x < 0) {
			bucket.x = 0;
		}
		if(bucket.x > gameWidth - bucketWidthHeight) {
			bucket.x = gameHeight - bucketWidthHeight;
		}

		// check if we need to create a new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > DROP_RESPAWN_TIME_IN_MILLIS && raindrops.size < MAX_DROPS) {
			spawnRaindrop();
		}

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we play back
		// a sound effect as well.
		Iterator<Rectangle> iter = raindrops.iterator();
		int cont = 0;
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= GAME_VELOCITY * Gdx.graphics.getDeltaTime();
			if (raindrop.y + dropWidthHeight < 0) {
				resetRainDropPosition(raindrop);
			}
			if (raindrop.overlaps(bucket)) {
				//
				if (resultado == raindropValues.get(cont)) {
					aleatorizaNumeros();
					dropSound.play();
					score += 1;
					iter.remove();
					raindropValues.removeIndex(cont);
				} else {
					score -= 1;
					dropSound.play();
					iter.remove();
					raindropValues.removeIndex(cont);
				}
			}
			cont++;
		}
	}

	private void drawDrops() {
		for (int cont = 0; cont < raindrops.size; cont++) {
			batch.draw(dropImage, raindrops.get(cont).x, raindrops.get(cont).y, dropWidthHeight, dropWidthHeight);
			int length = (raindropValues.get(cont) + "").length();
			shadow.draw(batch, raindropValues.get(cont) + "", raindrops.get(cont).x, raindrops.get(cont).y + (int)(dropWidthHeight * 1.7));
			font.draw(batch, raindropValues.get(cont) + "", raindrops.get(cont).x, (raindrops.get(cont).y + (int)(dropWidthHeight * 1.7)) - 1 );
		}
	}

	private void drawBucket() {
		batch.draw(bucketImage, bucket.x, bucket.y, bucketWidthHeight, bucketWidthHeight);
		int length = (bucketCalculation).length();
		shadow.draw(batch, bucketCalculation, gameWidth/2 - (5 * length), bucket.y);
		font.draw(batch, bucketCalculation, gameWidth/2 - (5 * length), bucket.y);
	}

	private void drawScore() {
		int length = ("" + score).length();
		shadow.draw(batch, "" + score, gameWidth/2 - (4 * length), scoreYPosition);
		font.draw(batch, "" + score, gameWidth/2 - (4 * length), scoreYPosition - 1);
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
