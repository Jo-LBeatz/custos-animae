import re

with open("app/src/main/java/com/example/ui/MainViewModel.kt", "r") as f:
    content = f.read()

target = "    val isLatinMode: StateFlow<Boolean> = _isLatinMode.asStateFlow()"
insertion = """
    // Persisted Manual Devotional Intention
    private val _manualDevotionalIntention = MutableStateFlow(sharedPrefs.getString("manual_devotional_intention", "") ?: "")
    val manualDevotionalIntention: StateFlow<String> = _manualDevotionalIntention.asStateFlow()

    fun updateManualDevotionalIntention(intention: String) {
        viewModelScope.launch {
            _manualDevotionalIntention.value = intention
            sharedPrefs.edit().putString("manual_devotional_intention", intention).apply()
        }
    }
"""

if "manualDevotionalIntention" not in content:
    content = content.replace(target, target + "\n" + insertion)
    with open("app/src/main/java/com/example/ui/MainViewModel.kt", "w") as f:
        f.write(content)
    print("Patched MainViewModel.")
else:
    print("Already patched.")
