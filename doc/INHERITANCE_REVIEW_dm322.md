### Inheritance Review

I was unable to attend class due to illness, but I spent some time on my own thinking about the lab questions. 

#Part 1

Our CellSociety project has a Cell class which we decided would only contain information about its Javafx Rectangle
properties and also its color, which would be the state of the cell. Each cell in the Grid for the simulation needs
no more information, as the Grid subclasses handle all state updates and logic for updating th cell.

We have a Grid abstract class and five (or more) Grid subclasses. These Grid subclasses define the specific states and
state colors for their respective simulations. Each simulation will implement the abstract updateCells() method and
define the logic involved for updating cell states for each round.

I believe our project is well-designed in that each class only handles the information it needs and all methods and
operations are separated in easily understandable way. Our goal is to make the project well suited for additional
classes and that the design follows the open/closed principle.

#Part 2

My part deals with the SimulationMain class which sets up the stage and initial scene and parameters based on the XML
file. I will also be responsible for the visualization, which presents the Grid and respective sliders. My classes and
work have little to no dependencies and are unique blocks of code that are merely used to set up the and display the
simulations.

#Part 3

I am most excited to working on how to updateCells for each simulation as I think that will be a challenging task to
accomplish. As well I am excited to create a well-designed, debug-friendly, functioning application.

