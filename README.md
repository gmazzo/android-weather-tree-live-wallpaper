![GitHub](https://img.shields.io/github/license/gmazzo/android-weather-tree-live-wallpaper)
[![Build Status](https://github.com/gmazzo/android-weather-tree-live-wallpaper/actions/workflows/build.yaml/badge.svg)](https://github.com/gmazzo/android-weather-tree-live-wallpaper/actions/workflows/build.yaml)
[![Coverage](https://codecov.io/gh/gmazzo/android-weather-tree-live-wallpaper/branch/main/graph/badge.svg?token=D5cDiPWvcS)](https://codecov.io/gh/gmazzo/android-weather-tree-live-wallpaper)

<a href="https://play.google.com/store/apps/details?id=io.github.gmazzo.android.livewallpaper.weather"><img alt="Get it on Google Play" src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Google_Play_Store_badge_EN.svg/2560px-Google_Play_Store_badge_EN.svg.png" width="135" height="40" /></a>

# android-weather-tree-live-wallpaper 
This is a resurrected project of `Weather Wallpaper` (`com.hm.weather`) originally developed by `yitiaocaiHM`, where you can still find its APK over the Internet.

Partially migrated to a modern stack (WIP):
- Kotlin
- KotlinX Serialzation JSON
- Hilt
- AndroixX framework:
  - Compose
  - Material 3
  - DataStore
  - WorkManager
- Retrofit
- External Services
  - Norway's [MET Weather API](https://api.met.no/) for fetching weather conditions
  - Bigdatacloud's [Reverse Geocoding](https://www.bigdatacloud.com/free-api/free-reverse-geocode-to-city-api) for getting current city's name for a given location coordinates

## Scenes
|                                  |                                |                            |
|----------------------------------|--------------------------------|----------------------------|
| ![Clear sky](./README_clear.gif) | ![Cloudy](./README_cloudy.gif) | ![Rain](./README_rain.gif) |
| ![Storm](./README_storm.gif)     | ![Snowy](./README_snow.gif)    | ![Fog](./README_fog.gif)   |

This project is a slowing migration to a modern setup as an attempt to keep it up to date with the Android ecosystem and keep it functional.

> [!NOTE]
> I kept this to run it on my own devices, just because I liked it very much and it suddenly disappeared from Google Play.
> I do not offer any guarantee or maintenance support rather than for my own needs for it.
