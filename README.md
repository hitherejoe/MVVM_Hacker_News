Hacker News Reader
===================

<br/>
<p align="center">
<a href="https://play.google.com/store/apps/details?id=com.hitherejoe.hackernews">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>
</p>

<p align="center"><img src="http://i59.tinypic.com/n48mzk.png" /></p>

A simple and modern Material Design influenced Hacker News reader Android app. Read, view comments, bookmark and share stories from the hacker news feed!

Building
--------

To build, install and run a debug version, run this from the root of the project:

    ./gradlew installRunDebug
    
Testing
--------

To run **unit** tests on your machine using [Robolectric] (http://robolectric.org/):

    ./gradlew testDebug
    
To run automated tests on connected devices:

    ./gradlew connectedAndroidTest
