# Why Not Be One Item (wnboi)

A minecraft library mod for adding highly customizable wheel based context menus for forge and fabric. The name comes from the idea that this can be used to make bags or other multi-use items with the goal of reducing inventory clutter.

You can customize the menu by extending the given classes and overriding the methods to get the behavior you want. Most features that I expect people would want to customize are split off into their own methods to make it easier.

This is currently intended mostly for use in my own mods, if you'd like to use it I suggest reading through some of its main classes to get a feel for how it works. If you have any problems feel free to open an issue on the github or find me on one of the big modding discords.

There are a few main parts:

## AbstractContextWheelScreen

You'll want to extend this to make your own screen. It has properties that control the general structure of the wheel/menu and methods that work as callbacks for user input and other events. 

## SpokeRenderer

This class is responsible for actually rendering each segment of the wheel. By default this will be a slice of the circle but you can mess with the curve settings and functions to change the shape significantly, or just override the render method to do something entirely different. 

There are also settings to control the color and set an outline and some callbacks to change various settings when the segment is selected/deselected. You can use the label methods to easily display items, entities, or text on the segment.

## KeyboundItem 

This interface is meant to be used on items that should open a context menu when you hold down a key while holding that item and optionally to close the menu when you release the key. 

## DynamicMultiItem (WIP)

This allows an item to work as a holder/passthrough for other items. It does not currently work particularly well and is here more for example than for actual use.

## RenderUtils

These are just some handy rendering functions that I use.

# How to Include

You'll want to use the modrinth maven. Check back in a bit for an example once I figure out the best way to do that.
