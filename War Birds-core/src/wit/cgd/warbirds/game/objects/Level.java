package wit.cgd.warbirds.game.objects;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pool;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.enemies.BossEnemy;
import wit.cgd.warbirds.game.enemies.Enemy;
import wit.cgd.warbirds.game.enemies.Enemy.Enemytype;
import wit.cgd.warbirds.game.objects.Bullet.BulletType;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;
import wit.cgd.warbirds.game.util.GamePreferences;

public class Level extends AbstractGameObject
{

	public static final String TAG = Level.class.getName();

	public Player player;
	public LevelDecoration levelDecoration;
	public float start;
	public float end;
	public float levelend;
	public boolean levelendet;
	public Array<Enemy> enemies;
	public Array<Drops> drops;
	private Level thislevel;
	public boolean hasboss;
	public BossEnemy boss;
	public boolean bosspawned;
	boolean ishigestdificulity;
	int levelnumber;

	public final Array<Bullet> bullets = new Array<Bullet>();

	public final Pool<Bullet> bulletPool = new Pool<Bullet>()
	{
		@Override
		protected Bullet newObject()
		{
			return new Bullet(level, BulletType.normalBullet);
		}
	};
	public final Array<Bullet> enemybullets = new Array<Bullet>();

	public final Pool<Bullet> enemybulletPool = new Pool<Bullet>(0, 5)
	{
		@Override
		protected Bullet newObject()
		{
			return new Bullet(level, BulletType.normalBullet);
		}
	};

	public final Pool<Bullet> bossenemybulletPool = new Pool<Bullet>(0, 15)
	{
		@Override
		protected Bullet newObject()
		{
			return new Bullet(level, BulletType.normalBullet);
		}
	};

	/**
	 * Simple class to store generic object in level.
	 */
	public static class LevelObject
	{
		String name;
		int x;
		int y;
		float rotation;
		int state;
	}

	/**
	 * Collection of all objects in level
	 */
	public static class LevelMap
	{
		int islandseed;
		ArrayList<LevelObject> enemies;
		String name;
		float length;
		boolean hasboss;
	}

	public Level(int level)
	{
		super(null);
		init(level);

	}

	private void init(int levelnum)
	{
		drops = new Array<>();
		levelnumber = levelnum;
		if (GamePreferences.instance.dificulity == 5)
		{
			ishigestdificulity = true;
		}
		thislevel = this;
		// player
	
		player = new Player(this);
	
		
		player.position.set(-0.5f, -0.5f);
		enemies = new Array<Enemy>();
		bosspawned = false;
		levelDecoration = new LevelDecoration(this);

		// read and parse level map (form a json file)
		String map = Gdx.files.internal("levels/level-0" + levelnumber + ".json").readString();

		Json json = new Json();
		json.setElementType(LevelMap.class, "enemies", LevelObject.class);
		LevelMap data = new LevelMap();
		data = json.fromJson(LevelMap.class, map);
		hasboss = data.hasboss;
		for (LevelObject enemyinfo : data.enemies)
		{
			Enemytype type = Enemytype.greenEnemy;
			switch (enemyinfo.name.toLowerCase())
			{
			case "greenenemy":
				type = Enemytype.greenEnemy;
				break;
			case "blueenemy":
				type = Enemytype.blueEnemy;
				break;
			case "yellowenemy":
				type = Enemytype.yellowEnemy;
				break;
			case "whiteenemy":
				type = Enemytype.whiteEnemy;
				break;
			}
			Enemy enemy = new Enemy(this, type);
			enemy.position.set(enemyinfo.x, enemyinfo.y);
			enemies.add(enemy);
		}
		/*
		 * Enemy en = new Enemy(this,data.enemies.get(0).MyType);
		 * en.position.set(0-0.5f, 3-0.5f); greenenemies.add(en);
		 */

		Gdx.app.log(TAG, "Data name = " + data.name);
		Gdx.app.log(TAG, "islands . . . ");

		Random rand = new Random(data.islandseed);

		Gdx.app.log(TAG, "enemies . . . ");
		for (Object e : data.enemies)
		{
			LevelObject p = (LevelObject) e;
			Gdx.app.log(TAG, "type = " + p.name + "\tx = " + p.x + "\ty =" + p.y);

			// TODO add enemies
		}
		position.set(0, 0);
		velocity.y = Constants.SCROLL_SPEED;
		state = State.ACTIVE;
		levelend = data.length;
		levelendet = false;

		for (int i = 0; i < (levelend / (rand.nextFloat() * 10)) * 3; i++)
		{
			switch (rand.nextInt(3) + 1)
			{
			case 1:
				levelDecoration.add("islandSmall", rand.nextInt(9) - 4, rand.nextInt((int) levelend), rand.nextInt(360));
				break;
			case 2:
				levelDecoration.add("islandTiny", rand.nextInt(9) - 4, rand.nextInt((int) levelend), rand.nextInt(360));
				break;
			case 3:
				levelDecoration.add("islandBig", rand.nextInt(9) - 4, rand.nextInt((int) levelend), rand.nextInt(360));
				break;
			}

		}

	}

	public void update(float deltaTime)
	{

		super.update(deltaTime);

		// limits for rendering
		start = position.y - scale.y * Constants.VIEWPORT_HEIGHT;
		end = position.y + scale.y * Constants.VIEWPORT_HEIGHT - 1;

		for (Enemy enemy : enemies)
		{
			enemy.update(deltaTime);
		}

		player.update(deltaTime);

		for (Bullet bullet : bullets)
		{
			bullet.update(deltaTime);
		}
		for (Drops drop : drops)
		{
			drop.update(deltaTime);
		}
		for (Bullet bullet : enemybullets)
		{
			bullet.update(deltaTime);
		}

		if (end > levelend)
		{
			velocity.y = 0;
			levelendet = true;
			if (hasboss && !bosspawned)
			{
				AudioManager.instance.play(Assets.instance.sounds.bosscomein);
				AudioManager.instance.turndownmusic();
				boss = new BossEnemy(thislevel, 1000);
				bosspawned = true;
			}

			if (hasboss)
			{

				if (boss.bossIsIn)
				{
					AudioManager.instance.turnupmusic();
				}
			}
		}

		if (bosspawned)
		{
			boss.update(deltaTime);
		}

	}

	public void render(SpriteBatch batch)
	{
		levelDecoration.render(batch);
		for (Enemy enemy : enemies)
		{
			enemy.render(batch);
		}

		for (Drops drop : drops)
		{
			drop.render(batch);
		}
		
		player.render(batch);

		for (Bullet bullet : bullets)
			bullet.render(batch);
		for (Bullet bullet : enemybullets)
			bullet.render(batch);

		if (bosspawned)
		{
			boss.render(batch);
		}
		//System.out.println("Bullets " + bullets.size);
	}

}
