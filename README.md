[![](https://jitpack.io/v/emircanpekyurek/PaginationRecyclerView.svg)](https://jitpack.io/#emircanpekyurek/PaginationRecyclerView)

## Installation

(app) build.gradle:
```gradle
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3' // or different version (for collect)
implementation 'com.github.emircanpekyurek:PaginationRecyclerView:1.0.0'
```
#### AND

(root) build.gradle:
```gradle
allprojects {
    repositories {
        ..
        maven { url "https://jitpack.io" }
    }
}
```
or settings.gradle:
```gradle
dependencyResolutionManagement {
    ..
    repositories {
        ..
        maven { url 'https://jitpack.io' }
    }
}
```

## Usage
Add the PaginationRecyclerView to your design file as below and enter the first page number (default value = null) and pagination offset (default value = 0):
```xml
<com.pekyurek.emircan.pagination.PaginationRecyclerView
    android:id="@+id/recyclerView"
    app:startPageIndex="1"
    app:paginationOffset="10"
    ...
/>
```
for the refresh:
```kotlin
recyclerView.resetPagination()
```

for the error:
```kotlin
recyclerView.setError("error text")
//or
recyclerView.setError(R.string.error_text)
```

You can listen to the changes as follows:
```kotlin
lifecycleScope.launch {
    recyclerView.paginationFlow.collect { paginationStatus ->
        when (paginationStatus) {
            is PaginationStatus.Success -> {
                val pageNumber = paginationStatus.page
                //TODO request this page
            }
            is PaginationStatus.Reset -> {
                //TODO clear list and request first page
            }
            is PaginationStatus.Error -> {
                val errorDescription = paginationStatus.errorDescription
                //TODO error handler
            }
        }
    }
}
```
