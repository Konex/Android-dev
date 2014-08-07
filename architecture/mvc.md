mvc in Android
===========

MVC Message Dispatcher

What is MVC
 
•	Modular, flexible, loose coupling.
•	Good for unit testing.
•	Because of modular and isolation design, it is easier for maintenance – bug fix, upgrade.
•	Team cooperation – UI team, Data persistence team, business modelling team can work together without too much interference of each other.
•	Logical and componentry structure helps developers to write simple and better code.

MVC in Android
 
Models –POJO plain old java object (in C# it is called POCO – plain old CLR object). This is the package that holds model objects.
Views – layouts. 
Activities – controllers/activities.

Before MVC – Message Dispatcher
Project Structure
 

Classes
 
 


IEvent:
 

IDispatcher:
 

EventDispatcher:
Note l.onEvent(event); 
 





FooModel:
 
FooModel sets source:
   

Label change event flow
In activity: 
  
  
 
In FooModel:
 
In EventDispatcher:
 

In activity:
 









A More complicated/useful scenario
 

References

http://www.therealjoshua.com/2012/07/android-architecture-part-10-the-activity-revisited/
http://www.therealjoshua.com/2012/03/event-dispatching-sending-messages/





