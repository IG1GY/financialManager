# financialManager
this is a tool to manage finances. it works like this:

each financial source is given to the tool, and it keeps track of them.
the financial development\hamperment is displayed via charts and graphes.

and... yeah, that's it.
the problem is defining the app, designing it, defining safe means of communication and interaction.
also, there are personal added features which I'll personally use. 
a chart and a tasklist for managing the progress of projects. ideally, 
each piece in the chart will have it's own tasklist, and owners. 
add git to that annnnnnnndd..... you've got a cool project manager for administrators. 
for now it's just a financialManager though. once I start working on the rest of the features it'll be a "progress manager"

TODO:
*) right now I'm adding the necessary javafx modules directly when calling javac. however, I would like to find a wae so it'll be permanent

*) for some reason "import Alert" doesn't work. had to create a private class. need to find a wae to fix that.

*) I'm using atom and ide-java for this. however, I don't know the wae to make ide-java to recognize javafx. this causes issues.

*) there's a NullPointerException I need to handle when calling the "financial report" button. fix that --urgent
