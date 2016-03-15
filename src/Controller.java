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
                                workers[i].setInput(inputArgument);
                                break;
                            }
                        }
                    }
                    else if (inputArgument.equals("p " + Integer.parseInt(arguments[1]) + " 32000")){
                        Controller.output.println("m " + Integer.parseInt(arguments[1]) + " 0");
                        System.out.println("Stopped worker " + Integer.parseInt(arguments[1]));
                        workers[Integer.parseInt(arguments[1])-1].busy = false;
                        continue;
                    }
                    else if (arguments[0].equals("f")){
                        workers[Integer.parseInt(arguments[1])-1].setInput(inputArgument);
                    }
                    else if (!workers[Integer.parseInt(arguments[1])-1].busy)
                        workers[Integer.parseInt(arguments[1])-1].setInput(inputArgument);
                }
            }
        }
