package simulator.launcher;

import org.apache.commons.cli.*;
import org.json.JSONObject;
import org.json.JSONTokener;
import simulator.control.Controller;
import simulator.factories.*;
import simulator.misc.Utils;
import simulator.model.*;
import simulator.view.MainWindow;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class to launch the simulator
 */
public class Main {
    private static final String _out_file = null;
    private static final ExecMode _mode = ExecMode.GUI;
    public static Factory<Animal> animal_factory;
    public static Factory<Region> region_factory;
    public static Factory<SelectionStrategy> selection_factory;
    // some attributes to stores values corresponding to command-line parameters
    private static Double _time = 0.0;
    public static Double _dt = 0.03;
    private static String _in_file = null;
    private static boolean _sv = false;

    /**
     * Parse the command-line arguments
     *
     * @param args the command-line arguments
     */
    private static void parse_args(String[] args) {

        // define the valid command line options
        Options cmdLineOptions = build_options();
        // parse the command line as provided in args
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(cmdLineOptions, args);
            parse_help_option(line, cmdLineOptions);
            parse_in_file_option(line);
            parse_time_option(line);
            parse_dtime_option(line);
            parse_sv_option(line, cmdLineOptions);
            parse_out_file_option(line);

            String[] remaining = line.getArgs();
            if (remaining.length > 0) {
                String error = "Illegal arguments:";
                for (String o : remaining)
                    error += (" " + o);
                throw new ParseException(error);
            }

        } catch (ParseException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Builds the command-line options.
     *
     * @return Options object containing the command-line options
     */
    private static Options build_options() {
        Options cmdLineOptions = new Options();

        // help .
        cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

        // input file
        cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

        // Excecution mode
        cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Execution Mode. Possible values: 'batch' (Batch mode), 'gui' (Graphical User Interface mode). Default value: " + _mode + ".").build());

        // steps
        cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
                .desc("An real number representing the total simulation time in seconds. Default value: "
                        + _time + ".")
                .build());

        cmdLineOptions.addOption(Option.builder("dt").longOpt("delta time").hasArg()
                .desc("A double representing actual time, in seconds, per simulation step. Default value: "
                        + _dt + ".")
                .build());

        cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());

        cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file. where output is written").build());
        return cmdLineOptions;
    }

    /**
     * Parses the delta time option from the command line.
     *
     * @param line CommandLine object containing parsed command-line options
     * @throws ParseException If the provided value for delta time is invalid
     */
    private static void parse_dtime_option(CommandLine line) throws ParseException {
        String dt = line.getOptionValue("dt", _dt.toString());

        try {
            _dt = Double.parseDouble(dt);
            assert (_time >= 0);
        } catch (Exception e) {
            throw new ParseException("Invalid value for time: " + dt);
        }
    }

    /**
     * Parses the help option from the command line and prints help message.
     *
     * @param line           CommandLine object containing parsed command-line options
     * @param cmdLineOptions Options object containing valid command-line options
     */
    private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);

        }
    }

    /**
     * Parses the simple viewer option from the command line.
     *
     * @param line           CommandLine object containing parsed command-line options
     * @param cmdLineOptions Options object containing valid command-line options
     */
    private static void parse_sv_option(CommandLine line, Options cmdLineOptions) {
        if (line.hasOption("sv")) {
            _sv = true;
        }
    }

    /**
     * Parses the input file option from the command line.
     *
     * @param line CommandLine object containing parsed command-line options
     * @throws ParseException If the input file option is missing or invalid
     */
    private static void parse_in_file_option(CommandLine line) throws ParseException {
        _in_file = line.getOptionValue("i");
        if (_mode == ExecMode.BATCH && _in_file == null) {
            throw new ParseException("In batch mode an input configuration file is required");
        }
    }

    /**
     * Parses the output file option from the command line.
     *
     * @param line CommandLine object containing parsed command-line options
     * @throws ParseException If the output file option is missing or invalid
     * @throws IOException    If an I/O error occurs
     */
    private static void parse_out_file_option(CommandLine line) throws ParseException, IOException {
        String _out_file = line.getOptionValue("o");
        if (_mode == ExecMode.BATCH && _out_file == null) {
            throw new ParseException("In batch mode an input configuration file is required");
        }
    }

    /**
     * Parses the simulation time option from the command line.
     *
     * @param line CommandLine object containing parsed command-line options
     * @throws ParseException If the provided value for simulation time is invalid
     */
    private static void parse_time_option(CommandLine line) throws ParseException {
        String t = line.getOptionValue("t");
        if(t == null) {
            return;
        }
        try {
            _time = Double.parseDouble(t);
            assert (_time >= 0);
        } catch (Exception e) {
            throw new ParseException("Invalid value for time: " + t);
        }
    }

    /**
     * Initializes the factories for the simulator
     *
     */
    private static void init_factories() {
        //strategies factory
        List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
        selection_strategy_builders.add(new SelectFirstBuilder());
        selection_strategy_builders.add(new SelectClosestBuilder());
        selection_strategy_builders.add(new SelectYoungestBuilder());
        selection_factory = new BuilderBasedFactory<>(selection_strategy_builders);

        try {
            //animal factory
            List<Builder<Animal>> animal_builders = new ArrayList<>();
            animal_builders.add(new SheepBuilder(selection_factory));
            animal_builders.add(new WolfBuilder(selection_factory));
            animal_factory = new BuilderBasedFactory<>(animal_builders);

            //region factory
            List<Builder<Region>> region_builders = new ArrayList<>();
            region_builders.add(new DefaultRegionBuilder());
            region_builders.add(new DynamicSupplyRegionBuilder());
            region_factory = new BuilderBasedFactory<>(region_builders);
        } catch (Exception e) {
            System.err.println("Error while loading the input file: " + e.getLocalizedMessage());

        }
    }

    /**
     * Load a JSON file into a JSONObject
     *
     * @param in the input stream
     * @return the JSONObject
     */
    private static JSONObject load_JSON_file(InputStream in) {
        return new JSONObject(new JSONTokener(in));
    }

    /**
     * Starts the simulator in batch mode
     *
     * @throws Exception If an error occurs while starting the simulator
     */
    private static void start_batch_mode() throws Exception {
        InputStream is = new FileInputStream(_in_file);
        try {
            // (1) Load the input file into a JSONObject
            JSONObject inputJson = load_JSON_file(is);

            // (2) Create the output file
            OutputStream outputFile = (_out_file != null) ? new FileOutputStream(_out_file) : System.out;

            // (3) Create an instance of Simulator passing the appropriate information to its constructor
            Simulator simulator = new Simulator(inputJson.getInt("width"), inputJson.getInt("height"), inputJson.getInt("cols"), inputJson.getInt("rows"), animal_factory, region_factory);

            // (4) Create an instance of Controller, passing it the simulator
            Controller controller = new Controller(simulator);

            // (5) Call load_data by passing it the input JSONObject
            controller.load_data(inputJson);

            // (6) Call the run method with the corresponding parameters
            controller.run(_time, _dt, _sv, outputFile);

            // (7) Close the output file
            outputFile.close();

        } catch (IOException e) {
            System.err.println("Error while loading the input file: " + e.getLocalizedMessage());
        }


    }

    /**
     * Starts the simulator in GUI mode
     *
     * @throws Exception If an error occurs while starting the simulator
     */
    private static void start_GUI_mode() throws Exception {
        Simulator sim;
        Controller ctrl;
        if (_in_file != null) {
            InputStream is = new FileInputStream(_in_file);
            JSONObject inputJson = load_JSON_file(is);
            is.close();
            Simulator simulator = new Simulator(inputJson.getInt("width"), inputJson.getInt("height"), inputJson.getInt("cols"), inputJson.getInt("rows"), animal_factory, region_factory);
            Controller controller = new Controller(simulator);
            controller.load_data(inputJson);
            SwingUtilities.invokeAndWait(() -> new MainWindow(controller));
        } else {
            sim = new Simulator(800, 600, 15, 20, animal_factory, region_factory);
            ctrl = new Controller(sim);
            SwingUtilities.invokeAndWait(() -> new MainWindow(ctrl));


        }
    }

    /**
     * Starts the simulator
     *
     * @param args the command-line arguments
     * @throws Exception If an error occurs while starting the simulator
     */
    private static void start(String[] args) throws Exception {
        init_factories();
        parse_args(args);
        switch (_mode) {
            case BATCH:
                start_batch_mode();
                break;
            case GUI:
                start_GUI_mode();
                break;
        }
    }

    public static void main(String[] args) {
        Utils._rand.setSeed(2147483647L);
        try {
            start(args);
        } catch (Exception e) {
            System.err.println("Something went wrong ...");
            System.err.println();
            e.printStackTrace();
        }
    }

    private enum ExecMode {
        BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

        private final String _tag;
        private final String _desc;


        ExecMode(String modeTag, String modeDesc) {
            _tag = modeTag;
            _desc = modeDesc;
        }

        public String get_tag() {
            return _tag;
        }

        public String get_desc() {
            return _desc;
        }
    }
}
