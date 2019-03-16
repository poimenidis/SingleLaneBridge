import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SingleLaneBridge {
	
	static Bridge bridge;
	static int numberOfCars = 0;
	
	public static void main(String[] args) 
    {
		int bridgeCategory = 0;
		
		//I ask user to enter which bridge category wants to start
		while (bridgeCategory < 1 || bridgeCategory > 4) {
			
	        System.out.print("Choose a category between 1 and 4:");
	        bridgeCategory = new Scanner(System.in).nextInt();
	        
	    }
		
		//I ask user to enter the number of cars on each side (the cars can be between 1 and 10. They can be more if I want)
		while (numberOfCars < 1 || numberOfCars > 10) {
			
	        System.out.print("Enter the number of cars between 1 and 10:");
	        numberOfCars = new Scanner(System.in).nextInt();
	        
	    }
		
		//I start the bridge which user asks
		switch (bridgeCategory) {
        case 1:  bridge = new BridgeUnsafe();
                 break;
        case 2:  bridge = new BridgeSafe();
                 break;
        case 3:  bridge = new BridgeSafeStrict(numberOfCars);
                 break;
        case 4:  bridge = new BridgeSafeCustomizable(numberOfCars);
                 break;
		}
		
        /*I create 2 threads which produce cars. The first thread "thRed" is for the red side and the thread thBlue is for the blue side*/  
        Thread thRed = new Thread( new Runnable() {
        	
            @Override
            public void run() {
             
                while(true)
                {
                	/*First the thread produce as cars as user wanted. In the real world they can't reach at the same time so 
                	  every car has 2 millisecond different.I pass the bridge which user has set.
                	  I also set the name of the car (which is their color and their thread id) 
                	  and their type (Red or Blue). To produce a car I call th.start. This creates a thread based of the class Car which
                	  is runnable.*/
                	for(int i=0; i<numberOfCars; i++) {
	                	Car car = new Car(bridge);
	                	Thread th = new Thread(car);
	                	car.setName("Red Car : "+th.getId());
	                	car.setType("Red");
	                    th.start();
	                    
	                    try{
	                    	Thread.sleep(2000);
	              		}
	                    catch(InterruptedException e){
	                    	
	                    } 
                	}
                	
                	/*After the thread produce the cars, I ask it to sleep for some random milliseconds for the next cars*/
                    try{
                    	Thread.sleep(randomWithRange(50,80)*1000);
              		}
                    catch(InterruptedException e){
                    } 
            	}

            }
            
        });
         
        Thread thBlue = new Thread( new Runnable() {
             
            @Override
            public void run() {
                 
                while(true)
                {
	                /*First the thread produce as cars as user wanted. In the real world they can't reach at the same time so 
	              	  every car has 2 millisecond different. I set the name of the car (which is their color and their thread id) 
	              	  and their type (Red or Blue). To produce a car I call th.start. This creates a thread based of the class Car which
	              	  is runnable*/
                	for(int i=0; i<numberOfCars; i++) {
	                	Car car = new Car(bridge);
		        	    Thread th = new Thread(car);
	                    car.setName("                                                                               Blue Car : "+th.getId());
	                    car.setType("Blue");
	                    th.start();
	                    try{
	                  		Thread.sleep(2000);
	                    }
	                    catch(InterruptedException e){
	                    }
                	}

                	/*After the thread produce the cars, I ask it to sleep for some random milliseconds for the next cars*/
                  	try{
                  		Thread.sleep(randomWithRange(1,10)*1000);
                    }
                    catch(InterruptedException e){
                    }

                }

            }
            
        });
        
        /*I print my program environment and I start my two threads (thRed and thBlue)*/
        System.out.println("Left Side                                Bridge                                Right Side");
        thRed.start();
        thBlue.start();
        
      }
 
	/*A class which help me to create a range between two numbers for my random Milliseconds*/
    static int randomWithRange(int min, int max)
    {
       int range = (max - min) + 1;     
       return (int)(Math.random() * range) + min;
    }
    
}

/*This is the class "Car". Every car has a name, a type (blue or red) and a bridge which it wants to pass
 The class car is runnable. When the run() is called (by a thread.start()) the car bridge.crossBridge(this) is called which let
 the car reach the bridge.*/
class Car implements Runnable
{
    private String name;
    private String type;
    private Bridge bridge;
     
    public Car(Bridge bridge)
    {
        this.bridge = bridge;
    }
     
    public void run()
    {
        bridge.crossBridge(this);
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
 
}

/*The "father" of all bridges*/
class Bridge
{
    
