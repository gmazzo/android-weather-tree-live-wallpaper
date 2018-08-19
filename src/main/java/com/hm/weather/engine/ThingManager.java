package com.hm.weather.engine;

import com.hm.weather.engine.Utility.Logger;
import javax.microedition.khronos.opengles.GL10;

public class ThingManager {
    private static final int MAX_THINGS = 64;
    private static final String TAG = "GL Engine";
    private static ThingManager instance;
    private int currentHighestThing = 0;
    private volatile Thing[] thingList = new Thing[MAX_THINGS];

    public ThingManager() {
        instance = this;
    }

    private synchronized void getByTargetname(Thing[] thingArray, String name) {
        int i = 0;
        int k = 0;
        while (i < this.currentHighestThing) {
            if (!(this.thingList[i] == null || this.thingList[i].targetName == null || !this.thingList[i].targetName.equals(name))) {
                thingArray[k] = this.thingList[i];
                k++;
            }
            i++;
        }
    }

    public static ThingManager getInstance() {
        return instance;
    }

    private void resetCurrentHighestThing() {
        for (int i = 0; i < this.thingList.length; i++) {
            if (this.thingList[i] != null) {
                this.currentHighestThing = i;
            }
        }
        this.currentHighestThing++;
    }

    public synchronized boolean add(Thing thing) {
        boolean z;
        int i = 0;
        while (i < this.thingList.length) {
            if (this.thingList[i] == null) {
                this.thingList[i] = thing;
                if (i <= this.currentHighestThing) {
                    this.currentHighestThing = i + 1;
                } else {
                    this.currentHighestThing = i;
                }
                if (this.currentHighestThing > this.thingList.length) {
                    this.currentHighestThing = this.thingList.length;
                }
                z = true;
            } else {
                i++;
            }
        }
        Logger.v(TAG, "ERROR: thingManager out of space!");
        z = false;
        return z;
    }

    public synchronized boolean clear() {
        for (int i = 0; i < this.thingList.length; i++) {
            this.thingList[i] = null;
        }
        this.currentHighestThing = 0;
        return false;
    }

    public synchronized boolean clearByTargetname(String name) {
        int i = 0;
        while (i < this.thingList.length) {
            if (!(this.thingList[i] == null || this.thingList[i].targetName == null || !this.thingList[i].targetName.contentEquals(name))) {
                this.thingList[i] = null;
            }
            i++;
        }
        resetCurrentHighestThing();
        return false;
    }

    public synchronized int count() {
        int k;
        k = 0;
        for (int i = 0; i < this.currentHighestThing; i++) {
            if (this.thingList[i] != null) {
                k++;
            }
        }
        return k;
    }

    public synchronized int countByTargetname(String name) {
        int count;
        count = 0;
        int i = 0;
        while (i < this.currentHighestThing) {
            if (!(this.thingList[i] == null || this.thingList[i].targetName == null || !this.thingList[i].targetName.equals(name))) {
                count++;
            }
            i++;
        }
        return count;
    }

    public synchronized Thing[] getByTargetname(String name) {
        Thing[] arrayOfThing;
        arrayOfThing = new Thing[countByTargetname(name)];
        getByTargetname(arrayOfThing, name);
        return arrayOfThing;
    }

    public synchronized Thing getFirstByTargetname(String name) {
        Thing thing;
        int i = 0;
        while (i < this.currentHighestThing) {
            if (this.thingList[i] != null && this.thingList[i].targetName != null && this.thingList[i].targetName.contentEquals(name)) {
                thing = this.thingList[i];
                break;
            }
            i++;
        }
        thing = null;
        return thing;
    }

    public Thing nearest(float x, float y, float z, float distance) {
        return nearest(x, y, z, distance, null);
    }

    public Thing nearest(float x, float y, float z, float distance, String name) {
        int i = 0;
        if (this.currentHighestThing < 0) {
            return null;
        }
        do {
            if (this.thingList[i] != null && (name == null || this.thingList[i].targetName.contentEquals(name))) {
                break;
            }
            i++;
        } while (this.thingList[i].origin.distanceTo(x, y, z) >= distance);
        return this.thingList[i];
    }

    public synchronized void render(GL10 gl10, TextureManager texMagr, MeshManager meshMagr) {
        for (int i = 0; i < this.currentHighestThing; i++) {
            if (this.thingList[i] != null) {
                this.thingList[i].renderIfVisible(gl10, texMagr, meshMagr);
            }
        }
    }

    public synchronized void sortByY() {
        for (int i = 0; i < this.currentHighestThing; i++) {
            if (this.thingList[i] != null) {
                int left = i;
                int right = i + 1;
                while (right < this.currentHighestThing) {
                    if (this.thingList[right] != null && this.thingList[right].origin.y < this.thingList[left].origin.y) {
                        Thing tmp = this.thingList[left];
                        this.thingList[left] = this.thingList[right];
                        this.thingList[right] = tmp;
                    }
                    right++;
                }
            }
        }
    }

    public synchronized void update(float timeDelta) {
        update(timeDelta, false);
    }

    public synchronized void update(float timeDelta, boolean onlyVisible) {
        for (int i = 0; i < this.currentHighestThing; i++) {
            if (this.thingList[i] != null) {
                if (this.thingList[i].isDeleted()) {
                    this.thingList[i] = null;
                    if (this.currentHighestThing != i + 1) {
                        this.currentHighestThing--;
                    }
                } else if (onlyVisible) {
                    this.thingList[i].updateIfVisible(timeDelta);
                } else {
                    this.thingList[i].update(timeDelta);
                }
            }
        }
    }

    public synchronized void updateVisibility(Vector3 cameraPos, float cameraAngleZ, float fov) {
        for (int i = 0; i < this.currentHighestThing; i++) {
            if (this.thingList[i] != null) {
                this.thingList[i].checkVisibility(cameraPos, cameraAngleZ, fov);
            }
        }
    }
}
