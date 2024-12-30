Readme.txt:
Steps:
Please clone the project from https://github.com/andrewPao/game-sales-import.git
Switch/Check out to master branch

Database:
1) Establish a connection and create a new schema "game_sales_db" in mySQLWorkBench.
2) In application.properties file, user may required to edit the username and password according to their own local db configuration.
3) Open "Script" folder and execute these queries from "vanguardTest_script.sql" into the new created schema "game_sales_db" from mySQL workbench.

Project: 
4) import as existing maven project into IDE.
5) run GameSaleImportApplication.java as Java application.

Postman:
 
Generate Csv end point:
http://localhost:8080/gameSalesApi/generateCsvFile

Task 1)
Import end point:
Post: http://localhost:8080/gameSalesApi/import
Select Body tab -> Key : file , Value: -- navigate to game-sale-import/target/output , Description: file
*Note: The csv file that contains 1,000,000 rows as per requirement locates at game-sale-import/target/output*


getGameSales endpoint:
Task 3 part 1) A list of game sales
http://localhost:8080/gameSalesApi/getGameSales?page=0&size=100

getGameSales endpoint:
Task 3 part 2) A list of game sales
http://localhost:8080/gameSalesApi/getGameSales?fromDate=2024-04-01&toDate=2024-04-30&page=0&size=100

getGameSales endpoint:
Task 3 part 3) A list of game sales
http://localhost:8080/gameSalesApi/getGameSales?price=30&lessThan=true&page=0&size=100
http://localhost:8080/gameSalesApi/getGameSales?price=30&lessThan=false&page=0&size=100


getTotalSales endpoint:
total number of games sold during a given period
Task 4 part 1)
http://localhost:8080/gameSalesApi/getTotalSales?fromDate=2024-04-01
Task 4 part 2)
http://localhost:8080/gameSalesApi/getTotalSales?fromDate=2024-04-01&toDate=2024-04-30
Task 4 part 3)
http://localhost:8080/gameSalesApi/getTotalSales?fromDate=2024-04-01&toDate=2024-04-30&gameNo=59


Task 5:
Screenshot_ExecutionTime:
The screenshot of execution time for task 3,4 and 5 are stored in game-sale-import/Screenshot_ExecutionTime

