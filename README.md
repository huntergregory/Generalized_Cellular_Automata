cell society
====

This project implements a cellular automata simulator.

Names: Dhanush Madabusi, Hunter Gregory, Connor Ghazaleh

### Timeline

Start Date: 1/27/19

Finish Date: 2/12/19

Hours Spent: 40

### Primary Roles

Connor was primarily responsible for constructing the abstract Cell and Grid classes as well as the Cell subclasses and
multiple of the CA simulations, or Grid subclasses. Hunter was primarily responsible for constructing the XML schemas
and various test files for all the simulations. Additionally, he handled the implementation for the FileChooser and
the XML parser, and he designed some of the Grid simulation subclasses. Dhanush was primarily responsible for all the 
visualization for the project. He constructed all front end components and handled what was displayed at each step in
the simulations.

### Resources Used

* Stack Overflow
* Java documentation
* JavaFX tutorial
* Information on Cellular Automata simulations


### Running the Program

Main class: SimulatorMain

Data files needed: 
* Schema files to generate new config files
* Config files generated from schema files

Interesting data files:
* If you're familiar with the gospel glider 
gun in the game of life - and even if you're not - the gospel-glider-gun-imposter.xml
file is a cool deviation. 
* The toroidal-gospel-gun-glider-imposter.xml is even wilder...

Features implemented:
* Different cell shapes
* Toroidal edges
* Sliders to configure input parameters
* Ability to encode any possible permutation of neighbors
* Initial grid configuration can be set using:
    * states at specific coordinates
    * percentages of the grid that each state occupies
    * number of cells in the grid that each state occupies
* Additional Rock Paper Scissors simulation
* Error checking for incorrect config file data
* Toggling outlines of grids

Assumptions or Simplifications:
* Grid has same height and width

Known Bugs:
* no known bugs in current state of project

Extra credit:
* Fun colors

### Notes


### Impressions

