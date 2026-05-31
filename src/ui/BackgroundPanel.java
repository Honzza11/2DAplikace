package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Vlastní komponenta (panel), která slouží k vykreslení obrázku na pozadí herního okna.
 * Automaticky roztahuje obrázek podle aktuální velikosti panelu a nastavuje
 * GridBagLayout, což usnadňuje centrování tlačítek a textů v hlavním menu nebo na obrazovkách.
 */
public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        // Načtení obrázku z disku pomocí ImageIcon
        this.backgroundImage = new ImageIcon(imagePath).getImage();
        // Nastavení flexibilního rozvržení pro snadné centrování herních prvků
        setLayout(new GridBagLayout());
    }

    /**
     * Přepsaná metoda pro vlastní vykreslování komponenty.
     * Stará se o to, aby se obrázek na pozadí vykreslil dříve než samotné vnitřní prvky (tlačítka atd.).
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Zajistí standardní vyčištění panelu před kreslením
        if (backgroundImage != null) {
            // Vykreslí obrázek roztažený na celou aktuální šířku a výšku panelu
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}