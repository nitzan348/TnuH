# TnuH

This is an app is an assitance app for speech articulation exercises originally developed in the "Migdal David" program for [milbat](https://www.milbat.org.il/).

## Main features
1. Includes three exercises
    1. Wide smile
    2. Wide mouth opening
    3. Kiss
2. Adjustable difficulty and adjustable minimum symmetry
3. History of all completed exercises sorted by date
4. Translation into English, Hebrew and Russian
## Main missing features
1. Tongue exercises
2. Translation to Arabic
3. Progress bars allowing users to see partial success

## Building The App
Because of difficulties with mediapipe, the setup process could be a bit difficult.
### Prerequisites
* Android Studio
* Android sdk 31 and ndk 21
* Gradle
* Bazel (for option 2)
### Option 1 - Use pre-built mediapipe
1. Clone the repository and link your sdk to the project
2. Add the custom aar file to `app/libs` which can be found [here](https://drive.google.com/file/d/1ZLd10UAiko3dN_JOFY4yNite3Z656V71/view?usp=sharing)
3. Sync gradle
### Option 2 - Build your own aar
1. Clone the mediapipe library from [here](https://github.com/google/mediapipe).
2. Open the file `mediapipe/java/com/google/mediapipe/BUILD` and append to it the following:

       load("//mediapipe/java/com/google/mediapipe:mediapipe_aar.bzl", "mediapipe_aar")  
         
       mediapipe_aar(  
           name = "mesh_export",  
         calculators = [  
           "//mediapipe/graphs/face_mesh:mobile_calculators",  
         "//mediapipe/graphs/face_mesh:desktop_live_calculators",  
         "//mediapipe/calculators/util:from_image_calculator",  
         "//mediapipe/calculators/image:image_transformation_calculator",  
         ]  
       )

3. Call the command:

       bazel build --fat_apk_cpu=arm64-v8a,armeabi-v7a,x86_64,x86 //mediapipe/java/com/google/mediapipe:mesh_export.aar

4. Download the aar file from [here](https://mvnrepository.com/artifact/com.google.mediapipe/solution-core)
5. Unpack both aar files. Replace the jni folder in the aar you just downloaded in step 4 with the jni folder from `mesh_export.aar` you created in step 3
6. Zip the new aar file (jni from step 3 and everything else from step 4) into `mediapipe_core_custom.aar`
7. Clone this repository
8. Move the created `mediapipe_core_custom.aar` into `app/libs`
9. Sync gradle
 
