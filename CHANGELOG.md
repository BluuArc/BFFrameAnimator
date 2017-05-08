# Working Change Log (To be put into release notes when ready)

## TODO (will be moved to the change log section below as these are completed)
* stretch goal: add in hit count animation
* try to fix rotation issues on some units
* make comments JDocs compatible

## May 8, 2018 (v2.1.2 Release)
* New Feature: Added wikiMode option to command line

## January 3, 2017 (v2.1.1 Release)
* Bugfix: fixed bug where program would not work properly on Unix environments

## December 21, 2016 to December 25, 2016 (v2.1.0 Release)
* General Tune-Up: Improved exception handling so program doesn't halt upon encountering an error
 * Exceptions are handled per CGS file per unit, which makes it easy to narrow down where the error occurred without stopping the program
* Bugfix: fixed bug where frames with 0 parts would skip the animation or stop the program
* General Tune-Up: removed test function
* New Feature: added experimental shrink mode feature
 * this removes the whitespace around the animation; in other words, it shrinks the border around an animation to be closest to the farthest pixels
 * this is experimental as some animations were glitchy as a result of this in v1.x
 * by default, this is enabled for wiki GIFs as the feature was derived from the algorithm for wiki GIFs

## December 2, 2016 to v2.0 Release Date (December 17, 2016)
* almost complete rewrite of entire program to better fit Object Oriented Principles
* cleaner and more concise output during program execution
* error messages are now output if any errors were encountered per unit
 * add in file checking to ensure that all files needed are available
* added option to save each frame as a strip of individual parts from the sprite sheet
* fixed bug where some shadows disappeared in the opacity options
* addition of debug messages and debug mode
* add check for improper frames in GIF (i.e. frames that weren't properly made transparent)
* opacity for units is improved
* parts created during frame creation process are now temporarily saved as individual parts
 * this increases the time needed to make longer animations, but this is necessary to reduce memory issues
* allow passing in of command line arguments (run JAR file with '-help' to see possible commands)
* added creation of a log file that contains information that is potentially useful for troubleshooting 
* wiki related code is a lot less scattered class wise
* option to disable UI via command line (only occurs if all directories are set)

## October 21, 2016
* added numbers next to menu options to make last input clearer
* there is now a method that takes in a CGG file, a CGS file, and the sprite sheet array (for modularity and for units that share files)
* updated relevant methods to take in Picture2 arrays for the sprite sheet (for units that have more than 1 source file
* added support for generating the Summoner unit

## July 25, 2016
* Change some menus to limit input to those options (using lists or buttons)
* v1.2.2 Release

## July 15, 2016
* fixed bug where wiki GIFs created from animation strips weren't the correct height (minimum 140px)
* fix bug in StripMaker where deleting frames message didn't output properly
* fixed typos in StripMaker (messages now say strips instead of GIFs) 
* add proper program termination from the animation menu (so JVM actually exits instead of returning)
* fixed begin/end message issues when running classes 
* v1.2.1 Release

## July 2, 2016
* finished clean up and reflection of changes from FrameMaker to BFStripMaker class
* v1.2 Release

## June 28, 2016
* modified and extended CGS file finding method to two general file finding functions (both single and multiple files)
* implemented file finding functions into main method of FrameMaker class, which greatly improves readability
 * as a result, the program is now compatible with FFBE
* FrameMaker now deletes frames after GIF is made
* added handy debug function to FrameMaker
* added todo list in animation menu (users can't see it though as its just a bunch of comments)
* major comment cleanup in BFFrameMakerWiki class, BFStripAnimator class; reflected applicable changes from FrameMaker (new file finding method, frame deletion)

## June 26, 2016
* created method to find CGS files in a folder.

## June 22, 2016 
* added version number to opening sequence of menu class
* added some comments and cleaned up some code of the menu class to make things more legible
* major comment cleanup in BFFrameMaker class; will reflect changes to other frame/strip making classes once I confirm that it's still working properly

## June 12, 2016
* v1.1 Release