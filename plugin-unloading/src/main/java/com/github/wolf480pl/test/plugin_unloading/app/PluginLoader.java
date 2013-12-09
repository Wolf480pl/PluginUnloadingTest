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

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class PluginLoader {
    private Map<String, ClassLoader> classloaders = new HashMap<String, ClassLoader>();
    private Map<String, Plugin> plugins = new HashMap<String, Plugin>();
    private Map<String, PluginState> states = new HashMap<String, PluginState>();
    private Map<String, PhantomReference<Plugin>> phantoms = new HashMap<String, PhantomReference<Plugin>>();
    private Map<String, ReferenceQueue<Plugin>> refQueues = new HashMap<String, ReferenceQueue<Plugin>>();

    public void loadPlugin(File file, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String name = file.getName().replaceAll("\\.jar", "");
        System.out.println("Plugin name: " + name);
        ClassLoader loader = null;
        try {
            loader = new URLClassLoader(new URL[] { file.toURI().toURL() });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.classloaders.put(name, loader);
        Class<?> clazz = Class.forName(className, true, loader);
        if (!Plugin.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Not a subclass of plugin:" + clazz.getCanonicalName());
        }
        @SuppressWarnings("unchecked")
        Class<Plugin> pluginClass = (Class<Plugin>) clazz;
        Plugin plugin = pluginClass.newInstance();
        this.plugins.put(name, plugin);
        this.states.put(name, PluginState.DISABLED);
    }

    public void enablePlugin(String name) {
        if (this.states.get(name) == PluginState.DISABLED) {
            this.plugins.get(name).onEnable();
            this.states.put(name, PluginState.ENABLED);
        } else {
            System.err.println("Cannot enable plugin " + name + " - it's state is " + this.states.get(name));
        }
    }

    public void disablePlugin(String name) {
        if (this.states.get(name) == PluginState.ENABLED) {
            this.plugins.get(name).onDisable();
            this.states.put(name, PluginState.DISABLED);
        } else {
            System.err.println("Cannot disable plugin " + name + " - it's state is " + this.states.get(name));
        }
    }

    public void unloadPlugin(String name) {
        if (this.states.get(name) != PluginState.DISABLED) {
            System.err.println("Cannot unload plugin " + name + " - it's state is " + this.states.get(name));
            return;
        }

        Plugin plugin = this.plugins.get(name);
        ReferenceQueue<Plugin> queue = new ReferenceQueue<Plugin>();
        PhantomReference<Plugin> ref = new PhantomReference<Plugin>(plugin, queue);

        this.plugins.remove(name);
        this.classloaders.remove(name);
        this.states.put(name, PluginState.LIMBO);
        this.phantoms.put(name, ref);
        this.refQueues.put(name, queue);
    }

    public boolean check(String name) {
        if (this.states.get(name) != PluginState.LIMBO) {
            System.err.println("Cannot check plugin " + name + " - it's state is " + this.states.get(name));
            return false;
        }
        ReferenceQueue<Plugin> queue = this.refQueues.get(name);
        if (queue.poll() == null) {
            return false;
        }
        this.states.remove(name);
        this.phantoms.remove(name);
        this.refQueues.remove(name);
        System.gc();
        return true;
    }

    public static enum PluginState {
        ENABLED, DISABLED, LIMBO
    }
}
