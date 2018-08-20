package gs.weather.engine;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class ThingManager {
    private static final int MAX_THINGS = 64;
    private static final String TAG = "GL Engine";
    private volatile List<Thing> thingList = new ArrayList<>();

    public synchronized boolean add(Thing thing) {
        return thingList.add(thing);
    }

    public synchronized boolean clear() {
        thingList.clear();
        return false;
    }

    public synchronized boolean clearByTargetname(String name) {
        for (Iterator<Thing> it = thingList.iterator(); it.hasNext(); ) {
            Thing thing = it.next();

            if (TextUtils.equals(thing.targetName, name)) {
                it.remove();
            }
        }
        return false;
    }

    public synchronized int countByTargetname(String name) {
        int count = 0;
        for (Thing thing : thingList) {
            if (TextUtils.equals(thing.targetName, name)) {
                count++;
            }
        }
        return count;
    }

    public synchronized void render(GL10 gl10, TextureManager texMagr, MeshManager meshMagr) {
        for (Thing thing : thingList) {
            thing.renderIfVisible(gl10, texMagr, meshMagr);
        }
    }

    public synchronized void sortByY() {
        Collections.sort(thingList, new Comparator<Thing>() {
            @Override
            public int compare(Thing lhs, Thing rhs) {
                return (int) (lhs.origin.y - rhs.origin.y);
            }
        });
    }

    public synchronized void update(float timeDelta) {
        update(timeDelta, false);
    }

    public synchronized void update(float timeDelta, boolean onlyVisible) {
        for (Iterator<Thing> it = thingList.iterator(); it.hasNext(); ) {
            Thing thing = it.next();

            if (thing.isDeleted()) {
                it.remove();

            } else if (onlyVisible) {
                thing.updateIfVisible(timeDelta);

            } else {
                thing.update(timeDelta);
            }
        }
    }

}
