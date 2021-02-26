Stock Easy 
----------
An easy way for For Restaurants 🍔, Bars 🍺 Etc. to order food, drink etc from a personalized list of distributors.    
Uses Gradle, Spring, Kotlin, Seleneium  

Under Construction 🚧
--------------------- 
This app is made freely available as-is and can be used as a starter to create a custom personalized Web store for small or medium sized locations that currently rely on manually sending off orders.  


Run tests
---------
    gradlew test --build-file local.build.gradle.kts
	
Does not start the web server or load the database

Run full suite (<b>Depreciated</b>)
-----------------------------------
    gradlew testFullSuite --build-file local.build.gradle.kts
	
Creates a test database, loads test data, [re]starts the tomcat server and runs all tests

Environment variables used by project (auto added when building locally)
------------------------------
    ${DATABASE_URL}="postgres://dad:business@localhost:5432/dad_business_to_business"
    
    ${MAILGUN_DOMAIN}=""
    ${MAILGUN_KEY}="
    ${MAILGUN_FROM_NAME}="Test Account"
    ${MAILGUN_FROM_EMAIL}=""
	
Deploy application (Does not run tests)
---------------------------------------
    gradlew bootRun --build-file local.build.gradle.kts
	
Application will be available on `localhost:8080`

Creating the Database
---------------------
**TODO: This will be done in a script soon...**

    CREATE USER dad WITH PASSWORD 'business'

    GRANT ALL
        ON ALL TABLES IN SCHEMA public TO dad;

Then run:

    gradlew dbTestDataLoader --build-file local.build.gradle.kts

Specific data testing
---------------------
`gradlew loadKomodoLoco --build-file local.build.gradle.kts` - recreates database but loads KomodoLoco data in full

publishing
----------
`git push heroku master` - deploys to Heroku.


url: https://dad-business2business.herokuapp.com/store

running in emulator
-------------------
To run in a MAC VM; OS/X simulation on XCode visit 10.0.2.2 inside the device simulation

TODO:
-----
Proper spring views, right now we do it all in JavaScipt