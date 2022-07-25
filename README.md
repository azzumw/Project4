<h1>Location Reminder</h1>
<h5>Note: This project constitutes Udacity's Android Kotlin Developer Nano-degree Program</h5>

<h3>Description</h3>
Don’t you just hate it when you’ve walked past the local grocery store, and forgot to grab the milk? Not anymore!

Location Reminder App (LRA) will make forgetting the milk (and anything else) a thing of the past! LRA allows you  to select a point-of-interest(POI) on the map, and set for yourself a to-do reminder. With LRA in your pocket, now when you’re within a 100 metres (default geofence app setting of 100 meters) of the POI (or the grocery shop) you’ll get a notification reminding you to grab that milk - you’ll never have to endure a black coffee or dry cereal ever again.

<h3>Topics/Skills covered as part of this project</h3>
The aim of this project is to showcase the following skills:

- [Firebase Authentication](https://firebase.google.com/docs/auth) API Integration
- [Permissions](https://developer.android.com/guide/topics/permissions/overview) ([Foreground/Background](https://developer.android.com/training/location/permissions))
- [Geofencing](https://developer.android.com/training/location/geofencing)
- [Google Maps](https://developers.google.com/android/reference/com/google/android/gms/maps/MapFragment) API Integration
- Testing:
    - __Unit Tests:__ ViewModel/LiveData, Test Double (Fake), Coroutines (see [ReminderListViewModelTest](https://github.com/azzumw/Project4/blob/master/app/src/test/java/com/udacity/project4/locationreminders/reminderslist/RemindersListViewModelTest.kt) and [SaveReminderViewModelTest](https://github.com/azzumw/Project4/blob/master/app/src/test/java/com/udacity/project4/locationreminders/savereminder/SaveReminderViewModelTest.kt)), and Room (see [ReminderDaoTest](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/locationreminders/data/local/RemindersDaoTest.kt))
    - __Integration Tests:__ Repository-Database (see [RemindersLocalRepositoryTest](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/locationreminders/data/local/RemindersLocalRepositoryTest.kt)), UI Controller-ViewModel (see [ReminderListFragmentTest](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/locationreminders/reminderslist/ReminderListFragmentTest.kt))
    - __Instrumentation(UI/E2E):__ Fragment Test (see [SaveReminderFragmentTests](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/locationreminders/SaveReminderFragmentTests.kt)), Acitivity Test (see [RemindersActivityTest](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/RemindersActivityTest.kt)), Application Navigation (see [AppNavigation](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/AppNavigation.kt))

    Note: The app makes extensive use of asynchronous calls to the local database, and [Data Binding Library](https://developer.android.com/topic/libraries/data-binding) to bind UI with ViewModels, hence 
    _IdlingResource_, and _DataBindingIdlingResource_ have been registered within the tests, respectively. 

```kotlin
private val dataBindingIdlingResource = DataBindingIdlingResource()


    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }
    
```


