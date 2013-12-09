PluginUnloadingTest
===================

A simple example of dynamic plugin unloading in java.

1. Run `mvn package`
2. Run `copy.sh`
3. Run `start.sh`
4. In another terminal check who's using plugin-1.jar `fuser plugin-1.jar`.
There shouldn't be anyone. Or at least process id of the JVM running the example shouldn't be on the list.
5. Type `load plugin-1.jar com.github.wolf480pl.test.plugin_unloading.plugin1.Plugin1` in the example's console and press enter.
It should output "Plugin name: plugin-1"
6. Check who's using plugin-1.jar again. The process id of the JVM running the example should be there.
7. Type `enable plugin-1` in the example's console to see if the plugin works. It should output "Plugin1 enabled"
8. Type `disable plugin-1`. It should output "Plugin1 disabled"
9. Type `unload plugin-1`. It shouldn't output anything.
10. Check who's using plugin-1.jar again. The process id of the JVM should be still there.
11. Type `check` in the example's console. It should output "Plugin still reachable"
12. Type `gc` in the example's console. It shouldn't output anything.
13. Check who's using plugin-1.jar again. The process id of the JVM should be still there.
14. Type `check` in the example's console. It should output "Plugin succesfully unloaded"
15. Check who's using plugin-1.jar again. Now process id of the JVM should be gone.

BTW. the example doesn't have "exit" command. You need to Ctrl+C it in order to exit.

Copyright (c) 2012-2013 Wolf480pl (wolf480@interia.pl)

PluginUnloadingTest is licensed under MIT license. Please see LICENSE.txt for details.
