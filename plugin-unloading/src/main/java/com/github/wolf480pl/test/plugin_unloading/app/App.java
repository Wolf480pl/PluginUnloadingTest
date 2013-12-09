/*
 * This file is part of Plugin Unloading Test.
 *
 * Copyright (c) 2013 Wolf480pl <wolf480@interia.pl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.wolf480pl.test.plugin_unloading.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {

    public static void main(String[] args) {
        PluginLoader loader = new PluginLoader();
        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader input = new BufferedReader(streamReader);
        try {
            while (true) {
                System.out.print(">");
                String cmdline = input.readLine();
                String[] cmd = cmdline.split(" ");
                if (cmd[0].equalsIgnoreCase("load")) {
                    if (cmd.length < 3) {
                        System.out.println("Usage: load <file> <className>");
                    } else {
                        File file = new File(cmd[1]);
                        try {
                            loader.loadPlugin(file, cmd[2]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (cmd[0].equalsIgnoreCase("enable")) {
                    if (cmd.length < 2) {
                        System.out.println("Usage: enable <pluginName>");
                    } else {
                        loader.enablePlugin(cmd[1]);
                    }
                } else if (cmd[0].equalsIgnoreCase("disable")) {
                    if (cmd.length < 2) {
                        System.out.println("Usage: disable <pluginName>");
                    } else {
                        loader.disablePlugin(cmd[1]);
                    }
                } else if (cmd[0].equalsIgnoreCase("unload")) {
                    if (cmd.length < 2) {
                        System.out.println("Usage: unload <pluginName>");
                    } else {
                        loader.unloadPlugin(cmd[1]);
                    }
                } else if (cmd[0].equalsIgnoreCase("check")) {
                    if (cmd.length < 2) {
                        System.out.println("Usage: check <pluginName>");
                    } else {
                        if (loader.check(cmd[1])) {
                            System.out.println("Plugin succesfully unloaded");
                        } else {
                            System.out.println("Plugin still reachable");
                        }
                    }
                } else if (cmd[0].equalsIgnoreCase("gc")) {
                    System.gc();
                } else {
                    System.out.println("Unknown command: " + cmd[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
