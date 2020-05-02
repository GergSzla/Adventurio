

# Adventurio (v1.2.1)
## Description:

Adventurio is an application which allows its users to keep track of any trips they may wish to take. Personally, I decided to develop this application because I always like to keep track of my journeys when I drive and keep track of the distances covered during the trip. I also enjoy walking and cycling and go quite frequently. The issue was that I had to have different applications for driving, cycling and walking and was therefore inconvenient switching between apps. I decided to build all of these functionalities into Adventurio so other users can keep track of all of their trips.
This README aims to outline all the functionalities Adventurio offers at its current version (v1.2.1) to its users.


![Logo.png](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_650/v1581811426/icon.png)

## Walkthrough:
Adventurio offers a range of functionalities that the users can use to allow them to have the best and most comfortable experience possible with the application. 
### 1) Login, Registration and Password Reset
Adventurio allows for two forms of login to use the application. Users can login using their Google accounts with ease if they wish without the need for registration. In this case, the application automatically saves their authentication details in Firebase and creates a user object on Firebase that can be used by the app. If the user exists within Firebase, the user will be logged in without creating a new user. 
A user can also choose an ordinary login. This login requires a manual registration to be performed. This can be done through the registration page. Here the user must enter all necessary details and once the registration button is pressed, their authentication details are saved, and a new user object is created in Firebase. Once registration is complete, the user can sign in with their email and password.
The app also includes a “Forgot Password” functionality, where if the user forgets their password, they can simply reset it through the reset password confirmation link that will be emailed to them.
![Login](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437544/Screenshot_20200427-234603_Adventurio.jpg) ![Registration](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437545/Screenshot_20200427-234614_Adventurio.jpg) ![Password Reset](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437545/Screenshot_20200428-211904_Adventurio.jpg)

### 2) Statistics Display
Upon a successful login, the user is redirected to the Statistics page which acts as the home page for the application. This page is simply informative with some added visual representations of the user’s statistics and data. 
It shows the user’s individual statistics for all three categories available: cycling, driving and walking. It shows details such as their overall progress with regards to their goals, their average distance, speed, and calories burned to the applicable categories.

To make full use of the statistics page, the user should first go to their profile with the help of the nav-drawer and adjust their goals, as it they are all set to zero by default when accounts are created.
![Stats1](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437548/Screenshot_20200428-204449_Adventurio.jpg) ![Stats2](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437552/Screenshot_20200428-204458_Adventurio.jpg)

### 3) User Profile
Each user has their own profile. By navigating to the Profile fragment of the app, the user can view their profile and their details. This fragment also offers the ability to delete or edit their profile if they wish. When editing their profile, the application reauthenticates the user if the user used the ordinary login type.
![Profile](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437541/Screenshot_20200428-220122_Adventurio.jpg) ![DeleteProf](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437545/Screenshot_20200428-220129_Adventurio.jpg) ![EditProf](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588438409/Screenshot_20200502-175151_Adventurio.jpg)

### 4) Adding and Recording a Trip
A user has the ability to record a trip or manually enter a trip should they decide against sharing their location with the application. 
If the user wishes to record their trip the standard way, firstly, they must give location access to the application via the permission request popup (or phone settings). The user here can record three different types of trips, each record different data to be stored. For example, walking records steps whereas cycling records speed instead. If the user records a driving trip, a vehicle will be required. When creating a vehicle, the user will need to put in the vehicle’s odometer count. If the user takes a particular car on a trip, the distance of that trip is automatically added to the odometer.
The user can also enter a trip manually if they choose to. Here they can select the trip category through a “spinner” dropdown. The UI is updated according to the selected item in the spinner, showing the user the required data for the specific trip.
![ManualTrip](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437548/Screenshot_20200428-204742_Adventurio.jpg) ![record1](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437532/Screenshot_20200428-204906_Adventurio.jpg) ![record2](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437534/Screenshot_20200428-204830_Adventurio.jpg) ![record3](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588438742/Screenshot_20200502-175654_Adventurio.jpg) ![record4](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588438742/Screenshot_20200502-175636_Adventurio.jpg)

### 5) Listing Trips
Needless to say, the users have the ability to list all of their trips through the “List Trips” fragment. This list allows for filtering with the buttons provided above the list. The list contains each trip displayed in a Card format providing the user with all the relevant details of the trip, including helpful icons indicating the category of the trips. 
#### 5.1) Editing, Deleting and Viewing a Trip
The user can edit their trips using simple a simple left to right swipe on the trip’s card. This will then open up the trip in an edit view and allows for the editing of any data within the trip.
Trips can be deleted in a similar fashion, requiring the user to do a right to left swipe on the card instead.
The trip can be viewed by tapping the card of the trip. This will allow the user to simply view the route they took shown on a Google map (Maps SDK), also showing the specific trip’s relevant statistics.
![ListTrips](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437532/Screenshot_20200428-204951_Adventurio.jpg) ![EditTrip](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437537/Screenshot_20200428-205045_Adventurio.jpg) ![DeleteTrip](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437535/Screenshot_20200428-205055_Adventurio.jpg) ![ViewTrip](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437536/Screenshot_20200428-205137_Adventurio.jpg)

### 6) Listing Vehicles
To record driving trips (standard or manual), a vehicle is required. The user can view their existing vehicles by navigating to the “Vehicles List” fragment in the app. 
#### 6.1) Adding, Editing and Deleting a Vehicle 
If the user wishes to add a vehicle to record driving trips, they can simply navigate to the “Add Vehicle” fragment, presenting the user with the screen to create a new vehicle. When this vehicle is added, the vehicle is also saved in Firebase. A user’s vehicles are saved within an array containing the user’s vehicles in the user object in Firebase.
To edit these vehicles, the user can swipe left to right on the vehicle’s card, presenting the user with the edit screen. Vehicles can be deleted simply by swiping right to left on the vehicle’s card.
![CreateVehicle](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437547/Screenshot_20200428-205709_Adventurio.jpg) ![EditVehicle](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437570/Screenshot_20200428-205657_Adventurio.jpg) ![ListVehicle](https://res.cloudinary.com/dkdptqakb/image/upload/c_scale,w_280/v1588437540/Screenshot_20200428-205645_Adventurio.jpg)

## References
### 1.) [Step Counter](https://medium.com/@ssaurel/create-a-step-counter-fitness-app-for-android-with-kotlin-bbfb6ffe3ea7)
### 2.) [Getting Coordinates Constantly](https://stackoverflow.com/questions/45958226/get-location-android-kotlin)
### 3.) [Speedometer (Cycling UI) used for getting speed (Driving and Cycling)](https://github.com/ajithvgiri/Stopwatch)
### 4.) [fade in/out transition](https://www.tutlane.com/tutorial/android/android-fade-in-out-animations-with-examples)
### 5.) [slide up/down transition](https://www.tutlane.com/tutorial/android/android-slide-up-down-animations-with-examples)
### 6.) [left to right transition](https://stackoverflow.com/questions/5151591/android-left-to-right-slide-animation)
### 7.) [Spinner Dropdown](https://android--code.blogspot.com/2018/02/android-kotlin-spinner-example.html)
### 8.) [ Speedometer and gauge Library (Displaying speed while recording for Driving, Used for data representation for     Statistics)](	https://github.com/anastr/SpeedView)
### 9.) [Float button menu Library used in View Profile](https://github.com/fstech/sell-android-floating-action-button)	 

