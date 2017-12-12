# 498Plugin

Where the system begins-
  The installation candidate should be a linux machine running the latest version of Jenkins, fully configured with users, permissions, and access to the web GUI.
Code structure-
  The code is mostly in one large file, separated into subclasses and helper functions. The main perform function kicks off the action and asks both the log parsing helper function and XML parsing helper function to run, and then uses them for reporting help to the user.
Compiling-
  The plugin can be compiled with maven and the included pom.xml file.
Running-
  The program can be compiled and run with maven and the included pom.xml by using the command mvn hpi:run and accessing it on either port 8080 or 8090 depending on your linux distribution, refer to your distributions wiki for more information on running jenkins test servers. 

Project Overview:
Make Jenkins produce finer grained messages, not just "build passed" or "build failed". 
Currently, when there is a build failure, the only output from Jenkins is a message that says, 
  "Build Passed" or "Build Failed". 

The goal of this project is to make Jenkins to give more details whenever there is a build failure. 
For example, if the build failed because tests fail, Jenkins should say so (it should also say which tests fail). 
Also, if the build failed because of missing dependencies or failures due to hardware or software (mis)configuration, 
Jenkins should output this along with the standard "build failed" message.
