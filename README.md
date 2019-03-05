# JavaScouting2
New, updated FTC app!
We plan on modernizing and redoing the old JavaScouting app.
Things which will be added:
- Scouting form and user interface
- Schedule form and user interface
- Match prediction and machine-learning generated alliance picker.

# Essay on the functionality of the Java Scouting App

With the release of the Rover Ruckus season, we hoped to continue our app from last year - updating its functionality and aesthetics to match our newfound knowledge of Android app programming. The app last year was hacked together - a side project of mine which grew and grew and grew until it actually got released onto the Google Play Store last year. I really had no motivation to continue the app, it being such a mess, and so I decided to hold off on it.

## On Tensorflow Models

When Keegan, a fellow team member received and began using an Orange Alliance api key, I refound my inspiration. I wanted to scrape The Orange Alliance and use a machine learning algorithm to be able the predict the best teams, in order to help with alliance selection. To do this, I learned Python and designed a script to scrape, merge, and train a TensorFlow Keras model on TOA data.
1. The scrape runs as expected, pulling match data for all 15000-so teams in the OA database.
2. The predictor is a simple Keras model. It takes 5 input nodes - Avg Auto Score, Avg Tele Score, Avg End Score, Avg Score, and Standard Deviation. It then outputs one node, a "KDR" or "Win/Loss" ratio. It was trained on the scraped data for 512 epochs running 256 cycles each. The best model is running the "Adam" optimizer and using mean-squared-error as its conditional.
(Details can be found on keras.io)
3. The model was then exported as a tflite file, which can be used on most mobile devices quickly and resource-unintensive.

## Basic App Functionality

The app is designed to be used during a competition. The way it is set up allows the user to achieve 


## On A Redesigned App

During the meantime, I felt the need to do a complete overhaul of the app. Previously, the app system was based off a numerous amount of "Activities", screens on which a certain layout is presented to the user. However, Activities are not meant to be hot-swapped for every UI event, from adding teams and removing teams. So, I learned and implemented the old activities as "Fragments", small containers for data which can easily be swapped.

With a clear end goal for the app in mind, I redesigned the user's navigation system to, instead of using a "Drawer", use a Bottom Navigation Bar. This allows for the three main sections, the "Scouting", the "Schedule", and the "Analysis" to be easily viewable and selectable by the user.

pic1

