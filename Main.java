/* This is a stub code. You can modify it as you wish. */

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

class AppClient {

    int onWattage = 0;
    int offWattage = 0;
    int onPower;
    int timeSteps;
    int location;
    int maxWattage;
    int totalWattage = 0;
    int brownOutCount = 0;
    int locationID;
    int step = 0;
    int roomPower;
    double probSmart;
    double onProb;
    double lowPower;
    double probOn;
    boolean smart;
    boolean smartAppsOn = true;
    boolean appType;
    String appName = "";
    String str;

    Scanner scan = new Scanner(System.in);
    ArrayList<Appliance> applianceList = new ArrayList<>(); //arrayList of 
    //appliances used for 
    ArrayList<Appliance> lowPowerApp = new ArrayList<>();
    HashMap<Integer, Integer> roomPowerList = new HashMap<>(); //key = location, value = power in room
    HashMap<Appliance, Integer> brownOut = new HashMap<>();
    HashMap<Integer, Integer> maxLocation = new HashMap<>(); //key number of appliances, value = location

    public void readAppFile(String file) { // method to read the comma seperated appliance file.

        try {
            File myFile = new File("output.txt");
            scan = new Scanner(myFile);//each line has the format
            //locationID,name of app,onPower,probability of staying on, smart or not,Smart appliances (if "on") power reduction percent when changed to "low" status(floating point, i.e..33=33%).
            while (scan.hasNextLine()) {
                String str;
                str = scan.nextLine();
                String[] values = str.split(",");

                int locationID;
                locationID = Integer.parseInt(values[0]);
                String appName;
                appName = values[1];

                int onPower;
                onPower = Integer.parseInt(values[2]);

                double probOn;
                probOn = Double.parseDouble(values[3]);

                boolean appType;
                appType = Boolean.parseBoolean((values[4]));

                probSmart = Double.parseDouble(values[5]);

                Appliance i = new Appliance(locationID, appName, onPower, probOn, appType, probSmart);
                applianceList.add(i);

            }
            scan.close();
        } catch (IOException ioe) {
            System.out.println("The file can not be read");
        }
    }

    public static void main(String[] args) throws IOException {
        AppClient app = new AppClient();
        String fileName = app.getInput();

        app.readAppFile(fileName);
        app.simulate();
    }

    public String getInput() {
        //Gets user input, max allowed wattage, text file name, and time steps
        Scanner scanner = new Scanner(System.in);
        String file;
        while (true) {
            System.out.println("Total allowed wattage:");
            maxWattage = scanner.nextInt();

            if (maxWattage > 0) {
                break;
            }
        }

        System.out.println("Text file containing appliances (default is ApplianceDetail.txt)");
        file = scan.nextLine();

        while (true) {
            System.out.println("Time steps:");
            timeSteps = scanner.nextInt();
            if (timeSteps > 0) {
                break;
            }
        }
        return file;
    }

    public void simulate() throws IOException {
        //Application menu that is run before the simulation
        //User can use it until they press Q to quit and then the timesteps start
        String option1;

        Scanner scanner = new Scanner(System.in);
        String quit = "no";
        do {
            // Application menu to be displayed to the user.
            System.out.println("\nSelect an option:");
            System.out.println("Type \"A\" Add an appliance");
            System.out.println("Type \"D\" Delete an appliance");
            System.out.println("Type \"L\" List the appliances");
            System.out.println("Type \"Q\" to Quit");
            option1 = scanner.nextLine();

            switch (option1) {
                //Add an appliance
                case "A":
                    System.out.println("Appliance name: ");
                    String applianceName = scanner.nextLine();
                    while (true) {
                        System.out.println("On wattage");
                        onWattage = scanner.nextInt();
                        if (onWattage > 0) {
                            break;
                        }

                    }

                    //gets probablity of appliance being on from user input
                    while (true) {
                        System.out.println("Probability of being on:");
                        onProb = scanner.nextDouble();
                        if (onProb > 0 && onProb <= 1) {
                            break;
                        }
                    }
                    //asks user if smart appliance, if yes then asks for power when
                    //low, if no then default is 0
                    System.out.println("Smart appliance? True/False");
                    smart = scanner.nextBoolean();
                    if (smart) {
                        System.out.println("Power when low: ");
                        lowPower = scanner.nextDouble();
                    } else {
                        lowPower = 0;
                    }
                    //writes user info to ApplianceDetail.txt
                    String write = applianceName + "," + onWattage + ","
                            + onProb + "," + smart + "," + lowPower;
                    FileWriter writer = new FileWriter("ApplianceDetail.txt", true);
                    BufferedWriter bw = new BufferedWriter(writer);

                    writer.write(write);
                    writer.write("\r\n");
                    bw.flush();
                    bw.close();

                    break;
                //delete an appliance
                case "D":
                    System.out.println("Name of appliance you would like to delete: ");

                    String delete = scanner.nextLine();

                    File inputFile = new File("ApplianceDetail.txt");
                    File tempFile = new File("temp.txt");

                    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    BufferedWriter wr = new BufferedWriter(new FileWriter(tempFile));

                    String currentLine;

                    while ((currentLine = reader.readLine()) != null) {

                        String trimmedLine = currentLine.trim();
                        if (trimmedLine.contains(delete)) {
                            continue;
                        }
                        wr.write(currentLine + System.getProperty("line.separator"));
                    }
                    wr.close();
                    reader.close();
                    inputFile.delete();
                    tempFile.renameTo(inputFile);
                    break;

                //prints out list of appliances
                case "L":
                    BufferedReader r = new BufferedReader(new FileReader("ApplianceDetail.txt"));
                    String line;

                    while ((currentLine = r.readLine()) != null) {
                        System.out.println(currentLine);
                    }
                    break;

                //quits application menu
                case "Q":
                    quit = "Q";
                    break;
            }

        } while (!quit.equals("Q"));

        //timesteps start simulation
        for (int i = 1; i < timeSteps + 1; ++i) {
            //goes through appliance list 
            //and turns them on if a random number is less than or equal to the
            //probability of being on
            totalWattage = 0;
            for (Appliance asdf : applianceList) {
                if (Math.random() <= asdf.probOn) {
                    asdf.isOn(true);
                    //adds onwattage to total power
                    totalWattage += asdf.onW;
                }
            }
            //if the total wattagage is greater than the max allowed wattage it
            //runs a power fixing method
            if (totalWattage > maxWattage) {
                this.fixPower();
            }
            System.out.println("\n---------\nStep " + (i));
            System.out.println("\nNumber of locations browned out: " + maxLocation.size());
            for (Integer a : maxLocation.keySet()) {
                System.out.println(maxLocation.get(a));
            }
            System.out.println("\nNumber of appliances in low: " + lowPowerApp.size());
            System.out.println("\nAppliances set to low: ");
            //for each appliance in lowPower arraylist, print
            lowPowerApp.forEach((n) -> System.out.println(n));
            System.out.println("\nMax affected location: ");

            //finds the browned out location with the most appliances in it
            int max = 0;
            for (Integer key : maxLocation.keySet()) {
                if (key > max) {
                    max = key;
                }
            }
            //if there are no locations browned out it returns null
            System.out.println(maxLocation.getOrDefault(max, null));

            //clears arrays needed for next loop
            maxLocation.clear();
            brownOut.clear();
            brownOutCount = 0;

        }
    }