    public Bridge()
    {
    }
    public void crossBridge(Car car)
    {
    }
}

/*This is the first bridge category. This bridge is called BridgeUnsafe. It lets every car to pass the bridge at any time without any control
 A lot of cars pass at the same time which is unsafe.*/
class BridgeUnsafe extends Bridge
{
     
    public BridgeUnsafe()
    {
    	
    }
    
    public void crossBridge(Car car)
    {
    	/*The same time that my cars arrive, they pass the bridge immediately*/
    	System.out.printf("%s Arrived at "+System.currentTimeMillis()+".\n",car.getName());
    	System.out.printf("%s Passing at "+System.currentTimeMillis()+".\n",car.getName());
    	
    	/*The time to pass the bridge is 5 seconds by default (it can be changed)*/
    	try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	System.out.printf("%s Passed at "+System.currentTimeMillis()+".\n",car.getName());
    }
}

/*This is the second Bridge category. It is called BridgeSafe. It lets only one car to pass the bridge each time which make this bridge safe.
  However it does not care about priorities and fair.*/
class BridgeSafe extends Bridge
{
	/*To achieve this I create a semaphore. This semaphore it could be only acquired one time(Semaphore(1)). So for my problem helps me
	 to let only one car to pass the bridge at a time. If semaphore.release() is not called nobody else can pass the bridge.*/
	private final Semaphore semaphore;
    
    public BridgeSafe()
    {
        semaphore = new Semaphore(1);
    }
    
    public void crossBridge(Car car)
    {
    	System.out.printf("%s Arrived at "+System.currentTimeMillis()+".\n",car.getName());
    	
    	try {
    		/*First to pass the bridge the car should call semaphore.acquire().If another car has already called this line and it has not
    		 yet released it, our car would wait.*/
    		semaphore.acquire();
    		System.out.printf("%s Passing at "+System.currentTimeMillis()+".\n",car.getName());
			TimeUnit.SECONDS.sleep(5);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	finally
        {
    		/*After the car has passed the bridge i call semaphore.release() which lets another car to pass the bridge.*/
    		semaphore.release();
    		System.out.printf("%s Passed at "+System.currentTimeMillis()+".\n",car.getName());
            
        }
    	
    }
}

/*This is the third Bridge category. It is called BridgeSafeStrict. It lets only one car to pass the bridge each time which make this bridge safe.
It also change the turn of every car (like a traffic light) which make it fair. It changes the turn by letting pass the number of cars
which users has set in the beginning.*/
class BridgeSafeStrict extends Bridge
{
	private final Semaphore semaphore;
	private static boolean redTurn = false;
	private static int passBlue=0;
	private static int passRed=0;
	private int numberOfCars;
    
	/*To achieve this I create a semaphore. This semaphore it could be only acquired one time(Semaphore(1)). So for my problem helps me
	 to let only one car to pass the bridge at a time. If semaphore.release() is not called nobody else can pass the bridge.
	 Also the integer numberOfCars helps the thread to understand when should change the turn. Is the number of cars
	 that user has set in the beginning*/
    public BridgeSafeStrict(int numberOfCars)
    {
        semaphore = new Semaphore(1);
        this.numberOfCars = numberOfCars;
    }
    public void crossBridge(Car car)
    {
    	System.out.printf("%s Arrived at "+System.currentTimeMillis()+".\n",car.getName());
    	
    	/*Here I synchronize all the values in my threads. This values are "redTurn","passRed","passBlue"*/
    	synchronized (this) {
    		/*If it is a blue car.*/
    		if(car.getType().equals("Blue")) {
    			/*While it is red turn wait and do not do anything*/
    			while (redTurn) {
			    	try {
						wait();
						} catch (InterruptedException e) {}
			    	}
    		}/*If it is a red car.*/
    		else {
    			/*While it is not red turn wait and do not do anything*/
    			while (!redTurn) {
			    	try {
						wait();
						} catch (InterruptedException e) {}
			    	}
    		}
    		try {
    			/*First to pass the bridge the car should call semaphore.acquire().If another car has already called this line and it has not
       		 	yet released it, our car would wait.*/
	    		semaphore.acquire();
	    		System.out.printf("%s Passing at "+System.currentTimeMillis()+".\n",car.getName());
				TimeUnit.SECONDS.sleep(5);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	finally
	        {
	    		/*After the car has passed the bridge i call semaphore.release() which lets another car to pass the bridge.*/
	            semaphore.release();
	            System.out.printf("%s Passed at "+System.currentTimeMillis()+".\n",car.getName());
	            
	            /*After that, we count the passing cars by their type*/
	            if(car.getType().equals("Blue")) {
	            	passBlue++;
	            }
	            else {
	            	passRed++;
	            }
	            
	            /*If it was reds turn and they have already passed as red cars as the numberOfCars, the thread sets the passRed equal to zero again
	             and the redTurn equal to false */
	            if(redTurn) {
	            	if(passRed==numberOfCars) {
	            		redTurn=false;
	            		passRed=0;
	            	}
	            }
           		else {
           			/*If it was blue turn and they have already passed as blue cars as the numberOfCars, 
    	           		the thread sets the passBlue equal to zero again
    	             	and the redTurn equal to true */
           			if(passBlue==numberOfCars) {
           				redTurn=true;
           				passBlue=0;
           			}
           		}
                
	            System.out.println("                                         "+redTurn);
	            /*This notify all my values in all my threads which may have changed*/
	          	notifyAll();
	        }
    	}
    
    	
    }
}
 
/*This is the third Bridge category. It is called BridgeSafeCustomizable. It lets only one car to pass the bridge at a time which make this bridge safe.
It also change the turn of every car (like a traffic light) which make it fair. It changes the turn by letting pass the number of cars
which users has set in the beginning. However it can change the turn when the cars in one side is more than ten (this number can be changed) 
of the other side. That makes this bridge Customizable*/
class BridgeSafeCustomizable extends Bridge
{
	private final Semaphore semaphore;
	private static boolean redTurn = false;
	private static int blueCars=0;
	private static int redCars=0;
	private static int passBlue=0;
	private static int passRed=0;
	private int numberOfCars;
    
