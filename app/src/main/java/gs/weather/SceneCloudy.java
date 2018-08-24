package gs.weather;

import android.content.Context;

import javax.microedition.khronos.opengles.GL11;

public class SceneCloudy extends SceneClear {

    public SceneCloudy(Context ctx, GL11 gl) {
        super(ctx, gl, R.drawable.bg1);
    }

}
