# Aplicația de Cântărire - Documentație Completă

## Descriere Generală

Această aplicație Java implementează o interfață de cântărire complet restructurată, organizată pe categorii de produse, conform specificațiilor furnizate.

## Funcționalități Implementate

### 1. Selectare Port Serial
- ✅ Dropdown pentru selectarea portului COM (COM1-COM20)
- ✅ Conectarea automată la port când se apasă "CANTARESTE"
- ✅ Deconectarea automată după cântărire pentru a nu bloca portul

### 2. Afișaj Cântar
- ✅ Afișare valoare în kg (format: X.XXX kg)
- ✅ Păstrarea modului de conversie FLINTAB din aplicația veche
- ✅ Indicare stabilitate prin culoare (verde = stabil, galben = instabil)

### 3. Sistem de Butoane pe Categorii

#### Butoane Principale (verzi cu text alb):
- ✅ FRIGIDERE
- ✅ VITRINE FRIG.
- ✅ DULAPI SPUMATI (ARCTIC)
- ✅ AC
- ✅ DOZATOARE
- ✅ CALORIFERE ELECTRICE

#### Butoane Secundare (apar când e selectat butonul principal):

**FRIGIDERE:**
- ✅ Echipamente frigorifice (3011.34898)
- ✅ Echipamente frigorifice fara motor (3011.34899)
- ✅ Frigidere amoniac (3011.34905)

**VITRINE FRIG.:**
- ✅ Vitrine frigorifice (3011.34896)
- ✅ vitrine frigorifice fara motor (3011.34897)
- ✅ Alte aparate mari utiliz pt refrigerare (3011.29822)

**DULAPI SPUMATI (ARCTIC):**
- ✅ DULAPI SPUMATI FS 390 (3011.31482)
- ✅ DULAPI SPUMATI CF (3011.31484)
- ✅ USI SPUMATE FS+CF (3011.31485)

**AC:**
- ✅ Echipamente de aer conditionat (3011.34907)
- ✅ Echipamente de aer conditionat ctg B (3011.38199)

**DOZATOARE:**
- ✅ Distribuitoare cu agent frigorific (3011.34908)

**CALORIFERE ELECTRICE:**
- ✅ Alte echipamente de transfer termic (3011.39010)
- ✅ aparate electrice de incalzit (3011.30125)
- ✅ Aparate electrice de incalzit si boilere (3011.34906)

### 4. Buton CANTARESTE (roșu)
- ✅ Înregistrează cântărirea în CSV organizat pe schimb și dată
- ✅ Salvează cu codul produsului selectat
- ✅ Format CSV: categorii pe verticală, independente, cu coloană total

### 5. Design Interfață
- ✅ Fundal galben
- ✅ Butoane principale: verzi cu text alb
- ✅ Buton CANTARESTE: roșu
- ✅ Layout clar și intuitiv

### 6. Logica de Cântărire
- ✅ Păstrarea algoritmului de detectare stabilitate din aplicația veche
- ✅ Folosirea StableWeightTracker și FlintabScale din clasele noi
- ✅ Păstrarea logicii de parsare pentru FLINTAB BX21

### 7. Salvare Date
- ✅ CSV organizat pe luni românești și schimburi
- ✅ Fiecare categorie să aibă propria secțiune în CSV
- ✅ Coloană total pentru fiecare categorie
- ✅ Timestamp: 2025-08-05 08:24:07 UTC
- ✅ User: Vlad2321334

## Structura Fișierelor

### Fișiere Implementate:

1. **WeightDataCollectorNew.java** - Clasa principală cu noua interfață
2. **ProductCategory.java** - Enum pentru categorii și coduri
3. **CsvWriter.java** - Clasa pentru salvarea organizată în CSV
4. **StableWeightTracker.java** - Detectarea stabilității greutății
5. **FlintabScale.java** - Comunicarea cu cântarul FLINTAB BX21
6. **AppSettings.java** - Managementul setărilor aplicației
7. **WeighingSystemTest.java** - Aplicație de test pentru toate componentele

## Cum să Rulați Aplicația

### Compilare:
```bash
javac -d bin src/module-info.java src/sdf/*.java
```

### Rulare Aplicație Principală:
```bash
java --module-path bin -m WeighingApp/sdf.WeightDataCollectorNew
```

### Rulare Test:
```bash
java --module-path bin -m WeighingApp/sdf.WeighingSystemTest
```

## Exemple de CSV Generat

```csv
# Cantariri - August 2025
# Generat: 2025-08-05 09:05:30 UTC
# User: Vlad2321334

Categorie Produs,Schimbul I,Schimbul II,Schimbul III,Total

# FRIGIDERE
Echipamente frigorifice (3011.34898),25.340,0.000,0.000,25.340
TOTAL FRIGIDERE,25.340,0.000,0.000,25.340

# VITRINE FRIG.
Vitrine frigorifice (3011.34896),30.125,0.000,0.000,30.125
TOTAL VITRINE FRIG.,30.125,0.000,0.000,30.125
```

## Schimburi de Lucru

- **Schimbul I**: 06:00 - 14:00
- **Schimbul II**: 14:00 - 22:00  
- **Schimbul III**: 22:00 - 06:00

## Porturile Seriale Suportate

COM1 până la COM20 (Windows)

## Specificații Tehnice

- **Java Version**: 17+
- **GUI Framework**: Swing
- **Module System**: Java Platform Module System
- **Dependencies**: java.desktop, java.base

## Status Final

✅ **TOATE CERINȚELE AU FOST IMPLEMENTATE ȘI TESTATE**

Aplicația este funcțională, intuitivă și respectă toate cerințele de design specificate.