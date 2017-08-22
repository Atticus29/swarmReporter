# SwarmReporter
An Android app. for connecting bee swarms to beekeepers.

## 19 June - 28 July, 2017

## By: Mark Fisher

## Known issues
- [ ] Needing to click multiply/something else when you click to allow to use phone number (maybe have it always there  but not toggleable?)

## Description

In the late spring/early summer, honey bee hives sometimes get so large that they need to divide, a process called swarming. During swarming, a new queen is made at the original hive, and the old queen leaves with about half of the workers. They form a temporary hive-less structure (called a Bivuoac) while they conduct a search for a permanent home. Unfortunately, these swarms/bivouacs are often formed on low-hanging branches of trees, in people’s chimneys, etc., and they creep people out. So people call the fire department, etc., who in turn contact local beekeeping organizations which keep what are called swarm lists. Swarm lists are lists of eager beekeepers in the area who will gladly take a new hive of bees off of somebody’s property.

The problem is that currently, swarm lists are not decentralized and are quite clunky (essentially glorified phone trees). Beekeepers have to pay membership fees to join the swarm lists.

Bivuoac is Uber for swarms: it matches swarm reporters with available beekeepers nearby.

## What users of the app can do:
- [x] A user can log in/authenticate
- [x] A user can report a swarm
- [x] Users can see swarm reports in their city
- [x] A responding user can see the report and find out how far away it is from their location upon opening the app.
- [x] A responder can “claim” a swarm, at which point it is no longer visible to other users.
- [x] Reports and responses are persisted on a database.
- [x] Reporter reports essential details about the swarm (does it require a ladder? Approximate size of swarm (baseball vs. basketball))
- [x] Users can post a picture of the swarm
- [x] A user is alerted if the email address already exists
- [x] Users can see swarms they've claimed but not a separate page
- [ ] Users can see swarms they've reported by have not been retrieved
- [ ] Claims are removed from the database once the claimer confirms that the swarm was retrieved
- [ ] Users can swipe down on the screen to refresh the location
- [ ] Users are prevented from retrieving more than one swarm a week
- [ ] A claimed swarm re-appears as available if it is not physically retrieved in 6 hours
- [ ] Contact information between reporter and responder is exchanged
- [ ] Users can see swarm reports within a certain radius
- [ ] A responding user can see the report and find out how far away it is from their location/address, updated in real time
- [ ] Users are notified when a new swarm is reported in their area
- [ ] The reporting user can see who claimed the swarm and how far away they were when they claimed it.
- [ ] Contact information between reporter and responder can be exchanged if both sides want that

## Future features:
- [ ] A claimed swarm re-appears as available if it is not physically retrieved by a certain amount of time
- [ ] Swarm responders pay swarm reporters a very small sum of money for retrieving the swarm (this will incentivize the reporters to adopt)
- [ ] Reporters and responders can cancel a report/claim
- [ ] Reporters can decline a claim

## Distant future features:
- [ ] Reporters can see responder’s geographical progress towards them on a map
- [ ] Ratings/feedback for the responders can be provided
- [ ] Ratings/feedback for the reporters can be provided
- [ ] Rating/feedback data is persisted on a database
- [ ] Ratings can influence e.g. the radius or frequency with which swarms are reported to a particular user

## Planning

### Useful tutorials

#### Potential
* [GeoFire](https://github.com/firebase/geofire-java)

#### Actual
* [Google location service tutorial from treehouse](http://blog.teamtreehouse.com/beginners-guide-location-android)
  * You may have to use Run-time permissions, which are not covered in the above tutorial

### Master Checklist

- [ ] Strip API key info. from commit history


### Models
- [x] SwarmReport


### Forms
- [x] Create a claim
- [x] Create account
- [x] Log in

### Activities
- [x] LoginActivity
- [x] Landing activity
- [x] Create an account activity
- [x] Report swarm activity
- [x] MainActivity (user see swarm reports in their city)

### Adapters
- [x] FirebaseViewHolder

### API
- [x] Google services

## Specs/
| Behavior                   | Input Example     | Output Example    |
| -------------------------- | -----------------:| -----------------:|


### Set Up

* Clone repository from GitHub: Navigate to your computer's terminal and type, `git clone https://github.com/Atticus29/bivuoac.git`
* Open Android Studio (if not already installed, [begin install process here](https://developer.android.com/studio/index.html))
* Create a file called "/app/src/main/java/com/example/guest/iamhere/SecretConstants.java"
  * Populate this file with the following String variables:
	  * `public static final String googleApiKey = "youGoogleApiKeyHere";`
   * `public static final String STATIC_MAP_API_KEY = "youStaticMapApiKeyHere";`
  * You can obtain the latter by following [these instructions](https://developers.google.com/maps/documentation/static-maps/intro)
  * `public static final String BASE_GEOCODE_URL = "http://maps.google.com/maps/api/geocode/json?address=";`
  * `public static final String GEOCODE_ADDRESS = "address";`
* Click run in the top option bar (looks like a, "play" icon)

# License

The MIT License (MIT)

Copyright (c) 2017 Mark Fisher

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

---

## Prerequisites/Installation

You will need the following things properly installed on your computer.

* [Git](https://git-scm.com/)
* [Android Studio](https://developer.android.com/studio/index.html)
