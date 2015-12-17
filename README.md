# Scouting

This is an app I wrote for scouting in the FIRST Robotics Competition (FRC) in
the fall of 2013 to the winter of 2014 (when main development ended as the
season started up and the app began to be used).

In the FRC competitions, there is a need for data gathering. On the first day
of the three-day competition, the teams are given time to practice and prepare,
also allowing students to go around and ask for information and statistics
regarding each others' robots. In this application this is called "Pit Scouting"
after the "pits" that the robots are worked on in.

The second form of information that students can collect using this application
is information on qualification matches, which typically occur on the second day
of competition.

The general workflow for a team of scouting students who are looking to use this
application is to run the server (after editing config/general.json and
config/questions.json to reflect your situation/year) on a machine which has
Python and the necessary libraries installed on it, allowing it to download
information about the regional. Then they should connect all devices running
the application to the same wifi (configuring them in ...->Settings with the IP
address of the server) (there should be 6 clients). The teams will be evenly
distributed among the scouters, accounting for conflicts. Scouters should then
hit the 'sync' button.
