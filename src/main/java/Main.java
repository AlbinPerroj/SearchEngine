import core.Command;
import core.SearchEngine;
import help.Status;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static void main(String[] args)
    {
        try
        {

            Scanner in = new Scanner(System.in);

            SearchEngine se = new SearchEngine(System.getProperty("user.dir") + "\\IndexMemory");
            se.createIndexDirectory();

            HashMap<String, String> queryReplacements = new HashMap<>();
            queryReplacements.put("query","");
            queryReplacements.put("|", " OR ");
            queryReplacements.put("&"," AND ");

            String commandLine="";

            System.out.println("Program started, type command.");

            while( ! (commandLine = in.nextLine()).equals("exit"))
            {
                try {
                    var commandLineLowerCase = commandLine.toLowerCase();

                    Command command = new Command(commandLineLowerCase, queryReplacements);
                    Status status = command.isValid();

                    if(status.getIsSuccess())
                    {
                        if (command.getType().equals("index"))
                        {
                            var indexResult = se.addDocToIndex(command.getDocIdOrQuery(), command.getTokens());
                            if(indexResult.getIsSuccess())
                                System.out.println(indexResult.getMessage());
                        }
                        else if(command.getType().equals("query"))
                        {
                            var queryResult = se.search(command.getDocIdOrQuery());
                            if(queryResult.getIsSuccess())
                                System.out.println(queryResult.getMessage());
                        }
                    }
                    else
                    {
                        System.out.println(status.getMessage());
                    }
                }
                catch (Exception e)
                {
                    System.out.println("error");
                }
            }

            System.out.println("Program exit");
        }
        catch (Exception e)
        {
            System.out.println("Exception. " + e.getMessage());
        }
    }
}
