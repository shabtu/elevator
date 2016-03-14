import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Simon on 2016-03-14.
 */
public class Worker extends Thread{

    int elevatorID;
    double position = 0, destination = 0, floor = 0;
    String input = "";

    Socket socket;
    PrintWriter output;

    public Worker(int elevatorID) throws IOException {
        this.elevatorID = elevatorID;
    }

    public void run() {
        try {
            handleInput();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setInput(String input){
        this.input = input;
        notify();
    }

    public synchronized void handleInput() throws InterruptedException {

        while (true) {
            wait();
            System.out.println("Worker " + elevatorID + " got message: " + input);

            if (input.charAt(0) == 'p'){

                destination = (double) input.charAt(4) - 48;
                Controller.output.println("s " + elevatorID + " " + destination);

                if (destination < position)
                    Controller.output.println("m " + elevatorID + " -1");

                if (destination == position)
                    continue;

                if (destination > position)
                    Controller.output.println("m " + elevatorID + " 1");

                position = destination;

            }
            if(input.charAt(0) == 'f'){
                double decimal = ((double) input.charAt(6)-48)/10;
                floor = (double)(input.charAt(4) - 48) + decimal;
                System.out.println("Floor: " + floor);
                System.out.println("Decimal: " + decimal);
                if (floor == destination){
                    Controller.output.println("m " + elevatorID + " 0");
                }
            }
            else
                continue;

            Controller.output.println(input);
            this.input = "";
        }
    }
}
