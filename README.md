> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
This project is a server application that provides a web API for data retrieval and search. 
Team members: Anna Luiza Arantes (asantan7) and Ibrahim Shah (ishah20)
Total estimated time: 20 hours
Link to repo: https://github.com/cs0320-s24/server-asantan7-ishah20

# Design Choices
This server responds to requests form both CVS files and the United States Census API. 
These requests are dealt with in the Sever class. Every request (loadCSV, viewCSV, searchCSV, 
and broadband) has its associated handler class (loadCSVHandler, viewCSVHandler, searchCSVHandler,
and broadbandHandler). The broadbandHandler handles an incoming request for broadband data 
by fetching data from the ACS API based on state and county parameters. The developer can control
the caching of ACS request-responses by modifying the parameters within this class's constructor.
If the developer opts to cache, BroadbandHandler then uses the ACSAPICacheProxy class. If not,
it uses the ACS API class (both of which extend from the ACS interface). 

# Tests
TestBroadbandHandler contains tests for the BroadbandHandler class. At the same time, 
TestLoadCSVHandler contains tests for the LoadCSVHandler class and TestSearchCSVHandler 
contains tests for the SearchCSV class.
MockACSAPI imitates the data retrieved by the ACS API.
These tests are currently not working as we are getting a NoClassDefFoundError:
java.lang.NoClassDefFoundError: org/junit/jupiter/api/extension/ScriptEvaluationException

# How to
To run this program, run the server, which will create a local host. 
Then, you can create entries as such to get your desired output (these have example parameters):
Load a CSV:
localhost:2412/loadCSV?filepath=stars/stardata.csv&hasHeaders=true
View a CSV:
localhost:2412/viewCSV
Search a CSV:
localhost:2412/searchCSV?value=Rory&column=1
Retrieve Broadband Percentage:
localhost:2412/broadband?state=California&county=Butte
