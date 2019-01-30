# Lab 1/24/19

## Participants

* Dhanush Madabusi
* Hunter Gregory
* Connor Ghazaleh

## RPS

Our design for the RPS implementation would include 2 classes, one for keeping track of game states and a second one for making decisions about player inputs and making a decision for who wins the round. In the decision class we would implement a hashmap to track destructors and their relationships with other destructors and we would have a method called didDestroy() that would take two destructors as inputs and output a boolean stating whether the first destroyed the second. Our main class would keep track of other aspects of the game like score and round number.

## Cell Society

###Intro

This task requires the group to 

###Overview

We would like to implement 4 types of classes: A class which would be our main class and would track game states and handle the visualization portion of the simulation, an abstract class to define shared properties of grids, subclasses for different types of grids used for different types of simulations, and a slider class that will let the user specify some inputs to the simulation. We plan to create unique subclasses of the abstract grid class for each of the 4 example simulations. 

Our abstract grid class will define any important properties of the grid such as: number of types of states, available states, grid size, etc… It will be used to instantiate subclasses that contain unique methods to implement the function responsible for changing the grid state. Each of the grid subclasses will contain an array of instantiations of the slider class that modify different properties of the simulation. Finally, our sim class will be responsible for using this grid to create a visual representation of the its state.

