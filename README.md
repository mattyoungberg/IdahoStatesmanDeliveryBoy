# Idaho Statesman Delivery Boy
#### Download a free PDF version of today's Idaho Statesman with your Boise Library card, made possible by their relationship with NewsBank.

---
##Readme Contents
* [The Story](#the-story)
* [Usage](#usage)

---
## The Story

##### Here's the thing: _I don't have any social media accounts._

I deleted them months ago after a long-time struggle with the effect that social media was having on me and on our society as a whole. I would come out of a 20-minute scrolling session feeling hostile toward and frustrated with people holding different viewpoints than myself. I had long understood that machine learning A.I. determined what kind of paid media and news came onto your newsfeed: the stuff you're most likely to click and engage with. The consequence, of course, was that social media became a place where I was unlikely to encounter substantial news and coverage that could change my point of view. It became an echo chamber that was only good at generating feelings of self-righteous indignation in me and condescension toward everyone else.

But I didn't want to just cut news out of my life. I still wanted to know what was going on in the world around me. So, I had to find a source of media that couldn't profile and feed me content that I already agreed with. The answer was simple: **Newspapers**.

A Sunday paper wasn't constant enough, but any other schedule for newspaper delivery was too costly for my budget. Naturally, I started looking through the resources of the Boise Library. I found that they had a relationship with NewsBank that allowed anyone with a Boise library card to get access to any day of the Idaho Statesman from 2017 all the way to __today's paper__. I had found my solution.

As I started reading the paper every day on NewsBank's website, I struggled with its presentation. The issue wasn't presented as a whole, but rather page-by-page. Not only that, but each page had apparently been broken down; The UI wouldn't load a single PDF or image, but it would rather piece together 24 JPG files, resulting often in blurred text where the lines would meet. Add to that the loading of the surrounding page and interface, and this monster of a process would result in a single page taking 20-30 seconds to load on my 100mbps connection! As time passed, I got increasingly frustrated with the long load times since they especially interrupted my ability to read front-page stories (which are almost always split across multiple pages).

I decided that consumption of the newspaper would be much more efficient if I could simply get it in a PDF format. NewsBank's interface only allowed download of a single page at a time, but digging around in all the of the supporting HTML and JavaScript revealed URLs that could serve the PDF untouched.

Hence, the motive for this project was born. After pouring through mountains of web requests to understand NewsBank's back-end structure, the result is the repository before you that allows you to download the paper today in an easily consumed PDF format.

Enjoy.

---
## Usage


The JAR file works as a Command Line Interface, and, in it's simplest form, can be used in the following manner to download today's paper:

```> java -jar /path/to/jar -i 12345678901234```

Here's a list of all the possible command line arguments:
* `-i` __Required.__ A valid, 14-digit Boise library card ID number.
* `-d` The date of the issue to request (yyyyMMdd); Defaults to today.
* `-f` A custom name for the output file; Defaults to IdahoStatesman_yyyyMMdd
* `-t` The target directory at which to save the PDF output; Defaults to the current working directory.
* `-h` Prints a help message.

---

Feel free to contribute to the repository through pull requests, submitting issues, and/or the like! I appreciate any and all contributions.

-Matt Youngberg
