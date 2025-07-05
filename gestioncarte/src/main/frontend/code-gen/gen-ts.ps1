& "C:\Program Files\Eclipse Adoptium\jdk-11.0.13.8-hotspot\bin\java" --illegal-access=permit -jar swagger-codegen-cli.jar generate -i http://localhost:8080/v3/api-docs -l typescript-fetch -o "./models"

$PSDefaultParameterValues['Out-File:Encoding'] = 'utf8'
$file = "..\src\types\dtos.ts"

$value = Get-Content .\models\api.ts
$value = $value | Select-Object -skip 79 
$ln = $value | Select-String "config"
$ln = $ln.LineNumber | Select-Object -first 1
$ln = ($ln - 5)
$value = $value | Select-Object -first $ln
$value = $value -replace '[A-z]+.(Ignored|Pattern)?Localization(s?)Enum', "LocalizationCode"
$value = $value -replace '[A-z]+.(Ignored|Pattern)?Tcg(s?)Enum', "TradingCardGame"
$value = $value -replace '[A-z]+.FoilEnum', "Foil"
$value = $value -replace ' CardTagDTO.TypeEnum',' string'
$value = $value -replace ' [A-z]+\.BulbapediaStatusEnum',' BulbapediaExtractionStatus'
$value = $value -replace '\s*\/\*\*'.trim() -ne ""
$value = $value -replace '\s* \*.*'.trim() -ne ""
$value = $value -replace '\s* \*.*/'.trim() -ne ""
$value = $value -replace 'translations: { \[key: string\]: { \[key: string\]: PokemonCardTranslationDTO; }; };', "translations: { [key in LocalizationCode]?: { [key: string]: PokemonCardTranslationDTO; }; };"
$value = $value -replace 'wikiUrls: { \[key: string\]: Array<WikiUrlDTO>; };', "wikiUrls: { [key in LocalizationCode]?: WikiUrlDTO[]; };"
$value = $value -replace 'Array<([^>]+)>', '$1[]'

$newValue = ""
[bool] $inNamespace = $false
foreach ($line in $value) {
    if (($line -like "*namespace*") -or ($line -like "*RevisionDTO*") -or ($line -like "*HistoryTreeDTO*") -or ($line -like "*export interface PokemonCardParentIdDTO*") -or ($line -like "*interface Ulid*") -or ($line -like "*export interface SortObject*") -or ($line -like "*export interface Page*")) {
        $inNamespace = $true
    }
    if ((($line -like "*translations*") -or ($line -like "*bulbapediaLinks*")) -and (-not ($line -like "*key in LocalizationCode*"))) {
        $line = $line -replace '\[key: string\]','[key in LocalizationCode]?'
    }
    if (-not $inNamespace) {
        $newValue += $line + "`r"
    }
    if ($inNamespace -and ($line -match "^}$")) {
        $inNamespace = $false
    }
}
$value = $newValue

$value = $value -replace 'Ulid', "string"
$value = $value -replace 'PokemonCardParentIdDTO', "string | number"
$value = $value -replace ' { \[key in LocalizationCode\]\?: ([A-z]+); };', ' Translations<$1>;'

Write-Output "/// /!\ Generated file do not modify /!\" > $file
Write-Output "" >> $file
Write-Output "import { BulbapediaExtractionStatus } from '@/types';" >> $file
Write-Output "import { TradingCardGame } from '@/tcg';" >> $file
Write-Output "import { LocalizationCode, Translations } from '@/localization';" >> $file
Write-Output "import { Foil } from '@components/cards/foil';" >> $file
Write-Output "" >> $file
$value >> $file

Remove-Item models/* -Recurse
