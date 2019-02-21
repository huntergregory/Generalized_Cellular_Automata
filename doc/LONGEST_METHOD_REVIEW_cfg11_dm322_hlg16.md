#Design

##Participants
Dhanush Madabusi (dm322)  
Hunter Gregory (hlg16)   
Connor Ghazaleh (cfg11)

###Methods 1 & 2
The longest two methods (1 and 2) have duplicated code that can be broken into a single method to simplify iterating over the input. That would drop the line count to 22.

###Method 3

Lines 217 through 224 were restructured as part of the masterpiece for cfg11 so that anything updating the state of a cell was refactored into its own method. This cut the length down to 22. The entire if statement in the bottom half of the code could also be refactored into its own method if need be.

