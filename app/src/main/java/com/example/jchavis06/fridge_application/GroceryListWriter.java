package com.example.jchavis06.fridge_application;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class GroceryListWriter {
        BufferedReader br;
        FileReader fr;

        BufferedWriter bw;
        FileWriter fw;

        private Context context;
        final String groceryListFile = "groceryList.txt";

        public GroceryListWriter(Context context) {
            this.context = context;
        }


        public void addToGroceryList(String item, String description) {

        /*
            Steps to add:
            1. add item to grocery list
            2. create file for the item
            3. add description to that items file.
         */
            try {
                File f = new File(context.getFilesDir() + "/" + groceryListFile);
                Log.d("TAG", "Trying to write to:" + context.getFilesDir() + "/" + groceryListFile);
                fw = new FileWriter(f, true);
                fw.write(item + "\n");
                fw.close();
            } catch (Exception e) {
                System.out.println("Error adding to grocery list file: " + e.getMessage());
            }

            try {
                //now we need to create a file for that specific item.
                //String newFileName = item + ".txt";
                File f = new File(context.getFilesDir() + "/" + item + ".txt");
                fw = new FileWriter(f);
                fw.write(description + "\n");
                fw.close();
            } catch (Exception e) {
                System.out.println("Error writing to new items file: " + e.getMessage());
            }

        }

        public void removeFromGroceryList(String item) {
            //first we need to read the grocery list so that we can easily remove from list
            ArrayList<String> currentGroceryList = readGroceryList();

            currentGroceryList.remove(item);

            writeGroceryListToFile(currentGroceryList);

            //now we need to remove the item-specific file.
            String fileName = item + ".txt";
            try {
                File f = new File(context.getFilesDir() + "/" + fileName);
                if (f.delete()) {
                    System.out.println("Successfully deleted file: " + fileName);
                } else {
                    System.out.println("Unable to delete file: " + fileName);
                }
            } catch (Exception e) {
                System.out.println("Error removing items file: " + e.getMessage());
            }

        }

        public void editGroceryListItemDescription(String item, String description) {
            try {
                String fileName = item + ".txt";
                File f = new File(context.getFilesDir() + "/" + fileName);
                fw = new FileWriter(f);
                fw.write(description + "\n");
                fw.close();
            } catch (Exception e) {
                System.out.println("Error editing description for item: " + e.getMessage());
            }

        }


        public ArrayList<String> readGroceryList() {
            System.out.println("Reading from the file...");
            Log.d("Tag", "Reading form the grocery list file.");
            //we want to read the current grocery list so that we can easily make modifications.
            ArrayList<String> groceryList = new ArrayList<String>();
            try {
                File f = new File(context.getFilesDir() + "/" + groceryListFile);
                fr = new FileReader(f);
                br = new BufferedReader(fr);

                String currentLine;

                while ((currentLine = br.readLine()) != null) {
                    Log.d("READLIST", "Read from list: " + currentLine);
                    groceryList.add(currentLine);
                }

                br.close();
                fr.close();
            } catch (Exception e) {
                System.out.print("Error reading from grocery list file: " + e.getMessage());
            }

            Log.d("TAG", "Number of items in the grocery list: " + groceryList.size());

            return groceryList;
        }

        public String readGroceryListItemDescription(String item) {
            try {
                String fileName = item + ".txt";
                String description = "";
                File f = new File(context.getFilesDir() + "/" + fileName);
                fr = new FileReader(f);
                br = new BufferedReader(fr);
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    description += currentLine;
                }

                br.close();
                fr.close();

                return description;

            } catch (Exception e) {
                System.out.println("Error reading grocery list item description: " + e.getMessage());
            }

            return null;
        }

        private void writeGroceryListToFile(ArrayList<String> groceryList) {
            //just write the grocery list on a clean slate to the file.
            try {
                File f = new File(context.getFilesDir() + "/" + groceryListFile);
                fw = new FileWriter(f);
                for (String item: groceryList) {
                    fw.write(item + "\n");
                }
                fw.close();
            } catch (Exception e) {
                System.out.print("Error writing grocery list file: " + e.getMessage());
            }

        }
    }