	/*These thread do exactly what BridgeSafeStrict. The only different is that it counts the number of cars which wait on each side.
	 The thread compares the two numbers (blueCars and redCars) and if they differ for ten cars, the thread change the turn for 
	 the side which has more cars.*/
	
    public BridgeSafeCustomizable(int numberOfCars)
    {
        semaphore = new Semaphore(1);
        this.numberOfCars = numberOfCars;
    }
    
    public void crossBridge(Car car)
    {
    	
    	System.out.printf("%s Arrived at "+System.currentTimeMillis()+".\n",car.getName());
    	synchronized (this) {
    		
    		if(car.getType().equals("Blue")) {
    			
    			blueCars++;
    			notifyAll();
    			/*"If the turn is for redCars and the blue cars are not 10 more than the red cars wait"*/
    			while ((blueCars-redCars)<10&&redTurn) {
    				
			    	try {
						wait();
					} catch (InterruptedException e) {}
			    	
			    }
    			/*"If the turn was for redCars and the blue cars were 10 more than the red cars change the turn for blue cars"*/
    			if((blueCars-redCars)>=10&&redTurn) {
    				redTurn=false;
    				notifyAll();
    				System.out.println("                                         "+redTurn+"££");
    				
    			}
    		}
    		else {
    			redCars++;
    			notifyAll();
    			/*"If the turn is for blueCars and the red cars are not 10 more than the blue cars wait"*/
    			while ((redCars-blueCars)<10&&!redTurn) {
			    	try {
						wait();
					} catch (InterruptedException e) {}
			    }
    			/*"If the turn was for blueCars and the red cars were 10 more than the blue cars change the turn for blue cars"*/
    			if((redCars-blueCars)>=10&&!redTurn) {
    				
    				redTurn=true;
    				notifyAll();
    				System.out.println("                                         "+redTurn+"££");
    				
    			}
    			
    		}
    		
    		System.out.println(blueCars-redCars);
    		
    		try {
    			
    		semaphore.acquire();
    		System.out.printf("%s Passing at "+System.currentTimeMillis()+".\n",car.getName());
			TimeUnit.SECONDS.sleep(5);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	finally
	        {
	            semaphore.release();
	            System.out.printf("%s Passed at "+System.currentTimeMillis()+".\n",car.getName());
	            
	            if(car.getType().equals("Blue")) {
	            	blueCars--;
	            	passBlue++;
	            }
	            else {
	            	redCars--;
	            	passRed++;
	            }
	            
	            if(redTurn) {
	            	if(passRed==numberOfCars) {
	            		redTurn=false;
	            		passRed=0;
	            	}
	            }
           		else {
           			if(passBlue==numberOfCars) {
           				redTurn=true;
           				passBlue=0;
           			}
           		}
                
	            System.out.println("                                         "+redTurn);
	          	notifyAll();
	        }
    	}
    
    	
    }
}
