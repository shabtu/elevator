import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Created by Simon on 2016-03-14.
 */
public class Worker extends Thread{

    int elevatorID;
    double position = 0, destination = -1;
    String[] arguments;
    String[] temp;
    String[] tempF;
    String input = "";
    boolean busy = false,stop = false, up = true;


    LinkedList <String> listUp = new LinkedList<>();
    LinkedList <String> listDown = new LinkedList<>();


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

    public synchronized void setInput(String input) {
        this.input = input;
        temp = input.split(" ");
        int counter = 0;

        if(temp[2].equals("32000")) {
            stop = true;
            listUp.clear();
            listDown.clear();
            busy = false;
        }
        else if (temp[0].equals("b")) {
            double wantedDestination = Double.parseDouble(temp[1]);

            if (!busy) {
                String floorStop = "p " +  + elevatorID + " " + temp[1];
                if ((wantedDestination - position) > 0) {
                    listUp.addFirst(floorStop);
                    up = true;
                }
                else {
                    listDown.addLast(floorStop);
                    up = false;
                }
            }
            else if (up) {
                if(destination < wantedDestination) {
                    counter = 0;
                    for(String n : listUp) {
                        if (Double.parseDouble(n.split(" ")[2]) > wantedDestination) {

                            continue;
                        }
                        counter++;
                    }
                    String floorStop = "p " + elevatorID + " " + temp[1];
                    listUp.add(counter, floorStop);
                }
                else {
                    String floorStop = "p " +  + elevatorID + " " + destination;
                    listUp.addFirst(floorStop);
                    destination = wantedDestination;
                }
            }
            else {
                if(destination > wantedDestination) {
                    counter = 0;
                    for(String n : listDown) {
                        if (Double.parseDouble(n.split(" ")[2]) > wantedDestination) {

                            continue;
                        }
                        counter++;
                    }
                    String floorStop = "p " + elevatorID + " " + temp[1];
                    listDown.add(counter, floorStop);
                }
                else {
                    String floorStop = "p " +  + elevatorID + " " + destination;
                    listUp.addLast(floorStop);
                    destination = wantedDestination;
                }
            }
        }
        else if(!temp[0].equals("f")) {
            double wantedDestination = Double.parseDouble(temp[2]);
            if (destination == wantedDestination);
            else if(destination < wantedDestination) {

                if (up){
                    counter = 0;
                    for(String n : listUp) {
                        if (Double.parseDouble(n.split(" ")[2]) > wantedDestination) {

                            continue;
                        }
                        counter++;
                    }
                    listUp.add(counter, input);
                }
                else if (!up){
                    counter = 0;
                    if (position < wantedDestination){
                        for(String n : listUp) {
                            if (Double.parseDouble(n.split(" ")[2]) > wantedDestination) {

                                continue;
                            }
                            counter++;
                        }

                        listUp.add(counter, input);

                    }
                    else {
                        String floorStop = ("p " + elevatorID + " " + destination);
                        listDown.addLast(floorStop);
                        destination = wantedDestination;
                    }
                }
            }
            else if (destination > wantedDestination){
                if (up){
                    String floorStop = ("p " + elevatorID + " " + destination);
                    if (position < wantedDestination){
                        listUp.addFirst(floorStop);

                        destination = wantedDestination;
                    }
                    else {
                        counter = 0;
                        for(String n : listDown) {
                            if (Double.parseDouble(n.split(" ")[2]) > wantedDestination) {
                                continue;
                            }
                            counter++;
                        }

                        listDown.add(counter, input);
                    }

                }
                else if (!up){
                    counter = 0;
                    for(String n : listDown) {
                        if (Double.parseDouble(n.split(" ")[2]) > wantedDestination) {

                            continue;
                        }
                        counter++;
                    }

                    listDown.add(counter, input);
                }
            }
        }

        else tempF = input.split(" ");

        notify();
    }

    public synchronized void handleInput() throws InterruptedException {

        while (true) {



            if(!busy || stop) {
                stop = false;
                if(listUp.isEmpty() && listDown.isEmpty()) {
                    wait();
                    while (temp[0].equals("f")) wait();
                }

                busy = true;
                if (up) {
                    if (!listUp.isEmpty()) {
                        arguments = listUp.removeFirst().split(" ");
                    }
                    else if (!listDown.isEmpty()) {
                        up = false;
                        arguments = listDown.removeLast().split(" ");
                    }
                }
                else if (!up){
                    if (!listDown.isEmpty()) {
                        arguments = listDown.removeLast().split(" ");
                    }
                    else if (!listUp.isEmpty()){
                        up = true;
                        arguments = listUp.removeFirst().split(" ");
                    }
                }

            }
            else {
                wait();
                arguments = tempF;
            }
            busy = true;

            System.out.println("Worker " + elevatorID + " got message: " + arguments[0] + " " + arguments[1] + " " + arguments[2]);


            if (arguments[0].equals("p")){

                Controller.output.println("d " + elevatorID + " -1");

                destination = Double.parseDouble(arguments[2]);


                if (destination < position)
                    Controller.output.println("m " + elevatorID + " -1");

                if (destination == position) {
                    busy = false;
                    continue;
                }

                if (destination > position)
                    Controller.output.println("m " + elevatorID + " 1");



            }
            if(arguments[0].equals("f")) {

                position = Double.parseDouble(arguments[2]);
                Controller.output.println("s "+elevatorID+ " " + Math.round(position));


                if ((destination-0.05) < position && position < (destination+0.05)) {
                    Controller.output.println("m " + elevatorID + " 0");
                    Controller.output.println("d " + elevatorID + " 1");

                    sleep(1000);
                    busy = false;
                }
            }
            if (arguments[0].equals("b")){
                Controller.output.println("d " + elevatorID + " -1");

                destination = Double.parseDouble(arguments[2]);

                System.out.println("N�gon vill �ka fr�n: " + destination);
                if (destination < position)
                    Controller.output.println("m " + elevatorID + " -1");

                if (destination == position){
                    Controller.output.println("d " + elevatorID + " 1");
                    busy = false;
                    continue;
                }

                if (destination > position)
                    Controller.output.println("m " + elevatorID + " 1");

            }


        }
    }
}
