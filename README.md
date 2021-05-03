# BigID - Backend Dev Task v1.5

## how to run 


Use IntelliJ IDEA to load and compile the project.<BR>
Simulation is main class so execute.

Current file used is hard coded to <br> 
`src/main/resources/big.txt` <br> 
This file was downloaded from   http://norvig.com/big.txt <br>
50 most common English names are hard coded at Constants class as comma separated string.<br>


## How does it work

The simulator reads the lines from the file one line at a time<br>
- It tracks character offsets and line offsets<br>
- every 1000 lines it create a matcher thread that receives <br>
  &nbsp;  The 1000 lines as one large string <br>
  &nbsp;  line offset and character offset<br>
  &nbsp;  Atomic counter to count completed matcher tasks<br>
  &nbsp;  BlockingQueue to report findings <br>
 - After reading all the lines will start  the printer aggregator thread.
 - start all matchers threads.


The matcher 

- transforms large line to lines
- searches for the names in each line using java Pattern and Match (generating regular expression from the names)
- for each match found store in FrequencyReport
- after complete send via BlockingQueue the FrequencyReport
- increase atomic counter.

The printer aggregator

- reads frequency Reports from BlockingQueue .It aggregates all reports to one big frequency map.
- it stops listening to BlockingQueue after all matchers have completed (Atomic counter equals amount of matchers)  and BlockingQueue is empty.
- upon exiting from the thread it will print the aggregated information 


Output file from execution with big.txt can be found at 









