# SingletonAPI
A simple API that provides the ability to restrict the amount of open instances of a program to 1.
<p>
The Singleton API utilizes the idea that only 1 program can be bound to a specific port on a computer at any given time.
To properly use the API, you need only implement the Singleton interface into the main part of your program and 
then register your Singleton instance with the SingletonHandler.
<p>
An example implementation of the API is provided as ExampleSingleton.java in the <b>src</b> folder.
