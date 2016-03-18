/**
 * Created by Simon on 2016-03-11.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Controller {
    public static Socket socket;
    public static PrintWriter output;
    public static void main(String[] args) throws IOException {


        BufferedReader input;
        String[] arguments;
        boolean done = false;
        int fallSafeCounter = 0;

        socket = new Socket("localhost", 4711);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);


                /*Create workers to handle elevators*/
        Worker[] workers = new Worker[3];


        for (int i=0; i < workers.length ; i++){
            workers[i] = new Worker(i+1);
            workers[i].start();
        }

        String inputArgument;

		/*Reading input from elevator buttons*/
        while ((inputArgument = input.readLine()) != null) {
            if ("".equals(input)) {
                continue;
            }
            arguments = inputArgument.split(" ");

            if (arguments[0].equals("v"))
                continue;
            else if (arguments[0].equals("b")){
                for (int i=0; i < workers.length; i++){
                    if (!workers[i].busy){
                        System.out.println(inputArgument);
                        workers[i].setInput(inputArgument);
                        done = true;
                        break;
                    }
                }
                if (!done){
                    for (int i=0; i < workers.length; i++ ){
                        if(workers[i].up == (Integer.parseInt(arguments[2]) == 1)){
                            if(workers[i].up){
                                if (workers[i].position < Double.parseDouble(arguments[1])){
                                    workers[i].setInput(inputArgument);
                                    done = true;
                                    break;
                                }
                            }
                            else {
                                if (workers[i].position > Double.parseDouble(arguments[1])){
                                    workers[i].setInput(inputArgument);
                                    done = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!done) {
                    int counter = 0;
                    String x = "b " + fallSafeCounter + " " + arguments[1];
                    if (workers[fallSafeCounter].up) {
                        for (String n : workers[fallSafeCounter].listDown) {
                            if (Double.parseDouble(n.split(" ")[2]) > Double.parseDouble(arguments[1])) {
                                continue;
                            }
                            counter++;
                        }
                        workers[fallSafeCounter].listDown.add(counter, x);
                    }
                    else {
                        for (String n : workers[fallSafeCounter].listUp) {
                            if (Double.parseDouble(n.split(" ")[2]) > Double.parseDouble(arguments[1])) {
                                continue;
                            }
                            counter++;
                        }
                        workers[fallSafeCounter].listUp.add(counter, x);
                    }

                    fallSafeCounter++;
                    fallSafeCounter = fallSafeCounter % 3;
                }
                done = false;
            }
            else if (inputArgument.equals("p " + Integer.parseInt(arguments[1]) + " 32000")){
                workers[Integer.parseInt(arguments[1])-1].setInput(inputArgument);
                Controller.output.println("m " + Integer.parseInt(arguments[1]) + " 0");
                System.out.println("Stopped worker " + Integer.parseInt(arguments[1]));
                workers[Integer.parseInt(arguments[1])-1].busy = false;
                continue;
            }
            else if (arguments[0].equals("f")){
                workers[Integer.parseInt(arguments[1])-1].setInput(inputArgument);
            }
            else workers[Integer.parseInt(arguments[1])-1].setInput(inputArgument);
        }
    }
}
