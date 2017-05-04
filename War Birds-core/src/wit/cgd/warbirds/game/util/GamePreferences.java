package wit.cgd.warbirds.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

public class GamePreferences {
	
	public float dificulity;
	public boolean playsound;
	public boolean playmusic;
	public float soundVolume;
	public float musicVolume; 

	public static final String			TAG			= GamePreferences.class.getName();

	public static final GamePreferences	instance	= new GamePreferences();
	private Preferences					prefs;

	private GamePreferences() {
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}

	public void load() {
	
		dificulity = MathUtils.clamp(prefs.getFloat("dificulity"), 0f, 5f);
		soundVolume = MathUtils.clamp(prefs.getFloat("soundlevel"), 0f, 1f);
		musicVolume = MathUtils.clamp(prefs.getFloat("musiclevel"), 0f, 1f);
		playsound = prefs.getBoolean("playsound");
		playmusic = prefs.getBoolean("playmusic");
	}

	public void save() {

		prefs.putFloat("dificulity", MathUtils.clamp(dificulity,0f,5f));
		prefs.putBoolean("playsound", playsound);
		prefs.putBoolean("playmusic", playmusic);
		prefs.putFloat("soundlevel", MathUtils.clamp(soundVolume,0f,1f));
		prefs.putFloat("musiclevel", MathUtils.clamp(musicVolume,0f,1f));
		prefs.flush();
	}

}
