package wit.cgd.warbirds.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameStats
{

	public static final String TAG = GameStats.class.getName();

	public static final GameStats instance = new GameStats();
	private Preferences prefs;
	public int lastscore;
	public int higestscore;

	private GameStats()
	{
		prefs = Gdx.app.getPreferences(Constants.STATS);
		load();
	}

	public void load()
	{
		lastscore = prefs.getInteger("lastscore", 0);
		higestscore = prefs.getInteger("higestscore", 0);
	}

	public void save()
	{
		prefs.putInteger("lastscore", lastscore);
		prefs.putInteger("higestscore", higestscore);
		prefs.flush();
	}


	public void reset()
	{
		lastscore = 0;
		higestscore = 0;
	}
	
	public void tjeckhigscore(){
		if (lastscore > higestscore)
		{
			higestscore = lastscore;
		}
	}

	public void addtoscore(int score)
	{
		if (GamePreferences.instance.dificulity == 0)
		{
			lastscore += (int) (score/2);
		}
		else	lastscore += (int) (score*GamePreferences.instance.dificulity);
	}
	
	public void removefromscore(int score)
	{
		lastscore -= score;
	}
}