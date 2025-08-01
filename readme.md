# Combat Helper
This program is designed to help a Dungeon Master run combat in D&D 5e14. It should work be adaptable for other systems with some modifications, but I really only play 5e14. (I imagine it works for 5e24 as well, but I haven't tested it.)

This project is primarily for my own use, so documentation and comments are limited. If you have any questions, please feel free to reach out directly!

## Install and Use

### Getting Started
Download the project and install Maven if you don't have it. (Maven installation instructions can be found [here](https://maven.apache.org/install.html)). Then open the `party_list.csv` file in `src/main/resources`. Delete the contents and add your own party or parties; the first column should have the campaign name, then the player name, and then the Dexterity score. (The latter is used for resolving initiative ties automatically.)

### Adding Monsters
This file comes with all of the 5e SRD monsters pre-loaded in `monster_list.csv` (in the `src/main/resources` directory), as well as a few homebrew and 3rd-party monsters. **Writing new ones into the CSV is not recommended.** Instead, run the `AddMonsterToCsv.java` file (in the `core` package) and follow the command-line instructions.

### Running Combat
Running combat is the core feature of this project (as the name implies!) Open `CombatRunner.java` in the `core` package and run its main function. From there, the command-line will prompt you every step of the way. If you enter invalid data, it will usually warn you and let you enter a new answer, and if the program crashes at any point, it will first output its state.

### Commands
When you run combat, most prompts will allow you to use commands. These start with the reserved character `!`, which cannot be used to start strings in any other context. Currently, the following commands exist:

| Command   | Effect  |
| --------- | ------- |
| !add      | Add another monster to the combat; it must already be in `monster_list.csv`. It will be inserted into initiative order. |
| !end      | Ends combat without declaring a winner. Irreversible. |
| !cond-add | Add a condition to a particular monster, which will be printed out on its turn. (This can be any string; it is not limited to 5e conditions.) |
| !cond-del | Delete a condition from a particular monster if it currently has it. |
| !help     | Print out each command along with a short description. |
| !hp       | Change a monster's hit points. |
| !init     | Change the initiative order. This is a finicky command, so be careful to write exactly the names of each monster, or its behavior is undefined. |
| !la       | Change the current number of legendary actions a particular monster has. This will not affect its maximum, so the changes will be effectively erased at the start of its next turn. |
| !lr       | Change the number of remaining legendary resistances a particular monster has. |


