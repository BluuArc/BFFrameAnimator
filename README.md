# Brave Frontier Frame Animator (BFFrameAnimator)

## Description
Program to create animation strips and GIFs of unit sprites for the mobile game Brave Frontier.

## Features
* Can create an animation strip or GIF for 1 unit or multiple units
* This program outputs 6 versions of the same unit in GIF form.
 * the three animations of a unit (idle, attack, and move) with opacity values
 * the three animations of a unit (idle, attack, and move) without opacity values
 * the idle animation of a unit with a minimum height of 140px for the BF wiki with opacity values
 * the idle animation of a unit with a minimum height of 140px for the BF wiki without opacity values
 * the three animations of a unit (idle, attack, and move) from an animation strip
 * the idle animation of a unit with a minimum height of 140px for the BF wiki from an animation strip
 * Note that the suffices of the files will tell you which option you chose
    * `unit_unitID_type_opac.gif` for one of the three animations with opacity values
     * replace `opac` with `nopac` to get the name of the file with no opacity values
     * replace `opac` with `strip` to get the name of the file created from an animation strip
    * same applies to `unit_ills_anime_unitID_opac.gif` for a wiki animation with opacity values
* This program also outputs animation strips, which are basically images where the frames are laid out horizontally; one strip represents one full animation (i.e. idle, move, atk).

## Setup
What this program does is it takes a .txt file (which I'll refer to as `list.txt`) and uses it to reference the locations of the source files and the location of where the results will be saved. Below are examples of the list.txt and the file-tree for accessing and saving files.

`list.txt` would contain:  
`10011`

The file-tree accessed from this list.txt would look like this:
```
/GIFs/
-/output/
--[GIFs for each unit would be saved here]
--/frames/
---[frames for each unit would be saved here]
/Units/
-[Folders are accessed by the unit ID]
-/10011/
--unit_atk_cgs_10011.csv  [contains info on the order of the frames for the attack animation]
--unit_idle_cgs_10011.csv [contains info on the order of the frames for the idle animation]
--unit_move_cgs_10011.csv [contains info on the order of the frames for the move animation]
--unit_cgg_10011.csv      [contains info on creating each frame]
--unit_anime_10011.png    [the spritesheet]
--unit_atk_10011.png      [the attack animation in strip form]
--unit_idle_10011.png     [the idle animation in strip form]
--unit_move_10011.png     [the move animation in strip form]
list.txt
```
You can view what the file tree would look like in [this file](https://www.dropbox.com/s/ov16bzl62xdtgio/example.zip?dl=0 "example.zip").
To get the spritesheet and CSV files, you would have to download them from the Global or JP server; to get these files for multiple units, a combination of `wget` and a batch file would be best. The atk, idle, and move strips are generated by the program itself as of v1.1 (June 12, 2016 update).

## How to Use
Download the BFFrameAnimator.jar in the root folder (or compile the classes in the /bin/ folder into a jar file with BFAnimationMenu as the main class) to somewhere on your computer. After that, run it through the command prompt with `java -jar BFFrameAnimator.jar`, and follow the onscreen prompts. Be sure that the folders of the file tree (shown above) are already made by the time you run the program, or you may encounter errors. Also ensure that nothing in those folders are being used by another program; you may encounter file opening/saving errors if the file tree isn't prepared correctly.

## Credits/Sources
* Thanks to [Deathmax](https://github.com/Deathmax/) for telling me what each value in the CSV files correspond to
* This program uses the following to function. Credits to them for their portions of my program.
  * A modified and stripped-down version of [bookClasses](http://home.cc.gatech.edu/TeaParty/47), which is licensed under a Creative Commons Attribution 3.0 US License, to handle some image operations and file operations
  * The Java class [AnimatedGifEncoder](http://www.java2s.com/Code/Java/2D-Graphics-GUI/AnimatedGifEncoder.htm) to handle GIF creation from one or more image files.
  * There are also code snippets from the Internet that I may adapt to use in my program. Those sources can be found near their relative blocks of code.

## Licensing

<a rel="license" href="http://creativecommons.org/licenses/by/3.0/us/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/3.0/us/88x31.png" /></a><br />The source code created by me (and not mentioned in the Credits/Sources section) is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/us/">Creative Commons Attribution 3.0 United States License</a>. By using this code, you agree to the terms stated in the Creative Commons Attribution 3.0 United States License.

## Other Notes
This project was started on December 21, 2015, my 18th birthday. It's gone through many, many revisions between then and the first time I put it on Github (May, 7, 2016). I'm sure that it will be more helpful here for others to view and use.

