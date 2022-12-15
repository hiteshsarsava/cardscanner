# Set Up

<b>1. Add it in your project level build.gradle at the end of repositories:</b>

```
allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
  ```      
  
  <b>2. Add the dependency:</b>
  
  ```  
  dependencies {
	        implementation 'com.github.hiteshsarsava:cardscanner:${latestVersion}' //e.g., v0.0.1
	}
  ```
  
  <b>latestVersion</b> : [![](https://jitpack.io/v/hiteshsarsava/cardscanner.svg)](https://jitpack.io/#hiteshsarsava/cardscanner)
  
  # How to use it
  
  <b>1. Prepare activity result:</b>

  ```
  private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent = result.data ?: return@registerForActivityResult
                val cardDetails: CardDetails =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data.getSerializableExtra(
                            ScannerActivity.CARD_DETAILS,
                            CardDetails::class.java
                        ) as CardDetails
                    } else {
                        data.getSerializableExtra(ScannerActivity.CARD_DETAILS) as CardDetails
                    }
                setCardData(cardDetails)
            }
        }
        
   ```

  <b>2. Launch Activity:</b>
  
  ```
  resultLauncher.launch(Intent(this@MainActivity, ScannerActivity::class.java))
  
  ```
