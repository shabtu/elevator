import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.DoubleSummaryStatistics;

/**
 * Created by Simon on 2016-03-14.
 */
public class Worker extends Thread{

    int elevatorID;
    double position = 0, destination = 0, floor = 0;
    String[] arguments;
    String input = "";
    boolean busy = false;

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
        arguments = input.split(" ");
        notify();
    }

    public synchronized void handleInput() throws InterruptedException {

        while (true) {
            wait();
            System.out.println("Worker " + elevatorID + " got message: " + input);
            busy = true;

            if (arguments[0].equals("p")){

                Controller.output.println("d " + elevatorID + " -1");

                destination = Double.parseDouble(arguments[2]);
                //Controller.output.println("s " + elevatorID + " " + (int) destination);

                if (destination < position)
                    Controller.output.println("m " + elevatorID + " -1");

                if (destination == position) {
                    busy = false;
                    continue;
                }

                if (destination > position)
                    Controller.output.println("m " + elevatorID + " 1");

                //position = destination;

            }
            if(arguments[0].equals("f")) {
                //double decimal = (Double.parseDouble(arguments[2])) / 10;
                position = Double.parseDouble(arguments[2]);
                System.out.println("Intervall: " + (destination-0.005) + " < X < " + (destination+0.005));
                if ((destination-0.05) < position && position < (destination+0.05)) {
                    Controller.output.println("m " + elevatorID + " 0");
                    Controller.output.println("d " + elevatorID + " 1");
                    busy = false;
                }
            }
            if (arguments[0].equals("b")){
                Controller.output.println("d " + elevatorID + " -1");

                destination = Double.parseDouble(arguments[1]);

                System.out.println("Någon vill åka från: " + destination);

                if (destination < position)
                    Controller.output.println("m " + elevatorID + " -1");

                if (destination == position){
                    busy = false;
                    continue;
                }

                if (destination > position)
                    Controller.output.println("m " + elevatorID + " 1");

            }
        }
    }
}
