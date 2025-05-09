$baseUrl = "https://raw.githubusercontent.com/google/material-design-icons/master/png/action/settings/materialicons/48dp/1x/baseline_settings_black_48dp.png"
$hdpiPath = "app\src\main\res\mipmap-hdpi\ic_launcher.png"
$mdpiPath = "app\src\main\res\mipmap-mdpi\ic_launcher.png"
$xhdpiPath = "app\src\main\res\mipmap-xhdpi\ic_launcher.png"
$xxhdpiPath = "app\src\main\res\mipmap-xxhdpi\ic_launcher.png"
$xxxhdpiPath = "app\src\main\res\mipmap-xxxhdpi\ic_launcher.png"

$hdpiRoundPath = "app\src\main\res\mipmap-hdpi\ic_launcher_round.png"
$mdpiRoundPath = "app\src\main\res\mipmap-mdpi\ic_launcher_round.png"
$xhdpiRoundPath = "app\src\main\res\mipmap-xhdpi\ic_launcher_round.png"
$xxhdpiRoundPath = "app\src\main\res\mipmap-xxhdpi\ic_launcher_round.png"
$xxxhdpiRoundPath = "app\src\main\res\mipmap-xxxhdpi\ic_launcher_round.png"

Write-Host "Downloading placeholder icons..."
Invoke-WebRequest -Uri $baseUrl -OutFile $hdpiPath
Invoke-WebRequest -Uri $baseUrl -OutFile $mdpiPath
Invoke-WebRequest -Uri $baseUrl -OutFile $xhdpiPath
Invoke-WebRequest -Uri $baseUrl -OutFile $xxhdpiPath
Invoke-WebRequest -Uri $baseUrl -OutFile $xxxhdpiPath

# Copy the same icon for round versions
Copy-Item -Path $hdpiPath -Destination $hdpiRoundPath
Copy-Item -Path $mdpiPath -Destination $mdpiRoundPath
Copy-Item -Path $xhdpiPath -Destination $xhdpiRoundPath
Copy-Item -Path $xxhdpiPath -Destination $xxhdpiRoundPath
Copy-Item -Path $xxxhdpiPath -Destination $xxxhdpiRoundPath

Write-Host "Icon files created successfully!" 