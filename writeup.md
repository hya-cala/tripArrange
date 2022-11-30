# Project Writeup

#### Team member

- Yueran Yang - yang568382@gmail.com - yy9928

- XiaoXiang Yang - 

## App

Our project is a travel todo app called **To Travel**. In this app, users can record the place they would love to go in the future time and also track the weather of their destinations. 

## Screenshot of the main functionalities

#### List all the trips

![list_trip.png](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/list_trip.png)

#### Add a trip

![add_trip.png](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/add_trip.png)

#### List all the destinations in a trip

![](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/2022-11-30-16-39-54-image.png)

#### Add a destination in the trip

![](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/2022-11-30-16-41-07-image.png)

## APIs in the app

#### Weather API

We use weather api to fetch the current weather conditions of the destinations. The fetched current weather condition is shown in the list of destinations, by the weather icons and the temperatures. 

We also use the weather api to check if the user is entering a valid city for the destinations when they finish editing the destination detail. 

#### Firebase (Authentication and Database)

We use the authentication and database in the Google Firebase. 

## Third Party Libraries

#### Glide

We use glide to grab the weather icons mentioned above. 

## UI/UX/display code

We try to make the app in pastel colors, to make each page look relaxing to users.    We also try to change the fonts into more cute font families to make the user feel agreeable when they are looking at their plans. Also the pages looks very clear and simple, to make sure that users are not overwhelmed. 

## Backend logic

When we fetch the data for the user, we will first not fetch all the destinations data. When user try to click into the trip, we start to fetch the destinations for that trip to make the application start faster. 

## Challenge

When we tried to use glide to grab the icons, it was very weird to fail all the time. Then we try to change a lot of codes to try to fix it, but they all failed. At last, we found the solution on google that we might need to add a rule in the AndroidManifest.xml. 

## Database Structure

#### Trips

![](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/2022-11-30-16-59-59-image.png)

![](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/2022-11-30-17-00-23-image.png)

We have a collection named allTrips. Inside allTrips, the documents name are the user's uid. Then under the user's uid, we have Trips. Inside Trips, we have all the documents for a trip detail including the name, start date, end date and ownerUid (which is user's uid). (Description is deprecated).

#### Destinations

![](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/2022-11-30-17-02-17-image.png)

![](/Users/renayang/UTAustin/AndroidProgramming/project/tripArrange/images/2022-11-30-17-02-43-image.png)

Similar to allTrips, instead here we use the trip id to be documents under allDestinations. For each destination, we have description (which is note in the app), destination (which is location in the app), start date, end date and tripUuid. 


