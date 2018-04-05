# Classifieds_Android_Project
INTRODUCTION:

Secure Classify is an android application in which users can 
share and view advertisements. Each user is verified through
Aadhaar authentication process.

PROJECT REQUIREMENTS:
 
minimum sdk-version = 3
Permissions: storage, Camera permissions to be after installing 
	     the application in the device.

BUILDING

The project directory is ready for use from either Eclipse or use
Android Studio.

To use Eclipse, install Eclipse, the Android SDK and the Eclipse ADT
plugin. Then start Eclipse, and create a new Android project. Use the
"from existing source" option, and browse to this directory. The
Android project should then be created, and you can browse the source,
build the application and run it in the emulator.

You can even use Android Studio to load the project and just build and
run in an emulator. Before running make sure to setup an emulator from
AVD manager.

APK

You can find the apk file here...Classifieds\app\build\outputs
Install the apk in a device and make sure to give storage and camera 
permissions.

SERVER

IP:192.168.60.27
username: iiitb
password: Th@nk$@#123

HTTP servlet: apache-tomcat-9.0.0.M22
IDE: Eclipse neon.3 with maven
Additional jar files included: mysql-connector-java-3.1.14, java-jason.jar

DatabaseConnection.java and UIDAIDatabaseConnection.java should be included
in a package named database.

Aadhaar.java and AadhaarResource.java should be included in package resource.

Setup the Tomcat server in eclipse and run the project on it.

Database: mySQL
Access database from terminal commands:
$ mysql -u root -p
Enter password: password
