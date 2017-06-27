# Bivuoac
An Android app. for connecting bee swarms to beekeepers.

## 19- June, 2017

## By: Mark Fisher

## Known issues
- [ ] Crashes on phone when you try to log out from email Login or google login


## Description

In the late spring/early summer, honey bee hives sometimes get so large that they need to divide, a process called swarming. During swarming, a new queen is made at the original hive, and the old queen leaves with about half of the workers. They form a temporary hive-less structure (called a Bivuoac) while they conduct a search for a permanent home. Unfortunately, these swarms/bivouacs are often formed on low-hanging branches of trees, in people’s chimneys, etc., and they creep people out. So people call the fire department, etc., who in turn contact local beekeeping organizations which keep what are called swarm lists. Swarm lists are lists of eager beekeepers in the area who will gladly take a new hive of bees off of somebody’s property.

The problem is that currently, swarm lists are not decentralized and are quite clunky (essentially glorified phone trees). Beekeepers have to pay membership fees to join the swarm lists.

Bivuoac is Uber for swarms: it matches swarm reporters with available beekeepers nearby.

## User stories
- [ ] A user can log in/authenticate
- [ ] A user can report a swarm
- [ ] A responding user can see the report and find out how far away it is from their location/address.
- [ ] A responder can “claim” a swarm, at which point it is no longer visible to other users.
- [ ] The reporting user can see who claimed the swarm and how far away they are.
- [ ] Reports and responses are persisted on a database.
- [ ] Reporter reports essential details about the swarm (does it require a ladder? Approximate size of swarm (baseball vs. basketball))
- [ ] Users can post a picture of the swarm
## Planning

### Master Checklist


### Models
- [ ] Claim


### Forms
- [ ] Create a claim
- [ ] Create account

### Activities
- [ ] LoginActivity
- [ ] MainActivity

### Adapters
- [ ]

### API
- [ ] Twitter (to Tweet results of completed experiments)

## Specs/
| Behavior                   | Input Example     | Output Example    |
| -------------------------- | -----------------:| -----------------:|
|User can enter experiment details|User enters experiment name, treatment 1 name, treatment 2 name, effect size|experiment details recorded|

### Set Up

* Clone repository from GitHub: Navigate to your computer's terminal and type, `git clone https://github.com/Atticus29/bivuoac.git`
* Open Android Studio (if not already installed, [begin install process here](https://developer.android.com/studio/index.html))
* Click run in the top option bar (looks like a, "play" icon)

### Future Functionality
- [ ] A user is alerted if a username already exists
- [ ] A claimed swarm re-appears as available if it is not physically retrieved by a certain amount of time
	- [ ] Contact information between reporter and responder is exchanged
	- [ ] Ratings/feedback for the responders can be provided
	- [ ] Ratings/feedback for the reporters can be provided
	- [ ] Rating/feedback data is persisted on a database
	- [ ] Ratings can influence e.g. the radius or frequency with which swarms are reported to a particular user
	- [ ] Swarm reporters pay swarm responders a very small sum of money for retrieving the swarm.
	- [ ] Reporters and responders can cancel a report/claim
	- [ ] Reporters can decline a claim
	- [ ] Reporters can see responder’s geographical progress towards them on a map

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

## Dependencies
* None yet
