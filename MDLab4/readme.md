# 2D Cellular Automata
_(just like 1D, but in 2D)_

2D cellular automata is a JavaFX program for simulating cellular automata in two dimensions.
It is based off of [_Conway's 'Game of Life'_](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life).

### What is a cellular automata?
_(yes, I copied this section from 1D CA)_

Cellular automata is a discrete computation model.
It consists of grid of cells (automatons), finite number of states and evolution rule.
In the simplest model, automaton can take only one of two states
(1 and 0, black and white etc.) at a given instance of time.
It's state in next instance of time can be deduced with a _rule_.
Rule is defined for an automaton and is based on its neighbourhood.
For this implementation I used Moore's neighbourhood,
which means next state of an automaton is based on 8 closest neighbours.

More info about cellular automata can be found [here](https://en.wikipedia.org/wiki/Cellular_automaton).

### Program usage
User selects a preset, number of its copies and general density on the grid,
as well as a boundary condition to be used on each edge.
After loading the domain, user can choose animation speed, start simulation,
or select iteration "by hand".