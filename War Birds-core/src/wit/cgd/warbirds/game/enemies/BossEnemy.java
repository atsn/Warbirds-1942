package wit.cgd.warbirds.game.enemies;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Vector2;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.objects.AbstractGameObject;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Bullet.BulletType;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;
import wit.cgd.warbirds.game.util.GameStats;

public class BossEnemy extends AbstractGameObject
{

	private float timeShootDelay;
	public boolean isdead = false;
	float deathtime = 0;
	Random random;
	float life;
	float maxlife;
	public boolean bossIsIn;

	public BossEnemy(Level level, float life)
	{
		super(level);
		init(level, life);

	}

	public void init(Level level, float life)
	{
		dimension.scl(0.8f);
		super.animation = Assets.instance.boss.animation;
		super.setAnimation(animation);
		state = State.ACTIVE;
		timeShootDelay = 0;
		random = new Random((long) Math.pow(System.currentTimeMillis(), 5));
		maxlife = life;
		this.life = maxlife;
		position.y = level.levelend + 1-origin.y;
		position.x = -2f-origin.x;
		velocity.y = -0.6f;
		velocity.x = 0;
		dimension.scl(5);
		rotation = 180;
		bossIsIn = false;

	}

	@Override
	public void update(float deltaTime)
	{

		super.update(deltaTime);
		origin.x = this.dimension.x / 2;
		origin.y = this.dimension.y / 2;
		timeShootDelay = timeShootDelay - deltaTime;
		if (rotation >= 360)
		{
			rotation = 0;
		}
		shoot(position);

		if (position.y < level.end - 4)
		{
			velocity.y = 0;
			bossIsIn = true;
		}
		
		shoot(position);

	}

	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion region = (TextureRegion) animation.getKeyFrame(stateTime, true);

		batch.draw(region.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, 1, 1, rotation, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
				region.getRegionHeight(), false, false);
		region = (TextureRegion) Assets.instance.Smoke.animation.getKeyFrame(stateTime, true);

	}

	public void hit(float damage)
	{
		if (!bossIsIn)
		{
			AudioManager.instance.play(Assets.instance.sounds.bossnottakindamagesound,0.3f);
		}
		else {
			life = life - damage;
			AudioManager.instance.play(Assets.instance.sounds.click_2);
			if (life <= 0)
			{
				die();
			}
		}
		
	}

	public void die()
	{
		if (!isdead)
		{
			AudioManager.instance.play(Assets.instance.sounds.playerhit);
			deathtime = 1.5f;
			isdead = true;
			animation = Assets.instance.bossexplotion.animation;
			animation.setPlayMode(Animation.PlayMode.NORMAL);
			super.setAnimation(animation);
			AudioManager.instance.play(Assets.instance.sounds.exploation);
			GameStats.instance.addtoscore(1300);
		}
	}

	public void shoot(Vector2 position)
	{
		// this.rotation = this.rotation + 1;
		if (timeShootDelay > 0 || isdead || level.enemybullets.size > 5) return;

		// get bullet
		Bullet bullet = level.bossenemybulletPool.obtain();
		bullet.reset();
		float r = random.nextFloat();
		
		if (r < 0.20)bullet.position.set(position.x - bullet.origin.x/2, position.y + origin.y - bullet.origin.y);
		else if (r < 0.40)bullet.position.set(position.x  + origin.x - bullet.origin.x, position.y + origin.y - bullet.origin.y);
		else if (r < 0.60)bullet.position.set(position.x  +origin.x*2 - bullet.origin.x , position.y + origin.y - bullet.origin.y);
		else if (r < 0.80)bullet.position.set(position.x  + origin.x*1.5f - bullet.origin.x, position.y + origin.y - bullet.origin.y);
		else bullet.position.set(position.x  + origin.x*0.5f - bullet.origin.x, position.y + origin.y - bullet.origin.y);
		
		bullet.setrotation(0);
		bullet.setbulletype(BulletType.normalBullet);
		bullet.level = this.level;
		level.enemybullets.add(bullet);
		timeShootDelay = Constants.PLAYER_SHOOT_DELAY + - + random.nextFloat()/2;
		// AudioManager.instance.play(Assets.instance.sounds.player_bullet);
	}


}