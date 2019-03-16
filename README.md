# SingleLaneBridge
This is the SingleLaneBridge problem created with java code. It was made with four different kind of bridges.

Problem : A single-lane bridge connects the two Vermont villages of Red cars and Blue cars. Farmers in the two villages use this bridge to deliver their produce to the neighboring town. The bridge can become deadlocked if both a red car and a blue car get on the bridge at the same time (Blue cars are stubborn and are unable to back up.) Using semaphores and synchronization, design an algorithm that prevents deadlock.

My solution have four different kind of bridges.

1. BridgeUnsafe: It lets every car to pass the bridge at any time without any control
 A lot of cars pass at the same time which is unsafe.
 
2. BridgeSafe. It lets only one car to pass the bridge each time which make this bridge safe.
 However it does not care about priorities and fair.

3. BridgeSafeStrict. It lets only one car to pass the bridge each time which make this bridge safe.
It also change the turn of every car (like a traffic light) which make it fair. It changes the turn by letting pass the number of cars
which users has set in the beginning.

4. BridgeSafeCustomizable. It lets only one car to pass the bridge at a time which make this bridge safe.
It also change the turn of every car (like a traffic light) which make it fair. It changes the turn by letting pass the number of cars
which users has set in the beginning. However it can change the turn when the cars in one side is more than ten (this number can be changed) of the other side. That makes this bridge Customizable.
