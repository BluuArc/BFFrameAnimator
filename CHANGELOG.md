# Working Change Log (To be put into release notes when ready)

## July 15, 2016
* fixed bug where wiki GIFs created from animation strips weren't the correct height (minimum 140px)
* fix bug in StripMaker where deleting frames message didn't output properly
* fixed typos in StripMaker (messages now say strips instead of GIFs) 
* add proper program termination from the animation menu (so JVM actually exits instead of returning)
* fixed begin/end message issues when running classes 


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