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


                socket = new Socket("localhost", 4711);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);


                /*Create workers to handle elevators*/
                Worker[] workers = new Worker[2];


                for (int i=0; i < 2 ; i++){
                    workers[i] = new Worker(i+1);
                    workers[i].start();
                }

                String inputArgument;

		/*Reading input from elevator buttons*/
                while ((inputArgument = input.readLine()) != null) {
                    if ("".equals(input)) {
                        continue;
                    }
                    System.out.println("Fick argument: " + inputArgument + "\nSkickar till: " + ((int) inputArgument.charAt(2) - 48));

                    if (inputArgument.charAt(0) == 'v')
                        continue;

                    workers[((int) inputArgument.charAt(2)-49)].setInput(inputArgument);
                }
            }
        }
