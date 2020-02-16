

# Adventurio
## Description:

Adventurio aims to allow its users to create their own account within the application and gain full access to Adventurio. The user can easily log their walking trips and keep track of their statistics. Each trip uses GPS to record location, along with a chronometer keeping track of time taken to finish the trip. For walking trips, the users steps are also recorded. The user can create their own overall Step and Distance goals, which will be displayed on the Statistics page with all relevant statistics.

![Logo.png](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_650/v1581811426/icon.png)
## Walkthrough:
### 0.) Permissions:
Before getting started with the application, location permissions need to be granted to the application in order to get the full Adventurio experience. 

![https://res.cloudinary.com/dkdptqakb/image/upload/v1581867226/Screenshot_20200216-151410_Package_installer.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_290/v1581867226/Screenshot_20200216-151410_Package_installer.jpg)

### 1.) Account creation and login:
Firstly, the user will need to create their accounts through the registration page. Once this has been done, the user's account is sent to a JSON file from which the application can take their details and give them full access upon logging in.
Should the user require a password reminder, "Forgot Password" should be then clicked. This is not yet totally secure and security questions will be added in the future.

   ![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811290/Screenshot_20200215-205308_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_290/v1581811290/Screenshot_20200215-205308_Adventurio.jpg)   ![1](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_290/v1581811289/Screenshot_20200215-205205_Adventurio.jpg) ![https://res.cloudinary.com/dkdptqakb/image/upload/v1581813196/Screenshot_20200216-003213_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_290/v1581813196/Screenshot_20200216-003213_Adventurio.jpg)
  
  ### 2.) Statistics (Home) and Profile
  The user after logging in, will be greeted with the statistics page. This will show the user's current progress with trips and their overall goals. The user can then go to the profile fragment through the accompanying Navigation Drawer, and view, delete or edit their accounts.
  
  ![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811287/Screenshot_20200215-205431_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581811287/Screenshot_20200215-205431_Adventurio.jpg)![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811288/Screenshot_20200215-205445_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581811288/Screenshot_20200215-205445_Adventurio.jpg)![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811289/Screenshot_20200215-205452_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581811289/Screenshot_20200215-205452_Adventurio.jpg)
### 3.) Recording A Trip 
When the user wishes to take an adventure/trip, they should tap on the "Record Adventure" fragment through the Navigation Drawer. The user will be presented with a screen where he will be able to input a goal for the journey they're about to take. In this example 50 steps were used.

When a trip is started, a chronometer will appear along with a step counter and progress bar. The progress bar measures the percentage of the step goal of the ongoing journey. The progressbar will remain at 0% if no step goal is entered. While the trip is recording, it continuously receives the user's coordinates (lat/long), adding them to a temporary mutableListOf Strings.

Once the stop button is clicked, the application will automatically save the trip to a JSON file with details such as Time Started/Ended, The result of the Chronometer, and two lists containing Lat/Long coordinates.

![https://res.cloudinary.com/dkdptqakb/image/upload/v1581817111/Screenshot_20200216-013644_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581817111/Screenshot_20200216-013644_Adventurio.jpg)![https://res.cloudinary.com/dkdptqakb/image/upload/v1581817111/Screenshot_20200216-013653_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581817111/Screenshot_20200216-013653_Adventurio.jpg)![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811291/Screenshot_20200215-205712_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581811291/Screenshot_20200215-205712_Adventurio.jpg)
### 4.) Viewing/Deleting/Editing A Trip
 Once the trip is finished recording and saving, the trip/adventure will be added and listed in a RecyclerView within a card in the "List Adventures" fragment. Each of these trips can be view and edited. To view a trip, the user can tap the Trip Card which will then show, the route they took on their journey in google maps (plotted through coordinates saved to the journey with polylines) along with additional statistics. 
 
These trips can also be edited (if for some reason, the phone does not have sensor or GPS options). Distance and Steps taken in can be edited, along with two sets of NumberPickers to change "time started" and "time finished" fields. Time elapsed is automatically calculated if the trip is edited.

![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811289/Screenshot_20200215-205630_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581811289/Screenshot_20200215-205630_Adventurio.jpg)![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811291/Screenshot_20200215-205649_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581811291/Screenshot_20200215-205649_Adventurio.jpg)![https://res.cloudinary.com/dkdptqakb/image/upload/v1581811291/Screenshot_20200215-205822_Adventurio.jpg](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1581811291/Screenshot_20200215-205822_Adventurio.jpg)

## References
### 1.) [Step Counter](https://medium.com/@ssaurel/create-a-step-counter-fitness-app-for-android-with-kotlin-bbfb6ffe3ea7)
### 2.) [Getting Coordinates Constantly](https://stackoverflow.com/questions/45958226/get-location-android-kotlin)
### 3.) [Stopwatch](https://github.com/ajithvgiri/Stopwatch)

## Video
### [Video Demo](https://www.youtube.com/watch?v=3lxLHU4wIV4)
