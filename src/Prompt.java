import java.util.Scanner;

/**
 * Created by jakewilson on 11/28/15.
 */
public class Prompt {

    private CommandProcessor processor;
    private Catalog catalog;
    private Scanner input;

    public Prompt(String path) {
        // TODO load file if saved
        // create new catalog if not
        catalog = new Catalog();
        processor = new CommandProcessor(catalog);
        input = new Scanner(System.in);
    }

    public boolean promptUser() {
        System.out.print("wSQL> ");
        String cmd = input.nextLine();
        if (cmd.trim().equalsIgnoreCase("exit"))
            return false;

        try {
            System.out.print(processor.execute(Parser.parse(cmd)));
        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
        }

        return true;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ./wsql <filepath>");
            return;
        }

        // TODO check if args[1] is there
        Prompt p = new Prompt(args[0]);
        while (p.promptUser());
    }

}
