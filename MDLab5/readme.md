# Forest Fire
_Forest Fire_ is a JavaFX project made over a concept of [2D CA](../MDLab4).

## Model of the simulation

### The basics

(_You're in the great Amazon rainforest.
You don't know how did you end up here, but luckily you have a phone in your hand,
and it just happens to have a real-time satellite view of the nearby location._) ¯\_(ツ)_/¯

Model consists of grid of square cells, each defining its own neighbourhood (Moore's in this example).
On the grid you can find sequentially generated river and randomly placed rocks.

### Starting the simulation

(_Oh no! There is a fire spreading all over the trees! Quick, run across the river!_)

When the simulation starts, one of cells is picked to be lit up.
Each burning cell counts to probabilities of neighbouring cells setting up on fire.
Initially, each burning cell counts as `1 * burningChance`.

### Putting the fire out
(_Wait, can you hear that? It's firefighters dropping water from planes! It seems like the fire is gone._)

There are 3 mechanisms of putting out the fire. The first one is a _water drop_.
The idea is simple, the grid is scanned top-down for first burning cell.
From this point, water is dropped and after a while it changes into wet grass.
Wet grass has lower chance of probability, which stops the fire ~~most of the time~~.

### Taming the wind
(_But did you notice the wind blowing so fast? In fact, too fast, the fire started again!_)

Second mechanism is wind. Okay, it's not really designed to put out the fire, but it changes burning chance!
Function (`calculateWindWeights` in [Board](src/main/java/com/MDLab5/Board.java)) defining wind is designed in a way it gives the highest weight to
the closest neighbour in direction from which the wind blows.
For Moore's neighbourhood it's not a big deal, but if we expand neighbourhood radius,
it will make a big change.

### Asking for help
(_There is nowhere left to go. All you can do is sit and wait for the end.
You feel the ground shaking as the fire comes closer and closer.
Hey, fire wouldn't make the ground shake so much!
Out of all the apocalyptic noises you start to hear a... melody?
You notice the nearby stones begin to grow out of the soil!
How is this possible? The fire has finally come to an end. You're safe._)

People say, native people living in this part of the forest have possessed the ability to bend fire.
They learnt how to perform the ancient ritual, which magically makes rocks grow atop burning wood.
No one knows how this works.

## So, how does this work?
a short example of what can be done in the program.