# 🃏 Rogue-like Card Game

Projekt reprezentuje textově-grafickou rogue-like karetní hru naprogramovanou v jazyce **Java** s využitím knihovny **Swing** pro uživatelské rozhraní. Hráč si na začátku vybere svého hrdinu, se kterým následně prochází procedurálně generovanou mapu, bojuje s nepřáteli, vylepšuje svůj balíček karet a sbírá mocné relikvie s cílem porazit finálního bosse.

## 🚀 Hlavní herní mechaniky

* **Správa balíčku (Deck-building):** Hráč začíná se základní sadou karet. Během hry balíček dynamicky upravuje – získává nové karty za odměnu ze soubojů, nakupuje je v obchodě nebo odstraňuje slabé karty u obchodníka.
* **Procedurální postup:** Pohyb po herní mapě přes různé typy uzlů (Souboj, Obchod, Tábořiště, Náhodná událost). Každé rozhodnutí má trvalé následky na stav hrdiny (HP, zlato).
* **Systém relikvií:** Pasivní předměty, které hráč získává z truhel, elitních soubojů nebo událostí. Relikvie poskytují permanentní bonusy pro zbytek herního průchodu.

---

## 💻 Přehled herních obrazovek (UI Komponenty)

Hra je rozdělena do několika specializovaných panelů, které dědí od `JPanel` a jsou dynamicky přepínány hlavním oknem `GameWindow`:

### 1. `RestPanel` (Tábořiště / Campfire)
Místo pro odpočinek a přípravu na další postupy. Hráč je postaven před exkluzivní volbu:
* **REST:** Vyléčí hrdinu o fixních 30 % z jeho maximálního zdraví.
* **SMITH:** Umožní permanentně vylepšit (*upgrade*) jednu vybranou kartu z balíčku.
* Po vykonání akce se panely zamknou a zpřístupní se tlačítko pro pokračování na mapu.

### 2. `RandomEventPanel` (Náhodné události)
Textové příběhové situace s atmosférickým popisem, které staví hráče před morální nebo strategická rozhodnutí.
* **Dynamické události:** Např. *Dark Altar*, *Lost Cache*, *Cursed Shrine*, *Blacksmith's Offer*, *Odd Merchant*.
* **Podmíněné volby:** Možnosti jsou aktivní pouze tehdy, pokud má hráč dostatek zdrojů (např. dostatek HP pro oběť u oltáře nebo dostatek zlata na nákup od obchodníka).

### 3. `ShopPanel` (Obchod u kupce)
Umožní hráči utratit nastřádané zlato ze soubojů:
* **Nákup karet:** Nabízí náhodně vygenerované karty filtrované podle třídy hrdiny a neutrálních karet.
* **Odstranění karty:** Za poplatek 75 zlaťáků může hráč vyčistit svůj balíček od nechtěné karty (omezeno na 1 použití na návštěvu obchodu).

### 4. `RewardPanel` (Obrazovka odměn)
Zobrazuje se po úspěšném souboji nebo při otevření truhly s pokladem (`isTreasure`).
* **Výběr karet:** Generuje 3 náhodné karty s ohledem na třídu hrdiny. Hráč si může jednu vybrat, nebo nabídku přeskočit (*Skip*).
* **Inkasování zlata a relikvií:** Automaticky připisuje zlaťáky a v případě elitních soubojů nebo truhel generuje unikátní relikvii s jejím popisem a ikonou.

### 5. `WinScreen` (Vítězná obrazovka)
Zobrazí se po poražení finálního bosse. Vypisuje gratulaci k uhašení milníku (*The Pyre has been extinguished*) a zobrazuje jméno hrdiny, se kterým byl run úspěšně dokončen.

---

## 🛠️ Použité technologie a architektura

* **Jazyk:** Java (SDK 11+)
* **GUI Knihovna:** Java Swing & AWT
* **Návrhové vzory:**
    * *MVC (Model-View-Controller):* Oddělení herních dat (`Player`, `Card`, `Relic`) od vykreslovací logiky (jednotlivé `JPanel` třídy).
    * *Factory / Rozcestníky:* Metody typu `buildEvent(id)` pro dynamické generování dat na základě ID.
* **Načítání dat:** Externí konfigurace karet je realizována přes JSON soubory (`CardLoader`).
* **Grafika:** Vlastní kreslení pomocí `Graphics2D` (antialiasing, zaoblené tvary karet, barevné přechody, rendering textu s automatickým zalamováním řádků).

---

## 📋 Požadavky na spuštění

1. Nainstalované prostředí **Java JDK 11** nebo novější.
2. Správně umístěná složka `Res/` v kořenovém adresáři projektu s následující strukturou:
    * `Res/cards.json` (databáze všech karet)
    * Obrázky pro pozadí (`medieval_shop.jpg`, `dark_altar.jpg`, atd.)