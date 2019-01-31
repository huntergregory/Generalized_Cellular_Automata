# Lab 1/31/19

## Participants

* Connor Ghazaleh
* Eric Werbel

## Part 1
1. My team's design encapsulates cell information (things like color, size, shape of the rectangle) contained within each cell. It also encapsulates grid manipulation from the rest of the code base by creating a class to house a method to update states of cells, and create the initial grid based on input parameters from the XML.

2. We plan to use inheritance to instantiate different types of simulations. The only difference in each type of simulation in our code will be the implementation of the method that updates the states of the cells in the grid, so that is the only method that will be defined as abstract in the class.

3. Methods not being abstracted are closed from subclasses so that subclasses are only responsible for the abstracted methods.

4. Errors about grid sizes, cell location, cell size, ...

5. Design keeps things compartmentalized and is spread out logically.

## Part 2
1. Links to the main simulation class and has internal public methods called from there.

2. Based on behavior of simulation class and progression of the simulation

3. Minimize dependencies by encapsulating

## Part 3
1. 5 different simulations

2. 