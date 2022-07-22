<h1>Location Reminder</h1>
<h5>Note: This project constitutes Udacity's Android Kotlin Developer Nano-degree Program</h5>

<h3>Description</h3>

Do you recall the moment you walked past a local grocery store, and forgot to grab the milk? Not anymore! 

Location Reminder App aims to address exactly that - where user can select a point-of-interest(POI) on the map, and set for themselves a to-do reminder. When the user enters the [geofence](https://developer.android.com/training/location/geofencing) (set to: 100 meters) of the POI, the app triggers a notification. 

<h3>Topics/Skills covered as part of this project</h3>
The aim of this project is to showcase the following skills:

- [Firebase Authentication](https://firebase.google.com/docs/auth) API Integration
- [Permissions](https://developer.android.com/guide/topics/permissions/overview) ([Foreground/Background](https://developer.android.com/training/location/permissions))
- [Geofencing](https://developer.android.com/training/location/geofencing)
- [Google Maps](https://developers.google.com/android/reference/com/google/android/gms/maps/MapFragment) API Integration
- Testing:
    - __Unit Tests:__ ViewModel/LiveData, Test Double (Fake), Coroutines (see [ReminderListViewModelTest](https://github.com/azzumw/Project4/blob/master/app/src/test/java/com/udacity/project4/locationreminders/reminderslist/RemindersListViewModelTest.kt) and [SaveReminderViewModelTest](https://github.com/azzumw/Project4/blob/master/app/src/test/java/com/udacity/project4/locationreminders/savereminder/SaveReminderViewModelTest.kt)), and Room (see [ReminderDaoTest](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/locationreminders/data/local/RemindersDaoTest.kt))
    - __Integration Tests:__ Repository-Database (see [RemindersLocalRepositoryTest](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/locationreminders/data/local/RemindersLocalRepositoryTest.kt)), UI Controller-Repository (see [ReminderListFragmentTest](https://github.com/azzumw/Project4/blob/master/app/src/androidTest/java/com/udacity/project4/locationreminders/reminderslist/ReminderListFragmentTest.kt))
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


