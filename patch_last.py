import re

with open('app/src/main/java/com/example/ui/screens/RuleOfLifeScreen.kt', 'r') as f:
    text = f.read()
    
text = re.sub(
    r'Text\(\s*text\s*=\s*freq,([^()]*?)maxLines\s*=\s*1,\s*softWrap\s*=\s*false\s*\)',
    r'com.example.ui.components.ResponsiveText(text = freq,\1maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)',
    text,
    flags=re.DOTALL
)

with open('app/src/main/java/com/example/ui/screens/RuleOfLifeScreen.kt', 'w') as f:
    f.write(text)

with open('app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt', 'r') as f:
    text = f.read()

text = re.sub(
    r'Text\(\s*text\s*=\s*saint\.location,([^()]*?)maxLines\s*=\s*1,\s*softWrap\s*=\s*false\s*\)',
    r'com.example.ui.components.ResponsiveText(text = saint.location,\1maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)',
    text,
    flags=re.DOTALL
)

with open('app/src/main/java/com/example/ui/screens/MissalAndOfficeScreen.kt', 'w') as f:
    f.write(text)