    //first turns smart appliances to low, and when they are all low then it
    //starts turning off rooms by browning them out
    public void fixPower() {
        boolean appliancesOn = true;
        smartAppsOn = true;
        do {
            //turns off all smart appliances until under total power

            lowPowerApp.clear();
            if (smartAppsOn) {
                for (Appliance appliance : applianceList) {
                    if (appliance.smart && appliance.on) {
                        appliance.isOn(false);
                        int calc = (int) (appliance.onW
                                * appliance.probSmart);
                        totalWattage = totalWattage - appliance.onW + calc;
                        appliance.setlowPower(calc);
                        lowPowerApp.add(appliance);
                    }
                }
            }
            smartAppsOn = false;

            //if all the smart appliances are turned off rooms start browning out
            if (smartAppsOn == false) {

                int currentRoom;
                roomPower = 0;
                int powerCount = 0;
                //puts total power usage for a room into a hashmap of keys
                //holding locations and values containing the total wattage
                //clears list from previous steps
                roomPowerList.clear();
                for (Appliance appliance : applianceList) {

                    if (appliance.on) {
                        currentRoom = appliance.location;
                        if (roomPowerList.containsKey(currentRoom)) {
                            powerCount = roomPowerList.get(currentRoom) + appliance.onW;
                            roomPowerList.put(currentRoom, powerCount);
                        } else {
                            roomPowerList.put(currentRoom, appliance.onW);
                        }

                    }

                }

                //finds the lowest value in a list of rooms by their power usage
                //that is still over or equal to the power needed to turn off 
                int lowest = Integer.MAX_VALUE;
                for (Integer v : roomPowerList.values()) {
                    if (v < lowest && v >= (totalWattage - maxWattage)) {

                        lowest = v;
                    }
                }
                //if the amount to turn off is so large that a single room cant
                //handle it, it will choose the room with the highest power draw
                if (lowest == Integer.MAX_VALUE) {
                    int highest = 0;
                    for (Integer v : roomPowerList.values()) {
                        if (v > highest) {

                            lowest = v;
                        }
                    }
                }
                //it gets the location string that corresponds with the power usage
                for (int key : roomPowerList.keySet()) {
                    if (roomPowerList.get(key).equals(lowest)) {
                        location = key;
                    }
                }

                //for all the rooms with the location, turn them off, subtract
                //their wattage from total, and add the appliance to an arrylist
                //of the brownout appliances
                for (Appliance appliance : applianceList) {

                    if (appliance.location == (location)) {
                        //if appliance is smart, turn off its low power usage
                        if (appliance.on && appliance.smart) {
                            appliance.on = false;
                            totalWattage = totalWattage - appliance.lowPower;
                            brownOut.put(appliance, location);
                            brownOutCount++;
                        } //if appliance is regular turn it off
                        else if (appliance.on) {
                            totalWattage = totalWattage - appliance.onW;
                            brownOut.put(appliance, location);
                            brownOutCount++;

                        }
                        if (maxLocation.containsValue(location)) {
                            maxLocation.put(brownOutCount, location);
                        } else {
                            maxLocation.put(brownOutCount, location);
                        }

                    }

                }

            }

            //runs as long as the totalwattage is greater than the max wattage
        } while (totalWattage > maxWattage);
    }
}
