package kosh.parsing;

import kosh.BandsAsRGB;
import kosh.parsing.ParsedArgs;
import org.silentsoft.arguments.parser.Arguments;
import org.silentsoft.arguments.parser.ArgumentsParser;
import org.silentsoft.arguments.parser.InvalidArgumentsException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgsParser {
    public static ParsedArgs parseArgs(String[] args) {
        String fileNameOpt = "-f";
        String clustersNumOpt = "-k";
        String fileName;
        int k;
        Arguments arguments;
        try {
            arguments = ArgumentsParser.parse(args);
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
            return null;
        }

        if (arguments.containsKey(clustersNumOpt)) {
            k = Integer.parseInt(arguments.get(clustersNumOpt).getValue());
            if (k <= 0) {
                System.err.println("Number of clusters(k) must be > 0");
                return null;
            }
        } else {
            System.err.println("Missed " + clustersNumOpt + " option");
            return null;
        }
        if (arguments.containsKey(fileNameOpt)) {
            fileName = arguments.get(fileNameOpt).getValue();
        } else {
            System.err.println("Missed " + fileNameOpt + "option");
            return null;
        }

        return new ParsedArgs(fileName, k);
    }

    public static File[] getFileNames(String stringFiles) {
        String[] strings = stringFiles.split(" ");
        File[] files = new File[strings.length];
        for (int i = 0; i < files.length; ++i) {
            files[i] = new File(strings[i]);
            System.out.println("created file " + strings[i]);
        }
        return files;
    }

    public static int parseBandsToLoad(boolean[] activeBands, Scanner scanner, int bands) {
        List<String> sIntegersList = new ArrayList<>();

        while (scanner.hasNextLine()) {
            sIntegersList.clear();
            Arrays.fill(activeBands, false);

            parseLineIn(sIntegersList, scanner);
            if (sIntegersList.size() < 3 && bands >= 3) {
                System.err.println("Invalid input");
                continue;
            }

            if (fillBandsToLoad(activeBands, sIntegersList)) {
                break;
            }
            System.out.println("Invalid input, select bands:");
        }
        return sIntegersList.size();
    }

    public static BandsAsRGB parseBandsToShow(boolean[] activeBands, Scanner scanner, int bands) {
        List<String> sIntegersList = new ArrayList<>();
        BandsAsRGB distribution = null;

        while (scanner.hasNextLine()) {
            sIntegersList.clear();

            parseLineIn(sIntegersList, scanner);
            if (sIntegersList.size() != 3 && bands >= 3) {
                System.err.println("Invalid, write 3 channels RGB");
                continue;
            }

            if ((distribution = getRGBDistribution(activeBands, sIntegersList)) != null) {
                break;
            }
            System.err.println("Invalid input, select bands:");
        }
        return distribution;
    }

    private static void parseLineIn(List<String> sIntegersList, Scanner scanner) {
        Pattern intPattern = Pattern.compile("\\d+");
        String input = scanner.nextLine();
        Matcher matcher = intPattern.matcher(input);
        while (matcher.find()) {
            sIntegersList.add(matcher.group());
        }
    }

    private static boolean fillBandsToLoad(boolean[] activeBands, List<String> sIntegersList) {
        for (String sInt : sIntegersList) {
            int arrIdx = Integer.parseInt(sInt) - 1;
            if (arrIdx < 0 || arrIdx >= activeBands.length) {
                return false;
            }
            activeBands[arrIdx] = true;
        }
        return true;
    }

    private static BandsAsRGB getRGBDistribution(boolean[] activeBands, List<String> sIntegersList) {
        int red = Integer.parseInt(sIntegersList.get(0)) - 1;
        int green = Integer.parseInt(sIntegersList.get(1)) - 1;
        int blue = Integer.parseInt(sIntegersList.get(2)) - 1;
        if (red >= 0 && green >= 0 && blue >= 0 && red < activeBands.length && green < activeBands.length && blue < activeBands.length) {
            if (activeBands[red] && activeBands[green] && activeBands[blue] && red != green && green != blue && red != blue) {
                return new BandsAsRGB(red, green, blue);
            }
        }
        return null;
    }

    public static String[] getAvailableBandsToShow(boolean[] bands, int activeNum) {
        String[] available = new String[activeNum];
        int availNum = 0;
        for (int i = 0; i < bands.length; ++i) {
            if (bands[i]) {
                available[availNum++] = Integer.toString(i + 1);
            }
        }
        return available;
    }



}
