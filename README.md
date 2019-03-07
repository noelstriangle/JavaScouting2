# JavaScouting2
New, updated FTC app!
We plan on modernizing and redoing the old JavaScouting app.
Things which will be added:
- Scouting form and user interface
- Schedule form and user interface
- Match prediction and machine-learning generated alliance picker.

# Essay on the functionality of the Java Scouting App

The JavaScouting app is designed to help teams perform better at their tournaments, by allowing data collection and analysis to help strategize.

With the release of the Rover Ruckus season, we hoped to continue our app from last year - updating its functionality and aesthetics to match our newfound knowledge of Android app programming. The app last year was hacked together - a side project of mine which grew and grew and grew until it actually got released onto the Google Play Store last year. I really had no motivation to continue the app, it being such a mess, and so I decided to hold off on it. At the end of last year, the app had the following functions.

---

## Previous App Functionality (Version 1)

The app is designed to be used during a competition. Functionality based on activity-switching. Only allowed scouting and using the scouting predictions to predict the match results.

### Scouting

The main point of the app is to be an easy way for teams to scout other teams. On the main screen, a user can decide to scout a team. When the "add" button is clicked, it brings up an activity which contains questions about the other team's abilities. When the user finishes updating the team, the team's information is stored in a local database. The app returns to the main screen, and the user can see the team they added, in a list. This process can be repeated as nessecary until all teams are scouted. The user can also take a picture.

If the user wants to see the details on the teams they added, they can click on a team's list item. This will bring up a screen detailing the abilities of the other team: in sentence form. In addition, the picture (if present) was displayed. From this screen, the user had the option to delete the team selected, or to edit the team's information.

The old way:
![](/pics/pic1.png)

On the main screen, the user could also choose to delete all the teams currently in the database, or export the information into a .csv file, which could be viewed from Google Sheets or other excel-like program, and shared with the team (or competition).

### Matches

The app's secondary feature was that of match "prediction". This consisted mainly of describing which scoring features each team on each alliance could do. Not very advanced. We never used this feature in a competition.

---

## New App Functionality (Version 2)

### Changes from version 1

During the meantime, I felt the need to do a complete overhaul of the app. Previously, the app system was based off a numerous amount of "Activities", screens on which a certain layout is presented to the user. However, Activities are not meant to be hot-swapped for every UI event, from adding teams and removing teams. So, I learned and implemented the old activities as "Fragments", small containers for data which can easily be swapped.

With a clear end goal for the app in mind, I redesigned the user's navigation system to, instead of using a "Drawer", use a Bottom Navigation Bar. This allows for the three main sections, the "Scouting", the "Schedule", and the "Analysis" to be easily viewable and selectable by the user. In addition, certain UI elements were modernized.

### New functionality

One major changes is using more data collection to make even better analysis. Instead of just using what teams say they could do, we now use what the team can actually do. We had to decide on what data would be easiest for a team to collect. Obviously, the data we used last year (just scouting data) is extremely easy to collect, so that stays in. The autonomous, teleop, and endgame scores are also relatively accessible: have one person keep an eye on the match result screen and take a picture of it. The most detailed data is really hard to acquire: they require a team of people to watch every robot on every match and detail what it could do. I figured the most useful data to add would be the match results.

![](/pics/pic2.jpg)

With this data, for every match a team participates in, the team's average autonomous, teleop, and endgame scores are updated. 

```java
//add the standard deviation to the current average standard deviation( new total point data - 
                                                                      old average point data)
if (!match.updated) {
  r1.standardDeviation = r1.standardDeviation + ((t0[0] / 2) + (t0[1] / 2) + (t0[2] / 2)) - 
                                                (r1.autoPoints + r1.telePoints + r1.endPoints);
  r1.autoPoints = (r1.autoPoints + t0[0]) / 2;
  r1.telePoints = (r1.telePoints + t0[1]) / 2;
  r1.endPoints = (r1.endPoints + t0[2]) / 2;

  r2.standardDeviation = r2.standardDeviation + ((t0[0] / 2) + (t0[1] / 2) + (t0[2] / 2)) - 
                                                (r2.autoPoints + r2.telePoints + r2.endPoints);
  r2.autoPoints = (r2.autoPoints + t0[0]) / 2;
  r2.telePoints = (r2.telePoints + t0[1]) / 2;
  r2.endPoints = (r2.endPoints + t0[2]) / 2;  

  b1.standardDeviation = b1.standardDeviation + ((t0[3] / 2) + (t0[4] / 2) + (t0[5] / 2)) - 
                                                (b1.autoPoints + b1.telePoints + b1.endPoints);
  b1.autoPoints = (b1.autoPoints + t0[3]) / 2;
  b1.telePoints = (b1.telePoints + t0[4]) / 2;
  b1.endPoints = (b1.endPoints + t0[5]) / 2;  

  b2.standardDeviation = b2.standardDeviation + ((t0[3] / 2) + (t0[4] / 2) + (t0[5] / 2)) - 
                                                (b2.autoPoints + b2.telePoints + b2.endPoints);
  b2.autoPoints = (b2.autoPoints + t0[3]) / 2;
  b2.telePoints = (b2.telePoints + t0[4]) / 2;
  b2.endPoints = (b2.endPoints + t0[5]) / 2;
  match.updated = true;
}
```

