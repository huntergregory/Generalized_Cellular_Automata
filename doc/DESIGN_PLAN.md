### Introduction
The goal of this assignment is to design a simulator for cellular automata that is flexible enough to model different types of cellular automata. To do so we will design our code around a particular interchangeable class or object that represents one particular simulation. At a high-level, the structure will include 3 main parts. The first part will be responsible for input, the second will be responsible for maintaining the grid, and the final part will be focused around the visual representation of the simulation.

### Overview
We would like to implement 4 types of classes. The SimulationMain class will parse the XML file to determine what simulation will be initially presented as well as the initial parameters. This class will also run the appropriate methods to present the visualization and calls for cells to be updated. The abstract GridCell.Grid class will define shared properties of grids for the various simulations as well as setup up methods that need to be implemented by subclasses. Our abstract grid class will define any important shared properties of the grid for various simulations such as: number of types of states, available states, grid size, etc. It will be used to instantiate subclasses that contain unique methods to implement the function responsible for changing the grid state. We will have a subclass that extends the GridCell.Grid class for each of the 5 unique simulations. These subclasses will define its specific states, how the cells are updated, as well create the necessary sliders for the simulation visualization. These GridCell.Grid subclasses also contain 2D arrays formed by Cells from a GridCell.Cell class, which maintains info about the GridCell.Cell’s location and state (or color). Finally, our sim class will be responsible for using this grid to create a visual representation of the its state.

*UML Class Diagram*
![Diagram](UML%20class%20diagram%20pic.png "UML Class diagram")

### User Interface
The user interface will allow the user to customize certain settings like grid size, speed of the simulation, and composition of the grid. Additionally there will be buttons that allow the user to switch between certain simulations, to reset the simulation, and to start the simulation. We anticipate that the interface will use a slider mechanism to customize settings and allow users to select values in a certain range. To control the flow of the simulation we will use either clickable buttons or keyboard input. We will structure the UI so that the window contains the grid at the top of the window, and below that will be the sliders and buttons (or keyboard instructions). The grid will be designed as a large square made up of many little squares that will change color based on their current states.


### Design Details
##### Classes:
- SimulationMain:
    - Scan XML doc
    - Initialize scene
    - Initialize grid based on XML input or button input
    - Monitor input from sliders and buttons
    - Run simulation

- GridCell.Grid (abstract class):
    - Instance variables
        - 2d array of Cells
        - State/Color Map
    - Abstract updateCells() method
    - Constructor that follows XML input and initializes instance variables
    - return 2d array of Cells to main to create visual representation

- GridCell.Grid subclasses:
    - Game of life
        - Assign Color map in constructor
        - Update states of cells based on rule set
    - Predator-prey
        - Assign Color map in constructor
        - Update states of cells based on rule set
    - GridCell.Segregation
        - Assign Color map in constructor
        - Update states of cells based on rule set
    - Percolation
        - Assign Color map in constructor
        - Update states of cells based on rule set
    - Fire
        - Assign Color map in constructor
        - Update states of cells based on rule set
- GridCell.Cell:
    - Instance variables 
        - Rectangle
    - Getter and setter methods for attributes of Rectangle

##### Visualization:
 - All visualization will be handled by methods in the SimulationMain class that will construct the correct GridCell.Grid subclass object and update the state of the cells based on an Animation Timer

##### Configuration Files:
- Five XML configuration files for the five GridCell.Grid subclasses (simulations)
- Each XML files contains:
    - Type of simulation, title, and author
    - Width and height of grid
    - Initial states of parameters that relate to that simulation
        - For example, segregation simulation XML file would contain initial parameters for “Similar %,” “Red/Blue” ratio, and “Empty %”


### Design Considerations
We chose to divide our code into 4 main classes to compartmentalize and maintain privacy wherever possible. The GridCell.Cell class is a good example of this as it will have no access to other variables concerning game state and will purely exist to manage the color of a rectangle on the screen (which corresponds to its state) and to track the location in the 2D array to which it maps. This kind of encapsulation allows us to more easily manage dependencies in the code and will allow us to debug errors faster.


### Team Responsibilities
Stage 1 (Foundations)
- Hunter - all things XML
- Connor - GridCell.Grid & cell
- Dhanush - Main

Stage 2 (Testing)
- Connor - create a simple, concrete GridCell.Grid subclass
- Hunter - create the corresponding XML file
- Everybody - test SimulationMain with these, fix any bugs

Stage 3 (Creating Cellular Automata)
- Everybody - create concrete GridCell.Grid subclasses
    - Fire
    - Predator-Prey
    - Percolation
    - Game of Life
    - GridCell.Segregation
