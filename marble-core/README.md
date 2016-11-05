[ ![Codeship Status for miguelfc/marble](https://codeship.com/projects/55178540-e169-0133-32b4-4a308660d35a/status?branch=master)](https://codeship.com/projects/145333) [![Stories in Progress](https://badge.waffle.io/miguelfc/marble.png?label=In Progress&title=Tasks In Progress)](https://waffle.io/miguelfc/marble) 
This is the Marble Initiative!
======

You will find here the main repository of this project. Feel free to take a peek, download and try this code, but be aware that it is not a finished product and most probably will crash your server.

If you want to know more about the project, the people involved, and the objectives, please head to: http://marble.miguelfc.com

You can contact me through my website if you need deeper information or want to get involved in this project: http://miguelfc.com.


Development Log
-----
- *2016-02-14*: Bootup branch was merged to the master one. All work will be done from the main branch this time forward. Everything is rest based, using AngularJS and the usual things in the backend (based on spring data). You only need to provide a mongodb database, everything else is provided by marble.
- *2015-10-12*: Finally a decision has been made. The selected projects to be used in this one are: Spring Framework, Thymeleaf (for minor things only) and AngularJS, all supported by Spring Boot as the "application server" and MongoDB as the database. The focus is to provide a powerful Rest API, in order to decouple the presentation layer from the backend server. This work is already done for the Topic model, and will be extended to the rest of the components. All the changes are already in the bootup branch.

- *2015-09-26*: Now you can follow development in the bootup branch. I am exploring the possibility of using Vaadin as the UI framework, so you will find some things related to it in the code. The final decision will be between Vaadin and Thymeleaf.

- *2015-09-06*: All the java classes have been migrated to the new architecture, and individual web components for Topics, Twitter Api Keys and Executions have been migrated to Thymeleaf. The entire processing and extraction flow remains untested, and the web elements are in the migration queue (this is the next task in hand). External components like bootstrap and jQuery have been externalize to a webjars scheme, leaving only custom code inside our project (as it should be from now on).

- *2015-08-30*: 
After a few months of inactivity, I'm back at work, and these are a few of the points being handled>
  - All postgres data will be moved to MongoDB, making postgresql no longer a requisite.
  - Marble will use spring data for mongo, instead of only a custom repository.
  - Apache Tiles will be replaced with Thymeleaf, as it is included in the spring boot suite. 
  - The application will run using spring boot capabilities, not needing a Java EE anymore (it will still be an option, but it will not be the only one).
  As I'm a bit lazy to work with branches right now, I will be working under the hood until a pseudo-stable version is available.


Installation Steps
-----

You will need a server, or collection of servers with the following:

- A Mongo Database (version 3.2 recommended, but any 3.x version should work)
- Java 8 or later.

Version numbers are just  for reference. I haven't got time to check other versions, but I guess that later versions of each part should work. 

So, after you have everything set up, you will need to create users and database instances for the system to use (one in Postgres and one in Mongo). You can assign the standard read/write privileges, but the user should own the database instance, so that it could create and delete tables without any issue.

Now, download, clone or export the project, and find the pom.properties file in the base path. Fill in the info for the database access, and finally package everything using maven:

mvn package

Finally, execute the generated jar (it is a spring boot application), and the system should go up in no time.



