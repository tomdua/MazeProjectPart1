package Server;

//import java.io.InputStream;
//import java.io.OutputStream;

import algorithms.mazeGenerators.Maze;
import algorithms.search.*;

import java.io.*;
import java.util.Properties;

public class ServerStrategySolveSearchProblem implements IServerStrategy {
    @Override
    public void serverStrategy(InputStream inputStream, OutputStream outputStream) {
        try {
            Properties properties = new Properties();
            InputStream input;
            File configFile = new File("resources/config.properties");
            ObjectInputStream fromClient = new ObjectInputStream(inputStream);
            ObjectOutputStream toClient = new ObjectOutputStream(outputStream);
            toClient.flush();
            Solution sol;
            // read maze from the client, and create a temp dir
            Maze mazeToClient = (Maze) fromClient.readObject();
            String dir = System.getProperty("java.io.tmpdir");
            String mazeName = mazeToClient.toString();
            byte tempByteArray[] = mazeToClient.toByteArray();
            //String firstBinaryLine = "";
            // take the i`s indexes  of the maze and had them to the name
            //for (int i = 8; i < 100; i++)
              //  if (i >= tempByteArray.length)
                //    break;
                //else firstBinaryLine += tempByteArray[i];
            //mazeName = mazeName + "-" + firstBinaryLine;
            //create file-maze name, and if exist take the solve else solve
            File mazeFileCreate = new File(dir, mazeName);
            if (mazeFileCreate.exists()) {
                FileInputStream fileInput = new FileInputStream(mazeFileCreate);
                ObjectInputStream FileToReturn = new ObjectInputStream(fileInput);
                sol = (Solution) FileToReturn.readObject();
                FileToReturn.close();
            } else {
                String algSearch;
                SearchableMaze searchableMaze = new SearchableMaze(mazeToClient); // create a new searchable maze
                ASearchingAlgorithm algorithmSolve;
                if (configFile.length() == 0)  //if properties file empty, and has not been run yet
                    Server.Configurations.config();

                input = new FileInputStream("resources/config.properties");
                // load a properties file
                properties.load(input);
                algSearch = properties.getProperty("MazeAlgorithmSearch"); //get algorithm type from config file
                //get from user the type of maze from prop file
                if (algSearch.equals("DepthFirstSearch"))
                    algorithmSolve = new DepthFirstSearch();
                else if (algSearch.equals("BestFirstSearch"))
                    algorithmSolve = new BestFirstSearch();
                else if (algSearch.equals("BreadthFirstSearch"))
                    algorithmSolve = new BreadthFirstSearch();
                else
                    algorithmSolve = new BreadthFirstSearch();
                sol = algorithmSolve.solve(searchableMaze);
                //Create "maze" file in the folder.
                FileOutputStream fileOut = new FileOutputStream(mazeFileCreate);
                ObjectOutputStream objectReturn = new ObjectOutputStream(fileOut);

                objectReturn.writeObject(sol);
                objectReturn.flush();

            }
            toClient.writeObject(sol);
            toClient.close();

        } catch (IOException |
                ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
