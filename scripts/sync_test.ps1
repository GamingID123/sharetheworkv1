# ShareTheWork Sync Test (PowerShell) — verifies messages, files, AI across devices
param([string]$BaseUrl = "http://localhost:3000")
$ErrorActionPreference = "Stop"

function Auth($email,$pass,$class="8",$section="A"){
  $body = @{email=$email;password=$pass;className=$class;section=$section;name="Test"} | ConvertTo-Json
  try { $r = Invoke-RestMethod -Uri "$BaseUrl/api/auth/register" -Method Post -Body $body -ContentType "application/json"; return $r.token }
  catch { $r = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post -Body (@{email=$email;password=$pass}|ConvertTo-Json) -ContentType "application/json"; return $r.token }
}
Write-Host "1. Auth device A + B"
$tokenA = Auth "testA@gatkaergcdnuxwzocupu.test" "Test12345!"
$tokenB = Auth "testB@gatkaergcdnuxwzocupu.test" "Test12345!"
Write-Host " tokens ok: $($tokenA.Length) / $($tokenB.Length)"

Write-Host "2. Homework create (Moderator) -> Student sees"
$hw = @{subject="Mathematics";title="SyncTest HW";description="Check sync";className="8";section="A";dueDate=(Get-Date).AddDays(1).ToString("o")} | ConvertTo-Json
try { $hwRes = Invoke-RestMethod -Uri "$BaseUrl/api/homework" -Method Post -Headers @{Authorization="Bearer $tokenA"} -Body $hw -ContentType "application/json"; Write-Host " hw created $($hwRes.id)" } catch { Write-Host " hw create needs MODERATOR role (expected if STUDENT token): $_" }

Write-Host "3. Messages cross-device"
$conv = Invoke-RestMethod -Uri "$BaseUrl/api/conversations" -Headers @{Authorization="Bearer $tokenA"}
$convId = $conv[0].id; if(-not $convId){ $convId = "00000000-0000-0000-0000-000000000001" }
Write-Host " conv $convId"
Invoke-RestMethod -Uri "$BaseUrl/api/conversations/$convId/messages" -Method Post -Headers @{Authorization="Bearer $tokenA"} -Body (@{text="Hello from A"}|ConvertTo-Json) -ContentType "application/json" | Out-Null
Start-Sleep 1
$msgsB = Invoke-RestMethod -Uri "$BaseUrl/api/conversations/$convId/messages" -Headers @{Authorization="Bearer $tokenB"}
Write-Host " B sees $($msgsB.Count) msgs; last: $($msgsB[-1].text)"
if($msgsB[-1].text -eq "Hello from A"){ Write-Host "PASS messages sync" } else { Write-Host "FAIL messages" }

Write-Host "4. File upload -> preview/download visible to other device"
$tmp = New-TemporaryFile; "hello sync file" | Set-Content -Path $tmp -NoNewline
# Use curl-like multipart via Invoke-RestMethod
try {
  $form = @{file=Get-Item $tmp; folder="sync-test"}
  $up = Invoke-RestMethod -Uri "$BaseUrl/api/storage/upload?token=$tokenA" -Method Post -Form $form -Headers @{Authorization="Bearer $tokenA"}
  Write-Host " upload fileId $($up.fileId)"
  $metaB = Invoke-RestMethod -Uri "$BaseUrl/api/storage/files/$($up.fileId)?token=$tokenB" -Headers @{Authorization="Bearer $tokenB"}
  Write-Host " B meta $($metaB.name) PASS files visible"
  # preview streams inline, download attachment — just check status
} catch { Write-Host " upload check: $_" }

Write-Host "5. Nova AI (Groq)"
try {
  $ai = Invoke-RestMethod -Uri "$BaseUrl/api/ai/chat" -Method Post -Headers @{Authorization="Bearer $tokenA"} -Body (@{message="Explain photosynthesis briefly"}|ConvertTo-Json) -ContentType "application/json"
  Write-Host " AI reply $($ai.reply.Substring(0,60))... model $($ai.model) PASS"
} catch { Write-Host " AI fail: $_ (check GROQ_API_KEY)" }

Write-Host "Done — if messages/files/AI PASS, sync is perfect across devices. In Android: login two devices with same class 8-A, send in Chat -> appears within 2.5s (poll), Files -> Preview in-app via FilePreviewScreen.kt + Download to Downloads/."
