# 修复图标文件中的渐变背景问题
$iconFiles = Get-ChildItem -Path "app\src\main\res" -Recurse -Filter "*.xml" | Where-Object { $_.Name -like "*launcher*" }

foreach ($file in $iconFiles) {
    $content = Get-Content $file.FullName -Raw
    
    # 修正渐变背景部分的语法错误
    $pattern = '(?s)<path\s+android:fillColor="#0A0A0A"\s+android:pathData="M0,0h\d+v\d+h-\d+z"/>\s*<path\s+android:pathData="M0,0h\d+v\d+h-\d+z"\s+android:fillColor="#1A1A2E">\s*<aapt:attr name="android:fillColor">'
    $replacement = '<path android:pathData="M0,0h{0}v{0}h-{0}z"><aapt:attr name="android:fillColor">'
    
    # 根据不同分辨率设置不同的尺寸
    if ($file.Name -like "*mdpi*") {
        $size = "48"
    } elseif ($file.Name -like "*hdpi*") {
        $size = "72"
    } elseif ($file.Name -like "*xhdpi*") {
        $size = "96"
    } elseif ($file.Name -like "*xxhdpi*") {
        $size = "144"
    } elseif ($file.Name -like "*xxxhdpi*") {
        $size = "192"
    } else {
        $size = "108"  # drawable folder
    }
    
    $fixedReplacement = $replacement -f $size
    
    if ($content -match $pattern) {
        $content = $content -replace $pattern, $fixedReplacement
        $content = $content -replace '</gradient>\s*</aapt:attr>\s*</path>\s*</vector>', "</gradient></aapt:attr></path></vector>"
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        Write-Output "已修复: $($file.Name)"
    }
}

Write-Output "所有图标文件修复完成"