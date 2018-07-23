# react-native-motionviews

## Code Guide : 

Use MotionViews-Android in react native

## Settings: 

check out config object structure in index.js

## Installation: 

To use button icons use font linking syntax (since react-native link only links fonts i used https://github.com/csae0/react-native-asset#filterAndRename to perform asset linking):

"rnpm": {
"assets": [
"./fonts",
"./App/Images/Icons"
]
}

Link to project:

add to settings.gadle:

include ':BoxedVerticalSeekBarLib'
project(':BoxedVerticalSeekBarLib').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-motionviews/android/BoxedVerticalSeekBar/BoxedVerticalSeekBarLib')
include ':react-native-motionviews'
project(':react-native-motionviews').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-motionviews/android/react-native-motionviews')
include ':react-native-merge-images'

add to MainApplication.java:

import at.csae0.reactnative.RNMotionViewPackage;
...
@Override
protected List<ReactPackage> getPackages() {
return Arrays.<ReactPackage>asList(
...
new RNMotionViewPackage()
...
);
}

# MotionViews-Android

![alt tag](http://i.giphy.com/3o7TKJhBZiimAe6JDG.gif)

## Code Guide : How to create Snapchat-like image stickers and text stickers

After spending 2000+ hours and releasing 4+ successful apps working with 
image transformations, we’ve decided to share our experience with the community.

## Task

So the task is pretty simple: **add the ability to move, scale and rotate stickers on Android**.

Even though it sounds easy, there are a couple of challenges as well. 
First, there is a zillion of screen sizes of Android devices, and we’d better 
support them all (or as many as we can). Moreover, it could be the case 
that you would need to enable users to save/edit their selfies. And if 
they open their custom works on other devices — the screen size might 
change, the loaded images might be of a different quality, etc.

As you might have guessed, the task is getting more complicated now.

**The solution needs to work on different screen sizes and be independent of the image quality**.

**In the second part we've also added an ability to create text stickers, 
update them, and manipulate in the same way as with image stickers**.

## Solution

**MotionViews-Android** - is fully functional app that meets the requirements.

Check the Medium articles [How to create Snapchat-like stickers for Android](https://medium.com/uptech-team/how-to-create-snapchat-like-stickers-for-android-50512957c351) 
and [How to create beautiful text stickers for Android](https://medium.com/uptech-team/how-to-create-beautiful-text-stickers-for-android-10eeea0cee09) about the details of the implementation.

Feel free to use the code for your own purposes. 

Check out the app on [Google Play](https://play.google.com/store/apps/details?id=team.uptech.motionviews).

Play with the online app emulator on [Appetize.io](https://appetize.io/app/kd51amwzp7fg4f8wrrb5mz673w).

The video of what we got in the end on the YouTube: [Image Stickers](https://www.youtube.com/watch?v=6IkmFmlrLPA) and [Text Stickers](https://www.youtube.com/watch?v=9q86Dx9-xTA).

