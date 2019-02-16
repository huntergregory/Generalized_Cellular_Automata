Design
==


### High level design goals

The goal of this project was to design a simulator that provided the foundation for running various types of simulations that all contained different rule sets. 
A goal when writing the code was to make it as flexible as possible so it would be easy to implement features later. 
With that in mind, we used polymorphism in 2 important places to allow for flexibility. 
The first was in creating cells for each simulation. 
This was done so that we could customize properties of the cell without having to make significant changes to the code base for each additional type of cell. 
This resulted in one super class called cell that 3 subclasses inherited from. 
Each of the subclasses was created to accommodate having different shapes of cells and being able to correctly lay them out on the screen to form our grid. 
A second place this was done was with our grid class. 
This allowed us to inherit from Grid (our superclass) to create various different types of simulations that shared a common code base but that only needed different rule sets and a few unique properties to be defined. 
These simulations all had certain things in common such as needing to keep track of a grid of cells, 
needing the ability to return all neighbors of a certain cell (or only a few particular ones depending on the configuration), 
and the ability to configure the simulation (at initialization or during the simulation) based on certain input parameters. 
All of this is managed by the Grid super class, meanwhile anything specific to individual simulations, 
such as rules for how to change the states of the cells or what colors to assign to particular states in the simulation, 
is managed in the subclasses that are created for each individual simulation.

### How to add new features

In general we would expect new features to fall into two categories. 
Either they could be major additions such as new simulations or new cell types that require entire new classes, or 
they could be concerned with details such as adding a toggle to change a cell in the visual component by clicking on it, 
or adding a new parameter to a simulation. 
Major changes that require new classes should be implemented in the GridCell package and should inherit from the super classes 
that were designed to work, in essence, as a template. 
The two super classes to inherit from are Cell and Grid. 
Any changes specific to an already existing cell should be implemented in the subclass for that cell type, 
as you would not want to interfere with the other cells by making that change in the super class. 
The same logic applies to making new simulations or adding features to already existing ones 
(modify the subclasses - not the Grid class - for already existing simulations). 
Any additional features added to the visual component of the simulations should be added in SimulatorMain which handles
actually running the simulation and displaying it on the screen. 
Complimentary methods may need to be added in simulation subclasses to track relevant data. 
Any additional features related to configuration should be added to XMLParser and XML schema files.
In addition, when a new simulation class is created to extend the Grid class,
one will have to create a new xml schema for that class. The schema are organized in a hierarchical pattern similar to the Grid hierarchy.
Each simulation's schema follows a general cellular automaton schema, but must override certain things such as state names and 
parameters that are unique to each sim. All other features of a sim are captured in the general schema.
After creating the new Grid subclass and xml schema, one must create a new CA_TYPE enum that contains both 
the filepath of the schema and the Grid subclass' class as variables. These variables allow the Main class to instantiate 
a new Grid without having to know its exact subclass type because the enumeration will hand the right constructor to the main 
via reflection.