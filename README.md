# rigidbodies
A simple 2D rigidbody physics sim

The project is a "failure" because it was technically never finished but in reality the goal was to experiment with physics simulations and collisoon algorithms as learning process.

Collisions are detected using a recursive search over the bounding boxes to find points of intersection. Then K-means cluster analysis is used to approximate points to apply forces.

The final step of calculating and applying forces is not implemented because I ran out of time.
