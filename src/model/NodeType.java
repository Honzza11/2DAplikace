package model;

/**
 * Výčtový typ (enum) definující všechny možné druhy místností/uzlů na herní mapě.
 * Každý typ určuje, jaká herní obrazovka nebo událost se hráči po vstupu aktivuje.
 */
public enum NodeType {

    /** Běžný souboj s řadovým nepřítelem. */
    ENEMY,

    /** Náhodná textová nebo interaktivní událost (Event), která může pomoci i uškodit. */
    EVENT,

    /** Obchod, kde si hráč může za nasbírané zlato koupit karty, relikvie nebo odstranit karty z balíčku. */
    SHOP,

    /** Náročný souboj se silnějším (elitním) nepřítelem, ze kterého padají lepší odměny (např. relikvie). */
    ELITE,

    /** Odpočinkové místo (ohniště), kde se hráč může vyléčit nebo vylepšit (upgrade) kartu. */
    REST,

    /** Místnost s pokladem (truhlou), která dává hráči relikvii nebo zlato zdarma bez boje. */
    TREASURE,

    /** Finální uzel patra představující souboj s hlavním bossem. Jeho poražením končí herní akt. */
    BOSS
}