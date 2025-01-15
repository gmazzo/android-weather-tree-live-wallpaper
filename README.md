# android-weather-tree-live-wallpaper 
This is a resurrected project of `Weather Wallpaper` (`com.hm.weather`) originally developed by `yitiaocaiHM`, where you can still find its APK over the Internet.

<a href="https://play.google.com/store/apps/details?id=io.github.gmazzo.android.livewallpaper.weather&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="240" height="92" /></a>

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
