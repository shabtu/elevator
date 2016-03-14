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
    int position = 0, destination = 0;
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

                destination = (int) input.charAt(4) - 48;
                Controller.output.println("s " + elevatorID + " " + destination);

                if (destination < position)
                    Controller.output.println("m " + elevatorID + " -1");

                if (destination == position)
                    continue;

                if (destination > position)
                    Controller.output.println("m " + elevatorID + " 1");

                position = destination;
            }
            else
                continue;

            Controller.output.println(input);
            this.input = "";
        }
    }
}
