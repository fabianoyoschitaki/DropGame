package br.com.fsy.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import java.util.Iterator;

public class DropGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;

	public static int SCREEN_WIDTH = 544;
	public static int SCREEN_HEIGHT = 816;

	private static int BUCKET_WIDTH_HEIGHT = 64;
	private static int DROP_WIDTH_HEIGHT = 64;

	private static int BUCKET_Y_POSITION = 20;

	private static long DROP_RESPAWN_TIME_IN_MILLIS = 1000000000/2;

	private static int GAME_VELOCITY = 200;

	@Override
	public void create() {
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
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.x = SCREEN_WIDTH / 2 - BUCKET_WIDTH_HEIGHT / 2; // center the bucket horizontally
		bucket.y = BUCKET_Y_POSITION; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		bucket.width = BUCKET_WIDTH_HEIGHT;
		bucket.height = BUCKET_WIDTH_HEIGHT;

		// create the raindrops array and spawn the first raindrop
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, SCREEN_WIDTH - DROP_WIDTH_HEIGHT);
		raindrop.y = SCREEN_HEIGHT;
		raindrop.width = DROP_WIDTH_HEIGHT;
		raindrop.height = DROP_WIDTH_HEIGHT;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
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
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();

		// process user input
		//if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		Gdx.app.log("DropGame", "Before unproject: " + touchPos.x + ", " + touchPos.y + ", " + touchPos.z);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - (BUCKET_WIDTH_HEIGHT / 2);
		Gdx.app.log("DropGame", "After unproject: " + touchPos.x + ", " + touchPos.y + ", " + touchPos.z);
		//}
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
		if(bucket.x > SCREEN_WIDTH - BUCKET_WIDTH_HEIGHT) {
			bucket.x = SCREEN_WIDTH - BUCKET_WIDTH_HEIGHT;
		}

		// check if we need to create a new raindrop
		if(TimeUtils.nanoTime() - lastDropTime > DROP_RESPAWN_TIME_IN_MILLIS) spawnRaindrop();

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we play back
		// a sound effect as well.
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= GAME_VELOCITY * Gdx.graphics.getDeltaTime();
			if(raindrop.y + DROP_WIDTH_HEIGHT < 0) {
				iter.remove();
			}
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}
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
