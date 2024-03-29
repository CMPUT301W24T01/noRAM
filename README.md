# noRAM
A course project for CMPUT 301 in the Winter 2024 semester at the University of Alberta.

See our [wiki](https://github.com/CMPUT301W24T01/noRAM/wiki) for details on submission for different project parts,
our [javadocs](https://github.com/CMPUT301W24T01/noRAM/tree/master/doc/javadocs) for our generated documentation, our [source code](https://github.com/CMPUT301W24T01/noRAM/tree/master/code/noRAM), or our [releases](https://github.com/CMPUT301W24T01/noRAM/releases).

## Team Members

| CCID          | Github        | Name          |
| ------------- | ------------- | ------------- | 
| clventer      | WhoShotCupidd | Christiaan    | 
| dewis         | ColeDewis     | Cole          | 
| elchung       | ethanchung936 | Ethan         | 
| cboyles       | Semicarlin    | Carlin        | 
| staskovi      | ServingCoder  | Sandra        | 
| gdumouli      | gdumouli      | Gabriel       | 

# Dependencies
Below is a list of the android gradle dependencies used for this project.

| Dependency                                     | URL                                                                             | Use Case
| ---------------------------------------------- | ------------------------------------------------------------------------------- | ----------------------------------------------
com.google.firebase:firebase-auth                | https://firebase.google.com/docs/auth                                           | For providing authentication services for users so they can be identified between app occurrences. 
androidx.appcompat:appcompat:1.6.1               | https://developer.android.com/jetpack/androidx/releases/appcompat               | Standard activity library. 
com.google.android.material:material:1.11.0      | https://github.com/material-components/material-components-android              | Standard material UI elements. 
androidx.constraintlayout:constraintlayout:2.1.4 | https://developer.android.com/jetpack/androidx/releases/constraintlayout        | Standard constraint layout for xml. 
junit:junit:4.13.2                               | https://mvnrepository.com/artifact/junit/junit/4.13.2                           | Standard junit unit tests. 
org.mockito:mockito-core:5.11.0                  | https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html | Mocking objects for unit testing. 
com.google.firebase:firebase-bom:32.7.2          | https://firebase.google.com/support/release-notes/android                       | Firebase database connection that contains all the data including the photos and all users. 
com.google.firebase:firebase-firestore           | https://firebase.google.com/docs/firestore/quickstart                           | Firebase firestore connection.  
com.google.firebase:firebase-storage             | https://firebase.google.com/docs/storage                                        | Firebase storage connection that allows us to access the stored images. 
com.github.yuriy-budiyev:code-scanner            | https://github.com/yuriy-budiyev/code-scanner                                   | QR Code scanner that the user will use to join an event. Generates through XZing QR codes as well. 
com.github.dhaval2404:imagepicker                | https://github.com/Dhaval2404/ImagePicker                                       | To upload photos from device to be used as a profile picture for the user and event photos for events. 
androidx.test.ext:junit:1.1.5                    | https://mvnrepository.com/artifact/org.junit                                    | Standard junit library. 
androidx.test.espresso:espresso-core:3.5.1       | https://developer.android.com/jetpack/androidx/releases/test                    | Standard espresso testing library. 
androidx.test.espresso:espresso-intents:3.5.1    | https://androidx.tech/artifacts/test.espresso/espresso-intents/                 | Espresso intent functions. 
androidx.test.espresso:espresso-contrib:3.5.1    | https://androidx.tech/artifacts/test.espresso/espresso-contrib/                 | Espresso contrib functions. 
org.mockito:mockito-android:5.11.0               | https://javadoc.io/doc/org.mockito/mockito-android/latest/index.html            | Mockito android library for unit testing. 
nl.dionsegijn:konfetti-xml:2.0.4                 | https://github.com/DanielMartinus/Konfetti                                      | Library for fun confetti animations. 
