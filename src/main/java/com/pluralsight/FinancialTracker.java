package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;

public class FinancialTracker {


    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public FinancialTracker() throws FileNotFoundException {
    }


    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }


    public static void loadTransactions(String fileName) {

        try {
            File file = new File(fileName); // creates a new file if transactions.csv is missing
            if (!file.exists()) {
                file.createNewFile();
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));

            String csvLine;
            while ((csvLine = reader.readLine()) != null) { // reads entry
                String[] tokens = csvLine.split("\\|"); //breaks it up using "|"
                LocalDate date = LocalDate.parse(tokens[0]); //turns it into a transaction (object) with all necessary fields
                LocalTime time = LocalTime.parse(tokens[1]);
                String description = tokens[2];
                String vendor = tokens[3];
                double amount = Double.parseDouble(tokens[4]);

                Transaction entry = new Transaction(date, time, description, vendor, amount);
                transactions.add(entry); //add that object to the array of transactions

                System.out.println(entry.toString());
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("ERROR: An unexpected error occurred");
            e.printStackTrace();
        }
        System.out.println();
    }


    private static void addDeposit(Scanner scanner) {
        System.out.println("Please enter the details of your transaction. ");

        boolean validEntry = false;
        LocalDateTime dateTime = null;
        while(validEntry == false) {
            System.out.println("Enter the date and time of the transaction in the format (yyyy-MM-dd HH:mm:ss):");
            String dateTimeStr = scanner.nextLine().trim();

            try {
                dateTime = LocalDateTime.parse(dateTimeStr, DATETIME_FMT); // takes date and time string
                validEntry = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date or time. Please try again.");
            }
        }

            LocalDate dateOfTran = dateTime.toLocalDate(); // breaks it into local date
            LocalTime timeOfTran = dateTime.toLocalTime(); // and local time

        System.out.println("Enter the description of the transaction:");
        String descOfTran = scanner.nextLine().trim();
        System.out.println("Enter the vendor name:");
        String vendorName = scanner.nextLine().trim();

        double tranAmt = 0;
        while (true) {
            System.out.println("Enter the transaction amount in the format (0.00):");

            if (!scanner.hasNextDouble()) {
                System.out.println("Invalid input please enter a valid amount"); // Error for non-numbers
                scanner.next();
                continue;
            }else
            {
                tranAmt = scanner.nextDouble();
                scanner.nextLine();
            }
             if (tranAmt >= 0) { // checks if the amount is positive
                break; // jumps out of the loop
            } else if (tranAmt < 0){
                System.out.println("Amount must be greater than 0"); //Error message for negative numbers
            }

        }


        Transaction entry = new Transaction(dateOfTran, timeOfTran, descOfTran, vendorName, tranAmt); //becomes a transaction object

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(entry.toString() + "\n");
            transactions.add(entry); //adds the new object to the array
            writer.close();
            System.out.println("Deposit added successfully\n"); // prints the success message

        } catch (IOException e) {
            System.out.println("ERROR: An unexpected error occurred");
            e.printStackTrace();
        }
    }

    private static void addPayment(Scanner scanner) {
        System.out.println("Please enter the details of your transaction.");

        boolean validEntry = false;
        LocalDateTime dateTime = null;
        while(validEntry == false) {
            System.out.println("Enter the date and time of the transaction in the format (yyyy-MM-dd HH:mm:ss):");
            String dateTimeStr = scanner.nextLine().trim();

            try {
                dateTime = LocalDateTime.parse(dateTimeStr, DATETIME_FMT); // same thing
                validEntry = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date or time. Please try again.");
            }
        }

        LocalDate dateOfTran = dateTime.toLocalDate();
        LocalTime timeOfTran = dateTime.toLocalTime();


        System.out.println("Enter the description of the transaction:");
        String descOfTran = scanner.nextLine().trim();
        System.out.println("Enter the vendor name:");
        String vendorName = scanner.nextLine().trim();

        double tranAmt = 0;

        while (true) {
            System.out.println("Enter the transaction amount in the format (0.00):");

            if (!scanner.hasNextDouble()) {
                System.out.println("Invalid input please enter a valid amount"); // Error for everything else
                scanner.next();
                continue;
            }else {
                tranAmt = scanner.nextDouble();
                scanner.nextLine();
            }

            if (tranAmt > 0) { // makes sure the amount is positive
                break; // jumps out of the loop
            } else if (tranAmt < 0){
                System.out.println("Amount must be greater than 0"); //Error message for negative numbers
            }
        }


        Transaction entry = new Transaction(dateOfTran, timeOfTran, descOfTran, vendorName, -1 * (tranAmt)); // makes the amount negative within the field

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(entry.toString() + "\n");
            transactions.add(entry);
            writer.close();
            System.out.println("Payment added successfully\n");

        } catch (IOException e) {
            System.out.println("ERROR: An unexpected error occurred");
            e.printStackTrace();
        }
    }

    private static void ledgerMenu(Scanner scanner) {

        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void displayLedger() {
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());//sorts the transactions from newest to oldest
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void displayDeposits() {
       boolean found = false;
       for (Transaction transaction : transactions) {
            if(transaction.getAmount() >= 0){ //checks for positive entries
                System.out.println(transaction); //prints
                found = true;
            }
        }
        if(found == false){
            System.out.println("No transactions found"); //if none, no entries found
        }
    }

    private static void displayPayments() {
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) { //checks for negative entries
                System.out.println(transaction); //prints them
                found = true;

            }
        }
        if (found == false){
            System.out.println("No transactions found"); //if none, prints no entries found
        }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            LocalDate today = LocalDate.now();


            LocalDate startOfMonth = today.withDayOfMonth(1); //this month day 1
            LocalDate startOfYear = startOfMonth.withMonth(1); //month 1 day one

            LocalDate startOfLastMonth = startOfMonth.minusMonths(1); // this month - 1 (previous)
            int daysInLastMonth = startOfLastMonth.lengthOfMonth(); //total number of days in previous month
            LocalDate endOfLastMonth = startOfLastMonth.withDayOfMonth(daysInLastMonth); // previous month + last day
            LocalDate startOfLastYear = startOfYear.minusYears(1); // this year - 1 (previous)
            LocalDate endOfLastYear = startOfLastYear.withMonth(12).withDayOfMonth(31); //last month of the year (12(December)) last day of month (31)

            switch (input) {
                case "1" -> {
                    filterTransactionsByDate(startOfMonth,today); //this month to today
                }
                case "2" -> {
                    filterTransactionsByDate(startOfLastMonth,endOfLastMonth); //last month (whole month)
                }
                case "3" -> {
                    filterTransactionsByDate(startOfYear,today); //January (start of this year) to now
                }
                case "4" -> {
                    filterTransactionsByDate(startOfLastYear,endOfLastYear); // last year (whole)
                }
                case "5" -> {
                    System.out.println("Enter the name of the vendor:");
                    String vendorName = scanner.nextLine(); //take name
                    filterTransactionsByVendor(vendorName); //check for matches
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }


    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        boolean found = false;  //transaction not found (yet)
        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isBefore(start) && !transaction.getDate().isAfter(end)){ //inclusive
                System.out.println(transaction); //print each
                found = true; // found match(es)

            }
        }
        if(found == false){ //still not found
            System.out.println("No transactions found"); //Don't exist
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        boolean found = false; //not found
        for (Transaction transaction : transactions) { //searching each transaction
            if(transaction.getVendor().equalsIgnoreCase(vendor)){
                System.out.println(transaction); //print each
                found = true; //end (match(es) found)
            }
        }
        if(found == false){
            System.out.println("No transactions found"); //DNE
        }
    }

    private static void customSearch(Scanner scanner) {
        System.out.println("Please enter your transaction details (Leave any unnecessary filters blank):\n");
        String customFiltersStr = scanner.nextLine();
    }


    private static LocalDate parseDate(String s) {
       if(!s.equalsIgnoreCase("")){
           LocalDate parsedDate = LocalDate.parse(s);
           return parsedDate;
       }else {
           return null;
       }
    }

    private static Double parseDouble(String s) {
        if (s.equalsIgnoreCase("")){
            return null;
        }else{
            return Double.parseDouble(s);
        }
    }
}
