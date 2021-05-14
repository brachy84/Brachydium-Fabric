# Brachydium

### Goal
The goal of Brachydium is to create an Api that can be used by anyone, is very modular and is easy to use.

Brachydium is the ultimate Tech mod api. It has everything you need:
 - Custom gui api (since vanillas sucks. I did consider Astrarre gui, but I don't like how it does stuff)
 - Complete new recipe system
 - easy BlockEntity system
 - material system to easily generate ingots, dusts, blocks etc... 
 - and more to come...

If you used GTCE from 1.12.2 before than some of the stuff might be familiar, since I used a few lines from it.

## Plans
Things I have planned for the future:
 - REI compat
 - Ores
 - Multiblocks
 - Armor and tools

Brachydium uses Astrarre transfer and acces as a Capability and fluid-item transfer api as Fabric doesn't currently have it's own;
Astrarre should be compatible with Lba and Tech Reborn.
I do plan to switch to Fabric transfer when it releases.

You can see exact Progress in the Project tab

## Contribution
I gladly accept contributions in form of issues and pull request.
Please discuss your idea on ![Discord](https://discord.gg/XwFsQjSwq7) before you begin to code.
### Requirements
Pull requests have to have decent code quality and good modularity. Remember this is an Api for anyone!
I will help if you have questions, just ask.

## Setup
To include Brachydium in you project add this to your build.gradle:
```gradle
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.brachy84:Brachydium:-SNAPSHOT'
}
```
You can should replace `-SNAPSHOT` with the latest version.
`-SNAPSHOT` is most of the time unstable

[![Release](https://jitpack.io/v/brachy84/Brachydium.svg)]
(https://jitpack.io/#brachy84/Brachydium)