We also wanted to help out teams by analysing the data more efficiently. Instead of just providing the information to the user, actively use it to make predictions about winners, and about the best teams. I've always been interested in machine learning, so I tried it.

#### On Tensorflow Models

When Keegan, a fellow team member received and began using an Orange Alliance api key, I refound my inspiration. I wanted to scrape The Orange Alliance and use a machine learning algorithm to be able the predict the best teams, in order to help with alliance selection. To do this, I learned Python and designed a script to scrape, merge, and train a TensorFlow Keras model on TOA data.
1. The scrape runs as expected, pulling match data for all 15000-so teams in the OA database.
2. The predictor is a simple Keras model. It takes 5 input nodes - Avg Auto Score, Avg Tele Score, Avg End Score, Avg Score, and Standard Deviation. It then outputs one node, a "KDR" or "Win/Loss" ratio. It was trained on the scraped data for 512 epochs running 256 cycles each. The best model is running the "Adam" optimizer and using mean-squared-error as its conditional.
(Details can be found on keras.io)

![](/pics/pic3.png)

3. The model was then exported as a tflite file, which can be used on most mobile devices quickly and resource-unintensive.

The tflite model is now in the app, and used in the following way:

```java
if (teams.size() != 0) {

  try {
    tflite = new Interpreter(loadModelFile(a));
    //loadModelFile simply converts the binary tflite file to an object to be used.
  } catch (IOException e) {
    //TODO: toast the user
  }


  allAnalysis = new ArrayList<>();

  for (Team team : teams) {
    //Loop through all teams

    float[][] o = new float[1][1];
    o[0][0] = 0;

    float[] input = new float[]{team.autoPoints, 
                                team.telePoints,
                                team.endPoints, 
                                team.autoPoints + team.telePoints + team.endPoints, 
                                team.standardDeviation};
                               
    //As described above, the input of the model is 5 data points.
    //the double array "o" is the ouput: a win/loss float
                               
    tflite.run(input, o);
    Log.d("USER", "prediction for " + String.valueOf(team.teamNumber) + " = " + String.valueOf(o[0][0]));

    allAnalysis.add(new Ana(team.teamNumber, o[0][0]));
    //add the prediction to the list of predictions for all teams

  }

  tflite.close();

}
```

UI:
![](/pics/pic4.jpg)

#### Importing and Exporting

The previous year, we did have an exporting feature. However, this proved to be mostly pointless. This year, I decided to add an import feature. This way, a team member who does the scouting can collaborate with a team member who records match data.

I still wanted to use the .csv file format which is used last year. So, I used a Java library called OpenCSV to add the import feature.

The user is prompted to choose a text file.

![](/pics/pic5.jpg)

The URI, or file location of the csv is parsed into memory with the following code:

```java
//open the file
InputStream fileInputStream = Objects.requireNonNull(getContext()).getContentResolver().openInputStream(input);
//use opencsv to parse the file, creating a list of "records"
CSVParser parser = CSVParser.parse(Objects.requireNonNull(fileInputStream), Charset.defaultCharset(), CSVFormat.DEFAULT);
List<CSVRecord> toAdd = parser.getRecords();
final List<Team> toPush = new ArrayList<>();
//for every team, add the data from its record to a "Team" object, and add it to a list, to be pushed later
for (int i = 1; i < toAdd.size(); i++) {

  Team t = new Team();
  CSVRecord curr = toAdd.get(i);
  t.id = Integer.valueOf(curr.get(0).trim());
  t.teamName = curr.get(1).trim();
  t.teamNumber = Integer.valueOf(curr.get(2).trim());
  t.canLand = Boolean.valueOf(curr.get(3).trim());
  t.canSample = Boolean.valueOf(curr.get(4).trim());
  t.canClaim = Boolean.valueOf(curr.get(5).trim());
  t.canPark = Boolean.valueOf(curr.get(6).trim());
  t.depotMinerals = Integer.valueOf(curr.get(7).trim());
  t.landerMinerals = Integer.valueOf(curr.get(8).trim());
  t.canLatch = Boolean.valueOf(curr.get(9).trim());
  t.canEndPark = Boolean.valueOf(curr.get(10).trim());
  t.autoPoints = Integer.valueOf(curr.get(11).trim());
  t.telePoints = Integer.valueOf(curr.get(12).trim());
  t.endPoints = Integer.valueOf(curr.get(13).trim());
  t.standardDeviation = Integer.valueOf(curr.get(14).trim());
  toPush.add(t);

}

```

#### Minor changes

Instead of displaying data in sentences (shown in an above pic), we now use charts.

Team data:
![](/pics/pic6.jpg)

Match data:
![](/pics/pic7.jpg)

---

## Questions? See the JavaScouts in the pit, or contact us at javascouts@gmail.com. Follow the development of the app at github.com/noelstriangle/JavaScouting2 and keep an eye on the Google Play Store
