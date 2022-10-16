# Android Programming Project Proposal

## Title

## Group Members

Yueran Yang (yy9928)

Xiaoxiang Yang (EID: xy4467, Email: xy4467@utexas.edu)

## Brief Intro of the app

We would like to do a trip planner app, which allows user to manage their planning trips and set up their schedules for those travels. The main functionalities are managing trips, planning the potential destinations and schedules of the trip and maybe setting up them inside the system calendar. We will not include any recommendations or other advertisements inside this app. 

## Subsystems of the App
1. A summary list of planned trips;
2. A detailed view of each trip, including basic information such as time, location, itinerary, weather, and etc
3. A login page; 
4. A map view for visualizing the location of each point of interest;
5. A calendar view for planning (if time allows).

## Major User Interface Elements
After opening the app, there will be a login page provided by Firebase, where users will be able to enter their email and password to log into their accounts.
After login, there will be a trip summary page, which is basically a list view of all the trips planned.
Each trip entry on the summary page will be clickable, and once you click on one of them, you will be brought to a detailed view of that particular trip.
In that will, you will able to see the time, location, detailed itinerary, and current weather associated with that trip.
On the top of the screen you will have two buttons. One is Map, which if clicked, will bring you into a map view to help visualize the location of each point of interest. Another one is Calendar, which if clicked, will show the itinerary on a calendar.

Please see the attached drawing.

## Weekly Schedule

## Attempted APIs

- Map APIs
  
  We plan to use google map apis for this app to show users the locations. 

- Database
  
  We plan to use Firebase to manage all the authentication data and users' trip data. 

- Weather API

  There are multiple APIs available that can help us get weather information for a given location, such as Openweathermap or Yahoo! Weather.

- Calendar API (potential)
  
  We plan to use calendar apis to help user import their schedule into the calendar. This is a stretch goal for the project. 

## Functional Specification

1. We will have a login system (using firebase). 

2. We will have a to-trip list as our main view to users, where users can add, delete or modify their plans for each trip. 

3. We will include a map function inside the app to show the locations to the users.

4. Inside a trip, users can add/delete/edit their destinations, time schedules (optional) and comments (optional) if they want. 

## Discussion of Other Similar Apps

#### Wanderlog

This is a trip planning app, which allows users to have a detailed plan of all the reservations in a destination including flights, car rentals, buses, trains and so forth. The app also includes expenses statistics, which will allow users to set a budget or count their total expenses. 

Our app is aimed to be simple and clear. It will be like a to-trip list, containing only where and when to go. A person can have a plan for future 5 years where he would love to visit and record his thoughts on this app. Unlike Wanderlog or other great planning apps, the plan in our app is much more like an outline. 

## Our Inspiration

Our inspiration comes from the combination of todo list and trip planner. We would love to make a relatively simple app only to help people make a schedule on their trip, containing (when and) where to go.  
