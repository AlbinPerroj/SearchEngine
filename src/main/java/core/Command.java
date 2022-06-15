package core;

import help.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Command {

    private final String commandLine;

    private String type;
    private String docIdOrQuery;
    private ArrayList<String> tokens;

    private final HashMap<String, String> queryReplacements;

    public Command(String commandLine, HashMap<String, String> queryReplacements)
    {
        this.commandLine = commandLine;
        this.queryReplacements = queryReplacements;
    }

    public Status isValid() throws NullPointerException, IndexOutOfBoundsException
    {
        if(this.commandLine.equals(null) || this.commandLine.isBlank())
            return new Status(false, "command error   null or blank");

        ArrayList<String> terms = new ArrayList<String>(Arrays.asList(this.commandLine.trim().split(" ")));

        terms.removeIf(t->t.isBlank());

        if( terms.size() < 2)
            return new Status(false, "command error   not enough terms");

        if(terms.get(0).equals("index"))
            return isIndexCommandValid(terms);
        else if (terms.get(0).equals("query"))
            return isQueryCommandValid();
        else
            return new Status(false, "command error   command is not known");

    }

    private Status isQueryCommandValid() throws NullPointerException, IndexOutOfBoundsException {

        this.type = "query";

        var query = manipulateQuery(this.commandLine, this.queryReplacements);

        this.docIdOrQuery = query;

        return new Status(true, "Command is valid. It is query command.");
    }

    private Status isIndexCommandValid(ArrayList<String> terms) throws NullPointerException, IndexOutOfBoundsException {
        if(terms.size() < 3)
            return new Status(false, "index error  index command should have at least three terms");

        if(terms.stream().anyMatch(t-> !(t.matches("^[a-zA-Z0-9]+"))))
            return new Status(false, "index error  terms should be alphanumeric.");

        if(!(terms.get(1).matches("^[0-9]+")))
            return new Status(false, "index error  document id should be numeric");

        this.type = "index";
        this.docIdOrQuery = terms.get(1);
        this.tokens=new ArrayList<String>();

        for(int i=2; i<terms.size();i++)
        {
            this.tokens.add(terms.get(i));
        }

        return new Status(true, "Command is valid. It is an index command");
    }

    public String getType()
    {
        return this.type;
    }

    public String getDocIdOrQuery()
    {
        return this.docIdOrQuery;
    }

    public ArrayList<String> getTokens()
    {
        return this.tokens;
    }

    private String manipulateQuery(String query, HashMap<String,String> replacements) throws NullPointerException, IndexOutOfBoundsException
    {
        String result = query;

        for(var key:replacements.keySet())
        {
            result = result.replace(key, replacements.get(key));
        }

        return result.trim();
    }


}
