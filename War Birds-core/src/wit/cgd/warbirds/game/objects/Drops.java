package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.AudioManager;

public class Drops extends AbstractGameObject
{
	public boolean exploadoing = false;
	TextureRegion region;
	float explotingleftime;
	Animation animation;

	public enum Droptype
	{
		ExtraLife, DubbleBullet, shield, bomb
	}

	public Droptype droptype;

	public Drops(Level level, Droptype droptype)
	{
		super(level);
		this.droptype = droptype;
		explotingleftime = 1.5f;

	}

	@Override
	public void render(SpriteBatch batch)
	{
		switch (droptype)
		{
		case ExtraLife:
			region = Assets.instance.extralife.region;
			break;
		case DubbleBullet:
			region = Assets.instance.doubleBullet.region;
			break;
		case shield:
			region = Assets.instance.shield.region;
			break;
		case bomb:
			region = Assets.instance.bomb.region;
			break;

		}
		if (droptype == droptype.DubbleBullet)
		{
			batch.draw(region.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x/2, dimension.y/2, 1, 1, rotation, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
					region.getRegionHeight(), false, false);
		}
		else if (!exploadoing)
		{			
			batch.draw(region.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, 1, 1, rotation, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
					region.getRegionHeight(), false, false);

		}
		
		else
		{
			region = (TextureRegion) animation.getKeyFrame(stateTime, true);
			batch.draw(region.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, 1, 1, rotation, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
					region.getRegionHeight(), false, false);
		}
		
		

	}

	@Override
	public void update(float deltaTime)
	{

		super.update(deltaTime);
		if (exploadoing)
		{
			explotingleftime = explotingleftime - deltaTime;
			if (explotingleftime < 0)
			{
				level.drops.removeValue(this, true);
			}
		}
	}

	public void gotpickedup()
	{
		if (droptype == Droptype.bomb && !exploadoing)
		{
			animation = Assets.instance.explosionsmall.animation;
			AudioManager.instance.play(Assets.instance.sounds.exploation);
			animation.setPlayMode(Animation.PlayMode.LOOP);
			super.setAnimation(animation);
			state = State.DYING;
			exploadoing = true;
		}
		else if (!exploadoing)
		{
			level.drops.removeValue(this, true);
		}
	}

}