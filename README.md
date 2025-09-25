# SOEN342
Nicole Antoun: 40284018

Sara Rezene Habte: 40224677

Sammy Mohellebi: 40282374




# Project Description:

This software system models a European railway network which loads train connection records from a CSV file into memory. It allows clients to:

Search for connections between cities based on parameters such as departure city, arrival city, departure time, train type, class, and price.

Display search results with all available information from the CSV, including calculated trip duration.

Sort results by key criteria such as trip duration and price for easy comparison.

Run the code from project folder in Vscode: javac -d out $(find src -name "*.java") java -cp out 
com.railwaysearch.app.Main