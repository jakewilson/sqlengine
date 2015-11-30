import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

/**
 * Created by jakewilson on 11/28/15.
 */
public class WSql {

    private CommandProcessor processor;
    private Catalog catalog;
    private Scanner input;

    public WSql(String path) {
        // TODO load file if saved
        // create new catalog if not
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            catalog = (Catalog) in.readObject();
            catalog.setPath(path);
            in.close();
            fileIn.close();
        } catch (FileNotFoundException fex) {
            catalog = new Catalog(path);
        } catch (IOException ioex) {
            catalog = new Catalog(path);
        } catch (ClassNotFoundException cnfex) {
            catalog = new Catalog(path);
        }

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
            System.out.println("Usage: ./wsql <filepath> [script]");
            return;
        }

        // TODO check if args[1] is there
        WSql w = new WSql(args[0]);
        while (w.promptUser());
    }

}
